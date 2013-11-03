/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2001, 2006 IBM Corporation and others
 * ------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 * --------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.locationtech.udig.feature.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * A FormToolkit customized for use making feature panels.
 */
public class FeaturePanelWidgetFactory extends FormToolkit {

    /**
     * private constructor.
     */
    public FeaturePanelWidgetFactory() {
        super(Display.getCurrent());
    }

    /**
     * Creates the tab folder as a part of the form.
     * 
     * @param parent the composite parent.
     * @param style the tab folder style.
     * @return the tab folder
     */
    public CTabFolder createTabFolder( Composite parent, int style ) {
        CTabFolder tabFolder = new CTabFolder(parent, style);
        return tabFolder;
    }

    /**
     * Creates the tab item as a part of the tab folder.
     * 
     * @param tabFolder the parent.
     * @param style the tab folder style.
     * @return the tab item.
     */
    public CTabItem createTabItem( CTabFolder tabFolder, int style ) {
        CTabItem tabItem = new CTabItem(tabFolder, style);
        return tabItem;
    }

    /**
     * Creates the list as a part of the form.
     * 
     * @param parent the composite parent.
     * @param style the list style.
     * @return the list.
     */
    public List createList( Composite parent, int style ) {
        List list = new org.eclipse.swt.widgets.List(parent, style);
        return list;
    }

    public Composite createComposite( Composite parent, int style ) {
        Composite c = super.createComposite(parent, style);
        paintBordersFor(c);
        return c;
    }

    public Composite createComposite( Composite parent ) {
        Composite c = createComposite(parent, SWT.NONE);
        return c;
    }

    /**
     * Creates a plain composite as a part of the form.
     * 
     * @param parent the composite parent.
     * @param style the composite style.
     * @return the composite.
     */
    public Composite createPlainComposite( Composite parent, int style ) {
        Composite c = super.createComposite(parent, style);
        c.setBackground(parent.getBackground());
        paintBordersFor(c);
        return c;
    }

    /**
     * Creates a scrolled composite as a part of the form.
     * 
     * @param parent the composite parent.
     * @param style the composite style.
     * @return the composite.
     */
    public ScrolledComposite createScrolledComposite( Composite parent, int style ) {
        ScrolledComposite scrolledComposite = new ScrolledComposite(parent, style);
        return scrolledComposite;
    }

    /**
     * Creates a combo box as a part of the form.
     * 
     * @param parent the combo box parent.
     * @param comboStyle the combo box style.
     * @return the combo box.
     */
    public CCombo createCCombo( Composite parent, int comboStyle ) {
        CCombo combo = new CCombo(parent, comboStyle);
        adapt(combo, true, false);
        // Bugzilla 145837 - workaround for no borders on Windows XP
        if (getBorderStyle() == SWT.BORDER) {
            combo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
        }
        return combo;
    }

    /**
     * Creates a combo box as a part of the form.
     * 
     * @param parent the combo box parent.
     * @return the combo box.
     */
    public CCombo createCCombo( Composite parent ) {
        return createCCombo(parent, SWT.FLAT | SWT.READ_ONLY);
    }

    /**
     * Creates a group as a part of the form.
     * 
     * @param parent the group parent.
     * @param text the group title.
     * @return the composite.
     */
    public Group createGroup( Composite parent, String text ) {
        Group group = new Group(parent, SWT.SHADOW_NONE);
        group.setText(text);
        group.setBackground(getColors().getBackground());
        group.setForeground(getColors().getForeground());
        return group;
    }

    /**
     * Creates a flat form composite as a part of the form.
     * 
     * @param parent the composite parent.
     * @return the composite.
     */
    public Composite createFlatFormComposite( Composite parent ) {
        Composite composite = createComposite(parent);
        FormLayout layout = new FormLayout();
        layout.marginWidth = 5 + 2;
        layout.marginHeight = 4;
        layout.spacing = 6 + 1;
        composite.setLayout(layout);
        return composite;
    }

    /**
     * Creates a label as a part of the form.
     * 
     * @param parent the label parent.
     * @param text the label text.
     * @return the label.
     */
    public CLabel createCLabel( Composite parent, String text ) {
        return createCLabel(parent, text, SWT.NONE);
    }

    /**
     * Creates a label as a part of the form.
     * 
     * @param parent the label parent.
     * @param text the label text.
     * @param style the label style.
     * @return the label.
     */
    public CLabel createCLabel( Composite parent, String text, int style ) {
        final CLabel label = new CLabel(parent, style);
        label.setBackground(parent.getBackground());
        label.setText(text);
        return label;
    }

    public void dispose() {
        if (getColors() != null) {
            super.dispose();
        }
    }
}
