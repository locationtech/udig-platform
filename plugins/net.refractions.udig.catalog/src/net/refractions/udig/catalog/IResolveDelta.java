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
package net.refractions.udig.catalog;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * Constants used to communicate Catalog Deltas.
 * <p>
 * For those familiar with IResourceChangeEvent and IResourceDelta from eclipse development there is
 * one <b>important addition</b>. The constant REPLACE indicates a reaname, or substiution, you
 * will need to replace any references you have to the oldObject with the newObject.
 * </p>
 * <p>
 * For "bit mask" style interation please use: <code>EnumSet.of(Kind.ADDED, Kind.REPLACED)</code>
 * </p>
 * 
 * @author Jody Garnett
 * @since 0.6.0
 */
public interface IResolveDelta {
    /** List indicating no children are present */
    public static final List<IResolveDelta> NO_CHILDREN = Collections.emptyList();

    /**
     * Kind of Delta, used to indicate change.
     * 
     * @author jgarnett
     * @since 0.9.0
     */
    public enum Kind {
        /**
         * Delta kind constant indicating no change.
         * 
         * @see getKind()
         */
        NO_CHANGE,

        /**
         * The resource has been added to the catalog.
         * 
         * @see getKind()
         */
        ADDED,

        /**
         * The resource has been removed from the catalog.
         * 
         * @see getKind()
         */
        REMOVED,

        /**
         * The resource has been changed.  getvalues will have values.
         * 
         * @see getKind()
         */
        CHANGED,

        /**
         * The resource has been replaced with another entry in the catalog.
         * 
         * @see getKind()
         */
        REPLACED,
    }
    /**
     * Returns the kind of this delta.
     * <p>
     * Normally, one of <code>ADDED</code>, <code>REMOVED</code>, <code>CHANGED</code> or
     * <code>REPLACED</code>.
     * </p>
     * <p>
     * This set is still open, during shutdown we may throw a few more kinds around. Eclipse makes
     * use of PHANTOM, and NON_PHANTOM not sure we care
     * </p>
     * 
     * @return the kind of this resource delta
     * @see Kind.ADDED
     * @see Kind.REMOVED
     * @see Kind.CHANGED
     * @see Kind.REPLACED
     */
    public Kind getKind();

    /**
     * Accepts the given visitor.
     * <p>
     * The only kinds of resource delta that our visited are ADDED, REMOVED, CHANGED and REPLACED.
     * </p>
     * <p>
     * This is a convenience method, equivalent to accepts( visitor, IService.NONE )
     * </p>
     * 
     * @param visitor
     * @throws CoreException
     */
    public void accept( IResolveDeltaVisitor visitor ) throws IOException;

    /**
     * Resource deltas for all added, removed, changed, or replaced.
     * <p>
     * This is a short cut for:
     * 
     * <pre><code>
     *  finally List list = new ArrayList();
     *  accept( IServiceDeltaVisitor() {
     *  public boolean visit(IResolveDelta delta) {
     *          switch (delta.getKind()) {
     *          case IDelta.ADDED :
     *          case IDelta.REMOVED :
     *          case IDelta.CHANGED :
     *          case IDelta.REPLACED :
     *              list.add( delta );
     *          default: // ignore    
     *          }
     *      return true;
     *      }
     *  });
     *  return list.toArray();
     * </code></pre>
     * 
     * </p>
     */
    public List<IResolveDelta> getChildren();

    /**
     * Finds and returns the delta information for a given resource.
     * 
     * @kindMask Set of IDelta.Kind
     * @return Array of IGeoResourceDelta
     */
    public List<IResolveDelta> getChildren( EnumSet<Kind> kindMask );

    /**
     * Returns a handle for the affected handle.
     * <p>
     * For additions (<code>ADDED</code>), this handle describes the newly-added resolve; i.e.,
     * the one in the "after" state.
     * <p>
     * For changes (<code>CHANGED</code>), this handle also describes the resource in the
     * "after" state.
     * <p>
     * For removals (<code>REMOVED</code>), this handle describes the resource in the "before"
     * state. Even though this handle not normally exist in the current workspace, the type of
     * resource that was removed can be determined from the handle.
     * <p>
     * For removals (<code>REPLACE</code>), this handle describes the resource in the "before"
     * state. The new handle can be determined with getNewResolve().
     * <p>
     * 
     * @return the affected resource (handle)
     */
    public IResolve getResolve();

    /**
     * For replacement (<code>REPLACE</code>), this handle describes the resource in the "after"
     * state. The old handle can be determined with getResolve().
     * <p>
     * 
     * @return The new resolve replacing the affected handle.
     */
    public IResolve getNewResolve();
    
    /**
     * If {@link #getKind()}==Kind.CHANGED this method returns the new value of the item changed for example the new bounds of the IGeoResource.  
     * Otherwise it will return null.
     *
     * @return  If {@link #getKind()}==Kind.CHANGED this method returns the new value of the item changed, otherwise it will return null.
     */
    public Object getNewValue();

    /**
     * If {@link #getKind()}==Kind.CHANGED this method returns the old value of the item changed for example the old bounds of the IGeoResource.  
     * Otherwise it will return null.
     *
     * @return  If {@link #getKind()}==Kind.CHANGED this method returns the old value of the item changed, otherwise it will return null.
     */
    public Object getOldValue();
}