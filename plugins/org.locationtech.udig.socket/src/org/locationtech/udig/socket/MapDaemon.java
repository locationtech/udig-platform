/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * This thread uses a ServerSocket to listen on a port (default 14921). When it
 * get a message it takes the first line and passes it to the different
 * MessageHandlers to see which one can handle the message. The first handler
 * that accepts it handles the message. If an error occurs then a Message
 * starting with "FAILURE" will be returned.
 * 
 * @author jesse
 */
public class MapDaemon extends Thread {

	// TODO This is a modification it will use the pluging system to
	// look up handlers in the future
	private final static List<MessageHandler> handlers = new ArrayList<MessageHandler>();
	static {
		handlers.add(new DisplayLayerHandler());
	}

	{
		setDaemon(true);
	}

	@Override
	public void run() {
		final int port = Activator.getDefault().getPreferenceStore().getInt(
				SocketPreferenceConstants.MAP_DAEMON_PORT);
		try {
				ServerSocket server = createSocket(port);
				while (true) {
					Socket socket = server.accept();

					handle(socket);
				}
		} catch (IOException e) {
			Activator.log("Communication error", e); //$NON-NLS-1$
		}

	}

	private ServerSocket createSocket(final int port) throws IOException {
		ServerSocket server=null;
		int bindPort = port;
		while(server == null ){
			try {
				server = new ServerSocket(bindPort);
			} catch (BindException e) {
				bindPort++;
			}
		}
		if( bindPort!=port){
			final int finalPort = bindPort;
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog
							.openError(Display.getDefault()
									.getActiveShell(),
									"Socket Port Change",
									"uDig is unable to open the server on port:" + port +" (the default port.)  \nInstead the server has been started on port "+finalPort);
				}
			});
		}
		return server;
	}

	private void handle(Socket socket) throws IOException {
		InputStream inputStream = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		String header = reader.readLine();

		boolean handled = false;
		for (MessageHandler handler : handlers) {
			try {
				if (handler.canHandleMessge(header)) {
					handled = true;
					run(handler, reader, socket);
				}
			} catch (Throwable e) {
				Activator
						.log(
								"Error with handler: " + handler + " in the canHandleMessage method", e); //$NON-NLS-1$//$NON-NLS-2$
			}
		}

		if (!handled) {
			String message = "FAILURE:" + "  No handler for message"; //$NON-NLS-1$ //$NON-NLS-2$
			OutputStream outputStream = socket.getOutputStream();
			try {
				outputStream.write(message.getBytes());
			} finally {
				reader.close();
				socket.close();
				outputStream.close();
			}
		}
	}

	private void run(final MessageHandler handler, final BufferedReader reader,
			final Socket socket) {
		PlatformGIS.run(new ISafeRunnable() {

			public void handleException(Throwable exception) {
				Activator.log(handler.getClass().getName()
						+ " failed in handling communications", //$NON-NLS-1$
						exception);
			}

			public void run() throws Exception {
				try {
					handler.execute(reader, socket);
				} finally {
					reader.close();
					if (!socket.isClosed()) {
						socket.close();
					}
				}
			}

		});
	}

	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("localhost", 8921); //$NON-NLS-1$
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out, true);

		writer.println(DisplayLayerHandler.HEADER);
		writer
				.println(DisplayLayerHandler.RESOURCE_ID
						+ "file:///Users/jesse/dev/Data/uDigData/nyct2000.shp#nyct2000"); //$NON-NLS-1$
		writer
				.println("url;URL;file:///Users/jesse/dev/Data/uDigData/nyct2000.shp"); //$NON-NLS-1$

		socket.getOutputStream().write(out.toString().getBytes());
	}
}
