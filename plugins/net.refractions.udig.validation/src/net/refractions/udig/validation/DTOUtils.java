/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.validation;

import java.util.Iterator;
import java.util.Map;

import net.refractions.udig.validation.internal.Messages;

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
