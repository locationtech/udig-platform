/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.command.navigation;

import java.text.MessageFormat;

import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.ViewportModel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import org.locationtech.jts.geom.Envelope;

/**
 * Sets the viewport's bounding box. The bbox have a positive width and height
 * and must have a aspect ratio within 0.0000001 units of the value returned by
 * {@linkplain ViewportModel#getViewportAspectRatio()}.
 * 
 * @author jeichar
 * @since 0.3
 */
public class SetViewportBBoxCommand extends AbstractNavCommand {

	private Envelope newbbox = null;

	private CoordinateReferenceSystem crs;

	private boolean forceContainBBoxZoom;

    /**
     * Creates a new instance of SetViewportBBoxCommand.  The bbox is expected to be the same as the viewport model.
     * 
     * @param bbox
     *            the new bounding box. The new bbox must have a positive width
     *            and height and must have a aspect ratio within 0.0000001 units
     *            of the value returned by
     *            {@linkplain ViewportModel#getViewportAspectRatio()}.
     * @deprecated Please use a ReferencedEnvelope
     */
    public SetViewportBBoxCommand(Envelope bbox) {
        this.newbbox = bbox;
        if ( bbox instanceof ReferencedEnvelope )
            crs=((ReferencedEnvelope)bbox).getCoordinateReferenceSystem();
	}

    /**
     * Creates a new instance of SetViewportBBoxCommand.  The bbox is expected to be the same as the viewport model.
     * 
     * @param bbox
     *            the new bounding box. The new bbox must have a positive width
     *            and height and must have a aspect ratio within 0.0000001 units
     *            of the value returned by
     *            {@linkplain ViewportModel#getViewportAspectRatio()}.
     */
    public SetViewportBBoxCommand(ReferencedEnvelope bbox) {
        this(bbox,false);
    }

    /**
     * Sets the bounds of the viewport model to the bounds.  The crs parameter indications the crs of 
     * the provided bounds.  The appropriate transformation will take place.
     * 
     * @param bounds the bounds to apply to the viewport model
     * @param crs The crs of the provided bounds.
     */
	public SetViewportBBoxCommand(Envelope bounds, CoordinateReferenceSystem crs) {
		this(new ReferencedEnvelope(bounds,crs));
	}

	public SetViewportBBoxCommand(ReferencedEnvelope bbox, boolean forceContainBBoxZoom) {
        this.newbbox = bbox;
        crs=((ReferencedEnvelope)bbox).getCoordinateReferenceSystem();
        this.forceContainBBoxZoom = forceContainBBoxZoom;
	}

	/**
	 * @see org.locationtech.udig.project.internal.command.MapCommand#copy()
	 */
	public MapCommand copy() {
		return new SetViewportBBoxCommand(newbbox, crs);
	}

	/**
	 * @see org.locationtech.udig.project.internal.command.navigation.AbstractNavCommand#runImpl()
	 */
	protected void runImpl(IProgressMonitor monitor) {
		if (crs != null) {
			try {
				MathTransform mt = CRS.findMathTransform(crs, model.getCRS(),
						true);
				if (!mt.isIdentity()) {
					Envelope transformedBounds = JTS.transform(newbbox, null,
							mt, 5);
					crs = model.getCRS();
					newbbox = transformedBounds;
				}
			} catch (Exception e) {
				ProjectPlugin
						.log(
								"Error transforming from " + crs.getName() + " to " + model.getCRS().getName(), e); //$NON-NLS-1$//$NON-NLS-2$
			}
		}else{
			crs = model.getCRS();
		}
		model.setBounds(new ReferencedEnvelope(newbbox,crs), forceContainBBoxZoom);
	}

	/**
	 * @see org.locationtech.udig.project.command.MapCommand#getName()
	 */
	public String getName() {
		return MessageFormat
				.format(
						Messages.SetViewportBBoxCommand_setViewArea, new Object[] { newbbox }); 
	}

}
