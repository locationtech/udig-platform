/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.tool.options;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * This object is used when creating tool options, it provide access to the preference store so that
 * tool options can update preferences when changes.
 * <p>
 * Sample use:
 * 
 * <pre>
 * public static class OptionContribtionItem extends ToolOptionContributionItem {
 *     public IPreferenceStore fillFields( Composite parent ) {
 *         Button check = new Button(parent, SWT.CHECK);
 *         check.setText(&quot;Scale&quot;);
 *         addField(&quot;scale&quot;, check);
 * 
 *         Button tiled = new Button(parent, SWT.CHECK);
 *         tiled.setText(&quot;Tiled&quot;);
 *         addField(&quot;tiled&quot;, tiled);
 * 
 *         return Activator.getDefault().getPreferenceStore();
 *     }
 * };
 * </pre>
 * 
 * @author leviputna
 * @since 1.2.0
 */
public abstract class ToolOptionContributionItem extends ContributionItem
        implements
            IPropertyChangeListener {
    protected Map<Control, String> fields = new HashMap<Control, String>();
    protected IPreferenceStore store;

    private SelectionListener selectionListener = new SelectionListener(){
        @Override
        public void widgetSelected( SelectionEvent e ) {
            String preferenceString = (String) fields.get(e.widget);
            if (e.widget instanceof Button) {
                Button button = (Button) e.widget;
                boolean selection = button.getSelection();
                boolean selectionPref = getPreferenceStore().getBoolean(preferenceString);
                if (selection != selectionPref) {
                    getPreferenceStore().setValue(preferenceString, selection);
                }
            }else if (e.widget instanceof Combo) {
                Combo combo = (Combo) e.widget;
                String selection = behaviour[combo.getSelectionIndex()];
                String selectionPref = getPreferenceStore().getString(preferenceString);
                if (selection != selectionPref) {
                    getPreferenceStore().setValue(preferenceString, selection);
                }
            }
        }
        @Override
        public void widgetDefaultSelected( SelectionEvent e ) {
        }
    };
    
    private FocusListener focusListener = new FocusListener(){
        @Override
        public void focusGained(FocusEvent e) { }

        @Override
        public void focusLost(FocusEvent e) {
            String preferenceString = (String) fields.get(e.widget);
            if (e.widget instanceof Text) {
                Text text = (Text) e.widget;
                String txt = text.getText();
                String txtPref = getPreferenceStore().getString(preferenceString);
                if (!txtPref.equals(txt)) {
                    getPreferenceStore().setValue(preferenceString, txt);
                }
            }
        }
    };
    
    private String[] behaviour;

    protected void setPreferenceStore( IPreferenceStore store ) {
        this.store = store;
    }
    protected IPreferenceStore getPreferenceStore() {
        return store;
    }
    public void propertyChange( PropertyChangeEvent event ) {
        String propertyString = event.getProperty();
        if (fields.containsValue(propertyString)) {
            try {
                listen(false); // don't listen to controls during update
                update(store);
            } finally {
                listen(true);
            }
        }
    }

    private void listen( boolean listen ) {
        if (listen) {
            for( Control control : fields.keySet() ) {
                if (control == null || control.isDisposed()) {
                    continue;
                }
                if (control instanceof Button) {
                    ((Button) control).addSelectionListener(selectionListener);
                }
                else if( control instanceof Combo) {
                    ((Combo) control).addSelectionListener(selectionListener);
                }
                else if( control instanceof Text) {
                    ((Text) control).addFocusListener(focusListener);
                }
            }
        } else {
            for( Control control : fields.keySet() ) {
                if (control == null || control.isDisposed()) {
                    continue;
                }
                if (control instanceof Button) {
                    ((Button) control).removeSelectionListener(selectionListener);
                }
                else if( control instanceof Combo) {
                    ((Combo) control).removeSelectionListener(selectionListener);
                }
                else if( control instanceof Text) {
                    ((Text) control).removeFocusListener(focusListener);
                }
            }
        }
    }

    /** Add a button to the tracked widgets used to invoke a preference change */
    public void addField( String preferenceString, Button button ) {
        if (preferenceString == null) {
            throw new NullPointerException("PreferenceString required");
        }
        fields.put(button, preferenceString);
        // normally we would use button.setData but ContributionItems gets stored there
        button.addSelectionListener(selectionListener);
    }
    
    public void addField( String preferenceString, Combo combo, String[] behaviour ) {
        if (preferenceString == null) {
            throw new NullPointerException("PreferenceString required");
        }
        this.behaviour = behaviour;
        fields.put(combo, preferenceString);
        combo.addSelectionListener(selectionListener);
        
    }
    
    public void addField( String preferenceString, Text text ) {
        if (preferenceString == null) {
            throw new NullPointerException("PreferenceString required");
        }

        fields.put(text, preferenceString);
        // normally we would use button.setData but ContributionItems gets stored there
        text.addFocusListener(focusListener);
    }

    /**
     * The default implementation of this <code>IContributionItem</code> Subclasses must override to
     * provide config to tool options UI.
     */
    public final void fill( Composite parent ) {
        IPreferenceStore store = fillFields(parent);
        setPreferenceStore(store);
        update(store);
        store.addPropertyChangeListener(this);
        //listen(true); --> already done with addFields, else we got double Listeners and get the events twice
    }
    /**
     * Used to fill in the fields displayed in the tool option area.
     * <p>
     * You can call {@link #addField} for each field you would like to be automatically filled in
     * with the correct event handling.
     * <p>
     * Example:
     * 
     * <pre>
     * public static class OptionContribtionItem extends ToolOptionContributionItem {
     *     public IPreferenceStore fillFields( Composite parent ) {
     *         Button check = new Button(parent, SWT.CHECK);
     *         check.setText(&quot;Scale&quot;);
     *         addField(&quot;scale&quot;, check);
     * 
     *         Button tiled = new Button(parent, SWT.CHECK);
     *         tiled.setText(&quot;Tiled&quot;);
     *         addField(&quot;tiled&quot;, tiled);
     * 
     *         return Activator.getDefault().getPreferenceStore();
     *     }
     * };
     * 
     * </pre>
     * 
     * @param parent
     * @return IPreferenceStore used for persistence
     */
    protected abstract IPreferenceStore fillFields( Composite parent );

    /**
     * Override this method to fill in your widget state from the provided preferenceStore.
     * <p>
     * This method is only called for the property values registered using addField method.
     * 
     * @param preferenceStore
     */
    protected void update( IPreferenceStore preferenceStore ) {
        
        for( Control control : fields.keySet() ) {
            
            if (control == null || control.isDisposed()) {
                continue;
            }

            String preferenceString = fields.get(control);
            if (preferenceString == null) continue;
            
            if (control instanceof Button) {
                Button button = (Button) control;
                boolean selection = button.getSelection();
                boolean selectionPref = preferenceStore.getBoolean(preferenceString);
                if (selection != selectionPref) {
                    button.setSelection(selectionPref);
                }
            } else if (control instanceof Combo) {
                
                Combo combo = (Combo) control;
            
//                String selection = behaviour[combo.getSelectionIndex()];
//                
                String selectionPref = getPreferenceStore().getString(preferenceString);
                //if (selection != selectionPref) {
                    for(int i = 0; i < behaviour.length; i++){
                        if(selectionPref.equals(behaviour[i]))
                            combo.select(i);
                    }
               // }
            } else if (control instanceof Text) {
                Text text = (Text) control;
                String txtPref = getPreferenceStore().getString(preferenceString);
                text.setText(txtPref);
            }
            
        }
    }

    @Override
    public void dispose() {
        if (fields != null) {
            listen(false);
            fields.clear();
        }
        if (store != null) {
            store.removePropertyChangeListener(this);
            store = null;
        }
        super.dispose();
    }
}
