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
package net.refractions.udig.project.ui.tileset;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import net.refractions.udig.project.internal.render.impl.ScaleUtils;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;
import net.refractions.udig.project.ui.preferences.PreferenceConstants;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * A control that create a TileSet definition list
 * 
 * @author jhudson
 * @since 1.2.0
 */
public class TileSetControl extends FieldEditorPreferencePage {

    private String resourceName;
    private ReferencedEnvelope resourceBounds;
    private ZoomItemListEditor editor;
    private SizeStringFieldEditor width;
    private SizeStringFieldEditor height;
    private ImageTypeFieldEditor imageType;
    private BooleanFieldEditor checkbox;

    protected final Integer DEFAULT_TILE_SIZE = 256;
    protected final String DEFAULT_IMAGE_TYPE = "image/png"; //$NON-NLS-1$

    public TileSetControl( String resourceName, ReferencedEnvelope bounds ) {
        super(GRID);
        setPreferenceStore(ProjectUIPlugin.getDefault().getPreferenceStore());
        setDescription(Messages.TileSet_dialog_description);
        this.resourceName = resourceName;
        this.resourceBounds = bounds;
    }

    @Override
    protected void createFieldEditors() {
        checkbox = new BooleanFieldEditor(PreferenceConstants.P_TILESET_ON_OFF + resourceName,
                Messages.TileSet_dialog_onoff_desc, getFieldEditorParent());
        width = new SizeStringFieldEditor(PreferenceConstants.P_TILESET_WIDTH + resourceName,
                Messages.TileSet_dialog_width, getFieldEditorParent());
        height = new SizeStringFieldEditor(PreferenceConstants.P_TILESET_HEIGHT + resourceName,
                Messages.TileSet_dialog_heigth, getFieldEditorParent());
        imageType =  new ImageTypeFieldEditor(PreferenceConstants.P_TILESET_IMAGE_TYPE + resourceName,
                Messages.TileSet_dialog_image_type, getFieldEditorParent());
        editor = new ZoomItemListEditor(PreferenceConstants.P_TILESET_RESOLUTIONS + resourceName,
                Messages.TileSet_dialog_zoom_desc, getFieldEditorParent());

        boolean enabled = getPreferenceStore().getBoolean(
                PreferenceConstants.P_TILESET_ON_OFF + resourceName);

        width.setEnabled(enabled, getFieldEditorParent());
        height.setEnabled(enabled, getFieldEditorParent());
        editor.setEnabled(enabled, getFieldEditorParent());
        imageType.setEnabled(enabled, getFieldEditorParent());
        
        addField(checkbox);
        addField(width);
        addField(height);
        addField(imageType);
        addField(editor);
    }

    @Override
    public void propertyChange( PropertyChangeEvent event ) {
        super.propertyChange(event);
        if (event.getSource().equals(checkbox)) {
            boolean useDefault = ((Boolean) event.getNewValue()).booleanValue();
            editor.setEnabled(useDefault, getFieldEditorParent());
            width.setEnabled(useDefault, getFieldEditorParent());
            imageType.setEnabled(useDefault, getFieldEditorParent());
            height.setEnabled(useDefault, getFieldEditorParent());
        }
    }

    /**
     * Load the default settings for this properties dialog
     */
    public void loadDefaults() {
        width.doLoadDefault();
        height.doLoadDefault();
        imageType.doLoadDefault();
        editor.doLoadDefault();
    }

    /**
     * Object to handle new items in the list - sets default values to calculated resolutions based
     * on current viewport scales
     * 
     * @author jhudson
     * @since 1.2.0
     */
    protected class ZoomItemListEditor extends ListEditor {
        protected ZoomItemListEditor( String name, String labelText, Composite parent ) {
            super(name, labelText, parent);
            getUpButton().setVisible(false);
            getDownButton().setVisible(false);
        }

        @Override
        protected String getNewInputObject() {
            String str = null;
            InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(),
                    Messages.TileSet_dialog_new_title, Messages.TileSet_dialog_new_desc, str, null);
            int result = dialog.open();
            if (result == Window.OK) {
                str = dialog.getValue();
            }
            if (str == null || "".equals(str)) { //$NON-NLS-1$
                return null; // nothing to add
            }

            try {
                Integer.parseInt(str);
            } catch (NumberFormatException nfe) {
                return null; // nothing to add
            }

            if (inList(str)) {
                return null;
            }

            return str;
        }

        /**
         * Calculate the resolutions as might be found in the header of a WMS-C capabilities
         * document. Uses the viewport scales for default calculations.
         */
        @Override
        protected void doLoadDefault() {
            getList().removeAll();
            SortedSet<Double> defaultScales = ApplicationGIS.getActiveMap().getViewportModel()
                    .getDefaultPreferredScaleDenominators();

            Double[] scales = new Double[defaultScales.size()];
            defaultScales.toArray(scales);
            for( int i = scales.length - 1; i >= 0; i-- ) {
                Double scale = scales[i];
       
                Double calculatedScale = ScaleUtils.calculateResolutionFromScale(resourceBounds,scale,width.getSize());
                
                System.out.println(scale + " - " + calculatedScale);
                
                getList().add(calculatedScale.toString());
            }

            setEnabled(false, getFieldEditorParent());
        }
        /**
         * Check if this zoom level has already been added
         * 
         * @param str
         * @return true if its already in the list
         */
        private boolean inList( final String str ) {
            List<String> list = Arrays.asList(getList().getItems());
            return list.contains(str);
        }

        @Override
        protected String[] parseString( String stringList ) {
            String[] items = stringList.split(" "); //$NON-NLS-1$
            return items;
        }
        @Override
        protected String createList( String[] items ) {
            StringBuilder stringList = new StringBuilder();
            for( String str : items ) {
                if (stringList.length() > 0) {
                    stringList.append(" "); //$NON-NLS-1$
                }
                stringList.append(str);
            }
            return stringList.toString();
        }
    }

    /**
     * Object to represent tile-size input fields - defaults to 265
     * 
     * @author jhudson
     * @since 1.2.0
     */
    private class SizeStringFieldEditor extends StringFieldEditor {
        public SizeStringFieldEditor( String name, String labelText, Composite parent ) {
            super(name, labelText, parent);
            getTextControl().setText(DEFAULT_TILE_SIZE.toString());
        }

        @Override
        protected void doLoadDefault() {
            getTextControl().setText(DEFAULT_TILE_SIZE.toString());
            setEnabled(false, getFieldEditorParent());
        }

        public int getSize() {
            int retInt = DEFAULT_TILE_SIZE;
            try {
                Integer.parseInt(getStringValue());
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
            return retInt;
        }
    }
    
    /**
     * Object to represent tile-size input fields - defaults to 265
     * 
     * @author jhudson
     * @since 1.2.0
     */
    private class ImageTypeFieldEditor extends StringFieldEditor {
        public ImageTypeFieldEditor( String name, String labelText, Composite parent ) {
            super(name, labelText, parent);
            getTextControl().setText(DEFAULT_IMAGE_TYPE);
        }

        @Override
        protected void doLoadDefault() {
            getTextControl().setText(DEFAULT_IMAGE_TYPE);
            setEnabled(false, getFieldEditorParent());
        }
    }
}
