package net.refractions.udig.tutorials.distancetool;

import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.SimpleTool;

import org.eclipse.jface.action.IStatusLineManager;
import org.geotools.geometry.jts.JTS;

import com.vividsolutions.jts.geom.Coordinate;

public class DistanceTool extends SimpleTool {
	public DistanceTool(){
		super( MOUSE );
	}
	public DistanceTool(int targets) {
		super(targets);
	}
	Coordinate start; // records where in the world the user clicked
	public void onMousePressed(MapMouseEvent e) {
		start=getContext().pixelToWorld(e.x, e.y);
	}
	public void onMouseReleased(MapMouseEvent e) {
		Coordinate end=getContext().pixelToWorld(e.x, e.y);
		try {
			double distance=JTS.orthodromicDistance(
	              start, end,
	              getContext().getCRS() );
			displayOnStatusBar(distance);
		} catch (Exception e1) {
			displayError();
		}
	}
	private void displayError() {
		final IStatusLineManager statusBar =
	              getContext().getActionBars().getStatusLineManager ();

		if( statusBar==null )
			return; // shouldn't happen if the tool is being used.

		getContext().updateUI(new Runnable() {
			public void run() {
				statusBar.setErrorMessage("Unable to calculate the distance");
		        }
		});
	  }
	private void displayOnStatusBar(double distance) {
	    final IStatusLineManager statusBar =
	        getContext().getActionBars().getStatusLineManager ();

	    if( statusBar==null )
			return; // shouldn't happen if the tool is being used.
		int totalmeters=(int)distance;
		final int km=totalmeters/1000;
		final int meters=totalmeters-(km*1000);
		float cm = (float) (distance-totalmeters)*10000;
		cm = Math.round(cm);
		final float finalcm=cm/100;
		getContext().updateUI(new Runnable(){
	                public void run() {
                        statusBar.setErrorMessage(null);
				statusBar.setMessage("Distance =  "+km+","+meters+"m "+finalcm+"cm");
			}
		});
	}
}
