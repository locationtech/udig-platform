/**
 * 
 */
package net.refractions.udig.project.ui.operations.example;

import java.text.MessageFormat;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Counts the selected features on the layer
 * 
 * @author jesse
 */
public class SelectedFeatureop implements IOp {

	public void op(final Display display, Object target, IProgressMonitor monitor)
			throws Exception {
		monitor.beginTask("Counting selected features", 30);
		monitor.worked(1);
		
		final ILayer layer = (ILayer) target;
		FeatureSource<SimpleFeatureType, SimpleFeature> source = layer.getResource(FeatureSource.class, new NullProgressMonitor());
		FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(layer.getFilter());
		
		FeatureIterator<SimpleFeature> features = collection.features();
		
		try{
			int count = 0;
			while(features.hasNext()){
				features.next();
				count++;
			}
			final int finalCount = count;
			display.asyncExec(new Runnable() {
				public void run() {
					String pattern = "There are {0} features selected in the current layer";
					String message = MessageFormat.format(pattern, new Object[]{finalCount});
					MessageDialog.openInformation(display.getActiveShell(), "Selected Features", message);
				}
			});
		}finally{
			features.close();
		}
	}

}
