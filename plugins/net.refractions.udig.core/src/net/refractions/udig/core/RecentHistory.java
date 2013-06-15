/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.core;

import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class to manage recent history preferences/settings. This class is used to meet the user
 * interface guideline on remembering user input.
 * <p>
 * This class replaces previous addToHistory based on ConfigurationWizardMainPage. It has been made
 * into a stand alone class supporting typed elements rather than only Strings.
 * 
 * <pre>
 * <code>
 *   RecentHistory<String> recent = new RecentHistory<String>( settings.getArray(WMSC_RECENT) );
 *   recent.add( url);
 *   settings.put(WMSC_RECENT, recent.toArray(new String[recent.size()]));
 *   </code>
 * </pre>
 * 
 * @author Jody Garnett (LocationTech)
 * @since 1.4.0
 */
public class RecentHistory<T> extends AbstractCollection<T> {
    private final static int DEFAULT_LIMIT = 15;

    private final int LIMIT;

    private List<T> history;

    public RecentHistory(T[] array) {
        this(array, DEFAULT_LIMIT);
    }

    public RecentHistory(T[] array, int limit) {
        LIMIT = limit;
        history = new ArrayList<T>();
        if (array != null) {
            for (T item : array) {
                add(item); // done this way to prevent duplicates
            }
        }
    }

    /**
     * Remembers the provided entry in recent history.
     * <p>
     * The entry will be recommended at the top of the list as it is considered most recent.
     * 
     * @param entry
     */
    public boolean add(T entry) {
        if (history.indexOf(entry) == 0) {
            return false; // already most recent - collection not changed
        }
        history.remove(entry);
        history.add(0, entry);
        if (history.size() > LIMIT) {
            history.remove(LIMIT); // trim to LIMIT
        }
        return true; // history changed
    }

    @Override
    public Iterator<T> iterator() {
        return history.iterator();
    }

    @Override
    public int size() {
        return history.size();
    }

    /**
     * Static utility method allowing the recent history workflow to be applied to an Array.
     * 
     * @param array
     * @param entry
     * @return modified array with entry added to the front of the array as recent history
     */
    @SuppressWarnings("unchecked")
    public static <E> E[] addRecent(E[] array, E entry) {
        List<E> list = new ArrayList<E>();
        if (array != null) {
            list.addAll(Arrays.asList(array));
        }
        list.remove(entry);
        list.add(0, entry);
        if (list.size() > DEFAULT_LIMIT) {
            list = list.subList(0, DEFAULT_LIMIT); // trim to size
        }
        return list.toArray((E[]) Array.newInstance(entry.getClass(), list.size()));
    }
}
