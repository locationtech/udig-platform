/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tool.edit;

import net.refractions.udig.tools.edit.EditBlackboardUtil;
import net.refractions.udig.tools.edit.support.EditBlackboard;

import org.eclipse.ui.IStartup;

/**
 * Temporary until we have good caching of MathTransforms and CRS objects
 * @author Jesse
 * @since 1.1.0
 */
public class PopulateCaches implements IStartup {

    public void earlyStartup() {
        @SuppressWarnings("unused")
        EditBlackboard bb = EditBlackboardUtil.EMPTY_BLACKBOARD;
    }

}
