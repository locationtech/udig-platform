How to wait in the display thread
=================================

**Q:** How can I wait in the display thread? For example I want the display thread to join with
another thread.

**A:** Use the display's read and dispatch method to run other jobs that are waiting for the display
thread. Only when there are no more jobs waiting(readAndDispatch returns false when no more jobs are
waiting) then let the thread sleep.

::

    while( condition ) {
        //run a display event continue if there is more work todo.
        if ( display.readAndDispatch() ){
            continue;
        }
                
        //no more work to do in display thread, wait on request if request has not
        //finished
        if (condition)
            break ;

        Thread.sleep(300);
    }

