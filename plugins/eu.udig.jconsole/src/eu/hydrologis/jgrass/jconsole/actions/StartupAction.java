package eu.hydrologis.jgrass.jconsole.actions;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.ui.IStartup;
import org.jgrasstools.gears.JGrassGears;
import org.jgrasstools.hortonmachine.HortonMachine;
import org.scannotation.ClasspathUrlFinder;

public class StartupAction implements IStartup {
    @Override
    public void earlyStartup() {
        try {
            URL url = ClasspathUrlFinder.findClassBase(HortonMachine.class);
            URL fileURL = FileLocator.toFileURL(url);
            HortonMachine.getInstance(fileURL);

            url = ClasspathUrlFinder.findClassBase(JGrassGears.class);
            fileURL = FileLocator.toFileURL(url);
            JGrassGears.getInstance(fileURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
