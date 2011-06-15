package net.refractions.udig.catalog.service.database;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class TableBasedExtraParamsControl implements ExtraParamsControl {

	private List<ExtraParams> paramsDefinition;

	public TableBasedExtraParamsControl(List<ExtraParams> paramsDefinition) {
		this.paramsDefinition = paramsDefinition;
	}

	public Control createControl(Composite parent) {
		TableViewer viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER); 
		
		createColumns(parent, viewer);
		final Table table = viewer.getTable();
		table.setHeaderVisible(false);
		table.setLinesVisible(true);
		
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(paramsDefinition);

		return viewer.getControl();
	}

	private void createColumns(Composite parent, final TableViewer viewer) {
		TableViewerColumn col = createTableViewerColumn(viewer, "c1", 200);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ExtraParams p = (ExtraParams) element;
				return p.name;
			}
		});

		col = createTableViewerColumn(viewer, "c2", 300);
		col.setEditingSupport(new ParamEditingSupport(viewer));
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ExtraParams p = (ExtraParams) element;
				if(p.getCellEditor() == null) {
					p.setCellEditor(p.createCellEditor(viewer.getTable()));
				}
				return p.getValue();
			}
		});
	}
	private TableViewerColumn createTableViewerColumn(TableViewer viewer,String title, int bound) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
				SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;

	}
	public Map<String, Serializable> getParams() {
		Map<String, Serializable> params = new HashMap<String, Serializable>();
		
		for (ExtraParams def : paramsDefinition) {
			CellEditor cellEditor = def.getCellEditor();
			if(cellEditor !=null && cellEditor.getValue() != null) {
				params.put(def.param.key, def.convertValue(cellEditor.getValue()));
			}
		}
		return params;
	}

	class ParamEditingSupport extends EditingSupport {

		private TableViewer viewer;

		public ParamEditingSupport(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			ExtraParams extraParams = (ExtraParams)element;
			CellEditor cellEditor = extraParams.getCellEditor();
			return cellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			ExtraParams def = (ExtraParams)element;
			return def.getCellEditor().getValue();
		}

		@Override
		protected void setValue(Object element, Object value) {
			ExtraParams def = (ExtraParams)element;
			viewer.refresh(element,true);
			def.getCellEditor().setValue(value);
		}
	}
}
