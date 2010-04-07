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

import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.geotools.data.FeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.validation.IntegrityValidation;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * An abstract class for integrity validation which uses org.geotools.validation
 * <p>
 *
 * </p>
 * @author chorner
 * @since 1.0.1
 */
public abstract class IntegrityValidationOp implements IOp  {
    public GenericValidationResults genericResults; //for testing
    
    /**
     *  
     *
     * @param layer 
     * @return the appropriate integrity validator
     */
    abstract IntegrityValidation getValidator(ILayer[] layer);
    
    /** 
     * 
     * @see net.refractions.udig.ui.operations.IOp#op(org.eclipse.swt.widgets.Display, java.lang.Object, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void op(final Display display, Object target, IProgressMonitor monitor) throws Exception {
        // define the ILayer array
        final ILayer[] layer;
        if (target.getClass().isArray()) {
            layer = (ILayer[]) target;
        } else {
            layer = new ILayer[1];
            layer[0] = (ILayer) target;
        }
        //construct the hashmap and run the validation
        ReferencedEnvelope envelope = layer[0].getMap().getViewportModel().getBounds();
        FeatureSource<SimpleFeatureType, SimpleFeature> source;
        String nameSpace;
        String typeName;
        Map<String,FeatureSource<SimpleFeatureType, SimpleFeature>> map = new HashMap<String,FeatureSource<SimpleFeatureType, SimpleFeature>>();
        for (int i = 0; i < layer.length; i++) {
            nameSpace = layer[i].getSchema().getName().getNamespaceURI();
            typeName = layer[i].getSchema().getName().getLocalPart();
            source = layer[i].getResource(FeatureSource.class, monitor);
            //map = dataStoreID:typeName
            map.put(nameSpace.toString()+":"+typeName, source); //$NON-NLS-1$
        }
        
        GenericValidationResults results = new GenericValidationResults();
        genericResults = results;
        
        PlatformGIS.syncInDisplayThread(new Runnable(){

            public void run() {
                Dialog dialog = getDialog(display.getActiveShell(), layer[0].getSchema());
                if (dialog != null) {
                    dialog.open();
                }
            }
            
        });
        
        final IntegrityValidation integrityValidation = getValidator(layer);
        if (integrityValidation == null) return;

        integrityValidation.validate(map, envelope, results);
        
        OpUtils.setSelection(layer[0], results);
        OpUtils.notifyUser(display, results);
        
        monitor.internalWorked(1);
        monitor.done();
    }

    protected Dialog getDialog(Shell shell, SimpleFeatureType featureType) {
        return null;
    }

}
