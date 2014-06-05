/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.style;

import java.awt.Color;
import java.awt.Font;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.internal.Messages;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.style.IStyleConfigurator;
import org.locationtech.udig.ui.graphics.AWTSWTImageUtils;

/**
 * A font chooser for the FontStyle
 * 
 * @author jesse
 * @since 1.1.0
 */
public class FontStyleConfigurator extends IStyleConfigurator {

    private Text label;
    private org.eclipse.swt.graphics.Font swtFont;
    private RGB color;

    @Override
    public boolean canStyle( Layer layer ) {
        return layer.hasResource(MapGraphic.class)
                && layer.getStyleBlackboard().contains(FontStyleContent.ID);
    }

    @Override
    public void createControl( Composite parent ) {
        GridLayout gridLayout = new GridLayout();
        int columns = 1;
        gridLayout.numColumns = columns;
        parent.setLayout(gridLayout);

        Composite group = new Composite(parent, SWT.NONE);
        group.setLayoutData(new GridData(GridData.FILL_BOTH));
        group.setLayout(new GridLayout(2, false));

        label = new Text(group, SWT.BORDER | SWT.WRAP);
        if (swtFont != null) {
            label.setFont(swtFont);
        }
        // This text is formally random :-) http://en.wikipedia.org/wiki/Lorem_ipsum
        label.setText("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."); //$NON-NLS-1$
        label.setLayoutData(new GridData(GridData.FILL_BOTH));

        final Button button = new Button(group, SWT.PUSH);
        button.setText(Messages.Font_ExampleText);
        button.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));

        button.addListener(SWT.Selection, new Listener(){

            public void handleEvent( Event event ) {

                final FontDialog dialog = new FontDialog(button.getShell());
                dialog.setRGB(color);
                // setup default font
                IBlackboard blackboard = getStyleBlackboard();
                FontStyle fontStyle = (FontStyle) blackboard.get(FontStyleContent.ID);
                if (fontStyle != null) {
                    Font f = fontStyle.getFont();
                    FontData[] fd = AWTSWTImageUtils.awtFontToSwt(f,JFaceResources.getFontRegistry()).getFontData();
                    dialog.setFontList(fd);
                }
                // show dialog and get results
                FontData result = dialog.open();
                if (result != null) {
                	color = dialog.getRGB();
                	
                    Font awtfont = AWTSWTImageUtils.swtFontToAwt(result);
                    fontStyle.setFont(awtfont);
                    fontStyle.setColor(new Color(color.red, color.green, color.blue));
                    
                    swtFont = AWTSWTImageUtils.awtFontToSwt(awtfont, JFaceResources.getFontRegistry());
                    label.setFont(swtFont);
                    
                    label.setForeground(toSwtColor(color));
                }
            }

        });
    }
    
	@Override
	public void preApply() {
	}
	
	
    @Override
    protected void refresh() {
        FontStyle fontStyle = (FontStyle) getStyleBlackboard().get(FontStyleContent.ID);
        
        if (fontStyle.getColor() != null){
        	color = new RGB(fontStyle.getColor().getRed(), fontStyle.getColor().getGreen(), fontStyle.getColor().getBlue());
        }else{
        	color = new RGB(0,0,0);
        }
        
        if (fontStyle.getFont() != null) {
            swtFont = AWTSWTImageUtils.awtFontToSwt(fontStyle.getFont(), JFaceResources
                    .getFontRegistry());
            if (label != null) {
            	label.setForeground(toSwtColor(color));
                label.setFont(swtFont);
            }
        }
    }
    
    private  org.eclipse.swt.graphics.Color toSwtColor(RGB rgb){
    	 String colorKey = rgb.red + "_" + rgb.green + "_"+ rgb.blue; //$NON-NLS-1$ //$NON-NLS-2$
         org.eclipse.swt.graphics.Color swtColor = JFaceResources.getColorRegistry().get(colorKey);
         if (swtColor == null){
         	swtColor = new org.eclipse.swt.graphics.Color(label.getDisplay(), color);
         	JFaceResources.getColorRegistry().put(colorKey, color);
         }
         return swtColor;
    }

}
