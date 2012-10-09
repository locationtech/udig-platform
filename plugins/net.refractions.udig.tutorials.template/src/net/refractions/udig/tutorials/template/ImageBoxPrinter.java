/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.tutorials.template;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.refractions.udig.printing.model.AbstractBoxPrinter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class ImageBoxPrinter extends AbstractBoxPrinter {
	private BufferedImage image;
	
	public ImageBoxPrinter() {
		super();
		
		URL imageURL = this.getClass().getResource("logo.jpg");
		try {
			image = ImageIO.read(imageURL);
		} catch(IOException e) {
			image = null;
		}
	}

	public void draw(Graphics2D graphics, IProgressMonitor monitor) {
		if(image != null) {
			graphics.drawImage(image, 0, 0, null);
		}
		
	}

    public String getExtensionPointID() {
        return "net.refractions.udig.tutorials.template.image"; //$NON-NLS-1$
    }
    
    public  Object getAdapter( Class adapter ) {
        if( adapter.isAssignableFrom( BufferedImage.class )){
            return image;
        }
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

}
