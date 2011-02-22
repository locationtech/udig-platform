/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.validation;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.geotools.feature.FeatureType;
import org.geotools.validation.FeatureValidation;
import org.geotools.validation.attributes.NullZeroValidation;

/**
 * Overrides the FeatureValidationOp abstract class to return NullZeroValidation()
 * <p>
 * </p>
 *
 * @author chorner
 * @since 1.0.1
 */
public class ValidateNullZero extends FeatureValidationOp {
    /**public for testing purposes only*/
    public String xPath;
    public Combo combo;

    public FeatureValidation getValidator() {
        if (xPath == null) return null;
        NullZeroValidation nullZero = new NullZeroValidation();
        nullZero.setAttribute(xPath);
        return nullZero;
    }

    /**public for testing purposes only*/
    public @Override Dialog getDialog( Shell shell, final FeatureType featureType ) {
        if (featureType.getAttributeCount() == 0) return null;
        final Dialog dialog = new Dialog(shell){

            @Override
            protected Control createDialogArea( Composite parent ) {
                Composite composite = (Composite) super.createDialogArea(parent);
                combo = new Combo(composite, SWT.DEFAULT);
                for (int i = 0; i < featureType.getAttributeCount(); i++) {
                    combo.add(featureType.getAttributeType(i).getName());
                }
                combo.addModifyListener(new ModifyListener(){

                    public void modifyText( ModifyEvent e ) {
                        setXpath(combo);
                    }

                });
                combo.addSelectionListener(new SelectionListener(){

                    public void widgetSelected( SelectionEvent e ) {
                        setXpath(combo);
                    }

                    public void widgetDefaultSelected( SelectionEvent e ) {
                        widgetSelected(e);
                        okPressed();
                    }

                });
                combo.select(0);
                xPath = featureType.getAttributeType(0).getName();
                return composite;
            }

            @Override
            protected void okPressed() {
                super.okPressed();
            }
        };
        return dialog;
    }
    /**public for testing purposes only*/
    public void setXpath( final Combo combo ) {
        xPath = combo.getItem(combo.getSelectionIndex());
    }
}
