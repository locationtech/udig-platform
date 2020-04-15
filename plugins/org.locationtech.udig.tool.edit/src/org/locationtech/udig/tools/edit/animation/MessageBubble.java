/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.animation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.project.ui.IAnimation;
import org.locationtech.udig.project.ui.commands.AbstractDrawCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseMotionListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseWheelEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseWheelListener;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Creates a semi transparent bubble (OSX like) that show a message to the user and disappears after
 * a few seconds.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class MessageBubble extends AbstractDrawCommand implements IAnimation {

    private int x;
    private int y;
    private String[] message;
    private short delay;
    private Rectangle validArea;
    private int verticalBorder=5;
    private int horizontalBorder=5;
    private int verticalCornerArc=15;
    private int horizontalCornerArc=15;
    private Color bubbleColor = new Color(0,0,0,167);
    private Color textColor = new Color( 200,200,200,167);
    private Font myFont = null;
    
    
    /**
     * 
     * @param x upperLeft of message
     * @param y upperLeft of message
     * @param message message to display
     * @param delay the length of time to show the message
     */
    public MessageBubble( final int x, final int y, final String message, short delay ) {
        this.x=x;
        this.y=y;
        processMessage(message);
        this.delay=delay;
    }

    private void processMessage(String message) {
        this.message=message.split("[\n\r]"); //$NON-NLS-1$
        for( String part : this.message ) {
            part = part.replaceAll("\t", "    ");  //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    public short getFrameInterval() {
        return delay;
    }

    public boolean hasNext() {
        return false;
    }

    public void nextFrame() {
    }

    @Override
    public void setGraphics(ViewportGraphics graphics, IMapDisplay mapDisplay) {
        super.setGraphics(graphics, mapDisplay);

        if (display != null) {
            display.addMouseListener(mouseListener);
            display.addMouseWheelListener(wheelListener);
        }
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        if (display == null) {
            return;
        }

        if (myFont != null){
            graphics.setFont(myFont);
        }

        Rectangle2D messageBounds=new Rectangle(0,0);
        
        for( String part : message ) {
            Rectangle2D size = graphics.getStringBounds(part);
            messageBounds=new Rectangle((int)(Math.max(size.getWidth(),messageBounds.getWidth())), 
                    (int)(size.getHeight()+messageBounds.getHeight()));
        }

        if( x+messageBounds.getWidth()>display.getWidth()+horizontalBorder ){
            x=(int) (display.getWidth()-horizontalBorder-messageBounds.getWidth());
        }
        if( y+messageBounds.getHeight()>display.getHeight()+verticalBorder ){
            y=(int) (display.getHeight()-verticalBorder-messageBounds.getHeight());
        }
        
        validArea=new Rectangle(x,y,(int)messageBounds.getWidth()+horizontalBorder, (int)messageBounds.getHeight()+verticalBorder);
        
        graphics.setColor(bubbleColor);
        graphics.fillRoundRect(x,y, (int)messageBounds.getWidth()+horizontalBorder, (int)messageBounds.getHeight()+verticalBorder, 
                horizontalCornerArc, verticalCornerArc);
        
        // color for the message
        graphics.setColor(textColor );
        int height = graphics.getFontHeight();
        int i=0;
        int verticalOffset=verticalBorder/2;
        int horizontalOffset=horizontalBorder/2;
        for( String part : message ) {
            graphics.drawString(part, x+horizontalOffset, y+verticalOffset+(height*i), 
                    ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_BOTTOM);
            i++;
        }
    }

    public Rectangle getValidArea() {
        return validArea;
    }

    /**
     * Sets the space between the edge of the bubble and the text.  Default = 10 pixels
     *
     * @param horizontalBorder border along the x-axis.  In pixels.
     */
    public void setHorizontalBorder( int horizontalBorder ) {
        this.horizontalBorder = horizontalBorder;
    }

    /**
     * Sets the space between the edge of the bubble and the text.  Default = 10 pixels
     *
     * @param verticalBorder border along the y-axis.  In pixels.
     */
    public void setVerticalBorder( int verticalBorder ) {
        this.verticalBorder = verticalBorder;
    }
    
    /**
     * Sets the horizontal Arc of the bubble corners for the four edges. Default is 15 pixels.
     *
     * @param horizontalCornerArc the horizontal Arc of the bubble corners for the four edges in pixels.
     */
    public void setHorizontalCornerArc( int horizontalCornerArc ) {
        this.horizontalCornerArc = horizontalCornerArc;
    }

    /**
     * Sets the vertical Arc of the bubble corners for the four edges. Default is 15 pixels.
     *
     * @param verticalCornerArc the vertical Arc of the bubble corners for the four edges in pixels.
     */
    public void setVerticalCornerArc( int verticalCornerArc ) {
        this.verticalCornerArc = verticalCornerArc;
    }

    /**
     * Returns the color used to draw the Message Bubble
     *
     * @return the color used to draw the Message Bubble
     */
    public Color getBubbleColor() {
        return bubbleColor;
    }

    /**
     * Sets the color used to draw the Message Bubble.  Default is Color(0,0,0,167);
     *
     * @param bubbleColor the new color to use
     */
    public void setBubbleColor( Color bubbleColor ) {
        this.bubbleColor = bubbleColor;
    }

    /**
     * Sets the font used to draw the message.
     *
     * @param font the fond to use
     */
    public void setFont(Font font){
        this.myFont = font;
    }
    
    /**
     * Gets the current font.
     *
     * @return null if no font set; otherwise the font being used to draw the message
     */
    public Font getFont(){
        return this.myFont;
    }
    
    /**
     * Returns the color used to draw the Message
     *
     * @return the color used to draw the Message
     */
    public Color getTextColor() {
        return textColor;
    }

    /**
     * Sets the color used to draw the Message.  Default is Color(200,200,200,167);
     *
     * @param bubbleColor the new color to use
     */
    public void setTextColor( Color textColor ) {
        this.textColor = textColor;
    }

    @Override
    public void setValid( boolean valid ) {
        super.setValid(valid);
        if (display != null) {
            display.removeMouseListener(mouseListener);
            display.removeMouseWheelListener(wheelListener);
        }
    }
    
    private MapMouseListener mouseListener=new MapMouseListener(){

        public void mouseDoubleClicked( MapMouseEvent event ) {
            disable((ViewportPane) event.source, this);
        }

        public void mouseEntered( MapMouseEvent event ) {
            disable((ViewportPane) event.source, this);
        }

        public void mouseExited( MapMouseEvent event ) {
            disable((ViewportPane) event.source, this);
        }

        public void mousePressed( MapMouseEvent event ) {
            disable((ViewportPane) event.source, this);
        }

        public void mouseReleased( MapMouseEvent event ) {
            disable((ViewportPane) event.source, this);
        }
        
    };
    
    private MapMouseWheelListener wheelListener=new MapMouseWheelListener(){

        public void mouseWheelMoved( MapMouseWheelEvent e ) {
            disable(display, this);
        }
        
    };
    
    
    
    void disable(ViewportPane pane, Object listener){
        if( !isValid(pane) ){
            if( listener instanceof MapMouseMotionListener )
                pane.removeMouseMotionListener((MapMouseMotionListener) listener);
            else if( listener instanceof MapMouseListener ){
                pane.removeMouseListener((MapMouseListener) listener);
            }else if( listener instanceof MapMouseWheelListener ){
                pane.removeMouseWheelListener((MapMouseWheelListener) listener);
            }
                
            return;
        }

        setValid(false);
        Rectangle bounds = getValidArea();
        if( bounds==null ){
            pane.repaint();
        }else{
            pane.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }
    
    private boolean isValid(IMapDisplay source){
        if( !MessageBubble.this.isValid() )
            return false;
        if( source!=display )
            return false;
        return true;
    }
}
