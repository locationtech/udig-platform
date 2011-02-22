package net.refractions.udig.project.ui.wizard.export.image;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class Image2Pdf {

	/**
	 * writes a buffered image to pdf at a resolution of 72dpi
	 *
	 *
	 * @param image
	 *            the image to write
	 * @param pdfPath
	 *            the path to the pdf document to create
	 * @param paper
	 *            the paper type
	 * @param widthBorder
	 *            border in pixels to use on the x-axis
	 * @param heightBorder
	 *            border in pixels to use on the y-axis
	 * @param lanscape
	 *            true if the document should be in landscape mode
	 */
	public static void write(BufferedImage image, String pdfPath, Paper paper,
			int widthBorder, int heightBorder, boolean landscape) {
		Dimension printPageSize = null;
		printPageSize = new Dimension(paper.getWidth(landscape), paper.getHeight(landscape));

		// step 1: creation of a document-object
		Document document = new Document(new Rectangle(printPageSize.width,
				printPageSize.height));

		try {

			// step 2:
			// we create a writer that listens to the document
			// and directs a PDF-stream to a file
			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream(pdfPath));

			// step 3: we open the document
			document.open();

			// step 4: we create a template and a Graphics2D object that
			// corresponds with it
			int w = printPageSize.width;
			int h = printPageSize.height;
			PdfContentByte cb = writer.getDirectContent();
			PdfTemplate tp = cb.createTemplate(w, h);
			Graphics2D g2 = tp.createGraphics(w, h);
			tp.setWidth(w);
			tp.setHeight(h);

			g2.drawImage(image, null, widthBorder, heightBorder);

			g2.dispose();
			cb.addTemplate(tp, 0, 0);

		} catch (DocumentException de) {
			System.err.println(de.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}

		// step 5: we close the document
		document.close();
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
