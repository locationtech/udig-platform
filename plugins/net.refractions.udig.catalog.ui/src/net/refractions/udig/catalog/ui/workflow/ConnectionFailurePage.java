/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.catalog.ui.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.catalog.ui.workflow.ConnectionFailureState.Data;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Wizard Page associated with ConnectionFailurePage
 *
 * @author Jesse
 * @since 1.1.0
 */
public class  ConnectionFailurePage extends WorkflowWizardPage implements ILabelProvider, ITreeContentProvider {

    private TreeViewer viewer;
    private Text details;
    private static String canProcess=Messages.ConnectionFailurePage_canProcess;

    public ConnectionFailurePage(  ) {
        super(Messages.ConnectionFailurePage_title);
        setTitle(Messages.ConnectionFailurePage_displayedTitle);
        setMessage(Messages.ConnectionFailurePage_message);
    }

    public void createControl( Composite parent ) {
        SashForm form=new SashForm(parent, SWT.VERTICAL);
        Tree tree=new Tree(form, SWT.V_SCROLL|SWT.SINGLE);
        TableLayout layout=new TableLayout();
        layout.addColumnData(new ColumnWeightData(100,false));
        tree.setLayout(layout);
        tree.setLinesVisible(true);
        viewer=new TreeViewer(tree);

        viewer.setContentProvider(this);
        viewer.setLabelProvider(this);

        viewer.addPostSelectionChangedListener(new ISelectionChangedListener(){

            public void selectionChanged( SelectionChangedEvent event ) {
                setDetails();
            }
        });

        viewer.setSorter(new ViewerSorter(){
             @Override
            public void sort( Viewer viewer, Object[] elements ) {
                Arrays.sort(elements, new Comparator<Object>(){

                    public int compare( Object o1, Object o2 ) {
                        return 0;
                    }

                });
            }

        });

        Composite composite = new Composite(form, SWT.BORDER);
        composite.setLayout(new FillLayout());
        details=new Text(composite, SWT.BORDER|SWT.WRAP|SWT.READ_ONLY|SWT.V_SCROLL);

        form.setWeights(new int[]{75,25});

        setControl(form);
    }
    private void setDetails() {
        Object firstElement = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
        if( firstElement instanceof Data ){
            Data data = (Data) firstElement;
            String string = data.message;
            if( string==null )
                string = canProcess;
            details.setText(string);
        }else{
            details.setText(Messages.ConnectionFailurePage_selectChild);
        }
    }

    @Override
    public void shown() {
        viewer.setInput(getState().getReports());
        if( viewer.getTree().getItemCount()>0){
            TreeItem item = viewer.getTree().getItem(0);
            viewer.getTree().setSelection(item);

            setDetails();
        }
    }

    public ConnectionFailureState getState(){
        return (ConnectionFailureState) super.getState();
    }

    @Override
    public boolean canFlipToNextPage() {
        return false;
    }

    public void addListener( ILabelProviderListener listener ) {
    }

    public boolean isLabelProperty( Object element, String property ) {
        return false;
    }

    public void removeListener( ILabelProviderListener listener ) {
    }

    public Image getImage( Object element ) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public String getText( Object element ) {
        if (element instanceof Entry) {
            Entry<String, List<Data>> entry = (Entry<String, List<Data>>) element;
            return entry.getValue().get(0).name;
        }
        if (element instanceof Data) {
            Data data=(Data) element;
            if( getParent(element) == getState().getReports() )
                return data.name;
            return data.url.toString();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Object[] getChildren( Object parentElement ) {
        if (parentElement instanceof Map) {
            Map<String, List<Data>> map = (Map<String, List<Data>>) parentElement;
            if( map.values().iterator().next().size()==1 ){

                // no need for hierarchy since each element only has a single data.
                Collection<List<Data>> values = map.values();
                List<Data> all=new ArrayList<Data>();
                for( List<Data> list : values ) {
                    all.addAll(list);
                }
                return all.toArray();
            }else{
                return map.entrySet().toArray();
            }
        }
        if (parentElement instanceof Entry) {
            Entry<String, List<Data>> entry = (Entry<String, List<Data>>) parentElement;
            return entry.getValue().toArray();
        }
        if (parentElement instanceof Data) {
            return null;
        }
        return null;
    }

    public Object getParent( Object parentElement ) {
        if (parentElement instanceof Map) {
            return null;
        }
        if (parentElement instanceof Entry) {
            return getState().getReports();
        }
        if (parentElement instanceof Data) {
            Set<Entry<String, List<Data>>> set = getState().getReports().entrySet();
            Data data=(Data) parentElement;
            for( Entry<String, List<Data>> entry : set ) {
                if(entry.getKey().equals(data.id)){
                    if( entry.getValue().size()==1 )
                        return getState().getReports();
                    return entry;
                }
            }
        }
        return null;
    }

    public boolean hasChildren( Object parentElement ) {
        if (parentElement instanceof Map) {
            return true;
        }
        if (parentElement instanceof Entry) {
            return true;
        }
        if (parentElement instanceof Data) {
            return false;
        }
        return false;
    }

    public Object[] getElements( Object elements ) {
        return getChildren(elements);
    }

    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
    }
}
