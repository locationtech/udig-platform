/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.catalog.ui.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.internal.Messages;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
/**
 * An abstract class that simplifies making a PreferencePage for obtaining a 3rd party proprietary jar required for a plugin.
 *
 * <p> An example of this is the Oracle Spatial JDBC Driver jar.  It cannot be shipped with uDig because of licensing so it has
 * a preference page that allows the user to easily install the jar into uDig</p>
 *
 * @see OracleSpatialPreferences
 *
 * @author Jesse
 * @since 1.1.0
 */
public abstract class AbstractProprietaryJarPreferencePage extends PreferencePage {

    private Color red;
    private Color black;
    private Listener listener;
    private Shell shell;
    private UI[] ui;

    protected static class UI {
    	public String jar_name;
    	public Label label;
    	public Text input;
    	public Button browse;
    	protected UI(String jar_name){
    		this.jar_name=jar_name;
    	}
    }

    /**
     * @return
     * @throws IOException
     */
    private File getFile( String name, boolean backup ) {
        URL url = getLibsURL();
        if (url == null)
            return new File("RandomCrazyPlaceholderlkjaljflkasdjfkjlkjasdfiwjkl"); //$NON-NLS-1$
        URL localURL;
        try {
            localURL = FileLocator.toFileURL(url);
        } catch (IOException e) {
            return new File("RandomCrazyPlaceholderlkjaljflkasdjfkjlkjasdfiwjkl"); //$NON-NLS-1$
        }
        String prefix = backup ? "." : ""; //$NON-NLS-1$ //$NON-NLS-2$
        File destDriver = new File(localURL.getFile() + prefix + name);
        return destDriver;
    }

    protected abstract URL getLibsURL();

    /**
     * Returns true if the Correct driver is installed.
     *
     * @return
     */
    protected abstract boolean installed();

    protected Control createContents( Composite parent ) {
        this.shell = parent.getShell();
        final Composite comp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginBottom = 0;
        layout.marginTop = 0;
        layout.marginRight = 0;
        layout.marginLeft = 0;
        comp.setLayout(layout);

        ui=new UI[getRequiredJarsCount()];
        List<Control> tablist=new ArrayList<Control>();

        for( int i = 0; i < ui.length; i++ ) {
            ui[i]=createJDBCdriverUI(getDefaultJarName(i), getDriverLabel(i), comp, null);
            tablist.add(ui[i].input);
            tablist.add(ui[i].browse);
        }
        comp.setTabList(tablist.toArray(new Control[0]));

        return comp;
    }

    /**
     * Returns the number of jars required to be imported.
     *
     * @return the number of jars required to be imported.
     */
    protected abstract int getRequiredJarsCount();
    /**
     * The label beside the text area that indicates what type of file the user needs to add.
     *
     * @param jarIndex the jar input area being created.
     * @see #getRequiredJarsCount()
     *
     * @return  The label beside the text area that indicates what type of file the user needs to add.
     */
    protected abstract String getDriverLabel(int jarIndex);

    /**
     * Returns a default name for the jar to import.  It will appear in the text area as a hint to the user
     * for what type of file they should be looking for.
     *
     * @param jarIndex the jar input area being created.
     * @see #getRequiredJarsCount()
     * @see #getDriverLabel(int)
     * @return a default name for the jar to import.
     */
    protected abstract String getDefaultJarName(int jarIndex);

    public AbstractProprietaryJarPreferencePage() {
        super();
    }

    /**
     * @param comp
     */
    private UI createJDBCdriverUI( String jar_name, String label, final Composite comp, Control above ) {
    	final UI ui=new UI(jar_name);
        ui.label = new Label(comp, SWT.FLAT);
        ui.label.setText(label);
        GridData layoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        ui.label.setLayoutData(layoutData);

        ui.input = new Text(comp, SWT.SINGLE | SWT.BORDER);
        ui.input.addModifyListener(new ModifyListener(){
            public void modifyText( org.eclipse.swt.events.ModifyEvent e ) {
                File srcDriver = new File(ui.input.getText());
                if (installed() )
                    acceptance(Messages.DependencyQueryPreferencePage_fileExists, ui.input);
                else if (!srcDriver.exists() )
                	error(Messages.DependencyQueryPreferencePage_fileNotFound, ui.input);
                else
                	error(Messages.DependencyQueryPreferencePage_notValid, ui.input);

            };
        });
        String storedValue = getPreferenceStore().getString(ui.jar_name);
        if (storedValue.trim().length() == 0 ) {
            storedValue = null;
        }
        ui.input.setText(storedValue == null ? ui.jar_name : storedValue);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        ui.input.setLayoutData(layoutData);

        ui.browse = new Button(comp, SWT.PUSH);
        ui.browse.setText(Messages.DependencyQueryPreferencePage_browse);

        ui.browse.addSelectionListener(new SelectionListener(){

            public void widgetSelected( SelectionEvent e ) {
                widgetDefaultSelected(e);
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
                FileDialog fileDialog = new FileDialog(comp.getShell(), SWT.OPEN);
                fileDialog.setFilterExtensions(new String[]{"*.jar"}); //$NON-NLS-1$
                fileDialog.setFilterNames(new String[]{Messages.DependencyQueryPreferencePage_archive});
                String result = fileDialog.open();
                if (result != null)
                    ui.input.setText(result);
            }

        });

        layoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        ui.browse.setLayoutData(layoutData);

        return ui;
    }

    public void performDefaults() {
    	for (UI ui : this.ui) {
    		if( ui.input!=null )
    		ui.input.setText(ui.jar_name);
    	}
        super.performDefaults();

        if (listener != null)
            listener.handleEvent(null);
    }

    public boolean performOk() {
        for( UI current : this.ui ) {
            if (current.input.getText().trim().length() > 0) {
                File srcDriver = null;
                try {
                    srcDriver = new File(current.input.getText());
                    if (!srcDriver.exists()) {
                        if (current.input.getText().equals(current.jar_name))
                            return true;
                        error(Messages.DependencyQueryPreferencePage_fileNotFound, current.input);
                        return false;
                    }
                } catch (Exception e) {
                    error(Messages.DependencyQueryPreferencePage_copyError, current.input);
                    return false;
                }
                try {
                    File destDriver = getFile(current.jar_name, false);
                    if (destDriver != null) {
                        renameFile(destDriver, "." + destDriver.getName(), false); //$NON-NLS-1$
                        destDriver.createNewFile();
                        copyfile(srcDriver, destDriver);
                    }
                } catch (Exception e) {
                    error(Messages.DependencyQueryPreferencePage_copyError, current.input);
                    return false;
                }
                getPreferenceStore().putValue(current.jar_name, current.input.getText());
            }
        }

        if (listener != null)
            listener.handleEvent(null);

        boolean restart = MessageDialog.openQuestion(getShell(),
                Messages.DependencyQueryPreferencePage_restartTitle, Messages.DependencyQueryPreferencePage_restartNeeded
                        + Messages.DependencyQueryPreferencePage_restartQuestion);
        if (restart) {
            PlatformUI.getWorkbench().restart();
        }

        return true;
    }

    @Override
    public Shell getShell() {
        return shell;
    }

    public AbstractProprietaryJarPreferencePage( String title ) {
        super( title );
    }

    /**
     * @param flag
     */
    private void renameFile( File flag, String newname, boolean deleteOnFail ) {
        if (flag != null) {
            File dest = new File(flag.getParentFile(), newname);

            try {
                if (dest.exists())
                    dest.delete();
                flag.renameTo(dest);
            } catch (Exception e) {
                try {
                    copyfile(flag, dest);
                } catch (IOException e1) {
                    CatalogUIPlugin.log("error renaming flag", e1);  //$NON-NLS-1$
                    if (deleteOnFail) {
                        flag.delete();
                    }
                }
            }
        }
    }

    private void copyfile( File src, File dest ) throws IOException {
        FileChannel srcChannel = null;
        FileChannel dstChannel = null;
        FileInputStream fileInputStream=null;
        FileOutputStream fileOutputStream=null;
        try {
            fileInputStream = new FileInputStream(src);
            fileOutputStream = new FileOutputStream(dest);
            // Create channel on the source
            srcChannel = fileInputStream.getChannel();

            // Create channel on the destination
            dstChannel = fileOutputStream.getChannel();

            // Copy file contents from source to destination
            dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
        } finally {
            // Close the channels
            try{
                if( fileInputStream!=null )
                    fileInputStream.close();
            }catch(IOException e){
                CatalogUIPlugin.log("", e); //$NON-NLS-1$
            }
            try{
                if( fileOutputStream!=null )
                    fileOutputStream.close();
            }catch(IOException e){
                CatalogUIPlugin.log("", e); //$NON-NLS-1$
            }
            try{
                if (srcChannel != null)
                    srcChannel.close();
            }catch(IOException e){
                CatalogUIPlugin.log("", e); //$NON-NLS-1$
            }
            try{
                if (dstChannel != null)
                    dstChannel.close();
            }catch(IOException e){
                CatalogUIPlugin.log("", e); //$NON-NLS-1$
            }
        }
    }

    private void error( String message, Text field ) {
    	setMessage(message, IStatus.WARNING);
        field.setForeground(getRed());
        field.setToolTipText(message);
    }

    private void acceptance( String message, Text field ) {
    	setMessage(null, IStatus.WARNING);
        field.setForeground(getBlack());
        field.setToolTipText(message);
    }

    private Color getRed() {
        if (red == null)
            red = new Color(getShell().getDisplay(), 255, 0, 0);
        return red;
    }

    private Color getBlack() {
        if (black == null)
            black = new Color(getShell().getDisplay(), 0, 0, 0);
        return black;
    }

    public void dispose() {
        super.dispose();
        if (red != null) {
            red.dispose();
            red = null;
        }
        if (black != null) {
            black.dispose();
            black = null;
        }
    }

    public void init( IWorkbench workbench ) {
    }

    public void setListener( Listener listener ) {
        this.listener = listener;
    }

    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return PlatformUI.getPreferenceStore();
    }

    public AbstractProprietaryJarPreferencePage( String title, ImageDescriptor desc ) {
        super( title, desc );
    }

}
