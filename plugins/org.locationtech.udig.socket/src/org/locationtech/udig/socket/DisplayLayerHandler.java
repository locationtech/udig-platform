/**
 * 
 */
package org.locationtech.udig.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.core.Pair;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * If the header of the message is: <strong>DisplayLayer version:1.0</strong>
 * then the following data must be the connection parameters for a IService and
 * an identifier of the IGeoResource to add to the map.
 * <p>
 * The format of the message is:
 * 
 * <pre>
 * DisplayLayer version:1.0
 * geoResourceId;URI
 * paramName;paramType;paramValue
 * </pre>
 * 
 * <ul>
 * <li>';' must be escaped</li>
 * <li>paramType must be one of {@link Types}</li>
 * </ul>
 * 
 * For example:
 * 
 * <pre>
 * DisplayLayer version:1.0
 * geoResourceId;file://home/user/data/myshp.shp#myshp
 * url;java.net.URL;file://home/user/data/myshp.shp
 * </pre>
 * 
 * @author jesse
 */
public class DisplayLayerHandler implements MessageHandler {
	public static final String HEADER = "DisplayLayer version:1.0";
	public static final String RESOURCE_ID = "georesourceid;";

	public boolean canHandleMessge(String header) {
		return HEADER.equalsIgnoreCase(header);
	}

	public void execute(BufferedReader reader, Socket socket) throws Exception {
		
		Pair<URL, Map<String, Serializable>> params = createParams(reader);

		IMap activeMap = ApplicationGIS.getActiveMap();
		if (activeMap != ApplicationGIS.NO_MAP) {
			for (ILayer layer : activeMap.getMapLayers()) {
				if (URLUtils.urlEquals(layer.getID(), params.getLeft(), false)) {
					layer.refresh(null);
					return;
				}
			}
		} 
		
		IGeoResource resource = obtainResource(params);
		
		if( resource != null ){
			if( activeMap!=ApplicationGIS.NO_MAP ){
				ApplicationGIS.addLayersToMap(activeMap, Collections.singletonList(resource), -1);
			} else {
				ApplicationGIS.createAndOpenMap(Collections.singletonList(resource));
			}
		}
	}

	private IGeoResource obtainResource(
			Pair<URL, Map<String, Serializable>> params) throws IOException {
		ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
		List<IGeoResource> resource = localCatalog.find(IGeoResource.class, params.getLeft(), new NullProgressMonitor());
		
		if( resource.isEmpty() ){
			List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(params.getRight());
			if ( !services.isEmpty() ){
				for (IService service : services) {
					List<? extends IGeoResource> resources = service.resources(new NullProgressMonitor());
					for (IGeoResource geoResource : resources) {
						if( URLUtils.urlEquals(params.getLeft(), geoResource.getIdentifier(), false) ){
							return geoResource;
						}
					}
				}
			}
		}
		return resource.get(0);
	}

	Pair<URL, Map<String, Serializable>> createParams(BufferedReader reader)
			throws Exception {
		String line = reader.readLine();

		URL resourceId = null;
		Map<String, Serializable> params = new HashMap<String, Serializable>();

		while (line != null) {
			line = line.trim();
			if (line.length() > 0) {
				if (resourceId == null
						&& line.toLowerCase().startsWith(RESOURCE_ID)) {
					String spec = line.substring(RESOURCE_ID.length());
					spec = spec.replaceAll("\\\\;", ";");
					resourceId = new URL(spec);
				} else {
					addParam(params, line);
				}
			}
			line = reader.readLine();
		}
		return new Pair<URL, Map<String, Serializable>>(resourceId, params);
	}

	private void addParam(Map<String, Serializable> params, String line)
			throws Exception {

		String key = null;
		String value = null;
		String type = null;

		String[] parts = line.split(";");
		String concat = "";
		for (String string : parts) {
			if (concat.length() == 0) {
				concat = string;
			} else {
				concat = safeTake(concat, -1) + ";" + string;
			}

			if (string.endsWith("\\")) {
				continue;
			}

			if (key == null) {
				key = concat;
			} else if (type == null) {
				type = concat;
			} else {
				value = concat;
			}

			concat = "";
		}

		Serializable concreteValue = null;
		for (Types t : Types.values()) {
			concreteValue = t.create(type, value);
			if (concreteValue != null) {
				break;
			}
		}

		if (concreteValue == null) {
			Activator.log("the parameter: " + key + " of type " + type
					+ " was ignored because it is not a known type", null);
		}
		params.put(key, concreteValue);
	}

	public static String safeTake(String string, int toTake) {
		if (toTake < 0) {
			toTake = string.length() + toTake;
		}
		if (string.length() > toTake && toTake > -1) {
			return string.substring(0, toTake);
		} else {
			return "";
		}
	}

	private enum Types {
		STRING {
			@Override
			protected Serializable create(String type, String val) {
				if ("string".equalsIgnoreCase(type)
						|| "java.lang.String".equalsIgnoreCase(type))
					return val;

				return null;
			}
		},
		INT {
			@Override
			protected Serializable create(String type, String val) {
				if ("int".equalsIgnoreCase(type)
						|| "integer".equalsIgnoreCase(type)
						|| "java.lang.Integer".equalsIgnoreCase(type))
					return Integer.valueOf(val);
				return null;
			}
		},
		BYTE {
			@Override
			protected Serializable create(String type, String val) {
				if ("byte".equalsIgnoreCase(type)
						|| "java.lang.Byte".equalsIgnoreCase(type))
					return Byte.valueOf(val);
				return null;
			}
		},
		URL {
			@Override
			protected Serializable create(String type, String val)
					throws Exception {
				if ("URL".equalsIgnoreCase(type)
						|| "java.net.URL".equalsIgnoreCase(type))
					return new URL(val);
				return null;
			}
		},
		BOOLEAN {
			@Override
			protected Serializable create(String type, String val) {
				if ("Boolean".equalsIgnoreCase(type)
						|| "java.lang.Boolean".equalsIgnoreCase(type))
					return Boolean.valueOf(val);
				return null;
			}
		},
		FLOAT {
			@Override
			protected Serializable create(String type, String val) {
				if ("float".equalsIgnoreCase(type)
						|| "java.lang.Float".equalsIgnoreCase(type))
					return Float.valueOf(val);
				return null;
			}
		},
		DOUBLE {
			@Override
			protected Serializable create(String type, String val) {
				if ("double".equalsIgnoreCase(type)
						|| "java.lang.Double".equalsIgnoreCase(type))
					return Double.valueOf(val);
				return null;
			}
		},
		LONG {
			@Override
			protected Serializable create(String type, String val) {
				if ("long".equalsIgnoreCase(type)
						|| "java.lang.Long".equalsIgnoreCase(type))
					return Long.valueOf(val);
				return null;
			}
		},
		CHAR {
			@Override
			protected Serializable create(String type, String val) {
				if ("char".equalsIgnoreCase(type)
						|| "character".equalsIgnoreCase(type)
						|| "java.lang.Character".equalsIgnoreCase(type))
					return val.charAt(0);
				return null;
			}
		};

		protected abstract Serializable create(String type, String val)
				throws Exception;
	}
}
