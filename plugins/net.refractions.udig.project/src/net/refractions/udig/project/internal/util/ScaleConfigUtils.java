package net.refractions.udig.project.internal.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.SortedSet;
import java.util.TreeSet;

import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.preferences.PreferenceConstants;

/**
 * @author fgdrf
 * Scale utilities to convert scale denominators, and Preferences Converter
 */
public class ScaleConfigUtils {

    private static final String SCALE_PREFIX = "1:";
    private static final String SCALE_DENOMINATOR_PREF_SEPARATOR = ";";
    
    /**
     * @param scaleDenominator
     * @return a formated String representation of a scale, 1:scaleDenominator
     */
    public static String toLabel(double scaleDenominator) {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        return SCALE_PREFIX + numberFormat.format(scaleDenominator);
    }
    
    /**
     * @param scaleLabel something like "1:1.000" or simple "10.000" would work also
     * @return <b>null</b> or a valid Double representation for the scale denominator 
     */
    public static Double fromLabel(String scaleLabel) {
        try {
            NumberFormat numberFormat = NumberFormat.getIntegerInstance();
            return numberFormat.parse(scaleLabel.replace(SCALE_PREFIX, "")).doubleValue();
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * set the preferred scale denominators in map's ViewportModel
     * @param map
     * @param scaleConfiguration
     */
    public static void setPreferredScales(Map map, SortedSet<Double> scaleConfiguration) {
        ViewportModel viewportModelInternal = map.getViewportModelInternal();
        if (!(scaleConfiguration == null || scaleConfiguration.isEmpty())) {
            viewportModelInternal
                .setPreferredScaleDenominators(scaleConfiguration);
        }
    }

    /**
     * @return a set of preferred scale denominators from Preferences Store
     */
    public static SortedSet<Double> getScaleDenominatorsFromPreferences() {
        return ScaleConfigUtils.getScaleConfiguration(ProjectPlugin
                .getPlugin().getPreferenceStore()
                .getString(PreferenceConstants.P_DEFAULT_PREFERRED_SCALES));
    }

    /**
     * @param scaleDenominators
     * @return the String representation of the configured scaleDenominator set
     */
    public static String toScaleDenominatorsPrefString(final SortedSet<Double> scaleDenominators) {
        StringBuffer stringBuffer = new StringBuffer();
        
        
        for (Double item : scaleDenominators) {
            if (stringBuffer.length() > 0) {
                stringBuffer.append(SCALE_DENOMINATOR_PREF_SEPARATOR);
            }
            
            if (item != null) {
                stringBuffer.append(item);
            }

        }
        return stringBuffer.toString();
    }
    
    /**
     * @param configuration
     * @return the configured preferred scale denominators from preferences string
     */
    public static SortedSet<Double> getScaleConfiguration(String configuration) {
        SortedSet<Double> scales = new TreeSet<Double>();
    
        if (configuration != null && configuration.length() > 0) {
            String[] items = configuration.split(SCALE_DENOMINATOR_PREF_SEPARATOR);
            if (items != null) {
                for (String item : items) {
                    if (item != null && item.length() > 0) {
                        scales.add(Double.valueOf(item));
                    }
                }
            }
        }
        return scales;
    }
}
