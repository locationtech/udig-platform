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
package eu.udig.omsbox.ui;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import eu.udig.omsbox.OmsBoxPlugin;

/**
 * Running processes chooser dialog.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class RunningProcessListDialog {

    private Dialog dialog;

    private String currentSelectedProcessId;

    private TableViewer tableViewer;

    private HashMap<String, Process> runningProcessesMap;

    public void open( Shell parentShell, final int selectionType ) {

        runningProcessesMap = OmsBoxPlugin.getDefault().getRunningProcessesMap();

        dialog = new Dialog(parentShell){

            @Override
            protected void configureShell( Shell shell ) {
                super.configureShell(shell);
                shell.setText("Kill selected process"); //$NON-NLS-1$
            }

            @Override
            protected Point getInitialSize() {
                return new Point(640, 450);
            }

            @Override
            protected Control createDialogArea( Composite parent ) {
                Composite parentPanel = (Composite) super.createDialogArea(parent);
                GridLayout gLayout = (GridLayout) parentPanel.getLayout();
                gLayout.numColumns = 1;

                createTableViewer(parentPanel);

                return parentPanel;
            }

            @Override
            protected void buttonPressed( int buttonId ) {
                if (buttonId == OK) {
                    OmsBoxPlugin.getDefault().killProcess(currentSelectedProcessId);
                }
                super.buttonPressed(buttonId);
            }
        };
        dialog.setBlockOnOpen(true);
        dialog.open();
    }

    public boolean isDisposed() {
        return dialog.getShell().isDisposed();
    }

    public void widgetSelected( SelectionEvent e ) {

    }

    private void createTableViewer( Composite parentPanel ) {
        Group processesGroup = new Group(parentPanel, SWT.NONE);
        processesGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        processesGroup.setLayout(new GridLayout(1, false));
        processesGroup.setText("Current running processes (select process and press ok to kill it)");
        
        tableViewer = new TableViewer(processesGroup);

        Control control = tableViewer.getControl();
        GridData controlGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        control.setLayoutData(controlGD);

        tableViewer.setContentProvider(new IStructuredContentProvider(){

            public Object[] getElements( Object inputElement ) {
                if (inputElement instanceof HashMap< ? , ? >) {
                    HashMap< ? , ? > paths = (HashMap< ? , ? >) inputElement;
                    Set< ? > keySet = paths.keySet();
                    Object[] array = new Object[keySet.size()];
                    int i = 0;
                    for( Object key : keySet ) {
                        array[i] = key;
                        i++;
                    }
                    return array;
                }
                return new String[0];
            }
            public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
            }
            public void dispose() {
            }

        });

        tableViewer.setLabelProvider(new LabelProvider(){
            public Image getImage( Object element ) {
                return null;
            }

            public String getText( Object element ) {
                if (element instanceof String) {
                    String processId = (String) element;
                    return processId;
                }
                return ""; //$NON-NLS-1$
            }
        });

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener(){

            public void selectionChanged( SelectionChangedEvent event ) {
                if (!(event.getSelection() instanceof IStructuredSelection)) {
                    return;
                }
                IStructuredSelection sel = (IStructuredSelection) event.getSelection();

                Object selectedItem = sel.getFirstElement();
                if (selectedItem == null) {
                    return;
                }

                if (selectedItem instanceof String) {
                    currentSelectedProcessId = (String) selectedItem;
                }
            }
        });

        if (runningProcessesMap.size() > 0) {
            tableViewer.setInput(runningProcessesMap);
        } else {
            tableViewer.setInput(""); //$NON-NLS-1$
        }
    }
}
