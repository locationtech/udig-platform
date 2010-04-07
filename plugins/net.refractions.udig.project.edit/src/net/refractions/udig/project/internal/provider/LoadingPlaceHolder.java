package net.refractions.udig.project.internal.provider;

import org.eclipse.swt.graphics.Image;

/**
 * Placeholder that will show up in viewers while the contentProvider is loading the data.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface LoadingPlaceHolder {
    String getText();
    Image getImage();
}