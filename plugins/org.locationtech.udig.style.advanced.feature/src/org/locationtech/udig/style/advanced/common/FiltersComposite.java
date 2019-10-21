/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.geotools.filter.text.ecql.ECQL;
import org.locationtech.udig.style.advanced.common.IStyleChangesListener.STYLEEVENTTYPE;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import org.locationtech.udig.style.advanced.internal.Messages;
import org.opengis.filter.Filter;

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
        nameLabel.setText(Messages.FiltersComposite_0);
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
                filterText.setText(""); //$NON-NLS-1$
            }
        } catch (Exception e) {
            filterText.setText(""); //$NON-NLS-1$
            e.printStackTrace();
        }
        filterText.addFocusListener(this);
        filterText.addListener(SWT.Modify, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				filterApplyButton.setEnabled(true);
			}
		});
        filterApplyButton = new Button(mainComposite, SWT.PUSH);
        filterApplyButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
        filterApplyButton.setText(Messages.FiltersComposite_3);
        filterApplyButton.addSelectionListener(this);
        filterApplyButton.setEnabled(false);

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
                filterText.setText(ECQL.toCQL(filter));
            } else {
                filterText.setText(""); //$NON-NLS-1$
            }
        } catch (Exception e) {
            filterText.setText(""); //$NON-NLS-1$
            e.printStackTrace();
        }
        filterApplyButton.setEnabled(false);
    }

    public void widgetSelected( SelectionEvent e ) {
        Object source = e.getSource();
        if (source.equals(filterApplyButton)) {
            String filterTextStr = filterText.getText();
            notifyListeners(filterTextStr, false, STYLEEVENTTYPE.FILTER);
            filterApplyButton.setEnabled(false);
        }
    }

}
