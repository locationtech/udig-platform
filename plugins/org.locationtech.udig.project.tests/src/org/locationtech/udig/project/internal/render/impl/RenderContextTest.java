/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.tests.support.AbstractProjectTestCase;

public class RenderContextTest extends AbstractProjectTestCase {

    private IRenderContext context;

    @Before
    public void setUp() {
        context = new RenderContextImpl(false);
    }

    @Test
    public void defaultImageIfRequestedSizeIsTooSmall() {
        BufferedImage image = context.getImage(0, 0);
        assertBufferedImage(1, 1, image);

    }

    @Test
    public void returnRequestSize() {
        BufferedImage firstImage = context.getImage(32, 32);
        assertBufferedImage(32, 32, firstImage);
        // ask a second time

        BufferedImage secondImage = context.getImage(32, 32);
        assertSame(firstImage, secondImage);

        BufferedImage thirdImage = context.getImage(132, 132);
        assertBufferedImage(132, 132, thirdImage);
        assertNotSame(firstImage, thirdImage);
    }

    private void assertBufferedImage(int expectedWidth, int expectedHeight, BufferedImage image) {
        assertNotNull("BufferedImage created", image); //$NON-NLS-1$
        assertEquals("width", expectedWidth, image.getWidth()); //$NON-NLS-1$
        assertEquals("height", expectedHeight, image.getHeight()); //$NON-NLS-1$
    }

}
