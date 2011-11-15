package net.refractions.udig.style.sld.editor;

import java.lang.reflect.Field;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.style.sld.SLD;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * Open the style editor dialog and add its pages
 */
public class OpenStyleEditorAction extends Action implements IWorkbenchWindowActionDelegate, IViewActionDelegate {

    public static final String ATT_ID = "id"; //$NON-NLS-1$
    public static final String ATT_CLASS = "class"; //$NON-NLS-1$
    public static final String ATT_LABEL = "label"; //$NON-NLS-1$
    public static final String ATT_REQUIRES = "requires"; //$NON-NLS-1$
    public static final String STYLE_ID = "net.refractions.udig.style.sld"; //$NON-NLS-1$

    private Layer selectedLayer;

    private Plugin plugin;

    /**
     * The workbench window; or <code>null</code> if this
     * action has been <code>dispose</code>d.
     */
    private IWorkbenchWindow workbenchWindow;

    /**
     * Create a new <code>OpenPreferenceAction</code>
     * This default constructor allows the the action to be called from the welcome page.
     */
    public OpenStyleEditorAction() {
        this(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
    }

    /**
     * Create a new <code>OpenPreferenceAction</code> and initialize it 
     * from the given resource bundle.
     * @param window
     */
    public OpenStyleEditorAction( IWorkbenchWindow window ) {
        super();
        if (window == null) {
            throw new IllegalArgumentException();
        }
        this.workbenchWindow = window;
        setActionDefinitionId("net.refractions.style.sld.editor"); //$NON-NLS-1$
        // setToolTipText(WorkbenchMessages.OpenPreferences_toolTip);
        // window.getWorkbench().getHelpSystem().setHelp(this,
        // IWorkbenchHelpContextIds.OPEN_PREFERENCES_ACTION);
    }

    public void run( IAction action ) {
        if (workbenchWindow == null) {
            return; // action has been disposed
        }

        Shell shell = workbenchWindow.getShell();
        // the page to select by default
        String pageId = "simple"; //$NON-NLS-1$
        // the filter to apply, if defined
        // String[] displayedIds = null;

        try {
            if (SLD.POINT.supports(selectedLayer)) {
                Class< ? > pointClass = Class.forName("eu.udig.style.advanced.editorpages.SimplePointEditorPage"); //$NON-NLS-1$
                Field idField = pointClass.getField("ID"); //$NON-NLS-1$
                Object value = idField.get(null);
                pageId = value.toString();
            } else if (SLD.LINE.supports(selectedLayer)) {
                Class< ? > pointClass = Class.forName("eu.udig.style.advanced.editorpages.SimpleLineEditorPage"); //$NON-NLS-1$
                Field idField = pointClass.getField("ID"); //$NON-NLS-1$
                Object value = idField.get(null);
                pageId = value.toString();
            } else if (SLD.POLYGON.supports(selectedLayer)) {
                Class< ? > pointClass = Class.forName("eu.udig.style.advanced.editorpages.SimplePolygonEditorPage"); //$NON-NLS-1$
                Field idField = pointClass.getField("ID"); //$NON-NLS-1$
                Object value = idField.get(null);
                pageId = value.toString();
            } else if (selectedLayer.getGeoResource().getInfo(new NullProgressMonitor()).getDescription()
                    .equals("grassbinaryraster")) { //$NON-NLS-1$
                Class< ? > pointClass = Class.forName("eu.udig.style.jgrass.colors.JGrassRasterStyleEditorPage"); //$NON-NLS-1$
                Field idField = pointClass.getField("ID"); //$NON-NLS-1$
                Object value = idField.get(null);
                pageId = value.toString();
            }
        } catch (Exception e) {
            // fallback on simple
            pageId = "simple"; //$NON-NLS-1$
        }

        final EditorPageManager manager = EditorPageManager.loadManager(plugin, selectedLayer);

        StyleEditorDialog dialog = StyleEditorDialog.createDialogOn(shell, pageId, selectedLayer, manager);
        dialog.open();
    }

    public void selectionChanged( IAction action, ISelection selection ) {
        if (selection.isEmpty() || !(selection instanceof IStructuredSelection))
            return;

        IStructuredSelection sselection = (IStructuredSelection) selection;
        if (sselection.getFirstElement() instanceof Layer) {
            selectedLayer = (Layer) sselection.getFirstElement();
        }
    }

    public void dispose() {
        plugin = null;
        workbenchWindow = null;
    }

    public void init( IWorkbenchWindow window ) {
    }

    public void init( IViewPart view ) {
    }

}
