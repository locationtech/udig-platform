/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.tool.info.internal.display;

import java.io.IOException;

import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.tool.info.InfoDisplay;
import org.locationtech.udig.tool.info.InfoPlugin;
import org.locationtech.udig.tool.info.LayerPointInfo;

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
        @Override
        public void run() {
            browser.back();
        }
    };
    private Action forwardAction = new Action("Forward") { //$NON-NLS-1$
        @Override
        public void run() {
            browser.forward();
        }
    };

    private Action stopAction = new Action("Stop") { //$NON-NLS-1$
        @Override
        public void run() {
            browser.stop();
            // cancel any partial progress.
            // getViewSite().getActionBars().getStatusLineManager().getProgressMonitor().done();
        }
    };

    private Action refreshAction = new Action("Refresh") { //$NON-NLS-1$
        @Override
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
    @Override
    public Control getControl() {
        return viewForm;
    }
    /*
     * Set up w/ an embeded brower.
     */
    @Override
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
     * @see org.locationtech.udig.tool.info.InfoDisplay#setInfo(org.locationtech.udig.project.render.LayerPointInfo)
     * @param info
     */
    @Override
    public void setInfo( LayerPointInfo info ) {
        if( info == null || info.getRequestURL() == null ) {
            browser.setVisible( false );
        }
        else {
            browser.setVisible( true );
            try {
                browser.setText((String) info.acquireValue());
            } catch (IOException e) {
                LoggingSupport.trace(InfoPlugin.getDefault(), "Could not acquire info value", e);
            }
        }
    }

    private Browser createBrowser(Composite parent, final ToolBar toolbar) {
        try{
        browser = new Browser(parent, SWT.NONE);
        }catch(Exception e){
            LoggingSupport.log(InfoPlugin.getDefault(), "Could not create browser", e); //$NON-NLS-1$
        }

        browser.addStatusTextListener(new StatusTextListener() {
            // IStatusLineManager status = toolbar.getStatusLineManager();
            @Override
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
            @Override
            public void changed(LocationEvent event) {
                if (event.top){
                    //label.setToolTipText( browser.getUrl() );
                }
            }
        });
        browser.addTitleListener(new TitleListener() {
            @Override
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
