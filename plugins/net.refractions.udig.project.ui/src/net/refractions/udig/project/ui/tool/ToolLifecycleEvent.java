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
package net.refractions.udig.project.ui.tool;

import net.refractions.udig.project.UDIGEvent;

/**
 * 
 * Event of tool lifecycle changing, such as:
 * <p>
 * <ul>
 * <li>setActive(boolean) is called</li>
 * <li>setEnabled(boolean) is called</li>
 * <li>setContext(IToolContext) - the new context is set</li>
 * </ul>
 * 
 * 
 * @author Vitalus
 * @since UDIG 1.1
 *
 */
public class ToolLifecycleEvent extends UDIGEvent {
	
	public static enum Type{
		
		
		ACTIVE,
		
		
		ENABLE,
		
		
		TOOL_CONTEXT
		
		
	}
	
	private Type type;

	/**
	 * 
	 * @param source2
	 * @param type
	 * @param newValue2
	 * @param oldValue2
	 */
	public ToolLifecycleEvent(Object source2, Type type, Object newValue2, Object oldValue2) {
		super(source2, newValue2, oldValue2);
		this.type = type;
	}

	/**
	 * 
	 * @return
	 */
	public ToolLifecycleEvent.Type getType(){
		return type;
	}
	
	@Override
	public Object getSource() {
		return source;
	}

}
