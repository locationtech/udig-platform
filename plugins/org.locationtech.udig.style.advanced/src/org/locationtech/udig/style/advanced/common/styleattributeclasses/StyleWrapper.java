/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.common.styleattributeclasses;

import static org.locationtech.udig.style.advanced.utils.Utilities.sf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geotools.styling.FeatureTypeConstraint;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.SLDTransformer;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.UserLayer;

import org.locationtech.udig.style.advanced.utils.Utilities;

/**
 * A wrapper for the {@link Style} object to ease gui use.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class StyleWrapper {
    private Style style;
    private String name;
    private List<FeatureTypeStyleWrapper> featureTypeStylesWrapperList = new ArrayList<FeatureTypeStyleWrapper>();

    public StyleWrapper( Style style ) {
        this.style = style;
        name = style.getName();

        List<FeatureTypeStyle> featureTypeStyles = style.featureTypeStyles();
        for( FeatureTypeStyle featureTypeStyle : featureTypeStyles ) {
            FeatureTypeStyleWrapper fstW = new FeatureTypeStyleWrapper(featureTypeStyle, this);
            featureTypeStylesWrapperList.add(fstW);
        }
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
        style.setName(name);
    }

    /**
     * Getter for the list of {@link FeatureTypeStyleWrapper}s.
     * 
     * @return an unmodifiable list of {@link FeatureTypeStyleWrapper}. 
     *              To add or remove items use {@link #addFeatureTypeStyle(FeatureTypeStyle)}
     *              and {@link #removeFeatureTypeStyle(FeatureTypeStyleWrapper)}.
     */
    public List<FeatureTypeStyleWrapper> getFeatureTypeStylesWrapperList() {
        return Collections.unmodifiableList(featureTypeStylesWrapperList);
    }

    /**
     * Facility to get the first rule, if available.
     * 
     * @return the first rule or <code>null</code>.
     */
    public RuleWrapper getFirstRule() {
        if (featureTypeStylesWrapperList.size() > 0) {
            FeatureTypeStyleWrapper featureTypeStyleWrapper = featureTypeStylesWrapperList.get(0);
            List<RuleWrapper> rulesWrapperList = featureTypeStyleWrapper.getRulesWrapperList();
            if (rulesWrapperList.size() > 0) {
                RuleWrapper ruleWrapper = rulesWrapperList.get(0);
                return ruleWrapper;
            }
        }
        return null;
    }

    /**
     * Add a supplied or new {@link FeatureTypeStyle} to the {@link Style}.
     * 
     * @param tmpFts the new {@link FeatureTypeStyle} or null to create a new one.
     * @return the {@link FeatureTypeStyleWrapper} for the new {@link FeatureTypeStyle}.
     */
    public FeatureTypeStyleWrapper addFeatureTypeStyle( FeatureTypeStyle tmpFts ) {
        if (tmpFts == null)
            tmpFts = Utilities.sf.createFeatureTypeStyle();

        style.featureTypeStyles().add(0, tmpFts);

        FeatureTypeStyleWrapper wrapper = new FeatureTypeStyleWrapper(tmpFts, this);
        featureTypeStylesWrapperList.add(0, wrapper);
        return wrapper;
    }

    /**
     * Remove a {@link FeatureTypeStyleWrapper} from the list.
     * 
     * @param ftsW the {@link FeatureTypeStyle} to remove.
     */
    public void removeFeatureTypeStyle( FeatureTypeStyleWrapper ftsW ) {
        FeatureTypeStyle fts = ftsW.getFeatureTypeStyle();
        style.featureTypeStyles().remove(fts);
        featureTypeStylesWrapperList.remove(ftsW);
    }

    /**
     * Clear all the {@link FeatureTypeStyle}s and {@link FeatureTypeStyleWrapper}s.
     */
    public void clear() {
        style.featureTypeStyles().clear();
        featureTypeStylesWrapperList.clear();
    }

    /**
     * Converts a style to its string representation to be written to file.
     * 
     * @param style the style to convert.
     * @return the style string.
     * @throws Exception
     */
    public String toXml() throws Exception {
        StyledLayerDescriptor sld = sf.createStyledLayerDescriptor();
        UserLayer layer = sf.createUserLayer();
        layer.setLayerFeatureConstraints(new FeatureTypeConstraint[]{null});
        sld.addStyledLayer(layer);
        layer.addUserStyle(style);

        SLDTransformer aTransformer = new SLDTransformer();
        aTransformer.setIndentation(4);
        String xml = aTransformer.transform(sld);
        return xml;
    }

    public Style getStyle() {
        return style;
    }

    /**
     * Swap two elements of the list.
     * 
     * @param src the position first element.
     * @param dest the position second element. 
     */
    public void swap( int src, int dest ) {
        List<FeatureTypeStyle> ftss = style.featureTypeStyles();
        if (src >= 0 && src < ftss.size() && dest >= 0 && dest < ftss.size()) {
            Collections.swap(ftss, src, dest);
            Collections.swap(featureTypeStylesWrapperList, src, dest);
        }
    }

}
