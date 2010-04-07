package net.refractions.udig.catalog.internal.mysql.ui;

import net.refractions.udig.catalog.service.database.UserHostPage;

public class MySQLUserHostPage extends UserHostPage {

    public MySQLUserHostPage( ) {
        super(new MySqlDialect());
    }


}
