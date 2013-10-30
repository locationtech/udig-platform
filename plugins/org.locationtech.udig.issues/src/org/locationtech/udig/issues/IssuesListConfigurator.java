/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IMemento;

/**
 * A interface for classes that can configuring an IssuesList.  Each Configurator is defined in the same extension as the IssuesList it can
 * configure.  Some IIssuesLists don't require a configurator.  An example of an issues list that requires a configuration is one that is backed onto
 * a database.  The username/password and connection information need to be set; it is the job of the IssuesListConfigurator to do this.  
 * <p>
 * The configuration appears in the preference pages of the application.
 * </p>
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface IssuesListConfigurator {
    /**
     * Initializes the IssuesListConfigurator <em>AND</em> the IssuesList.  It is important to initialize the issueslist with the memento object
     * when this object is called.  getControl() is not always called.
     * @param list the list that the configurator will configure.
     * @param memento a memento that contains the previous configuration.  Maybe null if the list has never been configured.
     */
    public void initConfiguration( IIssuesList list, IMemento memento );
    /**
     * Fills the memento object with the configuration data.  This is not used to configure the IssuesList.  It is used in the future
     * to configure the IssuesListConfiguration Object.
     * <p><em>Note:</em> The control may be disposed by this point so ensure that the method does not need to access any widgets in the control.</p>
     */
    public void getConfiguration(IMemento memento);
    /**
     * Returns a control that will configure the issues list.  Should use the configuration memento to fill out the controls.  
     * <p>Warning: the memento may be null</p>  
     *
     * @param parent the composite that will be used to create the control.  The Layout of the parent is a FillLayout.
     * @return a control that will configure the issues list.
     */
    public Control getControl( Composite parent, IIssuesPreferencePage page );
    /**
     * Returns true if the IIssuesList can be used.  If the IIssuesList is backed onto a database; the database should be queried to ensure that the
     * issues list is correctly configured.  If it is not then this method should return null so that the issues list will not be used.
     *
     * @return  true if the IIssuesList can be used
     */
    public boolean isConfigured();
    /**
     * This method is called after {@link #isConfigured()} is called.  If {@link #isConfigured()} returns false this method is called to obtain a 
     * human readable error message. 
     *
     * @return a human readable error message.  Or null if isConfigured was not previously called or if there was no error.
     */
    public String getError();
}
