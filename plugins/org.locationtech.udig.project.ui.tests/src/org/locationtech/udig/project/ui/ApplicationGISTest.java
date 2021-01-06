package org.locationtech.udig.project.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.locationtech.udig.core.internal.ExtensionPointUtil.list;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.core.internal.ExtensionPointItemCreator;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.internal.ActiveMapTracker;

public class ApplicationGISTest {


    @Before
    public void setUp() {
        ApplicationGIS.setActiveMapTracker(null);
    }

    @After
    public void shutDown() {
        ApplicationGIS.setActiveMapTracker(null);
    }

    @Test
    public void activeMapTrackerIsRegisteredIStartupExtension() {
        final String expectedElementRegistredAsIStartup = "expectedElementRegistredAsIStartup";
        @SuppressWarnings("unchecked")
        List<String> validationList = list("org.eclipse.ui.startup",
                new ExtensionPointItemCreator() {

            @Override
            public Object createItem(IExtension extension, IConfigurationElement element)
                    throws Exception {
                String iStartupClass = element.getAttribute("class");
                if (iStartupClass != null
                        && ActiveMapTracker.class.getName().equals(iStartupClass)) {
                            return expectedElementRegistredAsIStartup;
                }
                return null;
            }
        });
        assertThat("missing registered IStartup extension for ActiveMapTracker", validationList,
                hasItem(expectedElementRegistredAsIStartup));
    }

    @Test
    public void noMapInstanceIfActiveMapTrackerNotSet() {
        IMap activeMap = ApplicationGIS.getActiveMap();
        assertNotNull(activeMap);
        assertEquals(ApplicationGIS.NO_MAP, activeMap);
    }

    @Test
    public void openMapsReturnsEmptyCollectionIfActiveMapTrackerNotSet() {
        Collection<? extends IMap> openMap = ApplicationGIS.getOpenMaps();
        assertNotNull(openMap);
        assertTrue(openMap.isEmpty());
    }

    @Test
    public void visibleMapsReturnsEmptyCollectionIfActiveMapTrackerNotSet() throws Exception {
        Collection<? extends IMap> visibleMaps = ApplicationGIS.getVisibleMaps();
        assertNotNull(visibleMaps);
        assertTrue(visibleMaps.isEmpty());
    }

    @Test(expected = Error.class)
    public void setActiveMapTrackerTwiceCausesError() throws Exception {
        ActiveMapTracker first = new ActiveMapTracker();

        // first is fine
        ApplicationGIS.setActiveMapTracker(first);

        ActiveMapTracker second = new ActiveMapTracker();

        ApplicationGIS.setActiveMapTracker(second);
    }
}
