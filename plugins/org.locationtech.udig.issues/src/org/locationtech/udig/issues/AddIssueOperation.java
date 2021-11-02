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

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

import org.locationtech.udig.core.enums.Priority;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.issues.internal.IssuesActivator;
import org.locationtech.udig.issues.internal.Messages;
import org.locationtech.udig.issues.internal.view.IssuesView;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 * Adds selected features as issues to the issues list
 *
 * @author Jesse
 * @since 1.1.0
 */
public class AddIssueOperation implements IOp {

    @Override
    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        Object[] array = (Object[]) target;

        final InformationDialog dialog = openInformationDialog(display);

        if (dialog.getReturnCode() == Window.CANCEL) {
            return;
        }

        monitor.beginTask(Messages.AddIssueOperation_TaskName, array.length + 1);
        monitor.worked(1);

        Collection<IIssue> issues;
        if (array[0] instanceof Filter) {
            issues = addFeatureIssues((Filter[]) array, dialog, monitor);
        } else if (array[0] instanceof SimpleFeature) {
            issues = addFeatureIssues((SimpleFeature[]) array, dialog, monitor);
        }else{
        	issues = null;
        }
        if( issues != null ){
        	select(display,issues);
        }
    }

    private void select(Display display, final Collection<IIssue> issues) {
    	display.asyncExec(new Runnable() {
			@Override
            public void run() {
				IWorkbenchPage page = findPage();
				if( page!=null ){
					IViewPart view = page.findView(IssuesView.VIEW_ID);
					if (view instanceof IssuesView) {
						IssuesView issuesView = (IssuesView) view;
						issuesView.setSelection(new StructuredSelection(issues.toArray()));
					}
				}
			}
		});
	}

	private void showView() {
        IWorkbenchPage activePage = findPage();
        try {
        	if( activePage!=null ){
        		activePage.showView(IssuesView.VIEW_ID, null, IWorkbenchPage.VIEW_VISIBLE);
        	}
        } catch (PartInitException e) {
            LoggingSupport.log(IssuesActivator.getDefault(), "Error showing issues view",e); //$NON-NLS-1$
        }
    }

	private IWorkbenchPage findPage() {
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
        if (activeWorkbenchWindow == null) {
            return null;
        }
        IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
        if( activePage==null ){
            return null;
        }
		return activePage;
	}

    /**
     * @param display
     * @return
     */
    private InformationDialog openInformationDialog( final Display display ) {
        final InformationDialog[] dialog = new InformationDialog[1];
        PlatformGIS.syncInDisplayThread(display, new Runnable(){
            @Override
            public void run() {
                dialog[0] = new InformationDialog(display.getActiveShell());
                showView();
                dialog[0].open();
            }
        });
        return dialog[0];
    }

    private Collection<IIssue> addFeatureIssues( SimpleFeature[] features, InformationDialog dialog,
            IProgressMonitor monitor ) {

        Collection<IIssue> issues = new HashSet<>();

        for( SimpleFeature feature : features ) {
            if (feature instanceof IAdaptable) {
                IAdaptable adaptable = (IAdaptable) feature;
                ILayer layer = adaptable.getAdapter(ILayer.class);
                if (layer == null) {
                    LoggingSupport.log(IssuesActivator.getDefault(),
                                    "Couldn't adapt the feature to a layer so therefore couldn't add it as an issue"); //$NON-NLS-1$
                } else {
                    issues.add(addFeatureIssue(feature, layer, dialog));
                }
            } else {
                LoggingSupport.log(IssuesActivator.getDefault(),
                        "The feature is not adaptable and therefore a layor couldn't be determined for it.  " //$NON-NLS-1$
                                + "So it couldn't add it as an issue"); //$NON-NLS-1$
            }
            monitor.worked(1);
        }
        issues.remove(null);
        return issues;

    }

    private Collection<IIssue> addFeatureIssues( Filter[] filters, InformationDialog dialog,
            IProgressMonitor monitor ) throws IOException {
    	Collection<IIssue> issues = new HashSet<>();
        for( Filter filter : filters ) {
             FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = null;
            ILayer layer = null;
            if (filter instanceof IAdaptable) {
                IAdaptable adaptable = (IAdaptable) filter;
                layer = adaptable.getAdapter(ILayer.class);
                if (layer != null) {
                    featureSource = layer.getResource(FeatureSource.class, monitor);
                }
            }

            if (featureSource == null) {
                LoggingSupport.log(IssuesActivator.getDefault(),"SimpleFeature Source for filter: " + filter); //$NON-NLS-1$
            } else {
                FeatureCollection<SimpleFeatureType, SimpleFeature>  features = featureSource.getFeatures(filter);
                FeatureIterator<SimpleFeature> iter = features.features();
                try {
                    while( iter.hasNext() ) {
                        issues.add(addFeatureIssue(iter.next(), layer, dialog));
                    }
                } finally {
                    iter.close();
                }
            }
            monitor.worked(1);
        }
        issues.remove(null);
        return issues;
    }

    private IIssue addFeatureIssue( SimpleFeature feature, ILayer layer, InformationDialog dialog ) {
        if (feature == null) {
            LoggingSupport.log(IssuesActivator.getDefault(),"Can't construct an issue from a null feature!"); //$NON-NLS-1$
            return null;
        } else {
            String description = dialog.getDescription();
            Priority priority = dialog.getPriority();
            String groupId = dialog.getGroupId();
            FeatureIssue featureIssue = new FeatureIssue(priority, description, layer, feature,
			        groupId);
			IIssuesManager.defaultInstance.getIssuesList().add(
                    featureIssue);
			return featureIssue;
        }
    }

    private static class InformationDialog extends Dialog implements Listener {

        private Combo combo;
        private Text description;
        private Text groupId;
        private String descriptionText;
        private String groupIdText;
        private String priorityText;

        protected InformationDialog( Shell parentShell ) {
            super(parentShell);
            setShellStyle(SWT.APPLICATION_MODAL | SWT.RESIZE | SWT.DIALOG_TRIM);

            descriptionText = Messages.AddIssueOperation_DefaultDescription;
            groupIdText = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
            priorityText = Priority.WARNING.name();
        }

        @Override
        protected IDialogSettings getDialogBoundsSettings() {
            return IssuesActivator.getDefault().getDialogSettings();
        }

        @Override
        protected Control createDialogArea( Composite parent ) {
            getShell().setText(Messages.AddIssueOperation_DialogText);
            Composite comp = (Composite) super.createDialogArea(parent);
            comp.setLayout(new GridLayout(2, false));

            GridData data = (GridData) comp.getLayoutData();
            data.widthHint = 480;
            data.heightHint = 256;

            createPriorityWidgets(comp);

            createGroupIDWidgets(comp);

            createDescriptionWidgets(comp);

            createOperationDescription(comp);

            return comp;
        }

        private void createOperationDescription(Composite comp) {
        	Text about = new Text(comp, SWT.MULTI|SWT.BORDER|SWT.READ_ONLY|SWT.WRAP);
        	about.setText("A new task for each feature will be created.  Double clicking on the task will display the tasks feature.");
        	GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        	data.horizontalSpan=2;
            about.setLayoutData(data);
		}

		private void createDescriptionWidgets( Composite comp ) {
            createLabel(comp, Messages.AddIssueOperation_descriptionLabel);

            description = new Text(comp, SWT.SINGLE | SWT.BORDER);
            description.setToolTipText(Messages.AddIssueOperation_descriptionToolTip);
            description.setText(descriptionText);
            GridData createLayoutData = createLayoutData(true);
            description.setLayoutData(createLayoutData);
            description.addListener(SWT.Modify, this);
        }

        public void createGroupIDWidgets( Composite comp ) {
            createLabel(comp, Messages.AddIssueOperation_groupIdLabel);

            groupId = new Text(comp, SWT.SINGLE | SWT.BORDER);
            groupId.setToolTipText(Messages.AddIssueOperation_groupId);
            groupId.setText(groupIdText);
            groupId.setLayoutData(createLayoutData(true));
            groupId.addListener(SWT.Modify, this);
        }

        private void createPriorityWidgets( Composite comp ) {
            createLabel(comp, Messages.AddIssueOperation_PriorityLabel);

            Priority[] values = Priority.values();
            String[] priorities = new String[values.length];
            for( int i = 0; i < values.length; i++ ) {
                priorities[i] = values[i].name();
            }

            combo = new Combo(comp, SWT.READ_ONLY | SWT.BORDER);
            combo.setItems(priorities);
            combo.select(combo.indexOf(priorityText));
            combo.setLayoutData(createLayoutData(true));
            combo.addListener(SWT.Modify, this);
        }

        private void createLabel( Composite comp, String string ) {
            Label priorityLabel = new Label(comp, SWT.NONE);
            priorityLabel.setText(string);
            priorityLabel.setLayoutData(createLayoutData(false));
        }

        private static GridData createLayoutData( boolean fillHorizontal ) {
            return new GridData(SWT.FILL, SWT.TOP, fillHorizontal, false);
        }

        @Override
        public void handleEvent( Event event ) {
            if (event.widget == description) {
                descriptionText = description.getText();
            } else if (event.widget == groupId) {
                groupIdText = groupId.getText();
            } else if (event.widget == combo) {
                int selectionIndex = combo.getSelectionIndex();
                priorityText = combo.getItem(selectionIndex);
            }
        }

        public Priority getPriority() {
            return Priority.valueOf(priorityText);
        }

        public String getDescription() {
            return descriptionText;
        }

        public String getGroupId() {
            return groupIdText;
        }

    }

}
