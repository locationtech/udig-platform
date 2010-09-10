package net.refractions.udig.catalog.internal.wmt.ui.wizard.controls;

import java.net.URL;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.wmt.WMTService;
import net.refractions.udig.catalog.internal.wmt.WMTServiceExtension;
import net.refractions.udig.catalog.internal.wmt.wmtsource.CSSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSource;
import net.refractions.udig.catalog.wmt.internal.Messages;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class CSControl extends WMTWizardControl {

    private WMTServiceExtension serviceExtension;
    private Spinner spZoomMin;
    private Spinner spZoomMax;
    private Text txtUrl;
    
    @Override
    public IService getService() {
        if (txtUrl == null || txtUrl.isDisposed()) return null;
        
        String urlText = txtUrl.getText().trim();
        if (validUrl(urlText)) {
            URL url = WMTSource.getCustomServerServiceUrl(
                    urlText.substring(7), // strip out http://
                    spZoomMin.getText(),
                    spZoomMax.getText());      
            
            WMTService service = serviceExtension.createService(url, serviceExtension.createParams(url));
            
            return service;
        }
         
        return null;
    }
    
    private boolean validUrl(String url) {
        if (!url.contains(CSSource.TAG_ZOOM)) return false;
        if (!url.contains(CSSource.TAG_X)) return false;
        if (!url.contains(CSSource.TAG_Y)) return false;
        
        return url.toLowerCase().startsWith("http://"); //$NON-NLS-1$
    }
    
    @Override
    protected Control buildControl(Composite composite) {
        Composite control = new Composite(composite, SWT.NONE);
        control.setLayout(new RowLayout(SWT.VERTICAL));
        
        //region Description
        Link text = new Link(control, SWT.HORIZONTAL | SWT.WRAP);
        text.setLayoutData(new RowData(400, 110));
        text.setText(Messages.Wizard_CS_Description);
        text.addListener (SWT.Selection, new Listener () {
            public void handleEvent(Event event) {
                Program.launch(Messages.Wizard_CS_UrlTileNames);
            }
        });
        //endregion
        
        //region URL
        Label lblUrl = new Label (control, SWT.HORIZONTAL | SWT.BOLD);
        lblUrl.setText(Messages.Wizard_CS_Url);
        
        Font boldFont = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
        lblUrl.setFont(boldFont);
        
        txtUrl = new Text (control, SWT.BORDER);
        txtUrl.setLayoutData(new RowData(380, 20));
        txtUrl.setText(Messages.Wizard_CS_UrlDefault);
        //endregion
        
        //region Zoom-Range
        Composite compositeRow = new Composite(control, SWT.NONE);
        compositeRow.setLayout(new RowLayout(SWT.HORIZONTAL));
        
        Composite compositeZoom = new Composite(compositeRow, SWT.NONE);
        compositeZoom.setLayout(new RowLayout(SWT.VERTICAL));
        compositeZoom.setLayoutData(new RowData(200, 100));
        
        Label lblZoom = new Label (compositeZoom, SWT.HORIZONTAL | SWT.BOLD);
        lblZoom.setText(Messages.Wizard_CS_ZoomLevel);
        lblZoom.setFont(boldFont);
        
        //region Zoom-Min
        Composite compositeRowZoom = new Composite(compositeZoom, SWT.NONE);
        compositeRowZoom.setLayout(new GridLayout(2, true));
        Label lblZoomMin = new Label (compositeRowZoom, SWT.HORIZONTAL | SWT.BOLD);
        lblZoomMin.setText(Messages.Wizard_CS_Min);
                
        spZoomMin = new Spinner (compositeRowZoom, SWT.BORDER | SWT.READ_ONLY);
        spZoomMin.setMinimum(0);
        spZoomMin.setMaximum(22);
        spZoomMin.setSelection(2);
        spZoomMin.setIncrement(1);
        spZoomMin.pack();
        //endregion

        //region Zoom-Max
        Label lblZoomMax = new Label (compositeRowZoom, SWT.HORIZONTAL | SWT.BOLD);
        lblZoomMax.setText(Messages.Wizard_CS_Max);
                
        spZoomMax = new Spinner (compositeRowZoom, SWT.BORDER | SWT.READ_ONLY);
        spZoomMax.setMinimum(0);
        spZoomMax.setMaximum(22);
        spZoomMax.setSelection(18);
        spZoomMax.setIncrement(1);
        spZoomMax.pack();
        //endregion
        //endregion

        //region Tags
        Composite compositeTags = new Composite(compositeRow, SWT.NONE);
        compositeTags.setLayout(new RowLayout(SWT.VERTICAL));
        
        Label lblTags = new Label (compositeTags, SWT.HORIZONTAL | SWT.BOLD);
        lblTags.setText(Messages.Wizard_CS_AvailableTags);
        lblTags.setFont(boldFont);

        Label lblTagZ = new Label (compositeTags, SWT.HORIZONTAL | SWT.BOLD);
        lblTagZ.setText(Messages.Wizard_CS_TagZoom);
        
        Label lblTagX = new Label (compositeTags, SWT.HORIZONTAL | SWT.BOLD);
        lblTagX.setText(Messages.Wizard_CS_TagX);
        
        Label lblTagY = new Label (compositeTags, SWT.HORIZONTAL | SWT.BOLD);
        lblTagY.setText(Messages.Wizard_CS_TagY);
        //endregion

        serviceExtension = new WMTServiceExtension();
        this.control = control;
        
        return control;
    }

}
