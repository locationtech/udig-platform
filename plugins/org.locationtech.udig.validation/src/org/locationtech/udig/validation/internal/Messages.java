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
package org.locationtech.udig.validation.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.locationtech.udig.validation.internal.messages"; //$NON-NLS-1$
	public static String ValidationTableLabelProvider_invalidColumn;
	public static String OpUtils_results;
	public static String OpUtils_notifyResult;
	public static String GenericValidationResults_validationWarning;
	public static String GenericValidationResults_validationError;
	public static String DTOUtils_nullArg;
	public static String ValidationDialog_populating;
	public static String ValidationDialog_validating;
	public static String ValidationDialog_ellipsis;
	public static String ValidationDialog_nonUniqueTest;
	public static String ValidationDialog_fileExists;
	public static String ValidationDialog_noSuiteSuf;
	public static String ValidationDialog_noSuitePre;
	public static String ValidationDialog_fileNotFound;
	public static String ValidationDialog_fileNotExist;
	public static String ValidationDialog_filesAll;
	public static String ValidationDialog_filesXML;
	public static String ValidationDialog_title;
	public static String ValidationDialog_description;
	public static String ValidationDialog_name;
	public static String ValidationDialog_value;
	public static String ValidationDialog_argument;
	public static String ValidationDialog_runAll;
	public static String ValidationDialog_run;
	public static String ValidationDialog_export;
	public static String ValidationDialog_import;
	public static String ValidationDialog_delete;
	public static String ValidationDialog_new;
	public static String ValidationDialog_validations;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
