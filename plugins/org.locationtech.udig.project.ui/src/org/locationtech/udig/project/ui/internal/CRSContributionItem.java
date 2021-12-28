/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2021, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.commands.ChangeCRSCommand;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.render.IViewportModelListener;
import org.locationtech.udig.project.render.ViewportModelEvent.EventType;
import org.locationtech.udig.ui.CRSChooserDialog;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Displays a the current CRS and allows to change it
 */
public final class CRSContributionItem extends ContributionItem {

    private static final int MINIMUM_WIDTH = 120;

    private static final String CRS_ITEM_ID = "CRS Display"; //$NON-NLS-1$

    private static final int MAX_LENGTH = 12;

    private final MapPart mapPart;

    private Button button;

    private IViewportModelListener viewportModelListener = event -> {
        if (event.getType() == EventType.CRS) {
            Display display = PlatformUI.getWorkbench().getDisplay();
            if (display != null && !display.isDisposed()) {
                display.asyncExec(this::update);
            }
        }
    };

    /**
     * Create new StatusBarLabel object
     */
    public CRSContributionItem(MapPart mapPart) {
        super(CRS_ITEM_ID);
        this.mapPart = mapPart;
        this.mapPart.getMap().getViewportModel().addViewportModelListener(viewportModelListener);
    }

    /**
     * @return <code>true</code>, if the CRS selection should be disabled. Otherwise
     *         <code>false</code>.
     */
    public static boolean isCRSSelectionDisabled() {
        return ProjectPlugin.getPlugin().getPreferenceStore()
                .getBoolean(PreferenceConstants.P_DISABLE_CRS_SELECTION);
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public void dispose() {
        this.mapPart.getMap().getViewportModel().removeViewportModelListener(viewportModelListener);
    }

    /**
     * Updates the CRS label in the statusbar.
     */
    @Override
    public void update() {
        if (mapPart == null) {
            return;
        }
        final Map map = mapPart.getMap();
        if (map == null) {
            return;
        }

        final CoordinateReferenceSystem crs = map.getViewportModel().getCRS();
        if (crs == null || crs.getName() == null) {
            return;
        }

        setCRS(crs);
    }

    @Override
    public void fill(Composite parent) {
        new Label(parent, SWT.SEPARATOR);

        button = new Button(parent, SWT.PUSH | SWT.FLAT);

        StatusLineLayoutData data = new StatusLineLayoutData();
        data.widthHint = MINIMUM_WIDTH;
        button.setLayoutData(data);

        boolean disableCRSSelection = isCRSSelectionDisabled();

        if (!disableCRSSelection) {
            button.addListener(SWT.Selection, (listener -> promptForCRS()));
        }

        update();
    }

    private void promptForCRS() {
        final CoordinateReferenceSystem crs = this.mapPart.getMap().getViewportModel().getCRS();
        final CRSChooserDialog dialog = new CRSChooserDialog(this.mapPart.getSite().getShell(),
                crs);
        final int code = dialog.open();
        if (Window.OK == code) {
            final CoordinateReferenceSystem result = dialog.getResult();
            if (!result.equals(crs)) {
                update();
                this.mapPart.getMap().sendCommandSync(new ChangeCRSCommand(result));
            }
        }
    }

    private void setCRS(CoordinateReferenceSystem crs) {
        if (crs == null || crs.getName() == null) {
            return;
        }

        final String crsCode = crs.getName().getCode();
        if (crsCode == null) {
            return;
        }

        if (button == null || button.isDisposed()) {
            return;
        }
        String buttonLabel = crsCode;

        if (buttonLabel.length() > MAX_LENGTH) {
            final int start2 = buttonLabel.length() - MAX_LENGTH / 2;
            buttonLabel = buttonLabel.substring(0, MAX_LENGTH / 2) + "..." //$NON-NLS-1$
                    + buttonLabel.substring(start2, buttonLabel.length());
        }
        button.setText(buttonLabel);
        button.update();
        try {
            button.setToolTipText(crs.toWKT());
        } catch (Exception e) {
            // ignore any exception creating readable string for CRS
            button.setToolTipText(""); //$NON-NLS-1$
        }
    }
}