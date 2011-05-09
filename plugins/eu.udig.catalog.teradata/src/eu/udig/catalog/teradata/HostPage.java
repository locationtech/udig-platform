package eu.udig.catalog.teradata;

import net.refractions.udig.catalog.service.database.UserHostPage;

public class HostPage extends UserHostPage {

	public HostPage() {
		super(new TeradataDialect());
	}

}
