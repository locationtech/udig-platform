package net.refractions.udig.project.ui.controls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ui.internal.ProjectUIPlugin;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.PageBook;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureResults;
import org.geotools.feature.AttributeType;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.feature.SchemaException;

/**
 * A TreeViewer control for viewing a table of Feature attributes.
 * <p>
 * This object can be created in two ways. The first is using a FeatureReader. This method reads in
 * Features a page at a time and populates the table entries. The page size can be set by calling
 * FeatureTableControl#setPageSize(int).
 * </p>
 * <p>
 * The second way is using a DefaultFeatureCollection. In this case the control hangs on to a
 * reference to the DefaultFeatureCollection and populates the table entries directory from it. This
 * method results in a single page containing all features.
 * </p>
 * TODO: Create next and first action
 *
 * @author jdeolive
 * @since 0.3
 */
public class FeatureTableControl {

    /** results per page * */
    private int pageSize = 10; // XXX: actual put this as a user pref

    /** feature types and reader to get the actual feature data * */
    private FeatureReader fReader;
    FeatureType fType;

    /** feature collection for holding features in memory * */
    private List<Feature> features;

    /** table viewer control * */
    private TableViewer tableViewer;
    private Table table;

    private FeatureResults results;

    private PageBook control;

    /**
     * Construct <code>FeatureTableControl</code>.
     * <p>
     * Must call setFeatures before use.
     * </p>
     */
    public FeatureTableControl() {
        clear();
    }
    /**
     * Construct <code>FeatureTableControl</code>.
     *
     * @param features The DefaultFeatureCollection containing the features to be displayed.
     */
    public FeatureTableControl( DefaultFeatureCollection features ) {
        setFeatures(features);
    }

    /**
     * Construct a <code>FeatureTableControl</code>.
     *
     * @param fReader The FeatureReader that returns the actual features.
     */
    public FeatureTableControl( FeatureReader fReader ) {
        this(fReader, 10);
    }

    /**
     * Construct a <code>FeatureTableControl</code>.
     *
     * @param fReader The FeatureReader that returns the actual features.
     * @param resPerPage Results per page to be shown in the table.
     */
    public FeatureTableControl( FeatureReader fReader, int resPerPage ) {
        this.fType = fReader.getFeatureType();
        this.fReader = fReader;
        this.pageSize = resPerPage;

        features = new ArrayList<Feature>();

        // /features = (DefaultFeatureCollection) DefaultFeatureCollections.newCollection();

        // initial page
        next();
    }

    /**
     * Sets the number of features viewed in the table per page.
     *
     * @param resPerPage positive integer.
     */
    public void setPageSize( int resPerPage ) {
        this.pageSize = resPerPage;
    }

    /**
     * Returns the number of features viewed in the table per page.
     *
     * @return positive integer.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Returns the control representing the table control.
     *
     * @return The internal table viewer control.
     * @see Control
     */
    public Control getControl() {
        return control;
    }

    /**
     * Creates the table control.
     *
     * @param parent The to be parent of the control.
     * @see Composite
     */
    public void createTableControl( Composite parent ) {
        control = new PageBook(parent, SWT.NONE);
        createTableViewer(control);
    }

    /**
     * Moves the control to the next page of features. This method should only be called if the
     * control was created from a <code>org.geotools.data.FeatureReader</code>.
     */
    public void next() {
        // if the control was created directory from a feature collection
        // fReader will be null
        // TODO: Throw an exception here instead ??
        if (fReader != null) {
            nextInternal();
        }
    }

    /**
     * Moves the control to the next page of features.
     */
    protected void nextInternal() {

        // clear the current features
        features.clear();

        try {
            // read in the new features
            for( int i = 0; i < pageSize && fReader.hasNext(); i++ ) {
                features.add(fReader.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Updates the table control with the current set of features.
     * <p>
     * This method will ensure that the column information gets updated
     * </p>
     */
    public void update() {
        // System.out.println("Page size:" + pageSize);
        updateTable();
        tableViewer.setInput(features);
    }

    /**
     * Updates the table by adding/removing columns. TableViewer doesn't support updating columns
     * based on the data set supplied so this is a way around it that works nicely.
     */
    protected void updateTable() {
        table = tableViewer.getTable();
        TableColumn tableColumn;

        // aa: For some reason this column is hard coded in here??!!
        // if it's removed, all the loops should be rechecked to remove the +/- 1
        if (table.getColumnCount() > 0) {
            tableColumn = table.getColumn(0);
        } else {
            tableColumn = new TableColumn(table, SWT.CENTER | SWT.BORDER);
        }
        tableColumn.setText("FID"); //$NON-NLS-1$
        tableColumn.setWidth(100);

        if (fType != null) {
            for( int i = 0; i < fType.getAttributeCount(); i++ ) {
                AttributeType aType = fType.getAttributeType(i);
                if (table.getColumnCount() > i + 1) {
                    tableColumn = table.getColumn(i + 1);
                } else {
                    tableColumn = new TableColumn(table, SWT.CENTER | SWT.BORDER);
                }

                tableColumn.setText(aType.getName());
                tableColumn.setWidth(100);
            }
            for( int i = fType.getAttributeCount() + 1; i < table.getColumnCount(); i++ ) {
                tableColumn = table.getColumn(i);
                tableColumn.dispose();
            }
        }
    }

    /**
     * Creates the table control itself.
     *
     * @param parent Makes A TableViewer which diplays the features.
     * @see Composite
     */
    protected void createTableViewer( Composite parent ) {
        int style = SWT.MULTI; // | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL;
        table = new Table(parent, style);
        // TableColumn tableColumn = new TableColumn(table, SWT.CENTER | SWT.BORDER);
        // tableColumn.setText("FID"); //$NON-NLS-1$
        // tableColumn.setWidth(100);
        //
        // for( int i = 0; i < fType.getAttributeCount(); i++ ) {
        // AttributeType aType = fType.getAttributeType(i);
        // tableColumn = new TableColumn(table, SWT.CENTER | SWT.BORDER);
        // if (aType.isGeometry()) {
        // // jg: wot is this maddness? jd: paul said so
        // tableColumn.setText("GEOMETRY"); //$NON-NLS-1$
        // } else {
        // tableColumn.setText(aType.getName());
        // }
        // tableColumn.setWidth(100);
        // }
        table.setHeaderVisible(true);

        FeatureTableProvider ftp = new FeatureTableProvider();
        tableViewer = new TableViewer(table);
        tableViewer.setContentProvider(ftp);
        tableViewer.setLabelProvider(ftp);
        tableViewer.setInput(features);
        control.showPage(tableViewer.getControl());
        updateTable();

        // tableViewer.refresh();
        // tableViewer.getControl().redraw();
        // tableViewer.getControl().update();
    }

    /**
     * The TableViewer used is returned... would be used for adding listeners and various other
     * things to the tableViewer
     *
     * @return TableViewer
     */
    public TableViewer getTableViewer() {
        return tableViewer;
    }

    /**
     * API use?
     *
     * @author jdeolive
     * @see LabelProvider
     * @see ITableLabelProvider
     * @see IStructuredContentProvider
     */
    class FeatureTableProvider extends LabelProvider
            implements
                ITableLabelProvider,
                IStructuredContentProvider {

        /**
         * Returns the elemetns for the viewer. Input to this method should be at type of
         * <code>org.geotools.feature.DefaultFeatureCollection</code>
         *
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         * @param inputElement A <code>org.geotools.feature.DefaultFeatureCollection.</code>
         * @return An array of <code>org.geotools.feature.Feature</code>
         * @throws ClassCastException when inputElement not DefaultFeatureCollection
         */
        public Object[] getElements( Object inputElement ) throws ClassCastException {
            @SuppressWarnings("unchecked")
            List<Feature> features = (List<Feature>) inputElement;
            return features.toArray();
        }

        /**
         * Does nothing.
         *
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
         *      java.lang.Object, java.lang.Object)
         * @param viewer
         * @param oldInput
         * @param newInput
         */
        public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
            // do nothing.
            ((TableViewer) viewer).getTable().layout();
        }

        /**
         * Does nothing.
         *
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         * @param element
         * @param columnIndex
         * @return null
         */
        public Image getColumnImage( Object element, int columnIndex ) {
            return null;
        }

        /**
         * Returns the value of the column / feature attribute.
         *
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         * @param element the array of feature attributes.
         * @param columnIndex the column index / feature attribute.
         * @return the string representation of the feature attribute, except for attributes of type
         *         Geometry in which a string representing the type of Geometry is returned.
         * @throws ClassCastException when element not Feature
         */
        public String getColumnText( Object element, int columnIndex ) throws ClassCastException {
            Feature f = (Feature) element;
            if (f == null) {
                ProjectUIPlugin.trace(getClass(), "Feature was null in FeatureTableControl", (Exception)null); //$NON-NLS-1$
                return null;
            }
            if (columnIndex == 0)
                return f.getID();

            if (columnIndex >= fType.getAttributeCount()) {
                return null;
            }
            AttributeType at = fType.getAttributeType(columnIndex - 1);
            if (at == null)
                return null;
            if ( at instanceof GeometryAttributeType ) {
                String s = f.getAttribute(columnIndex - 1).getClass().getName();
                return s.substring(s.lastIndexOf('.') + 1);
            }
            if (f.getAttribute(columnIndex - 1) == null)
                return null;
            return f.getAttribute(columnIndex - 1).toString();

        }
    }

    /**
     * Does nothing.
     *
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    public void setFocus() {
        // do nothing.
    }
    /**
     * Contents of the current page of features
     *
     * @return DefaultFeatureCollection
     * @see DefaultFeatureCollection
     */
    public List<Feature> getFeatures() {
        return features;
    }
    /**
     * Set up for a one time only paged access
     *
     * @param reader
     * @see Featurereader API should this throw exceptions? the answer also potentially propagates
     *      the issue and the question ... API is there a memory issue here?
     */
    public void setFeatures( FeatureReader reader ) {
        this.fReader = reader;
        this.fType = reader.getFeatureType();
        features = new ArrayList<Feature>();
        next(); // load the first page into the collection
        update();
    }
    /**
     * Set up for a single page of content
     *
     * @param features
     * @see DefaultFeatureCollection
     */
    public void setFeatures( List<Feature> features ) {
        this.features = features;
        if (features.size() != 0) {
            this.fType = features.get(0).getFeatureType();
        } else {
            this.fType = null;
        }
        // this.pageSize = features.size();
        this.fReader = null;
        update();
    }
    /**
     * Set up for pages access with a next and a reset button. API should this throw exceptions? the
     * answer also potentially propagates the issue and the question ... API is there a memory issue
     * here?
     *
     * @param results
     * @see FeatureResults
     */
    public void setFeatures( FeatureResults results ) {
        this.results = results;
        reset();
        update();
    }
    /** Reset to start of FeatureResutls */
    public void reset() {
        int count = pageSize + 1;
        try {
            count = results.getCount();
            // System.out.println("results count " + count );
        } catch (IOException e) {
            // System.out.println("results count " + e );
            e.printStackTrace();
        }

        if (count < this.pageSize) {
            ArrayList<Feature> list = new ArrayList<Feature>(count);
            try {
                list.addAll(results.collection());
                setFeatures(list);
            } catch (IOException e) {
                e.printStackTrace();
                clear();
            }
        } else {
            try {
                setFeatures(results.reader());
            } catch (IOException e1) {
                e1.printStackTrace();
                clear();
            }
        }
    }
    /**
     * Don't display nothing :-)
     */
    public void clear() {
        features = new ArrayList<Feature>();
        try {
            fType = DataUtilities.createType("empty", "message:String"); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (SchemaException e) {
            // TODO Catch e
        }
        fReader = null;
        this.results = null;
    }
}
