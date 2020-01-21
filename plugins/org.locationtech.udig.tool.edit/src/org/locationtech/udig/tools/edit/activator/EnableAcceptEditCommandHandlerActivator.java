/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.internal.render.displayAdapter.impl.ViewportPaneSWT;
import org.locationtech.udig.tools.edit.Activator;
import org.locationtech.udig.tools.edit.BehaviourCommand;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditToolHandler;

/**
 * Enables the
 * {@link org.locationtech.udig.tools.edit.handler.SnapBehaviourCommandHandler}
 * on the CycleSnapBehaviour command.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class EnableAcceptEditCommandHandlerActivator implements Activator {
	/**
	 * Runs the tool's accept behaviours when the command comes in.
	 * 
	 * @author Jesse
	 * @since 1.1.0
	 */
	private static final class AcceptEditCommandHandler extends AbstractHandler {

		EditToolHandler handler;

		public Object execute(ExecutionEvent arg0) throws ExecutionException {
			if (handler != null && ((Event) arg0.getTrigger()).widget instanceof ViewportPaneSWT && ((Event) arg0.getTrigger()).type == SWT.KeyDown) {
				BehaviourCommand acceptCommands = handler.getCommand(handler
						.getAcceptBehaviours());
				handler.getContext().sendASyncCommand(acceptCommands);
			}
			return null;
		}
	}

	private class MapPartListener implements IPartListener, IPageListener{
		Command command;
		IWorkbenchPage page;

		public void partActivated(IWorkbenchPart part) {
			if (command != null) {
				if( part instanceof MapPart ){
					command.setHandler(commandHandler);
				}else{
					command.setHandler(null);					
				}
			}
		}

		public void partBroughtToTop(IWorkbenchPart part) {

		}

		public void partClosed(IWorkbenchPart part) {

		}

		public void partDeactivated(IWorkbenchPart part) {
		}

		public void partOpened(IWorkbenchPart part) {

		}

		public void disable() {
			partActivated(null);
			mapPartListener.page.removePartListener(mapPartListener);
		}

		public void activate() {
			page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			page.addPartListener(this);
			IWorkbenchPart activePart = page.getActivePart();
			partActivated(activePart);
		}

		public void pageActivated(final IWorkbenchPage page) {
			if( page!=this.page){
				partDeactivated(null);
				this.page = page;
				partActivated(page.getActivePart());
			}
		}

		public void pageClosed(IWorkbenchPage page) {
		}

		public void pageOpened(IWorkbenchPage page) {
		}

	}

	private static final String COMMAND_ID = "org.locationtech.udig.tool.edit.acceptAction"; //$NON-NLS-1$
	private final ICommandService service = (ICommandService) PlatformUI
			.getWorkbench().getAdapter(ICommandService.class);
	private final AcceptEditCommandHandler commandHandler = new AcceptEditCommandHandler();
	private final MapPartListener mapPartListener = new MapPartListener();

	public void activate(EditToolHandler handler) {
		commandHandler.handler = handler;
		Command command = service.getCommand(COMMAND_ID);
		mapPartListener.command = command;
		mapPartListener.activate();
	}

	public void deactivate(EditToolHandler handler) {
		mapPartListener.disable();
		commandHandler.handler = null;
		mapPartListener.command = null;
	}

	public void handleActivateError(EditToolHandler handler, Throwable error) {
		EditPlugin.log("", error); //$NON-NLS-1$
	}

	public void handleDeactivateError(EditToolHandler handler, Throwable error) {
		EditPlugin.log("", error); //$NON-NLS-1$
	}

}
