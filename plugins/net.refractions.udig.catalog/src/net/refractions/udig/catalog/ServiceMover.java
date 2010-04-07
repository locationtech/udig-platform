package net.refractions.udig.catalog;

import java.io.File;

/**
 * Interface for physical moving of local services on disk.
 * <p>
 * For this to work correctly the following steps MUST be taken:
 * 
 * <ol>
 * <li>Move the file(s) required by the service</li>
 * <li>Update the parameters in the service so that they indicate the new
 * location</li>
 * <li>Create a new instance of the service so the underlying object is aware
 * of the change and discards any cached information</li>
 * <li>Replace the old version in the local catalog by calling
 * {@link ICatalog#replace(java.net.URL, IService)}</li>
 * </ol>
 * </p>
 * 
 * @author Andrea Antonello - http://www.hydrologis.com
 */
public interface ServiceMover {

    /**
     * Move the service from its actual path to the given destination
     * 
     * @param destination
     *                the destination file, can be a folder as well as a file
     * @return null if the move action worked correctly or the error message
     */
    public String move(File destination);

}