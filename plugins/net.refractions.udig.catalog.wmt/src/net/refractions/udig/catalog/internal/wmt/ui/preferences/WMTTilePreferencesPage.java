package net.refractions.udig.catalog.internal.wmt.ui.preferences;

import net.refractions.udig.catalog.internal.wmt.WMTPlugin;
import net.refractions.udig.catalog.wmt.internal.Messages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preferences page for WMT Tile settings
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class WMTTilePreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private Scale sclScaleFactor;
    private Label lblValue;
	
    public WMTTilePreferencesPage() {
        super(FieldEditorPreferencePage.FLAT); 
        setPreferenceStore(WMTPlugin.getDefault().getPreferenceStore());
    }

    /**
     * Displays the currently selected value of the scale component.
     */
    private void updateScaleFactorValue() {
        lblValue.setText(Integer.toString(sclScaleFactor.getSelection()));
    }
    
	@Override
	protected void createFieldEditors() {
	    getFieldEditorParent().setLayout(new RowLayout(SWT.VERTICAL));	    
	    	    
	    //region Scale-Factor
        Group grpScaleFactor = new Group(getFieldEditorParent(), SWT.NONE);
        grpScaleFactor.setText(Messages.Preferences_ScaleFactor_Title);
        
        grpScaleFactor.setLayout(new RowLayout(SWT.VERTICAL));
        grpScaleFactor.setLayoutData(new RowData(300, 20));
        
        //region Scale-Component Description
        Composite cScaleFactorDescription = new Composite(grpScaleFactor, SWT.NONE);
        cScaleFactorDescription.setLayoutData(new RowData(400, 25));
        
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        gridLayout.makeColumnsEqualWidth = false;        
        
        cScaleFactorDescription.setLayout(gridLayout);        
        
        Label lblFast = new Label (cScaleFactorDescription, SWT.HORIZONTAL);
        lblFast.setText(Messages.Preferences_ScaleFactor_FastRendering);
        lblFast.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));
        
        
        lblValue = new Label (cScaleFactorDescription, SWT.HORIZONTAL);
        lblValue.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL));
        
        Label lblQuality = new Label (cScaleFactorDescription, SWT.HORIZONTAL | GridData.FILL_HORIZONTAL);
        lblQuality.setText(Messages.Preferences_ScaleFactor_HighestQuality);
        lblQuality.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END));
        //endregion
        
        //region Scale-Component
        ScaleFieldEditor scaleFactorFieldEditor = new ScaleFieldEditor(
                WMTPreferenceConstants.P_WMT_SCALEFACTOR,
                "", //$NON-NLS-1$
                grpScaleFactor,
                1,
                100,
                1,
                5
        );
        scaleFactorFieldEditor.getLabelControl(grpScaleFactor).setLayoutData(new RowData());
        addField(scaleFactorFieldEditor);
        
        
        sclScaleFactor = scaleFactorFieldEditor.getScaleControl();
        sclScaleFactor.setSize(400, 30);
        sclScaleFactor.setMinimum(0);
        sclScaleFactor.setMaximum(100);
        sclScaleFactor.setIncrement(1);
        sclScaleFactor.setPageIncrement(10);
        sclScaleFactor.setSelection(50);
        sclScaleFactor.setLayoutData(new RowData(400, 30));
        sclScaleFactor.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent arg0) {}

            public void widgetSelected(SelectionEvent event) {
                updateScaleFactorValue();
            }            
        });
        
        sclScaleFactor.addListener(SWT.Paint, new Listener() {
            /**
             * When the scale-component is painted for the first time,
             * also update the display of the scale-factor.
             */
            public void handleEvent( Event arg0 ) {
                updateScaleFactorValue();
                sclScaleFactor.removeListener(SWT.Paint, this);
            }
            
        });
        //endregion

        //region Scale-Component Label
        Composite cScaleFactorDescriptionValues = new Composite(grpScaleFactor, SWT.NONE);
        cScaleFactorDescriptionValues.setLayoutData(new RowData(400, 30));        
        
        GridLayout gridLayoutTwo = new GridLayout();
        gridLayoutTwo.numColumns = 2;
        gridLayoutTwo.makeColumnsEqualWidth = false;    
        
        cScaleFactorDescriptionValues.setLayout(gridLayoutTwo);        
        
        Label lblValue0 = new Label (cScaleFactorDescriptionValues, SWT.HORIZONTAL);
        lblValue0.setText("0"); //$NON-NLS-1$
        lblValue0.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));
        
                   
        Label lblValue100 = new Label (cScaleFactorDescriptionValues, SWT.HORIZONTAL | GridData.FILL_HORIZONTAL);
        lblValue100.setText("100"); //$NON-NLS-1$
        lblValue100.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END));
        

        Label lblScaleFactorExpl = new Label (grpScaleFactor, SWT.HORIZONTAL | SWT.WRAP);
        lblScaleFactorExpl.setText(Messages.Preferences_ScaleFactor_Description);
        lblScaleFactorExpl.setLayoutData(new RowData(400, 50));
        //endregion
        //endregion
        
        //region Tile-Limit
        Group grpTileLimit = new Group(getFieldEditorParent(), SWT.NONE);
        grpTileLimit.setLayout(new RowLayout(SWT.VERTICAL));
        grpTileLimit.setText(Messages.Preferences_TileLimit_Title);
	    
        Composite limitFields = new Composite(grpTileLimit, SWT.NONE);
        
        IntegerFieldEditor warningFieldEditor = new IntegerFieldEditor(
                WMTPreferenceConstants.P_WMT_TILELIMIT_WARNING,
                Messages.Preferences_TileLimit_Warning,
                limitFields                
        );
        addField(warningFieldEditor);

        IntegerFieldEditor errorFieldEditor = new IntegerFieldEditor(
                WMTPreferenceConstants.P_WMT_TILELIMIT_ERROR,
                Messages.Preferences_TileLimit_Error,
                limitFields                
        );
        addField(errorFieldEditor);
        

        Label lblTileLimitExpl = new Label (grpTileLimit, SWT.HORIZONTAL | SWT.WRAP);
        lblTileLimitExpl.setText(Messages.Preferences_TileLimit_Description);
        lblTileLimitExpl.setLayoutData(new RowData(400, 90));
        //endregion

        //region Reset LayoutManagers (they are set to GridLayout when the field is added)
        grpScaleFactor.setLayout(new RowLayout(SWT.VERTICAL));
        cScaleFactorDescription.setLayout(gridLayout);      
        cScaleFactorDescriptionValues.setLayout(gridLayoutTwo);     
        grpScaleFactor.pack();
        grpTileLimit.pack();
        //endregion
	}
	
	@Override
    protected void performDefaults() {
        super.performDefaults();
        updateScaleFactorValue();
    }

    /**
	 * Update the display of the scale-factor, when the scale-component changes
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		
		if (event.getSource() == sclScaleFactor)
		    updateScaleFactorValue();
	}

    public void init(IWorkbench arg0) {}
}