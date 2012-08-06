/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.ui.filter;

import java.awt.Color;

import junit.framework.TestCase;

import net.refractions.udig.ui.filter.ViewerFactory.Appropriate;

import org.eclipse.swt.SWT;
import org.geotools.filter.expression.LiteralBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;

/**
 * Test class for {@link RGBExpressionViewer}.
 * 
 * @author Naz Chan 
 */
@SuppressWarnings("nls")
public class RGBExpressionViewerTest extends TestCase {

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
