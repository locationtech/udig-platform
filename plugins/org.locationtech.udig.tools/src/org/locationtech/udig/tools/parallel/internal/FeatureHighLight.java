/*******************************************************************************
 * Copyright (c) 2006,2012,2013 County Council of Gipuzkoa, Department of Environment
 *                              and Planning and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Aritz Davila (Axios) - initial API, implementation, and documentation
 *    Mauricio Pazos (Axios) - initial API, implementation, and documentation
 *******************************************************************************/
package org.locationtech.udig.tools.parallel.internal;

import java.awt.Rectangle;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.locationtech.udig.project.ui.IAnimation;
import org.locationtech.udig.project.ui.commands.AbstractDrawCommand;
import org.locationtech.udig.project.ui.commands.IDrawCommand;

/**
 * <p>
 * Animation command, will highlight the selected feature for some seconds.
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class FeatureHighLight extends AbstractDrawCommand implements IAnimation {

	private int									runs	= 0;
	private final List<? extends IDrawCommand>	commands;
	private final Rectangle						validArea;

	public FeatureHighLight(List<? extends IDrawCommand> commands, Rectangle validArea) {

		super();
		this.commands = commands;
		this.validArea = validArea;
	}

	public short getFrameInterval() {
		return 300;
	}

	public void nextFrame() {
		runs++;
	}

	public boolean hasNext() {
		return runs < 8;
	}

	public void run(IProgressMonitor monitor) throws Exception {
		if (runs % 2 == 0) {
			for (IDrawCommand command : commands) {
				command.setGraphics(graphics, display);
				command.setMap(getMap());
				command.run(monitor);
			}
		}
	}

	public Rectangle getValidArea() {
		return validArea;
	}

	@Override
	public void setValid(boolean valid) {
		super.setValid(valid);
		for (IDrawCommand command : commands) {
			command.setValid(valid);
		}
	}

}
