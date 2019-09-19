package org.locationtech.udig.core;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.core.runtime.IAdapterFactory;

public class NumberAdaptableFactory implements IAdapterFactory {
	
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {

		if (adaptableObject == null) {
			return null;
		}
		
		if (adaptableObject instanceof Number) {
			if (adapterType==Long.class) {
				//System.out.println("Adapting " + adaptableObject.getClass()  + " to Long. Rounding or truncation may occur");
				return ((Number)adaptableObject).longValue();
			} else if (adapterType==Integer.class) {
				//System.out.println("Adapting " + adaptableObject.getClass()  + " to Integer. Rounding or truncation may occur");
				return ((Number)adaptableObject).intValue();
			} else if (adapterType==Double.class) {
				//System.out.println("Adapting " + adaptableObject.getClass()  + " to Double. Rounding may occur");
				return ((Number)adaptableObject).doubleValue();
			} else if (adapterType==Short.class) {
				//System.out.println("Adapting " + adaptableObject.getClass()  + " to Short. Rounding or truncation may occur");
				return ((Number)adaptableObject).shortValue();
			} else if (adapterType==Float.class) {
				//System.out.println("Adapting " + adaptableObject.getClass()  + " to Long. Rounding may occur");
				return ((Number)adaptableObject).floatValue();
			} else if (adapterType==BigDecimal.class) {
				//System.out.println("Adapting " + adaptableObject.getClass()  + " to BigDecimal");
				return new BigDecimal(adaptableObject.toString());
			} else if (adapterType==BigInteger.class) {
				//System.out.println("Adapting " + adaptableObject.getClass()  + " to BigInteger. Rounding may occur");
				return new BigDecimal(adaptableObject.toString()).toBigInteger();
			}
		}
		
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[]{Number.class};
	}

}
