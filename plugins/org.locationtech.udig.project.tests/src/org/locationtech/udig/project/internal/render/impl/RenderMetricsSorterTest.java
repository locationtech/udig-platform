package org.locationtech.udig.project.internal.render.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.easymock.EasyMock;
import org.junit.Test;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.impl.IEListVisitor;
import org.locationtech.udig.project.internal.impl.ISynchronizedEListIteration;

public class RenderMetricsSorterTest {

    class MyTestSynchedList extends ArrayList<Layer> implements ISynchronizedEListIteration<Layer> {

        private static final long serialVersionUID = -2865786804588259540L;

        @Override
        public void syncedIteration(final IEListVisitor<Layer> visitor) {
            final ListIterator<Layer> listIterator = listIterator();
            while (listIterator.hasNext()) {
                visitor.visit(listIterator.next());
            }
        }

        @Override
        public boolean addAll(final Collection<? extends Layer> c) {
            throw new AssertionError("Unexpected call, syncedIteration should be called instead");
        }
    }

    @Test
    public void testCopiedLayerListAsArrayList() {
        final Layer testLayer = EasyMock.createNiceMock(Layer.class);

        EasyMock.replay(testLayer);
        final List<Layer> layers = Collections.singletonList(testLayer);

        final RenderMetricsSorter renderMetricsSorter = new RenderMetricsSorter(layers);
        final List<Layer> layerOfSorter = renderMetricsSorter.getLayers();
        assertTrue(layers.contains(testLayer));
        assertTrue(layerOfSorter.contains(testLayer));
        assertEquals(layers.size(), layerOfSorter.size());
        assertNotSame(layers, layerOfSorter);

        EasyMock.verify(testLayer);
    }

    @Test
    public void testCopiedLayerListAsSynchedEList() {
        final ArrayList<Layer> synchedLayerList = new MyTestSynchedList();

        final Layer testLayer = EasyMock.createNiceMock(Layer.class);
        synchedLayerList.add(testLayer);

        EasyMock.replay(testLayer);

        final RenderMetricsSorter renderMetricsSorter = new RenderMetricsSorter(synchedLayerList);
        final List<Layer> layerOfSorter = renderMetricsSorter.getLayers();
        assertTrue(synchedLayerList.contains(testLayer));
        assertTrue(layerOfSorter.contains(testLayer));
        assertEquals(synchedLayerList.size(), layerOfSorter.size());
        assertNotSame(synchedLayerList, layerOfSorter);

        EasyMock.verify(testLayer);
    }

    @Test
    public void testHandleNullConstructor() {
        final RenderMetricsSorter renderMetricsSorter = new RenderMetricsSorter(null);
        assertNotNull(renderMetricsSorter);
        assertTrue(renderMetricsSorter.getLayers().isEmpty());
    }
}
