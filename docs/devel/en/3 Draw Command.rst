3 Draw Command
==============

Draw Commands are a special, and slightly confusing class of commands. Draw Commands affect the
MapDisplay and are how tools provide dynamic feedback to users.

:doc:`Always on!`


:doc:`Note on Drawing Technology`


* :doc:`What are Draw Commands used for Again`

* :doc:`How do I remove a Draw Command`

* :doc:`How do I do Animation`


:doc:`Draw Command Example`


Always on!
==========

Draw commands are strange because then are not fire and forget like "normal" commands because the
command must exist each time the MapDisplay is refreshed/repainted (this is different from
re-rendering ).

-  Each time the MapDisplay is refreshed all the currently active draw commands are executed if
   isActive return true.
-  Each time a re-rendering occurs all commands are removed.

This is the case because it is the most common requirement of tools. For example pan applies a
transformation to the ViewportGraphics and when the button is released a re-rendering occurs.
Currently there is not support for persistent DrawCommands although if the requirement emerges and
new interface can be created to flag commans that should stay valid across renders.

There are two types of Draw Commands currently IPreMapDrawCommand and the basic IDrawCommand.

-  Normal **IDrawCommands** are executed after the rendered image is displayed. They are good for
   drawing shapes, such as the zoom box.
-  **IPreMapDrawCommands** are executed before the rendered image is displayed. The TranslateCommand
   is an example of a IPreMapDrawCommand.

Summary:

#. Draw Commands must provide the correct result after every execution.
#. Draw Commands have an isActive method that the MapDisplay calls to determine whether the command
   should be kept in the execution stack. If you call setActive( false ) on your command it will be
   removed from the screen.
#. All Draw Commands are dropped when a re-render begins.
#. Two types of Draw Commands: IPreMapDrawCommand (executed before rendered image is drawn) and
   IDrawCommand (executed after the rendered image is drawn).

Note on Drawing Technology
==========================

Note the abstraction used to draw against is not a Java Graphics2D, we make use of our own API
inorder to draw to both SWT, AWT and hopefully OpenGL.

As with all uDig APIs, if you need something please ask! We are limited by time, not ability, we
cannot tell what is needed until someone asks for it.

What are Draw Commands used for Again
-------------------------------------

Draw commands are used to provide quick visual feedback; often in response to the activity of a
tool; or in response to a background process.

How do I remove a Draw Command
------------------------------

Set your command to be inactive:

::

    drawCommand.setActive( false ); // command will be removed next time the screen is redrawn

How do I do Animation
---------------------

Change what your draw command draws and trigger a screen refresh; this is preferable to creating a
new draw command for each frame of animation; and calling setActive( false ) on the previous frame.

There is a code example of how to do this in the tutorials directory.

Draw Command Example
====================

**DeleteFeatureCommand.java**

::

    /**
     * Sets the ViewportGraphics object translate its 0,0 coordinate by -x,-y.  
     * IE. shapes are drawn down and right if x,y are both positive.
     * 
     * @author jeichar
     * @since 0.3
     */
    public class TranslateCommand extends AbstractDrawCommand implements IMapTransformCommand, IPreMapDrawCommand {

        private Point offset;

        /**
         * Construct <code>TranslateCommand</code>.
         *
         * @param offset The amount of offset
         */
        public TranslateCommand(Point offset){
            this.offset=offset;
        }
        
        /**
         * Construct <code>TranslateCommand</code>.
         *
         * @param x The amount of offset in the x-direction
         * @param y The amount of offset in the y-direction
         */
        public TranslateCommand(int x, int y){
            this.offset=new Point(x,y);
        }
        /**
         * @see net.refractions.udig.project.internal.command.Command#run()
         */
        public void run() throws Exception {
            if(offset.x>0){
                graphics.clearRect(0,0,offset.x,display.getHeight());
            }else{
                graphics.clearRect(display.getWidth(),0,-offset.x,display.getHeight());            
            }
            if(offset.y>0){
                graphics.clearRect(0,0,display.getWidth(), offset.y);
            }else{
                graphics.clearRect(0,display.getHeight(),display.getWidth(),-offset.y);            
            }
            graphics.translate(offset);
        }

        /**
         * @see net.refractions.udig.project.internal.command.Command#copy()
         */
        public Command copy() {
            return new TranslateCommand(offset);
        }

        /**
         * Sets the amount the command will translate during the next paint phase
         * 
         * @param x x-translation
         * @param y y-translation
         */
        public void setTranslation( int x, int y ) {
            offset.x=x;
            offset.y=y;
        }
        /**
         * Sets the amount the command will translate during the next paint phase
         * 
         * @param offset  The amount of translation
         */    
        public void setTranslation(Point offset ){
            this.offset=offset;
        }

        /**
         * @see net.refractions.udig.project.command.Command#getName()
         */
        public String getName() {
            return Policy.bind("TranslateCommand.translateDisplayArea"); //$NON-NLS-1$
        }

    }

