package net.refractions.udig.project.internal.provider;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.awt.Color;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.ui.graphics.Glyph;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.junit.Before;
import org.junit.Test;

public class LayerItemProviderTest {

    private Map   map;
    private Layer layer;

    @Before
    public void setUp() throws Exception {
        map = MapTests.createDefaultMap("typename", 4, true, null); //$NON-NLS-1$
        layer = map.getLayersInternal().get(0);
    }

    @Test
    public void testGetImageObject() {

        AdapterFactory adapterFactoryImpl = new ProjectItemProviderAdapterFactory();
        LayerItemProvider itemProvider = new LayerItemProvider(adapterFactoryImpl);
        AdapterFactoryLabelProvider fac=new AdapterFactoryLabelProvider(adapterFactoryImpl);
        
        Image image = null;
        Image image2 = null;
        try {
            layer.eAdapters().add(itemProvider);
            image = (Image) fac.getImage(layer);
            assertNotNull(image);
            image2 = (Image) fac.getImage(layer);
            assertSame(image, image2);

            layer.getProperties().put(LayerItemProvider.GENERATED_ICON,
                    Glyph.geometry(Color.RED, Color.BLACK));

            image2 = (Image) fac.getImage(layer);
            assertNotSame(image, image2);
            
            layer.getProperties().put(LayerItemProvider.GENERATED_ICON,
                    Glyph.geometry(Color.GREEN, Color.BLACK));

            image = (Image) fac.getImage(layer);
            assertNotSame(image, image2);
            
            layer.setIcon(Glyph.geometry(Color.GREEN, Color.RED));
            image2 = (Image) fac.getImage(layer);
            assertNotSame(image, image2);
            
            layer.getProperties().put(LayerItemProvider.GENERATED_ICON,
                    Glyph.geometry(Color.GREEN, Color.BLUE));
            image = (Image) fac.getImage(layer);
            assertSame(image, image2);
            
        } finally {
            fac.dispose();
            itemProvider.dispose();
        }
    }

    @Test
    public void testGetTextObject() {
        // TODO
    }

}
