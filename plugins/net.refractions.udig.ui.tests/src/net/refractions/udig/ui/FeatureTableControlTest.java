package net.refractions.udig.ui;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.refractions.udig.core.StaticProvider;
import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.PageBook;
import org.geotools.data.DataUtilities;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureType;
import org.geotools.filter.FidFilter;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;

public class FeatureTableControlTest extends TestCase {

    private Shell shell;
    private FeatureTableControl table;
    private Feature feature1;
    private Feature feature2;
    private Feature feature3;
    private Feature feature4;
    private FeatureCollection features;

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        Display display = Display.getCurrent();
        shell = new Shell(display.getActiveShell());
        shell.setLayout(new FillLayout());

        FeatureType ft = DataUtilities.createType("type", "name:String,id:int"); //$NON-NLS-1$//$NON-NLS-2$
        feature1 = ft.create(new Object[]{"feature1", 1}, "feature1"); //$NON-NLS-1$ //$NON-NLS-2$
        feature2 = ft.create(new Object[]{"feature2", 2}, "feature2"); //$NON-NLS-1$ //$NON-NLS-2$
        feature3 = ft.create(new Object[]{"feature3", 3}, "feature3"); //$NON-NLS-1$ //$NON-NLS-2$
        feature4 = ft.create(new Object[]{"feature4", 4}, "feature4"); //$NON-NLS-1$ //$NON-NLS-2$

        features = FeatureCollections.newCollection();

        features.add(feature1);
        features.add(feature2);
        features.add(feature3);
        features.add(feature4);

        UiPlugin.getDefault().getPreferenceStore().setValue(FeatureTableControl.CACHING_WARNING,
                true);

        table = new FeatureTableControl(new StaticProvider<IProgressMonitor>(
                new NullProgressMonitor()), shell, features);
        shell.open();
        shell.redraw();

        while( Display.getCurrent().readAndDispatch() );


    }

    @Override
    protected void tearDown() throws Exception {
        shell.dispose();
    }

    public void testDeleteSelection() throws Exception{
        IStructuredSelection selection=new StructuredSelection(new Feature[]{feature1, feature3});
        table.setSelection(selection);

        UDIGTestUtil.inDisplayThreadWait(1000, new WaitCondition(){

            public boolean isTrue() {
                return table.getSelectionCount()==2;
            }

        }, false);

        table.deleteSelection();

        UDIGTestUtil.inDisplayThreadWait(1000, new WaitCondition(){

            public boolean isTrue() {
                return table.getViewer().getTable().getItemCount()==2;
            }

        }, false);

        assertEquals( feature2, table.getViewer().getTable().getItem(0).getData());
        assertEquals( feature4, table.getViewer().getTable().getItem(1).getData());
        assertEquals( 0, table.getSelectionCount() );
        assertTrue( table.getSelection().isEmpty());
    }

    @SuppressWarnings("unchecked")
    public void testUpdateFeatureCollection() throws Exception {
        FeatureType ft = DataUtilities.createType("type", "name:String,id:int"); //$NON-NLS-1$//$NON-NLS-2$
        Feature f1 = ft.create(new Object[]{"feature1", 10}, "feature1"); //$NON-NLS-1$ //$NON-NLS-2$
        Feature f2 = ft.create(new Object[]{"feature5", 5}, "feature5"); //$NON-NLS-1$ //$NON-NLS-2$

        FeatureCollection newFeatures = FeatureCollections.newCollection();

        newFeatures.add(f1);
        newFeatures.add(f2);

        table.update(newFeatures);

        UDIGTestUtil.inDisplayThreadWait(100000000, new WaitCondition(){

            public boolean isTrue() {
                Feature feature=(Feature) table.getViewer().getTable().getItem(0).getData();
                return table.getViewer().getTable().getItemCount()==5
                    && ((Integer) feature.getAttribute("id")).intValue()==10; //$NON-NLS-1$
            }

        }, false);

        Feature feature=(Feature) table.getViewer().getTable().getItem(0).getData();

        assertEquals( 5, table.getViewer().getTable().getItemCount() );
        assertEquals( 10, ((Integer) feature.getAttribute("id")).intValue() ); //$NON-NLS-1$
        table.assertInternallyConsistent();
    }

    public void testUpdate() throws Exception {

        TableViewer viewer = (TableViewer) table.getViewer();

        TableItem topItem = viewer.getTable().getItem(0);

        Feature f = (Feature) topItem.getData();
        f.setAttribute(0, "newName"); //$NON-NLS-1$

        while( Display.getCurrent().readAndDispatch() );
        assertEquals("feature1", topItem.getText(1)); //$NON-NLS-1$
        table.update();
        while( Display.getCurrent().readAndDispatch() );
        assertEquals("newName", table.getViewer().getTable().getItem(0).getText(1)); //$NON-NLS-1$
    }

    public void testGetFeatures() {
        assertEquals(features, table.getFeatures());
    }

    @SuppressWarnings("unchecked")
    public void testSetFeatures() {
        FeatureCollection newFeatures = FeatureCollections.newCollection();
        newFeatures.add(feature1);
        table.setFeatures(newFeatures);
        while( Display.getCurrent().readAndDispatch() );

        assertEquals(1, table.getViewer().getTable().getItemCount());
        TableItem item = table.getViewer().getTable().getItem(0);
        assertEquals(feature1, item.getData());

        newFeatures.add(feature2);
        while( Display.getCurrent().readAndDispatch() );
        assertEquals(2, table.getViewer().getTable().getItemCount());
        item = table.getViewer().getTable().getItem(0);
        assertEquals(feature1, item.getData());
        item = table.getViewer().getTable().getItem(1);
        assertEquals(feature2, item.getData());

        newFeatures.remove(feature1);
        while( Display.getCurrent().readAndDispatch() );
        assertEquals(1, table.getViewer().getTable().getItemCount());
        item = table.getViewer().getTable().getItem(0);
        assertEquals(feature2, item.getData());

    }

    public void testClear() {
        table.clear();
        while( Display.getCurrent().readAndDispatch() );
        Table t = table.getViewer().getTable();
        for( int i = 0; i < 4; i++ ) {
            assertEquals("", t.getItem(i).getText()); //$NON-NLS-1$
        }
    }

    public void testMessage() {
        String string = "Test Message"; //$NON-NLS-1$
        table.message(string);
        while( Display.getCurrent().readAndDispatch() );

        PageBook book = (PageBook) table.getControl();
        assertTrue(book.getChildren()[0] instanceof Text);
        assertTrue(book.getChildren()[0].isVisible());
        assertTrue(book.getChildren()[1] instanceof Table);
        assertFalse(book.getChildren()[1].isVisible());

        Text text = (Text) book.getChildren()[0];
        assertEquals(string, text.getText());

        table.message(null);
        while( Display.getCurrent().readAndDispatch() );
        assertFalse(book.getChildren()[0].isVisible());
        assertTrue(book.getChildren()[1].isVisible());

    }

    @SuppressWarnings("unchecked")
    public void testGetSelection() throws Exception {

        FeatureType featureType = feature1.getFeatureType();
        List<Feature> newFeatures = new ArrayList<Feature>();
        for( int i = 5; i < 50; i++ ) {
            Feature f = featureType.create(new Object[]{"feature" + i, i}, "feature" + i); //$NON-NLS-1$ //$NON-NLS-2$
            newFeatures.add(f);
        }

        features.addAll(newFeatures);
        while( Display.getCurrent().readAndDispatch() );

        assertSelected(35, false);
        assertSelected(0, false);
        assertSelected(48, false);

        Table table2 = table.getViewer().getTable();
        table2.setTopIndex(0);

        // now test without reveal
        selectFeature(40, false);
        assertEquals(0, table2.getTopIndex());

        // test with reveal
        selectFeature(30, true);

        assertEquals(30, table2.getTopIndex());
    }

    /**
     * @param index index of feature (in collection) to select)
     * @param blockForViewing for debugging. just blocks so that the table can be
     *        visually/interactively inspected.
     * @throws Exception
     */
    private void assertSelected( final int index, boolean blockForViewing ) throws Exception {
        final String fid = "feature" + (index + 1); //$NON-NLS-1$
        final Table tree = table.getViewer().getTable();
        selectFeature(index, true);

        if (blockForViewing) {
            doWait();
        }

        TableItem item = tree.getItem(index);

        Color selectedBackColor = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_SELECTION);
        Color selectedForeColor = Display.getCurrent()
                .getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT);

        assertEquals(fid, ((Feature) item.getData()).getID());

        TableItem[] items = tree.getItems();
        for( TableItem item2 : items ) {
            if (item2 == item) {
                assertEquals(selectedBackColor, item.getBackground());
                assertEquals(selectedForeColor, item.getForeground());
            } else {
                assertNotSame(selectedBackColor, item.getBackground());
            }
        }
        Rectangle bounds = item.getBounds();
        assertTrue(tree.getBounds().intersects(bounds));
    }

    private void doWait() throws Exception {
        UDIGTestUtil.inDisplayThreadWait(555555, WaitCondition.FALSE_CONDITION, false);
    }

    private void selectFeature( final int index, boolean reveal ) throws Exception {

        final Table tree = table.getViewer().getTable();

        FilterFactory fac = FilterFactoryFinder.createFilterFactory();
        final String fid = "feature" + (index + 1); //$NON-NLS-1$

        FidFilter selection = fac.createFidFilter(fid);
        table.setSelection(new StructuredSelection(selection), reveal);
        UDIGTestUtil.inDisplayThreadWait(3000, new WaitCondition(){

            public boolean isTrue() {
                TableItem item = tree.getItem(index);
                if (item.getData() == null)
                    return false;
                return fid.equals(((Feature) item.getData()).getID());
            }

        }, false);
    }

    public void testSort() {
        TableColumn column = table.getViewer().getTable().getColumn(0);
        table.sort(new FIDComparator(SWT.DOWN), SWT.UP, column);
        while( Display.getCurrent().readAndDispatch() );
        assertSort(false);

        table.sort(new FIDComparator(SWT.DOWN), SWT.DOWN, column);
        while( Display.getCurrent().readAndDispatch() );
        assertSort(true);

        table.sort(new FIDComparator(SWT.DOWN), SWT.UP, column);
        while( Display.getCurrent().readAndDispatch() );
        assertSort(false);

        table.sort(new FIDComparator(SWT.UP), SWT.UP, column);
        while( Display.getCurrent().readAndDispatch() );
        assertSort(true);
    }

    private void assertSort( boolean increment ) {
        Table tree = table.getViewer().getTable();
        if( increment ){
            assertEquals(feature1, tree.getItem(0).getData());
            assertEquals(feature2, tree.getItem(1).getData());
            assertEquals(feature3, tree.getItem(2).getData());
            assertEquals(feature4, tree.getItem(3).getData());
        }else{
            assertEquals(feature4, tree.getItem(0).getData());
            assertEquals(feature3, tree.getItem(1).getData());
            assertEquals(feature2, tree.getItem(2).getData());
            assertEquals(feature1, tree.getItem(3).getData());
        }
    }

    public void testPromoteSelection() throws Exception {
        selectFeature(3, true);
        table.promoteSelection();
        while( Display.getCurrent().readAndDispatch() );
        final Table tree = table.getViewer().getTable();

        Color selectedBackColor = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_SELECTION);

        assertEquals(feature4, tree.getItem(0).getData());
        assertEquals(tree.getItem(0).getBackground(), selectedBackColor);

        selectFeature(1, false);
        while( Display.getCurrent().readAndDispatch() );

        assertEquals(feature2, tree.getItem(2).getData());
        assertEquals(feature4, tree.getItem(0).getData());
        assertEquals(tree.getItem(2).getBackground(), selectedBackColor);
    }

    public void testUserSelection() throws Exception {

        // test normal click
        doEventBasedSelection(1, 0, 1);

        Object firstElement = ((IStructuredSelection) table.getSelection()).getFirstElement();
        String[] fids = ((FidFilter) firstElement).getFids();
        assertEquals(feature2.getID(), fids[0]);
        assertSelectedBackgroundColor(1);

        doEventBasedSelection(2, 0, 1);

        firstElement = ((IStructuredSelection) table.getSelection()).getFirstElement();
        fids = ((FidFilter) firstElement).getFids();
        assertEquals(1, fids.length);
        assertEquals(feature3.getID(), fids[0]);
        assertSelectedBackgroundColor(2);

        doEventBasedSelection(2, 0, 2);

        firstElement = ((IStructuredSelection) table.getSelection()).getFirstElement();
        assertEquals(1, fids.length);
        fids = ((FidFilter) firstElement).getFids();
        assertEquals(feature3.getID(), fids[0]);
        assertSelectedBackgroundColor(2);

        // test MOD2 click
        doEventBasedSelection(0, SWT.MOD2, 1);

        firstElement = ((IStructuredSelection) table.getSelection()).getFirstElement();
        fids = ((FidFilter) firstElement).getFids();
        assertEquals(3, fids.length);
        assertTrue(contains(feature3.getID(),fids));
        assertTrue(contains(feature1.getID(),fids));
        assertTrue(contains(feature2.getID(),fids));
        assertSelectedBackgroundColor(0,1,2);

        doEventBasedSelection(1, SWT.MOD2, 1);

        firstElement = ((IStructuredSelection) table.getSelection()).getFirstElement();
        fids = ((FidFilter) firstElement).getFids();
        assertEquals(2, fids.length);
        assertTrue(contains(feature3.getID(),fids));
        assertTrue(contains(feature2.getID(),fids));
        assertSelectedBackgroundColor(1,2);

        // test MOD1 click
        doEventBasedSelection(1, SWT.MOD1, 1);

        firstElement = ((IStructuredSelection) table.getSelection()).getFirstElement();
        fids = ((FidFilter) firstElement).getFids();
        assertEquals(1, fids.length);
        assertTrue(contains(feature3.getID(),fids));
        assertSelectedBackgroundColor(2);

        doEventBasedSelection(1, SWT.MOD1, 1);

        firstElement = ((IStructuredSelection) table.getSelection()).getFirstElement();
        fids = ((FidFilter) firstElement).getFids();
        assertEquals(2, fids.length);
        assertTrue(contains(feature3.getID(),fids));
        assertTrue(contains(feature2.getID(),fids));
        assertSelectedBackgroundColor(1,2);

    }

    private void doEventBasedSelection( int item, int stateMask, int button ) {

        Table t = table.getViewer().getTable();
        t.setSelection(item);
        while( Display.getCurrent().readAndDispatch() );
        Rectangle bounds = t.getItem(item).getBounds();
        Event event = new Event();
        event.button = button;
        event.stateMask=stateMask;
        event.x = bounds.x + 1;
        event.y = bounds.y + 1;
        t.notifyListeners(SWT.MouseUp, event);
        while( Display.getCurrent().readAndDispatch() );
    }

    // make paramter integers
    private void assertSelectedBackgroundColor( Object... selectedItems ) {
        Color selectedBackColor = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_SELECTION);
        Color backColor = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
        Table t = table.getViewer().getTable();
        for( int i = 0; i < 4; i++ ) {
            if (contains(i, selectedItems))
                assertEquals("" + i, selectedBackColor, t.getItem(i).getBackground()); //$NON-NLS-1$
            else
                assertEquals("" + i, backColor, t.getItem(i).getBackground()); //$NON-NLS-1$
        }
    }

    private boolean contains( Object i, Object[] selectedItems ) {
        for( Object j : selectedItems ) {
            if (j.equals(i))
                return true;
        }
        return false;
    }



}
