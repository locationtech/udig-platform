package org.locationtech.udig.project.ui.internal;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.preferences.PreferenceConstants;

@RunWith(Parameterized.class)
public class CRSContributionItemDisableStateButtonTest {

    private String givenPropertyValue;

    private boolean expectedState;

    @SuppressWarnings("rawtypes")
    @Parameters
    public static Collection disabledButton() {
        return asList(new Object[][] {
                { "True", true }, { "tRuE", true }, { "true", true }, { "false", false },
                { "False", false }, });
    }

    public CRSContributionItemDisableStateButtonTest(String givenPropertyValue, boolean expectedState) {
        this.givenPropertyValue = givenPropertyValue;
        this.expectedState = expectedState;
    }

    @Test
    public void testDisabledState() {
        ProjectPlugin.getPlugin().getPreferenceStore().setValue(
                PreferenceConstants.P_DISABLE_CRS_SELECTION,
                givenPropertyValue);

        assertEquals(expectedState, CRSContributionItem.isCRSSelectionDisabled());

    }
}
