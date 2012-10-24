/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.printing.ui.pdf;

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
