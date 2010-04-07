package net.refractions.udig.tutorials.genericprojectelement;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * This tells the framework how to display the object in JFace viewers
 * 
 * @author jesse
 * @since 1.1.0
 */
public class MyProjectElementLabelProvider extends LabelProvider implements IColorProvider {

    @Override
    public String getText( Object element ) {
        return ((MyProjectElement)element).getLabel();
    }

    public Color getBackground( Object element ) {
        if( ((MyProjectElement)element).getLabel()==null){
            return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        }
        return null;
    }

    public Color getForeground( Object element ) {
        return null;
    }

}
