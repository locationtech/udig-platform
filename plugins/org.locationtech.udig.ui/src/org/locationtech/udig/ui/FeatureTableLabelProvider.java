/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

import org.locationtech.udig.core.IProvider;
import org.locationtech.udig.core.StaticProvider;
import org.locationtech.udig.ui.internal.Messages;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import org.locationtech.jts.geom.Geometry;

class FeatureTableLabelProvider extends LabelProvider
implements ITableLabelProvider, IColorProvider {
    /** FeatureLabelProvider owningFeatureTableControl field */
    private final FeatureTableControl owningFeatureTableControl;
    private IProvider<RGB> selectionColor = new StaticProvider<RGB>(new RGB(255,255,0));
    private RGB currentRGB = new RGB(255,255,0);
    private Color currentColor;

    /**
     * @param control
     */
    FeatureTableLabelProvider( FeatureTableControl control ) {
        owningFeatureTableControl = control;
    }

    public Image getColumnImage( Object element, int columnIndex ) {
        return null;
    }

    /**
     *
     * Returns the value of the column / feature attribute.
     * 
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     * @param element the array of feature attributes.
     * @param columnIndex the column index / feature attribute.
     * @return the string representation of the feature attribute, except
     * for attributes of type Geometry in which a string representing the 
     * type of Geometry is returned.
     */
    public String getColumnText( Object element, int columnIndex ) {
    	if (element instanceof FeatureCollection) {
            FeatureCollection<SimpleFeatureType, SimpleFeature> fc = (FeatureCollection<SimpleFeatureType, SimpleFeature>) element;
            if (columnIndex == 0) {
                return fc.getSchema().getName().getLocalPart();
            }
            return ""; //$NON-NLS-1$
        } else if (element instanceof SimpleFeature) {
            SimpleFeature f = (SimpleFeature) element;            
            if (columnIndex == 0) return f.getID();
            if( owningFeatureTableControl.features==null )
                return ""; //$NON-NLS-1$
            String attName = owningFeatureTableControl.getViewer().getTable().getColumn(columnIndex).getText();
            AttributeDescriptor at = f.getFeatureType().getDescriptor(attName);
            if (Geometry.class.isAssignableFrom(at.getType().getBinding())) { //was at.isGeometry()
                Object att = f.getAttribute(attName);
                if( att==null )
                    return ""; //$NON-NLS-1$
                String s = 
                    att.getClass().getName();
                return s.substring(s.lastIndexOf('.')+1);
            }
        
            Object attribute = f.getAttribute(attName);
            return attribute == null ? "" : attribute.toString(); //$NON-NLS-1$
        }else if( element instanceof Throwable  ){
            if( columnIndex==0 ){
                return ((Throwable) element).getLocalizedMessage();
            }
            return null;
        }else if( element instanceof String ){
            return (String) element;
        }else
        	return Messages.FeatureTableControl_loadingMessage; 
    }

    public Color getBackground( Object element ) {
        Display currentDisplay = Display.getCurrent();
        if( element == FeatureTableControl.LOADING )
            return getSelectionColor(currentDisplay);
        if(  element instanceof Throwable ){
            return currentDisplay.getSystemColor(SWT.COLOR_RED);
        }
        if( element instanceof SimpleFeature ){
            SimpleFeature feature=(SimpleFeature) element;
            if( owningFeatureTableControl.getSelectionProvider().getSelectionFids().contains(feature.getID()) ){
                return getSelectionColor(currentDisplay);
            }
        }

        return null;
    }

    private Color getSelectionColor(Display display) {
        if( currentColor!=null && this.currentRGB.equals(this.selectionColor.get()) ){
            return currentColor;
        }else{
            if( currentColor!=null ){
                currentColor.dispose();
            }
            currentRGB = this.selectionColor.get();
            this.currentColor = new Color(display,currentRGB);
            return currentColor;
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if( currentColor!=null ){
            currentColor.dispose();
        }
    }

    public Color getForeground( Object element ) {
        Display currentDisplay = Display.getCurrent();
        if(  element instanceof Throwable ){
            return currentDisplay.getSystemColor(SWT.COLOR_WHITE);
        }
        if( element instanceof SimpleFeature ){
            SimpleFeature feature=(SimpleFeature) element;
            if( owningFeatureTableControl.getSelectionProvider().getSelectionFids().contains(feature.getID()) ){
                if( darkBackground() ){
                    return currentDisplay.getSystemColor(SWT.COLOR_WHITE);
                }else{
                    return currentDisplay.getSystemColor(SWT.COLOR_BLACK);
                }
            }
        }

        return null;
    }

    private boolean darkBackground() {
        RGB selectColor2 = selectionColor.get();

        int brightness = ((selectColor2.red * 299) + (selectColor2.green * 587) + (selectColor2.blue * 114)) / 1000;
        return brightness<150;
    }

    public void setSelectionColor( IProvider<RGB> selectionColor ) {
        if( selectionColor==null ){
            this.selectionColor = new StaticProvider<RGB>(new RGB(255,255,0));
        }else{
            this.selectionColor = selectionColor;
        }
    }
}
