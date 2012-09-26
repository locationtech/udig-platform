/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
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
package net.refractions.udig.catalog.internal.wmt.ui.wizard.controls;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.wmt.WMTService;
import net.refractions.udig.catalog.internal.wmt.WMTServiceExtension;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSource;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

public class JGrasstoolsTmsFolderControl extends WMTWizardControl {

    private WMTServiceExtension serviceExtension;
    private Text txtUrl;
    private boolean isTMS = false;

    @Override
    public IService getService() {
        if (txtUrl == null || txtUrl.isDisposed())
            return null;

        String urlText = txtUrl.getText().trim();
        File urlFile = new File(urlText);
        if (!urlFile.exists()) {
            return null;
        }
        String urlString = null;
        String zoomMin = "0";
        String zoomMax = "18";
        List<String> fileLines = null;
        try {
            fileLines = FileUtils.readLines(urlFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        for( String line : fileLines ) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }

            int split = line.indexOf('=');
            if (split != -1) {
                String value = line.substring(split + 1).trim();
                if (line.startsWith("url")) {
                    int indexOfZ = value.indexOf("ZZZ"); //$NON-NLS-1$
                    String folderName = value.substring(0, indexOfZ);
                    urlString = urlFile.getParent() + "/" + folderName + "/{z}/{x}/{y}.png";
                }
                if (line.startsWith("minzoom")) {
                    zoomMin = value;
                }
                if (line.startsWith("maxzoom")) {
                    zoomMax = value;
                }
                // if (line.startsWith("center")) {
                // try {
                //                        String[] coord = value.split("\\s+"); //$NON-NLS-1$
                // double x = Double.parseDouble(coord[0]);
                // double y = Double.parseDouble(coord[1]);
                // centerPoint = new GeoPoint(y, x);
                // } catch (NumberFormatException e) {
                // // use default
                // }
                // }
                if (line.startsWith("type")) {
                    if (value.toLowerCase().equals("tms")) {
                        isTMS = true;
                    }
                }
            }
        }

        URL url = WMTSource.getCustomServerServiceUrl(urlString, zoomMin, zoomMax, isTMS ? "TMS" : null);
        WMTService service = serviceExtension.createService(url, serviceExtension.createParams(url));
        return service;
    }

    @Override
    protected Control buildControl( Composite composite ) {
        final Composite control = new Composite(composite, SWT.NONE);
        control.setLayout(new GridLayout(2, false));

        // region Description
        Link text = new Link(control, SWT.HORIZONTAL | SWT.WRAP);
        GridData textGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        textGD.horizontalSpan = 2;
        text.setLayoutData(textGD);
        text.setText("Here you can load a TMS tiles folder that was exported with the Spatial Toolbox and the JGrasstools tiler.");
        // endregion

        Label lblUrl = new Label(control, SWT.HORIZONTAL | SWT.BOLD);
        lblUrl.setText("Select the tiles file definition (*.mapurl)");
        Font boldFont = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
        lblUrl.setFont(boldFont);
        GridData lblUrlGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        lblUrlGD.horizontalSpan = 2;
        lblUrl.setLayoutData(lblUrlGD);

        txtUrl = new Text(control, SWT.BORDER);
        txtUrl.setLayoutData(new RowData(380, 20));
        GridData txtUrlGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        txtUrl.setLayoutData(txtUrlGD);
        Button browseButton = new Button(control, SWT.PUSH);
        browseButton.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
        browseButton.setText("...");
        browseButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                FileDialog fileDialog = new FileDialog(control.getShell(), SWT.OPEN);
                String path = fileDialog.open();
                if (path == null || path.length() < 1) {
                    txtUrl.setText("");
                } else {
                    txtUrl.setText(path);
                }
            }
        });

        serviceExtension = new WMTServiceExtension();
        this.control = control;
        return control;
    }

}
