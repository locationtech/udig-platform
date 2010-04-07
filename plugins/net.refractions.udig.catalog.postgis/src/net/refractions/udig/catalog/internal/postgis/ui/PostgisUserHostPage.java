/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.internal.postgis.ui;

import net.refractions.udig.catalog.service.database.UserHostPage;

/**
 * The first of a two page wizard for connecting to a postgis. This page requires the user enter
 * host, port, username and password.
 * 
 * @author jesse
 * @since 1.1.0
 */
public class PostgisUserHostPage extends UserHostPage {

    public PostgisUserHostPage( ) {
        super(new PostgisServiceDialect());
    }
}
