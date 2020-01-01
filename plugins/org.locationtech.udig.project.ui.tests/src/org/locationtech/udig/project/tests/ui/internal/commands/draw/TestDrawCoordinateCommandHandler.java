/* uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2018, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.project.tests.ui.internal.commands.draw;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.HandlerUtil;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.Command;
import org.locationtech.udig.project.command.CompositeCommand;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.internal.command.navigation.SetViewportCenterCommand;
import org.locationtech.udig.project.ui.AnimationUpdater;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.IAnimation;
import org.locationtech.udig.project.ui.commands.DrawCommandFactory;
import org.locationtech.udig.project.ui.commands.IDrawCommand;
import org.locationtech.udig.project.ui.internal.FeatureAnimation;
import org.locationtech.udig.project.ui.internal.commands.draw.DrawCoordinateCommand;
import org.locationtech.udig.tools.internal.CursorPosition;

/**
 * Test for DraWCoordinateCommand. 
 * The handler prompts for input a coordinate. navigates to it and
 * flashes 3 times using an animation based on {@link DrawCoordinateCommand}
 * 
 * @author nprigour
 *
 */
public class TestDrawCoordinateCommandHandler extends AbstractHandler {

    private boolean isOpen;

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {

        if (isOpen) {
            return null;
        }

        CoordinateSearchDialog dialog = new CoordinateSearchDialog(
                HandlerUtil.getActiveShell(event)) {

            @Override
            protected void init() {
                super.init();
                isOpen = true;
            }

            @Override
            public boolean close() {
                boolean result = false;
                try {
                    isOpen = false;
                    result = super.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            }
        };

        dialog.open();

        return null;
    }

    /**
     * shows a dialog for providing coordinate to show.
     * 
     * @author nprigour
     *
     */
    private static class CoordinateSearchDialog extends MessageDialog {

        static char decimalSeparator;

        IAnimation animation;

        private Text coordText;

        private Coordinate position;

        public CoordinateSearchDialog(Shell parentShell) {
            super(parentShell, "Show Coordinate", null, null, NONE, new String[] {}, 0);

            setShellStyle(SWT.DIALOG_TRIM | SWT.CLOSE);

            decimalSeparator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
        }

        protected void init() {

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.dialogs.MessageDialog#open()
         */
        @Override
        public int open() {
            return super.open();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.dialogs.Dialog#close()
         */
        @Override
        public boolean close() {
            return super.close();
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Control control = super.createDialogArea(parent);
            init();
            return control;
        }

        /**
         * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected Control createCustomArea(Composite parent) {

            Composite area = new Composite(parent, SWT.NONE);
            area.setLayout(new GridLayout(1, false));

            coordText = new Text(area, SWT.BORDER | SWT.CENTER);
            GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
            gd_text.widthHint = 180;
            coordText.setLayoutData(gd_text);

            coordText.addKeyListener(new KeyListener() {

                public void keyPressed(KeyEvent e) {
                    // do nothing
                }

                public void keyReleased(KeyEvent e) {
                    if (e.character == SWT.Selection) {
                        go();
                    } else if (e.character == SWT.ESC) {
                        coordText.setText(CursorPosition.getString(getPosition()));
                    }

                }
            });
            return area;
        }

        /**
         * sets the position based on passed value. If an 
         * exception occurs position is set to the ViewPort center.  
         * @param coord
         */
        public void setPosition(Coordinate coord) {
            position = coord;
            try {
                if (coordText != null && !coordText.isDisposed()) {
                    coordText.setText(CursorPosition.getString(position));
                }
            } catch (Exception e) {
                position = getPosition();
                coordText.setText(CursorPosition.getString(getPosition()));              
            }
                 
        }

        /**
         * @return the position
         */
        protected Coordinate getPosition() {
            if (position == null) {
                position = ApplicationGIS.getActiveMap().getViewportModel().getCenter();
            }
            return position;
        }

        /**
         * 
         */
        private void go() {
            final IMap activeMap = ApplicationGIS.getActiveMap();
            if (activeMap.equals(ApplicationGIS.NO_MAP)) {
                return;
            }

            final Coordinate newpos = getCoordinateToShow(activeMap); 

            List<Command> commandList = new ArrayList<Command>();
            
            // create the set viewport command if need be
            setPosition(newpos);
            MapCommand centerCommand = new SetViewportCenterCommand(newpos);
            commandList.add(centerCommand);

            MapCommand animateCommand = new AbstractCommand() {

                @Override
                public void run(IProgressMonitor monitor) throws Exception {
                    final List<IDrawCommand> commands = new ArrayList<IDrawCommand>();

                    final Point point = activeMap.getViewportModel().worldToPixel(newpos);
                    Rectangle2D r = new Rectangle2D.Double(point.x, point.y, 3, 3);
                    DrawCoordinateCommand drawCommand = DrawCommandFactory.getInstance()
                            .createDrawCoordinateCommand(newpos, activeMap);
                    drawCommand.setFill(Color.BLACK);
                    drawCommand.setPaint(Color.BLACK);
                    drawCommand.setUseCircle(true);
                    // drawCommand.setMap(activeMap);
                    commands.add(drawCommand);

                    Rectangle2D rect = new Rectangle();
                    final Rectangle validArea = (Rectangle) rect;

                    animation = new FeatureAnimation(commands, validArea);
                    
                    AnimationUpdater.runTimer(activeMap.getRenderManager().getMapDisplay(),
                            animation);
                }

                @Override
                public Command copy() {
                    return null;
                }

                @Override
                public String getName() {
                    return "animateCoordinateCommand";
                }

            };

            commandList.add(animateCommand);

            if (!commandList.isEmpty()) {
                CompositeCommand commands = new CompositeCommand(commandList);
                activeMap.sendCommandASync(commands);
            }

        }

        /**
         * return the input coordinate or the center of the current viewport
         * if parsing fails the {@link #getPosition()} coordinate is returned
         * 
         * @param activeMap
         * @return
         */
        private Coordinate getCoordinateToShow(IMap activeMap) {
            try {
                return CursorPosition.parse(coordText.getText(),
                        activeMap.getEditManager().getSelectedLayer().getCRS());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getPosition();
        }

    }

}
