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
package org.locationtech.udig.printing.ui.pdf;

/**
 * 
 * This bean is used to pass configuration information to the ExportPDFWizard. 
 * <p>
 *
 * </p>
 * @author brocka
 * @since 1.1.0
 */
public class ExportPDFWizardConfigBean {

    public static final String BLACKBOARD_KEY = "ExportPDFWizardConfiBean.key"; //$NON-NLS-1$
    
    private String defaultFilename;

    public ExportPDFWizardConfigBean() {
        this.defaultFilename = null;
    }
    
    public String getDefaultFilename() {
        return defaultFilename;
    }

    public void setDefaultFilename( String defaultFilename ) {
        this.defaultFilename = defaultFilename;
    }
    
    
}
