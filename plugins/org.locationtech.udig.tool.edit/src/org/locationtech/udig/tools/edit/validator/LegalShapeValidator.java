/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.validator;

import java.util.List;

import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.behaviour.IEditValidator;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.EditUtils;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;

/**
 * Checks for:
 * 
 * <ul>
 * <li>If polygon:
 * <ul>
 * <li>Self Intersection in each part of the each geometry that is flagged as
 * changed</li>
 * <li>Intersection between holes</li>
 * <li>All holes are contained within shell</li>
 * </ul>
 * </li>
 * <li>If other shapes then anything goes</li>
 * </ul>
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class LegalShapeValidator implements IEditValidator {

	String holeOverlap = Messages.LegalShapeValidator_holeOverlap;
	String holeOutside = Messages.LegalShapeValidator_holeOutside;

	public String isValid(EditToolHandler handler, MapMouseEvent event,
			EventType type) {
		EditBlackboard editBlackboard = handler.getEditBlackboard(handler
				.getEditLayer());
		List<EditGeom> geoms = editBlackboard.getGeoms();
		for (EditGeom geom : geoms) {
			String message = test(geom);
			if (message != null) {
				return message;
			}
		}
		return null;
	}

	private String test(EditGeom geom) {
		String message = testSelfIntersection(geom);
		if (message != null)
			return message;
		message = testHoleIntersection(geom);
		if (message != null)
			return message;
		message = testShellContainsHoles(geom);
		if (message != null) {
			return message;
		}

		return message;
	}

	private String testSelfIntersection(EditGeom geom) {
		if (EditUtils.instance.selfIntersection(geom.getShell())) {
			return Messages.LegalShapeValidator_shellIntersection;
		}
		for (PrimitiveShape hole : geom.getHoles()) {
			if (EditUtils.instance.selfIntersection(hole)) {
				return Messages.LegalShapeValidator_holeIntersection;
			}
		}
		return null;
	}

	private String testHoleIntersection(EditGeom geom) {
		for (PrimitiveShape hole : geom.getHoles()) {
			for (PrimitiveShape hole2 : geom.getHoles()) {
				if (hole == hole2)
					continue;

				if (hole.overlap(hole2, true, false))
					return Messages.LegalShapeValidator_holeOverlap;
			}
		}
		return null;
	}

	private String testShellContainsHoles(EditGeom geom) {
		for (PrimitiveShape shape : geom.getHoles()) {
			for (Point point : shape) {
				if (!geom.getShell().contains(point, true, true))
					return Messages.LegalShapeValidator_holeOutside;
			}
		}
		return null;
	}

}
