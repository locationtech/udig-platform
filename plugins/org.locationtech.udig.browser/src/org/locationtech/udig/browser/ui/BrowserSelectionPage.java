/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.browser.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.locationtech.udig.browser.BrowserPlugin;
import org.locationtech.udig.browser.ExternalCatalogueImportDescriptor;
import org.locationtech.udig.browser.ExternalCatalogueImportPage;
import org.locationtech.udig.browser.ExternalCatalogueImportPageDescriptor;
import org.locationtech.udig.browser.ExternalCatalogueImportURLDescriptor;
import org.locationtech.udig.catalog.ui.IDataWizard;
import org.locationtech.udig.core.internal.ExtensionPointProcessor;
import org.locationtech.udig.core.internal.ExtensionPointUtil;
import org.locationtech.udig.internal.ui.UiPlugin;

/**
 * Displays a list of catalog pages extending the extension point.
 * <p>
 *
 * </p>
 *
 * @author mleslie
 * @since 1.0.0
 */
public class BrowserSelectionPage extends WizardSelectionPage implements ISelectionChangedListener {
    private static String BROWSER_SELECTION = "BROWSER_SELECTION"; //$NON-NLS-1$

    private static String SELECTED_BROWSER = "SELECTED_BROWSER"; //$NON-NLS-1$

    private IDialogSettings settings;

    private List<ExternalCatalogueImportDescriptor> descriptors;

    private WizardViewer viewer;

    private ExternalCatalogueImportDescriptor selectedDescriptor;

    private Map<ExternalCatalogueImportDescriptor, ExternalCatalogueImportPage> pageCache;

    /**
     *
     */
    public BrowserSelectionPage() {
        this("Select a Catalog"); //$NON-NLS-1$
    }

    /**
     * @param pageName
     */
    public BrowserSelectionPage(String pageName) {
        super(pageName);
        settings = BrowserPlugin.getDefault().getDialogSettings().getSection(BROWSER_SELECTION);
        if (settings == null) {
            settings = BrowserPlugin.getDefault().getDialogSettings()
                    .addNewSection(BROWSER_SELECTION);
        }
    }

    @Override
    public boolean canFlipToNextPage() {
        return isPageComplete();
    }

    @Override
    public boolean isPageComplete() {
        return this.selectedDescriptor != null
                && this.selectedDescriptor instanceof ExternalCatalogueImportPageDescriptor;
    }

    private Map<ExternalCatalogueImportDescriptor, ExternalCatalogueImportPage> getPageCache() {
        if (this.pageCache == null) {
            this.pageCache = new HashMap<>();
        }
        return this.pageCache;
    }

    /**
     *
     * @return URL of the selected URL descriptor, or null
     */
    public URL getUrl() {
        if (this.selectedDescriptor != null
                && this.selectedDescriptor instanceof ExternalCatalogueImportURLDescriptor) {
            return ((ExternalCatalogueImportURLDescriptor) this.selectedDescriptor).getUrl();
        }
        return null;
    }

    @Override
    public IWizardPage getNextPage() {
        ExternalCatalogueImportDescriptor id = this.selectedDescriptor;
        ExternalCatalogueImportPageDescriptor d;
        if (id instanceof ExternalCatalogueImportPageDescriptor) {
            d = (ExternalCatalogueImportPageDescriptor) id;
        } else {
            return null;
        }

        if (d != null) {
            try {
                IDataWizard wizard = (IDataWizard) getWizard();

                ExternalCatalogueImportPage page = getPageCache().get(d);
                if (page == null) {
                    page = d.createImportPage();
                    getPageCache().put(d, page);
                }
                wizard.init((WizardPage) page);

                return page;
            } catch (CoreException e) {
                String msg = "Could not instantiate import wizard page"; //$NON-NLS-1$
                System.out.println(msg);
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection selected = (IStructuredSelection) event.getSelection();
        if (selected == null || selected.isEmpty())
            return;

        this.selectedDescriptor = (ExternalCatalogueImportDescriptor) selected.getFirstElement();
        settings.put(SELECTED_BROWSER, this.selectedDescriptor.getLabel());
        setDescription(this.selectedDescriptor.getDescription());
        setImageDescriptor(this.selectedDescriptor.getDescriptionImage());

        getWizard().getContainer().updateButtons();
    }

    @Override
    public void createControl(Composite parent) {
        viewer = new WizardViewer(parent, SWT.SINGLE);
        List<ExternalCatalogueImportDescriptor> list = getDescriptors();
        viewer.setInput(list.toArray());
        String browser = settings.get(SELECTED_BROWSER);
        if (browser != null && browser.length() != 0) {
            final List<ExternalCatalogueImportDescriptor> selection = new LinkedList<>();

            for (ExternalCatalogueImportDescriptor desc : list) {
                if (desc.getLabel().equals(browser)) {
                    selection.add(desc);
                    viewer.setSelection(new IStructuredSelection() {
                        @Override
                        public Object getFirstElement() {
                            return selection.get(0);
                        }

                        @Override
                        public Iterator iterator() {
                            return selection.iterator();
                        }

                        @Override
                        public int size() {
                            return 1;
                        }

                        @Override
                        public Object[] toArray() {
                            return selection.toArray();
                        }

                        @Override
                        public List toList() {
                            return selection;
                        }

                        @Override
                        public boolean isEmpty() {
                            return false;
                        }
                    });
                }
            }
        }

        viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                IWizardPage next = getNextPage();
                getWizard().getContainer().showPage(next);
            }
        });

        viewer.addSelectionChangedListener(this);
        setControl(viewer.getControl());
    }

    /**
     *
     * @return List of descriptors
     */
    @SuppressWarnings("unchecked")
    protected List<ExternalCatalogueImportDescriptor> getDescriptors() {

        if (this.descriptors == null) {
            ExternalCatalogueImportPageProcessor p = new ExternalCatalogueImportPageProcessor();
            String xpid = ExternalCatalogueImportPage.XPID;

            ExtensionPointUtil.process(BrowserPlugin.getDefault(), xpid, p);
            this.descriptors = p.descriptors;

            ExternalCatalogueImportURLProcessor up = new ExternalCatalogueImportURLProcessor();
            ExtensionPointUtil.process(BrowserPlugin.getDefault(), xpid, up);
            this.descriptors.addAll(up.descriptors);
        }
        if (this.descriptors != null) {
            try {
                Collections.sort(this.descriptors, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        String s1 = ((ExternalCatalogueImportDescriptor) o1).getLabel();
                        String s2 = ((ExternalCatalogueImportDescriptor) o2).getLabel();
                        return s1.compareTo(s2);
                    }
                });
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
        return this.descriptors;

    }

    private static class ExternalCatalogueImportPageProcessor implements ExtensionPointProcessor {
        List<ExternalCatalogueImportDescriptor> descriptors = new LinkedList<>();

        @Override
        public void process(IExtension extension, IConfigurationElement element) throws Exception {
            IConfigurationElement[] childs = element.getChildren("externalCataloguePage"); //$NON-NLS-1$
            if (childs.length > 0) {
                ExternalCatalogueImportPageDescriptor d = new ExternalCatalogueImportPageDescriptor(
                        element);
                this.descriptors.add(d);
            }
        }
    }

    private static class ExternalCatalogueImportURLProcessor implements ExtensionPointProcessor {
        List<ExternalCatalogueImportDescriptor> descriptors = new LinkedList<>();

        @Override
        public void process(IExtension extension, IConfigurationElement element) throws Exception {
            IConfigurationElement[] childs = element.getChildren("externalCatalogueURL"); //$NON-NLS-1$
            if (childs.length == 0)
                return;
            URL url = null;
            try {
                url = new URL(childs[0].getAttribute("url")); //$NON-NLS-1$
            } catch (MalformedURLException ex) {
                return;
            }
            ExternalCatalogueImportURLDescriptor d = new ExternalCatalogueImportURLDescriptor(url);
            d.setLabel(element.getAttribute("name")); //$NON-NLS-1$
            d.setID(element.getAttribute("id")); //$NON-NLS-1$
            d.setDescription(element.getAttribute("description")); //$NON-NLS-1$
            d.setListener(element.getAttribute("listener")); //$NON-NLS-1$
            d.setViewName(element.getAttribute("viewName")); //$NON-NLS-1$
            String ns = element.getNamespaceIdentifier();
            String banner = element.getAttribute("image"); //$NON-NLS-1$

            if (banner != null)
                d.setDescriptionImage(AbstractUIPlugin.imageDescriptorFromPlugin(ns, banner));

            banner = element.getAttribute("icon"); //$NON-NLS-1$

            if (banner != null)
                d.setIcon(AbstractUIPlugin.imageDescriptorFromPlugin(ns, banner));
            this.descriptors.add(d);
        }
    }

    @Override
    public String getTitle() {
        if (this.selectedDescriptor != null) {
            return this.selectedDescriptor.getLabel();
        }
        return super.getTitle();
    }

    private static class WizardViewer extends TableViewer {

        /**
         * @param parent
         * @param style
         */
        public WizardViewer(Composite parent, int style) {
            super(parent, style);

            setContentProvider(ArrayContentProvider.getInstance());
            setLabelProvider(new LabelProvider() {
                @Override
                public String getText(Object object) {
                    ExternalCatalogueImportDescriptor descriptor = (ExternalCatalogueImportDescriptor) object;

                    return descriptor.getLabel();
                }

                @Override
                public Image getImage(Object object) {
                    ExternalCatalogueImportDescriptor descriptor = (ExternalCatalogueImportDescriptor) object;

                    String id = descriptor.getID();
                    ImageRegistry registry = UiPlugin.getDefault().getImageRegistry();
                    ImageDescriptor image = descriptor.getIcon();
                    synchronized (registry) {
                        if (registry.get(id) == null && image != null) {
                            registry.put(id, image);
                        }

                        return registry.get(id);
                    }
                }
            });
        }
    }

    /**
     *
     * @return true if the finish button should be enabled
     */
    public boolean canFinish() {
        if (selectedDescriptor != null) {
            return (this.selectedDescriptor instanceof ExternalCatalogueImportURLDescriptor);
        }
        return false;
    }

    /**
     *
     * @return descriptor of the icon of the selected descriptor
     */
    public ImageDescriptor getIconDescriptor() {
        if (this.selectedDescriptor != null) {
            return this.selectedDescriptor.getIcon();
        }
        return null;
    }

    /**
     *
     * @return LocationListener
     */
    public LocationListener getListener() {
        return this.selectedDescriptor.getListener();
    }

    public String getViewName() {
        return this.selectedDescriptor.getViewName();
    }
}
