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
package eu.udig.style.advanced.common;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.opengis.filter.Filter;

import eu.udig.style.advanced.common.IStyleChangesListener.STYLEEVENTTYPE;
import eu.udig.style.advanced.common.styleattributeclasses.RuleWrapper;

/**
 * A composite that holds filters.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class FiltersComposite extends ParameterComposite {

    private final Composite parent;

    private Text filterText;
    private Button filterApplyButton;

    private Composite mainComposite;

    public FiltersComposite( Composite parent ) {
        this.parent = parent;
    }

    public Composite getComposite() {
        return mainComposite;
    }

    /**
     * Initialize the panel with pre-existing values.
     * 
     * @param ruleWrapper the {@link RuleWrapper}.
     */
    public void init( RuleWrapper ruleWrapper ) {
        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainComposite.setLayout(new GridLayout(1, true));

        // rule name
        Label nameLabel = new Label(mainComposite, SWT.NONE);
        nameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        nameLabel.setText("Filter string");
        filterText = new Text(mainComposite, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.LEAD | SWT.BORDER);
        GridData nameTextGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        nameTextGD.widthHint = 100;
        nameTextGD.heightHint = 100;
        filterText.setLayoutData(nameTextGD);
        try {
            Filter filter = ruleWrapper.getRule().getFilter();
            if (filter != null) {
                filterText.setText(filter.toString());
            } else {
                filterText.setText("");
            }
        } catch (Exception e) {
            filterText.setText("");
            e.printStackTrace();
        }
        filterText.addFocusListener(this);

        filterApplyButton = new Button(mainComposite, SWT.PUSH);
        filterApplyButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
        filterApplyButton.setText("Apply filter");
        filterApplyButton.addSelectionListener(this);

    }

    /**
     * Update the panel.
     * 
     * @param ruleWrapper the {@link RuleWrapper}.
     */
    public void update( RuleWrapper ruleWrapper ) {
        try {
            Filter filter = ruleWrapper.getRule().getFilter();
            if (filter != null) {
                filterText.setText(filter.toString());
            } else {
                filterText.setText("");
            }
        } catch (Exception e) {
            filterText.setText("");
            e.printStackTrace();
        }

    }

    public void widgetSelected( SelectionEvent e ) {
        Object source = e.getSource();
        if (source.equals(filterApplyButton)) {
            String filterTextStr = filterText.getText();
            notifyListeners(filterTextStr, false, STYLEEVENTTYPE.FILTER);
        }
    }

}
