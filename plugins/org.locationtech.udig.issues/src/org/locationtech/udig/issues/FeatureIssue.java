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
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.factory.GeoTools;
import org.locationtech.udig.core.enums.Priority;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.issues.internal.Messages;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IProject;
import org.locationtech.udig.project.command.NavCommand;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.ApplicationGISInternal;
import org.locationtech.udig.project.ui.internal.FeatureEditorLoader;
import org.locationtech.udig.project.ui.internal.MapEditorInput;
import org.locationtech.udig.project.ui.internal.MapEditorWithPalette;
import org.locationtech.udig.project.ui.internal.tool.ToolContext;
import org.locationtech.udig.ui.ProgressManager;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Represents a problem or issue with a feature. The map containing the feature and the
 * FeatureEditor will both be show. The map will be zoomed to show the feature.
 * 
 * @author jones
 * @since 1.0.0
 */
public class FeatureIssue extends AbstractIssue {
	public static final String EXT_ID="org.locationtech.udig.issues.featureIssue"; //$NON-NLS-1$
    private static final String MAP_KEY = "map"; //$NON-NLS-1$
    private static final String LAYER_KEY = "layer"; //$NON-NLS-1$
    private static final String PROJECT_KEY = "project"; //$NON-NLS-1$
    private static final String FEATURE_KEY = "feature"; //$NON-NLS-1$
    
    private SimpleFeature feature;
    private String viewid;
    private FeatureEditorLoader featureEditorLoader;
	private ILayer layer;
    private String featureID;
    private String mapID;
    private String layerID;
    private String projectID;
    /**
     * ONLY USE THIS FOR TESTING!!!!!!!
     */
    private static boolean testing=false;

    public FeatureIssue( ){
        
    }
    
    public FeatureIssue( Priority priority, String description, ILayer containingLayer, SimpleFeature feature, String groupId ) {
    	assert groupId!=null && priority!=null && containingLayer!=null && feature!=null;
    	
        setPriority(priority);
        setDescription(description);
        this.layer=containingLayer;
        this.feature = feature;
		featureEditorLoader = ApplicationGISInternal.getFeatureEditorLoader(feature);
		viewid = featureEditorLoader.getViewId();
        
		setGroupId(groupId);
        setBounds(new ReferencedEnvelope(feature.getBounds()));
    }
    
    @Override
    public void setId( String id ) {
        super.setId(id);
    }
    
    @Override
    public String getEditorID() {
    	return MapEditorWithPalette.ID;
    }
    
    @Override
    public IEditorInput getEditorInput() {
        return new MapEditorInput(getLayer().getMap());
    }

    @Override
    public String getViewPartId() {
        if( viewid==null ){
            
        }
        return viewid;
    }

    public String getProblemObject() {
    	SimpleFeature feature = getFeature();
    	SimpleFeatureType featureType = feature.getFeatureType();
    	String text = null;
    	text = getAttribute(feature, featureType, Messages.FeatureIssue_attributeName);
		if( text==null ){
			text = getAttribute(feature, featureType, Messages.FeatureIssue_idAttempt1);
		}
		if( text==null ){
			text = getAttribute(feature, featureType, Messages.FeatureIssue_idAttempt2);
		}
		if( text==null ){
			text = feature.getID();
		}

        return text;
    }

	private String getAttribute(SimpleFeature feature, SimpleFeatureType featureType,
			String attName) {
		int attributeIndex = featureType.indexOf(attName); 
    	if ( attributeIndex!=-1 ){
    		Object attribute = feature.getAttribute(attributeIndex);
			return attribute.toString();
    	}
    	return null;
	}

    public void fixIssue( IViewPart part, IEditorPart editor ) {
    	if( getLayer() == null ){
    		Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					Shell parent = Display.getCurrent().getActiveShell();
					String title = Messages.FeatureIssue_DialogText;
					String message = Messages.FeatureIssue_DialogMessage;
					MessageDialog.openInformation(parent, title, message);
					
				}
			});
    		return;
    	}
        final ToolContext context = ApplicationGISInternal.createContext(getLayer().getMap());
        final CoordinateReferenceSystem crs=getLayer().getCRS( );
        ReferencedEnvelope bounds = new ReferencedEnvelope(getFeature().getBounds());
        double deltax=bounds.getWidth()/4;
        double deltay=bounds.getHeight()/4;
        bounds.expandToInclude(bounds.getMinX()-deltax, bounds.getMinY()-deltay);
        bounds.expandToInclude(bounds.getMaxX()+deltax, bounds.getMaxY()+deltay);
        UndoableComposite composite = new UndoableComposite();
        IAction tool = ApplicationGIS.getToolManager().getToolAction("org.locationtech.udig.tools.selectionTool", "org.locationtech.udig.tool.edit.edit"); //$NON-NLS-1$ //$NON-NLS-2$
        // could be null if tool.edit plug-in is not in distribution.
        if( tool !=null ){
        	tool.run();
        }
        NavCommand zoom = context.getNavigationFactory().createSetViewportBBoxCommand(
                bounds, crs);
        context.sendASyncCommand(zoom);
        composite.getCommands().add(context.getSelectionFactory().createFIDSelectCommand(getLayer(),getFeature()));
        composite.getCommands().add(context.getEditFactory().createSetEditFeatureCommand(getFeature(), getLayer()));
        context.sendASyncCommand(composite);
    }

	public String getExtensionID() {
		return EXT_ID;
	}

    public void init( IMemento memento, IMemento viewMemento, String issueId, String groupId, ReferencedEnvelope bounds ) {
        if( !testing || memento!=null ){
            mapID=memento.getString(MAP_KEY);
            layerID=memento.getString(LAYER_KEY);
            projectID=memento.getString(PROJECT_KEY);
            featureID=memento.getString(FEATURE_KEY);
        }
        setViewMemento(viewMemento);
        setId(issueId);
        setGroupId(groupId);
        setBounds(bounds);
    }

    public void save( IMemento memento ) {
        memento.putString(MAP_KEY, getLayer().getMap().getID().toString());
        memento.putString(LAYER_KEY, getLayer().getID().toString());
        memento.putString(PROJECT_KEY, getLayer().getMap().getProject().getID().toString());
        memento.putString(FEATURE_KEY, getFeature().getID());
    }

    private SimpleFeature getFeature() {
        if( feature==null ){
            ILayer layer=getLayer();
             FeatureSource<SimpleFeatureType, SimpleFeature> featureSource;
            try {
                featureSource = layer.getResource(FeatureSource.class, ProgressManager.instance().get());
                FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
				Id id2 = filterFactory.id(FeatureUtils.stringToId(filterFactory,featureID));
				FeatureCollection<SimpleFeatureType, SimpleFeature>  features = featureSource.getFeatures(id2);
                FeatureIterator<SimpleFeature> iter = features.features();
                try{
                    if ( iter.hasNext() )
                        feature=iter.next();
                }finally{
                    iter.close();
                }
            } catch (IOException e) {
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
        }
        if( feature==null )
            // this should get it removed from the issues list
            throw new IllegalStateException("This issue is not legal for this uDig instance because the feature cannot be loaded."); //$NON-NLS-1$
        return feature;
    }

    private ILayer getLayer() {
        if( layer==null ){
            List< ? extends IProject> projects = ApplicationGIS.getProjects();
            IProject found=null;
            for( IProject project : projects ) {
                if( project.getID().toString().equals(projectID)){
                    found=project;
                    break;
                }
            }
            if( found==null )
                throw new IllegalStateException("This issue is not legal for this uDig instance because the project:"+projectID+" cannot be found."); //$NON-NLS-1$ //$NON-NLS-2$
            List<IMap> maps = found.getElements(IMap.class);
            IMap foundMap=null;
            for( IMap map : maps ) {
                if( map.getID().toString().equals(mapID) ){
                    foundMap=map;
                }
            }
            if( foundMap==null )
                throw new IllegalStateException("This issue is not legal for this uDig instance because the map:"+mapID+" cannot be found.");  //$NON-NLS-1$//$NON-NLS-2$
    
            List<ILayer> layers= foundMap.getMapLayers();
            for( ILayer layer : layers ) {
                if( layer.getID().toString().equals(layerID) ){
                    this.layer=layer;
                }
            }
        }
        if( layer==null )
            throw new IllegalStateException("This issue is not legal for this uDig instance because the alyer:"+layerID+" cannot be found."); //$NON-NLS-1$ //$NON-NLS-2$
        return layer;
    }

    /**
     * ONLY USE THIS FOR TESTING!!!!!!!
     */
    public static void setTesting( boolean b ) {
        testing=b;
    }

	
}
