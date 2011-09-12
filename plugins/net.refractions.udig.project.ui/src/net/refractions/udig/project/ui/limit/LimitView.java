package net.refractions.udig.project.ui.limit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * This is the view that allows a uses to select the method to define the limit
 * @author pfeiffp
 *
 */
public class LimitView extends ViewPart {

    private Text text;
    //private ISelectionListener selectionListener;
    private Text description;

	/**
	 * 
	 */
	public LimitView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        parent.setLayout( layout );
        Label label = new Label(parent, SWT.RIGHT );
        label.setLayoutData( new GridData(SWT.RIGHT,SWT.TOP,true,false ) );
        label.setText("Limit:");
        
        text = new Text(parent, SWT.DEFAULT | SWT.READ_ONLY | SWT.WRAP );
        text.setTextLimit(70);
        text.setLayoutData( new GridData(SWT.LEFT,SWT.TOP,true,true, 3,1 ) );
        
        label = new Label(parent, SWT.RIGHT );
        label.setLayoutData( new GridData(SWT.RIGHT,SWT.TOP,true,false ) );
        label.setText("Content:");
        
        description = new Text(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI );
        GridData gridData = new GridData(SWT.DEFAULT,SWT.DEFAULT,true,true, 3,3 );
        gridData.widthHint = 500;
        gridData.heightHint = 200;
        description.setLayoutData( gridData );
        
        /*selectionListener = new WorkbenchSelectionListener();
        ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
        selectionService.addPostSelectionListener(selectionListener);*/


	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		// TODO Auto-generated method stub
		super.init(site, memento);
	}

}
