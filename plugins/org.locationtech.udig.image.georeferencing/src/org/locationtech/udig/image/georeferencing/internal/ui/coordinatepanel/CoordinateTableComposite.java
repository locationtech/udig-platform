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
package org.locationtech.udig.image.georeferencing.internal.ui.coordinatepanel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.impl.EditManagerImpl;
import org.locationtech.udig.project.render.IViewportModel;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.project.ui.tool.Tool;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Coordinate;

import org.locationtech.udig.image.georeferencing.internal.i18n.Messages;
import org.locationtech.udig.image.georeferencing.internal.process.MarkModel;
import org.locationtech.udig.image.georeferencing.internal.process.MarkModel.MarkModelChange;
import org.locationtech.udig.image.georeferencing.internal.ui.GeoReferencingCommand;
import org.locationtech.udig.image.georeferencing.internal.ui.GeoReferencingComposite;
import org.locationtech.udig.image.georeferencing.internal.ui.GeoreferencingCommandEventChange;
import org.locationtech.udig.image.georeferencing.internal.ui.InputEvent;
import org.locationtech.udig.image.georeferencing.internal.ui.MainComposite;
import org.locationtech.udig.image.georeferencing.internal.ui.MouseSelectionListener;
import org.locationtech.udig.image.georeferencing.internal.ui.coordinatepanel.tools.AddCoordinateTool;
import org.locationtech.udig.image.georeferencing.internal.ui.coordinatepanel.tools.CapturedCoordinateListener;
import org.locationtech.udig.image.georeferencing.internal.ui.coordinatepanel.tools.CoordToolPropertyValue;
import org.locationtech.udig.image.georeferencing.internal.ui.coordinatepanel.tools.DeleteCoordinateTool;
import org.locationtech.udig.image.georeferencing.internal.ui.coordinatepanel.tools.DeletedCoordinateListener;
import org.locationtech.udig.image.georeferencing.internal.ui.coordinatepanel.tools.MoveCoordinateListener;
import org.locationtech.udig.image.georeferencing.internal.ui.coordinatepanel.tools.MoveCoordinateTool;
import org.locationtech.udig.image.georeferencing.internal.ui.message.InfoMessage;
import org.locationtech.udig.image.georeferencing.internal.ui.message.InfoMessage.Type;

/**
 * Composite responsible of managing the mark model coordinates edit operations
 * with the aid of tools that interact with the map and a table to directly edit
 * them.
 * 
 * It's also an observer of the {@link GeoReferencingCommand} and
 * {@link MarkModel}.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.3
 * 
 */
public final class CoordinateTableComposite extends Composite implements Observer, GeoReferencingComposite {

	private CLabel							crsLabel				= null;
	private CLabel							mapLabel;
	private Table							coordinatesTable		= null;

	private Composite						compositeGridCoord		= null;
	private GeoReferencingCommand			cmd						= null;
	private IToolContext					toolContext				= null;

	private TableColumn						tableColumnX			= null;
	private TableColumn						tableColumnY			= null;
	private TableColumn						tableColumnID			= null;
	private TableEditor						editorX					= null;
	private TableEditor						editorY					= null;

	@SuppressWarnings("unused")
	private Thread							uiThread				= null;

	private CapturedCoordinateListener		capturedListener		= null;
	private DeletedCoordinateListener		deletedListener			= null;
	private MoveCoordinateListener			moveListener			= null;

	private MouseSelectionListener			mapSelectionListener	= null;
	private MouseSelectionListener			imageSelectionListener	= null;

	private AddCoordinateTool				addTool				= null;
	private DeleteCoordinateTool			deleteTool				= null;
	private MoveCoordinateTool				moveTool				= null;

	private ToolBar							mapToolBar				= null;
	private ImageRegistry					registry				= null;

	private List<MouseSelectionListener>	listeners				= new LinkedList<MouseSelectionListener>();
	private ToolItem						itemDeleteAll;

	/**
	 * Constructor method.
	 * 
	 * @param cmd
	 *            The georeferencing command.
	 * @param parent
	 *            The parent composite.
	 * @param style
	 *            The style.
	 */
	public CoordinateTableComposite(GeoReferencingCommand cmd, Composite parent, int style) {

		super(parent, style);

		assert cmd != null;
		this.cmd = cmd;
		createContent();
	}

	private void createContent() {

		this.registry = createImageRegistry();
		this.uiThread = Thread.currentThread();

		createListeners();
		setCoordinateTools();

		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 2;
		gridLayout2.makeColumnsEqualWidth = true;

		compositeGridCoord = new Composite(this, SWT.BORDER);
		compositeGridCoord.setLayout(gridLayout2);

		createCRSselector(compositeGridCoord);
		createToolbar(compositeGridCoord);
		createGrid(compositeGridCoord);
	}

	/**
	 * Creates the required listeners, the majority of them are tool listeners.
	 */
	private void createListeners() {

		this.capturedListener = new CapturedCoordinateListener() {

			public void capturedCoordinate(Coordinate newCoord) {

				addCoordinateFromMapToTable(newCoord);

				assert getMainComposite().getMapMarkGraphic() != null;
				getMainComposite().refreshMapGraphicLayer();
			}

			public void activated(boolean active) {
			}
		};

		this.deletedListener = new DeletedCoordinateListener() {

			public void deletedCoordinate(java.awt.Point point, InputEvent event) {

				deleteFromTable(point, event);

				// feedback to show the removed coordinate
				assert getMainComposite().getMapMarkGraphic() != null;
				getMainComposite().refreshMapGraphicLayer();
			}

			public void activated(boolean active) {
			}

		};

		this.moveListener = new MoveCoordinateListener() {

			public void MoveCoordinate(java.awt.Point coor, InputEvent event) {

				moveFromTable(coor, event);

				// feedback to show the removed coordinate
				assert getMainComposite().getMapMarkGraphic() != null;
				getMainComposite().refreshMapGraphicLayer();
			}

			public void activated(boolean active) {
			}
		};

		this.mapSelectionListener = new MouseSelectionListener() {

			public void inEvent(MarkModel mark) {

				selectRow(mark);
			}

			public void outEvent(MarkModel mark) {

				deselectRow(mark);
			}
		};

		this.imageSelectionListener = new MouseSelectionListener() {

			public void inEvent(MarkModel mark) {

				selectRow(mark);
			}

			public void outEvent(MarkModel mark) {

				deselectRow(mark);
			}
		};
	}

	/**
	 * Creates the {@link ImageRegistry} that stores the image shown by the
	 * tools.
	 * 
	 * @return The imageRegistry.
	 */
	private ImageRegistry createImageRegistry() {

		ImageRegistry registry = new ImageRegistry(this.getDisplay());

		String opId = "Delete"; //$NON-NLS-1$
		String imgFile = "image/delete.gif"; //$NON-NLS-1$ 
		registry.put(opId, ImageDescriptor.createFromFile(CoordinateTableComposite.class, imgFile));

		opId = "Add"; //$NON-NLS-1$
		imgFile = "image/add.png"; //$NON-NLS-1$ 
		registry.put(opId, ImageDescriptor.createFromFile(CoordinateTableComposite.class, imgFile));

		opId = "Move"; //$NON-NLS-1$
		imgFile = "image/movemarker.png"; //$NON-NLS-1$ 
		registry.put(opId, ImageDescriptor.createFromFile(CoordinateTableComposite.class, imgFile));

		opId = "DeleteAll"; //$NON-NLS-1$
		imgFile = "image/deleteAll.gif"; //$NON-NLS-1$ 
		registry.put(opId, ImageDescriptor.createFromFile(CoordinateTableComposite.class, imgFile));

		return registry;
	}

	/**
	 * Gets a reference to the tools and add the listeners.
	 */
	private void setCoordinateTools() {

		Tool tool = ApplicationGIS.getToolManager().findTool(AddCoordinateTool.ID);
		this.addTool = (AddCoordinateTool) tool;
		this.addTool.addCapturedCoordinateListener(capturedListener);

		tool = ApplicationGIS.getToolManager().findTool(DeleteCoordinateTool.ID);
		this.deleteTool = (DeleteCoordinateTool) tool;
		this.deleteTool.addDeletedCoordinateListener(deletedListener);

		tool = ApplicationGIS.getToolManager().findTool(MoveCoordinateTool.ID);
		this.moveTool = (MoveCoordinateTool) tool;
		this.moveTool.addMoveCoordinateListener(moveListener);
	}

	private void createCRSselector(Composite parent) {

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalSpan = 1;
		gridData.verticalAlignment = GridData.FILL;

		mapLabel = new CLabel(parent, SWT.NONE);
		mapLabel.setText(Messages.CoordinateTableComposite_map);
		mapLabel.setLayoutData(gridData);

		crsLabel = new CLabel(parent, SWT.NONE);
		crsLabel.setText("CRS:"); //$NON-NLS-1$
		crsLabel.setLayoutData(gridData);
	}

	
	/**
	 * Creates the toolbar that contains the tools that interact with the map.
	 * 
	 * @param parent
	 *            The parent composite.
	 */
	private void createToolbar(Composite parent) {

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalSpan = 2;
		gridData.verticalAlignment = GridData.FILL;

		mapToolBar = new ToolBar(parent, SWT.LEFT_TO_RIGHT);

		itemDeleteAll = new ToolItem(mapToolBar, SWT.PUSH);
		itemDeleteAll.setImage(this.registry.get("DeleteAll")); //$NON-NLS-1$
		itemDeleteAll.setToolTipText(Messages.CoordinateTableComposite_itemDeleteAll_Tooltip);
		itemDeleteAll.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {

				deleteAllGCP();
			}
		});

		setItemsEnabled(false);
		setCertainItemsEnabled(false);

		// present the coordinate tools in the tool panel
		CoordToolPropertyValue.setVisible(true);
	}

	private void setItemsEnabled(boolean enabled) {

	}

	private void setCertainItemsEnabled(boolean enabled) {

		itemDeleteAll.setEnabled(enabled);

		if (!enabled) {
			itemDeleteAll.setSelection(false);
		}
	}

	private void createGrid(final Composite parent) {

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 2;
		gridData.verticalAlignment = GridData.FILL;

		coordinatesTable = new Table(parent, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
					| SWT.HIDE_SELECTION);
		coordinatesTable.setHeaderVisible(true);
		coordinatesTable.setLayoutData(gridData);
		coordinatesTable.setLinesVisible(true);

		tableColumnID = new TableColumn(coordinatesTable, SWT.NONE);
		tableColumnID.setWidth(45);
		tableColumnID.setText("ID"); //$NON-NLS-1$
		tableColumnID.setMoveable(true);
		tableColumnID.setResizable(true);

		tableColumnX = new TableColumn(coordinatesTable, SWT.NONE);
		tableColumnX.setText("X"); //$NON-NLS-1$
		tableColumnX.setMoveable(true);
		tableColumnX.setResizable(true);

		tableColumnY = new TableColumn(coordinatesTable, SWT.NONE);
		tableColumnY.setText("Y"); //$NON-NLS-1$
		tableColumnY.setMoveable(true);
		tableColumnY.setResizable(true);

		parent.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				Rectangle area = parent.getClientArea();
				Point size = coordinatesTable.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				ScrollBar vBar = coordinatesTable.getVerticalBar();
				int width = area.width - coordinatesTable.computeTrim(0, 0, 0, 0).width - vBar.getSize().x;
				if (size.y > area.height + coordinatesTable.getHeaderHeight()) {
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					Point vBarSize = vBar.getSize();
					width -= vBarSize.x;
				}
				Point oldSize = coordinatesTable.getSize();
				if (oldSize.x > area.width) {
					// table is getting smaller so make the columns
					// smaller first and then resize the table to
					// match the client area width
					tableColumnX.setWidth(width / 3);
					tableColumnY.setWidth(width - tableColumnX.getWidth());
					coordinatesTable.setSize(area.width, area.height);
				} else {
					// table is getting bigger so make the table
					// bigger first and then make the columns wider
					// to match the client area width
					coordinatesTable.setSize(area.width, area.height);
					tableColumnX.setWidth(width / 3);
					tableColumnY.setWidth(width - tableColumnX.getWidth());
				}
			}
		});

		editorX = new TableEditor(coordinatesTable);
		// The editor must have the same size as the cell and must
		// not be any smaller than 50 pixels.
		editorX.horizontalAlignment = SWT.LEFT;
		editorX.grabHorizontal = true;
		editorX.minimumWidth = 50;

		editorY = new TableEditor(coordinatesTable);
		// The editor must have the same size as the cell and must
		// not be any smaller than 50 pixels.
		editorY.horizontalAlignment = SWT.LEFT;
		editorY.grabHorizontal = true;
		editorY.minimumWidth = 50;

		coordinatesTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Clean up any previous editor control
				deleteOldEditors();

				// Identify the selected row
				final TableItem item = (TableItem) e.item;
				if (item == null)
					return;
				// get the mark associated to this row.
				final MarkModel mark = (MarkModel) item.getData();

				createEditorColumnX(mark, item);

				createEditorColumnY(mark, item);
				// show feedback using listeners
				broadcastSelectionInEvent(mark);

				getMainComposite().refreshMapGraphicLayer();
			}
		});
	}

	/**
	 * Creates the column editor to edit the X coordinate values.
	 * 
	 * @param mark
	 *            Associated mark model
	 * @param item
	 *            Table item.
	 */
	private void createEditorColumnX(final MarkModel mark, final TableItem item) {

		// The control that will be the editor must be a child of the
		// Table
		final Text editorColumnX = new Text(coordinatesTable, SWT.NONE);
		if (mark != null && !(mark.getXCoord().equals(Double.NaN))) {

			editorColumnX.setText(String.valueOf(mark.getXCoord()));
		}
		editorColumnX.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {

				Text text = (Text) editorX.getEditor();
				String newText = text.getText();
				if (mark != null) {
					try {
						// first, try to parse the text into double
						Double value = Double.parseDouble(newText);
						mark.setXCoord(value);
						cmd.evalPrecondition();
						getMainComposite().refreshMapGraphicLayer();
					} catch (NumberFormatException ex) {
						if (newText.equals("")) { //$NON-NLS-1$
							mark.setXCoord(Double.NaN);
						} else {
							newText = String.valueOf(mark.getXCoord());
						}
						cmd.evalPrecondition();
						getMainComposite().refreshMapGraphicLayer();
					}
				}
				item.setText(1, newText);
			}
		});
		editorColumnX.addKeyListener(new KeyListener() {

			public void keyReleased(KeyEvent e) {
				// not used
			}

			public void keyPressed(KeyEvent e) {

				try {
					validateCharacter(e);
				} catch (NumberFormatException ex) {
					e.doit = false;
				}
			}
		});
		editorColumnX.selectAll();
		editorColumnX.setFocus();
		editorX.setEditor(editorColumnX, item, 1);
	}

	/**
	 * Creates the column editor to edit the Y coordinate values.
	 * 
	 * @param mark
	 *            Associated mark model
	 * @param item
	 *            Table item.
	 */
	private void createEditorColumnY(final MarkModel mark, final TableItem item) {

		final Text editorColumnY = new Text(coordinatesTable, SWT.NONE);
		if (mark != null && !(mark.getYCoord().equals(Double.NaN))) {
			editorColumnY.setText(String.valueOf(mark.getYCoord()));
		}
		editorColumnY.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {

				Text text = (Text) editorY.getEditor();
				String newText = text.getText();
				if (mark != null) {
					try {
						// first, try to parse the text into double
						Double value = Double.parseDouble(newText);
						mark.setYCoord(value);
						cmd.evalPrecondition();
						getMainComposite().refreshMapGraphicLayer();
					} catch (NumberFormatException ex) {
						if (newText.equals("")) { //$NON-NLS-1$
							mark.setYCoord(Double.NaN);
						} else {
							newText = String.valueOf(mark.getYCoord());
						}
						cmd.evalPrecondition();
						getMainComposite().refreshMapGraphicLayer();
					}
				}
				item.setText(2, newText);
			}
		});
		editorColumnY.addKeyListener(new KeyListener() {

			public void keyReleased(KeyEvent e) {
				// not used
			}

			public void keyPressed(KeyEvent e) {

				try {
					validateCharacter(e);
				} catch (NumberFormatException ex) {
					e.doit = false;
				}
			}
		});
		editorY.setEditor(editorColumnY, item, 2);
	}

	/**
	 * Delete old editors from the coordinate table.
	 */
	private void deleteOldEditors() {

		Control oldEditorX = editorX.getEditor();
		if (oldEditorX != null)
			oldEditorX.dispose();

		Control oldEditorY = editorY.getEditor();
		if (oldEditorY != null)
			oldEditorY.dispose();
	}

	/**
	 * Listen to the changes occurred to the command and to the mark model.
	 */
	public void update(Observable obs, Object arg) {

		if (obs instanceof GeoReferencingCommand) {

			setCertainItemsEnabled(cmd.canEnableMapTools());

			GeoreferencingCommandEventChange cmdEvent = (GeoreferencingCommandEventChange) arg;
			MarkModel mark = cmdEvent.getMark();

			switch (cmdEvent.getEvent()) {
			case MARK_ADDED:
				addMarkOnTable(mark);
				break;
			case MARK_DELETED:
				deleteMarkOnTable(mark);
				break;
			case ALL_MARKS_DELETED:
				deleteAllMarksOnTable();
				break;
			default:
				break;
			}

		} else if (obs instanceof MarkModel) {

			MarkModel mark = (MarkModel) obs;
			MarkModelChange event = (MarkModelChange) arg;
			switch (event) {
			case MODIFY:
				modifyCoordinateOnTable(mark);
				break;

			default:
				break;
			}
		}

	}

	/**
	 * Find the row that contains a reference to this mark model and update the
	 * values of that row.
	 * 
	 * @param mark
	 *            The mark model.
	 */
	private void modifyCoordinateOnTable(MarkModel mark) {

		boolean selection = false;
		TableItem[] selectedItems = coordinatesTable.getSelection();
		if (selectedItems.length > 0) {
			selection = true;
		}
		// find the row.
		TableItem[] items = coordinatesTable.getItems();
		for (TableItem item : items) {

			MarkModel compareMark = (MarkModel) item.getData();
			if (mark.equals(compareMark)) {

				Coordinate newCoord = new Coordinate(mark.getXCoord(), mark.getYCoord());
				if (selection && selectedItems[0].equals(item)) {
					// the selected item is the one to update
					createEditorsAndSetText(mark, item, newCoord);
				}

				setRowData(item, mark, newCoord);
			}
		}

	}

	/**
	 * Creates the table editors and set the current value.
	 * 
	 * @param mark
	 *            MarkModel model.
	 * @param item
	 *            Table item.
	 * @param newCoord
	 *            Coordinate with the values.
	 */
	private void createEditorsAndSetText(MarkModel mark, TableItem item, Coordinate newCoord) {

		deleteOldEditors();
		createEditorColumnX(mark, item);
		createEditorColumnY(mark, item);

		Text textX = (Text) editorX.getEditor();
		Text textY = (Text) editorY.getEditor();
		if (!(Double.isNaN(newCoord.x))) {
			textX.setText(String.valueOf(newCoord.x));
		}
		if (!(Double.isNaN(newCoord.y))) {
			textY.setText(String.valueOf(newCoord.y));
		}

	}

	/**
	 * <p>
	 * Adds a mark to the table.
	 * 
	 * Creates a {@link MarkMapPresenterImp} and associates it with the
	 * {@link MapMarksGraphics}.
	 * </p>
	 * 
	 * @param mark
	 *            Mark model.
	 */
	private void addMarkOnTable(MarkModel mark) {

		final TableItem tableItem = new TableItem(coordinatesTable, SWT.NONE);

		// set the mark data to this entire row.
		tableItem.setData(mark);
		tableItem.setText(0, String.valueOf(mark.getID()));

		if (!mark.getXCoord().equals(Double.NaN)) {
			tableItem.setText(1, String.valueOf(mark.getXCoord()));
		}
		if (!mark.getYCoord().equals(Double.NaN)) {
			tableItem.setText(2, String.valueOf(mark.getYCoord()));
		}

		// if a previous row is selected, select the new created row.
		updateRowSelection(tableItem, mark);

		assert getMainComposite().getMapMarkGraphic() != null;

		// create the MarkMapGraphics associated to this mark
		MarkMapPresenter markPresenter = new MarkMapPresenterImp(mark);
		getMainComposite().getMapMarkGraphic().addMarkMapPresenter(markPresenter);

		setItemsEnabled(true);

		mark.addObserver(this);
	}

	/**
	 * Given the next scenario:
	 * 
	 * <pre>
	 * - New row added 
	 * - There was a previous row selected.
	 * </pre>
	 * 
	 * Update the selected row so it'll select the newly created row.
	 * 
	 * @param tableItem
	 *            Table item.
	 * @param mark
	 *            Mark model.
	 */
	private void updateRowSelection(TableItem addedItem, MarkModel mark) {

		TableItem[] selection = coordinatesTable.getSelection();

		if (selection.length > 0) {

			// get the new item
			coordinatesTable.setSelection(addedItem);

			deleteOldEditors();

			createEditorColumnX(mark, addedItem);
			createEditorColumnY(mark, addedItem);
		}
	}

	/**
	 * Adds a coordinate to the table.
	 * 
	 * There must be 1 empty row. If there is a selected row and also is empty,
	 * then the coordinate will be added there. If there isn't any selected row,
	 * the coordinate will be added in the first empty row. If there isn't any
	 * empty rows, the coordinate won't be added.
	 * 
	 * 
	 * @param newCoord
	 *            Coordinate to be added.
	 */
	private void addCoordinateFromMapToTable(Coordinate newCoord) {

		TableItem[] selection = coordinatesTable.getSelection();

		if (selection.length > 0) {
			// selected item
			// get the row and add the coordinate to that row.
			TableItem item = selection[0];
			addCoordinateOnTable(item, newCoord, true);
		} else {// non selected item
			// get the first empty row and add the coordinate to that row.
			boolean added = false;
			TableItem[] items = coordinatesTable.getItems();
			for (TableItem item : items) {

				added = addCoordinateOnTable(item, newCoord, false);
				// if the coordinate was added, return.
				if (added)
					return;
			}
			// if the code reach this point, the coordinate wasn't added, show
			// an advertise message.
			getMainComposite()
						.setMessage(new InfoMessage(Messages.CoordinateTableComposite_cant_add_gcp, Type.WARNING));
		}
	}

	/**
	 * Finds the {@link MainComposite} and returns it.
	 * 
	 * @return The main composite.
	 */
	private MainComposite getMainComposite() {

		if (isDisposed()) {
			return null;
		}
		Composite parent = getParent();
		for (;;) {
			if (parent instanceof MainComposite) {
				return (MainComposite) parent;
			} else {
				parent = parent.getParent();
			}
		}
	}

	/**
	 * Validate if the given character is a valid integer.
	 * 
	 * @param e
	 * @throws NumberFormatException
	 *             When the given character isn't an Integer.
	 */
	private void validateCharacter(KeyEvent e) throws NumberFormatException {

		if (e.character == '.' || e.keyCode == SWT.DEL || e.keyCode == SWT.BS || e.keyCode == SWT.END
					|| e.keyCode == SWT.HOME || e.keyCode == 16777219 || e.keyCode == 16777220 || e.character == '-'
					|| e.character == '+') {
			return;
		}

		Integer.parseInt(String.valueOf(e.character));
	}

	/**
	 * Delete this mark on the table and refresh the layer that contains the
	 * mapGraphic so the changed are shown.
	 * 
	 * @param mark
	 *            Mark model to be deleted.
	 */
	private void deleteMarkOnTable(MarkModel mark) {

		int index = getMarkIndexWithinTheTable(mark);
		deleteOldEditors();
		coordinatesTable.remove(index);

		getMainComposite().refreshMapGraphicLayer();

		mark.deleteObserver(this);
	}

	/**
	 * Find the index of this mark model searching all the table items.
	 * 
	 * @param mark
	 *            The mark model.
	 * @return The index.
	 */
	private int getMarkIndexWithinTheTable(MarkModel mark) {

		int index = -1;
		// find this mark on the table
		TableItem[] items = coordinatesTable.getItems();
		for (TableItem item : items) {
			index++;
			MarkModel itemMark = (MarkModel) item.getData();

			if (mark.equals(itemMark)) {
				break;
			}
		}

		assert index != -1 : "index can't be null, it must have found the given mark."; //$NON-NLS-1$

		return index;
	}

	/**
	 * Delete all the marks from the coordinate table.
	 */
	private void deleteAllMarksOnTable() {

		deleteOldEditors();
		TableItem[] items = coordinatesTable.getItems();
		coordinatesTable.remove(0, items.length - 1);

		assert coordinatesTable.getItems().length == 0;

		getMainComposite().getMapMarkGraphic().clear();
	}

	/**
	 * Add a coordinate into table when mark associated to this table item
	 * doesn't have any coordinate value stored.
	 * 
	 * @param item
	 *            Table item
	 * @param newCoord
	 *            Coordinate to be added.
	 * @return True when the coordinate was added into the table.
	 */
	private boolean addCoordinateOnTable(TableItem item, Coordinate newCoord, boolean existSelection) {

		MarkModel mark = (MarkModel) item.getData();

		if (existSelection) {

			createEditorsAndSetText(mark, item, newCoord);

			setRowData(item, mark, newCoord);

		} else if (mark.getXCoord().equals(Double.NaN) && mark.getYCoord().equals(Double.NaN)) { 
			// if marks are empty.

			setRowData(item, mark, newCoord);
			return true;
		}

		return false;
	}

	/**
	 * Set the data of the given coordinate in the mark model and in the table.
	 * 
	 * @param item
	 *            Table item.
	 * @param mark
	 *            MarkModel model.
	 * @param newCoord
	 *            The coordinate containing the data.
	 */
	private void setRowData(TableItem item, MarkModel mark, Coordinate newCoord) {

		if (!(Double.isNaN(newCoord.x))) {
			item.setText(1, String.valueOf(newCoord.x));
		}else{
			item.setText(1, ""); //$NON-NLS-1$
		}
		if (!(Double.isNaN(newCoord.y))) {
			item.setText(2, String.valueOf(newCoord.y));
		}else{
			item.setText(2, ""); //$NON-NLS-1$
		}

		mark.setXCoord(newCoord.x);
		mark.setYCoord(newCoord.y);
		// evaluate preconditions
		this.cmd.evalPrecondition();
	}

	/**
	 * The first time, adds the listeners.
	 */
	public void setContext(IToolContext newContext) {

		if (this.toolContext == null) {
			// add the listener the first time.
			getMainComposite().getMapMarkGraphic().addMouseSelectionListener(mapSelectionListener);
			getMainComposite().addMouseSelectionListenerToImgComposite(imageSelectionListener);
		}
		this.toolContext = newContext;

		if (toolContext != null) {

			// display the map
			IMap map = toolContext.getMap();
			mapLabel.setText("Map: " + map.getName()); //$NON-NLS-1$
			// display the CRS.
			CoordinateReferenceSystem crs = getCurrentMapCrs(map);
			crsLabel.setText("CRS: " + crs.getName().toString()); //$NON-NLS-1$
		}

	}

	/**
	 * @param map
	 * @return the current map's CRS or null if current map is null
	 */
	private CoordinateReferenceSystem getCurrentMapCrs(IMap map) {
		
        IViewportModel viewportModel = map.getViewportModel();
        CoordinateReferenceSystem mapCrs = viewportModel.getCRS();
		return mapCrs;
	}

	/**
	 * Remove all the listeners of the tools and de-activates them.
	 * 
	 * @param mainComposite
	 *            The main composite.
	 */
	public void close(MainComposite mainComposite) {

		hideCoordinateTools();
		
		// remove the listener
		this.addTool.removeCapturedCoordinateListener(this.capturedListener);
		this.deleteTool.removeDeletedCoordinateListener(this.deletedListener);
		this.moveTool.removeMoveCoordinateListener(this.moveListener);

		this.addTool.setActive(false);
		this.deleteTool.setActive(false);
		this.moveTool.setActive(false);

		if (this.toolContext != null) {// & !isDisposed()) {
			// delete the listener
			mainComposite.getMapMarkGraphic().deleteMouseSelectionListener(mapSelectionListener);
			mainComposite.deleteMouseSelectionListenerToImgComposite(imageSelectionListener);
		}
	}

	/**
	 * Hide the coordinate tools
	 */
	private void hideCoordinateTools() {
		
		// this is a workaround to provoke an event in the layer view that could be catch by the CoordToolPropertyValue  
		CoordToolPropertyValue.setVisible(false);

		EditManagerImpl editManager = (EditManagerImpl) ApplicationGIS.getActiveMap().getEditManager();
		Layer layer = editManager.getSelectedLayer();
		editManager.setSelectedLayerGen(layer);
	}

	@Override
	public void setEnabled(boolean enabled) {

		this.coordinatesTable.setEnabled(enabled);
		if (this.editorX.getEditor() != null && !this.editorX.getEditor().isDisposed()) {
			this.editorX.getEditor().setEnabled(enabled);
		}
		if (this.editorY.getEditor() != null && !this.editorY.getEditor().isDisposed()) {
			this.editorY.getEditor().setEnabled(enabled);
		}

		setItemsEnabled(enabled);
		setCertainItemsEnabled(enabled);

		super.setEnabled(enabled);
	}

	/**
	 * <p>
	 * When a mouse_down event occur, gets all the map presenters and search if
	 * any of these presenters are affected by the event. If any of the
	 * presenter returns true, this mark coordinate data will be deleted from
	 * the table and from the mark model.
	 * 
	 * If the event is not a mouse_down, delegates the event to the
	 * {@link MapMarksGraphics}.
	 * </p>
	 * 
	 * @param point
	 *            Map point where it was clicked.
	 * @param event
	 *            Input event.
	 */
	private void deleteFromTable(java.awt.Point point, InputEvent event) {

		if (InputEvent.MOUSE_DOWN.equals(event)) {
			// get the presenters
			Map<String, MarkMapPresenter> presenters = getMainComposite().getMapMarkGraphic().getPresenters();

			// iterate the presenters and see if the point belongs to any mark
			MarkModel mark = null;
			Set<Entry<String, MarkMapPresenter>> entrySet = presenters.entrySet();
			Iterator<Entry<String, MarkMapPresenter>> iter = entrySet.iterator();
			while (iter.hasNext()) {

				Entry<String, MarkMapPresenter> entry = iter.next();
				MarkMapPresenter mapPresenter = entry.getValue();

				if (mapPresenter.eventHandler(InputEvent.DELETE, point.x, point.y)) {
					mark = mapPresenter.getMarkModel();
					break;
				}
			}

			if (mark != null) {

				deleteOldEditors();

				int index = getMarkIndexWithinTheTable(mark);
				TableItem item = coordinatesTable.getItem(index);

				item.setText(1, ""); //$NON-NLS-1$
				item.setText(2, ""); //$NON-NLS-1$

				mark.setXCoord(Double.NaN);
				mark.setYCoord(Double.NaN);

				this.cmd.evalPrecondition();
				getMainComposite().refreshMapGraphicLayer();

			}
		} else {
			getMainComposite().getMapMarkGraphic().eventhandler(event, point);
		}
	}

	/**
	 * Delegate the move event to the {@link MapMarksGraphics}.
	 * 
	 * @param point
	 *            Map point where it was clicked.
	 * @param event
	 *            Input event.
	 */
	private void moveFromTable(java.awt.Point point, InputEvent event) {

		// the mapMarkGraphics will be the responsible to handle these
		// behaviours
		getMainComposite().getMapMarkGraphic().eventhandler(event, point);
	}

	/**
	 * Selects the row corresponding to the given mark.
	 * 
	 * @param mark
	 *            MarkModel
	 */
	private void selectRow(MarkModel mark) {

		deleteOldEditors();

		int index = getMarkIndexWithinTheTable(mark);
		assert index >= 0;
		coordinatesTable.setSelection(index);

		createEditorColumnX(mark, coordinatesTable.getSelection()[0]);
		createEditorColumnY(mark, coordinatesTable.getSelection()[0]);
	}

	/**
	 * If a row is selected and it owns the given mark model, it'll deselect it.
	 * 
	 * @param mark
	 *            The mark model.
	 */
	private void deselectRow(MarkModel mark) {

		int index = getMarkIndexWithinTheTable(mark);
		int selection = coordinatesTable.getSelectionIndex();
		if (index == selection) {
			coordinatesTable.deselect(selection);
			deleteOldEditors();
		}

	}

	/**
	 * Adds a {@link MouseSelectionListener}.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void addMouseSelectionListener(MouseSelectionListener listener) {

		listeners.add(listener);
	}

	/**
	 * Deletes a {@link MouseSelectionListener}.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void deleteMouseSelectionListener(MouseSelectionListener listener) {

		listeners.remove(listener);
	}

	/**
	 * Broadcast to all its listeners that the given mark has been selected in
	 * the coordinate table.
	 * 
	 * @param mark
	 *            The mark model
	 */
	private void broadcastSelectionInEvent(MarkModel mark) {

		for (MouseSelectionListener listener : listeners) {
			listener.inEvent(mark);
		}
	}

	private void deleteAllGCP() {

		Coordinate emptyCoord = new Coordinate(Double.NaN, Double.NaN);
		TableItem[] items = this.coordinatesTable.getItems();
		for (TableItem item : items) {
			MarkModel itemMark = (MarkModel) item.getData();
			itemMark.updateCoordinatePosition(emptyCoord);
			
		}
		getMainComposite().refreshMapGraphicLayer();
	}
}
