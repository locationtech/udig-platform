/**
 * <copyright></copyright> $Id$
 */
package net.refractions.udig.project.internal.render.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.render.CompositeRenderContext;
import net.refractions.udig.project.internal.render.RenderContext;
import net.refractions.udig.project.render.ICompositeRenderContext;
import net.refractions.udig.project.render.IRenderContext;

/**
 * Default Implementation
 * 
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class CompositeRenderContextImpl extends RenderContextImpl implements CompositeRenderContext {
    /**
     * The cached value of the '{@link #getContextsInternal() <em>Contexts Internal</em>}'
     * reference list. 
     * 
     * @see #getContextsInternal()
     */
    protected final Collection<RenderContext> contextsInternal = Collections.synchronizedSortedSet(new TreeSet<RenderContext>());
    private final Set<CompositeContextListener> listeners=new CopyOnWriteArraySet<CompositeContextListener>();

    public CompositeRenderContextImpl() {
        super();
    }

    public CompositeRenderContextImpl( CompositeRenderContextImpl impl ) {
        super(impl);
        assert assertNoSelfReference(this, this, impl.contextsInternal);
        synchronized (impl.contextsInternal) {
            for( RenderContext context : impl.contextsInternal ) {
                if( context == impl ){
                    contextsInternal.add(this);
                }else
                    contextsInternal.add(context.copy());
            }
        }
    }

    public boolean isVisible() {
        if (getLayer() != null && getLayer().isVisible())
            return true;
        for( Iterator iter = getContexts().iterator(); iter.hasNext(); ) {
            IRenderContext context = (IRenderContext) iter.next();
            if (context == this)
                continue;
            if (context.isVisible())
                return true;
        }
        return false;
    }

    public List<Layer> getLayersInternal() {
        List<Layer> list = new ArrayList<Layer>();
        synchronized (contextsInternal) {
            for( RenderContext context : contextsInternal )
                list.add(context.getLayerInternal());
        }
        return Collections.unmodifiableList(list);
    }

    @SuppressWarnings("unchecked")
    public List getLayers() {
        return getLayersInternal();
    }

    @SuppressWarnings("unchecked")
    public List getContexts() {
        synchronized (contextsInternal) {
            return Collections.unmodifiableList(new ArrayList<Object>(contextsInternal));
        }
    }

	@Override
	public String toString() {
        if( contextsInternal.isEmpty() ){
            return super.toString();
        }
        
		StringBuffer buffer = new StringBuffer("{CompositeContext: "); //$NON-NLS-1$
        synchronized (contextsInternal) {
                
    		for (RenderContext context : contextsInternal) {
    			if (context==this )
    				buffer.append( "\n -" + super.toString() ); //$NON-NLS-1$
    			else
    				buffer.append("\n -" + context.toString()); //$NON-NLS-1$
    		}
            buffer.append("\n}"); //$NON-NLS-1$
    		return buffer.length()==0?super.toString():buffer.toString();
        }
	}

	@Override
	public void setStatus(int status) {
		if (getContexts().size() == 0) {
			super.setStatus(status);
			return;
		} 

        synchronized (contextsInternal) {
            
            for (RenderContext context : contextsInternal) {
                if( context!=this)
                    context.setStatus(status);
                else
                    super.setStatus(status);
            }
        }
	}

    @Override
    public void setStatusMessage( String message ) {
        if (getContexts().size() == 0) {
            super.setStatusMessage(message);
            return;
        }
        synchronized (contextsInternal) {
                for( RenderContext context : contextsInternal ) {
                    if (context != this)
                        context.setStatusMessage(message);
                    else
                        super.setStatusMessage(message);
                }
        }

    }

    @Override
    public void dispose() {
        super.dispose();
        synchronized (contextsInternal) {
            for( RenderContext context : contextsInternal ) {
                if (context != this)
                    ((RenderContextImpl) context).dispose();
            }
        }
    }

    public void addListener( CompositeContextListener contextListener ) {
        listeners.add(contextListener);
    }

    public void removeListener( CompositeContextListener contextListener ) {
        listeners.remove(contextListener);
    }

    private void notifyListeners(List<RenderContext> contexts, boolean added){
        for( CompositeContextListener l : listeners ) {
            l.notifyChanged(this, contexts, added);
        }
    }
    
    public void clear() {
        List<RenderContext> contexts;
        synchronized (contextsInternal) {
            contexts = new ArrayList<RenderContext>(contextsInternal);
        }
        contextsInternal.clear();
        if( !contexts.isEmpty() )
            notifyListeners(contexts, false);
    }

    public void addContexts( Collection<? extends RenderContext> contexts ) {
        if( contexts.isEmpty() )
            return ;
        assert assertNoSelfReference(this, this, contexts);
        contextsInternal.addAll(contexts);
        notifyListeners(new ArrayList<RenderContext>(contexts), true);
    }

    /**
     * For testing and assertions that the context is in good state.
     * 
     * @return
     */
    public static boolean assertNoSelfReference( IRenderContext parent, IRenderContext reference, Collection<? extends IRenderContext> contexts ) {
        for( IRenderContext context : contexts ) {

            if( context==parent )
                continue;
            
            if( context==reference )
                return false;
            
            if( context instanceof ICompositeRenderContext){
                ICompositeRenderContext comp = (ICompositeRenderContext)context;
                List<IRenderContext> children = comp.getContexts();
                for( IRenderContext context2 : children ) {
                    if( context2==null )
                        return false;

                    if( context2 instanceof ICompositeRenderContext){
                        if( !assertNoSelfReference(context2, reference, ((ICompositeRenderContext)context2).getContexts() ) )
                            return false;
                    }
                }
            }
        }
        return true;
    }

    public void removeContexts( Collection<? extends RenderContext> contexts ) {
        if( contexts.isEmpty() )
            return ;
        contextsInternal.removeAll(contexts);
        notifyListeners(new ArrayList<RenderContext>(contexts), false);
    }
    
    @Override
    public CompositeRenderContextImpl copy() {
        return new CompositeRenderContextImpl(this);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        
        for( RenderContext context : contextsInternal ) {
            if( context==this )
                result = PRIME * result + super.hashCode();
            else
                result = PRIME * result + context.hashCode();   
        }
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if( !(obj instanceof CompositeRenderContextImpl) )
            return false;
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final CompositeRenderContextImpl other = (CompositeRenderContextImpl) obj;
        if (contextsInternal == null) {
            if (other.contextsInternal != null)
                return false;
        } else if (!contextsEqual(other))
            return false;
        return true;
    }
    
    @Override
    public int compareTo( RenderContext o ) {
        if( o==this )
            return 0;
        int result = super.compareTo(o);
        // check the order of children also.  If they are not the same the return -1.
        // this is done so that TreeMap and TreeSet will not think 2 contexts are the same if they
        // have the same layer.  identity also takes the child contexts into account.
        if( result == 0 ){
            if (o instanceof ICompositeRenderContext) {
                ICompositeRenderContext comp = (ICompositeRenderContext) o;
                if( contextsEqual(comp) )
                    return 0;
                else
                    return -1; 
            }else{
                return -1;
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private boolean contextsEqual( ICompositeRenderContext o ) {
        if( o.getContexts().size()!=contextsInternal.size() )
            return false;
        
        Iterator<IRenderContext> iter1=getContexts().iterator();
        Iterator<IRenderContext> iter2=o.getContexts().iterator();
        while ( iter1.hasNext() ){
            IRenderContext o1 = iter1.next();
            IRenderContext o2 = iter2.next();
            
            if( o1==this ){
                if ( o2==o )
                    continue;
                else
                    return false;
            }
            if( !o1.equals(o2) )
                return false;
        }
        return true;
    }
	
} // CompositeRenderContextImpl
