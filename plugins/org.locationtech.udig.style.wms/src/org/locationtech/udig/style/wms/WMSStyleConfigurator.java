/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.wms;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.style.IStyleConfigurator;
import org.locationtech.udig.style.wms.internal.Messages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.geotools.ows.wms.StyleImpl;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.styling.FeatureTypeStyle;

public class WMSStyleConfigurator extends IStyleConfigurator {

	Combo styleCombo;
    private List<StyleImpl> styles=new ArrayList<StyleImpl>();
    private Text text;
    private SashForm sashForm;
    private Composite root;
    private Layer layer;
	
	public WMSStyleConfigurator() {
		super();
	}

	@Override
	public boolean canStyle(Layer aLayer) {
		return aLayer.hasResource(WebMapServer.class);
	}

	@Override
	protected void refresh() {
        if( layer==getLayer() )
            return;
		layer = getLayer();
		
		List<StyleImpl> allStyles = getStyles(layer.findGeoResource(org.geotools.ows.wms.Layer.class));	
        styles.clear();
        styleCombo.setItems(new String[0]);
        // Map<DisplayName,wmsStyle>
        Map<String,StyleImpl> nameMap = new HashMap<String, StyleImpl>();

        // calculate display names for all styles
        // If there are duplicate titles then a combo title(name) is displayed
        for (Object s : allStyles) {
            StyleImpl wmsStyle = 
                (StyleImpl) s;
            String name = getDisplayName(wmsStyle);
            if( nameMap.containsKey(name) ){
                // rename the old one and mark it as deleted
                StyleImpl oldStyle = nameMap.get(name);
                if( oldStyle!=null){
                    nameMap.put(name, null);
                    String oldStyleName = name + " ("+oldStyle.getName()+")"; //$NON-NLS-1$ //$NON-NLS-2$
                    nameMap.put(oldStyleName, oldStyle);
                }
                name = name + " ("+wmsStyle.getName()+")"; //$NON-NLS-1$ //$NON-NLS-2$
            }
            // if the key is still used then we will use the first instance only.  If
            // there are two definitions of the same name and title then there isn't any
            // more we can be expected to do
            if( !nameMap.containsKey(name) ){
                nameMap.put(name, wmsStyle);
            }
        }
        
        for( Entry<String, StyleImpl> entry : nameMap.entrySet() ) {
            if( entry.getValue()!=null) {
                styleCombo.add(entry.getKey());
                styles.add(entry.getValue());
            }
        }
		//look for a value to set the combo to on the blackboard
		StyleImpl style = 
			(StyleImpl) layer.getStyleBlackboard().get(WMSStyleContent.WMSSTYLE);
		boolean set=false;
        if (style != null) {
            for( int i=0; i<styles.size(); i++) {
                StyleImpl wmsStyle = (StyleImpl) styles.get(i);
                if( style.equals(wmsStyle) ){
                    set=true;
                    styleCombo.select(i);
                    setDetails(wmsStyle);
                    break;
                }
            }
		}
        if(!set && styles.size()>0){
            styleCombo.select(0);
            setDetails((StyleImpl) styles.get(0));
        }
        
	}

    /**
     * Returns all the style in the resource if the resource can resolve to an {@link org.geotools.ows.wms.Layer}.
     *
     * @param wmsResource resource to search.
     * @return all named styles.
     */
    @SuppressWarnings("unchecked")
    static List<StyleImpl> getStyles(IGeoResource wmsResource) {
        org.geotools.ows.wms.Layer wmsLayer = null;
		try {
			wmsLayer = wmsResource.resolve(org.geotools.ows.wms.Layer.class, null);
		} 
		catch (IOException e) {
			IStatus status = 
				new Status(IStatus.ERROR, WMSStylePlugin.ID, -1, e.getLocalizedMessage(), e);
			WMSStylePlugin.getDefault().getLog().log(status);
		}
		
		if (wmsLayer != null) {
		    return wmsLayer.getStyles();
		}
        return Collections.emptyList();
    }

	private String getDisplayName( StyleImpl wmsStyle ) {
        String name=wmsStyle.getName();
        if( wmsStyle.getTitle()!=null )
            name=wmsStyle.getTitle().toString(Locale.getDefault());
        return name;
    }

    @Override
	public void createControl(Composite parent) {
        root=parent;
        GridLayout gridLayout = new GridLayout(1,false);
        gridLayout.marginBottom=0;
        gridLayout.marginHeight=0;
        gridLayout.marginLeft=0;
        gridLayout.marginRight=0;
        gridLayout.marginTop=0;
        gridLayout.marginWidth=0;
        parent.setLayout(gridLayout);
		createChooser(parent);
		createDetails(parent);
	}

    private void createDetails( Composite sashForm ) {
        text=new Text(sashForm, SWT.READ_ONLY|SWT.BORDER|SWT.WRAP|SWT.V_SCROLL);
        text.setBackground(sashForm.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        text.setLayoutData(gridData);
    }

    private void createChooser( Composite sashForm ) {
        Composite chooserComposite = new Composite(sashForm, SWT.NONE);
		chooserComposite.setLayout(new GridLayout(2,false));
        chooserComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        
		Label styleLabel = new Label(chooserComposite, SWT.HORIZONTAL);
		styleLabel.setText(Messages.WMSStyleConfigurator_style_label);
        
		styleCombo = new Combo(chooserComposite, SWT.DROP_DOWN|SWT.BORDER|SWT.READ_ONLY);
		styleCombo.addSelectionListener(
			new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					int i = styleCombo.getSelectionIndex();
					if (i > -1) {
                        StyleImpl wmsStyle = 
                            (StyleImpl) styles.get(i);
						StyleBlackboard bb = getLayer().getStyleBlackboard();
						bb.put(WMSStyleContent.WMSSTYLE, wmsStyle);
                        bb.setSelected(new String[]{WMSStyleContent.WMSSTYLE});
                        setDetails(wmsStyle);
					}
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetDefaultSelected(e);
				}	
			}
		);
		
		GridData gridData = new GridData();
        gridData.verticalAlignment=SWT.BEGINNING;
        styleLabel.setLayoutData(gridData);
        gridData=new GridData(SWT.FILL, SWT.NONE, true, false);
        gridData.verticalAlignment=SWT.BEGINNING;
        styleCombo.setLayoutData(gridData);
    }

    protected void setDetails( StyleImpl wmsStyle ) {
        boolean detailsSet=false;
        if( wmsStyle.getAbstract()!=null ){
            text.setText(MessageFormat.format(Messages.WMSStyleConfigurator_abstract_format, new Object[] {
            		wmsStyle.getAbstract().toString(Locale.getDefault())
            }));
            detailsSet=true;
        }
        if( wmsStyle.getFeatureStyles() !=null ){
        	StringBuffer buff = new StringBuffer();
        	List<FeatureTypeStyle > fts = wmsStyle.getFeatureStyles();
            for( FeatureTypeStyle style : fts ) {
                String name = style.getName();
                if( style.getDescription().getTitle().toString()!=null )
                    name = style.getDescription().getTitle().toString();                    
                buff.append( name );
                buff.append("\n"); //$NON-NLS-1$
            }
            
            text.setText(MessageFormat.format(Messages.WMSStyleConfigurator_featureStyles_format, new Object[] {buff}));
            detailsSet=true;
        }
        
        if (!detailsSet ){
            text.setText(Messages.WMSStyleConfigurator_no_info);
        }
    }
}
