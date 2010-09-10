package net.refractions.udig.catalog.internal.wmt.ui.wizard.controls;

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

public class OSMControl extends WMTWizardControl {

    private ImageRegistry imageCache;
    private static final String IMG_OSM = "osm.png"; //$NON-NLS-1$
    
    @Override
    protected Control buildControl(Composite composite) {
        Composite control = new Composite(composite, SWT.NONE);
        control.setLayout(new RowLayout(SWT.VERTICAL));
        
        Label text = new Label(control, SWT.HORIZONTAL | SWT.WRAP);
        text.setLayoutData(new RowData(400, 70));
        text.setText(Messages.Wizard_Osm_Info);
        
        Link link = new Link(control, SWT.BORDER);
        link.setText(Messages.Wizard_Osm_InfoLink);
        link.setLayoutData(new RowData(400, 40));
        link.addListener (SWT.Selection, new Listener () {
            public void handleEvent(Event event) {
                Program.launch("http://www.openstreetmap.org/"); //$NON-NLS-1$
            }
        });

        imageCache = new ImageRegistry(composite.getDisplay());
        ImageDescriptor desc = ImageDescriptor.createFromFile(getClass(), 
                OSMControl.IMG_OSM);
        imageCache.put(OSMControl.IMG_OSM, desc);
        
        Composite imgControl = new Composite(control, SWT.NONE);
        imgControl.setLayoutData(new RowData(300, 100));
        imgControl.setBackgroundImage(imageCache.get(OSMControl.IMG_OSM));
        
        this.control = control;
        
        return control;
    }

}
