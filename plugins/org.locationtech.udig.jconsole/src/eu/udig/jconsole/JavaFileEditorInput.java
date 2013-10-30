/**
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package eu.udig.jconsole;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class JavaFileEditorInput implements IPathEditorInput, ILocationProvider {

    public final static String UNTITLEDFILE = System.getProperty("user.home") + File.separator
            + "default.groovy";

    /**
     * The workbench adapter which simply provides the label.
     * 
     * @since 3.1
     */
    private class WorkbenchAdapter implements IWorkbenchAdapter {
        private WorkbenchAdapter() {
            super();
        }
        public Object[] getChildren( Object o ) {
            return null;
        }
        public ImageDescriptor getImageDescriptor( Object object ) {
            return null;
        }
        public String getLabel( Object o ) {
            return ((JavaFileEditorInput) o).getName();
        }
        public Object getParent( Object o ) {
            return null;
        }
    }

    private File fFile;

    private WorkbenchAdapter fWorkbenchAdapter = new WorkbenchAdapter();

    public JavaFileEditorInput( File file ) {

        super();

        fFile = file;

        fWorkbenchAdapter = new WorkbenchAdapter();

    }

    public boolean exists() {

        return fFile.exists();

    }

    public ImageDescriptor getImageDescriptor() {

        return null;

    }

    public String getName() {

        return fFile.getName();

    }

    public IPersistableElement getPersistable() {

        return null;

    }

    public String getToolTipText() {

        return fFile.getAbsolutePath();

    }

    public String getAbsolutePath() {

        return fFile.getAbsolutePath();

    }

    public Object getAdapter( Class adapter ) {

        if (ILocationProvider.class.equals(adapter))
            return this;

        if (IWorkbenchAdapter.class.equals(adapter))
            return fWorkbenchAdapter;

        // if (IFile.class.equals(adapter)) {
        //            
        // IPath location = Path.fromOSString(fFile.getAbsolutePath());
        // IFileStore fileStore = EFS.getLocalFileSystem().getStore(location);
        // fileStore.fetchInfo().
        //            
        //            
        // IWorkspace ws = ResourcesPlugin.getWorkspace();
        // IProject project = ws.getRoot().getProject();
        // try {
        // if (!project.exists())
        // project.create(null);
        // if (!project.isOpen())
        // project.open(null);
        // } catch (CoreException e) {
        // e.printStackTrace();
        // }
        // IFile file = project.getFile(location.lastSegment());
        // return file;
        // }

        return Platform.getAdapterManager().getAdapter(this, adapter);

    }
    public IPath getPath( Object element ) {

        if (element instanceof JavaFileEditorInput) {

            JavaFileEditorInput input = (JavaFileEditorInput) element;

            return Path.fromOSString(input.fFile.getAbsolutePath());

        }

        return null;

    }

    public IPath getPath() {

        return Path.fromOSString(fFile.getAbsolutePath());

    }

    public boolean equals( Object o ) {

        if (o == this)
            return true;

        if (o instanceof JavaFileEditorInput) {

            JavaFileEditorInput input = (JavaFileEditorInput) o;

            return fFile.equals(input.fFile);

        }

        if (o instanceof IPathEditorInput) {

            IPathEditorInput input = (IPathEditorInput) o;

            return getPath().equals(input.getPath());

        }

        return false;

    }

    public int hashCode() {

        return fFile.hashCode();

    }

}
