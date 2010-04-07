/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.validation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.core.enums.Priority;
import net.refractions.udig.issues.FeatureIssue;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.validation.internal.Messages;

import org.geotools.validation.Validation;
import org.geotools.validation.ValidationResults;
import org.opengis.feature.simple.SimpleFeature;

/**
 * A generic version of the validation results class which returns mostly everything you would want
 * to know about the validation results.
 * <p>
 * </p>
 * 
 * @since 1.0.1
 */
public class GenericValidationResults implements ValidationResults, Iterable<SimpleFeature> {
    public ArrayList<Validation> validationList;
    public ArrayList<SimpleFeature> failedFeatures;
    public ArrayList<SimpleFeature> warningFeatures;
    public ArrayList<String> failureMessages;
    public ArrayList<String> warningMessages;
    public ArrayList<FeatureIssue> issues;
    
    /**
     * GenericValidationResults constructor.
     * <p>
     * Description
     * </p>
     * 
     */
    public GenericValidationResults() {
        validationList = new ArrayList<Validation>();
        failedFeatures = new ArrayList<SimpleFeature>();
        warningFeatures = new ArrayList<SimpleFeature>();
        failureMessages = new ArrayList<String>();
        warningMessages = new ArrayList<String>();
        issues = new ArrayList<FeatureIssue>();
        
    }

    /**
     * Override setValidation.
     * <p>
     * Description ...
     * </p>
     * @see org.geotools.validation.ValidationResults#setValidation(org.geotools.validation.Validation)
     * 
     * @param validation
     */
    public void setValidation( Validation validation ) {
        if (!validationList.contains(validation)) { //only add each validation once
            validationList.add(validation);
        }
    }

    /**
     * Override error.
     * <p>
     * Description ...
     * </p>
     * @see org.geotools.validation.ValidationResults#error(org.geotools.feature.SimpleFeature, java.lang.String)
     * 
     * @param feature
     * @param message
     */
    public void error( SimpleFeature feature, String message ) {
        //add the error to our list of failed features + failure messages
    	if (message == null) message = ""; //$NON-NLS-1$
    	failedFeatures.add(feature);
        failureMessages.add(feature.getID() + ": " + message); //$NON-NLS-1$
        //find the layer of the current feature
        IMap activeMap = ApplicationGIS.getActiveMap();
        List<ILayer> layers = new ArrayList<ILayer>();
        if (activeMap != ApplicationGIS.NO_MAP) {
            layers.addAll(activeMap.getMapLayers());
        }
        Layer layer = null;
        for (Iterator i = layers.listIterator(); i.hasNext();) {
        	Layer thisLayer = (Layer) i.next();
        	if (feature.getName().getLocalPart().equals(thisLayer.getName())) {
                layer = thisLayer;
        		break;
        	}
        }
        //add the error to the issues list
        FeatureIssue issue = new FeatureIssue(Priority.HIGH, message, layer, feature,  Messages.GenericValidationResults_validationError ); 
        issues.add(issue);
    }

    /**
     * Override warning.
     * <p>
     * Description ...
     * </p>
     * @see org.geotools.validation.ValidationResults#warning(org.geotools.feature.SimpleFeature, java.lang.String)
     * 
     * @param feature
     * @param message
     */
    public void warning( SimpleFeature feature, String message ) {
        //add the warning to our list of warned features + warning messages
        warningFeatures.add(feature);
        warningMessages.add(feature.getID() + ": " + message); //$NON-NLS-1$
        //find the layer of the current feature
        List<ILayer> layers = ApplicationGIS.getActiveMap().getMapLayers();
        ILayer layer = null;
        for (Iterator i = layers.listIterator(); i.hasNext();) {
        	layer = (ILayer) i.next();
        	if (feature.getName().getLocalPart().equals(layer.getName())) {
        		break;
        	}
        }
        //add the error to the issues list
        FeatureIssue issue = new FeatureIssue(Priority.WARNING, message, layer, feature, Messages.GenericValidationResults_validationWarning); 
        issues.add(issue);
    }

    /**
     * returns the failed features from validation
     */
    public Iterator<SimpleFeature> iterator() {
        return failedFeatures.iterator();
    }

}
