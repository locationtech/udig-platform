package net.refractions.udig.tutorials.featureeditor;

import net.refractions.udig.project.ui.IUDIGView;
import net.refractions.udig.project.ui.tool.IToolContext;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.opengis.feature.simple.SimpleFeature;

public class CountryView extends ViewPart implements IUDIGView {

    // CountryPanel panel = new CountryPanel();
    // CountryPanelForm panel = new CountryPanelForm();
    // CountryPanelFormCellEditor panel = new CountryPanelFormCellEditor();
    private CountryPanelFormEditFeature panel;;
    private FormToolkit toolkit;
    private IToolContext context;

    public void createPartControl( Composite parent ) {
        this.panel = new CountryPanelFormEditFeature();
        this.toolkit = new FormToolkit(parent.getDisplay());
        this.panel.createControl(parent, toolkit);
    }

    @Override
    public void init( IViewSite site ) throws PartInitException {
        super.init(site);
    }

    public void setFocus() {
        panel.setFocus();
    }

    public void setContext( IToolContext newContext ) {
        context = newContext;
    }

    public IToolContext getContext() {
        return context;
    }

    public void editFeatureChanged(SimpleFeature feature) {
        panel.setEditFeature(feature, context);
    }

}
