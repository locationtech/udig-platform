package net.refractions.udig.tutorials.featureeditor;

import net.refractions.udig.project.ui.IUDIGView;
import net.refractions.udig.project.ui.tool.IToolContext;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.geotools.feature.Feature;

public class CountryView extends ViewPart implements IUDIGView {

    CountryPanel panel=new CountryPanel();

    public void createPartControl( Composite parent ) {
        panel.createControl(parent);
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        // TODO Auto-generated method stub
        super.init(site);
    }
    public void setFocus() {
        panel.setFocus();
    }

    private IToolContext context;
    public void setContext( IToolContext newContext ) {
        context=newContext;
    }

    public IToolContext getContext() {
        return context;
    }

    public void editFeatureChanged( Feature feature ) {
        panel.setEditFeature(feature, context);
    }

}
