/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.scalebar;

import java.awt.Color;

import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.internal.Messages;
import org.locationtech.udig.mapgraphic.scalebar.BarStyle.BarType;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.ui.internal.dialogs.ColorEditor;
import org.locationtech.udig.style.IStyleConfigurator;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;

/**
 * A chooser for the various bar type settings. Includes selecting the bar type, color, and number
 * of intervals.
 * 
 * @author egouge
 * @since 1.1.0
 */
public class BarStyleConfigurator extends IStyleConfigurator
        implements
            SelectionListener,
            ISelectionChangedListener {

    private Spinner divSpinner = null;
    private ColorEditor chooser = null;
    private TableViewer tViewer;
    private Combo cmbUnits;
    @Override
    public boolean canStyle( Layer layer ) {
        return layer.hasResource(MapGraphic.class)
                && layer.getStyleBlackboard().contains(BarStyleContent.ID);
    }

    @Override
    public void createControl( Composite parent ) {
        parent.setLayout(new GridLayout(2, false));

        Label label = new Label(parent, SWT.NONE);
        label.setText(Messages.BarStyleConfigurator_barstylelabel);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

        
        final Table table = new Table(parent, SWT.BORDER | SWT.SINGLE |  SWT.V_SCROLL | SWT.H_SCROLL);
        table.setLinesVisible(false);
        table.setHeaderVisible(false);
        table.setLayoutData(new GridData(235, 45));
        
        tViewer = new TableViewer(table);

        TableViewerColumn c = new TableViewerColumn(tViewer, SWT.LEFT);
        c.getColumn().setResizable(false);
        c.getColumn().setWidth(250);
        c.setLabelProvider(new ColumnLabelProvider(){
            
            @Override
            public String getText(Object element){
                return ((BarStyle.BarType)element).getName();
            }
            
            @Override
            public Image getImage(Object element){
                BarType bt = (BarStyle.BarType) element;
              return bt.getImage();
            }
        });
       
        
        tViewer.setContentProvider(new IStructuredContentProvider(){

            public void dispose() {
            }

            public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
            }

            public Object[] getElements( Object inputElement ) {
                return BarStyle.getTypes();
            }});

        tViewer.setInput(BarStyle.getTypes());        
        tViewer.addSelectionChangedListener(this);
        
        label = new Label(parent, SWT.RIGHT);
        label.setText(Messages.BarStyleConfigurator_divisionslabel);
        label.setLayoutData(new GridData());

        divSpinner = new Spinner(parent, SWT.BORDER);
        divSpinner.setMaximum(BarStyle.MAXIMUM_DIVISIONS);
        divSpinner.setMinimum(BarStyle.MINIMUM_DIVISIONS);
        divSpinner.setIncrement(BarStyle.DIVISION_INCREMENT);
        divSpinner.addSelectionListener(this);

        label = new Label(parent, SWT.RIGHT);
        label.setText(Messages.BarStyleConfigurator_colorlable);
        label.setLayoutData(new GridData());
        chooser = new ColorEditor(parent);
        chooser.addSelectionListener(this);
        
        label = new Label(parent, SWT.RIGHT);
        label.setText(Messages.BarStyleConfigurator_UnitsLabel);
        label.setLayoutData(new GridData());
        
        cmbUnits = new Combo(parent, SWT.DROP_DOWN);
        cmbUnits.setItems(new String[]{UnitPolicy.AUTO.getLabel(), UnitPolicy.METRIC.getLabel(), UnitPolicy.IMPERIAL.getLabel()});
        cmbUnits.select(0);
        cmbUnits.setLayoutData(new GridData());
        cmbUnits.addSelectionListener(this);
    }

    @Override
    protected void refresh() {
        BarStyle barStyle = (BarStyle) getStyleBlackboard().get(BarStyleContent.ID);
        if (divSpinner != null) {
            divSpinner.setSelection(barStyle.getNumintervals());
        }

        if (tViewer != null) {
            for (int i = 0; i < tViewer.getTable().getItemCount(); i ++){
                if (((BarStyle.BarType)tViewer.getTable().getItem(i).getData()) == barStyle.getType()){
                    tViewer.getTable().setSelection(i);
                    break;
                }
            }
        }
        if (chooser != null) {
            Color c = barStyle.getColor();
            chooser.setColorValue(new RGB(c.getRed(), c.getGreen(), c.getBlue()));
        }
        
        if (barStyle.getUnits() == UnitPolicy.METRIC){
            cmbUnits.select(1);
        }else if (barStyle.getUnits() == UnitPolicy.IMPERIAL){
            cmbUnits.select(2);
        }else{
            cmbUnits.select(0);
        }
    }

    public void widgetDefaultSelected( SelectionEvent e ) {

    }

    public void widgetSelected( SelectionEvent e ) {
        updateStyle();
    }

    public void selectionChanged( SelectionChangedEvent event ) {
        updateStyle();
    }
    private void updateStyle() {
        BarStyle barStyle = (BarStyle) getStyleBlackboard().get(BarStyleContent.ID);

        RGB rgb = chooser.getColorValue();
        barStyle.setColor(new Color(rgb.red, rgb.green, rgb.blue));

        barStyle.setNumIntervals(divSpinner.getSelection());

        if (tViewer.getTable().getSelection().length > 0) {
            barStyle.setType((BarStyle.BarType) tViewer.getTable().getSelection()[0].getData());
        }

        if (cmbUnits.getSelectionIndex() == 1) {
            barStyle.setUnits(UnitPolicy.METRIC);
        } else if (cmbUnits.getSelectionIndex() == 2) {
            barStyle.setUnits(UnitPolicy.IMPERIAL);
        } else {
            barStyle.setUnits(UnitPolicy.AUTO);
        }
    }
}
