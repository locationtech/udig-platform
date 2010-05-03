package net.refractions.udig.feature.editor.field;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;

/* NOT to be fully implmented to the extent it will add stuff to EditManager just
 * showing the business users what it will look like
 */

public class AddsetAttributeField extends ListAttributeField {

    private String lastPath;

    /**
     * The special label text for directory chooser, 
     * or <code>null</code> if none.
     */
    private String dirChooserLabelText;

    /**
     * Creates a new add set attribute field 
     */
    protected AddsetAttributeField() {
    }

    /**
     * Creates a add set attribute field.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param dirChooserLabelText the label text displayed for the directory chooser
     * @param parent the parent of the field editor's control
     */
    public AddsetAttributeField(String name, String labelText,
            String dirChooserLabelText, Composite parent) {
        init(name, labelText);
        this.dirChooserLabelText = dirChooserLabelText;
        createControl(parent);
    }

    /* (non-Javadoc)
     * Method declared on ListAttributeField.
     * Creates a single string from the given array by separating each
     * string with the appropriate OS-specific path separator.
     */
    protected String createList(String[] items) {
        StringBuffer path = new StringBuffer("");//$NON-NLS-1$

        for (int i = 0; i < items.length; i++) {
            path.append(items[i]);
            path.append(File.pathSeparator);
        }
        return path.toString();
    }

    /* (non-Javadoc)
     * Method declared on ListAttributeField.
     * Creates a new path element by means of a directory dialog.
     */
    protected String getNewInputObject() {

        DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SHEET);
        if (dirChooserLabelText != null) {
            dialog.setMessage(dirChooserLabelText);
        }
        if (lastPath != null) {
            if (new File(lastPath).exists()) {
                dialog.setFilterPath(lastPath);
            }
        }
        String dir = dialog.open();
        if (dir != null) {
            dir = dir.trim();
            if (dir.length() == 0) {
                return null;
            }
            lastPath = dir;
        }
        return dir;
    }

    /* (non-Javadoc)
     * Method declared on ListAttributeField.
     */
    protected String[] parseString(String stringList) {
        StringTokenizer st = new StringTokenizer(stringList, File.pathSeparator
                + "\n\r");//$NON-NLS-1$
        ArrayList<Object> v = new ArrayList<Object>();
        while (st.hasMoreElements()) {
            v.add(st.nextElement());
        }
        return v.toArray(new String[v.size()]);
    }
}
