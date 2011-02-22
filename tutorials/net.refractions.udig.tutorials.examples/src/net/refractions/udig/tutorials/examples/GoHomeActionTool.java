package net.refractions.udig.tutorials.examples;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.spatialschema.geometry.DirectPosition;
import org.opengis.spatialschema.geometry.MismatchedDimensionException;

import com.vividsolutions.jts.geom.Coordinate;

import net.refractions.udig.project.command.NavCommand;
import net.refractions.udig.project.command.factory.NavigationCommandFactory;
import net.refractions.udig.project.internal.command.navigation.PanCommand;
import net.refractions.udig.project.internal.command.navigation.SetViewportCenterCommand;
import net.refractions.udig.project.ui.tool.AbstractActionTool;

public class GoHomeActionTool extends AbstractActionTool {
    public void run() {
        NavigationCommandFactory factory = getContext().getNavigationFactory();

        CoordinateReferenceSystem worldCRS = getContext().getCRS();
        double y = 48.428611;
        double x = -123.365556;
        NavCommand goHome = new SetViewportCenterCommand(new Coordinate(x,y), DefaultGeographicCRS.WGS84 );

        getContext().sendASyncCommand( goHome );
    }
    public void dispose() {
    }
}
