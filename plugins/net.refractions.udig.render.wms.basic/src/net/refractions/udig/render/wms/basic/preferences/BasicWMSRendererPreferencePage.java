package net.refractions.udig.render.wms.basic.preferences;


import net.refractions.udig.render.wms.basic.WMSPlugin;
import net.refractions.udig.render.wms.basic.internal.Messages;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class BasicWMSRendererPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	private ImageTypeListEditor editor;
   private BooleanFieldEditor checkbox;

   /**
	 *
	 */
	public BasicWMSRendererPreferencePage() {
		super(GRID);
		setPreferenceStore(WMSPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.BasicWMSRendererPreferencePage_warning);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	@Override
   public void createFieldEditors() {
      checkbox = new BooleanFieldEditor(PreferenceConstants.P_USE_DEFAULT_ORDER,
                     Messages.BasicWMSRendererPreferencePage_useDefaults,
                     getFieldEditorParent());
//      checkbox.setPropertyChangeListener(this);
      addField( checkbox );
      editor = new ImageTypeListEditor(PreferenceConstants.P_IMAGE_TYPE_ORDER,
                     Messages.BasicWMSRendererPreferencePage_setOrder,
                     getFieldEditorParent());
      editor.setEnabled(!getPreferenceStore().getBoolean(PreferenceConstants.P_USE_DEFAULT_ORDER),
                     getFieldEditorParent());
      addField( editor );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
      // nothing to do here
	}

   @Override
   public void propertyChange(PropertyChangeEvent event){
      super.propertyChange(event);
      if(event.getSource().equals(checkbox)){
         boolean useDefault = ((Boolean)event.getNewValue()).booleanValue();
         editor.setEnabled(!useDefault, getFieldEditorParent());
      }
   }

   protected class ImageTypeListEditor extends ListEditor {
      protected ImageTypeListEditor( String name, String labelText, Composite parent ){
         super(name, labelText, parent);
      }

      @Override
      protected String createList(String[] items){
         StringBuilder stringList = new StringBuilder();
         for(String str : items){
            if(stringList.length() > 0){
               stringList.append(',');
            }
            stringList.append(str);

         }
         return stringList.toString();
      }

      @Override
      protected String getNewInputObject(){
//         String str = new String("image/");
//         InputDialog dialog = new InputDialog(
//                        Display.getCurrent().getActiveShell(),
//                        "New Image Type",
//                        "Enter the image type",
//                        str,
//                        null
//         );
//         dialog.open();
//         if(dialog.getReturnCode() == Window.OK){
//            str = dialog.getValue();
//         }
//         return str;
         return null;
      }

      @Override
      protected String[] parseString(String stringList){
         String[] items = stringList.split(","); //$NON-NLS-1$
         return items;
      }

   }


}
