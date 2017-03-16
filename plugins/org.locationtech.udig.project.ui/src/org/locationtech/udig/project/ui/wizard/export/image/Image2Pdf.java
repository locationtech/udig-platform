/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.wizard.export.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author Andrea Antonello - www.hydrologis.com
 * @author Frank Gasdorf
 */
public class Image2Pdf {

    /**
     * writes a buffered image to pdf at a given resolution
     *
     *
     * @param image the image to write
     * @param pdfPath the path to the pdf document to create
     * @param paper the paper type
     * @param widthMarginInPixel border in pixels to use on the x-axis
     * @param heightMarginInPixel border in pixels to use on the y-axis
     * @param lanscape true if the document should be in landscape mode
     */
    public static void write(BufferedImage image, String pdfPath, Paper paper,
            int widthMarginInPixel, int heightMarginInPixel, boolean landscape) {

        Rectangle documentPageSize = calculateSize(landscape, paper, 0, 0);
        Rectangle imageSizeInPixel = calculateSize(landscape, paper, widthMarginInPixel,
                heightMarginInPixel);

        float imgHeightInPixel = imageSizeInPixel.getHeight();
        float imgWidthInPixel = imageSizeInPixel.getWidth();

        final Document doc = new Document();
        try {

            PdfWriter.getInstance(doc, new FileOutputStream(pdfPath));
            doc.open();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            Image iTextImage = Image.getInstance(baos.toByteArray());

            doc.setPageSize(documentPageSize);
            doc.newPage(); // not needed for page 1, needed for >1


            // high in itext is measured from lower left
            iTextImage.setAbsolutePosition(widthMarginInPixel, heightMarginInPixel);
            iTextImage.scaleToFit(imgWidthInPixel, imgHeightInPixel);
            doc.add(iTextImage);
        } catch (DocumentException de) {
            System.err.println(de.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        // step 5: we close the document
        doc.close();
    }

    /**
     * @param landscape if the image is in landscape format
     * @param paper paper definition
     * @param widthMarginInPixel right and left margin in pixel
     * @param heightMarginInPixel top and bottom margin in pixel
     * @return final image size to render
     */
    private static Rectangle calculateSize(boolean landscape, Paper paper, int widthMarginInPixel,
            int heightMarginInPixel) {
        Rectangle rectangle = null;
        switch (paper) {
        case LETTER:
            rectangle = PageSize.LETTER;
            break;
        case LEGAL:
            rectangle = PageSize.LEGAL;
            break;
        case A4:
            rectangle = PageSize.A4;
            break;
        case A3:
            rectangle = PageSize.A3;
            break;
        case A2:
            rectangle = PageSize.A2;
            break;
        case A1:
            rectangle = PageSize.A1;
            break;
        case A0:
            rectangle = PageSize.A0;
            break;
        default:
            System.err.println("Cannot handle Paper to PageSize");
        }

        if (landscape) {
            rectangle = new Rectangle(rectangle.getHeight(), rectangle.getWidth());
        }

        // apply margins for the border
        return new Rectangle(rectangle.getWidth() - (2 * widthMarginInPixel),
                rectangle.getHeight() - (2 * heightMarginInPixel));
    }

    public static void main(String[] args) {

        String path = "/home/moovida/Desktop/screens/austrocontrol_2.png"; //$NON-NLS-1$
        BufferedImage b = null;
        try {
            b = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        write(b, path + ".pdf", Paper.A1, 10, 10, false); //$NON-NLS-1$
        System.out.println("finished"); //$NON-NLS-1$
    }
}
