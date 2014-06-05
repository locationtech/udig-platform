/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.mapgraphic.style;

import java.awt.Point;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.internal.Messages;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.style.IStyleConfigurator;

/**
 * Configurator for the position style.  Allows uers to pick 
 * and x and y location.
 * 
 * @author Emily
 *
 */
public final class PositionStyleConfigurator extends IStyleConfigurator implements SelectionListener {

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


    /**
     * @see org.locationtech.udig.style.StyleConfigurator#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {
        parent.setLayout( new GridLayout( 2, false ));
        
        Label xLabel = new Label(parent, SWT.RIGHT);
        xLabel.setText(Messages.ScalebarStyleConfigurator_horizontalAlignment); 

        xCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        xCombo.setItems(new String[]{LEFT, CENTER, RIGHT});
        xCombo.select(0);
        xCombo.addSelectionListener(this);
        xCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        
        Label yLabel = new Label(parent, SWT.RIGHT);
        yLabel.setText(Messages.ScalebarStyleConfigurator_verticalAlignment); 

        yCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        yCombo.setItems(new String[]{TOP, MIDDLE, BOTTOM});
        yCombo.select(0);
        yCombo.addSelectionListener(this);
        yCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
    }
    

	public void init() {
		// do nothing

	}

	public void refresh() {
	    IBlackboard blackboard = getStyleBlackboard();
        Point point = (Point) blackboard.get(PositionStyleContent.ID);

        if (point == null) {
            point = PositionStyleContent.createDefaultStyle();
            setLeft(point);
            setTop(point);

            blackboard.put(PositionStyleContent.ID, point);
            ((StyleBlackboard) blackboard).setSelected(new String[]{PositionStyleContent.ID});
        }

        if (isLeft(point))
            xCombo.select(0);
        else if (isCenter(point))
            xCombo.select(1);
        else if (isRight(point))
            xCombo.select(2);
        
        if (isTop(point)) 
            yCombo.select(0);
        else if (isMiddle(point)) 
            yCombo.select(1);
        else if (isBottom(point)) 
            yCombo.select(2);
        
    }

	@Override
	public void preApply() {
	}
	
    public boolean canStyle( Layer layer ) {
        return layer.hasResource(MapGraphic.class);
    }


    /*
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetSelected( SelectionEvent e ) {
        IBlackboard blackboard = getStyleBlackboard();
        Point point = (Point) blackboard.get(PositionStyleContent.ID);

        if (point == null) {
            point = PositionStyleContent.createDefaultStyle();
            setLeft(point);
            setTop(point);

            blackboard.put(PositionStyleContent.ID, point);
            ((StyleBlackboard) getStyleBlackboard()).setSelected(new String[]{PositionStyleContent.ID});
        }
        
        //read object state from ui widgets
        switch (xCombo.getSelectionIndex()) {
            case 0:
                setLeft(point);
                break;
            case 1:
                setCenter(point);
                break;
            case 2:
                setRight(point);
                
        }
        
        switch(yCombo.getSelectionIndex()) {
            case 0:
                setTop(point);
                break;
            case 1:
                setMiddle(point);
                break;
            case 2:
                setBottom(point);
        }
    }
    
    /*
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetDefaultSelected( SelectionEvent e ) {
        //do nothing
    }

    
    protected boolean isLeft( Point point ) {
        return point.x == LocationStyleContent.XPAD_LEFT;
    }

    protected void setLeft( Point point ) {
        point.x = LocationStyleContent.XPAD_LEFT;
    }

    protected boolean isCenter( Point point ) {
        IMapDisplay display = getMapDisplay();

        int x = display.getWidth() / 2;
        return point.x == x;
    }

    protected void setCenter( Point point ) {
        IMapDisplay display = getMapDisplay();
        int x = display.getWidth() / 2;
        point.x = x;
    }

    protected boolean isRight( Point point ) {
        return point.x == -LocationStyleContent.XPAD_RIGHT;
    }

    protected void setRight( Point point ) {
    	point.x = -LocationStyleContent.XPAD_RIGHT;
    }

    protected boolean isTop( Point point ) {
        return point.y == LocationStyleContent.YPAD_TOP; //+ rect.height;
    }

    protected void setTop( Point point ) {
    	point.y = LocationStyleContent.YPAD_TOP; //+ rect.height;
    }

    protected boolean isMiddle( Point point ) {
        IMapDisplay display = getMapDisplay();

        int y = display.getHeight() / 2;
        return point.y == y;
    }

    protected void setMiddle( Point point ) {
        IMapDisplay display = getMapDisplay();

        int y = display.getHeight() / 2;
        point.y = y;
    }

    protected boolean isBottom( Point point ) {
        return point.y == -LocationStyleContent.YPAD_BOTTOM;
    }

    protected void setBottom( Point point ) {
    	point.y = -LocationStyleContent.YPAD_BOTTOM;
    }
    
    protected IMapDisplay getMapDisplay() {
        return getLayer().getMap().getRenderManager().getMapDisplay();
    }

}

