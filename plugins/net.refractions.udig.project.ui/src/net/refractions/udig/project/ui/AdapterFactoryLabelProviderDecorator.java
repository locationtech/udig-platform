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
package net.refractions.udig.project.ui;

import net.refractions.udig.project.internal.provider.LoadingPlaceHolder;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.PlatformUI;

/**
 * Connects Layers with the usual workbench decorators.
 * <p>
 * Similar to the usual eclipse boilerplate code:<pre><code>
 * new DecoratingLabelProvider(
 *     ProjectExplorer.getProjectExplorer().getAdapterFactory(),
 *     PlatformUI.getWorkbench().getDecoratorManager()
 * )
 * </code></pre>
 * 
 * @author jones
 * @since 0.6.0
 */
public class AdapterFactoryLabelProviderDecorator extends DecoratingLabelProvider  {

    StructuredViewer viewer;

    public AdapterFactoryLabelProviderDecorator( AdapterFactory factory, StructuredViewer viewer ) {
        super(new LabelProvider(factory), getWorkbenchDecorators() );
        this.viewer=viewer;
    }

    /** Check to see that our lightweight decorators make it into the mix */
    static final ILabelDecorator getWorkbenchDecorators(){
        IDecoratorManager manager = PlatformUI.getWorkbench().getDecoratorManager();
        ILabelDecorator decorator = manager.getLabelDecorator();
        return decorator;
    }

    /**
     * @see org.eclipse.jface.viewers.DecoratingLabelProvider#getText(java.lang.Object)
     */
    public String getText( Object element ) {
        if( element instanceof LoadingPlaceHolder ){
            return ((LoadingPlaceHolder)element).getText();
        }
        if( !viewer.getControl().isDisposed() )
            return super.getText(element);
        return null;
    }
    @Override
    public Image getImage( Object element ) {
        if( element instanceof LoadingPlaceHolder ){
            return ((LoadingPlaceHolder)element).getImage();
        }
        if( !viewer.getControl().isDisposed() )
            return super.getImage(element);
        return null;
    }
    
    @Override
    public void dispose() {
        getLabelProvider().dispose();
        super.dispose();
    }

    private static class LabelProvider extends AdapterFactoryLabelProvider implements IColorProvider{

        public LabelProvider(AdapterFactory adapterFactory) {
            super(adapterFactory);
        }

        public Color getForeground(Object object) {
             // Get the adapter from the factory.
            //
            IColorProvider colorProvider = (IColorProvider)adapterFactory.adapt(object, IColorProvider.class);
            if( colorProvider!=null ){
                return colorProvider.getForeground(object);
            }

            IItemLabelProvider itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(object, IItemLabelProvider.class);
            if( itemLabelProvider instanceof IColorProvider ){
                colorProvider=(IColorProvider) itemLabelProvider;
                return colorProvider.getForeground(object);
            }
            return null;
        }

        public Color getBackground(Object object) {
             // Get the adapter from the factory.
            //
            IColorProvider colorProvider = (IColorProvider)adapterFactory.adapt(object, IColorProvider.class);
            if( colorProvider!=null ){
                return colorProvider.getBackground(object);
            }

            IItemLabelProvider itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(object, IItemLabelProvider.class);
            if( itemLabelProvider instanceof IColorProvider ){
                colorProvider=(IColorProvider) itemLabelProvider;
                return colorProvider.getBackground(object);
            }
            return null;
        }
        
        
        
    }
    
}