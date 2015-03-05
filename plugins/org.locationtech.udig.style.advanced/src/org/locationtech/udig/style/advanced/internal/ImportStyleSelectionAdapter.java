package org.locationtech.udig.style.advanced.internal;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.locationtech.udig.style.advanced.common.StyleManager;

/**
 * SelectionHandler for Style Editor Import Buttons
 * 
 * @author Frank Gasdorf
 *
 */
public final class ImportStyleSelectionAdapter extends SelectionAdapter {

    private StyleManager styleManager;

    private Shell shell;

    /**
     * @param shell for FileDialog
     * @param styleManager styleManager to import to
     */
    public ImportStyleSelectionAdapter(Shell shell, StyleManager styleManager) {
        this.shell = shell;
        this.styleManager = styleManager;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        FileDialog fileDialog = new FileDialog(shell, SWT.OPEN | SWT.MULTI);

        // TODO add supported extension for FileDialog
        String path = fileDialog.open();
        if (path == null || path.length() < 1) {
            return;
        }
        File firstFile = new File(path);
        if (!firstFile.exists()) {
            throw new IllegalArgumentException();
        }
        File folder = firstFile.getParentFile();
        String[] fileNames = fileDialog.getFileNames();
        File[] files = new File[fileNames.length];
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(folder, fileNames[i]);
        }

        for (File file : files) {
            String name = file.getName();
            File newFile = new File(styleManager.getStyleFolderFile(), name);
            try {
                FileUtils.copyFile(file, newFile);
            } catch (IOException e1) {
                MessageDialog.openWarning(shell, Messages.PointPropertiesEditor_25,
                        "Failed to copy style file into folder " + folder + "\n" + e1.getMessage());
            }
        }

        styleManager.reloadStyleFolder();
    }

}
