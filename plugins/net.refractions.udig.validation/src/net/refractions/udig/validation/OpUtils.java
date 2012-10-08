/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.validation;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.factory.SelectionCommandFactory;
import net.refractions.udig.validation.internal.Messages;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
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
