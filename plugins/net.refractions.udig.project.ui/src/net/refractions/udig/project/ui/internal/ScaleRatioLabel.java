package net.refractions.udig.project.ui.internal;

import java.text.NumberFormat;

import net.refractions.udig.project.internal.commands.SetScaleCommand;
import net.refractions.udig.project.render.IViewportModel;
import net.refractions.udig.project.render.IViewportModelListener;
import net.refractions.udig.project.render.ViewportModelEvent;
import net.refractions.udig.project.render.ViewportModelEvent.EventType;
import net.refractions.udig.ui.ZoomingDialog;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * Displays the current scale ratio on the status bar
 *
 * @author Andrea Aime
 */
class ScaleRatioLabel extends ContributionItem implements KeyListener, FocusListener {
    /** ScaleRatioLabel editor field */
    private final MapPart mapPart;
    static final String SCALE_ITEM_ID = "Current scale"; //$NON-NLS-1$
    NumberFormat nf = NumberFormat.getIntegerInstance();
    Text label;
    IViewportModel viewportModel;
    /** Listens to viewport changes and updates the displayed scale accordingly */
    IViewportModelListener listener = new IViewportModelListener(){

        public void changed( ViewportModelEvent event ) {
            if (event.getType() == EventType.CRS || event.getType() == EventType.BOUNDS) {
                Display display = PlatformUI.getWorkbench().getDisplay();
                if (display == null)
                    display = Display.getDefault();

                display.asyncExec(new Runnable(){

                    public void run() {
                        updateScale();
                    }
                });

            }

        }

    };

    public ScaleRatioLabel(MapPart editor) {
        super(SCALE_ITEM_ID);
        this.mapPart = editor;
    }

    /**
     * Sets the current viewport model. Should be called every time the map changes in order
     * update the shared ratio label
     */
    public void setViewportModel( IViewportModel newViewportModel ) {
        // if(newViewportModel != null)
        // System.out.println(System.currentTimeMillis() + " - changing viewport model - map " +
        // newViewportModel.getMap().getName()); //$NON-NLS-1$
        if (newViewportModel != this.viewportModel) {
            if (viewportModel != null) {
                viewportModel.removeViewportModelListener(listener);
            }
            viewportModel = newViewportModel;
            viewportModel.addViewportModelListener(listener);
            updateScale();
        }
    }

    /**
     * @see org.eclipse.jface.action.IContributionItem#isDynamic()
     */
    public boolean isDynamic() {
        return true;
    }

    /**
     * @see org.eclipse.jface.action.ContributionItem#dispose()
     */
    public void dispose() {
        if (label != null)
            label.dispose();
        if (viewportModel != null) {
            viewportModel.removeViewportModelListener(listener);
            viewportModel = null;
        }
    }

    /**
     * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void fill( Composite c ) {
        Label separator = new Label(c, SWT.SEPARATOR);
        StatusLineLayoutData data = new StatusLineLayoutData();
        separator.setLayoutData(data);
        data.widthHint = 1;
        data.heightHint = MapEditor.STATUS_LINE_HEIGHT;
        label = new Text(c, SWT.BORDER|SWT.CENTER);
        label.addKeyListener(this);
        label.addFocusListener(this);
        label.addListener(SWT.MouseDown, new Listener(){
           public void handleEvent(Event e){
               if( label.getText().contains(":") ) //$NON-NLS-1$
                   formatForEditing();
           }
        });
        data = new StatusLineLayoutData();
        label.setLayoutData(data);
        updateScale();
        data.widthHint = 80;
        data.heightHint = MapEditor.STATUS_LINE_HEIGHT;
        this.mapPart.setFont(label);
    }

    public void keyPressed(KeyEvent e) {
        if( label.getText().contains(":") ) //$NON-NLS-1$
            formatForEditing();
        if( !isLegalKey(e) ){
            e.doit=false;
        }
    }

    public boolean isLegalKey(KeyEvent e){
        char c=e.character;

        if( c == '0' ||
                c == '1' ||
                c == '2' ||
                c == '3' ||
                c == '4' ||
                c == '5' ||
                c == '6' ||
                c == '7' ||
                c == '8' ||
                c == '9' ||
                c == SWT.DEL ||
                c == SWT.BS ){
            return true;
        }

        if( e.keyCode == SWT.ARROW_LEFT ||
                e.keyCode == SWT.ARROW_RIGHT ||
                e.keyCode == SWT.HOME ||
                e.keyCode == SWT.END ||
                e.keyCode == SWT.OK)
            return true;

        return false;

    }

    public void keyReleased(KeyEvent e) {
        if (e.character == SWT.Selection) {
            go();
        } else if (e.character == SWT.ESC) {
            updateScale();
        }

    }

    private void go() {
        String newScale=label.getText().trim();
        try{
        	double d = nf.parse(newScale.replace(" ","")).doubleValue();
            SetScaleCommand command=new SetScaleCommand(d);
            this.mapPart.getMap().sendCommandASync(command);
        }catch(Exception e){
            org.eclipse.swt.graphics.Rectangle start=ZoomingDialog.calculateBounds(label);

            ZoomingDialog.openErrorMessage(start, this.mapPart
					.getMapEditorSite().getShell(),
					Messages.MapEditor_illegalScaleTitle,
					Messages.MapEditor_illegalScaleMessage);
        }
    }

    public void focusGained(FocusEvent e) {
        formatForEditing();
    }

    private void formatForEditing(){
        String text=label.getText();
        if( text.contains(":")) //$NON-NLS-1$
            text=text.substring(2);
        StringBuilder builder=new StringBuilder();
        for( int i=0; i<text.length(); i++ ){
            char c=text.charAt(i);
            if( c!=',' )
                builder.append(c);
        }
        label.setText(builder.toString());
        int end = label.getText().length();
        label.setSelection(0, end);
    }
    public void focusLost(FocusEvent e) {
        updateScale();
    }

    private void updateScale() {
        if (label == null || label.isDisposed())
            return;

        if (viewportModel != null) {
            label.setText("1:" + nf.format(viewportModel.getScaleDenominator())); //$NON-NLS-1$
            label.setToolTipText(label.getText());
        } else {
            label.setText(""); //$NON-NLS-1$
        }

    }

}
