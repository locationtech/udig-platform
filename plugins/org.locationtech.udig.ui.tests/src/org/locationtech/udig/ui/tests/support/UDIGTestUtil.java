/**
 * 
 */
package org.locationtech.udig.ui.tests.support;

import org.eclipse.swt.widgets.Display;
import org.locationtech.udig.ui.WaitCondition;

/**
 * Utility class with a number of static convenience methods
 * 
 * @author jeichar
 */
@SuppressWarnings("nls")
public final class UDIGTestUtil {

	/**
	 * The method will throw an exception if the current thread is not in the
	 * display thread.
	 * 
	 * @param maxWaitInMilliSeconds
	 *            The maximum time to wait in Milliseconds
	 * @param condition
	 *            the condition that will cause the method to return early.
     * @param throwExceptionOnTimeout if true an exception will be thrown if a 
     *                                  timeout occurs before condition is true. 
	 * @throws Exception 
	 * @throws Exception
	 */
	public static void inDisplayThreadWait(long maxWaitInMilliSeconds,
			WaitCondition condition, boolean throwExceptionOnTimeout) throws Exception {
		long startTime = System.currentTimeMillis();
        long lastTime=System.currentTimeMillis();
		while (startTime + maxWaitInMilliSeconds > lastTime
				&& !condition.isTrue()) {
            Thread.yield();
			if (!Display.getCurrent().readAndDispatch()){
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					//don't worry about it
				}
            }
                
            lastTime=System.currentTimeMillis();
		}
		while(Display.getCurrent().readAndDispatch());
		if (throwExceptionOnTimeout && !condition.isTrue() && startTime + maxWaitInMilliSeconds < lastTime){
            throw new IllegalStateException("condition is false!  Condition that failed is:"+ condition);  //$NON-NLS-1$
            }
	}

}
