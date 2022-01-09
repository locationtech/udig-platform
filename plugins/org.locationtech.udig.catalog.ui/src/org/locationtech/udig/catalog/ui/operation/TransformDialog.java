/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.operation;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.geotools.process.vector.TransformProcess;
import org.geotools.process.vector.TransformProcess.Definition;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.core.IProvider;
import org.locationtech.udig.core.StaticProvider;
import org.locationtech.udig.core.internal.ExtensionPointList;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import net.miginfocom.swt.MigLayout;

/**
 * Dialog used to ask the user to enter in a series of expression for use with the Transform
 * process.
 *
 * @author Jody Garnett
 * @since 1.0.0
 */
public class TransformDialog extends Dialog {

    private static final String NO_CONTENT = "--"; //$NON-NLS-1$

    private final class Null_Action implements PostReshapeAction {
        @Override
        public void execute(IGeoResource original, IGeoResource reshaped) {
        }
    }

    private static final String ACTION_COMBO_SETTINGS = "RESHAPE_ACTION_COMBO_SETTINGS"; //$NON-NLS-1$

    private SimpleFeatureType featureType;

    private Combo actionCombo;

    private IProvider<PostReshapeAction> postActionProvider;

    private ControlDecoration feedbackDecorator;

    private TransformPanel panel;

    private SimpleFeature sampleFeature;

    /**
     * Transform Dialog used assemble a {@link ProcessTransform.Definition} based on the provided
     * sample feature.
     * <p>
     * The initial transform is defined by
     * {@link #createDefaultTransformDefinition(SimpleFeatureType)} to be a simple attribute by
     * attribute copy of the source material. The sample feature is also used to determine expected
     * type when defining new expressions.
     *
     * @param parent
     * @param sample
     */
    public TransformDialog(Shell parent, SimpleFeature sample) {
        super(parent);
        this.sampleFeature = sample;
        setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM | SWT.CLOSE);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 500);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        getShell().setText(Messages.TransformDialog_Title);

        Composite dialogArea = (Composite) super.createDialogArea(parent);
        dialogArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        dialogArea.setLayout(new MigLayout("flowy")); //$NON-NLS-1$

        panel = new TransformPanel(dialogArea, SWT.NO_SCROLL);
        panel.setInput(sampleFeature);
        panel.setLayoutData("width 300:100%:100%,height 450:pref:100%"); //$NON-NLS-1$
        Label label = new Label(dialogArea, SWT.LEFT);
        label.setText(Messages.TransformDialog_Post_Action_Prompt);
        label.setLayoutData("width pref!, height pref!"); //$NON-NLS-1$

        actionCombo = new Combo(dialogArea, SWT.READ_ONLY);
        actionCombo(actionCombo);
        actionCombo.setLayoutData("width pref!,height pref!"); //$NON-NLS-1$
        return dialogArea;
    }

    private void actionCombo(Combo actionCombo) {
        actionCombo.add(Messages.ReshapeOperation_noAction);
        actionCombo.setData(Messages.ReshapeOperation_noAction,
                new StaticProvider<PostReshapeAction>(new Null_Action()));

        int i = 1;
        String lastSelection = CatalogUIPlugin.getDefault().getDialogSettings()
                .get(ACTION_COMBO_SETTINGS);
        int selected = 0;

        List<IConfigurationElement> extensions = ExtensionPointList
                .getExtensionPointList("org.locationtech.udig.catalog.ui.reshapePostAction"); //$NON-NLS-1$
        for (final IConfigurationElement configurationElement : extensions) {
            String name = configurationElement.getAttribute("name"); //$NON-NLS-1$
            IProvider<PostReshapeAction> provider = new IProvider<PostReshapeAction>() {

                @Override
                public PostReshapeAction get(Object... params) {
                    try {
                        return (PostReshapeAction) configurationElement
                                .createExecutableExtension("class"); //$NON-NLS-1$
                    } catch (CoreException e) {
                        throw (RuntimeException) new RuntimeException().initCause(e);
                    }
                }

            };
            actionCombo.add(name);
            actionCombo.setData(name, provider);
            String id = configurationElement.getNamespaceIdentifier() + "." //$NON-NLS-1$
                    + configurationElement.getAttribute("id"); //$NON-NLS-1$
            actionCombo.setData(name + "id", id); //$NON-NLS-1$

            if (id.equals(lastSelection)) {
                selected = i;
            }
            i++;
        }
        actionCombo.select(selected);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void okPressed() {
        boolean ok = false;
        try {
            List<Definition> transform = panel.getTransform();
            ok = transform != null;
            String selected = actionCombo.getItem(actionCombo.getSelectionIndex());
            CatalogUIPlugin.getDefault().getDialogSettings().put(ACTION_COMBO_SETTINGS,
                    (String) actionCombo.getData(selected + "id")); //$NON-NLS-1$
            postActionProvider = (IProvider<PostReshapeAction>) actionCombo.getData(selected);
        } catch (Throwable t) {

            // showFeedback(null, t);
        }
        if (ok) {
            super.okPressed();
        }
    }

    /**
     * FeatureType for resulting output; only valid after {@link #okPressed()}
     *
     * @return
     */
    public SimpleFeatureType getFeatureType() {
        return featureType;
    }

    public void executePostAction(IGeoResource handle, IGeoResource transformed) {
        PostReshapeAction postReshapeAction = postActionProvider.get();
        postReshapeAction.execute(handle, transformed);
    }

    /**
     * Transform process definition; only valid after {@link #okPressed()}.
     *
     * @return
     */
    public List<TransformProcess.Definition> getTransform() {
        return panel.getTransform();
    }
}
