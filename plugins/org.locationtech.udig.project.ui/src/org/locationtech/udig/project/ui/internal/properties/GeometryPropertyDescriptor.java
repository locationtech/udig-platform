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
package org.locationtech.udig.project.ui.internal.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.geotools.geometry.jts.JTS;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.command.navigation.SetViewportBBoxCommand;
import org.locationtech.udig.project.ui.internal.MapEditorPart;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;

/**
 * @author jones
 * @since 0.3
 */
public class GeometryPropertyDescriptor extends PropertyDescriptor {

    /**
     * Construct <code>GeometryPropertyDescriptor</code>.
     * 
     * @param id
     * @param displayName
     */
    public GeometryPropertyDescriptor( Object id, String displayName ) {
        super(id, displayName);
    }

    /**
     * @see org.eclipse.ui.views.properties.PropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
     */
    public CellEditor createPropertyEditor( Composite parent ) {

        return new CellEditor(parent, SWT.NONE){

            Geometry geometry;
            private Button button;
            private Label label;

            protected Control createControl( Composite comp ) {
                Composite parent = new Composite(comp, SWT.NONE);
                parent.setLayout(new GridLayout(2, false));
                label = new Label(parent, SWT.READ_ONLY);
                if (geometry != null)
                    label.setText(getLabelProvider().getText(geometry));
                GridData data = new GridData(SWT.FILL, SWT.TOP, true, true);
                data.verticalSpan = 2;
                label.setLayoutData(data);
                button = new Button(parent, SWT.PUSH | SWT.FLAT);
                button.setText(Messages.GeometryPropertyDescriptor_viewButton_text); 
                data = new GridData(SWT.RIGHT, SWT.TOP, false, false);
                data.verticalSpan = 1;
                button.setLayoutData(data);
                button.addListener(SWT.MouseUp, new Listener(){

                    public void handleEvent( Event event ) {
                        MapEditorPart editor = (MapEditorPart) PlatformUI.getWorkbench()
                                .getActiveWorkbenchWindow().getActivePage().getActiveEditor();
                        Map map = editor.getMap();
                        Envelope env = geometry.getEnvelopeInternal();
                        try {
                            env = JTS.transform(env, map.getEditManager().getEditLayer()
                                    .layerToMapTransform());
                        } catch (Exception e1) {
                            ProjectUIPlugin.log(null, e1);
                        }
                        map.sendCommandASync(new SetViewportBBoxCommand(env, map.getViewportModel().getCRS()));
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                                .activate(editor);
                    }

                });
                button.addKeyListener(new KeyAdapter(){
                    public void keyReleased( KeyEvent e ) {
                        if (e.character == '\u001b') { // Escape
                            fireCancelEditor();
                        }
                    }
                });
                return parent;
            }

            protected Object doGetValue() {
                return geometry;
            }

            protected void doSetFocus() {
                button.setFocus();
            }

            protected void doSetValue( Object value ) {
                geometry = (Geometry) value;
                label.setText(getLabelProvider().getText(geometry));
            }

        };
    }

    /**
     * @see org.eclipse.ui.views.properties.PropertyDescriptor#getLabelProvider()
     */
    public ILabelProvider getLabelProvider() {
        return new LabelProvider(){
            /**
             * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
             */
            public String getText( Object element ) {

                Geometry geom = null;
                if (element instanceof GeomPropertySource) {
                    geom = (Geometry) ((GeomPropertySource)element).getEditableValue();
                } else {
                    geom = (Geometry) element;
                }
                return geom.getGeometryType();
            }
        };
    }

}
