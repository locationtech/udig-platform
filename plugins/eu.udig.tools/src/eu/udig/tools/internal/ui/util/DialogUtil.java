/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 * 		Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 * 		http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to licence under Lesser General Public License (LGPL).
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package eu.udig.tools.internal.ui.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import eu.udig.tools.internal.mediator.PlatformGISMediator;
import eu.udig.tools.internal.i18n.Messages;

/**
 * Constructs common dialog
 * <p>
 * 
 * </p>
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.1.0
 * 
 */
public final class DialogUtil {

	private DialogUtil() {
		// util class
	}

	/**
	 * Opens an error dialog in a standardized way
	 * 
	 * @param title
	 *            message dialog title
	 * @param message
	 *            message dialog content
	 */
	public static void openError(final String title, final String message) {

		PlatformGISMediator.syncInDisplayThread(new Runnable() {
			@Override
            public void run() {
				MessageDialog.openError(null, title, message);
			}
		});
	}

	/**
	 * Opens an information dialog in a standardized way
	 * 
	 * @param title
	 *            message dialog title
	 * @param message
	 *            message dialog content
	 */
	public static void openInformation(final String title, final String message) {
		PlatformGISMediator.syncInDisplayThread(new Runnable() {
			@Override
            public void run() {
				MessageDialog.openInformation(null, title, message);
			}
		});
	}

	/**
	 * Opens an warning dialog in a standardized way
	 * 
	 * @param title
	 *            message dialog title
	 * @param message
	 *            message dialog content
	 */
	public static void openWarning(final String title, final String message) {
		PlatformGISMediator.syncInDisplayThread(new Runnable() {
			@Override
            public void run() {
				MessageDialog.openWarning(null, title, message);
			}
		});
	}

	/**
	 * Opens an question dialog in a standardized way
	 * 
	 * @param title
	 *            message dialog title
	 * @param message
	 *            message dialog content
	 * @return wether the question was accepted or not
	 */
	public static boolean openQuestion(final String title, final String message) {

		final boolean[] confirm = { false };
		PlatformGISMediator.syncInDisplayThread(new Runnable() {
			@Override
            public void run() {
				confirm[0] = MessageDialog.openQuestion(null, title, message);
			}
		});
		return confirm[0];
	}

	/**
	 * Opens an confirmation dialog in a standardized way
	 * 
	 * @param title
	 *            message dialog title
	 * @param message
	 *            message dialog content
	 * @return wether the question was confirmed or not
	 */
	public static boolean openConfirm(final String title, final String message) {
		final boolean[] confirm = { false };
		PlatformGISMediator.syncInDisplayThread(new Runnable() {
			@Override
            public void run() {
				confirm[0] = MessageDialog.openConfirm(null, title, message);
			}
		});
		return confirm[0];
	}

	/**
	 * Runs a blocking task in a ProgressDialog. It is ran in such a way that
	 * even if the task blocks it can be cancelled. This is unlike the normal
	 * ProgressDialog.run(...) method which requires that the
	 * {@link IProgressMonitor} be checked and the task to "nicely" cancel.
	 * 
	 * @param dialogTitle
	 *            The title of the Progress dialog
	 * @param showRunInBackground
	 *            if true a button added to the dialog that will make the job be
	 *            ran in the background.
	 * @param process
	 *            the task to execute.
	 * @param runASync
	 * @param confirmCancelRequests
	 *            wether to ask the user to confirm the cancelation when the
	 *            cancel button is pressed
	 */
	public static void runInProgressDialog(	final String dialogTitle,
											final boolean showRunInBackground,
											final IRunnableWithProgress process,
											boolean runASync,
											final boolean confirmCancelRequests) {
		Runnable object = new Runnable() {

			@Override
            public void run() {

				Shell shell = Display.getDefault().getActiveShell();

				ProgressMonitorDialog dialog = DialogUtil.openProgressMonitorDialog(shell, dialogTitle,
							showRunInBackground, confirmCancelRequests);

				try {

					dialog.run(true, true, new IRunnableWithProgress() {

						@Override
                        public void run(IProgressMonitor monitor)
							throws InvocationTargetException, InterruptedException {
							try {
								PlatformGISMediator.runBlockingOperation(new IRunnableWithProgress() {

									@Override
                                    public void run(IProgressMonitor monitor)
										throws InvocationTargetException, InterruptedException {
										process.run(monitor);
									}
								}, monitor);

							} catch (InvocationTargetException e) {
								throw e;
							} catch (InterruptedException e) {
								throw e;

							} catch (Exception e) {
								// TODO feedback to user is required
								e.printStackTrace();
							}

						}
					});
				} catch (Exception e) {
					// TODO feedback to user is required
					e.printStackTrace();
				}
			}
		};

		if (runASync)
			Display.getDefault().asyncExec(object);
		// TODO should be tested with this method
		// PlatformGISMediator.asyncInDisplayThread(object, false);
		else
			PlatformGISMediator.syncInDisplayThread(object);
	}

	public static void runsyncInDisplayThread(	final String dialogTitle,
												final boolean showRunInBackground,
												final IRunnableWithProgress process,
												final boolean confirmCancelRequests) {
		Runnable object = new Runnable() {

			@Override
            public void run() {

				Shell shell = Display.getDefault().getActiveShell();

				ProgressMonitorDialog dialog = DialogUtil.openProgressMonitorDialog(shell, dialogTitle,
							showRunInBackground, confirmCancelRequests);

				try {

					dialog.run(true, true, new IRunnableWithProgress() {

						@Override
                        public void run(final IProgressMonitor monitor)
							throws InvocationTargetException, InterruptedException {
							try {
								PlatformGISMediator.syncInDisplayThread(new Runnable() {

									@Override
                                    public void run() {
										try {
											process.run(monitor);
										} catch (InvocationTargetException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								});

							} catch (Exception e) {
								// TODO feedback to user is required
								e.printStackTrace();
							}

						}
					});
				} catch (Exception e) {
					// TODO feedback to user is required
					e.printStackTrace();
				}
			}
		};

		// if (runASync)
		// Display.getDefault().asyncExec(object);
		// TODO should be tested with this method
		// PlatformGISMediator.asyncInDisplayThread(object, false);
		// else
		PlatformGISMediator.syncInDisplayThread(object);
	}

	public static ProgressMonitorDialog openProgressMonitorDialog(	final Shell shell,
																	final String dialogTitle,
																	final boolean showRunInBackground,
																	final boolean confirmCancelRequests) {

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell) {
			@Override
			protected void cancelPressed() {
				boolean confirmed = true;
				if (confirmCancelRequests) {
					String title = Messages.DialogUtil_title;
					String message = Messages.DialogUtil_message;
					confirmed = DialogUtil.openQuestion(title, message);
				}
				if (confirmed) {
					super.cancelPressed();
				}
			}

			@Override
			protected void configureShell(Shell shell) {
				super.configureShell(shell);
				shell.setText(dialogTitle);
			}

			@Override
			protected void createButtonsForButtonBar(Composite parent) {
				if (showRunInBackground)
					createBackgroundButton(parent);
				super.createButtonsForButtonBar(parent);
			}

			private void createBackgroundButton(Composite parent) {
				createButton(parent, IDialogConstants.BACK_ID, Messages.DialogUtil_runInBackground, true);
			}

			@Override
			protected void buttonPressed(int buttonId) {
				if (buttonId == IDialogConstants.BACK_ID) {
					getShell().setVisible(false);
				} else
					super.buttonPressed(buttonId);
			}
		};

		return dialog;
	}

}
