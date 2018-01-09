/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.locationtech.udig.internal.ui.UiPlugin;

/**
 * A dialog that, on opening, will zoom from the start location/size the location of the provided dialog.
 * <p>
 * IMPORTANT:  Since there is no way for ZoomingDialog to determine whether setBlockOnOpen is
 * set on the wrapped/decorated Dialog setBlockOnOpen <em>MUST</em> be set on ZoomingDialog
 * </p>
 * @author Jesse
 * @since 1.1.0
 */
public class ZoomingDialog extends Dialog {
    public static final int FAST=1;
    public static final int MEDIUM=4;
    public static final int SLOW=8;
    private static final int BASE_NUMBER_STEPS = 15;
    private final Dialog delegate;
    private final Point size;
    private final Point location;
    private Rectangle end;
    private int zoomSpeed=FAST;
    private boolean shouldBlock;
    
    /**
     * Creates a new instance.
     * @param parentShell shell to use as a parent 
     * @param delegate The dialog to open
     * @param start the rectangle, in Display coordinates, to zoom from when opening.
     */
    public ZoomingDialog( Shell parentShell, Dialog delegate, Rectangle start){
        super(new SameShellProvider(parentShell)); 
        this.delegate=delegate;
        this.location=new Point(start.x, start.y);
        this.size=new Point(start.width, start.height);
        setShellStyle(SWT.ON_TOP|SWT.NO_FOCUS|SWT.NO_TRIM|SWT.NO_BACKGROUND|SWT.NO_REDRAW_RESIZE);
    }
    
    /**
     * Create new instance.  Will zoom from the provided rectangle to the dialog location.
     *  
     * @param parentShell shell to use as a parent 
     * @param dialog dialog to open
     * @param x 
     * @param y
     * @param width
     * @param height
     */
    public ZoomingDialog( Shell parentShell, Dialog dialog, int x, int y, int width, int height ) {
        this(parentShell, dialog, new Rectangle(x,y,width,height));
    }

    /**
     * Sets how long it takes for the Dialog to open, default is {@link #FAST}.  Is one of
     * 
     * {@link #FAST}
     * {@link #MEDIUM}
     * {@link #SLOW}
     *
     * @param speed a constant indicating the speed at which the dialog zooms
     */
    public void setZoomSpeed(int speed ){
        this.zoomSpeed=speed;
    }
    
    @Override
    public void setBlockOnOpen( boolean shouldBlock ) {
        this.shouldBlock=shouldBlock;
    }

    
    public boolean close() {
        
        boolean result = delegate.close();
        return result;
    }

    private void closeInternal() {
        super.getShell().setVisible(true);
        delegate.getShell().setVisible(false);
        zoom(false);
        super.close();
    }


    public void create() {
        super.create();
        if( delegate.getShell()==null )
            delegate.create();
    }

    public int getReturnCode() {
        return delegate.getReturnCode();
    }

    @Override
    protected Point getInitialLocation( Point initialSize ) {
        return location;
    }
    
    @Override
    protected Point getInitialSize() {
        return size;
    }
    /**
     * Opens the dialog.
     * 
     * <p>
     * IMPORTANT:  Since there is no way for ZoomingDialog to determine whether setBlockOnOpen is
     * set on the wrapped/decorated Dialog setBlockOnOpen <em>MUST</em> be set on ZoomingDialog
     * </p>
     * 
     */
    public int open() {
        if( delegate.getShell()==null )
            delegate.create();
        
        end=delegate.getShell().getBounds();
//        end=new Rectangle(300, 300, 800,600);
        super.setBlockOnOpen(false);
        super.open();
        
        zoom(true);

        // Must add listeners after delegate is open.
        // Therefore blocking must be false on the delegate so that
        // listeners can be added.
        delegate.setBlockOnOpen(false);
        int open = delegate.open();
        addClosingListeners();

        if( shouldBlock )
            runEventLoop(getShell());
        return open;
    }

    private void runEventLoop(Shell loopShell) {

        //Use the display provided by the shell if possible
        Display display;
        if (getShell() == null) {
            display = Display.getCurrent();
        } else {
            display = loopShell.getDisplay();
        }

        while (loopShell != null && !loopShell.isDisposed()) {
            try {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            } catch (Throwable e) {
                UiPlugin.log( "Exception in UI thread while waiting", e); //$NON-NLS-1$
            }
        }
        display.update();
    }

    private void addClosingListeners() {
        delegate.getShell().addListener(SWT.Close|SWT.Dispose, new Listener(){

            public void handleEvent( Event event ) {
                closeInternal();
            }
            
        });
        delegate.getShell().addListener(SWT.Dispose, new Listener(){

            public void handleEvent( Event event ) {
                closeInternal();
            }
            
        });
    }

    private void zoom(boolean grow) {
        Point location=this.location;
        Point size=this.size;
        int totalSteps = BASE_NUMBER_STEPS*zoomSpeed;
        int xstep=(end.x-location.x)/totalSteps;
        int ystep=(end.y-location.y)/totalSteps;
        int xsize=(end.width-size.x)/totalSteps;
        int ysize=(end.height-size.y)/totalSteps;
        if( grow ){
             int steps=0;
            while( steps<totalSteps  ){
                int nextX = location.x+steps*xstep;
                int nextY = location.y+steps*ystep;
                int nextWidth = size.x+steps*xsize;
                int nextHeight = size.y+steps*ysize;
                super.getShell().setBounds(new Rectangle(nextX, nextY,nextWidth, nextHeight));
                steps++;
            }
        }else{
            int steps=totalSteps;
            while( steps>-1 ){
                int nextX = location.x+steps*xstep;
                int nextY = location.y+steps*ystep;
                int nextWidth = size.x+steps*xsize;
                int nextHeight = size.y+steps*ysize;
                super.getShell().setBounds(new Rectangle(nextX, nextY,nextWidth, nextHeight));
                steps--;
            }            
        }
        super.getShell().setVisible(false);

    }

    public String toString() {
        return delegate.toString();
    }
    
    @Override
    protected Control createButtonBar( Composite parent ) {
        return new Composite(parent, SWT.NONE);
    }
    
    /**
     * Create a message dialog. Notethat the dialog will have no visual
     * representation (no widgets) until it is told to open.
     * <p>
     * The labels of the buttons to appear in the button bar are supplied in
     * this constructor as an array. The <code>open</code> method will return
     * the index of the label in this array corresponding to the button that was
     * pressed to close the dialog. If the dialog was dismissed without pressing
     * a button (ESC, etc.) then -1 is returned. Note that the <code>open</code>
     * method blocks.
     * </p>
     * 
     * @param start 
     *            the location to zoom from.
     * @param parentShell
     *            the parent shell
     * @param dialogTitle
     *            the dialog title, or <code>null</code> if none
     * @param dialogTitleImage
     *            the dialog title image, or <code>null</code> if none
     * @param dialogMessage
     *            the dialog message
     * @param dialogImageType
     *            one of the following values:
     *            <ul>
     *            <li><code>MessageDialog.NONE</code> for a dialog with no
     *            image</li>
     *            <li><code>MessageDialog.ERROR</code> for a dialog with an
     *            error image</li>
     *            <li><code>MessageDialog.INFORMATION</code> for a dialog
     *            with an information image</li>
     *            <li><code>MessageDialog.QUESTION </code> for a dialog with a
     *            question image</li>
     *            <li><code>MessageDialog.WARNING</code> for a dialog with a
     *            warning image</li>
     *            </ul>
     * @param dialogButtonLabels
     *            an array of labels for the buttons in the button bar
     * @param defaultIndex
     *            the index in the button label array of the default button
     * @return 
     */
    public static int openMessageDialog(Rectangle start,
            Shell parentShell, 
            String dialogTitle, 
            Image dialogImage, 
            String dialogMessage,
            int dialogImageType, 
            String[] buttonLabels, 
            int defaultIndex){
        
        MessageDialog dialog = new MessageDialog(parentShell, dialogTitle, dialogImage, dialogMessage, dialogImageType, buttonLabels, defaultIndex );
        ZoomingDialog zd=new ZoomingDialog(parentShell, dialog, start);
        
        zd.open();
        return zd.getReturnCode();
    }
    
    public static void openErrorMessage(Rectangle start,
            Shell parentShell,
            String dialogTitle,
            String dialogMessage ){
        openMessageDialog(start, parentShell, dialogTitle, null, dialogMessage, MessageDialog.ERROR, 
                new String[]{IDialogConstants.OK_LABEL}, 1);
    }
    
    public static void openWarningMessage(Rectangle start,
            Shell parentShell,
            String dialogTitle,
            String dialogMessage ){
        openMessageDialog(start, parentShell, dialogTitle, null, dialogMessage, MessageDialog.WARNING, 
                new String[]{IDialogConstants.OK_LABEL}, 1);
    }
    
    public static void openInformationMessage(Rectangle start,
            Shell parentShell,
            String dialogTitle,
            String dialogMessage ){
        openMessageDialog(start, parentShell, dialogTitle, null, dialogMessage, MessageDialog.INFORMATION, 
                new String[]{IDialogConstants.OK_LABEL}, 1);
    }
    
    
    public static boolean openQuestionMessage(Rectangle start,
            Shell parentShell,
            String dialogTitle,
            String dialogMessage ){
        
            int result = openMessageDialog(start, parentShell, dialogTitle, null, dialogMessage, MessageDialog.QUESTION, 
                    new String[]{IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 1);
            return result==IDialogConstants.YES_ID;
    }

    /**
     * Calculates the bounds of the Control in Display coordinates (Required by ZoomingDialog constructor).
     *
     * @param control control to use as the starting position 
     * @return the bounds of the Control in Display coordinates
     */
    public static Rectangle calculateBounds(Control control){
        Point ul=control.toDisplay(0, 0);
        Point size2 = control.getSize();
        Rectangle start=new Rectangle(ul.x, ul.y, size2.x, size2.y);
        return start;
    }
    
    /**
     * Calculates the bounds of the Control in Display coordinates (Required by ZoomingDialog constructor).
     *
     * @param item TreeItem to use as the starting position
     * @param columnIndex the index of the column to find the bounds for.  If -1 bounds of entire item are found 
     * @return the bounds of the Control in Display coordinates
     */
    public static Rectangle calculateBounds( TreeItem item, int columnIndex ) {
        Point ulTree=item.getParent().toDisplay(0,0);
        Rectangle bounds;
        if( columnIndex>-1){
            bounds = item.getBounds(columnIndex);
            bounds.x+=ulTree.x;
            bounds.y+=ulTree.y;
        }else{
            Rectangle bounds2 = item.getBounds(0);
            bounds=new Rectangle(ulTree.x, ulTree.y, 0, 0);
            bounds.width=item.getParent().getSize().x;
            bounds.height=bounds2.height;
        }
        
        return bounds;
    }



}
