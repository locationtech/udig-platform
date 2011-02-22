/*
 * Created on 28-Mar-2005
 */
package net.refractions.udig.catalog.tests;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;
import net.refractions.udig.catalog.IResolve;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Sub-class me and fill in the appripriate protected methods ...
 *
 * @author dzwiers
 */
public abstract class AbstractResolveTest extends TestCase {
    protected abstract IResolve getResolve();

    public static final class FakeProgress implements IProgressMonitor {

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.core.runtime.IProgressMonitor#beginTask(java.lang.String, int)
         */
        public void beginTask( String name, int totalWork ) {
            total = totalWork;
        }

        // public Date start = new Date();

        public int total = 0;
        public int completed = 0;

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.core.runtime.IProgressMonitor#done()
         */
        public void done() {
            completed = total;
            // Date done = new Date();
            // assume 3 milisec
            // assertTrue("Took too long ... you sure you don't block?",(new
            // Date(start.getTime()+4)).after(done));
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.core.runtime.IProgressMonitor#internalWorked(double)
         */
        public void internalWorked( double work ) {
            // no op
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled()
         */
        public boolean isCanceled() {
            return completed == -1;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.core.runtime.IProgressMonitor#setCanceled(boolean)
         */
        public void setCanceled( boolean value ) {
            if (value)
                completed = -1;
            else
                completed = 0;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.core.runtime.IProgressMonitor#setTaskName(java.lang.String)
         */
        public void setTaskName( String name ) {
            // no op
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.core.runtime.IProgressMonitor#subTask(java.lang.String)
         */
        public void subTask( String name ) {
            // no op
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.core.runtime.IProgressMonitor#worked(int)
         */
        public void worked( int work ) {
            if (completed > -1)
                completed += work;
        }

    }

    protected abstract boolean hasParent();

    public void testParent() throws IOException {
        if (hasParent()) {
            IResolve parent = getResolve().parent(null);
            assertNotNull("Parent value does not match expected value", parent); //$NON-NLS-1$
        }
    }

    public void testParentMonitor() throws IOException {
        FakeProgress monitor = new FakeProgress();
        IResolve parent = getResolve().parent(monitor);
        if (hasParent()) {
            assertNotNull("Parent value does not match expected value", parent); //$NON-NLS-1$
        }
    }

    protected abstract boolean isLeaf();

    public void testMembers() throws IOException {
        List< ? extends IResolve> children = getResolve().members(null);
        if (!isLeaf())
            assertNotNull("Child list null", children); //$NON-NLS-1$
    }

    public void testMembersMonitor() throws IOException {
        FakeProgress monitor = new FakeProgress();
        List< ? extends IResolve> children = getResolve().members(monitor);
        if (!isLeaf()) {
            assertNotNull("Child list null", children); //$NON-NLS-1$
            assertEquals( "Monitor must be finished",  monitor.total, monitor.completed); //$NON-NLS-1$
        }
    }

    public void testID() {
        long start = System.currentTimeMillis();
        assertNotNull("Id is required for admission", getResolve().getIdentifier()); //$NON-NLS-1$
        assertTrue("Took too long ... blocking?", (start + BLOCK) >= System.currentTimeMillis()); //$NON-NLS-1$
    }

    public static final int BLOCK = 150; // number of acceptable milliseconds for a run
}
