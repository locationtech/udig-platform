/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.geotools.data;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
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
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.geotools.data.DataAccessFactory;
import org.geotools.data.DataAccessFactory.Param;
import org.geotools.data.DataUtilities;

public class DataStoreParameterPage extends AbstractUDIGImportPage implements UDIGConnectionPage {

    private final class TestConnection implements IRunnableWithProgress {
        private boolean isConnected;
        public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                InterruptedException {
            isConnected = false;
            DataAccessFactory factory = getPreviousPage().getFactory();
            connectionParameters = getParams();

            if (factory.canProcess(connectionParameters)) {
                try {
                    factory.createDataStore(connectionParameters);
                    isConnected = true;
                } catch (IOException e) {
                    setErrorMessage(e.toString());
                }
            }
        }
        public boolean isConnected() {
            return isConnected;
        }
    }

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
            sync(param, field);

            getContainer().updateButtons();
            // setPageComplete(isParametersComplete(false));
        }
        public void keyPressed( KeyEvent e ) {
        }
    };

    private Map<String, Serializable> connectionParameters;

    private TestConnection connect = new TestConnection();

    /**
     * Validates the indicated field; using Param to both parse and test.
     * <p>
     * The connectionParameters are updated as a result of this validation; you can call
     * doPageComplete to verify if the connection parameters result in something that can actually
     * connect.
     */
    protected void sync( final Param param, final Text field ) {
        Display display = getControl().getDisplay();
        PlatformGIS.asyncInDisplayThread(display, new Runnable(){
            public void run() {
                if( field.isDisposed()){
                    return;
                }
                String text = field.getText();
                if (text.length() == 0) {
                    // use default value if available
                    // connection will end up using default value
                    connectionParameters.remove(param.key);
                    if (param.sample != null) {
                        field.setToolTipText("Default: " + param.text(param.sample));
                        setErrorMessage(null); // using default or empty
                    } else {
                        field.setToolTipText("Empty");
                        if( param.required && param.sample == null ){
                            setErrorMessage(param.key +" is required");
                        }
                        else {
                            setErrorMessage(null); // using default or empty
                        }
                    }
                } else {
                    Object value;
                    try {
                        value = param.parse(text);
                        setErrorMessage(null); // all good
                    } catch (Throwable e) {
                        setErrorMessage( e.getLocalizedMessage() );
                        value = null;
                    }               
                    
                    if (value == null && param.required) {
                        field.setToolTipText("Required");
                        connectionParameters.remove(param.key);
                    } else {
                        field.setToolTipText("Value: "+value);                    
                        connectionParameters.put(param.key, (Serializable) value);
                    }
                }
            }
        }, false);
    }

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
        
        Label seperator = new Label(getControl(), SWT.HORIZONTAL | SWT.SEPARATOR );
        seperator.setLayoutData("growx,span,wrap");
        Label advanced = new Label(getControl(), SWT.LEFT);
        advanced.setLayoutData("growx,span,wrap");
        
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

    protected Text addField( final Composite parent, final Param param ) {
        Label label = new Label(parent, SWT.RIGHT);
        String name = param.title == null ? param.key : param.title.toString();
        String suffix = param.required ? "*:" : ":";
        label.setText(name + suffix);
        label.setToolTipText( param.description.toString() );

        Text field;

        final String EXTENSION = (String) (param.metadata != null
                ? param.metadata.get(Param.EXT)
                : null);
        
        if (param.isPassword()) {
            field = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
            field.setLayoutData("span, growx, wrap unrelated");
        } else if (File.class.isAssignableFrom(param.type) || URL.class.isAssignableFrom(param.type)) {
            field = new Text(parent, SWT.SINGLE | SWT.BORDER);
            field.setLayoutData("growx");
            Button button = new Button(parent, SWT.DEFAULT);
            button.setText("Browse");
            button.setLayoutData("wrap unrelated");
            final Text target = field;
            button.addSelectionListener(new SelectionListener(){
                public void widgetSelected( SelectionEvent e ) {
                	getPathAndSynchWithText(EXTENSION, parent, target, param.type);
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
        
        if( "dbtype".equals( param.key)){
            // cannot modify dbtype
            field.setEditable(false);
        }

        Object value = null;
        if (getParams() != null && getParams().containsKey(param.key)) {
            value = getParams().get(param.key);
        }
        if( value == null && param.required && param.sample != null ){
            value = param.sample;
        }
        String text = value != null ? param.text(value) : "";
        if (value != null) {
            field.setText(text);
        }
        return field;
    }

	@SuppressWarnings("rawtypes")
	protected void getPathAndSynchWithText(String extension, Composite parent, Text target,
			Class targetClass) {
		String path = null;
		if (extension != null) {
			FileDialog browse = new FileDialog(parent.getShell(), SWT.OPEN);
			browse.setFilterExtensions(new String[] { wrapExtension(extension) });
			path = browse.open();
		} else {
			DirectoryDialog browse = new DirectoryDialog(parent.getShell(),
					SWT.OPEN);
			path = browse.open();
		}

		if (path != null) {
			String text = null;
			if (File.class.isAssignableFrom(targetClass)) {
				text = path;
			} else if (URL.class.isAssignableFrom(targetClass)) {
				File file = new File(path);
				URL url = DataUtilities.fileToURL(file);
				text = url.toString();
			}

			if (text != null) {
				target.setText(text);
			}

			sync((Param) target.getData(), target);
		}
	}

	private String wrapExtension(String extension) {
    	if (extension != null) {
	    	int index = extension.lastIndexOf('.');
	        return "*." + (index >= 0 ? extension.substring(index) : extension);
    	}
    	return null;
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

    // @Override
    // public boolean leavingPage() {
    // return super.leavingPage(); //isParametersComplete(true);
    // }

    @Override
    public boolean canFlipToNextPage() {
        boolean flip = super.canFlipToNextPage();
        if (flip) {
            // validate user input (usually checking state of ui)
            if (isParametersComplete(false)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the parameters can connect and update setPageComplete if possible
     */
    protected boolean isParametersComplete( boolean testConnection ) {
        DataAccessFactory factory = getPreviousPage().getFactory();
        connectionParameters = getParams();

        if (!factory.canProcess(connectionParameters)) {
            return false;
        } else if (testConnection) {
            // dispatch job that will call SetPageComplete as needed
            try {
                // check that the conneciton parameters actually connect
                // job will call setPageComplete itself
                getWizard().getContainer().run(false, true, connect);
            } catch (InvocationTargetException e) {
                setErrorMessage(e.getCause().toString());
            } catch (InterruptedException e) {
                // canceled
            }
            return connect.isConnected();
        } else {
            return true;
        }
    }

}
