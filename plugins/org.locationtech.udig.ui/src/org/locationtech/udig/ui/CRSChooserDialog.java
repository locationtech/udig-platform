/**
 * 
 */
package org.locationtech.udig.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.locationtech.udig.ui.internal.Messages;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Dialog to choose a {@link CoordinateReferenceSystem} with CRSChooser.
 *
 */
public class CRSChooserDialog extends Dialog {
    private final CRSChooser chooser = new CRSChooser();

    private final CoordinateReferenceSystem initialValue;

    private CoordinateReferenceSystem result;

    public CRSChooserDialog(Shell parentShell, CoordinateReferenceSystem initialValue) {
        super(parentShell);
        this.initialValue = initialValue;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        getShell().setText(Messages.CRSChooserDialog_title);
        chooser.setController(new Controller() {

            @Override
            public void handleClose() {
                close();
            }

            @Override
            public void handleOk() {
                result = chooser.getCRS();
            }

        });

        Control control = chooser.createControl(parent, initialValue);
        chooser.setFocus();
        return control;
    }

    @Override
    public boolean close() {
        result = chooser.getCRS();
        return super.close();
    }

    public CoordinateReferenceSystem getResult() {
        return result;
    }
}
