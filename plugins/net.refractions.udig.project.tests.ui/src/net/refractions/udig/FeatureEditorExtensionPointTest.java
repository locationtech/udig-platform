package net.refractions.udig;

import java.net.URI;

import net.refractions.udig.project.ui.internal.FeatureEditorExtensionProcessor;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.geotools.feature.DefaultAttributeTypeFactory;
import org.geotools.feature.DefaultFeatureTypeFactory;
import org.geotools.feature.FeatureTypeBuilder;
import org.geotools.feature.GeometryAttributeType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;

public class FeatureEditorExtensionPointTest extends AbstractProjectUITestCase {


    @SuppressWarnings("deprecation")
    public void testGetEditWithMenu() throws Exception{
        FeatureEditorExtensionProcessor processor=new FeatureEditorExtensionProcessor();
        IContributionItem item=processor.getEditWithFeatureMenu(new StructuredSelection());
        assertTrue(item instanceof GroupMarker);
        FeatureTypeBuilder builder=new DefaultFeatureTypeFactory();
        builder.setName("testType"); //$NON-NLS-1$
        builder.setNamespace(new URI("http://test.uri")); //$NON-NLS-1$
        builder.setDefaultGeometry((GeometryAttributeType) DefaultAttributeTypeFactory.newAttributeType("geo",Geometry.class)); //$NON-NLS-1$
        item = processor.getEditWithFeatureMenu(new StructuredSelection(builder.getFeatureType().create(new Object[]{null})));
        MenuManager manager=(MenuManager) item;
        assertTrue(0<((MenuManager) item).getItems().length );
        assertNotNull(manager.find("net.refractions.udig.feature.editor.MatchAnyGeom")); //$NON-NLS-1$
        assertNotNull(manager.find("net.refractions.udig.feature.editor.MatchGeomNamedGeo")); //$NON-NLS-1$
        assertNotNull(manager.find("net.refractions.udig.feature.editor.MatchOnTypeName")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.NeverShown")); //$NON-NLS-1$

        builder=new DefaultFeatureTypeFactory();
        builder.setName("testType2"); //$NON-NLS-1$
        builder.setNamespace(new URI("http://test.uri")); //$NON-NLS-1$
        builder.setDefaultGeometry((GeometryAttributeType) DefaultAttributeTypeFactory.newAttributeType("the_geom",MultiLineString.class)); //$NON-NLS-1$
        item = processor.getEditWithFeatureMenu(new StructuredSelection(builder.getFeatureType().create(new Object[]{null})));
        manager=(MenuManager) item;
        assertTrue(0<((MenuManager) item).getItems().length );
        assertNull(manager.find("net.refractions.udig.feature.editor.MatchAnyGeom")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.MatchGeomNamedGeo")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.MatchOnTypeName")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.NeverShown")); //$NON-NLS-1$

        builder=new DefaultFeatureTypeFactory();
        builder.setName("testType"); //$NON-NLS-1$
        builder.setNamespace(new URI("http://test.uri1")); //$NON-NLS-1$
        builder.setDefaultGeometry((GeometryAttributeType) DefaultAttributeTypeFactory.newAttributeType("the_geom",Geometry.class)); //$NON-NLS-1$
        item = processor.getEditWithFeatureMenu(new StructuredSelection(builder.getFeatureType().create(new Object[]{null})));
        manager=(MenuManager) item;
        assertTrue(0<((MenuManager) item).getItems().length );
        assertNotNull(manager.find("net.refractions.udig.feature.editor.MatchAnyGeom")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.MatchGeomNamedGeo")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.MatchOnTypeName")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.NeverShown")); //$NON-NLS-1$

        builder=new DefaultFeatureTypeFactory();
        builder.setName("testType"); //$NON-NLS-1$
        builder.setNamespace(new URI("http://test.uri1")); //$NON-NLS-1$
        item = processor.getEditWithFeatureMenu(new StructuredSelection(builder.getFeatureType().create(new Object[]{})));
        manager=(MenuManager) item;
        assertTrue(0<((MenuManager) item).getItems().length );
        assertNull(manager.find("net.refractions.udig.feature.editor.MatchAnyGeom")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.MatchGeomNamedGeo")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.MatchOnTypeName")); //$NON-NLS-1$
        assertNull(manager.find("net.refractions.udig.feature.editor.NeverShown")); //$NON-NLS-1$
    }

    @SuppressWarnings("deprecation")
    public void testOpenMemory()throws Exception{
        FeatureEditorExtensionProcessor processor=new FeatureEditorExtensionProcessor();
        FeatureTypeBuilder builder=new DefaultFeatureTypeFactory();

        builder.setName("testType"); //$NON-NLS-1$
        builder.setNamespace(new URI("http://test.uri")); //$NON-NLS-1$
        builder.setDefaultGeometry((GeometryAttributeType) DefaultAttributeTypeFactory.newAttributeType("the_geo",Geometry.class)); //$NON-NLS-1$
        StructuredSelection selection1 = new StructuredSelection(builder.getFeatureType().create(new Object[]{null}));
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

        DefaultFeatureTypeFactory builder2 = new DefaultFeatureTypeFactory();
        builder2.setName("testType"); //$NON-NLS-1$
        builder2.setNamespace(new URI("http://test.uri1")); //$NON-NLS-1$
        builder2.setDefaultGeometry((GeometryAttributeType) DefaultAttributeTypeFactory.newAttributeType("geo",Geometry.class)); //$NON-NLS-1$
        StructuredSelection selection2 = new StructuredSelection(builder2.getFeatureType().create(new Object[]{null}));
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
