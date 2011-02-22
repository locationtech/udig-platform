package net.refractions.udig.tools.edit;

import junit.framework.TestCase;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.commands.NullCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.support.TestHandler;

public class EditToolHandlerTest extends TestCase {
    LockingBehaviour object = new LockingBehaviour(){

        public Object getKey(EditToolHandler handler) {
            return this;
        }

        public boolean isValid( EditToolHandler handler, MapMouseEvent e,
                EventType eventType ) {
            return false;
        }

        public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
                EventType eventType ) {
            return null;
        }

        public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        }

    };

    /*
     * Test method for 'net.refractions.udig.tools.edit.latest.EditToolHandler.setActive(boolean)'
     */
    public void testSetActive() throws Exception {
        class TestActivator implements Activator {
            boolean activated = false;
            public void activate( EditToolHandler handler ) {
                activated = true;
            }

            public void deactivate( EditToolHandler handler ) {
                activated = false;
            }

            public void handleActivateError( EditToolHandler handler, Throwable error ) {
            }

            public void handleDeactivateError( EditToolHandler handler, Throwable error ) {
            }

        };

        TestActivator activator = new TestActivator();
        TestHandler testHandler = new TestHandler();
        testHandler.getActivators().add(activator);
        assertFalse(activator.activated);
        testHandler.setActive(true);
        assertTrue(activator.activated);
        testHandler.setActive(false);
        assertFalse(activator.activated);

    }

    /*
     * Test method for
     * 'net.refractions.udig.tools.edit.latest.EditToolHandler.handleEvent(MapMouseEvent,
     * EventType)'
     */
    public void testHandleEvent() throws Exception {
        class TrueMode implements EventBehaviour {

            boolean run = false;

            public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
                return true;
            }

            public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
                    EventType eventType ) {
                run = true;
                return new NullCommand();
            }

            public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
            }

        }
        class FalseMode implements EventBehaviour {

            boolean run = false;

            public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
                return false;
            }

            public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
                    EventType eventType ) {
                run = true;
                return new NullCommand();
            }

            public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
            }
        }

        TrueMode trueMode = new TrueMode();
        FalseMode falseMode = new FalseMode();

        TestHandler handler = new TestHandler();
        assertFalse(trueMode.run);
        assertFalse(falseMode.run);
        handler.getBehaviours().add(trueMode);
        handler.getBehaviours().add(falseMode);
        handler.handleEvent(null, null);
        assertTrue(trueMode.run);
        assertFalse(falseMode.run);

    }

    public void testBehaviourLocking() throws Exception {
        TestHandler handler = new TestHandler();

        final boolean[] locked = new boolean[1];
        locked[0] = false;

        final boolean[] ran = new boolean[3];
        ran[0] = false;
        ran[1] = false;
        ran[2] = false;

        handler.getBehaviours().add(new EventBehaviour(){

            public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
                return true;
            }

            public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
                    EventType eventType ) {
                if (locked[0])
                    fail("handler should be locked so this should not be ran"); //$NON-NLS-1$
                else {
                    ran[0] = true;
                }
                return null;
            }

            public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
                throw new RuntimeException(error);
            }

        });

        handler.getBehaviours().add(new LockingBehaviour(){

            public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
                return true;
            }

            public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
                    EventType eventType ) {

                if (locked[0])
                    fail("handler should be locked with a different key so this should not be ran"); //$NON-NLS-1$
                else
                    ran[1] = true;
                return null;
            }

            public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
                throw new RuntimeException(error);
            }

            public Object getKey(EditToolHandler handler) {
                return this;
            }

        });

        LockingBehaviour locker = new LockingBehaviour(){

            public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
                return true;
            }

            public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
                    EventType eventType ) {
                // this should be ran
                ran[2] = true;
                return null;
            }

            public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
                throw new RuntimeException(error);
            }

            public Object getKey(EditToolHandler handler) {
                return this;
            }

        };

        handler.getBehaviours().add(locker);

        MapMouseEvent event = new MapMouseEvent(null, 0, 0, 0, 0, 0);
        handler.handleEvent(event, EventType.RELEASED);

        assertTrue(ran[0]);
        assertTrue(ran[1]);
        assertTrue(ran[2]);
        ran[0] = false;
        ran[1] = false;
        ran[2] = false;

        assertFalse(handler.isLockOwner(locker));
        assertFalse(handler.isLockOwner(object));

        handler.lock(locker);

        locked[0]=true;
        assertTrue(handler.isLocked());
        assertTrue(handler.isLockOwner(locker));
        assertFalse(handler.isLockOwner(object));
        handler.handleEvent(event, EventType.RELEASED);

        assertFalse(ran[0]);
        assertFalse(ran[1]);
        assertTrue(ran[2]);
        try {

            handler.unlock(object);
            fail("only a behaviour with the same key as the locking behaviour should be able" + //$NON-NLS-1$
                    "to unlock the handler"); //$NON-NLS-1$
        } catch (Exception e) {
            // good
        }

        try {
            handler.lock(locker);
            fail("not implemented as a reentrant lock."); //$NON-NLS-1$
        } catch (Exception e) {
            // good
        }

        handler.unlock(locker);
        locked[0]=false;

        assertFalse(handler.isLocked());

        ran[0] = false;
        ran[1] = false;
        ran[2] = false;
        handler.handleEvent(event, EventType.RELEASED);

        assertTrue(ran[0]);
        assertTrue(ran[1]);
        assertTrue(ran[2]);

        try{
            handler.lock(new LockingBehaviour(){

                public Object getKey(EditToolHandler handler) {
                    return null;
                }

                public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
                    return false;
                }

                public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
                    return null;
                }

                public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
                }

            });
            fail("Null is not a legal key"); //$NON-NLS-1$
        }catch (Exception e) {
            // good
        }
    }

}
