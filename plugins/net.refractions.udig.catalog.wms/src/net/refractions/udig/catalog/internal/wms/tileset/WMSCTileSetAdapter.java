package net.refractions.udig.catalog.internal.wms.tileset;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.catalog.internal.wms.WMSGeoResourceImpl;
import net.refractions.udig.catalog.internal.wmsc.WMSCServiceImpl;
import net.refractions.udig.catalog.wmsc.server.TileSet;
import net.refractions.udig.catalog.wmsc.server.TiledWebMapServer;
import net.refractions.udig.catalog.wmsc.server.WMSTileSet;
import net.refractions.udig.render.wms.basic.WMSPlugin;
import net.refractions.udig.render.wms.basic.preferences.PreferenceConstants;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.ows.AbstractOpenWebService;
import org.geotools.data.ows.CRSEnvelope;
import org.geotools.data.wms.WebMapServer;
import org.geotools.referencing.CRS;

public class WMSCTileSetAdapter implements IResolveAdapterFactory {

    @Override
    public boolean canAdapt( IResolve resolve, Class< ? extends Object> adapter ) {
        if (adapter.isAssignableFrom(TileSet.class)) {
            return true;
        }

        return false;
    }

    @Override
    public Object adapt( IResolve resolve, Class< ? extends Object> adapter,
            IProgressMonitor monitor ) throws IOException {

        System.out.println("++++++++++++++++");
        
        AbstractOpenWebService server = null;

        if (resolve.canResolve(WebMapServer.class)) {
            server = resolve.resolve(WebMapServer.class, monitor);
        }
        if (resolve.canResolve(TiledWebMapServer.class)) {
            server = resolve.resolve(TiledWebMapServer.class, monitor);
        }
        
        System.out.println("++++++++++++++++"); //$NON-NLS-1$

        if (adapter.isAssignableFrom(TileSet.class)) {
            
            WMSGeoResourceImpl fs = (WMSGeoResourceImpl) resolve;
            IGeoResourceInfo info = fs.getInfo(monitor);

            URL caps = new URL(server.getInfo().getSource()+"version="+server.getCapabilities().getVersion()+"&request=GetCapabilities"); //$NON-NLS-1$
            String srs = CRS.toSRS(info.getCRS());
            
            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put(WMSCServiceImpl.WMSC_URL_KEY, caps);

            TileSet tileset = new WMSTileSet();

            double minX = info.getBounds().getMinimum(0);
            double maxX = info.getBounds().getMaximum(0);
            double minY = info.getBounds().getMinimum(1);
            double maxY = info.getBounds().getMaximum(1);

            CRSEnvelope bbox = new CRSEnvelope(srs, minX, minY, maxX, maxY);
            tileset.setBoundingBox(bbox);
            tileset.setCoorindateReferenceSystem(srs);
            
            tileset.setWidth(128);
            tileset.setHeight(128);

            String img = getImageFormat();
            
            tileset.setFormat("image/png");
            tileset.setLayers("tasmania");
            tileset.setResolutions("156543.03390625 78271.516953125 39135.7584765625 19567.87923828125 9783.939619140625 4891.9698095703125 2445.9849047851562 1222.9924523925781 611.4962261962891 305.74811309814453 152.87405654907226 76.43702827453613 38.218514137268066 19.109257068634033 9.554628534317017 4.777314267158508 2.388657133579254 1.194328566789627 0.5971642833948135 0.29858214169740677 0.14929107084870338 0.07464553542435169 0.037322767712175846 0.018661383856087923 0.009330691928043961 0.004665345964021981");
            tileset.setStyles("");
            tileset.setServer(server);

            return tileset;
        }
        return null;
    }
    
    private String getImageFormat( ) {
        String str = "";
        if (WMSPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_USE_DEFAULT_ORDER)) {
            str = WMSPlugin.getDefault().getPreferenceStore().getDefaultString(PreferenceConstants.P_IMAGE_TYPE_ORDER);
        } else {
            str = WMSPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_IMAGE_TYPE_ORDER);
        }
        return str;
    }
}
