/**
 * uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2022, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.catalog.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.util.AST;
import org.locationtech.udig.catalog.util.ASTFactory;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author FGasdorf
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CatalogImplTest {

    private static final String NOT_MATCHING_TEXT = "whatever";

    private static final Envelope ENVELOPE = new Envelope(15, 17, -5, 5);

    private static final String MATCHING_TEXT = "test";

    private final AST testAST = ASTFactory.parse(MATCHING_TEXT);

    @Mock
    IService serviceMock;

    @Mock
    IGeoResource geoResource;

    @Mock
    IGeoResourceInfo geoResourceInfo;

    @Mock
    IServiceInfo serviceInfo;

    @Test
    public void checkIsTrueWithGeoResourceAndNullPattern() {
        assertTrue(CatalogImpl.check(geoResource, null));
    }

    @Test
    public void checkIsFalseWithGeoResource() {
        IGeoResource nullGeoResource = null;
        assertFalse(CatalogImpl.check(nullGeoResource, testAST));
    }

    @Test
    public void checkIsTrueIfResourceInfoTitleMatch() throws Exception {
        when(geoResourceInfo.getTitle()).thenReturn(MATCHING_TEXT);
        when(geoResource.getInfo(any())).thenReturn(geoResourceInfo);

        assertTrue(CatalogImpl.check(geoResource, testAST));
    }

    @Test
    public void checkWithEnvelopeIsTrueIfResourceInfoNameMatch() throws Exception {
        when(geoResourceInfo.getTitle()).thenReturn(NOT_MATCHING_TEXT);
        when(geoResourceInfo.getName()).thenReturn(MATCHING_TEXT);
        when(geoResource.getInfo(any())).thenReturn(geoResourceInfo);

        assertTrue(CatalogImpl.check(geoResource, testAST));
    }

    @Test
    public void checkWithEnvelopeIsTrueIfResourceInfoNameMatchAndEnvelopeIsNull() throws Exception {
        when(geoResourceInfo.getTitle()).thenReturn(NOT_MATCHING_TEXT);
        when(geoResourceInfo.getName()).thenReturn(MATCHING_TEXT);
        when(geoResource.getInfo(any())).thenReturn(geoResourceInfo);

        assertTrue(CatalogImpl.check(geoResource, testAST, null));
    }

    @Test
    public void checkWithEnvelopeIsTrueIfResourceInfoNameMatchAndInfoBoundsisNull()
            throws Exception {
        when(geoResourceInfo.getTitle()).thenReturn(MATCHING_TEXT);
        when(geoResourceInfo.getBounds()).thenReturn(null);
        when(geoResource.getInfo(any())).thenReturn(geoResourceInfo);

        assertTrue(CatalogImpl.check(geoResource, testAST, ENVELOPE));
    }

    @Test
    public void checkWithEnvelopeIsTrueIfResourceInfoNameMatchAndInfoWithBounds() throws Exception {
        when(geoResourceInfo.getTitle()).thenReturn(MATCHING_TEXT);
        when(geoResourceInfo.getBounds())
                .thenReturn(new ReferencedEnvelope(10, 20, -10, 10, DefaultGeographicCRS.WGS84));
        when(geoResource.getInfo(any())).thenReturn(geoResourceInfo);

        assertTrue(CatalogImpl.check(geoResource, testAST, ENVELOPE));
    }

    @Test
    public void checkWithEnvelopeIsFalseWithGeoResource() {
        IGeoResource nullGeoResource = null;
        assertFalse(CatalogImpl.check(nullGeoResource, testAST, null));
    }

    @Test
    public void checkIsTrueIfResourceInfoNameMatchAndEnvelopeIsNullCheck() throws Exception {
        Envelope envelope = new Envelope();
        when(geoResourceInfo.getTitle()).thenReturn(NOT_MATCHING_TEXT);
        when(geoResourceInfo.getName()).thenReturn(MATCHING_TEXT);
        when(geoResource.getInfo(any())).thenReturn(geoResourceInfo);

        assertTrue(CatalogImpl.check(geoResource, testAST, envelope));
    }

    @Test
    public void checkIsTrueIfResourceInfoAtLeastOneKeywordMatch() throws Exception {
        when(geoResourceInfo.getTitle()).thenReturn(NOT_MATCHING_TEXT);
        when(geoResourceInfo.getName()).thenReturn(NOT_MATCHING_TEXT);
        when(geoResourceInfo.getKeywords())
                .thenReturn(new HashSet<>(Arrays.asList(NOT_MATCHING_TEXT, MATCHING_TEXT)));
        when(geoResource.getInfo(any())).thenReturn(geoResourceInfo);

        assertTrue(CatalogImpl.check(geoResource, testAST));
    }

    @Test
    public void checkIsTrueIfSchemaUriMatch() throws Exception {
        when(geoResourceInfo.getTitle()).thenReturn(NOT_MATCHING_TEXT);
        when(geoResourceInfo.getName()).thenReturn(NOT_MATCHING_TEXT);
        when(geoResourceInfo.getKeywords())
                .thenReturn(new HashSet<>(Arrays.asList("one", "two", "tree")));
        when(geoResourceInfo.getSchema()).thenReturn(URI.create("whatever/" + MATCHING_TEXT));

        when(geoResource.getInfo(any())).thenReturn(geoResourceInfo);

        assertTrue(CatalogImpl.check(geoResource, testAST));
    }

    @Test
    public void checkIsTrueIfResourceInfoDescriptionMatch() throws Exception {
        when(geoResourceInfo.getTitle()).thenReturn(NOT_MATCHING_TEXT);
        when(geoResourceInfo.getName()).thenReturn(NOT_MATCHING_TEXT);
        when(geoResourceInfo.getKeywords()).thenReturn(null);
        when(geoResourceInfo.getSchema()).thenReturn(null);
        when(geoResourceInfo.getDescription()).thenReturn("this is just a test description");

        when(geoResource.getInfo(any())).thenReturn(geoResourceInfo);

        assertTrue(CatalogImpl.check(geoResource, testAST));
    }

    @Test
    public void checkIsFalseIfNothingMatches() throws Exception {
        when(geoResourceInfo.getTitle()).thenReturn(NOT_MATCHING_TEXT);
        when(geoResourceInfo.getName()).thenReturn(NOT_MATCHING_TEXT);
        when(geoResourceInfo.getKeywords()).thenReturn(null);
        when(geoResourceInfo.getSchema()).thenReturn(URI.create("whatever/not/match"));
        when(geoResourceInfo.getDescription()).thenReturn("this is just a description");

        when(geoResource.getInfo(any())).thenReturn(geoResourceInfo);

        assertFalse(CatalogImpl.check(geoResource, testAST));
    }

    @Test
    public void checkIsFalseWithServiceAndNullPattern() {
        assertFalse(CatalogImpl.check(serviceMock, null));
    }

    @Test
    public void checkIsFalseWithNullService() {
        IService nullService = null;
        assertFalse(CatalogImpl.check(nullService, testAST));
    }

    @Test
    public void checkIsTrueIfServiceInfoTitleMatch() throws Exception {
        when(serviceInfo.getTitle()).thenReturn(MATCHING_TEXT);
        when(serviceMock.getInfo(null)).thenReturn(serviceInfo);

        assertTrue(CatalogImpl.check(serviceMock, testAST));
    }

    @Test
    public void checkIsTrueIfServiceInfoKeywordsMatch() throws Exception {
        when(serviceInfo.getTitle()).thenReturn(NOT_MATCHING_TEXT);
        when(serviceInfo.getKeywords())
                .thenReturn(new HashSet<>(Arrays.asList(NOT_MATCHING_TEXT, MATCHING_TEXT)));
        when(serviceMock.getInfo(null)).thenReturn(serviceInfo);

        assertTrue(CatalogImpl.check(serviceMock, testAST));
    }

    @Test
    public void checkIsTrueIfServiceInfoWithNullKeywordInSetMatch() throws Exception {
        when(serviceInfo.getTitle()).thenReturn(NOT_MATCHING_TEXT);
        when(serviceInfo.getKeywords())
                .thenReturn(new HashSet<>(Arrays.asList(NOT_MATCHING_TEXT, null, MATCHING_TEXT)));
        when(serviceMock.getInfo(null)).thenReturn(serviceInfo);

        assertTrue(CatalogImpl.check(serviceMock, testAST));
    }

    @Test
    public void checkIsTrueIfServiceInfoAbstractMatch() throws Exception {
        when(serviceInfo.getTitle()).thenReturn(NOT_MATCHING_TEXT);
        when(serviceInfo.getAbstract())
                .thenReturn("Detailed Abstract with " + MATCHING_TEXT + " matching string");
        when(serviceMock.getInfo(null)).thenReturn(serviceInfo);

        assertTrue(CatalogImpl.check(serviceMock, testAST));
    }

    @Test
    public void checkIsTrueIfServiceInfoDescriptionMatch() throws Exception {
        when(serviceInfo.getTitle()).thenReturn(NOT_MATCHING_TEXT);
        when(serviceInfo.getDescription()).thenReturn(MATCHING_TEXT);
        when(serviceMock.getInfo(null)).thenReturn(serviceInfo);

        assertTrue(CatalogImpl.check(serviceMock, testAST));
    }

    @Test
    public void checkIsFalseIfServiceNotMatchAnything() throws Exception {
        when(serviceInfo.getTitle()).thenReturn(NOT_MATCHING_TEXT);
        when(serviceInfo.getDescription()).thenReturn(NOT_MATCHING_TEXT);
        when(serviceMock.getInfo(null)).thenReturn(serviceInfo);

        assertFalse(CatalogImpl.check(serviceMock, testAST));
    }
}
