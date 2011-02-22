/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.mapgraphic.style;

import java.awt.Rectangle;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.internal.Messages;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.style.IStyleConfigurator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Configures a "location style", which indicates that a particular layer can
 * be relocated, such as a scalebar or a legend.
 *
 * @author Richard Gould
 * @since 0.6.0
 */
public class LocationStyleConfigurator extends IStyleConfigurator {

    private Text xText;
    private Text yText;
    private Text widthText;
    private Text heightText;


    /**
     * @see net.refractions.udig.style.StyleConfigurator#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {
        GridData gridData;

        GridLayout gridLayout = new GridLayout();
        int columns = 1;
        gridLayout.numColumns = columns;
        parent.setLayout(gridLayout);

        gridData = new GridData();
        Label xLabel = new Label(parent, SWT.NONE);
        xLabel.setText(Messages.LocationStyleConfigurator_x);
        xLabel.setLayoutData(gridData);

        gridData = new GridData(GridData.FILL_HORIZONTAL);
        xText = new Text(parent, SWT.BORDER);
        xText.setLayoutData(gridData);

        gridData = new GridData();
        Label yLabel = new Label(parent, SWT.NONE);
        yLabel.setText(Messages.LocationStyleConfigurator_y);
        yLabel.setLayoutData(gridData);

        gridData = new GridData(GridData.FILL_HORIZONTAL);
        yText = new Text(parent, SWT.BORDER);
        yText.setLayoutData(gridData);

        gridData = new GridData();
        Label widthLabel = new Label(parent, SWT.NONE);
        widthLabel.setText(Messages.LocationStyleConfigurator_width);
        widthLabel.setLayoutData(gridData);

        gridData = new GridData(GridData.FILL_HORIZONTAL);
        widthText = new Text(parent, SWT.BORDER);
        widthText.setLayoutData(gridData);

        gridData = new GridData();
        Label heightLabel = new Label(parent, SWT.NONE);
        heightLabel.setText(Messages.LocationStyleConfigurator_height);
        heightLabel.setLayoutData(gridData);

        gridData = new GridData(GridData.FILL_HORIZONTAL);
        heightText = new Text(parent, SWT.BORDER);
        heightText.setLayoutData(gridData);



    }


	public void init() {
		// do nothing

	}

	public void refresh() {
        //do nothing
    }

	@Override
	public void preApply() {

        int x = Integer.parseInt(xText.getText());
        int y = Integer.parseInt(yText.getText());
        int width = Integer.parseInt(widthText.getText());
        int height = Integer.parseInt(heightText.getText());

        Rectangle location = new Rectangle(x, y, width, height);
        StyleBlackboard styleBlackboard = getLayer().getStyleBlackboard();
        styleBlackboard.put(LocationStyleContent.ID, location);
        styleBlackboard.setSelected(new String[]{LocationStyleContent.ID});
	}

    public boolean canStyle( Layer layer ) {
        return layer.hasResource(MapGraphic.class);
    }



}
