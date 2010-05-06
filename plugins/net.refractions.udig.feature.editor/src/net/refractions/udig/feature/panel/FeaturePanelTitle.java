/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2001, 2008 IBM Corporation and others
 * ------
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * --------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package net.refractions.udig.feature.panel;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormColors;

/**
 * Title for feature panel page.
 * 
 * @author Anthony Hunter
 */
public class FeaturePanelTitle extends Composite {

    private CLabel label;

    private Image image = null;

    private String text = null;

    private static final String BLANK = ""; //$NON-NLS-1$

    private static final String TITLE_FONT = "org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyTitle"; //$NON-NLS-1$

    private FeaturePanelWidgetFactory factory;

    /**
     * Constructor for TabbedPropertyTitle.
     * 
     * @param parent the parent composite.
     * @param factory2 the widget factory for the tabbed property sheet
     */
    public FeaturePanelTitle( Composite parent, FeaturePanelWidgetFactory factory ) {
        super(parent, SWT.NO_FOCUS);
        this.factory = factory;

        this.addPaintListener(new PaintListener(){

            public void paintControl( PaintEvent e ) {
                if (image == null && (text == null || text.equals(BLANK))) {
                    label.setVisible(false);
                } else {
                    label.setVisible(true);
                    drawTitleBackground(e);
                }
            }
        });

        factory.getColors().initializeSectionToolBarColors();
        setBackground(factory.getColors().getBackground());
        setForeground(factory.getColors().getForeground());

        FormLayout layout = new FormLayout();
        layout.marginWidth = 1;
        layout.marginHeight = 2;
        setLayout(layout);

        Font font;
        if (!JFaceResources.getFontRegistry().hasValueFor(TITLE_FONT)) {
            FontData[] fontData = JFaceResources.getFontRegistry().getBold(
                    JFaceResources.DEFAULT_FONT).getFontData();
            /* title font is 2pt larger than that used in the tabs. */
            fontData[0].setHeight(fontData[0].getHeight() + 2);
            JFaceResources.getFontRegistry().put(TITLE_FONT, fontData);
        }
        font = JFaceResources.getFont(TITLE_FONT);

        label = factory.createCLabel(this, BLANK);
        label.setBackground(new Color[]{factory.getColors().getColor(IFormColors.H_GRADIENT_END),
                factory.getColors().getColor(IFormColors.H_GRADIENT_START)}, new int[]{100}, true);
        label.setFont(font);
        label.setForeground(factory.getColors().getColor(IFormColors.TITLE));
        FormData data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.top = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);
        data.bottom = new FormAttachment(100, 0);
        label.setLayoutData(data);

        /*
         * setImage(PlatformUI.getWorkbench().getSharedImages().getImage(
         * ISharedImages.IMG_OBJ_ELEMENT));
         */
    }

    /**
     * @param e
     */
    protected void drawTitleBackground( PaintEvent e ) {
        Rectangle bounds = getClientArea();
        label.setBackground(new Color[]{factory.getColors().getColor(IFormColors.H_GRADIENT_END),
                factory.getColors().getColor(IFormColors.H_GRADIENT_START)}, new int[]{100}, true);
        Color bg = factory.getColors().getColor(IFormColors.H_GRADIENT_END);
        Color gbg = factory.getColors().getColor(IFormColors.H_GRADIENT_START);
        GC gc = e.gc;
        gc.setForeground(bg);
        gc.setBackground(gbg);
        gc.fillGradientRectangle(bounds.x, bounds.y, bounds.width, bounds.height, true);
        // background bottom separator
        gc.setForeground(factory.getColors().getColor(IFormColors.H_BOTTOM_KEYLINE1));
        gc.drawLine(bounds.x, bounds.height - 2, bounds.x + bounds.width - 1, bounds.height - 2);
        gc.setForeground(factory.getColors().getColor(IFormColors.H_BOTTOM_KEYLINE2));
        gc.drawLine(bounds.x, bounds.height - 1, bounds.x + bounds.width - 1, bounds.height - 1);
    }

    /**
     * Set the text label.
     * 
     * @param text the text label.
     * @param image the image for the label.
     */
    public void setTitle( String text, Image image ) {
        this.text = text;
        this.image = image;
        if (text != null) {
            label.setText(text);
        } else {
            label.setText(BLANK);
        }
        label.setImage(image);
        redraw();
    }
}
