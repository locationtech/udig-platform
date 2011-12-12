package net.refractions.udig.catalog.internal.wms.tileset;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.catalog.internal.wmsc.WMSCServiceImpl;
import net.refractions.udig.catalog.wmsc.server.TileSet;
import net.refractions.udig.catalog.wmsc.server.TiledWebMapServer;
import net.refractions.udig.catalog.wmsc.server.WMSTileSet;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;
import net.refractions.udig.project.ui.preferences.PreferenceConstants;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.ows.AbstractOpenWebService;
import org.geotools.data.ows.CRSEnvelope;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.StyleImpl;
import org.geotools.data.wms.WebMapServer;
import org.geotools.referencing.CRS;

/**
 * An object to allow a IGeoResource to adapt to a TileSet, thus allowing any wms service the
 * ability to masquerade as a tile cache.
 * 
 * @author jhudson
 * @since 1.2.0
 */
public class WMSCTileSetAdapter implements IResolveAdapterFactory {

    @Override
    public boolean canAdapt( IResolve resolve, Class< ? extends Object> adapter ) {

        if (adapter.isAssignableFrom(TileSet.class)) {

            IGeoResource fs = (IGeoResource) resolve;

            try {
                IGeoResourceInfo info = fs.getInfo(null);

                boolean enabled = ProjectUIPlugin.getDefault().getPreferenceStore()
                        .getBoolean(PreferenceConstants.P_TILESET_ON_OFF + info.getName());

                if (!enabled) {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        return false;
    }

    @Override
    public Object adapt( IResolve resolve, Class< ? extends Object> adapter,
            IProgressMonitor monitor ) throws IOException {

        AbstractOpenWebService< ? , ? > server = null;

        if (resolve.canResolve(WebMapServer.class)) {
            server = resolve.resolve(WebMapServer.class, monitor);
        }
        if (resolve.canResolve(TiledWebMapServer.class)) {
            server = resolve.resolve(TiledWebMapServer.class, monitor);
        }

        /*
         * if there is no server for the tiles to come from, we can't/wont continue
         */
        if (server == null) {
            return null;
        }

        if (adapter.isAssignableFrom(TileSet.class)) {

            IGeoResource fs = (IGeoResource) resolve;
            IGeoResourceInfo info = fs.getInfo(monitor);

            URL caps = new URL(
                    server.getInfo().getSource()
                            + "version=" + server.getCapabilities().getVersion() + "&request=GetCapabilities"); //$NON-NLS-1$ //$NON-NLS-2$
            String srs = CRS.toSRS(info.getCRS());

            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put(WMSCServiceImpl.WMSC_URL_KEY, caps);

            TileSet tileset = new WMSTileSet();

            double minX = info.getBounds().getMinimum(0);
            double maxX = info.getBounds().getMaximum(0);
            double minY = info.getBounds().getMinimum(1);
            double maxY = info.getBounds().getMaximum(1);

            System.out.println(srs);
            System.out.println(minX);
            System.out.println(minY);
            System.out.println(maxX);
            System.out.println(maxY);
            
            CRSEnvelope bbox = new CRSEnvelope(srs, minX, minY, maxX, maxY);
            tileset.setBoundingBox(bbox);
            tileset.setCoorindateReferenceSystem(srs);

            int width = ProjectUIPlugin.getDefault().getPreferenceStore()
                    .getInt(PreferenceConstants.P_TILESET_WIDTH + info.getName());

            int height = ProjectUIPlugin.getDefault().getPreferenceStore()
                    .getInt(PreferenceConstants.P_TILESET_HEIGHT + info.getName());

            tileset.setWidth(width);
            tileset.setHeight(height);

            String imageType = ProjectUIPlugin.getDefault().getPreferenceStore()
                    .getString(PreferenceConstants.P_TILESET_IMAGE_TYPE + info.getName());
            tileset.setFormat(imageType);

            /*
             * The layer ID
             */
            tileset.setLayers(info.getName());

            String resolutions = ProjectUIPlugin.getDefault().getPreferenceStore()
                    .getString(PreferenceConstants.P_TILESET_RESOLUTIONS + info.getName());

            System.out.println(resolutions);
            
            /*
             * If we have no resolutions to try - we wont.
             */
            if ("".equals(resolutions)) { //$NON-NLS-1$
                return null;
            }

            tileset.setResolutions(resolutions);

            /*
             * The styles
             */
            String style = ""; //$NON-NLS-1$
            if (resolve.canResolve(Layer.class)) {
                Layer layer = resolve.resolve(Layer.class, monitor);
                StringBuilder sb = new StringBuilder(""); //$NON-NLS-1$
                for( StyleImpl layerStyle : layer.getStyles() ) {
                    sb.append(layerStyle.getName());
                }
                style = sb.toString();
            }
            tileset.setStyles(style);

            /*
             * The server is where tiles can be retrieved
             */
            tileset.setServer(server);

            return tileset;
        }
        return null;
    }
}
