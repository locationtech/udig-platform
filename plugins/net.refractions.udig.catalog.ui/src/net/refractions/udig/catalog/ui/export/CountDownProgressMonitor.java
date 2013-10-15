/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.ui.export;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.ui.ProgressManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * Each time worked is called the total is decremented and the task name is done. 
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class CountDownProgressMonitor extends SubProgressMonitor{

    private int remaining;
    private long lastChange;
    private int worked=0;
    private String name;

    /**
     * new instance
     * @param monitor the "real" progress monitor
     * @param ticks the number of ticks that this monitor can use
     * @param resource the layer that is being exported
     * @param remaining the number of features left to export.
     */
    public CountDownProgressMonitor( IProgressMonitor monitor, int ticks, IGeoResource resource, int remaining ) {
        super(monitor, ticks);
        this.remaining=remaining;
        try {
            IGeoResourceInfo info = resource.getInfo(ProgressManager.instance().get());
            name=info.getTitle();
            if( name==null ){
                name=info.getName();
            }
        } catch (IOException e) {
            CatalogUIPlugin.log("Couldn't read info", e); //$NON-NLS-1$
            name=resource.getIdentifier().toString();
        }
        setTaskName();
    }

    private void setTaskName() {
        setTaskName(Messages.CountDownProgressMonitor_taskNamePart1+name+","+Messages.CountDownProgressMonitor_taskNamePart2+remaining); //$NON-NLS-1$
        lastChange=System.currentTimeMillis();
    }
    
    @Override
    public void worked( int work ) {
        worked++;
        remaining-=work;
        if((System.currentTimeMillis()-lastChange)>1000 ){
            setTaskName();
            super.worked(worked);
            worked=0;
        }
    }

    public int getRemaining() {
        return remaining;
    }

}
