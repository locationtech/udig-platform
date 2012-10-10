package eu.udig.omsbox.utils;

import java.lang.reflect.Method;
import java.util.Locale;

import oms3.annotations.Description;

public class AnnotationUtilities {

    private static String LANG = Locale.getDefault().getISO3Language();

    public static String getLocalizedDescription( Description description ) throws Exception {
        // try to get the language
        Method method = Description.class.getMethod(LANG);
        if (method == null) {
            method = Description.class.getMethod("value");
        }

        if (method != null) {
            Object result = method.invoke(description);
            if (result instanceof String) {
                String descriptionStr = (String) result;
                return descriptionStr;
            }
        }

        return " - ";
    }

}
