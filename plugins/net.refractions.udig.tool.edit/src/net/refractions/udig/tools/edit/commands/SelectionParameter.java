package net.refractions.udig.tools.edit.commands;

import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditToolHandler;

import com.vividsolutions.jts.geom.Geometry;

public class SelectionParameter {
    public final EditToolHandler handler;
    public final MapMouseEvent event;
    public final Class< ? extends Geometry>[] acceptableClasses;
    public final short filterType;
    public final boolean permitClear;
    public final boolean onlyAdd;

    public final List<SelectionStrategy> selectionStrategies = new LinkedList<SelectionStrategy>();
    public final List<DeselectionStrategy> deselectionStrategies = new LinkedList<DeselectionStrategy>();

    public SelectionParameter( EditToolHandler handler, MapMouseEvent e,
            Class< ? extends Geometry>[] acceptableClasses, short filterType, boolean permitClear,
            boolean onlyAdd ) {
        this.handler = handler;
        this.event = e;
        this.acceptableClasses = acceptableClasses;
        this.filterType = filterType;
        this.permitClear = permitClear;
        this.onlyAdd = onlyAdd;
    }

}
