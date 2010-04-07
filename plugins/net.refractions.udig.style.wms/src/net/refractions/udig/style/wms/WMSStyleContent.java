package net.refractions.udig.style.wms;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.StyleContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.StyleImpl;

public class WMSStyleContent extends StyleContent {

    /**
     * Key used to store a wms style on the style blackboard of a layer.
     */
    public static final String WMSSTYLE = "net.refractions.udig.render.wmsStyle"; //$NON-NLS-1$

	public WMSStyleContent() {
		super(WMSSTYLE);
	}

	@Override
	public Class getStyleClass() {
		return StyleImpl.class;
	}

	@Override
	public void save(IMemento memento, Object value) {
        StyleImpl style = (StyleImpl)value;
		memento.putString("value", style.getName()); //$NON-NLS-1$
	}

	@Override
	public Object load(IMemento memento) {
		return new StyleImpl(memento.getString("value")); //$NON-NLS-1$
	}

	@Override
	public Object load(URL url, IProgressMonitor monitor) throws IOException {
		return null;
	}

	@Override
	public Object createDefaultStyle(IGeoResource resource, Color colour, IProgressMonitor monitor) throws IOException {
        if( !resource.canResolve(Layer.class) ){
            return null;
        }
        List<StyleImpl> styles = WMSStyleConfigurator.getStyles(resource);
        if( styles.isEmpty() )
            return null;
        else
            return styles.get(0);
	}

}
