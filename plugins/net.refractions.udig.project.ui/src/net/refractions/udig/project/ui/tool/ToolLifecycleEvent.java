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
