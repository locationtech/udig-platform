/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering S.L. (Axios)
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.geometry.split;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.locationtech.jts.algorithm.CGAlgorithms;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateArrays;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geomgraph.DirectedEdge;

import org.locationtech.udig.tools.geometry.split.RingExtractor.ResultRingExtractor;

/**
 * Performs a split of a LineString, MultiLineString, Polygon or MultiPolygon
 * using a provided LineString as cutting edge.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.1.0
 */
public class SplitStrategy {

    private static final Logger						LOGGER	= Logger.getLogger(SplitStrategy.class.getName());

	/** The split line. */
    private final LineString                        splitLine;

    /** Valid strategies for split geometries. */
    private final Map<Class<?>, SpecificSplitOp> strategies;

    /**
     * Constructor for splitStrategy.
     * 
     * @param splitLine
     *            The split line.
     */
    public SplitStrategy(final LineString splitLine) {

	assert splitLine != null : "should not be null"; //$NON-NLS-1$

	this.splitLine = splitLine;

	this.strategies = new HashMap<Class<?>, SplitStrategy.SpecificSplitOp>(
		4);
	this.strategies.put(LineString.class, new LineStringSplitter());
	this.strategies.put(MultiLineString.class,
		new MultiLineStringSplitter());
	this.strategies.put(Polygon.class, new PolygonSplitter());
	this.strategies.put(MultiPolygon.class, new MultiPolygonSplitter());
    }

    /**
     * @return the original split line
     */
    public LineString getSplitLine() {
	return this.splitLine;
    }

    /**
     * @param geomToSplit
     * @return A list of the resultant geometries.
     */
    public List<Geometry> split(final Geometry geomToSplit) {

	assert !((geomToSplit instanceof Point) || (geomToSplit instanceof MultiPoint)) : "cannot split point or multipoint"; //$NON-NLS-1$

	Class<?> geometryClass = geomToSplit.getClass();
	SpecificSplitOp splitOp = findSplitStrategy(geometryClass);

    	UsefulSplitLineBuilder usefulSplitLineBuilder = UsefulSplitLineBuilder.newInstance(getSplitLine());

	LOGGER.fine("SplitStrategy, geomToSplit: " + geomToSplit.toText()); //$NON-NLS-1$
	LOGGER.fine("SplitStrategy, split line: " + usefulSplitLineBuilder.getOriginalSplitLine().toText()); //$NON-NLS-1$

	splitOp.setSplitLine(usefulSplitLineBuilder);
	List<Geometry> splitResult = splitOp.split(geomToSplit);

	return splitResult;
    }

    /**
     * Check if it is a valid intersection between the original geometry and the
     * split line.
     * 
     * Get the boundary of the polygon geometry and intersects it with the split
     * line. A valid split operation must fulfill the next:
     * 
     * <pre>
     * 
     * -The split line must intersect the polygon boundary at least at 2
     * points.
     * -The line comes from outside the feature, intersects on one point a boundary (interior-exterior), 
     * the line must have at least one point inside the feature, and then intersects again
     * the same boundary(interior-exterior).
     * </pre>
     * 
     * @param geomToSplit
     *            is a LineString, MultilineString, Polygon or MultiPolygon
     * @param splitInMapCrs
     *            a LineString
     * @return True if it can split the originalGeometry.
     */
    public boolean canSplit(Geometry geomToSplit) {

		assert geomToSplit != null : "the Geometry to split musn't be null"; //$NON-NLS-1$
	
		Class<?> geometryClass = geomToSplit.getClass();
		SpecificSplitOp splitOp = findSplitStrategy(geometryClass);
	
		UsefulSplitLineBuilder usefulSplitLineBuilder = UsefulSplitLineBuilder.newInstance(getSplitLine());
	
		LOGGER.fine("SplitStrategy, geomToSplit: " + geomToSplit.toText()); //$NON-NLS-1$
		LOGGER.fine("SplitStrategy, split line: " + usefulSplitLineBuilder.getOriginalSplitLine().toText()); //$NON-NLS-1$
	
		splitOp.setSplitLine(usefulSplitLineBuilder);
		boolean bool = splitOp.canSplit(geomToSplit);
	
		return bool;
    }

	/**
	 * Depending on the class of the Geometry to be split, found the
	 * correspondent class responsible of doing the split operation.
	 * 
	 * @param geomToSplitClass
	 *            Class of the geometry to be split.
	 * @return The class responsible of doing split.
	 */
	private SpecificSplitOp findSplitStrategy(Class<?> geomToSplitClass) {

		assert strategies.containsKey(geomToSplitClass) :  geomToSplitClass;

		return strategies.get(geomToSplitClass);
	}

	/**
	 * Strategy object for split geometry, subclasses are targeted towards
	 * specific kinds of geometry.
	 * <p>
	 * The GeometryCollectionSplitter will hold onto a LineString provided by
	 * the user, and use it to break provided geometry one by one.
	 */
	private static interface SpecificSplitOp {
		/**
		 * LineString used for splitting; as provided by the user.
		 * 
		 * @param splitLine
		 *            LineString used by the split method to break up geometry
		 *            one by one.
		 */
		public void setSplitLine(final UsefulSplitLineBuilder splitLine);

		public boolean canSplit(Geometry geomToSplit);

		/**
		 * Split the provided Geometry using the LineString provided by the
		 * user.
		 * 
		 * @param geomToSplit
		 *            Geometry to split using the user supplied lineString
		 * @return A list of geometries containing the result geometries.
		 */
		public List<Geometry> split(Geometry geomToSplit);

		/**
		 * Get the split line.
		 * 
		 * @return The split line.
		 */
		public UsefulSplitLineBuilder getSplitLine();
	}

        /**
         * Hold onto a LineString for use by subclasses.
         * 
         */
        private static abstract class AbstractSplitter implements SpecificSplitOp {
    
        	private UsefulSplitLineBuilder usefulSplitLineBuilder;
        
        	/**
        	 * Sets the split line.
        	 * 
        	 * @param splitter
        	 *            split line.
        	 */
        	public void setSplitLine(final UsefulSplitLineBuilder splitLine) {
        
        	    this.usefulSplitLineBuilder = splitLine;
        	}
        
        	/**
        	 * Return a copy of split line (defensive copy)
        	 * 
        	 * @return The split line.
        	 */
        	public UsefulSplitLineBuilder getSplitLine() {
        
        	    return this.usefulSplitLineBuilder;
        	}
        }

	/**
	 * User the lineString provided by the user to break up lineStrings one by
	 * one.
	 */
	private static class LineStringSplitter extends AbstractSplitter {

		/**
		 * Split method used for lineStrings.
		 * 
		 * @param geomToSplit
		 *            the {@link LineString} to be split.
		 * @return the resultant geometry.
		 */
		public List<Geometry> split(Geometry geomToSplit) {

			LineString lineString = (LineString) geomToSplit;
			LineString splitLine = getSplitLine().getOriginalSplitLine();
			Geometry result = lineString.difference(splitLine);

			List<Geometry> listLines = new ArrayList<Geometry>();
			// for each geometry, add to the list.
			for (int i = 0; i < result.getNumGeometries(); i++) {

				listLines.add(result.getGeometryN(i));
			}

			return listLines;
		}

		public boolean canSplit(Geometry lineString) {
		    
		    if (!(lineString instanceof LineString)){
			throw new IllegalArgumentException("LineString is spected"); //$NON-NLS-1$
    		    }
		    LineString splitLine = getSplitLine().getOriginalSplitLine();
    		    return splitLine.intersects(lineString) ;
		}

	} // end LineStringSplitter class

	/**
	 * Strategy object for splitting (a geometry collection).
	 * <p>
	 * The GeometryCollectionSplitter will hold onto the SpecificcSplitOp in
	 * order to hold onto the LineString provided by the user for splitting.
	 * 
	 */
	private static abstract class AbstractGeometryCollectionSplitter implements SpecificSplitOp {
		/** Used to split a single geometry */
	    
		private SpecificSplitOp		splitter;
		private UsefulSplitLineBuilder 	splitLineBuilder;

		/**
		 * Constructor.
		 * 
		 * @param spliterOperation
		 *            Specific split operation class.
		 */
		private AbstractGeometryCollectionSplitter(SpecificSplitOp spliterOperation) {

			this.splitter = spliterOperation;
		}

		/**
		 * Update the singlePartSplitter with the provided LineString.
		 * 
		 * @param splitLine
		 *            LineString used by split method to split provided geometry
		 *            one by one
		 */
		public final void setSplitLine(UsefulSplitLineBuilder splitLine) {

		    this.splitLineBuilder = splitLine;
		    this.splitter.setSplitLine(splitLine);
		}

		/**
		 * @return The split line.
		 */
        	public UsefulSplitLineBuilder getSplitLine() {
        
        	    return this.splitLineBuilder;
        	}

		/**
		 * Split method for polygons and multiPolygons.
		 * 
		 * @param geomToSplit
		 * 
		 * @return The resultant geometries.
		 */
		public final List<Geometry> split(final Geometry geomToSplit) {

			final GeometryCollection coll = (GeometryCollection) geomToSplit;
			final int numParts = coll.getNumGeometries();

			List<Geometry> result = new ArrayList<Geometry>();

			for (int partN = 0; partN < numParts; partN++) {

				Geometry simplePartN = coll.getGeometryN(partN);

				// for multiGeometry, the geometry that is intersected, split
				// it, the one that isn't, add without changes.
				if (this.splitter.canSplit(simplePartN)) {
					List<Geometry> splittedPart = this.splitter.split(simplePartN);
					result.addAll(splittedPart);
				} else {
					result.add(simplePartN);
				}
			}

			return result;
		}

		public boolean canSplit(Geometry geomToSplit) {

		    if( !((geomToSplit instanceof MultiPolygon) || (geomToSplit instanceof MultiLineString)) ){
			throw new IllegalArgumentException("MultiPolygon or MultiLineString geometry is expected" ); //$NON-NLS-1$
		    }
		    final GeometryCollection coll = (GeometryCollection) geomToSplit;
		    final int numParts = coll.getNumGeometries();

		    for (int partN = 0; partN < numParts; partN++) {

			Geometry currentGeometry = coll.getGeometryN(partN);

			if (this.splitter.canSplit(currentGeometry)) {
			    return true;
			}
		    }
		    return false;

		}
		

		/**
		 * Build a geometry collection with the provided parts.
		 * 
		 * @param gf
		 *            Provided geometry factory.
		 * @param parts
		 *            Split parts.
		 * @return The geometry collection with the geometries after being
		 *         split.
		 */
		protected abstract GeometryCollection buildFromParts(GeometryFactory gf, List<?> parts);

	} // end AbstractGeometryCollectionSplitter class

	/**
	 * Class responsible for doing split of MultiLineString geometries.
	 */
	private static class MultiLineStringSplitter extends AbstractGeometryCollectionSplitter {

		/**
		 * Default constructor.
		 */
		public MultiLineStringSplitter() {

			super(new LineStringSplitter());
		}

		@Override
		protected GeometryCollection buildFromParts(GeometryFactory gf, List<?> parts) {

			LineString[] lines = parts.toArray(new LineString[parts.size()]);
			MultiLineString result = gf.createMultiLineString(lines);
			return result;
		}

		public boolean canSplit(Geometry multiLine) {
		    
		    if (!(multiLine instanceof MultiLineString)){
			throw new IllegalArgumentException("LineString is spected"); //$NON-NLS-1$
    		    }
    		    // lines doesn't needed to be checked.
		    LineString splitLine = getSplitLine().getOriginalSplitLine();
    		    return splitLine.intersects(multiLine) ;
		}
	}

	/**
	 * Class responsible for doing split of MultiPolygon geometries.
	 */
	private static class MultiPolygonSplitter extends AbstractGeometryCollectionSplitter {

		/**
		 * Default constructor.
		 */
		public MultiPolygonSplitter() {

			super(new PolygonSplitter());
		}

		@Override
		protected GeometryCollection buildFromParts(GeometryFactory gf, List<?> parts) {

			Polygon[] polygons = parts.toArray(new Polygon[parts.size()]);
			MultiPolygon result = gf.createMultiPolygon(polygons);
			return result;
		}

//		public boolean canSplit(Geometry mutiPolygon) {
//		    if(!(mutiPolygon instanceof MultiPolygon)){
//			throw new IllegalArgumentException("MultiPolygon geometry is expected"); //$NON-NLS-1$
//		    }
//		    UsefulSplitLineBuilder splitBuilder = this.getSplitLine();
//
//		    return SplitUtil.canSplitPolygon(mutiPolygon, splitBuilder);
//		}
	}

	/**
	 * Responsible for splitting a single polygon; polygon may be split into
	 * several parts (or a hole may be formed).
	 * 
	 * Polygon Strategy:
	 * <ul>
	 * <li>1- Build a graph with all the edges and nodes from the intersection
	 * between the polygon and the line
	 * <li>2- Go through the graph, building the new polygons.
	 * <li>2.1- Get a non-visited edge from the intersection with the line, and
	 * go through it.
	 * <li>2.2- Get the next edges, if exist an intersection with other edges,
	 * take the one with less angle, CW direction. If not, take the next. Add
	 * the edge.
	 * <li>2.3- Do the same with the non-visited shell edges and holes. The next
	 * edges will be calculated with the less angles in CCW direction.
	 * <li>2.4- Analysis. Once all the rings are created, check that all
	 * intersection edges (those edges belong to 2 features) are visited twice.
	 * </ul>
	 * 
	 */
	private static class PolygonSplitter extends AbstractSplitter {

		private List<LinkedHashSet<SplitEdge>>	interiorRings	= new ArrayList<LinkedHashSet<SplitEdge>>();
		private GeometryFactory 		geometryFactory;

		/**
		 * Split the provided geometry.
		 * 
		 * @param geomToSplit
		 *            Polygon geometry to split.
		 * 
		 * @return split geometry or null.
		 */
		public List<Geometry> split(Geometry geomToSplit) {
    
    			assert geomToSplit instanceof Polygon;
    
    			final Polygon polygon = (Polygon) geomToSplit;
    			final List<Geometry> splitPolygon;
    			LineString splitLine = getSplitLine().getOriginalSplitLine();
    			if (SplitUtil.isClosedLine(splitLine)) {
    
    				splitPolygon = splitPolygonClosedLines(polygon);
    			} else {
    
    				splitPolygon = splitPolygon(polygon);
    			}
    
    			return splitPolygon;
		}

		/**
		 * Split the provided polygon.
		 * 
		 * @param aPolygon
		 *            The source polygon to split.
		 * @return Return a single polygon or multiPolygon depending on how the
		 *         split went.
		 */
		private List<Geometry> splitPolygon(final Polygon aPolygon) {

    		    	this.geometryFactory = aPolygon.getFactory();
    
    		    	final UsefulSplitLineBuilder splitLine = getSplitLine();
			SplitGraphBuilder graphBuilder = new SplitGraphBuilder(aPolygon, splitLine);
			graphBuilder.build();
			final Graph graph = graphBuilder.getResultantGraph();
			
			
			// fill the list with all the directedEdge that are forward
			// direction.
			List<DirectedEdge> directedEdgeList = new LinkedList<DirectedEdge>();
			Iterator<?> it = graph.getEdgeEnds().iterator();
			while (it.hasNext()) {

				DirectedEdge de = (DirectedEdge)it.next();
				if (de.isForward()) {
					directedEdgeList.add(de);
				} 
			}

			List<LinkedHashSet<SplitEdge>> allTheRings = new ArrayList<LinkedHashSet<SplitEdge>>();

			// Get a non-visited edge from the intersection, and go through
			// it with building rings. Get an edge from the shell.
			// After that, if still exist and edge from the holes that wasn't
			// visited, go through it.
			List<LinkedHashSet<SplitEdge>> ringList = new ArrayList<LinkedHashSet<SplitEdge>>();
			ringList = findRing(directedEdgeList);
			allTheRings.addAll(ringList);

			// analyze the graph assuring all the interior edges has been taken
			// into account twice.
			ringList = checkRings(directedEdgeList);
			allTheRings.addAll(ringList);

			// get the non-split edges.
			List<LinearRing> nonSplitHoles = getNonSplitHoles( graphBuilder);

			// Build the resultant polygon.
			List<Geometry> resultingPolygons = buildSimplePolygons(allTheRings, nonSplitHoles);

			return resultingPolygons;
		}

		/**
		 * Get the non split holes. The interior rings are calculated while
		 * finding rings, and also while construction the graph.
		 * 
		 * @param graph
		 *            The split graph.
		 * @return A list with the holes that weren't modified.
		 */
		private List<LinearRing> getNonSplitHoles( SplitGraphBuilder graph) {

			final List<LinearRing> nonSplitHoles = new ArrayList<LinearRing>();

			for (LinkedHashSet<SplitEdge> edgeList : this.interiorRings) {

				List<LinearRing> rings = buildLinearRing(edgeList);
				nonSplitHoles.addAll(rings);
			}

			Set<LinearRing> holes = graph.getNonSplitRings();

			for (LinearRing hole : holes) {

				nonSplitHoles.add(hole);
			}
			return nonSplitHoles;
		}

		/**
		 * Go through the graph and find the created rings, those rings will be
		 * the new features. Start finding a ring from an intersection edge if
		 * it isn't visited. Then go through the shell edges. At this point, the
		 * edges that form the new polygons are created, go through the hole
		 * edges that weren't visited, those holes will be interior rings that
		 * aren't modified. At the end, all the shell edges will be covered.
		 * 
		 * @param directedEdgeList
		 *            List with all the edges from the graph that are forward.
		 * @return A list containing rings, those rings will be the new
		 *         pre-polygons.
		 */
		private List<LinkedHashSet<SplitEdge>> findRing(List<DirectedEdge> directedEdgeList) {

			List<LinkedHashSet<SplitEdge>> edgeList = new ArrayList<LinkedHashSet<SplitEdge>>();
			
			// first, find the rings provided by the intersection edges.
			edgeList =findIntersectionRings(directedEdgeList,edgeList);

			// go through shell DirectEdge
			edgeList=findShellRings(directedEdgeList,edgeList);

			// then, find the rings taken into account only the holeEdges.
			storeHolesRings(directedEdgeList);

			return edgeList;
		}

		/**
		 * Store the rings obtained from the holes edges in the this.interiorRings list.
		 * 
		 * @param directedEdgeList
		 */
		private void storeHolesRings( List<DirectedEdge> directedEdgeList ) {
           
            for (DirectedEdge de : directedEdgeList) {

                DirectedEdge startEdge = de;
                SplitEdge edge = (SplitEdge) startEdge.getEdge();

                // get one of the holesEdge but calculate the angle seeking
                // which edge is nearest at CCW direction.
                if (!edge.isVisited() && edge.isHoleEdge()) {

                    // check that the edge is only a hole
                    // edge and not intersection-hole edge.
                    if (!isOnlyHoleEdge(edge, directedEdgeList)) {
                        // continue and pick other edge.
                        continue;
                    }

                    LinkedHashSet<SplitEdge> interiorRings = builtRing(startEdge, CGAlgorithms.COUNTERCLOCKWISE);
                    this.interiorRings.add(interiorRings);
                }
            }
        }

	/**
	 * Find the shell rings.
	 * 
	 * @param directedEdgeList
	 * @param edgeList
	 * @return The edgeList with the "shell rings"
	 */
	private List<LinkedHashSet<SplitEdge>> findShellRings(
		List<DirectedEdge> directedEdgeList,
		List<LinkedHashSet<SplitEdge>> edgeList) {

	    for (DirectedEdge de : directedEdgeList) {

		DirectedEdge startEdge = de;
		SplitEdge edge = (SplitEdge) startEdge.getEdge();

		// Starts from a shell edge
		if (!edge.isVisited() && edge.isShellEdge()) {
		    edgeList.add(builtRing(startEdge,
			    CGAlgorithms.COUNTERCLOCKWISE));
		}
	    }
	    return edgeList;
	}

	/**
	 * Find the rings provided by the intersection edges.
	 * 
	 * @param directedEdgeList
	 * @param edgeList
	 * @return The edgeList with the "intersection rings"
	 */
	private List<LinkedHashSet<SplitEdge>> findIntersectionRings(
		List<DirectedEdge> directedEdgeList,
		List<LinkedHashSet<SplitEdge>> edgeList) {

	    for (DirectedEdge de : directedEdgeList) {

		DirectedEdge startEdge = de;
		SplitEdge edge = (SplitEdge) startEdge.getEdge();

		// Get one of the intersection edges, and find ring
		if (!edge.isVisited() && edge.isIntersectionEdge()) {

		    // check that the edge is only an intersection
		    // edge and not intersection-hole edge.
		    if (!isOnlyIntersectionEdge(edge, directedEdgeList)) {
			// continue and pick other edge.
			continue;
		    }
		    // get the ring.
		    LinkedHashSet<SplitEdge> result = builtRing(startEdge,
			    CGAlgorithms.CLOCKWISE);
		    // It will return null if while building the ring, not all
		    // the edges are an intersection edges.
		    if (result != null) {
			edgeList.add(result);
		    }
		}
	    }

	    return edgeList;
	}

	/**
	 * Check if the given edge is unique edge, that means that there isn't
	 * an intersection edge with the same coordinates.
	 * 
	 * @param edge
	 *            Edge to find.
	 * @param directedEdgeList
	 *            List where to find the edge.
	 * @return True if it is unique edge.
	 */
	private boolean isOnlyHoleEdge(SplitEdge edge,
		List<DirectedEdge> directedEdgeList) {
	    // compare with the intersection edges.
	    // go through the DirectEdge
	    for (DirectedEdge direct : directedEdgeList) {
		SplitEdge possibleEdge = (SplitEdge) direct.getEdge();
		if (possibleEdge.isIntersectionEdge()) {
		    if (sameCoordinates(edge.getCoordinates(),
			    possibleEdge.getCoordinates())) {
			return false;
		    }
		}
	    }
	    return true;
	}

	/**
	 * Check if the given edge is unique edge, that means that there isn't a
	 * hole edge with the same coordinates.
	 * 
	 * @param edge
	 *            Edge to find.
	 * @param directedEdgeList
	 *            List where to find the edge.
	 * @return True if only exist this intersection edge, any hole edge has
	 *         the same coordinates.
	 */
	private boolean isOnlyIntersectionEdge(SplitEdge edge,
		List<DirectedEdge> directedEdgeList) {

	    // compare with the holes edges.
	    // go through the DirectEdge
	    for (DirectedEdge direct : directedEdgeList) {
		SplitEdge possibleHole = (SplitEdge) direct.getEdge();
		if (possibleHole.isHoleEdge()) {
		    if (sameCoordinates(edge.getCoordinates(),
			    possibleHole.getCoordinates())) {
			return false;
		    }
		}
	    }
	    return true;
	}

	/**
	 * Assure that all the intersection rings have been taken into account
	 * twice. Each edge belong to 2 features, so it must be visited twice.
	 * If it was not visited twice, visit it again.
	 * 
	 * @param directedEdgeList
	 *            List with all the edges from the graph that are forward.
	 * @return A list containing rings, those rings will be the new
	 *         polygons.
	 */
	private List<LinkedHashSet<SplitEdge>> checkRings(
		List<DirectedEdge> directedEdgeList) {

	    List<LinkedHashSet<SplitEdge>> edgeList = new ArrayList<LinkedHashSet<SplitEdge>>();

	    // go through the DirectEdge
	    for (DirectedEdge de : directedEdgeList) {

		DirectedEdge startEdge = de;
		SplitEdge edge = (SplitEdge) startEdge.getEdge();

		// interior edges must be visited twice, assure all of them
		// were processed 2 times.
		if (!edge.isTwiceVisited() && edge.isIntersectionEdge()) {

		    // if the intersection edge is also a hole edge (there are 2
		    // edges, one belong to an intersection edge and the other
		    // to the hole, but both of them have the same
		    // coordinates.), do nothing with it.
		    if (!intersectionAndHoleSame(directedEdgeList)) {

			// get the right direction.
			// test with CW direction
			int direction = testDirection(startEdge,
				CGAlgorithms.CLOCKWISE);

			edgeList.add(builtRing(startEdge, direction));
		    }
		}
	    }
	    return edgeList;
	}

	/**
	 * Seek out if there exist an intersection edge that is exactly as a
	 * hole edge.
	 * 
	 * @param directedEdgeList
	 *            The list with the edges.
	 * @return True if exist an intersect edge that also is a hole edge.
	 */
	private boolean intersectionAndHoleSame(
		List<DirectedEdge> directedEdgeList) {

	    // go through the DirectEdge
	    for (DirectedEdge de : directedEdgeList) {

		SplitEdge edge = (SplitEdge) de.getEdge();
		if (edge.isIntersectionEdge()) {
		    // compare with the holes edges.
		    // go through the DirectEdge
		    for (DirectedEdge direct : directedEdgeList) {
			SplitEdge possibleHoleOrShell = (SplitEdge) direct
				.getEdge();
			if (possibleHoleOrShell.isHoleEdge()
				|| possibleHoleOrShell.isShellEdge()) {
			    if (sameCoordinates(edge.getCoordinates(),
				    possibleHoleOrShell.getCoordinates())) {
				return true;
			    }
			}
		    }
		}
	    }

	    return false;
	}

	/**
	 * check the coordinates are the same, that means, if the coordinate 1
	 * equals to the coordinate 2 from the hole, then, the coordinate 2 must
	 * equal the coordinate 1 from the hole.
	 * 
	 * @param intersection
	 *            Array containing coordinates from the intersection.
	 * @param hole
	 *            Array containing coordinates from the hole.
	 * @return True if a coordinate from the intersection or the hole exists
	 *         on the other side (hole/intersection).
	 */
	private boolean sameCoordinates(Coordinate[] intersection,
		Coordinate[] hole) {

	    // check the coordinates are the same, that means, if the coordinate
	    // 1 equals to the coordinate 2 from the hole, then, the coordinate
	    // 2 must equal the coordinate 1 from the hole.

	    // the next code could be merged into one single 'if', but this way
	    // is easier to read it.
	    if (intersection[0].equals(hole[0])
		    && intersection[1].equals(hole[1])) {
		return true;
	    }
	    if (intersection[1].equals(hole[0])
		    && intersection[0].equals(hole[1])) {
		return true;
	    }
	    if (intersection[0].equals(hole[1])
		    && intersection[1].equals(hole[0])) {
		return true;
	    }
	    if (intersection[1].equals(hole[1])
		    && intersection[0].equals(hole[0])) {
		return true;
	    }

	    return false;
	}

	/**
	 * Get the next edges, if exists an intersection with other edges, take
	 * the one with less angle in the given direction. If not, take the
	 * next. Add the edge.
	 * 
	 * @param startEdge
	 *            The directedEdge from which start.
	 * @param direction
	 *            CCW or CW direction.
	 * @return A list with a ring of edges.
	 */
	private LinkedHashSet<SplitEdge> builtRing(
		final DirectedEdge startEdge, final int direction) {

	    DirectedEdge currentDirectedEdge = startEdge;
	    DirectedEdge nextDirectedEdge = null;

	    LinkedHashSet<SplitEdge> ring = new LinkedHashSet<SplitEdge>();

	    while (!edgesAreEqual((SplitEdge) startEdge.getEdge(),
		    nextDirectedEdge)) {
		SplitEdge currentEdge = (SplitEdge) currentDirectedEdge
			.getEdge();

		ring.add(currentEdge);

		currentEdge.countVisited();

		DirectedEdge sym = currentDirectedEdge.getSym();
		SplitGraphNode endNode = (SplitGraphNode) sym.getNode();

		SplitEdgeStar nodeEdges = (SplitEdgeStar) endNode.getEdges();

		nextDirectedEdge = nodeEdges.findClosestEdgeInDirection(sym,
			direction);
		assert nextDirectedEdge != null;
		currentDirectedEdge = nextDirectedEdge;
	    }
	    return ring;
	}

	/**
	 * This function will test if the given direction will form new rings,
	 * or will form repeated rings. It depends on the line direction, so
	 * first test with CW direction, if the new ring isn't repeated, thats
	 * ok.
	 * 
	 * @param startEdge
	 *            The directedEdge from which start.
	 * @param direction
	 *            CCW or CW direction.
	 * @return The correct direction to form the new ring.
	 */
	private int testDirection(final DirectedEdge startEdge,
		final int direction) {

	    DirectedEdge currentDirectedEdge = startEdge;
	    DirectedEdge nextDirectedEdge = null;

	    int finalDirection = direction;

	    while (!edgesAreEqual((SplitEdge) startEdge.getEdge(),
		    nextDirectedEdge)) {

		SplitEdge currentEdge = (SplitEdge) currentDirectedEdge
			.getEdge();

		// if thats true, it means that ring already exist, so return
		// the opposite direction.
		if (currentEdge.isTwiceVisited()) {
		    finalDirection = (direction == CGAlgorithms.CLOCKWISE) ? CGAlgorithms.COUNTERCLOCKWISE
			    : CGAlgorithms.CLOCKWISE;
		    break;
		}
		DirectedEdge sym = currentDirectedEdge.getSym();
		SplitGraphNode endNode = (SplitGraphNode) sym.getNode();

		SplitEdgeStar nodeEdges = (SplitEdgeStar) endNode.getEdges();
		nextDirectedEdge = nodeEdges.findClosestEdgeInDirection(sym,
			direction);

		assert nextDirectedEdge != null;

		currentDirectedEdge = nextDirectedEdge;
	    }
	    return finalDirection;
	}

	/**
	 * Check if those edges are equal.
	 * 
	 * @param startEdge
	 *            Start directedEdge
	 * @param nextDirectedEdge
	 *            The next one that could be the same as the start.
	 * @return True if they are equal.
	 */
	private boolean edgesAreEqual(SplitEdge startEdge,
		DirectedEdge nextDirectedEdge) {

	    if (nextDirectedEdge == null) {
		return false;
	    }

	    return startEdge.equalsCoordinates(nextDirectedEdge.getEdge());
	}

	/**
	 * With the provided rings and the non-split holes, build polygons.
	 * 
	 * @param allRings
	 *            The ring of the polygon/s.
	 * @param nonSplitHoles
	 *            Non-split holes.
	 * @return A list with all the built polygons.
	 */
	private List<Geometry> buildSimplePolygons(
		List<LinkedHashSet<SplitEdge>> allRings,
		List<LinearRing> nonSplitHoles) {

	    List<Geometry> polygons = new ArrayList<Geometry>(allRings.size());

	    for (LinkedHashSet<SplitEdge> edgeList : allRings) {

		// boolean polyIsHole = false;
		Polygon poly = buildPolygon(edgeList);
		Geometry result = poly;

		for (LinearRing holeRing : nonSplitHoles) {

		    // if it contain a ring, make the difference for creating a
		    // hole.
		    if (holeRing.within(poly)) {

			Geometry hole = this.geometryFactory.createPolygon(
				holeRing, null);
			result = result.difference(hole);
		    }
		}
		// don't add repeated polygons.
		boolean repeated = false;
		for (Geometry insertedPol : polygons) {

		    if (result.equals(insertedPol)) {
			repeated = true;
		    }
		}
		if (!repeated) {
		    // add each polygon. There could be more than one result
		    // only after applying difference with a hole.
		    for (int i = 0; i < result.getNumGeometries(); i++) {
			polygons.add(result.getGeometryN(i));
		    }
		}
	    }
	    return polygons;
	}

	/**
	 * Build a polygon from a edgeList. This edgeList is a ring of edges.
	 * 
	 * @param edgeList
	 *            A ring of edges.
	 * @param gf
	 *            The geometry factory.
	 * @return The resultant polygon.
	 */
	private Polygon buildPolygon(LinkedHashSet<SplitEdge> edgeList) {

	    List<Coordinate> coords = new LinkedList<Coordinate>();
	    Coordinate[] lastCoordinates = null;
	    for (SplitEdge edge : edgeList) {
		Coordinate[] coordinates = edge.getCoordinates();
		if (lastCoordinates != null) {
		    Coordinate endPoint = lastCoordinates[lastCoordinates.length - 1];
		    Coordinate startPoint = coordinates[0];
		    if (!endPoint.equals2D(startPoint)) {
			coordinates = CoordinateArrays.copyDeep(coordinates);
			CoordinateArrays.reverse(coordinates);
		    }
		}
		lastCoordinates = coordinates;
		for (int i = 0; i < coordinates.length; i++) {
		    Coordinate coord = coordinates[i];
		    coords.add(coord);
		}
	    }
	    // shell coordinates.
	    Coordinate[] shellCoords = new Coordinate[coords.size()];
	    coords.toArray(shellCoords);
	    shellCoords = CoordinateArrays.removeRepeatedPoints(shellCoords);
	    // hole/s rings.
	    List<LinearRing> holeList = new ArrayList<LinearRing>();
	    LinearRing[] ring;

	    // even if it hasn't self-intersections, do the first so
	    // LinearRing[] result is filled with values.
	    do {
		ring = extractInteriorRing(shellCoords);
		// result[0] is the shell.
		// result[1] if exists, is a hole.
		if (ring[1] != null) {
		    holeList.add(ring[1]);
		}
		shellCoords = new Coordinate[ring[0].getCoordinates().length];
		shellCoords = ring[0].getCoordinates();

	    } while (hasSelfIntersection(shellCoords));

	    Polygon poly;
	    if (holeList.isEmpty()) {
		poly = this.geometryFactory.createPolygon(ring[0],
			(LinearRing[]) null);
	    } else {
		LinearRing[] holes = holeList.toArray(new LinearRing[holeList
			.size()]);
		poly = this.geometryFactory.createPolygon(ring[0], holes);
	    }

	    return poly;
	}

	/**
	 * Check the coordinates seeking for self-intersection, that means, seek
	 * for repeated coordinates.
	 * 
	 * @param shellCoords
	 *            Shell coordinates off the polygon.
	 * @return True if it has self-intersection.
	 */
	private boolean hasSelfIntersection(Coordinate[] shellCoords) {

	    // check if it has self-intersections
	    for (int i = 1; i < shellCoords.length; i++) {

		Coordinate actualCoord = shellCoords[i];
		for (int j = i + 1; j < shellCoords.length - 1; j++) {

		    Coordinate selfIntersectionCoord = shellCoords[j];
		    if (actualCoord.equals2D(selfIntersectionCoord)) {
			// here we have a self intersection coordinate.
			return true;
		    }
		}
	    }
	    return false;
	}

	/**
	 * Go through the shell coordinates seeking for self-intersections and
	 * extract them. Then those extracted rings will be inserted as holes of
	 * the polygon.
	 * 
	 * @param shellCoords
	 *            Shell coordinates off the polygon.
	 * @return 2 linearRing, one will be the shell and the other a hole if
	 *         exists.
	 */
	private LinearRing[] extractInteriorRing(Coordinate[] shellCoords) {

	    // check for self-intersection
	    // the shell coordinates can't have self-intersection, if a
	    // self-intersection is found, the coordinates between then will
	    // form an interior ring.

	    // take into account that the first and last coordinate are the
	    // same.
	    int selfIntersectionStart = -1;
	    int selfIntersectionEnd = -1;
	    for (int i = 1; i < shellCoords.length
		    && selfIntersectionStart == -1; i++) {

		Coordinate actualCoord = shellCoords[i];
		for (int j = i + 1; j < shellCoords.length - 1; j++) {

		    Coordinate selfIntersectionCoord = shellCoords[j];
		    if (actualCoord.equals2D(selfIntersectionCoord)) {
			// here we have a self intersection coordinate.
			selfIntersectionStart = i;
			selfIntersectionEnd = j;
			break;
		    }
		}

	    }
	    LinearRing[] shellAndHole = new LinearRing[2];
	    List<Coordinate> shell = new LinkedList<Coordinate>();
	    List<Coordinate> hole = new LinkedList<Coordinate>();

	    for (int j = 0; j < shellCoords.length; j++) {

		// add the actual shell coordinate
		shell.add(shellCoords[j]);
		if (j == selfIntersectionStart) {

		    // retrieve the interior ring.
		    for (int i = selfIntersectionStart; i <= selfIntersectionEnd; i++) {

			hole.add(shellCoords[i]);
		    }
		    // move the cursor to continue after the
		    // self-intersection coordinate
		    j = selfIntersectionEnd;
		}
	    }
	    shellAndHole[0] = this.geometryFactory.createLinearRing(shell
		    .toArray(new Coordinate[shell.size()]));
	    if (hole.size() != 0) {
		shellAndHole[1] = this.geometryFactory.createLinearRing(hole
			.toArray(new Coordinate[hole.size()]));
	    } else {
		shellAndHole[1] = null;
	    }
	    return shellAndHole;
	}

	/**
	 * From those interior rings (holes), build linearRings. Those rings
	 * will be the new split features, or an existent ring that has not been
	 * modified.
	 * 
	 * @param edgeList
	 *            List of splitEdges.
	 * @return The linearRing of the holes.
	 */
	private List<LinearRing> buildLinearRing(
		LinkedHashSet<SplitEdge> edgeList) {

	    List<Coordinate> coords = new ArrayList<Coordinate>();
	    Coordinate[] lastCoordinates = null;
	    for (SplitEdge edge : edgeList) {
		Coordinate[] coordinates = edge.getCoordinates();
		if (lastCoordinates != null) {
		    Coordinate endPoint = lastCoordinates[lastCoordinates.length - 1];
		    Coordinate startPoint = coordinates[0];
		    if (!endPoint.equals2D(startPoint)) {
			coordinates = CoordinateArrays.copyDeep(coordinates);
			CoordinateArrays.reverse(coordinates);
		    }
		}
		lastCoordinates = coordinates;
		for (int i = 0; i < coordinates.length; i++) {
		    Coordinate coord = coordinates[i];
		    coords.add(coord);
		}
	    }
	    Coordinate[] shellCoords = new Coordinate[coords.size()];
	    coords.toArray(shellCoords);
	    shellCoords = CoordinateArrays.removeRepeatedPoints(shellCoords);

	    // hole/s rings.
	    List<LinearRing> holeList = new ArrayList<LinearRing>();
	    LinearRing[] result;

	    // even if it hasn't self-intersections, do the first so
	    // LinearRing[] result is filled with values.

	    do {
		// shellCoords could have nested rings(self-intersected), so
		// extract them.
		// find which one has self-intersections.
		result = extractInteriorRing(shellCoords);

		if (hasSelfIntersection(result[0].getCoordinates())) {
		    // the remaining hole is on result[0]
		    // result[1] if exists, is a nested hole, but now is
		    // separate.
		    if (result[1] != null) {
			holeList.add(result[1]);
		    }
		    // result[0] is the remaining hole.
		    shellCoords = new Coordinate[result[0].getCoordinates().length];
		    shellCoords = result[0].getCoordinates();
		} else if (result[1] != null
			&& hasSelfIntersection(result[1].getCoordinates())) {
		    // the remaining hole is on result[1]
		    // result[0] if exists, is a nested hole, but now is
		    // separate.
		    if (result[0] != null) {
			holeList.add(result[0]);
		    }
		    // result[1] is the remaining hole.
		    shellCoords = new Coordinate[result[1].getCoordinates().length];
		    shellCoords = result[1].getCoordinates();
		} else {
		    // no one has intersections, so fill the shellCoords with
		    // result[0] or result[1], it doesn't matter.
		    shellCoords = new Coordinate[result[0].getCoordinates().length];
		    shellCoords = result[0].getCoordinates();
		}

	    } while (hasSelfIntersection(shellCoords));

	    // add the last holes, result[0] and result[1] if exists.
	    holeList.add(result[0]);
	    if (result[1] != null) {
		holeList.add(result[1]);
	    }
	    return holeList;
	}

	/**
	 * <p>
	 * 
	 * <pre>
	 * Split operation for polygons using closed lines.
	 * First extract the rings and the remaining line from the 
	 * closed line with the aid of the {@link RingExtractor}.
	 * Second make the operation using {@link SplitClosedLines}.
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param polygon
	 *            Polygon that will suffer the split operation.
	 * @return List with the resultant polygons.
	 */
	private List<Geometry> splitPolygonClosedLines(final Polygon polygon) {

	    LineString splitLine = getSplitLine().getOriginalSplitLine();

	    RingExtractor ringExtractor = new RingExtractor(splitLine);
	    ResultRingExtractor data = ringExtractor.processExtraction();
	    SplitClosedLines closedLines = new SplitClosedLines(data);

	    return closedLines.runClosedLineSplit(polygon);
	}

	public boolean canSplit(Geometry polygon) {
	    
	    if(!(polygon instanceof Polygon)){
	    	throw new IllegalArgumentException("Polygon geometry is expected"); //$NON-NLS-1$
	    }
	    
	    UsefulSplitLineBuilder splitBuilder = this.getSplitLine();

	    return SplitUtil.canSplitPolygon(polygon, splitBuilder);
	}
    }
}
