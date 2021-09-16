/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.tool;


/**
 * Constants that are often used when defining tool extensions or creating tool extensions. Some of
 * the constants are the well known tool category ids.
 * 
 * @author jones
 */
public interface ToolConstants {
    public static final String ZOOM_CA = "org.locationtech.udig.tool.category.zoom"; //$NON-NLS-1$
    public static final String RENDER_CA = "org.locationtech.udig.tool.category.render"; //$NON-NLS-1$
    public static final String PAN_CA = "org.locationtech.udig.tool.category.pan"; //$NON-NLS-1$
    public static final String SELECTION_CA = "org.locationtech.udig.tool.category.selection"; //$NON-NLS-1$
    public static final String INFO_CA = "org.locationtech.udig.tool.category.info"; //$NON-NLS-1$
    public static final String EDIT_CA = "org.locationtech.udig.tool.category.edit"; //$NON-NLS-1$
    public static final String TOOL_EDIT_CA = "org.locationtech.udig.tool.edit.edit"; //$NON-NLS-1$
    public static final String TOOL_CREATE_CA = "org.locationtech.udig.tool.edit.create"; //$NON-NLS-1$
    public static final String TOOL_FEATURE_CA = "org.locationtech.udig.tool.edit.feature"; //$NON-NLS-1$
    
    
    /**
     * Key for default tool cursor ID.
     * 
     * @see org.locationtech.udig.project.ui.tool.Tool#getProperty()
     * @see org.locationtech.udig.project.ui.tool.Tool#setProperty()
     */
    public static final String DEFAULT_CURSOR_ID_KEY = "defaultCursorId";  //$NON-NLS-1$
    
    
    /**
     * The ID of the action tools toolbar contribution item.
     * <p>
     * The contribution item is an instance of <code>org.eclipse.jface.action.ToolBarContributionItem</code>
     * to be used in cool bar managers.
     * <p>
     * @see IToolManager.contributeToCoolBar( SubCoolBarManager cbmanager, IActionBars bars )
     */
    public static final String ACTION_TOOLBAR_ID = "org.locationtech.udig.tool.actionToolBar"; //$NON-NLS-1$
    
    /**
     * The ID of the modal tools toolbar contribution item.
     */
    public static final String MODAL_TOOLBAR_ID = "org.locationtech.udig.tool.modalToolBar"; //$NON-NLS-1$
}
