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

import static org.locationtech.udig.style.advanced.utils.Utilities.DEFAULT_COLOR;
import static org.locationtech.udig.style.advanced.utils.Utilities.ff;
import static org.locationtech.udig.style.advanced.utils.Utilities.sb;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.graphics.FontData;
import org.geotools.styling.AnchorPoint;
import org.geotools.styling.Displacement;
import org.geotools.styling.Fill;
import org.geotools.styling.Font;
import org.geotools.styling.Halo;
import org.geotools.styling.LabelPlacement;
import org.geotools.styling.LinePlacement;
import org.geotools.styling.PointPlacement;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.locationtech.udig.style.advanced.internal.VendorOptions;
import org.locationtech.udig.style.sld.SLD;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.filter.expression.Expression;

/**
 * A wrapper for a {@link TextSymbolizer} to ease interaction with gui.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class TextSymbolizerWrapper extends SymbolizerWrapper {

    private String fontFamily;
    private String fontStyle;
    private String fontWeight;
    private String fontSize;
    private String color;
    private String opacity;
    private String haloColor;
    private String anchorX;
    private String anchorY;
    private String displacementX;
    private String displacementY;
    private String rotation;
    private String maxDisplacementVO;
    private String repeatVO;
    private String autoWrapVO;
    private String spaceAroundVO;
    private Font font;

    private TextSymbolizer textSymbolizer;
    private Fill fill;
    private Halo halo;
    private Fill haloFill;
    private PointPlacement pointPlacement;
    private LinePlacement linePlacement;
    private AnchorPoint anchorPoint;
    private Displacement displacement;
    private String haloRadius;
    private String initialGap;
    private String perpendicularOffset;
    private String followLineVO;
    private String maxAngleDeltaVO;
    private SLD geomType;
    private String labelName;

    public TextSymbolizerWrapper( Symbolizer symbolizer, RuleWrapper parent, SLD geomType ) {
        super(symbolizer, parent);
        this.geomType = geomType;

        textSymbolizer = (TextSymbolizer) symbolizer;

        Expression labelExpression = textSymbolizer.getLabel();
        if (labelExpression != null) {
            labelName = expressionToString(labelExpression);
        }

        font = textSymbolizer.getFont();
        if (font != null) {
            List<Expression> family = font.getFamily();
            fontFamily = family.get(0).evaluate(null, String.class);

            Expression styleExpression = font.getStyle();
            fontStyle = styleExpression.evaluate(null, String.class);

            Expression styleWeight = font.getWeight();
            fontWeight = styleWeight.evaluate(null, String.class);

            Expression styleSize = font.getSize();
            fontSize = styleSize.evaluate(null, String.class);
        }

        fill = textSymbolizer.getFill();
        if (fill != null) {
            Expression colorExpression = fill.getColor();
            color = colorExpression.evaluate(null, String.class);
            Expression opacityExpression = fill.getOpacity();
            if (opacityExpression != null) {
                opacity = expressionToString(opacityExpression);
            }
        }

        halo = textSymbolizer.getHalo();
        if (halo != null) {
            haloFill = halo.getFill();
            Expression haloColorExpression = haloFill.getColor();
            haloColor = haloColorExpression.evaluate(null, String.class);

            Expression haloRadiusExpression = halo.getRadius();
            haloRadius = haloRadiusExpression.evaluate(null, String.class);
        }

        LabelPlacement labelPlacement = textSymbolizer.getLabelPlacement();
        if (geomType != null) {
            switch( geomType ) {
            case POINT:
            case POLYGON:
                if (labelPlacement instanceof PointPlacement) {
                    pointPlacement = (PointPlacement) labelPlacement;
                    if (pointPlacement != null) {
                        anchorPoint = pointPlacement.getAnchorPoint();
                        if (anchorPoint != null) {
                            Expression anchorPointXExpression = anchorPoint.getAnchorPointX();
                            anchorX = anchorPointXExpression.evaluate(null, String.class);
                            Expression anchorPointYExpression = anchorPoint.getAnchorPointY();
                            anchorY = anchorPointYExpression.evaluate(null, String.class);
                        }

                        displacement = pointPlacement.getDisplacement();
                        if (displacement != null) {
                            Expression displacementXExpression = displacement.getDisplacementX();
                            displacementX = displacementXExpression.evaluate(null, String.class);
                            Expression displacementYExpression = displacement.getDisplacementY();
                            displacementY = displacementYExpression.evaluate(null, String.class);
                        } else {
                            displacementX = "0.0"; //$NON-NLS-1$
                            displacementY = "0.0"; //$NON-NLS-1$
                        }

                        Expression rotationExpression = pointPlacement.getRotation();
                        rotation = expressionToString(rotationExpression);
                    }
                }
                break;
            case LINE:
                if (labelPlacement instanceof LinePlacement) {
                    linePlacement = (LinePlacement) labelPlacement;
                    if (linePlacement != null) {
                        Expression initialGapExpression = linePlacement.getInitialGap();
                        if (initialGapExpression != null)
                            initialGap = initialGapExpression.evaluate(null, String.class);

                        Expression perpendicularOffsetExpression = linePlacement.getPerpendicularOffset();
                        if (perpendicularOffset != null)
                            perpendicularOffset = perpendicularOffsetExpression.evaluate(null, String.class);
                    }
                }
                break;
            default:
                break;
            }
        }

        /*
         * vendoroptions
         */
        Map<String, String> vendorOptions = textSymbolizer.getOptions();
        Set<Entry<String, String>> entrySet = vendorOptions.entrySet();
        for( Entry<String, String> entry : entrySet ) {
            String key = entry.getKey();
            String value = entry.getValue();

            switch( VendorOptions.toVendorOption(key) ) {
            case VENDOROPTION_MAXDISPLACEMENT:
                maxDisplacementVO = value;
                break;
            case VENDOROPTION_REPEAT:
                repeatVO = value;
                break;
            case VENDOROPTION_AUTOWRAP:
                autoWrapVO = value;
                break;
            case VENDOROPTION_SPACEAROUND:
                spaceAroundVO = value;
                break;
            case VENDOROPTION_FOLLOWLINE:
                followLineVO = value;
                break;
            case VENDOROPTION_MAXANGLEDELTA:
                maxAngleDeltaVO = value;
                break;
            default:
                break;
            }
        }
    }

    public FontData[] getFontData() {
        return SLDs.textFont((TextSymbolizer) getSymbolizer());
    }

    private void checkFontExists() {
        if (font == null) {
            font = sb.createFont("Arial", false, false, 12); //$NON-NLS-1$
            textSymbolizer.setFont(font);
        }
    }

    private void checkFillExists() {
        if (fill == null) {
            fill = sb.createFill(ff.literal(DEFAULT_COLOR));
            textSymbolizer.setFill(fill);
        }
    }

    private void checkHaloFillExists() {
        if (haloFill == null) {
            haloFill = sb.createFill(ff.literal(DEFAULT_COLOR));
            checkHaloExists();
            halo.setFill(haloFill);
        }
    }

    private void checkHaloExists() {
        if (halo == null) {
            halo = sb.createHalo();
            textSymbolizer.setHalo(halo);
        }
    }

    private void checkPlacementExists() {
        switch( geomType ) {
        case POINT:
        case POLYGON:
            if (pointPlacement == null) {
                pointPlacement = sb.createPointPlacement();
                textSymbolizer.setLabelPlacement(pointPlacement);
            }
            break;
        case LINE:
            if (linePlacement == null) {
                linePlacement = sb.createLinePlacement(0.0);
                textSymbolizer.setLabelPlacement(linePlacement);
            }
            break;

        default:
            break;
        }
    }

    private void checkAnchorPointExists() {
        if (anchorPoint == null) {
            anchorPoint = sb.createAnchorPoint(0.5, 0.5);
            checkPlacementExists();
            pointPlacement.setAnchorPoint(anchorPoint);
        }
    }

    private void checkDisplacementExists() {
        if (displacement == null) {
            displacement = sb.createDisplacement(0.0, 0.0);
            checkPlacementExists();
            pointPlacement.setDisplacement(displacement);
        }
    }

    public void setLabelName( String labelName, boolean fromField ) {
        this.labelName = labelName;
        if (fromField) {
            textSymbolizer.setLabel(ff.property(labelName));
        } else {
            textSymbolizer.setLabel(ff.literal(labelName));
        }
    }

    public void setFont( Font font ) {
        this.font = font;
        textSymbolizer.setFont(font);
    }

    public void setFontFamily( String fontFamily ) {
        this.fontFamily = fontFamily;
        checkFontExists();
        font.getFamily().set(0, ff.literal(fontFamily));
    }

    public void setFontStyle( String fontStyle ) {
        this.fontStyle = fontStyle;
        checkFontExists();
        font.setStyle(ff.literal(fontStyle));
    }

    public void setFontWeight( String fontWeight ) {
        this.fontWeight = fontWeight;
        checkFontExists();
        font.setWeight(ff.literal(fontWeight));
    }

    public void setFontSize( String fontSize ) {
        this.fontSize = fontSize;
        checkFontExists();
        font.setSize(ff.literal(fontSize));
    }

    public void setColor( String color ) {
        this.color = color;
        checkFillExists();
        fill.setColor(ff.literal(color));
    }

    public void setOpacity( String opacity, boolean fromField ) {
        this.opacity = opacity;
        checkFillExists();
        if (fromField) {
            fill.setOpacity(ff.property(opacity));
        } else {
            fill.setOpacity(ff.literal(opacity));
        }
    }

    public void setHaloColor( String haloColor ) {
        this.haloColor = haloColor;
        checkHaloFillExists();
        haloFill.setColor(ff.literal(haloColor));
    }

    public void setHaloRadius( String haloRadius ) {
        this.haloRadius = haloRadius;
        checkHaloExists();
        halo.setRadius(ff.literal(haloRadius));
    }

    public void setAnchorX( String anchorX ) {
        this.anchorX = anchorX;
        checkAnchorPointExists();
        anchorPoint.setAnchorPointX(ff.literal(anchorX));
    }

    public void setAnchorY( String anchorY ) {
        this.anchorY = anchorY;
        checkAnchorPointExists();
        anchorPoint.setAnchorPointY(ff.literal(anchorY));
    }

    public void setDisplacement( String displacement ) {
        if (displacement == null || displacement.indexOf(',') == -1) {
            return;
        }
        String[] split = displacement.split(","); //$NON-NLS-1$
        try {
            Double.parseDouble(split[0]);
            Double.parseDouble(split[1]);
            setDisplacementX(split[0]);
            setDisplacementY(split[1]);
        } catch (Exception e) {
            // ignore wrong stuff
        }
    }

    public void setDisplacementX( String displacementX ) {
        this.displacementX = displacementX;
        checkDisplacementExists();
        displacement.setDisplacementX(ff.literal(displacementX));
    }

    public void setDisplacementY( String displacementY ) {
        this.displacementY = displacementY;
        checkDisplacementExists();
        displacement.setDisplacementY(ff.literal(displacementY));
    }

    public void setRotation( String rotation, boolean fromField ) {
        this.rotation = rotation;
        checkPlacementExists();
        if (fromField) {
            pointPlacement.setRotation(ff.property(rotation));
        } else {
            pointPlacement.setRotation(ff.literal(rotation));
        }
    }

    public void setInitialGap( String initialGap ) {
        this.initialGap = initialGap;
        checkPlacementExists();
        linePlacement.setInitialGap(ff.literal(initialGap));
    }

    public void setPerpendicularOffset( String perpendicularOffset ) {
        this.perpendicularOffset = perpendicularOffset;
        checkPlacementExists();
        linePlacement.setPerpendicularOffset(ff.literal(perpendicularOffset));
    }

    public void setMaxDisplacementVO( String maxDisplacementVO ) {
        this.maxDisplacementVO = maxDisplacementVO;
        if (maxDisplacementVO == null || maxDisplacementVO.equals("")) { //$NON-NLS-1$
            textSymbolizer.getOptions().remove(VendorOptions.VENDOROPTION_MAXDISPLACEMENT.toString());
        } else {
            textSymbolizer.getOptions().put(VendorOptions.VENDOROPTION_MAXDISPLACEMENT.toString(), maxDisplacementVO);
        }
    }

    public void setRepeatVO( String repeatVO ) {
        this.repeatVO = repeatVO;
        if (repeatVO == null || repeatVO.equals("") || geomType != SLD.LINE) { //$NON-NLS-1$
            textSymbolizer.getOptions().remove(VendorOptions.VENDOROPTION_REPEAT.toString());
        } else {
            textSymbolizer.getOptions().put(VendorOptions.VENDOROPTION_REPEAT.toString(), repeatVO);
        }
    }

    public void setAutoWrapVO( String autoWrapVO ) {
        this.autoWrapVO = autoWrapVO;
        if (autoWrapVO == null || autoWrapVO.equals("")) { //$NON-NLS-1$
            textSymbolizer.getOptions().remove(VendorOptions.VENDOROPTION_AUTOWRAP.toString());
        } else {
            textSymbolizer.getOptions().put(VendorOptions.VENDOROPTION_AUTOWRAP.toString(), autoWrapVO);
        }
    }

    public void setSpaceAroundVO( String spaceAroundVO ) {
        this.spaceAroundVO = spaceAroundVO;
        if (spaceAroundVO == null || spaceAroundVO.equals("")) { //$NON-NLS-1$
            textSymbolizer.getOptions().remove(VendorOptions.VENDOROPTION_SPACEAROUND.toString());
        } else {
            textSymbolizer.getOptions().put(VendorOptions.VENDOROPTION_SPACEAROUND.toString(), spaceAroundVO);
        }
    }

    public void setFollowLineVO( String followLineVO ) {
        this.followLineVO = followLineVO;
        if (followLineVO == null || followLineVO.equals("") || geomType != SLD.LINE) { //$NON-NLS-1$
            textSymbolizer.getOptions().remove(VendorOptions.VENDOROPTION_FOLLOWLINE.toString());
        } else {
            textSymbolizer.getOptions().put(VendorOptions.VENDOROPTION_FOLLOWLINE.toString(), followLineVO);
        }
    }

    public void setMaxAngleDeltaVO( String maxAngleDeltaVO ) {
        this.maxAngleDeltaVO = maxAngleDeltaVO;
        if (maxAngleDeltaVO == null || maxAngleDeltaVO.equals("") || geomType != SLD.LINE) { //$NON-NLS-1$
            textSymbolizer.getOptions().remove(VendorOptions.VENDOROPTION_MAXANGLEDELTA.toString());
        } else {
            textSymbolizer.getOptions().put(VendorOptions.VENDOROPTION_MAXANGLEDELTA.toString(), maxAngleDeltaVO);
        }
    }

    // getters
    public String getLabelName() {
        return labelName;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public String getFontSize() {
        return fontSize;
    }

    public String getColor() {
        return color;
    }

    public String getOpacity() {
        return opacity;
    }

    public String getHaloColor() {
        return haloColor;
    }

    public String getHaloRadius() {
        return haloRadius;
    }

    public String getAnchorX() {
        return anchorX;
    }

    public String getAnchorY() {
        return anchorY;
    }

    public String getDisplacementX() {
        return displacementX;
    }

    public String getDisplacementY() {
        return displacementY;
    }

    public String getRotation() {
        return rotation;
    }

    public String getInitialGap() {
        return initialGap;
    }

    public String getPerpendicularOffset() {
        return perpendicularOffset;
    }

    public String getMaxDisplacementVO() {
        return maxDisplacementVO;
    }

    public String getRepeatVO() {
        return repeatVO;
    }

    public String getAutoWrapVO() {
        return autoWrapVO;
    }

    public String getSpaceAroundVO() {
        return spaceAroundVO;
    }

    public String getFollowLineVO() {
        return followLineVO;
    }

    public String getMaxAngleDeltaVO() {
        return maxAngleDeltaVO;
    }
}
