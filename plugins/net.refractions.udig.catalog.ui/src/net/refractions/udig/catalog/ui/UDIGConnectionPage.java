/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.catalog.ui;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * A udig import wizard page is responsible for providing a user interface which is used to gather
 * connection information for a specific service.
 * <p>
 * It is not strictly required but it is highly recommended that the implementation be a subclass of
 * {@link AbstractUDIGImportPage}.
 * </p>
 *
 * @author jeichar
 * @since 0.9.0
 * @see AbstractUDIGImportPage
 */
public interface UDIGConnectionPage extends IWizardPage {

    /**
     * Returns the connection parameters stored by the connection page. If this is not the final
     * page in the sequence of pages null(or anything else) may be returned.
     *
     * @return A map of connection parameters.
     */
    Map<String, Serializable> getParams();
    /**
     * Sets or clears the error message for this page.
     *
     * @param newMessage the message, or <code>null</code> to clear the error message
     */
    void setErrorMessage( String newMessage );

}
