/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.catalog.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IForward;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IProcess;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveChangeListener;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IResolveFolder;
import net.refractions.udig.catalog.ISearch;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IResolveChangeEvent.Type;
import net.refractions.udig.catalog.IResolveDelta.Kind;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.ui.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IColorDecorator;
import org.eclipse.jface.viewers.IFontDecorator;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Decorate labels with actual titles from the info objects.
 * 
 * @author jgarnett
 * @since 0.6.0
 */
public class ResolveTitlesDecorator implements ILabelDecorator, IColorDecorator, IFontDecorator {

    final static Set<ILabelProviderListener> listeners = new CopyOnWriteArraySet<ILabelProviderListener>();
    final Set<ILabelProviderListener> instanceListeners = new HashSet<ILabelProviderListener>();

    private ResolveLabelProviderSimple source;

    private final Map<IResolve, LabelData> decorated = new IdentityHashMap<IResolve, LabelData>();
    private final Map<IResolve, LabelData> images = new IdentityHashMap<IResolve, LabelData>();
    private final Map<IResolve, ImageDescriptor> imageDescriptorCache=new IdentityHashMap<IResolve, ImageDescriptor>();

    final Queue<IResolve> toDecorate = new ConcurrentLinkedQueue<IResolve>();
    final Queue<IResolve> imagesToDecorate = new ConcurrentLinkedQueue<IResolve>();
    
    final UpdateLabel textWorker = new UpdateLabel(toDecorate, decorated, true);
    final Queue<UpdateLabel> availableImageWorkers = new ConcurrentLinkedQueue<UpdateLabel>();
    final List<UpdateLabel> allImageWorkers=new ArrayList<UpdateLabel>();


    private volatile boolean disposed;
    
    private Font brokenFont;

    private IResolveChangeListener listener=new IResolveChangeListener(){

        public void changed( IResolveChangeEvent event ) {
            if (PlatformUI.getWorkbench().isClosing())
                return;
            if (event.getType() == Type.POST_CHANGE && event.getDelta() != null) {
                final List<IResolve> changed = new ArrayList<IResolve>();
                //  TODO enable when resolve information is available
//                updateCache(event.getDelta(), changed);

                if (!changed.isEmpty()) {
                    Runnable runnable = new Runnable(){
                        public void run() {
                            LabelProviderChangedEvent labelEvent = new LabelProviderChangedEvent(source,
                                    changed.toArray());
                            for( ILabelProviderListener l : listeners ) {
                                l.labelProviderChanged(labelEvent);
                            }
                            
                        }
                    };
                    
                    if( Display.getCurrent()==null ){
                        Display.getDefault().asyncExec(runnable);
                    }else{
                        runnable.run();
                    }
                }

            }
        }

        private void updateCache( IResolveDelta delta, List<IResolve> changed ) {

            if (delta.getKind() == Kind.REPLACED || delta.getKind() == Kind.REMOVED
                    || delta.getKind() == Kind.CHANGED) {
                if (delta.getResolve() != null) {
                    decorated.remove(delta.getResolve());
                    changed.add(delta.getResolve());
                }
                List<IResolveDelta> children = delta.getChildren();
                for( IResolveDelta delta2 : children ) {
                    updateCache(delta2, changed);
                }
            }
        }

    };
    
    /**
     * Prevent fetching of remote icons (localhost and files are okay)
     */
    private boolean decorateImages;

    /**
     * Wrap around the ResolveLabelProviderSimple and add some state markup.
     * 
     * @param resolveLabelProviderSimple
     */
    public ResolveTitlesDecorator( ResolveLabelProviderSimple resolveLabelProviderSimple ) {
        this( resolveLabelProviderSimple, false);
    }
    /**
     * @param resolveLabelProviderSimple
     * @param decorateImages
     */
    public ResolveTitlesDecorator( ResolveLabelProviderSimple resolveLabelProviderSimple, boolean decorateImages ) {
        this.source = resolveLabelProviderSimple;
        this.decorateImages=decorateImages;
        CatalogPlugin.addListener(listener);
        CatalogPlugin.removeListener(source);
        for( int i = 0; i < 4; i++ ) {
            UpdateLabel worker = new UpdateLabel(imagesToDecorate, images, false);
            availableImageWorkers.add(worker);
            allImageWorkers.add(worker);
        }
    }
    /*
     * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image,
     *      java.lang.Object)
     */
    public Image decorateImage( Image image, Object element ) {
       if( disposed ) return null;
        if (!(element instanceof IResolve))
            return null;

        IResolve resolve = (IResolve) element;
        
        if (images.containsKey(element)) {
            LabelData data = images.get(element);
            // if data is null then it is being loaded already so return
            if (data == null)
                return null;
        }

        // look image up in registry to see if it is already loaded
        ImageRegistry imageRegistry = CatalogUIPlugin.getDefault().getImageRegistry();
        Image i;
        synchronized (imageRegistry) {
            i = imageRegistry.get(resolve.getIdentifier().toString());
            // if it is loaded and not disposed then we're good, return it.
            if( i!=null && !i.isDisposed()){
                return i;
            }
            
            if (i!=null && i.isDisposed() )
                imageRegistry.remove(resolve.getIdentifier().toString());
        }
        
        // we tried to look up a cached version... If not around and viewer doesn't want decorated images then we'll return.
        
        if( !resolve.getID().isLocal() && !decorateImages ){
            return null;
        }
        
        // put an element in the map so that it will not be loaded again.
        images.put(resolve, null);

        // put resolve in queue for loading
        imagesToDecorate.offer(resolve);
        synchronized (imageRegistry) {
            // get a worker for loading images and schedule it.  
            // If pool is empty then don't worry request will be processed.
            UpdateLabel imageWorker = availableImageWorkers.poll();
            if( imageWorker!=null )
                imageWorker.schedule();
        }
        return null;

    }

    public String decorateText( String text, Object element ) {
        if( disposed ) return null;
        if (!(element instanceof IResolve)){
            return null;
        }

        IResolve resolve = (IResolve) element;
        
        if (decorated.containsKey(element)) {
            LabelData data = decorated.get(element);
            if (data == null){
                return null;
            }
            if( resolve instanceof IService ){
                if( resolve.getID().getTypeQualifier() != null ){
                    return data.text + " ("+resolve.getID().getTypeQualifier() +")";
                }
            }
            return data.text;            
        }

        decorated.put(resolve, null);
        if (resolve.getTitle() != null) {
            LabelData data = new LabelData();
            data.text = resolve.getTitle();
            decorated.put(resolve, data);       
            if( resolve instanceof IService ){
                if( resolve.getID().getTypeQualifier() != null ){
                    return data.text + " ("+resolve.getID().getTypeQualifier() +")";
                }
            }
            return data.text;            
        }
        toDecorate.offer(resolve);
        textWorker.schedule();
        return null;
    }

    public Color decorateBackground( Object element ) {
        return null;
    }
    public Color decorateForeground( Object element ) {
        if( disposed ) return null;
        if (!(element instanceof IResolve))
            return null;

        IResolve resolve = (IResolve) element;
        if (resolve.getStatus() == net.refractions.udig.catalog.IResolve.Status.BROKEN) {
            return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        }
        if (resolve.getStatus() == net.refractions.udig.catalog.IResolve.Status.RESTRICTED_ACCESS) {
            return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW);
        }
        return null;
    }
    
    public Font decorateFont( Object element ) {
        if (!(element instanceof IResolve))
            return null;

        IResolve resolve = (IResolve) element;
        if (resolve.getStatus() == net.refractions.udig.catalog.IResolve.Status.BROKEN 
                || resolve.getStatus() == net.refractions.udig.catalog.IResolve.Status.RESTRICTED_ACCESS) {
            return getBrokenFont();
        }
        return null;
    }

    private Font getBrokenFont() {
        if( brokenFont==null ){
            Font systemFont = Display.getCurrent().getSystemFont();
            FontData fd = systemFont.getFontData()[0];
            brokenFont=new Font(systemFont.getDevice(), fd.getName(), fd.getHeight(), SWT.ITALIC );
        }
        
        return brokenFont;
    }
    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
        assert Display.getCurrent()!=null;
        disposed=true;
        toDecorate.clear();
        textWorker.cancel();
        images.clear();
        for( UpdateLabel updater : allImageWorkers ) {
            updater.cancel();
        }
        // should clean up after hack
        decorated.clear();
        listeners.removeAll(instanceListeners);
        instanceListeners.clear();
        CatalogPlugin.removeListener(listener);
        if( brokenFont!=null )
            brokenFont.dispose();
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
     *      java.lang.String)
     */
    public boolean isLabelProperty( Object element, String property ) {
        return true;
    }
    public ILabelProvider getSource() {
        return source;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener( ILabelProviderListener listener ) {
        assert Display.getCurrent()!=null;
        listeners.remove(listener);
        instanceListeners.remove(listener);
    }

    /*
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void addListener( ILabelProviderListener listener ) {
        assert Display.getCurrent()!=null;
        listeners.add(listener);
        instanceListeners.add(listener);
    }

    /**
     * This Job executes in the background and tries to update the labels.
     * <p>
     * It can actually get "stuck" on any bad label or icon (such as a WMS that takes a while); so
     * it is important to have default titles that work okay.
     */
    private class UpdateLabel extends Job {

        DisplayUpdater updater;
        private Queue<IResolve> toDecorate;
        private Map<IResolve, LabelData> decorated;
        private boolean text;
        
        public UpdateLabel(Queue<IResolve> toDecorate, Map<IResolve, LabelData> decorated, boolean text) {
            // message is just for debugging since job is a system job
            super("Decorate Resolve "+(text?"Titles":"Images")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            setSystem(true);
            setPriority(DECORATE);
            this.toDecorate=toDecorate;
            this.decorated=decorated;
            this.text=text;
        }

        public IStatus run( final IProgressMonitor monitor ) {
            updater=new DisplayUpdater(monitor);
            try{
                while( true ) {
                    final IResolve element = toDecorate.poll();
                    if(element == null) break;
                    // check if some how request has already been fullfilled.
                    LabelData labelData = decorated.get(element);
                    if (text && labelData != null)
                        continue;
                    if (!text && labelData!=null)
                        continue;
                    
                    URL identifier = element.getIdentifier();
                    monitor.beginTask(Messages.ResolveTitlesDecorator_0 + identifier.getFile(),
                            IProgressMonitor.UNKNOWN);
                    if( monitor.isCanceled() ){
                        return Status.OK_STATUS;
                    }
                    LabelData data = new LabelData();
                    try {
                        if(text){
                        	if(element instanceof IGeoResource) {
                        		IGeoResource resource = (IGeoResource) element;
                        		data.text = resource.getInfo(monitor).getTitle();
                        		IService service = resource.service(monitor);
                        		service.getPersistentProperties().put(resource.getID() + "_title", data.text);
                        	} else if(element instanceof IService) {
                        		IService service = (IService) element;
                        		IServiceInfo info = service.getInfo(monitor);
                        		if( info != null ){
                        		    data.text = info.getTitle();
                        		    service.getPersistentProperties().put("title", data.text);
                        		}
                        	} else if(element instanceof IProcess) {
                        		IProcess proc = (IProcess) element;
                        		data.text = proc.getInfo(monitor).getTitle();
                        	} else if(element instanceof ISearch) {
                        		ISearch search = (ISearch) element;
                        		data.text = search.getInfo(monitor).getTitle();
                        	} else {
                        		IResolveFolder folder = (IResolveFolder) element;
                        		data.text = folder.getID().toString();
                        	}
                        }
                    } catch (Throwable e) {
                        CatalogUIPlugin.log("Error fetching the Title for the resource", e); //$NON-NLS-1$
                        continue;
                    }
                    try {
                        if(!text)
                            creatImage(data,element);
                    } catch (Throwable e) {
                        CatalogUIPlugin.log("Error fetching the Image for the resource", e); //$NON-NLS-1$
                        continue;
                    }
                    decorated.put(element, data);
                    if( monitor.isCanceled() )
                        return Status.OK_STATUS;
                    
                    addUpdate(monitor, element);
    
                }
                return Status.OK_STATUS;
            }finally{
                if( !text ){
                    // need to protect the items put in the image registry
                    // in addition to availableImageWorkers
                    synchronized (images) {
                        availableImageWorkers.add(this);
                    }
                }
            }

        }

        /**
         *
         * @param monitor
         * @param element
         */
        private void addUpdate( final IProgressMonitor monitor, final IResolve element ) {
            // we are processing as many resolve as possible.
            // so there is a possibility that we will resolve a few
            // before the view is updated so batch the updates
            // since the updates are in the updater thread
            // we have to synchronize on the updates list.
            synchronized (updater.updates) {
                if (updater.updates.isEmpty()){
                    updater=new DisplayUpdater(monitor);
                    
                    updater.updates.add(element);
                    final Display display = Display.getDefault();
                    display.asyncExec(new Runnable(){
                        public void run() {
                            display.timerExec(500, updater);
                        }
                    });
                }else{
                    updater.updates.add(element);
                }
            }
        }

        private Image creatImage( LabelData data, final IResolve element ) throws IOException {
            String key = element.getIdentifier().toString();
            ImageRegistry imageRegistry = CatalogUIPlugin.getDefault().getImageRegistry();
            // need to protect the items put in the image registry
            // in addition to availableImageWorkers
            Image i;
            synchronized (imageRegistry) {
                i = imageRegistry.get(key);
            }
            if (i==null || i.isDisposed()){
                data.image = CatalogUIPlugin.icon(element);
                imageDescriptorCache.put(element, data.image);
                try{
                    if( data.image!=null)
                        i = data.image.createImage();
                }catch (Throwable e) {
                    CatalogUIPlugin.log("Error creating the Image for the resource", e); //$NON-NLS-1$
                }
            }else{
                data.image=imageDescriptorCache.get(element);// get cached
            }
            if( i==null )
                i=CatalogUIPlugin.image(element);
            synchronized (imageRegistry) {
                Image i2 = imageRegistry.get(key);
                if( i2!=null && !i2.isDisposed()){
                    return i;
                }
                
                if (i2!=null && i2.isDisposed() )
                    imageRegistry.remove(key);
                
                imageRegistry.put(key, i);
            }
            return i;
        }

    }
    
    private class DisplayUpdater implements Runnable{

        IProgressMonitor monitor;
        Queue<IResolve> updates=new LinkedList<IResolve>();

        public DisplayUpdater( IProgressMonitor monitor2 ) {
            this.monitor=monitor2;
        }

        public void run() {
            if( disposed ) return ;

            while( !updates.isEmpty() ) {
                Object[] element;
                synchronized (updates) {
                    element = updates.toArray();
                    updates.clear();
                }
                for( ILabelProviderListener l : listeners ) {
                    if (monitor.isCanceled())
                        return;
                    l.labelProviderChanged(new LabelProviderChangedEvent(source, element));
                }
            }
        }
        
    }
    private static class LabelData {
        public ImageDescriptor image;
        String text;
    }

    
}
