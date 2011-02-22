/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.mapgraphic.style;

import java.awt.Font;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.internal.Messages;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.style.IStyleConfigurator;
import net.refractions.udig.ui.graphics.SWTGraphics;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * A font chooser for the FontStyle
 *
 * @author jesse
 * @since 1.1.0
 */
public class FontStyleConfigurator extends IStyleConfigurator {

    private Text label;
    private org.eclipse.swt.graphics.Font swtFont;

    @Override
    public boolean canStyle( Layer layer ) {
        return layer.hasResource(MapGraphic.class) &&
            layer.getStyleBlackboard().contains(FontStyleContent.ID);
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

        label = new Text(group, SWT.BORDER);
        if (swtFont != null) {
            label.setFont(swtFont);
        }
        label.setText(Messages.FontStyleConfigurator_example);
        label.setLayoutData(new GridData(GridData.FILL_BOTH));

        final Button button = new Button(group, SWT.PUSH);
        button.setText(Messages.FontStyleConfigurator_Select);
        button.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false,false));

        button.addListener(SWT.Selection, new Listener(){

            public void handleEvent( Event event ) {

                final FontDialog dialog = new FontDialog(button.getShell());
                FontData result = dialog.open();

                if (result != null) {
                    Font awtfont = SWTGraphics.swtFontToAwt(result);

                    IBlackboard blackboard = getStyleBlackboard();
                    FontStyle fontStyle = (FontStyle) blackboard.get(FontStyleContent.ID);
                    fontStyle.setFont(awtfont);

                    swtFont = SWTGraphics.awtFontToSwt(awtfont, JFaceResources.getFontRegistry());
                    label.setFont(swtFont);
                    label.setText(label.getText());
                }

            }

        });
    }

    @Override
    protected void refresh() {
        FontStyle fontStyle = (FontStyle) getStyleBlackboard().get(FontStyleContent.ID);

        if (fontStyle.getFont() != null) {
            swtFont = SWTGraphics.awtFontToSwt(fontStyle.getFont(), JFaceResources.getFontRegistry());
            if (label != null) {
                label.setFont(swtFont);
            }
        }
    }

}
