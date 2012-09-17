package net.refractions.udig.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

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
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

public class FeatureTypeEditorTest {

    private SimpleFeature[] features;
    private SimpleFeatureType featureType;
    private FeatureTypeEditor editor;
    private Dialog dialog;
    private String featureTypeName = "FeatureTypeEditorFeatures"; //$NON-NLS-1$
    
    @Before
    public void setUp() throws Exception {
        features = UDIGTestUtil.createDefaultTestFeatures(featureTypeName, 1); 
        featureType = features[0].getFeatureType();
        dialog = new Dialog(Display.getCurrent().getActiveShell()){
            SimpleFeatureTypeBuilder builder=null;
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
                editor.createTable(parent, null,builder.buildFeatureType(),true);
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

    @After
    public void tearDown() throws Exception {
        if( dialog != null ){
            dialog.close();
        }
    }
    
    public void labels() throws Exception {

        assertTrue(dialog.getShell().isVisible());

        TreeViewer viewer = editor.testingGetViewer();

        Tree tree = viewer.getTree();
        TreeItem[] items = tree.getItems();

        assertEquals("geom", items[0].getText(0)); //$NON-NLS-1$
        assertEquals("Geometry", items[0].getText(1)); //$NON-NLS-1$
        assertEquals("name", items[1].getText(0)); //$NON-NLS-1$
        assertEquals("String", items[1].getText(1)); //$NON-NLS-1$
    }

    @Ignore
    @Test
    public void testCellModifierGetValue() throws Exception {
        TreeViewer testingGetViewer = editor.testingGetViewer();
        testingGetViewer.editElement(featureType.getDescriptor(0), 0);
        testingGetViewer.cancelEditing();
        testingGetViewer.editElement(featureType.getDescriptor(0), 1);
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
        assertEquals("geom", cellModifier.getValue(featureType.getDescriptor(0), "0")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(geomindex, cellModifier.getValue(featureType.getDescriptor(0), "1")); //$NON-NLS-1$ 
        assertEquals("name", cellModifier.getValue(featureType.getDescriptor(1), "0")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(stringindex, cellModifier.getValue(featureType.getDescriptor(1), "1")); //$NON-NLS-1$ 
    }

    @Ignore
    @Test
    public void testCellModifierModify() throws Exception {
        TreeViewer testingGetViewer = editor.testingGetViewer();

        ICellModifier cellModifier = testingGetViewer.getCellModifier();

        cellModifier.modify(testingGetViewer.getTree().getItem(0), "0", "the_new_name"); //$NON-NLS-1$//$NON-NLS-2$

        AttributeDescriptor attributeType = getAttributeDescriptor(0);
        assertEquals("the_new_name", attributeType.getName()); //$NON-NLS-1$

        for( int i = 0; i < FeatureTypeEditor.testingGetTYPES().size(); i++ ) {
            cellModifier.modify(testingGetViewer.getTree().getItem(0), "1", i); //$NON-NLS-1$.
            attributeType = getAttributeDescriptor(0);
            assertEquals(FeatureTypeEditor.testingGetTYPES().get(i).getType(), attributeType
                    .getType());
        }
    }

	private AttributeDescriptor getAttributeDescriptor(int index) {
        TreeViewer testingGetViewer = editor.testingGetViewer();
		SimpleFeatureTypeBuilder newFeatureTypeBuilder = (SimpleFeatureTypeBuilder) testingGetViewer.getInput();
        SimpleFeatureType type = newFeatureTypeBuilder.buildFeatureType();
        AttributeDescriptor attributeType = type.getDescriptor(index);
		return attributeType;
	}

    @Ignore
    @Test
    public void testGetCreateAttributeAction() throws Exception {
        IAction action = editor.getCreateAttributeAction();

        assertNotNull( action.getId() );
        action.runWithEvent(new Event());

        editor.testingGetViewer().getInput();
        assertEquals(
                Messages.FeatureTypeEditor_newAttributeTypeDefaultName + 0, getAttributeDescriptor(2).getName()); 
        assertEquals(String.class, getAttributeDescriptor(2).getType());

        assertEquals(3, editor.testingGetViewer().getTree().getItemCount());

        action.runWithEvent(new Event());

        assertEquals(4, editor.testingGetViewer().getTree().getItemCount());
        assertEquals(
                Messages.FeatureTypeEditor_newAttributeTypeDefaultName + 1, getAttributeDescriptor(3).getName()); 

    }

    @Ignore
    @Test
    public void testSetFeatureType() throws Exception {
        editor.setFeatureType(null);

        SimpleFeatureTypeBuilder builder = (SimpleFeatureTypeBuilder) editor.testingGetViewer().getInput();
        assertEquals(2, builder.buildFeatureType().getAttributeCount());
        assertEquals(Messages.FeatureTypeEditor_newFeatureTypeName, builder.getName()); 
        assertEquals(String.class, getAttributeDescriptor(0).getType().getBinding());
        assertEquals(Messages.FeatureTypeEditor_defaultNameAttributeName, getAttributeDescriptor(0).getName()); 
        assertEquals(LineString.class, getAttributeDescriptor(1).getType().getBinding());
        assertEquals(Messages.FeatureTypeEditor_defaultGeometryName, getAttributeDescriptor(1).getName()); 
        assertEquals(Messages.FeatureTypeEditor_newFeatureTypeName, editor.testingGetNameText().getText() ); 
        
        editor.setFeatureType(this.featureType);
        assertEquals(featureTypeName, editor.testingGetNameText().getText() ); 

        labels();
    }

    @Ignore
    @Test
    public void testGetDeleteAction() throws Exception {
        IAction action = editor.getDeleteAction();

        assertNotNull( action.getId() );
        
        TreeViewer viewer = editor.testingGetViewer();
        viewer.setSelection(new StructuredSelection(featureType.getDescriptor(0)));
        action.runWithEvent(new Event());

        SimpleFeatureTypeBuilder builder = (SimpleFeatureTypeBuilder) editor.testingGetViewer().getInput();
        assertEquals(1, builder.buildFeatureType().getAttributeCount());
        assertEquals(String.class, builder.buildFeatureType().getDescriptor(0).getType().getBinding());
        assertEquals("name", builder.buildFeatureType().getDescriptor(0).getName()); //$NON-NLS-1$

        IAction create = editor.getCreateAttributeAction();
        create.runWithEvent(new Event());
        create.runWithEvent(new Event());

        assertEquals(3, builder.buildFeatureType().getAttributeCount());

        List<AttributeDescriptor> attrs = new ArrayList<AttributeDescriptor>(2);
        attrs.add(builder.buildFeatureType().getDescriptor(1));
        attrs.add(builder.buildFeatureType().getDescriptor(2));
        viewer.setSelection(new StructuredSelection(attrs));

        action.runWithEvent(new Event());

        assertEquals(1, builder.buildFeatureType().getAttributeCount());
        assertEquals(String.class, builder.buildFeatureType().getDescriptor(0).getType().getBinding());
        assertEquals("name", builder.buildFeatureType().getDescriptor(0).getName()); //$NON-NLS-1$

    }
    
    @Ignore
    @Test
    public void testCreateLabel() throws Exception {
        SimpleFeatureTypeBuilder builder = (SimpleFeatureTypeBuilder) editor.testingGetViewer().getInput();
        
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
    
    @Ignore
    @Test
    public void testCloseOpenDialog() throws Exception {
        dialog.close();
        dialog.open();
        labels();
    }
}
