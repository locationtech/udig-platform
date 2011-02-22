package net.refractions.udig;

import net.refractions.udig.core.internal.CorePlugin;

import org.eclipse.ui.IStartup;

public class JaiInstallerBootstrap implements IStartup {

    public void earlyStartup() {
        // This looks for a InstallJaiStartup class contributed by a fragment.  If it finds it is executes it.
        // If it doesn't find it then it doesn't worry about it.
        try{
            Class< ? > installerClass = Class.forName("net.refractions.udig.InstallJaiStartup", true, getClass().getClassLoader()); //$NON-NLS-1$
            IStartup installer = (IStartup) installerClass.newInstance();
            installer.earlyStartup();
        }catch (ClassNotFoundException e) {
            // Its ok
//            CorePlugin.log("JAI Installer class not found", e); //$NON-NLS-1$
        } catch (InstantiationException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        } catch (IllegalAccessException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }
}
