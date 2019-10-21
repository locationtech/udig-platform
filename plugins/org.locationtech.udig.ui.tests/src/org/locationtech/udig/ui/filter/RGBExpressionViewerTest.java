/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.geotools.brewer.styling.filter.expression.LiteralBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.udig.ui.filter.ViewerFactory.Appropriate;

/**
 * Test class for {@link RGBExpressionViewer}.
 * 
 * @author Naz Chan 
 */
@SuppressWarnings("nls")
public class RGBExpressionViewerTest {

    @Test
    public void testFactory() {
        
        final RGBExpressionViewer.Factory factory = new RGBExpressionViewer.Factory();
        
        final LiteralBuilder lb = new LiteralBuilder();
        lb.value(new Color(150, 150, 150));
        
        assertEquals("Score is not expected.", Appropriate.APPROPRIATE.getScore(),
                factory.score(null, lb.build()));
        
        assertEquals("Score is not expected.", Appropriate.NOT_APPROPRIATE.getScore(),
                factory.score(null, null));
        
        try {
            assertEquals("Score is not expected.", Appropriate.NOT_APPROPRIATE.getScore(),
                    factory.score(null, CQL.toExpression("STATE")));
        } catch (CQLException e) {
            e.printStackTrace();
            fail("CQL expression should not fail.");
        }
        
    }
    
    @Deprecated
    @Ignore
    @Test
    public void xtestRefreshExpressionNPE() {
        
        // Need to find a way to instantiate the viewer without a parent composite
        final RGBExpressionViewer viewer = new RGBExpressionViewer(null, SWT.MULTI);
        
        final LiteralBuilder litBuilder = new LiteralBuilder();
        litBuilder.value(new Color(150, 150, 150)); 
        viewer.setExpression(litBuilder.build());
        viewer.refreshExpression();
        
        viewer.setExpression(null);
        viewer.refreshExpression();
        
    }
    
}
