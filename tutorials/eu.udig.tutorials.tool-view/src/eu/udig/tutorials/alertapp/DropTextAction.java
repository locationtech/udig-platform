package eu.udig.tutorials.alertapp;

import java.awt.Color;
import java.awt.Rectangle;

import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.ui.IDropAction;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * When a URL is dropped on the map this action will render the URL on the 
 * map at the drop location for 2 seconds (more or less)
 */
public class DropTextAction extends IDropAction {

	@Override
	public boolean accept() {
		return true;
	}

	@Override
	public void perform(IProgressMonitor monitor) {
		DrawTextCommand cmd = new DrawTextCommand(getEvent().x,getEvent().y,getData().toString());
		((AlertAppContext)getDestination()).getMap().sendCommandASync(cmd);
	}

	public static final class DrawTextCommand extends AbstractDrawCommand {

		private int x;
		private int y;
		private String data;

		public DrawTextCommand(int x, int y, String data) {
			this.x = x;
			this.y = y;
			this.data = data;
		}

		@Override
		public Rectangle getValidArea() {
			return null;
		}

		@Override
		public void run(IProgressMonitor monitor) throws Exception {
			graphics.setColor(Color.BLUE);
			graphics.drawString(data, x, y, ViewportGraphics.ALIGN_MIDDLE, ViewportGraphics.ALIGN_BOTTOM);
		}
		
	}
}
