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
package net.refractions.udig.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

/**
 * Essentially a protocol for handling messages from the MapDaemon.
 * 
 * @author jesse
 */
public interface MessageHandler {

	/**
	 * If method returns true then this handler understands the message type and execute
	 * will be called.
	 * 
	 * @param header the first line of the message.
	 * 
	 * @return true if this handler can handle message
	 */
	boolean canHandleMessge(String header);

	/**
	 * This must handle the request from the requestor.  The reader, inputStrean and 
	 * socket will all be closed after the method is called.  It is recommended to close
	 * any other streams opened during execution. 
	 * 
	 * @param reader  a BufferedReader that wraps the socket's inputStream
	 * @param socket the socket.
	 * @throws IOException 
	 */
	void execute(BufferedReader reader, Socket socket) throws Exception;
	
}
