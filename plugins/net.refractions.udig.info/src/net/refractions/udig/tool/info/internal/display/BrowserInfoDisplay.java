/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.tool.info.internal.display;

import java.io.IOException;

import net.refractions.udig.tool.info.InfoDisplay;
import net.refractions.udig.tool.info.InfoPlugin;
import net.refractions.udig.tool.info.LayerPointInfo;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;

/**
 * Nested browser used to display LayerPointInfo.
 * 
 * @author Jody Garnett
 * @since 0.3
 */
public class BrowserInfoDisplay extends InfoDisplay {

    /** <code>browser</code> field */
    protected Browser browser;
        
    private Action backAction = new Action("Back") { //$NON-NLS-1$
        public void run() {
            browser.back();
        }
    };    
    private Action forwardAction = new Action("Forward") { //$NON-NLS-1$
        public void run() {
            browser.forward();
        }
    };

    private Action stopAction = new Action("Stop") { //$NON-NLS-1$
        public void run() {
            browser.stop();
            // cancel any partial progress.
            // getViewSite().getActionBars().getStatusLineManager().getProgressMonitor().done();
        }
    };

    private Action refreshAction = new Action("Refresh") { //$NON-NLS-1$
        public void run() {
            browser.refresh();
        }
    };
    /** <code>DEBUG</code> field */
    static final protected boolean DEBUG = false;
    //private CLabel label;
    private ViewForm viewForm;
    
    /*
     * Nested viewForm containing browser, locationbar and toolbar 
     * @return embded browser
     */
    public Control getControl() {
        return viewForm;
    }
    /*
     * Set up w/ an embeded brower.
     */
    public void createDisplay( Composite parent ) {
        viewForm= new ViewForm( parent, SWT.NONE);
        
        //label= new CLabel( viewForm, SWT.NONE);
        //viewForm.setTopLeft( label );
        
        ToolBar toolBar= new ToolBar( viewForm, SWT.FLAT | SWT.WRAP);
        viewForm.setTopCenter(toolBar);
                        
        browser = createBrowser( viewForm, toolBar );        
        browser.setUrl( "about:blank" ); //$NON-NLS-1$
        
        viewForm.setContent( browser );
    }
    
    /**
     * Focus the browser onto LayerPointInfo.getRequestURL.
     * 
     * @see net.refractions.udig.tool.info.InfoDisplay#setInfo(net.refractions.udig.project.render.LayerPointInfo)
     * @param info
     */
    public void setInfo( LayerPointInfo info ) {
        if( info == null || info.getRequestURL() == null ) {
            browser.setVisible( false );
        }
        else {
            browser.setVisible( true );
            try {
                browser.setText((String) info.acquireValue());
            } catch (IOException e) {
                InfoPlugin.trace("Could not acquire info value", e);
            }
        }
    }
    
    
    private Browser createBrowser(Composite parent, final ToolBar toolbar) {      
        try{
        browser = new Browser(parent, SWT.NONE);
        }catch(Exception e){
            InfoPlugin.log( "Could not create browser", e); //$NON-NLS-1$
        }
        
        browser.addStatusTextListener(new StatusTextListener() {
            // IStatusLineManager status = toolbar.getStatusLineManager(); 
            public void changed(StatusTextEvent event) {
                /*
                if (DEBUG) {
                    System.out.println("status: " + event.text);
                }
                status.setMessage(event.text);
                */
            }         
        });
        browser.addLocationListener(new LocationAdapter() {
            public void changed(LocationEvent event) {
                if (event.top){
                    //label.setToolTipText( browser.getUrl() );                    
                }
            }
        });
        browser.addTitleListener(new TitleListener() {
            public void changed(TitleEvent event) {
                //label.setText( event.title );
            }
        });
        
        // Hook the navigation actons as handlers for the retargetable actions
        // defined in BrowserActionBuilder.
        ToolBarManager tbmanager= new ToolBarManager( toolbar );        
        tbmanager.add( backAction );
        tbmanager.add( forwardAction );
        tbmanager.add( stopAction) ;
        tbmanager.add( refreshAction );
        
        return browser;
    }
}