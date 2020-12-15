/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2020, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.operations;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.ui.operations.IOpFilterListener;
import org.opengis.filter.Filter;

public class LayerSelectionPropertyTest {

    ILayer layerMock = createNiceMock(Layer.class);

    IOpFilterListener listenerMock = createNiceMock(IOpFilterListener.class);

    @Test
    public void testLayerGetsNotificationOnFilterChange() {
        expect(layerMock.getFilter()).andReturn(Filter.EXCLUDE).anyTimes();
        layerMock.addListener(anyObject());
        expectLastCall().times(2);

        LayerSelectionProperty layerSelectionProperty = new LayerSelectionProperty();
        replay(layerMock, listenerMock);
        // first call tries to add Listener
        layerSelectionProperty.isTrue(layerMock, "Whatever");
        // second call tries to add Listener again
        layerSelectionProperty.isTrue(layerMock, "Whatever");

        verify(layerMock, listenerMock);
    }

    @Test
    public void testCachingIsDisabled() {
        assertFalse(new LayerSelectionProperty().canCacheResult());
    }
}
