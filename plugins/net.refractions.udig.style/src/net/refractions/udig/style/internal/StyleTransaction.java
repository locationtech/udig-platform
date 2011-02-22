package net.refractions.udig.style.internal;

import java.util.Set;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.style.IStyleConfigurator;

public class StyleTransaction {

    /** master of styles **/
    private StyleManager styleManager;

    /** The layer being styled **/
    private Layer layer;

    /** The style blackboard being populated with style information **/
    private StyleBlackboard styleBlackboard;

    public StyleTransaction(StyleManager styleManager, Layer layer, Set<IStyleConfigurator> configurators) {
        this.styleManager = styleManager;
        this.layer = layer;
    }

    /**
     * Returns the layer being worked on by the transaction.
     *
     * @return Returns the layer.
     *
    public Layer getLayer() {
        return layer;
    }

    /**
     * Sets the layer being worked on by the transaction.
     * @param layer The layer to set.
     *
    public void setLayer( Layer layer ) {
        this.layer = layer;
    }
    */
    /**
     * Sets the style blackboard used in the transaction.
     *
     * @param styleBlackboard The new style blackboard.
     */
    public void setStyleBlackboard(StyleBlackboard styleBlackboard) {
        this.styleBlackboard = styleBlackboard;
    }

    /**
     * Returns the style blackboard being used by the transaction.
     *
     * @return The current style blackboard.
     */
    public StyleBlackboard getStyleBlackboard() {
        return styleBlackboard;
    }

    /**
     *
     * @return Returns the configurators.
     *
    public Set<IStyleConfigurator> getConfigurators() {
        return styleManager.getCurrentConfigurators();
    }*/

    /**
     * @param configurators The configurators to set.
     *
    public void setConfigurators( Set<IStyleConfigurator> configurators ) {
        this.configurators = configurators;
    }*/

    /**
     * Starts the styling transaction.
     */
    public void begin() {
       // create a clone of the blackboard
       styleBlackboard = (StyleBlackboard) styleManager.getCurrentLayer().getStyleBlackboard().clone();

//       for (IStyleConfigurator config : styleManager.getStyleConfigurators()) {
//           config.focus(styleManager.getCurrentLayer(),styleBlackboard);
//       }
    }

    /*
    public void suspend() {
        //tell all the configurators to save state to the blackboard
        for (IStyleConfigurator config : configurators) {
            //only apply if ui has been created
            if (config.getControl() != null && !config.getControl().isDisposed()) {
                config.apply();
            }
        }
    }*/
    /*
    public void resume() {
        //reset the blackboard for the configurators
        for (IStyleConfigurator config : configurators) {
            config.setLayer(styleManager.getCurrentLayer());
            config.setStyleBlackboard(styleBlackboard);

            //only init if ui has been created
            if (config.getControl() != null && !config.getControl().isDisposed()) {
                config.refresh();
            }
        }
    }
    */
    public void rollback() {
        begin();
    }

    public void commit() {
        //tell all the configurators to save state to the blackboard
        //for (IStyleConfigurator config : styleManager.getStyleConfigurators()) {
            //only apply if ui has been created
            //if (config.getControl() != null) { config.apply();}
        //}
        ApplyStyleCommand applyCommand = new ApplyStyleCommand(
            layer, layer.getStyleBlackboard(), styleBlackboard
        );
        layer.getContextModel().getMap().sendCommandASync(applyCommand);
    }
}

//class ApplyStyleCommand implements UndoableCommand {
//
//    StyleBlackboard oldStyleBlackboard;
//    StyleBlackboard newStyleBlackboard;
//    Layer layer;
//
//    public ApplyStyleCommand(
//        Layer layer, StyleBlackboard oldStyleBlackboard, StyleBlackboard newStyleBlackboard
//    ) {
//        this.oldStyleBlackboard = oldStyleBlackboard;
//        this.newStyleBlackboard = newStyleBlackboard;
//        this.layer = layer;
//    }
//
//    /*
//     * @see net.refractions.udig.project.command.UndoableCommand#rollback()
//     */
//    public void rollback() throws Exception {
//        //overwrite with the orignal blackboard
//        layer.setStyleBlackboard(oldStyleBlackboard);
//    }
//
//    public void run() throws Exception {
//        //overwrite with new blackboard
//        layer.setStyleBlackboard(newStyleBlackboard);
//    }
//
//    /*
//     * @see net.refractions.udig.project.command.MapCommand#copy()
//     */
//    public MapCommand copy() {
//        return new ApplyStyleCommand(layer,oldStyleBlackboard,newStyleBlackboard);
//    }
//
//    /**
//     * @see net.refractions.udig.project.command.MapCommand#getName()
//     */
//    public String getName() {
//       return Messages.StyleTransaction_apply_style;
//    }
//
//}
