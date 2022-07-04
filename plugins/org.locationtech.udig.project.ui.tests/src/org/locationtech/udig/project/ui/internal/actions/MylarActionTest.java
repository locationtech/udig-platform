/**
 * uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2022, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.project.ui.internal.actions;

import static org.mockito.Mockito.when;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MylarActionTest {

    Map map;

    @Mock
    IAction action;

    @Mock
    ISelection selection;

    @Mock
    IBlackboard blackboard;

    @Captor
    ArgumentCaptor<Boolean> booleanCaptor;

    MylarAction mylarAction = new MylarAction() {
        @Override
        protected Map getCurrentMap() {
            return map;
        };
    };

    @Test
    public void noMapCheckNeverCalled() {
        map = ApplicationGIS.NO_MAP;
        mylarAction.selectionChanged(action, selection);
        Mockito.verifyNoMoreInteractions(action, selection);
    }

    @Test
    public void noBlackboardCheckNeverCalled() {
        map = Mockito.mock(Map.class);
        when(map.getBlackboard()).thenReturn(null);
        mylarAction.selectionChanged(action, selection);
        Mockito.verifyNoMoreInteractions(action, selection);
    }

    @Test
    public void blackboardKeyNotPresentCheckisFalse() {
        map = Mockito.mock(Map.class);
        when(map.getBlackboard()).thenReturn(blackboard);
        when(blackboard.get(MylarAction.KEY)).thenReturn(null);

        mylarAction.selectionChanged(action, selection);

        Mockito.verify(action).setChecked(booleanCaptor.capture());
        Mockito.verifyNoMoreInteractions(action, selection);
        Assert.assertFalse(booleanCaptor.getValue());
    }

    @Test
    public void blackboardKeyTrueCheckisTrue() {
        map = Mockito.mock(Map.class);
        when(map.getBlackboard()).thenReturn(blackboard);
        when(blackboard.get(MylarAction.KEY)).thenReturn(Boolean.TRUE);

        mylarAction.selectionChanged(action, selection);

        Mockito.verify(action).setChecked(booleanCaptor.capture());
        Mockito.verifyNoMoreInteractions(action, selection);
        Assert.assertTrue(booleanCaptor.getValue());
    }

    @Test
    public void blackboardKeyFalseCheckisFalse() {
        map = Mockito.mock(Map.class);
        when(map.getBlackboard()).thenReturn(blackboard);
        when(blackboard.get(MylarAction.KEY)).thenReturn(Boolean.FALSE);

        mylarAction.selectionChanged(action, selection);

        Mockito.verify(action).setChecked(booleanCaptor.capture());
        Mockito.verifyNoMoreInteractions(action, selection);
        Assert.assertFalse(booleanCaptor.getValue());
    }
}
