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
package net.refractions.udig.project.ui.summary;

import java.util.Collection;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * A control that shows a TableTree summary of the provided {@link SummaryData}
 * @author Jesse
 * @since 1.1.0
 */
public class SummaryControl {

    private static final String VALUE = "VALUE"; //$NON-NLS-1$
    private Collection<SummaryData> data;
    private TreeViewer viewer;

    public SummaryControl(Collection<SummaryData> data){
        this.data=data;
    }
    
    public Control createControl( Composite parent ) {
    	viewer=new TreeViewer(parent, SWT.SINGLE|SWT.FULL_SELECTION);
    	Tree tree = viewer.getTree();
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);
        
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,true));
        TableLayout tableLayout=new TableLayout();
        tableLayout.addColumnData(new ColumnWeightData(1,200));
        tableLayout.addColumnData(new ColumnWeightData(3,200));
        TreeColumn nameColumn=new TreeColumn(tree, SWT.LEFT);
        nameColumn.setWidth(200);
        TreeColumn infoColumn=new TreeColumn(tree, SWT.LEFT|SWT.H_SCROLL);
        viewer.setContentProvider(new SummaryDataProvider());
        viewer.setLabelProvider(new SummaryLabelProvider());
        viewer.setInput(data);
        viewer.setColumnProperties(new String[]{"TITLE",VALUE});  //$NON-NLS-1$
        
        infoColumn.pack();
        setCellEditor(viewer);
        
        return tree;
    }

    private void setCellEditor( TreeViewer viewer ) {
        TextCellEditor textCellEditor = new TextCellEditor(viewer.getTree());
        textCellEditor.setValidator(new SummaryCellEditorValidator(data, viewer.getTree()));
        viewer.setCellEditors(new CellEditor[]{null, textCellEditor});
        viewer.setCellModifier(new SummaryCellModifier(data));
    }
    /**
     * Refresh an element
     *
     * @param element
     */
    public void refresh( Object element ) {
        viewer.refresh(element);
    }
    /**
     * Applies edits if an editor is active
     */
    public void applyEdit() {
        viewer.getCellEditors()[1].deactivate();
    }
    /**
     * Cancel edits if an editor is active
     */
    public void cancelEdit() {
        viewer.cancelEditing();
    }

    private class SummaryDataProvider implements ITreeContentProvider{

        public Object[] getChildren( Object parentElement ) {
            if( parentElement == data ){
                return data.toArray();
            }
            if ( parentElement instanceof SummaryData ){
                return ((SummaryData)parentElement).getChildren();
            }
            return null;
        }

        public Object getParent( Object element ) {
            if( element instanceof SummaryData ){
                SummaryData parent = ((SummaryData)element).getParent();
                if ( parent==null )
                    return data;
                else
                    return parent;
            }
            return null;
        }

        public boolean hasChildren( Object element ) {
            if( element == data )
                return true;
            if ( element instanceof SummaryData ){
                SummaryData[] children = ((SummaryData)element).getChildren();
                if (  children!=null && children.length>0 )
                    return true;
            }
            return false;
        }

        public Object[] getElements( Object inputElement ) {
            return getChildren(inputElement);
        }

        public void dispose() {
        }

        public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
        }
        
    }
    
    private static class SummaryLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider{

        public Image getColumnImage( Object element, int columnIndex ) {
            return null;
        }

        public String getColumnText( Object element, int columnIndex ) {
            if( !(element instanceof SummaryData) )
                return null;
            SummaryData data=(SummaryData) element;
            if( columnIndex==0 )
                return data.getTitle();
            return data.getInfo();
        }

        public Color getBackground( Object element, int columnIndex ) {
            return null;
        }

        public Color getForeground( Object element, int columnIndex ) {
            
            if ( columnIndex==0 )
                return null;
            SummaryData data = (SummaryData) element;
            if (data.getModifier().canModify(element, VALUE))
                return null;
            return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
        }
        
    }
}
