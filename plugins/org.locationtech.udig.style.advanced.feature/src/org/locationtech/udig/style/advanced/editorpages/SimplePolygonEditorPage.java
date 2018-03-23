/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.editorpages;

import java.util.List;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.style.internal.StyleLayer;
import org.locationtech.udig.style.sld.SLDContent;
import org.locationtech.udig.style.sld.editor.StyleEditorDialog;
import org.locationtech.udig.style.sld.editor.StyleEditorPage;
import org.locationtech.udig.ui.graphics.SLDs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.geotools.data.FeatureSource;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.visitor.DuplicatingStyleVisitor;

import org.locationtech.udig.style.advanced.internal.Messages;
import org.locationtech.udig.style.advanced.polygons.PolygonPropertiesEditor;
import org.locationtech.udig.style.advanced.utils.Utilities;

/**
 * Style editor for simple polygons.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class SimplePolygonEditorPage extends StyleEditorPage {
    
    private Style style = null;
    private PolygonPropertiesEditor propertiesEditor;
    private StackLayout stackLayout;
    private Label noFeatureLabel;
    private Composite mainComposite;

    private Style oldStyleCopy;

    public SimplePolygonEditorPage() {
        super();
        setSize(new Point(740, 500));
    }

    @Override
    public void createPageContent( Composite parent ) {

        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        stackLayout = new StackLayout();
        mainComposite.setLayout(stackLayout);

        noFeatureLabel = new Label(mainComposite, SWT.NONE);
        noFeatureLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        noFeatureLabel.setText(Messages.SimplePolygonEditorPage_0);

        StyleLayer layer = getSelectedLayer();
        IGeoResource resource = layer.getGeoResource();
        if (resource.canResolve(FeatureSource.class)) {
            StyleBlackboard styleBlackboard = layer.getStyleBlackboard();
            style = (Style) styleBlackboard.get(SLDContent.ID);
            if (style == null) {
                style = Utilities.createDefaultPolygonStyle();
            }
            
            DuplicatingStyleVisitor dsv = new DuplicatingStyleVisitor();
            dsv.visit(style);
            oldStyleCopy = (Style) dsv.getCopy();

            if (isPolygonStyle(style)) {
                propertiesEditor = new PolygonPropertiesEditor(layer);
                propertiesEditor.open(mainComposite, style);
                stackLayout.topControl = propertiesEditor.getControl();
            } else {
                stackLayout.topControl = noFeatureLabel;
            }
        } else {
            stackLayout.topControl = noFeatureLabel;
        }

    }

    private boolean isPolygonStyle( Style style ) {
        Symbolizer[] symbolizers = SLDs.symbolizers(style);
        for( Symbolizer symbolizer : symbolizers ) {
            if (symbolizer instanceof PolygonSymbolizer) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public void gotFocus() {
        refresh();
    }

    @Override
    public boolean performCancel() {
        return true;
    }

    @Override
    public void styleChanged( Object source ) {

    }

    public boolean okToLeave() {
        return true;
    }

    public boolean performApply() {
        applyStyle();

        return true;
    }

    private void applyStyle() {
       StyleLayer layer = getSelectedLayer();
        if (propertiesEditor == null) return;
        Style newStyle = propertiesEditor.getStyle();
        List<FeatureTypeStyle> featureTypeStyles = newStyle.featureTypeStyles();
        int ftsNum = featureTypeStyles.size();
        if (ftsNum < 1) {
            MessageDialog.openWarning(getShell(), Messages.SimplePolygonEditorPage_1, Messages.SimplePolygonEditorPage_2);
            style = oldStyleCopy;
            setStyle(oldStyleCopy);
            layer.revertAll();
            layer.apply();
            
            StyleEditorDialog dialog = (StyleEditorDialog) getContainer();
            dialog.getCurrentPage().refresh();
            return;
        }
        
        newStyle.setName(layer.getName());

        setStyle(newStyle);

        StyleBlackboard styleBlackboard = layer.getStyleBlackboard();
        styleBlackboard.put(SLDContent.ID, newStyle);
    }

    public boolean performOk() {
        applyStyle();
        propertiesEditor.close();
        return true;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public void refresh() {
        Layer layer = getSelectedLayer();
        IGeoResource resource = layer.getGeoResource();
        if (!resource.canResolve(FeatureSource.class)) {
            return;
        }

        StyleBlackboard styleBlackboard = layer.getStyleBlackboard();
        style = (Style) styleBlackboard.get(SLDContent.ID);
        if (style == null) {
            style = Utilities.createDefaultPolygonStyle();
        }
        DuplicatingStyleVisitor dsv = new DuplicatingStyleVisitor();
        dsv.visit(style);
        oldStyleCopy = (Style) dsv.getCopy();

        if (!isPolygonStyle(style)) {
            stackLayout.topControl = noFeatureLabel;
        } else {
            stackLayout.topControl = propertiesEditor.getControl();
            propertiesEditor.updateStyle(style);
        }
        mainComposite.layout();
        
    }

}
