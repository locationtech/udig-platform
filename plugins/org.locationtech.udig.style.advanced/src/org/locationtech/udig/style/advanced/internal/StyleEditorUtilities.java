package org.locationtech.udig.style.advanced.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.locationtech.udig.style.advanced.common.StyleManager;
import org.locationtech.udig.style.advanced.utils.ImageCache;

public final class StyleEditorUtilities {

    private static Image importImg = ImageCache.getInstance().getImage(ImageCache.IMPORT);
    
    /**
     * @param parent the parent composite to add the button
     * @param type the type (e.g. SWT.PUSH)
     * @param styleManager the style manager to apply the imported style
     * @return the created button with default behavior
     */
    public static Button createImportButton(Composite parent, int type, StyleManager styleManager) {
        final Button importButton = new Button(parent, SWT.PUSH);
        importButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        importButton.setImage(importImg);
        importButton.setToolTipText(Messages.PointPropertiesEditor_23);
        importButton.addSelectionListener(new ImportStyleSelectionAdapter(parent.getShell(), styleManager));
        return importButton;
    }
}
