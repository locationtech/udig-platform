/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.operation;

import static java.text.MessageFormat.format;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import net.miginfocom.swt.MigLayout;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.core.IProvider;
import org.locationtech.udig.core.StaticProvider;
import org.locationtech.udig.core.internal.ExtensionPointList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.process.vector.TransformProcess;
import org.geotools.process.vector.TransformProcess.Definition;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Geometry;

/**
 * Dialog used to ask the user to enter in a series of expression for use with the Transform process.
 * 
 * @author Jody Garnett
 * @since 1.0.0
 */
public class ReshapeDialog extends Dialog {
    
    private final class Null_Action implements PostReshapeAction {
        public void execute( IGeoResource original, IGeoResource reshaped ) {
        }
    }

    private static final String ACTION_COMBO_SETTINGS = "RESHAPE_ACTION_COMBO_SETTINGS"; //$NON-NLS-1$

    private SimpleFeatureType originalFeatureType;
    private SimpleFeature sample;
    private Text text;
    private SimpleFeatureType featureType;

    private List<TransformProcess.Definition> transform;
    
    private Combo actionCombo;
    private IProvider<PostReshapeAction> postActionProvider;

    private ControlDecoration feedbackDecorator;

    private Composite panel;

    private Listener listener =  new Listener(){
        public void handleEvent( Event event ) {
            try {
                feedbackDecorator.hide();
                feedbackDecorator.hideHover();
                List<Definition> list = createTransformProcessDefinitionList();
                
                int count = 0;
                boolean changed = false;
                for( Definition definition: list ){
                    count++;
                    if( definition.expression instanceof PropertyName){
                        PropertyName propertyName = (PropertyName) definition.expression;
                        if( !definition.name.equals( propertyName.getPropertyName())){
                            changed = true;
                            break;
                        }
                    }
                    else {
                        changed = true; // some other kind of expression (so that is good then)
                        break;
                    }
                }
                if( !changed && originalFeatureType.getAttributeCount() == count ){
                    showFeedback("Transform does not modify any content",null);
                }
            }
            catch (Throwable t){
                showFeedback( null, t );
            }
        }
    };
    
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
        panel = (Composite) super.createDialogArea(parent);
        
        // parent uses Grid Data hense the fun here
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        panel.setLayoutData(gridData);

        panel.setLayout(new MigLayout( "fillx","[grow,fill]","[][grow,fill][][]") );
        
        Label label = new Label(panel, SWT.LEFT );
        label.setText("Transform");
        label.setLayoutData("width pref!,left,wrap");
        
        feedbackDecorator = new ControlDecoration(label, SWT.RIGHT|SWT.TOP);

        text = new Text(panel, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL );
        text.setEditable(true);
        text.setText(getDefaultText());
        
        text.setLayoutData("wrap, height 100:pref:100%");
        
        text.addListener(SWT.Modify, listener);

        label = new Label(panel, SWT.LEFT );
        label.setText("How would you like to handle the result:");
        label.setLayoutData("width pref!,wrap");

        actionCombo = new Combo(panel, SWT.READ_ONLY);
        actionCombo.setLayoutData( "width pref!");
        actionCombo(actionCombo);

        
        return panel;
    }
    
    private void actionCombo( Combo actionCombo ) {
        actionCombo.add(Messages.ReshapeOperation_noAction);
        actionCombo.setData(Messages.ReshapeOperation_noAction, new StaticProvider<PostReshapeAction>(new Null_Action()));
        
        int i=1;
        String lastSelection = CatalogUIPlugin.getDefault().getDialogSettings().get(ACTION_COMBO_SETTINGS);
        int selected = 0;

        
        List<IConfigurationElement> extensions = ExtensionPointList.getExtensionPointList("org.locationtech.udig.catalog.ui.reshapePostAction"); //$NON-NLS-1$
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
            transform = createTransformProcessDefinitionList();
            featureType = createFeatureType();
            ok = featureType != null;
            String selected = actionCombo.getItem(actionCombo.getSelectionIndex());
            CatalogUIPlugin.getDefault().getDialogSettings().put(ACTION_COMBO_SETTINGS, (String)actionCombo.getData(selected+"id")); //$NON-NLS-1$
            postActionProvider = (IProvider<PostReshapeAction>) actionCombo.getData(selected);

        }
        catch( Throwable t ){
            showFeedback(null,t);
        }
        if( ok ){
            super.okPressed();
        }
    }

    /**
     * Show an error in the UI
     * @param t
     */
    private void showFeedback( String message, Throwable t ) {
        feedbackDecorator.hide();
        feedbackDecorator.hideHover();
        if( t == null && message != null){
            // warning feedback!
            FieldDecorationRegistry decorations = FieldDecorationRegistry.getDefault();
            FieldDecoration errorDecoration = decorations.getFieldDecoration(FieldDecorationRegistry.DEC_WARNING);
            feedbackDecorator.setImage(errorDecoration.getImage());
            feedbackDecorator.setDescriptionText( message );
            feedbackDecorator.showHoverText( message );
            feedbackDecorator.show();
        }
        else if( t != null ){
            // if(! (t instanceof ReshapeException) ){
            // CatalogUIPlugin.log("error with reshape", t); //$NON-NLS-1$
            // }
            String errormessage = t.getLocalizedMessage();
            if( errormessage == null ){
                errormessage = Messages.ReshapeOperation_2;
            }
            else {
                // fix up really long CQL messages
                errormessage = errormessage.replaceAll("\\n\\s+", " ");
            }
            if( message == null ){
                message = MessageFormat.format(Messages.ReshapeOperation_3,errormessage);
            }
            
            FieldDecorationRegistry decorations = FieldDecorationRegistry.getDefault();
            FieldDecoration errorDecoration = decorations.getFieldDecoration(FieldDecorationRegistry.DEC_WARNING);
            feedbackDecorator.setImage(errorDecoration.getImage());
            feedbackDecorator.setDescriptionText( message );
            feedbackDecorator.showHoverText( message );
            feedbackDecorator.show();
        }
    }
    
    /**
     * FeatureType for resulting output; only valid after {@link #okPressed()}
     * @return
     */
    public SimpleFeatureType getFeatureType() {
        return featureType;
    }
    /**
     * Transform process definition; only valid after {@link #okPressed()}.
     * @return
     */
    public List<TransformProcess.Definition> getTransform() {
        return transform;
    }
    
    /**
     * You cannot call this once the dialog is closed, see the okPressed method.
     * @return a SimpleFeatureType created based on the contents of Text
     */
    private SimpleFeatureType createFeatureType() throws SchemaException {

        SimpleFeatureTypeBuilder build = new SimpleFeatureTypeBuilder();
        
        transform = createTransformProcessDefinitionList();
        
        for( Definition definition : transform ){
            String name = definition.name;
            Expression expression = definition.expression;

            // FIXME : sometimes expression returns null.  I think the real bug is with AttributeExpression
            Class<?> binding = definition.binding;
            if( binding == null ){
                Object value = expression.evaluate(sample);
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
        build.setName( ReshapeOperation.getNewTypeName( originalFeatureType.getTypeName() ) );
        
        return build.buildFeatureType();
    }

    /**
     * You cannot call this once the dialog is closed, see the {@link #okPressed()} for details.
     * 
     * @return Transform definition
     */
    public List<TransformProcess.Definition> createTransformProcessDefinitionList() {
        List<TransformProcess.Definition> list = new ArrayList<TransformProcess.Definition>();

        String definition = text.getText().replaceAll("\r","\n").replaceAll("[\n\r][\n\r]", "\n");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        
        list = TransformProcess.toDefinition( definition );
        return list;
    }
}
