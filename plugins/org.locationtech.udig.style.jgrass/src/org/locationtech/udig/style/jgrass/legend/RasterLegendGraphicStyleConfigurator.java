/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.jgrass.legend;

import java.awt.Color;
import java.io.File;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.locationtech.udig.catalog.jgrass.activeregion.dialogs.JGRasterChooserDialog;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapGeoResource;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.style.IStyleConfigurator;
import org.locationtech.udig.style.jgrass.messages.Messages;
import org.locationtech.udig.ui.ColorEditor;

public class RasterLegendGraphicStyleConfigurator extends IStyleConfigurator implements SelectionListener, ModifyListener {

    private Text xposText;
    private ColorEditor fontColour;
    private ColorEditor backgroundColour;
    private Text yposText;
    private Text legHeightText;
    private Text legWidthText;
    private Text boxWidthText;
    private Button isroundedButton;
    private Text backgroundAlphaText;
    private Text forgroundAlphaText;
    private ColorEditor foregroundColor;
    private Text titleText;
    private Label mapNameLabel;
    private RasterLegendStyle style;

    /*
     * verticalMargin = 3; horizontalMargin = 2; verticalSpacing = 5; horizontalSpacing = 3;
     * indentSize = 10; imageHeight = 16; imageWidth = 16; maxWidth = -1; maxHeight = -1;
     * foregroundColour = Color.BLACK; backgroundColour = Color.WHITE; location = new Point(30, 10);
     */

    public void createControl( Composite parent ) {

        ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        Composite c = new Composite(scrollComposite, SWT.None);
        c.setLayout(new GridLayout());
        c.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

        GridData layoutData = null;
        Group chooseMapGroup = new Group(c, SWT.BORDER);
        GridLayout layout1 = new GridLayout(3, false);
        chooseMapGroup.setLayout(layout1);
        chooseMapGroup.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
        Label mapLabel = new Label(chooseMapGroup, SWT.NONE);
        mapLabel.setLayoutData(layoutData);
        mapLabel.setText(Messages.getString("LegendGraphicStyleConfigurator.map4legend")); //$NON-NLS-1$
        mapNameLabel = new Label(chooseMapGroup, SWT.BORDER);
        GridData gd = new GridData();
        gd.widthHint = 100;
        mapNameLabel.setLayoutData(gd);

        final Button mapButton = new Button(chooseMapGroup, SWT.BORDER | SWT.PUSH);
        GridData gd2 = new GridData();
        gd2.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
        mapButton.setLayoutData(gd2);
        mapButton.setText(Messages.getString("LegendGraphicStyleConfigurator.browse")); //$NON-NLS-1$
        mapButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                JGRasterChooserDialog cDialog = new JGRasterChooserDialog(null);
                cDialog.open(mapButton.getShell(), SWT.SINGLE);

                List<JGrassMapGeoResource> selectedResource = cDialog.getSelectedResources();
                JGrassMapGeoResource jGrassMapGeoResource = selectedResource.get(0);
                File mapFile = jGrassMapGeoResource.getMapFile();
                String name = mapFile.getName();
                mapNameLabel.setText(name);
                if (style == null)
                    checkStyle();
                style.mapPath = mapFile.getAbsolutePath();
            }
        });

        Group propertiesGroup = new Group(c, SWT.BORDER);

        GridLayout layout2 = new GridLayout(2, true);
        propertiesGroup.setLayout(layout2);

        Label titleLabel = new Label(propertiesGroup, SWT.NONE);
        titleLabel.setLayoutData(layoutData);
        titleLabel.setText(Messages.getString("LegendGraphicStyleConfigurator.title")); //$NON-NLS-1$
        titleText = new Text(propertiesGroup, SWT.BORDER);
        titleText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        Label xposLabel = new Label(propertiesGroup, SWT.NONE);
        xposLabel.setLayoutData(layoutData);
        xposLabel.setText(Messages.getString("LegendGraphicStyleConfigurator.xpos")); //$NON-NLS-1$
        xposText = new Text(propertiesGroup, SWT.BORDER);
        xposText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        Label yposLabel = new Label(propertiesGroup, SWT.NONE);
        yposLabel.setLayoutData(layoutData);
        yposLabel.setText(Messages.getString("LegendGraphicStyleConfigurator.ypos")); //$NON-NLS-1$
        yposText = new Text(propertiesGroup, SWT.BORDER);
        yposText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        Label legWidthLabel = new Label(propertiesGroup, SWT.NONE);
        legWidthLabel.setLayoutData(layoutData);
        legWidthLabel.setText(Messages.getString("LegendGraphicStyleConfigurator.legendwidth")); //$NON-NLS-1$
        legWidthText = new Text(propertiesGroup, SWT.BORDER);
        legWidthText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        Label legHeightLabel = new Label(propertiesGroup, SWT.NONE);
        legHeightLabel.setLayoutData(layoutData);
        legHeightLabel.setText(Messages.getString("LegendGraphicStyleConfigurator.legendheight")); //$NON-NLS-1$
        legHeightText = new Text(propertiesGroup, SWT.BORDER);
        legHeightText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        Label boxWidthLabel = new Label(propertiesGroup, SWT.NONE);
        boxWidthLabel.setLayoutData(layoutData);
        boxWidthLabel.setText(Messages.getString("LegendGraphicStyleConfigurator.boxwidth")); //$NON-NLS-1$
        boxWidthText = new Text(propertiesGroup, SWT.BORDER);
        boxWidthText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        isroundedButton = new Button(propertiesGroup, SWT.BORDER | SWT.CHECK);
        isroundedButton.setText(Messages.getString("LegendGraphicStyleConfigurator.roundedrect")); //$NON-NLS-1$
        GridData gdata = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gdata.horizontalSpan = 2;
        isroundedButton.setLayoutData(gdata);

        Label fontColourLabel = new Label(propertiesGroup, SWT.NONE);
        fontColourLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        fontColourLabel.setText(Messages.getString("LegendGraphicStyleConfigurator.fontcolor")); //$NON-NLS-1$
        fontColour = new ColorEditor(propertiesGroup);

        Label backgroundColourLabel = new Label(propertiesGroup, SWT.NONE);
        backgroundColourLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        backgroundColourLabel.setText(Messages.getString("LegendGraphicStyleConfigurator.backgroundcolor")); //$NON-NLS-1$
        backgroundColour = new ColorEditor(propertiesGroup);

        Label backgroundAlphaLabel = new Label(propertiesGroup, SWT.NONE);
        backgroundAlphaLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        backgroundAlphaLabel.setText(Messages.getString("LegendGraphicStyleConfigurator.backgroundalpha")); //$NON-NLS-1$
        backgroundAlphaText = new Text(propertiesGroup, SWT.BORDER);
        backgroundAlphaText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        Label foregroundColourLabel = new Label(propertiesGroup, SWT.NONE);
        foregroundColourLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        foregroundColourLabel.setText(Messages.getString("LegendGraphicStyleConfigurator.foregroundcolor")); //$NON-NLS-1$
        foregroundColor = new ColorEditor(propertiesGroup);

        Label forgroundAlphaLabel = new Label(propertiesGroup, SWT.NONE);
        forgroundAlphaLabel.setLayoutData(layoutData);
        forgroundAlphaLabel.setText(Messages.getString("LegendGraphicStyleConfigurator.foregroundalpha")); //$NON-NLS-1$
        forgroundAlphaText = new Text(propertiesGroup, SWT.BORDER);
        forgroundAlphaText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        c.layout();
        Point size = c.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        c.setSize(size);
        scrollComposite.setContent(c);

        titleText.addModifyListener(this);
        xposText.addModifyListener(this);
        yposText.addModifyListener(this);
        legWidthText.addModifyListener(this);
        legHeightText.addModifyListener(this);
        boxWidthText.addModifyListener(this);
        backgroundAlphaText.addModifyListener(this);
        forgroundAlphaText.addModifyListener(this);
        isroundedButton.addSelectionListener(this);
        foregroundColor.addButtonSelectionListener(this);
        backgroundColour.addButtonSelectionListener(this);
        fontColour.addButtonSelectionListener(this);
    }
    @Override
    public boolean canStyle( Layer aLayer ) {
        return aLayer.hasResource(RasterLegendGraphic.class);
    }

    private void checkStyle() {
        IMap activeMap = ApplicationGIS.getActiveMap();

        IBlackboard styleBlackboard = activeMap.getBlackboard();
        if (style == null) {
            style = (RasterLegendStyle) styleBlackboard.get(RasterLegendStyleContent.ID);
        }
        if (style == null) {
            style = RasterLegendStyleContent.createDefault();
            styleBlackboard.put(RasterLegendStyleContent.ID, style);
            // styleBlackboard.setSelected(new String[]{RasterLegendStyleContent.ID});
        }
    }

    @Override
    protected void refresh() {
        checkStyle();

        String mapPath = style.mapPath;
        if (mapPath == null) {
            mapPath = "";
        }
        File mapFile = new File(mapPath);
        String mName = null;
        if (mapFile.exists()) {
            mName = mapFile.getName();
        }
        if (mName == null)
            mName = "";
        mapNameLabel.setText(mName);

        fontColour.setColorValue(new RGB(style.fontColor.getRed(), style.fontColor.getGreen(), style.fontColor.getBlue()));
        foregroundColor.setColorValue(new RGB(style.foregroundColor.getRed(), style.foregroundColor.getGreen(),
                style.foregroundColor.getBlue()));
        backgroundColour.setColorValue(new RGB(style.backgroundColor.getRed(), style.backgroundColor.getGreen(),
                style.backgroundColor.getBlue()));

        if (style.titleString == null)
            style.titleString = " ";
        titleText.setText(style.titleString);

        xposText.setText(Integer.toString(style.xPos));
        yposText.setText(Integer.toString(style.yPos));
        legWidthText.setText(Integer.toString(style.legendWidth));
        legHeightText.setText(Integer.toString(style.legendHeight));
        boxWidthText.setText(Integer.toString(style.boxWidth));
        forgroundAlphaText.setText(Integer.toString(style.fAlpha));
        backgroundAlphaText.setText(Integer.toString(style.bAlpha));
        isroundedButton.setSelection(style.isRoundedRectangle);

    }

    public void preApply() {
        updateBlackboard();
    }

    private void updateBlackboard() {
        IMap activeMap = ApplicationGIS.getActiveMap();
        IBlackboard styleBlackboard = activeMap.getBlackboard();
        style = (RasterLegendStyle) styleBlackboard.get(RasterLegendStyleContent.ID);

        if (style == null) {
            style = RasterLegendStyleContent.createDefault();
            styleBlackboard.put(RasterLegendStyleContent.ID, style);
            // styleBlackboard.setSelected(new String[]{RasterLegendStyleContent.ID});
        }

        RGB bg = backgroundColour.getColorValue();
        try {
            int bAlpha = Integer.parseInt(backgroundAlphaText.getText());
            style.backgroundColor = new Color(bg.red, bg.green, bg.blue, bAlpha);
        } catch (Exception e) {
            style.backgroundColor = new Color(bg.red, bg.green, bg.blue);
        }
        bg = foregroundColor.getColorValue();
        try {
            int fAlpha = Integer.parseInt(forgroundAlphaText.getText());
            style.foregroundColor = new Color(bg.red, bg.green, bg.blue, fAlpha);
        } catch (Exception e) {
            style.foregroundColor = new Color(bg.red, bg.green, bg.blue);
        }
        bg = fontColour.getColorValue();
        style.fontColor = new Color(bg.red, bg.green, bg.blue);

        style.titleString = titleText.getText();
        style.xPos = Integer.parseInt(xposText.getText());
        style.yPos = Integer.parseInt(yposText.getText());
        style.legendHeight = Integer.parseInt(legHeightText.getText());
        style.legendWidth = Integer.parseInt(legWidthText.getText());
        style.boxWidth = Integer.parseInt(boxWidthText.getText());
        style.isRoundedRectangle = isroundedButton.getSelection();

        styleBlackboard.put(RasterLegendStyleContent.ID, style);
    }

    public void widgetSelected( SelectionEvent e ) {
        updateBlackboard();
    }

    public void widgetDefaultSelected( SelectionEvent e ) {
        updateBlackboard();
    }

    public void modifyText( ModifyEvent e ) {
        // updateBlackboard();
    }
}
