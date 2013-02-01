/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.style.raster.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.geotools.brewer.color.BrewerPalette;
import org.geotools.styling.ColorMap;
import org.geotools.styling.ColorMapEntry;
import org.geotools.styling.ColorMapImpl;
import org.geotools.styling.builder.ColorMapEntryBuilder;

/**
 * Implementation of the color map type panel
 * that colors the raster via the unique values
 * in the raster.
 * 
 * @author Emily Gouge (Refractions Research Inc.)
 *
 */
public class UniqueValuesPanel implements IColorMapTypePanel{

	/* table setup */
	private static enum TableColumn{
		COLOR("Color", 50),
		OPACITY("Opacity", 60),
		VALUE("Value", 60),
		LABEL("Label", 100);
		
		String guiName;
		int size;
		private TableColumn(String guiName, int size){
			this.guiName = guiName;
			this.size = size;
		}
		
		/**
		 * Returns the appropriate cell editor for the
		 * current column
		 * @param tableViewer
		 * @return
		 */
		public CellEditor getCellEditor(TableViewer tableViewer){
			if (this == COLOR){
				return new ColorCellEditor(tableViewer.getTable());	
			}else if (this == OPACITY || this == VALUE || this == LABEL){
				return new TextCellEditor(tableViewer.getTable());
			}
			return null;
		}
		
		
		/**
		 * Updates the color entry value for the
		 * current column with the new value
		 * @param entry
		 * @param newValue
		 */
		public void updateValue(ColorEntry entry, Object newValue){
			if (this == TableColumn.COLOR){
				if (newValue instanceof Color){
					entry.setColor((Color) newValue);
				}else if (newValue instanceof RGB){
					RGB r = (RGB)newValue;
					entry.setColor( new Color(r.red, r.green, r.blue));
				}
			}else if (this == TableColumn.OPACITY){
				if (newValue instanceof Double){
					entry.setOpacity((Double)newValue);
				}else if (newValue instanceof String){
					try{
						Double d= Double.parseDouble((String)newValue);
						if (d >= 0 && d <=1){
							entry.setOpacity(d);
						}else if (d >= 0 && d <= 100){
							entry.setOpacity(d /100);
						}
					}catch(Exception ex){}
				}
				
			}else if (this == TableColumn.VALUE){
				if (newValue instanceof Double){
					entry.setValue((Double)newValue);
				}else if (newValue instanceof String){
					try{
						entry.setValue(Double.parseDouble((String)newValue));
					}catch(Exception ex){}
				}
			}else if (this== TableColumn.LABEL){
				entry.setLabel(newValue.toString());
			}
		}
		
		/**
		 * The string value associated with the color entry object for the current 
		 * column,
		 * @param entry
		 * @return
		 */
		private String getValue(ColorEntry entry){
			if (this == TableColumn.OPACITY){
				return String.valueOf(entry.getOpacity() * 100) + "%"; //$NON-NLS-1$
			}else if (this == TableColumn.VALUE){
				return String.valueOf(entry.getValue());
			}else if (this == TableColumn.LABEL){
				if (entry.getLabel() == null){
					return "";
				}
				return entry.getLabel();
			}
			return ""; //$NON-NLS-1$
		}
	}

	private TableViewer tblViewer;
	
	protected ArrayList<ColorEntry> colors = new ArrayList<ColorEntry>();

	private BrewerPalette currentPalette;
	private boolean reverseColors;
	
	protected SingleBandEditorPage page;
	
	/**
	 * Creates a new unique values panel
	 * @param parent parent control
	 * @param page parent style page
	 */
	public UniqueValuesPanel(SingleBandEditorPage page){
		this.page = page;
	}
	

	/**
	 * @param parent
	 * @return
	 */
	@Override
	public Composite createControl(Composite parent){
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		main.setLayout(new GridLayout(2, false));
		
		//create table
		tblViewer = new TableViewer(main, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER | SWT.VIRTUAL);
		tblViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tblViewer.getTable().setHeaderVisible(true);
		tblViewer.getTable().setLinesVisible(true);
		TableLabelProvider labelProvider = new TableLabelProvider();
		
		for (int i = 0; i < TableColumn.values().length; i++){
			final TableColumn col = TableColumn.values()[i];
			TableViewerColumn colOpacity = new TableViewerColumn(tblViewer, SWT.CENTER);
			colOpacity.setLabelProvider(labelProvider);
			colOpacity.getColumn().setText(col.guiName);
			colOpacity.getColumn().setWidth(col.size);
		
			colOpacity.setEditingSupport( 
					new EditingSupport(colOpacity.getViewer()){

						@Override
						protected CellEditor getCellEditor(Object element) {
							return col.getCellEditor(tblViewer);
						}

						@Override
						protected boolean canEdit(Object element) {
							return element instanceof ColorEntry;
						}

						@Override
						protected Object getValue(Object element) {
							if (col == TableColumn.COLOR){
								Color c = ((ColorEntry)element).getColor();
								return new RGB(c.getRed(), c.getGreen(), c.getBlue());
							}else{
								return col.getValue((ColorEntry)element);
							}
							
						}

						@Override
						protected void setValue(Object element, Object value) {
							col.updateValue((ColorEntry)element, value);
							tblViewer.refresh();
						}});
				
		}
		
		tblViewer.setContentProvider(ArrayContentProvider.getInstance());
		tblViewer.setInput(colors.toArray(new Object[colors.size()]));
		
		createButtonPanel(main);
		return main;
	}
	
	/* 
	 * creates the button panel
	 */
	private void createButtonPanel(Composite parent){
		Composite btnPanel = new Composite(parent, SWT.NONE);
		btnPanel.setLayout(new GridLayout(1, false));
		btnPanel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false,false));
		
		Button btnAdd = new Button(btnPanel, SWT.PUSH);
		btnAdd.setText("Add");
		btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		btnAdd.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorEntry ce = new ColorEntry();
				colors.add(ce);
				updateColors();
				refresh();
				validate();
			}
		});
		
		final Button btnRemove = new Button(btnPanel, SWT.PUSH);
		btnRemove.setText("Remove");
		btnRemove.setEnabled(false);
		btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		tblViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				btnRemove.setEnabled(!tblViewer.getSelection().isEmpty());
			}
		});
		btnRemove.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				for (Iterator<?> iterator = ((IStructuredSelection)tblViewer.getSelection()).iterator(); iterator.hasNext();) {
					Object type = (Object) iterator.next();
					if (type instanceof ColorEntry){
						colors.remove(type);
					}
				}
				updateColors();
				refresh();
				validate();
			}
			
		});
		
		Button btnSort = new Button(btnPanel, SWT.PUSH);
		btnSort.setText("Sort");
		btnSort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		btnSort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sort();
				updateColors();
				refresh();
			}
		});
		
		Button btnAddNoData = new Button(btnPanel, SWT.PUSH);
		btnAddNoData.setText("Add NoData");
		btnAddNoData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		btnAddNoData.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorEntry ce = new ColorEntry(Color.WHITE,0, DEFAULT_NO_DATA, NO_DATA_LABEL);
				colors.add(ce);
				updateColors();
				sort();
				refresh();
			}
		});
	}
	
	/*
	 * Sorts the entries by value.  If this is not
	 * done styling will produce incorrect or error results 
	 */
	protected void sort(){
		Collections.sort(colors, new Comparator<ColorEntry>(){
			@Override
			public int compare(ColorEntry arg0, ColorEntry arg1) {
				return ((Double)arg0.getValue()).compareTo(arg1.getValue());
			}});
	}
	
	/**
	 * @see net.refractions.udig.style.raster.ui.IColorMapTypePanel#getColorMap()
	 */
	@Override
	public ColorMap getColorMap() throws Exception{
		sort();
		refresh();
		
		ColorMapImpl colorMap = new ColorMapImpl();
		colorMap.setType(ColorMapImpl.TYPE_VALUES);

		for (ColorEntry c : colors){
			ColorMapEntryBuilder cme = new ColorMapEntryBuilder();
			ColorMapEntry e = cme.color(c.getColor()).opacity(c.getOpacity()).quantity(c.getValue()).build();
			if (c.getLabel() != null && !c.getLabel().trim().isEmpty()){
				e.setLabel(c.getLabel());
			}
			colorMap.addColorMapEntry(e);
		}
		return colorMap;
		
	}
	

	/**
	 * @see net.refractions.udig.style.raster.ui.IColorMapTypePanel#setColorPalette(org.geotools.brewer.color.BrewerPalette)
	 */
	@Override
	public void setColorPalette(BrewerPalette palette, boolean reverse){
		this.currentPalette = palette;
		this.reverseColors = reverse;
		updateColors();
		refresh();
	}
	
	private void reverse(Color[] cls){
		if (cls == null){
			return ;
		}
		int i = 0; 
		int j = cls.length-1;
		while(i < j){
			Color tmp = cls[i];
			cls[i] = cls[j];
			cls[j] = tmp;
			j--;
			i++;
		}
	}
	
	/**
	 * Updates the colors 
	 */
	private void updateColors(){
		if (currentPalette != null){
			if (currentPalette.getMaxColors() >= colors.size()){
				int i =0;
				Color[] clrs = currentPalette.getColors(colors.size());
				if (reverseColors){
					reverse(clrs);
				}
				for (ColorEntry ce : colors){
					if (ce.getLabel() != null && ce.getLabel().equals(NO_DATA_LABEL)){
						continue;
					}
					ce.setColor( clrs[i++]);
				}
			}else if (currentPalette.getType().isSuitableUnique()){
				Color[] clrs = currentPalette.getColors(currentPalette.getMaxColors());
				if (reverseColors){
					reverse(clrs);
				}
				int i = 0;
				for (ColorEntry ce : colors){
					if (ce.getLabel() != null && ce.getLabel().equals(NO_DATA_LABEL)){
						continue;
					}
					ce.setColor(clrs[i % clrs.length]);
					i++;
				}
			}else{
				Color[] clrs = currentPalette.getColors(currentPalette.getMaxColors());
				if (reverseColors){
					reverse(clrs);
				}
				int i = 0;
				for (ColorEntry ce : colors){
					if (ce.getLabel() != null && ce.getLabel().equals(NO_DATA_LABEL)){
						continue;
					}
					ce.setColor(clrs[i]);
					if (i < clrs.length-1){
						i++;
					}
				}
			}
		}
	}
	
	/*
	 * refresh table viewer
	 */
	protected void refresh(){
		tblViewer.setInput(this.colors.toArray(new Object[this.colors.size()]));
		tblViewer.refresh();
	}
	
	/**
	 * @see net.refractions.udig.style.raster.ui.IColorMapTypePanel#init(org.geotools.styling.ColorMap)
	 */
	@Override
	public void init(ColorMap cm){
		colors.clear();
		ColorMapEntry[] entries = cm.getColorMapEntries();
		for (int i = 0; i < entries.length; i ++){
			ColorEntry ce = new ColorEntry();
			ce.setColor(entries[i].getColor().evaluate(null, Color.class));
			ce.setValue( entries[i].getQuantity().evaluate(null, Double.class));
			ce.setOpacity( entries[i].getOpacity().evaluate(null, Double.class));
			ce.setLabel(entries[i].getLabel());
			colors.add(ce);
		}
		refresh();
		validate();
	}
	
	/**
	 * Sets the unique value entries. This clears
	 * any existing entries, sets the new entries then
	 * recreates the colors based on the 
	 * current selected color map.
	 * <p>The color is NOT kept from the entires in the
	 * newValues collection.
	 * </p>
	 * @param newValues list of new color entries
	 */
	public void setBreaks(List<ColorEntry> newValues){
		colors.clear();
		colors.addAll(newValues);
		updateColors();
		refresh();
		validate();
	}
	
	/*
	 * Validates the current model
	 */
	private void validate(){
		if (colors.size() > 256){
			page.setErrorMessage("A maximum of 256 entries can be supplied");
		}else{
			page.setErrorMessage(null);
		}
	}
	

	@Override
	public String getName() {
		return "Unique Values";
	}

	@Override
	public void computeValues() {
		UniqueValuesDialog dialog = new UniqueValuesDialog(page.getShell(), page.getGridCoverageReader());
		if (dialog.open() == Window.OK){
			dialog.updatePanel(this);
		}
	}

	@Override
	public String getComputeValuesLabel() {
		return "Compute Values...";
	}


	@Override
	public boolean canSupport(int colorMapType) {
		return (colorMapType == ColorMap.TYPE_VALUES);
	}
	
	/**
	 * Label provider for unique values table.
	 * @author Emily
	 *
	 */
	private class TableLabelProvider extends CellLabelProvider  {

		public Image getImage(Object element, int index) {
			if (element instanceof ColorEntry && TableColumn.values()[index] == TableColumn.COLOR){
				return ((ColorEntry)element).getImage();
			}
			return null;
		}

		public String getText(Object element, int index) {
			if (element instanceof ColorEntry){
				return TableColumn.values()[index].getValue((ColorEntry) element);
			}
			return ""; //$NON-NLS-1$
		}

		@Override
		public void update(ViewerCell cell) {
			Object element = cell.getElement();
			cell.setText(getText(element, cell.getColumnIndex()));
			Image image = getImage(element, cell.getColumnIndex());
			cell.setImage(image);			
		}
		
	}

}
