/**
 * Image Georeferencing
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
package org.locationtech.udig.image.georeferencing.internal.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.image.georeferencing.internal.i18n.Messages;
import org.locationtech.udig.image.georeferencing.internal.preferences.Preferences;
import org.locationtech.udig.image.georeferencing.internal.process.MarkModel;
import org.locationtech.udig.image.georeferencing.internal.ui.coordinatepanel.CoordinateTableComposite;
import org.locationtech.udig.image.georeferencing.internal.ui.coordinatepanel.MapMarksGraphics;
import org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.ImageComposite;
import org.locationtech.udig.image.georeferencing.internal.ui.message.InfoMessage;
import org.locationtech.udig.image.georeferencing.internal.ui.message.InfoMessage.Type;
import org.locationtech.udig.mapgraphic.internal.MapGraphicService;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.MapCompositionEvent;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.commands.DeleteLayerCommand;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Main composite of the georeferencing view.
 *
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.3
 */
public class MainComposite extends Composite implements Observer, GeoReferencingComposite {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(MainComposite.class.getName());

    private Composite outputComposite = null;

    private Composite informationComposite = null;

    private Composite contentComposite = null;

    private SashForm sashForm = null;

    private ImageComposite imageComposite = null;

    private CoordinateTableComposite coordinateTableComposite = null;

    private CLabel messageImage = null;

    private CLabel messageText = null;

    private IToolContext udigContext = null;

    private ViewForm viewForm = null;

    private CLabel outputLabel = null;

    private Text filePathText = null;

    private Button browseButton = null;

    private GeoReferencingCommand cmd = null;

    private IToolContext toolContext = null;

    private MapMarksGraphics mapMarkGraphic = null;

    private ILayer mapGraphicLayer = null;

    private Thread uiThread = null;

    /**
     * This composite will be the responsible to create all the composites.
     *
     * @param cmd The GeoReferencingCommand.
     * @param parent The parent composite which in this case is the view.
     * @param style Style
     */
    public MainComposite(GeoReferencingCommand cmd, Composite parent, int style) {
        super(parent, style);

        assert cmd != null;
        this.cmd = cmd;
        this.cmd.addObserver(this);

        createContent();
        this.pack();
    }

    private void createContent() {

        this.uiThread = Thread.currentThread();

        viewForm = new ViewForm(this, SWT.NONE);
        viewForm.setLayout(new FillLayout());

        Composite infoComposite = createCompositeLegend(viewForm);
        viewForm.setTopLeft(infoComposite);

        contentComposite = new Composite(viewForm, SWT.NONE);
        contentComposite.setLayout(new GridLayout());

        createContentComposite(contentComposite);

        viewForm.setContent(contentComposite);

    }

    private void createContentComposite(Composite parent) {

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL;

        GridData gridData2 = new GridData();
        gridData2.horizontalAlignment = GridData.FILL;
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.grabExcessVerticalSpace = false;
        gridData2.verticalAlignment = GridData.FILL;

        sashForm = new SashForm(parent, SWT.V_SCROLL);
        sashForm.setOrientation(SWT.HORIZONTAL);
        sashForm.setLayout(new GridLayout());
        sashForm.setLayoutData(gridData);

        createImageComposite(sashForm);
        createCoordinateTableComposite(sashForm);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;

        outputComposite = new Composite(parent, SWT.BORDER);
        outputComposite.setLayout(gridLayout);
        outputComposite.setLayoutData(gridData2);

        createOutputBrowser(outputComposite);
    }

    private void createOutputBrowser(Composite parent) {

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.grabExcessHorizontalSpace = false;
        gridData.grabExcessVerticalSpace = false;
        gridData.verticalAlignment = GridData.FILL;

        GridData gridData2 = new GridData();
        gridData2.horizontalAlignment = GridData.FILL;
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.grabExcessVerticalSpace = false;
        gridData2.verticalAlignment = GridData.END;

        outputLabel = new CLabel(parent, SWT.NONE);
        outputLabel.setText(Messages.MainComposite_outputText);
        outputLabel.setToolTipText(Messages.MainComposite_outputToolTip);
        outputLabel.setLayoutData(gridData);

        filePathText = new Text(parent, SWT.BORDER);
        filePathText.setLayoutData(gridData2);
        filePathText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent me) {

                cmd.setOutputFileName(filePathText.getText());
                cmd.evalPrecondition();
            }
        });

        browseButton = new Button(parent, SWT.NONE);
        browseButton.setText(Messages.MainComposite_browseText);
        browseButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {

                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

                FileDialog fd = new FileDialog(shell, SWT.SAVE);
                fd.setFilterExtensions(new String[] { "*.tif" }); //$NON-NLS-1$
                fd.setText("Save file"); //$NON-NLS-1$
                fd.setFilterPath(Preferences.getOutputFilePath());
                // fd.setFilterPath(cmd.getOutputFilePath());

                String filename = fd.open();
                if (filename != null) {
                    filePathText.setText(filename);
                    cmd.setOutputFileName(filename);
                    cmd.evalPrecondition();
                    // store the path to load in further attempts
                    File file = new File(filename);
                    Preferences.setOutputFilePath(file.getParent());
                    // cmd.setOutputFilePath(file.getParent());
                }
            }
        });

    }

    private Composite createCompositeLegend(final Composite parent) {

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = false;
        gridData.minimumHeight = 300;
        gridData.minimumWidth = 500;
        gridData.verticalAlignment = SWT.CENTER;

        Composite compositeSelection = new Composite(parent, SWT.BORDER);
        compositeSelection.setLayoutData(gridData);
        compositeSelection.setLayout(layout);

        createCompositeInformation(compositeSelection);

        return compositeSelection;

    }

    /**
     * This method initializes compositeInformation
     *
     */
    private void createCompositeInformation(final Composite parent) {

        GridData gridData6 = new GridData();
        gridData6.horizontalAlignment = GridData.FILL;
        gridData6.grabExcessHorizontalSpace = true;
        gridData6.grabExcessVerticalSpace = true;
        gridData6.verticalAlignment = GridData.FILL;

        informationComposite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(3, false);
        informationComposite.setLayoutData(gridData6);
        informationComposite.setLayout(gridLayout);

        this.messageImage = new CLabel(informationComposite, SWT.NONE);
        GridData gridData7 = new GridData();
        gridData7.horizontalAlignment = GridData.BEGINNING;
        gridData7.minimumWidth = 30;
        gridData7.widthHint = 30;
        this.messageImage.setLayoutData(gridData7);

        this.messageText = new CLabel(informationComposite, SWT.NONE);
        GridData gridData8 = new GridData();
        gridData8.horizontalAlignment = GridData.FILL;
        gridData8.grabExcessHorizontalSpace = true;
        gridData8.grabExcessVerticalSpace = true;
        gridData8.verticalAlignment = GridData.FILL;
        this.messageText.setLayoutData(gridData8);
        this.messageText.setFont(JFaceResources.getDialogFont());

        InfoMessage message = this.cmd.getMessage();
        displayMessage(message);
    }

    private void createImageComposite(Composite parent) {

        this.imageComposite = new ImageComposite(this.cmd, parent, SWT.BORDER | SWT.NO_FOCUS);
        this.cmd.addObserver(this.imageComposite);
    }

    private void createCoordinateTableComposite(Composite parent) {

        ScrolledComposite scrollComposite = new ScrolledComposite(parent,
                SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL);
        scrollComposite.setLayout(new FillLayout());

        this.coordinateTableComposite = new CoordinateTableComposite(this.cmd, scrollComposite,
                SWT.NONE | SWT.NO_FOCUS);
        this.coordinateTableComposite.setLayout(new FillLayout());
        this.cmd.addObserver(coordinateTableComposite);

        // adds the parameters container to scroll composite
        scrollComposite.setContent(coordinateTableComposite);
        scrollComposite.setExpandHorizontal(true);
        scrollComposite.setExpandVertical(true);
        scrollComposite.setMinHeight(300);
    }

    /**
     * Shows the message in the standard information area
     */
    public void displayMessage(final InfoMessage message) {

        assert message != null;

        // The following sentences does a filter of those obvious messages
        InfoMessage filteredMessage = message;
        // shows the message
        this.informationComposite.setVisible(true);
        this.messageImage.setImage(filteredMessage.getImage());
        this.messageText.setToolTipText(filteredMessage.getText());
        this.messageText.setText(filteredMessage.getText());

    }

    /**
     * Executes the operation associated to selected control.
     *
     */
    public void executeOperation() {

        ViewportPane pane = this.udigContext.getViewportPane();
        Display display = getDisplay();

        try {
            setEnabled(false);

            // sets the wait cursor and disables this panel

            pane.setCursor(display.getSystemCursor(SWT.CURSOR_WAIT));

            this.cmd.execute();

        } catch (IOException e) {
            displayMessage(new InfoMessage(e.getMessage(), InfoMessage.Type.FAIL));
        } finally {
            pane.setCursor(null);

            setEnabled(true);
        }

    }

    @Override
    public void update(Observable o, Object arg) {

        InfoMessage message = this.cmd.getMessage();

        // listen to the map change notification so it can broadcast the set
        // enable=false to all his children's.

        if (!(arg instanceof GeoreferencingCommandEventChange))
            return;
        GeoreferencingCommandEventChange cmdEvent = (GeoreferencingCommandEventChange) arg;

        switch (cmdEvent.getEvent()) {
        case MAP_CHANGE:

            message.setType(Type.WARNING);
            setEnabled(false);
            break;
        case MAP_CHANGE_TO_ORIGINAL:

            message.setType(Type.INFORMATION);
            setEnabled(true);
            break;
        default:

            break;
        }

        displayMessage(message);
    }

    @Override
    public void setContext(IToolContext context) {

        IMap map;
        if (context != null) {

            map = context.getMap();
            CoordinateReferenceSystem crs = getCurrentMapCrs(map);

            // create the mapGraphics
            try {
                createTheMapGraphic(map);
            } catch (IOException e) {
                InfoMessage info = new InfoMessage(Messages.MainComposite_mapGraphicFailText,
                        Type.FAIL);
                setMessage(info);
                e.printStackTrace();
            }

            // set the values on the command.
            this.cmd.setCRS(crs);
            this.cmd.setMap(map);
            this.cmd.evalPrecondition();
        }

        if (this.toolContext == null) {
            this.toolContext = context;
            // add the listeners the first time
            this.mapMarkGraphic.associateListeners(this.coordinateTableComposite,
                    this.imageComposite);
        }

        ((GeoReferencingComposite) imageComposite).setContext(context);
        ((GeoReferencingComposite) coordinateTableComposite).setContext(context);
    }

    /**
     * @param map
     * @return the current map's CRS or null if current map is null
     */
    private CoordinateReferenceSystem getCurrentMapCrs(IMap map) {

        return map.getViewportModel().getCRS();
    }

    public void setMessage(InfoMessage info) {

        displayMessage(info);
    }

    /**
     * Delete the {@link MapMarksGraphics} and the listeners associated with it.
     */
    public void close() {

        imageComposite.close(this);
        coordinateTableComposite.close(this);
        // remove the mapGraphic
        try {
            if (mapMarkGraphic != null) {
                mapMarkGraphic.deleteAssociatedListeners(this.coordinateTableComposite,
                        this.imageComposite);
                getMapMarkGraphic().clear();
                IMap map = cmd.getMap();
                removeOldMapGraphicFromLayer(map);
            }
        } catch (IOException e) {
            // we are closing the view, there is no need to inform the user
            // about this exception.
            e.printStackTrace();
        } finally {
            mapMarkGraphic = null;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {

        imageComposite.setEnabled(enabled);
        coordinateTableComposite.setEnabled(enabled);
        informationComposite.setEnabled(enabled);

        super.setEnabled(enabled);
    }

    /**
     * Given the loaded marks, create the marks and create the associated presenters for each one of
     * them.
     *
     * After creating all of them, refresh it so the established data can be seen.
     *
     * @param marks
     */
    public void createMarks(Map<String, MarkModel> marks) {

        imageComposite.createMarks(marks);
        refreshMapGraphicLayer();
        setCursor(null);
    }

    /**
     * Communicates with the imageComposite and tell him to delete all the marks from the image.
     */
    public void deleteAllPoints() {
        setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_WAIT));
        imageComposite.deleteAllPoints();
    }

    /**
     * If the toolContext isn't null and the mapMarkGraphic wasn't created yet, create it now.
     *
     * @param map2
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void createTheMapGraphic(IMap map) throws IOException {

        if (getMapMarkGraphic() != null) {
            return;
        }

        removeOldMapGraphicFromLayer(map);

        List<IResolve> mapgraphics = CatalogPlugin.getDefault().getLocalCatalog()
                .find(MapGraphicService.SERVICE_URL, null);
        List<IResolve> members = new ArrayList<>();
        members = mapgraphics.get(0).members(null);
        assert members != null;

        IGeoResource resolved = null;
        for (IResolve resolve : members) {

            if (resolve.canResolve(MapMarksGraphics.class)) {
                resolved = resolve.resolve(IGeoResource.class, null);
                break;
            }
        }
        assert resolved != null;
        ApplicationGIS.addLayersToMap(map, Collections.singletonList(resolved), -1);

        // Get the newly added mapgraphic layer, add the current images to
        // it,
        // and store it
        List<ILayer> mapLayers = map.getMapLayers();
        Iterator<ILayer> iterator = mapLayers.iterator();
        while (iterator.hasNext()) {
            ILayer layer = iterator.next();
            IGeoResource geoResource = layer.findGeoResource(MapMarksGraphics.class);
            if (geoResource != null) {
                MapMarksGraphics mapGraphic = geoResource.resolve(MapMarksGraphics.class, null);
                this.mapMarkGraphic = mapGraphic;
                this.mapGraphicLayer = layer;
                break;
            }
        }
        assert getMapMarkGraphic() != null;
    }

    /**
     * Removes the old mapGraphic layer from the layer list.
     *
     * @param map
     * @throws IOException
     */
    private void removeOldMapGraphicFromLayer(IMap map) throws IOException {

        // remove the mapgraphic layer from the map
        List<ILayer> mapLayers = map.getMapLayers();
        Iterator<ILayer> iterator = mapLayers.iterator();
        while (iterator.hasNext()) {
            ILayer layer = iterator.next();
            IGeoResource geoResource = layer.findGeoResource(MapMarksGraphics.class);
            if (geoResource != null) {
                map.sendCommandASync(new DeleteLayerCommand((Layer) layer));
                break;
            }
        }
    }

    public MapMarksGraphics getMapMarkGraphic() {

        return this.mapMarkGraphic;
    }

    public ILayer getMapGraphicLayer() {

        return this.mapGraphicLayer;
    }

    public void refreshMapGraphicLayer() {

        mapGraphicLayer.refresh(null);
    }

    /**
     * This method is called if the collection of layer is updated (added or removed).
     *
     * // TODO work in progress.
     *
     * @param event
     */
    private void updatedMapLayersActions(final MapCompositionEvent event) {

        MapCompositionEvent.EventType eventType = event.getType();

        switch (eventType) {

        case ADDED:
        case MANY_ADDED:
            Display.findDisplay(uiThread).asyncExec(new Runnable() {

                @Override
                public void run() {

                }
            });
            break;
        default:
            break;
        }
    }

    /**
     * Adds a {@link MouseSelectionListener} to the {@link ImageComposite}.
     *
     * @param listener The listener.
     */
    public void addMouseSelectionListenerToImgComposite(MouseSelectionListener listener) {

        this.imageComposite.addMouseSelectionListener(listener);
    }

    /**
     * Deletes a {@link MouseSelectionListener} on the {@link ImageComposite}.
     *
     * @param listener The listener.
     */
    public void deleteMouseSelectionListenerToImgComposite(MouseSelectionListener listener) {

        this.imageComposite.deleteMouseSelectionListener(listener);
    }

    /**
     * Adds a {@link MouseSelectionListener} to the {@link CoordinateTableComposite}.
     *
     * @param listener The listener.
     */
    public void addMouseSelectionListenerToCoordinate(MouseSelectionListener listener) {

        this.coordinateTableComposite.addMouseSelectionListener(listener);
    }

    /**
     * Deletes a {@link MouseSelectionListener} to the {@link CoordinateTableComposite}.
     *
     * @param listener The listener.
     */
    public void deleteMouseSelectionListenerToCoordinate(MouseSelectionListener listener) {

        this.coordinateTableComposite.deleteMouseSelectionListener(listener);
    }

}
