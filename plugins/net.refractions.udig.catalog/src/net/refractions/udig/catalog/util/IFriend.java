package net.refractions.udig.catalog.util;

import java.util.List;

import net.refractions.udig.catalog.IResolve;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Used to allow peer GeoSpatial resources to associate with each other.
 * <p>
 * This interface is used (by the local catalog) to determine associated IResolve
 * handles to the one provided.
 * <p>
 * In the wild associations are captured in the following manner:
 * <ul>
 * <li>informally: as with GeoServer and MapServer URL based pattern matching
 * <li>formally: with a CSW 2.0 assoication model (a plugin will need to implement this interface allowing uDig to make the correct queriest to the association model)
 * <li>formally: with OWS metadata, as with WMS Layer resource URL pointing to a WFS service
 * </ul>
 * @author Jody Garnett
 */
public abstract class IFriend {
	
	/** List of known peers for the provided resource. */
	public abstract List<IResolve> friendly( IResolve handle, IProgressMonitor monitor );
}
