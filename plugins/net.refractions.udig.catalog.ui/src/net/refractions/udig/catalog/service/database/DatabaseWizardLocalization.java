package net.refractions.udig.catalog.service.database;

/**
 * This class allows the wizards to be shared by different database plugins.  There are defaults given and
 * they can be changed on construction as required by the actual implementation if the defaults don't work  
 * 
 * @author jesse
 * @since 1.1.0
 */
public class DatabaseWizardLocalization {

    public String portError = Messages.DatabaseWizardLocalization_portError;
    public String host = Messages.DatabaseWizardLocalization_host;
    public String port = Messages.DatabaseWizardLocalization_port;
    public String username = Messages.DatabaseWizardLocalization_username;
    public String password = Messages.DatabaseWizardLocalization_password;
    public String database = Messages.DatabaseWizardLocalization_database;
    public String storePassword = Messages.DatabaseWizardLocalization_storePassword;
    public String removeConnection = Messages.DatabaseWizardLocalization_removeConnection;
    public String confirmRemoveConnection = Messages.DatabaseWizardLocalization_confirmRemoveConnection;
    public String previousConnections = Messages.DatabaseWizardLocalization_previousConnections;
    public String requiredField = Messages.DatabaseWizardLocalization_requiredField;
    public String changePasswordQuery = Messages.DatabaseWizardLocalization_changePasswordQuery;
    public String databaseConnectionInterrupted = Messages.DatabaseWizardLocalization_databaseConnectionInterrupted;
    public String unexpectedError = Messages.DatabaseWizardLocalization_unexpectedError;
    public String brokenElements = Messages.DatabaseWizardLocalization_brokenElements;
    public String table = Messages.DatabaseWizardLocalization_table;
    public String schema = Messages.DatabaseWizardLocalization_schema;
    public String geometryName = Messages.DatabaseWizardLocalization_geometryName;
    public String geometryType = Messages.DatabaseWizardLocalization_geometryType;
    public String publicSchema = Messages.DatabaseWizardLocalization_publicSchema;
    public String publicSchemaTooltip = Messages.DatabaseWizardLocalization_publicSchemaTooltip;
    public String filter = Messages.DatabaseWizardLocalization_filter;
    public String tableSelectionFilterTooltip = Messages.DatabaseWizardLocalization_tableSelectionFilterTooltip;
    public String incorrectConfiguration = Messages.DatabaseWizardLocalization_incorrectConfiguration;
    public String list = Messages.DatabaseWizardLocalization_list;
    public String databasePermissionProblemMessage = Messages.DatabaseWizardLocalization_databasePermissionProblemMessage;
	public String optionalParams=Messages.DatabaseWizardLocalization_optionalParams;

}
