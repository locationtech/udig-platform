/**
 * 
 */
package net.refractions.udig.ui.operations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.internal.ui.operations.OperationCategory;
import net.refractions.udig.internal.ui.operations.OperationMenuFactory;
import net.refractions.udig.ui.internal.Messages;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog that 
 * @author Jody
 *
 */
public class OperationDialog extends TitleAreaDialog implements ITreeContentProvider {

    private class OperationLabelProvider extends LabelProvider implements IColorProvider {
        @Override
        public String getText( Object element ) {
            if( element instanceof OperationDialog ){
                return ((OperationDialog)element).getTitleImageLabel().getText();
            }
            else if (element instanceof OperationCategory) {
                return ((OperationCategory)element).getMenuText();                    
            }
            else if (element instanceof OpAction){
                return ((OpAction)element).getText();
            }
            return super.getText(element);
        }

        public Color getBackground( Object element ) {
            return null;
        }

        public Color getForeground( Object element ) {
            if (isEnabled( element )){
                return null; // use default color
            }
            return Display.getCurrent().getSystemColor(SWT.COLOR_GRAY );            
        }
    }
    
    public boolean isEnabled( Object element ){
        if (element == null ){
            return false;
        }
        if (element instanceof OperationCategory ){
            return !((OperationCategory)element).isEmpty();
        }            
        if (element instanceof OpAction ){
            return ((OpAction)element).isEnabled();
        }
        return true;
    }
    
    private TreeViewer viewer;
    private ISelection selection;
    
    public OperationDialog( Shell parentShell, ISelection selection ) {
        super(parentShell);
        this.selection = selection;
    }
    
    @Override
    protected int getShellStyle() {
        return SWT.RESIZE|SWT.MAX|SWT.CLOSE|SWT.MIN|SWT.APPLICATION_MODAL;
    }

    public void dispose() {        
        selection = null;
    }
    
    @Override
    protected void configureShell( Shell newShell ) {
        newShell.setText("Operations");
        super.configureShell(newShell);
    }
    @Override
    protected Point getInitialSize() {
        return new Point(400,400);
    }
    @Override
    protected Control createContents( Composite parent ) {
        Control control = super.createContents(parent);
        setTitle("Operations");
        setMessage("What would you like to do:");
        return control;
    }
    @Override
    protected Control createDialogArea( Composite parent ) {
        Composite composite = (Composite) super.createDialogArea(parent);
        org.eclipse.swt.widgets.Tree tree=new org.eclipse.swt.widgets.Tree(composite, SWT.V_SCROLL|SWT.MULTI);
        viewer = new TreeViewer(tree);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.heightHint=SWT.DEFAULT;
        gridData.widthHint=SWT.DEFAULT;
        gridData.verticalSpan=4;
        tree.setLayoutData(gridData);
        viewer.setContentProvider(this);
        //ResolveLabelProviderSimple resolveLabelProviderSimple = new ResolveLabelProviderSimple();
        // ResolveTitlesDecorator resolveTitlesDecorator = new ResolveTitlesDecorator(resolveLabelProviderSimple, true);
        // viewer.setLabelProvider(new DecoratingLabelProvider(resolveLabelProviderSimple,resolveTitlesDecorator));
        viewer.setLabelProvider( new OperationLabelProvider());

        viewer.setInput( this );
        viewer.addPostSelectionChangedListener(new ISelectionChangedListener(){
            public void selectionChanged( SelectionChangedEvent event ) {
                IStructuredSelection s=(IStructuredSelection) event.getSelection();
                if( s.isEmpty() ){
                    return; 
                }
                Button button = getButton( IDialogConstants.OK_ID );
                Object selection = s.getFirstElement();
                String title=null;
                if( selection instanceof OperationDialog){
                    setMessage( "What would you like to do:");
                    button.setEnabled(false);
                }
                else if (selection instanceof OperationCategory){
                    OperationCategory category = (OperationCategory) selection;
                    if( category.getDescription() != null ){
                        setMessage( category.getDescription() );
                    }
                    else {
                        setMessage( "Please select an operation:");
                    }
                    button.setEnabled(false);
                }
                else if (selection instanceof OpAction){
                    OpAction action = (OpAction) selection;
                    setMessage(action.getDescription());
                    button.setEnabled( action.isEnabled() );
                }
            }                    
        });
        
        viewer.addDoubleClickListener(new IDoubleClickListener(){
            public void doubleClick( DoubleClickEvent event ) {
                buttonPressed(IDialogConstants.OK_ID);
            }
        });
        return composite;
    }
    @Override
    protected void createButtonsForButtonBar( Composite parent ) {
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        createButton(parent, IDialogConstants.OK_ID, Messages.OperationDialog_Operate, true);
    }
    @Override
    protected void buttonPressed( int buttonId ) {
        if (buttonId == IDialogConstants.OK_ID) {
            IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
            Iterator iter = selection.iterator();
            List<IOp> selectedOperations = new ArrayList<IOp>();
            boolean enabled = false;
            while( iter.hasNext() ){
                Object selected = iter.next();
                if( selected instanceof OpAction){
                    OpAction action = (OpAction) selected;
                    action.run();
                }
            }
            // is their a service that we can run operations with?
            Button button = getButton(IDialogConstants.OK_ID);
            if( button != null ){
                
            }
        }
        super.buttonPressed(buttonId);
    }

    public Object[] getElements( Object inputElement ) {
        if (inputElement instanceof OperationCategory) {
            OperationCategory operationCategory = (OperationCategory) inputElement;
            return operationCategory.getActions().toArray();
        }
        else if (inputElement instanceof OperationDialog) {
            OperationMenuFactory factory = UiPlugin.getDefault().getOperationMenuFactory();
            return factory.getCategories().values().toArray();
        }
        return null;
    }

    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
        // really?
    }

    public Object[] getChildren( Object parentElement ) {
        return getElements(parentElement);
    }

    public Object getParent( Object element ) {
        if( element instanceof OpAction){
            OpAction action = (OpAction) element;
            return action.category;
        }
        else if ( element instanceof OperationCategory){
            return this;
        }
        else {
            return null;
        }
    }

    public boolean hasChildren( Object element ) {
        if (element instanceof OperationDialog) {
            return true;
        }
        else if (element instanceof OperationCategory){
            return !((OperationCategory)element).isEmpty();
        }
        return false;
    }
}