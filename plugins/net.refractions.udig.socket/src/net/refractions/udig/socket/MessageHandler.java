/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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
