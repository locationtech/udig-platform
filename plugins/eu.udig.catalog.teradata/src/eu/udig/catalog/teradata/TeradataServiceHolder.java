package eu.udig.catalog.teradata;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;

public class TeradataServiceHolder extends IService {

	private URL id;
	private Map<String, Serializable> params;

	public TeradataServiceHolder(URL finalID, Map<String, Serializable> params2) {
		this.id = finalID;
		this.params = params2;
	}

	@Override
	public Status getStatus() {
		return Status.NOTCONNECTED;
	}

	@Override
	public Throwable getMessage() {
		return null;
	}

	@Override
	public URL getIdentifier() {
		return id;
	}

	@Override
	public List<? extends IGeoResource> resources(IProgressMonitor monitor)
			throws IOException {
		return Collections.emptyList();
	}

	@Override
	protected IServiceInfo createInfo(IProgressMonitor monitor)
			throws IOException {
		return new IServiceInfo("Driverless Teradata connection", "Teradata plugin needs jdbc driver", "", null, null, null, null,null);
	}

	@Override
	public Map<String, Serializable> getConnectionParams() {
		return params;
	}

}
