package net.refractions.udig.catalog.internal.wmt.ui.wizard.controls;

import java.net.URL;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.wmt.WMTService;
import net.refractions.udig.catalog.internal.wmt.WMTServiceExtension;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSource;
import net.refractions.udig.catalog.wmt.internal.Messages;
import net.refractions.udig.core.internal.CorePlugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class OSMCloudMadeControl extends WMTWizardControl {
    
    private ListViewer lvCloudMade;
    private ListViewer lvFeatured;
    private Label lblStyleIdField;
    private Text txtStyleId;
    private Link link;
    private Button btnStyleGroups;
    private Group gBCloudMade;
    private Group gBFeatured;
    private Button btnRefresh;
    private Composite previewImage;
    private Display display;

    private Label lblStyleNameValue;
    private Label lblStyleIdValue;
    private Label lblStyleAuthorValue;

    private OSMCloudMadeStylesManager stylesManager;
    private OSMCloudMadeStylesManager.CloudMadeStyleGroup styleGroupFeatured;
    private OSMCloudMadeStylesManager.CloudMadeStyleGroup styleGroupCloudMade;
    
    private WMTServiceExtension serviceExtension;
    
    private ImageRegistry imageCache;
    
    private volatile String previewStyleId;
  
    @Override
    public IService getService() {
        URL url = WMTSource.getCloudMadeServiceUrl(getSelectedStyleId());        
        WMTService service = serviceExtension.createService(url, serviceExtension.createParams(url));
        
        return service;
    }

    private String getSelectedStyleId() {
        if ((btnStyleGroups != null) && !btnStyleGroups.isDisposed()) {
            if (btnStyleGroups.getSelection()) {
                return getSelectedStyle().getId();
            } else {
                return txtStyleId.getText().trim();
            }
        }
        
        return ""; //$NON-NLS-1$
    }
    
    //region Listener methods
    private SelectionAdapter radioSelectionListener = new SelectionAdapter() {
        
        /**
         * Is called when one of the two radio-buttons is selected.
         */
        public void widgetSelected(SelectionEvent event) {
            boolean enableState = btnStyleGroups.getSelection();
            
            // disable every component one by one, so that everyone is grayed out
            lvCloudMade.getList().setEnabled(enableState);
            lvFeatured.getList().setEnabled(enableState);
            gBCloudMade.setEnabled(enableState);
            gBFeatured.setEnabled(enableState);
            lblStyleIdField.setEnabled(!enableState);
            txtStyleId.setEnabled(!enableState);
            btnRefresh.setEnabled(!enableState);
            link.setEnabled(!enableState);
            
            updatePreview();
        }
        
    };
    
    private SelectionAdapter listSelectionListener = new SelectionAdapter() {
        
        /**
         * Is called when a style in one of the two groups
         * is selected. Ensures that just one style is selected
         * in both groups.
         */
        public void widgetSelected(SelectionEvent event) {
            // clear selection of the other list
            if (event.getSource() == lvCloudMade.getList()) {
                lvFeatured.getList().setSelection(-1);
            } else {
                lvCloudMade.getList().setSelection(-1);
            }
            
            updatePreview();            
        }
        
    };
    //endregion
    
    @Override
    protected Control buildControl(Composite infoBox) {
        display = infoBox.getDisplay();
        Composite composite = new Composite(infoBox, SWT.NONE);
        composite.setLayout(new RowLayout(SWT.VERTICAL));
                
        //region Get Style From Groups
        btnStyleGroups = new Button(composite, SWT.RADIO);
        btnStyleGroups.setSelection(true);
        btnStyleGroups.setText(Messages.Wizard_CloudMade_StyleFromGroup);
        btnStyleGroups.addSelectionListener(radioSelectionListener);
                
        Composite compositeGroups = new Composite(composite, SWT.NONE);
        compositeGroups.setLayout(new RowLayout(SWT.HORIZONTAL));
                
        gBCloudMade = new Group(compositeGroups, SWT.BORDER);
        gBCloudMade.setLayout(new RowLayout(SWT.VERTICAL));
        gBCloudMade.setText(Messages.Wizard_CloudMade_GroupCloudMade);
        
        lvCloudMade = new ListViewer(gBCloudMade);
        lvCloudMade.getList().setLayoutData(new RowData(180, 80));        
        lvCloudMade.setContentProvider(new ArrayContentProvider());
        lvCloudMade.setLabelProvider(new LabelProvider());     
        
        gBCloudMade.pack();
                
        gBFeatured = new Group(compositeGroups, SWT.BORDER);
        gBFeatured.setLayout(new RowLayout(SWT.VERTICAL));
        gBFeatured.setText(Messages.Wizard_CloudMade_GroupFeatured);
        
        lvFeatured = new ListViewer(gBFeatured);
        lvFeatured.getList().setLayoutData(new RowData(180, 80));        
        lvFeatured.setContentProvider(new ArrayContentProvider());
        lvFeatured.setLabelProvider(new LabelProvider());   
        
        gBFeatured.pack();
        //endregion
            
        //region Get Style From Id
        Button btnOwnStyle = new Button(composite, SWT.RADIO);
        btnOwnStyle.setText(Messages.Wizard_CloudMade_StyleFromId);
                
        Composite compositeStyleId = new Composite(composite, SWT.NONE);
        compositeStyleId.setLayout(new RowLayout(SWT.HORIZONTAL));
        
        lblStyleIdField = new Label (compositeStyleId, SWT.HORIZONTAL);
        lblStyleIdField.setText(Messages.Wizard_CloudMade_StyleId);
        
        txtStyleId = new Text (compositeStyleId, SWT.BORDER);
        txtStyleId.setBounds(10, 10, 200, 200);
        txtStyleId.setText(Messages.Wizard_CloudMade_DefaultStyleId);
        
        txtStyleId.addKeyListener(new KeyListener(){
            public void keyPressed(KeyEvent event) {
                if ((event.keyCode == SWT.CR) || (event.keyCode == SWT.KEYPAD_CR)) {
                    updatePreview();
                }               
            }

            public void keyReleased(KeyEvent arg0) {}           
        });
        
        txtStyleId.addListener (SWT.Verify, new Listener () {
            public void handleEvent (Event e) {
                String input = e.text;
                for (int i=0; i < input.length(); i++) {
                    if (!('0' <= input.charAt(i) && input.charAt(i) <= '9')) {
                        e.doit = false;
                        return;
                    }
                }
            }
        });
        
        btnRefresh = new Button (compositeStyleId, SWT.PUSH);
        btnRefresh.setText (Messages.Wizard_CloudMade_RefreshPreview);
        btnRefresh.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updatePreview();
            }
        });

        
        link = new Link(composite, SWT.BORDER);
        link.setText(Messages.Wizard_CloudMade_StyleEditorInfo);
        link.setLayoutData(new RowData(400, 50));
        link.addListener (SWT.Selection, new Listener () {
            public void handleEvent(Event event) {
                Program.launch("http://maps.cloudmade.com/editor"); //$NON-NLS-1$
            }
        });
        //endregion
                 
        //region Preview
        Group gBPreview = new Group(composite, SWT.BORDER);
        gBPreview.setLayout(new RowLayout(SWT.HORIZONTAL));
        gBPreview.setText(Messages.Wizard_CloudMade_Preview);
                
        Composite compositeStyleInfo = new Composite(gBPreview, SWT.NONE);
        compositeStyleInfo.setLayout(new RowLayout(SWT.VERTICAL));
        compositeStyleInfo.setLayoutData(new RowData(180, 160));
                        
        Label lblStyleName = new Label (compositeStyleInfo, SWT.HORIZONTAL | SWT.BOLD);
        lblStyleName.setText(Messages.Wizard_CloudMade_PreviewName);
        Font boldFont = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
        lblStyleName.setFont(boldFont);
        
        lblStyleNameValue = new Label (compositeStyleInfo, SWT.HORIZONTAL);
        lblStyleNameValue.setText(""); //$NON-NLS-1$
        
        Label lblStyleId = new Label (compositeStyleInfo, SWT.HORIZONTAL);
        lblStyleId.setText(Messages.Wizard_CloudMade_PreviewId);
        lblStyleId.setFont(boldFont);
        
        lblStyleIdValue = new Label (compositeStyleInfo, SWT.HORIZONTAL);
        lblStyleIdValue.setText(""); //$NON-NLS-1$
        
        Label lblStyleAuthor = new Label (compositeStyleInfo, SWT.HORIZONTAL);
        lblStyleAuthor.setText(Messages.Wizard_CloudMade_PreviewAuthor);
        lblStyleAuthor.setFont(boldFont);
        
        lblStyleAuthorValue = new Label (compositeStyleInfo, SWT.HORIZONTAL);
        lblStyleAuthorValue.setText(""); //$NON-NLS-1$
        
        previewImage = new Composite(gBPreview, SWT.NONE);
        previewImage.setLayoutData(new RowData(233, 160));
        previewImage.setCursor(new Cursor(display, SWT.CURSOR_HAND));
        previewImage.setToolTipText(Messages.Wizard_CloudMade_PreviewGetFullMap);
        previewImage.addMouseListener(new MouseListener() {

            public void mouseDoubleClick(MouseEvent arg0) {}
            public void mouseUp(MouseEvent arg0) {}

            public void mouseDown(MouseEvent arg0) {
                if (!previewStyleId.equals(OSMCloudMadeStylesManager.CloudMadeStyle.EMPTY_STYLE_ID)) {
                    Program.launch("http://maps.cloudmade.com/?lat=51.508315&lng=-0.124712&zoom=14&styleId=" + previewStyleId);  //$NON-NLS-1$
                }           
            }
        });

        gBPreview.pack();
        //endregion

        //region Load group styles
        stylesManager = new OSMCloudMadeStylesManager();
        
        // Group CloudMade
        styleGroupCloudMade = stylesManager.getGroupCloudMade();
        lvCloudMade.setInput(styleGroupCloudMade.getStyles());
        
        // Group Featured
        styleGroupFeatured = stylesManager.getGroupFeatured();
        lvFeatured.setInput(styleGroupFeatured.getStyles());
        
        // Select default style
        if (styleGroupCloudMade.getStyles().size() > 0) {
            lvCloudMade.getList().setSelection(0);
        }     
        //endregion
        
        //region Set up image cache
        imageCache = new ImageRegistry(display);
        
        // load default image
        ImageDescriptor descDefault = ImageDescriptor.createFromFile(getClass(), 
                OSMCloudMadeStylesManager.IMG_DEFAULT);
        imageCache.put(OSMCloudMadeStylesManager.CloudMadeStyle.EMPTY_STYLE_ID, descDefault);
        
        // load loading image
        ImageDescriptor descLoading = ImageDescriptor.createFromFile(getClass(), 
                OSMCloudMadeStylesManager.IMG_LOADING);
        imageCache.put(OSMCloudMadeStylesManager.IMG_LOADING, descLoading);
        //endregion
        
        //region Set up listeners
        lvCloudMade.getList().addSelectionListener(listSelectionListener);
        lvFeatured.getList().addSelectionListener(listSelectionListener);
        radioSelectionListener.widgetSelected(null);
        //endregion
        
        serviceExtension = new WMTServiceExtension();
        
        control = composite;
        
        return composite;
    }
    
    private void updatePreview() {
        OSMCloudMadeStylesManager.CloudMadeStyle style;
        
        if (btnStyleGroups.getSelection()) {
            style = getSelectedStyle();
        } else {
            // try to load the style from the id
            style = stylesManager.getStyleFromId(txtStyleId.getText().trim());
        }
        
        updatePreviewWithStyle(style);              
    }
    
    private void updatePreviewWithStyle(OSMCloudMadeStylesManager.CloudMadeStyle style) {
        lblStyleNameValue.setText(style.getName());
        lblStyleNameValue.pack();
        lblStyleIdValue.setText(style.getId());
        lblStyleIdValue.pack();
        lblStyleAuthorValue.setText(style.getAuthor());
        lblStyleAuthorValue.pack();
        
        // update preview map
        updatePreviewMap(style.getId());        
    }
    
    private void updatePreviewMap(final String styleId) {
        previewStyleId = styleId;
        
        // show loading image
        previewImage.setBackgroundImage(imageCache.get(OSMCloudMadeStylesManager.IMG_LOADING));
        
        // then get map preview image
        Image image = imageCache.get(styleId);
        
        if (image == null) {
            new Thread() {
                public void run() {  
                    String imageUrl = "http://tile.cloudmade.com/c8d1aeca771d57d6a0584fea7ce386f4/" + styleId + "/256/15/16372/10896.png"; //$NON-NLS-1$ //$NON-NLS-2$
                    
                    Image newImage = null;
                    try{
                        URL url = new URL(null, imageUrl, CorePlugin.RELAXED_HANDLER);
                        ImageDescriptor imageDescr = ImageDescriptor.createFromURL(url);
                        
                        imageCache.put(styleId, imageDescr);
                        newImage = imageCache.get(styleId);
                    } catch(Exception exc) {
                        // getting image failed, display default image
                        newImage = null;
                    } finally {
                        if (newImage == null || ((!newImage.isDisposed()) && (newImage.getBounds().width < 256))) {
                            newImage = imageCache.get(OSMCloudMadeStylesManager.CloudMadeStyle.EMPTY_STYLE_ID);
                        }
                    }
                    
                    // check if no other style was selected and do need to display this image
                    if (previewStyleId.equals(styleId)) {
                        final Image newImageToDisplay = newImage;
                        display.asyncExec(new Runnable(){
                            public void run() {
                                previewImage.setBackgroundImage(newImageToDisplay);
                            }
                        });
                    }
                }
            }.start();
        } else {
            previewImage.setBackgroundImage(image);
        }
    }
    
    private OSMCloudMadeStylesManager.CloudMadeStyle getSelectedStyle() {
        if (lvCloudMade.getList().getSelectionCount() > 0) {
            return styleGroupCloudMade.getStyles().get(lvCloudMade.getList().getSelectionIndex());
        } else if (lvFeatured.getList().getSelectionCount() > 0) {
            return styleGroupFeatured.getStyles().get(lvFeatured.getList().getSelectionIndex());
        } else {        
            return OSMCloudMadeStylesManager.CloudMadeStyle.EMPTY_STYLE;
        }
    }
}
