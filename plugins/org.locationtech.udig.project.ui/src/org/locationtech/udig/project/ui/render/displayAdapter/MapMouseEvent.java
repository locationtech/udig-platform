/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.render.displayAdapter;

import java.awt.Point;

import org.eclipse.swt.SWT;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;

/**
 * Encapsulates a mouse event. MapMouseListeners receive MapMouse events.
 * 
 * @author jeichar
 */
public class MapMouseEvent {
    
    /** Indicates no modifiers or no buttons. */
    public static final int NONE = 0;
    /** Indicates that the alt key is down */
    public static final int ALT_DOWN_MASK = SWT.ALT;

    /** Indicates that the ctrl key is down */
    public static final int CTRL_DOWN_MASK = SWT.CTRL;

    /** Indicates that the shift key is down */
    public static final int SHIFT_DOWN_MASK = SWT.SHIFT;
    
    /** Indicates that the mod1 key is down */
    public static final int MOD1_DOWN_MASK = SWT.MOD1;
    
    /** Indicates that the mod2 key is down */
    public static final int MOD2_DOWN_MASK = SWT.MOD2;
    
    /** Indicates that the mod3 key is down */
    public static final int MOD3_DOWN_MASK = SWT.MOD3;
    
    /** Indicates that the mod3 key is down */
    public static final int MOD4_DOWN_MASK = SWT.MOD4;

    /** Indicates that the 1st mouse button, the left button on right handed mouses */
    public static final int BUTTON1 = 1 << 3;

    /** Indicates that the 2nd mouse button. */
    public static final int BUTTON2 = 1 << 4;

    /** Indicates that the 3rd mouse button. */
    public static final int BUTTON3 = 1 << 5;

    /** The Viewport pane that raised the event. */
    public final IMapDisplay source;

    /** 
     * The state consists buttons|modifiers
     * @deprecated user modifiers and buttons
     */
    public final int state;
    
    /**
     * All the buttons that are currently down ORed together
     */
    public final int buttons;

    /**
     * All the key modifiers ORed together
     */
    public final int modifiers;

    /** indicates the button that last changed */
    public final int button;

    /** indicates the x position of the event */
    public final int x;

    /** indicates the y position of the event */
    public final int y;
    
    /** the time the event occurred */
    public final long timestamp;

    /**
     * Construct <code>MapMouseEvent</code>.
     * 
     * @param source The object that raised the event
     * @param x the x position of the event
     * @param y the y position of the event
     * @param modifiers indicates what modifiers are down.  Modifiers are ORed together
     * @param buttons indicates the buttons that are down.  button ids are ORed together.
     * @param button the button that last changed
     */
    public MapMouseEvent( IMapDisplay source, int x, int y, int modifiers, int buttons, int button ) {
        this.source = source;
        this.state = modifiers|buttons;
        this.x = x;
        this.y = y;
        this.button = button;
        this.buttons=buttons;
        this.modifiers=modifiers & SWT.MODIFIER_MASK;
        timestamp=System.currentTimeMillis();
    }

    /**
     * Returns the location of the event.
     * 
     * @return the location of the event.
     * @see Point
     */
    public Point getPoint() {
        return new Point(x, y);
    }

    /**
     * Returns true if shift key is down.
     * 
     * @return true if shift key is down.
     */
    public boolean isShiftDown() {
        return isModifierDown(SHIFT_DOWN_MASK);
    }

    /**
     * Returns true if control key is down.
     * 
     * @return true if control key is down.
     */
    public boolean isControlDown() {
        return isModifierDown(CTRL_DOWN_MASK);
    }

    /**
     * Returns true if alt key is down.
     * 
     * @return true if alt key is down.
     */
    public boolean isAltDown() {
        return isModifierDown(ALT_DOWN_MASK);
    }

    /**
     * Returns true if the modifier is down 
     * 
     * @see #CTRL_DOWN_MASK
     * @see #ALT_DOWN_MASK
     * @see #SHIFT_DOWN_MASK
     * @see #MOD1_DOWN_MASK
     * @see #MOD2_DOWN_MASK
     * @see #MOD3_DOWN_MASK
     * @see #MOD4_DOWN_MASK
     *
     * @param mask the modifier to test for.
     * @return true if modifier is down.
     */
    public boolean isModifierDown(int mask){
        return (modifiers & mask) != 0;
    }
    
    /**
     * @return Returns true if a keyboard modifier is down.
     */
    public boolean modifiersDown() {
        return modifiers != 0;
    }

    /**
     * @return Returns true if a button is down.
     */
    public boolean buttonsDown(){
        return buttons!=0;
    }
    
}
