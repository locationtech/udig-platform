package net.refractions.udig.catalog.ui.operation;

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.internal.ui.Images;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.catalog.util.GeoToolsAdapters;
import net.refractions.udig.core.IProvider;
import net.refractions.udig.core.StaticProvider;
import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.ProgressListener;

import com.vividsolutions.jts.geom.Geometry;
/**
 * This class pops up a dialog and asks the user to 
 * fiddle with the expressions used to create a
 * new temporary layer.
 * <p>
 * While the Dialog may be simple; this is a reasonable
 * example at how to hack at an operation that processes
 * data in a way that does not force all the data into
 * memory.
 * <p>
 * However the createTemporaryResource method may use a
 * MemoryDataStore depending on how your environment is
 * configured.
 * <p>
 * I would like to see this class changed to be a wizard,
 * with an option to export directly to shapefile
 * (rather than a temporary resource ).
 * <p>
 * @author Jody Garnett
 */
public class ReshapeOperation implements IOp {

    
    
    @SuppressWarnings("unchecked")
    public void op(final Display display, Object target, final IProgressMonitor monitor)
            throws Exception {
        final IGeoResource handle = (IGeoResource) target;
        final FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = handle.resolve(
                FeatureSource.class, null);
        SimpleFeature feature;
        final FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource
                .getFeatures();
        FeatureIterator<SimpleFeature> iterator = collection.features();
        try {
            if (!iterator.hasNext()) {
                return; // no contents ... ignore
            }
            feature = iterator.next();
        } finally {
            iterator.close();
        }
        final SimpleFeature sample = feature;
        PlatformGIS.asyncInDisplayThread( new Runnable(){
            public void run() {
                final ReshapeDialog dialog = new ReshapeDialog( display.getActiveShell(), sample );
                int result = dialog.open();
                if( result == Window.CANCEL ){
                    return;
                }
                if( result == Window.OK){
                    try {
                        PlatformGIS.runBlockingOperation( new IRunnableWithProgress(){
                            public void run(IProgressMonitor monitor) {
                                try {
                                    final IGeoResource reshaped = process( featureSource, dialog, monitor );
                                    PlatformGIS.asyncInDisplayThread(new Runnable(){

                                        public void run() {
                                            dialog.executePostAction(handle, reshaped);
                                        }
                                        
                                    }, true);
                                } catch (IOException e) {
                                    throw new RuntimeException( e.getMessage(), e );
                                }
                            }
                        }, monitor );
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }               
            }
        }, true );
    }
    /** Called to do the actual processing once we have everything set up 
     * @return */
    @SuppressWarnings("unchecked")
    public IGeoResource process(FeatureSource<SimpleFeatureType, SimpleFeature> source, ReshapeDialog dialog, IProgressMonitor monitor ) throws IOException {
        if( monitor == null ) monitor = new NullProgressMonitor();
        
        monitor.beginTask(Messages.ReshapeOperation_task, 100 );
        
        final SimpleFeatureType featureType = dialog.getFeatureType();
        final List<Expression> expressionList = dialog.getExpressionList();
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures();
        
        IGeoResource scratch = CatalogPlugin.getDefault().getLocalCatalog().createTemporaryResource( featureType );
        final FeatureStore<SimpleFeatureType, SimpleFeature> store = scratch.resolve(FeatureStore.class, SubMonitor.convert(monitor,Messages.ReshapeOperation_createTempSpaceTask, 10));
        
        DefaultTransaction transaction = new DefaultTransaction("Process to "+featureType.getTypeName() ); //$NON-NLS-1$
        store.setTransaction( transaction );

        final SimpleFeatureBuilder build = new SimpleFeatureBuilder(featureType);
        
        ProgressListener progessListener = GeoToolsAdapters.progress( SubMonitor.convert(monitor,"Process "+featureType.getTypeName(), 90));         //$NON-NLS-1$
        try {
            collection.accepts(new FeatureVisitor(){
                boolean warning = true;
                public void visit( Feature rawFeature ) {
                    SimpleFeature feature = (SimpleFeature) rawFeature;
                    for( int i = 0; i < expressionList.size(); i++ ) {
                        build.add(expressionList.get(i).evaluate(feature));
                    }
                    SimpleFeature created = build.buildFeature(feature.getID());
                    try {
                        store.addFeatures(DataUtilities.collection(created));
                    } catch (Throwable t) {
                        if (warning) {
                            t.printStackTrace();
                            warning = false;
                        }
                    }
                }
            }, progessListener );           
            transaction.commit();
            
        }
        catch (RuntimeException huh){
            transaction.rollback();
            huh.printStackTrace();
        }
        finally {
            monitor.done();
        }
        // we need to show our new scratch feature in the catalog view?
        return scratch;
    }
    
    static int count = 0;
    public String getNewTypeName( String typeName ){
        return typeName + (count++);
    }

    class ReshapeDialog extends Dialog {
        
        private final class Null_Action implements PostReshapeAction {
            public void execute( IGeoResource original, IGeoResource reshaped ) {
            }
        }

        private static final String ACTION_COMBO_SETTINGS = "RESHAPE_ACTION_COMBO_SETTINGS"; //$NON-NLS-1$

        private SimpleFeatureType originalFeatureType;
        private SimpleFeature sample;
        private Text text;
        private SimpleFeatureType featureType;
        private List<String> nameList;
        private List<Expression> exprList;
        private Combo actionCombo;
        private IProvider<PostReshapeAction> postActionProvider;

        private ControlDecoration errors;

        private GridLayout mainLayout;

        private Composite mainComp;
        
        public ReshapeDialog(Shell parent, SimpleFeature sample ) {
            super( parent );
            this.originalFeatureType = sample.getFeatureType();
            this.sample = sample;
            setShellStyle(SWT.RESIZE|SWT.DIALOG_TRIM|SWT.CLOSE);
        }
        
        public void executePostAction( IGeoResource original, IGeoResource reshaped ) {
            postActionProvider.get().execute(original, reshaped);
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            getShell().setText(Messages.ReshapeOperation_DialogText);          
            mainComp = (Composite) super.createDialogArea(parent);
            
            mainLayout = new GridLayout();
            mainLayout.numColumns=1;
            mainLayout.makeColumnsEqualWidth=false;
            mainLayout.marginWidth=0;
            mainLayout.marginHeight=0;
            mainLayout.marginLeft = 0;
            mainLayout.marginTop = 5;
            
            mainComp.setLayout(mainLayout);
            GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            mainComp.setLayoutData(gridData);

            text = new Text(mainComp, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL );         
            text.setEditable(true);
            text.setText(getDefaultText());
            
            gridData = new GridData(SWT.FILL, SWT.FILL, true, true,1,1);
            text.setLayoutData(gridData);
            
            errors = new ControlDecoration(text, SWT.LEFT|SWT.TOP);
            ImageDescriptor desc = Images.getDescriptor(ISharedImages.ERROR_OVR);
            ImageRegistry imageRegistry = CatalogUIPlugin.getDefault().getImageRegistry();
            Image image = imageRegistry.get(ISharedImages.ERROR_OVR);
            if( image==null ){
                imageRegistry.put(ISharedImages.ERROR_OVR, desc.createImage());
            }
            errors.setImage(image);
            errors.setMarginWidth(2);
            errors.setShowHover(true);
            errors.hide();
            errors.addSelectionListener(new SelectionListener(){

                public void widgetDefaultSelected( SelectionEvent e ) {
                    errors.showHoverText(errors.getDescriptionText());
                }

                public void widgetSelected( SelectionEvent e ) {
                    widgetDefaultSelected(e);
                }
                
            });
            
            text.addListener(SWT.Modify, new Listener(){

                public void handleEvent( Event event ) {
                    errors.hide();
                    errors.hideHover();
                    mainLayout.marginLeft = 0;
                    mainComp.layout(true);
                }
                
            });
            
            actionCombo = new Combo(mainComp, SWT.READ_ONLY);
            actionCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            
            fillActionCombo(actionCombo);
            return mainComp;
        }
        private void fillActionCombo( Combo actionCombo ) {
            actionCombo.add(Messages.ReshapeOperation_noAction);
            actionCombo.setData(Messages.ReshapeOperation_noAction, new StaticProvider<PostReshapeAction>(new Null_Action()));
            
            int i=1;
            String lastSelection = CatalogUIPlugin.getDefault().getDialogSettings().get(ACTION_COMBO_SETTINGS);
            int selected = 0;

            
            List<IConfigurationElement> extensions = ExtensionPointList.getExtensionPointList("net.refractions.udig.catalog.ui.reshapePostAction"); //$NON-NLS-1$
            for( final IConfigurationElement configurationElement : extensions ) {
                String name = configurationElement.getAttribute("name"); //$NON-NLS-1$
                IProvider<PostReshapeAction> provider = new IProvider<PostReshapeAction>(){

                    public PostReshapeAction get( Object... params ) {
                        try {
                            return (PostReshapeAction) configurationElement.createExecutableExtension("class"); //$NON-NLS-1$
                        } catch (CoreException e) {
                            throw (RuntimeException) new RuntimeException( ).initCause( e );
                        }
                    }
                    
                };
                actionCombo.add(name);
                actionCombo.setData(name, provider );
                String id = configurationElement.getNamespaceIdentifier()+"."+configurationElement.getAttribute("id");  //$NON-NLS-1$//$NON-NLS-2$
                actionCombo.setData(name+"id", id  ); //$NON-NLS-1$

                if( id.equals(lastSelection) ){
                    selected =i;
                }
                i++;
            }
            actionCombo.select(selected);
        }

        protected Point getInitialSize() {
            return new Point(500, 500);
        }
        String getDefaultText(){
            StringBuffer buffer = new StringBuffer();
            for( AttributeDescriptor descriptor : originalFeatureType.getAttributeDescriptors() ){
                buffer.append( descriptor.getName() );
                buffer.append( "=" ); //$NON-NLS-1$
                buffer.append( descriptor.getName() );
                buffer.append( "\n" ); //$NON-NLS-1$
            }
            return buffer.toString();
        }
        @SuppressWarnings("unchecked")
        @Override
        protected void okPressed() {
            boolean ok = false;
            try {
                nameList = createNameList();
                exprList = createExpressionList();
                featureType = createFeatureType();
                ok = featureType != null;
                String selected = actionCombo.getItem(actionCombo.getSelectionIndex());
                CatalogUIPlugin.getDefault().getDialogSettings().put(ACTION_COMBO_SETTINGS, (String)actionCombo.getData(selected+"id")); //$NON-NLS-1$
                postActionProvider = (IProvider<PostReshapeAction>) actionCombo.getData(selected);

            }
            catch( Throwable t ){
                showError(t);
                
            }
            if( ok ){
                super.okPressed();
            }
        }

        /**
         * Show an error in the UI
         * @param t
         */
        private void showError( Throwable t ) {
            if(! (t instanceof ReshapeException) ){
                CatalogUIPlugin.log("error with reshape", t); //$NON-NLS-1$
            }
            String errormessage = t.getLocalizedMessage();
            if( errormessage==null ){
                errormessage = Messages.ReshapeOperation_2;
            }
            String message = MessageFormat.format(Messages.ReshapeOperation_3,errormessage);
            errors.setDescriptionText(message);
            errors.showHoverText(message);
            errors.show();                 
            mainLayout.marginLeft = 10;
            mainComp.layout(true);
        }
        public Properties getReshapeDefinition(){
            Properties properties = new Properties();
            try {
                properties.load( new ByteArrayInputStream(text.getText().getBytes()) );
            } catch (IOException e) {
                showError(e);
                return null;
            }           
            return properties;
        }
        public SimpleFeatureType getFeatureType() {
            return featureType;
        }
        /**
         * You cannot call this once the dialog is closed, see the okPressed method.
         * @return a SimpleFeatureType created based on the contents of Text
         */
        private SimpleFeatureType createFeatureType() throws SchemaException {

            SimpleFeatureTypeBuilder build = new SimpleFeatureTypeBuilder();
            
             
            List<String> names = createNameList();
            List<Expression> expressions = getExpressionList();
            
            for( int i=0; i<names.size(); i++){
                String name = names.get( i );
                
                Expression expression = expressions.get( i );

                Object value = expression.evaluate(sample);
                
                //hack because sometimes expression returns null.  I think the real bug is with AttributeExpression
                Class<?> binding = null;
                if( value == null){
                    if(  expression instanceof PropertyName){
                        String path = ((PropertyName)expression).getPropertyName();
                        AttributeType attributeType = sample.getFeatureType().getType(path);
                        if( attributeType == null ){
                            String msg = Messages.ReshapeOperation_4;
                            throw new ReshapeException(format(msg, name, path));
                        }
                        binding = attributeType.getClass();
                    }
                } else {
                    binding = value.getClass();
                }
                
                if( binding ==null ){
                    String msg = Messages.ReshapeOperation_5;
                    throw new ReshapeException(format(msg, name));
                }
                
                if( Geometry.class.isAssignableFrom( binding )){
                    CoordinateReferenceSystem crs;
                    AttributeType originalAttributeType = originalFeatureType.getType(name);
                    if( originalAttributeType == null && originalAttributeType instanceof GeometryType ) {
                        crs = ((GeometryType)originalAttributeType).getCoordinateReferenceSystem();
                    } else {
                        crs = originalFeatureType.getCoordinateReferenceSystem();
                    }
                    build.crs(crs);
                    
                    build.add(name, binding);
                }
                else {
                    build.add(name, binding);
                }
            }
            build.setName( getNewTypeName( originalFeatureType.getTypeName() ) );
            
            return build.buildFeatureType();
        }
        public List<String> getNameList() {
            return nameList;
        }
        public List<Expression> getExpressionList() {
            return exprList;
        }

        /**
         * You cannot call this once the dialog is closed, see the okPressed method.
         * 
         * @return a SimpleFeatureType created based on the contents of Text
         */
        public List<String> createNameList() {
            List<String> list = new ArrayList<String>();

            String definition = text.getText().replaceAll("\r","\n").replaceAll("[\n\r][\n\r]", "\n");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            for( String line : definition.split("\n") ) { //$NON-NLS-1$
                int mark = line.indexOf("="); //$NON-NLS-1$
                if (mark != -1) {
                    String name = line.substring(0, mark).trim();
                    if( list.contains(name)){
                            String msg = Messages.ReshapeOperation_6;
                            throw new ReshapeException(format(msg, name));
                    }
                    list.add(name);
                }
            }
            return list;
        }

        public List<Expression> createExpressionList() {
            List<Expression> list = new ArrayList<Expression>();

            String definition = text.getText().replaceAll("\r","\n").replaceAll("[\n\r][\n\r]", "\n");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            for( String line : definition.split("\n") ) { //$NON-NLS-1$
                int mark = line.indexOf("="); //$NON-NLS-1$
                if (mark != -1) {
                    String expressionDefinition = line.substring(mark + 1).trim();

                    Expression expression;
                    try {
                        expression = CQL.toExpression(expressionDefinition);
                    } catch (CQLException e) {
                        throw new ReshapeException(e.toString());
                    }
                    list.add(expression);
                }
            }
            return list;
        }

    }
    
    class ReshapeException extends RuntimeException{
        /** long serialVersionUID field */
        private static final long serialVersionUID = -8097645450776812983L;

        public ReshapeException(String message) {
            super(message);
        }
    }
}
