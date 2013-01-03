package eu.hydrologis.jgrass.jconsole.jgrasstools;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.jgrasstools.gears.JGrassGears;
import org.jgrasstools.gears.libs.modules.ClassField;
import org.jgrasstools.hortonmachine.HortonMachine;

import eu.hydrologis.jgrass.jconsole.actions.StartupAction;

public class JGrassTools {
    
    public static final String OPENFILES = "OPENFILES"; //$NON-NLS-1$

    private static JGrassTools jgrassTools;
    private LinkedHashMap<String, List<ClassField>> moduleName2Fields;
    private String[] allFields;
    private String[] allClasses;
    private LinkedHashMap<String, Class< ? >> moduleName2Class;

    private JGrassTools() {
    }

    public static JGrassTools getInstance() {
        if (jgrassTools == null) {
            jgrassTools = new JGrassTools();

            // make sure everything is set
            new StartupAction().earlyStartup();
        }

        return jgrassTools;
    }

    public LinkedHashMap<String, List<ClassField>> getModuleName2Fields() {
        if (moduleName2Fields == null) {
            moduleName2Fields = HortonMachine.getInstance().moduleName2Fields;
            LinkedHashMap<String, List<ClassField>> moduleName2FieldsGears = JGrassGears.getInstance().moduleName2Fields;
            moduleName2Fields.putAll(moduleName2FieldsGears);

            Set<Entry<String, List<ClassField>>> entrySet = moduleName2Fields.entrySet();
            for( Entry<String, List<ClassField>> entry : entrySet ) {
                String name = entry.getKey();
                List<ClassField> fields = entry.getValue();

                System.out.print(name + ": ");
                for( ClassField classField : fields ) {
                    System.out.print(classField.fieldName + "/");
                }
                System.out.println();
            }
            entrySet = moduleName2FieldsGears.entrySet();
            for( Entry<String, List<ClassField>> entry : entrySet ) {
                String name = entry.getKey();
                List<ClassField> fields = entry.getValue();

                System.out.print(name + ": ");
                for( ClassField classField : fields ) {
                    System.out.print(classField.fieldName + "/");
                }
                System.out.println();
            }
        }
        return moduleName2Fields;
    }

    public LinkedHashMap<String, Class< ? >> getModuleName2Classes() {
        if (moduleName2Class == null) {
            moduleName2Class = HortonMachine.getInstance().moduleName2Class;
            LinkedHashMap<String, Class< ? >> moduleName2ClassesGears = JGrassGears.getInstance().moduleName2Class;
            moduleName2Class.putAll(moduleName2ClassesGears);
        }
        return moduleName2Class;
    }

    public String[] getAllFields() {
        if (allFields == null) {
            int jggLength = JGrassGears.getInstance().allFields.length;
            int hmLength = HortonMachine.getInstance().allFields.length;
            allFields = new String[hmLength + jggLength];

            System.arraycopy(JGrassGears.getInstance().allFields, 0, allFields, 0, jggLength);
            System.arraycopy(HortonMachine.getInstance().allFields, 0, allFields, jggLength, hmLength);
        }
        return allFields;
    }

    public String[] getAllClasses() {
        if (allClasses == null) {
            int jggLength = JGrassGears.getInstance().allClasses.length;
            int hmLength = HortonMachine.getInstance().allClasses.length;
            allClasses = new String[hmLength + jggLength];

            System.arraycopy(JGrassGears.getInstance().allClasses, 0, allClasses, 0, jggLength);
            System.arraycopy(HortonMachine.getInstance().allClasses, 0, allClasses, jggLength, hmLength);
        }
        return allClasses;
    }

    public static void firstModulesGathering() {
        try {
            IWorkbench wb = PlatformUI.getWorkbench();
            IProgressService ps = wb.getProgressService();
            ps.busyCursorWhile(new IRunnableWithProgress(){
                public void run( IProgressMonitor pm ) {
                    pm.beginTask("First time modules definitions retrival. This might take a moment.", IProgressMonitor.UNKNOWN);
                    JGrassTools.getInstance().getAllFields();
                    JGrassTools.getInstance().getAllClasses();
                    pm.done();
                }
            });
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
