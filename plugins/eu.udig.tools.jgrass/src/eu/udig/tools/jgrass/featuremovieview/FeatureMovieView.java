/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.tools.jgrass.featuremovieview;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import eu.udig.tools.jgrass.JGrassToolsPlugin;

/**
 * A navigation view.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class FeatureMovieView extends ViewPart {

    private Image playImage;
    private Image stopImage;
    private Text zoomBufferText;
    private double zoomBuffer;

    public FeatureMovieView() {
        ImageDescriptor playImageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(JGrassToolsPlugin.PLUGIN_ID,
                "icons/play.gif");
        playImage = playImageDescriptor.createImage();
        ImageDescriptor stopImageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(JGrassToolsPlugin.PLUGIN_ID,
                "icons/stop.gif");
        stopImage = stopImageDescriptor.createImage();

    }

    public void createPartControl( Composite theparent ) {

        Composite parent = new Composite(theparent, SWT.NONE);
        parent.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
        parent.setLayout(new GridLayout(1, true));

        Group playGroup = new Group(parent, SWT.NONE);
        playGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        playGroup.setLayout(new GridLayout(1, false));
        playGroup.setText("Commands");

        final Button playButton = new Button(playGroup, SWT.PUSH);
        playButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        playButton.setText("Play");
        playButton.setImage(playImage);
        playButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {

                playButton.setImage(stopImage);
            }
        });

        Group paramsGroup = new Group(parent, SWT.NONE);
        paramsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        paramsGroup.setLayout(new GridLayout(2, true));
        paramsGroup.setText("Parameters");

        Label currentFidLabel = new Label(paramsGroup, SWT.NONE);
        currentFidLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        currentFidLabel.setText("Current feature fid: ");
        Label currentFidValue = new Label(paramsGroup, SWT.NONE);
        currentFidValue.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        currentFidValue.setText(" - ");

        Label zoomBufferLabel = new Label(paramsGroup, SWT.NONE);
        zoomBufferLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        zoomBufferLabel.setText("Zoom buffer around feature");

        zoomBufferText = new Text(paramsGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        zoomBufferText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        zoomBufferText.setText("");
        zoomBufferText.addModifyListener(new ModifyListener(){
            public void modifyText( ModifyEvent e ) {
                updateZoomBuffer();
            }
        });
        updateZoomBuffer();

    }

    private void updateZoomBuffer() {
        String text = zoomBufferText.getText();
        try {
            zoomBuffer = Double.parseDouble(text);
        } catch (Exception e) {
            zoomBuffer = 10.0;
            zoomBufferText.setText(zoomBuffer + "");
        }
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

}
