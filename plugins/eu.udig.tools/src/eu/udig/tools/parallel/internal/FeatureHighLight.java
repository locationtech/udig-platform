/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering S.L. (Axios)
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package eu.udig.tools.parallel.internal;

import java.awt.Rectangle;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import net.refractions.udig.project.ui.IAnimation;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.project.ui.commands.IDrawCommand;

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
