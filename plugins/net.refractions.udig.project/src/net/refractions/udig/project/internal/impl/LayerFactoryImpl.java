/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.refractions.udig.project.internal.impl;

import java.awt.Color;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.project.interceptor.LayerInterceptor;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.LayerFactory;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.ProgressManager;
import net.refractions.udig.ui.palette.ColourScheme;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Layer Factory</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link net.refractions.udig.project.internal.impl.LayerFactoryImpl#getMap <em>Map</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class LayerFactoryImpl extends EObjectImpl implements LayerFactory {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected LayerFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected EClass eStaticClass() {
        return ProjectPackage.eINSTANCE.getLayerFactory();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Map getMap() {
        if (eContainerFeatureID != ProjectPackage.LAYER_FACTORY__MAP)
            return null;
        return (Map) eContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setMap( Map newMap ) {
        if (newMap != eContainer
                || (eContainerFeatureID != ProjectPackage.LAYER_FACTORY__MAP && newMap != null)) {
            if (EcoreUtil.isAncestor(this, (EObject) newMap))
                throw new IllegalArgumentException(
                        "Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newMap != null)
                msgs = ((InternalEObject) newMap).eInverseAdd(this,
                        ProjectPackage.MAP__LAYER_FACTORY, Map.class, msgs);
            msgs = eBasicSetContainer((InternalEObject) newMap, ProjectPackage.LAYER_FACTORY__MAP,
                    msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.LAYER_FACTORY__MAP, newMap, newMap));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @SuppressWarnings("unchecked")
    public NotificationChain eInverseAdd( InternalEObject otherEnd, int featureID, Class baseClass,
            NotificationChain msgs ) {
        if (featureID >= 0) {
            switch( eDerivedStructuralFeatureID(featureID, baseClass) ) {
            case ProjectPackage.LAYER_FACTORY__MAP:
                if (eContainer != null)
                    msgs = eBasicRemoveFromContainer(msgs);
                return eBasicSetContainer(otherEnd, ProjectPackage.LAYER_FACTORY__MAP, msgs);
            default:
                return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null)
            msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @SuppressWarnings("unchecked")
    public NotificationChain eInverseRemove( InternalEObject otherEnd, int featureID,
            Class baseClass, NotificationChain msgs ) {
        if (featureID >= 0) {
            switch( eDerivedStructuralFeatureID(featureID, baseClass) ) {
            case ProjectPackage.LAYER_FACTORY__MAP:
                return eBasicSetContainer(null, ProjectPackage.LAYER_FACTORY__MAP, msgs);
            default:
                return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public NotificationChain eBasicRemoveFromContainer( NotificationChain msgs ) {
        if (eContainerFeatureID >= 0) {
            switch( eContainerFeatureID ) {
            case ProjectPackage.LAYER_FACTORY__MAP:
                return eContainer.eInverseRemove(this, ProjectPackage.MAP__LAYER_FACTORY,
                        Map.class, msgs);
            default:
                return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null,
                msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Object eGet( EStructuralFeature eFeature, boolean resolve ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.LAYER_FACTORY__MAP:
            return getMap();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void eSet( EStructuralFeature eFeature, Object newValue ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.LAYER_FACTORY__MAP:
            setMap((Map) newValue);
            return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void eUnset( EStructuralFeature eFeature ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.LAYER_FACTORY__MAP:
            setMap((Map) null);
            return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public boolean eIsSet( EStructuralFeature eFeature ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.LAYER_FACTORY__MAP:
            return getMap() != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * Creates a list of {@linkplain Layer}objects from the provided selection.
     * 
     * @param selection A selection of CatalogEntries obtained from a {@linkplain CatalogTreeViewer}
     *        object.
     * @return a list of {@linkplain Layer}objects from the provided selection.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public List<Layer> getLayers( List selection ) throws IOException {
        List<Layer> layers = new LinkedList<Layer>();
        for( Iterator<Object> iter = selection.iterator(); iter.hasNext(); ) {
            Object obj = iter.next();
            if (obj instanceof IService) {
                layers.addAll(getLayers((IService) obj));
            } else if (obj instanceof IGeoResource) {
                IGeoResource entry = (IGeoResource) obj;
                Layer ref = createLayer(entry);

                if (ref != null)
                    layers.add(ref);
            }
        }
        return layers;
    }

    public List<Layer> getLayers( IService service ) throws IOException {
        Layer ref = null;
        List<Layer> layers = new LinkedList<Layer>();

        Iterator< ? extends IGeoResource> rentryIter = service.resources(null).iterator();
        while( rentryIter.hasNext() ) {
            IGeoResource entry = rentryIter.next();

            ref = createLayer(entry);

            if (ref != null)
                layers.add(ref);
        }
        return layers;
    }

    // /**
    // * Creates a LayerRef for WMSs.
    // *
    // * @param service the registry entry parent of the CatalogEntry that the Layer ref references.
    // * @param geoResource the CatalogEntry that the Ref refers to.
    // * @return a LayerRef for DataStores.
    // * @throws IOException
    // */
    // protected Layer createWMSLayer( IService service, IGeoResource geoResource ) throws
    // IOException {
    //
    // org.geotools.data.ows.Layer wmslayer = null;
    //
    // IGeoResourceInfo info = geoResource.resolve(IGeoResourceInfo.class, null);
    // String layerName = info.getName();
    // if (layerName == null || layerName.length() == 0) {
    //            throw new IllegalArgumentException("Cannot determine name of resource."); //$NON-NLS-1$
    // }
    //
    // WebMapServer wms = service.resolve(WebMapServer.class, null);
    // if (wms == null) {
    //            throw new IOException("Cannot communicate with Web Map Server."); //$NON-NLS-1$
    // }
    // org.geotools.data.ows.Layer[] layers = WMSUtils.getNamedLayers(wms.getCapabilities());
    // for( org.geotools.data.ows.Layer layer : layers ) {
    // String targetName = layer.getName();
    // if (targetName.equals(layerName)) {
    // wmslayer = layer;
    // break;
    // }
    // }
    //
    // if (wmslayer == null) {
    // throw new IOException(
    //                    "This resource (" + layerName + ") is not a part of the service. This is likely caused by inconsistent data returned by a search."); //$NON-NLS-1$//$NON-NLS-2$
    // }
    //        
    // Layer layer = ProjectFactory.eINSTANCE.createLayer();
    //
    // layer.setID(geoResource.getIdentifier());
    //
    // return layer;
    // }

    /**
     * Returns a LayerFactory object
     * 
     * @return a LayerFactory object.
     */
    public static LayerFactoryImpl create() {
        return new LayerFactoryImpl();
    }

    /**
     * Creates a layer from a service and a resource. The layer is represented the data in resource.
     * May return null if it cannot resolve the service.
     * 
     * @param service
     * @param resource
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public Layer createLayer( IGeoResource resource ) throws IOException {
        IService service = resource.service(ProgressManager.instance().get());

        if (service == null) {
            return null;
        }
        // check that the service is part of catalog... If not add
        if (CatalogPlugin.getDefault().getLocalCatalog().getById(IService.class, service.getID(),
                new NullProgressMonitor()) == null) {
            CatalogPlugin.getDefault().getLocalCatalog().add(resource.service(null));
        }

        LayerImpl layer = (LayerImpl) ProjectFactory.eINSTANCE.createLayer();

        layer.setResourceID(resource.getID());

        if (layer == null) {
            throw new IOException(
                    "Unable to create layer from resource '" + resource.getIdentifier() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // process the style content extension point to initially populate
        // the style blackboard with style info
        // TODO: the style objects need access to preference system
        final Layer theLayer = layer;

        ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
        List<IResolve> resolves = localCatalog.find(layer.getResourceID(), ProgressManager
                .instance().get());
        EList resources = new EDataTypeUniqueEList(IGeoResource.class, this,
                ProjectPackage.LAYER__GEO_RESOURCES);
        LayerResource preferredResource = null;
        for( IResolve resolve : resolves ) {
            if (resolve instanceof IGeoResource) {
                LayerResource layerResource = new LayerResource((LayerImpl) layer,
                        (IGeoResource) resolve);
                if (resolve.getID().equals(layer.getResourceID())) {
                    resources.add(0, layerResource);
                } else {
                    resources.add(layerResource);
                }
                if (resolve == resource)
                    preferredResource = layerResource;
            }
        }

        ((LayerImpl) layer).geoResources = resources;
        layer.setGeoResource(preferredResource);

        // determine the default colour
        ColourScheme colourScheme = getColorScheme();
        Color colour = colourScheme.addItem(theLayer.getID().toString());
        theLayer.setDefaultColor(colour);

        runLayerCreatedInterceptor(layer);

        return layer;
    }

    private ColourScheme getColorScheme() {
        if (getMap() == null) {
            return ColourScheme.getDefault(PlatformGIS.getColorBrewer().getPalettes()[0]);
        }
        return getMap().getColourScheme();
    }

    private void runLayerCreatedInterceptor( Layer layer ) {
        List<IConfigurationElement> list = ExtensionPointList
                .getExtensionPointList(LayerInterceptor.EXTENSION_ID);
        for( IConfigurationElement element : list ) {
            if (element.getName().equals(LayerInterceptor.CREATED_ID)) {
                String attribute = element.getAttribute("name"); //$NON-NLS-1$
                try {
                    LayerInterceptor interceptor = (LayerInterceptor) element
                            .createExecutableExtension("class"); //$NON-NLS-1$
                    interceptor.run(layer);
                } catch (CoreException e) {
                    ProjectPlugin
                            .log(
                                    "Error creating class: " + element.getAttribute("class") + " part of layer interceptor: " + attribute, e); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                } catch (Throwable t) {
                    ProjectPlugin.log("error running interceptor: " + attribute, t); //$NON-NLS-1$
                }
            }
        }
    }

} // LayerFactoryImpl
