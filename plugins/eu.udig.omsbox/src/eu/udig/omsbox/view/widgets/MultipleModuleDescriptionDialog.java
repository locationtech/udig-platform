/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.omsbox.view.widgets;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import eu.udig.omsbox.core.ModuleDescription;

/**
 * A dialog that holds a {@link ModuleDescription} gui.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class MultipleModuleDescriptionDialog {

    private static final int DIALOG_WIDTH = 500;
    private Dialog dialog;
    private Composite parentPanel;
    private final List<ModuleDescription> moduleDescriptions;
    private ModuleDescription currentModuleDescription;

    private StackLayout stackLayout;
    private Combo namesCombo;
    private Composite moduleComposite;
    private String title = "";
    private ModuleDescription lastUsedModuleDescription;

    public MultipleModuleDescriptionDialog( String title, List<ModuleDescription> moduleDescriptions ) {
        this.moduleDescriptions = moduleDescriptions;
        if (title != null) {
            this.title = title;
        }
        lastUsedModuleDescription = null;
    }

    /**
     * Sets the last used ModuleDescription to remember the type.
     * 
     * @param moduleDescription
     */
    public void setLastUsedModuleDescription( ModuleDescription moduleDescription ) {
        this.lastUsedModuleDescription = moduleDescription;
    }

    public void open( final Shell parentShell ) {
        dialog = new Dialog(parentShell){

            protected void configureShell( Shell shell ) {
                super.configureShell(shell);
                shell.setText(title);
                shell.setSize(DIALOG_WIDTH, 700);

                Monitor primary = Display.getDefault().getPrimaryMonitor();
                Rectangle bounds = primary.getBounds();
                Rectangle rect = shell.getBounds();
                int x = bounds.x + (bounds.width - rect.width) / 2;
                int y = bounds.y + (bounds.height - rect.height) / 2;
                shell.setLocation(x, y);
            }

            protected int getShellStyle() {
                return SWT.DIALOG_TRIM | SWT.RESIZE;
            };
            // protected Point getInitialSize() {
            // return new Point(620, 450);
            // }

            protected Control createDialogArea( Composite parent ) {
                parentPanel = (Composite) super.createDialogArea(parent);
                parentPanel.setLayout(new GridLayout(1, false));
                parentPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

                String[] names = new String[moduleDescriptions.size()];
                for( int i = 0; i < names.length; i++ ) {
                    names[i] = moduleDescriptions.get(i).getName();
                }

                namesCombo = new Combo(parentPanel, SWT.DROP_DOWN | SWT.READ_ONLY);
                namesCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
                namesCombo.setItems(names);
                if (lastUsedModuleDescription != null) {
                    for( int j = 0; j < moduleDescriptions.size(); j++ ) {
                        if (moduleDescriptions.get(j).getClassName().equals(lastUsedModuleDescription.getClassName())) {
                            namesCombo.select(j);
                        }
                    }
                } else {
                    namesCombo.select(0);
                }
                namesCombo.addSelectionListener(new SelectionAdapter(){
                    public void widgetSelected( SelectionEvent e ) {
                        setGui();
                    }
                });

                moduleComposite = new Composite(parentPanel, SWT.NONE);
                moduleComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                stackLayout = new StackLayout();
                moduleComposite.setLayout(stackLayout);

                setGui();

                return parentPanel;
            }

            protected void buttonPressed( int buttonId ) {
                if (buttonId == Dialog.CANCEL) {
                    currentModuleDescription = null;
                }

                // modification of whatever should be done in any gui
                super.buttonPressed(buttonId);
            }
        };
        dialog.setBlockOnOpen(true);
        dialog.open();

    }

    private void setGui() {
        String text = namesCombo.getText();

        for( int i = 0; i < moduleDescriptions.size(); i++ ) {
            if (moduleDescriptions.get(i).getName().equals(text)) {
                currentModuleDescription = moduleDescriptions.get(i);
                break;
            }
        }

        Composite tmpComposite = new Composite(moduleComposite, SWT.NONE);
        tmpComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tmpComposite.setLayout(new GridLayout(1, false));
        ModuleGui moduleGui = new ModuleGui(currentModuleDescription);
        moduleGui.makeGui(tmpComposite, true);

        stackLayout.topControl = tmpComposite;
        moduleComposite.layout();

    }

    public ModuleDescription getModuleDescription() {
        return currentModuleDescription;
    }
}
