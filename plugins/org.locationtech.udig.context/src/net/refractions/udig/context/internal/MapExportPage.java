/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.context.internal;

import java.io.File;
import java.net.URL;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.dialogs.WizardDataTransferPage;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Ask the user to select a Map, and directory (filename?) to save to ...
 * for now just use current!
 * <p>
 * The spiritual opposite of URLWizardPage.
 * </p>
 * @author Jody Garnett
 * @since 1.0.0
 */
public class MapExportPage extends WizardDataTransferPage implements KeyListener {
    
    public IMap selectedMap;
    protected File file = null;
    private Text text;
    
    private String promptMessage = Messages.MapExportPage_prompt_initial; 
    Button overwriteExistingFileCheckbox;
    
    /**
     * Force subclasses to actually cough up the stuff needed for
     * a pretty user interface.
     * <p>
     * Subclass should really:
     * <ul>
     * <li>setDescription
     * <li>setMessage
     * </ul>
     * @param pageName
     * @param title
     * @param titleImage
     */
    protected MapExportPage( String pageName, String title, ImageDescriptor titleImage ) {
        super( pageName );
        setTitle( title );
        setImageDescriptor( titleImage );
        
        selectedMap = ApplicationGIS.getActiveMap();
    }
    
    public void setMessage( String newMessage ) {
        if( newMessage == null ) newMessage = promptMessage;
        super.setMessage(newMessage);
    }
    
    /**
     * Called to create the user interface components.
     * <p>
     * As per usual eclipse practice, both the constructor (duh) and
     * init have been called.
     * </p>
     */
    public void createControl( Composite parent ) {
        Composite composite = new Composite(parent,SWT.NULL);
        composite.setLayout(new GridLayout(3, false));
        
        // add url
        Label label = new Label( composite, SWT.NONE );
        label.setText(Messages.MapExportPage_label_url_text ); 
        label.setLayoutData( new GridData(SWT.END, SWT.DEFAULT, false, false ) );

        text = new Text( composite, SWT.BORDER );
        text.setLayoutData( new GridData(GridData.FILL_HORIZONTAL) );
        text.setText( "" ); //$NON-NLS-1$
        text.addKeyListener( this );
        
        Button button = new Button(composite, SWT.PUSH);
        button.setLayoutData(new GridData());

        button.setText(Messages.MapExportPage_button_browse_text); 
        button.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                Display display = Display.getCurrent();
                if (display == null) { // not on the ui thread?
                    display = Display.getDefault();
                }
                
                FileDialog dialog = new FileDialog(display.getActiveShell(), SWT.OPEN);
                //dialog.setFilterExtensions(new String[]{"*.shp"});
                dialog.setText( Messages.MapExportPage_dialog_import_text ); // hope for open ? 
                
                String open = dialog.open();
                if(open == null){
                    // canceled - no change
                }
                else {
                    text.setText( open );
                    setPageComplete(isPageComplete());                    
                }
            }
        });        
        setControl(text);
        createBoldLabel(composite, Messages.MapExportPage_group_options_text);         
        createOptionsGroup(composite);
        
//      eclipse ui guidelines say we must start with prompt (not error)
        setMessage( null );
        setPageComplete(true);        
    }
    /**
     * Creates a new label with a bold font.
     *
     * @param parent the parent control
     * @param text the label text
     * @return the new label control
     */
    protected Label createBoldLabel(Composite parent, String text) {
        Label label = new Label(parent, SWT.NONE);
        label.setFont(JFaceResources.getBannerFont());
        label.setText(text);
        GridData data = new GridData();
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.FILL;
        label.setLayoutData(data);
        return label;
    }
    
    /**
     *  Create the options specification widgets.
     *
     *  @param parent org.eclipse.swt.widgets.Composite
     */
    protected void createOptionsGroup(Composite parent) {
        // options group
        Group optionsGroup = new Group(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        optionsGroup.setLayout(layout);
        optionsGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.GRAB_HORIZONTAL));
        optionsGroup.setText(Messages.MapExportPage_group_options_text); 
        optionsGroup.setFont(parent.getFont());

        createOptionsGroupButtons(optionsGroup);

    }
    /**
     * Creates the import/export options group controls.
     * <p>
     * The <code>WizardDataTransferPage</code> implementation of this method does
     * nothing. Subclasses wishing to define such components should reimplement
     * this hook method.
     * </p>
     *
     * @param optionsGroup the parent control
     */
    protected void createOptionsGroupButtons(Group optionsGroup) {
        Font font = optionsGroup.getFont();
        createOverwriteExisting( optionsGroup, font );        
    }
    
    /**
     * Create the button for checking if we should ask if we are going to
     * overwrite existing files.
     * @param optionsGroup
     * @param font
     */
    protected void createOverwriteExisting(Group optionsGroup, Font font) {
        // overwrite... checkbox
        overwriteExistingFileCheckbox = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
        overwriteExistingFileCheckbox.setText(Messages.MapExportPage_checkbox_overwrite_text); 
        overwriteExistingFileCheckbox.setFont(font);
    }
    
    /**
     * We cannot assume how far we got in the consturction process.
     * So we have to carefully tear the roof down over our heads and
     * sweep away our footprints.
     */
    @Override
    public void dispose() {
        if( text != null ){
            text.removeKeyListener( this ); 
            text.dispose();
            text = null;
        }
        super.dispose();
    }
    
    public void keyReleased( KeyEvent e ) {
        if(isPageComplete()){
            setPageComplete(true);
        }
    }
    public void keyPressed( KeyEvent e ) {
        setPageComplete(false);
    }
    
    /**
     * Default implementation will return true for a useful file.
     * <p>
     * Subclasses may override to perform extra sanity checks on
     * the URL (for things like extention, magic, etc...)
     * </p>
     * 
     * @see org.eclipse.jface.wizard.IWizardPage#isPageComplete()
     * @return true if we have a useful URL
     */
    public boolean isPageComplete() {
        String txt = text.getText();        
        file = new File( txt );
        
        return file != null;
    }
    /**
     * Called as a quick sanity check on the provided url.
     * <p>
     * Note this method is called before checking if the url
     * is a file. You can assume that urlCheck is true if fileCheck
     * is called.
     * </p>
     * <p>
     * The default implementation just check that the url is non
     * <code>null</code>. Please override to check for things like
     * the correct extention ...
     * </p>
     * 
     * @param url
     * @return true if url is okay
     */
    protected boolean urlCheck( URL url ){
        return url != null;
    }
    
    /**
     * Used for a <b>quick</b> file check - don't open it!
     * <p>
     * Default implementation checks that the file exists, override
     * to check file extention or permissions or something.
     * </p>
     * <p>
     * This method is being called on every key press so don't
     * waste your user's time. Save that till they hit Next, that
     * way you get a progress monitor...
     * </p>
     * <p>
     * Method can call setErrorMessage to report reasonable user
     * level explainations back to the user, default implementation
     * will complain if the file does not exist.
     * </p>
     * @param file
     * @return true if file passes a quick sanity check
     */
    protected boolean fileCheck( File file ){
        /*
         * Restriction like this is not user friendly. Perhaps adding ".xml" 
         * would be a better idea?
         * -rgould
         */
//        if( !file.getName().toUpperCase().endsWith(".XML") ){
//            setErrorMessage("File required to have extention XML" );
//            return false;
//        }
        if( file.exists() ){
            if( !overwriteExistingFileCheckbox.getSelection() ){
                setErrorMessage(Messages.MapExportPage_prompt_error_fileExists); 
                return false;
            }
        }        
        // need to add code to allow for creation of directories...
        setMessage(Messages.MapExportPage_prompt_ready); 
        return true;
    }

    public void handleEvent( Event event ) {
        if(isPageComplete()){
            setPageComplete(true);
        }
    }
}
