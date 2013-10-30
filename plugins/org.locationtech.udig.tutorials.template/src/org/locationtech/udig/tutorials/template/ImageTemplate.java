/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.tutorials.template;

import org.locationtech.udig.printing.model.Box;
import org.locationtech.udig.printing.model.ModelFactory;
import org.locationtech.udig.printing.model.Page;
import org.locationtech.udig.printing.model.impl.LabelBoxPrinter;
import org.locationtech.udig.printing.model.impl.MapBoxPrinter;
import org.locationtech.udig.printing.ui.internal.AbstractTemplate;
import org.locationtech.udig.project.internal.Map;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

public class ImageTemplate extends AbstractTemplate {

    public void init( Page page, Map map ) {        
        page.setName(map.getName());

        Dimension a4 = new Dimension( 842, 595 ); // assume 72 pixels per inch
        page.setSize( a4 );        

        Box labelBox = ModelFactory.eINSTANCE.createBox();
        Box mapBox = ModelFactory.eINSTANCE.createBox();
        Box imageBox = ModelFactory.eINSTANCE.createBox();
        
        boxes.add(labelBox);
        boxes.add(mapBox);
        boxes.add(imageBox);
        
        mapBox.setSize(new Dimension(400, 400));
        imageBox.setSize(new Dimension(200, 162));
        labelBox.setSize(new Dimension(150, 30));
        
        imageBox.setLocation(new Point(43, 10));
        mapBox.setLocation(new Point(143, 210));
        labelBox.setLocation(new Point(100, 612));
        
        LabelBoxPrinter lbPrinter = new LabelBoxPrinter();
        MapBoxPrinter mbPrinter = new MapBoxPrinter();
        ImageBoxPrinter ibPrinter = new ImageBoxPrinter();
        
        mbPrinter.setMap(map);
        lbPrinter.setText("Image Example");
        
        mapBox.setBoxPrinter(mbPrinter);
        labelBox.setBoxPrinter(lbPrinter);
        imageBox.setBoxPrinter(ibPrinter);
        
    }

    public String getName() {
        return "Image Template"; //Should be internationalized!
    }

    public String getAbbreviation() {
        return getName();
    }

}
