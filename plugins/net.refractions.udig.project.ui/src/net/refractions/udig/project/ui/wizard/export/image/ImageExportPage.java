/**
 *
 */
package net.refractions.udig.project.ui.wizard.export.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

import net.refractions.udig.project.ui.SelectionStyle;
import net.refractions.udig.project.ui.internal.Messages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

/**
 * Wizard page that allows the export directory and format to be selected.
 *
 * @author Jesse
 */
public class ImageExportPage extends WizardPage {

    private final String PDF_FORMAT = "PDF"; //$NON-NLS-1$

    private Combo formatCombo;

    private Spinner width;
    private Spinner height;
    private Button baseHeightOnWidth;
    private List<String> extensions = new ArrayList<String>();
    private Combo paperCombo;
    private Button landscape;

    private Spinner topMarginSpinner;
    private Spinner lowerMarginSpinner;
    private Spinner leftMarginSpinner;
    private Spinner rightMarginSpinner;

    private Combo selectionCombo;

    protected ImageExportPage() {
        super("Image Settings", Messages.ImageExportPage_ImageSettings, null ); //$NON-NLS-1$
        setDescription(Messages.ImageExportPage_ImageSettingsDescription);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite top = createTopLevelComposite(parent, 1);

        Composite comp = createTopLevelComposite(top, 3);
        GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
        comp.setLayoutData(gridData);

        createFormatLabel(comp);

        createFormatCombo(comp);

        Composite sizeComposite = createTopLevelComposite(top, 5);
        gridData = new GridData(SWT.FILL, SWT.NONE, true, false);

        sizeComposite.setLayoutData(gridData);

        createWidthSpinner(sizeComposite);

        createHeightSpinner(sizeComposite);

        createCheckBox(sizeComposite);

        createSelectionHandling(comp);

        addPDFSupport(top);

        selectPreferedFormat();

        setControl(top);

    }

    private void createSelectionHandling(Composite top) {
        createLabel(top, Messages.ImageExportPage_Selection);
        selectionCombo = new Combo(top, SWT.READ_ONLY);
        selectionCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
                false));
        selectionCombo.setItems(new String[] { Messages.ImageExportPage_SelectionOverlay,
                Messages.ImageExportPage_SelectionOnly, Messages.ImageExportPage_SelectionIfAvailable,
                Messages.ImageExportPage_SelectionIgnore });

        selectionCombo.setData(0 + "", SelectionStyle.OVERLAY); //$NON-NLS-1$
        selectionCombo.setData(1 + "", SelectionStyle.EXCLUSIVE); //$NON-NLS-1$
        selectionCombo.setData(2 + "", SelectionStyle.EXCLUSIVE_ALL); //$NON-NLS-1$
        selectionCombo.setData(3 + "", SelectionStyle.IGNORE); //$NON-NLS-1$
        selectionCombo.select(0);

        String selection = getWizard().getDialogSettings().get(
                ExportMapToImageWizard.SELECTION);
        if (selection != null) {
            SelectionStyle saved = SelectionStyle.valueOf(selection);
            for (int i = 0; i < 4; i++) {
                if (selectionCombo.getData(i + "") == saved) { //$NON-NLS-1$
                    selectionCombo.select(i);
                    break;
                }
            }
        }
    }

    public SelectionStyle getSelectionHandling() {
        SelectionStyle value = (SelectionStyle) selectionCombo
                .getData(selectionCombo.getSelectionIndex() + ""); //$NON-NLS-1$
        getWizard().getDialogSettings().put(ExportMapToImageWizard.SELECTION,
                value.name());
        return value;
    }

    private void addPDFSupport(Composite comp) {
        extensions.add(0, "pdf"); //$NON-NLS-1$
        formatCombo.add(PDF_FORMAT, 0);

        final Group group = new Group(comp, SWT.NONE);
        group.setText(Messages.ImageExportPage_PDF_Group_Description);
        group.setVisible(false);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        group.setLayout(new GridLayout(4, false));

        createPaperLabel(group);
        createPaperCombo(group);
        createLandscapeButton(group);
        createLandscapeLabel(group);
        createMarginsGroup(group);

        formatCombo.addListener(SWT.Modify, new Listener() {

            public void handleEvent(Event event) {
                boolean pdfComposite;
                boolean other;

                if (getFormatName().equals(PDF_FORMAT)) {
                    pdfComposite = true;
                    other = false;
                } else {
                    pdfComposite = false;
                    other = true;
                }

                group.setVisible(pdfComposite);
                baseHeightOnWidth.setEnabled(other);
                height.setEnabled(other);
                width.setEnabled(other);
            }

        });
    }

    private void createMarginsGroup(Group group) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = false;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.horizontalSpan = 4;
        Group marginsGroup = new Group(group, SWT.NONE);
        marginsGroup.setLayoutData(gridData);
        marginsGroup.setLayout(gridLayout);
        marginsGroup.setText(Messages.ImageExportPage_marginsGroup);
        Label topLabel = new Label(marginsGroup, SWT.NONE);
        topLabel.setText(Messages.ImageExportPage_topMargin);
        topMarginSpinner = new Spinner(marginsGroup, SWT.NONE);
        topMarginSpinner.setSelection(10);
        Label lowerLabel = new Label(marginsGroup, SWT.NONE);
        lowerLabel.setText(Messages.ImageExportPage_lowerMargin);
        lowerMarginSpinner = new Spinner(marginsGroup, SWT.NONE);
        lowerMarginSpinner.setSelection(10);
        Label leftLabel = new Label(marginsGroup, SWT.NONE);
        leftLabel.setText(Messages.ImageExportPage_leftMargin);
        leftMarginSpinner = new Spinner(marginsGroup, SWT.NONE);
        leftMarginSpinner.setSelection(10);
        Label rightLabel = new Label(marginsGroup, SWT.NONE);
        rightLabel.setText(Messages.ImageExportPage_rightMargin);
        rightMarginSpinner = new Spinner(marginsGroup, SWT.NONE);
        rightMarginSpinner.setSelection(10);

    }

    private void createLandscapeLabel(Group group) {
        createLabel(group, Messages.ImageExportPage_landscapeLabel);
    }

    private void createPaperLabel(Group group) {
        createLabel(group, Messages.ImageExportPage_size_Label);
    }

    private void createPaperCombo(Group group) {
        paperCombo = new Combo(group, SWT.READ_ONLY);
        paperCombo
                .setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        Paper[] paperTypes = Paper.values();
        for (Paper paper : paperTypes) {
            paperCombo.add(paper.name());
        }
        paperCombo.select(0);
    }

    private void createLandscapeButton(Group group) {
        landscape = new Button(group, SWT.CHECK);
        landscape
                .setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
    }

    private void createLabel(Composite comp, String label) {
        Label scaleLabel = new Label(comp, SWT.NONE);
        scaleLabel.setText(label);
        scaleLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
                false));
    }

    private void createCheckBox(Composite comp) {
        baseHeightOnWidth = new Button(comp, SWT.CHECK);
        baseHeightOnWidth.setText(Messages.ImageExportPage_AspectRatioCheck);

        GridData layoutData = new GridData();
        layoutData.horizontalIndent = 5;
        baseHeightOnWidth.setLayoutData(layoutData);

        baseHeightOnWidth.setSelection(true);
        baseHeightOnWidth.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            public void widgetSelected(SelectionEvent e) {
                height.setEnabled(!baseHeightOnWidth.getSelection());
            }

        });
    }

    private void createHeightSpinner(Composite comp) {
        Label label = new Label(comp, SWT.NONE);
        label.setText(Messages.ImageExportPage_ImageHeight);
        GridData layoutData = new GridData();
        layoutData.horizontalIndent = 5;
        label.setLayoutData(layoutData);

        height = new Spinner(comp, SWT.BORDER);
        initSpinner(height, ExportMapToImageWizard.HEIGHT_KEY);
        height.setEnabled(false);
    }

    private void createWidthSpinner(Composite comp) {
        Label label = new Label(comp, SWT.NONE);
        label.setText(Messages.ImageExportPage_ImageWidth);
        label.setLayoutData(new GridData());

        width = new Spinner(comp, SWT.BORDER);
        initSpinner(width, ExportMapToImageWizard.WIDTH_KEY);
    }

    private void initSpinner(Spinner spinner, String sizeKey) {
        spinner.setDigits(0);
        spinner.setIncrement(1);
        spinner.setPageIncrement(100);
        spinner.setMinimum(10);
        spinner.setMaximum(20000);

        String defaultSize = getWizard().getDialogSettings().get(sizeKey);

        if (defaultSize == null) {
            spinner.setSelection(1024);
        } else {
            spinner.setSelection(getWizard().getDialogSettings()
                    .getInt(sizeKey));
        }

        spinner.setLayoutData(new GridData());
    }

    private Composite createTopLevelComposite(Composite parent, int columns) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(columns, false);
        comp.setLayout(layout);
        return comp;
    }

    private void createFormatCombo(Composite comp) {
        formatCombo = new Combo(comp, SWT.READ_ONLY);

        loadImageWriterSpis();

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.horizontalSpan = 2;
        formatCombo.setLayoutData(gridData);
    }

    private void selectPreferedFormat() {
        String preferedFormat = getWizard().getDialogSettings().get(
                ExportMapToImageWizard.FORMAT_KEY);
        if (preferedFormat == null) {
            preferedFormat = Messages.ImageExportPage_png;
        }

        formatCombo.select(0);

        int index = 0;

        for (String extension : extensions) {
            List<String> list = Arrays.asList(extension);
            if (contains(list, preferedFormat)) {
                formatCombo.select(index);
                break;
            }
            if (contains(list, Messages.ImageExportPage_png)) {
                formatCombo.select(index);
            }
            index++;
        }

    }

    private boolean contains(List<String> list, String preferedFormat) {
        for (String string : list) {
            if (string.equalsIgnoreCase(preferedFormat)) {
                return true;
            }
        }

        return false;
    }

    private void loadImageWriterSpis() {
        IIORegistry defaultInstance = IIORegistry.getDefaultInstance();
        Iterator<ImageWriterSpi> writers = defaultInstance.getServiceProviders(
                ImageWriterSpi.class, false);

        HashSet<String> tested = new HashSet<String>();
        tested.add("PNG");
        tested.add("TIFF");
        tested.add("GIF");

        TreeMap<String,String> found = new TreeMap<String,String>();
        while (writers.hasNext()) {
            ImageWriterSpi writer = writers.next();
            String description = writer.getDescription(null);
            String suffixes[] = writer.getFileSuffixes();
            String names[] = writer.getFormatNames();
            String mimeTypes[] = writer.getMIMETypes();
            String vendor = writer.getVendorName();
            String version = writer.getVersion();
            //System.out.println( "description:" + description );
            for( String format : names){
                if( description.startsWith("Standard") ){
                    continue;
                }
                if( !found.containsKey( format )){
                    found.put( format, suffixes[0] );
                }
            }
                //if( !extensions.contains(extensions.add(writer.getFileSuffixes()[0])) ){
            	//extensions.add(writer.getFileSuffixes()[0]);
            	//formatCombo.add(writer.getFormatNames()[0]);
            //}
        }
        for( String format : found.keySet() ){
            if( !tested.contains(format)) continue;
            formatCombo.add( format );
            extensions.add( found.get( format ) );
        }
    }

    private void createFormatLabel(Composite comp) {
        createLabel(comp, Messages.ImageExportPage_FormatLabel);
    }

    public String getFormatName() {
        int selectionIndex = formatCombo.getSelectionIndex();
        String item = formatCombo.getItem(selectionIndex);
        return item;
    }

    public String getFormatExtension() {
        String format = getFormatName();
        return this.extensions.get(formatCombo.getSelectionIndex());
    }

    public int getWidth() {
        if (isPDF()) {
            int paperWidth = paper().getWidth(landscape());
            int rightMargin = rightMarginSpinner.getSelection();
            int leftMargin = leftMarginSpinner.getSelection();
            return paperWidth - rightMargin - leftMargin;
        } else {

            return width.getSelection();
        }
    }

    public int getHeight(double mapwidth, double mapheight) {
        int height;
        if (isPDF()) {
            int paperHeight = paper().getHeight(landscape());
            int topMargin = topMarginSpinner.getSelection();
            int lowerMargin = lowerMarginSpinner.getSelection();

            height = paperHeight - topMargin - lowerMargin;

        } else {
            if (baseHeightOnWidth.getSelection()) {
                height = (int) (mapheight / (mapwidth / getWidth()));
            } else {
                height = this.height.getSelection();
            }
        }

        return height;

    }

    public boolean isPDF() {
        return formatCombo.getItem(formatCombo.getSelectionIndex()).equals(
                PDF_FORMAT);
    }

    public boolean landscape() {
        return landscape.getSelection();
    }

    public Paper paper() {
        return Paper
                .valueOf(paperCombo.getItem(paperCombo.getSelectionIndex()));
    }

    public void write( BufferedImage image, File destination ) throws IOException {
        if (isPDF()) {
            Image2Pdf.write(image, destination.getAbsolutePath(), paper(), this.leftMarginSpinner
                    .getSelection(), this.topMarginSpinner.getSelection(), landscape());
        } else {
            String formatName = getFormatName();

            ImageOutputStream stream = null;
            try {
                destination.delete();
                stream = ImageIO.createImageOutputStream(destination);
            } catch (IOException e) {
                throw new IIOException("Cannot create " + destination, e);
            }
            try {
                ImageWriter writer = null;
                ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(image);
                Iterator<ImageWriter> iter = ImageIO.getImageWriters(type, formatName);
                while (iter.hasNext()) {
                    writer = (ImageWriter) iter.next();
                    String description = writer.getOriginatingProvider().getDescription(null);
                    if( !description.contains("Standard") ){
                        break;
                    }
                }
                if (writer == null) {
                    int index = formatCombo.indexOf(formatName);
                    formatCombo.remove(formatName);
                    extensions.remove(index);
                    throw new IOException("Could not find writer for this image in " + formatName);
                }
                writer.setOutput(stream);
                try {
                    writer.write(image);
                } finally {
                    writer.dispose();
                    stream.flush();
                }
            } finally {
                stream.close();
            }
        }
    }

}
