package net.refractions.udig.tutorials.tool.coverage;

import java.util.List;
import java.util.Set;

import net.refractions.udig.tools.edit.AbstractEditTool;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.DefaultEditToolBehaviour;
import net.refractions.udig.tools.edit.EditToolConfigurationHelper;
import net.refractions.udig.tools.edit.EnablementBehaviour;
import net.refractions.udig.tools.edit.activator.DrawGeomsActivator.DrawType;
import net.refractions.udig.tools.edit.behaviour.MoveVertexBehaviour;
import net.refractions.udig.tools.edit.behaviour.SelectFeatureBehaviour;
import net.refractions.udig.tools.edit.behaviour.SelectVertexBehaviour;
import net.refractions.udig.tools.edit.behaviour.SelectVertexOnMouseDownBehaviour;

import org.opengis.filter.spatial.Intersects;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class CoverageTool extends AbstractEditTool {
    
    @Override
    protected void initAcceptBehaviours( List<Behaviour> acceptBehaviours ) {
        List<Behaviour> defaults = DefaultEditToolBehaviour.createAcceptAllChanges();
        acceptBehaviours.addAll(defaults);
    }
    
    @Override
    protected void initActivators( Set<Activator> activators ) {
        DrawType geometryType=DrawType.POLYGON;
        Set<Activator> defaults = DefaultEditToolBehaviour.createDefaultCreateActivators(geometryType);
        activators.addAll(defaults);
    }
    
    @Override
    protected void initCancelBehaviours( List<Behaviour> cancelBehaviours ) {
        List<Behaviour> defaults = DefaultEditToolBehaviour.createDefaultCancelBehaviours();
        cancelBehaviours.addAll(defaults);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void initEnablementBehaviours( List<EnablementBehaviour> enablementBehaviours ) {
        Class< ? extends Geometry>[] classes = new Class[]{
                Polygon.class, MultiPolygon.class
        };
        List<EnablementBehaviour> defaults = DefaultEditToolBehaviour.createValidToolEnablementBehaviour(classes );
        enablementBehaviours.addAll(defaults);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void initEventBehaviours( EditToolConfigurationHelper helper ) {
        helper.startMutualExclusiveList();
        helper.add(new SelectVertexOnMouseDownBehaviour());
        helper.add(new SelectVertexBehaviour());
        SelectFeatureBehaviour selectFeatureBehaviour =
        	new SelectFeatureBehaviour(new Class[]{Geometry.class}, Intersects.class );
        selectFeatureBehaviour.addSelectionStrategy(new SelectNeightborsStrategy());
        
        helper.add(selectFeatureBehaviour);
        helper.stopMutualExclusiveList();
    
        helper.add( new MoveVertexBehaviour() );
        
        helper.done();
    }

}
