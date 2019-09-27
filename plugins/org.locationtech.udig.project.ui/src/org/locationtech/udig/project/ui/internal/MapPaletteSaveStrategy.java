/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.catalog.ui.export.CatalogExport;
import org.locationtech.udig.catalog.ui.export.CatalogExportWizard;
import org.locationtech.udig.catalog.ui.export.Data;
import org.locationtech.udig.catalog.ui.export.ExportResourceSelectionState;
import org.locationtech.udig.catalog.ui.workflow.State;
import org.locationtech.udig.catalog.ui.workflow.Workflow;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizard;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerFactory;
import org.locationtech.udig.project.internal.StyleBlackboard;

/**
 * Save strategy for Map Palette
 * 
 * @author Jody Garnett
 * @since 1.3.0
 * @version 1.3.0
 */
public final class MapPaletteSaveStrategy extends CatalogExport {
	private final ExportResourceSelectionState state;
	private MapEditorWithPalette editor;

	public MapPaletteSaveStrategy(ExportResourceSelectionState state, MapEditorWithPalette editor) {
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
					MessageDialog.openError(editor.getSite().getShell(), Messages.MapSaveStrategy_error_title, Messages.MapSaveStrategy_error_messages);
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
				List<Layer> layers = MapPaletteSaveStrategy.this.editor.getMap()
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
				LayerFactory layerFactory = MapPaletteSaveStrategy.this.editor
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
