package net.refractions.udig.tutorials.catalog.csv.internal;

import org.eclipse.core.runtime.Plugin;

public class Activator extends Plugin {
    private static Activator instance;
    public Activator() {
        instance = this;
    }
    public static Activator getDefault() {
        return instance;
    }
}
