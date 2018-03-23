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

import static org.locationtech.udig.style.advanced.utils.Utilities.createDefaultLineRule;
import static org.locationtech.udig.style.advanced.utils.Utilities.createDefaultPointRule;
import static org.locationtech.udig.style.advanced.utils.Utilities.createDefaultPolygonRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Rule;

/**
 * A wrapper for the {@link FeatureTypeStyle} object to ease gui use.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class FeatureTypeStyleWrapper {
    private FeatureTypeStyle featureTypeStyle;
    private String name;
    private List<RuleWrapper> rulesWrapperList = new ArrayList<RuleWrapper>();
    private final StyleWrapper parent;

    public FeatureTypeStyleWrapper( FeatureTypeStyle featureTypeStyle, StyleWrapper parent ) {
        this.featureTypeStyle = featureTypeStyle;
        this.parent = parent;
        name = featureTypeStyle.getName();

        List<Rule> rules = featureTypeStyle.rules();
        for( Rule rule : rules ) {
            RuleWrapper ruleWrapper = new RuleWrapper(rule, this);
            rulesWrapperList.add(ruleWrapper);
        }
    }

    public StyleWrapper getParent() {
        return parent;
    }

    public FeatureTypeStyle getFeatureTypeStyle() {
        return featureTypeStyle;
    }

    public String getName() {
        int indexOf = parent.getStyle().featureTypeStyles().indexOf(featureTypeStyle);
        name = "group" + indexOf;
        featureTypeStyle.setName(name);
        return name;
    }

    public void setName( String name ) {
        this.name = name;
        featureTypeStyle.setName(name);
    }

    /**
     * Getter for the list of {@link RuleWrapper}s.
     * 
     * @return an unmodifiable list of {@link RuleWrapper}. 
     *              To add or remove items use {@link #addRule(Rule, Class)}
     *              and {@link #removeRule(RuleWrapper)}.
     */
    public List<RuleWrapper> getRulesWrapperList() {
        return Collections.unmodifiableList(rulesWrapperList);
    }

    /**
     * Add a supplied or new {@link Rule} to the {@link FeatureTypeStyle}.
     * 
     * @param tmpRule the new {@link Rule} or null to create a new one.
     * @param symbolizerWrapperClass the class for which to create the symbolizer wrapper. 
     *                  Needed only in the case of new creation of rule. 
     * @return the {@link RuleWrapper} for the new {@link Rule}.
     */
    public RuleWrapper addRule( Rule tmpRule, Class< ? > symbolizerWrapperClass ) {
        if (tmpRule == null) {
            if (symbolizerWrapperClass.isAssignableFrom(PointSymbolizerWrapper.class)) {
                tmpRule = createDefaultPointRule();
            } else if (symbolizerWrapperClass.isAssignableFrom(LineSymbolizerWrapper.class)) {
                tmpRule = createDefaultLineRule();
            } else if (symbolizerWrapperClass.isAssignableFrom(PolygonSymbolizerWrapper.class)) {
                tmpRule = createDefaultPolygonRule();
            } else {
                throw new IllegalArgumentException("Unsupported symbolizer."); //$NON-NLS-1$
            }
        }

        featureTypeStyle.rules().add(0, tmpRule);

        RuleWrapper wrapper = new RuleWrapper(tmpRule, this);
        rulesWrapperList.add(0, wrapper);
        return wrapper;
    }

    /**
     * Remove a {@link RuleWrapper} from the list.
     * 
     * @param remRule the {@link Rule} to remove.
     * @return the {@link FeatureTypeStyleWrapper} for the new {@link FeatureTypeStyle}.
     */
    public void removeRule( RuleWrapper remRule ) {
        Rule rule = remRule.getRule();
        featureTypeStyle.rules().remove(rule);
        rulesWrapperList.remove(remRule);
    }

    /**
     * Clear all the {@link Rule}s and {@link RuleWrapper}s.
     */
    public void clear() {
        featureTypeStyle.rules().clear();
        rulesWrapperList.clear();
    }

    /**
     * Swap two elements of the list.
     * 
     * @param src the position first element.
     * @param dest the position second element. 
     */
    public void swap( int src, int dest ) {
        List<Rule> rules = featureTypeStyle.rules();
        if (src >= 0 && src < rules.size() && dest >= 0 && dest < rules.size()) {
            Collections.swap(rules, src, dest);
            Collections.swap(rulesWrapperList, src, dest);
        }
    }

}
