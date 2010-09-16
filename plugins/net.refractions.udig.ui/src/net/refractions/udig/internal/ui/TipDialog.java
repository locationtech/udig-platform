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
package net.refractions.udig.internal.ui;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.ui.internal.Messages;
import net.refractions.udig.ui.preferences.PreferenceConstants;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.service.prefs.Preferences;

/**
 * Dialog for showing tips from the tip extension point.
 */
public class TipDialog extends Dialog {
	public final static String PREFERENCE_ID = "net.refractions.udig.ui.tips"; //$NON-NLS-1$

	public final static String EXTENSION_ID = "net.refractions.udig.ui.tip"; //$NON-NLS-1$

	private static Tip current;

	private static Configuration currentConfiguration;

	private Image image;

	private Label title;

	private Text tip;

	private Button check;

	private Label imageLabel;

	protected TipDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText(Messages.TipDialog_shellText); 
		super.configureShell(newShell);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 300);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// create a composite with standard margins and spacing
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(2, false));

		imageLabel = new Label(composite, SWT.NONE);
		imageLabel
				.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));

		title = new Label(composite, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
		gridData.verticalAlignment = SWT.END;
		title.setLayoutData(gridData);

		tip = new Text(composite, SWT.WRAP|SWT.READ_ONLY| SWT.V_SCROLL);
		tip.setBackground(getShell().getDisplay().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 2;
		tip.setLayoutData(gridData);

		updateTip();

		check = new Button(composite, SWT.CHECK);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 2;
		check.setLayoutData(gridData);
		check.setText(Messages.TipDialog_question); 
		boolean selected;
		try {
            IPreferenceStore store = UiPlugin.getDefault().getPreferenceStore();
            selected = store.getBoolean(PreferenceConstants.P_SHOW_TIPS);
		} catch (Exception e) {
			UiPlugin.log("", e); //$NON-NLS-1$
			selected = true;
		}
		check.setSelection(selected);

		return composite;
	}

	private void updateTip() {
		if (getCurrentTip().image != null) {
			this.image = getCurrentTip().image.createImage();
			imageLabel.setImage(this.image);
		}
		title.setText(getCurrentTip().name);
		FontData[] fontData = getShell().getFont().getFontData();
		FontData[] newData = new FontData[fontData.length];
		for (int i = 0; i < fontData.length; i++) {
			FontData data = fontData[i];
			newData[i] = new FontData(data.getName(), data.getHeight() + 2, SWT.BOLD);
		}
		title.setFont(new Font(getShell().getDisplay(), newData));

        if( getCurrentTip() != null && getCurrentTip().hint!=null )
            tip.setText(getCurrentTip().hint);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.NEXT_ID,
				IDialogConstants.NEXT_LABEL, false);
		createButton(parent, IDialogConstants.CLOSE_ID,
				IDialogConstants.CLOSE_LABEL, true);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.NEXT_ID == buttonId) {
			next(-1);
			updateTip();
		}
		if (buttonId == IDialogConstants.CLOSE_ID) {
			IPreferenceStore store = UiPlugin.getDefault().getPreferenceStore();
			store.setValue(PreferenceConstants.P_SHOW_TIPS, check.getSelection());
			okPressed();
		}
	}

	public static boolean hasTips() {
		return getCurrentTip() != null;
	}

	private static Tip getCurrentTip() {
		if (current == null) {
			next(-1);
		}
		return current;
	}

	private static boolean isInCurrentConfiguration(IConfigurationElement elem) {
		if (currentConfiguration == null) {

			List<IConfigurationElement> extensions = ExtensionPointList
					.getExtensionPointList(EXTENSION_ID);
			IConfigurationElement mostRecent = null;
			for (IConfigurationElement element : extensions) {
				if (element.getName().equals("activeConfiguration")) { //$NON-NLS-1$
					String attribute = element.getAttribute("configurationID"); //$NON-NLS-1$
					if (attribute != null)
						mostRecent = element;
				}
			}
			if (mostRecent == null) {
				currentConfiguration = Configuration.DEFAULT;
			} else {
				for (IConfigurationElement element : extensions) {
					if( mostRecent.getAttribute("configurationID").equals(element.getAttribute("id")) ) { //$NON-NLS-1$ //$NON-NLS-2$
						currentConfiguration=new Configuration(element);
						break;
					}
				}
				if( currentConfiguration.extensionIDs.size()==0 && 
						currentConfiguration.tipIDs.size()==0 )
					currentConfiguration = Configuration.DEFAULT;
					
			}
		}
		if (currentConfiguration == Configuration.DEFAULT) {
			return true;
		}

		return currentConfiguration.tipIDs.contains(elem.getAttribute("id")) //$NON-NLS-1$
		|| currentConfiguration.extensionIDs.contains(elem.getDeclaringExtension().getUniqueIdentifier());
	}

	private static void next(int next2) {
        int next=next2;
		try {
			Preferences node = getPreferences();
			List<IConfigurationElement> extensions = ExtensionPointList
					.getExtensionPointList(EXTENSION_ID);
			if (next == -1) {
				Random r = new Random();
				next = r.nextInt(extensions.size());
			}
			for (int i = next; i < extensions.size(); i++) {
				IConfigurationElement elem = extensions.get(i);
				if (isPermittedNext(node, elem)) {
					node.put(elem.getAttribute("id"), ""); //$NON-NLS-1$ //$NON-NLS-2$
					current = new Tip(elem);
					return;
				}
			}
			for (int i = 0; i < next; i++) {
				IConfigurationElement elem = extensions.get(i);
				if (isPermittedNext(node, elem)) {
					node.put(elem.getAttribute("id"), ""); //$NON-NLS-1$ //$NON-NLS-2$
					current = new Tip(elem);
					return;
				}
			}
			if (node.keys().length == 0) {
				current = null;
				return;
			}
			node.clear();
			next(next);
		} catch (Exception e) {
			UiPlugin.log("", e); //$NON-NLS-1$
		}
	}

	private static boolean isPermittedNext(Preferences node,
			IConfigurationElement elem) {
		if (!elem.getName().equals("tip")) //$NON-NLS-1$
			return false;
		boolean notPreviouslyShown = node.get(elem.getAttribute("id"), null) == null; //$NON-NLS-1$
		boolean notSameAsCurrent = (current == null || !elem.getAttribute("id") //$NON-NLS-1$
				.equals(current.id));
		return notPreviouslyShown && notSameAsCurrent
				&& isInCurrentConfiguration(elem);
	}

	public static Preferences getPreferences() throws CoreException,
			IOException {
		Preferences userPreferences = UiPlugin.getUserPreferences();
		Preferences node = userPreferences.node(PREFERENCE_ID);
		return node;
	}

	private static class Configuration {
		public static final Configuration DEFAULT=new Configuration();
		
		Collection<String> tipIDs;
		Collection<String> extensionIDs;
		
		Configuration(IConfigurationElement confElem) {
			this.tipIDs=new HashSet<String>();
			this.extensionIDs=new HashSet<String>();
			IConfigurationElement[] tipRefs = confElem.getChildren("tipRef"); //$NON-NLS-1$
			for (IConfigurationElement element : tipRefs) {
				String attribute = element.getAttribute("tipID"); //$NON-NLS-1$
				if( attribute!=null )
					tipIDs.add(attribute);
			}
			IConfigurationElement[] extensionRefs = confElem.getChildren("tipExtensionRef"); //$NON-NLS-1$
			for (IConfigurationElement element : extensionRefs) {
				String attribute = element.getAttribute("extensionID"); //$NON-NLS-1$
				if( attribute!=null )
					extensionIDs.add(attribute);
			}
		}

		Configuration() {
			// do nothing
		}
	}

	private static class Tip {
		public String id;

		ImageDescriptor image;

		String name;

		String hint;

		Tip(IConfigurationElement tipElem) {
			id = tipElem.getAttribute("id"); //$NON-NLS-1$
			name = tipElem.getAttribute("name"); //$NON-NLS-1$
			hint = tipElem.getValue();
			if (tipElem.getAttribute("icon") != null) { //$NON-NLS-1$
				image = AbstractUIPlugin.imageDescriptorFromPlugin(tipElem
						.getNamespaceIdentifier(), tipElem.getAttribute("icon")); //$NON-NLS-1$
			} else {
				image = AbstractUIPlugin.imageDescriptorFromPlugin(UiPlugin.ID,
						"icons/elcl16/light.GIF"); //$NON-NLS-1$
			}
		}
	}
}
