package net.refractions.udig.catalog.geotools.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.catalog.internal.ui.CatalogImport.CatalogImportWizard;
import net.refractions.udig.catalog.ui.AbstractUDIGImportPage;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;
import net.refractions.udig.catalog.ui.workflow.IntermediateState;
import net.refractions.udig.catalog.ui.workflow.State;
import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.geotools.data.DataAccessFactory;
import org.geotools.data.DataAccessFactory.Param;

public class DataStoreParameterPage extends AbstractUDIGImportPage implements UDIGConnectionPage {

    private DataAccessFactory paramFactory;
    private List<Param> paramInfo;
    
    public DataStoreParameterPage() {
        super("Parameters");
    }

    @Override
    public Composite getControl() {
        return (Composite) super.getControl();
    }
    
    @Override
    public CatalogImportWizard getWizard() {
        return (CatalogImportWizard) super.getWizard();
    }
    
    @Override
    public DataStoreConnectionPage getPreviousPage() {
        return (DataStoreConnectionPage) super.getPreviousPage();
    }
    public void createControl( Composite parent ) {
        // retrieve any and all context!
        State workflowState = getState();
        Object context = null;
        if( workflowState != null ){
            // gak!
            String name = workflowState.getName();
            Workflow workflow = workflowState.getWorkflow();
            context = workflow.getContext();
        }
        Map<String, Serializable> params = getParams();
        CatalogImportWizard importWizard = getWizard();

        DataAccessFactory factory = getPreviousPage().getFactory();
        setTitle( factory.getDisplayName() );
        setDescription( factory.getDescription() );
        
        // do the layout thing
        //
        setControl( new Composite( parent, SWT.NONE ));
        getControl().setLayout( new MigLayout("debug","[right,pref!]para[grow]",""));
        Label label = new Label( getControl(), SWT.RIGHT);
        label.setText("Data Source:");
        
        Text field = new Text( getControl(), SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
        field.setText( factory.getDisplayName() );
        field.setLayoutData("span, growx, wrap");
        
        for( Param param : getParameterInfo() ){
            label = new Label( getControl(), SWT.RIGHT);
            String name = param.title == null ? param.key : param.title.toString();
            label.setText(name );
            
            field = new Text( getControl(), SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
            Object value = param.sample;
            
            field.setText( param.text( value ) );
            field.setLayoutData("span, growx, wrap");
        }
    }
    
    protected synchronized List<Param> getParameterInfo(){
        if( paramFactory == getPreviousPage().getFactory()){
            return paramInfo;
        }
        paramFactory = getPreviousPage().getFactory();
        Param[] info = getPreviousPage().getFactory().getParametersInfo();
        paramInfo = Arrays.asList( info );
        
        return paramInfo;
    }
    
    @Override
    public Map<String, Serializable> getParams() {
        return super.getParams();
    }
    
}
