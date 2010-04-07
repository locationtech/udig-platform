package net.refractions.udig.catalog.memory;

/**
 * Permits the instantiation of custom version of ActiveMemoryDataStore.
 * @author rgould
 * @since 1.1.0
 */
public interface MemoryDSFactory {

    /**
     * Construct and return a new instance of an ActiveMemoryDataStore.
     * Typically this is a custom sub-class implementation
     *
     * @return a new instance of an ActiveMemoryDataStore
     */
    ActiveMemoryDataStore createNewDS();
}
