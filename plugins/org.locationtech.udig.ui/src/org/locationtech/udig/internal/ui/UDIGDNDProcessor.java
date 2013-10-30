/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.internal.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;
import net.refractions.udig.ui.IDropAction;
import net.refractions.udig.ui.TransferFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;

/**
 *  Processes drag and drop extensions.
 *  <p>
 *  The following extension points are processed:
 *  <ul>
 *  <li>net.refractions.udig.ui.dropTransfers</li>
 *  </ul>
 * @author jones
 * @since 1.0.0
 */
public class UDIGDNDProcessor {



    private static TransferProcessor processor;

    /**
     * Gets the transfers that are available in the current uDig configurations.
     * The transfers are order with the "known" UDIG transfers(UDIGByteAndLocalTransfer) as the first
     * elements in the set, then the defined transfers and finally the "known"
     * eclipse transfers(TextTransfer, FileTransfer, etc..)
     *
     * @return the transfers that are available in the current uDig configurations.
     */
	public static Set<Transfer> getTransfers() {
		if (processor == null) {
			processor = new TransferProcessor();
			ExtensionPointUtil.process(UiPlugin.getDefault(),
					UiPlugin.DROP_TRANSFERS_ID, processor);
		}
		return new LinkedHashSet<Transfer>(processor.transfers);
	}

	static class TransferProcessor implements ExtensionPointProcessor {

		TreeSet<Transfer> transfers = new TreeSet<Transfer>(new TransferComparator());

		public void process(IExtension extension, IConfigurationElement element)
				throws Exception {
			Transfer[] tmp = ((TransferFactory) element.createExecutableExtension("class")).getTransfers(); //$NON-NLS-1$
			for (Transfer transfer : tmp) {
				transfers.add(transfer);  				
			}
		}
	}
    static class TransferComparator implements Comparator<Transfer>, Serializable{

        /** long serialVersionUID field */
        private static final long serialVersionUID = 1L;

        public int compare( Transfer o1, Transfer o2 ) {
            if( o1 instanceof UDigByteAndLocalTransfer)
                return -1;
            if( o2 instanceof UDigByteAndLocalTransfer)
                return 1;
            if( o1 instanceof UDIGTransfer)
                return -1;
            if( o2 instanceof UDIGTransfer)
                return 1;
            
            return -1;
        }

    }
    public static class DropActionProcessor implements ExtensionPointProcessor {
    
    	Object data;
    	UDIGDropHandler handler;
    	List<IDropAction> actions;
        private DropTargetEvent event;
    	
    	DropActionProcessor(Object data, UDIGDropHandler handler, DropTargetEvent event) {
    		this.data = data;
    		this.handler = handler;
            this.event=event;
    		actions = new ArrayList<IDropAction>();
    	}
    	
    	public void process(IExtension extension, IConfigurationElement element) 
    		throws Exception {
    	    EnablesFor enablesFor=new EnablesFor(element);
    	    if( enablesFor.minimum<1 && !enablesFor.expandable )
                return;
            
            //first find a matching target
    		Object concreteTarget=findTarget(element);

    		if (concreteTarget == null) 
    			return;
            
            //next find a matching type
            List<Object> concreteData = findData(element);
            if (!concreteData.isEmpty()) {
                try {
                    addActions(element, concreteTarget, concreteData, enablesFor);
                } catch(Throwable t) {
                    String msg = "Error validating drop action"; //$NON-NLS-1$
                    String ns = element.getNamespaceIdentifier();
                    
                    Status s = new Status(IStatus.WARNING,ns,0,msg,t);
                    UiPlugin.getDefault().getLog().log(s);
                }
            }

    	}

        private void addActions( IConfigurationElement element, Object concreteTarget, List<Object> concreteData, EnablesFor enablesFor ) throws CoreException {
            if( enablesFor.minimum>concreteData.size() )
                return;
            if( enablesFor.minimum==concreteData.size() ){
                addAction(element, concreteTarget, concreteData);
                return;
            }
            
            if( enablesFor.expandable && enablesFor.minimum<concreteData.size() ){
                addAction(element, concreteTarget, concreteData);
                return;
            }
            // if there is a set size then make a bunch of actions with that many items.
            if( !enablesFor.expandable ){
                List<Object> data=new ArrayList<Object>();
                for( Object object : concreteData ) {
                    if( data.size()<enablesFor.minimum ){
                        data.add(object);
                    }else{
                        addAction(element, concreteTarget, data);
                        // reset the list just in case the action stores a reference to the array
                        data=new ArrayList<Object>();
                        data.add(object);
                    }
                }
                
                if( data.size()==enablesFor.minimum ){
                    addAction(element, concreteTarget, data);
                }
            }
        }
        /** Processes the configuration element; checking if the provided IDropAction can accept the provided data (if so it is added to actions
         * for later).
         * @param element
         * @param concreteTarget
         * @param concreteData
         * @throws CoreException
         */
        private void addAction( IConfigurationElement element, Object concreteTarget, List<Object> concreteData ) throws CoreException {
            if( concreteData.isEmpty() )
                throw new IllegalArgumentException("Data cannot be null"); //$NON-NLS-1$
            Object data;
            if( concreteData.size()==1 ){
                data=concreteData.get(0);
            }else{
                data=concreteData.toArray();
            }
            IDropAction action = (IDropAction)element.createExecutableExtension("class"); //$NON-NLS-1$
            action.init(element, event, handler.getViewerLocation(), concreteTarget, data);
            if (action.accept()) {
                actions.add(action);
            }
        }

        private List<Object> findData( IConfigurationElement element ) {
            IConfigurationElement[] acceptedTypes = element.getChildren("acceptedType"); //$NON-NLS-1$
            List<Object> data=new ArrayList<Object>(Arrays.asList((Object[])this.data));
            
            Class<? extends Object> c = null;
    		final List<Object> concreteData = new ArrayList<Object>();
            

            for (int i = 0; i < acceptedTypes.length && !data.isEmpty(); i++) {
    			IConfigurationElement acceptedType = acceptedTypes[i];
    
    			try {
    
    				String clazz = acceptedType.getAttribute("class"); //$NON-NLS-1$
                    
    				c = loadClass(clazz,true);
    				
    				if (c == null)
    					continue;

                    String adapt=acceptedType.getAttribute("adapt"); //$NON-NLS-1$
                    boolean doAdapt = "true".equals(adapt); //$NON-NLS-1$
                    
                    concreteData.addAll(processArray(data, c, doAdapt));
    			}
    			catch(ClassNotFoundException e) {
    				//expected, do nothing
    				continue;
    			}
    		}
            
            return concreteData;
        }

        private Class< ? extends Object> loadClass( String clazz, boolean isArray ) throws ClassNotFoundException {
            if( isArray ){
                Object[] array = (Object[])data;
                for( Object object : array ) {
                    Class< ? extends Object> c;
                    c = getClassLoader(object).loadClass(clazz);
                    if( c!=null )
                        return c;
                }
                return null;
            }else{
                return getClassLoader(data).loadClass(clazz);
            }
        }

        private List<Object> processArray( List<Object> data, Class< ? extends Object> c, boolean doAdapt ) {
               
            List<Object> tmp=new ArrayList<Object>(data.size());
            for( Object obj:data) {
                Object d=getConcreteObject(obj, c, doAdapt);
                
                if( d!=null ){
                    tmp.add(d);
                }
            }
            
            data.removeAll(tmp);
            return tmp;
        }
        /**
         * Check out the provided configuration element (destination, possible targets) and
         * figure out a target object.
         *
         * @param element
         * @return
         */
        private Object findTarget(IConfigurationElement element) {
            IConfigurationElement[] targets = element.getChildren("destination"); //$NON-NLS-1$
            Object concreteTarget = null; 
            ClassLoader dloader = getClassLoader(handler.getTarget());
            Class<? extends Object> c = null;
            
            for (int i = 0; i < targets.length && c == null; i++) {
                IConfigurationElement target = targets[i];
                
                try {
                    String clazz = target.getAttribute("class"); //$NON-NLS-1$
                    c = dloader.loadClass(clazz);
                    
                    if (c == null)
                        continue;
                    String adapt=target.getAttribute("adapt"); //$NON-NLS-1$
                    
                    concreteTarget = getConcreteObject(handler.getTarget(), c , "true".equals(adapt)); //$NON-NLS-1$
                    if (concreteTarget==null){
                        c = null;
                    }
                }
                catch(ClassNotFoundException e) {
                    //expected, do nothing
                    continue;
                }
            }
    
            return concreteTarget;
        }

        private ClassLoader getClassLoader( Object data ) {
            ClassLoader sloader = data.getClass().getClassLoader();
    		if (sloader == null) {
    			//probably boot class loader
    			sloader = ClassLoader.getSystemClassLoader();
    		}
            return sloader;
        }

        private Object getConcreteObject( Object obj, Class< ? extends Object> desiredClass, boolean adapt ) {
 
            if( desiredClass.isAssignableFrom(obj.getClass()) )
                return obj;
            if( !adapt )
                return null;
            if (obj instanceof IAdaptable) {
                IAdaptable adaptable = (IAdaptable) obj;
                Object adapter = adaptable.getAdapter(desiredClass);
                if( adapter!=null )
                    return adapter;
            }
            return null;
        }
    }
    public static List<IDropAction> process(Object data, UDIGDropHandler handler, DropTargetEvent event) {
    	if (data == null || handler == null || handler.getTarget() == null)
    		return new ArrayList<IDropAction>();
    	
    	//process to see if anyone cares
    	DropActionProcessor d = new DropActionProcessor(data, handler, event);
    	ExtensionPointUtil.process(UiPlugin.getDefault(), IDropAction.XPID, d);
    	
    	return d.actions;
    }
    
    private static class EnablesFor{

        int minimum;
        boolean expandable;
        
        public EnablesFor( IConfigurationElement element ) {
            String enablesFor=element.getAttribute("enablesFor"); //$NON-NLS-1$
            minimum=1;
            expandable=false; 
            if( enablesFor!=null ){
                if( enablesFor.contains("+") ){ //$NON-NLS-1$
                    expandable=true;
                    enablesFor=enablesFor.substring(0, enablesFor.indexOf('+') ).trim();
                }
                if( enablesFor.trim().length()>0 ){
                    try{
                        minimum=Integer.valueOf(enablesFor);
                    }catch (NumberFormatException e) {
                        throw new NumberFormatException("enablesFor in DropAction: " + //$NON-NLS-1$
                                element.getName() + " is not a number or a +.  " + //$NON-NLS-1$
                                "See extension point for legal values."); //$NON-NLS-1$
                    }
                }
            }

        }
    }
}
