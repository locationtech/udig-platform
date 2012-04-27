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
package net.refractions.udig.catalog.ui.operation;

import java.util.List;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.core.IProvider;
import net.refractions.udig.core.StaticProvider;
import net.refractions.udig.core.internal.ExtensionPointList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.geotools.process.feature.gs.TransformProcess;
import org.geotools.process.feature.gs.TransformProcess.Definition;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

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
     * attribute copy of the source material. The sample fature is also used to determine expected
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
    protected Control createDialogArea(Composite parent) {
        getShell().setText(Messages.TransformDialog_Title);

        Composite dialogArea = (Composite) super.createDialogArea(parent);
        dialogArea.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, true) );
        dialogArea.setSize(500, 400);
        
        dialogArea.setLayout( new MigLayout("flowy") );
        
        panel = new TransformPanel(dialogArea, SWT.NO_SCROLL);
        panel.setInput(sampleFeature);
        panel.setLayoutData("width 300:100%:100%,height 450:pref:100%");
        Label label = new Label(dialogArea, SWT.LEFT);
        label.setText(Messages.TransformDialog_Post_Action_Prompt);
        label.setLayoutData("width pref!, height pref!");
        
        actionCombo = new Combo(dialogArea, SWT.READ_ONLY);
        actionCombo(actionCombo);
        actionCombo.setLayoutData("width pref!,height pref!");
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
                .getExtensionPointList("net.refractions.udig.catalog.ui.reshapePostAction"); //$NON-NLS-1$
        for (final IConfigurationElement configurationElement : extensions) {
            String name = configurationElement.getAttribute("name"); //$NON-NLS-1$
            IProvider<PostReshapeAction> provider = new IProvider<PostReshapeAction>() {

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
            String id = configurationElement.getNamespaceIdentifier()
                    + "." + configurationElement.getAttribute("id"); //$NON-NLS-1$//$NON-NLS-2$
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
            // transform = createTransformProcessDefinitionList();
            // featureType = createFeatureType();
            // ok = featureType != null;
            List<Definition> transform = panel.getTransform();
            ok = transform != null;
            String selected = actionCombo.getItem(actionCombo.getSelectionIndex());
            CatalogUIPlugin.getDefault().getDialogSettings()
                    .put(ACTION_COMBO_SETTINGS, (String) actionCombo.getData(selected + "id")); //$NON-NLS-1$
            postActionProvider = (IProvider<PostReshapeAction>) actionCombo.getData(selected);
        } catch (Throwable t) {
            
             //showFeedback(null, t);
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