/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.ui.wizard;

/**
 * Holds the six database parameters: host, port, user, pass, db, schema as
 * Strings and a timestamp.
 * 
 * Orginaly this class created new Strings to prevent side effects however 
 * this is unnessacary as all Strings are constants and unmodifiable.
 * The methods defensively create new Strings so we are sure this is an
 * isolated clone which will have no side effects. Similarly we should copy
 * not DBCI's into the lists.
 * 
 */
public class DataBaseConnInfo {

	private String host;
	private String port;
	private String user;
	private String pass;
	private String db;
	private String schema;
	private boolean treatEmptyStringAsNull;

	public DataBaseConnInfo(String hst, String prt, String usr, String pas,
			String dbs, String sch) {
		// Cloneing the Strings is unessacary see above 
		setHost(hst);
		setPort(prt);
		setUser(usr);
		setPass(pas);
		setDb(dbs);
		setSchema(sch);
	}

	public DataBaseConnInfo(String dbEntry) {
		String[] temp = dbEntry.split("\t"); //$NON-NLS-1$

		if (temp.length > 0)
			this.setHost(temp[0]);
		if (temp.length > 1)
			this.setPort(temp[1]);
		if (temp.length > 2)
			this.setUser(temp[2]);
		if (temp.length > 3)
			this.setPass(temp[3]);
		if (temp.length > 4)
			this.setDb(temp[4]);
		if (temp.length > 5)
			this.setSchema(temp[5]);
	}

	public String getHostString() {
		String string = host;
		return processStringForGetter(string); //$NON-NLS-1$
	}

	public String getPortString() {
		String string = port;
		return processStringForGetter(string); //$NON-NLS-1$
	}

	public String getUserString() {
		String string = user;
		return processStringForGetter(string); //$NON-NLS-1$
	}

	public String getPassString() {
		String string = pass;
		return processStringForGetter(string); //$NON-NLS-1$
	}

	public String getDbString() {
		String string = db;
		return processStringForGetter(string); //$NON-NLS-1$
	}

	public String getSchemaString() {
		String string = schema;
		return processStringForGetter(string); //$NON-NLS-1$
	}

	/**
	 * Processes a string so that it is formatted well for a getter
	 */
	private String processStringForGetter(String string) {
		String toReturn = string == null ? "" : string;
		toReturn = toReturn.trim();
		if (treatEmptyStringAsNull && toReturn.length() == 0) {
			return null;
		}
		return toReturn;
	}

	public void setParameters(DataBaseConnInfo dbci) {

		if (null == dbci)
		{
			host = port = user = pass = db = schema = null;
			return;
		}

		setHost(dbci.getHostString());
		setPort(dbci.getPortString());
		setUser(dbci.getUserString());
		setPass(dbci.getPassString());
		setDb(dbci.getDbString());
		setSchema(dbci.getSchemaString());
	}

	public void setHost(String h) {
		host = h;
	}

	public void setPort(String p) {
		port = p;
	}

	public void setUser(String u) {
		user = u;
	}

	public void setPass(String p) {
		pass = p;
	}

	public void setDb(String d) {
		db = d;
	}

	public void setSchema(String s) {
		schema = s;
	}

	public String toDisplayString() {
		return getHostString()
				+ " : " + getPortString() + " : " + getUserString() + " : " + getPassString() + " : " + getDbString() + " : " + getSchemaString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}

	@Override
	public String toString() {
		return getHostString()
				+ "\t" + getPortString() + "\t" + getUserString() + "\t" + getPassString() + "\t" + getDbString() + "\t" + getSchemaString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}

	

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((db == null) ? 0 : db.hashCode());
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((pass == null) ? 0 : pass.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        result = prime * result + ((schema == null) ? 0 : schema.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DataBaseConnInfo other = (DataBaseConnInfo) obj;
        if (db == null) {
            if (other.db != null)
                return false;
        } else if (!db.equals(other.db))
            return false;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (pass == null) {
            if (other.pass != null)
                return false;
        } else if (!pass.equals(other.pass))
            return false;
        if (port == null) {
            if (other.port != null)
                return false;
        } else if (!port.equals(other.port))
            return false;
        if (schema == null) {
            if (other.schema != null)
                return false;
        } else if (!schema.equals(other.schema))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }

    /**
	 * If set to true then instead of returning an empty string null will be
	 * returned.
	 * <p>
	 * This is useful for some datastores like postgis because it looks for
	 * the existence of a parameter but doesn't verify that is is non-empty.
	 * </p>
	 * 
	 * @param treatAsNull
	 *            if true then instead of returning an empty string null
	 *            will be returned.
	 */
	public void treatEmptyStringAsNull(boolean treatAsNull) {
		treatEmptyStringAsNull = treatAsNull;
	}

}