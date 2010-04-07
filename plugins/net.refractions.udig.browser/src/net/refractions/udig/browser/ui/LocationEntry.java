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
package net.refractions.udig.browser.ui;

import net.refractions.udig.browser.BrowserPlugin;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Text field that allows a user to entry a URL
 * 
 * @author jesse
 * @since 1.1.0
 */
public class LocationEntry {

    private BrowserContainerView view;
    private Text entry;

    protected LocationEntry( BrowserContainerView view ) {
        this.view = view;
    }

    protected Control createControl( Composite parent ) {
        entry = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.CANCEL);
        addKeyboardListener();
        
        return entry;

    }

    private void addKeyboardListener() {
        entry.addListener(SWT.KeyUp, new Listener(){

            public void handleEvent( Event event ) {
                switch( event.keyCode ) {
                case SWT.CR:
                    updateView(event);
                    break;
                case SWT.LF:
                    updateView(event);
                    break;
                case SWT.KEYPAD_CR:
                    updateView(event);
                    break;
                default:
                    break;
                }
            }

        });
    }

    private void updateView( Event event ) {
        if( (event.stateMask & SWT.MOD1) != 0 ){
            view.addTab(entry.getText(), entry.getText(), view.getListener());
        }else{
            view.setCurrentURL(entry.getText());
        }
    }

    public IAction getButton() {
        Action action = new Action(){
            @Override
            public void runWithEvent( Event event ) {
                updateView(event);
            }

        };
        
        action.setImageDescriptor(BrowserPlugin.getImageDescriptor(BrowserPlugin.ICON_GO));
        return action;
    }

    public void setText( String location ) {
        entry.setText(location);
    }

}
