/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.printing.ui.pdf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.core.internal.Icons;
import org.locationtech.udig.printing.model.Box;
import org.locationtech.udig.printing.model.ModelFactory;
import org.locationtech.udig.printing.model.Page;
import org.locationtech.udig.printing.ui.Template;
import org.locationtech.udig.printing.ui.TemplateFactory;
import org.locationtech.udig.printing.ui.internal.Messages;
import org.locationtech.udig.printing.ui.internal.PrintingEngine;
import org.locationtech.udig.printing.ui.internal.PrintingPlugin;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.command.navigation.SetViewportBBoxCommand;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.ApplicationGIS.DrawMapParameter;
import org.locationtech.udig.project.ui.BoundsStrategy;
import org.locationtech.udig.project.ui.SelectionStyle;
import org.locationtech.udig.project.ui.internal.MapEditorInput;
import org.opengis.coverage.grid.GridCoverageReader;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

/**
 * A user interface for choosing an export template and other export options. The wizard is
 * configurable by placing an ExportPDFWizardConfigBean on the map blackboard (see
 * ExportPDFWizardConfigBean for the blackboard key).
 *
 * @author brocka
 * @since 1.1.0
 */
public class ExportPDFWizard extends Wizard implements IExportWizard {

    private Map map;

    private ExportPDFWizardPage1 page1;

    public ExportPDFWizard() {
        PrintingPlugin plugin = PrintingPlugin.getDefault();

        java.util.Map<String, TemplateFactory> templateFactories = plugin.getTemplateFactories();

        setWindowTitle(Messages.ExportPDFWizard_Title);

        String key = Icons.WIZBAN + "exportpdf_wiz.gif"; //$NON-NLS-1$
        ImageRegistry imageRegistry = plugin.getImageRegistry();
        ImageDescriptor image = imageRegistry.getDescriptor(key);
        if (image == null) {
            URL banURL = plugin.getBundle().getResource("icons/" + key); //$NON-NLS-1$
            image = ImageDescriptor.createFromURL(banURL);
            imageRegistry.put(key, image);
        }
        setDefaultPageImageDescriptor(image);

        // get copy of map
        IEditorInput input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .getActiveEditor().getEditorInput();
        map = (Map) ((MapEditorInput) input).getProjectElement();

        // get configuration for this wizard
        IBlackboard mapBlackboard = map.getBlackboard();
        ExportPDFWizardConfigBean config = (ExportPDFWizardConfigBean) mapBlackboard
                .get(ExportPDFWizardConfigBean.BLACKBOARD_KEY);

        page1 = new ExportPDFWizardPage1(templateFactories, config);
        addPage(page1);
    }

    @Override
    public boolean canFinish() {
        return page1.isPageComplete();
    }

    @Override
    public boolean performFinish() {

        // create the document
        Rectangle suggestedPageSize = getITextPageSize(page1.getPageSize());
        Rectangle pageSize = rotatePageIfNecessary(suggestedPageSize); // rotate if we need
                                                                       // landscape

        Document document = new Document(pageSize);

        try {

            // Basic setup of the Document, and get instance of the iText Graphics2D
            // to pass along to uDig's standard "printing" code.
            String outputFile = page1.getDestinationDir() + System.getProperty("file.separator") + //$NON-NLS-1$
                    page1.getOutputFile();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.open();
            Graphics2D graphics = null;
            Template template = getTemplate();

            int i = 0;
            int numPages = 1;
            do {

                // sets the active page
                template.setActivePage(i);

                PdfContentByte cb = writer.getDirectContent();

                Page page = makePage(pageSize, document, template);

                graphics = cb.createGraphics(pageSize.getWidth(), pageSize.getHeight());

                // instantiate a PrinterEngine (pass in the Page instance)
                PrintingEngine engine = new PrintingEngine(page);

                // make page format
                PageFormat pageFormat = new PageFormat();
                pageFormat.setOrientation(PageFormat.PORTRAIT);
                java.awt.print.Paper awtPaper = new java.awt.print.Paper();
                awtPaper.setSize(pageSize.getWidth() * 3, pageSize.getHeight() * 3);
                awtPaper.setImageableArea(0, 0, pageSize.getWidth(), pageSize.getHeight());
                pageFormat.setPaper(awtPaper);

                // run PrinterEngine's print function
                engine.print(graphics, pageFormat, 0);

                graphics.dispose();
                document.newPage();
                if (i == 0) {
                    numPages = template.getNumPages();
                }
                i++;

            } while (i < numPages);

            // cleanup
            document.close();
            writer.close();
        } catch (DocumentException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (PrinterException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * converts a page size "name" (such as "A3" or "A4" into a rectangle object that iText will
     * understand.
     */
    private Rectangle getITextPageSize(String pageSizeName) {
        if (pageSizeName.equals("A3")) //$NON-NLS-1$
            return PageSize.A3;
        if (pageSizeName.equals("A4")) //$NON-NLS-1$
            return PageSize.A4;
        throw new IllegalArgumentException(pageSizeName + " is not a supported page size"); //$NON-NLS-1$
    }

    protected Rectangle rotatePageIfNecessary(Rectangle suggestedPageSize) {
        // rotate the page if dimensions are given as portrait, but template prefers landscape
        if (suggestedPageSize.getHeight() > suggestedPageSize.getWidth() && page1.isLandscape()) {
            float temp = suggestedPageSize.getWidth();
            float newWidth = suggestedPageSize.getHeight();
            float newHeight = temp;
            return new Rectangle(suggestedPageSize.getHeight(), suggestedPageSize.getWidth());
        }
        return suggestedPageSize;
    }

    /**
     * Creates a page based on the template selected in the wizard **Note: this function may swap
     * the width and height if the template prefers different page orientation.
     *
     * @return a page
     */
    protected Page makePage(Rectangle pageSize, Document doc, Template template) {

        Map mapOnlyRasterLayers = null;
        Map mapNoRasterLayers = null;

        // **Note: the iText API doesn't render rasters at a high enough resolution if
        // they are written to the PDF via graphics2d. To work around this problem, I
        // create two copies of the map: one with only the raster layers, and one with
        // everything else.
        // The "everything else" map gets drawn by a graphics2d. The other layer must be
        // rasterized and inserted into the PDF via iText's API.

        // make one copy of the map with no raster layers
        mapNoRasterLayers = (Map) ApplicationGIS.copyMap(map);
        List<Layer> layersNoRasters = mapNoRasterLayers.getLayersInternal();
        List<Layer> toRemove = new ArrayList<>();
        for (Layer layer : layersNoRasters) {
            for (IGeoResource resource : layer.getGeoResources()) {
                if (resource.canResolve(GridCoverageReader.class)) {
                    toRemove.add(layer);
                }
            }
        }
        layersNoRasters.removeAll(toRemove);

        // adjust scale
        double currentViewportScaleDenom = map.getViewportModel().getScaleDenominator();
        if (currentViewportScaleDenom == -1)
            throw new IllegalStateException(
                    "no scale denominator is available from the viewport model"); //$NON-NLS-1$

        if (page1.getScaleOption() == PrintWizardPage1.CUSTOM_MAP_SCALE) {
            float customScale = page1.getCustomScale();
            template.setMapScaleHint(customScale);
        } else if (page1.getScaleOption() == PrintWizardPage1.CURRENT_MAP_SCALE) {
            template.setMapScaleHint(currentViewportScaleDenom);
        } else if (page1.getScaleOption() == PrintWizardPage1.ZOOM_TO_SELECTION) {
            template.setZoomToSelectionHint(true);
            template.setMapScaleHint(currentViewportScaleDenom);
        }

        // 3. make the page itself
        Page page = ModelFactory.eINSTANCE.createPage();
        page.setSize(new Dimension((int) pageSize.getWidth(), (int) pageSize.getHeight()));

        // page name stuff not required, because this page will just get discarded
        MessageFormat formatter = new MessageFormat(Messages.CreatePageAction_newPageName,
                Locale.getDefault());
        if (page.getName() == null || page.getName().length() == 0) {
            page.setName(formatter.format(new Object[] { mapNoRasterLayers.getName() }));
        }

        template.init(page, mapNoRasterLayers);

        if (page1.getRasterEnabled()) {
            // make another copy with only raster layers
            mapOnlyRasterLayers = (Map) ApplicationGIS.copyMap(map);
            List<Layer> layersOnlyRasters = mapOnlyRasterLayers.getLayersInternal();
            List<Layer> toRemove2 = new ArrayList<>();
            for (Layer layer : layersOnlyRasters) {
                for (IGeoResource resource : layer.getGeoResources()) {
                    if (!resource.canResolve(GridCoverageReader.class)) {
                        toRemove2.add(layer);
                    }
                }
            }
            layersOnlyRasters.removeAll(toRemove2);

            // set bounds to match the other map
            SetViewportBBoxCommand cmdBbox = new SetViewportBBoxCommand(
                    mapNoRasterLayers.getViewportModel().getBounds());
            mapOnlyRasterLayers.sendCommandSync(cmdBbox);

            if (!layersNoRasters.isEmpty()) {
                writeRasterLayersOnlyToDocument(mapOnlyRasterLayers, template.getMapBounds(), doc,
                        page.getSize(), /* currentViewportScaleDenom */mapNoRasterLayers
                                .getViewportModel().getScaleDenominator());
            }
        }

        // copy the boxes from the template into the page
        Iterator<Box> iter = template.iterator();
        while (iter.hasNext()) {
            page.getBoxes().add(iter.next());
        }
        return page;

        // TODO Throw some sort of exception if the page can't be created

    }

    /**
     * Gets the template selected in the wizard.
     *
     * @return a template
     */
    private Template getTemplate() {

        TemplateFactory templateFactory = page1.getTemplateFactory();
        Template template = templateFactory.createTemplate();

        return template;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    }

    /**
     * double scaleDenom = page1.isCustomScale() ? page1.getCustomScale() :
     * map.getViewportModel().getScaleDenominator();
     *
     * @param mapWithRasterLayersOnly a map with only raster layers
     * @param mapBoundsInTemplate a rectangle indicating the coordinates of the top left, width and
     *        height (where the coordinate system has (0,0) in the top left.
     * @param doc the PDF document object
     */
    private void writeRasterLayersOnlyToDocument(Map mapWithRasterLayersOnly,
            org.eclipse.swt.graphics.Rectangle mapBoundsInTemplate, Document doc,
            Dimension pageSize, double currentViewportScaleDenom) {

        // set dimensions of the raster image to be the same ratio as
        // the required map bounds within the page, but scaled up so
        // we can achieve a higher DPI when we later insert the image
        // into the page (90 refers to the default DPI)
        int w = mapBoundsInTemplate.width * page1.getDpi() / 90;
        int h = mapBoundsInTemplate.height * page1.getDpi() / 90;

        BufferedImage imageOfRastersOnly = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imageOfRastersOnly.createGraphics();

        // define a DrawMapParameter object with a custom BoundsStrategy if a custom scale is set
        DrawMapParameter drawMapParameter = null;

        double scaleDenom = (page1.getScaleOption() == ExportPDFWizardPage1.CUSTOM_MAP_SCALE)
                ? page1.getCustomScale()
                : currentViewportScaleDenom;

        drawMapParameter = new DrawMapParameter(g, new java.awt.Dimension(w, h),
                mapWithRasterLayersOnly, new BoundsStrategy(scaleDenom), page1.getDpi(),
                SelectionStyle.EXCLUSIVE_ALL, null);

        try {

            // draw the map (at a high resolution as specified above)
            ApplicationGIS.drawMap(drawMapParameter);
            Image img = Image.getInstance(bufferedImage2ByteArray(imageOfRastersOnly));

            // scale the image down to fit into the page
            img.scaleAbsolute(mapBoundsInTemplate.width, mapBoundsInTemplate.height);

            // set the location of the image
            int left = mapBoundsInTemplate.x;
            int bottom = pageSize.height - mapBoundsInTemplate.height - mapBoundsInTemplate.y;
            img.setAbsolutePosition(left, bottom); // (0,0) is bottom left in the PDF coordinate
                                                   // system

            doc.add(img);
            addWhiteMapBorder(img, doc);

        } catch (Exception e) {
            // TODO: fail gracefully.
        }

    }

    /**
     * This function is used to draw very thin white borders over the outer edge of the raster map.
     * It's necessary because the map edge "bleeds" into the adjacent pixels, and we need to cover
     * that.
     *
     * I think this quirky behaviour is possibly an iText bug
     */
    private void addWhiteMapBorder(Image img, Document doc) {

        try {

            Color color = Color.white;
            int borderWidth = 1;

            BufferedImage bufferedTop = new BufferedImage((int) img.getScaledWidth(), borderWidth,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g1 = bufferedTop.createGraphics();
            g1.setBackground(color);
            g1.clearRect(0, 0, bufferedTop.getWidth(), bufferedTop.getHeight());
            Image top = Image.getInstance(bufferedImage2ByteArray(bufferedTop));
            top.setAbsolutePosition(img.getAbsoluteX(),
                    img.getAbsoluteY() + img.getScaledHeight() - bufferedTop.getHeight() / 2);

            BufferedImage bufferedBottom = new BufferedImage((int) img.getScaledWidth(),
                    borderWidth, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bufferedBottom.createGraphics();
            g2.setBackground(color);
            g2.clearRect(0, 0, bufferedBottom.getWidth(), bufferedBottom.getHeight());
            Image bottom = Image.getInstance(bufferedImage2ByteArray(bufferedBottom));
            bottom.setAbsolutePosition(img.getAbsoluteX(),
                    img.getAbsoluteY() - bufferedTop.getHeight() / 2);

            BufferedImage bufferedLeft = new BufferedImage(borderWidth, (int) img.getScaledHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g3 = bufferedLeft.createGraphics();
            g3.setBackground(color);
            g3.clearRect(0, 0, bufferedLeft.getWidth(), bufferedLeft.getHeight());
            Image left = Image.getInstance(bufferedImage2ByteArray(bufferedLeft));
            left.setAbsolutePosition(img.getAbsoluteX() - bufferedLeft.getWidth() / 2,
                    img.getAbsoluteY());

            BufferedImage bufferedRight = new BufferedImage(borderWidth,
                    (int) img.getScaledHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g4 = bufferedRight.createGraphics();
            g4.setBackground(color);
            g4.clearRect(0, 0, bufferedRight.getWidth(), bufferedRight.getHeight());
            Image right = Image.getInstance(bufferedImage2ByteArray(bufferedRight));
            right.setAbsolutePosition(
                    img.getAbsoluteX() + img.getScaledWidth() - bufferedRight.getWidth() / 2,
                    img.getAbsoluteY());

            doc.add(top);
            doc.add(bottom);
            doc.add(left);
            doc.add(right);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] bufferedImage2ByteArray(BufferedImage img) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
            ImageIO.write(img, "png", baos); //$NON-NLS-1$
            baos.flush();
            byte[] result = baos.toByteArray();
            baos.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

}
