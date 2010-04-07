package net.refractions.udig.core.filter;

import org.opengis.filter.And;
import org.opengis.filter.ExcludeFilter;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.Id;
import org.opengis.filter.IncludeFilter;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;

public class AdaptingFilterFactory {
    /**
     * Visitor used to quickly create the correct concrete AdaptingFilter sub class.
     */
    static CreateAdaptingFilterVisitor creator = new CreateAdaptingFilterVisitor();
        
    /**
     * Create an AdaptingFilter for the provided filter, configured to adapt
     * to the provided source.
     *
     * @param filter
     * @param source Often an ILayer or IGeoResource
     * @return A specific AdaptingFilter subclass based on the type of filter
     */
    public static AdaptingFilter createAdaptingFilter( Filter filter, Object source ){
        AdaptingFilter adaptingFilter = createAdaptingFilter(filter);
        adaptingFilter.addAdapter( source );        
        return adaptingFilter;
    }
    /**
     * Create an AdaptingFilter for the provided filter.
     *
     * @param filter
     * @return A specific AdaptingFilter subclass based on the type of filter
     */
    public static AdaptingFilter createAdaptingFilter( Filter filter ){
        return (AdaptingFilter) filter.accept( creator, null );
    }
    
    /**
     * Use a FilterVisitor rather than instance of checks.
     * @author Jody
     * @since 1.1.0
     */
    static class CreateAdaptingFilterVisitor implements FilterVisitor {
        public AdaptingFilter visit( ExcludeFilter filter, Object data ) {
            return new AdaptingExcludeFilter();
        }

        public AdaptingFilter visit( IncludeFilter filter, Object data ) {
            return new AdaptingIncludeFilter();
        }

        public AdaptingFilter visit( And filter, Object data ) {
            return new AdaptingAnd( filter );
        }

        public AdaptingFilter visit( Id filter, Object data ) {
            return new AdaptingId( filter );
        }

        public AdaptingFilter visit( Not filter, Object data ) {
            return new AdaptingNot( filter );
        }

        public AdaptingFilter visit( Or filter, Object data ) {
            return new AdaptingOr( filter );
        }

        public AdaptingFilter visit( PropertyIsBetween filter, Object data ) {
            return new AdaptingPropertyIsBetween( filter );
        }

        public AdaptingFilter visit( PropertyIsEqualTo filter, Object data ) {
            return new AdaptingPropertyIsEqualTo( filter );
        }

        public AdaptingFilter visit( PropertyIsNotEqualTo filter, Object data ) {
            return new AdaptingPropertyIsNotEqualTo( filter );
        }

        public AdaptingFilter visit( PropertyIsGreaterThan filter, Object data ) {
            return new AdaptingPropertyIsGreaterThan( filter );
        }

        public AdaptingFilter visit( PropertyIsGreaterThanOrEqualTo filter, Object data ) {
            return new AdaptingPropertyIsGreaterThanOrEqualTo( filter );
        }

        public AdaptingFilter visit( PropertyIsLessThan filter, Object data ) {
            return new AdaptingPropertyIsLessThan( filter );
        }

        public AdaptingFilter visit( PropertyIsLessThanOrEqualTo filter, Object data ) {
            return new AdaptingPropertyIsLessThanOrEqualTo( filter );
        }

        public AdaptingFilter visit( PropertyIsLike filter, Object data ) {
            return new AdaptingPropertyIsLike( filter );
        }

        public AdaptingFilter visit( PropertyIsNull filter, Object data ) {
            return new AdaptingPropertyIsNull( filter );
        }

        public AdaptingFilter visit( BBOX filter, Object data ) {
            return new AdaptingBBOX( filter );
        }

        public AdaptingFilter visit( Beyond filter, Object data ) {
            return new AdaptingBeyond( filter );
        }

        public AdaptingFilter visit( Contains filter, Object data ) {
            return new AdaptingContains( filter );
        }

        public AdaptingFilter visit( Crosses filter, Object data ) {
            return new AdaptingCrosses( filter );
        }

        public AdaptingFilter visit( Disjoint filter, Object data ) {
            return new AdaptingDisjoint( filter );
        }

        public AdaptingFilter visit( DWithin filter, Object data ) {
            return new AdaptingDWithin( filter );
        }

        public AdaptingFilter visit( Equals filter, Object data ) {
            return new AdaptingEquals( filter );
        }

        public AdaptingFilter visit( Intersects filter, Object data ) {
            return new AdaptingIntersects( filter );
        }

        public AdaptingFilter visit( Overlaps filter, Object data ) {
            return new AdaptingOverlaps( filter );
        }

        public AdaptingFilter visit( Touches filter, Object data ) {
            return new AdaptingTouches( filter );
        }

        public AdaptingFilter visit( Within filter, Object data ) {
            return new AdaptingWithin( filter );
        }

        public AdaptingFilter visitNullFilter( Object data ) {
            return null;
        }
        
    }
}
