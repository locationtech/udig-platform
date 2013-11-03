/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.common;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.ui.graphics.SLDs;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.viewers.TableViewer;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayerDescriptor;

import org.locationtech.udig.style.advanced.common.styleattributeclasses.FeatureTypeStyleWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.StyleWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.SymbolizerWrapper;
import static org.locationtech.udig.style.advanced.utils.Utilities.*;

/**
 * An abstract manager for styles.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public abstract class StyleManager {

    public static String POINTSTYLEFOLDER = "pointstylefolder"; //$NON-NLS-1$
    public static String POLYGONSTYLEFOLDER = "polygonstylefolder"; //$NON-NLS-1$
    public static String LINESTYLEFOLDER = "linestylefolder"; //$NON-NLS-1$

    protected TableViewer stylesViewer;
    protected StyleWrapper currentSelectedStyleWrapper;
    protected File styleFolderFile;

    /**
     * Reload the style folder content in the {@link TableViewer viewer}.
     */
    public void reloadStyleFolder() {
        File[] sldFilesArray = styleFolderFile.listFiles(new FilenameFilter(){
            public boolean accept( File dir, String name ) {
                return name.toLowerCase().endsWith(SLD_EXTENTION);
            }
        });

        List<StyleWrapper> newStylesList = new ArrayList<StyleWrapper>();
        for( File file : sldFilesArray ) {
            try {
                StyledLayerDescriptor sld = readStyle(file);
                Style style = SLDs.getDefaultStyle(sld);
                String name = FilenameUtils.removeExtension(file.getName());
                style.setName(name);

                StyleWrapper styleWrapper = new StyleWrapper(style);
                newStylesList.add(styleWrapper);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        stylesViewer.setInput(newStylesList);
    }

    /**
     * Dump the given style to disk.
     * 
     * @param styleWrapper the style to write to disk.
     * @throws Exception
     */
    protected void styleToDisk( StyleWrapper styleWrapper ) throws Exception {
        String name = styleWrapper.getName();
        String styleStr = styleWrapper.toXml();
        File newFile = new File(styleFolderFile, name + SLD_EXTENTION);
        FileUtils.writeStringToFile(newFile, styleStr);
    }

    /**
     * Set the styles folder to a new location and reload the styles contained.
     * 
     * @param styleFolderFile the new folder to set.
     */
    public void setStyleFolderFile( File styleFolderFile ) {
        this.styleFolderFile = styleFolderFile;
        reloadStyleFolder();
    }

    /**
     * Getter for the style folder.
     * 
     * @return the style folder.
     */
    public File getStyleFolderFile() {
        return styleFolderFile;
    }

    /**
     * Getter for the internal table viewer.
     * 
     * @return the {@link TableViewer}.
     */
    public TableViewer getTableViewer() {
        return stylesViewer;
    }

    /**
     * The {@link StyleWrapper} that was last selected in the viewer.
     * 
     * @return the selected style.
     */
    public StyleWrapper getCurrentSelectedStyle() {
        return currentSelectedStyleWrapper;
    }

    /**
     * Getter for the styles contained in the viewer.
     * 
     * @return the styles in the viewer.
     */
    @SuppressWarnings("unchecked")
    public List<StyleWrapper> getStyles() {
        List<StyleWrapper> styles = (List<StyleWrapper>) stylesViewer.getInput();
        return styles;
    }

    /**
     * Add a style to the {@link TableViewer viewer}.
     * 
     * @param styleWrapper the {@link StyleWrapper} to add.
     * @throws Exception 
     */
    public void addStyle( StyleWrapper styleWrapper ) throws Exception {
        styleToDisk(styleWrapper);
        reloadStyleFolder();
    }

    /**
     * Remove a style from the {@link TableViewer viewer}.
     * 
     * @param styleWrapper the {@link StyleWrapper} to remove.
     * @throws IOException 
     */
    public void removeStyle( StyleWrapper styleWrapper ) throws IOException {
        String name = styleWrapper.getName();

        File styleFile = new File(styleFolderFile, name + SLD_EXTENTION);
        if (styleFile.exists()) {
            FileUtils.forceDelete(styleFile);
        }
        reloadStyleFolder();
    }

    /**
     * Exports a style to file taking care of external graphics.
     * 
     * <p>This copies the graphics to the file and puts relative paths in the sld.</p>
     * 
     * @param styleWrapper the style to dump
     * @param file the file of the resource that need the sld (for ex. a shapefile).
     * @throws Exception
     */
    public void exportStyle( StyleWrapper styleWrapper, File file ) throws Exception {
        File parentFolder = file.getParentFile();
        String baseName = FilenameUtils.getBaseName(file.getAbsolutePath());

        int index = 1;

        List<FeatureTypeStyleWrapper> featureTypeStylesWrapperList = styleWrapper.getFeatureTypeStylesWrapperList();
        for( FeatureTypeStyleWrapper featureTypeStyleWrapper : featureTypeStylesWrapperList ) {
            List<RuleWrapper> rulesWrapperList = featureTypeStyleWrapper.getRulesWrapperList();
            for( RuleWrapper ruleWrapper : rulesWrapperList ) {
                SymbolizerWrapper symbolizersWrapper = ruleWrapper.getGeometrySymbolizersWrapper();
                String externalGraphicPath = symbolizersWrapper.getExternalGraphicPath();
                if (externalGraphicPath != null && externalGraphicPath.length() > 0) {
                    File graphicsFile = new File(externalGraphicPath);
                    String extension = FilenameUtils.getExtension(graphicsFile.getName());
                    String newImageName = baseName + "_" + index + "." + extension; //$NON-NLS-1$ //$NON-NLS-2$
                    index++;
                    File newFile = new File(parentFolder, newImageName);
                    FileUtils.copyFile(graphicsFile, newFile);
                    symbolizersWrapper.setExternalGraphicPath(newFile.getAbsolutePath());
                }
            }
        }

        String styleString = styleWrapper.toXml();

        File newFile = new File(parentFolder, baseName + SLD_EXTENTION);
        FileUtils.writeStringToFile(newFile, styleString);
    }

}
