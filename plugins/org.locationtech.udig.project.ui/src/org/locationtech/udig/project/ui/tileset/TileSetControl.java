/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.tileset;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.preferences.PreferenceConstants;

/**
 * A control that create a TileSet definition list
 * 
 * @author jhudson
 * @since 1.2.0
 */
public class TileSetControl extends FieldEditorPreferencePage {

    // private String resourceName;
    private ZoomItemListEditor editor;
    private SizeStringFieldEditor width;
    private SizeStringFieldEditor height;
    private ImageTypeFieldEditor imageType;
    private TileSetBooleanFieldEditor checkbox;
    private IGeoResource resource;

    public TileSetControl( final IGeoResource resource ) {
        super(GRID);
        setDescription(Messages.TileSet_dialog_description);
        noDefaultAndApplyButton();
        this.resource = resource;
    }

    @Override
    protected void createFieldEditors() {
        Map<String, Serializable> props = resource.getPersistentProperties();

        String heightValue = (String) props.get(PreferenceConstants.P_TILESET_HEIGHT);
        if (heightValue == null) {
            heightValue = PreferenceConstants.DEFAULT_TILE_SIZE.toString();
        }

        String widthValue = (String) props.get(PreferenceConstants.P_TILESET_WIDTH);
        if (widthValue == null) {
            widthValue = PreferenceConstants.DEFAULT_TILE_SIZE.toString();
        }

        String imageTypeValue = (String) props.get(PreferenceConstants.P_TILESET_IMAGE_TYPE);

        if (imageTypeValue == null) {
            imageTypeValue = PreferenceConstants.DEFAULT_IMAGE_TYPE;
        }

        Boolean enabled = (Boolean) props.get(PreferenceConstants.P_TILESET_ON_OFF);

        if (enabled == null) {
            enabled = true;
        }

        String scales = (String) props.get(PreferenceConstants.P_TILESET_SCALES);

        if (scales == null) {
            scales = ""; //$NON-NLS-1$
        }

        checkbox = new TileSetBooleanFieldEditor(PreferenceConstants.P_TILESET_ON_OFF,
                Messages.TileSet_dialog_onoff_desc, enabled, getFieldEditorParent());
        width = new SizeStringFieldEditor(PreferenceConstants.P_TILESET_WIDTH,
                Messages.TileSet_dialog_width, widthValue, getFieldEditorParent());
        height = new SizeStringFieldEditor(PreferenceConstants.P_TILESET_HEIGHT,
                Messages.TileSet_dialog_heigth, heightValue, getFieldEditorParent());
        imageType = new ImageTypeFieldEditor(PreferenceConstants.P_TILESET_IMAGE_TYPE,
                Messages.TileSet_dialog_image_type, imageTypeValue, getFieldEditorParent());
        editor = new ZoomItemListEditor(PreferenceConstants.P_TILESET_SCALES,
                Messages.TileSet_dialog_zoom_desc, scales, getFieldEditorParent());

        width.setEnabled(enabled, getFieldEditorParent());
        height.setEnabled(enabled, getFieldEditorParent());
        editor.setEnabled(enabled, getFieldEditorParent());
        imageType.setEnabled(enabled, getFieldEditorParent());

        if (scales == null || "".equals(scales)) { //$NON-NLS-1$
            loadDefaults();
        }

        addField(checkbox);
        addField(width);
        addField(height);
        addField(imageType);
        addField(editor);
    }

    /**
     * Load the default settings for this properties dialog
     */
    public void loadDefaults() {
        width.doLoadDefault();
        height.doLoadDefault();
        imageType.doLoadDefault();
        editor.doLoadDefault();
        checkbox.doLoadDefault();
    }

    @Override
    public boolean performOk() {
        resource.getPersistentProperties().put(PreferenceConstants.P_TILESET_ON_OFF,
                checkbox.getBooleanValue());
        resource.getPersistentProperties().put(PreferenceConstants.P_TILESET_WIDTH,
                width.getStringValue());
        resource.getPersistentProperties().put(PreferenceConstants.P_TILESET_HEIGHT,
                height.getStringValue());
        resource.getPersistentProperties().put(PreferenceConstants.P_TILESET_IMAGE_TYPE,
                imageType.getStringValue());
        resource.getPersistentProperties().put(PreferenceConstants.P_TILESET_SCALES,
                editor.getAsListString());

        return true;
    }

    /**
     * Object to handle new items in the list - sets default values to calculated resolutions based
     * on current viewport scales
     * 
     * @author jhudson
     * @since 1.2.0
     */
    protected class ZoomItemListEditor extends ListEditor {
        protected ZoomItemListEditor( String name, String labelText, String scales, Composite parent ) {
            super(name, labelText, parent);
            getUpButton().setVisible(false);
            getDownButton().setVisible(false);
            loadScales(parseString(scales));
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
         * Load in an arbitrary string - should be scales
         * 
         * @param scales
         */
        private void loadScales( String[] scales ) {
            for( String scale : scales ) {
                getList().add(scale);
            }
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
                getList().add(scale.toString());
            }

            setEnabled(false, getFieldEditorParent());
        }

        public String getAsListString() {
            return createList(getList().getItems());
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
        public SizeStringFieldEditor( String name, String labelText, String value, Composite parent ) {
            super(name, labelText, parent);
            getTextControl().setText(value);
        }

        @Override
        protected void doLoadDefault() {
            getTextControl().setText(PreferenceConstants.DEFAULT_TILE_SIZE.toString());
            setEnabled(false, getFieldEditorParent());
        }
    }

    /**
     * Object to represent tile-size input fields - defaults to 256
     * 
     * @author jhudson
     * @since 1.2.0
     */
    private class ImageTypeFieldEditor extends StringFieldEditor {
        public ImageTypeFieldEditor( String name, String labelText, String value, Composite parent ) {
            super(name, labelText, parent);
            getTextControl().setText(value);
        }

        @Override
        protected void doLoadDefault() {
            getTextControl().setText(PreferenceConstants.DEFAULT_IMAGE_TYPE);
            setEnabled(false, getFieldEditorParent());
        }
    }

    private class TileSetBooleanFieldEditor extends BooleanFieldEditor {
        public TileSetBooleanFieldEditor( String pTilesetOnOff, String tileSet_dialog_onoff_desc,
                boolean value, Composite fieldEditorParent ) {
            super(pTilesetOnOff, tileSet_dialog_onoff_desc, fieldEditorParent);
            getChangeControl(getFieldEditorParent()).setSelection(value);
            this.getChangeControl(getFieldEditorParent()).addSelectionListener(
                    new SelectionListener(){
                        public void widgetSelected( SelectionEvent event ) {
                            editor.setEnabled(getBooleanValue(), getFieldEditorParent());
                            width.setEnabled(getBooleanValue(), getFieldEditorParent());
                            imageType.setEnabled(getBooleanValue(), getFieldEditorParent());
                            height.setEnabled(getBooleanValue(), getFieldEditorParent());
                        }
                        public void widgetDefaultSelected( SelectionEvent event ) {}
                    });
        }

        @Override
        protected void doLoadDefault() {
            setPresentsDefaultValue(true);
            getChangeControl(getFieldEditorParent()).setSelection(false);
        }
    }
}
