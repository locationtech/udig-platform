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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.locationtech.udig.browser.BrowserPlugin;
import org.locationtech.udig.browser.internal.Messages;
import org.locationtech.udig.catalog.ui.FileConnectionFactory;
import org.locationtech.udig.internal.ui.UDIGDropHandler;
import org.locationtech.udig.project.ui.internal.ApplicationGISInternal;

/**
 * Provides a tabbed browser view using the native web browser.
 *
 * @author mleslie
 * @since 1.0.0
 */
public class BrowserContainerView extends ViewPart {
    /** BrowserContainerView ID field */
    public static final String VIEW_ID = "org.locationtech.udig.browser.ui.browserContainerView"; //$NON-NLS-1$

    private static final String BROWSER_INITIAL_URL_PROPERTY = "org.locationtech.udig.browser.initialURL"; //$NON-NLS-1$

    private static String BROWSER_TYPE = "org.locationtech.udig.browser.TYPE"; //$NON-NLS-1$

    private static String BROWSER_NAME = "BROWSER_NAME"; //$NON-NLS-1$

    private static String BROWSER_URL = "BROWSER_URL"; //$NON-NLS-1$

    private static String BROWSER_LISTENER = "BROWSER_LISTENER"; //$NON-NLS-1$

    private static String BROWSER_ALERT_URL = "BROWSER_ALERT_URL"; //$NON-NLS-1$

    /** BrowserContainerView tabFolder field */
    private CTabFolder tabFolder;

    private static int count = 1;

    private LocationListener locListen;

    private URL lastAlertURL;

    // next time use policy bind less code!
    private String forwardIconEnabled = "icons/elcl16/forward_nav.gif"; //$NON-NLS-1$

    private String forwardIconDisabled = "icons/dlcl16/forward_nav.gif"; //$NON-NLS-1$

    private String backwardIconEnabled = "icons/elcl16/backward_nav.gif"; //$NON-NLS-1$

    private String backwardIconDisabled = "icons/dlcl16/backward_nav.gif"; //$NON-NLS-1$

    private String refreshIconEnabled = "icons/elcl16/refresh_co.gif"; //$NON-NLS-1$

    private IAction forward;

    private IAction backward;

    private ChangeListener changeListener;

    private Map<Browser, CTabItem> tabMap = new HashMap<>();

    private Map<Browser, String> listenerMap = new HashMap<>();

    private List<BrowserData> browserData;

    private LocationEntry locationEntry;

    private final class BrowserProgressListener implements ProgressListener {
        private final IProgressMonitor monitor;

        private CTabItem item;

        public BrowserProgressListener(final IProgressMonitor monitor, final CTabItem item,
                CTabFolder tabFolder) {
            this.monitor = monitor;
            this.item = item;
            tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
                @Override
                public void close(CTabFolderEvent event) {
                    if (event.item == item) {
                        monitor.done();
                    }
                }
            });

            item.addListener(SWT.Dispose, new Listener() {

                @Override
                public void handleEvent(Event event) {
                    monitor.done();
                }

            });
        }

        int current = -1;

        Timer timer = new Timer();

        Timeout timeout = null;

        class Timeout extends TimerTask {

            @Override
            public void run() {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        monitor.done();
                    }

                });
            }

        };

        @Override
        public void changed(ProgressEvent event) {
            if (timeout != null) {
                timeout.cancel();
            }
            timeout = new Timeout();
            timer.schedule(timeout, 20000);
            if (current > 0) {
                monitor.worked(Math.max(0, event.current - current));
                if (event.current >= event.total) {
                    completed(event);
                }
            } else {
                String msg = MessageFormat.format(Messages.BrowserContainerView_loadingMessage,
                        item.getText());
                monitor.beginTask(msg, event.total);
                monitor.worked(event.current);
                current = event.current;
            }
        }

        @Override
        public void completed(ProgressEvent event) {
            if (timeout != null) {
                timeout.cancel();
            }
            monitor.done();
            current = -1;
        }
    }

    private static class BrowserData {
        private String name;

        private String url;

        private String listener;

        BrowserData(String name1, String url1, String listener1) {
            this.name = name1;
            this.url = url1;
            this.listener = listener1;
        }

        BrowserData(IMemento m) {
            this(m.getString(BROWSER_NAME), m.getString(BROWSER_URL),
                    m.getString(BROWSER_LISTENER));
        }

        /**
         *
         * @return page name
         */
        public String getName() {
            return this.name;
        }

        /**
         *
         * @return String
         */
        public String getUrl() {
            return this.url;
        }

        /**
         *
         * @return LocationListener, or null
         */
        public LocationListener getListener() {
            if (this.listener == null || this.listener.equals("DEFAULT")) //$NON-NLS-1$
                return null;
            try {
                return (LocationListener) Class.forName(this.listener).getDeclaredConstructor()
                        .newInstance();
            } catch (Exception e) {
                return null;
            }
        }
    }

    public BrowserContainerView() {
        super();
    }

    @Override
    public void createPartControl(Composite parent) {
        FormLayout layout = new FormLayout();
        parent.setLayout(layout);

        locationEntry = new LocationEntry(this);
        Control control = locationEntry.createControl(parent);
        FormData data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);
        control.setLayoutData(data);

        this.tabFolder = new CTabFolder(parent, SWT.BORDER | SWT.TOP | SWT.CLOSE);
        this.tabFolder.addSelectionListener(getChangeListener());
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(control, 0);
        data.bottom = new FormAttachment(100, 0);
        tabFolder.setLayoutData(data);

        IViewSite site = getViewSite();
        IActionBars bars = site.getActionBars();
        IToolBarManager toolbarMgr = bars.getToolBarManager();
        toolbarMgr.add(getBackwardAction());
        toolbarMgr.add(getForwardAction());
        toolbarMgr.add(getRefreshAction());
        toolbarMgr.add(locationEntry.getButton());

        if (this.browserData != null) {
            for (BrowserData bd : this.browserData) {
                addTab(bd.getName(), bd.getUrl(), bd.getListener());
            }
            this.browserData = null;
        } else {
            String initialBrowserURL = System.getProperty(BROWSER_INITIAL_URL_PROPERTY,
                    "http://udig.github.io/data/"); //$NON-NLS-1$
            addTab(Messages.BrowserContainerView_tabTitle, initialBrowserURL, (Image) null,
                    getListener()); // $NON-NLS-1$
        }
    }

    @Override
    public void setFocus() {
        this.tabFolder.getSelection().getControl().setFocus();
    }

    /**
     *
     * @return Action
     */
    public IAction getForwardAction() {
        if (this.forward == null) {
            this.forward = new Action() {
                @Override
                public void run() {
                    CTabItem tab = tabFolder.getSelection();
                    if (tab == null)
                        return;
                    Control cont = tab.getControl();
                    if (cont instanceof Browser) {
                        ((Browser) cont).forward();
                    }
                }
            };
            this.forward.setEnabled(false);
            this.forward.setText(Messages.BrowserContainerView_forward_text);
            this.forward
                    .setImageDescriptor(BrowserPlugin.getImageDescriptor(this.forwardIconEnabled));
            this.forward.setDisabledImageDescriptor(
                    BrowserPlugin.getImageDescriptor(this.forwardIconDisabled));
            this.forward.setToolTipText(Messages.BrowserContainerView_forward_tooltip);
        }
        return this.forward;
    }

    /**
     *
     * @return Action
     */
    public IAction getBackwardAction() {
        if (this.backward == null) {
            this.backward = new Action() {
                @Override
                public void run() {
                    CTabItem tab = tabFolder.getSelection();
                    if (tab == null)
                        return;
                    Control cont = tab.getControl();
                    if (cont instanceof Browser) {
                        ((Browser) cont).back();
                    }
                }

            };
            this.backward.setEnabled(false);
            this.backward.setText(Messages.BrowserContainerView_back_text);
            this.backward
                    .setImageDescriptor(BrowserPlugin.getImageDescriptor(this.backwardIconEnabled));
            this.backward.setDisabledImageDescriptor(
                    BrowserPlugin.getImageDescriptor(this.backwardIconDisabled));
            this.backward.setToolTipText(Messages.BrowserContainerView_back_tooltip);
        }
        return this.backward;
    }

    private IAction refresh;

    public IAction getRefreshAction() {
        if (this.refresh == null) {
            this.refresh = new Action() {
                @Override
                public void run() {
                    CTabItem tab = tabFolder.getSelection();
                    if (tab == null)
                        return;
                    Control cont = tab.getControl();
                    if (cont instanceof Browser) {
                        ((Browser) cont).refresh();
                    }
                }
            };
            this.refresh.setEnabled(true);
            this.refresh.setText(Messages.BrowserContainerView_refresh);
            this.refresh
                    .setImageDescriptor(BrowserPlugin.getImageDescriptor(this.refreshIconEnabled));
            this.refresh
                    .setImageDescriptor(BrowserPlugin.getImageDescriptor(this.refreshIconEnabled));
            this.refresh.setToolTipText(Messages.BrowserContainerView_refresh);
        }
        return this.refresh;
    }

    public void setName(String name) {
        this.setPartName(name);
    }

    /**
     *
     * @return singleton LocationListener
     */
    public LocationListener getListener() {
        if (this.locListen == null) {
            this.locListen = new LocationListener() {
                @Override
                public void changing(LocationEvent event) {
                    String url = event.location;
                    // Should be part of ServiceExtension extension point.
                    if (url.toLowerCase().indexOf("=getcapabilities") != -1 //$NON-NLS-1$
                            || url.toLowerCase().indexOf("jdbc:postgis://") != -1 //$NON-NLS-1$
                            || url.toLowerCase().indexOf("postgis:jdbc://") != -1 //$NON-NLS-1$
                            || recognizedFile(url)) {
                        event.doit = false;

                        UDIGDropHandler dropHandler = new UDIGDropHandler();
                        dropHandler.setTarget(ApplicationGISInternal.getActiveMapPart());
                        dropHandler.performDrop(url, null);
                    }
                }

                private boolean recognizedFile(String url) {
                    if (url.toLowerCase().indexOf("file://") != -1) { //$NON-NLS-1$
                        return new FileConnectionFactory().canProcess(url);
                    }
                    final Pattern PATTERN = Pattern.compile("[^:/]+://?.+"); //$NON-NLS-1$
                    if (PATTERN.matcher(url).matches()) {
                        return new FileConnectionFactory().canProcess(url);
                    }
                    return false;
                }

                @Override
                public void changed(LocationEvent event) {
                    //
                }
            };
        }
        return this.locListen;
    }

    /**
     *
     * @return change listener
     */
    public ChangeListener getChangeListener() {
        if (this.changeListener == null) {
            this.changeListener = new ChangeListener();
        }
        return changeListener;
    }

    /**
     *
     * @param name
     * @param url
     * @param listen
     */
    public void addTab(String name, String url, LocationListener listen) {
        addTab(name, url, (Image) null, listen);
    }

    /**
     *
     * @param name
     * @param url
     * @param desc
     * @param listen
     */
    public void addTab(String name, String url, ImageDescriptor desc, LocationListener listen) {
        if (desc != null)
            addTab(name, url, desc.createImage(), listen);
        else
            addTab(name, url, (Image) null, listen);
    }

    /**
     *
     * @param name
     * @param url
     * @param desc
     * @param listen
     */
    public void addTab(String name, URL url, ImageDescriptor desc, LocationListener listen) {
        addTab(name, url.toString(), desc, listen);
    }

    /**
     *
     * @param name
     * @param url
     * @param image
     * @param listen
     */
    public void addTab(String name, String url, Image image, LocationListener listen) {
        CTabItem item = new CTabItem(this.tabFolder, SWT.NONE);
        if (name != null) {
            item.setText(name);
        } else {
            item.setText(MessageFormat.format(Messages.BrowserContainerView_pageCount,
                    new Object[] { count++ }));
        }
        Browser browser = createBrowser(item, listen);
        browser.setUrl(url);

        if (image != null)
            item.setImage(image);
        this.tabFolder.setSelection(item);
        this.tabFolder.layout();
    }

    /**
     *
     * @param url
     * @param listen
     */
    public void addTab(URL url, LocationListener listen) {
        addTab(null, url, listen);
    }

    /**
     *
     * @param url
     * @param image
     * @param listen
     */
    public void addTab(URL url, Image image, LocationListener listen) {
        addTab(null, url.toString(), image, listen);
    }

    /**
     *
     * @param name
     * @param url
     * @param listen
     */
    public void addTab(String name, URL url, LocationListener listen) {
        addTab(name, url.toString(), listen);
    }

    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        super.init(site, memento);
        if (memento != null) {
            IMemento[] array = memento.getChildren(BROWSER_TYPE);
            this.browserData = new ArrayList<>(array.length);
            for (IMemento mem : array) {
                this.browserData.add(new BrowserData(mem));
            }
            String urlString = memento.getString(BROWSER_ALERT_URL);
            if (urlString != null) {
                try {
                    this.lastAlertURL = new URL(urlString);
                } catch (MalformedURLException ex) {
                    ;
                }
            }
        }
    }

    @Override
    public void saveState(IMemento memento) {
        super.saveState(memento);
        if (this.lastAlertURL != null)
            memento.putString(BROWSER_ALERT_URL, this.lastAlertURL.toString());
        Control[] controls = this.tabFolder.getChildren();
        for (Control control : controls) {
            if (control instanceof Browser) {
                Browser browser = (Browser) control;
                if (!browser.isDisposed()) {
                    CTabItem item = this.tabMap.get(browser);
                    String listener = this.listenerMap.get(browser);
                    if (item != null && !item.isDisposed()) {
                        IMemento child = memento.createChild(BROWSER_TYPE);
                        child.putString(BROWSER_NAME, item.getText());
                        child.putString(BROWSER_URL, browser.getUrl());
                        child.putString(BROWSER_LISTENER, listener);
                    }
                }
            }
        }
    }

    private Browser createBrowser(CTabItem item, LocationListener listen) {
        Browser browser = new Browser(tabFolder, SWT.NONE);
        if (listen != null) {
            browser.addLocationListener(listen);
            listenerMap.put(browser, listen.getClass().getCanonicalName());
        } else {
            browser.addLocationListener(getListener());
            listenerMap.put(browser, "DEFAULT"); //$NON-NLS-1$
        }
        // needed to keep the forward/back buttons up to date
        browser.addLocationListener(getChangeListener());
        browser.addVisibilityWindowListener(getChangeListener());
        browser.addOpenWindowListener(getChangeListener());
        browser.addTitleListener(getChangeListener());
        item.setControl(browser);
        tabMap.put(browser, item);

        final IProgressMonitor monitor = getViewSite().getActionBars().getStatusLineManager()
                .getProgressMonitor();

        browser.addProgressListener(new BrowserProgressListener(monitor, item, tabFolder));

        return browser;
    }

    public void addTab(String url, LocationListener listen) {
        addTab(null, url, listen);
    }

    private class ChangeListener implements LocationListener, VisibilityWindowListener,
            SelectionListener, OpenWindowListener, CloseWindowListener, TitleListener {

        @Override
        public void changing(LocationEvent event) {
            locationEntry.setText(event.location);
            return;
        }

        @Override
        public void changed(LocationEvent event) {
            setEnability();
        }

        @Override
        public void hide(WindowEvent event) {
            setEnability();
        }

        @Override
        public void show(WindowEvent event) {
            setEnability();
        }

        private void setEnability() {
            CTabItem tab = tabFolder.getSelection();
            if (tab == null) {
                getForwardAction().setEnabled(false);
                getBackwardAction().setEnabled(false);
                getRefreshAction().setEnabled(false);
            } else {
                Control cont = tab.getControl();
                if (cont instanceof Browser) {
                    getForwardAction().setEnabled(((Browser) cont).isForwardEnabled());
                    getBackwardAction().setEnabled(((Browser) cont).isBackEnabled());
                    getRefreshAction().setEnabled(true);
                }
            }
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            setEnability();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            setEnability();
        }

        @Override
        public void open(WindowEvent event) {
            CTabItem item = new CTabItem(tabFolder, SWT.NONE);
            Browser browser = createBrowser(item, null);
            item.setText(MessageFormat.format(Messages.BrowserContainerView_pageCount,
                    new Object[] { count++ }));
            tabFolder.setSelection(item);
            event.browser = browser;
        }

        @Override
        public void changed(TitleEvent event) {
            String title = event.title;
            if (title != null && title.length() != 0) {
                if (title.indexOf("http://") == -1) { //$NON-NLS-1$
                    CTabItem item = tabMap.get(event.getSource());
                    item.setText(title);
                }
            }
        }

        @Override
        public void close(WindowEvent event) {
            Browser browser = event.browser;
            tabMap.remove(browser);
            listenerMap.remove(browser);
        }
    }

    public void setCurrentURL(String text) {
        CTabItem item = tabFolder.getSelection();
        if (item == null) {
            addTab(Messages.BrowserContainerView_tabTitle, text, (Image) null, getListener());
        } else {
            Browser b = (Browser) item.getControl();
            b.setUrl(text);
        }
    }
}
