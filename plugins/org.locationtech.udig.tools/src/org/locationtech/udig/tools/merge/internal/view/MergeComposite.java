/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering S.L. (Axios)
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.merge.internal.view;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.geotools.factory.CommonFactoryFinder;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.tools.internal.i18n.Messages;
import org.locationtech.udig.tools.internal.ui.util.InfoMessage;
import org.locationtech.udig.tools.internal.ui.util.LayerUtil;
import org.locationtech.udig.tools.merge.MergeContext;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;

/**
 * Merge Controls for composite
 * <p>
 * Presents the source features in tree view and result feature in table. The user can customize the
 * merge.
 * </p>
 *
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @author Marco Foi (www.mcfoi.it)
 */
class MergeComposite extends Composite {

    private SashForm sashForm = null;

    private Composite compositeSourceFeatures = null;

    private CLabel cLabelSources = null;

    private Tree treeFeatures = null;

    private Composite compositeMergeFeature = null;

    private CLabel cLabelTarget = null;

    private Table tableMergeFeature = null;

    private Composite compositeMergeControls = null;

    private Label labelResult = null;

    private Label labelResultGeometry = null;

    private ViewForm viewForm = null;

    private Composite infoComposite = null;

    private String message = null;

    private CLabel messageTitle = null;

    private ImageRegistry registry = null;

    private Button trashButton = null;

    private MergeView mergeView = null;

    private Menu menu;

    /** data handle */
    private MergeFeatureBuilder mergeBuilder = null;

    /**
     * Union geometry
     */
    private static final String UNION = "Union"; //$NON-NLS-1$

    /**
     * Index of the column holding the attribute names in both views
     */
    private static final int NAME_COLUMN = 0;

    /**
     * Index of the column holding the attribute values in both views
     */
    private static final int VALUE_COLUMN = 1;

    /**
     * Label to use as attribute value in the merged view when an attribute is {@code null}
     */
    private static final String NULL_LABEL = "<null>"; //$NON-NLS-1$

    private CLabel messagePanel;

    public MergeComposite(Composite parent, int style) {

        super(parent, style);
        createContent();
    }

    /**
     * Creates the widget of this composite.
     */
    private void createContent() {

        this.setLayout(new FillLayout());

        viewForm = new ViewForm(this, SWT.NONE);
        viewForm.setLayout(new FillLayout());

        infoComposite = new Composite(viewForm, SWT.NONE);

        createCompositeInformation();
        viewForm.setTopLeft(infoComposite);

        sashForm = new SashForm(viewForm, SWT.V_SCROLL);
        sashForm.setOrientation(SWT.HORIZONTAL);

        sashForm.setLayout(new FillLayout());

        createCompositeSourceFeatures();
        createCompositeMergeFeature();
        createContextMenu();
        viewForm.setContent(sashForm);
    }

    /**
     * The composite that shows the information.
     */
    private void createCompositeInformation() {

        GridLayout gridLayout1 = new GridLayout();
        gridLayout1.numColumns = 2;

        infoComposite.setLayout(gridLayout1);

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalSpan = 2;
        gridData.verticalAlignment = GridData.FILL;

        messageTitle = new CLabel(infoComposite, SWT.BOLD);
        messageTitle.setLayoutData(gridData);

        GridData gridData2 = new GridData();
        gridData2.horizontalAlignment = GridData.FILL;
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.grabExcessVerticalSpace = true;
        gridData2.verticalAlignment = GridData.FILL;

        this.messagePanel = new CLabel(infoComposite, SWT.NONE);
        this.messagePanel.setLayoutData(gridData2);

    }

    /**
     * The composite that shows the resultant merge feature.
     */
    private void createCompositeMergeFeature() {

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL;

        GridLayout gridLayout1 = new GridLayout();
        gridLayout1.numColumns = 1;
        gridLayout1.makeColumnsEqualWidth = true;

        compositeMergeFeature = new Composite(sashForm, SWT.BORDER);
        compositeMergeFeature.setLayout(gridLayout1);

        cLabelTarget = new CLabel(compositeMergeFeature, SWT.NONE);
        cLabelTarget.setText(Messages.MergeFeaturesComposite_merge_feature);

        tableMergeFeature = new Table(compositeMergeFeature, SWT.MULTI);
        tableMergeFeature.setHeaderVisible(true);
        tableMergeFeature.setLayoutData(gridData);
        tableMergeFeature.setLinesVisible(true);

        createCompositeMergeControls();

        TableColumn tableColumnName = new TableColumn(tableMergeFeature, SWT.NONE);
        tableColumnName.setWidth(150);
        tableColumnName.setText(Messages.MergeFeaturesComposite_property);

        TableColumn tableColumnValue = new TableColumn(tableMergeFeature, SWT.NONE);
        tableColumnValue.setWidth(60);
        tableColumnValue.setText(Messages.MergeFeaturesComposite_value);
    }

    /**
     * The composite that shows the merge feature geometry.
     */
    private void createCompositeMergeControls() {

        GridData gridData3 = new GridData();
        gridData3.horizontalAlignment = GridData.FILL;
        gridData3.grabExcessHorizontalSpace = true;
        gridData3.verticalAlignment = GridData.CENTER;

        GridData gridData2 = new GridData();
        gridData2.horizontalAlignment = GridData.FILL;
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.verticalAlignment = GridData.CENTER;

        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = GridData.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.verticalAlignment = GridData.CENTER;

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.makeColumnsEqualWidth = true;

        compositeMergeControls = new Composite(compositeMergeFeature, SWT.NONE);
        compositeMergeControls.setLayout(gridLayout);
        compositeMergeControls.setLayoutData(gridData1);

        labelResult = new Label(compositeMergeControls, SWT.NONE);
        labelResult.setText(Messages.MergeFeaturesComposite_result_geometry);
        labelResult.setLayoutData(gridData2);

        labelResultGeometry = new Label(compositeMergeControls, SWT.NONE);
        labelResultGeometry.setText(Messages.MergeFeaturesComposite_result);
        labelResultGeometry.setLayoutData(gridData3);

    }

    /**
     * The composite that shows the tree with the source features.
     */
    private void createCompositeSourceFeatures() {

        GridData gridData4 = new GridData();
        gridData4.horizontalAlignment = GridData.FILL;
        gridData4.grabExcessHorizontalSpace = true;
        gridData4.grabExcessVerticalSpace = true;
        gridData4.horizontalSpan = 2;
        gridData4.verticalAlignment = GridData.FILL;

        GridData gridData3 = new GridData();
        gridData3.horizontalAlignment = GridData.FILL;
        gridData3.grabExcessHorizontalSpace = false;
        gridData3.grabExcessVerticalSpace = false;
        gridData3.verticalAlignment = GridData.FILL;

        GridData gridData2 = new GridData();
        gridData2.horizontalAlignment = GridData.END;
        gridData2.grabExcessHorizontalSpace = false;
        gridData2.grabExcessVerticalSpace = false;
        gridData2.verticalAlignment = GridData.END;

        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 2;
        gridLayout2.makeColumnsEqualWidth = true;

        compositeSourceFeatures = new Composite(sashForm, SWT.BORDER);
        compositeSourceFeatures.setLayout(gridLayout2);

        cLabelSources = new CLabel(compositeSourceFeatures, SWT.NONE);
        cLabelSources.setText(Messages.MergeFeaturesComposite_source);
        cLabelSources.setLayoutData(gridData3);

        createImageRegistry();

        trashButton = new Button(compositeSourceFeatures, SWT.NONE);
        trashButton.setLayoutData(gridData2);
        trashButton.setToolTipText(Messages.MergeView_remove_tool_tip);
        trashButton.setImage(getImage());
        trashButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {

                deleteSourceFeatures();
            }
        });

        treeFeatures = new Tree(compositeSourceFeatures, SWT.SINGLE | SWT.CHECK);
        treeFeatures.setHeaderVisible(true);
        treeFeatures.setLayoutData(gridData4);
        treeFeatures.setLinesVisible(true);

        treeFeatures.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event event) {

                if (event.detail == SWT.CHECK) {
                    handleTreeEvent(event);
                }
            }
        });

        treeFeatures.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                MergeContext mergeContextSingleton = MergeContext.getInstance();
                if (mergeContextSingleton.getMergeMode() == MergeContext.MERGEMODE_TOOL) {
                    // The handleTreeEventClick function selects in map the clicked treeFeature:
                    // such behaviour is not compatible with MERGEMODE_OPERATION due to filterChange
                    // listeners in place when in that mode, so the function call is allowed just
                    // when in TOOL mode
                    handleTreeEventClick(e);
                }
            }
        });

        treeFeatures.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {

                if (e.button == 3) {
                    showContextMenu(e);
                }

            }
        });

        TreeColumn treeColumnFeature = new TreeColumn(treeFeatures, SWT.NONE);
        treeColumnFeature.setWidth(150);
        treeColumnFeature.setText(Messages.MergeFeaturesComposite_feature);

        TreeColumn treeColumnValue = new TreeColumn(treeFeatures, SWT.NONE);
        treeColumnValue.setWidth(60);
        treeColumnValue.setText(Messages.MergeFeaturesComposite_value);
    }

    /**
     * Removes the set of features selected
     */
    private void deleteSourceFeatures() {

        TreeItem[] items = this.treeFeatures.getSelection();
        for (int i = 0; i < items.length; i++) {
            String id = items[i].getText();

            List<SimpleFeature> sourceFeatures = this.mergeBuilder.getSourceFeatures();
            for (SimpleFeature feature : sourceFeatures) {

                if (feature.getID().equals(id)) {

                    this.mergeBuilder.removeFromSourceFeatures(feature);

                    unselect(feature);

                    break;
                }
            }

            // deletes from three view
            items[i].dispose();
        }
        changed();

    }

    /**
     * Deselects the merged features
     *
     * @param unselectedFeature
     */
    private void unselect(SimpleFeature unselectedFeature) {

        IToolContext context = this.mergeView.getContext();
        UndoableMapCommand unselectCommand = context.getSelectionFactory().createNoSelectCommand();

        context.sendASyncCommand(unselectCommand);
    }

    /**
     * Creates the context menu that will be showed when user do a right click on the
     * treeSourceFeatures.
     */
    private void createContextMenu() {

        final MenuManager contextMenu = new MenuManager();

        contextMenu.setRemoveAllWhenShown(true);

        this.menu = contextMenu.createContextMenu(compositeSourceFeatures);
    }

    private Image getImage() {

        return registry.get("trash"); //$NON-NLS-1$
    }

    private void createImageRegistry() {

        registry = new ImageRegistry();

        String imgFile = "images/" + "trash" + ".gif"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        registry.put("trash", ImageDescriptor.createFromFile(MergeComposite.class, imgFile)); //$NON-NLS-1$
    }

    /**
     * Display in this composite the features of the indeed layer
     *
     * @param selectedFeatures
     * @param layer
     */
    public void display(List<SimpleFeature> selectedFeatures, ILayer layer) {

        addSourceFeatures(selectedFeatures);
        display();
    }

    /**
     * Populate its data.
     */
    public void display() {

        // assert mergeBuilder != null : "the merge builder was not been set"; //$NON-NLS-1$
        this.mergeBuilder = getMergeBuilder();

        // presents the source in tree view
        populateSourceFeaturesView();

        populateMergeFeatureView();

        changed();
    }

    /**
     * Call back function to report a change in the merged geometry attribute
     *
     * @param builder
     */
    private void mergeGeometryChanged(MergeFeatureBuilder builder) {

        String geomName = builder.getPrittyMergeGeometry();
        if (builder.isGeometriesUnion()) {
            labelResultGeometry.setText(UNION);
        } else {
            labelResultGeometry.setText(geomName.toString());
        }

        final String msg = MessageFormat.format(Messages.MergeFeaturesComposite_result_will_be,
                geomName);

        setMessage(msg, InfoMessage.Type.INFORMATION);
    }

    /**
     * Set the message showed on the information view.
     *
     * @param usrMessage
     * @param type
     */
    public void setMessage(final String usrMessage, final InfoMessage.Type type) {

        InfoMessage info = new InfoMessage(usrMessage, type);
        messagePanel.setImage(info.getImage());
        messagePanel.setText(info.getText());
        messageTitle.setText(Messages.MergeFeaturesComposite_merge_result_title);
    }

    /**
     * Call back function invoked every time a user interface event occurs over an item of the
     * source features view.
     * <p>
     * Applies the following logic:
     * <ul>
     * <li>If the TreeItem state change <code>event</code> was produced on a Feature item, selects
     * or deselects all the attributes of the corresponding feature
     * <li>If the TreeItem state change <code>event</code> was produced on an Attribute item, that
     * same item checked state will be respected, and all the attribute items at the same index for
     * the rest of the source features will become <b>unchecked</b>
     * <li>The internal {@link MergeFeatureBuilder} state will be updated accordingly, whether all
     * the attributes of a given feature has to be set for the target feature, or just the attribute
     * selected, depending on if the event were raised at a Feature item or an Attribute item
     * </ul>
     * </p>
     * <p>
     * No manipulation of the target feature view is done here. Instead, as this method calls
     * {@link #setAttributeValue(int, int, boolean)}, the {@link MergeFeatureBuilder} will raise
     * change events that will be catched up by {@link #changed()}
     * </p>
     *
     * @param event
     * @see #setSelectedFeature(int, boolean)
     * @see #selectAttributePropagate(int, int, boolean)
     * @see #setAttributeValue(int, int, boolean)
     */
    private void handleTreeEvent(Event event) {

        TreeItem item = (TreeItem) event.item;
        final boolean isFeatureItem = isFeatureItem(item);
        final boolean checked = item.getChecked();
        if (isFeatureItem) {
            int featureIndex = ((Integer) item.getData()).intValue();
            setSelectedFeature(featureIndex, checked);
        } else {
            TreeItem featureItem = item.getParentItem();
            int featureIndex = ((Integer) featureItem.getData()).intValue();
            int attributeIndex = ((Integer) item.getData()).intValue();
            selectAttributePropagate(featureIndex, attributeIndex, checked);
            setAttributeValue(featureIndex, attributeIndex, checked);
        }
    }

    /**
     * Called when a click is done on the sources tree. If the click was done over the name of a
     * feature, this feature is selected on the map.
     *
     * @param event
     */
    private void handleTreeEventClick(SelectionEvent event) {

        TreeItem item = (TreeItem) event.item;
        final boolean isFeatureItem = isFeatureItem(item);
        if (isFeatureItem) {
            Object obj = item.getData();

            if (obj instanceof Integer) {
                ILayer layer = mergeBuilder.getLayer();
                Filter filter = getSelectedFeatureFilter((Integer) obj);
                LayerUtil.presentSelection(layer, filter);
            }

        }
    }

    /**
     * Get the filter of the desired feature.
     *
     * @param index The index of this feature.
     * @return The geometry filter for this feature.
     */
    private Filter getSelectedFeatureFilter(Integer index) {

        FilterFactory ff = CommonFactoryFinder.getFilterFactory(null);
        String id = this.mergeBuilder.getFeature(index).getID();
        FeatureId fid = ff.featureId(id);
        Set<FeatureId> ids = new HashSet<>(1);
        ids.add(fid);
        Id filter = ff.id(ids);

        return filter;
    }

    /**
     * Get the selected item, if it is a feature item, show the context menu with the option of
     * remove that feature from the tree list.
     *
     * @param e
     */
    private void showContextMenu(MouseEvent e) {

        Tree tree = (Tree) e.getSource();
        TreeItem selection = tree.getSelection()[0];

        boolean isFather = isFeatureItem(selection);
        if (isFather) {

            menu.setVisible(true);
        }
    }

    /**
     * Called whenever a merged feature attribute value changed to update the merge feature view
     */
    private void changed() {

        updateMergePanel();

        updateCommandButtonStatus();
    }

    /**
     * Updates the merge's attributes using the values present in the {@link MergeFeatureBuilder}
     * object.
     */
    private void updateMergePanel() {

        List<SimpleFeature> sourceFeatures = this.mergeBuilder.getSourceFeatures();
        if (sourceFeatures.isEmpty()) {

            this.tableMergeFeature.removeAll();

        } else {

            final int attributeCount = this.mergeBuilder.getAttributeCount();

            for (int attIndex = 0; attIndex < attributeCount; attIndex++) {

                TableItem attrItem = this.tableMergeFeature.getItem(attIndex);

                String strValue;
                if (attIndex == this.mergeBuilder.getDefaultGeometryIndex()) {

                    strValue = this.mergeBuilder.getPrittyMergeGeometry();

                } else {
                    Object mergeFeatureProperty = this.mergeBuilder.getMergeAttribute(attIndex);

                    strValue = (mergeFeatureProperty == null) ? NULL_LABEL
                            : String.valueOf(mergeFeatureProperty);
                }
                attrItem.setText(VALUE_COLUMN, strValue);
            }
        }
    }

    private void updateCommandButtonStatus() {

        canMerge();
        canDelete();
    }

    private void canDelete() {

        this.trashButton.setEnabled(!this.mergeBuilder.getSourceFeatures().isEmpty());
    }

    /**
     * This is the single point where the {@link MergeFeatureBuilder} state is modified. This method
     * is called whenever a UI event in the source features view implies to change the value of an
     * attribute for the merge feature.
     * <p>
     * As a result of calling {@link MergeFeatureBuilder#copyAttributeToMerge(int, int)} or
     * {@link MergeFeatureBuilder#clearMergeAttribute(int)}, the change event will be caught up by
     * {@link #changed()} to reflect the change in the merge feature view
     * </p>
     *
     * @param sourceFeatureIndex the index of the source feature where the UI event event occurred
     * @param attributeIndex the index of the attribute of the source feature where the event
     *        occurred
     * @param setValue whether to set or clear the target feature attribute value at index
     *        <code>attributeIndex</code>
     * @see MergeFeatureBuilder#copyAttributeToMerge(int, int)
     * @see MergeFeatureBuilder#clearMergeAttribute(int)
     */
    private void setAttributeValue(int sourceFeatureIndex, int attributeIndex, boolean setValue) {

        if (setValue) {
            mergeBuilder.copyAttributeToMerge(sourceFeatureIndex, attributeIndex);
        } else {
            mergeBuilder.clearMergeAttribute(attributeIndex);
        }
    }

    private void setSelectedFeature(final int featureIndex, final boolean checked) {

        final int numFeatures = mergeBuilder.getFeatureCount();
        final int numAttributes = mergeBuilder.getAttributeCount();
        final TreeItem[] featureItems = treeFeatures.getItems();

        assert numFeatures == featureItems.length;

        for (int currFeatureIdx = 0; currFeatureIdx < numFeatures; currFeatureIdx++) {
            TreeItem featureItem = featureItems[currFeatureIdx];
            final boolean checkIt = checked && currFeatureIdx == featureIndex;
            featureItem.setChecked(checkIt);

            for (int attIdx = 0; attIdx < numAttributes; attIdx++) {
                selectAttribute(currFeatureIdx, attIdx, checkIt);
                if (currFeatureIdx == featureIndex) {
                    setAttributeValue(featureIndex, attIdx, checkIt);
                }
            }
        }
    }

    /**
     * Simply selects or deselects an item in the source features view, does not make any state
     * change in the {@link MergeFeatureBuilder}
     *
     * @param featureIndex the index of the feature item the desired attribute item belongs to
     * @param attributeIndex the index of the attribute to change the checked state
     * @param checked whether to check or uncheck the pointed attribute item
     */
    private void selectAttribute(final int featureIndex, final int attributeIndex,
            final boolean checked) {

        TreeItem featureItem = treeFeatures.getItem(featureIndex);
        TreeItem attItem = featureItem.getItem(attributeIndex);
        attItem.setChecked(checked);
    }

    /**
     * Selects a source feature attribute item in the source features view, propagating the contrary
     * effect to the TreeItems for the attributes of the other features at the same attribute index.
     * In other words, if selecting an attribute of one feature, deselects the same attribute of the
     * other features.
     *
     * @param featureIndex
     * @param attributeIndex
     * @param checked
     * @see #selectAttribute(int, int, boolean)
     */
    private void selectAttributePropagate(final int featureIndex, final int attributeIndex,
            final boolean checked) {

        final int numFeatures = mergeBuilder.getFeatureCount();

        for (int currFIndex = 0; currFIndex < numFeatures; currFIndex++) {
            boolean checkIt = checked && currFIndex == featureIndex;
            selectAttribute(currFIndex, attributeIndex, checkIt);
        }
    }

    /**
     * Checks if <code>item</code> corresponds to the root item of a source feature (a.k.a, it has
     * children)
     *
     * @param item
     * @return <code>true</code> if item is the root item of a Feature, <code>false</code> otherwise
     */
    private boolean isFeatureItem(final TreeItem item) {

        Object itemData = item.getData();
        boolean isFeatureItem = item.getItemCount() > 0;
        isFeatureItem = isFeatureItem && itemData instanceof Integer;
        return isFeatureItem;
    }

    /**
     * Populates the source feature panel.
     * <p>
     * Feature item's {@link TreeItem#getData() data} are <code>Integer</code> values with the
     * corresponding feature index in the {@link MergeFeatureBuilder}. Feature's attribute data are
     * the attribute value as per {@link MergeFeatureBuilder#getAttribute(int, int)}
     * </p>
     */
    private void populateSourceFeaturesView() {

        this.treeFeatures.removeAll();

        final int featureCount = mergeBuilder.getFeatureCount();
        // add feature as parent
        for (int featureIndex = 0; featureIndex < featureCount; featureIndex++) {
            TreeItem featureItem = new TreeItem(this.treeFeatures, SWT.NONE);
            // store the
            featureItem.setData(Integer.valueOf(featureIndex));
            featureItem.setText(mergeBuilder.getID(featureIndex));

            final int geometryIndex = mergeBuilder.getDefaultGeometryIndex();
            boolean isFisrtFeature = featureIndex == 0;
            // adds feature's attribute as child items
            for (int attIndex = 0; attIndex < mergeBuilder.getAttributeCount(); attIndex++) {

                TreeItem attrItem = new TreeItem(featureItem, SWT.NONE);

                // sets Name
                String attrName = mergeBuilder.getAttributeName(attIndex);
                attrItem.setText(0, attrName);
                attrItem.setData(Integer.valueOf(attIndex));

                // sets value
                Object attrValue = mergeBuilder.getAttribute(featureIndex, attIndex);
                String strValue = attrValue == null ? NULL_LABEL : String.valueOf(attrValue);
                attrItem.setText(VALUE_COLUMN, strValue);

                // check geometry only if it is not union and it is the first
                // feature
                if (isFisrtFeature && attIndex == geometryIndex) {
                    attrItem.setChecked(!mergeBuilder.isGeometriesUnion());
                } else {
                    attrItem.setChecked(isFisrtFeature);
                }
            }
            featureItem.setExpanded(isFisrtFeature);
        }
    }

    /**
     * Adds the feature as last element in the tree view that shows the source feature list.
     *
     * @param feature
     */
    private void displaySourceFeature(SimpleFeature feature) {

        setMessage("", InfoMessage.Type.NULL); //$NON-NLS-1$

        MergeFeatureBuilder builder = getMergeBuilder();

        if (!builder.canMerge(feature)) {
            this.message = Messages.MergeFeatureBehaviour_must_intersect;
            setMessage(this.message, InfoMessage.Type.WARNING);
            return;
        }
        int position = this.mergeBuilder.addSourceFeature(feature);
        if (position == -1) {
            // it was inserted previously y the source feature list.
            return;
        }

        TreeItem featureItem = new TreeItem(this.treeFeatures, SWT.NONE);
        // store the feature id
        featureItem.setData(position);
        featureItem.setText(builder.getID(position));

        // adds feature's attribute as child items
        for (int attIndex = 0; attIndex < builder.getAttributeCount(); attIndex++) {

            TreeItem attrItem = new TreeItem(featureItem, SWT.NONE);

            // sets Name
            String attrName = builder.getAttributeName(attIndex);
            attrItem.setText(0, attrName);
            attrItem.setData(Integer.valueOf(attIndex));

            // sets value
            Object attrValue = builder.getAttribute(position, attIndex);
            String strValue = attrValue == null ? NULL_LABEL : String.valueOf(attrValue);
            attrItem.setText(VALUE_COLUMN, strValue);

        }
        featureItem.setExpanded(true);
    }

    /**
     * Adds the target feature and its attributes. The merge feature view {@link TableItem}s value
     * property will hold integers representing the index of each attribute in the target feature's
     * schema
     */
    private void populateMergeFeatureView() {

        this.tableMergeFeature.removeAll();

        final int attributeCount = mergeBuilder.getAttributeCount();
        for (int attIndex = 0; attIndex < attributeCount; attIndex++) {

            TableItem attrItem = new TableItem(this.tableMergeFeature, SWT.NONE);
            attrItem.setData(attIndex);
            String attrName = mergeBuilder.getAttributeName(attIndex);
            attrItem.setText(NAME_COLUMN, attrName);
        }
    }

    public void setView(MergeView mergeView) {

        this.mergeView = mergeView;

    }

    /**
     * Adds the features to the existent source feature set.
     *
     * @param featureList
     */
    public void addSourceFeatures(List<SimpleFeature> featureList) {

        // If in operation mode, clean tree-view before adding new features
        if (MergeContext.getInstance().getMergeMode() == MergeContext.MERGEMODE_OPERATION) {
            this.mergeBuilder = getMergeBuilder();
            mergeBuilder.removeFromSourceFeaturesAll();
        }

        for (SimpleFeature feature : featureList) {
            displaySourceFeature(feature);
        }

    }

    public void addSourceFeature(SimpleFeature newFeature) {
        displaySourceFeature(newFeature);
    }

    /**
     * Checks the conditions to execute the merge operation.
     *
     * @return true if the features could be merge
     */
    private boolean canMerge() {

        boolean valid = true;

        // Must select two or more feature
        if (this.mergeBuilder.getSourceFeatures().size() < 2) {
            this.message = Messages.MergeFeatureBehaviour_select_two_or_more;
            setMessage(this.message, InfoMessage.Type.WARNING);
            valid = false;
        }
        this.mergeView.canMerge(valid);

        return valid;
    }

    /**
     * The builder used to merge the features. This is a factory method if the builder instance is
     * null a new one will be created
     *
     * @return {@link MergeFeatureBuilder}
     */
    public MergeFeatureBuilder getMergeBuilder() {
        if (this.mergeBuilder != null)
            return this.mergeBuilder;

        // create a new merge builder
        if (this.mergeView.isOperationMode()) {
            this.mergeBuilder = new MergeFeatureBuilder(
                    this.mergeView.getCurrentEventTriggeringLayer());
        } else {
            this.mergeBuilder = new MergeFeatureBuilder(
                    this.mergeView.getContext().getSelectedLayer());
        }

        this.mergeBuilder.addChangeListener(new MergeFeatureBuilder.ChangeListener() {

            @Override
            public void attributeChanged(MergeFeatureBuilder builder, int attributeIndex,
                    Object oldValue) {

                if (attributeIndex == builder.getDefaultGeometryIndex()) {
                    mergeGeometryChanged(builder);
                }
                changed();
            }
        });
        return this.mergeBuilder;
    }
}
