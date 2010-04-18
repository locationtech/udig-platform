package net.refractions.udig.catalog.geotools.data;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.geotools.data.DataAccessFactory;
import org.geotools.data.DataAccessFactory.Param;

public class DataStoreParameterPage extends AbstractUDIGImportPage implements UDIGConnectionPage {

    private DataAccessFactory paramFactory;
    private List<Param> paramInfo = null; // lazy creation

    private Map<Param, Text> fields = new HashMap<Param, Text>();
    private KeyListener listener = new KeyListener(){
        public void keyReleased( KeyEvent e ) {
            Text field = (Text) e.getSource();
            if( field.isDisposed() ) {
                return;
            }
            
            Param param = (Param) field.getData();
            validate( param, field );
        }
        public void keyPressed( KeyEvent e ) {
        }
    };

    private Map<String, Serializable> connectionParameters;

    protected boolean validate( Param param, Text field ) {
        try {
            Object value = param.handle(field.getText());
            if( value == null && param.required ){
                field.setToolTipText("Required");
                return false;
            }
        } catch (IOException e) {
            field.setToolTipText( e.getLocalizedMessage() );
            return false;
        }
        return true;
    };

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
        if (workflowState != null) {
            // gak!
            String name = workflowState.getName();
            Workflow workflow = workflowState.getWorkflow();
            context = workflow.getContext();
        }
        Map<String, Serializable> params = getParams();
        CatalogImportWizard importWizard = getWizard();

        DataAccessFactory factory = getPreviousPage().getFactory();
        connectionParameters = getPreviousPage().getParams();
        if (connectionParameters == null) {
            connectionParameters = new HashMap<String, Serializable>();
            for( Param param : getParameterInfo() ) {
                if (param.required) {
                    connectionParameters.put(param.key, (Serializable) param.sample);
                }
            }
        }
        setTitle(factory.getDisplayName());
        setDescription(factory.getDescription());

        // do the layout thing
        //
        setControl(new Composite(parent, SWT.NONE));
        getControl().setLayout(new MigLayout("", "[right,pref!]para[grow]", ""));

        for( Param param : getParameterInfo() ) {
            String check = param.metadata == null ? "user" : (String) param.metadata
                    .get(Param.LEVEL);
            if (check == null) {
                check = "user";
            }
            if (!"user".equals(check)) {
                continue;
            }
            Text field = addField(getControl(), param);
            fields.put(param, field);
        }
        Label sepearator = new Label(getControl(), SWT.SEPARATOR);
        sepearator.setLayoutData("span,wrap");
        for( Param param : getParameterInfo() ) {
            String check = param.metadata == null ? "user" : (String) param.metadata
                    .get(Param.LEVEL);
            if (check == null) {
                check = "user";
            }
            if (!"advanced".equals(check)) {
                continue;
            }
            Text field = addField(getControl(), param);
            fields.put(param, field);
        }
        listen(true);
    }
    private void listen( boolean listen ) {
        if (listen) {
            for( Text field : fields.values() ) {
                if( field.isDisposed()){
                    continue;
                }
                field.addKeyListener(listener);
            }
        } else {
            for( Text field : fields.values() ) {
                if( field.isDisposed()){
                    continue;
                }
                field.removeKeyListener(listener);
            }
        }
    }
    
    public void dispose() {
        if( getControl() != null ){
            listen(false);
            fields.clear();
            fields = null;
        }
        paramFactory = null;
        if( paramInfo != null){
            paramInfo.clear();
            paramInfo = null;
        }
        super.dispose();
    }
    
    protected Text addField( Composite parent, Param param ) {
        Label label = new Label(parent, SWT.RIGHT);
        String name = param.title == null ? param.key : param.title.toString();
        String suffix = param.required ? "*:" : ":";
        label.setText(name + suffix);

        Text field;
        if (param.isPassword()) {
            field = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
        } else {
            field = new Text(parent, SWT.SINGLE | SWT.BORDER);
        }
        field.setData( param );
        
        Object value = param.sample;
        if (getParams() != null && getParams().containsKey(param.key)) {
            value = getParams().get(param.key);
        }
        String text = value != null ? param.text(value) : "";
        if (value != null) {
            field.setText(text);
        }
        field.setLayoutData("span, growx, wrap");
        return field;
    }

    protected synchronized List<Param> getParameterInfo() {
        if (paramFactory == getPreviousPage().getFactory()) {
            return paramInfo;
        }
        paramFactory = getPreviousPage().getFactory();
        Param[] info = getPreviousPage().getFactory().getParametersInfo();
        paramInfo = Arrays.asList(info);

        return paramInfo;
    }

    @Override
    public Map<String, Serializable> getParams() {
        return connectionParameters;
    }

}
