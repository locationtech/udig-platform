/* Image Georeferencing
 * 
 * Axios Engineering 
 *      http://www.axios.es 
 *
 * (C) 2011, Axios Engineering S.L. (Axios)
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.image.georeferencing.internal.ui.imagepanel;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import net.refractions.udig.project.ui.tool.IToolContext;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.locationtech.udig.image.georeferencing.internal.i18n.Messages;
import org.locationtech.udig.image.georeferencing.internal.preferences.Preferences;
import org.locationtech.udig.image.georeferencing.internal.process.MarkModel;
import org.locationtech.udig.image.georeferencing.internal.ui.GeoReferencingCommand;
import org.locationtech.udig.image.georeferencing.internal.ui.GeoReferencingComposite;
import org.locationtech.udig.image.georeferencing.internal.ui.GeoreferencingCommandEventChange;
import org.locationtech.udig.image.georeferencing.internal.ui.InputEvent;
import org.locationtech.udig.image.georeferencing.internal.ui.MainComposite;
import org.locationtech.udig.image.georeferencing.internal.ui.MouseSelectionListener;
import org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.ImageComposite.ZoomFeedBack.ZOOM_TYPE;
import org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.tools.AddMarkImageTool;
import org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.tools.DeleteMarkImageTool;
import org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.tools.ImageInputEvent;
import org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.tools.ImageTool;
import org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.tools.MoveMarkImageTool;
import org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.tools.PanImageTool;
import org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.tools.ZoomInImageTool;
import org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.tools.ZoomOutImageTool;


/**
 * Class responsible of the canvas and its management.
 * 
 * It's also responsible of the creation of the mark models. This class contains
 * tools which are used to manipulate marks in the canvas.
 * 
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.3
 * 
 */
public final class ImageComposite extends Composite implements Observer, GeoReferencingComposite {

	private Composite					imageComposite						= null;
	private Composite					buttonComposite						= null;
	private Canvas						canvas								= null;
	private Image						image								= null;
	private IToolContext				toolContext							= null;
	private ToolBar						imageToolBar						= null;
	private ToolItem					itemLoad							= null;
	private ToolItem					itemAdd								= null;
	private ToolItem					itemDelete							= null;
	private ToolItem					itemDeleteAll						= null;
	private ToolItem					itemDragDrop						= null;
	private ToolItem					itemZoomIn							= null;
	private ToolItem					itemZoomOut							= null;
	private ToolItem					itemZoomFit							= null;
	private ToolItem					itemPan								= null;

	private ImageRegistry				registry							= null;
	@SuppressWarnings("unused")
	private Thread						uiThread							= null;

	private List<MarkImagePresenter>	markPresenterList					= new LinkedList<MarkImagePresenter>();

	private GeoReferencingCommand		cmd									= null;

	private ImageMetricPosition			imageMetrics						= null;
	private List<ImageTool>				tools								= null;

	private ZoomFeedBack				zoomFeedback						= null;

	private MouseSelectionListener		mapMouseSelectionListener			= null;
	private MouseSelectionListener		coordPanelMouseSelectionListener	= null;

	public ImageComposite(GeoReferencingCommand cmd, Composite parent, int style) {
		super(parent, style);

		assert cmd != null;
		this.cmd = cmd;
		createContent();
		pack();
	}

	private void createContent() {

		this.registry = createImageRegistry();
		this.uiThread = Thread.currentThread();
		this.imageMetrics = new ImageMetricPosition();

		createListeners();

		createNewTools();

		// layout for this image composite
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = false;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 3;
		gridData.verticalAlignment = GridData.FILL;

		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.horizontalSpan = 3;
		gridData2.verticalAlignment = GridData.FILL;

		GridLayout gridlayout = new GridLayout(3, true);
		setLayout(gridlayout);

		buttonComposite = new Composite(this, SWT.BORDER);
		buttonComposite.setLayoutData(gridData);
		buttonComposite.setLayout(gridlayout);
		createButtons(buttonComposite);

		imageComposite = new Composite(this, SWT.BORDER);
		imageComposite.setLayoutData(gridData2);
		imageComposite.setLayout(gridlayout);
		createCanvas(imageComposite);
	}

	/**
	 * Listener used by the Image composite.
	 * 
	 * This composite listen to the map tools and to the coordinate table panel.
	 */
	private void createListeners() {

		this.mapMouseSelectionListener = new MouseSelectionListener() {

			public void inEvent(MarkModel mark) {

				// feedback
				mouseOverFeedback(mark);
			}

			public void outEvent(MarkModel mark) {

				mouseNotOverFeedback(mark);
			}
		};

		this.coordPanelMouseSelectionListener = new MouseSelectionListener() {

			public void inEvent(MarkModel mark) {
				// feedback
				mouseOverFeedback(mark);
			}

			public void outEvent(MarkModel mark) {

				mouseNotOverFeedback(mark);
			}
		};
	}

	/**
	 * Create the images that are shown in the tools.
	 * 
	 * @return The imageRegistry
	 */
	private ImageRegistry createImageRegistry() {

		ImageRegistry registry = new ImageRegistry(this.getDisplay());

		String opId = "DeleteAll"; //$NON-NLS-1$
		String imgFile = "image/cancel_all_co2.gif"; //$NON-NLS-1$ 
		registry.put(opId, ImageDescriptor.createFromFile(ImageComposite.class, imgFile));

		opId = "Delete"; //$NON-NLS-1$
		imgFile = "image/clear_co2.gif"; //$NON-NLS-1$ 
		registry.put(opId, ImageDescriptor.createFromFile(ImageComposite.class, imgFile));

		opId = "Load"; //$NON-NLS-1$
		imgFile = "image/folder.png"; //$NON-NLS-1$ 
		registry.put(opId, ImageDescriptor.createFromFile(ImageComposite.class, imgFile));

		opId = "Add"; //$NON-NLS-1$
		imgFile = "image/placemark_pointer2.gif"; //$NON-NLS-1$ 
		registry.put(opId, ImageDescriptor.createFromFile(ImageComposite.class, imgFile));

		opId = "Move"; //$NON-NLS-1$
		imgFile = "image/movemarker.png"; //$NON-NLS-1$ 
		registry.put(opId, ImageDescriptor.createFromFile(ImageComposite.class, imgFile));

		opId = "ZoomIn"; //$NON-NLS-1$
		imgFile = "image/zoom-in-5.png"; //$NON-NLS-1$ 
		registry.put(opId, ImageDescriptor.createFromFile(ImageComposite.class, imgFile));

		opId = "ZoomOut"; //$NON-NLS-1$
		imgFile = "image/zoom-out-5.png"; //$NON-NLS-1$ 
		registry.put(opId, ImageDescriptor.createFromFile(ImageComposite.class, imgFile));

		opId = "ZoomFit"; //$NON-NLS-1$
		imgFile = "image/zoom_extent.gif"; //$NON-NLS-1$ 
		registry.put(opId, ImageDescriptor.createFromFile(ImageComposite.class, imgFile));

		opId = "Pan"; //$NON-NLS-1$
		imgFile = "image/pan.png"; //$NON-NLS-1$ 
		registry.put(opId, ImageDescriptor.createFromFile(ImageComposite.class, imgFile));

		return registry;
	}

	/**
	 * Creates all the {@link ImageTool} with its associated cursor.
	 */
	private void createNewTools() {

		this.tools = new LinkedList<ImageTool>();

		// cursors:
		Cursor cursor = null;
		Image image = null;
		Display device = Display.getCurrent();

		image = this.registry.get("Add"); //$NON-NLS-1$
		cursor = new Cursor(device, image.getImageData(), 8, 8);
		AddMarkImageTool addTool = new AddMarkImageTool(cursor, this);

		image = this.registry.get("Delete"); //$NON-NLS-1$
		cursor = new Cursor(device, image.getImageData(), 1, 14);
		DeleteMarkImageTool deleteTool = new DeleteMarkImageTool(cursor, this);

		image = this.registry.get("Move"); //$NON-NLS-1$
		cursor = new Cursor(device, image.getImageData(), 6, 6);
		MoveMarkImageTool moveTool = new MoveMarkImageTool(cursor, this);

		image = this.registry.get("ZoomIn"); //$NON-NLS-1$
		cursor = new Cursor(device, image.getImageData(), 5, 5);
		ZoomInImageTool zoomInTool = new ZoomInImageTool(cursor, this);

		image = this.registry.get("ZoomOut"); //$NON-NLS-1$
		cursor = new Cursor(device, image.getImageData(), 5, 5);
		ZoomOutImageTool zoomOutTool = new ZoomOutImageTool(cursor, this);

		image = this.registry.get("Pan"); //$NON-NLS-1$
		cursor = new Cursor(device, image.getImageData(), 7, 7);
		PanImageTool panTool = new PanImageTool(cursor, this);

		this.tools.add(addTool);
		this.tools.add(deleteTool);
		this.tools.add(moveTool);
		this.tools.add(zoomInTool);
		this.tools.add(zoomOutTool);
		this.tools.add(panTool);
	}

	/**
	 * Given the class, it'll active from the list of tools the one that match
	 * the desired class.
	 * 
	 * @param clazz
	 *            ImageTool class to be activated.
	 */
	private void setToolActive(Class<?> clazz) {

		for (ImageTool tool : tools) {

			if (tool.getClass().equals(clazz)) {
				tool.setActive(true);
			} else {
				tool.setActive(false);
			}
		}
	}

	private void createButtons(Composite parent) {

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.verticalAlignment = GridData.FILL;

		imageToolBar = new ToolBar(parent, SWT.LEFT_TO_RIGHT);

		itemLoad = new ToolItem(imageToolBar, SWT.PUSH);
		itemLoad.setImage(this.registry.get("Load")); //$NON-NLS-1$
		itemLoad.setToolTipText(Messages.ImageComposite_itemLoadTooltip);
		itemLoad.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {

				loadFile();
			}
		});
		itemLoad.setEnabled(false);

		itemAdd = new ToolItem(imageToolBar, SWT.RADIO);
		itemAdd.setImage(this.registry.get("Add")); //$NON-NLS-1$
		itemAdd.setToolTipText(Messages.ImageComposite_itemAddTooltip);
		itemAdd.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {

				setToolActive(AddMarkImageTool.class);
			}
		});

		itemDelete = new ToolItem(imageToolBar, SWT.RADIO);
		itemDelete.setImage(this.registry.get("Delete")); //$NON-NLS-1$
		itemDelete.setToolTipText(Messages.ImageComposite_itemDeleteTooltip);
		itemDelete.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {

				setToolActive(DeleteMarkImageTool.class);
			}
		});

		itemDragDrop = new ToolItem(imageToolBar, SWT.RADIO);
		itemDragDrop.setImage(this.registry.get("Move")); //$NON-NLS-1$
		itemDragDrop.setToolTipText(Messages.ImageComposite_itemDragDropTooltip);
		itemDragDrop.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {

				setToolActive(MoveMarkImageTool.class);
			}
		});

		itemZoomIn = new ToolItem(imageToolBar, SWT.RADIO);
		itemZoomIn.setImage(this.registry.get("ZoomIn")); //$NON-NLS-1$
		itemZoomIn.setToolTipText(Messages.ImageComposite_itemZoomInTooltip);
		itemZoomIn.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {

				setToolActive(ZoomInImageTool.class);
			}
		});

		itemZoomOut = new ToolItem(imageToolBar, SWT.RADIO);
		itemZoomOut.setImage(this.registry.get("ZoomOut")); //$NON-NLS-1$
		itemZoomOut.setToolTipText(Messages.ImageComposite_itemZoomOutTooltip);
		itemZoomOut.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {

				setToolActive(ZoomOutImageTool.class);
			}
		});

		itemPan = new ToolItem(imageToolBar, SWT.RADIO);
		itemPan.setImage(this.registry.get("Pan")); //$NON-NLS-1$
		itemPan.setToolTipText(Messages.ImageComposite_itemPanTooltip);
		itemPan.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {

				setToolActive(PanImageTool.class);
			}
		});

		itemDeleteAll = new ToolItem(imageToolBar, SWT.PUSH);
		itemDeleteAll.setImage(this.registry.get("DeleteAll")); //$NON-NLS-1$
		itemDeleteAll.setToolTipText(Messages.ImageComposite_itemDelAllTooltip);
		itemDeleteAll.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {

				deactivateTools();
				deleteAllPoints();
				getMainComposite().refreshMapGraphicLayer();
			}
		});

		itemZoomFit = new ToolItem(imageToolBar, SWT.PUSH);
		itemZoomFit.setImage(this.registry.get("ZoomFit")); //$NON-NLS-1$
		itemZoomFit.setToolTipText(Messages.ImageComposite_itemFitTooltip);
		itemZoomFit.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {

				zoomFit();
			}
		});

		setItemsEnabled(false);
		setCertainToolsEnabled(false);
	}

	/**
	 * Deactivate the tools and deselect them.
	 */
	private void deactivateTools() {

		for (ImageTool tool : tools) {

			tool.setActive(false);
		}

		setItemsSelected(false);
	}

	private void setItemsSelected(boolean selected) {

		this.itemAdd.setSelection(selected);
		this.itemDelete.setSelection(selected);
		this.itemDragDrop.setSelection(selected);
		this.itemDeleteAll.setSelection(selected);
		this.itemZoomIn.setSelection(selected);
		this.itemZoomOut.setSelection(selected);
		this.itemZoomFit.setSelection(selected);
		this.itemPan.setSelection(selected);
	}

	private void setItemsEnabled(boolean enabled) {

		this.itemAdd.setEnabled(enabled);
		this.itemZoomIn.setEnabled(enabled);
		this.itemZoomOut.setEnabled(enabled);
		this.itemZoomFit.setEnabled(enabled);
		this.itemPan.setEnabled(enabled);
	}

	private void setCertainToolsEnabled(boolean enabled) {

		this.itemDelete.setEnabled(enabled);
		this.itemDragDrop.setEnabled(enabled);
		this.itemDeleteAll.setEnabled(enabled);

		if (!enabled) {
			this.itemDelete.setSelection(false);
			this.itemDragDrop.setSelection(false);
			this.itemDeleteAll.setSelection(false);
		}
	}

	private void createCanvas(Composite parent) {

		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.horizontalSpan = 3;
		gridData2.verticalAlignment = GridData.FILL;

		canvas = new Canvas(parent, SWT.BACKGROUND | SWT.DOUBLE_BUFFERED);
		canvas.setLayoutData(gridData2);
		canvas.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				performAction(e, InputEvent.MOUSE_UP);
			}

			@Override
			public void mouseDown(MouseEvent e) {

				performAction(e, InputEvent.MOUSE_DOWN);
			}
		});

		canvas.addMouseMoveListener(new MouseMoveListener() {

			public void mouseMove(MouseEvent e) {

				performAction(e, InputEvent.MOUSE_DRAG);
			}
		});

		canvas.addMouseWheelListener(new MouseWheelListener() {

			public void mouseScrolled(MouseEvent e) {

				performAction(e, InputEvent.MOUSE_SCROLL);
			}
		});

		canvas.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event event) {

				if (image == null) {
					return;
				}
				GC gc = event.gc;
				ImageData imageData = imageMetrics.getImageData();

				if (zoomFeedback != null) {
					zoomFeedback(gc, imageData);
				} else {

					// XXX we may improve it. Give the image already modified
					/*
					 * Scale the image when drawing, using the user's selected
					 * scaling factor.
					 */
					int w = Math.round(imageData.width * imageMetrics.getScale());
					int h = Math.round(imageData.height * imageMetrics.getScale());

					/* Draw the image */
					gc.drawImage(image, 0, 0, imageData.width, imageData.height, imageMetrics.getHScrollValue()
								+ imageData.x, imageMetrics.getVScrollValue() + imageData.y, w, h);
				}
			}
		});

		canvas.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent event) {
				if (image == null) {
					return;
				}
				imageMetrics.updateMaxXY();
			}
		});

	}

	/**
	 * Loads the file inside the canvas.
	 */
	private void loadFile() {

		setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_WAIT));

		FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell());

		dialog.setFileName(Preferences.getImagePath());
		// dialog.setFileName(cmd.getImagePath());

		String file = dialog.open();

		if (file != null && !file.equals("")) { //$NON-NLS-1$

			this.image = new Image(Display.getCurrent(), file);
			setDefaultLoadData();
			Preferences.setImagePath(file);
			this.cmd.setImagePath(file);
			this.cmd.setImageData(this.imageMetrics.getImageData());

			canvas.redraw(0, 0, this.canvas.getClientArea().width, this.canvas.getClientArea().height, false);

			// restore the default layout/values
			deleteAllPoints();
			getMainComposite().refreshMapGraphicLayer();
		}
		setCursor(null);
	}

	/**
	 * Set the default data for the ImageMetrics class.
	 */
	private void setDefaultLoadData() {

		assert this.image != null;
		assert this.imageMetrics != null;

		this.imageMetrics.setImageData(image.getImageData());
		this.imageMetrics.setDefaultValues();
	}

	public void update(Observable o, Object arg) {

		if (!(arg instanceof GeoreferencingCommandEventChange))
			return;
		GeoreferencingCommandEventChange cmdEvent = (GeoreferencingCommandEventChange) arg;

		switch (cmdEvent.getEvent()) {
		case IMAGE_LOADED:
			setItemsEnabled(true);
			break;
		default:
			setCertainToolsEnabled(cmd.canEnableImageTools());
			break;
		}
	}

	/**
	 * Set buttons deselected.
	 */
	private void resetRadioButtons() {

		itemAdd.setSelection(false);
		itemDelete.setSelection(false);
		itemDragDrop.setSelection(false);
		itemZoomIn.setSelection(false);
		itemZoomOut.setSelection(false);
		itemPan.setSelection(false);
	}

	/**
	 * Delete all the points from the canvas and also the current preview if
	 * exist.
	 */
	public void deleteAllPoints() {

		this.markPresenterList.clear();
		this.cmd.deleteAllMarks();
		this.canvas.redraw();
		this.canvas.setCursor(null);
		resetRadioButtons();
		deactivateTools();
	}

	/**
	 * Handles all the input actions of the canvas.
	 * 
	 * @param e
	 *            Mouse event.
	 * @param eventType
	 *            Input event.
	 */
	private void performAction(MouseEvent e, InputEvent eventType) {

		// get the clicked point
		int x = e.x;
		int y = e.y;
		ImageInputEvent event = new ImageInputEvent(e, eventType, x, y);

		ImageTool tool = getActiveTool();
		if (tool == null) {
			return;

		}
		this.canvas.setCursor(tool.getCursor());
		tool.eventHandle(event);
	}

	/**
	 * @return The active tool.
	 */
	private ImageTool getActiveTool() {

		for (ImageTool tool : tools) {

			if (tool.isActive()) {
				return tool;
			}
		}
		return null;
	}

	public void setContext(IToolContext newContext) {

		if (this.toolContext == null) {
			// add the listener the first time.
			getMainComposite().getMapMarkGraphic().addMouseSelectionListener(mapMouseSelectionListener);
			getMainComposite().addMouseSelectionListenerToCoordinate(coordPanelMouseSelectionListener);
		}
		this.toolContext = newContext;
		if (cmd.getMap() != null) {
			itemLoad.setEnabled(true);
		}
	}

	/**
	 * When the view is closed, delete the listeners.
	 * 
	 * @param mainComposite
	 *            The main composite.
	 */
	public void close(MainComposite mainComposite) {

		if (this.toolContext != null) {// && !isDisposed()) {
			// delete the listener
			mainComposite.getMapMarkGraphic().deleteMouseSelectionListener(mapMouseSelectionListener);
			mainComposite.deleteMouseSelectionListenerToCoordinate(coordPanelMouseSelectionListener);
		}
	}

	/**
	 * Given the following mark list, creates its presenters and store these
	 * marks in the command.
	 * 
	 * @param marks
	 *            Marks loaded from a property file.
	 */
	public void createMarks(Map<String, MarkModel> marks) {

		int hScroll = Math.abs(imageMetrics.getHScrollValue());
		int vScroll = Math.abs(imageMetrics.getVScrollValue());

		Set<Entry<String, MarkModel>> entrySet = marks.entrySet();
		Iterator<Entry<String, MarkModel>> iter = entrySet.iterator();
		while (iter.hasNext()) {

			Entry<String, MarkModel> entry = iter.next();
			MarkModel markModel = entry.getValue();
			
			Point position = new Point(markModel.getXImage(), markModel.getYImage());
			
			MarkImagePresenter markPresenter = MarkImagePresenterFactory.createMarkPresenter(markModel, position,  hScroll, vScroll, getCanvas(), getScale());
			
			addMarkPresenter(markPresenter);

			this.cmd.addMark(markModel);
		}
		this.cmd.evalPrecondition();
		canvas.redraw();
	}

	/**
	 * Get the main composite.
	 * 
	 * @return The {@link MainComposite}.
	 */
	public MainComposite getMainComposite() {

		Composite parent = getParent();
		for (;;) {
			if (parent instanceof MainComposite) {
				return (MainComposite) parent;
			} else {
				parent = parent.getParent();
			}
		}
	}

	@Override
	public void setEnabled(boolean enabled) {

		this.imageToolBar.setEnabled(enabled);
		this.canvas.setEnabled(enabled);

		super.setEnabled(enabled);
	}

	/**
	 * The scale.
	 * 
	 * @return
	 */
	public float getScale() {

		return this.imageMetrics.getScale();
	}

	/**
	 * Broadcast the pan event through all the mark presenters.
	 */
	public void broadcastPanEvent() {

		int hScroll = Math.abs(imageMetrics.getHScrollValue());
		int vScroll = Math.abs(imageMetrics.getVScrollValue());

		// broadcast this event through all the presenters.
		for (MarkImagePresenter presenter : markPresenterList) {

			presenter.eventHandler(InputEvent.PAN, hScroll, vScroll);
		}
	}

	/**
	 * Broadcast the zoom event through all the mark presenters.
	 */
	public void broadcastZoomEvent() {

		// broadcast this event through all the presenters.
		for (MarkImagePresenter presenter : markPresenterList) {

			presenter.eventHandler(InputEvent.ZOOM, this.imageMetrics.getScale());
		}
	}

	/**
	 * Fit the image inside the canvas. Calculates which side will fit better,
	 * width or height.
	 */
	private void zoomFit() {

		this.imageMetrics.updateScrollValues(0, 0);

		// calculate the xscale and yscale so the image width or height will fit
		// the canvas size.
		ImageData imageData = this.imageMetrics.getImageData();

		float canvasHeight = this.canvas.getClientArea().height;
		float canvasWidth = this.canvas.getClientArea().width;
		float imageHeight = imageData.height;
		float imageWidth = imageData.width;

		float heightFactor = canvasHeight / imageHeight;
		float widthFactor = canvasWidth / imageWidth;

		if (heightFactor > widthFactor) {
			this.imageMetrics.updateScale(heightFactor);
		} else {
			this.imageMetrics.updateScale(widthFactor);
		}

		updateZoomState();
	}

	/**
	 * Update some variables that involves zooming, broadcast zoom and pan event
	 * to the mark presenters and repaint them.
	 */
	private void updateZoomState() {

		this.imageMetrics.updateMaxXY();

		this.broadcastPanEvent();
		this.broadcastZoomEvent();

		if (this.image != null) {
			this.canvas.redraw();
		}
	}

	/**
	 * Check if the given coordinate is under any of the existent mark image
	 * presenters
	 * 
	 * @param x
	 *            Position
	 * @param y
	 *            Position
	 * @return If it found a presenter, it'll return it. Null otherwise.
	 */
	public MarkImagePresenter getMarkUnderCursor(int x, int y) {

		for (MarkImagePresenter presenter : this.markPresenterList) {

			if (presenter.eventHandler(InputEvent.MOUSE_OVER, x, y)) {

				return presenter;
			}
		}
		return null;
	}

	/**
	 * @return The mark presenters list.
	 */
	public List<MarkImagePresenter> getMarkPresenterList() {

		return this.markPresenterList;
	}

	/**
	 * Used by the presenters.
	 * 
	 * @return The canvas.
	 */
	public Canvas getCanvas() {

		return this.canvas;
	}

	/**
	 * @return The {@link GeoReferencingCommand}.
	 */
	public GeoReferencingCommand getCmd() {

		return this.cmd;
	}

	/**
	 * Add the given presenter to the list of {@link MarkImagePresenterImp}.
	 * 
	 * @param newMarkPresenter
	 *            The presenter.
	 */
	public void addMarkPresenter(MarkImagePresenter newMarkPresenter) {

		this.markPresenterList.add(newMarkPresenter);
	}

	/**
	 * @return The are of the canvas.
	 */
	public Rectangle getCanvasClientArea() {

		return this.canvas.getClientArea();
	}

	/**
	 * Makes a canvas redraw.
	 */
	public void canvasRedraw() {

		this.canvas.redraw();
	}

	/**
	 * @return The horizontal scroll value.
	 */
	public int getHScrollValue() {

		return this.imageMetrics.getHScrollValue();
	}

	/**
	 * @return The vertical scroll value.
	 */
	public int getVScrollValue() {

		return this.imageMetrics.getVScrollValue();
	}

	/**
	 * @return The max X.
	 */
	public int getMaxX() {

		return this.imageMetrics.getMaxX();
	}

	/**
	 * @return The max Y.
	 */
	public int getMaxY() {

		return this.imageMetrics.getMaxY();
	}

	/**
	 * Updates the values of both scroll.
	 * 
	 * @param xScrollSelection
	 *            Horizontal scroll value.
	 * @param yScrollSelection
	 *            Vertical scroll value.
	 */
	public void updateScrollValues(int xScrollSelection, int yScrollSelection) {

		this.imageMetrics.updateScrollValues(xScrollSelection, yScrollSelection);
	}

	/**
	 * Increase the current value of the scale.
	 * 
	 * @param addValue
	 *            Increment value.
	 */
	public void increaseScale(float addValue) {

		this.imageMetrics.increaseScale(addValue);
	}

	/**
	 * Decrease the current value of the scale.
	 * 
	 * @param subtractValue
	 *            Decrement value.
	 */
	public void decreaseScale(float subtractValue) {

		this.imageMetrics.decreaseScale(subtractValue);
	}

	/**
	 * Check if the given point is inside the image borders.
	 * 
	 * @param point
	 *            Point to validate.
	 * @return True if it lies inside the image.
	 */
	public boolean validateInside(Point point) {

		return this.image.getBounds().contains(point);
	}

	/**
	 * <p>
	 * Zoom operation.
	 * </p>
	 * 
	 * Given the clicked point and the actual and previous zoom factor, it'll
	 * calculate the current image position inside the canvas.
	 * 
	 * <p>
	 * If the image position is smaller than the canvas, it'll show at the 0,0
	 * position.
	 * 
	 * If the entire image can be shown inside the canvas area, it'll show it no
	 * matter what are the scrolls position in that moment.
	 * </p>
	 * 
	 * @param x
	 *            Position in the canvas.
	 * @param y
	 *            Position in the canvas.
	 */
	public void focusPosition(int x, int y) {

		ImageData imageData = this.imageMetrics.getImageData();
		int w = Math.round(imageData.width * this.imageMetrics.getScale());
		int h = Math.round(imageData.height * this.imageMetrics.getScale());
		Rectangle area = this.getCanvasClientArea();

		// first, calculate if any of the sides of the image can fit inside the
		// canvas. If that's true, there will not be any scroll.
		int HScroll;
		int VScroll;
		if (area.width >= w || area.height >= h) {

			HScroll = 0;
			VScroll = 0;
		} else {

			float oldHScrollValue = this.imageMetrics.getHScrollValue();
			float oldVScrollValue = this.imageMetrics.getVScrollValue();

			// calculate the actual click point value respect the image.
			int xImg = x + Math.abs(Math.round(oldHScrollValue));
			int yImg = y + Math.abs(Math.round(oldVScrollValue));

			int actualXimg = Math.round((Math.round(xImg * this.imageMetrics.getScale()))
						/ this.imageMetrics.getPreviousScale());
			int actualYimg = Math.round((Math.round(yImg * this.imageMetrics.getScale()))
						/ this.imageMetrics.getPreviousScale());

			// negative values
			HScroll = -(Math.abs(actualXimg - x));
			VScroll = -(Math.abs(actualYimg - y));

			// check the scroll fit inside the canvas
			if ((HScroll != 0) && (Math.abs(HScroll) + this.imageMetrics.getMaxX() < area.width)) {
				HScroll = 0;
			}
			if ((VScroll != 0) && (Math.abs(VScroll) + this.imageMetrics.getMaxY() < area.height)) {
				VScroll = 0;
			}
		}

		this.imageMetrics.updatePreviousScroll();
		this.imageMetrics.updateScrollValues(HScroll, VScroll);
		this.imageMetrics.updatePreviousScale();

		this.updateZoomState();
	}

	/**
	 * Creates the zoom feedback when zoom in happens.
	 * 
	 * @param x
	 *            Image position.
	 * @param y
	 *            Image position.
	 */
	public void setZoomInFeedback(int x, int y) {
		this.zoomFeedback = new ZoomFeedBack(x, y, ZOOM_TYPE.ZOOM_IN);
	}

	/**
	 * Creates the zoom feedback when zoom out happens.
	 * 
	 * @param x
	 *            Image position.
	 * @param y
	 *            Image position.
	 */
	public void setZoomOutFeedback(int x, int y) {
		this.zoomFeedback = new ZoomFeedBack(x, y, ZOOM_TYPE.ZOOM_OUT);
	}

	/**
	 * Feedback method, draw the image as it is before zoom and show the
	 * feedback.
	 * 
	 * @param gc
	 * @param imageData
	 */
	private void zoomFeedback(GC gc, ImageData imageData) {

		int w = Math.round(imageData.width * imageMetrics.getScaleBeforeZoom());
		int h = Math.round(imageData.height * imageMetrics.getScaleBeforeZoom());

		/* Draw the image */
		gc.drawImage(image, 0, 0, imageData.width, imageData.height, imageMetrics.getHScrollBeforeZoom() + imageData.x,
					imageMetrics.getVScrollBeforeZoom() + imageData.y, w, h);

		switch (zoomFeedback.type) {
		case ZOOM_IN:
			zoomInFeedBack(gc, zoomFeedback.x, zoomFeedback.y, zoomFeedback.startNumber);
			break;
		case ZOOM_OUT:
			zoomOutFeedBack(gc, zoomFeedback.x, zoomFeedback.y, zoomFeedback.startNumber);
			break;
		default:
			break;
		}

		this.canvas.redraw();

	}

	/**
	 * Updates the number of times the feedback will be shown when doing zoom
	 * in.
	 * 
	 * @param gc
	 *            Graphic context
	 * @param x
	 *            Position
	 * @param y
	 *            Position
	 * @param startNumber
	 *            feedback number counter.
	 */
	private void zoomInFeedBack(GC gc, int x, int y, int startNumber) {

		drawZoomFeedBack(gc, x, y, startNumber);

		if (startNumber == 3) {
			zoomFeedback = null;
		} else {
			zoomFeedback.startNumber++;
		}
	}

	/**
	 * Updates the number of times the feedback will be shown when doing zoom
	 * out.
	 * 
	 * @param gc
	 *            Graphic context
	 * @param x
	 *            Position
	 * @param y
	 *            Position
	 * @param startNumber
	 *            feedback number counter.
	 */
	private void zoomOutFeedBack(GC gc, int x, int y, int startNumber) {

		drawZoomFeedBack(gc, x, y, startNumber);

		if (startNumber == 1) {
			zoomFeedback = null;
		} else {
			zoomFeedback.startNumber--;
		}
	}

	/**
	 * Draws the feedback. It'll draw 3 different feedbacks depending on the
	 * start number.
	 * 
	 * @param gc
	 *            Graphic context
	 * @param x
	 *            Position
	 * @param y
	 *            Position
	 * @param startNumber
	 *            Current tick number, between 1 and 3.
	 */
	private void drawZoomFeedBack(GC gc, int x, int y, int startNumber) {

		Color c = Color.RED;
		org.eclipse.swt.graphics.Color color = new org.eclipse.swt.graphics.Color(gc.getDevice(), c.getRed(),
					c.getGreen(), c.getBlue());
		gc.setForeground(color);
		gc.setBackground(color);

		int subtractValue = 0, addValue = 0;

		switch (startNumber) {
		case 1:
			subtractValue = 15;
			addValue = 30;
			break;
		case 2:
			subtractValue = 20;
			addValue = 40;
			break;
		case 3:
			subtractValue = 25;
			addValue = 50;
			break;
		default:
			break;
		}
		gc.drawRectangle(x - subtractValue, y - subtractValue, addValue, addValue);

		gc.drawLine(x, y + subtractValue, x, y + addValue);
		gc.drawLine(x, y - subtractValue, x, y - addValue);
		gc.drawLine(x - subtractValue, y, x - addValue, y);
		gc.drawLine(x + subtractValue, y, x + addValue, y);
	}

	/**
	 * When a mouse over happens on the map, show the feedback in the image
	 * composite.
	 * 
	 * @param mark
	 *            The mark model.
	 */
	private void mouseOverFeedback(MarkModel mark) {

		// find the presenter which contains this mark, and show the feedback
		// for the given mark.

		for (MarkImagePresenter presenter : this.markPresenterList) {

			if (presenter.getMarkModel().equals(mark)) {
				presenter.showSelectedFeedback(true);
			} else {
				presenter.showSelectedFeedback(false);
			}
		}
		this.canvas.redraw();
	}

	/**
	 * Represents the feedback when the mouse is not over a mark, that means
	 * that this mark musn't show his feedback.
	 * 
	 * @param mark
	 *            The mark model.
	 */
	private void mouseNotOverFeedback(MarkModel mark) {

		for (MarkImagePresenter presenter : this.markPresenterList) {

			if (presenter.getMarkModel().equals(mark)) {
				presenter.showSelectedFeedback(false);
			}
		}
		this.canvas.redraw();
	}

	/**
	 * Adds a {@link MouseSelectionListener} to the tools.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void addMouseSelectionListener(MouseSelectionListener listener) {

		for (ImageTool tool : tools) {

			tool.addMouseSelectionListener(listener);
		}
	}

	/**
	 * Delete a {@link MouseSelectionListener} from the tools.
	 * 
	 * @param listener
	 *            The listener
	 */
	public void deleteMouseSelectionListener(MouseSelectionListener listener) {

		for (ImageTool tool : tools) {

			tool.deleteMouseSelectionListener(listener);
		}
	}

	/**
	 * Class used to maintain valuable info about the feedback process.
	 */
	static class ZoomFeedBack {
		protected int		x, y, startNumber;
		protected ZOOM_TYPE	type;

		public enum ZOOM_TYPE {
			ZOOM_IN, ZOOM_OUT
		}

		/**
		 * Constructor. Depending on the zoom_type it'll automatically set a
		 * value in the startNumber variable.
		 * 
		 * @param x
		 *            Position.
		 * @param y
		 *            Position.
		 * @param type
		 *            Zoom type (in/out).
		 */
		public ZoomFeedBack(int x, int y, ZOOM_TYPE type) {

			this.x = x;
			this.y = y;
			this.type = type;
			switch (type) {
			case ZOOM_IN:
				this.startNumber = 1;
				break;
			case ZOOM_OUT:
				this.startNumber = 3;
				break;
			default:
				break;
			}
		}
	}
}
