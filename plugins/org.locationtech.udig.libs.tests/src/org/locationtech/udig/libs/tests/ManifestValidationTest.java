package org.locationtech.udig.libs.tests;

import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.ManifestElement;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.udig.libs.internal.Activator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

public class ManifestValidationTest {

    private static Bundle libsBundle;

    private static Set<String> manifestClasspathEntries;

    @BeforeClass
    public static void beforeClass() throws BundleException {
        libsBundle = Platform.getBundle(Activator.ID);
        manifestClasspathEntries = getManifestClasspathEntries(libsBundle);
    }

    @Test
    public void allJarsInClasspathAreBundled() throws Exception {
        Set<String> missingFilesReferencedInClasspath = new HashSet<>();
        for (String clasthpathLibEntry : manifestClasspathEntries) {
            URL find = FileLocator.find(libsBundle, new Path(clasthpathLibEntry));
            if (find == null) {
                missingFilesReferencedInClasspath.add(clasthpathLibEntry);
            }
        }
        StringBuffer sb = new StringBuffer();
        missingFilesReferencedInClasspath.stream().sorted().forEach(s -> sb.append(" " + s + "\n"));

        assertTrue(
                "Expected that all jars listed in classpath are present\nClasspath-entries but missing on File-System:\n"
                        + sb.toString(),
                missingFilesReferencedInClasspath.isEmpty());
    }

    @Test
    public void allExistingJarsAreInClasspath() throws Exception {
        Enumeration<URL> libs = libsBundle.findEntries("lib/", "*.jar", false);
        Set<String> jarsNotListedInClasspath = new HashSet<>();

        while (libs.hasMoreElements()) {
            URL libURL = libs.nextElement();
            String libUrl = libURL.toString();
            int lastIndexOf = libUrl.lastIndexOf("/");
            if (lastIndexOf > 0) {
                String substring = "lib" + libUrl.substring(lastIndexOf);
                if (!manifestClasspathEntries.contains(substring)) {
                    jarsNotListedInClasspath.add(substring);
                }
            }
        }
        StringBuffer sb = new StringBuffer();
        jarsNotListedInClasspath.stream().sorted().forEach(s -> sb.append(" " + s + "\n"));

        assertTrue(
                "Expected that available jars in lib folder of Bundle are not listed in Bundle-ClassPath of Manifest-File\nMissing Bundle-ClassPath entry for:\n"
                        + sb.toString(),
                jarsNotListedInClasspath.isEmpty());
    }

    @Test
    public void checkThatLibsAreNoteBundlesBecauseOtherBundlesProvideThese() {
        List<String> blacklist = new ArrayList<>();
        blacklist.add("jfreechart");
        blacklist.add("jcommon");
        blacklist.add("netcdf");
        blacklist.add("imageio-ext-netcdf");
        blacklist.add("opencsv");

        List<String> libsThatShouldNotBeBundled = manifestClasspathEntries.stream()
                .filter(p -> matches(p, blacklist)).collect(Collectors.toList());

        StringBuffer sb = new StringBuffer();
        libsThatShouldNotBeBundled.stream().sorted().forEach(s -> sb.append(" " + s + "\n"));

        assertTrue(
                "Expected empty List of black-listed libraries\nLibraries that should not be bundled here:\n"
                        + sb.toString(),
                libsThatShouldNotBeBundled.isEmpty());
    }

    private static Set<String> getManifestClasspathEntries(Bundle bundle) throws BundleException {
        ManifestElement[] elements = ManifestElement.parseHeader(Constants.BUNDLE_CLASSPATH,
                bundle.getHeaders("").get(Constants.BUNDLE_CLASSPATH));
        Set<String> libs = new HashSet<>();
        if (elements != null) {
            for (ManifestElement element : elements) {
                if (element.getValue().endsWith(".jar")) {
                    libs.add(element.getValue());
                }
            }
        }
        return libs;
    }

    private static boolean matches(String libEntry, List<String> blacklist) {
        for (String blEntry : blacklist) {
            if (libEntry.contains(blEntry)) {
                return true;
            }
        }
        return false;

    }
}
