/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.printing.ui.internal.editor;

import org.locationtech.udig.core.AdapterUtil;
import org.locationtech.udig.printing.model.BoxPrinter;
import org.locationtech.udig.printing.ui.IBoxEditAction;
import org.locationtech.udig.printing.ui.internal.PrintingPlugin;
import org.locationtech.udig.printing.ui.internal.editor.parts.PrintingEditPolicy;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;

/**
 * An action that is added to a PageEditor. Each BoxAction corresponds to a editAction in a box
 * extension point.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class BoxAction extends SelectionAction {

    private IConfigurationElement editActionElement;
    Request request;
    private String acceptable;
    private Class<? extends BoxPrinter> acceptableClass;
    private IBoxEditAction editAction;

    public BoxAction( IWorkbenchPart part, IConfigurationElement editActionElement2,
            String acceptable ) {
        super(part);
        this.editActionElement = editActionElement2;
        this.acceptable = acceptable;

        setText(editActionElement2.getAttribute("name")); //$NON-NLS-1$
        String attribute = editActionElement2.getAttribute("image");//$NON-NLS-1$
        ImageDescriptor image; 
        if( attribute!=null && attribute.trim().length()!=0){
            image = PrintingPlugin.imageDescriptorFromPlugin(editActionElement2
                    .getNamespaceIdentifier(), attribute);
            setImageDescriptor(image);
        }
        final String id = editActionElement2.getNamespaceIdentifier()+"."+editActionElement2.getAttribute("id"); //$NON-NLS-1$ //$NON-NLS-2$
        setId( id ); 

        request = new Request(){
            String type=id;
            @Override
            public Object getType() {
                return type;
            }
        }; 
    }

    public BoxAction( BoxAction action ) {
        super(action.getWorkbenchPart());
        setText(action.getText());
        setToolTipText(action.getToolTipText());
        setId(action.getId());
        setActionDefinitionId(action.getActionDefinitionId());
        this.acceptable=action.acceptable;
        this.acceptableClass=action.acceptableClass;
        this.editActionElement=action.editActionElement;
        this.request=action.request;
    }
    
    @Override
    protected boolean calculateEnabled() {
        if (getSelectedObjects().size() == 1 && (getSelectedObjects().get(0) instanceof EditPart)) {
            EditPart part = (EditPart) getSelectedObjects().get(0);
            return part.understandsRequest(request);
        }
        return false;
    }

    @Override
    public void run() {
        try {
            EditPart part = (EditPart) getSelectedObjects().get(0);
            part.performRequest(request);
        } catch (ClassCastException e) {
            PrintingPlugin.log("", e); //$NON-NLS-1$
        } catch (IndexOutOfBoundsException e) {
            PrintingPlugin.log("", e); //$NON-NLS-1$
        }
    }

    /**
     * Returns true if the action can be run on the printer
     *
     * @param printer the printer being tested
     * @return true if the action can be run on the printer
     */
    @SuppressWarnings("unchecked")
    public synchronized boolean isAcceptableBoxPrinter( BoxPrinter printer) {
        if (acceptableClass == null) {
            try {
                // if the printer can adapt to the class then it should be possible to constructs the acceptableClass
                // using its classloader.  If it can't... well then it must not be an acceptable printer
                acceptableClass = (Class< ? extends BoxPrinter>) Class.forName(acceptable, true, printer.getClass().getClassLoader());
            } catch (Exception e) {
                PrintingPlugin.log("Couldn't instantiate the editAction acceptable action. Verify the class exists", e); //$NON-NLS-1$
                return false;
            }
        }
        return acceptableClass.isAssignableFrom(printer.getClass()) || AdapterUtil.instance.canAdaptTo(printer, acceptableClass);
    }

    public synchronized EditPolicy getEditPolicy() {
        PrintingEditPolicy editPolicy = new PrintingEditPolicy(new BoxAction(this));
        return editPolicy;
    }

    public Request getRequest() {
        return request;
    }

    public synchronized IBoxEditAction getBoxEditAction() {
        if( editAction==null ){
            try{
                editAction=(IBoxEditAction) editActionElement.createExecutableExtension("class"); //$NON-NLS-1$
            }catch (Exception e) {
                PrintingPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, 
                        editActionElement.getNamespaceIdentifier(), IStatus.OK, "", e)); //$NON-NLS-1$
            }
        }
        return editAction;
    }
    
    
    @Override
    public String toString() {
        return "BoxAction for: "+acceptable+" boxes";  //$NON-NLS-1$//$NON-NLS-2$
    }
}
