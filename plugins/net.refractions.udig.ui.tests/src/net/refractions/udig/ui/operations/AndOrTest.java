package net.refractions.udig.ui.operations;

import junit.framework.TestCase;

public class AndOrTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();

    }

    public void testOrAccept() {
        Or or = new Or();
        or.getFilters().add(new NoCacheNoBlock(false));
        or.getFilters().add(new NoCacheNoBlock(false));
        or.getFilters().add(new NoCacheNoBlock(false));
        assertFalse(or.accept("")); //$NON-NLS-1$

        or.getFilters().add(new NoCacheNoBlock(true));
        assertTrue(or.accept("")); //$NON-NLS-1$

    }
    public void testAndAccept() {
        And and = new And();
        and.getFilters().add(new NoCacheNoBlock(true));
        and.getFilters().add(new NoCacheNoBlock(true));
        and.getFilters().add(new NoCacheNoBlock(true));
        assertTrue(and.accept("")); //$NON-NLS-1$

        and.getFilters().add(new NoCacheNoBlock(false));
        assertFalse(and.accept("")); //$NON-NLS-1$
    }

    public void testANDListenersMethods() {
        And and = new And();
        final int[] added = new int[1];
        final int[] removed = new int[1];
        and.getFilters().add(new NoCacheNoBlock(false){
            @Override
            public void addListener( IOpFilterListener listener ) {
                added[0]++;
            }

            public void removeListener( IOpFilterListener listener ) {
                removed[0]++;
            }
        });
        and.getFilters().add(new NoCacheNoBlock(false){
            @Override
            public void addListener( IOpFilterListener listener ) {
                added[0]++;
            }

            public void removeListener( IOpFilterListener listener ) {
                removed[0]++;
            }
        });
        and.getFilters().add(new NoCacheNoBlock(false){
            @Override
            public void addListener( IOpFilterListener listener ) {
                added[0]++;
            }

            public void removeListener( IOpFilterListener listener ) {
                removed[0]++;
            }
        });

        added[0] = 0;

        and.addListener(new IOpFilterListener(){

            public void notifyChange( Object changed ) {
            }
        });

        assertEquals(3, added[0]);
        removed[0] = 0;

        and.removeListener(new IOpFilterListener(){

            public void notifyChange( Object changed ) {
            }
        });

        assertEquals(3, removed[0]);

    }

    public void testORListenersMethods() {
        Or or = new Or();
        final int[] added = new int[1];
        final int[] removed = new int[1];
        or.getFilters().add(new NoCacheNoBlock(false){
            @Override
            public void addListener( IOpFilterListener listener ) {
                added[0]++;
            }

            public void removeListener( IOpFilterListener listener ) {
                removed[0]++;
            }
        });
        or.getFilters().add(new NoCacheNoBlock(false){
            @Override
            public void addListener( IOpFilterListener listener ) {
                added[0]++;
            }

            public void removeListener( IOpFilterListener listener ) {
                removed[0]++;
            }
        });
        or.getFilters().add(new NoCacheNoBlock(false){
            @Override
            public void addListener( IOpFilterListener listener ) {
                added[0]++;
            }

            public void removeListener( IOpFilterListener listener ) {
                removed[0]++;
            }
        });

        added[0] = 0;

        or.addListener(new IOpFilterListener(){

            public void notifyChange( Object changed ) {
            }
        });

        assertEquals(3, added[0]);
        removed[0] = 0;

        or.removeListener(new IOpFilterListener(){

            public void notifyChange( Object changed ) {
            }
        });

        assertEquals(3, removed[0]);

    }
    public void testANDCanCacheResult() {
        And and = new And();

        and.getFilters().add(new CacheBlock(false));
        and.getFilters().add(new CacheNoBlock(false));

        assertTrue(and.canCacheResult());

        and.getFilters().add(new NoCacheBlock(false));

        assertFalse(and.canCacheResult());
    }
    public void testORCanCacheResult() {
        Or or = new Or();

        or.getFilters().add(new CacheBlock(false));
        or.getFilters().add(new CacheNoBlock(false));

        assertTrue(or.canCacheResult());

        or.getFilters().add(new NoCacheBlock(false));

        assertFalse(or.canCacheResult());
    }

    public void testANDIsBlocking() {
        And and = new And();

        and.getFilters().add(new CacheNoBlock(false));
        and.getFilters().add(new NoCacheNoBlock(false));

        assertFalse(and.isBlocking());

        and.getFilters().add(new NoCacheBlock(false));

        assertTrue(and.isBlocking());
    }
    public void testORIsBlocking() {
        Or or = new Or();

        or.getFilters().add(new CacheNoBlock(false));
        or.getFilters().add(new NoCacheNoBlock(false));

        assertFalse(or.isBlocking());

        or.getFilters().add(new NoCacheBlock(false));

        assertTrue(or.isBlocking());
    }
}
