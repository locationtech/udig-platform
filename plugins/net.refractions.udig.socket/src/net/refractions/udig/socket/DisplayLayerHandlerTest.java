package net.refractions.udig.socket;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;

import net.refractions.udig.core.Pair;

import org.junit.Test;

public class DisplayLayerHandlerTest {

	@Test
	public void testCreateShpParams() throws Exception {
		DisplayLayerHandler handler = new DisplayLayerHandler();
		String resourceId = "file://home/user/data/myshp.shp#myshp";
		String shpId ="file://home/user/data/myshp.shp";
		StringBuilder builder = new StringBuilder("geoResourceId;"+resourceId );
		builder.append("\nurl;java.net.URL;"+shpId );
		String message = builder.toString();
		Pair<URL, Map<String, Serializable>> results = handler.createParams(toReader(message));
		assertEquals(resourceId, results.getLeft().toExternalForm());
		assertEquals(shpId, ((URL) results.getRight().get("url")).toExternalForm());
	}

	private BufferedReader toReader(String message) {
		return new BufferedReader(new StringReader(message));
	}

}
