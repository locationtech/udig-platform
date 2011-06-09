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
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.AnyInteracts;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.Begins;
import org.opengis.filter.temporal.BegunBy;
import org.opengis.filter.temporal.During;
import org.opengis.filter.temporal.EndedBy;
import org.opengis.filter.temporal.Ends;
import org.opengis.filter.temporal.Meets;
import org.opengis.filter.temporal.MetBy;
import org.opengis.filter.temporal.OverlappedBy;
import org.opengis.filter.temporal.TContains;
import org.opengis.filter.temporal.TEquals;
import org.opengis.filter.temporal.TOverlaps;

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
    public static <F extends Filter> AdaptingFilter<F> createAdaptingFilter( F filter, Object source ){
        AdaptingFilter<F> adaptingFilter = createAdaptingFilter(filter);
        adaptingFilter.addAdapter( source );        
        return adaptingFilter;
    }
    /**
     * Create an AdaptingFilter for the provided filter.
     *
     * @param filter
     * @return A specific AdaptingFilter subclass based on the type of filter
     */
    @SuppressWarnings("unchecked")
    public static <F extends Filter> AdaptingFilter<F> createAdaptingFilter( F filter ){
        return (AdaptingFilter<F>) filter.accept( creator, null );
    }
    
    /**
     * Use a FilterVisitor rather than instance of checks.
     * @author Jody
     * @since 1.1.0
     */
    static class CreateAdaptingFilterVisitor implements FilterVisitor {
        public AdaptingExcludeFilter visit( ExcludeFilter filter, Object data ) {
            return new AdaptingExcludeFilter();
        }

        public AdaptingIncludeFilter visit( IncludeFilter filter, Object data ) {
            return new AdaptingIncludeFilter();
        }

        public AdaptingAnd visit( And filter, Object data ) {
            return new AdaptingAnd( filter );
        }

        public AdaptingId visit( Id filter, Object data ) {
            return new AdaptingId( filter );
        }

        public AdaptingNot visit( Not filter, Object data ) {
            return new AdaptingNot( filter );
        }

        public AdaptingOr visit( Or filter, Object data ) {
            return new AdaptingOr( filter );
        }

        public AdaptingPropertyIsBetween visit( PropertyIsBetween filter, Object data ) {
            return new AdaptingPropertyIsBetween( filter );
        }

        public AdaptingPropertyIsEqualTo visit( PropertyIsEqualTo filter, Object data ) {
            return new AdaptingPropertyIsEqualTo( filter );
        }

        public AdaptingPropertyIsNotEqualTo visit( PropertyIsNotEqualTo filter, Object data ) {
            return new AdaptingPropertyIsNotEqualTo( filter );
        }

        public AdaptingPropertyIsGreaterThan visit( PropertyIsGreaterThan filter, Object data ) {
            return new AdaptingPropertyIsGreaterThan( filter );
        }

        public AdaptingPropertyIsGreaterThanOrEqualTo visit( PropertyIsGreaterThanOrEqualTo filter, Object data ) {
            return new AdaptingPropertyIsGreaterThanOrEqualTo( filter );
        }

        public AdaptingPropertyIsLessThan visit( PropertyIsLessThan filter, Object data ) {
            return new AdaptingPropertyIsLessThan( filter );
        }

        public AdaptingPropertyIsLessThanOrEqualTo visit( PropertyIsLessThanOrEqualTo filter, Object data ) {
            return new AdaptingPropertyIsLessThanOrEqualTo( filter );
        }

        public AdaptingPropertyIsLike visit( PropertyIsLike filter, Object data ) {
            return new AdaptingPropertyIsLike( filter );
        }

        public AdaptingPropertyIsNull visit( PropertyIsNull filter, Object data ) {
            return new AdaptingPropertyIsNull( filter );
        }

        public AdaptingBBOX visit( BBOX filter, Object data ) {
            return new AdaptingBBOX( filter );
        }

        public AdaptingBeyond visit( Beyond filter, Object data ) {
            return new AdaptingBeyond( filter );
        }

        public AdaptingContains visit( Contains filter, Object data ) {
            return new AdaptingContains( filter );
        }

        public AdaptingCrosses visit( Crosses filter, Object data ) {
            return new AdaptingCrosses( filter );
        }

        public AdaptingDisjoint visit( Disjoint filter, Object data ) {
            return new AdaptingDisjoint( filter );
        }

        public AdaptingDWithin visit( DWithin filter, Object data ) {
            return new AdaptingDWithin( filter );
        }

        public AdaptingEquals visit( Equals filter, Object data ) {
            return new AdaptingEquals( filter );
        }

        public AdaptingIntersects visit( Intersects filter, Object data ) {
            return new AdaptingIntersects( filter );
        }

        public AdaptingOverlaps visit( Overlaps filter, Object data ) {
            return new AdaptingOverlaps( filter );
        }

        public AdaptingTouches visit( Touches filter, Object data ) {
            return new AdaptingTouches( filter );
        }

        public AdaptingWithin visit( Within filter, Object data ) {
            return new AdaptingWithin( filter );
        }
        /** Called null is visited */
        public AdaptingFilter<?> visitNullFilter( Object data ) {
            return null;
        }
        // temporal
        
        public AdaptingAfter visit( After filter, Object arg1 ) {
            return new AdaptingAfter( filter);
        }

        public AdaptingAnyInteracts visit( AnyInteracts filter, Object arg1 ) {
            return new AdaptingAnyInteracts( filter );
        }

        public AdaptingBefore visit( Before filter, Object arg1 ) {
            return new AdaptingBefore( filter );
        }

        public AdaptingBegins visit( Begins filter, Object arg1 ) {
            return new AdaptingBegins( filter );
        }

        public AdaptingBegunBy visit( BegunBy filter, Object arg1 ) {
            return new AdaptingBegunBy( filter );
        }

        public AdaptingDuring visit( During filter, Object arg1 ) {
            return new AdaptingDuring( filter );
        }
        
        public AdaptingEndedBy visit( EndedBy filter, Object arg1 ) {
            return new AdaptingEndedBy( filter );
        }

        public AdaptingEnds visit( Ends filter, Object arg1 ) {
            return new AdaptingEnds( filter );
        }

        public AdaptingMeets visit( Meets filter, Object arg1 ) {
            return new AdaptingMeets( filter );
        }

        public AdaptingMetBy visit( MetBy filter, Object arg1 ) {
            return new AdaptingMetBy( filter );
        }

        public AdaptingOverlappedBy visit( OverlappedBy filter, Object arg1 ) {
            return new AdaptingOverlappedBy( filter );
        }

        public AdaptingTContains visit( TContains filter, Object arg1 ) {
            return new AdaptingTContains( filter );
        }

        public AdaptingTEquals visit( TEquals filter, Object arg1 ) {
            return new AdaptingTEquals( filter );
        }

        public AdaptingTOverlaps visit( TOverlaps filter, Object arg1 ) {
            return new AdaptingTOverlaps( filter );
        }        
    }
}
