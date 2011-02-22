package net.refractions.udig.ui;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.refractions.udig.ui.internal.Messages;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

public class FeatureTypeEditorTest extends TestCase {

    private Feature[] features;
    private FeatureType featureType;
    private FeatureTypeEditor editor;
    private Dialog dialog;
    private String featureTypeName = "FeatureTypeEditorFeatures"; //$NON-NLS-1$

    @Override
    protected void setUp() throws Exception {
        features = UDIGTestUtil.createDefaultTestFeatures(featureTypeName, 1);
        featureType = features[0].getFeatureType();

        dialog = new Dialog(Display.getCurrent().getActiveShell()){
            FeatureTypeBuilder builder=null;
            @Override
            protected Point getInitialSize() {
                return new Point(500, 500);
            }

            @Override
            protected Layout getLayout() {
                return new GridLayout(1, true);
            }

            @Override
            protected Control createDialogArea( Composite parent ) {
                editor = new FeatureTypeEditor();
                editor.createFeatureTypeNameText(parent, null);
                editor.createTable(parent, null,builder,true);
                return editor.getControl();
            }
            @Override
            public boolean close() {
                builder=editor.getFeatureTypeBuilder();
                return super.close();
            }
        };
        try{
            editor.setFeatureType(featureType);
            fail("Should throw an exception if the feature type is set before table is created"); //$NON-NLS-1$
        }catch (Exception e) {
            //proper behaviour.
        }
        dialog.setBlockOnOpen(false);
        dialog.open();
        editor.setFeatureType(featureType);

        UDIGTestUtil.inDisplayThreadWait(1000, new WaitCondition(){

            public boolean isTrue() {
                return dialog.getShell().isVisible();
            }
        }, true);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        dialog.close();
    }

    public void testLabels() throws Exception {

        assertTrue(dialog.getShell().isVisible());

        TreeViewer viewer = editor.testingGetViewer();

        Tree tree = viewer.getTree();
        TreeItem[] items = tree.getItems();

        assertEquals("geom", items[0].getText(0)); //$NON-NLS-1$
        assertEquals("Geometry", items[0].getText(1)); //$NON-NLS-1$
        assertEquals("name", items[1].getText(0)); //$NON-NLS-1$
        assertEquals("String", items[1].getText(1)); //$NON-NLS-1$
    }

    public void testCellModifierGetValue() throws Exception {
        TreeViewer testingGetViewer = editor.testingGetViewer();
        testingGetViewer.editElement(featureType.getAttributeType(0), 0);
        testingGetViewer.cancelEditing();
        testingGetViewer.editElement(featureType.getAttributeType(0), 1);
        testingGetViewer.cancelEditing();

        List<LegalAttributeTypes> types = FeatureTypeEditor.testingGetTYPES();
        int geomindex = 0;
        int stringindex = 0;
        int i = 0;
        for( LegalAttributeTypes type : types ) {
            if (type.getType() == String.class) {
                stringindex = i;
            } else if (type.getType() == Geometry.class) {
                geomindex = i;
            }
            i++;
        }

        ICellModifier cellModifier = testingGetViewer.getCellModifier();
        assertEquals("geom", cellModifier.getValue(featureType.getAttributeType(0), "0")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(geomindex, cellModifier.getValue(featureType.getAttributeType(0), "1")); //$NON-NLS-1$
        assertEquals("name", cellModifier.getValue(featureType.getAttributeType(1), "0")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(stringindex, cellModifier.getValue(featureType.getAttributeType(1), "1")); //$NON-NLS-1$
    }

    public void testCellModifierModify() throws Exception {

        TreeViewer testingGetViewer = editor.testingGetViewer();

        ICellModifier cellModifier = testingGetViewer.getCellModifier();

        cellModifier.modify(testingGetViewer.getTree().getItem(0), "0", "the_new_name"); //$NON-NLS-1$//$NON-NLS-2$

        FeatureTypeBuilder newFeatureType = (FeatureTypeBuilder) testingGetViewer.getInput();
        AttributeType attributeType = newFeatureType.get(0);
        assertEquals("the_new_name", attributeType.getName()); //$NON-NLS-1$

        for( int i = 0; i < FeatureTypeEditor.testingGetTYPES().size(); i++ ) {
            cellModifier.modify(testingGetViewer.getTree().getItem(0), "1", i); //$NON-NLS-1$.
            newFeatureType = (FeatureTypeBuilder) testingGetViewer.getInput();
            attributeType = newFeatureType.get(0);
            assertEquals(FeatureTypeEditor.testingGetTYPES().get(i).getType(), attributeType
                    .getType());
        }
    }

    public void testGetCreateAttributeAction() throws Exception {
        IAction action = editor.getCreateAttributeAction();

        assertNotNull( action.getId() );
        action.runWithEvent(new Event());

        FeatureTypeBuilder builder = (FeatureTypeBuilder) editor.testingGetViewer().getInput();
        assertEquals(
                Messages.FeatureTypeEditor_newAttributeTypeDefaultName + 0, builder.get(2).getName());
        assertEquals(String.class, builder.get(2).getType());

        assertEquals(3, editor.testingGetViewer().getTree().getItemCount());

        action.runWithEvent(new Event());

        assertEquals(4, editor.testingGetViewer().getTree().getItemCount());
        assertEquals(
                Messages.FeatureTypeEditor_newAttributeTypeDefaultName + 1, builder.get(3).getName());

    }

    public void testSetFeatureType() throws Exception {
        editor.setFeatureType(null);

        FeatureTypeBuilder builder = (FeatureTypeBuilder) editor.testingGetViewer().getInput();
        assertEquals(2, builder.getAttributeCount());
        assertEquals(Messages.FeatureTypeEditor_newFeatureTypeName, builder.getName());
        assertEquals(String.class, builder.get(0).getType());
        assertEquals(Messages.FeatureTypeEditor_defaultNameAttributeName, builder.get(0).getName());
        assertEquals(LineString.class, builder.get(1).getType());
        assertEquals(Messages.FeatureTypeEditor_defaultGeometryName, builder.get(1).getName());
        assertEquals(Messages.FeatureTypeEditor_newFeatureTypeName, editor.testingGetNameText().getText() );

        editor.setFeatureType(this.featureType);
        assertEquals(featureTypeName, editor.testingGetNameText().getText() );

        testLabels();
    }

    public void testGetDeleteAction() throws Exception {
        IAction action = editor.getDeleteAction();

        assertNotNull( action.getId() );

        TreeViewer viewer = editor.testingGetViewer();
        viewer.setSelection(new StructuredSelection(featureType.getAttributeType(0)));
        action.runWithEvent(new Event());

        FeatureTypeBuilder builder = (FeatureTypeBuilder) editor.testingGetViewer().getInput();
        assertEquals(1, builder.getAttributeCount());
        assertEquals(String.class, builder.get(0).getType());
        assertEquals("name", builder.get(0).getName()); //$NON-NLS-1$

        IAction create = editor.getCreateAttributeAction();
        create.runWithEvent(new Event());
        create.runWithEvent(new Event());

        assertEquals(3, builder.getAttributeCount());

        List<AttributeType> attrs = new ArrayList<AttributeType>(2);
        attrs.add(builder.get(1));
        attrs.add(builder.get(2));
        viewer.setSelection(new StructuredSelection(attrs));

        action.runWithEvent(new Event());

        assertEquals(1, builder.getAttributeCount());
        assertEquals(String.class, builder.get(0).getType());
        assertEquals("name", builder.get(0).getName()); //$NON-NLS-1$

    }

    public void testCreateLabel() throws Exception {
        FeatureTypeBuilder builder = (FeatureTypeBuilder) editor.testingGetViewer().getInput();

        Text text=editor.testingGetNameText();
        text.setText("newName"); //$NON-NLS-1$
        Event event = new Event();
        event.character=SWT.Selection;
        text.notifyListeners(SWT.KeyDown, event);

        assertEquals("newName", builder.getName()); //$NON-NLS-1$

        text.setText("newName"); //$NON-NLS-1$
        event = new Event();
        event.character=SWT.ESC;
        text.notifyListeners(SWT.KeyDown, event);

        assertEquals("newName", builder.getName()); //$NON-NLS-1$

        text.setSelection(0,0);
        event = new Event();
        text.notifyListeners(SWT.FocusIn, event);
        assertEquals(new Point(0,text.getText().length()), text.getSelection());
    }

    public void testCloseOpenDialog() throws Exception {
        dialog.close();
        dialog.open();
        testLabels();
    }
}
