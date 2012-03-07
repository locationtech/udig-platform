package net.refractions.udig.catalog.internal.postgis.ui;
import static org.geotools.data.postgis.PostgisNGDataStoreFactory.*;

import java.util.Map;

import net.refractions.udig.catalog.PostgisServiceExtension2;
import net.refractions.udig.catalog.internal.postgis.PostgisPlugin;
import net.refractions.udig.catalog.service.database.DataConnectionPage;
import net.refractions.udig.catalog.service.database.DatabaseConnectionRunnable;
import net.refractions.udig.catalog.service.database.DatabaseServiceDialect;
import net.refractions.udig.catalog.service.database.DatabaseWizardLocalization;
import net.refractions.udig.catalog.service.database.LookUpSchemaRunnable;
import net.refractions.udig.catalog.service.database.Tab;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * Describes the postgis parameters for creating the Postgis DataStore and ServiceExtension
 * 
 * @author jeichar
 */
public class PostgisServiceDialect extends DatabaseServiceDialect {

    public PostgisServiceDialect() {
        super(SCHEMA, DATABASE, HOST, PORT, USER, PASSWD, PostgisServiceExtension2
                .getPram(DBTYPE.key), "postgis", "jdbc.postgis", new DatabaseWizardLocalization()); //$NON-NLS-1$

    }
    @Override
    public IDialogSettings getDialogSetting() {
        return PostgisPlugin.getDefault().getDialogSettings();
    }

    @Override
    public void log( String message, Throwable e ) {
        PostgisPlugin.log(message, e);
    }

    @Override
    public DatabaseConnectionRunnable createDatabaseConnectionRunnable( String host, int port,
            String username, String password ) {
        return new PostgisDatabaseConnectionRunnable(host, port, username, password);
    }

    @Override
    public Map<Control, Tab> createOptionConnectionPageTabs( TabFolder tabFolder,
            DataConnectionPage containingPage ) {
        Map<Control, Tab> tabs = super.createOptionConnectionPageTabs(tabFolder, containingPage);
        //addSQLTab(tabFolder, containingPage, tabs);
        return tabs;
    }

    private void addSQLTab( TabFolder tabFolder, DataConnectionPage containingPage,
            Map<Control, Tab> tabs ) {
        SQLTab sqlComposite = new SQLTab(getDialogSetting());

        sqlComposite.setWizard(containingPage.getWizard());
        TabItem item = new TabItem(tabFolder, SWT.NONE);
        item.setText("SQL"); //$NON-NLS-1$
        item.setControl(sqlComposite.createControl(tabFolder, SWT.NONE));
        tabs.put(item.getControl(), sqlComposite);
    }

    @Override
    public LookUpSchemaRunnable createLookupSchemaRunnable( String host, int port, String username,
            String password, String database ) {
        return new PostgisLookUpSchemaRunnable(host, port, username, password, database);
    }

}
