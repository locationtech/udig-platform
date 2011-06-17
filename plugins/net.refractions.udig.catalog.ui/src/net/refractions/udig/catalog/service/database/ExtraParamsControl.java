package net.refractions.udig.catalog.service.database;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface ExtraParamsControl {
	public Control createControl(Composite parent);
	public Map<String,Serializable> getParams();
}
