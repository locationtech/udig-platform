/**
 * 
 */
package org.locationtech.udig.project.ui.wizard.export.image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageWriterSpi;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.locationtech.udig.project.ui.SelectionStyle;
import org.locationtech.udig.project.ui.internal.Messages;

/**
 * Wizard page that allows the export directory and format to be selected.
 * 
 * @author Jesse
 */
public class ImageExportPage extends WizardPage {

    private Combo formatCombo;
    int currentFormatIndex;

    private Spinner width;
    private Spinner height;
    private Button baseHeightOnWidth;

    private Combo selectionCombo;
    private ArrayList<ImageExportFormat> formats;
    private Shell temporaryParent;
    private Composite formatConfiguration;

    public ImageExportPage() {
        super("Image Settings", Messages.ImageExportPage_ImageSettings, null); //$NON-NLS-1$
        setDescription(Messages.ImageExportPage_ImageSettingsDescription);
    }

    public void createControl( Composite parent ) {
        Composite top = createTopLevelComposite(parent, 1);

        Composite comp = createTopLevelComposite(top, 3);
        GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
        comp.setLayoutData(gridData);

        createFormatLabel(comp);

        createFormatCombo(comp);

        loadFormats();

        Composite sizeComposite = createTopLevelComposite(top, 5);
        gridData = new GridData(SWT.FILL, SWT.NONE, true, false);

        sizeComposite.setLayoutData(gridData);

        createWidthSpinner(sizeComposite);

        createHeightSpinner(sizeComposite);

        createCheckBox(sizeComposite);

        createSelectionHandling(comp);

        createFormatConfigurationComposite(top);

        selectPreferedFormat();

        setControl(top);

    }

    private void createFormatConfigurationComposite( Composite top ) {

        int maxWidth = -1, maxHeight = -1;
        for( ImageExportFormat format : formats ) {
            Point size = format.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);
            if (maxWidth < size.x) {
                maxWidth = size.x;
            }
            if (maxHeight < size.y) {
                maxHeight = size.y;
            }
            
        }

        formatConfiguration = new Composite(top, SWT.NONE);
        formatConfiguration.setLayout(new FillLayout());
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.widthHint = maxWidth;
        gridData.heightHint = maxHeight;
        formatConfiguration.setLayoutData(gridData);
    }

    private void loadFormats() {

        formats = new ArrayList<ImageExportFormat>();
        formats.addAll(loadImageWriterSpis());
        formats.add(new GeotiffImageExportFormat());
        formats.add(new PDFImageExportFormat());

        Collections.sort(formats, new Comparator<ImageExportFormat>(){

            public int compare( ImageExportFormat format1, ImageExportFormat format2 ) {
                String name1 = format1.getName().toLowerCase();
                String name2 = format2.getName().toLowerCase();
                return name1.compareTo(name2);
            }

        });

        temporaryParent = new Shell();

        // this is to make sure that temporaryParent is also disposed
        formatCombo.addDisposeListener(new DisposeListener(){

            public void widgetDisposed( DisposeEvent e ) {
                temporaryParent.dispose();
            }

        });

        for( ImageExportFormat format : formats ) {
            format.createControl(temporaryParent);
            formatCombo.add(format.getName());
        }

    }

    private List<WorldImageExportFormat> loadImageWriterSpis() {
        IIORegistry defaultInstance = IIORegistry.getDefaultInstance();
        Iterator<ImageWriterSpi> writers = defaultInstance.getServiceProviders(
                ImageWriterSpi.class, false);
        List<WorldImageExportFormat> formats = new ArrayList<WorldImageExportFormat>();
        while( writers.hasNext() ) {
            ImageWriterSpi writer = writers.next();
            formats.add(new WorldImageExportFormat(writer.getFormatNames()[0], writer
                    .getFileSuffixes()[0]));
        }

        return formats;
    }

    private void createSelectionHandling( Composite top ) {
        createLabel(top, Messages.ImageExportPage_Selection);
        selectionCombo = new Combo(top, SWT.READ_ONLY);
        selectionCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        selectionCombo.setItems(new String[]{Messages.ImageExportPage_SelectionOverlay,
                Messages.ImageExportPage_SelectionOnly,
                Messages.ImageExportPage_SelectionIfAvailable,
                Messages.ImageExportPage_SelectionIgnore});

        selectionCombo.setData(0 + "", SelectionStyle.OVERLAY); //$NON-NLS-1$
        selectionCombo.setData(1 + "", SelectionStyle.EXCLUSIVE); //$NON-NLS-1$
        selectionCombo.setData(2 + "", SelectionStyle.EXCLUSIVE_ALL); //$NON-NLS-1$
        selectionCombo.setData(3 + "", SelectionStyle.IGNORE); //$NON-NLS-1$
        selectionCombo.select(0);

        String selection = getWizard().getDialogSettings().get(ExportMapToImageWizard.SELECTION);
        if (selection != null) {
            SelectionStyle saved = SelectionStyle.valueOf(selection);
            for( int i = 0; i < 4; i++ ) {
                if (selectionCombo.getData(i + "") == saved) { //$NON-NLS-1$
                    selectionCombo.select(i);
                    break;
                }
            }
        }
    }

    static void createLabel( Composite comp, String label ) {
        Label scaleLabel = new Label(comp, SWT.NONE);
        scaleLabel.setText(label);
        scaleLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
    }

    public SelectionStyle getSelectionHandling() {
        SelectionStyle value = (SelectionStyle) selectionCombo.getData(selectionCombo
                .getSelectionIndex()
                + ""); //$NON-NLS-1$
        getWizard().getDialogSettings().put(ExportMapToImageWizard.SELECTION, value.name());
        return value;
    }

    private void createCheckBox( Composite comp ) {
        baseHeightOnWidth = new Button(comp, SWT.CHECK);
        baseHeightOnWidth.setText(Messages.ImageExportPage_AspectRatioCheck);

        GridData layoutData = new GridData();
        layoutData.horizontalIndent = 5;
        baseHeightOnWidth.setLayoutData(layoutData);

        baseHeightOnWidth.setSelection(true);
        baseHeightOnWidth.addSelectionListener(new SelectionListener(){

            public void widgetDefaultSelected( SelectionEvent e ) {
                widgetSelected(e);
            }

            public void widgetSelected( SelectionEvent e ) {
                height.setEnabled(!baseHeightOnWidth.getSelection());
            }

        });
    }

    private void createHeightSpinner( Composite comp ) {
        Label label = new Label(comp, SWT.NONE);
        label.setText(Messages.ImageExportPage_ImageHeight);
        GridData layoutData = new GridData();
        layoutData.horizontalIndent = 5;
        label.setLayoutData(layoutData);

        height = new Spinner(comp, SWT.BORDER);
        initSpinner(height, ExportMapToImageWizard.HEIGHT_KEY);
        height.setEnabled(false);
    }

    private void createWidthSpinner( Composite comp ) {
        Label label = new Label(comp, SWT.NONE);
        label.setText(Messages.ImageExportPage_ImageWidth);
        label.setLayoutData(new GridData());

        width = new Spinner(comp, SWT.BORDER);
        initSpinner(width, ExportMapToImageWizard.WIDTH_KEY);
    }

    private void initSpinner( Spinner spinner, String sizeKey ) {
        spinner.setDigits(0);
        spinner.setIncrement(1);
        spinner.setPageIncrement(100);
        spinner.setMinimum(10);
        spinner.setMaximum(20000);

        String defaultSize = getWizard().getDialogSettings().get(sizeKey);

        if (defaultSize == null) {
            spinner.setSelection(1024);
        } else {
            spinner.setSelection(getWizard().getDialogSettings().getInt(sizeKey));
        }

        spinner.setLayoutData(new GridData());
    }

    private Composite createTopLevelComposite( Composite parent, int columns ) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(columns, false);
        comp.setLayout(layout);
        return comp;
    }

    private void createFormatCombo( final Composite comp ) {
        formatCombo = new Combo(comp, SWT.READ_ONLY);

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.horizontalSpan = 2;
        formatCombo.setLayoutData(gridData);

        formatCombo.addListener(SWT.Modify, new Listener(){

            public void handleEvent( Event event ) {
                if (currentFormatIndex != formatCombo.getSelectionIndex()) {
                    ImageExportFormat newFormat = getFormat();
                    ImageExportFormat previousFormat = formats.get(currentFormatIndex);

                    boolean useStandardDimensions = newFormat.useStandardDimensionControls();
                    baseHeightOnWidth.setEnabled(useStandardDimensions);
                    height.setEnabled(useStandardDimensions);
                    width.setEnabled(useStandardDimensions);

                    previousFormat.getControl().setParent(temporaryParent);

                    newFormat.getControl().setParent(formatConfiguration);
                    formatConfiguration.layout(true);
                }

                currentFormatIndex = formatCombo.getSelectionIndex();

            }

        });
    }

    private void selectPreferedFormat() {
        String preferedFormat = getWizard().getDialogSettings().get(
                ExportMapToImageWizard.FORMAT_KEY);
        if (preferedFormat == null) {
            preferedFormat = "png"; //$NON-NLS-1$
        }

        formatCombo.select(0);

        int index = 0;

        for( ImageExportFormat format : formats ) {
            if (format.getName().equalsIgnoreCase(preferedFormat)) {
                formatCombo.select(index);
                break;
            }
            if (format.getExtension().equalsIgnoreCase("png")) { //$NON-NLS-1$
                formatCombo.select(index);
            }
            index++;
        }

    }

    private void createFormatLabel( Composite comp ) {
        createLabel(comp, Messages.ImageExportPage_FormatLabel);
    }

    /**
     * The format that will be used to write out the rendered map
     * 
     * @return format the will write out the rendered map.
     */
    public ImageExportFormat getFormat() {
        return formats.get(formatCombo.getSelectionIndex());
    }

    public int getWidth( double mapwidth, double mapheight ) {
        if (getFormat().useStandardDimensionControls()) {
            return width.getSelection();
        } else {
            return getFormat().getWidth(mapwidth, mapheight);
        }
    }

    public int getHeight( double mapwidth, double mapheight ) {
        int height;
        if (getFormat().useStandardDimensionControls()) {
            if (baseHeightOnWidth.getSelection()) {
                height = (int) (mapheight / (mapwidth / getWidth(mapwidth, mapheight)));
            } else {
                height = this.height.getSelection();
            }
        } else {
            height = getFormat().getHeight(mapwidth, mapheight);
        }

        return height;

    }

    public boolean isPDF() {
        return getFormat() instanceof PDFImageExportFormat;
    }

}
