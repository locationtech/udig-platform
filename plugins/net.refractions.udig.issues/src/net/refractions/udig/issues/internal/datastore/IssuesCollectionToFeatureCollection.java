/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.issues.internal.datastore;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;

import net.refractions.udig.core.enums.Priority;
import net.refractions.udig.core.enums.Resolution;
import net.refractions.udig.issues.IIssue;
import net.refractions.udig.issues.internal.IssuesActivator;
import net.refractions.udig.issues.internal.Messages;

import org.eclipse.ui.XMLMemento;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.collection.AbstractFeatureCollection;
import org.geotools.feature.type.GeometricAttributeType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Adapts a collection of issues to a collection of features.
 * @author Jesse
 * @since 1.1.0
 */
public class IssuesCollectionToFeatureCollection extends AbstractFeatureCollection
        implements
            FeatureCollection {
    private final ReferencedEnvelope DEFAULT_BOUNDS=new ReferencedEnvelope(-180,180,-90,90, DefaultGeographicCRS.WGS84);
    private Collection<? extends IIssue> issues;
    private FeatureTypeAttributeMapper mapper;

    public IssuesCollectionToFeatureCollection(Collection<? extends IIssue> issues, FeatureTypeAttributeMapper mapper){
        super(mapper.getSchema());
        this.issues=issues;
        this.mapper=mapper;
    }

    @Override
    protected void closeIterator( Iterator close ) {
    }

    @Override
    protected Iterator openIterator() {
        return new Iterator<Feature>(){
            Iterator<? extends IIssue> iter=issues.iterator();
            public boolean hasNext() {
                return iter.hasNext();
            }

            public Feature next() {
                IIssue issue = iter.next();
                String extId=issue.getExtensionID();
                String groupId=issue.getGroupId();

                String id=issue.getId();

                String resolution=issue.getResolution().name();
                String priority=issue.getPriority().name();
                String description=issue.getDescription();

                String viewMemento=createViewMemento(issue);

                String issueMemento=createIssueMemento(issue);
                Geometry bounds=createBounds(issue);
                Object[] attributes=new Object[getSchema().getAttributeCount()];
                try{
                    Feature feature = getSchema().create(attributes);
                    feature.setAttribute(mapper.getExtensionId(), extId);
                    if( bounds==null )
                        bounds=toMultiPolygon(DEFAULT_BOUNDS);
                    feature.setAttribute(mapper.getBounds(), bounds);
                    if( groupId==null )
                        groupId=Messages.IssuesCollectionToFeatureCollection_defaultGroup;
                    feature.setAttribute(mapper.getGroupId(), groupId);
                    feature.setAttribute(mapper.getId(), id);
                    if( resolution==null )
                        resolution=Resolution.UNRESOLVED.name();
                    feature.setAttribute(mapper.getResolution(), resolution);
                    if( priority==null )
                        priority=Priority.WARNING.name();
                    feature.setAttribute(mapper.getPriority(), priority);
                    if( description==null )
                        description=""; //$NON-NLS-1$
                    feature.setAttribute(mapper.getDescription(), description);
                    if( viewMemento!=null )
                    feature.setAttribute(mapper.getViewMemento(), viewMemento);
                    if( issueMemento!=null )
                    feature.setAttribute(mapper.getMemento(), issueMemento);
                    return feature;
                }catch(Exception e){
                    throw new RuntimeException(e);
                }
            }

            public void remove() {
                iter.remove();
            }

        };
    }

    protected Geometry createBounds( IIssue issue ) {
        AttributeType att=mapper.getSchema().getAttributeType(mapper.getBounds());
        if( MultiPolygon.class.isAssignableFrom(att.getType()) )
            return toMultiPolygon(issue.getBounds());

        return toPolygon(issue.getBounds());
    }

    protected MultiPolygon toMultiPolygon(ReferencedEnvelope env2){
        GeometryFactory factory=new GeometryFactory();
        return factory.createMultiPolygon(new Polygon[]{ toPolygon(env2)} );
    }

    protected Polygon toPolygon(ReferencedEnvelope env2){
        ReferencedEnvelope env=env2;
        if( env==null )
            env=new ReferencedEnvelope(-180,180,-90,90,DefaultGeographicCRS.WGS84);

        AttributeType att=mapper.getSchema().getAttributeType(mapper.getBounds());
        CoordinateReferenceSystem crs=null;

        if( att instanceof GeometricAttributeType )
            crs=((GeometricAttributeType)att).getCoordinateSystem();

        if( crs==null )
            crs=DefaultGeographicCRS.WGS84;

        GeometryFactory factory=new GeometryFactory();
        try{
            env=env.transform(crs, true);
        }catch(Exception e){
            IssuesActivator.log("", e); //$NON-NLS-1$
        }

        return factory.createPolygon(factory.createLinearRing(new Coordinate[]{
                new Coordinate(env.getMinX(), env.getMinY()),
                new Coordinate(env.getMaxX(), env.getMinY()),
                new Coordinate(env.getMaxX(), env.getMaxY()),
                new Coordinate(env.getMinX(), env.getMaxY()),
                new Coordinate(env.getMinX(), env.getMinY()),

        }), new LinearRing[0]);
    }


    protected String createIssueMemento( IIssue issue ) {
        StringWriter out;
        XMLMemento memento;
        out=new StringWriter();
        memento=XMLMemento.createWriteRoot("root"); //$NON-NLS-1$
        issue.save(memento);
        try {
            memento.save(out);
        } catch (IOException e) {
            IssuesActivator.log("", e); //$NON-NLS-1$
        }
        return out.toString();
    }

    protected String createViewMemento( IIssue issue ) {
        StringWriter out=new StringWriter();
        XMLMemento memento=XMLMemento.createWriteRoot("root"); //$NON-NLS-1$
        issue.getViewMemento(memento);
        try {
            memento.save(out);
        } catch (IOException e) {
            IssuesActivator.log("", e); //$NON-NLS-1$
        }
       return out.toString();
    }


    @Override
    public int size() {
        return issues.size();
    }

}
