/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.common;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.geotools.styling.Style;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.FeatureTypeStyleWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.StyleWrapper;
import org.locationtech.udig.style.advanced.internal.Messages;
import org.locationtech.udig.style.advanced.internal.WrapperUtilities;
import org.locationtech.udig.style.advanced.utils.Utilities;
import org.locationtech.udig.style.internal.StyleLayer;
import org.locationtech.udig.style.sld.SLD;
import org.locationtech.udig.ui.graphics.AWTSWTImageUtils;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;

/**
 * Common class for properties editors.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public abstract class PropertiesEditor {
    protected final StyleLayer layer;

    protected StyleWrapper styleWrapper;

    protected TreeViewer groupRulesTreeViewer;

    protected Image previewImage;

    protected SLD type;

    protected Name geometryPropertyName;

    protected List<String> stringAttributeNames = new ArrayList<String>();

    protected List<String> numericAttributeNames = new ArrayList<String>();

    protected List<String> allAttributeNames = new ArrayList<String>();

    protected Composite mainComposite;

    protected Canvas previewCanvas;

    protected static final int PREVIEWWIDTH = 150;

    protected static final int PREVIEWHEIGHT = 150;

    protected Color white = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);

    protected Color gray = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);

    public PropertiesEditor(StyleLayer layer) {
        this.layer = layer;
        loadWithAttributeTypes(layer);

        SimpleFeatureType schema = layer.getSchema();
        if (SLD.isPoint(schema)) {
            type = SLD.POINT;
        } else if (SLD.isLine(schema)) {
            type = SLD.LINE;
        } else if (SLD.isPolygon(schema)) {
            type = SLD.POLYGON;
        }

    }

    public void open(Composite parent, Style style) {

        if (style == null) {
            switch (type) {
            case POINT:
                style = Utilities.createDefaultPointStyle();
                break;
            case LINE:
                style = Utilities.createDefaultLineStyle();
                break;
            case POLYGON:
                style = Utilities.createDefaultPolygonStyle();
                break;
            default:
                throw new IllegalArgumentException();
            }
        }
        styleWrapper = new StyleWrapper(style);

        createGui(parent);
    }

    protected abstract void createGui(Composite parent);

    public Composite getControl() {
        return mainComposite;
    }

    public Style getStyle() {
        return styleWrapper.getStyle();
    }

    public void close() {
        if (previewImage != null) {
            previewImage.dispose();
            previewImage = null;
        }
    }

    public void updateStyle(Style style) {
        if (style == null) {
            style = Utilities.createDefaultPointStyle();
        }

        styleWrapper = new StyleWrapper(style);

        groupRulesTreeViewer.setInput(styleWrapper.getFeatureTypeStylesWrapperList());
        RuleWrapper ruleWrapper = styleWrapper.getFirstRule();
        if (ruleWrapper != null) {
            setRuleToSelected(ruleWrapper);
            refreshTreeViewer(ruleWrapper);
            refreshPreviewCanvasOnStyle();
        }
    }

    /**
     * Reloads the list of all rules currently available.
     */
    public void reloadGroupsAndRules() {
        List<FeatureTypeStyleWrapper> featureTypeStylesWrapperList = styleWrapper
                .getFeatureTypeStylesWrapperList();
        if (featureTypeStylesWrapperList.size() > 0) {
            groupRulesTreeViewer.setInput(featureTypeStylesWrapperList);
        } else {
            groupRulesTreeViewer.setInput(null);
        }
    }

    /**
     * Refreshes the name/label of the supplied rule.
     */
    public void refreshTreeViewer(RuleWrapper rule) {
        groupRulesTreeViewer.update(rule.getParent(), null);
        groupRulesTreeViewer.update(rule, null);
    }

    /**
     * Redraws the preview image with the current rules.
     */
    public void refreshPreviewCanvasOnStyle() {
        if (previewImage == null || previewImage.getImageData().width < 1) {
            Display display = Display.getDefault();
            previewImage = new Image(display, PREVIEWWIDTH, PREVIEWHEIGHT);
        }
        GC gc = null;
        try {
            gc = new GC(previewImage);
        } catch (Exception e) {
            e.printStackTrace();
            previewImage = null;
            return;
        }
        Rectangle clientArea = previewCanvas.getClientArea();
        gc.setBackground(white);
        gc.fillRectangle(clientArea);

        List<FeatureTypeStyleWrapper> featureTypeStylesWrapperList = styleWrapper
                .getFeatureTypeStylesWrapperList();
        if (type != null) {
            for (FeatureTypeStyleWrapper featureTypeStyleWrapper : featureTypeStylesWrapperList) {
                List<RuleWrapper> rulesWrapperList = featureTypeStyleWrapper.getRulesWrapperList();
                BufferedImage tmpImage = WrapperUtilities.rulesWrapperToImage(rulesWrapperList,
                        PREVIEWWIDTH, PREVIEWHEIGHT, type);
                Image convertToSWTImage = AWTSWTImageUtils.convertToSWTImage(tmpImage);
                gc.drawImage(convertToSWTImage, 0, 0);
                convertToSWTImage.dispose();
            }
        }
        gc.dispose();
        previewCanvas.redraw();
    }

    protected void setRuleToSelected(RuleWrapper ruleWrapper) {
        IStructuredSelection sel = new StructuredSelection(ruleWrapper);
        groupRulesTreeViewer.setSelection(sel, true);
        // rulesTableViewer.refresh(rule, true, true);
        groupRulesTreeViewer.update(ruleWrapper, null);
    }

    protected RuleWrapper getSelectedRule() {
        TreeSelection selection = (TreeSelection) groupRulesTreeViewer.getSelection();
        Object firstElement = selection.getFirstElement();
        if (firstElement instanceof RuleWrapper) {
            RuleWrapper wrapper = (RuleWrapper) firstElement;
            return wrapper;
        }
        return null;
    }

    protected FeatureTypeStyleWrapper getSelectedFtsw() {
        TreeSelection selection = (TreeSelection) groupRulesTreeViewer.getSelection();
        Object firstElement = selection.getFirstElement();
        if (firstElement instanceof FeatureTypeStyleWrapper) {
            FeatureTypeStyleWrapper wrapper = (FeatureTypeStyleWrapper) firstElement;
            return wrapper;
        }
        return null;
    }

    protected void swap(boolean doUp) {
        FeatureTypeStyleWrapper selectedFtsw = getSelectedFtsw();
        RuleWrapper selectedRule = getSelectedRule();
        if (selectedFtsw != null) {
            List<FeatureTypeStyleWrapper> featureTypeStylesWrapperList = selectedFtsw.getParent()
                    .getFeatureTypeStylesWrapperList();
            if (featureTypeStylesWrapperList.size() < 2) {
                return;
            }
            int ftsWIndex = featureTypeStylesWrapperList.indexOf(selectedFtsw);
            if (doUp) {
                if (ftsWIndex > 0) {
                    styleWrapper.swap(ftsWIndex, ftsWIndex - 1);
                    reloadGroupsAndRules();
                    refreshPreviewCanvasOnStyle();
                }
            } else {
                if (ftsWIndex < featureTypeStylesWrapperList.size() - 1) {
                    styleWrapper.swap(ftsWIndex + 1, ftsWIndex);
                    reloadGroupsAndRules();
                    refreshPreviewCanvasOnStyle();
                }
            }
        } else if (selectedRule != null) {
            FeatureTypeStyleWrapper featureTypeStyleWrapper = selectedRule.getParent();
            List<RuleWrapper> rulesWrapperList = featureTypeStyleWrapper.getRulesWrapperList();
            if (rulesWrapperList.size() < 2) {
                return;
            }
            int ruleWrapperIndex = rulesWrapperList.indexOf(selectedRule);
            if (doUp) {
                if (ruleWrapperIndex > 0) {
                    featureTypeStyleWrapper.swap(ruleWrapperIndex - 1, ruleWrapperIndex);
                    reloadGroupsAndRules();
                    refreshPreviewCanvasOnStyle();
                }
            } else {
                if (ruleWrapperIndex < rulesWrapperList.size() - 1) {
                    featureTypeStyleWrapper.swap(ruleWrapperIndex, ruleWrapperIndex + 1);
                    reloadGroupsAndRules();
                    refreshPreviewCanvasOnStyle();
                }
            }
        } else {
            MessageDialog.openWarning(mainComposite.getShell(), Messages.PropertiesEditor_0,
                    Messages.PropertiesEditor_1);
            return;
        }
    }

    private void loadWithAttributeTypes(StyleLayer selectedLayer) {
        SimpleFeatureType featureType = selectedLayer.getSchema();
        if (featureType != null) {
            for (int i = 0; i < featureType.getAttributeCount(); i++) {
                AttributeDescriptor attributeType = featureType.getDescriptor(i);
                if (!(attributeType instanceof GeometryDescriptor)) { // don't include the geometry
                    if (isNumber(attributeType)) {
                        numericAttributeNames.add(attributeType.getName().getLocalPart());
                    } else if (isString(attributeType)) {
                        stringAttributeNames.add(attributeType.getName().getLocalPart());
                    }
                    allAttributeNames.add(attributeType.getName().getLocalPart());
                } else {
                    geometryPropertyName = attributeType.getName();
                }
            }
            // add none option
            numericAttributeNames.add(0, Utilities.NONE);
            allAttributeNames.add(0, Utilities.NONE);
            stringAttributeNames.add(0, Utilities.NONE);
        }
    }

    private boolean isNumber(AttributeDescriptor attributeType) {
        if (Number.class.isAssignableFrom(attributeType.getType().getBinding())) {
            return true;
        }
        return false;
    }

    private boolean isString(AttributeDescriptor attributeType) {
        if (String.class.isAssignableFrom(attributeType.getType().getBinding())) {
            return true;
        }
        return false;
    }

    public Name getGeometryPropertyName() {
        return geometryPropertyName;
    }

    public List<String> getNumericAttributeNames() {
        return numericAttributeNames;
    }

    public List<String> getAllAttributeNames() {
        return allAttributeNames;
    }

    public List<String> getStringAttributeNames() {
        return stringAttributeNames;
    }
}
