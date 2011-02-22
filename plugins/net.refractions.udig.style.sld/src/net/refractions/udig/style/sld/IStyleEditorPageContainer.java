package net.refractions.udig.style.sld;

import net.refractions.udig.style.internal.StyleLayer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayerDescriptor;

public interface IStyleEditorPageContainer extends IEditorPageContainer, IPageChangeProvider {

    public StyledLayerDescriptor getSLD();

    public Style getStyle();

    public void setStyle(Style style);

    public StyleLayer getSelectedLayer();

    public void setExitButtonState();

    /**
     * Action for applying the current changes.  Can be used to enable or disable the
     * button.  Also to execute the action.
     *
     * @return apply action
     */
    public IAction getApplyAction();


}
