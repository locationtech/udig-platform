package org.locationtech.udig.catalog.tests.ui;

import java.util.HashSet;
import java.util.Set;

import org.locationtech.udig.catalog.service.FormatProvider;

public class MoreThanOneExtensionFileProvider implements FormatProvider {

    public static final Set<String> testExtensions = new HashSet<String>();
    public MoreThanOneExtensionFileProvider() {
        testExtensions.add("*.test1");
        testExtensions.add("*.test2");
        testExtensions.add("*.test3");
    }

    @Override
    public Set<String> getExtensions() {
        return testExtensions;
    }

}
