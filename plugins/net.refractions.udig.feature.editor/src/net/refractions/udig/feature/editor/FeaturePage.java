package net.refractions.udig.feature.editor;

import java.util.List;

import net.refractions.udig.project.EditManagerEvent;
import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.EditManager;
import net.refractions.udig.project.ui.IFeatureSite;
import net.refractions.udig.project.ui.feature.FeaturePanelProcessor;
import net.refractions.udig.project.ui.feature.FeatureSiteImpl;
import net.refractions.udig.project.ui.feature.FeaturePanelProcessor.FeaturePanelEntry;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * FeaturePage is used to show a series of feature panels and allow interaction with
 * the EditManager.
 * 
 * @since 1.2.0
 */
@SuppressWarnings("nls")
public class FeaturePage extends Page implements IPage {

    private IEditManager editManager;
    CLabel title;
    private IEditManagerListener listener = new IEditManagerListener(){        
        public void changed( EditManagerEvent event ) {
            refresh( event );
        }
    };
    /**
     * Refresh the user interface in response to an event from the edit manager
     * (currently only focuses on the EDIT_FEATURE events).
     *
     * @param event
     */
    public void refresh( final EditManagerEvent event ){
        title.getDisplay().asyncExec( new Runnable(){
            public void run() {
                if( title == null || event == null) return;
                if( event.getType() == EditManagerEvent.EDIT_FEATURE ){
                    Feature feature = (Feature) event.getNewValue();
                    if( feature != null ){
                        title.setText( feature.getIdentifier().toString() );
                    }
                    else {
                        title.setText( "unknown fid" );
                    }
                }
                else {
                    title.setText( "Editing "+event.getSource().getSelectedLayer().getName() );
                }
            }
        });        
    }
    
    public FeaturePage( IEditManager editManager ){
        this.editManager = editManager;
    }
    
    @Override
    public void init( IPageSite pageSite ) {
        super.init(pageSite);
    }
    
    @Override
    public void createControl( Composite parent ) {
        editManager.addListener(listener);
        
        setEditLayer( editManager.getSelectedLayer() );        
    }
    /**
     * Will set up the contents for the current layer; if it has a featureType
     * <p>
     * Please note the contents will not be enabled unless
     * there is currently an editFeature
     * </p>
     * @param layer
     */
    private void setEditLayer( ILayer layer ) {
        SimpleFeatureType schema = layer.getSchema();
        if( schema == null ){
            // there is no feature content here to edit!
            return;
        }
        FeaturePanelProcessor panels = ProjectUIPlugin.getDefault().getFeaturePanelProcessor();
        IFeatureSite site = new FeatureSiteImpl();
        List<FeaturePanelEntry> avaialble = panels.search(schema, site);
        for( FeaturePanelEntry entry : avaialble ){
            
        }
    }

    @Override
    public Control getControl() {
        return title;
    }

    @Override
    public void setFocus() {
        title.setFocus();
    }
    @Override
    public void dispose() {
        if( editManager != null ){
            editManager.removeListener(listener);
            editManager = null;
        }
        super.dispose();
    }

}
