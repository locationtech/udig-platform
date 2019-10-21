/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.udig.ui.internal.Messages;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;

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
                editor.createTable(parent, null, featureType, true);
                return editor.getControl();
            }
            @Override
            public boolean close() {
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
    
    private void assertDialogVisableAndItemOrderCorrect() throws Exception {

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

    @Test
    public void testCellModifierModify() throws Exception {
        TreeViewer testingGetViewer = editor.testingGetViewer();

        ICellModifier cellModifier = testingGetViewer.getCellModifier();

        cellModifier.modify(testingGetViewer.getTree().getItem(0), "0", "the_new_name"); //$NON-NLS-1$//$NON-NLS-2$

        AttributeDescriptor attributeType = getAttributeDescriptor(0);
        assertEquals("the_new_name", attributeType.getLocalName()); //$NON-NLS-1$

        for( int i = 0; i < FeatureTypeEditor.testingGetTYPES().size(); i++ ) {
            cellModifier.modify(testingGetViewer.getTree().getItem(0), "1", i); //$NON-NLS-1$.
            attributeType = getAttributeDescriptor(0);
            assertEquals(FeatureTypeEditor.testingGetTYPES().get(i).getType(), attributeType
                    .getType().getBinding());
        }
    }

    private AttributeDescriptor getAttributeDescriptor(int index) {
        TreeViewer testingGetViewer = editor.testingGetViewer();
        SimpleFeatureType featureType = (SimpleFeatureType) testingGetViewer
                .getInput();
        AttributeDescriptor attributeType = featureType.getDescriptor(index);
        return attributeType;
    }

    @Test
    public void testGetCreateAttributeAction() throws Exception {
        IAction createAction = editor.getCreateAttributeAction();

        assertNotNull( createAction.getId() );

        createAction.runWithEvent(new Event());

        SimpleFeatureType featureType = (SimpleFeatureType) editor.testingGetViewer().getInput();
        assertAttributeAtIndex(String.class, Messages.FeatureTypeEditor_newAttributeTypeDefaultName + 0, featureType, 2);
        assertEquals(3, editor.testingGetViewer().getTree().getItemCount());

        createAction.runWithEvent(new Event());

        featureType = (SimpleFeatureType) editor.testingGetViewer().getInput();
        assertAttributeAtIndex(String.class, Messages.FeatureTypeEditor_newAttributeTypeDefaultName + 1, featureType, 3);
        assertEquals(4, editor.testingGetViewer().getTree().getItemCount());
    }

    @Test
    public void testSetFeatureType() throws Exception {
        editor.setFeatureType(null);

        SimpleFeatureType featureType = (SimpleFeatureType) editor.testingGetViewer().getInput();
        assertEquals(2, featureType.getAttributeCount());

        assertAttributeAtIndex(String.class, Messages.FeatureTypeEditor_defaultNameAttributeName,
                featureType, 0);
        assertAttributeAtIndex(LineString.class, Messages.FeatureTypeEditor_defaultGeometryName,
                featureType, 1);

        assertEquals(Messages.FeatureTypeEditor_newFeatureTypeName,
                editor.testingGetNameText().getText());
        
        editor.setFeatureType(this.featureType);
        assertEquals(this.featureType.getTypeName(), editor.testingGetNameText().getText());
        assertDialogVisableAndItemOrderCorrect();
    }

    @Test
    public void testGetDeleteAction() throws Exception {
        final IAction deleteAction = editor.getDeleteAction();

        assertNotNull( deleteAction.getId() );
        
        TreeViewer viewer = editor.testingGetViewer();
        viewer.setSelection(new StructuredSelection(featureType.getDescriptor(0)));
        deleteAction.runWithEvent(new Event());

        SimpleFeatureType featureType = (SimpleFeatureType) editor.testingGetViewer().getInput();

        assertEquals(1, featureType.getAttributeCount());
        assertAttributeAtIndex(String.class, "name", featureType, 0);

        IAction createAction = editor.getCreateAttributeAction();
        createAction.runWithEvent(new Event());
        createAction.runWithEvent(new Event());

        SimpleFeatureType featureType2 = (SimpleFeatureType) editor.testingGetViewer().getInput();

        assertEquals(3, featureType2.getAttributeCount());

        List<AttributeDescriptor> attrs = new ArrayList<AttributeDescriptor>(2);
        attrs.add(featureType2.getDescriptor(1));
        attrs.add(featureType2.getDescriptor(2));

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                countDownLatch.countDown();
                deleteAction.runWithEvent(new Event());

                SimpleFeatureType featureType3 = (SimpleFeatureType) editor.testingGetViewer()
                        .getInput();
                assertEquals(1, featureType3.getAttributeCount());
                assertAttributeAtIndex(String.class, "name", featureType3, 0);//$NON-NLS-1$
            }
        });
        viewer.setSelection(new StructuredSelection(attrs));
        assertTrue(countDownLatch.await(1, TimeUnit.SECONDS));
    }
    
    private void assertAttributeAtIndex(Class<?> expectedBindingClass, String expectedLocalName,
            SimpleFeatureType featureType, int describtorIndex) {
        assertEquals(expectedBindingClass, featureType.getDescriptor(describtorIndex).getType().getBinding());
        assertEquals(expectedLocalName, featureType.getDescriptor(describtorIndex).getLocalName()); //$NON-NLS-1$
    }

    @Test
    public void testCreateLabel() throws Exception {
        SimpleFeatureType featureType = (SimpleFeatureType) editor.testingGetViewer().getInput();
        
        Text text=editor.testingGetNameText();
        text.setText("newName"); //$NON-NLS-1$
        Event event = new Event();
        event.character=SWT.Selection;
        text.notifyListeners(SWT.KeyDown, event);

        featureType = (SimpleFeatureType) editor.testingGetViewer().getInput();
        assertEquals("newName", featureType.getTypeName()); //$NON-NLS-1$
        
        text.setText("newName"); //$NON-NLS-1$
        event = new Event();
        event.character=SWT.ESC;
        text.notifyListeners(SWT.KeyDown, event);
        
        featureType = (SimpleFeatureType) editor.testingGetViewer().getInput();
        assertEquals("newName", featureType.getTypeName()); //$NON-NLS-1$
        
        text.setSelection(0,0);
        event = new Event();
        text.notifyListeners(SWT.FocusIn, event);
        assertEquals(new Point(0,text.getText().length()), text.getSelection());
    }
    
    @Test
    public void testCloseOpenDialog() throws Exception {
        dialog.close();
        dialog.open();
        assertDialogVisableAndItemOrderCorrect();
    }
}
