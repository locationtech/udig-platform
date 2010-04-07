/**
 * 
 */
package net.refractions.udig.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.internal.ui.TransferStrategy;
import net.refractions.udig.internal.ui.UDIGTransfer;
import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * This class provides the framework for a transfer to have different behaviours depending on the 
 * strategy that is set.
 * <p> 
 * For example:
 * </p><p>
 * A GeometryTextTransfer can encode the geometry as a GML string or a WKT string (as created by JTS WKTWriter).
 * The GeometryTextTransfer class subclasses AbstractStrategizedTransfer and only needs to provide the current
 * strategy and the set of possible strategies.  The AbstractStrategizedTransfer will first try to use the current strategy for 
 * encoding the geometry then all the rest if the current strategy fails.  
 * Similarly for decoding an input(nativeToJava) the current strategy will be tried first then the rest of the
 * known strategies will be used until an object is obtained.
 * </p>
 * 
 * @see net.refractions.udig.internal.ui.TransferStrategy
 * @author jeichar
 */
public abstract class AbstractStrategizedTransfer extends ByteArrayTransfer implements UDIGTransfer{

	final Set<TransferStrategy> knownStrategies=Collections.synchronizedSet(new HashSet<TransferStrategy>());
	public AbstractStrategizedTransfer(){
		knownStrategies.addAll(Arrays.asList(getAllStrategies()));
	}
	
	@Override
	public void javaToNative(Object object, TransferData transferData) {
		try{
			getCurrentStrategy().javaToNative(object, transferData);
			return;
		}catch(Throwable e){
			UiPlugin.log("Error encoding "+object, e); //$NON-NLS-1$
		}
		for (TransferStrategy strategy : knownStrategies) {			
			if( strategy==getCurrentStrategy())
				continue;
			try{
				strategy.javaToNative(object, transferData);
				return;
			}catch(Throwable e){
				UiPlugin.log("Error encoding "+object, e); //$NON-NLS-1$
				continue;
			}
		}
		throw new RuntimeException("No strategies were capable of encoding "+object); //$NON-NLS-1$
	}
	
	@Override
	public Object nativeToJava(TransferData transferData) {
		try{
			return getCurrentStrategy().nativeToJava(transferData);
		}catch(Throwable e){
            UiPlugin.trace( getClass(), "Error decoding transferData", e); //$NON-NLS-1$
		}
		for (TransferStrategy strategy : knownStrategies) {			
			if( strategy==getCurrentStrategy())
				continue;
			try{
				Object value = strategy.nativeToJava(transferData);
				if( value!=null )
					return value;
			}catch(Throwable e){
				UiPlugin.log("Error decoding transferData", e); //$NON-NLS-1$
				continue;
			}
		}
		throw new RuntimeException("No strategies were capable of decoding transfer data"); //$NON-NLS-1$
	}
    
    /**
     * Returns the current strategy as indicated by the pereferences
     *
     * @return the current strategy as indicated by the pereferences
     */
	public TransferStrategy getCurrentStrategy() {
        String indicator=UiPlugin.getDefault().getPreferenceStore().getString(getClass().getName());
        if( indicator==null || indicator.length()==0 )
            return getDefaultStrategy();
        try{
            return getAllStrategies()[Integer.valueOf(indicator)];
        }catch (Exception e) {
            return getDefaultStrategy();
        }
    }

    /**
	 * Returns the default strategy that is used to encode the java objects.
	 * 
	 * @return the strategy to use for encoding the java object.
	 */
	public abstract TransferStrategy getDefaultStrategy();
	/**
	 * This method is only called once during construction to get the list of strategies known by
	 * the implementation.  
	 * 
	 * @return
	 */
	public abstract TransferStrategy[] getAllStrategies();
    /**
     * Returns the names for the strategies returned by {@link #getAllStrategies()}.  The ith name
     * must correspond to the ith strategy.
     *
     * @return the names for the strategies returned by {@link #getAllStrategies()}
     */
    public abstract String[] getStrategyNames();
    /**
     * Returns true if the transfer can transfer to and from the object.
     *
     * @return true if the transfer can transfer to and from the object.
     */
    public abstract boolean validate(Object object);

	/**
	 * Adds a new strategy the list of known strategies.
	 */
	public void addStrategy( TransferStrategy newStrategy ){
		knownStrategies.add(newStrategy);
	}

    /**
     * Returns a name for the Transfer.
     *
     * @return name for the Transfer
     */
    public abstract String getTransferName();
}
