package net.refractions.udig.feature.editor;

import net.refractions.udig.project.EditManagerEvent;
import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.project.internal.EditManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.opengis.feature.Feature;

@SuppressWarnings("nls")
public class FeaturePage extends Page implements IPage {

    private IEditManager editManager;
    CLabel title;
    private IEditManagerListener listener = new IEditManagerListener(){        
        public void changed( EditManagerEvent event ) {
            refresh( event );
        }
    };
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
        title = new CLabel( parent, SWT.SHADOW_OUT | SWT.CENTER );
        title.setText("Hello World");
        editManager.addListener(listener);
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
