package net.refractions.udig.project.internal.provider;

import java.awt.Color;

import junit.framework.TestCase;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.ui.graphics.Glyph;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.swt.graphics.Image;

public class LayerItemProviderTest extends TestCase {

    private Map   map;
    private Layer layer;

    protected void setUp() throws Exception {
        super.setUp();
        map = MapTests.createDefaultMap("typename", 4, true, null); //$NON-NLS-1$
        layer = map.getLayersInternal().get(0);
    }

    @SuppressWarnings("unchecked")
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
            
            layer.setGlyph(Glyph.geometry(Color.GREEN, Color.RED));
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

    public void testGetTextObject() {
        // TODO
    }

}
