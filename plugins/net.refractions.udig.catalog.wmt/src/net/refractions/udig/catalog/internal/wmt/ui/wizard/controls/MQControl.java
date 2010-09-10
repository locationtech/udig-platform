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

public class MQControl extends WMTWizardControl {
    
    private ImageRegistry imageCache;
    private static final String IMG_MQ = "mq.gif"; //$NON-NLS-1$
    
    @Override
    protected Control buildControl(Composite composite) {
        Composite control = new Composite(composite, SWT.NONE);
        control.setLayout(new RowLayout(SWT.VERTICAL));
        
        Label text = new Label(control, SWT.HORIZONTAL | SWT.WRAP);
        text.setLayoutData(new RowData(400, 60));
        text.setText(Messages.Wizard_Mq_Info);
        
        Link link = new Link(control, SWT.BORDER);
        link.setText(Messages.Wizard_Mq_InfoLink);
        link.setLayoutData(new RowData(400, 60));
        link.addListener (SWT.Selection, new Listener () {
            public void handleEvent(Event event) {
                Program.launch("http://developer.mapquest.com/Home/FreeTermsAndConditions"); //$NON-NLS-1$
            }
        });

        imageCache = new ImageRegistry(composite.getDisplay());
        ImageDescriptor desc = ImageDescriptor.createFromFile(getClass(), 
                MQControl.IMG_MQ);
        imageCache.put(MQControl.IMG_MQ, desc);
        
        Composite imgControl = new Composite(control, SWT.NONE);
        imgControl.setLayoutData(new RowData(300, 100));
        imgControl.setBackgroundImage(imageCache.get(MQControl.IMG_MQ));
        
        this.control = control;
        
        return control;
    }

}
