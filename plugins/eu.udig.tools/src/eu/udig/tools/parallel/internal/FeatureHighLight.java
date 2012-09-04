/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 *      http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to licence under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
