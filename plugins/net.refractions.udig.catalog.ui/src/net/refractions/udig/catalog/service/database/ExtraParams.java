package net.refractions.udig.catalog.service.database;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.ui.BooleanCellEditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.geotools.data.DataAccessFactory.Param;

/**
 * Extra configuration params for a Connection wizard
 * 
 * @author jeichar
 */
public abstract class ExtraParams {
	public final String name;
	private CellEditor cellEditor;
	public final Param param;
	
	public ExtraParams(String name, Param param) {
		this.name = name;
		this.param = param;
	}
	/**
	 * See {@link EditingSupport#setValue(Object)} 
	 */
	public abstract void setValue(String value);
	/**
	 * See {@link EditingSupport#getValue()} 
	 */
	public abstract String getValue();


	/**
	 * Obtains the celleditor that was set by setCellEditor.
	 * @return
	 */
	public final CellEditor getCellEditor() {
		return cellEditor;
	}
	/**
	 * Called by the framework.  cellEditor should only be set by framework.
	 * @param cellEditor
	 */
	public final void setCellEditor(CellEditor cellEditor) {
		this.cellEditor = cellEditor;
	}
	/**
	 * Converts the value from the CellEditor to the needed type as required by the DatastoreFactory
	 * 
	 *  Default implementation attempts to convert it to the required value as dictated by Param otherwise exception.
	 *  
	 *  Basic supported versions are Numbers, String, Character, URL, URI and Boolean
	 *  
	 * @param cellEditorValue non-null value obtained from the CellEditor
	 * 
	 * @return the value consumable by the Datastore
	 * @throws IllegalStateException if param.type is not one of the "known" types.  
	 * 							     If this is the case this method must be overridden in the subclass
	 */
	public Serializable convertValue(Object cellEditorValue) {
		String string = cellEditorValue.toString();
		if(param.type == String.class) return string;
		if(param.type == Integer.class) return Integer.parseInt(string);
		if(param.type == Double.class) return Double.parseDouble(string);
		if(param.type == Float.class) return Float.parseFloat(string);
		if(param.type == Byte.class) return Byte.parseByte(string);
		if(param.type == Character.class) return string.charAt(0);
		if(param.type == Boolean.class) return Boolean.parseBoolean(string);
		try {
			if(param.type == URL.class)return new URL(string);
			if(param.type == URI.class) return new URI(string);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new IllegalStateException(param.type+" is not one of the types that are handled by convertValue().  Method must be overridden; Param:"+param);
	}
	/**
	 * Create a cell editor for editing the value of this parameter.
	 */
	public abstract CellEditor createCellEditor(Composite parent);
	
	/**
	 * Create a text based Extra param
	 * 
	 * @param name name to display
	 * @param param the actual param 
	 * @param defaultValue the default value to set if null then the text is blank
	 */
	public static ExtraParams text(String name,Param param, final String defaultValue) {
		return new ExtraParams(name,param) {
			
			@Override
			public CellEditor createCellEditor(Composite parent) {
				TextCellEditor textCellEditor = new TextCellEditor(parent);
				textCellEditor.setValue(defaultValue==null?"":defaultValue);
				return textCellEditor;
			}

			@Override
			public void setValue(String value) {
				getCellEditor().setValue(value.toString());
			}

			@Override
			public String getValue() {
				return getCellEditor().getValue().toString();
			}
		};
	}
	
	/**
	 * Create a combo based Extra param
	 * 
	 * @param name name to display
	 * @param param the actual param 
	 * @param defaultValue the default value to set if null then the first option is set
	 * @param options the list of options to put in the combo
	 */
	public static ExtraParams combo(String name, Param param, final String defaultValue,final String... options) {
		int current = 0;
		if(defaultValue!=null) {
			for (String option : options) {
				if(option.equals(defaultValue)) {
					break;
				}
				current ++;
			}
			
			if(current >= options.length) {
				CatalogUIPlugin.log("ERROR:  "+defaultValue+" is not one of the legal options", new Exception());
				current = 0;
			}
		}
		
		final int choice = current;
		return new ExtraParams(name,param) {
			
			@Override
			public CellEditor createCellEditor(Composite parent) {
				ComboBoxCellEditor comboBoxCellEditor = new ComboBoxCellEditor(parent, options);
				comboBoxCellEditor.setValue(choice);
				return comboBoxCellEditor;
			}
			
			@Override
			public Serializable convertValue(Object cellEditorValue) {
				ComboBoxCellEditor editor = (ComboBoxCellEditor)getCellEditor();
				String value = editor.getItems()[(Integer) cellEditorValue];
				return super.convertValue(value);
			}

			@Override
			public void setValue(String value) {
				int index = 0; 
				for (String option : options) {
					if(option.equals(value)) {
						getCellEditor().setValue(index);
						return;
					}
					index++;
				}
				throw new IllegalArgumentException(value+" is not one of the options in the combo:"+(Arrays.toString(options)));
			}

			@Override
			public String getValue() {
				ComboBoxCellEditor editor = (ComboBoxCellEditor)getCellEditor();
				int cellEditorValue = (Integer) editor.getValue();
				String value = editor.getItems()[cellEditorValue];
				return value;
			}
		};
	}
	/**
	 * Create a boolean based Extra param
	 * 
	 * @param name name to display
	 * @param param the actual param 
	 * @param defaultValue the default value to set if null then the text is blank
	 */
	public static ExtraParams bool(String name,Param param, final boolean initialValue) {
		return new ExtraParams(name,param) {
			
			@Override
			public CellEditor createCellEditor(Composite parent) {
				BooleanCellEditor booleanCellEditor = new BooleanCellEditor(parent);
				booleanCellEditor.setValue(initialValue);
				return booleanCellEditor;
			}

			@Override
			public void setValue(String value) {
				getCellEditor().setValue(Boolean.parseBoolean(value.toString()));
			}

			@Override
			public String getValue() {
				return getCellEditor().getValue().toString();
			}
		};
	}

}
