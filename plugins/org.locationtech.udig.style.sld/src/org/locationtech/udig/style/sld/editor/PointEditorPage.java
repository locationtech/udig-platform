/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.part.PageBook;
import org.geotools.filter.FilterFactoryImpl;
import org.geotools.styling.Displacement;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Graphic;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Symbolizer;
import org.locationtech.udig.filter.ComboExpressionViewer;
import org.locationtech.udig.style.sld.SLD;
import org.locationtech.udig.style.sld.SLDPlugin;
import org.locationtech.udig.style.sld.internal.Messages;
import org.opengis.filter.expression.Expression;
import org.opengis.style.AnchorPoint;

public class PointEditorPage extends StyleEditorPage {
    private static int standardPadding = 2;

    public PointEditorPage() {
    }
    
 
    

    @Override
    public void createPageContent( Composite parent ) {
    	parent.setLayout(new GridLayout(2, false));
    	createListContent(parent);
    	createGraphicBook(parent);
    	createGraphicContent(parent);
    }
    
    private void applyStyle() {
        /*
         * This first section should really be handled by a containing shell or some such 
         * thing that will allow multiple rules, and handle the rule-level options  such as 
         * max/min scale denominator and filter.
         */
        FeatureTypeStyle defaultStyle = null;
        for(FeatureTypeStyle typeStyle : getStyle().featureTypeStyles()) {
            if(typeStyle.getName().equals("Default Styler")) {
                defaultStyle = typeStyle;
                break;
            }
        }
        if(defaultStyle == null)
            return;
        
        Rule rule = defaultStyle.rules().get(0);
        // Find the first PointSymbolizer in the rule
        PointSymbolizer sym = null;
        for(Symbolizer symbolizer : rule.symbolizers()) {
            if(symbolizer instanceof PointSymbolizer) {
                sym = (PointSymbolizer)symbolizer;
                break;
            }
        }
        if(sym == null) {
            SLD.POINT.createDefault();
            rule.symbolizers().add(sym);
        }
        
//        IProgressMonitor monitor = new NullProgressMonitor();
//        getSelectedLayer().getResource(FeatureSource.class, monitor);
        
        Graphic g = sym.getGraphic();
        Expression opacity = g.getOpacity();
        Expression rotation = g.getRotation();
        Expression size = g.getSize();
        AnchorPoint anchor = g.getAnchorPoint();
        Displacement displacement = g.getDisplacement();
        
        
    }
    
    /*
     * These belong to the list composite responsible for managing External
     * Graphics and Marks.  Multiple options are provided, with the first that
     * the system can render being used.
     */
    Composite listComposite;
    List representationList;
    Button listAddButton;
    Button listUpButton;
    Button listDownButton;
    Button listRemoveButton;
    
    private void createListContent(Composite parent) {
        listComposite = new Composite(parent, SWT.NONE);
        listComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        listComposite.setLayout(new GridLayout(2, false));
        
        representationList = new List(listComposite, SWT.SINGLE | SWT.DEFAULT);
        representationList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        representationList.setItems(getGraphicRepresentations());
        representationList.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected( SelectionEvent e ) {
                widgetSelected(e);
            }

            public void widgetSelected( SelectionEvent e ) {
                listRemoveButton.setEnabled(true);
                if(representationList.getSelectionIndex() == 0)
                    listUpButton.setEnabled(false);
                else
                    listUpButton.setEnabled(true);
                if(representationList.getSelectionIndex() == representationList.getItemCount() -1)
                    listDownButton.setEnabled(false);
                else
                    listDownButton.setEnabled(true);
            }
            
        });
        
        Composite buttonComposite = new Composite(listComposite, SWT.NONE);
        buttonComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        buttonComposite.setLayout(new FormLayout());
        
        listAddButton = new Button(buttonComposite, SWT.PUSH);
        listAddButton.setText("Add");
        FormData data = new FormData();
        data.left = new FormAttachment(0, standardPadding);
        data.top = new FormAttachment(0, standardPadding);
        listAddButton.setLayoutData(data);
        
        listUpButton = new Button(buttonComposite, SWT.PUSH);
        listUpButton.setText("Move Up");
        data = new FormData();
        data.left = new FormAttachment(listAddButton, 0, SWT.LEFT);
        data.top = new FormAttachment(listAddButton, standardPadding * 3);
        listUpButton.setLayoutData(data);
        listUpButton.setEnabled(false);
        
        listDownButton = new Button(buttonComposite, SWT.PUSH);
        listDownButton.setText("Move Down");
        data = new FormData();
        data.left = new FormAttachment(listAddButton, 0, SWT.LEFT);
        data.top = new FormAttachment(listUpButton, standardPadding);
        listDownButton.setLayoutData(data);
        listDownButton.setEnabled(false);
        
        listRemoveButton = new Button(buttonComposite, SWT.PUSH);
        listRemoveButton.setText("Remove");
        data = new FormData();
        data.top = new FormAttachment(listDownButton, standardPadding * 3);
        data.left = new FormAttachment(listAddButton, 0, SWT.LEFT);
        data.bottom = new FormAttachment(100, -1 * standardPadding);
        listRemoveButton.setLayoutData(data);
        listRemoveButton.setEnabled(false);
        
    }
    
 
    
    /*
     * These widgets belong to the graphic composite and provide attributes 
     * common to all graphic options listed in the list composite above.
     */
    Composite graphicComposite;
    ComboExpressionViewer opacityViewer;
    ComboExpressionViewer sizeViewer;
    ComboExpressionViewer rotationViewer;
    ComboExpressionViewer anchorXViewer;
    ComboExpressionViewer anchorYViewer;
    ComboExpressionViewer displacementXViewer;
    ComboExpressionViewer displacementYViewer;
    
    private void createGraphicContent(Composite parent) {
        graphicComposite = new Composite(parent, SWT.NONE);
        graphicComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        graphicComposite.setLayout(new GridLayout(2, false));
        
        Label label = new Label(graphicComposite, SWT.NONE);
        label.setText(Messages.StylingConstants_label_opacity);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
        label.setToolTipText(Messages.StylingConstants_tooltip_opacity);
        opacityViewer = new ComboExpressionViewer(graphicComposite, SWT.SINGLE);
        opacityViewer.setOptions(getOpacityList());
        opacityViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        opacityViewer.getControl().setToolTipText(Messages.StylingConstants_label_opacity);
        
        label = new Label(graphicComposite, SWT.NONE);
        label.setText(Messages.StylingConstants_label_size);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
        label.setToolTipText(Messages.StylingConstants_tooltip_size);
        sizeViewer = new ComboExpressionViewer(graphicComposite, SWT.SINGLE);
        sizeViewer.setOptions(getSizeList());
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        GC gc = new GC(sizeViewer.getControl());
        try {
            Point extent = gc.textExtent("X");//$NON-NLS-1$
            gd.widthHint = 3 * extent.x;
        } finally {
            gc.dispose();
        }
        sizeViewer.getControl().setLayoutData( gd);
        sizeViewer.getControl().setToolTipText(Messages.StylingConstants_tooltip_size);
        
        label = new Label(graphicComposite, SWT.NONE);
        label.setText(Messages.StylingConstants_label_rotation);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
        label.setToolTipText(Messages.StylingConstants_tooltip_rotation);
        rotationViewer = new ComboExpressionViewer(graphicComposite, SWT.SINGLE);
        rotationViewer.setOptions(getRotationList());
        rotationViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        rotationViewer.getControl().setToolTipText(Messages.StylingConstants_tooltip_rotation);
        
        label = new Label(graphicComposite, SWT.NONE);
        label.setText(Messages.StylingConstants_label_anchor);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
        label.setToolTipText(Messages.StylingConstants_tooltip_anchor);
        Composite anchorComposite = new Composite(graphicComposite, SWT.NONE);
        anchorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        anchorComposite.setLayout(new FormLayout());
        anchorXViewer = new ComboExpressionViewer(anchorComposite, SWT.SINGLE);
        anchorXViewer.setOptions(getAnchorList());
        FormData data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.top = new FormAttachment(0, 0);
        data.bottom = new FormAttachment(100, 0);
        anchorXViewer.getControl().setLayoutData(data);
        anchorXViewer.getControl().setToolTipText(Messages.StylingConstants_tooltip_anchor);
        label = new Label(anchorComposite, SWT.NONE);
        label.setText("X");
        data = new FormData();
        data.left = new FormAttachment(anchorXViewer.getControl(), 4);
        data.bottom = new FormAttachment(anchorXViewer.getControl(), 0, SWT.BOTTOM) ;
        label.setLayoutData(data);
        label.setToolTipText(Messages.StylingConstants_tooltip_anchor);
        anchorYViewer = new ComboExpressionViewer(anchorComposite, SWT.SINGLE);
        anchorYViewer.setOptions(getAnchorList());
        data = new FormData();
        data.left = new FormAttachment(label, 4);
        data.top = new FormAttachment(anchorXViewer.getControl(), 0, SWT.TOP);
        data.bottom = new FormAttachment(anchorXViewer.getControl(), 0, SWT.BOTTOM);
        data.right = new FormAttachment(100, 0);
        anchorYViewer.getControl().setLayoutData(data);
        anchorYViewer.getControl().setToolTipText(Messages.StylingConstants_tooltip_anchor);
        
        label = new Label(graphicComposite, SWT.NONE);
        label.setText(Messages.StylingConstants_label_displacement);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
        label.setToolTipText(Messages.StylingConstants_tooltip_displacement);
        Composite displacementComposite = new Composite(graphicComposite, SWT.NONE);
        displacementComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        displacementComposite.setLayout(new FormLayout());
        displacementXViewer = new ComboExpressionViewer(displacementComposite, SWT.SINGLE);
        displacementXViewer.setOptions(getDisplacementList());
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.top = new FormAttachment(0, 0);
        data.bottom = new FormAttachment(100, 0);
        displacementXViewer.getControl().setLayoutData(data);
        displacementXViewer.getControl().setToolTipText(Messages.StylingConstants_tooltip_displacement);
        label = new Label(displacementComposite, SWT.NONE);
        label.setText("X");
        data = new FormData();
        data.left = new FormAttachment(displacementXViewer.getControl(), 4, SWT.RIGHT);
        data.bottom = new FormAttachment(displacementXViewer.getControl(), 0, SWT.BOTTOM);
        label.setLayoutData(data);
        label.setToolTipText(Messages.StylingConstants_tooltip_displacement);
        displacementYViewer = new ComboExpressionViewer(displacementComposite, SWT.SINGLE);
        displacementYViewer.setOptions(getDisplacementList());
        data = new FormData();
        data.left = new FormAttachment(label, 4, SWT.RIGHT);
        data.top = new FormAttachment(displacementXViewer.getControl(), 0, SWT.TOP);
        data.bottom = new FormAttachment(displacementXViewer.getControl(), 0, SWT.BOTTOM);
        data.right = new FormAttachment(100, 0);
        displacementYViewer.getControl().setLayoutData(data);
        displacementYViewer.getControl().setToolTipText(Messages.StylingConstants_tooltip_displacement);
    }
    
    /*
     * These widgets belong to the various components of the graphic book.
     * They provide the appropriate details when a graphic is selected from the
     * list composite.
     */
    PageBook graphicBook;
    
    Composite externalGraphicPage;
    MarkEditorPage markComponent;
    
    private void createGraphicBook(Composite parent) {
        graphicBook = new PageBook(parent, SWT.NONE);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.verticalSpan = 2;
        graphicBook.setLayoutData(gd);
        
        markComponent = new MarkEditorPage();
        markComponent.createControl(graphicBook);
        markComponent.markComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        graphicBook.showPage(markComponent.getControl());
        
    }
    @Override
    public String getErrorMessage() {
        // check over content and return
        // non null if in error
        return null;
    }

    @Override
    public String getLabel() {
        return null; // not sure what this is for we need to update the javadocs
    }

    @Override
    public void gotFocus() {
        //refresh();
        //dirty = false;
    }

    @Override
    public boolean performCancel() {
        return true;
    }

    @Override
    public void styleChanged( Object source ) {
        
    }

    public boolean okToLeave() {
        return true;
    }

    public boolean performApply() {
        return true;
    }

    public boolean performOk() {
        return true;
    }

    public void refresh() {
    }

    private static FilterFactoryImpl factory;
    private FilterFactoryImpl getFilterFactory() {
        if(factory == null){
            synchronized(factory) {
                if(factory == null) {
                    factory = new FilterFactoryImpl();
                }
            }
        }
        return factory;
    }
    /*
     * TODO: implement stuff
     */
    private String[] getGraphicRepresentations() {
        return new String[] {"External Graphic (PNG)",
                "External Graphic (BMP)",
                "Well Known Mark (Star)"};
    }
    
    /*
     * TODO: implement stuff
     */
//    private Expression[] getOpacityList(Expression exp) {
//        return new Expression[] {exp,
//                getFilterFactory().createLiteralExpression("Clear"),
//                getFilterFactory().createLiteralExpression("25%"), 
//                getFilterFactory().createLiteralExpression("50%"),
//                getFilterFactory().createLiteralExpression("75%"), 
//                getFilterFactory().createLiteralExpression("Opaque")};
//    }
    private String[] getOpacityList() {
        return new String[] {"Clear", "25%", "50%", "75%", "Opaque"};
    }
    
    private int[] getSizeList() {
        return new int[] {1, 2, 3, 5, 10, 12, 15, 20};
    }
    
    private String[] getRotationList() {
        return new String[] {"-150�", "-135�", "-120�", "-90�", "-60�", "-45�", "-30�", "0�", 
                "30�", "45�", "60�", "90�", "120�", "135�", "150�", "180�"};
    }
    
    private String[] getAnchorList() {
        return new String[] {"0%", "25%", "50%", "75%", "100%"};
    }
    
    private int[] getDisplacementList() {
        return new int[] {1, 2, 3, 5, 10, 12, 15, 20};
    }
    
    private Combo generateFancyCombo(Composite parent, String[] items, int style) {
        return generateFancyCombo(parent, items, true, -1, style);
    }
    
    /**
     * Creates a fancy combo box that may or may not have an included default indicator, and 
     * has an unselected message initially, if no selected value or default are available.
     *
     * @param parent Parent Composite
     * @param items array of display names
     * @param hasDefault indicates if a default option should be inserted
     * @param selection The index of the item to select by default.  If selection is outside the 
     *                  array limits, the default or unselected message will be selected.
     * @param style Style bits for the combo.  Can include SWT.READONLY and/or one of 
     *              SWT.DROPDOWN or SWT.SIMPLE.
     * @return
     */
    private Combo generateFancyCombo(Composite parent, String[] items, boolean hasDefault, int selection, int style) {
        final Combo fancyCombo = new Combo(parent, style);
        fancyCombo.setItems(items);
        if(selection >= 0 && selection < items.length)
            fancyCombo.select(selection);
        if(hasDefault) {
            fancyCombo.add(Messages.PointEditorPage_fancyCombo_default, 0);
            if(selection < 0 || selection >= items.length)
                fancyCombo.select(0);
        } else if(selection < 0 || selection >= items.length) {
            fancyCombo.add(Messages.PointEditorPage_fancyCombo_unselected, 0);
            fancyCombo.select(0);
            final SelectionListener listener = new SelectionListener() {
                public void widgetDefaultSelected( SelectionEvent e ) {
                    widgetSelected(e);
                }

                public void widgetSelected( SelectionEvent e ) {
                    fancyCombo.remove(0);
                    fancyCombo.removeSelectionListener(this);
                    SLDPlugin.log("Removing selection listener", null);
                }
            };
            fancyCombo.addSelectionListener(listener);
        }
        return fancyCombo;
    }
}
