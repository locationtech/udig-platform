/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.catalog.ui.workflow.DataSourceSelectionState;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPage;
import org.locationtech.udig.internal.ui.UiPlugin;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

/**
 * This is a wizard page which is used to select from a set of data sources.
 * <p>
 * Each data source is described an instance of
 * 
 * @see org.locationtech.udig.catalog.ui.UDIGImportPageDescriptor.
 *      </p>
 *      <p>
 *      Client code may instantiate objects of this class,but may only call
 *      setSelectedDescriptor(UDIGImportPageDescriptor). All other methods are
 *      wizard lifecycle methods.page
 *      </p>
 * @author Justin Deoliveira,Refractions Research Inc.,jdeolive@refractions.net
 */
public class DataSourceSelectionPage extends WorkflowWizardPage implements
		ISelectionChangedListener {

	/** the selected extension * */
	private IStructuredSelection selected;

	/** list of wizard page extension* */
	private List<UDIGConnectionFactoryDescriptor> descriptors;
	
	/** the viewer to select * */
	private WizardViewer viewer;

	public DataSourceSelectionPage() {
		super("dataSourceWizardPage"); //$NON-NLS-1$
		setTitle(Messages.DataSourceSelectionPage_pageTitle); 
		setDescription(Messages.DataSourceSelectionPage_defaultMessage);
		setImageDescriptor(CatalogUIPlugin.imageDescriptorFromPlugin(CatalogUIPlugin.ID, "icons/wizban/add_wiz.gif"));
	}

	/**
	 * Sets the selected set of import page descriptiors.
	 * 
	 * @param ids
	 *            A list of import page identifiers.
	 * 
	 */
	public void select(List<String> ids) {
		descriptors=ConnectionFactoryManager.instance().getConnectionFactoryDescriptors(ids);
		setSelection(descriptors);
	}

	/**
	 * Sets the selected import page descriptor.
	 * 
	 * @param id
	 *            The id of the import page to select.
	 */
	public void select(String id) {
		ArrayList<String> l = new ArrayList<String>();
		l.add(id);
		select(l);
	}

	/**
	 * Sets the selected import page descriptor.
	 * 
	 * @param descriptor
	 *            The descriptor to be selected.
	 */
	public void setSelection(List<UDIGConnectionFactoryDescriptor> descriptors) {
	    selected = new StructuredSelection(descriptors);
		if (viewer != null) {
			// only set first in viewer
			if (!selected.isEmpty()) {
				viewer.setSelection(new StructuredSelection(selected
						.getFirstElement()));
			} else {
				viewer.setSelection(new StructuredSelection());
			}
		}
	}

	@Override
	public boolean canFlipToNextPage() {
		boolean more = super.canFlipToNextPage();
		if (more) {
			// if selection set, we are done
			if (selected != null && selected.size() == 1) {
				return true;
			}

			// if there is only one choice, we are also done
			if (descriptors != null && descriptors.size() == 1){
				return true;
			}
			return false;
		}

		return false;
	}

	@Override
	public DataSourceSelectionState getState() {
	    return (DataSourceSelectionState) super.getState();
	}
	
	@Override
	public void shown() {
	    this.selected = (IStructuredSelection) viewer.getSelection();
        syncStateWithUI();
	}
	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new FillLayout());
		
        DataSourceSelectionState state = (DataSourceSelectionState) getState();
        List<UDIGConnectionFactoryDescriptor> descriptorList;
        if (state.getShortlist() != null) {
            descriptorList = state.getShortlist();
        }
        else {
            descriptorList = getDescriptors();
        }
        
		viewer = new WizardViewer(comp, SWT.SINGLE | SWT.BORDER);
		viewer.setInput(descriptorList.toArray());
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IWizardContainer container = getContainer();
				if (container instanceof Dialog) {
					Dialog d = (Dialog) container;
					Button button = findButton(d.buttonBar,
							IDialogConstants.NEXT_ID);
					if (button != null)
						button.notifyListeners(SWT.Selection, new Event());
				}
			}
		});
		viewer.addSelectionChangedListener(this);

		// check the state for an initial selection
		if (state.getDescriptor() != null){
			viewer.setSelection(new StructuredSelection(state.getDescriptor()));
		}
		setControl(comp);
	}

	protected Button findButton(Control buttonBar, int buttonID) {
		if (buttonBar instanceof Composite) {
			Composite composite = (Composite) buttonBar;
			Control[] children = composite.getChildren();
			for (Control control : children) {
				if (control instanceof Button) {
					Button button = (Button) control;
					if (((Integer) button.getData()).intValue() == buttonID)
						return button;
				} else if (control instanceof Composite) {
					Button button = findButton(control, buttonID);
					if (button != null)
						return button;
				}
			}
		}
		if (buttonBar instanceof Button) {
			Button button = (Button) buttonBar;
			if (((Integer) button.getData()).intValue() == buttonID)
				return button;
		}

		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		selected = (IStructuredSelection) event.getSelection();
		syncStateWithUI();
	}

    private void syncStateWithUI() {
        if (selected != null && !selected.isEmpty()) {
			UDIGConnectionFactoryDescriptor descriptor = (UDIGConnectionFactoryDescriptor) selected
					.getFirstElement();

			// change the page description + image
			setImageDescriptor(descriptor.getDescriptionImage(0));
			setMessage(descriptor.getDescription(0));

			// update the underlying state
			DataSourceSelectionState state = getState();
			state.setDescriptor(descriptor);
		}

		// update the container buttons
		getWizard().getContainer().updateButtons();
    }

	public Viewer getViewer() {
		return viewer;
	}

	public List<UDIGConnectionFactoryDescriptor> getDescriptors() {
        List<UDIGConnectionFactoryDescriptor> connectionFactoryDescriptors = ConnectionFactoryManager.instance().getConnectionFactoryDescriptors();
        return connectionFactoryDescriptors;
	}	
	
	protected void saveSelectedDescriptor() {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		if (selection.isEmpty())
			return;
	}

	private static class WizardViewer extends TableViewer {
		public WizardViewer(Composite parent, int style) {
			super(parent, style);

			setContentProvider(new ArrayContentProvider());
			setLabelProvider(new LabelProvider() {
				public String getText(Object object) {
					UDIGConnectionFactoryDescriptor descriptor = (UDIGConnectionFactoryDescriptor) object;
				    return descriptor.getLabel(0);
				}

				public Image getImage(Object object) {
					UDIGConnectionFactoryDescriptor descriptor = (UDIGConnectionFactoryDescriptor) object;

					String id = descriptor.getId();
					ImageRegistry registry = UiPlugin.getDefault()
							.getImageRegistry();
					ImageDescriptor image = descriptor.getImage(0);
					synchronized (registry) {
					    if (registry.get(id) == null && image != null) {
					        registry.put(id, image);
					    }

					return registry.get(id);
					}
				}
			});
		}
	}
}
