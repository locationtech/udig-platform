2 NavCommand Example
====================

NavCommand is used to go places, specifically these are used to modify the Viewport.

:doc:`Examples`


* :doc:`Use from an ActionTool`


:doc:`Custom Navigation Command`


* :doc:`INavCommand Code Example`


Changes to the viewport are kept in their own undo/redo stack accessible with the Back/Forward
navigation buttons (just like a web browser).

Examples
========

Use from an ActionTool
----------------------

Action Tools are a good place to issue commands from; this example places a "Go Home" option in the
Navigate menu. This example is included in net.refractions.udig.tutorials.examples.

::

    <extension
             point="net.refractions.udig.project.ui.tool">
          <actionTool
                class="net.refractions.udig.tutorials.examples.GoHomeActionTool"
                id="net.refractions.udig.tutorials.examples.actionTool1"
                menuPath="navigate/gohome"
                name="Home"
                onToolbar="false"
                tooltip="Go Home">
             <enablement></enablement>
          </actionTool>
       </extension>

And here is the implementation:

::

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

Custom Navigation Command
=========================

A navigation command (INavCommand) is a special type of command that changes the current area being
viewed, the viewport. Navigation commands are placed in a separate stack from normal commands and
are undone and redone using the navigation arrows on the user interface as opposed to the undo/redo
buttons int the edit menu.

Navigation commands should be limited to commands that only change the view area, not changing the
current CRS.

The class AbstractNavCommand reduces the burden on the developer by implementing undo functionality.

INavCommand Code Example
------------------------

**PanCommand.java**

::

    /**
     * A command that pans the viewport. The command can be defined in terms of pixels on the screen or
     * in terms of world units.
     * 
     * @author jeichar
     * @since 0.3
     */
    public class PanCommand extends AbstractNavCommand implements NavCommand {

        double worldx, worldy;

        int pixelx, pixely;

        boolean inPixel;

        /**
         * Creates a new instance of PanCommand
         * 
         * @param pixelx The amount to pan in the x direction
         * @param pixely The amount to pan in the y direction
         */
        public PanCommand(int pixelx, int pixely) {
            this.pixelx = pixelx;
            this.pixely = pixely;
            inPixel = true;
        }

        /**
         * Creates a new instance of PanCommand
         * 
         * @param worldx The amount to pan in the x direction
         * @param worldy The amount to pan in the y direction
         */
        public PanCommand(double worldx, double worldy) {
            this.worldx = worldx;
            this.worldy = worldy;
            inPixel = false;
        }

        /**
         * @see net.refractions.udig.project.internal.command.navigation.AbstractNavCommand#runImpl()
         */
        protected void runImpl() throws Exception {
            if (inPixel)
                model.panUsingScreenCoords(pixelx, pixely);
            else
                model.panUsingWorldCoords(worldx, worldy);
        }

        /**
         * @see net.refractions.udig.project.internal.command.Command#copy()
         */
        public Command copy() {
            if (inPixel)
                return new PanCommand(pixelx, pixely);

            return new PanCommand(worldx, worldy);
        }

        /**
         * @see net.refractions.udig.project.command.Command#getName()
         */
        public String getName() {
            return Policy.bind("PanCommand.pan"); //$NON-NLS-1$
     }

    }

