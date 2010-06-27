/**
 * LGPL
 */
package net.refractions.udig.project.ui.wizard.export.project;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.project.internal.Messages;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.impl.ProjectFactoryImpl;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Wizard for exporting a project, its maps and all associated data to a project.
 * 
 * @author Jesse
 */
public class ExportProjectWizard extends Wizard implements IExportWizard, IRunnableWithProgress {

    private String destinationDirectory = "";
    private IStructuredSelection selection;
    
    /** prompts the user about what to export; select projects to export, and directory to export */
    private ExportSelectionPage selectionPage;
    
    ImageDescriptor wizardPageIconDescriptor = 
        ProjectUIPlugin.imageDescriptorFromPlugin(ProjectUIPlugin.ID, "icons/wizban/exportproject_wiz.png");

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        destinationDirectory = selectionPage.getDestinationDirectory();
        try {
            // run in a background thread using this wizards progress bar
            getContainer().run(true, true, this);
        } catch (Exception e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
        return true;
    }

    @Override
    public boolean canFinish() {
        boolean canFinish = true;
        if(!selectionPage.isPageComplete()){
            canFinish = false;
        } 
        return canFinish;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle(Messages.ExportProjectWizard_Title);
        setNeedsProgressMonitor(true);
        selectionPage = new ExportSelectionPage(
        		Messages.ExportSelectionPage_Destination,
        		Messages.ExportProjectWizard_Destination2, wizardPageIconDescriptor);
        this.selection = selection;
    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(selectionPage);
    }
    /**
     * This is the method that actually does the export (called by performFinish)
     */
    public void run( IProgressMonitor monitor ) throws InvocationTargetException,
    InterruptedException {
        Project project = selectionPage.getProject();
        if( project == null ) return;
        
        monitor.beginTask(Messages.ExportProjectWizard_Exporting + project.getName(), 1 );

        // this represents the file we are going to write out to
        Resource resource = project.eResource();
        if( resource == null ){
            // this project has never been saved to a file yet; so we are in
            // a bit of trouble knowing how to write it out
            throw new NullPointerException("Project does not have a file");            
        }
        // destination ends up being a file path
        String destination = generateDestinationProjectFilePath(resource, project.getName());
        
        // creates a new resource (ie copy) and gathers everything into it
        Resource copy = collectAllAndCopyIntoDestResource(resource, destination);
        saveResource(copy);
        monitor.worked(1);

    }

    private String generateDestinationProjectFilePath( Resource resource, String name ) {
        URI origURI = resource.getURI();
        File file = new File(origURI.toFileString());
        return destinationDirectory + File.separator + name + ".udig" + File.separator + file.getName();
    }

    private void saveResource( Resource copy ) {
        try {
            copy.save(null);
        } catch (IOException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }

    /**
     * It looks like this method collects all the contents of the project (maps and pages and other)
     * and some how adds them to a single resource defined by the dest file path.
     * 
     * @param projectResource Contains the selected project we want to write out
     * @param dest the file path used to write out the resources
     * @return Resource used to store everything
     */
    @SuppressWarnings("unchecked")
    private Resource collectAllAndCopyIntoDestResource(Resource projectResource, String dest){
        // this is the contents of our project (ie maps and pages and other)
        // note these resources in the resource set (ie the raw form not objects)
        //
        ResourceSet resourceSet = projectResource.getResourceSet();
        
        // start with the project resource
        List<Resource> resources = gatherAllResourcesToList(projectResource);

        // let us make the new file to write out
        File destFile = new File(dest);
        String absoluteDestPath = destFile.getAbsolutePath();
        
        URI destURI = URI.createFileURI(absoluteDestPath);
        Resource destResource = resourceSet.createResource(destURI);
        
        Collection<EObject> collection = new ArrayList<EObject>();
        for(Resource curResource : resources){
            collection.addAll(curResource.getContents());
        }
        // here is where we actually do the copy!
        Collection<EObject> copyAll = EcoreUtil.copyAll(collection);
        destResource.getContents().addAll(copyAll);
        
        return destResource;
    }

    /**
     * We need a list of all the resources in a project so we can save them.
     * <p>
     * Handy tip - the project resource itself is one of the resources that must be included
     * in the list! (along with all the contents of the project).
     * 
     * @param projectResource
     * @return list of resources for projectResoruce
     */
    private List<Resource> gatherAllResourcesToList(Resource projectResource){
        List<Resource> resources = new ArrayList<Resource>();
        resources.add( projectResource );
        addReferencedResourcesToList( resources );
        
        return resources;
    }
    /**
     * Go through the provided list of resources; and tack any referenced resoruces
     * on to the end of the list recursively.
     * <p>
     * At the end of this method the resource list will contain an entry
     * for every resource.
     * 
     * @param resources
     */
    private void addReferencedResourcesToList( List<Resource> resources ) {
        for(int i = 0; i < resources.size(); i++){
            Resource r = resources.get(i);
            for(Iterator<EObject> j = r.getAllContents(); j.hasNext(); ){
                for(Object object : j.next().eCrossReferences()){
                    if (object instanceof EObject) {
                        EObject eObject = (EObject)object;
                        Resource otherResource = eObject.eResource();
                        if(otherResource != null && !resources.contains(otherResource)){
                            resources.add(otherResource);
                        }   
                    }
                }
            }
        }
    }
}
