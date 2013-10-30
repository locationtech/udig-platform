/**
 * 
 */
package org.locationtech.udig.catalog.memory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.udig.catalog.ui.AbstractUDIGImportPage;
import org.locationtech.udig.catalog.ui.UDIGConnectionPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author jones
 */
public class NewServiceConnectionPage extends AbstractUDIGImportPage implements UDIGConnectionPage {

    private Map<String, Serializable> params=new HashMap<String, Serializable>();
    public NewServiceConnectionPage() {
        super("New Layer"); //$NON-NLS-1$
    }

    public Map<String, Serializable> getParams() {
        return params;
    }

    public void createControl( Composite parent ) {
        setControl(new Composite(parent, SWT.NONE));
    }

}
