/**
 * <copyright></copyright> $Id$
 */
package org.locationtech.udig.project.internal.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.locationtech.udig.project.command.Command;
import org.locationtech.udig.project.command.CommandManager;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectElement;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.commands.DefaultErrorHandler;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * Default implementation
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class ProjectImpl extends EObjectImpl implements Project {

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     *
     * @see #getName()
     * @generated NOT
     * @ordered
     */
    protected static final String NAME_EDEFAULT = ""; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The cached value of the '{@link #getElementsInternal() <em>Elements Internal</em>}' reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getElementsInternal()
     * @generated
     * @ordered
     */
    protected EList<ProjectElement> elementsInternal;

    private Adapter projectPersistenceListener = new AdapterImpl() {
        /**
         * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
         */
        public void notifyChanged(Notification msg) {
            switch (msg.getFeatureID(Project.class)) {
            case ProjectPackage.PROJECT__ELEMENTS_INTERNAL:
            case ProjectPackage.PROJECT__NAME:
                if (ProjectImpl.this.eResource() != null)
                    ProjectImpl.this.eResource().setModified(true);
            }
        }
    };

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
    protected ProjectImpl() {
        super();
        eAdapters().add(projectPersistenceListener);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ProjectPackage.Literals.PROJECT;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.PROJECT__NAME,
                    oldName, name));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated not
     */
    public List<ProjectElement> getElementsInternal() {
        if (elementsInternal == null) {
            elementsInternal = new SynchronizedEObjectWithInverseResolvingEList<ProjectElement>(
                    ProjectElement.class, this, ProjectPackage.PROJECT__ELEMENTS_INTERNAL,
                    ProjectPackage.PROJECT_ELEMENT__PROJECT_INTERNAL) {

                /** long serialVersionUID field */
                private static final long serialVersionUID = 3978658123285628492L;

                @Override
                protected void didAdd(int index, ProjectElement newObject) {
                    createResourceAndAddElement(ProjectImpl.this, (ProjectElement) newObject);
                    super.didAdd(index, newObject);
                }

                @Override
                protected void didSet(int index, ProjectElement newObject, ProjectElement oldObject) {
                    createResourceAndAddElement(ProjectImpl.this, (ProjectElement) newObject);
                    super.didSet(index, newObject, oldObject);
                }
            };
        }
        return elementsInternal;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID,
            NotificationChain msgs) {
        switch (featureID) {
        case ProjectPackage.PROJECT__ELEMENTS_INTERNAL:
            return ((InternalEList<InternalEObject>) (InternalEList<?>) getElementsInternal())
                    .basicAdd(otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID,
            NotificationChain msgs) {
        switch (featureID) {
        case ProjectPackage.PROJECT__ELEMENTS_INTERNAL:
            return ((InternalEList<?>) getElementsInternal()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case ProjectPackage.PROJECT__NAME:
            return getName();
        case ProjectPackage.PROJECT__ELEMENTS_INTERNAL:
            return getElementsInternal();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case ProjectPackage.PROJECT__NAME:
            setName((String) newValue);
            return;
        case ProjectPackage.PROJECT__ELEMENTS_INTERNAL:
            getElementsInternal().clear();
            getElementsInternal().addAll((Collection<? extends ProjectElement>) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
        case ProjectPackage.PROJECT__NAME:
            setName(NAME_EDEFAULT);
            return;
        case ProjectPackage.PROJECT__ELEMENTS_INTERNAL:
            getElementsInternal().clear();
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case ProjectPackage.PROJECT__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        case ProjectPackage.PROJECT__ELEMENTS_INTERNAL:
            return elementsInternal != null && !elementsInternal.isEmpty();
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
    public List getElements(Class type) {
        List lists = new ArrayList();
        for (Iterator iter = getElementsInternal().iterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (type.isAssignableFrom(obj.getClass()))
                lists.add(obj);
        }
        return lists;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (name: "); //$NON-NLS-1$
        result.append(name);
        result.append(')');
        return result.toString();
    }

    CommandManager commandManager;

    /**
     * @see org.locationtech.udig.project.IProject#getElements()
     */
    @SuppressWarnings("unchecked")
    public List getElements() {
        return Collections.unmodifiableList(getElementsInternal());
    }

    private void initCommandManager() {

        synchronized (CommandManager.class) {
            if (commandManager == null) {
                commandManager = new CommandManager(Messages.ProjectImpl_commandManagerName,
                        new DefaultErrorHandler());
            }
        }
    }

    public void sendASync(Command command) {
        initCommandManager();
        commandManager.aSyncExecute(command);
    }

    public void sendSync(Command command) {
        initCommandManager();
        commandManager.syncExecute(command);
    }

    /**
     * Creates a new Resource from map.  The new Resource will be in the same directory as the project's
     * resource.  The Resource will start with map appended with a number that will make the name unique.
     * The resource will end in .umap.
     */
    private void createResourceAndAddElement(Project value, ProjectElement projectElement) {
        if (projectElement == null || projectElement.eIsProxy())
            return;
        Resource projectResource = eResource();
        if (projectResource != null) {
            URI projectURI = projectResource.getURI();
            String elementPath = null;
            elementPath = findElementResourcePath(projectElement, elementPath);

            String projectPath = findProjectResourcePath(projectURI);

            if (!projectPath.equals(elementPath))
                doCreation(projectElement, projectResource, elementPath, projectPath);

        }
    }

    @SuppressWarnings("unchecked")
    private static void doCreation(ProjectElement projectElement, Resource projectResource,
            String elementPath, String projectPath) {
        Resource resource = null;

        URI uri = createNewResource(projectResource, projectPath, projectElement);
        resource = projectResource.getResourceSet().createResource(uri);
        resource.getContents().add(projectElement);
        resource.setTrackingModification(true);
        resource.setModified(true);
    }

    private static String findProjectResourcePath(URI projectURI) {
        String projectPath = projectURI.toFileString();
        if (projectURI.hasAuthority()) { //remove '//' character (added in URI from emf version 2.9 and onward
            projectPath = StringUtils.removeStart(projectPath, "//");
        }
        projectPath = projectPath.substring(0, projectPath.lastIndexOf(File.separatorChar));
        while (projectPath.startsWith(File.separator + File.separator)) {
            projectPath = projectPath.substring(1);
        }
        if (Platform.getOS().equals(Platform.OS_WIN32) && projectPath.startsWith(File.separator)) {
            projectPath = projectPath.substring(1);
        }
        return projectPath;
    }

    private String findElementResourcePath(ProjectElement projectElement, String elementPath2) {
        String elementPath = elementPath2;
        if (projectElement.eResource() != null) {
            URI elementPathUri = projectElement.eResource().getURI();
            elementPath = elementPathUri.toFileString();
            if (elementPathUri.hasAuthority()) { //remove '//' character (added in URI from emf version 2.9 and onward
                elementPath = StringUtils.removeStart(elementPath, "//");
            }
            elementPath = elementPath.substring(0, elementPath.lastIndexOf(File.separatorChar));
            while (elementPath.startsWith(File.separator + File.separator)) {
                elementPath = elementPath.substring(1);
            }
            if (Platform.getOS().equals(Platform.OS_WIN32)
                    && elementPath.startsWith(File.separator)) {
                elementPath = elementPath.substring(1);
            }
        }
        return elementPath;
    }

    @SuppressWarnings("unchecked")
    private static URI createNewResource(Resource projectResource, String projectPath,
            ProjectElement projectElement) {
        int i = 0;
        List<Resource> list = projectResource.getResourceSet().getResources();
        URIConverter uriConverter = projectResource.getResourceSet().getURIConverter();
        URI uri = null;
        boolean found = false;
        do {
            found = false;
            i++;
            //TODO Add file extension name to ProjectElement
            uri = generateResourceName(projectPath, projectElement, i);

            URI normalizedURI = uriConverter.normalize(uri);
            for (Resource resource2 : list) {
                if (uriConverter.normalize(resource2.getURI()).equals(normalizedURI)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                File file = new File(uri.toFileString());
                if (file.exists())
                    found = true;
            }
        } while (found);
        uri.deresolve(projectResource.getURI(), true, true, true);
        return uri;
    }

    private static URI generateResourceName(String projectPath, ProjectElement projectElement, int i) {
        URI uri;
        String resourceName = (projectElement.getName() == null ? "element" : projectElement.getName()) + i; //$NON-NLS-1$
        resourceName = resourceName.replaceAll("[/\\\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$
        resourceName = resourceName.replaceAll("\\s", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        resourceName = resourceName.replaceAll("_+", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        resourceName = resourceName.replaceAll(":", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        String extension = projectElement.getFileExtension();
        if (!extension.startsWith(".")) //$NON-NLS-1$
            extension = "." + extension; //$NON-NLS-1$
        String tempPath = "file://" + projectPath + File.separator + resourceName + extension; //$NON-NLS-1$  
        uri = URI.createURI(tempPath);
        return uri;
    }

    public URI getID() {
        if (eResource() == null)
            return URI.createFileURI(getName());
        return eResource().getURI();
    }

} // ProjectImpl
