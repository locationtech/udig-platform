/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * (C) C.U.D.A.M. Universita' di Trento
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.style.advanced.raster;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.RectIterFactory;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.internal.Layer;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.geotools.coverage.grid.GridCoverage2D;

/**
 * The composite holding the raster map style logic.
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class CoverageColorEditor extends Composite implements SelectionListener {

    private ArrayList<CoverageRule> listOfRules = null;
    private Button addRuleButton = null;
    private Button removeRuleButton = null;
    private Button moveRuleUpButton = null;
    private Button moveRuleDownButton = null;
    private Composite rulesComposite = null;
    private Group alphaGroup = null;
    private Scale alphaScale = null;
    private ScrolledComposite scrolledRulesComposite = null;
    private Layer layer;
    private Label alphaLabel = null;
    private Combo predefinedRulesCombo;
    private Button resetColormapButton;
    private HashMap<String, String[][]> colorRulesMap;
    private GridCoverage2D gridCoverage;

    private double[] minMax = null;
    private Text novaluesText;

    public CoverageColorEditor( Composite parent, int style ) {
        super(parent, style);
        listOfRules = new ArrayList<CoverageRule>();
        initialize();
    }

    private void initialize() {

        GridData gridData3 = new GridData();
        gridData3.horizontalAlignment = GridData.FILL;
        gridData3.grabExcessHorizontalSpace = true;
        gridData3.verticalAlignment = GridData.CENTER;
        GridData gridData2 = new GridData();
        gridData2.horizontalAlignment = GridData.FILL;
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.verticalAlignment = GridData.CENTER;
        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = GridData.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.verticalAlignment = GridData.CENTER;
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = GridData.CENTER;
        addRuleButton = new Button(this, SWT.NONE);
        addRuleButton.setText("+");
        addRuleButton.setLayoutData(gridData);
        addRuleButton.addSelectionListener(this);
        removeRuleButton = new Button(this, SWT.NONE);
        removeRuleButton.setText("-");
        removeRuleButton.setLayoutData(gridData1);
        removeRuleButton.addSelectionListener(this);
        moveRuleUpButton = new Button(this, SWT.UP | SWT.ARROW);
        moveRuleUpButton.setLayoutData(gridData2);
        moveRuleUpButton.addSelectionListener(this);
        moveRuleDownButton = new Button(this, SWT.DOWN | SWT.ARROW);
        moveRuleDownButton.setLayoutData(gridData3);
        moveRuleDownButton.addSelectionListener(this);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;
        gridLayout.makeColumnsEqualWidth = true;
        this.setLayout(gridLayout);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        this.setLayoutData(gd);
        createScrolledRulesComposite();

        GridData gridDataBC = new GridData();
        gridDataBC.horizontalAlignment = GridData.FILL;
        gridDataBC.horizontalSpan = 4;
        gridDataBC.verticalAlignment = GridData.CENTER;
        Composite buttonComposite = new Composite(this, SWT.NONE);
        buttonComposite.setLayoutData(gridDataBC);
        buttonComposite.setLayout(new GridLayout(8, true));

        Button selectAllButton = new Button(buttonComposite, SWT.PUSH);
        GridData selectAllGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        selectAllGD.horizontalSpan = 3;
        selectAllButton.setLayoutData(selectAllGD);
        selectAllButton.setText("Select all");
        selectAllButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                for( CoverageRule cRule : listOfRules ) {
                    cRule.setActive(true);
                }
                redoLayout();
            }
        });

        Button unselectAllButton = new Button(buttonComposite, SWT.PUSH);
        GridData unselectAllGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        unselectAllGD.horizontalSpan = 3;
        unselectAllButton.setLayoutData(unselectAllGD);
        unselectAllButton.setText("Unselect all");
        unselectAllButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                for( CoverageRule cRule : listOfRules ) {
                    cRule.setActive(false);
                }
                redoLayout();
            }
        });

        Button invertSelectionButton = new Button(buttonComposite, SWT.PUSH);
        GridData invertSelectionGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        invertSelectionGD.horizontalSpan = 2;
        invertSelectionButton.setLayoutData(invertSelectionGD);
        invertSelectionButton.setText("Invert selection");
        invertSelectionButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                for( CoverageRule cRule : listOfRules ) {
                    cRule.setActive(!cRule.isActive());
                }
                redoLayout();
            }
        });

        GridData resetGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        resetGD.horizontalSpan = 8;
        resetColormapButton = new Button(buttonComposite, SWT.NONE);
        resetColormapButton.setText("reset colormap");
        resetColormapButton.setLayoutData(resetGD);
        resetColormapButton.addSelectionListener(this);

        // predefined rules combo
        Label predefinedRulesLabel = new Label(buttonComposite, SWT.NONE);
        GridData rulesLabelGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        rulesLabelGD.horizontalSpan = 3;
        predefinedRulesLabel.setLayoutData(rulesLabelGD);
        predefinedRulesLabel.setText("Set from predefined table");

        predefinedRulesCombo = new Combo(buttonComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData comboGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboGD.horizontalSpan = 5;
        predefinedRulesCombo.setLayoutData(comboGD);
        colorRulesMap = PredefinedColorRules.getColorsFolder(true);
        Set<String> keySet = colorRulesMap.keySet();
        String[] rulesNames = (String[]) keySet.toArray(new String[keySet.size()]);
        Arrays.sort(rulesNames);
        predefinedRulesCombo.setItems(rulesNames);
        predefinedRulesCombo.addSelectionListener(this);

        createRulesComposite();

        GridLayout novaluesLayout = new GridLayout();
        novaluesLayout.numColumns = 4;
        GridData novaluesGD = new GridData();
        novaluesGD.horizontalSpan = 4;
        novaluesGD.verticalAlignment = GridData.CENTER;
        novaluesGD.grabExcessHorizontalSpace = true;
        novaluesGD.horizontalAlignment = GridData.FILL;
        Group novaluesGroup = new Group(this, SWT.NONE);
        novaluesGroup.setLayoutData(novaluesGD);
        novaluesGroup.setLayout(novaluesLayout);
        novaluesGroup.setText("Novalues to ignore");
        novaluesText = new Text(novaluesGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        novaluesText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        novaluesText.setText("-9999.0");
        Button addNovalueRulesButton = new Button(novaluesGroup, SWT.PUSH);
        addNovalueRulesButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        addNovalueRulesButton.setText("Add rules for novalues");
        addNovalueRulesButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                double[] nvArray = getExtraNovalues();
                if (nvArray.length > 0) {
                    for( double nv : nvArray ) {
                        Color fromColor = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
                        Color toColor = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
                        CoverageRule rule = new CoverageRule(new double[]{nv, nv}, fromColor, toColor, 0.0, true);
                        listOfRules.add(0, rule);
                        redoLayout();
                    }
                }
            }
        });

        createAlphaGroup();
        // setSize(new Point(395, 331));
    }

    /**
     * This method initializes rulesComposite
     */
    private void createRulesComposite() {
        GridData gridData4 = new GridData();
        gridData4.horizontalSpan = 4;
        gridData4.verticalAlignment = GridData.FILL;
        gridData4.grabExcessHorizontalSpace = true;
        gridData4.grabExcessVerticalSpace = true;
        gridData4.horizontalAlignment = GridData.FILL;
        rulesComposite = new Composite(scrolledRulesComposite, SWT.NONE);
        rulesComposite.setLayout(new GridLayout());
        rulesComposite.setLayoutData(gridData4);
        scrolledRulesComposite.setContent(rulesComposite);
    }

    /**
     * This method initializes scrolledRulesComposite
     */
    private void createScrolledRulesComposite() {
        GridData gridData7 = new GridData();
        gridData7.horizontalSpan = 4;
        gridData7.verticalAlignment = GridData.FILL;
        gridData7.grabExcessVerticalSpace = true;
        gridData7.grabExcessHorizontalSpace = false;
        gridData7.horizontalAlignment = GridData.FILL;

        scrolledRulesComposite = new ScrolledComposite(this, SWT.V_SCROLL | SWT.BORDER);
        scrolledRulesComposite.setLayoutData(gridData7);
        scrolledRulesComposite.setExpandHorizontal(true);
        scrolledRulesComposite.setExpandVertical(true);
        scrolledRulesComposite.setMinHeight(2000);
    }

    /**
     * This method initializes alphaGroup
     */
    private void createAlphaGroup() {
        GridLayout gridLayout1 = new GridLayout();
        gridLayout1.numColumns = 4;
        GridData gridData6 = new GridData();
        gridData6.horizontalAlignment = GridData.FILL;
        gridData6.grabExcessHorizontalSpace = true;
        gridData6.horizontalSpan = 3;
        gridData6.verticalAlignment = GridData.CENTER;
        GridData gridData5 = new GridData();
        gridData5.horizontalSpan = 4;
        gridData5.verticalAlignment = GridData.CENTER;
        gridData5.grabExcessHorizontalSpace = true;
        gridData5.horizontalAlignment = GridData.FILL;
        alphaGroup = new Group(this, SWT.NONE);
        alphaGroup.setLayoutData(gridData5);
        alphaGroup.setLayout(gridLayout1);
        alphaGroup.setText("alpha");
        alphaScale = new Scale(alphaGroup, SWT.NONE);
        alphaScale.setLayoutData(gridData6);
        alphaScale.setMinimum(0);
        alphaScale.setMaximum(100);
        alphaScale.setPageIncrement(10);
        alphaScale.setSelection(100);
        alphaLabel = new Label(alphaGroup, SWT.NONE);
        alphaLabel.setText(alphaScale.getSelection() + "");
        alphaScale.addListener(SWT.Selection, new Listener(){
            public void handleEvent( Event event ) {
                int perspectiveValue = alphaScale.getSelection();
                alphaLabel.setText(perspectiveValue + "");
            }
        });
    }

    public void widgetDefaultSelected( SelectionEvent e ) {
    }

    public void widgetSelected( SelectionEvent e ) {
        Object source = e.getSource();
        if (source instanceof Button) {
            Button selectedButton = (Button) source;

            if (selectedButton.equals(addRuleButton)) {
                // add an empty rule to te composite
                CoverageRule r = new CoverageRule();
                listOfRules.add(0, r);
                redoLayout();
            } else if (selectedButton.equals(removeRuleButton)) {
                for( int i = 0; i < listOfRules.size(); i++ ) {
                    CoverageRule r = listOfRules.get(i);
                    if (r.isActive()) {
                        listOfRules.remove(r);
                        // if (i > 0) {
                        // i--;
                        // }
                    }
                }
                redoLayout();
            } else if (selectedButton.equals(moveRuleUpButton)) {
                for( int i = 0; i < listOfRules.size(); i++ ) {
                    CoverageRule r = listOfRules.get(i);
                    if (r.isActive()) {
                        if (i > 0) {
                            listOfRules.remove(r);
                            listOfRules.add(i - 1, r);
                        }
                    }
                }
                redoLayout();
            } else if (selectedButton.equals(moveRuleDownButton)) {
                for( int i = 0; i < listOfRules.size(); i++ ) {
                    CoverageRule r = listOfRules.get(i);
                    if (r.isActive()) {
                        if (i < listOfRules.size() - 1) {
                            listOfRules.remove(r);
                            listOfRules.add(i + 1, r);
                            i++;
                        }
                    }
                }
                redoLayout();
            } else if (selectedButton.equals(resetColormapButton)) {
                // final OperationJAI op = new OperationJAI("Extrema");
                // ParameterValueGroup params = op.getParameters();
                // params.parameter("Source").setValue(gridCoverage);
                // gridCoverage = (GridCoverage2D) op.doOperation(params, null);
                // System.out.println(((double[]) gridCoverage.getProperty("minimum"))[0]);
                // System.out.println(((double[]) gridCoverage.getProperty("maximum"))[0]);

                double[] nvArray = getExtraNovalues();

                RenderedImage renderedImage = gridCoverage.getRenderedImage();
                RectIter iter = RectIterFactory.create(renderedImage, null);
                double min = Double.MAX_VALUE;
                double max = -Double.MIN_VALUE;
                do {
                    do {
                        double value = iter.getSampleDouble();
                        if (!Double.isNaN(value)) {
                            boolean jump = false;
                            for( int i = 0; i < nvArray.length; i++ ) {
                                if (value - nvArray[i] < 10E-6) {
                                    jump = true;
                                    break;
                                }
                            }
                            if (jump) {
                                continue;
                            }

                            if (value < min) {
                                min = value;
                            }
                            if (value > max) {
                                max = value;
                            }
                        }
                    } while( !iter.nextPixelDone() );
                    iter.startPixels();
                } while( !iter.nextLineDone() );
                minMax = new double[]{min, max};

                Color fromColor = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
                Color toColor = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
                CoverageRule rule = new CoverageRule(minMax, fromColor, toColor, 1.0, true);
                listOfRules.clear();
                listOfRules.add(rule);
                redoLayout();
            }
        }
        if (source instanceof Combo) {
            Combo combo = (Combo) source;
            if (combo.equals(predefinedRulesCombo)) {
                int selectionIndex = predefinedRulesCombo.getSelectionIndex();
                String item = predefinedRulesCombo.getItem(selectionIndex);
                String[][] colorRules = colorRulesMap.get(item);

                try {
                    listOfRules.clear();
                    List<RuleValues> rulesValuesList = PredefinedColorRules.getRulesValuesList(colorRules, minMax);
                    for( RuleValues ruleValues : rulesValuesList ) {
                        double[] fromToValues = new double[]{ruleValues.fromValue, ruleValues.toValue};
                        Color fromColor = RuleValues.asSWT(ruleValues.fromColor);
                        Color toColor = RuleValues.asSWT(ruleValues.toColor);
                        CoverageRule rule = new CoverageRule(fromToValues, fromColor, toColor, 1.0, true);
                        listOfRules.add(rule);
                    }
                    redoLayout();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        }
    }

    private double[] getExtraNovalues() {
        List<Double> novaluesList = new ArrayList<Double>();
        String novaluesStr = novaluesText.getText();
        if (novaluesStr != null && novaluesStr.length() > 0) {
            String[] nvSplit = novaluesStr.split(",");
            for( String nvStr : nvSplit ) {
                try {
                    double nv = Double.parseDouble(nvStr.trim());
                    novaluesList.add(nv);
                } catch (Exception ex) {
                    // ignore numbers that are not ok
                }
            }
        }
        double[] nvArray = new double[novaluesList.size()];
        for( int i = 0; i < nvArray.length; i++ ) {
            nvArray[i] = novaluesList.get(i);
        }
        return nvArray;
    }

    /**
     * Set the layer that called this style editor. Needed for putting the alpha value into the
     * blackboard whenever it something changes.
     * 
     * @param layer
     */
    public void setLayer( Layer layer ) {
        this.layer = layer;
        IGeoResource resource = layer.getGeoResource();
        try {
            gridCoverage = resource.resolve(GridCoverage2D.class, new NullProgressMonitor());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAlphaValue( final int value ) {
        Display.getDefault().syncExec(new Runnable(){
            public void run() {
                alphaScale.setSelection(value);
                alphaLabel.setText(String.valueOf(value));
            }
        });
    }

    public int getAlphaVAlue() {
        try {
            return Integer.parseInt(alphaLabel.getText());
        } catch (Exception e) {
            return 100;
        }
    }

    public void setRulesList( ArrayList<CoverageRule> listOfRules ) {
        this.listOfRules = listOfRules;
        /*
         * take minMax out of that
         */
        if (listOfRules.size() > 0) {
            CoverageRule coverageRule = listOfRules.get(0);
            double from = coverageRule.getFromToValues()[0];
            coverageRule = listOfRules.get(listOfRules.size() - 1);
            double to = coverageRule.getFromToValues()[1];
            minMax = new double[]{from, to};
        }
        redoLayout();
    }

    protected void redoLayout() {

        Display.getDefault().syncExec(new Runnable(){
            public void run() {
                // remove the rules from the composite
                Control[] rulesControls = rulesComposite.getChildren();
                for( int i = 0; i < rulesControls.length; i++ ) {
                    rulesControls[i].dispose();
                }

                // recreate the rules composites from the list
                for( CoverageRule rule : listOfRules ) {
                    new CoverageRuleComposite(rulesComposite, SWT.BORDER, rule);
                }

                rulesComposite.layout();
            }
        });
    }

    public ArrayList<CoverageRule> getRulesList() {
        return listOfRules;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
