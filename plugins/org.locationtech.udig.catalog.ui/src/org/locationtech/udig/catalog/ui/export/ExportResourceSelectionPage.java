/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.export;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.catalog.ui.ResolveLabelProviderSimple;
import org.locationtech.udig.catalog.ui.ResolveTitlesDecorator;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizard;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPage;
import org.locationtech.udig.ui.CRSDialogCellEditor;
import org.locationtech.udig.ui.CharsetDialogCellEditor;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import org.geotools.data.FeatureSource;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A page that allows the user to select the layer(s) to save.
 * 
 * @author chorner
 * @since 1.1.0
 */
public class ExportResourceSelectionPage extends WorkflowWizardPage implements IPageChangedListener, ICheckStateListener {
    public static final String DIRECTORY_KEY = "ExportResourceSelection_DIRECTORY_KEY"; //$NON-NLS-1$
	private CheckboxTreeViewer viewer;
	private Text destText;

    public ExportResourceSelectionPage( String pageName, String title, ImageDescriptor banner ) {
        super(pageName, title, banner );
        setMessage(Messages.LayerSelectionPage_message);
    }

    public List<Data> getCheckedElements() {
        List<Data> list = new ArrayList<Data>();
        for( Object object : viewer.getCheckedElements() ) {
            Data resource = (Data) object;
            list.add(resource);
        }
        return list;
    }

    public void createControl( Composite parent ) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        
        createExportDirComponent(composite);

        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.ExportPage_ResourceList);
        GridData layoutData = new GridData(SWT.FILL, SWT.NONE, true, false);
        //layoutData.horizontalSpan = 3;
        label.setLayoutData(layoutData);
        
        viewer = new CheckboxTreeViewer(composite, SWT.CHECK|SWT.BORDER|SWT.SINGLE|SWT.FULL_SELECTION);        
        Tree tree = createTree(viewer.getTree());
        
        viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.addPostSelectionChangedListener(new ISelectionChangedListener(){

            public void selectionChanged( SelectionChangedEvent event ) {
                getWizard().getContainer().updateButtons();
            }

        });
        viewer.addCheckStateListener(this);
        viewer.setContentProvider(new ResourceContentProvider());

        viewer.setLabelProvider(new ExportResourceLabelProvider());
        viewer.setAutoExpandLevel(3);
        
        final String crs="CRS"; //$NON-NLS-1$
        final String charset="charset"; //$NON-NLS-1$
        viewer.setColumnProperties(new String[]{"name",crs, charset}); //$NON-NLS-1$
        viewer.setCellEditors(new CellEditor[]{new TextCellEditor(tree), new CRSDialogCellEditor(tree), new CharsetDialogCellEditor(tree)});
        viewer.setCellModifier(new ICellModifier(){

            public boolean canModify( Object element, String property ) {
                return true;
            }

            public Object getValue( Object element, String property ) {
                Data data = (Data)element;
                if( crs.equals(property) ){
                    return data.getCRS();
                } else if (charset.equals(property)) {
                    return data.getCharset();
                }else{
                    if( data.getName()==null ){
                        return ((ExportResourceLabelProvider)viewer.getLabelProvider()).getColumnText(data, 0);
                    }else {
                        return data.getName();
                    }
                        
                }
            }

            public void modify( Object element, String property, Object value ) {
                TreeItem item=(TreeItem) element;
                Data data=(Data) item.getData();
                if( crs.equals(property) ){
                    data.setCRS((CoordinateReferenceSystem)value);
                } else if (charset.equals(property)) {
                    data.setCharset((Charset) value);
                }else{
                    data.setName((String) value);
                }
                viewer.update(data, null);
            }
            
        });
        // use the state to initialize ui
        ExportResourceSelectionState state = (ExportResourceSelectionState) getState();
        setInput(state);

        setControl(composite);
    }
    

    private void createExportDirComponent(Composite top) {
    	Composite comp = new Composite(top, SWT.NONE);
    	comp.setLayout(new GridLayout(3,false));
    	comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	
        createExportLabel(comp);

        String tooltip = Messages.ExportPage_ExportDir;

        createExportText(comp, tooltip);

        createExportBrowseButton(comp, tooltip);
		
	}
    

    private void createExportBrowseButton(Composite comp, String tooltip) {
        Button select = new Button(comp, SWT.PUSH);
        select.setText(Messages.ExportPage_Browse);
        select.setToolTipText(tooltip);
        GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gridData.horizontalAlignment = SWT.FILL;
        select.setLayoutData(gridData);
        select.addSelectionListener(new SelectButtonListener());
    }

    private void createExportText(Composite comp, String tooltip) {
        destText = new Text(comp, SWT.SINGLE | SWT.BORDER);
        destText.setToolTipText(tooltip);
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        destText.setLayoutData(gridData);
        WorkflowWizard wizard2 = getWizard();
		IDialogSettings dialogSettings = wizard2.getDialogSettings();
		String previousLocation = dialogSettings.get(DIRECTORY_KEY);
        if (previousLocation != null) {
            destText.setText(previousLocation);
            getState().setExportDir(previousLocation);
        } else {
            destText.setText(getState().getExportDir());
        }
        destText.addListener(SWT.Modify, new Listener(){

			public void handleEvent(Event event) {
				getState().setExportDir(destText.getText());
			}
        	
        });
    }
    
    @Override
    public ExportResourceSelectionState getState() {
    	return (ExportResourceSelectionState) super.getState();
    }

    private void createExportLabel(Composite comp) {
        Label scaleLabel = new Label(comp, SWT.NONE);
        scaleLabel.setText(Messages.ExportPage_Destination);
        scaleLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
                false));
    }

    public File getExportDir() {
        return new File(this.destText.getText());
    }


	public void checkStateChanged( CheckStateChangedEvent event ) {
        if (event.getChecked()) {
            Object o = event.getElement();
            if (viewer.getGrayed(o)) {
                viewer.setChecked(o, false); //grayed elements cannot be checked
                return; //(this is temporary until multiple format export is supported)
            }
        }
        
        // set all children to same check state
        Object o = event.getElement();
        ResourceContentProvider p = (ResourceContentProvider) viewer.getContentProvider();
        Object[] elements = p.getElements(o);
        if (elements != null && elements.length > 0) {
            for( int i = 0; i < elements.length; i++ )
                viewer.setChecked(elements[i], event.getChecked());
        }

        if (viewer.getCheckedElements().length > 0) { //set complete flag
            setPageComplete(true);
        } else {
            setPageComplete(false);
        }

        syncWithUI(); //update the selected layers value in the state
        
    }


    private Tree createTree( Tree tree ) {
        
        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnWeightData(1,true));
        tableLayout.addColumnData(new ColumnWeightData(1,true));
        tableLayout.addColumnData(new ColumnWeightData(1,true));
        tree.setLayout(tableLayout);

        TreeColumn name=new TreeColumn(tree, SWT.DEFAULT);
        name.setMoveable(false);
        name.setResizable(true);
        
        TreeColumn projection=new TreeColumn(tree, SWT.DEFAULT);
        projection.setMoveable(false);
        projection.setResizable(true);

        TreeColumn charset =new TreeColumn(tree, SWT.DEFAULT);
        charset.setMoveable(false);
        charset.setResizable(true);
 
        return tree;
    }

    public CheckboxTreeViewer getViewer() {
        return viewer;
    }

    public void syncWithUI() {
        ExportResourceSelectionState state = (ExportResourceSelectionState) getState(); 
        List<Data> selectedLayers = new ArrayList<Data>();
        Object[] elements = getViewer().getCheckedElements();
        for( int i = 0; i < elements.length; i++ ) {
            Data layer = (Data) elements[i];
            selectedLayers.add(layer);
        }
        state.setSelectedLayers(selectedLayers);
    }

    @Override
    public void shown() {
        setInput((ExportResourceSelectionState) getState());
    }

    private void setInput( ExportResourceSelectionState state ) {
        List<Data> resources = state.getLayers();
        viewer.setInput(resources);
        if (resources != null) {
            for( Data data : resources ) {
                viewer.setChecked(data, false);
                if (data.getResource().canResolve(FeatureSource.class))
                    viewer.setGrayed(data, false);
                else
                    viewer.setGrayed(data, true);
            }

            //select possible layers
            for( Data data : resources ) {
                boolean checked = !viewer.getGrayed(data) && data.isChecked();
                viewer.setChecked(data, checked);
            }
        }
        
        if (viewer.getCheckedElements().length > 0) {
            setPageComplete(true);
        } else {
            setPageComplete(false);
        }
        
        syncWithUI();
    }
    
    /**
     * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
     */
    @Override
    public boolean isPageComplete() {
        return viewer.getCheckedElements().length > 0;
    }
     
    Button findButton( Control[] children, int id ) {
        if (((Integer) getShell().getDefaultButton().getData()).intValue() == id)
            return getShell().getDefaultButton();

        for( Control child : children ) {
            if (child instanceof Button) {
                Button button = (Button) child;
                if (((Integer) button.getData()).intValue() == id)
                    return button;
            }
            if (child instanceof Composite) {
                Composite composite = (Composite) child;
                Button button = findButton(composite.getChildren(), id);
                if (button != null)
                    return button;
            }
        }
        return null;
    }

    private static class ResourceContentProvider implements ITreeContentProvider {

        @SuppressWarnings("unchecked")
        public Object[] getElements( Object inputElement ) {
            return getChildren(inputElement);
        }

        public void dispose() {
        }

        public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
        }

        @SuppressWarnings("unchecked")
        public Object[] getChildren( Object parentElement ) {
            if( parentElement instanceof List ){
                List<Data> resources = (List<Data>) parentElement;
                return resources.toArray();
            }
            return null;
        }

        public Object getParent( Object element ) {
            return null;
        }

        public boolean hasChildren( Object element ) {
            return false;
        }

    }


    /**
     * Adapts the new ResolveTitlesDecorator(new ResolveLabelProviderSimple()) so that it will can appear in the
     * table with the title returned by the decorator as the name column and the projection as the title column
     * @author Jesse
     * @since 1.1.0
     */
    public class ExportResourceLabelProvider implements ITableLabelProvider{

        private DecoratingLabelProvider wrapped;

        public ExportResourceLabelProvider() {
            ResolveTitlesDecorator titleDecorator = new ResolveTitlesDecorator(new ResolveLabelProviderSimple());
            wrapped = new DecoratingLabelProvider(titleDecorator.getSource(),
                    titleDecorator);
        }
        
        public void addListener( ILabelProviderListener listener ) {
            wrapped.addListener(listener);
        }

        public void dispose() {
            wrapped.dispose();
        }

        public boolean isLabelProperty( Object element, String property ) {
            return wrapped.isLabelProperty(element, property);
        }

        public void removeListener( ILabelProviderListener listener ) {
            wrapped.addListener(listener);
        }

        public Image getColumnImage( Object element, int columnIndex ) {
            assert element instanceof Data;
            assert columnIndex < 3;

            Data data=(Data)element;
            switch( columnIndex ) {
            case 0:
                return wrapped.getImage(data.getResource());
            case 1:
                return null;
            case 2:
                return null;
            default:
                break;
            }
            return null;
        }

        public String getColumnText( Object element, int columnIndex ) {
            assert element instanceof Data;
            assert columnIndex < 3;

            Data data=(Data)element;
            CoordinateReferenceSystem crs = data.getCRS();
			switch( columnIndex ) {
            case 0:
                if( data.getName()==null ){
                    return wrapped.getText(data.getResource());
                }else{
                    return data.getName(); 
                }
            case 1:
                if( crs==null ) {
                    return "undefined"; //$NON-NLS-1$
                } else {
                    return crs.getName().toString();
                }
            case 2:
                if( data.getCharset()==null ) {
                    //addition to handle problem with charset always being set to ISO-8859-1 
                    data.setCharset((System.getProperty("shp.encoding") != null) ? 
                            Charset.forName(System.getProperty("shp.encoding")) : Charset.defaultCharset()); 
                } 
                return data.getCharset().displayName();

            default:
                break;
            }
            throw new RuntimeException("Don't know how to handle column 3"); //$NON-NLS-1$
        }

    }
    
    public void pageChanged( PageChangedEvent event ) {
    }
    
    public class SelectButtonListener implements SelectionListener {

        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }

        public void widgetSelected(SelectionEvent e) {
            DirectoryDialog d = new DirectoryDialog(e.widget.getDisplay()
                    .getActiveShell());
            String selection = d.open();
            if (selection != null) {
                destText.setText(selection);
            }
        }

    }
}
