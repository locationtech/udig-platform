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

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.style.raster.Activator;
import net.refractions.udig.style.raster.internal.Messages;
import net.refractions.udig.style.sld.SLDContent;
import net.refractions.udig.style.sld.editor.CustomDynamicPalette;
import net.refractions.udig.style.sld.editor.CustomPalettesLoader;
import net.refractions.udig.style.sld.editor.StyleEditor;
import net.refractions.udig.style.sld.editor.StyleEditorPage;
import net.refractions.udig.style.sld.editor.internal.BrewerPaletteLabelProvider;
import net.refractions.udig.ui.PlatformGIS;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
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
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.SLDTransformer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
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

    private static final String SLD_EXTENSION = ".sld"; //$NON-NLS-1$

	private StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
    
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

	/**
	 * Creates a new style editor page
	 */
	public SingleBandEditorPage() {
		super();
	}

	/**
	 * @see net.refractions.udig.style.sld.IEditorPage#okToLeave()
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
			Activator.log("Error applying style.", ex); //$NON-NLS-1$
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
		
		Link lnk = new Link(main, SWT.NONE);
		lnk.setText("<a>" + Messages.SingleBandEditorPage_ReverseColorLabel + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
		lnk.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				reverseColors = !reverseColors;
				if (getCurrentSelection() != null){
					BrewerPalette palette = (BrewerPalette) ((IStructuredSelection)cmbPalette.getSelection()).getFirstElement();
					getCurrentSelection().setColorPalette(palette, reverseColors);
				}
			}
		});
		
		
		Link lnk2 = new Link(main, SWT.NONE);
		lnk2.setText("<a>OneClick Export</a>"); //$NON-NLS-1$ //$NON-NLS-2$
		lnk2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false,2,1));
		lnk2.setToolTipText("One click style export for file based layers.");
		lnk2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
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
						MessageDialog.openInformation(getShell(), "Export Successful", "Export successful.");
					} catch (Exception e1) {
						MessageDialog.openError(getShell(), "Error",
								"Error saving style to file.");
						e1.printStackTrace();
					}
				} else {
					MessageDialog.openWarning(getShell(), "Warning",
							"The selected layer is not file based.");
				}
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
				}
			}
		});
		
		
		cmbPalette.addSelectionChangedListener(new ISelectionChangedListener() {			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				BrewerPalette palette = (BrewerPalette) ((IStructuredSelection)cmbPalette.getSelection()).getFirstElement();
				if (getCurrentSelection() != null){
					getCurrentSelection().setColorPalette(palette, reverseColors);
				}
			}
		});

		init();
	}

	/**
	 * 
	 * @return the current selected color map panel
	 */
	private IColorMapTypePanel getCurrentSelection(){
		return (IColorMapTypePanel) ((IStructuredSelection)cmbThemingStyle.getSelection()).getFirstElement();
	}
	
	/**
	 * 
	 * @return the grid coverage associated with the current
	 * layer being styled
	 */
	public GridCoverageReader getGridCoverageReader(){	
		try {
			GridCoverageReader reader = getSelectedLayer().getGeoResource().resolve(GridCoverageReader.class, null);
			//TODO: we need to make sure this reader is disposed of
			return reader;
			
		}catch (Exception ex){
			Activator.log("Error getting grid coverage.", ex); //$NON-NLS-1$
		}
		return null;
	}
	
	public static final void sortEntries(ColorMapEntry[] entries){
		Arrays.sort(entries, new Comparator<ColorMapEntry>(){

			@Override
			public int compare(ColorMapEntry c0, ColorMapEntry c1) {
				Double v1 = (Double) c0.getQuantity().evaluate(null);
				Double v2 = (Double) c1.getQuantity().evaluate(null);
				return v1.compareTo(v2);
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
        
        BrewerPalette palette = (BrewerPalette) ((IStructuredSelection)cmbPalette.getSelection()).getFirstElement();
        if (palette != null){
        	styleBlackboard.put("net.refractions.udig.style.raster.palette", palette.getName()); //$NON-NLS-1$
        }
	}
	
	
	private ComboViewer createPaletteViewer(Composite parent) {
		ComboViewer thiscv = new ComboViewer(parent, SWT.DROP_DOWN
				| SWT.READ_ONLY);
		thiscv.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		((GridData)thiscv.getControl().getLayoutData()).widthHint = 150;
		// list of matching palettes
		final LabelProvider provider = new BrewerPaletteLabelProvider(); 
		thiscv.setLabelProvider(provider);
		thiscv.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof ArrayList) {
					@SuppressWarnings("unchecked")
					ArrayList<Object> list = (ArrayList<Object>) inputElement;
					return list.toArray();
				} else {
					if (inputElement instanceof ColorBrewer) {
						ColorBrewer brewer = (ColorBrewer) inputElement;
						BrewerPalette[] palettes = brewer
								.getPalettes(ColorBrewer.ALL);
						
						Arrays.sort(palettes, 0, palettes.length, new Comparator<BrewerPalette>(){
							@Override
							public int compare(BrewerPalette arg0,
									BrewerPalette arg1) {
								return Collator.getInstance().compare(provider.getText(arg0), provider.getText(arg1));
							}});
						return palettes;
					}
					return null;
				}
			}
		});

		thiscv.setInput(getBrewer());
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

	private void init(){
		Layer l = getSelectedLayer();
		
		String paletteName = (String) l.getStyleBlackboard().get("net.refractions.udig.style.raster.palette"); //$NON-NLS-1$
		if (paletteName != null){
			ColorBrewer cb = getBrewer();
			cmbPalette.setSelection(new StructuredSelection(cb.getPalette(paletteName)));
		}
		
		Style style = (Style) l.getStyleBlackboard().get(SLDContent.ID);
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
