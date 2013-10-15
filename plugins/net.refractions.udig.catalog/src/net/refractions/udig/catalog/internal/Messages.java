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
package net.refractions.udig.catalog.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.catalog.internal.messages"; //$NON-NLS-1$
	public static String CatalogImpl_monitorTask2;
	public static String CatalogImpl_monitorTask;
	public static String CatalogImpl_resolving;
	public static String CatalogImpl_finding;
	public static String CatalogImpl_localCatalog_title;
    public static String CatalogPlugin__ErrorLoadingMessage;
    public static String CatalogPlugin_ErrorLoading;
    public static String CatalogPlugin_SavingCatalog;
    public static String DataStoreServiceExtension_butWas;
    public static String DataStoreServiceExtension_missingKey;
    public static String DataStoreServiceExtension_nullParam;
    public static String DataStoreServiceExtension_nullparams;
    public static String DataStoreServiceExtension_theParam;
    public static String DataStoreServiceExtension_wrongType;
    public static String ICatalog_dispose;
    public static String IService_dispose;
	public static String ResolveDelta_error_newHandleRequired;
    public static String catalog_memory_service_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
