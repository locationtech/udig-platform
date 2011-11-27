package net.refractions.udig.tutorials.style.color;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IStyleBlackboard;
import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.RenderException;
import net.refractions.udig.tutorials.catalog.csv.CSV;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.csvreader.CsvReader;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class ColorCSVRenderer extends RendererImpl {

    @Override
    public void render( IProgressMonitor monitor ) throws RenderException {
        Graphics2D g = getContext().getImage().createGraphics();
        render(g, monitor);
    }

    /**
     * This example shows how to obtain a color.
     * 
     * @param g
     * @param monitor
     * @throws RenderException
     */
public void render( Graphics2D g, IProgressMonitor monitor ) throws RenderException {
    if (monitor == null)
        monitor = new NullProgressMonitor();

    CsvReader reader = null;
    try {
        ILayer layer = getContext().getLayer();
        IGeoResource resource = layer.findGeoResource(CSV.class);
        if (resource == null)
            return;        
        ReferencedEnvelope bounds = getRenderBounds();
        monitor.subTask("connecting");
        CSV csv = resource.resolve(CSV.class, null);
        // LOOK UP STYLE
        IStyleBlackboard style = layer.getStyleBlackboard();
        Color color = (Color) style.get( ColorStyle.ID );

        // DATA TO WORLD
        CoordinateReferenceSystem dataCRS = layer.getCRS();
        CoordinateReferenceSystem worldCRS = context.getCRS();
        MathTransform dataToWorld = CRS.findMathTransform(dataCRS, worldCRS, false);

        // DRAW FILE
        monitor.beginTask("csv render", csv.getSize());
        reader = csv.reader();
        reader.readHeaders();
        int nameIndex = reader.getIndex("name");
        Coordinate worldLocation = new Coordinate();
        while( reader.readRecord() ) {
            Point point = CSV.getPoint(reader);
            Coordinate dataLocation = point.getCoordinate();
            try {
                JTS.transform(dataLocation, worldLocation, dataToWorld);
            } catch (TransformException e) {
                continue;
            }
            if (bounds != null && !bounds.contains(worldLocation)) {
                continue; // optimize!
            }
            java.awt.Point p = getContext().worldToPixel(worldLocation);

            g.setColor( color );
            g.fillRect(p.x-2, p.y-2, 6, 6);
            
            g.setColor(Color.BLACK);
            String name = reader.get(nameIndex);
            g.drawString(name, p.x + 15, p.y + 15);
            monitor.worked(1);
            if (monitor.isCanceled()) break;
        }
    } catch (IOException e) {
        throw new RenderException(e); // rethrow any exceptions encountered
    } catch (FactoryException e) {
        throw new RenderException(e); // rethrow any exceptions encountered
    } finally {
        if (reader != null)
            reader.close();
        monitor.done();
    }
}

}
