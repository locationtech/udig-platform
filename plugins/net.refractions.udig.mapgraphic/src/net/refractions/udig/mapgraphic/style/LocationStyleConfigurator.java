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
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;
import net.refractions.udig.style.IStyleConfigurator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Configures a "location style", which indicates that a particular layer can
 * be relocated, such as a scalebar or a legend.
 * 
 * @author Richard Gould
 * @since 0.6.0
 * @see LocationStyleContent
 */
public final class LocationStyleConfigurator extends IStyleConfigurator implements SelectionListener {

    /** vertical alignment constants * */
    private static final String TOP = Messages.ScalebarStyleConfigurator_top; 
    private static final String MIDDLE = Messages.ScalebarStyleConfigurator_middle; 
    private static final String BOTTOM = Messages.ScalebarStyleConfigurator_bottom; 

    /** horizontal alignment constants * */
    private static final String LEFT = Messages.ScalebarStyleConfigurator_left; 
    private static final String CENTER = Messages.ScalebarStyleConfigurator_center; 
    private static final String RIGHT = Messages.ScalebarStyleConfigurator_right; 

    /** ui widgets * */
    private Combo xCombo;
    private Combo yCombo;
    private Text widthText;
    private Text heightText;


    /**
     * @see net.refractions.udig.style.StyleConfigurator#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {
        parent.setLayout( new GridLayout( 2, true ));
        
        GridData gridData;
        
        Label xLabel = new Label(parent, SWT.RIGHT);
        xLabel.setText(Messages.ScalebarStyleConfigurator_horizontalAlignment); 

        xCombo = new Combo(parent, SWT.DROP_DOWN);
        xCombo.setItems(new String[]{LEFT, CENTER, RIGHT});
        xCombo.select(0);
        xCombo.addSelectionListener(this);

        Label yLabel = new Label(parent, SWT.RIGHT);
        yLabel.setText(Messages.ScalebarStyleConfigurator_verticalAlignment); 

        yCombo = new Combo(parent, SWT.DROP_DOWN);
        yCombo.setItems(new String[]{TOP, MIDDLE, BOTTOM});
        yCombo.select(0);
        yCombo.addSelectionListener(this);
        
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
	    IBlackboard blackboard = getStyleBlackboard();
        Rectangle rect = (Rectangle) blackboard.get(LocationStyleContent.ID);

        if (rect == null) {
            rect = LocationStyleContent.createDefaultStyle();
            setLeft(rect);
            setTop(rect);

            blackboard.put(LocationStyleContent.ID, rect);
            ((StyleBlackboard) blackboard).setSelected(new String[]{LocationStyleContent.ID});
        }

        if (isLeft(rect))
            xCombo.select(0);
        else if (isCenter(rect))
            xCombo.select(1);
        else if (isRight(rect))
            xCombo.select(2);
        
        if (isTop(rect)) 
            yCombo.select(0);
        else if (isMiddle(rect)) 
            yCombo.select(1);
        else if (isBottom(rect)) 
            yCombo.select(2);
        
        widthText.setText(Integer.toString(rect.width));
        heightText.setText(Integer.toString(rect.height));
    }

	@Override
	public void preApply() {

        int width = Integer.parseInt(widthText.getText());
        int height = Integer.parseInt(heightText.getText());
        
        StyleBlackboard styleBlackboard = getLayer().getStyleBlackboard();
        Rectangle rec = (Rectangle)styleBlackboard.get(LocationStyleContent.ID);
        styleBlackboard.setSelected(new String[]{LocationStyleContent.ID});
        rec.setSize(width, height);
        styleBlackboard.put(LocationStyleContent.ID, rec);
	}
	
    public boolean canStyle( Layer layer ) {
        return layer.hasResource(MapGraphic.class);
    }


    /*
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetSelected( SelectionEvent e ) {
        IBlackboard blackboard = getStyleBlackboard();
        Rectangle rect = (Rectangle) blackboard.get(LocationStyleContent.ID);

        if (rect == null) {
            rect = LocationStyleContent.createDefaultStyle();
            setLeft(rect);
            setTop(rect);

            blackboard.put(LocationStyleContent.ID, rect);
            ((StyleBlackboard) getStyleBlackboard()).setSelected(new String[]{LocationStyleContent.ID});
        }
        
        //read object state from ui widgets
        switch (xCombo.getSelectionIndex()) {
            case 0:
                setLeft(rect);
                break;
            case 1:
                setCenter(rect);
                break;
            case 2:
                setRight(rect);
                
        }
        
        switch(yCombo.getSelectionIndex()) {
            case 0:
                setTop(rect);
                break;
            case 1:
                setMiddle(rect);
                break;
            case 2:
                setBottom(rect);
        }
    }
    
    /*
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetDefaultSelected( SelectionEvent e ) {
        //do nothing
    }

    
    protected boolean isLeft( Rectangle rect ) {
        return rect.x == LocationStyleContent.XPAD_LEFT;
    }

    protected void setLeft( Rectangle rect ) {
        rect.x = LocationStyleContent.XPAD_LEFT;
    }

    protected boolean isCenter( Rectangle rect ) {
        IMapDisplay display = getMapDisplay();

        int x = display.getWidth() / 2;
        x -= rect.width / 2;

        return rect.x == x;
    }

    protected void setCenter( Rectangle rect ) {
        IMapDisplay display = getMapDisplay();

        int x = display.getWidth() / 2;
        x -= rect.width / 2;

        rect.x = x;
    }

    protected boolean isRight( Rectangle rect ) {
        return rect.x == -LocationStyleContent.XPAD_RIGHT;
    }

    protected void setRight( Rectangle rect ) {
        rect.x = -LocationStyleContent.XPAD_RIGHT;
    }

    protected boolean isTop( Rectangle rect ) {
        return rect.y == LocationStyleContent.YPAD_TOP; //+ rect.height;
    }

    protected void setTop( Rectangle rect ) {
        rect.y = LocationStyleContent.YPAD_TOP; //+ rect.height;
    }

    protected boolean isMiddle( Rectangle rect ) {
        IMapDisplay display = getMapDisplay();

        int y = display.getHeight() / 2;
        y -= rect.height / 2;

        return rect.y == y;
    }

    protected void setMiddle( Rectangle rect ) {
        IMapDisplay display = getMapDisplay();

        int y = display.getHeight() / 2;
        y -= rect.height / 2;

        rect.y = y;
    }

    protected boolean isBottom( Rectangle rect ) {
        return rect.y == -LocationStyleContent.YPAD_BOTTOM;
    }

    protected void setBottom( Rectangle rect ) {
        rect.y = -LocationStyleContent.YPAD_BOTTOM;
    }
    
    protected IMapDisplay getMapDisplay() {
        return getLayer().getMap().getRenderManager().getMapDisplay();
    }

}
