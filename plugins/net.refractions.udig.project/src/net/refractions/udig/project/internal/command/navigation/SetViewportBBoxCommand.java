/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.command.navigation;

import java.text.MessageFormat;

import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.internal.Messages;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.render.ViewportModel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Envelope;

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
        this.newbbox = bbox;
        crs=((ReferencedEnvelope)bbox).getCoordinateReferenceSystem();
    }

    /**
     * Sets the bounds of the viewport model to the bounds.  The crs parameter indications the crs of 
     * the provided bounds.  The appropriate transformation will take place.
     * 
     * @param bounds the bounds to apply to the viewport model
     * @param crs The crs of the provided bounds.
     */
	public SetViewportBBoxCommand(Envelope bounds, CoordinateReferenceSystem crs) {
		this(bounds);
		this.crs = crs;
	}

	/**
	 * @see net.refractions.udig.project.internal.command.MapCommand#copy()
	 */
	public MapCommand copy() {
		return new SetViewportBBoxCommand(newbbox, crs);
	}

	/**
	 * @see net.refractions.udig.project.internal.command.navigation.AbstractNavCommand#runImpl()
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
		model.setBounds(new ReferencedEnvelope(newbbox,crs));
	}

	/**
	 * @see net.refractions.udig.project.command.MapCommand#getName()
	 */
	public String getName() {
		return MessageFormat
				.format(
						Messages.SetViewportBBoxCommand_setViewArea, new Object[] { newbbox }); 
	}

}