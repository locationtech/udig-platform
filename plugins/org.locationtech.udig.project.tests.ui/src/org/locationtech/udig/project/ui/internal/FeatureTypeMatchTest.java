/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2016, Refractions Research Inc. and Others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.geotools.data.DataUtilities;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * @author Frank Gasdorf
 */
public class FeatureTypeMatchTest {

    IConfigurationElement matcherElement;

    @Test
    public void testNullNamespaceURI() throws Exception {
        final String typeName="myLineType";
        matcherElement = EasyMock.createNiceMock(IConfigurationElement.class);
        matcherElement.getChildren("typeName");
        EasyMock.expectLastCall()
                .andReturn(new IConfigurationElement[] { new IConfigurationElement() {

                    @Override
                    public boolean isValid() {
                        return true;
                    }

                    @Override
                    public String getValueAsIs() throws InvalidRegistryObjectException {
                        return null;
                    }

                    @Override
                    public String getValue(String locale) throws InvalidRegistryObjectException {
                        return null;
                    }

                    @Override
                    public String getValue() throws InvalidRegistryObjectException {
                        return null;
                    }

                    @Override
                    public Object getParent() throws InvalidRegistryObjectException {
                        return null;
                    }

                    @Override
                    public String getNamespaceIdentifier() throws InvalidRegistryObjectException {
                        return null;
                    }

                    @Override
                    public String getNamespace() throws InvalidRegistryObjectException {
                        return null;
                    }

                    @Override
                    public String getName() throws InvalidRegistryObjectException {
                        return null;
                    }

                    @Override
                    public IExtension getDeclaringExtension() throws InvalidRegistryObjectException {

                        return null;
                    }

                    @Override
                    public IContributor getContributor() throws InvalidRegistryObjectException {

                        return null;
                    }

                    @Override
                    public IConfigurationElement[] getChildren(String name)
                            throws InvalidRegistryObjectException {

                        return null;
                    }

                    @Override
                    public IConfigurationElement[] getChildren()
                            throws InvalidRegistryObjectException {
                        return null;
                    }

                    @Override
                    public String[] getAttributeNames() throws InvalidRegistryObjectException {
                        return null;
                    }

                    @Override
                    public String getAttributeAsIs(String name)
                            throws InvalidRegistryObjectException {
                        return null;
                    }

                    @Override
                    public String getAttribute(String attrName, String locale)
                            throws InvalidRegistryObjectException {
                        return null;
                    }

                    @Override
                    public String getAttribute(String name) throws InvalidRegistryObjectException {
                        if (name.equalsIgnoreCase("name")) {
                            return typeName;
                        }
                        if (name.equalsIgnoreCase("namespace")) {
                            return "*";
                        }
                        return null;
                    }

                    @Override
                    public Object createExecutableExtension(String propertyName)
                            throws CoreException {
                        return null;
                    }
                } }).anyTimes();

        SimpleFeatureType featureTypeWithNullNamspaceURI = DataUtilities.createType(
                typeName, "geom:LineString,name:String"); //$NON-NLS-1$ //$NON-NLS-2$

        EasyMock.replay(matcherElement);

        FeatureTypeMatch ftp = new FeatureTypeMatch(matcherElement);

        int matches = ftp.matches(featureTypeWithNullNamspaceURI);

        assertEquals(FeatureTypeMatch.PERFECT, matches);

        EasyMock.verify(matcherElement);
    }
}
