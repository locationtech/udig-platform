/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.ui.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.ui.export.CatalogExport;
import net.refractions.udig.catalog.ui.export.CatalogExportWizard;
import net.refractions.udig.catalog.ui.export.Data;
import net.refractions.udig.catalog.ui.export.ExportResourceSelectionState;
import net.refractions.udig.catalog.ui.workflow.State;
import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.LayerFactory;
import net.refractions.udig.project.internal.StyleBlackboard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public final class MapSaveStrategy extends CatalogExport {
	private final ExportResourceSelectionState state;
	private MapEditorPart editor;

	public MapSaveStrategy(ExportResourceSelectionState state, MapEditorPart editor) {
		super(false);
		this.state = state;
		this.editor = editor;

		initWorkflow();
	}

	@Override
	protected Workflow createWorkflow() {
		final Workflow workflow = new Workflow(new State[] { state });

		// create the workflow for the export wizard
		return workflow;
	}

	@Override
	protected WorkflowWizard createWorkflowWizard(Workflow workflow,
			java.util.Map<Class<? extends State>, WorkflowWizardPageProvider> map) {
		CatalogExportWizard catalogExportWizard = new CatalogExportWizard(
				workflow, map) {
			@Override
			protected boolean performFinish(IProgressMonitor monitor) {
				boolean result = super.performFinish(monitor);
				replaceExportedLayers(state);
				try {
					editor.getMap().getEditManagerInternal()
							.commitTransaction();
					editor.setDirty(false);
				} catch (IOException e) {
					ProjectUIPlugin.log("failed committing transaction", e); //$NON-NLS-1$
					MessageDialog.openError(null, Messages.MapSaveStrategy_error_title, Messages.MapSaveStrategy_error_messages);
				}
				return result;
			}

			private void replaceExportedLayers(
					final ExportResourceSelectionState layerState) {
				List<Data> exportedLayers = layerState.getExportData();
				for (Data data : exportedLayers) {
					Collection<IGeoResource> exported = data
							.getExportedResources();
					replaceLayer(data.getResource(), exported);
				}
			}

			private void replaceLayer(IGeoResource resource,
					Collection<IGeoResource> exported) {
				List<Layer> layers = MapSaveStrategy.this.editor.getMap()
						.getLayersInternal();
				Layer found;
				do {
					found = null;
					for (Layer layer : layers) {
						if (URLUtils.urlEquals(layer.getID(), resource
								.getIdentifier(), false)) {
							found = layer;
							break;
						}
					}

					if (found != null) {
						layers.addAll(layers.indexOf(found), toLayers(found,
								exported));
						layers.remove(found);
					}
				} while (found != null);

			}

			private Collection<Layer> toLayers(Layer found,
					Collection<IGeoResource> exported) {
				LayerFactory layerFactory = MapSaveStrategy.this.editor
						.getMap().getLayerFactory();
				Collection<Layer> newLayers = new ArrayList<Layer>();

				for (IGeoResource exportedResource : exported) {
					try {
						Layer createLayer = layerFactory
								.createLayer(exportedResource);
						StyleBlackboard clone = (StyleBlackboard) found
								.getStyleBlackboard().clone();
						createLayer.setStyleBlackboard(clone);
						newLayers.add(createLayer);
					} catch (IOException e) {
						throw (RuntimeException) new RuntimeException()
								.initCause(e);
					}
				}

				return newLayers;
			}

		};
		catalogExportWizard.setSelectExportedInCatalog(false);
		return catalogExportWizard;
	}
	
}