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
package org.locationtech.udig.project.ui.commands;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.io.IOException;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.render.IViewportModel;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.project.ui.IAnimation;
import org.locationtech.udig.project.ui.internal.commands.draw.CompositeDrawCommand;
import org.locationtech.udig.project.ui.internal.commands.draw.DrawCoordinateCommand;
import org.locationtech.udig.project.ui.internal.commands.draw.DrawEditFeatureCommand;
import org.locationtech.udig.project.ui.internal.commands.draw.DrawFeatureCommand;
import org.locationtech.udig.project.ui.internal.commands.draw.DrawShapeCommand;
import org.locationtech.udig.project.ui.internal.commands.draw.StartAnimationCommand;
import org.locationtech.udig.project.ui.internal.commands.draw.StopAnimationCommand;
import org.locationtech.udig.project.ui.internal.commands.draw.TranslateCommand;
import org.locationtech.udig.project.ui.internal.commands.draw.ZoomDrawCommand;
import org.locationtech.udig.ui.graphics.ViewportGraphics;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Creates draw commands.
 * 
 * @author jeichar
 * @since 0.3
 */
public class DrawCommandFactory {

    private DrawCommandFactory() {
        // no op
    }
    private static final DrawCommandFactory instance = new DrawCommandFactory();

    /**
     * Creates a new DrawCommandFactory object
     * 
     * @return a new DrawCommandFactory object
     */
    public static DrawCommandFactory getInstance() {
        return instance;
    }

    /**
     * Creates a new {@linkplain DrawShapeCommand}
     * 
     * @param shape The shape to draw
     * @param paint the shape outline color
     * @param lineStyle see {@linkplain ViewportGraphics} for line styles
     * @param lineWidth The width of the shape outline
     * @return a new DrawShapeCommand object
     * @see DrawShapeCommand
     */
    public DrawShapeCommand createDrawShapeCommand( Shape shape, Color paint, int lineStyle,
            int lineWidth ) {
        return new DrawShapeCommand(shape, paint, lineStyle, lineWidth);
    }
    /**
     * Creates a new {@linkplain DrawShapeCommand}
     * 
     * @param shape
     * @param paint
     * @return a new DrawShapeCommand object
     * @see DrawShapeCommand
     */
    public DrawShapeCommand createDrawShapeCommand( Shape shape, Color paint ) {
        return createDrawShapeCommand(shape, paint, -1, 1);
    }
    /**
     * Creates a new {@linkplain DrawShapeCommand}
     * 
     * @param shape
     * @return a new DrawShapeCommand object
     */
    public DrawShapeCommand createDrawShapeCommand( Shape shape ) {
        return createDrawShapeCommand(shape, null, -1, 1);
    }
    /**
     * Creates a new {@linkplain DrawEditFeatureCommand}
     * 
     * @param model The viewport model associated with the viewport that will be rendered to.
     * @return a new DrawFeatureCommand object
     * @see DrawEditFeatureCommand
     */
    public IDrawCommand createEditFeaturesCommand( IViewportModel model ) {
        return new DrawEditFeatureCommand(model);
    }
    /**
     * Creates a new {@linkplain TranslateCommand}
     * 
     * @param offset The amount of translation
     * @return a new TranslateCommand object
     * @see TranslateCommand
     */
    public TranslateCommand createTranslateCommand( Point offset ) {
        return new TranslateCommand(offset);
    }
    /**
     * Creates a new {@linkplain TranslateCommand}
     * 
     * @param x the amount of y translation
     * @param y the amount of y translation
     * @return a new {@linkplain TranslateCommand}
     * @see TranslateCommand
     */
    public TranslateCommand createTranslateCommand( int x, int y ) {
        return new TranslateCommand(x, y);
    }
    /**
     * Creates a new {@linkplain TranslateCommand}
     * 
     * @param x the amount of y translation
     * @param y the amount of y translation
     * @return a new {@linkplain ZoomDrawCommand}
     * @see ZoomDrawCommand
     */
    public ZoomDrawCommand createZoomDrawCommand( int centerx, int centery, double amount ) {
        return new ZoomDrawCommand(centerx, centery, amount);
    }

    /**
     * Creates a new {@linkplain DrawFeatureCommand}
     * 
     * @param feature the feature to draw
     * @param layer the layer that the feature is part of.
     * @param model the ViewportModel that is used to calculate size and position.
     * @return a new {@linkplain DrawFeatureCommand}
     * @see DrawFeatureCommand
     */
    public DrawFeatureCommand createDrawFeatureCommand( SimpleFeature feature, ILayer layer ) {
        try {
            return new DrawFeatureCommand(feature, layer);
        } catch (IOException e) {
            return new DrawFeatureCommand(feature);
        }
    }

    /**
     * Creates a new {@linkplain DrawFeatureCommand}
     * 
     * @param feature the feature to draw
     * @param evaluationObject the layer that the feature is part of.
     * @param model the ViewportModel that is used to calculate size and position.
     * @return a new {@linkplain DrawFeatureCommand}
     * @see DrawFeatureCommand
     */
    public DrawFeatureCommand createDrawFeatureCommand( SimpleFeature feature ) {
        return new DrawFeatureCommand(feature);
    }

    /**
     *  Creates a new {@linkplain DrawCoordinateCommand}
     *  
     * @param coord the Coordinate to draw its location
     * @param map the map to run command on
     * @return a new {@linkplain DrawCoordinateCommand}
     * @see DrawCoordinateCommand
     */
    public DrawCoordinateCommand createDrawCoordinateCommand( Coordinate coord, IMap map) {
        return new DrawCoordinateCommand(coord, map);
    }
       
    /**
     *  Creates a new {@linkplain DrawCoordinateCommand}
     *  
     * @param coord the Coordinate to draw its location
     * @param crs the crs of the Coordinate
     * @return a new {@linkplain DrawCoordinateCommand}
     * @see DrawCoordinateCommand
     */
    public DrawCoordinateCommand createDrawCoordinateCommand( Coordinate coord, CoordinateReferenceSystem crs) {
        return new DrawCoordinateCommand(coord, crs);
    }
    
    /**
     * Creates a new {@linkplain DrawFeatureCommand}
     * 
     * @param feature the feature to draw
     * @param evaluationObject the layer that the feature is part of.
     * @return a new {@linkplain DrawFeatureCommand}
     * @see DrawFeatureCommand
     */
    public DrawFeatureCommand createDrawFeatureCommand( SimpleFeature feature,
            CoordinateReferenceSystem crs ) {
        return new DrawFeatureCommand(feature, crs);
    }

    
    /**
     * Creates a new {@linkplain StartAnimationCommand}
     * 
     * @return a new {@linkplain StartAnimationCommand}
     * @see StartAnimationCommand
     */
    public UndoableMapCommand createStartAnimationCommand(IMapDisplay display, List<IAnimation> animations ) {
        return new StartAnimationCommand(display, animations);
    }

    /**
     * Creates a new {@linkplain StartAnimationCommand}
     * 
     * @return a new {@linkplain StartAnimationCommand}
     * @see StartAnimationCommand
     */
    public UndoableMapCommand createStopAnimationCommand( IMapDisplay display, List<IAnimation> animations ) {
        return new StopAnimationCommand(display, animations);
    }
    
    /**
     * Creates a new {@linkplain CompositeDrawCommand}
     * 
     * @param commandsArray
     * @return
     */
    public IDrawCommand createCompositeDrawCommand(IDrawCommand[] commandsArray){
    	return new CompositeDrawCommand(commandsArray);
    }
    
    /**
     * Creates a new {@linkplain CompositeDrawCommand}
     * 
     * @param commandsList
     * @return
     */
    public IDrawCommand createCompositeDrawCommand(List<? extends IDrawCommand> commandsList){
    	return new CompositeDrawCommand(commandsList);
    }
    
}
