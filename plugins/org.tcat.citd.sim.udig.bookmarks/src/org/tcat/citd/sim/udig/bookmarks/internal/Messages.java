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
package org.tcat.citd.sim.udig.bookmarks.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.tcat.citd.sim.udig.bookmarks.internal.messages"; //$NON-NLS-1$
	public static String BookmarkAction_dialogtitle_renamebookmark;
	public static String BookmarkAction_dialogtitle_bookmarklocation;
	public static String BookmarkAction_dialogprompt_removebookmark;
	public static String BookmarkAction_dialogtitle_removebookmark;
	public static String BookmarkAction_dialogprompt_removeselectedbookmarks;
	public static String BookmarksContentProvider_emptybookmarkslist;
	public static String BookmarkAction_dialogprompt_removeselectedprojects;
	public static String BookmarkAction_dialogprompt_removeprojectbookmarks;
	public static String BookmarkAction_dialogprompt_removeselectedmaps;
	public static String BookmarkAction_dialogprompt_removemapbookmarks;
	public static String BookmarkAction_dialogprompt_removeallbookmarks;
	public static String BookmarkAction_dialogtitle_removebookmarks;
	public static String BookmarkManager_name_bookmarkmanager;
	public static String BookmarkManager_bookmarkdefaultname;
	public static String BookmarkAction_restorebookmarksaction;
	public static String BookmarkAction_savebookmarksaction;
	public static String BookmarkAction_renamebookmarkaction;
	public static String BookmarkAction_addbookmarkaction;
	public static String BookmarkAction_gotobookmarkaction;
	public static String BookmarkAction_removeallbookmarksaction;
	public static String BookmarkAction_removeprojectaction;
	public static String BookmarkAction_removemapaction;
	public static String BookmarkAction_removebookmarkaction;
	public static String BookmarkAction_dialogprompt_enterbookmarkname;
	
    static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
