package net.refractions.udig.project.ui.internal.tool.display;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;

import net.refractions.udig.ui.operations.IOpFilterListener;
import net.refractions.udig.ui.operations.PropertyValue;

@Ignore
public class TestProperty implements PropertyValue {

    public static final List<IOpFilterListener> listeners=new ArrayList<IOpFilterListener>();
    public static boolean returnVal=true;
    public static Object lastObj;

    public void addListener( IOpFilterListener listener ) {
        listeners.add(listener);
    }

    public boolean canCacheResult() {
        return false;
    }

    public boolean isBlocking() {
        return false;
    }

    public boolean isTrue( Object object, String value ) {
        lastObj=object;
        return returnVal;
    }

    public void removeListener( IOpFilterListener listener ) {
        listeners.remove(listener);
    }

}
