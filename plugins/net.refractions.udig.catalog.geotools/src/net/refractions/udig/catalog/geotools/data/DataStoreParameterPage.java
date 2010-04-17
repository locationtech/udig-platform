package net.refractions.udig.catalog.geotools.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.catalog.ui.AbstractUDIGImportPage;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;

public class DataStoreParameterPage extends AbstractUDIGImportPage implements UDIGConnectionPage {

    public DataStoreParameterPage() {
        super("Parameters");
    }

    public void createControl( Composite parent ) {
        parent.setLayout( new MigLayout());
        setControl( parent );
        Label label = new Label(parent, SWT.DEFAULT);
        label.setText("Factory:");        
    }

}
