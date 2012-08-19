package net.refractions.udig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import net.refractions.udig.project.ui.internal.FeatureEditorExtensionProcessor;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;

public class FeatureEditorExtensionPointTest extends AbstractProjectUITestCase {
    
    
    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testGetEditWithMenu() throws Exception{
        FeatureEditorExtensionProcessor processor=new FeatureEditorExtensionProcessor();
        IContributionItem item=processor.getEditWithFeatureMenu(new StructuredSelection());
        assertTrue(item instanceof GroupMarker);
        SimpleFeatureTypeBuilder builder=new SimpleFeatureTypeBuilder();
        builder.setName("testType"); //$NON-NLS-1$
        builder.setNamespaceURI(new URI("http://test.uri")); //$NON-NLS-1$
        builder.add("geo",Geometry.class); //$NON-NLS-1$
        builder.setDefaultGeometry("geo");
        SimpleFeatureType featureType = builder.buildFeatureType();
		Object[] defaultAtts = new Object[]{null};
		String id = "id";
		item = processor.getEditWithFeatureMenu(new StructuredSelection(SimpleFeatureBuilder.build(featureType, defaultAtts, id)));
        MenuManager manager=(MenuManager) item;
        assertTrue(0<((MenuManager) item).getItems().length );
        assertNotNull(manager.find("net.refractions.udig.feature.editor.MatchAnyGeom")); //$NON-NLS-1$
        assertNotNull(manager.find("net.refractions.udig.feature.editor.MatchGeomNamedGeo")); //$NON-NLS-1$
        assertNotNull(manager.find("net.refractions.udig.feature.editor.MatchOnTypeName")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.NeverShown")); //$NON-NLS-1$

        builder=new SimpleFeatureTypeBuilder();
        builder.setName("testType2"); //$NON-NLS-1$
        builder.setNamespaceURI(new URI("http://test.uri")); //$NON-NLS-1$
        builder.add("the_geom",MultiLineString.class);
        StructuredSelection selection = new StructuredSelection(SimpleFeatureBuilder.build(builder.buildFeatureType(), defaultAtts, id));
		item = processor.getEditWithFeatureMenu(selection);
        manager=(MenuManager) item;
        assertTrue(0<((MenuManager) item).getItems().length );
        assertNull(manager.find("net.refractions.udig.feature.editor.MatchAnyGeom")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.MatchGeomNamedGeo")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.MatchOnTypeName")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.NeverShown")); //$NON-NLS-1$

        builder=new SimpleFeatureTypeBuilder();
        builder.setName("testType"); //$NON-NLS-1$
        builder.setNamespaceURI(new URI("http://test.uri1")); //$NON-NLS-1$
        builder.add("the_geom",Geometry.class); //$NON-NLS-1$
        item = processor.getEditWithFeatureMenu(new StructuredSelection(SimpleFeatureBuilder.build(builder.buildFeatureType(), defaultAtts, id)));
        manager=(MenuManager) item;
        assertTrue(0<((MenuManager) item).getItems().length );
        assertNotNull(manager.find("net.refractions.udig.feature.editor.MatchAnyGeom")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.MatchGeomNamedGeo")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.MatchOnTypeName")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.NeverShown")); //$NON-NLS-1$

        builder=new SimpleFeatureTypeBuilder();
        builder.setName("testType"); //$NON-NLS-1$
        builder.setNamespaceURI(new URI("http://test.uri1")); //$NON-NLS-1$
        item = processor.getEditWithFeatureMenu(new StructuredSelection(SimpleFeatureBuilder.build(builder.buildFeatureType(), new Object[0], id)));
        manager=(MenuManager) item;
        assertTrue(0<((MenuManager) item).getItems().length );
        assertNull(manager.find("net.refractions.udig.feature.editor.MatchAnyGeom")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.MatchGeomNamedGeo")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.MatchOnTypeName")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.NeverShown")); //$NON-NLS-1$
    }
    
    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testOpenMemory()throws Exception{
        FeatureEditorExtensionProcessor processor=new FeatureEditorExtensionProcessor();
        SimpleFeatureTypeBuilder builder=new SimpleFeatureTypeBuilder();
        
        builder.setName("testType"); //$NON-NLS-1$
        builder.setNamespaceURI(new URI("http://test.uri")); //$NON-NLS-1$
        builder.add("the_geo",Geometry.class); //$NON-NLS-1$
        Object[] defaultAtts = new Object[]{null};
		String id = "id";
		StructuredSelection selection1 = new StructuredSelection(SimpleFeatureBuilder.build(builder.buildFeatureType(), defaultAtts, id));
        IContributionItem item = processor.getEditFeatureAction(selection1);
        assertEquals( "net.refractions.udig.feature.editor.MatchOnTypeName", item.getId() ); //$NON-NLS-1$

        Event event=new Event();
        event.display=Display.getDefault();
        MenuManager editWith=(MenuManager) processor.getEditWithFeatureMenu(selection1);
        IContributionItem[] items = editWith.getItems();
        for( IContributionItem item2 : items ) {
            if ( !(item2.getId().equals(item.getId() ) ) ){
                item=item2;
                //simulate the ui menubutton being pressed.
                ((ActionContributionItem)item2).getAction().setChecked(true);
                
                ((ActionContributionItem)item2).getAction().runWithEvent(event);
                break;
            }
        }
        assertSame(item.getId(), processor.getEditFeatureAction(selection1).getId());
        editWith=(MenuManager) processor.getEditWithFeatureMenu(selection1);
        item= editWith.findUsingPath(item.getId());
        assertTrue( ((ActionContributionItem)item).getAction().isChecked() );
        
        SimpleFeatureTypeBuilder builder2 = new SimpleFeatureTypeBuilder();
        builder2.setName("testType"); //$NON-NLS-1$
        builder2.setNamespaceURI(new URI("http://test.uri1")); //$NON-NLS-1$
        builder2.add("geo",Geometry.class); //$NON-NLS-1$
        StructuredSelection selection2 = new StructuredSelection(SimpleFeatureBuilder.build(builder.buildFeatureType(), defaultAtts, id));
        IContributionItem item6 = processor.getEditFeatureAction(selection2);
        assertEquals( "net.refractions.udig.feature.editor.MatchGeomNamedGeo", item6.getId() ); //$NON-NLS-1$

        editWith=(MenuManager) processor.getEditWithFeatureMenu(selection2);
        items = editWith.getItems();
        for( IContributionItem item2 : items ) {
            if ( !(item2.getId().equals(item6.getId() ) ) ){
                item6=item2;
                //simulate the ui menubutton being pressed.
                ((ActionContributionItem)item2).getAction().setChecked(true);
                ((ActionContributionItem)item2).getAction().runWithEvent(event);
                break;
            }
        }
        
        assertSame(item6.getId(), processor.getEditFeatureAction(selection2).getId());
        editWith=(MenuManager) processor.getEditWithFeatureMenu(selection1);
        item6= editWith.findUsingPath(item6.getId());
        assertTrue( ((ActionContributionItem)item6).getAction().isChecked() );


        assertSame(item.getId(), processor.getEditFeatureAction(selection1).getId());
    }
}
