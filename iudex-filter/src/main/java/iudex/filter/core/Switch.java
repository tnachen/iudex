package iudex.filter.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gravitext.htmap.UniMap;
import com.gravitext.util.Closeables;

import iudex.filter.Filter;
import iudex.filter.FilterContainer;
import iudex.filter.FilterException;
import iudex.filter.FilterListener;
import iudex.filter.NoOpListener;

/**
 * Supports branching of filters by sequentially testing a list of predicates
 * and executing the consequent of the first success.
 */
public final class Switch implements FilterContainer
{
    public Switch()
    {
    }

    /**
     * Set listener for any unhandled FilterException's from predicate or
     * consequent filters. Full event may be achieved by using FilterChain's for
     * either or both predicate or consequent.
     */
    public void setListener( FilterListener listener )
    {
        _listener = listener;
    }

    /**
     * Add a filter to be used for a given value. Note that the null value
     * is also a permissible case (filtered content value not set).
     */
    public void addProposition( Filter predicate, Filter consequent )
    {
        _props.add( new Proposition( predicate, consequent ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Filter> children()
    {
        return new ArrayList<Filter>( _props );
    }

    /**
     * {@inheritDoc}
     * @return result of consequent of matching predicate, or false if no
     * matching predicate was found.
     */
    @Override
    public boolean filter( final UniMap content )
    {
        for( Proposition prop : _props ) {
            if( prop.testPredicate( content ) ) {
                return prop.filterConsequent( content );
            }
        }
        return false;
        //FIXME: Or always return true?
    }

    @Override
    public void close()
    {
        for( Proposition prop : _props ) {
            prop.close();
        }
        _props.clear();
    }

    private final class Proposition implements FilterContainer
    {

        public Proposition( Filter predicate, Filter consequent )
        {
            _predicate = predicate;
            _consequent = consequent;
        }

        @Override
        public List<Filter> children()
        {
            return Arrays.asList( _predicate, _consequent );
        }

        public boolean filterSafe( final Filter filter, final UniMap content )
        {
            boolean passed = true;
            try {
                passed = filter.filter( content );
            }
            catch( FilterException e ) {
                _listener.failed( filter, content, e );
                passed = false;
            }
            return passed;
        }

        public boolean testPredicate( final UniMap content )
        {
            return filterSafe( _predicate, content );
        }

        public boolean filterConsequent( final UniMap content )
        {
            return filterSafe( _consequent, content );
        }

        @Override
        public boolean filter( final UniMap content )
        {
            return testPredicate( content ) && filterConsequent( content );
        }

        @Override
        public void close()
        {
            Closeables.closeIf( _predicate );
            Closeables.closeIf( _consequent );
        }

        private final Filter _predicate;
        private final Filter _consequent;
    }

    private final List<Proposition> _props = new ArrayList<Proposition>();
    private FilterListener _listener = new NoOpListener();
}
