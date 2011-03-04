package eu.udig.tutorials.alertapp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.ui.IDropAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/**
 * When a URL is dropped on the map this action will render the URL on the 
 * map at the drop location for 5 seconds (more or less).
 * 
 * The input (as defined in the drop action extension definition) is a string.  The string may come from 
 * outside the application or within the application.  In this case there is no way to drag and drop a string
 * within the application so it will be coming from outside the application.
 * 
 * String from the outside can be nearly anything.  A file, an image from a word document, a file from a browser.
 * 
 * In this case we will take the file and try to turn it into a url by either:
 *  - directly creating a url from the string
 *  - turning the string into a file then a url
 *  - or using regex to test if the string contains an image tag and extracting the src url from the image tag.
 *  
 * Since this is not production code those are the only cases we are looking for.  
 */
public class DropTextAction extends IDropAction implements Runnable{
	// pattern for extracting the image tag
	private final static Pattern IMAGE_TAG_PATTERN = Pattern.compile(".*<\\s*img[^>]*src=\"(.*?)\".*");
	
	// the image to be shown if the image could not be loaded
	private final static BufferedImage ERROR_IMG;
	static {
		ERROR_IMG = new BufferedImage(20,20,BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2d = ERROR_IMG.createGraphics();
		g2d.clearRect(0, 0, 20, 20);
		g2d.dispose();
	}

	/**
	 * Only accepting urls that we think reference an image
	 */
	@Override
	public boolean accept() {
		String string = toImageURLStrimg(getData().toString());
		if(string == null) {
			return false;
		}
		for(String suffix:ImageIO.getReaderFileSuffixes()) {
			if(suffix.trim().length() > 0 && string.endsWith("."+suffix.trim())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This action takes a string.  Many datatypes that are dropped on this target from outside the 
	 * custom application can be converted to a string.  If an object is dropped from within the application
	 * it will likely be a specific object so will not be caught by this class.
	 * 
	 * The reason this is important is because when DND from outside the application it is hard to know what the
	 * string will look like.  It might be a file in which case it is very platform dependent.  It might be a URL
	 * in which case it will be platform and browser dependent.  It might be an image from a browser in which case it
	 * is typically a nice string but might be an image tag.  So testing on many browsers and platforms is required
	 * when dropping objects from outside uDig.
	 * 
	 * In this case we accept files, image tags and URL strings
	 */
	private String toImageURLStrimg(String string) {
		String urlString = string;
		Matcher matcher = IMAGE_TAG_PATTERN.matcher(string);
		
		if(matcher.find()) {
			urlString = matcher.group(1);
		}

		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			File file = new File(urlString);
			if(file.exists()) {
				try {
					file.toURI().toURL();
				} catch (MalformedURLException e1) {
					// skip
				}
			}
		}
		if(url==null) {
			return null;
		}
		return url.toExternalForm();
	}

	@Override
	public void perform(IProgressMonitor monitor) {
		final Display display = getEvent().display;
		display.asyncExec(this);
	}

	@Override
	public void run() {
		// ApplicationGIS has a handy method for creating a context from a map object
		final IToolContext context = ApplicationGIS.createContext(((View)getDestination()).getMap());
		
		/*
		 * We want to show the image at the drop location but remember, the event.x and y are in the 
		 * display coordinate reference system (normally 0,0 is corner of application not control) so 
		 * we need to call the display.map function to get the position within the MapViewer control 
		 */
		final Display display = getEvent().display;
		int x = getEvent().x;
		int y = getEvent().y;
		Point mappedToViewport = display.map(null, context.getViewportPane().getControl(), x,y);
		
		String urlString = toImageURLStrimg(getData().toString());
		if(urlString == null) {
			throw new IllegalArgumentException("Do not know how to convert "+getData()+" to a URL");
		}
		
		BufferedImage image;
		try {
			image = ImageIO.read(new URL(urlString));
		} catch (IOException e) {
			image = ERROR_IMG;
		}
		
		// create a command and send to the context so it will be displayed on the viewport.
		final DrawImageCommand cmd = new DrawImageCommand(mappedToViewport.x,mappedToViewport.y,image);
		context.sendASyncCommand(cmd);
		
		// after 5 seconds invalidate the image and replaint the viewport to get rid of the image
		display.timerExec(5000, new Runnable() {
			public void run() {
				cmd.setValid(false);
				context.getViewportPane().repaint();
			}
		});
	}
	
	/**
	 * Simple Command implementation for drawing the image.
	 */
	public static final class DrawImageCommand extends AbstractDrawCommand {

		private int x;
		private int y;
		private BufferedImage image;

		public DrawImageCommand(int x, int y, BufferedImage image) {
			this.x = x;
			this.y = y;
			this.image = image;
		}

		@Override
		public Rectangle getValidArea() {
			return null;
		}

		@Override
		public void run(IProgressMonitor monitor) throws Exception {
			graphics.setColor(Color.BLUE);
			graphics.drawImage((RenderedImage)image, x - image.getWidth()/2, y-image.getHeight()/2);
		}
		
	}
}
