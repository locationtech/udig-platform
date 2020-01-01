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
package org.locationtech.udig.style.sld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.PageBook;
import org.geotools.data.FeatureSource;
import org.geotools.feature.NameImpl;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;
import org.locationtech.udig.core.internal.ExtensionPointProcessor;
import org.locationtech.udig.core.internal.ExtensionPointUtil;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.style.IStyleConfigurator;
import org.locationtech.udig.style.sld.internal.Messages;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * An abstract class for style configurators intended for SLD style objects.
 * <p>
 * This class makes use of the sldEditorPart configuration point to produce several ViewParts
 * capable of editing individual Symbolizers.
 * </p>
 * <p>
 * 
 * @author Justin Deoliveira, Refractions Research Inc.
 */
public class SLDConfigurator extends IStyleConfigurator {

    /** The content manager for modifying the style. * */
    SLDContentManager sldContentManager;

    /** The editor book * */
    PageBook editorBook;

    /** blank page * */
    Composite blank;

    /** class 2 menu mapping * */
    List<SLDEditorMenuAction> toolbarItems;

    /** This is the data structure the extention point is processed into */
    SortedMap<Class, List<SLDEditorPart>> classToEditors;

    /**
     * Creates the configurator.
     */
    public SLDConfigurator() {
    }

    /**
     * Checks if the layer is a feature layer or if the layer is a wms that supports post.
     */
    public boolean canStyle( Layer layer ) {
        if (layer.hasResource(FeatureSource.class))
            return true;
        if (layer.hasResource(org.geotools.ows.wms.Layer.class))
            return true;
        return false; // TODO: check for wms supporting sld
    }

    /**
     * @see org.locationtech.udig.style.IStyleConfigurator#init(org.eclipse.ui.IViewSite)
     */
    public void init() {
        sldContentManager = new SLDContentManager();

        // process the extension point and add editors to the tool bar
        Comparator<Class> compare = new Comparator<Class>(){
            public int compare( Class a, Class b) {
                return a.getSimpleName().compareTo( b.getSimpleName() );
            }                
        };
        classToEditors = new TreeMap<Class, List<SLDEditorPart>>( compare );

        ExtensionPointProcessor p = new ExtensionPointProcessor(){
            public void process( IExtension ext, IConfigurationElement element ) throws Exception {
                try {
                    SLDEditorPart editor = (SLDEditorPart) element
                            .createExecutableExtension("class"); //$NON-NLS-1$

                    editor.setPluginId(element.getNamespace());
                    editor.setLabel(element.getAttribute("label")); //$NON-NLS-1$

                    ImageDescriptor image = editor.createImageDescriptor();
                    if (image == null) {
                        // TODO: set a default
                    } else
                        editor.setImageDescriptor(image);

                    Class contentClass = editor.getContentType(); // aka symbolizer

                    List<SLDEditorPart> list = classToEditors.get(contentClass);
                    if (list == null) {
                        list = new ArrayList<SLDEditorPart>();
                        classToEditors.put(contentClass, list);
                    }
                    list.add(editor);
                } catch (Exception e) {
                    SLDPlugin.log(null, e);
                }
            }
        };
        ExtensionPointUtil.process(SLDPlugin.getDefault(),SLDEditorPart.XPID, p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.style.IStyleConfigurator#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {
        createToolbarItems(); // now we have toolbar items
        editorBook = new PageBook(parent, SWT.NONE);
        editorBook.setVisible(true);

        blank = new Composite(editorBook, SWT.NONE);
        editorBook.showPage(blank);

        for( List<SLDEditorPart> parts : classToEditors.values() ) {
            for( SLDEditorPart part : parts ) {
                try {
                    part.createControl(editorBook);
                } catch (Throwable t) {
                    SLDPlugin.log(null, t);
                }
            }
        }
    }	
    /*
     * Called when new layer and blackbard values are available. <p> This provides update
     * information as a callback (rather than an event listener). </p>
     */
    protected void refresh() {
        Layer layer = getLayer();
        if (!canStyle(layer)) {
            throw new IllegalStateException("Hey I can't style " + layer); //$NON-NLS-1$
        }
        // pull the sld style off the blackboard, and initialize the cm
        Style style = (Style) getStyleBlackboard().get(SLDContent.ID);
        
        // if no style information, create default
        if (style == null) {
            style = SLDContent.createDefaultStyle();
            getStyleBlackboard().put(SLDContent.ID, style);
        }
        sldContentManager.init(SLDContent.getStyleBuilder(), style);
        
        // pull the feature type name out of the layer
        if (layer.getSchema() != null) {
            SimpleFeatureType featureType = layer.getSchema();
        
            //set the name of the feature type style for the feature renderer
            String name = featureType.getName().getLocalPart();
            sldContentManager.getDefaultFeatureTypeStyle().featureTypeNames().clear();
            sldContentManager.getDefaultFeatureTypeStyle().featureTypeNames().add(new NameImpl(SLDs.GENERIC_FEATURE_TYPENAME));
        }
        
        // force the toolbar to refresh
        //
        IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
        tbm.markDirty();
        tbm.update(true);
        getViewSite().getActionBars().updateActionBars();

        for( IContributionItem item : tbm.getItems() ) {
            ActionContributionItem action = (ActionContributionItem) item;
            action.getAction().setEnabled(action.getAction().isEnabled());
        }

        // focus the active editor if any exisits
        SLDEditorPart editor = (SLDEditorPart) editorBook.getData();
        List<Class> supported = SLD.getSupportedTypes( layer );
        if (editor != null) {
            if (supported.contains(editor.getContentType())) {
                initEditor(editor);
                editor.reset();
                return; // current editor is still okay
            }
        }
        // if we get here the current editor wasn't applicable, show first that works
        for( Class type : classToEditors.keySet() ){
            if( !supported.contains( type )) continue;
            
            for( SLDEditorPart part : classToEditors.get( type ) ){
                initEditor( part ); // FIXME: we don't want the editor to have content it should not
                part.reset();
                editorBook.setData( part );
                editorBook.showPage( part.getPage() );
                part.getPage().setVisible( true );
                return;
            }
        }
        editorBook.showPage(blank);
        
    }

    protected void createToolbarItems() {
        toolbarItems = new ArrayList<SLDEditorMenuAction>(); // FIXME!
        for( Class contentClass : classToEditors.keySet() ) {
            List<SLDEditorPart> editors = classToEditors.get(contentClass);
            if (editors == null || editors.isEmpty()) {
                continue; // skip class with no editors
            }
            // add an editor for disable
            editors.add(new SLDDisableEditorPart(contentClass));

            SLDEditorMenuAction action = new SLDEditorMenuAction(editors);
            // add to list of all
            toolbarItems.add(action);
        }

        // sort items so they dont randomly appear in the toolbar
        Collections.sort(toolbarItems);

        // add em here
        IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
        for( SLDEditorMenuAction item : toolbarItems ) {
            tbm.add(item);
        }

    }

    /**
     * Sets the editor up for a reset(). Ensures the editor has content.
     */
    void initEditor( SLDEditorPart editor ) {
        // enable the symbolizer if necessary
    	@SuppressWarnings("unchecked") Class<Symbolizer> symbolizerType = editor.getContentType();
    	
        Symbolizer content = sldContentManager.getSymbolizer( symbolizerType );
        if (content == null) {
            sldContentManager.addSymbolizer( symbolizerType );
            content = (Symbolizer)sldContentManager.getSymbolizer( symbolizerType );
        }

        editor.setStyleBuilder(sldContentManager.getStyleBuilder());
        editor.setLayer(getLayer());
        editor.setContent(content);
    }

    /**
     * Composite action made up of a drop down menu of actions.
     */
    class SLDEditorMenuAction extends Action
            implements
                IMenuCreator,
                Comparable<SLDEditorMenuAction> {

        List<SLDEditorPart> editors;
        Menu menu;

        public SLDEditorMenuAction( List<SLDEditorPart> editors ) {
            this.editors = editors;

            setMenuCreator(this);
            setImageDescriptor(editors.get(0).getImageDescriptor());
        }

        public Class getContentType() {
            if (editors == null || editors.isEmpty())
                return null;

            return editors.get(0).getContentType();
        }

        public void dispose() {
            if (menu != null) {
                menu.dispose();
            }            
        }

        public Menu getMenu( Control parent ) {
            if (menu != null) {
                menu.dispose();
            }
            menu = new Menu(parent);

            for( SLDEditorPart editor : editors ) {
                Action action = new SLDEditorAction(editor, this);
                addActionToMenu(menu, action);
            }

            return menu;
        }

        public Menu getMenu( Menu parent ) {
            return null;
        }

        protected void addActionToMenu( Menu parent, Action action ) {
            ActionContributionItem item = new ActionContributionItem(action);
            item.fill(parent, -1);
        }

        public int compareTo( SLDEditorMenuAction other ) {
            return editors.get(0).getContentType().getName().compareTo(
                    other.editors.get(0).getContentType().getName());
        }
        /**
         * @see org.eclipse.jface.action.Action#isEnabled()
         */
        public boolean isEnabled() {
            Layer layer = getLayer();
            if (layer == null)
                return false;

            return SLD.getSupportedTypes(layer).contains(getContentType());
        }
    }

    /**
     * Action for firing up a specific editor.
     */
    class SLDEditorAction extends Action {

        SLDEditorMenuAction parent;
        SLDEditorPart editor;

        public SLDEditorAction( SLDEditorPart editor, SLDEditorMenuAction parent ) {
            super();

            this.editor = editor;
            this.parent = parent;

            setImageDescriptor(getImageDescriptor(editor));
            setText(editor.getLabel());
        }

        public void run() {
            // update the icon to reflect the currently selected editor
            //
            parent.setImageDescriptor(getImageDescriptor());

            if (editor instanceof SLDDisableEditorPart) {
                // disable the symbolizer
            	@SuppressWarnings("unchecked") Class<Symbolizer> symbolizer = editor.getContentType();
                sldContentManager.removeSymbolizer( symbolizer );
            } else {
                initEditor(editor);
            }

            // signal editor to reset
            editor.reset();

            // finally show the editor page
            editorBook.setData(editor); // set the data of the editor book to be the active editor
            Composite page = editor.getPage();
            editorBook.setVisible(true);
            editorBook.showPage(page);
            // page.setVisible( true );
        }
        protected ImageDescriptor getImageDescriptor( SLDEditorPart editor1 ) {
            return editor1.getImageDescriptor();
        }
    }

    /**
     * A editor that does nothing except displays a "disabled" icon and creates a blank page.
     */
    class SLDDisableEditorPart extends SLDEditorPart {

        private Class<Symbolizer> contentType;

        public SLDDisableEditorPart( Class<Symbolizer> contentType ) {
            this.contentType = contentType;
            setImageDescriptor(SLD.createDisabledImageDescriptor(contentType));
            setLabel(Messages.SLDConfigurator_disable); 
        }

        public void init() {
            // do nothing
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.locationtech.udig.style.sld.SLDEditorPart#getContentType()
         */
        public Class getContentType() {
            return contentType;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.locationtech.udig.style.sld.SLDEditorPart#createPartControl(org.eclipse.swt.widgets.Composite)
         */
        protected Control createPartControl( Composite parent ) {
            return new Composite(parent, SWT.NONE);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.locationtech.udig.style.sld.SLDEditorPart#reset()
         */
        public void reset() {
            // disable the content configurator
            sldContentManager.removeSymbolizer(contentType);
        }

    }

}
