/**
 *
 */
package net.refractions.udig.style.sld.editor;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.style.sld.SLDPlugin;
import net.refractions.udig.style.sld.internal.Messages;
import net.refractions.udig.ui.graphics.SLDs;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeItem;
import org.geotools.brewer.color.BrewerPalette;
import org.geotools.brewer.color.PaletteSuitability;
import org.geotools.brewer.color.SampleScheme;
import org.geotools.brewer.color.StyleGenerator;
import org.geotools.filter.Expression;
import org.geotools.filter.function.CustomClassifierFunction;
import org.geotools.styling.Fill;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.Symbolizer;

final class IPaletteCellEditor implements ICellModifier {
		/**
		 *
		 */
		private final StyleThemePage styleThemePage;

		/**
		 * @param styleThemePage
		 */
		IPaletteCellEditor(StyleThemePage styleThemePage) {
			this.styleThemePage = styleThemePage;
		}

		public boolean canModify(Object element, String property) {
		    if (property.equals("styleExpr")) { //$NON-NLS-1$
		        if (element instanceof Rule) {
		            Rule rule = (Rule) element;
		            if (rule.getName().startsWith("rule")) { //$NON-NLS-1$
		                if (rule.hasElseFilter()) {
		                    return false;
		                }
		                return true;
		            } else {
		                return false;
		            }
		        }
		    } else if (property.equals("title")) { //$NON-NLS-1$
		        return true;
		    } else if (property == "colour") { //$NON-NLS-1$
		        return true;
		    }
		    return false;
		}

		public Object getValue(Object element, String property) {
		    if (property.equals("styleExpr")) { //$NON-NLS-1$
		        if (element instanceof Rule) {
		            Rule rule = (Rule) element;
		            if (rule.hasElseFilter()) {
		                return "else"; //$NON-NLS-1$
		            } else {
		                return StyleGenerator.toStyleExpression(rule.getFilter());
		            }
		        }
		    } else if (property.equals("title")) { //$NON-NLS-1$
		        if (element instanceof Rule) {
		            Rule rule = (Rule) element;
		            //the title contains the same value, but we'll obtain it from the rule anyways
		            return rule.getTitle();
		        }
		    } else if (property.equals("colour")) { //$NON-NLS-1$
		        if (element instanceof Rule) {
		            Rule rule = (Rule) element;
		            Color colour = SLDs.toColor(SLDs.colors(rule)[0]);
		            RGB rgb = new RGB(colour.getRed(), colour.getGreen(), colour.getBlue());
		            return rgb;
		        }
		    }

		    return new Object[0];
		}

		public void modify(Object element, String property, Object value) {
		    if (property.equals("styleExpr")) { //$NON-NLS-1$
		        if (element instanceof TreeItem) {
		            String newExpr = (String) value;
		            TreeItem item = (TreeItem) element;
		            Object data = item.getData();
		            //create/modify the custom break and regen
		            this.styleThemePage.customBreak = new CustomClassifierFunction();
		            //TODO: set expression?
		            //figure out which rule has changed
		            int ruleIndex = Integer.parseInt(((Rule) data).getName().substring(4))-1;
		            //fill the custom classifier with the rule boundary values
		            Rule[] rules = this.styleThemePage.getFTS().getRules();
		            for (int i = 0; i < rules.length; i++) {
		                String thisExpr;
		                if (i == ruleIndex) { //use the new value
		                    thisExpr = newExpr;
		                } else { //use the fts value
		                    if (rules[i].getFilter() == null) {
		                        thisExpr = null; //probably an else filter
		                    } else {
		                        thisExpr = StyleGenerator.toStyleExpression(rules[i].getFilter());
		                    }
		                }
		                if (thisExpr == null) {
		                    //TODO: mark as "else"
		                } else if (StyleGenerator.isRanged(thisExpr)) {
		                    String[] minMax = thisExpr.split("\\.\\."); //$NON-NLS-1$
		                    Double min = new Double(minMax[0]);
		                    Double max = new Double(minMax[1]);
		                    this.styleThemePage.customBreak.setRangedValues(i, min, max);
		                } else {
		                    String[] values = thisExpr.split(", "); //$NON-NLS-1$
		                    for (int j = 0; j < values.length; j++) {
		                        this.styleThemePage.customBreak.setExplicitValues(i, values[j]);
		                    }
		                }
		            }
		            Combo breaks = this.styleThemePage.getCombo(StyleThemePage.COMBO_BREAKTYPE);
		            String[] allBreaks = breaks.getItems();
		            int hasCustom = -1;
		            for (int i = 0; i < allBreaks.length; i++) {
		                if (allBreaks[i].equalsIgnoreCase(Messages.StyleEditor_theme_custom)) {
		                    hasCustom = i;
		                    break;
		                }
		            }
		            if (hasCustom > -1) {
		                breaks.select(hasCustom);
		            } else {
		                breaks.add(Messages.StyleEditor_theme_custom);
		                breaks.select(allBreaks.length);
		            }
		            if (this.styleThemePage.inputsValid()) this.styleThemePage.generateTheme();
		            //old method: update the fts
//                        int index = item.getParent().indexOf(item);
//                        //TODO: check that style conforms to our standard
//                        FeatureTypeStyle fts = getFTS();
//                        try {
//                            StyleGenerator.modifyFTS(fts, index, (String) value);
//                        } catch (IllegalFilterException e) {
//                            // TODO Handle IllegalFilterException
//                            throw new RuntimeException(e.getMessage());
//                        }
//                        treeViewer.setInput(fts);
		        }
		    } else if (property.equals("title")) { //$NON-NLS-1$
		        if (element instanceof TreeItem) {
		            TreeItem item = (TreeItem) element;
		            Object data = item.getData();
		            if (data instanceof Rule) {
		                Rule rule = (Rule) data;
		                if (rule.getTitle().equals(value)) return; //don't bother -- value is the same
		                rule.setTitle(value.toString());
		            }
		            this.styleThemePage.treeViewer.refresh();
		        }
		    } else if (property.equals("colour")) { //$NON-NLS-1$
		        if (value == null) return;
		        if (element instanceof Item) {
		            modifyColourColumn(element, value);
		        }
		    }

		}

        private void modifyColourColumn( Object element, Object value ) {
            Item item = (Item) element;
            Object data = item.getData();
            Expression newColorExpr = null;
            Expression oldColorExpr = null;
            Color newColor = null;
            StyleBuilder sb = new StyleBuilder();
            if (value instanceof RGB) {
                RGB rgb = (RGB) value;
                newColor = new Color(rgb.red, rgb.green, rgb.blue);
                String htmlColor = SLDs.toHTMLColor(newColor);
                newColorExpr = sb.literalExpression(htmlColor);
            }
            if (data instanceof Rule) {
                Rule rule = (Rule) data;
                Symbolizer[] symb = rule.getSymbolizers();
                if (symb.length == 1) { //we're only expecting 1
                    if (symb[0] instanceof PolygonSymbolizer) {
                        PolygonSymbolizer ps = (PolygonSymbolizer) symb[0];
                        Fill fill = ps.getFill();
                        oldColorExpr = fill.getColor();
                        fill.setColor(newColorExpr);
                    }else if (symb[0] instanceof PointSymbolizer) {
                        PointSymbolizer ps = (PointSymbolizer) symb[0];
                        Mark[] marks = ps.getGraphic().getMarks();
                        if(marks!=null && marks.length>0){
                            oldColorExpr = marks[0].getFill().getColor();
                            marks[0].getFill().setColor(newColorExpr);
                        }
                    }else if (symb[0] instanceof LineSymbolizer) {
                        LineSymbolizer ps = (LineSymbolizer) symb[0];
                        Stroke stroke = ps.getStroke();
                        oldColorExpr = stroke.getColor();
                        stroke.setColor(newColorExpr);
                    }

                    if (newColorExpr.equals(oldColorExpr)) {
                        return; //don't bother, same colour
                    }
                        //determine if the palette is already customized
                        if (this.styleThemePage.customPalette == null) {
                            int numClasses = new Integer(this.styleThemePage.getCombo(StyleThemePage.COMBO_CLASSES).getItem(this.styleThemePage.getCombo(StyleThemePage.COMBO_CLASSES).getSelectionIndex())).intValue();
                            //create the palette from the current one
                            BrewerPalette pal = (BrewerPalette) ((StructuredSelection) this.styleThemePage.paletteTable.getSelection()).getFirstElement();
                            this.styleThemePage.customPalette = new BrewerPalette();
                            PaletteSuitability suitability = new PaletteSuitability();
                            //suitability.
                            //customPalette.setColors()
                            SampleScheme newScheme = new SampleScheme();
                            Color[] colors = new Color[pal.getMaxColors()];
                            Color[] allColorsArray = pal.getColors();
                            List<Color> allColors = new ArrayList<Color>();
                            for (int i = 0; i < allColorsArray.length; i++) {
                                allColors.add(allColorsArray[i]);
                            }
                            String unknown = "?"; //$NON-NLS-1$
                            for (int i = 0; i < pal.getMaxColors(); i++) {
                                if (i > 0) {
                                    //create a simple scheme
                                    int[] scheme = new int[i+1];
                                    for (int j = 0; j < i+1; j++) {
                                        scheme[j] = j;
                                    }
                                    newScheme.setSampleScheme(i+1, scheme);
                                    //set the suitability to unknown
                                    try {
                                        suitability.setSuitability(i+1, new String[] {unknown, unknown, unknown, unknown, unknown, unknown});
                                    } catch (IOException e) {
                                        SLDPlugin.log("setSuitability() failed", e); //$NON-NLS-1$
                                        return;
                                    }
                                }
                                //copy the color
                                if (i < numClasses) {
                                    //copy the colors directly over
                                    colors[i] = pal.getColor(i, numClasses);
                                    allColors.remove(colors[i]);
                                } else {
                                    //find unique colors to fill in the rest of the palette
                                    colors[i] = allColors.remove(0);
                                }
                            }
                            //newScheme.setSampleScheme(3, new int[] {0,1});
                            this.styleThemePage.customPalette.setPaletteSuitability(suitability);
                            this.styleThemePage.customPalette.setColors(colors);
                            this.styleThemePage.customPalette.setColorScheme(newScheme);
                            this.styleThemePage.customPalette.setName(Messages.StyleEditor_theme_custom);
                            this.styleThemePage.customPalette.setDescription(Messages.StyleEditor_theme_custom_desc);
                            this.styleThemePage.customPalette.setType(pal.getType());
                            if (!this.styleThemePage.getBrewer().hasPalette(Messages.StyleEditor_theme_custom)) {
                                this.styleThemePage.getBrewer().registerPalette(this.styleThemePage.customPalette);
                            }
                        }
                        //seek and destroy the old colour
                        Color[] colors = this.styleThemePage.customPalette.getColors();
                        int expectedIndex = -1;
                        if (rule.getName().toLowerCase().startsWith("rule")) { //$NON-NLS-1$
                            expectedIndex = Integer.parseInt(rule.getName().substring(4))-1;
                        }
                        int actualIndex = -1;
                        for (int i = 0; i < colors.length; i++) {
                            if (sb.literalExpression(SLDs.toHTMLColor(colors[i])).equals(oldColorExpr)) {
                                actualIndex = i;
                                if (expectedIndex == i) {
                                    //we found the correct old color where we expected to
                                    break;
                                } //otherwise, keep looking just in case
                            }
                        }
                        if (actualIndex == -1) {
                            SLDPlugin.log("color match unsuccessful... "+oldColorExpr+" vs "+newColorExpr, null); //$NON-NLS-1$ //$NON-NLS-2$
                            //TODO: use different colour matching technique
                            return;
                        }
                        colors[actualIndex] = newColor;
                        this.styleThemePage.customPalette.setColors(colors);
                        this.styleThemePage.paletteTable.setInput(this.styleThemePage.getBrewer());
                        this.styleThemePage.paletteTable.setSelection(new StructuredSelection(this.styleThemePage.customPalette));
                    }
                }
            this.styleThemePage.treeViewer.refresh();
        }
	}
