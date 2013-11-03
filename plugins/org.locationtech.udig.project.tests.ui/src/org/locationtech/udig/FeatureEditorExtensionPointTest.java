/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.locationtech.udig.project.ui.internal.FeatureEditorExtensionProcessor;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;

@SuppressWarnings("nls")
public class FeatureEditorExtensionPointTest extends AbstractProjectUITestCase {
    
    private static final String MATCH_ALL = "org.locationtech.udig.feature.editor.MatchAll";
    private static final String MATCH_ON_TYPE_NAME = "org.locationtech.udig.feature.editor.MatchOnTypeName";
    private static final String MATCH_GEOM_NAMED_GEO = "org.locationtech.udig.feature.editor.MatchGeomNamedGeo";
    private static final String MATCH_ANY_GEOM = "org.locationtech.udig.feature.editor.MatchAnyGeom";
    private static final String NEVER_SHOWN = "org.locationtech.udig.feature.editor.NeverShown";

    private static final String TEST_TYPE = "testType";
    private static final String INVALID_TEST_TYPE = "testType2";
 
    private static final String TEST_URI = "http://test.uri";
    private static final String INVALID_TEST_URI = "http://test.uri1";

    private static final Object[] DEFAULT_ATTS = new Object[]{null};
    private static final String ID = "id";

    private FeatureEditorExtensionProcessor processor;

    @Before
    public void setUp() {
        processor = new FeatureEditorExtensionProcessor();
    }

    @Test
    public void testGetEditWithMenuGroupMarker() {
        StructuredSelection selection = new StructuredSelection();
        IContributionItem item = processor.getEditWithFeatureMenu(selection);
        
        assertTrue(item instanceof GroupMarker);
    }

    @Test
    public void testGetEditWithMenuAll() {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName(TEST_TYPE);
        builder.setNamespaceURI(TEST_URI);
        builder.add("geo", Geometry.class);
        builder.setDefaultGeometry("geo");
        
        MenuManager manager = getEditWithFeatureMenuManager(builder);
        
        assertTrue(manager.getItems().length > 0);
        assertNotNull(manager.find(MATCH_ALL));
        assertNotNull(manager.find(MATCH_ANY_GEOM));
        assertNotNull(manager.find(MATCH_GEOM_NAMED_GEO));
        assertNotNull(manager.find(MATCH_ON_TYPE_NAME));
        assertNull(manager.find(NEVER_SHOWN));
    }

    @Test
    public void testGetEditWithMenuAnyGeom() {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName(TEST_TYPE);
        builder.setNamespaceURI(INVALID_TEST_URI);
        builder.add("the_geom", Geometry.class);
        
        MenuManager manager = getEditWithFeatureMenuManager(builder);
        
        assertTrue(manager.getItems().length > 0);
        assertNotNull(manager.find(MATCH_ALL));
        assertNotNull(manager.find(MATCH_ANY_GEOM));
        assertNull(manager.find(MATCH_GEOM_NAMED_GEO));
        assertNull(manager.find(MATCH_ON_TYPE_NAME));
        assertNull(manager.find(NEVER_SHOWN));
    }

    @Test
    public void testGetEditWithMenuTypeName() {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName(TEST_TYPE);
        builder.setNamespaceURI(TEST_URI);
        builder.add("the_geom", MultiLineString.class);
        
        MenuManager manager = getEditWithFeatureMenuManager(builder);
        
        assertTrue(manager.getItems().length > 0);
        assertNotNull(manager.find(MATCH_ALL));
        assertNull(manager.find(MATCH_ANY_GEOM));
        assertNull(manager.find(MATCH_GEOM_NAMED_GEO));
        assertNotNull(manager.find(MATCH_ON_TYPE_NAME));
        assertNull(manager.find(NEVER_SHOWN));
    }

    @Test
    public void testGetEditWithMenuNone() {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName(INVALID_TEST_TYPE);
        builder.setNamespaceURI(TEST_URI);
        builder.add("the_geom", MultiLineString.class);
        
        MenuManager manager = getEditWithFeatureMenuManager(builder);
        
        assertTrue(manager.getItems().length > 0);
        assertNotNull(manager.find(MATCH_ALL));
        assertNull(manager.find(MATCH_ANY_GEOM));
        assertNull(manager.find(MATCH_GEOM_NAMED_GEO));
        assertNull(manager.find(MATCH_ON_TYPE_NAME));
        assertNull(manager.find(NEVER_SHOWN));
    }

    @Test
    public void testGetEditWithMenuNoGeom() {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName(TEST_TYPE);
        builder.setNamespaceURI(INVALID_TEST_URI);
        
        MenuManager manager = getEditWithFeatureMenuManager(builder, new Object[0], ID);
        
        assertTrue(manager.getItems().length > 0);
        assertNotNull(manager.find(MATCH_ALL));
        assertNull(manager.find(MATCH_ANY_GEOM));
        assertNull(manager.find(MATCH_GEOM_NAMED_GEO));
        assertNull(manager.find(MATCH_ON_TYPE_NAME));
        assertNull(manager.find(NEVER_SHOWN));
    }

    private MenuManager getEditWithFeatureMenuManager(SimpleFeatureTypeBuilder builder) {
        return getEditWithFeatureMenuManager(builder, DEFAULT_ATTS, ID);
    }

    private MenuManager getEditWithFeatureMenuManager(SimpleFeatureTypeBuilder builder, Object[] atts, String id) {
        SimpleFeatureType featureType = builder.buildFeatureType();
        StructuredSelection selection = new StructuredSelection(SimpleFeatureBuilder.build(featureType, atts, id));
        IContributionItem item = processor.getEditWithFeatureMenu(selection);
        MenuManager manager = (MenuManager) item;
        
        return manager;
    }

    @Ignore("test fails in tycho")
    @Test
    public void testOpenMemory() throws Exception {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName(TEST_TYPE);
        builder.setNamespaceURI(TEST_URI);
        builder.add("the_geo", Geometry.class);
        
        SimpleFeatureType featureType = builder.buildFeatureType();
        StructuredSelection selection1 = new StructuredSelection(SimpleFeatureBuilder.build(featureType, DEFAULT_ATTS, ID));
        IContributionItem item = processor.getEditFeatureAction(selection1);
        
        assertEquals(MATCH_ON_TYPE_NAME, item.getId());
        
        item = checkItems(selection1, item);
        
        assertSame(item.getId(), processor.getEditFeatureAction(selection1).getId());
        MenuManager editWith = (MenuManager) processor.getEditWithFeatureMenu(selection1);
        item = editWith.findUsingPath(item.getId());
        assertTrue(((ActionContributionItem) item).getAction().isChecked());
        
        SimpleFeatureTypeBuilder builder2 = new SimpleFeatureTypeBuilder();
        builder2.setName(TEST_TYPE);
        builder2.setNamespaceURI(INVALID_TEST_URI);
        builder2.add("geo", Geometry.class);
        
        SimpleFeatureType featureType2 = builder2.buildFeatureType();
        StructuredSelection selection2 = new StructuredSelection(SimpleFeatureBuilder.build(featureType2, DEFAULT_ATTS, ID));
        IContributionItem item2 = processor.getEditFeatureAction(selection2);
        
        assertEquals(MATCH_GEOM_NAMED_GEO, item2.getId());
        
        item2 = checkItems(selection2, item2);
        
        assertSame(item2.getId(), processor.getEditFeatureAction(selection2).getId());
        editWith = (MenuManager) processor.getEditWithFeatureMenu(selection1);
        item2 = editWith.findUsingPath(item2.getId());
        assertTrue( ((ActionContributionItem)item2).getAction().isChecked() );
        
        assertSame(item.getId(), processor.getEditFeatureAction(selection1).getId());
    }

    private IContributionItem checkItems(StructuredSelection selection, IContributionItem initialItem) {
        IContributionItem initialItemTmp = initialItem;
        Event event = new Event();
        event.display = Display.getDefault();
        MenuManager editWith = (MenuManager) processor.getEditWithFeatureMenu(selection);
        IContributionItem[] items = editWith.getItems();
        
        for (IContributionItem item : items) {
            if (!(item.getId().equals(initialItemTmp.getId()))) {
                initialItemTmp = item;
                
                //simulate the ui menubutton being pressed.
                ((ActionContributionItem) item).getAction().setChecked(true);
                ((ActionContributionItem) item).getAction().runWithEvent(event);
                break;
            }
        }
        
        return initialItemTmp;
    }
}
