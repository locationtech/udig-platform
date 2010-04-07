package net.refractions.udig.style.sld.editor;

import net.refractions.udig.filter.ComboExpressionViewer;
import net.refractions.udig.filter.ExpressionViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class FillEditorComponent {
    private ComboExpressionViewer fillTypeViewer;
    private ComboExpressionViewer colourViewer;
    private ComboExpressionViewer opacityViewer;
    
    public void createControl(Composite parent) {
        parent.setLayout(new GridLayout(2, false));
        
        Label label = new Label(parent, SWT.NONE);
        label.setText("Fill Type");
        label.setToolTipText("Select the type of fill");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
        fillTypeViewer = new ComboExpressionViewer(parent, SWT.SINGLE);
        fillTypeViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        fillTypeViewer.getControl().setToolTipText("Select the type of fill");
        
        label = new Label(parent, SWT.NONE);
        label.setText("Colour");
        label.setToolTipText("Select fill colour");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
        colourViewer = new ComboExpressionViewer(parent, SWT.SINGLE);
        colourViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        label = new Label(parent, SWT.NONE);
        label.setText("Opacity");
        label.setToolTipText("Select the opacity");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
        opacityViewer = new ComboExpressionViewer(parent, SWT.SINGLE);
        opacityViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            
        
        
    }

}
