/**
 * 
 */
package org.locationtech.udig.core;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * An implementation of a Provider that provides the reference it was constructed with.
 * 
 * <p>
 * Example:
 * </p><p>
 * Provider<Integer> p=new StaticProvider<Integer>(integerInstance);
 * </p>
 * 
 * @author jones
 * @since 1.1.0
 */
public class StaticBlockingProvider<T> implements IBlockingProvider<T> {

	T object;
	
	public StaticBlockingProvider( T objectToProvide){
		this.object=objectToProvide;
	}

	public T get(IProgressMonitor monitor, Object... params) throws IOException {
		return object;
	}

}
