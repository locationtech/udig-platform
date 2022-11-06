/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.context.internal;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The most basic, simple annoying URL prompt you can imagine.
 * <p>
 * This import page is similar the eclipse example and was last seen as the ShapefileImportPage in
 * UDIG 0.7. Yes I am that lazy - lazy smart.
 * </p>
 * <p>
 * This time around I am going to try and keep things reusable, so we can use this for Context
 * Documents, SLD files whatever. Also will let us improve the "look" as needed.
 * </p>
 * <p>
 * The following user-interface elements are used:
 * <ul>
 * <li>text - will need to be a URL to proceed. (Valid file syntax will also do...)</li>
 * <li>browse - button used to bring up a file browser</li>
 * </ul>
 * </p>
 * <p>
 * In the future - I would really like to hook this into the generic DnD system Jesse is working on
 * for UDIG 1.1.x. In such a use the end of this process would be the URL being "dropped" onto the
 * current selection.
 * </p>
 *
 * @author Jody Garnett
 * @since 1.0.5
 */
public class URLWizardPage extends WizardPage implements KeyListener {

    protected URL url = null;

    private Text text;

    private String promptMessage = Messages.URLWizardPage_prompt_initial;

    /**
     * Force subclasses to actually cough up the stuff needed for a pretty user interface.
     * <p>
     * Subclass should really:
     * <ul>
     * <li>setDescription
     * <li>setMessage
     * </ul>
     * </p>
     *
     * @param pageName
     * @param title
     * @param titleImage
     */
    protected URLWizardPage(String pageName, String title, ImageDescriptor titleImage) {
        super(pageName, title, titleImage);
    }

    @Override
    public void setMessage(String newMessage) {
        if (newMessage == null)
            newMessage = promptMessage;
        super.setMessage(newMessage);
    }

    /**
     * Called to create the user interface components.
     * <p>
     * As per usual eclipse practice, both the constructor (duh) and init have been called.
     * </p>
     */
    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout(3, false));

        // add URL
        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.URLWizardPage_label_url_text);
        label.setLayoutData(new GridData(SWT.END, SWT.DEFAULT, false, false));

        text = new Text(composite, SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        text.setText(""); //$NON-NLS-1$
        text.addKeyListener(this);

        Button button = new Button(composite, SWT.PUSH);
        button.setLayoutData(new GridData());

        button.setText(Messages.URLWizardPage_button_browse_text);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Display display = Display.getCurrent();
                if (display == null) { // not on the UI thread?
                    display = Display.getDefault();
                }

                FileDialog dialog = new FileDialog(display.getActiveShell(), SWT.OPEN);
                dialog.setText(Messages.URLWizardPage_dialog_text); // hope for open ?

                String open = dialog.open();
                if (open == null) {
                    // canceled - no change
                } else {
                    text.setText(open);
                    setPageComplete(isPageComplete());
                }
            }
        });
        setControl(text);

        // Eclipse UI guidelines say we must start with prompt (not error)
        setMessage(null);
        setPageComplete(true);
    }

    /**
     * We cannot assume how far we got in the construction process. So we have to carefully tear the
     * roof down over our heads and sweep away our footprints.
     */
    @Override
    public void dispose() {
        if (text != null) {
            text.removeKeyListener(this);
            text.dispose();
            text = null;
        }
        super.dispose();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (isPageComplete()) {
            setPageComplete(true);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        setPageComplete(false);
    }

    /**
     * Default implementation will return true for a valid URL.
     * <p>
     * Valid URL is based on text containing:
     * <ul>
     * <li>URL that parses without exception</li>
     * <li>Text that can be treated as a File (and thus a URL)</li>
     * <li>URLs w/ the File protocol the file must exist!</li>
     * </ul>
     * Subclasses may override to perform extra sanity checks on the URL (for things like extention,
     * magic, etc...)
     * </p>
     *
     * @see org.eclipse.jface.wizard.IWizardPage#isPageComplete()
     * @return true if we have a useful URL
     */
    @Override
    public boolean isPageComplete() {
        url = null;

        String txt = text.getText();
        Exception errorMessage = null;

        if (txt == null || txt.length() == 0) {
            // not available
        } else {
            try {
                url = new URL(txt);
            } catch (MalformedURLException erp) {
                errorMessage = erp;
            }
            if (errorMessage != null) { // try file
                try {
                    File file = new File(txt);
                    url = file.toURI().toURL();
                    errorMessage = null;
                } catch (MalformedURLException notFile) {
                }
            }
        }
        if (url == null) {
            if (errorMessage != null) {
                setErrorMessage(errorMessage.getLocalizedMessage());
            } else {
                setMessage(""); //$NON-NLS-1$
            }
        }
        setMessage(Messages.URLWizardPage_prompt_import);
        return true;
    }

    /**
     * Called as a quick sanity check on the provided URL.
     * <p>
     * Note this method is called before checking if the URL is a file. You can assume that urlCheck
     * is true if fileCheck is called.
     * </p>
     * <p>
     * The default implementation just check that the URL is non <code>null</code>. Please override
     * to check for things like the correct extention ...
     * </p>
     *
     * @param url
     * @return true if URL is okay
     */
    protected boolean urlCheck(URL url) {
        return url != null;
    }

    /**
     * Used for a <b>quick</b> file check - don't open it!
     * <p>
     * Default implementation checks that the file exists, override to check file extention or
     * permissions or something.
     * </p>
     * <p>
     * This method is being called on every key press so don't waste your user's time. Save that
     * till they hit Next, that way you get a progress monitor...
     * </p>
     * <p>
     * Method can call setErrorMessage to report reasonable user level explanations back to the
     * user, default implementation will complain if the file does not exist.
     * </p>
     *
     * @param file
     * @return true if file passes a quick sanity check
     */
    protected boolean fileCheck(File file) {
        if (file.exists()) {
            return true;
        }
        // file = file.getAbsoluteFile();
        while (file.getParent() != null && !file.exists()) {
            file = file.getParentFile();
        }
        setErrorMessage(MessageFormat.format(Messages.URLWizardPage_prompt_error_fileNotExist,
                file.getName()));

        return false;
    }
}
