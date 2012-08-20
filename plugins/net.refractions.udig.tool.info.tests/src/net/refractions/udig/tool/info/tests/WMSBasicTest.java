package net.refractions.udig.tool.info.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.catalog.wmsc.server.MockHttpResponse;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tool.info.InfoTool;
import net.refractions.udig.tool.info.LayerPointInfo;
import net.refractions.udig.tool.info.internal.WMSDescribeLayer;

import org.geotools.data.ows.HTTPResponse;
import org.geotools.data.ows.Layer;
import org.geotools.data.wms.WebMapServer;
import org.geotools.data.wms.request.GetFeatureInfoRequest;
import org.geotools.data.wms.response.GetFeatureInfoResponse;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.ows.ServiceException;
import org.geotools.referencing.CRS;
import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

public class WMSBasicTest {

	public URL stableWMS;

	IMap map;

	ReferencedEnvelope bbox;
	CoordinateReferenceSystem crs;
	ViewportPane viewportPane;
	ViewportModel viewportModel;
	List<ILayer> layers;
	Dimension displaySize;
	Layer wmslayer;

	public WebMapServer wms;

	TestRenderManager renderManager;

	ReferencedEnvelope bufferedClickBbox;
	
	@Before
	public void setUp() throws Exception {
		crs = CRS.decode("EPSG:4326");
		
		Envelope env = new Envelope(-10.0, 10.0, -10.0, 10.0);
		bbox = new ReferencedEnvelope(env, crs);
		bufferedClickBbox = new ReferencedEnvelope(new Envelope(-1.0, 1.0, -1.0, 1.0), crs);
		
		displaySize = new Dimension(400, 400);
		viewportPane = new TestViewportPane(displaySize);
		viewportModel = new TestViewportModel(displaySize, bbox, crs);
		renderManager = new TestRenderManager(viewportPane);

		wmslayer = new Layer("test");
		wmslayer.setName("test");
		wmslayer.setQueryable(true);
		wmslayer.setSrs(Collections.singleton("EPSG:4326"));
		
		stableWMS = new URL("http://vmap0.tiles.osgeo.org/wms/vmap0?SERVICE=wms&VERSION=1.1.0&REQUEST=GetCapabilities");
		wms = new FudgeServer();
		
		layers = new ArrayList<ILayer>();
		
		map = new TestMap(renderManager, viewportModel, layers);
		
		layers.add(new WMSLayer(this.map, this.wmslayer, this.wms));
	}

	@Test
	public void testWMSBasic() throws Exception {
		InfoTool infoTool = new InfoTool();
		
		IToolContext context = new TestToolContext(this.bufferedClickBbox, 
				this.crs, this.viewportPane, this.layers, this.viewportModel
				);
		
		infoTool.setContext(context);
		
		MapMouseEvent e = new MapMouseEvent(null, 5, 5, 
				MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.NONE);
		
		/*
		 * Taken straight from InfoTool.mouseReleased()
		 */
        Envelope box = context.getBoundingBox( e.getPoint(), 5 );
        ReferencedEnvelope bbox;
        if( box instanceof ReferencedEnvelope) {
            bbox = (ReferencedEnvelope) box;
        }
        else {
            CoordinateReferenceSystem crs = context.getViewportModel().getCRS();
            bbox = new ReferencedEnvelope(box, crs);
            
        } 
        /*
         * End InfoTool.mouseReleased() code
         */
		
		LayerPointInfo hit = WMSDescribeLayer.info2(this.layers.get(0), bbox);
		
		assertNotNull(hit);
		
		Object value = hit.acquireValue();
		
		assertNotNull(value);
		
		assertTrue(value.equals("SUCCESS"));
		
	}
	
	public class FudgeServer extends WebMapServer {

		
		
		public FudgeServer() throws IOException, ServiceException {
			super(stableWMS);
		}

		@Override
		public GetFeatureInfoResponse issueRequest(GetFeatureInfoRequest request) throws IOException, ServiceException {
						
			if ((request.getProperties().get(GetFeatureInfoRequest.QUERY_X).equals("200"))
				&& (request.getProperties().get(GetFeatureInfoRequest.QUERY_Y).equals("200"))) {
				String success = "SUCCESS";
				
				ByteArrayInputStream input = new ByteArrayInputStream(success.getBytes());
				
				HTTPResponse response = new MockHttpResponse( input, "text/html");
				return new GetFeatureInfoResponse( response );
			}
			
			String failure = "FAILURE";
			
			ByteArrayInputStream input = new ByteArrayInputStream(failure.getBytes());
			HTTPResponse response = new MockHttpResponse( input, "text/html");
			
			return new GetFeatureInfoResponse( response );
		}
		
		
		
	}
}
