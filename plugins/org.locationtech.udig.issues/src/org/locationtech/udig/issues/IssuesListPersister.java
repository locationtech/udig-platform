/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.locationtech.udig.core.enums.Priority;
import org.locationtech.udig.core.enums.Resolution;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.issues.internal.IssuesActivator;
import org.locationtech.udig.issues.internal.Messages;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Persists an issues list to a file
 *
 * @author Jesse
 */
public class IssuesListPersister {

    private static final String MEMENTO_CHILD_TYPE = "issue"; //$NON-NLS-1$

    private static final String MEMENTO_ISSUE_DATA = "MEMENTO_ISSUE_DATA"; //$NON-NLS-1$

    private static final String EXTENSION_ID = "EXTENSION_ID"; //$NON-NLS-1$

    private static final String MEMENTO_VIEW_DATA = "MEMENTO_VIEW_DATA"; //$NON-NLS-1$

    private static final String GROUP_ID = "GROUP_ID"; //$NON-NLS-1$

    private static final String DESCRIPTION = "DESCRIPTION"; //$NON-NLS-1$

    private static final String MIN_X = "MIN_X"; //$NON-NLS-1$

    private static final String MAX_X = "MAX_X"; //$NON-NLS-1$

    private static final String MIN_Y = "MIN_Y"; //$NON-NLS-1$

    private static final String MAX_Y = "MAX_Y"; //$NON-NLS-1$

    private static final String CRS = "CRS"; //$NON-NLS-1$

    private static final String PRIORITY = "PRIORITY"; //$NON-NLS-1$

    private static final String RESOLUTION = "RESOLUTION"; //$NON-NLS-1$

    private IIssuesList list;

    public IssuesListPersister(IIssuesList list, String fileName) {
        this.list = list;
    }

    /**
     * Saves the issues list to the workspace
     *
     * @throws IOException Thrown if there is a failure writing the output file.
     */
    public void save() throws IOException {
        XMLMemento memento = XMLMemento
                .createWriteRoot(Messages.IssuesListPersister_xmlRootElement);
        for (IIssue issue : this.list) {
            try {
                IMemento child = memento.createChild(MEMENTO_CHILD_TYPE, issue.getId());

                child.putString(GROUP_ID, issue.getGroupId());
                child.putString(EXTENSION_ID, issue.getExtensionID());
                child.putString(DESCRIPTION, issue.getDescription());
                child.putString(PRIORITY, issue.getPriority().name());
                child.putString(RESOLUTION, issue.getResolution().name());

                // persist bounds
                ReferencedEnvelope bounds = issue.getBounds();
                child.putString(MIN_X, Double.toString(bounds.getMinX()));
                child.putString(MAX_X, Double.toString(bounds.getMaxX()));
                child.putString(MIN_Y, Double.toString(bounds.getMinY()));
                child.putString(MAX_Y, Double.toString(bounds.getMaxY()));
                child.putString(CRS, bounds.getCoordinateReferenceSystem().toWKT());

                issue.getViewMemento(child.createChild(MEMENTO_VIEW_DATA));
                issue.save(child.createChild(MEMENTO_ISSUE_DATA));
            } catch (Throwable e) {
                LoggingSupport.log(IssuesActivator.getDefault(), "error when daving issue", e); //$NON-NLS-1$
            }
        }
        FileWriter fileWriter = new FileWriter(getLocalIssuesFile());
        try {
            memento.save(fileWriter);
        } finally {
            fileWriter.close();
        }
    }

    /**
     * Reads the local issues from disk
     *
     * @throws IOException thrown if there was a problem reading the issues file
     * @throws WorkbenchException thrown if the xml in the file is bad or doesn't conform to what
     *         the XMLMemento expects
     */
    public void load() throws IOException, WorkbenchException {
        if (getLocalIssuesFile().exists()) {
            FileReader reader = new FileReader(getLocalIssuesFile());
            XMLMemento memento = XMLMemento.createReadRoot(reader);
            IMemento[] children = memento.getChildren(MEMENTO_CHILD_TYPE);
            for (IMemento issueMemento : children) {
                try {
                    IIssue issue = IssuesListUtil.createIssue(issueMemento.getString(EXTENSION_ID));
                    if (issue == null) {
                        continue;
                    }
                    IMemento dataMemento = issueMemento.getChild(MEMENTO_ISSUE_DATA);
                    IMemento viewMemento = issueMemento.getChild(MEMENTO_VIEW_DATA);
                    String issueId = issueMemento.getID();
                    String groupId = issueMemento.getString(GROUP_ID);

                    double minX = Double.parseDouble(issueMemento.getString(MIN_X));
                    double maxX = Double.parseDouble(issueMemento.getString(MAX_X));
                    double minY = Double.parseDouble(issueMemento.getString(MIN_Y));
                    double maxY = Double.parseDouble(issueMemento.getString(MAX_Y));
                    CoordinateReferenceSystem crs;
                    try {
                        crs = org.geotools.referencing.CRS.parseWKT(issueMemento.getString(CRS));
                    } catch (FactoryException e) {
                        crs = null;
                    }
                    ReferencedEnvelope bounds = new ReferencedEnvelope(minX, maxX, minY, maxY, crs);
                    issue.init(dataMemento, viewMemento, issueId, groupId, bounds);
                    issue.setDescription(issueMemento.getString(DESCRIPTION));
                    issue.setPriority(Priority.valueOf(issueMemento.getString(PRIORITY)));
                    issue.setResolution(Resolution.valueOf(issueMemento.getString(RESOLUTION)));

                    list.add(issue);
                } catch (Throwable e) {
                    LoggingSupport.log(IssuesActivator.getDefault(), "error when loading issue", e); //$NON-NLS-1$
                }

            }
        }
    }

    private File getLocalIssuesFile() throws IOException {
        File userLocation = new File(
                FileLocator.toFileURL(Platform.getInstanceLocation().getURL()).getFile());
        if (!userLocation.exists())
            userLocation.mkdirs();
        File catalogLocation = new File(userLocation, ".issues.xml"); //$NON-NLS-1$
        return catalogLocation;
    }

}
