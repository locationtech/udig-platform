/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.style.advanced.common;

import static eu.udig.style.advanced.utils.Utilities.*;

import java.awt.image.BufferedImage;
import java.util.List;

import net.refractions.udig.style.sld.SLD;
import net.refractions.udig.ui.graphics.AWTSWTImageUtils;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import eu.udig.style.advanced.common.styleattributeclasses.FeatureTypeStyleWrapper;
import eu.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import eu.udig.style.advanced.internal.Messages;

/**
 * A tree view label provider for {@link FeatureTypeStyleWrapper}s with {@link RuleWrapper} childs.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GroupRulesTreeLabelProvider extends LabelProvider {

    private final SLD type;

    public GroupRulesTreeLabelProvider( SLD type ) {
        this.type = type;
    }

    public Image getImage( Object element ) {
        BufferedImage image = null;
        if (element instanceof FeatureTypeStyleWrapper) {
            FeatureTypeStyleWrapper ftsW = (FeatureTypeStyleWrapper) element;
            List<RuleWrapper> rulesWrapperList = ftsW.getRulesWrapperList();
            image = rulesWrapperToImage(rulesWrapperList, 16, 16, type);
        } else if (element instanceof RuleWrapper) {
            RuleWrapper ruleWrapper = (RuleWrapper) element;
            image = rulesWrapperToImage(ruleWrapper, 16, 16, type);
        }
        if (image != null) {
            Image convertToSWTImage = AWTSWTImageUtils.convertToSWTImage(image);
            return convertToSWTImage;
        }
        return null;
    }

    public String getText( Object element ) {
        if (element instanceof FeatureTypeStyleWrapper) {
            FeatureTypeStyleWrapper ftsW = (FeatureTypeStyleWrapper) element;
            String name = ftsW.getName();
            if (name == null || name.length() == 0) {
                name = Messages.GroupRulesTreeLabelProvider_0;
                name = checkSameNameFeatureTypeStyle(ftsW.getParent().getFeatureTypeStylesWrapperList(), name);
                ftsW.setName(name);
            }
            return name;
        } else if (element instanceof RuleWrapper) {
            RuleWrapper ruleWrapper = (RuleWrapper) element;
            String name = ruleWrapper.getName();
            if (name == null || name.length() == 0) {
                name = Messages.GroupRulesTreeLabelProvider_1;
                name = checkSameNameRule(ruleWrapper.getParent().getRulesWrapperList(), name);
                ruleWrapper.setName(name);
            }
            return name;
        }
        return null;
    }

}
