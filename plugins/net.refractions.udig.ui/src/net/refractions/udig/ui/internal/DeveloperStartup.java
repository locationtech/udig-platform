package net.refractions.udig.ui.internal;

import net.refractions.udig.core.internal.CorePlugin;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;

/**
 * Turns off annoying features for developer sanity.
 * 
 * @author chorner
 * @since 1.1.0
 */
public class DeveloperStartup implements IStartup {

    public void earlyStartup() {
        if (CorePlugin.isDeveloping()) {
            closeIntro();
        }
    }
    
    private void closeIntro() {
        PlatformGIS.syncInDisplayThread(new Runnable(){

            public void run() {
                IIntroManager introManager = PlatformUI.getWorkbench().getIntroManager();
                IIntroPart intro = introManager.getIntro();
                if (intro != null)
                    introManager.closeIntro(intro);
            }

        });
    }


}
