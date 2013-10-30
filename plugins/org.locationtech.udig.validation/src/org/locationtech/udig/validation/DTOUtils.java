/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.validation;

import java.util.Iterator;
import java.util.Map;

import org.locationtech.udig.validation.internal.Messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.geotools.validation.dto.ArgumentDTO;
import org.geotools.validation.dto.TestDTO;
import org.geotools.validation.dto.TestSuiteDTO;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author chorner
 * @since 1.0.1
 */
public class DTOUtils {
    /**
     * Checks an individual test to determine if all of the arguments are non-null.
     *
     * @param test
     * @return
     */
    public static boolean noNullArguments(TestDTO test) {
    	Map args = test.getArgs();
    	for (Iterator i = args.keySet().iterator(); i.hasNext();) {
    		ArgumentDTO arg = (ArgumentDTO) args.get(i.next());
    		if (arg.getValue() == null) return false;
    	}
    	return true;
    }

    /**
	 * Ensures that each test in the testSuite does not contain any null
	 * arguments. Tests that contain "errors" are selected in red and a dialog
	 * complains to the user.
	 * 
	 * @param testSuite
	 * @return
	 */
    public static boolean noNullArguments(TestSuiteDTO testSuite) {
    	Map tests = testSuite.getTests();
    	boolean badTests = false;
		for (Iterator i = tests.keySet().iterator(); i.hasNext();) {
            TestDTO currentTest = (TestDTO) tests.get(i.next());
            if (!noNullArguments(currentTest)) {
            	//the test contains a null argument!
            	badTests = true;
            	break;
            }
		}
		if (badTests) {
			//yell at the user
	        MessageBox mb = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR  | SWT.OK);
	        mb.setMessage(Messages.DTOUtils_nullArg); 
	        mb.open();
			return false;
		}
		return true;
    }

}
