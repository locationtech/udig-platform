package net.refractions.udig.catalog.internal.wmt.ui.wizard.controls;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.wmt.ww.WWServiceExtension;
import net.refractions.udig.catalog.wmt.internal.Messages;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;

public class WWControl extends WMTWizardControl {
    
    private ImageRegistry imageCache;
    private static final String IMG_DEMIS = "demis.png"; //$NON-NLS-1$

    @Override
    public IService getService() {
        WWServiceExtension serviceExtension = new WWServiceExtension();
        
        URL url = WWControl.class.getResource("DemisWorldMap.xml"); //$NON-NLS-1$
        Map<String, Serializable> params =  serviceExtension.createParams(url); 
                
        IService service = serviceExtension.createService(url, params);
        
        return service;
    }
    
    @Override
    protected Control buildControl(Composite composite) {
        Composite control = new Composite(composite, SWT.NONE);
        control.setLayout(new RowLayout(SWT.VERTICAL));
        
        Label text = new Label(control, SWT.HORIZONTAL | SWT.WRAP);
        text.setLayoutData(new RowData(400, 80));
        text.setText(Messages.Wizard_Ww_Example_Demis_Info);
        
        imageCache = new ImageRegistry(composite.getDisplay());
        ImageDescriptor desc = ImageDescriptor.createFromFile(getClass(), 
                WWControl.IMG_DEMIS);
        imageCache.put(WWControl.IMG_DEMIS, desc);
        
        Composite imgControl = new Composite(control, SWT.NONE);
        imgControl.setLayoutData(new RowData(300, 100));
        imgControl.setBackgroundImage(imageCache.get(WWControl.IMG_DEMIS));
        
        Link link = new Link(control, SWT.BORDER);
        link.setText(Messages.Wizard_Ww_Example_Demis_Link);
        link.setLayoutData(new RowData(400, 40));
        link.addListener (SWT.Selection, new Listener () {
            public void handleEvent(Event event) {
                Program.launch(Messages.Wizard_Ww_Example_Demis_LinkUrl); 
            }
        });
        
        this.control = control;
        
        return control;
    }
    
    
}
