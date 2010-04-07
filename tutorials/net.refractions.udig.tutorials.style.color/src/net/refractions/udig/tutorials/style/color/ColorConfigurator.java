package net.refractions.udig.tutorials.style.color;

import java.awt.Color;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.style.IStyleConfigurator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class ColorConfigurator extends IStyleConfigurator {
    private Color color;
    private Button button;
    private Label label;
        
    @Override
    public boolean canStyle( Layer layer ) {
        return layer.getStyleBlackboard().get(ColorStyle.ID) != null;
    }

    @Override
    public void createControl( Composite parent ) {
        parent.setLayout(new FillLayout(SWT.VIRTUAL));
        button = new Button(parent, SWT.DEFAULT );
        button.setText("Change Color");        
        label = new Label(parent, SWT.NONE);
        label.setText( "Color" );
    }

    private SelectionListener pressed = new SelectionListener(){
        public void widgetDefaultSelected( SelectionEvent e ) {
            widgetSelected(e);
        }
        public void widgetSelected( SelectionEvent e ) {
            ColorDialog dialog = new ColorDialog(e.display.getActiveShell());
            RGB currentRGB = new RGB(color.getRed(), color.getGreen(), color.getBlue());
            dialog.setRGB(currentRGB);
            dialog.open();
            
            RGB rgb = dialog.getRGB();
            color = new Color(rgb.red, rgb.green, rgb.blue);
            getStyleBlackboard().put(ColorStyle.ID, color);
            updateLabel();
            getApplyAction().setEnabled(true);
        }
    };
    public void listen( boolean listen ){
        if( listen ){
            button.addSelectionListener( pressed );
        }
        else {
            button.removeSelectionListener( pressed );
        }
    }
    
    @Override
    protected void refresh() {
        getApplyAction().setEnabled(false);
        listen( false );
        try {
            color = (Color) getStyleBlackboard().get( ColorStyle.ID );
            if( color == null ) {                
                color = Color.BLACK;
            }            
            updateLabel();
        }
        finally {
            listen(true);
        }
    }
    void updateLabel() {
        Image oldImage = label.getImage();
        Rectangle bounds = label.getBounds();
        if (bounds.width == 0 || bounds.height == 0) return;

        Display display = label.getDisplay();
        Image newImage = new Image(display, bounds.width, bounds.width);

        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        label.setText("Color:"+r+","+g+","+b);
        org.eclipse.swt.graphics.Color color2 =
            new org.eclipse.swt.graphics.Color(display,r,g,b );

        GC gc = new GC(newImage);
        try {
            gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
            gc.drawRectangle(0, 0, bounds.width - 1, bounds.height - 1);
            int alpha = color.getAlpha();
            gc.setAlpha(alpha);
            gc.setBackground(color2);
            gc.fillRectangle(1, 1, bounds.width - 2, bounds.height - 2);
        } finally {
            gc.dispose();
            color2.dispose();
        }
        label.setImage(newImage);
        if (oldImage != null)
            oldImage.dispose();
        label.redraw();
    }
}
