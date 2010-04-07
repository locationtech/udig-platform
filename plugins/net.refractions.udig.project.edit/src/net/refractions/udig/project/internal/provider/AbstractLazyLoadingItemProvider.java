package net.refractions.udig.project.internal.provider;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.project.edit.internal.Messages;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class AbstractLazyLoadingItemProvider extends ItemProviderAdapter {

    protected volatile ChildFetcher childFetcher;

    /**
     * Gets the "real" list of children so that the children in childFetcher doesn't have to be kept in sync with
     * the real data... 
     *
     * @param object object that is the parent.
     * @return
     */
    @SuppressWarnings("unchecked")
    protected Collection< ? extends Object> getConcreteChildren(Object object){ return super.getChildren(object); }

    /**
     * Returns the child at the indicated index or null if there is fewer children
     *
     * @param object the parent object.
     * @param childIndex the index of the child to return.
     * @return the child at the indicated index or null if there is fewer children
     */
    @SuppressWarnings("unchecked")
    public Object getChild( Object object, int childIndex ) {
        if (!(object instanceof EObject) )
            return null;
        int currentIndex=0;
        Collection<? extends EStructuralFeature> features = getChildrenFeatures(object);
        for( EStructuralFeature feature : features ) {
            Object value = getFeatureValue((EObject) object, feature);
            if( value instanceof Collection ){
                Collection<Object> collection = (Collection) value;
                
                // index of child is in another feature
                if( currentIndex+collection.size()<childIndex )
                    continue;
                // ok the child is in this feature lets find it.
                if( value instanceof List ){
                    List list = ((List)value);
                    if ( currentIndex+list.size()<=childIndex ){
                        currentIndex+=list.size();
                        continue;
                    }
                    return list.get(childIndex);
                }else{
                    Iterator<Object> iter=collection.iterator();
                    while (currentIndex<childIndex){
                        iter.next();
                        currentIndex++;
                    }
                    return iter.next();
                }
            }
            currentIndex++;
            if( currentIndex==childIndex )
                return value;
        }
        return null;
    }

    @Override
    public Collection getElements( Object arg0 ) {
        return getChildren(arg0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection getChildren( Object object ) {
        if( Display.getCurrent()==null )
            throw new SWTException("Invalid Thread access, not in UI thread."); //$NON-NLS-1$
           
        if (!getChildFetcher().dataReady) {
        	getChildFetcher().parent = object;
        	getChildFetcher().schedule();
        	return getChildFetcher().getChildren();
        }
        
        return getConcreteChildren(object);
    }
    
    public ChildFetcher getChildFetcher() {
        if( childFetcher==null ){
            synchronized (this) {
                if( childFetcher==null ){
                    childFetcher=createChildFetcher();
                }
            }
        }
            
        return childFetcher;
    }
    
    private static final LoadingPlaceHolder LOADING_PLACEHOLDER = new LoadingPlaceHolder(){
        
        public Image getImage() {
            return null;
        }

        public String getText() {
            return Messages.ProjectItemProvider_loading;
        }
        @Override
        public String toString() {
            return getText();
        }

    };

    /**
     * Returns a placeholder item.  It should be a stateless object and a singleton.
     *
     * @return a placeholder item.  It should be a stateless object and a singleton.
     */
    protected LoadingPlaceHolder getLoadingItem() {
        return LOADING_PLACEHOLDER;
    }

    /**
     * Creates a new instance of a {@link ChildFetcher}.  Does not retrieve an old instance.
     *
     * @return new instance of a {@link ChildFetcher}
     */
    protected ChildFetcher createChildFetcher() {
        return new ChildFetcher(this);
    }

    public AbstractLazyLoadingItemProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
    }
    
}