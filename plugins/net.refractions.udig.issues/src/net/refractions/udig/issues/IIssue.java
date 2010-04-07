/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.issues;

import net.refractions.udig.core.enums.Priority;
import net.refractions.udig.core.enums.Resolution;
import net.refractions.udig.issues.listeners.IIssueListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * Specifies an issue that requires the user's input to be dealt with. One example is an invalid
 * feature.
 * 
 * @author jones
 * @since 1.0.0
 */
public interface IIssue {
    /**
     * Adds a {@link IIssueListener} to the issue.  The {@link IIssuesManager} listens to the issue in order to know which issues need
     * to be saved.  If an implementation does not raise events when a property of the issue is changed and it does not call 
     * {@link IIssuesManager#save(IProgressMonitor)} then the change will not be saved.
     *
     * @param listener listener to add.
     */
    public void addIssueListener(IIssueListener listener);
    /**
     * Removes a {@link IIssueListener} from the issue.
     *
     * @param listener listener to remove.
     */
    public void removeIssueListener(IIssueListener listener);
    /**
     * Returns the id of the ViewPart that the issue requires in order to be resolved. This is
     * called by the issues view. If a non-null value is returned, the IssuesView will show the
     * ViewPart identified by the id. Otherwise nothing is done.
     * 
     * @return The id of the ViewPart the issue concerns.
     */
    String getViewPartId();
    /**
     * If the ViewPart identified by getViewPartID is a view and the IMemento returned by this
     * method is non-null, the view will be initialized with the memento.
     * 
     * @memento IMemento for the state of the View identified by {@link #getViewPartId()} if it is a
     *         view. Null otherwise.
     */
    void getViewMemento(IMemento memento);
    /**
     * The id of the editor that will be opened to edit the Editor Input.  If the {@link #getEditorInput()}
     * does not return null and {@link #getEditorID()} also does not then the editor will be
     * opened by the framework (It does not need to be done in fixIssue() method).
     * 
     * @return The id of the editor that will be opened to edit the Editor Input.  
     */
    String getEditorID();
    /**
     * If returns not null the framework will attempt to open an editor that can edit the input
     * object.
     */
    public IEditorInput getEditorInput();
    /**
     * If not null the framework will open the identified perspective.
     * 
     * @return id of a perspective to open. Or null.
     */
    public String getPerspectiveID();
    /**
     * Returns A one or two word name for the object that has the <i>issue</i>. This should be
     * translateable and understandable by a human.
     */
    String getProblemObject();
    /**
     * Gets the description of the Issue. Should be a single line and around one line. Should be
     * translateable.
     * 
     * @return the description of the Issue.
     */
    String getDescription();

    /**
     * Sets the description of the issue.
     * 
     * @param description
     */
    void setDescription( String description );
    /**
     * Returns the priority of the issue.
     * 
     * @return the priority of the issue.
     */
    Priority getPriority();
    /**
     * Sets the priority of the issue.
     */
    void setPriority( Priority priority );
    /**
     * This is called when the user wishes to fix the issue. It will be called after the
     * WorkbenchPath has been shown and set with the memento if it is a view.
     * <p>
     * This method can do other initializations and return or it can run the user through a workflow
     * and only return when the problem is fixed or the user cancels the operation.
     * </p>
     * <p>
     * This method does <b>NOT</b> have to fix the issue
     * </p>
     * <p>
     * This method <b>IS</b> run in the display thread.
     * </p>
     * 
     * @param part The workbenchpart identified by {@linkplain #getViewPartId()} or null.
     * @param editor The editor that was identified by {@linkplain #getEditorID()} or null.
     * @see FeatureIssue
     */
    void fixIssue( IViewPart part, IEditorPart editor );
    /**
     * Indicates whether the issue has been resolved. This method <b>MUST</b> return quickly. If
     * the resolution state cannot be determined quickly then either {@linkplain Resolution#UNKNOWN}
     * or {@linkplain Resolution#UNRESOLVED} should be returned.
     * 
     * @return true if the issue no longer exists.
     */
    Resolution getResolution();
    /**
     * Sets the state of resolution.
     * 
     * @param newResolution the new state.
     */
    void setResolution( Resolution newResolution );
    /**
     * Returns the id of the issue.  
     *
     * @return id of the issue.
     */
    String getId();
    /**
     * Sets the id of the issue.  This should only be called by the Issues list never by client code.
     *
     * @param newId
     */
    void setId(String newId);
    /**
     * The GroupID groups sets of Issues.  
     * A potential use is to group Issues by analysis operation so that groups of Issues can be
     * deleted/resolved/replaced as a group.
     */
    String getGroupId();
    /**
     * Gets a property of the issue.
     * This allows the interface to be more flexible.
     * @param property the name/key of the property
     */
    Object getProperty( String property );
    /**
     * Returns an array of all the property names in the issue.  For correctness
     * all the names should be able to passed to getProperty() without throwing an exception.
     * 
     * @return an array of all the property names in the issue
     */
    String[] getPropertyNames();
    /**
     * Called by framework to initialize the Issue.
     * @param memento The saved state of a Issue. May be null.
     * @param viewMemento the memento for initializing the view, may be null
     * @param issueId the id the issue is assigned.  {@link #getId()} must return the same id.
     * @param groupId the group id of the issue.
     * @param bounds the bounds of the issue.
     */
    void init( IMemento memento, IMemento viewMemento, String issueId, String groupId, ReferencedEnvelope bounds );
    /**
     * Populates the memento with data specialized for the issues type.  Most data (see below) is saved by the framework.  Only data, such as properties,
     * or other data required for executing {@link #fixIssue(IViewPart, IEditorPart)} needs to be saved in the memento.
     * 
     * <p>State that is saved by the framework and is not required to be saved in this memento include:</p>
     * <ul>
     * <li>id</li>
     * <li>priority</li>
     * <li>group id</li>
     * <li>description</li>
     * <li>resolution</li>
     * <li>view memento</li>
     * <li>bounds</li>
     * <li>issue extension point</li>
     * </ul>  
     * @param memento the memento to populate with state data.
     */
    void save( IMemento memento );
    /**
     * Returns the id of the extension definition for this type of Issue.
     * This is required so the framework can load the correct issue class during issue loading.
     * If the class is can not be loaded then a Placeholder issue will be created instead which will
     * notify the user that a plugin is required to manage the issue.  
     * <p> The extension id is formed by the namespace of the plugin combined with the name of the extension element.</p>
     * <p>For example:  The FeatureIssue's extension id would be: <em>net.refractions.udig.issues.featureIssue</em> because the issue implementation is in
     * the Issues plugin (which the id <em>net.refractions.udig.issues</em>) and the extension name is featureIssue.  The two are combined to form the id of the
     * extension</p> 
     */
    String getExtensionID();
    /**
     * Returns the area that this issue affects.  May return null.
     *
     * @return Returns the area that this issue affects.  May return null.
     */
    ReferencedEnvelope getBounds();
}
