/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.mapgraphic.scalebar;

import java.awt.Rectangle;

import net.refractions.udig.mapgraphic.internal.Messages;
import net.refractions.udig.mapgraphic.style.LocationStyleContent;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;
import net.refractions.udig.style.IStyleConfigurator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Allows user to set the location of the scalebar?
 * 
 *  ScalebarStyleConfigurator x = new ScalebarStyleConfigurator( ... );
 *  TODO code example
 *  
 * </code></pre>
 * 
 * </p>
 * 
 * @author jdeolive
 * @since 0.6.0
 */
public class ScalebarStyleConfigurator extends IStyleConfigurator
    implements SelectionListener {

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

    /* (non-Javadoc)
	 * @see net.refractions.udig.style.IStyleConfigurator#init()
	 */
	public void init() {
		// do nothing

	}
    
    /*
     * @see net.refractions.udig.style.IStyleConfigurator#refresh()
     */
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

    }

    /*
     * @see net.refractions.udig.style.IStyleConfigurator#apply()
     */
    public void apply() {
        IBlackboard blackboard = getStyleBlackboard();
        Rectangle rect = (Rectangle) blackboard.get(LocationStyleContent.ID);

        if (rect == null) {
            rect = LocationStyleContent.createDefaultStyle();
            blackboard.put(LocationStyleContent.ID, rect);
            ((StyleBlackboard) blackboard).setSelected(new String[]{LocationStyleContent.ID});

        }

    }

    /*
     * @see net.refractions.udig.style.IStyleConfigurator#canStyle(net.refractions.udig.project.Layer)
     */
    public boolean canStyle( Layer layer ) {
        return layer.hasResource(ScalebarMapGraphic.class);
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
        IMapDisplay display = getMapDisplay();

        int x = display.getWidth() - LocationStyleContent.XPAD_RIGHT - rect.width;
        return rect.x == x;
    }

    protected void setRight( Rectangle rect ) {
        IMapDisplay display = getMapDisplay();

        int x = display.getWidth() - LocationStyleContent.XPAD_RIGHT - rect.width;
        rect.x = x;
    }

    protected boolean isTop( Rectangle rect ) {
        return rect.y == LocationStyleContent.YPAD_TOP + rect.height;
    }

    protected void setTop( Rectangle rect ) {
        rect.y = LocationStyleContent.YPAD_TOP + rect.height;
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
        IMapDisplay display = getMapDisplay();

        int y = display.getHeight() - LocationStyleContent.YPAD_BOTTOM - rect.height;

        return rect.y == y;
    }

    protected void setBottom( Rectangle rect ) {
        IMapDisplay display = getMapDisplay();

        int y = display.getHeight() - LocationStyleContent.YPAD_BOTTOM - rect.height;

        rect.y = y;
    }

    protected IMapDisplay getMapDisplay() {
        return getLayer().getMap().getRenderManager().getMapDisplay();
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

    /* (non-Javadoc)
	 * @see net.refractions.udig.style.IStyleConfigurator#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
        parent.setLayout( new GridLayout( 2, true ));
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
	}

}
