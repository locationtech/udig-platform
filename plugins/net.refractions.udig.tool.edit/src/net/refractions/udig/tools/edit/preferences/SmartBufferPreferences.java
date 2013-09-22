/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
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
package net.refractions.udig.tools.edit.preferences;

import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.ui.FeatureEditorFieldEditor;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditPlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.vividsolutions.jts.operation.buffer.BufferOp;

/**
 * Preferences page for the Smart Buffer Tool
 * 
 * @author leviputna
 * @since 1.2.0
 */
public class SmartBufferPreferences extends FieldEditorPreferencePage
implements
IWorkbenchPreferencePage {
    
    private final Integer ROUND = BufferOp.CAP_ROUND;
    private final Integer BUTT = BufferOp.CAP_BUTT;
    private final Integer SQUARE = BufferOp.CAP_SQUARE;
    private IntegerFieldEditor bufferSegmentsFieldEditor;
    private IntegerFieldEditor bufferDefultSizeFieldEditor;
    private IntegerFieldEditor bufferMaxSizeFieldEditor;
    
    public SmartBufferPreferences(  ) {
        super(GRID);
    }

    @Override
    public void init(IWorkbench workbench) {
        IPreferenceStore store = EditPlugin.getDefault().getPreferenceStore();
        setPreferenceStore(store);
        setDescription(Messages.PerformancelPreferences_description);
    }

    @Override
    protected void createFieldEditors() {
        
        bufferSegmentsFieldEditor = new IntegerFieldEditor(PreferenceConstants.P_BUFFER_SEGMENTS,
                "Number of segments",
                getFieldEditorParent());
        addField(bufferSegmentsFieldEditor);
        
        
        bufferDefultSizeFieldEditor = new IntegerFieldEditor(PreferenceConstants.P_BUFFER_DEFULT_SIZE,
                "Defult Buffer Size (px)",
                getFieldEditorParent());
        addField(bufferDefultSizeFieldEditor);
        
        bufferMaxSizeFieldEditor = new IntegerFieldEditor(PreferenceConstants.P_BUFFER_MAX_SIZE,
                "Max Size (px)",
                getFieldEditorParent());
        addField(bufferMaxSizeFieldEditor);

        String[][] labelsAndValues = new String[3][2];
        labelsAndValues[0][0] = "Round - end caps are rounded off at the buffer distance beyond the line ends";
        labelsAndValues[0][1] = ROUND.toString();
        
        labelsAndValues[1][0] = "Square - end caps are squared off at the buffer distance beyond the line ends";
        labelsAndValues[1][1] = SQUARE.toString();
        
        labelsAndValues[2][0] = "Butt - end caps are truncated flat at the line ends";
        labelsAndValues[2][1] = BUTT.toString();
        
        addField(new ComboFieldEditor(PreferenceConstants.P_BUFFER_CAP_TYPE,
                "End Cap Type",
                labelsAndValues,
                getFieldEditorParent() ));
        
    }
    
    protected void checkState() {
        super.checkState();
        
        if(!bufferSegmentsFieldEditor.VALUE.isEmpty() && bufferSegmentsFieldEditor.getIntValue() > 0 && bufferSegmentsFieldEditor.getIntValue() < 40){
                  setErrorMessage(null);
              setValid(true);
        }else{
              setErrorMessage("Number of segments must be greater than 0 and less than 40");
              setValid(false);
        }
        
        if(!bufferDefultSizeFieldEditor.VALUE.isEmpty() && bufferDefultSizeFieldEditor.getIntValue() > 0 && bufferDefultSizeFieldEditor.getIntValue() < 100){
            setErrorMessage(null);
            setValid(true);
        } else {
            setErrorMessage("Defult Buffer Size must be greater than 0 and less than 100");
            setValid(false);
        }
        
        if(!bufferMaxSizeFieldEditor.VALUE.isEmpty() && bufferMaxSizeFieldEditor.getIntValue() > bufferDefultSizeFieldEditor.getIntValue() && bufferMaxSizeFieldEditor.getIntValue() < 1000){
            setErrorMessage(null);
            setValid(true);
        } else {
            setErrorMessage("Max Buffer Size must be greater than Defult Buffer Size and less than 1000");
            setValid(false);
        }
        
    }
    
    public void propertyChange(PropertyChangeEvent event) {
        super.propertyChange(event);
        
        if (event.getProperty().equals(bufferSegmentsFieldEditor.VALUE)) {
                  checkState();
        }
        
        if (event.getProperty().equals(bufferDefultSizeFieldEditor.VALUE)) {
            checkState();
        } 
        
        if (event.getProperty().equals(bufferMaxSizeFieldEditor.VALUE)) {
            checkState();
        } 
        
        
    }

   
}
