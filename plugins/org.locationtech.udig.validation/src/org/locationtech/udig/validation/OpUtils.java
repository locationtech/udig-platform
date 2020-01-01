/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.validation;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.factory.SelectionCommandFactory;
import org.locationtech.udig.validation.internal.Messages;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.Identifier;

/**
 * A utility class which provides a method to notify the user of the validation results, and a
 * method selection features which failed the validation on the current layer.
 * <p>
 * </p>
 * 
 * @author chorner
 * @since 1.0.1
 */
public class OpUtils {

    /**
     * Notifies the user of the result of the validation. Currently, this method displays a crude
     * pop-up window, but one day... it will populate the analysis window
     * 
     * @param display
     * @param evaluationObject
     * @param results
     */
    public static void notifyUser( final Display display, GenericValidationResults results) {
        // display the results -- to be deleted soon
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Failures: " + results.failedFeatures.size() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append("Warnings: " + results.warningFeatures.size() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
        for( int i = 0; (i < results.failedFeatures.size()) && (i < 8); i++ ) {
            buffer.append("error " + i + ": " + results.failureMessages.get(i) + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        buffer.append("\n"); //$NON-NLS-1$
        for( int i = 0; (i < results.validationList.size()) && (i < 10); i++) {
        	buffer.append(MessageFormat.format(Messages.OpUtils_notifyResult, i, results.validationList.get(i)));
        }
        display.asyncExec(new Runnable(){
            public void run() {
                MessageDialog.openInformation(display.getActiveShell(), Messages.OpUtils_results, buffer.toString());  
            }
        });
    }

    /**
     * Given a layer and validation result, this method creates a fid filter and selects the
     * features in the current layer which failed the validation.
     * 
     * @param layer
     * @param results
     * @throws FactoryConfigurationError
     */public static void setSelection( final ILayer layer, GenericValidationResults results ) {
    	 FilterFactory ff = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
    	 
    	 Set<Identifier> fid = new HashSet<Identifier>();
        // generate a fid filter out of the invalid features
        for( SimpleFeature feature : results ) {
            fid.add(ff.featureId(feature.getID()));
        }
        // select the invalid features on the current layer
        MapCommand selectionCommand = SelectionCommandFactory.getInstance().createSelectCommand(
                layer, ff.id(fid));
        layer.getMap().sendCommandASync(selectionCommand);
    }

}
