package net.refractions.udig.catalog.ui.operation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.core.IProvider;
import net.refractions.udig.core.StaticProvider;
import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.ui.filter.CQLExpressionViewer;
import net.refractions.udig.ui.filter.ExpressionInput;
import net.refractions.udig.ui.filter.ExpressionViewer;
import net.refractions.udig.ui.filter.IExpressionViewer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.process.feature.gs.TransformProcess;
import org.geotools.process.feature.gs.TransformProcess.Definition;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;

/**
 * Dialog used to ask the user to enter in a series of expression for use with the Transform
 * process.
 * 
 * @author Jody Garnett
 * @since 1.0.0
 */
public class TransformDialog extends Dialog {

    private static final String NO_CONTENT = "--";

    private final class Null_Action implements PostReshapeAction {
        public void execute(IGeoResource original, IGeoResource reshaped) {
        }
    }

    private static final String ACTION_COMBO_SETTINGS = "RESHAPE_ACTION_COMBO_SETTINGS"; //$NON-NLS-1$

    private SimpleFeatureType schema;

    private SimpleFeature sample;

    private SimpleFeatureType featureType;

    private List<TransformProcess.Definition> transform;

    private Combo actionCombo;

    private IProvider<PostReshapeAction> postActionProvider;

    private ControlDecoration feedbackDecorator;

    private Composite panel;

    /**
     * viewer used to review {@link #transform}
     */
    private TableViewer table;

    private ISelectionChangedListener tableListener = new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            Definition definition = selectedDefinition();

            listen(false);
            try {
                if (definition == null) {
                    name.setText("");
                    name.setEnabled(false);

                    expression.setExpression(Expression.NIL);
                    expression.getControl().setEnabled(false);
                } else {
                    name.setText(definition.name);
                    name.setEnabled(true);

                    expression.setExpression(definition.expression);
                    expression.getControl().setEnabled(true);
                }
            } finally {
                listen(true);
            }
        }
    };

    private Text name;

    private ModifyListener nameListener = new ModifyListener() {
        public void modifyText(ModifyEvent e) {
            Definition definition = selectedDefinition();
            String text = name.getText();
            if (definition.name == null || !definition.name.equals(text)) {
                definition.name = text;

                // refresh the display, including labels and display the row if needed
                table.refresh(definition, true, true);
            }
        }
    };

    private IExpressionViewer expression;

    private ISelectionChangedListener expressionListener = new ISelectionChangedListener() {
        public void selectionChanged(SelectionChangedEvent event) {
            Definition definition = selectedDefinition();
            Expression expr = expression.getExpression();

            if (definition.expression == null || !definition.equals(expr)) {
                definition.expression = expr;

                try {
                    Object value = definition.expression.evaluate(sample);
                    definition.binding = value.getClass();
                } catch (Throwable t) {
                    definition.binding = null; // unknown
                }
                // refresh the display, including labels and display the row if needed
                table.refresh(definition, true, true);
            }
        }
    };

    /**
     * Transform Dialog used assemble a {@link ProcessTransform.Definition} based on the provided
     * sample feature.
     * <p>
     * The initial transform is defined by
     * {@link #createDefaultTransformDefinition(SimpleFeatureType)} to be a simple attribute by
     * attribute copy of the source material. The sample fature is also used to determine expected
     * type when defining new expressions.
     * 
     * @param parent
     * @param sample
     */
    public TransformDialog(Shell parent, SimpleFeature sample) {
        super(parent);
        this.schema = sample.getFeatureType();
        this.sample = sample;
        this.transform = createDefaultTransformDefinition(schema);
        setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM | SWT.CLOSE);
    }

    protected void listen(boolean listen) {
        if (listen) {
            name.addModifyListener(nameListener);
            expression.addSelectionChangedListener(expressionListener);
        } else {
            name.removeModifyListener(nameListener);
            expression.removeSelectionChangedListener(expressionListener);
        }
    }

    protected Definition selectedDefinition() {
        ISelection selectedRow = table.getSelection();
        if (!selectedRow.isEmpty() && selectedRow instanceof StructuredSelection) {
            StructuredSelection selection = (StructuredSelection) selectedRow;
            return (Definition) selection.getFirstElement();
        }
        return null; // nothing to see here
    }

    public void executePostAction(IGeoResource original, IGeoResource reshaped) {
        postActionProvider.get().execute(original, reshaped);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        getShell().setText(Messages.ReshapeOperation_DialogText);
        panel = (Composite) super.createDialogArea(parent);

        // parent uses Grid Data hense the fun here
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        panel.setLayoutData(gridData);

        panel.setLayout(new MigLayout("", "[grow,fill][]",
                "[][][][][grow,fill][][][][grow,fill][][]"));

        Label label = new Label(panel, SWT.LEFT);
        label.setText("Transform");
        label.setLayoutData("cell 0 0 2 1,width pref!,left");

        Button button = new Button(panel, SWT.CENTER);
        button.setText("Add");
        button.setLayoutData("cell 1 1 1 1,grow");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int row = table.getTable().getSelectionIndex();
                Definition definition = new Definition();
                definition.name = "";
                definition.expression = Expression.NIL;
                transform.add(row, definition);
                table.refresh();
                table.setSelection(new StructuredSelection(definition));
            }
        });

        button = new Button(panel, SWT.CENTER);
        button.setText("Up");
        button.setLayoutData("cell 1 2 1 1,grow");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int row = table.getTable().getSelectionIndex();
                if (row == 0 || row == -1){
                    return;
                }
                row--;
                Definition definition = selectedDefinition();
                transform.remove(definition);
                transform.add(row, definition);
                table.refresh();
                table.setSelection(new StructuredSelection(definition));
            }
        });

        button = new Button(panel, SWT.CENTER);
        button.setText("Down");
        button.setLayoutData("cell 1 3 1 1,grow");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int row = table.getTable().getSelectionIndex();
                if (row == transform.size()-1 || row == -1){
                    return;
                }
                row++;
                Definition definition = selectedDefinition();
                transform.remove(definition);
                transform.add(row, definition);
                table.refresh();
                table.setSelection(new StructuredSelection(definition));
            }
        });

        button = new Button(panel, SWT.CENTER);
        button.setText("Remove");
        button.setLayoutData("cell 1 5 1 1,grow");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int row = table.getTable().getSelectionIndex();
                if( row == -1 ) {
                    return;
                }
                if (row > 0){
                    row=row-1;
                }
                Definition definition = selectedDefinition();
                transform.remove(definition);
                table.refresh();
                
                if( row< transform.size() ){
                    table.getTable().setSelection(row);
                }
                else {
                    table.getTable().deselectAll();
                }
            }
        });
        table = new TableViewer(panel, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        table.setContentProvider(ArrayContentProvider.getInstance());
        table.getControl().setLayoutData(
                "cell 0 1 1 5, grow, height 200:50%:50%,width 300:pref:100%");

        TableViewerColumn column = new TableViewerColumn(table, SWT.NONE);
        column.getColumn().setWidth(100);
        column.getColumn().setMoveable(false);
        column.getColumn().setResizable(true);
        column.getColumn().setText("Attribute");
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Definition definition = (Definition) element;
                return definition.name;
            }
        });
        column = new TableViewerColumn(table, SWT.NONE);
        column.getColumn().setWidth(60);
        column.getColumn().setMoveable(false);
        column.getColumn().setResizable(true);
        column.getColumn().setText("Type");
        column.getColumn().setAlignment(SWT.CENTER);
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Definition definition = (Definition) element;
                return definition.binding == null ? NO_CONTENT : definition.binding.getSimpleName();
            }
        });
        column = new TableViewerColumn(table, SWT.NONE);
        column.getColumn().setWidth(140);
        column.getColumn().setMoveable(false);
        column.getColumn().setResizable(true);
        column.getColumn().setText("Expression");
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Definition definition = (Definition) element;
                return definition.expression == null ? NO_CONTENT : ECQL
                        .toCQL(definition.expression);
            }
        });
        table.getTable().setHeaderVisible(true);
        table.getTable().setLinesVisible(true);
        table.addSelectionChangedListener(tableListener);

        table.setInput(this.transform);
        table.refresh();

        label = new Label(panel, SWT.LEFT);
        label.setText("Definition");
        label.setLayoutData("cell 0 6 2 1, width pref!,left");

        feedbackDecorator = new ControlDecoration(label, SWT.RIGHT|SWT.TOP);

        name = new Text(panel, SWT.SINGLE | SWT.BORDER);
        name.setEditable(true);
        name.setText("");
        name.setLayoutData("cell 0 7 2 1");
        name.setEnabled(false);

        ExpressionInput expressionInput = new ExpressionInput(schema,true);
        expressionInput.setFeedback(feedbackDecorator);

        expression = new ExpressionViewer(panel, SWT.MULTI);
        expression.setInput(expressionInput);
        expression.getControl().setLayoutData(
                "cell 0 8 2 1,height 200:50%:50%,width 300:pref:100%");
        expression.getControl().setEnabled(false);
        expression.addSelectionChangedListener(expressionListener);
        
        label = new Label(panel, SWT.LEFT);
        label.setText("How would you like to handle the result:");
        label.setLayoutData("cell 0 9 2 1,width pref!");

        actionCombo = new Combo(panel, SWT.READ_ONLY);
        actionCombo.setLayoutData("cell 0 10 2 1,width pref!");
        actionCombo(actionCombo);

        return panel;
    }

    private void actionCombo(Combo actionCombo) {
        actionCombo.add(Messages.ReshapeOperation_noAction);
        actionCombo.setData(Messages.ReshapeOperation_noAction,
                new StaticProvider<PostReshapeAction>(new Null_Action()));

        int i = 1;
        String lastSelection = CatalogUIPlugin.getDefault().getDialogSettings()
                .get(ACTION_COMBO_SETTINGS);
        int selected = 0;

        List<IConfigurationElement> extensions = ExtensionPointList
                .getExtensionPointList("net.refractions.udig.catalog.ui.reshapePostAction"); //$NON-NLS-1$
        for (final IConfigurationElement configurationElement : extensions) {
            String name = configurationElement.getAttribute("name"); //$NON-NLS-1$
            IProvider<PostReshapeAction> provider = new IProvider<PostReshapeAction>() {

                public PostReshapeAction get(Object... params) {
                    try {
                        return (PostReshapeAction) configurationElement
                                .createExecutableExtension("class"); //$NON-NLS-1$
                    } catch (CoreException e) {
                        throw (RuntimeException) new RuntimeException().initCause(e);
                    }
                }

            };
            actionCombo.add(name);
            actionCombo.setData(name, provider);
            String id = configurationElement.getNamespaceIdentifier()
                    + "." + configurationElement.getAttribute("id"); //$NON-NLS-1$//$NON-NLS-2$
            actionCombo.setData(name + "id", id); //$NON-NLS-1$

            if (id.equals(lastSelection)) {
                selected = i;
            }
            i++;
        }
        actionCombo.select(selected);
    }

    protected Point getInitialSize() {
        return new Point(500, 500);
    }

    static List<Definition> createDefaultTransformDefinition(SimpleFeatureType featureType) {
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        List<Definition> list = new ArrayList<TransformProcess.Definition>();
        if (featureType != null) {
            for (AttributeDescriptor descriptor : featureType.getAttributeDescriptors()) {
                Definition definition = new Definition();

                definition.name = descriptor.getLocalName();
                definition.binding = descriptor.getType().getBinding();
                definition.expression = ff.property(descriptor.getName());

                list.add(definition);
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void okPressed() {
        boolean ok = false;
        try {
            // transform = createTransformProcessDefinitionList();
            // featureType = createFeatureType();
            // ok = featureType != null;
            ok = transform != null;
            String selected = actionCombo.getItem(actionCombo.getSelectionIndex());
            CatalogUIPlugin.getDefault().getDialogSettings()
                    .put(ACTION_COMBO_SETTINGS, (String) actionCombo.getData(selected + "id")); //$NON-NLS-1$
            postActionProvider = (IProvider<PostReshapeAction>) actionCombo.getData(selected);
        } catch (Throwable t) {
            showFeedback(null, t);
        }
        if (ok) {
            super.okPressed();
        }
    }

    /**
     * Show an error in the UI
     * 
     * @param t
     */
    private void showFeedback(String message, Throwable t) {
        feedbackDecorator.hide();
        feedbackDecorator.hideHover();
        if (t == null && message != null) {
            // warning feedback!
            FieldDecorationRegistry decorations = FieldDecorationRegistry.getDefault();
            FieldDecoration errorDecoration = decorations
                    .getFieldDecoration(FieldDecorationRegistry.DEC_WARNING);
            feedbackDecorator.setImage(errorDecoration.getImage());
            feedbackDecorator.setDescriptionText(message);
            feedbackDecorator.showHoverText(message);
            feedbackDecorator.show();
        } else if (t != null) {
            // if(! (t instanceof ReshapeException) ){
            // CatalogUIPlugin.log("error with reshape", t); //$NON-NLS-1$
            // }
            String errormessage = t.getLocalizedMessage();
            if (errormessage == null) {
                errormessage = Messages.ReshapeOperation_2;
            } else {
                // fix up really long CQL messages
                errormessage = errormessage.replaceAll("\\n\\s+", " ");
            }
            if (message == null) {
                message = MessageFormat.format(Messages.ReshapeOperation_3, errormessage);
            }

            FieldDecorationRegistry decorations = FieldDecorationRegistry.getDefault();
            FieldDecoration errorDecoration = decorations
                    .getFieldDecoration(FieldDecorationRegistry.DEC_WARNING);
            feedbackDecorator.setImage(errorDecoration.getImage());
            feedbackDecorator.setDescriptionText(message);
            feedbackDecorator.showHoverText(message);
            feedbackDecorator.show();
        }
    }

    /**
     * FeatureType for resulting output; only valid after {@link #okPressed()}
     * 
     * @return
     */
    public SimpleFeatureType getFeatureType() {
        return featureType;
    }

    /**
     * Transform process definition; only valid after {@link #okPressed()}.
     * 
     * @return
     */
    public List<TransformProcess.Definition> getTransform() {
        return transform;
    }

    /**
     * You cannot call this once the dialog is closed, see the okPressed method.
     * 
     * @return a SimpleFeatureType created based on the contents of Text
     */
    // private SimpleFeatureType createFeatureType() throws SchemaException {
    //
    // SimpleFeatureTypeBuilder build = new SimpleFeatureTypeBuilder();
    //
    // transform = createTransformProcessDefinitionList();
    //
    // for( Definition definition : transform ){
    // String name = definition.name;
    // Expression expression = definition.expression;
    //
    // // hack because sometimes expression returns null. I think the real bug is with
    // AttributeExpression
    // Class<?> binding = definition.binding;
    // if( binding == null ){
    // Object value = expression.evaluate(sample);
    // if( value == null){
    // if( expression instanceof PropertyName){
    // String path = ((PropertyName)expression).getPropertyName();
    // AttributeType attributeType = sample.getFeatureType().getType(path);
    // if( attributeType == null ){
    // String msg = Messages.ReshapeOperation_4;
    // throw new ReshapeException(format(msg, name, path));
    // }
    // binding = attributeType.getClass();
    // }
    // } else {
    // binding = value.getClass();
    // }
    // if( binding ==null ){
    // String msg = Messages.ReshapeOperation_5;
    // throw new ReshapeException(format(msg, name));
    // }
    // }
    // if( Geometry.class.isAssignableFrom( binding )){
    // CoordinateReferenceSystem crs;
    // AttributeType originalAttributeType = originalFeatureType.getType(name);
    // if( originalAttributeType == null && originalAttributeType instanceof GeometryType ) {
    // crs = ((GeometryType)originalAttributeType).getCoordinateReferenceSystem();
    // } else {
    // crs = originalFeatureType.getCoordinateReferenceSystem();
    // }
    // build.crs(crs);
    // build.add(name, binding);
    // }
    // else {
    // build.add(name, binding);
    // }
    // }
    // build.setName( ReshapeOperation.getNewTypeName( originalFeatureType.getTypeName() ) );
    //
    // return build.buildFeatureType();
    // }

    /**
     * You cannot call this once the dialog is closed, see the {@link #okPressed()} for details.
     * 
     * @return Transform definition
     */
    // public List<TransformProcess.Definition> createTransformProcessDefinitionList() {
    // List<TransformProcess.Definition> list = new ArrayList<TransformProcess.Definition>();
    //
    //        String definition = text.getText().replaceAll("\r","\n").replaceAll("[\n\r][\n\r]", "\n");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    //
    // list = TransformProcess.toDefinition( definition );
    // return list;
    // }
}