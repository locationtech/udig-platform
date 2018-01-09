/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2000, 2009 IBM Corporation and others
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Teddy Walker <teddy.walker@googlemail.com>
 *      - Bug 188056 [Preferences] PreferencePages have to less indent in PreferenceDialog
 *******************************************************************************/
package org.locationtech.udig.style.sld.editor.internal;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogMessageArea;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.locationtech.udig.style.sld.IEditorPage;
import org.locationtech.udig.style.sld.IEditorPageContainer;
import org.locationtech.udig.style.sld.SLDPlugin;
import org.locationtech.udig.style.sld.editor.EditorPageManager;

/**
 * Adapting PreferenceDialog to allow for the general approach to be used for
 * editing attributes.
 * <p>
 * Each page is represented by a node in the tree shown on the left hand side of the
 * dialog; when a node is selected, the corresponding page is shown on the right hand side.
 * 
 * @see PreferenceDialog
 */
public class EditorDialog extends Dialog implements IEditorPageContainer, IPageChangeProvider {
	/**
	 * Layout for the page container.
	 *  
	 */
	private class PageLayout extends Layout {
		@Override
        public Point computeSize(Composite composite, int wHint, int hHint, boolean force) {
			if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT)
				return new Point(wHint, hHint);
			int x = minimumPageSize.x;
			int y = minimumPageSize.y;
			Control[] children = composite.getChildren();
			for (int i = 0; i < children.length; i++) {
				Point size = children[i].computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
				x = Math.max(x, size.x);
				y = Math.max(y, size.y);
			}
			
			//As pages can implement thier own computeSize
			//take it into account
			if(currentPage != null){
				Point size = currentPage.computeSize();
				x = Math.max(x, size.x);
				y = Math.max(y, size.y);
			}
			
			if (wHint != SWT.DEFAULT)
				x = wHint;
			if (hHint != SWT.DEFAULT)
				y = hHint;
			return new Point(x, y);
		}

		@Override
        public void layout(Composite composite, boolean force) {
			Rectangle rect = composite.getClientArea();
			Control[] children = composite.getChildren();
			for (int i = 0; i < children.length; i++) {
				children[i].setSize(rect.width, rect.height);
			}
		}
	}

	//The id of the last page that was selected
	private static String lastPageId = null;

	//The last known tree width
	private static int lastTreeWidth = 150;

	/**
	 * Indentifier for the error image
	 */
	public static final String PREF_DLG_IMG_TITLE_ERROR = DLG_IMG_MESSAGE_ERROR;

	/**
	 * Title area fields
	 */
	public static final String PREF_DLG_TITLE_IMG = "preference_dialog_title_image"; //$NON-NLS-1$
	static {
		ImageRegistry reg = JFaceResources.getImageRegistry();
		reg.put(PREF_DLG_TITLE_IMG, ImageDescriptor.createFromFile(EditorDialog.class,
				"images/pref_dialog_title.gif")); //$NON-NLS-1$
	}

	/**
	 * The current preference page, or <code>null</code> if there is none.
	 */
	private IEditorPage currentPage;

	private DialogMessageArea messageArea;

	/**
	 * Indicates whether help is available; <code>false</code> by default.'
	 * 
	 * @see #setHelpAvailable
	 */
	private boolean isHelpAvailable = false;

	private Point lastShellSize;

	private IEditorNode lastSuccessfulNode;

	/**
	 * The minimum page size; 400 by 400 by default.
	 * 
	 * @see #setMinimumPageSize(Point)
	 */
	private Point minimumPageSize = new Point(400, 400);

	/**
	 * The OK button.
	 */
	private Button okButton;

	/**
	 * The Composite in which a page is shown.
	 */
	private Composite pageContainer;

	/**
	 * The preference manager.
	 */
	private EditorPageManager editorPageManager;

	/**
	 * Flag for the presence of the error message.
	 */
	private boolean showingError = false;

	private Composite titleArea;

	/**
	 * The tree viewer.
	 */
	private TreeViewer treeViewer;
	
    private ListenerList<IPageChangedListener> pageChangedListeners = 
    		new ListenerList<IPageChangedListener>(ListenerList.IDENTITY);

	/**
	 * Creates a new preference dialog under the control of the given preference
	 * manager.
	 * 
	 * @param parentShell
	 *            the parent shell
	 * @param manager
	 *            the preference manager
	 */
	public EditorDialog(Shell parentShell, EditorPageManager manager) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
		editorPageManager = manager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
    protected void buttonPressed(int buttonId) {
		switch (buttonId) {
		case IDialogConstants.OK_ID: {
			okPressed();
			return;
		}
		case IDialogConstants.CANCEL_ID: {
			cancelPressed();
			return;
		}
		case IDialogConstants.HELP_ID: {
			helpPressed();
			return;
		}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	@Override
    protected void cancelPressed() {
		// Inform all pages that we are cancelling
		Iterator nodes = editorPageManager.getElements(EditorPageManager.PRE_ORDER).iterator();
		while (nodes.hasNext()) {
			final IEditorNode node = (IEditorNode) nodes.next();
			if (getPage(node) != null) {
				SafeRunnable.run(new SafeRunnable() {
					public void run() {
						if (!getPage(node).performCancel())
							return;
					}
				});
			}
		}
		setReturnCode(CANCEL);
		close();
	}

	/**
	 * Clear the last selected node. This is so that we not chache the last
	 * selection in case of an error.
	 */
	void clearSelectedNode() {
		setSelectedNode(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#close()
	 */
	@Override
    public boolean close() {
		List nodes = editorPageManager.getElements(EditorPageManager.PRE_ORDER);
		for (int i = 0; i < nodes.size(); i++) {
			IEditorNode node = (IEditorNode) nodes.get(i);
			node.disposeResources();
		}
		return super.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
    protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(JFaceResources.getString("EditorDialog.title")); //$NON-NLS-1$
		newShell.addShellListener(new ShellAdapter() {
			@Override
            public void shellActivated(ShellEvent e) {
				if (lastShellSize == null)
					lastShellSize = getShell().getSize();
			}

		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#constrainShellSize()
	 */
	@Override
    protected void constrainShellSize() {
		super.constrainShellSize();
		// record opening shell size
		if (lastShellSize == null)
			lastShellSize = getShell().getSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
    protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		getShell().setDefaultButton(okButton);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		if (isHelpAvailable) {
			createButton(parent, IDialogConstants.HELP_ID, IDialogConstants.HELP_LABEL, false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
    protected Control createContents(final Composite parent) {
		final Control[] control = new Control[1];
		BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
			public void run() {
				control[0] = EditorDialog.super.createContents(parent);
				// Add the first page
				selectSavedItem();
			}
		});

		return control[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
    protected Control createDialogArea(Composite parent) {
		final Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout parentLayout = ((GridLayout) composite.getLayout());
		parentLayout.numColumns = 4;
		parentLayout.marginHeight = 0;
		parentLayout.marginWidth = 0;
		parentLayout.verticalSpacing = 0;
		parentLayout.horizontalSpacing = 0;
		
		composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		
		Control treeControl = createTreeAreaContents(composite);
		createSash(composite,treeControl);
		
		Label versep = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
		GridData verGd = new GridData(GridData.FILL_VERTICAL | GridData.GRAB_VERTICAL);
	
		versep.setLayoutData(verGd);
		versep.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		
		Composite pageAreaComposite = new Composite(composite, SWT.NONE);
		pageAreaComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(1, true);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		pageAreaComposite.setLayout(layout);
		
	
		
		// Build the title area and separator line
		Composite titleComposite = new Composite(pageAreaComposite, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		titleComposite.setLayout(layout);
		GridData titleLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		titleLayoutData.horizontalIndent = IDialogConstants.HORIZONTAL_MARGIN;
		titleComposite.setLayoutData(titleLayoutData);
		createTitleArea(titleComposite);
		
		Label separator = new Label(pageAreaComposite, SWT.HORIZONTAL | SWT.SEPARATOR);

		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		
		
		// Build the Page container
		pageContainer = createPageContainer(pageAreaComposite);
		GridData pageContainerData = (GridData) pageContainer.getLayoutData(); //new GridData(GridData.FILL_BOTH);
		pageContainerData.horizontalIndent = IDialogConstants.HORIZONTAL_MARGIN;
		pageContainer.setLayoutData(pageContainerData);
		// Build the separator line
		Label bottomSeparator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		bottomSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		return composite;
	}

	/**
	 * Create the sash with right control on the right. Note 
	 * that this method assumes GridData for the layout data
	 * of the rightControl.
	 * @param composite
	 * @param rightControl
	 * @return Sash
	 * 
	 * @since 3.1
	 */
	protected Sash createSash(final Composite composite, final Control rightControl) {
		final Sash sash = new Sash(composite, SWT.VERTICAL);
		sash.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		sash.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		// the following listener resizes the tree control based on sash deltas.
		// If necessary, it will also grow/shrink the dialog.
		sash.addListener(SWT.Selection, new Listener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
			 */
			public void handleEvent(Event event) {
				if (event.detail == SWT.DRAG)
					return;
				int shift = event.x - sash.getBounds().x;
				GridData data = (GridData) rightControl.getLayoutData();
				int newWidthHint = data.widthHint + shift;
				if (newWidthHint < 20)
					return;
				Point computedSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
				Point currentSize = getShell().getSize();
				// if the dialog wasn't of a custom size we know we can shrink
				// it if necessary based on sash movement.
				boolean customSize = !computedSize.equals(currentSize);
				data.widthHint = newWidthHint;
				setLastTreeWidth(newWidthHint);
				composite.layout(true);
				// recompute based on new widget size
				computedSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
				// if the dialog was of a custom size then increase it only if
				// necessary.
				if (customSize)
					computedSize.x = Math.max(computedSize.x, currentSize.x);
				computedSize.y = Math.max(computedSize.y, currentSize.y);
				if (computedSize.equals(currentSize))
					return;
				setShellSize(computedSize.x, computedSize.y);
				lastShellSize = getShell().getSize();
			}
		});
		return sash;
	}

	/**
	 * Creates the inner page container.
	 * 
	 * @param parent
	 * @return Composite
	 */
	protected Composite createPageContainer(Composite parent) {
		
		//Create an outer composite for spacing
		Composite outer = new Composite(parent, SWT.NONE);
		
		GridData outerData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL);
				
		outer.setLayout(new GridLayout());
		outer.setLayoutData(outerData);

		Composite result = new Composite(outer, SWT.NONE);
		
		GridData resultData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL);
				
		result.setLayout(getPageLayout());
		result.setLayoutData(resultData);
		
		return result;
	}

	/**
	 * Return the layout for the composite that contains
	 * the pages.
	 * @return PageLayout
	 * 
	 * @since 3.1
	 */
	protected Layout getPageLayout() {
		return new PageLayout();
	}

	/**
	 * Creates the wizard's title area.
	 * 
	 * @param parent
	 *            the SWT parent for the title area composite.
	 * @return the created title area composite.
	 */
	protected Composite createTitleArea(Composite parent) {
		// Create the title area which will contain
		// a title, message, and image.
		int margins = 2;
		titleArea = new Composite(parent, SWT.NONE);
		FormLayout layout = new FormLayout();
		layout.marginHeight = 0;
		layout.marginWidth = margins;
		titleArea.setLayout(layout);

		
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = SWT.TOP;
		titleArea.setLayoutData(layoutData);

		// Message label
		messageArea = new DialogMessageArea();
		messageArea.createContents(titleArea);

		titleArea.addControlListener(new ControlAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
			 */
			@Override
            public void controlResized(ControlEvent e) {
				updateMessage();
			}
		});

		final IPropertyChangeListener fontListener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (JFaceResources.BANNER_FONT.equals(event.getProperty()))
					updateMessage();
				if (JFaceResources.DIALOG_FONT.equals(event.getProperty())) {
					updateMessage();
					Font dialogFont = JFaceResources.getDialogFont();
					updateTreeFont(dialogFont);
					Control[] children = ((Composite) buttonBar).getChildren();
					for (int i = 0; i < children.length; i++)
						children[i].setFont(dialogFont);
				}
			}
		};

		titleArea.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				JFaceResources.getFontRegistry().removeListener(fontListener);
			}
		});
		JFaceResources.getFontRegistry().addListener(fontListener);
		messageArea.setTitleLayoutData(createMessageAreaData());
		messageArea.setMessageLayoutData(createMessageAreaData());
		return titleArea;
	}

	/**
	 * Create the layout data for the message area.
	 * 
	 * @return FormData for the message area.
	 */
	private FormData createMessageAreaData() {
		FormData messageData = new FormData();
		messageData.top = new FormAttachment(0);
		messageData.bottom = new FormAttachment(100);
		messageData.right = new FormAttachment(100);
		messageData.left = new FormAttachment(0);
		return messageData;
	}

	/**
	 * @param parent
	 *            the SWT parent for the tree area controls.
	 * @return the new <code>Control</code>.
	 * @since 3.0
	 */
	protected Control createTreeAreaContents(Composite parent) {
		// Build the tree an put it into the composite.
		treeViewer = createTreeViewer(parent);
		treeViewer.setInput(getEditorPageManager());
		updateTreeFont(JFaceResources.getDialogFont());
		layoutTreeAreaControl(treeViewer.getControl());
		return treeViewer.getControl();
	}

	/**
	 * Create a new <code>TreeViewer</code>.
	 * 
	 * @param parent
	 *            the parent <code>Composite</code>.
	 * @return the <code>TreeViewer</code>.
	 * @since 3.0
	 */
	protected TreeViewer createTreeViewer(Composite parent) {
		final TreeViewer viewer = new TreeViewer(parent, SWT.NONE);
		addListeners(viewer);
		viewer.setLabelProvider(new EditorPageLabelProvider());
		viewer.setContentProvider(new EditorPageContentProvider()); 
		return viewer;
	}

	/**
	 * Add the listeners to the tree viewer.
	 * @param viewer
	 * 
	 * @since 3.1
	 */
	protected void addListeners(final TreeViewer viewer) {
		viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
			private void handleError() {
				try {
					// remove the listener temporarily so that the events caused
					// by the error handling dont further cause error handling
					// to occur.
					viewer.removePostSelectionChangedListener(this);
					showPageFlippingAbortDialog();
					selectCurrentPageAgain();
					clearSelectedNode();
				} finally {
					viewer.addPostSelectionChangedListener(this);
				}
			}

			public void selectionChanged(SelectionChangedEvent event) {
				Object selection = getSingleSelection(event.getSelection());
				if (selection instanceof IEditorNode) {
					if (!isCurrentPageValid()) {
						handleError();
					} else if (!showPage((IEditorNode) selection)) {
						// Page flipping wasn't successful
						handleError();
					} else {
						// Everything went well
						lastSuccessfulNode = (IEditorNode) selection;
					}
				}
			}
		});
		((Tree) viewer.getControl()).addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetDefaultSelected(final SelectionEvent event) {
				ISelection selection = viewer.getSelection();
				if (selection.isEmpty())
					return;
				IEditorNode singleSelection = getSingleSelection(selection);
				boolean expanded = viewer.getExpandedState(singleSelection);
				viewer.setExpandedState(singleSelection, !expanded);
			}
		});
		//Register help listener on the tree to use context sensitive help
		viewer.getControl().addHelpListener(new HelpListener() {
			public void helpRequested(HelpEvent event) {
				// call perform help on the current page
				if (currentPage != null) {
					currentPage.performHelp();
				}
			}
		});
	}

	/**
	 * Find the <code>IEditorNode</code> that has data the same id as the
	 * supplied value.
	 * 
	 * @param nodeId
	 *            the id to search for.
	 * @return <code>IEditorNode</code> or <code>null</code> if not
	 *         found.
	 */
	protected IEditorNode findNodeMatching(String nodeId) {
		List nodes = editorPageManager.getElements(EditorPageManager.POST_ORDER);
		for (Iterator i = nodes.iterator(); i.hasNext();) {
			IEditorNode node = (IEditorNode) i.next();
			if (node.getId().equalsIgnoreCase(nodeId))
				return node;
		}
		return null;
	}

	/**
	 * Get the last known right side width.
	 * 
	 * @return the width.
	 */
	protected int getLastRightWidth() {
		return lastTreeWidth;
	}

	/**
	 * Returns the preference mananger used by this preference dialog.
	 * 
	 * @return the preference mananger
	 */
	public EditorPageManager getEditorPageManager() {
		return editorPageManager;
	}

	/**
	 * Get the name of the selected item preference
	 * 
	 * @return String
	 */
	protected String getSelectedNode() {
		return lastPageId;
	}

	/**
	 * @param selection
	 *            the <code>ISelection</code> to examine.
	 * @return the first element, or null if empty.
	 */
	protected IEditorNode getSingleSelection(ISelection selection) {
		if (!selection.isEmpty()) {
			IStructuredSelection structured = (IStructuredSelection) selection;
			if (structured.getFirstElement() instanceof IEditorNode)
				return (IEditorNode) structured.getFirstElement();
		}
		return null;
	}

	/**
	 * @return the <code>TreeViewer</code> for this dialog.
	 * @since 3.0
	 */
	protected TreeViewer getTreeViewer() {
		return treeViewer;
	}

	/**
	 * Notifies that the window's close button was pressed, the close menu was
	 * selected, or the ESCAPE key pressed.
	 * <p>
	 * The default implementation of this framework method sets the window's
	 * return code to <code>CANCEL</code> and closes the window using
	 * <code>close</code>. Subclasses may extend or reimplement.
	 * </p>
	 */
	@Override
    protected void handleShellCloseEvent() {
		// handle the same as pressing cancel
		cancelPressed();
	}

	/**
	 * Notifies of the pressing of the Help button.
	 * <p>
	 * The default implementation of this framework method calls
	 * <code>performHelp</code> on the currently active page.
	 * </p>
	 */
	protected void helpPressed() {
		if (currentPage != null) {
			currentPage.performHelp();
		}
	}

	/**
	 * Returns whether the current page is valid.
	 * 
	 * @return <code>false</code> if the current page is not valid, or or
	 *         <code>true</code> if the current page is valid or there is no
	 *         current page
	 */
	protected boolean isCurrentPageValid() {
		if (currentPage == null)
			return true;
		return currentPage.isValid();
	}

	/**
	 * @param control
	 *            the <code>Control</code> to lay out.
	 * @since 3.0
	 */
	protected void layoutTreeAreaControl(Control control) {
		GridData gd = new GridData(GridData.FILL_VERTICAL);
		gd.widthHint = getLastRightWidth();
		gd.verticalSpan = 1;
		control.setLayoutData(gd);
	}

	/**
	 * The preference dialog implementation of this <code>Dialog</code>
	 * framework method sends <code>performOk</code> to all pages of the
	 * preference dialog, then calls <code>handleSave</code> on this dialog to
	 * save any state, and then calls <code>close</code> to close this dialog.
	 */
	@Override
    protected void okPressed() {
		SafeRunnable.run(new SafeRunnable() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.core.runtime.ISafeRunnable#run()
			 */
			public void run() {
				getButton(IDialogConstants.OK_ID).setEnabled(false);
				boolean hasFailedOK = false;
				try {
					// Notify all the pages and give them a chance to abort
					Iterator nodes = editorPageManager.getElements(EditorPageManager.PRE_ORDER)
							.iterator();
					while (nodes.hasNext()) {
						IEditorNode node = (IEditorNode) nodes.next();
						IEditorPage page = node.getPage();
						if (page != null) {
							if (!page.performOk()){
								hasFailedOK = true;
								return;
							}
						}
					}
				} catch (Exception e) {
					handleException(e);
				} finally {
					//Don't bother closing if the OK failed
					if(hasFailedOK)
						return;
					
					close();
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.core.runtime.ISafeRunnable#handleException(java.lang.Throwable)
			 */
			@Override
            public void handleException(Throwable e) {
				SLDPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, SLDPlugin.ID, 0, e.toString(), e));

				clearSelectedNode();
				String message = JFaceResources.getString("SafeRunnable.errorMessage"); //$NON-NLS-1$
				MessageDialog.openError(getShell(), JFaceResources.getString("Error"), message); //$NON-NLS-1$

			}
		});
	}

	/**
	 * Selects the page determined by <code>lastSuccessfulNode</code> in the
	 * page hierarchy.
	 */
	void selectCurrentPageAgain() {
		if (lastSuccessfulNode == null)
			return;
		getTreeViewer().setSelection(new StructuredSelection(lastSuccessfulNode));
		currentPage.setVisible(true);
	}

	/**
	 * Selects the saved item in the tree of preference pages. If it cannot do
	 * this it saves the first one.
	 */
	protected void selectSavedItem() {
		IEditorNode node = findNodeMatching(getSelectedNode());
		if (node == null) {
			IEditorNode[] nodes = editorPageManager.getRootSubNodes();
            if (lastPageId != null)
                node = findNodeMatching(lastPageId);
			if (node == null && nodes.length > 0)
				node = nodes[0];
		}
		if (node != null) {
			getTreeViewer().setSelection(new StructuredSelection(node), true);
			// Keep focus in tree. See bugs 2692, 2621, and 6775.
			getTreeViewer().getControl().setFocus();
		}
	}

	/**
	 * Display the given error message. The currently displayed message is saved
	 * and will be redisplayed when the error message is set to
	 * <code>null</code>.
	 * 
	 * @param newErrorMessage
	 *            the errorMessage to display or <code>null</code>
	 */
	public void setErrorMessage(String newErrorMessage) {
		if (newErrorMessage == null)
			messageArea.clearErrorMessage();
		else
			messageArea.updateText(newErrorMessage, IMessageProvider.ERROR);
	}

	/**
	 * Save the last known tree width.
	 * 
	 * @param width
	 *            the width.
	 */
	private void setLastTreeWidth(int width) {
		lastTreeWidth = width;
	}

	/**
	 * Sets whether a Help button is available for this dialog.
	 * <p>
	 * Clients must call this framework method before the dialog's control has
	 * been created.
	 * <p>
	 * 
	 * @param b
	 *            <code>true</code> to include a Help button, and
	 *            <code>false</code> to not include one (the default)
	 */
	public void setHelpAvailable(boolean b) {
		isHelpAvailable = b;
	}

	/**
	 * Set the message text. If the message line currently displays an error,
	 * the message is stored and will be shown after a call to clearErrorMessage
	 * <p>
	 * Shortcut for <code>setMessage(newMessage, NONE)</code>
	 * </p>
	 * 
	 * @param newMessage
	 *            the message, or <code>null</code> to clear the message
	 */
	public void setMessage(String newMessage) {
		setMessage(newMessage, IMessageProvider.NONE);
	}

	/**
	 * Sets the message for this dialog with an indication of what type of
	 * message it is.
	 * <p>
	 * The valid message types are one of <code>NONE</code>,
	 * <code>INFORMATION</code>,<code>WARNING</code>, or
	 * <code>ERROR</code>.
	 * </p>
	 * <p>
	 * Note that for backward compatibility, a message of type
	 * <code>ERROR</code> is different than an error message (set using
	 * <code>setErrorMessage</code>). An error message overrides the current
	 * message until the error message is cleared. This method replaces the
	 * current message and does not affect the error message.
	 * </p>
	 * 
	 * @param newMessage
	 *            the message, or <code>null</code> to clear the message
	 * @param newType
	 *            the message type
	 * @since 2.0
	 */
	public void setMessage(String newMessage, int newType) {
		messageArea.updateText(newMessage, newType);
	}

	/**
	 * Sets the minimum page size.
	 * 
	 * @param minWidth
	 *            the minimum page width
	 * @param minHeight
	 *            the minimum page height
	 * @see #setMinimumPageSize(Point)
	 */
	public void setMinimumPageSize(int minWidth, int minHeight) {
		minimumPageSize.x = minWidth;
		minimumPageSize.y = minHeight;
	}

	/**
	 * Sets the minimum page size.
	 * 
	 * @param size
	 *            the page size encoded as <code>new Point(width,height)</code>
	 * @see #setMinimumPageSize(int,int)
	 */
	public void setMinimumPageSize(Point size) {
		minimumPageSize.x = size.x;
		minimumPageSize.y = size.y;
	}

	/**
	 * Save the currently selected node.
	 */
	private void setSelectedNode() {
		String storeValue = null;
		IStructuredSelection selection = (IStructuredSelection) getTreeViewer().getSelection();
		if (selection.size() == 1) {
			IEditorNode node = (IEditorNode) selection.getFirstElement();
			storeValue = node.getId();
		}
		setSelectedNode(storeValue);
	}

	/**
	 * Sets the name of the selected item preference.
	 * 
	 * @param pageId
	 *            The identifier for the page
	 */
	public void setSelectedNode(String pageId) {
		lastPageId = pageId;
	}

	/**
	 * Changes the shell size to the given size, ensuring that it is no larger
	 * than the display bounds.
	 * 
	 * @param width
	 *            the shell width
	 * @param height
	 *            the shell height
	 */
	private void setShellSize(int width, int height) {
		Rectangle preferred = getShell().getBounds();
		preferred.width = width;
		preferred.height = height;
		getShell().setBounds(getConstrainedShellBounds(preferred));
	}

	/**
	 * Shows the preference page corresponding to the given preference node.
	 * Does nothing if that page is already current.
	 * 
	 * @param node
	 *            the preference node, or <code>null</code> if none
	 * @return <code>true</code> if the page flip was successful, and
	 *         <code>false</code> is unsuccessful
	 */
	protected boolean showPage(IEditorNode node) {
		if (node == null)
			return false;
		// Create the page if nessessary
		if (node.getPage() == null)
			createPage(node);
		if (node.getPage() == null)
			return false;
		IEditorPage newPage = getPage(node);
		if (newPage == currentPage)
			return true;
		if (currentPage != null) {
			if (!currentPage.okToLeave())
				return false;
		}
		IEditorPage oldPage = currentPage;
		currentPage = newPage;
		// Set the new page's container
		currentPage.setContainer(this);
		// Ensure that the page control has been created
		// (this allows lazy page control creation)
		if (currentPage.getControl() == null) {
			final boolean[] failed = { false };
			SafeRunnable.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					failed[0] = true;
				}

				public void run() {
					createPageControl(currentPage, pageContainer);
				}
			});
			if (failed[0])
				return false;
			// the page is responsible for ensuring the created control is
			// accessable
			// via getControl.
			Assert.isNotNull(currentPage.getControl());
		}
		// Force calculation of the page's description label because
		// label can be wrapped.
		final Point[] size = new Point[1];
		final Point failed = new Point(-1, -1);
		SafeRunnable.run(new ISafeRunnable() {
			public void handleException(Throwable e) {
				size[0] = failed;
			}

			public void run() {
				size[0] = currentPage.computeSize();
			}
		});
		if (size[0].equals(failed))
			return false;
		Point contentSize = size[0];
		// Do we need resizing. Computation not needed if the
		// first page is inserted since computing the dialog's
		// size is done by calling dialog.open().
		// Also prevent auto resize if the user has manually resized
		Shell shell = getShell();
		Point shellSize = shell.getSize();
		if (oldPage != null) {
			Rectangle rect = pageContainer.getClientArea();
			Point containerSize = new Point(rect.width, rect.height);
			int hdiff = contentSize.x - containerSize.x;
			int vdiff = contentSize.y - containerSize.y;
			if ((hdiff > 0 || vdiff > 0) && shellSize.equals(lastShellSize)) {
					hdiff = Math.max(0, hdiff);
					vdiff = Math.max(0, vdiff);
					setShellSize(shellSize.x + hdiff, shellSize.y + vdiff);
					lastShellSize = shell.getSize();
					if (currentPage.getControl().getSize().x == 0)
						currentPage.getControl().setSize(containerSize);
				
			} else //Set the size to be sure we use the result of computeSize
				currentPage.setSize(containerSize);
		}
		// Ensure that all other pages are invisible
		// (including ones that triggered an exception during
		// their creation).
		Control[] children = pageContainer.getChildren();
		Control currentControl = currentPage.getControl();
		for (int i = 0; i < children.length; i++) {
			if (children[i] != currentControl)
				children[i].setVisible(false);
		}
		// Make the new page visible
		currentPage.setVisible(true);
		if (oldPage != null)
			oldPage.setVisible(false);
		// update the dialog controls
		update();
		return true;
	}

	/**
	 * Create the page for the node.
	 * @param node
	 * 
	 * @since 3.1
	 */
	protected void createPage(IEditorNode node) {
		node.createPage(getPageContainer(), this);
	}

	/**
	 * Get the page for the node.
	 * @param node
	 * @return IEditorPage
	 * 
	 * @since 3.1
	 */
	protected IEditorPage getPage(IEditorNode node) {
		return node.getPage();
	}

	/**
	 * Shows the "Page Flipping abort" dialog.
	 */
	void showPageFlippingAbortDialog() {
//		MessageDialog.openError(getShell(), JFaceResources
//				.getString("AbortPageFlippingDialog.title"), //$NON-NLS-1$
//				JFaceResources.getString("AbortPageFlippingDialog.message")); //$NON-NLS-1$
	}

	/**
	 * Updates this dialog's controls to reflect the current page.
	 */
	protected void update() {
		// Update the title bar
		updateTitle();
		// Update the message line
		updateMessage();
		// Update the buttons
		updateButtons();
		//Saved the selected node in the preferences
		setSelectedNode();
		firePageChanged(new PageChangedEvent(this, getCurrentPage()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IEditorPageContainer#updateButtons()
	 */
	public void updateButtons() {
		okButton.setEnabled(isCurrentPageValid());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IEditorPageContainer#updateMessage()
	 */
	public void updateMessage() {
		String message = null;
		String errorMessage = null;
		if(currentPage != null){
			message = currentPage.getMessage();
			errorMessage = currentPage.getErrorMessage();
		}
		int messageType = IMessageProvider.NONE;
		if (message != null && currentPage instanceof IMessageProvider)
			messageType = ((IMessageProvider) currentPage).getMessageType();

		if (errorMessage == null){
			if (showingError) {
				// we were previously showing an error
				showingError = false;
			}
		}
		else {
			message = errorMessage;
			messageType = IMessageProvider.ERROR;
			if (!showingError) {
				// we were not previously showing an error
				showingError = true;
			}
		}  
		messageArea.updateText(message,messageType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IEditorPageContainer#updateTitle()
	 */
	public void updateTitle() {
		if(currentPage == null)
			return;
		messageArea.showTitle(currentPage.getTitle(), currentPage.getImage());
	}

	/**
	 * Update the tree to use the specified <code>Font</code>.
	 * 
	 * @param dialogFont
	 *            the <code>Font</code> to use.
	 * @since 3.0
	 */
	protected void updateTreeFont(Font dialogFont) {
		getTreeViewer().getControl().setFont(dialogFont);
	}

	/**
	 * Returns the currentPage.
	 * @return IEditorPage
	 * @since 3.1
	 */
	protected IEditorPage getCurrentPage() {
		return currentPage;
	}

	/**
	 * Sets the current page.
	 * @param currentPage
	 * 
	 * @since 3.1
	 */
	protected void setCurrentPage(IEditorPage currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * Set the treeViewer.
	 * @param treeViewer
	 * 
	 * @since 3.1
	 */
	protected void setTreeViewer(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	/**
	 * Get the composite that is showing the page.
	 *
	 * @return Composite.
	 * 
	 * @since 3.1
	 */
	protected Composite getPageContainer() {
		return this.pageContainer;
	}

	/**
	 * Set the composite that is showing the page.
	 * @param pageContainer Composite
	 * 
	 * @since 3.1
	 */
	protected void setPageContainer(Composite pageContainer) {
		this.pageContainer = pageContainer;
	}
	/**
	 * Create the page control for the supplied page.
	 * 
	 * @param page - the preference page to be shown
	 * @param parent - the composite to parent the page
	 * 
	 * @since 3.1
	 */
	protected void createPageControl(IEditorPage page, Composite parent) {
		page.createControl(parent);
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.IPageChangeProvider#getSelectedPage()
	 * 
	 * @since 3.1
	 */
	public Object getSelectedPage() {
			return getCurrentPage();
		}
	
	/**
	 * @see org.eclipse.jface.dialogs.IPageChangeProvider#addPageChangedListener(org.eclipse.jface.dialogs.IPageChangedListener)
	 * @since 3.1
	 */
	public void addPageChangedListener(IPageChangedListener listener) {
		pageChangedListeners.add(listener);
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.IPageChangeProvider#removePageChangedListener(org.eclipse.jface.dialogs.IPageChangedListener)
	 * @since 3.1
	 */
	public void removePageChangedListener(IPageChangedListener listener) {
		pageChangedListeners.remove(listener);
		
	}

	/**
     * Notifies any selection changed listeners that the selected page
     * has changed.
     * Only listeners registered at the time this method is called are notified.
     *
     * @param event a selection changed event
     *
     * @see IPageChangedListener#pageChanged
     * 
     * @since 3.1
     */
    protected void firePageChanged(final PageChangedEvent event) {
        Object[] listeners = pageChangedListeners.getListeners();
        for (int i = 0; i < listeners.length; i++) {
            final IPageChangedListener l = (IPageChangedListener) listeners[i];
            SafeRunnable.run(new SafeRunnable() {
                public void run() {
                    l.pageChanged(event);
                }
            });
        }
    }	
}
