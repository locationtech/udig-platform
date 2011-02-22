package net.refractions.udig.style.sld.editor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.transform.TransformerException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.internal.SetDefaultStyleProcessor;
import net.refractions.udig.style.internal.StyleLayer;
import net.refractions.udig.style.sld.SLDContent;
import net.refractions.udig.style.sld.SLDPlugin;
import net.refractions.udig.style.sld.internal.Messages;
import net.refractions.udig.ui.graphics.SLDs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.FeatureSource;
import org.geotools.event.GTEvent;
import org.geotools.renderer.style.SLDStyleFactory;
import org.geotools.styling.SLDParser;
import org.geotools.styling.SLDTransformer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryFinder;
import org.geotools.styling.StyledLayerDescriptor;
import org.opengis.coverage.grid.GridCoverage;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class StyleXMLPage extends StyleEditorPage {

    SashForm sash;
    Text sldTextBox;
    Text errorsTextBox;
    SLDValidator validator;
    Label validateStatus;
    Button validateButton;

    boolean validSLD = true;
    boolean dirty = false;

    @Override
    public void createPageContent( Composite parent ) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        comp.setLayoutData(gd);
        comp.setLayout(new GridLayout(3, false));

        sash = new SashForm(comp, SWT.VERTICAL);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = parent.getBounds().x;
        gd.heightHint = parent.getBounds().y;
        gd.horizontalSpan = 3;
        sash.setLayoutData(gd);

        sldTextBox = new Text(sash, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        sldTextBox.setEditable(true);
        sldTextBox.addModifyListener(new ModifyListener() {

            public void modifyText( ModifyEvent e ) {
                dirty = true;
                validateStatus.setText(Messages.StyleEditor_xml_validation_needed);
                validateButton.setEnabled(true);
            }

        });

        sldTextBox.addFocusListener(new FocusListener() {
            public void focusGained( FocusEvent e ) {
            }

            public void focusLost( FocusEvent e ) {
//                if (isValid())
//                    updateSLD();
            }
        });

        errorsTextBox = new Text(sash, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        errorsTextBox.setVisible(false);
        errorsTextBox.setEditable(false);

        validateStatus = new Label(comp, SWT.LEFT);
        validateStatus.setText(Messages.StyleEditor_xml_validation_needed);
        gd = new GridData(SWT.LEFT, SWT.BOTTOM, true, false);
        validateStatus.setLayoutData(gd);

//TODO: add code for style reset
//        Button resetButton = new Button(comp, SWT.RIGHT);
//        resetButton.setText("Reset");
//        gd = new GridData(SWT.RIGHT, SWT.NONE, false, false);
//        resetButton.setLayoutData(gd);
//        resetButton.addSelectionListener(new SelectionListener() {
//
//            public void widgetSelected( SelectionEvent e ) {
//
//            }
//
//            public void widgetDefaultSelected( SelectionEvent e ) {
//                widgetSelected(e);
//            }
//
//        });

        validateButton = new Button(comp, SWT.RIGHT);
        validateButton.setText(Messages.StyleEditor_xml_validate);
        gd = new GridData(SWT.RIGHT, SWT.NONE, false, false);
        validateButton.setLayoutData(gd);
        validateButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected( SelectionEvent e ) {
                validateSLD();
                if (validSLD) {
                    updateSLD();
                }
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
                widgetSelected(e);
            }

        });

        styleChanged(null);
    }

    @Override
    public boolean performCancel() {
        return true;
    }

    @Override
    public String getErrorMessage() {
        if (!validSLD) {
            return Messages.StyleEditor_xml_invalid;
        }
        return null;
    }

    public void validateSLD() {
        Cursor waitCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_WAIT);
        setDisplayCursor(waitCursor);

        validateButton.setEnabled(false);
        validateStatus.setText(Messages.StyleEditor_xml_validation_progress);
        String xml = sldTextBox.getText();
        dirty = false;
        if (xml.length() == 0) {
            validSLD = false;
            sldTextBox.setText(styleToXML()); //revert to default
            return;
        }
        InputStream is = getXMLasInputStream(xml, "UTF-8"); //$NON-NLS-1$
        if (validator == null) {
            validator = new SLDValidator();
        }
        Object result = validator.validateSLD(is, "http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd"); //$NON-NLS-1$

        if (result instanceof List) {
            List errors = (List) result;
            if (errors.size() == 0) {
            	validSLD = true;
                this.errorsTextBox.setText("");  //$NON-NLS-1$
                this.validateStatus.setText(Messages.StyleEditor_xml_validation_success);
                this.errorsTextBox.setVisible(false);
            } else {
            	validSLD = false;
                this.errorsTextBox.setText(SLDValidator.getErrorMessage(is, errors));
                this.validateStatus.setText(Messages.StyleEditor_xml_validation_failure);
                this.errorsTextBox.setVisible(true);
            }
            this.errorsTextBox.getParent().layout();
            getContainer().updateMessage();
        }
        try {
            is.close();
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        } finally {
            resetCursor(waitCursor);
        }
        if (dirty) {
            validateButton.setEnabled(true);
            validateStatus.setText(Messages.StyleEditor_xml_validation_needed);
        }
    }

    private void resetCursor(Cursor c) {
        setDisplayCursor(null);
        c.dispose();
        c = null;
    }

    /**
     * Sets the given cursor for all shells currently active
     * for this window's display.
     *
     * @param c the cursor
     */
    private void setDisplayCursor(Cursor c) {
        Shell[] shells = Display.getCurrent().getShells();
        for (int i = 0; i < shells.length; i++)
            shells[i].setCursor(c);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public boolean okToLeave() {
        boolean readyToLeave = false;
        if (validSLD || (!validSLD && dirty)) { //!dirty was a condition -- not required for the moment
            readyToLeave = updateSLD();
        }
        if (readyToLeave) {
            return true;
        } else {
            //inform the user that the SLD is invalid -- fix it or lose it
            return MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
                Messages.StyleEditor_xml_lose_changes_1,
                Messages.StyleEditor_xml_lose_changes_2);
        }
    }

    public boolean performOk() {
        return true;
    }

    private String styleToXML() {
        SLDTransformer aTransformer = new SLDTransformer();
        aTransformer.setIndentation(StyleEditor.INDENT);
        StyledLayerDescriptor sld = getSLD();
        try {
            return aTransformer.transform(sld);
        } catch (TransformerException e) {
            e.printStackTrace();
            return null;
        }
    }

    private StyledLayerDescriptor XMLtoSLD(String xml) {
        return XMLtoSLD(xml, "UTF-8"); //$NON-NLS-1$
    }

    private StyledLayerDescriptor XMLtoSLD(String xml, String encoding) {
        //save changes to style object
        StyleFactory factory = StyleFactoryFinder.createStyleFactory();
        InputStream is = getXMLasInputStream(xml, encoding);
        if (is == null) return null;
        SLDParser stylereader = new SLDParser(factory, is);
        StyledLayerDescriptor sld = stylereader.parseSLD();
        return sld;
    }

    private InputStream getXMLasInputStream(String xml, String encoding) {
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(xml.getBytes(encoding));
        } catch (UnsupportedEncodingException e1) {
            // TODO Handle UnsupportedEncodingException
            throw (RuntimeException) new RuntimeException( ).initCause( e1 );
        }
        return is;
    }

    public void gotFocus() {
        refresh();
        dirty = false;
    };

    @Override
    public void styleChanged( GTEvent arg ) {
        dirty = true;
        validateButton.setEnabled(true);
    }

    public void refresh() {
        String xmlOrig = sldTextBox.getText();
        String xml = styleToXML();
        if (xml != null && !xml.equals(xmlOrig)) {
            sldTextBox.setText(xml);
            validSLD = true;
            dirty = true;
        }
    }

    private boolean updateSLD() {
        //busy cursor
        Cursor waitCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_WAIT);
        setDisplayCursor(waitCursor);

        //generate the SLD
        StyledLayerDescriptor sld = null;
        Style style = null;
        String xml = sldTextBox.getText();
        if (xml == null) {
            resetCursor(waitCursor);
            return false;
        }
        try {
            sld = XMLtoSLD(xml);
            style = SLDs.getDefaultStyle(sld);
        } catch (Exception e) {

            validSLD = false;

            boolean result = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                    Messages.StyleEditor_xml_failure_1,
                    Messages.StyleEditor_xml_failure_2);

            resetCursor(waitCursor);

            if( result ){
                try {
                    StyleLayer layer = getContainer().getSelectedLayer();
                    IGeoResource resource = layer.findGeoResource(FeatureSource.class);
                    if( resource!=null ){
                        style = (Style) new SLDContent().createDefaultStyle(resource, layer.getDefaultColor(), null);
                    }else{
                        resource = layer.findGeoResource(GridCoverage.class);
                        if( resource!=null ){
                            style = (Style) new SLDContent().createDefaultStyle(resource, layer.getDefaultColor(), null);
                        }
                    }
                    if( style!=null ){
                        sld = SLDContent.createDefaultStyledLayerDescriptor(style);
                        setStyle(style);
                        refresh();
                    }
                } catch (IOException e1) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e1 );
                }
            } else return false; // abort

        }
        //update the style / SLD
        if (sld != null && style != null) {
            validSLD = true;
            setStyle(style);
            resetCursor(waitCursor);
            return true;
        }
        resetCursor(waitCursor);
        return false;
    }

    @Override
    public void dispose() {
        validator = null;
        super.dispose();
    }

    @Override
    public String getLabel() {
        return null;
    }

    public boolean performApply() {
        return updateSLD();
    }

}
