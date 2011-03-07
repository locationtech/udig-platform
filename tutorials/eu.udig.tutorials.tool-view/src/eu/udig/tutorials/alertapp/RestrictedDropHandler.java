package eu.udig.tutorials.alertapp;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.dnd.DropTargetEvent;

import net.refractions.udig.internal.ui.UDIGDropHandler;
import net.refractions.udig.ui.IDropAction;

public class RestrictedDropHandler extends UDIGDropHandler {
	@Override
	protected List<IDropAction> filterActions(DropTargetEvent event,
			List<IDropAction> actions) {
		for(Iterator<IDropAction> iter = actions.iterator(); iter.hasNext();) {
			if(!(iter.next() instanceof DropImageAction)) {
				iter.remove();
			}
		}
		return actions;
	}
}
