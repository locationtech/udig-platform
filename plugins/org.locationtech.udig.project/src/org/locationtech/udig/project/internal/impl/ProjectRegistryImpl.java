/**
 * <copyright></copyright> $Id$
 */
package org.locationtech.udig.project.internal.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.project.IProject;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.ProjectRegistry;
import org.locationtech.udig.ui.PlatformGIS;

/**
 * TODO Purpose of org.locationtech.udig.project.internal.impl
 * <p>
 * </p>
 *
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class ProjectRegistryImpl extends EObjectImpl implements ProjectRegistry {
    /**
     * The cached value of the '{@link #getCurrentProject() <em>Current Project</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getCurrentProject()
     * @generated
     * @ordered
     */
    protected Project currentProject;

    /**
     * The cached value of the '{@link #getProjects() <em>Projects</em>}' reference list. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getProjects()
     * @generated NOT
     * @ordered
     */
    protected List projects = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    protected ProjectRegistryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ProjectPackage.Literals.PROJECT_REGISTRY;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Project> getProjects() {
        if (projects == null) {
            projects = new EObjectResolvingEList(Project.class, this,
                    ProjectPackage.PROJECT_REGISTRY__PROJECTS);
        }

        for (Iterator<Project> iter = projects.iterator(); iter.hasNext();) {
            Project project = iter.next();
            if (project.eResource() == null) {
                iter.remove(); // delete this one its resource is missing
            }
        }
        return projects;
    }

    /**
     * @see org.locationtech.udig.project.internal.ProjectRegistry#getDefaultProject()
     */
    @Override
    public Project getDefaultProject() {
        String projectName = Messages.ProjectRegistry_defaultName;
        String path = new File(Platform.getLocation().toString() + File.separatorChar + projectName)
                .getAbsolutePath();
        currentProject = getProject(path);
        return currentProject;
    }

    /**
     * @see org.locationtech.udig.project.internal.ProjectRegistry#getCurrentProject()
     * @uml.property name="currentProject"
     */
    @Override
    public Project getCurrentProject() {
        Project p = getCurrentProjectGen();
        if (p == null && !getProjects().isEmpty()) {
            p = getProjects().get(0);
        }
        if (p == null) {
            p = getDefaultProject();
        }
        return p;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public Project getCurrentProjectGen() {
        if (currentProject != null && currentProject.eIsProxy()) {
            InternalEObject oldCurrentProject = (InternalEObject) currentProject;
            currentProject = (Project) eResolveProxy(oldCurrentProject);
            if (currentProject != oldCurrentProject) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                            ProjectPackage.PROJECT_REGISTRY__CURRENT_PROJECT, oldCurrentProject,
                            currentProject));
            }
        }
        return currentProject;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public Project basicGetCurrentProject() {
        return currentProject;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public void setCurrentProject(Project newCurrentProject) {
        Project oldCurrentProject = currentProject;
        currentProject = newCurrentProject;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.PROJECT_REGISTRY__CURRENT_PROJECT, oldCurrentProject,
                    currentProject));
    }

    @Override
    public NotificationChain eSetResource(Internal resource, NotificationChain notifications) {
        if (resource == null || resource.getResourceSet() != resourceSet || resourceSet == null)
            throw new AssertionError();
        return super.eSetResource(resource, notifications);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public Project getProject(URI uri) {
        // There is a sporatic bug here. Remove this try/catch when it is fixed.
        try {
            if (findProject(uri) != null) {
                // already available!
                return findProject(uri);
            }

            URI projectURI = URI.createURI(uri.toString());

            final ProjectRegistry registry = getProjectRegistry();
            Resource registryResource = registry.eResource();
            if (registryResource == null) {
                System.out.println("Registery was unable to load"); //$NON-NLS-1$
                throw new Error(Messages.ProjectRegistryImpl_load_error);
            }

            URI registryURI = registryResource.getURI();
            projectURI.deresolve(registryURI, true, true, true);

            ResourceSet registeryResourceSet = registryResource.getResourceSet();
            Resource projectResource = registeryResourceSet.createResource(projectURI);
            try {
                projectResource.load(null);
            } catch (IOException e1) {
                // resource doesn't exist. That is ok.
            }

            Project incomingProject = null;
            if (projectResource.getContents().isEmpty()) {
                // new file being created!
                if (projectURI.isFile()) {
                    // check to see if it exists; we don't like empty existing files
                    File file = new File(projectURI.toFileString());
                    if (file.exists()) {
                        if (!file.delete())
                            throw new NullPointerException(
                                    "Unable to load " + uri + " file was empty");
                    }
                }
                // creating a new project from the new project wizard
                incomingProject = createProject(uri, projectResource);

            } else {
                // Go through list of resources
                EList<EObject> contents = projectResource.getContents();
                for (EObject eObject : contents) {
                    if (eObject instanceof Project) {
                        incomingProject = (Project) eObject;
                        break;
                    }
                }
                if (incomingProject == null) {
                    // this project was not saved with a project file?
                    // (does it represent an individial map? we are not sure)
                    throw new NullPointerException(
                            "Unable to load " + uri + " - does not contain a project");
                }
                // if (incomingProject == null) {
                // incomingProject = createProject(uri, resource);
                // List<ProjectElement> eContents = incomingProject.getElementsInternal();
                // for( EObject eObject : contents ) {
                // if (eObject instanceof MapImpl) {
                // MapImpl tmpMap = (MapImpl) eObject;
                // eContents.add(tmpMap);
                // }
                // }
                // }
            }

            final Project newProject = incomingProject;
            PlatformGIS.syncInDisplayThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        setCurrentProject(newProject);
                        registry.getProjects().add(newProject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return newProject;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            // System.err.println("GetProject URI: " +uri);
        }
        return null; // remove this too
    }

    private Project findProject(URI uri) {
        List<Project> projects = getProjects();
        for (Project project : projects) {
            if (isURIEqual(project.eResource().getURI(), uri))
                return project;
        }
        return null;
    }

    public static boolean isURIEqual(URI uri, URI uri2) {
        if (uri.equals(uri2))
            return true;

        String uriString = uri.toString();
        String uri2String = uri2.toString();
        uriString = replaceBackSlashes(uriString);
        uri2String = replaceBackSlashes(uri2String);

        uriString = uriString.replace("://", ":/"); //$NON-NLS-1$//$NON-NLS-2$
        uri2String = uri2String.replace("://", ":/"); //$NON-NLS-1$//$NON-NLS-2$
        return uriString.equals(uri2String);
    }

    private static String replaceBackSlashes(String uriString) {
        StringBuffer buffer = new StringBuffer(uriString.length());
        for (int i = 0; i < uriString.length(); i++) {
            char charAt = uriString.charAt(i);
            if (charAt == '\\')
                buffer.append('/');
            else {
                buffer.append(charAt);
            }
        }
        return buffer.toString();
    }

    @SuppressWarnings("unchecked")
    private Project createProject(URI uri, Resource resource) {
        Project tmpProject = ProjectFactory.eINSTANCE.createProject();
        String path = uri.toFileString();
        int start = path.indexOf(File.separator) != -1 ? path.lastIndexOf(File.separator) : 0;
        int end = path.indexOf('.') != -1 ? path.lastIndexOf('.') : path.length() - 1;
        tmpProject.setName(path.substring(start + 1, end));
        resource.getContents().add(tmpProject);
        resource.setModified(true);

        return tmpProject;
    }

    /**
     * Convert projectPath into a URI and call getProject( uri ).
     */
    @Override
    public Project getProject(String projectPath) {
        URL url;
        if (projectPath.startsWith("file:")) { //$NON-NLS-1$
            try {
                url = new URL(projectPath); // actually already a URL!
            } catch (MalformedURLException e) {
                System.err.println("Unable to turn " + projectPath + " into a URL to load");
                return null; // not a project
            }
        } else {
            File file = new File(projectPath);
            url = URLUtils.fileToURL(file);
            // projectPath = "file://" + projectPath; //$NON-NLS-1$
        }

        final String uriText = url.toExternalForm() + File.separatorChar
                + ProjectRegistry.PROJECT_FILE;
        final URI uri = URI.createURI(uriText);

        Project project = getProject(uri);
        return project;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case ProjectPackage.PROJECT_REGISTRY__CURRENT_PROJECT:
            if (resolve)
                return getCurrentProject();
            return basicGetCurrentProject();
        case ProjectPackage.PROJECT_REGISTRY__PROJECTS:
            return getProjects();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case ProjectPackage.PROJECT_REGISTRY__CURRENT_PROJECT:
            setCurrentProject((Project) newValue);
            return;
        case ProjectPackage.PROJECT_REGISTRY__PROJECTS:
            getProjects().clear();
            getProjects().addAll((Collection<? extends Project>) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
        case ProjectPackage.PROJECT_REGISTRY__CURRENT_PROJECT:
            setCurrentProject((Project) null);
            return;
        case ProjectPackage.PROJECT_REGISTRY__PROJECTS:
            getProjects().clear();
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case ProjectPackage.PROJECT_REGISTRY__CURRENT_PROJECT:
            return currentProject != null;
        case ProjectPackage.PROJECT_REGISTRY__PROJECTS:
            return projects != null && !projects.isEmpty();
        }
        return super.eIsSet(featureID);
    }

    /**
     * @see org.locationtech.udig.IProjectRegistry#getDefaultIProject()
     */
    public IProject getDefaultIProject() {
        return getDefaultProject();
    }

    /**
     * @see org.locationtech.udig.IProjectRegistry#getCurrentIProject()
     */
    public IProject getCurrentIProject() {
        return getCurrentProject();
    }

    /**
     * @see org.locationtech.udig.IProjectRegistry#setICurrentProject(org.locationtech.udig.IProject)
     */
    public void setICurrentProject(IProject value) {
        setCurrentProject((Project) value);
    }

    /**
     * @see org.locationtech.udig.IProjectRegistry#getIProject(java.lang.String)
     */
    public IProject getIProject(String projectPath) {
        return getProject(projectPath);
    }

    /**
     * @see org.locationtech.udig.IProjectRegistry#getIProjects()
     */
    @SuppressWarnings("unchecked")
    public List getIProjects() {
        return getProjects();
    }

    private static ResourceSetImpl resourceSet;

    private static ProjectRegistry projectRegistry;

    /**
     * Loads the project registry.
     *
     * @return the loaded project registry.
     */
    @SuppressWarnings("unchecked")
    private synchronized static ProjectRegistry load() {
        ProjectRegistry projectRegistry = null;
        try {

            IPath registrypath = Platform.getLocation().append(".projectRegistry"); //$NON-NLS-1$
            URI uri = URI.createURI("file://" + registrypath.toOSString()); //$NON-NLS-1$

            projectRegistry = backwardsCompatibility(projectRegistry, registrypath, uri);

            if (projectRegistry == null) {

                // Load the resource through the editing domain.
                resourceSet = new ResourceSetImpl();

                if (registrypath.toFile().exists()) {
                    Resource resource = resourceSet.getResource(uri, true);
                    resourceSet.eSetDeliver(false);
                    projectRegistry = (ProjectRegistry) resource.getContents().get(0);
                    if (projectRegistry == null) {
                        projectRegistry = ProjectFactory.eINSTANCE.createProjectRegistry();
                        resource.getContents().add(projectRegistry);
                    }
                } else {
                    Resource resource = resourceSet.createResource(uri);
                    if (resource == null)
                        throw new Exception("Unable to load or create ProjectRegistry resource"); //$NON-NLS-1$

                    projectRegistry = new ProjectRegistryImpl();
                    resource.getContents().add(projectRegistry);
                }
            }
        } catch (Exception exception) {
            ProjectPlugin.INSTANCE.log(exception);
        }
        if (projectRegistry == null)
            ProjectPlugin.log("Error getting project from resource"); //$NON-NLS-1$
        else {
            final ProjectRegistry registry = projectRegistry;
            projectRegistry.eAdapters().add(new AdapterImpl() {
                /**
                 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
                 */
                @Override
                public void notifyChanged(Notification msg) {
                    if (msg.getFeatureID(
                            ProjectRegistry.class) == ProjectPackage.PROJECT_REGISTRY__PROJECTS) {
                        registry.eResource().setModified(true);
                    }
                }
            });
        }
        resourceSet.eSetDeliver(false);
        return projectRegistry;
    }

    /**
     * @param projectRegistry
     * @param registrypath
     * @param uri
     * @return
     */
    private static ProjectRegistry backwardsCompatibility(ProjectRegistry projectRegistry,
            IPath registrypath, URI uri) {
        IPath oldregistrypath = ProjectPlugin.getPlugin().getStateLocation()
                .append("ProjectRegistry"); //$NON-NLS-1$
        URI olduri = URI.createURI("file://" + oldregistrypath.toOSString()); //$NON-NLS-1$

        if (oldregistrypath.toFile().exists()) {
            resourceSet = new ResourceSetImpl();
            Resource resource = resourceSet.getResource(olduri, true);
            resourceSet.eSetDeliver(false);
            projectRegistry = (ProjectRegistry) resource.getContents().get(0);

            if (projectRegistry != null) {
                resourceSet = new ResourceSetImpl();
                resource = resourceSet.createResource(uri);
                resource.getContents().add(projectRegistry);
                oldregistrypath.toFile().deleteOnExit();
            }
        }
        return projectRegistry;
    }

    /**
     * @see org.locationtech.udig.project.internal.ProjectFactory#getProjectRegistry()
     * @uml.property name="projectRegistry"
     */
    public static ProjectRegistry getProjectRegistry() {
        synchronized (ProjectRegistry.class) {
            if (projectRegistry == null) {
                projectRegistry = load();
            }
            if (projectRegistry.eResource() == null
                    || projectRegistry.eResource().getResourceSet() != resourceSet
                    || resourceSet == null)
                throw new AssertionError();
        }
        return projectRegistry;
    }

    @Override
    public EList<Adapter> eAdapters() {
        return SynchronizedEList.create(super.eAdapters());
    }

} // ProjectRegistryImpl
