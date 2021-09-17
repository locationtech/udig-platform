/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.scalebar;

import java.awt.Rectangle;

import org.locationtech.udig.mapgraphic.internal.Messages;
import org.locationtech.udig.mapgraphic.style.LocationStyleContent;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.style.IStyleConfigurator;

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

    public void init() {
        // do nothing
    }

    /*
    * @see org.locationtech.udig.style.IStyleConfigurator#refresh()
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
    * @see org.locationtech.udig.style.IStyleConfigurator#apply()
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
    * @see org.locationtech.udig.style.IStyleConfigurator#canStyle(org.locationtech.udig.project.Layer)
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
