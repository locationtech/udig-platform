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
package org.locationtech.udig.style.sld.raster.editor;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.style.sld.SLDContent;
import org.locationtech.udig.style.sld.raster.SLDRasterPlugin;
import org.locationtech.udig.style.sld.editor.BrewerPaletteLabelProvider;
import org.locationtech.udig.style.sld.editor.CustomDynamicPalette;
import org.locationtech.udig.style.sld.editor.CustomPalettesLoader;
import org.locationtech.udig.style.sld.editor.StyleEditor;
import org.locationtech.udig.style.sld.editor.StyleEditorPage;
import org.locationtech.udig.style.sld.raster.editor.PredefinedColorRules.PredefinedColorRule;
import org.locationtech.udig.style.sld.raster.editor.ValueFormatter.DataType;
import org.locationtech.udig.style.sld.raster.internal.Messages;
import org.locationtech.udig.ui.PlatformGIS;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.geotools.brewer.color.BrewerPalette;
import org.geotools.brewer.color.ColorBrewer;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.ColorMap;
import org.geotools.styling.ColorMapEntry;
import org.geotools.styling.ColorMapEntryImpl;
import org.geotools.styling.ColorMapImpl;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.SLDTransformer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridCoverageReader;

/**
 * Style editor page for single banded rasters
 * that allow users to create styles for
 * each ColorMap type -
 * ColorMap.TYPE_RAMP, 
 * ColorMap.TYPE_INTERVALS, 
 * ColorMap.TYPE_VALUES
 * 
 * @author Emily Gouge (Refractions Research Inc.)
 *
 */
public class SingleBandEditorPage extends StyleEditorPage {
	/**
	 * Maximum number of unique values supported by 
	 * the geotools styling.
	 */
	public static final int MAX_ENTRIES = 256;

	
    private static final String SLD_EXTENSION = ".sld"; //$NON-NLS-1$

    private double[] noDataValues = null;
    
	private StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
	private LabelProvider brewerLblProvider = new BrewerPaletteLabelProvider();
	
    final private IColorMapTypePanel[] stylePanels = new IColorMapTypePanel[]{
    		new UniqueValuesPanel(this),
    		new RampValuesPanel(this),
    		new IntervalValuesPanel(this)
    };

    private String errorMessage = null;
    
	private ComboViewer cmbThemingStyle;
	private ComboViewer cmbPalette;
	private Composite tableComp;
	private ColorBrewer brewer = null;
	private boolean reverseColors = false;
	private ValueFormatter formatter;
	
	private PredefinedColorRules colorRules;
	
	/**
	 * Creates a new style editor page
	 */
	public SingleBandEditorPage() {
		super();
		formatter = new ValueFormatter();
		
	}

	/**
	 * @see org.locationtech.udig.style.sld.IEditorPage#okToLeave()
	 */
	@Override
	public boolean okToLeave() {
		return errorMessage == null;
	}

	@Override
	public boolean performOk() {
		return performApply();
	}

	@Override
	public boolean performApply() {
		try{
			updateStyle();
		}catch (Exception ex){
			SLDRasterPlugin.log("Error applying style.", ex); //$NON-NLS-1$
			MessageDialog.openError(getShell(), Messages.SingleBandEditorPage_Error, Messages.SingleBandEditorPage_ApplyError + ex.getLocalizedMessage());
			return false;
		}
		return true;
	}

	@Override
	public void refresh() {
		init();
	}
	
	
	@Override
	public void createPageContent(Composite parent) {
		
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		main.setLayout(new GridLayout(3, false));

		Label lbl = new Label(main, SWT.NONE);
		lbl.setText(Messages.SingleBandEditorPage_ThemingStyleLabel);
		cmbThemingStyle = new ComboViewer(main, SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbThemingStyle.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 2, 1));
		((GridData)cmbThemingStyle.getControl().getLayoutData()).widthHint = 150;
		cmbThemingStyle.setContentProvider(ArrayContentProvider.getInstance());
		cmbThemingStyle.setInput(stylePanels);
		cmbThemingStyle.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element){
				if (element instanceof IColorMapTypePanel){
					return ((IColorMapTypePanel) element).getName();
				}
				return super.getText(element);
			}
		});
		

		lbl = new Label(main, SWT.NONE);
		lbl.setText(Messages.SingleBandEditorPage_ColorPaletteLabel);
		cmbPalette = createPaletteViewer(main);

		final Button btnTest = new Button(main, SWT.PUSH);
		btnTest.setText(Messages.SingleBandEditorPage_ComputeBreaksButtonText);
		btnTest.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnTest.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				getCurrentSelection().computeValues();
			}
		});
		
		tableComp = new Composite(main, SWT.NONE);
		tableComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		tableComp.setLayout(new StackLayout());
		
		final HashMap<IColorMapTypePanel, Composite> stacks = new HashMap<IColorMapTypePanel, Composite>();
		for (IColorMapTypePanel pnl : stylePanels){
			Composite stack = pnl.createControl(tableComp);
			stacks.put(pnl, stack);
		}
		
		Composite linkPnl = new Composite(main, SWT.NONE);
		linkPnl.setLayout(new GridLayout(4, false));
		linkPnl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		
		
		Link lnk = new Link(linkPnl, SWT.NONE);
		lnk.setText("<a>" + Messages.SingleBandEditorPage_ReverseColorLabel + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
		lnk.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				reverseColors = !reverseColors;
				if (getCurrentSelection() != null){
					Object x = ((IStructuredSelection)cmbPalette.getSelection()).getFirstElement();
					if (x instanceof BrewerPalette){
						BrewerPalette palette = (BrewerPalette) ((IStructuredSelection)cmbPalette.getSelection()).getFirstElement();
						getCurrentSelection().setColorPalette(palette, reverseColors);
					}else if (x instanceof PredefinedColorRule){
						ColorMap cm = ((PredefinedColorRule)x).getColorMap();
						if (cm != null && getCurrentSelection() != null){
							//flip colors
							if (reverseColors){
								ColorMap reverse = new ColorMapImpl();
								for (int i = 0; i < cm.getColorMapEntries().length; i ++){
									ColorMapEntry entry = cm.getColorMapEntries()[i];
									ColorMapEntry clone = new ColorMapEntryImpl();
									clone.setColor(cm.getColorMapEntry(cm.getColorMapEntries().length - 1 - i).getColor());
									clone.setLabel(entry.getLabel());
									clone.setQuantity(entry.getQuantity());
									clone.setOpacity(entry.getOpacity());
									reverse.addColorMapEntry(clone);
									
								}
								cm = reverse;
							}
							
							getCurrentSelection().init(cm);
						}
					}
				}
			}
		});
		
		Label lblSep = new Label(linkPnl, SWT.SEPARATOR | SWT.VERTICAL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.heightHint = 10;
		lblSep.setLayoutData(gd);
		
		lnk = new Link(linkPnl, SWT.NONE);
		lnk.setText("<a>" + Messages.SingleBandEditorPage_FormatExportLink + "</a>");  //$NON-NLS-1$ //$NON-NLS-2$
		lnk.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				getFormat();
			}
		});
		
		Link lnk2 = new Link(linkPnl, SWT.NONE);
		lnk2.setText("<a>" + Messages.SingleBandEditorPage_OneClickExportLink + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
		lnk2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		lnk2.setToolTipText(Messages.SingleBandEditorPage_OneClickTooltip);
		lnk2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				oneClickExport();
			}
		});
		
		
		cmbThemingStyle.getCombo().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IColorMapTypePanel panel = getCurrentSelection();
				if (panel != null){
					((StackLayout)tableComp.getLayout()).topControl = stacks.get(panel);
					tableComp.layout();
					btnTest.setText(panel.getComputeValuesLabel());
					btnTest.getParent().layout();
					

					Object x = ((IStructuredSelection)cmbPalette.getSelection()).getFirstElement();;
					if (x instanceof BrewerPalette){
						BrewerPalette palette = (BrewerPalette) x;
						getCurrentSelection().setInitialColorPalette(palette);
					}else if (x instanceof PredefinedColorRule){
						ColorMap cm = ((PredefinedColorRule)x).getColorMap();
						if (cm != null && getCurrentSelection() != null){
							getCurrentSelection().init(cm);
						}
						
					}
				}
			}
		});
		
		
		cmbPalette.addSelectionChangedListener(new ISelectionChangedListener() {			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object x = ((IStructuredSelection)cmbPalette.getSelection()).getFirstElement();;
				if (x instanceof BrewerPalette){
					BrewerPalette palette = (BrewerPalette) x;
					if (getCurrentSelection() != null){
						getCurrentSelection().setColorPalette(palette, reverseColors);
					}
				}else if (x instanceof PredefinedColorRule){
					ColorMap cm = ((PredefinedColorRule)x).getColorMap();
					if (cm != null && getCurrentSelection() != null){
						getCurrentSelection().init(cm);
					}
				}
				
			}
		});

		cmbThemingStyle.getControl().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (colorRules != null){
					colorRules.dispose();
				}
			}
		});
		
		init();
	}

	private void getFormat(){
		FormatDialog fd = new FormatDialog(getShell(), this.formatter);
		if (fd.open() == FormatDialog.OK){
			if (fd.getSelectedDataType() != null){
				formatter.setDataType(fd.getSelectedDataType());
				formatter.setNumberFormatter(null);
			}else if (fd.getSelectedDataType() == null && fd.getCustom() == null){
				formatter.setDataType(null);
				formatter.setNumberFormatter(null);
			}else{
				formatter.setNumberFormatter(fd.getCustom());
			}
			for (IColorMapTypePanel pnl : stylePanels){
				pnl.refresh();
			}
		}
	}
	
	/*
	 * Performs a single click export which exports
	 * the style to a .sld file beside the associated layer file.
	 * Does nothing for non-file based layers.
	 */
	private void oneClickExport(){
		IGeoResource geoResource = getSelectedLayer().getGeoResource();
        ID id = geoResource.getID();
		if (id.isFile()) {
			try {
				File file = id.toFile();
				SLDTransformer aTransformer = new SLDTransformer();
				aTransformer.setIndentation(StyleEditor.INDENT);
				String xml = aTransformer.transform(getSLD());
				File newFile = new File(file.getParent(), FilenameUtils
						.getBaseName(file.getAbsolutePath())
						+ SLD_EXTENSION);
				FileUtils.writeStringToFile(newFile, xml);
				MessageDialog.openInformation(getShell(), Messages.SingleBandEditorPage_ExportOkDialogTitle, Messages.SingleBandEditorPage_ExportOkDialogMessage);
			} catch (Exception e1) {
				MessageDialog.openError(getShell(), Messages.SingleBandEditorPage_ErrorDialogTitle,
						Messages.SingleBandEditorPage_ErrorMessage);
				e1.printStackTrace();
			}
		} else {
			MessageDialog.openWarning(getShell(), Messages.SingleBandEditorPage_WarningDialogTitle,
					Messages.SingleBandEditorPage_WarningMessage);
		}
	}
	/**
	 * 
	 * @return the current selected color map panel
	 */
	private IColorMapTypePanel getCurrentSelection(){
		return (IColorMapTypePanel) ((IStructuredSelection)cmbThemingStyle.getSelection()).getFirstElement();
	}
	
	
	/**
	 * If you call then function you MUST dispose of the reader
	 * when you are finished with it!
	 * 
	 * @return the grid coverage associated with the current
	 * layer being styled
	 */
	public GridCoverageReader getGridCoverageReader(){	
		try {
			GridCoverageReader reader = getSelectedLayer().getGeoResource().resolve(GridCoverageReader.class, null);
			return reader;
			
		}catch (Exception ex){
			SLDRasterPlugin.log("Error getting grid coverage.", ex); //$NON-NLS-1$
		}
		return null;
	}
	
	public static final void sortEntries(ColorMapEntry[] entries){
		Arrays.sort(entries, new Comparator<ColorMapEntry>(){

			@Override
			public int compare(ColorMapEntry c0, ColorMapEntry c1) {
				Number v1 = (Number) c0.getQuantity().evaluate(null, Double.class);
				Number v2 = (Number) c1.getQuantity().evaluate(null, Double.class);
				return ((Double)v1.doubleValue()).compareTo(v2.doubleValue());
			}});
	}
	
	/**
	 * Updates the style of the current layer
	 * with the style specified the current style
	 * panel.
	 * 
	 * @throws Exception
	 */
	private void updateStyle() throws Exception{
        RasterSymbolizer rasterSym = sf.createRasterSymbolizer();
               
        ColorMap colorMap = getCurrentSelection().getColorMap();
        rasterSym.setColorMap(colorMap);

        Style newStyle = SLD.wrapSymbolizers(rasterSym);
        newStyle.setName(((Layer)getSelectedLayer()).getName() );
        StyleBlackboard styleBlackboard = getSelectedLayer().getStyleBlackboard();

        // put style back on blackboard
        styleBlackboard.put(SLDContent.ID, newStyle);
        styleBlackboard.setSelected(new String[]{SLDContent.ID});
        
        Object x = ((IStructuredSelection)cmbPalette.getSelection()).getFirstElement();
        if (x instanceof BrewerPalette){
        	BrewerPalette palette = (BrewerPalette) x;
        	if (palette != null){
        		styleBlackboard.put("org.locationtech.udig.style.raster.palette", palette.getName()); //$NON-NLS-1$
        	}
        }else if (x instanceof PredefinedColorRule){
        	styleBlackboard.put("org.locationtech.udig.style.raster.palette", ((PredefinedColorRule) x).getName()); //$NON-NLS-1$
        }
	
	}
	
	
	private ComboViewer createPaletteViewer(Composite parent) {
		ComboViewer thiscv = new ComboViewer(parent, SWT.DROP_DOWN
				| SWT.READ_ONLY);
		thiscv.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		((GridData)thiscv.getControl().getLayoutData()).widthHint = 150;
		// list of matching palettes
		 
		
		thiscv.setLabelProvider(new LabelProvider(){
			
			public String getText(Object element){
				if (element instanceof BrewerPalette){
					return brewerLblProvider.getText(element);
				}else if (element instanceof String){
					return (String)element;
				}else if (element instanceof PredefinedColorRule){
					return ((PredefinedColorRule) element).getName();
				}
				return super.getText(element);
			}
		});
		thiscv.setContentProvider(ArrayContentProvider.getInstance());
		
		
		return thiscv;

	}

	private ColorBrewer getBrewer() {
		if (brewer == null) {
			brewer = PlatformGIS.getColorBrewer();

			// add custom palettes
			List<BrewerPalette> palettesList = CustomPalettesLoader.PALETTESLIST;
			for (BrewerPalette brewerPalette : palettesList) {
				brewer.registerPalette(brewerPalette);
			}
			// add a dynamic one that support everything
			CustomDynamicPalette customDynamicPalette = new CustomDynamicPalette(
					CustomDynamicPalette.TABLE.RAINBOW);
			brewer.registerPalette(customDynamicPalette);
			customDynamicPalette = new CustomDynamicPalette(
					CustomDynamicPalette.TABLE.GREY);
			brewer.registerPalette(customDynamicPalette);

		}
		return brewer;
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public boolean performCancel() {
		return false;
	}

	@Override
	public void gotFocus() {
		cmbPalette.getControl().setFocus();
	}

	@Override
	public void styleChanged(Object source) {

	}

	/**
	 * 
	 * @return the nodata values associated with the grid coverate
	 * <code>null</code> if no, no data values found
	 */
	public double[] getNoDataValues(){
		return this.noDataValues;
	}
	
	
	private void init(){
		Layer l = getSelectedLayer();
		
		for (IColorMapTypePanel pnl : stylePanels){
			pnl.setFormatter(this.formatter);
		}
		try{
			GridCoverage coverage = l.getGeoResource().resolve(GridCoverage.class, null);
			
			if (coverage.getNumSampleDimensions() > 0){
				formatter.setRawDataType(coverage.getSampleDimension(0).getSampleDimensionType());
				this.noDataValues = coverage.getSampleDimension(0).getNoDataValues();			
			}
		}catch (Exception ex){
			//eat me 
		}
		if (colorRules != null){
			colorRules.dispose();
			colorRules = null;
		}
		colorRules = new PredefinedColorRules(getGridCoverageReader(), this.noDataValues);
		
		Style style = (Style) l.getStyleBlackboard().get(SLDContent.ID);
		
		//set inputs based on brewer and colorRules
		//do this first so we don't overwrite any custom color rules
		List<Object> inputs = new ArrayList<Object>();
		BrewerPalette[] palettes = getBrewer().getPalettes(ColorBrewer.ALL);
		Arrays.sort(palettes, 0, palettes.length, new Comparator<BrewerPalette>(){
			@Override
			public int compare(BrewerPalette arg0,
					BrewerPalette arg1) {
				return Collator.getInstance().compare(brewerLblProvider.getText(arg0), brewerLblProvider.getText(arg1));
			}});
		for (BrewerPalette p : palettes){
			inputs.add(p);
		}
		inputs.add(Messages.SingleBandEditorPage_predefinedStyleSeperator);
		for (PredefinedColorRule key: colorRules.getPredefinedSets()){
			inputs.add(key);
		}
		cmbPalette.setInput(inputs);
		
		/* reselect based on blackboard setting */
		String paletteName = (String) l.getStyleBlackboard().get("org.locationtech.udig.style.raster.palette"); //$NON-NLS-1$
		if (paletteName != null){
			ColorBrewer cb = getBrewer();
			BrewerPalette palette = cb.getPalette(paletteName);
			if (palette != null){
				cmbPalette.setSelection(new StructuredSelection(cb.getPalette(paletteName)));
			}else{
				PredefinedColorRule cm = colorRules.getPredefinedRule(paletteName);
				if (cm != null){
					cmbPalette.setSelection(new StructuredSelection(cm));
				}
			}
		}
				
		RasterSymbolizer symbolizer = SLD.rasterSymbolizer(style);
		if (symbolizer == null || symbolizer.getColorMap() == null){
			//no symbolizer
			
		}else{
			ColorMap cm = symbolizer.getColorMap();
			for (IColorMapTypePanel pnl : stylePanels){
				if (pnl.canSupport(cm.getType())){
					pnl.init(cm);
					cmbThemingStyle.setSelection(new StructuredSelection(pnl));
					cmbThemingStyle.getControl().notifyListeners(SWT.Selection, new Event());
				}
			}
			if (formatter.getRawDataType() == DataType.INTEGER){
				//if some of the entries are doubles we want to format in double
				//regardless of the fact that the raster is an integer raster.
				try{
					for (ColorMapEntry e : cm.getColorMapEntries()){ 
						Double dvalue = (Double) e.getQuantity().evaluate(null, Double.class);
						Integer ivalue = (Integer) e.getQuantity().evaluate(null, Integer.class);
						if (ivalue.doubleValue() != dvalue){
							formatter.setDataType(DataType.DOUBLE);
						}
					}
				}catch (Exception ex){
					//eatme
				}
			}
			
		}
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String message){
		this.errorMessage = message;
		super.getContainer().updateMessage();
	}
}
