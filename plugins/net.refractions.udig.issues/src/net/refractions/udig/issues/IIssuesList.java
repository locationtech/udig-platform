package net.refractions.udig.issues;

import java.util.List;
import java.util.Set;

import net.refractions.udig.issues.listeners.IIssuesListListener;


/**
 * Encapsulates a list of issues.  The default implementation is a list kept in memory.  But other implementation can be defined by
 * the
 *
 * @author Jesse
 * @since 1.1.0
 */
public interface IIssuesList extends List<IIssue> {
    /**
     * Returns a set of all the issue groups that are in the list.
     *
     * @return a set of all the issue groups.
     */
    public Set<String> getGroups();
    /**
     * Gets the all issues with the groupID
     *
     * @param groupId groupId of a group of issues
     * @return all issues with the groupID
     */
    public List<IIssue> getIssues(String groupId);
    /**
     * Remove all issues in the group.
     *
     * @param groupId group id of issues to remove
     */
    public void removeIssues(String groupId);
    /**
     * Add Listener to list
     *
     * @param listener listener to add
     */
    public void addListener( IIssuesListListener listener );
    /**
     * Remove listener from list
     *
     * @param listener listener to remove
     */
    public void removeListener( IIssuesListListener listener );
    /**
     * Returns The extension id of the list as defined in a plugin.xml file.
     * @return The extension id of the list as defined in a plugin.xml file.
     */
    public String getExtensionID();
}
