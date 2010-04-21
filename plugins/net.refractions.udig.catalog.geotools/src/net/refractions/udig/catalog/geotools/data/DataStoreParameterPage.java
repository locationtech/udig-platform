package net.refractions.udig.catalog.geotools.data;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.catalog.internal.ui.CatalogImport.CatalogImportWizard;
import net.refractions.udig.catalog.ui.AbstractUDIGImportPage;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;
import net.refractions.udig.catalog.ui.workflow.State;
import net.refractions.udig.catalog.ui.workflow.Workflow;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
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
            if (field.isDisposed()) {
                return;
            }

            Param param = (Param) field.getData();
            validate(param, field);
        }
        public void keyPressed( KeyEvent e ) {
        }
    };

    private Map<String, Serializable> connectionParameters;
    
    private IRunnableWithProgress connect = new IRunnableWithProgress(){
        public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                InterruptedException {
            boolean isComplete = false;
            
            DataAccessFactory factory = getPreviousPage().getFactory();
            connectionParameters = getParams();
            
            if( factory.canProcess( connectionParameters ) ){
                try {
                    factory.createDataStore( connectionParameters );
                    isComplete = true;
                } catch (IOException e) {
                    setErrorMessage( e.toString() );
                }
            }
            setPageComplete( isComplete );
        }
    };

    protected boolean validate( Param param, Text field ) {
        try {
            Object value = param.handle(field.getText());
            if (value == null && param.required) {
                field.setToolTipText("Required");
                return false;
            }
        } catch (IOException e) {
            field.setToolTipText(e.getLocalizedMessage());
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
        
        getParams();
        
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
        getControl().setLayout(new MigLayout("", "[right,pref!]para[grow]rel[pref!]", ""));

        for( Param param : getParameterInfo() ) {
            if (!"user".equals(param.getLevel())) {
                continue;
            }
            Text field = addField(getControl(), param);
            fields.put(param, field);
        }
        //Label sepearator = new Label(getControl(), SWT.SEPARATOR|SWT.HORIZONTAL);
        //sepearator.setLayoutData("growx,span,wrap");
        for( Param param : getParameterInfo() ) {
            
            if (!"advanced".equals(param.getLevel())) {
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
                if (field.isDisposed()) {
                    continue;
                }
                field.addKeyListener(listener);
            }
        } else {
            for( Text field : fields.values() ) {
                if (field.isDisposed()) {
                    continue;
                }
                field.removeKeyListener(listener);
            }
        }
    }

    public void dispose() {
        if (getControl() != null) {
            listen(false);
            fields.clear();
            fields = null;
        }
        paramFactory = null;
        if (paramInfo != null) {
            paramInfo.clear();
            paramInfo = null;
        }
        super.dispose();
    }

    protected Text addField( final Composite parent, Param param ) {
        Label label = new Label(parent, SWT.RIGHT);
        String name = param.title == null ? param.key : param.title.toString();
        String suffix = param.required ? "*:" : ":";
        label.setText(name + suffix);

        Text field;
        
        final String EXTENSION = (String) (param.metadata != null ? param.metadata.get(Param.EXT) : null);
        final Integer LENGTH = (Integer) (param.metadata != null ? param.metadata.get(Param.LENGTH) : null);
        final Object MIN = (param.metadata != null ? param.metadata.get(Param.MIN) : null);
        final Object MAX = (param.metadata != null ? param.metadata.get(Param.MAX) : null);
        
        if (param.isPassword()) {
            field = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
            field.setLayoutData("span, growx, wrap unrelated");            
        } else if (File.class.isAssignableFrom(param.type)) {
            field = new Text(parent, SWT.SINGLE | SWT.BORDER);
            field.setLayoutData("growx");            
            Button button = new Button( parent, SWT.DEFAULT );
            button.setText("Browse");
            button.setLayoutData("wrap unrelated");
            final Text target = field;
            button.addSelectionListener( new SelectionListener(){                
                public void widgetSelected( SelectionEvent e ) {
                    FileDialog browse = new FileDialog(parent.getShell(), SWT.OPEN );
                    if( EXTENSION != null ){
                        browse.setFilterExtensions( new String[]{ EXTENSION });
                    }
                    String path = browse.open();
                    if( path != null ){
                        target.setText( path );
                    }
                }                
                public void widgetDefaultSelected( SelectionEvent e ) {
                    widgetSelected(e);
                }
            });
        } else {
            field = new Text(parent, SWT.SINGLE | SWT.BORDER);
            field.setLayoutData("span, growx, wrap unrelated");            
        }
        field.setData(param);

        Object value = param.sample;
        if (getParams() != null && getParams().containsKey(param.key)) {
            value = getParams().get(param.key);
        }
        String text = value != null ? param.text(value) : "";
        if (value != null) {
            field.setText(text);
        }
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

    /**
     * Check if the parameters can connect and update setPageComplete if possible
     */
    protected void doPageComplete(){
        try {
            getWizard().getContainer().run(true, true, connect );
        } catch (InvocationTargetException e) {
            setErrorMessage(e.getCause().toString());
        } catch (InterruptedException e) {
            // canceled
        }
    }
    
}
