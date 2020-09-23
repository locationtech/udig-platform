package org.locationtech.udig.project.internal.render.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.junit.Test;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.preferences.PreferenceConstants;

public class CompositeRendererHideJobsTest {

    class TestRenderer extends RenderExecutorComposite {
        public RenderJob getRenderJob() {
            return renderJob;
        }
    }

    @Test
    public void defaultHideRenderJobIsFalse() {
        ScopedPreferenceStore preferenceStore = ProjectPlugin.getPlugin().getPreferenceStore();
        assertFalse(preferenceStore.getDefaultBoolean(PreferenceConstants.P_HIDE_RENDER_JOB));
    }

    @Test
    public void jobIsShownInProgressView() {
        assertJobHasSystemState(false, false);
    }

    @Test
    public void jobIsSystemJobAndThereforeNotVisibleInProgressView() {
        assertJobHasSystemState(true, false);
    }

    @Test
    public void jobIsStillSystemJobAfterRenderIsSet() {
        assertJobHasSystemState(true, true);
    }

    @Test
    public void jobIsStillNonSystemJobAfterRenderIsSet() {
        assertJobHasSystemState(false, true);
    }

    private void assertJobHasSystemState(boolean expectedState, boolean setRenderer) {
        ScopedPreferenceStore preferenceStore = ProjectPlugin.getPlugin().getPreferenceStore();
        preferenceStore.setValue(PreferenceConstants.P_HIDE_RENDER_JOB, expectedState);
        TestRenderer testRenderer = new TestRenderer();
        if (setRenderer) {
            testRenderer.setRenderer(null);
        }
        assertEquals(expectedState, testRenderer.getRenderJob().isSystem());
    }

}
