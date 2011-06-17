package eu.udig.catalog.teradata;

import static eu.udig.catalog.teradata.TeradataServiceExtension.DIALECT;
import static org.geotools.data.teradata.TeradataDataStoreFactory.DBTYPE;
import static org.geotools.data.teradata.TeradataDataStoreFactory.PORT;
import static org.geotools.jdbc.JDBCDataStoreFactory.DATABASE;
import static org.geotools.jdbc.JDBCDataStoreFactory.HOST;
import static org.geotools.jdbc.JDBCDataStoreFactory.PASSWD;
import static org.geotools.jdbc.JDBCDataStoreFactory.USER;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ui.UDIGConnectionFactory;

public class ConnectionFactory extends UDIGConnectionFactory {

	@Override
	public boolean canProcess(Object context) {
		return toCapabilitiesURL(context) != null;
	}

	@Override
	public Map<String, Serializable> createConnectionParameters(Object context) {
		if (context instanceof TeradataService) {
			TeradataService teradata = (TeradataService) context;
			return teradata.getConnectionParams();
		}
		URL url = toCapabilitiesURL(context);
		if (url == null) {
			// so we are not sure it is a teradata url
			// lets guess
			url = ID.cast(context).toURL();
		}
		if (url != null && TeradataServiceExtension.isTeradata(url)) {
			// well we have a url - lets try it!
			List<IResolve> list = CatalogPlugin.getDefault().getLocalCatalog()
					.find(url, null);
			for (IResolve resolve : list) {
				if (resolve instanceof TeradataService) {
					// got a hit!
					TeradataService teradataService = (TeradataService) context;
					return teradataService.getConnectionParams();
				} else if (resolve instanceof TeradataGeoResource) {
					TeradataGeoResource layer = (TeradataGeoResource) resolve;
					TeradataService teradata;
					try {
						teradata = (TeradataService) layer.parent(null);
						return teradata.getConnectionParams();
					} catch (IOException e) {
						toCapabilitiesURL(layer.getIdentifier());
					}
				}
			}
			return createParams(url);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public URL createConnectionURL(Object context) {
		if (context instanceof URL) {
			return (URL) context;
		}
		if (context instanceof Map) {
			@SuppressWarnings("rawtypes")
			Map params = (Map) context;

			try {
				return DIALECT.toURL(params);
			} catch (MalformedURLException e) {
				return null;
			}

		}
		if (context instanceof String) {
			return toCapabilitiesURL((String) context);
		}
		return null;
	}

	/**
	 * Convert "data" to a Teradata url
	 * <p>
	 * Candidates for conversion are:
	 * <ul>
	 * <li>URL - from browser DnD
	 * <li>TeradataService - from catalog DnD
	 * <li>IService - from search DnD
	 * </ul>
	 * </p>
	 * <p>
	 * No external processing should be required here, it is enough to guess and
	 * let the ServiceFactory try a real connect.
	 * </p>
	 * 
	 * @param data
	 *            IService, URL, or something else
	 * @return URL considered a possibility for a Teradata connection, or null
	 */
	protected URL toCapabilitiesURL(Object data) {
		if (data instanceof IResolve) {
			return toCapabilitiesURL((IResolve) data);
		} else if (data instanceof URL) {
			return toCapabilitiesURL((URL) data);
		} else if (data instanceof String) {
			return toCapabilitiesURL((String) data);
		} else if (ID.cast(data) != null && ID.cast(data).toURL() != null) {
			return toCapabilitiesURL(ID.cast(data).toURL());
		} else {
			Activator.log("Unable to convert object: "+data+" to a capabilities url for teradata", null);
			return null; // no idea what this should be
		}
	}

	protected URL toCapabilitiesURL(IResolve resolve) {
		if (resolve instanceof IService) {
			return toCapabilitiesURL((IService) resolve);
		}
		return toCapabilitiesURL(resolve.getIdentifier());
	}

	protected URL toCapabilitiesURL(IService resolve) {
		if (resolve instanceof TeradataService) {
			return toCapabilitiesURL((TeradataService) resolve);
		}
		return toCapabilitiesURL(resolve.getIdentifier());
	}

	/** No further QA checks needed - we know this one works */
	protected URL toCapabilitiesURL(TeradataService teradata) {
		return teradata.getIdentifier();
	}

	/** Quick sanity check to see if url is a Teradata url */
	protected URL toCapabilitiesURL(URL url) {
		if (url == null)
			return null;

		String protocol = url.getProtocol() != null ? url.getProtocol()
				.toLowerCase() : null;

		if (!"http".equals(protocol) //$NON-NLS-1$
				&& !"https".equals(protocol) && !DIALECT.urlPrefix.equals(protocol)) { //$NON-NLS-1$ 
			return null;
		}
		if (url.toExternalForm().indexOf(DIALECT.urlPrefix) != -1) {
			return url;
		}
		return null;
	}

	/** Quick sanity check to see if url is a Teradata url String */
	protected URL toCapabilitiesURL(String string) {
		if (string == null)
			return null;

		if (!string.contains(DIALECT.urlPrefix)
				&& !string.contains("jdbc.teradata") && !string.contains("teradata")) { //$NON-NLS-1$ //$NON-NLS-2$ 
			return null;
		}
		// jdbc.postgresql://username:password@host:port/database
		int startindex = string.indexOf("//") + 2; //$NON-NLS-1$
		int usernameEnd = string.indexOf(":", startindex); //$NON-NLS-1$
		int passwordEnd = string.indexOf("@", usernameEnd); //$NON-NLS-1$
		int hostEnd = string.indexOf(":", passwordEnd); //$NON-NLS-1$
		int portEnd = string.indexOf("/", hostEnd); //$NON-NLS-1$
		int databaseEnd = string.indexOf("/", portEnd + 1); //$NON-NLS-1$

		// int databaseEnd = string.indexOf(" ", databaseStart);
		String the_host = string.substring(passwordEnd + 1, hostEnd);
		String the_username = string.substring(startindex, usernameEnd);
		String the_password = string.substring(usernameEnd + 1, passwordEnd);
		String the_port;
		String the_database;

		if (portEnd < 1) {
			the_port = string.substring(hostEnd + 1);
			the_database = ""; //$NON-NLS-1$
		} else {
			the_port = string.substring(hostEnd + 1, portEnd);
			the_database = string.substring(portEnd + 1, databaseEnd);
		}
		Integer intPort;
		if (!the_port.equalsIgnoreCase("")) { //$NON-NLS-1$
			intPort = new Integer(the_port);
		} else {
			intPort = new Integer(5432);
		}

		// URL(String protocol, String host, int port, String file)
		URL url = null;
		try {
			url = DIALECT.toURL(the_username, the_host, intPort,
					the_database);

		} catch (MalformedURLException e) {
			// TODO Catch e
			Activator.log("bad url", e); //$NON-NLS-1$
		}
		return url;
	}

	/** 'Create' params given the provided url, no magic occurs */
	@SuppressWarnings("unchecked")
	protected Map<String, Serializable> createParams(URL url) {
		TeradataServiceExtension serviceFactory = new TeradataServiceExtension();
		Map params = serviceFactory.createParams(url);
		if (params != null)
			return params;

		Map<String, Serializable> params2 = new HashMap<String, Serializable>();

		params2.put(DBTYPE.key, (Serializable) DBTYPE.sample); //$NON-NLS-1$
		params2.put(HOST.key, url.getHost());
		String dbport = ((Integer) url.getPort()).toString();
		try {
			params2.put(PORT.key, new Integer(dbport));
		} catch (NumberFormatException e) {
			params2.put(PORT.key, new Integer(5432));
		}

		String the_database = url.getPath() == null ? "" : url.getPath(); //$NON-NLS-1$
		params2.put(DATABASE.key, the_database); // database
		String userInfo = url.getUserInfo() == null ? "" : url.getUserInfo(); //$NON-NLS-1$
		params2.put(USER.key, userInfo); // user
		params2.put(PASSWD.key, ""); // pass //$NON-NLS-1$

		return params2;
	}

}
