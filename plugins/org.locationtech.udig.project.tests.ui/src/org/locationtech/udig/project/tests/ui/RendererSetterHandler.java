/* uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2017, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.project.tests.ui;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.HandlerUtil;
import org.geotools.util.Range;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IStyleBlackboard;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.render.MultiLayerRenderer;
import org.locationtech.udig.project.internal.render.RenderContext;
import org.locationtech.udig.project.internal.render.impl.RenderMetricsSorter;
import org.locationtech.udig.project.internal.render.impl.RendererCreatorImpl;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.ApplicationGISInternal;


/**
 * Test handler that provides a way to check behavior of {@link RenderMetricsSorter}.
 * 
 * @author Nikolaos Pringouris <nprigour@gmail.com>
 *
 */
public class RendererSetterHandler extends AbstractHandler {


    /* (non-Javadoc)
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {


        final Shell shell= HandlerUtil.getActiveShell(event);

        final RendererControlDialog dialog = new RendererControlDialog(shell);

        shell.getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                dialog.open();
            }

        });

        return null;
    }

    /**
     * Helper class to compute rate of an {@link AbstractRenderMetrics} metric. 
     * see also {@link RenderMetricsSorter}
     * 
     * @author Nikolaos Pringouris <nprigour@gmail.com>
     *
     */
    private class RenderMetricsSorterExt extends RenderMetricsSorter {

        public RenderMetricsSorterExt(List<Layer> layers) {
            super(layers);
            // TODO Auto-generated constructor stub
        }

        protected double rate( AbstractRenderMetrics metrics ) {
            ILayer layer = metrics.getRenderContext().getLayer();
            final IStyleBlackboard style = layer.getStyleBlackboard();

            //render metrics - 
            //guarenteed to be between 0 & 1 - higher better so we subtract one to get make lower better
            double renderAppearanceMetric = 1 - metrics.getUserAppearanceMetric(style);
            double userAppearanceMetric = 1 - metrics.getRenderAppearanceMetric(style);

            double latencyMetric = metrics.getLatencyMetric();
            latencyMetric = latencyMetric / MAXIMUM_LATENCY;

            double drawingTimeMetric = metrics.getDrawingTimeMetric();
            drawingTimeMetric = drawingTimeMetric / MAXIMUM_DRAWINGTIME;

            // resolution metric - close to 1 the better
            //worst case is the screen size
            double width = metrics.getRenderContext().getMapDisplay().getWidth();
            double resolutionMetric = metrics.getResolutionMetric();
            double diff = Math.abs(resolutionMetric - 1);
            if (diff == 0){
                resolutionMetric = 0; //perfect match (lower is better)
            }else{
                resolutionMetric = (diff / width);  //lower number 
            }

            int multiLayerMetric = 1 - rateMultiLayerRenderer(metrics);
            double scaleRangeMetric = 1 - rateScaleRange(metrics);

            double rating = renderAppearanceMetric * WEIGHT_RENDER_APPEARANCE_METRIC;
            rating += userAppearanceMetric * WEIGHT_USER_APPEARANCE_METRIC;
            rating += latencyMetric * WEIGHT_LATENCY_METRIC;
            rating += drawingTimeMetric * WEIGHT_DRAWING_TIME_METRIC;
            rating += resolutionMetric * WEIGHT_RESOLUTION_METRIC;
            rating += multiLayerMetric * WEIGHT_MULTILAYER_METRIC;
            rating += scaleRangeMetric * WEIGHT_SCALERANGE_METRIC;

            return rating;
        }

        private int rateScaleRange( AbstractRenderMetrics metrics ) {
            Set<Range<Double>> scales =new HashSet<Range<Double>>(); 
            try {
                scales =metrics.getValidScaleRanges();
            } catch (Exception e) {
                //
            }
            for( Range<Double> range : scales ) {
                if( range.contains(metrics.getRenderContext().getViewportModel().getScaleDenominator()) ){
                    return 1;
                }
            }
            return 0;
        }

        private int rateMultiLayerRenderer( AbstractRenderMetrics metrics ) {
            if (MultiLayerRenderer.class.isAssignableFrom(metrics.getRenderMetricsFactory()
                    .getRendererType())) {
                int indexOf = getLayers().indexOf(metrics.getRenderContext().getLayer());
                if( indexOf>0 )
                    if( metrics.canAddLayer(getLayers().get(indexOf-1)) )
                        return 1;
                if( indexOf<getLayers().size()-1 )
                    if( metrics.canAddLayer(getLayers().get(indexOf+1)) )
                        return 1;
            }
            return 0;
        }
    }

    /**
     * Provides a Dialog that enables setting/resetting of preferred Renderer
     * and displays info of available metrics ratings.
     *  
     * @author Nikolaos Pringouris <nprigour@gmail.com>
     *
     */
    private class RendererControlDialog extends TitleAreaDialog {

        private Text paramsHolder;

        private ComboViewer layerCombo;
        private ComboViewer rendererCombo;

        private Button mapPreferredRendererButton;
        private Button layerPreferredRendererButton;
        private Button mapLastResortRendererButton;
        private Button layerLastResortRendererButton;

        private RenderMetricsSorterExt sorter;
        private Text outputText;

        /**
         * @param parentShell
         * @param emf 
         */
        protected RendererControlDialog(Shell parentShell) {
            super(parentShell);
            setShellStyle(SWT.RESIZE|SWT.DIALOG_TRIM|SWT.CLOSE);
        }


        /* (non-Javadoc)
         * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected Control createDialogArea(Composite parent) {
            Composite container = (Composite) super.createDialogArea(parent);
            container.setLayout(new GridLayout(3, false));

            //COMBO VIEWER for layers SELECTION
            layerCombo = new ComboViewer(container, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
            layerCombo.setContentProvider(ArrayContentProvider.getInstance());

            layerCombo.setInput(ApplicationGIS.getActiveMap().getMapLayers());
            layerCombo.addSelectionChangedListener(new ISelectionChangedListener() {

                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    Object selection = ((IStructuredSelection)event.getSelection()).getFirstElement();
                    if (selection instanceof Layer) {
                        Layer layer = (Layer)selection;
                        sorter = new RenderMetricsSorterExt(Collections.singletonList(layer));
                        outputText.setText(printRendererInfo(layer));	

                        Collection<AbstractRenderMetrics> metrics = getApplicableRendererMetrics(layer);
                        rendererCombo.setInput(metrics);
                        
                        RenderContext context = layer.getMapInternal().getRenderManagerInternal().getRendererCreator().getRenderContext(layer);
                        IRenderer rend = layer.getMapInternal().getRenderManagerInternal().getRendererCreator().getRenderer(context);
                        for (AbstractRenderMetrics metric : metrics) {
                            if (metric.getRenderMetricsFactory().getRendererType() == rend.getClass()) {
                                rendererCombo.setSelection(new StructuredSelection(metric));
                            }
                        }
                    }
                }
            });
            layerCombo.getControl().addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(FocusEvent e) {
                    layerCombo.setInput(ApplicationGIS.getActiveMap().getMapLayers());
                }

            });
            layerCombo.getControl().setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1));


            //COMBO VIEWER for renderer SELECTION
            rendererCombo = new ComboViewer(container, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
            rendererCombo.setContentProvider(ArrayContentProvider.getInstance());
            rendererCombo.addSelectionChangedListener(new ISelectionChangedListener() {

                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    Layer layer = (Layer) ((IStructuredSelection)layerCombo.getSelection()).getFirstElement();
                    AbstractRenderMetrics rendererMetric = (AbstractRenderMetrics) ((IStructuredSelection)rendererCombo.getSelection()).getFirstElement();

                    if (layer != null) {
                        /*
						if (mapPreferredRendererButton.getSelection()) {
							ApplicationGISInternal.getActiveMap().getBlackBoardInternal().putString(RendererCreatorImpl.PREFERRED_RENDERER_ID, rendererMetric.getId());
						}
						if (layerPreferredRendererButton.getSelection()) {
							layer.getStyleBlackboard().putString(RendererCreatorImpl.PREFERRED_RENDERER_ID, rendererMetric.getId());
						}
                         */
                        outputText.setText(printRendererInfo(layer));	
                        RendererControlDialog.this.setMessage("A close and reopen of the Map is needed in order changes to apply", MessageDialog.WARNING);
                    }

                }
            });
            rendererCombo.getControl().setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1));
            ((GridData)rendererCombo.getControl().getLayoutData()).minimumWidth = 500;
            rendererCombo.setLabelProvider(new LabelProvider() {

                @Override
                public String getText(Object element) {
                    if (element instanceof AbstractRenderMetrics) {
                        return ((AbstractRenderMetrics) element).getId();
                    }
                    return "";
                }

            });
            mapPreferredRendererButton = new Button(container, SWT.CHECK|SWT.BORDER);
            mapPreferredRendererButton.setText("set as Map preferred");
            mapPreferredRendererButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1));
            mapPreferredRendererButton.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    Layer layer = (Layer) ((IStructuredSelection)layerCombo.getSelection()).getFirstElement();
                    AbstractRenderMetrics rendererMetric = (AbstractRenderMetrics) ((IStructuredSelection)rendererCombo.getSelection()).getFirstElement();
                    if (mapPreferredRendererButton.getSelection()) {
                        ApplicationGISInternal.getActiveMap().getBlackBoardInternal().putString(RendererCreatorImpl.PREFERRED_RENDERER_ID, rendererMetric.getId());
                    } else {
                        ApplicationGISInternal.getActiveMap().getBlackBoardInternal().remove(RendererCreatorImpl.PREFERRED_RENDERER_ID);
                    }
                    RendererControlDialog.this.setErrorMessage("A close and reopen of the Map is needed in order changes to apply");
                    outputText.setText(printRendererInfo(layer));	
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    // TODO Auto-generated method stub

                }

            });

            layerPreferredRendererButton = new Button(container, SWT.CHECK|SWT.BORDER);
            layerPreferredRendererButton.setText("set as layer preferred");
            layerPreferredRendererButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1));
            layerPreferredRendererButton.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    Layer layer = (Layer) ((IStructuredSelection)layerCombo.getSelection()).getFirstElement();
                    AbstractRenderMetrics rendererMetric = (AbstractRenderMetrics) ((IStructuredSelection)rendererCombo.getSelection()).getFirstElement();
                    if (layerPreferredRendererButton.getSelection()) {
                        layer.getStyleBlackboard().putString(RendererCreatorImpl.PREFERRED_RENDERER_ID, rendererMetric.getId());
                    } else {
                        layer.getStyleBlackboard().remove(RendererCreatorImpl.PREFERRED_RENDERER_ID);
                    }
                    RendererControlDialog.this.setErrorMessage("A close and reopen of the Map is needed in order changes to apply");
                    outputText.setText(printRendererInfo(layer));	
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    // TODO Auto-generated method stub

                }

            });

            mapLastResortRendererButton = new Button(container, SWT.CHECK|SWT.BORDER);
            mapLastResortRendererButton.setText("set as Map last resort");
            mapLastResortRendererButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1));
            mapLastResortRendererButton.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    Layer layer = (Layer) ((IStructuredSelection)layerCombo.getSelection()).getFirstElement();
                    AbstractRenderMetrics rendererMetric = (AbstractRenderMetrics) ((IStructuredSelection)rendererCombo.getSelection()).getFirstElement();
                    if (mapLastResortRendererButton.getSelection()) {
                        ApplicationGISInternal.getActiveMap().getBlackBoardInternal().putString(RendererCreatorImpl.LAST_RESORT_RENDERER_ID, rendererMetric.getId());
                    } else {
                        ApplicationGISInternal.getActiveMap().getBlackBoardInternal().remove(RendererCreatorImpl.LAST_RESORT_RENDERER_ID);
                    }
                    RendererControlDialog.this.setErrorMessage("A close and reopen of the Map is needed in order changes to apply");
                    outputText.setText(printRendererInfo(layer));	
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    // TODO Auto-generated method stub

                }

            });

            layerLastResortRendererButton = new Button(container, SWT.CHECK|SWT.BORDER);
            layerLastResortRendererButton.setText("set as layer last resort");
            layerLastResortRendererButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1));
            layerLastResortRendererButton.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    Layer layer = (Layer) ((IStructuredSelection)layerCombo.getSelection()).getFirstElement();
                    AbstractRenderMetrics rendererMetric = (AbstractRenderMetrics) ((IStructuredSelection)rendererCombo.getSelection()).getFirstElement();
                    if (layerLastResortRendererButton.getSelection()) {
                        layer.getStyleBlackboard().putString(RendererCreatorImpl.LAST_RESORT_RENDERER_ID, rendererMetric.getId());
                    } else {
                        layer.getStyleBlackboard().remove(RendererCreatorImpl.LAST_RESORT_RENDERER_ID);
                    }
                    RendererControlDialog.this.setErrorMessage("A close and reopen of the Map is needed in order changes to apply");
                    outputText.setText(printRendererInfo(layer));	
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    // TODO Auto-generated method stub

                }

            });

            Button clearButton = new Button(container, SWT.PUSH|SWT.BORDER);
            clearButton.setText("clear blackboard values");
            clearButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1));
            clearButton.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    Layer layer = (Layer) ((IStructuredSelection)layerCombo.getSelection()).getFirstElement();
                    layer.getStyleBlackboard().remove(RendererCreatorImpl.LAST_RESORT_RENDERER_ID);
                    layer.getStyleBlackboard().remove(RendererCreatorImpl.PREFERRED_RENDERER_ID);
                    ApplicationGISInternal.getActiveMap().getBlackBoardInternal().remove(RendererCreatorImpl.LAST_RESORT_RENDERER_ID);
                    ApplicationGISInternal.getActiveMap().getBlackBoardInternal().remove(RendererCreatorImpl.PREFERRED_RENDERER_ID);
                    RendererControlDialog.this.setErrorMessage("A close and reopen of the Map is needed in order changes to apply");
                    outputText.setText(printRendererInfo(layer));   
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    // TODO Auto-generated method stub

                }

            });

            outputText = new Text(container, SWT.BORDER|SWT.MULTI|SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
            outputText.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, true, 3, 1));
            ((GridData)outputText.getLayoutData()).heightHint = 80;

            return container;
        }

        /**
         * Return the initial size of the dialog.
         */
        @Override
        protected Point getInitialSize() {
            return new Point(1000, 600);
        }


        private String printRendererInfo(Layer layer) {
            if (layer == null) {
                return "";
            }
            StringBuffer buf = new StringBuffer();
            buf.append("MAP Blackboard-->" + RendererCreatorImpl.PREFERRED_RENDERER_ID + "= " + ApplicationGIS.getActiveMap().getBlackboard().get(RendererCreatorImpl.PREFERRED_RENDERER_ID)).append("\n");
            buf.append("MAP Blackboard-->" + RendererCreatorImpl.LAST_RESORT_RENDERER_ID + "= " + ApplicationGIS.getActiveMap().getBlackboard().get(RendererCreatorImpl.LAST_RESORT_RENDERER_ID)).append("\n");
            buf.append("\n");
            buf.append("Render Info for Layer " + layer).append("\n");
            buf.append("----------------------------------").append("\n");
            buf.append("Layer Blackboard-->" + RendererCreatorImpl.PREFERRED_RENDERER_ID + ": " + layer.getStyleBlackboard().get(RendererCreatorImpl.PREFERRED_RENDERER_ID)).append("\n");
            buf.append("Layer Blackboard-->" + RendererCreatorImpl.LAST_RESORT_RENDERER_ID + ": " + layer.getStyleBlackboard().get(RendererCreatorImpl.LAST_RESORT_RENDERER_ID)).append("\n");

            RenderContext context = layer.getMapInternal().getRenderManagerInternal().getRendererCreator().getRenderContext(layer);
            buf.append("Selected Renderer of layer: " + layer + " " +
                    layer.getMapInternal().getRenderManagerInternal().getRendererCreator().getRenderer(context)).append("\n").append("\n");
            buf.append("APPLICABLE RendererMetrics FOR LAYER :" + layer + ":\n ");
            Collection<AbstractRenderMetrics>  metrics = getApplicableRendererMetrics(layer);
            int order = 1;
            for (AbstractRenderMetrics metric : metrics) {
                buf.append(order++).append(".  ").append(metric).append(" --> (rate:").append(sorter.rate(metric)).append(")\n");
            }
            buf.append("----------------------------------").append("\n");
            buf.append("\n Remaining RendererMetrics (NOT APPLICABLE for :" + layer + "):\n ");
            metrics = getNotApplicableRendererMetrics(layer);
            for (AbstractRenderMetrics metric : metrics) {
                buf.append("-  ").append(metric).append(" --> (rate:").append(sorter.rate(metric)).append(")\n");
            }
            buf.append("----------------------------------").append("\n");
            return buf.toString();
        }
        
        /**
         * 
         * @param layer
         */
        private Collection<AbstractRenderMetrics> getApplicableRendererMetrics(Layer layer) {
            return getRendererMetrics(layer, true);
        }

        /**
         * 
         * @param layer
         */
        private Collection<AbstractRenderMetrics> getNotApplicableRendererMetrics(Layer layer) {
            return getRendererMetrics(layer, false);
        }
        
        /**
         * 
         * @param layer
         */
        private Collection<AbstractRenderMetrics> getRendererMetrics(Layer layer, boolean applicable) {
            Collection<AbstractRenderMetrics> result = new ArrayList<AbstractRenderMetrics>();
            RenderContext context = layer.getMapInternal().getRenderManagerInternal().getRendererCreator().getRenderContext(layer);
            
            Collection<AbstractRenderMetrics>  metrics = layer.getMapInternal().getRenderManagerInternal().getRendererCreator().getAvailableRendererMetrics(layer);
            int order = 1;
            for (AbstractRenderMetrics metric : metrics) {
                try {
                    boolean canRender = metric.getRenderMetricsFactory().canRender(context);
                    if (canRender == applicable) {
                        result.add(metric);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
    }
}
