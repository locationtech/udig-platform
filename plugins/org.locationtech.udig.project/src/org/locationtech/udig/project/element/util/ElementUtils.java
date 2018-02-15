package org.locationtech.udig.project.element.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.geotools.data.ows.Layer;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.udig.catalog.util.CRSUtil;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.ui.PlatformGIS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Utility class to analyze Elements such as Layers, Projects, Map, etc. Classes migrated from
 * BasicWMSRenderer to de-couple bundles from each other.
 * 
 * @author fgdrf
 *
 */
public class ElementUtils {

    /**
     * We have made this visible so that WMSDescribeLayer (used by InfoView2) can figure out how to
     * make the *exact* same request in order to a getInfo operation. We should really store the
     * last request on the layer blackboard for this into module communication.
     * 
     * @return SRS code
     */
    public static String findRequestCRS(List<Layer> layers, CoordinateReferenceSystem viewportCRS,
            IMap map, List<String> preferredCodesEPSG) {
        String requestCRS = null;

        if (layers == null || layers.isEmpty()) {
            return null;
        }

        Collection<String> viewportEPSG = extractEPSG(map, viewportCRS, preferredCodesEPSG);
        if (viewportEPSG != null) {
            String match = matchEPSG(layers, viewportEPSG);
            if (match != null) {
                return match;
            }
        }
        if (preferredCodesEPSG != null) {
            for (String epsgCode : preferredCodesEPSG) {
                if (epsgCode != null && matchEPSG(layers, epsgCode)) {
                    return epsgCode;
                }
            }
        }

        Layer firstLayer = layers.get(0);
        for (Object object : firstLayer.getSrs()) {
            String epsgCode = (String) object;

            try {
                // Check to see if *we* can actually use this code first.
                CoordinateReferenceSystem check = CRS.decode(epsgCode);
                if (check == null) {
                    continue; // skip this one!
                }
            } catch (NoSuchAuthorityCodeException e) {
                continue; // skip this one we do not have an authority for it
            } catch (FactoryException e) {
                e.printStackTrace(); // internal trouble :(
            }

            if (matchEPSG(layers, epsgCode)) {
                requestCRS = epsgCode;
                return requestCRS;
            }
        }
        return requestCRS;
    }

    /**
     * @see #findRequestCRS(List, CoordinateReferenceSystem, IMap, List)
     */
    public static String findRequestCRS(List<Layer> layers, CoordinateReferenceSystem viewportCRS,
            IMap map) {
        return findRequestCRS(layers, viewportCRS, map, null);
    }

    /**
     * Quickly check provided layers to ensure they have the provided epsgCode in common.
     * 
     * @param layers
     * @param epsgCode
     * @return
     */
    public static boolean matchEPSG(List<Layer> layers, String epsgCode) {
        boolean match = true;
        for (Layer layer : layers) {
            Set<String> srs = layer.getSrs();
            if (!srs.contains(epsgCode)) {
                match = false;
                break;
            }
        }
        return match;
    }

    public static String matchEPSG(List<Layer> layers, Collection<String> epsgCodes) {
        for (String epsg : epsgCodes) {
            if (matchEPSG(layers, epsg))
                return epsg;
        }
        return null;
    }

    public static Collection<String> extractEPSG(final IMap map,
            final CoordinateReferenceSystem crs, List<String> preferredCodesEPSG) {

        final Collection<String> codes = new ArrayList<String>();
        if (CRS.equalsIgnoreMetadata(crs, DefaultGeographicCRS.WGS84)) {
            if (preferredCodesEPSG != null) {
                for (String epsgCode : preferredCodesEPSG) {
                    if (epsgCode != null) {
                        codes.add(epsgCode);
                    }
                }
            }
            return codes;
        }
        codes.addAll(CRSUtil.extractAuthorityCodes(crs));

        return codes;
    }

    public static final Collection<String> updateViewportModelCRS(final IMap map,
            final CoordinateReferenceSystem crs, final String blackboardKey,
            final String blackboardValueNotMatch) {
        final Collection<String> codes = new ArrayList<String>();
        boolean search = map.getBlackboard().get(blackboardKey) != blackboardValueNotMatch;

        if (search) {
            PlatformGIS.syncInDisplayThread(new Runnable() {
                public void run() {
                    Shell shell = Display.getCurrent().getActiveShell();

                    ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
                    try {
                        dialog.run(false, true, new IRunnableWithProgress() {

                            public void run(IProgressMonitor monitor)
                                    throws InvocationTargetException, InterruptedException {
                                CoordinateReferenceSystem found = CRSUtil.findEPSGCode(crs,
                                        monitor);
                                if (found == null) {
                                    return;
                                }
                                // sets the found CRS to viewPortModel of the map
                                ViewportModel model = (ViewportModel) map.getViewportModel();
                                model.eSetDeliver(false);
                                try {
                                    model.setCRS(found);
                                    codes.addAll(CRSUtil.extractAuthorityCodes(found));
                                } finally {
                                    model.eSetDeliver(true);
                                }
                            }

                        });
                    } catch (Exception e) {
                        ProjectPlugin.log("Error tracking down EPSG Code", e);
                    } finally {
                        setMapBlackboardValue(map, blackboardKey, blackboardValueNotMatch);
                    }
                }
            });
        }
        return codes;
    }

    public static void setMapBlackboardValue(final IMap map, String blackboardKey,
            String blackboardValueNotMatch) {
        map.getBlackboard().put(blackboardKey, blackboardValueNotMatch);
    }

}
