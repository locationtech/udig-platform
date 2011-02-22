/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.tool.info;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.tool.info.internal.Messages;

/**
 * The response to a getInfo request of a service w.r.t. a layer.
 * <p>
 * This LayerPointInfo object allows the wrapping arbitrary content, providing type information as a MIME
 * type, and the possability of lazy access.
 * </p>
 * <p>
 * This class is expected to be subclassed inorder to perform lazy parsing via a custom
 * implementation of getValue();
 * </p>
 */
public abstract class LayerPointInfo {

	/**
	 * Layer responsible for providing this information.
	 */
	ILayer layer;

	/**
	 * Used to indicate an NOT_FOUND result.
	 * <p>
	 * Not found is different from "unavailable".
	 * </p>
	 */
	public static final LayerPointInfo NOT_FOUND = new LayerPointInfo(null) {
		public String getMimeType() {
			return ""; //$NON-NLS-1$
		}

		public Object acquireValue() {
			return null;
		}
	};
	/** The mime type for gml - aka Feature */
	public static final String GML = "application/vnd.ogc.gml"; //$NON-NLS-1$

	/** The mime type for text/plain */
	public static final String TEXT = "text/plain"; //$NON-NLS-1$

	/** The mime type for text/html */
	public static final String HTML = "text/html"; //$NON-NLS-1$

	/** The mime type for text/xml */
	public static final String XML = "text/xml"; //$NON-NLS-1$

	/** Server Error - value is Exception or String */
	public static final String ERROR = "application/vnd.ogc.se_xml"; //$NON-NLS-1$

	/** Construct an LayerPointInfo for the provided Layer */
	public LayerPointInfo(ILayer layer) {
		this.layer = layer;
	}

	/**
	 * Subclass must implement to acquire value in a lazy manner.
	 * <p>
	 * The default implementation will make the request specified by getRequestURL().getContent();
	 * </p>
	 *
	 * @return Object to be used as the Value of this LayerPointInfo
	 * @throws IOException if an IO error occurs during the request process
	 */
	public Object acquireValue() throws IOException {
		return getRequestURL().getContent();
	}

	/**
	 * The data's MIME type.
	 * <p>
	 * Well known MIME types:
	 * <ul>
	 * <li>"application/vnd.ogc.gml" - represents a FeatureCollection
	 * <li>"text/html" - represents a web page
	 * <li>"text/plain"
	 * <li>"" - indicates LayerPointInfo.EMPTY
	 * </ul>
	 * </p>
	 * <p>
	 * The system may be extended at a later time to allow the use of additional MIME types. The
	 * goal should be to allow the complete set used in WMS getFeatureInfo opperations.
	 * </p>
	 *
	 * @see http://www.w3.org/Protocols/HTTP/1.1/spec.html#MIME
	 */
	abstract public String getMimeType();

	/**
	 * Request url, if applicable, will be sent to an embded browser or something similar.
	 * <p>
	 * For *real objects* please just return the default value, aka null. acquireValue will be
	 * called.
	 * </p>
	 *
	 * @return requested URL or null if working with a real objects.
	 */
	public URL getRequestURL() {
		return null;
	}

	/**
	 * Layer responsible for providing this information.
	 * <p>
	 * Can be used as a back point to focus the map on the indicated hit.
	 * </p>
	 *
	 * @return Returns the layer.
	 */
	public ILayer getLayer() {
		return layer;
	}

	/** @return LayerPointInfo mimeType : request */
	public String toString() {
		return MessageFormat
				.format(
						Messages.LayerPointInfo_toString, new Object[] { getMimeType(), getRequestURL() });
	}
}
