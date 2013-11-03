/**
 * 
 */
package org.locationtech.udig.validation;

import java.util.Iterator;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.validation.FeatureValidation;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * An abstract class for feature validation which uses org.geotools.validation
 *
 * @author chorner
 * @since 1.0.1
 */
abstract class FeatureValidationOp implements IOp {
    public GenericValidationResults results; //for testing
    
    /**
     * 
     *
     * @return the appropriate Validating SimpleFeature Method Class
     */
    abstract FeatureValidation getValidator();
    
	/** 
     * 
	 * @see org.locationtech.udig.ui.operations.IOp#op(org.eclipse.swt.widgets.Display, java.lang.Object, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void op(final Display display, Object target, IProgressMonitor monitor) throws Exception {
	    final ILayer layer = (ILayer) target;
	    FeatureSource<SimpleFeatureType, SimpleFeature> source = layer.getResource(FeatureSource.class, monitor);
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection;
        collection = source.getFeatures();
        results = new GenericValidationResults();
        
        PlatformGIS.syncInDisplayThread(new Runnable(){

            public void run() {
                Dialog dialog = getDialog(display.getActiveShell(), layer.getSchema());
                if (dialog != null) {
                    dialog.open();
                }
            }
            
        });
        
        final FeatureValidation featureValidation = getValidator();
        if (featureValidation == null) return;
        //IsValidGeometryValidation geometryValidation = new IsValidGeometryValidation();
        SimpleFeatureType type;
        
        // iterate through the collection and validate each feature
        Iterator iterator;
        for( iterator = collection.iterator(); iterator.hasNext(); ){
            SimpleFeature feature = (SimpleFeature) iterator.next();
            type = feature.getFeatureType();
            if (canValidate(type)) {
                featureValidation.validate(feature, type, results);
            }
        }
        collection.close( iterator );
        
        OpUtils.setSelection(layer, results);
        //OpUtils.notifyUser(display, results);

        monitor.internalWorked(1);
        monitor.done();
	}

	/**
     * This method may be overridden for classes which need a dialog for user input
     * 
     * @param shell
     * @param featureType
     * @return null
     */
    protected Dialog getDialog(Shell shell, SimpleFeatureType featureType) {
        return null;
    }
    
    /**
     * This method may be overridden for classes which only validate certain featureTypes
     *
     * @param featureType
     * @return boolean
     */
    protected boolean canValidate(SimpleFeatureType featureType) {
        return true;
    }
}
