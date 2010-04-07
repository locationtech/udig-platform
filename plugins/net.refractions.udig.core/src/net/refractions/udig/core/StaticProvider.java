/**
 * 
 */
package net.refractions.udig.core;


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
public class StaticProvider<T> implements IProvider<T> {

	T object;
	
	public StaticProvider( T objectToProvide){
		this.object=objectToProvide;
	}

	public T get(Object... params) {
		return object;
	}

}
