package org.locationtech.udig.ui.tests.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class DisplayRule implements TestRule {

    private final Collection<Shell> capturedShells;

    private boolean displayOwner;

    private Display display;

    public DisplayRule() {
        capturedShells = new LinkedList();
        capturedShells.addAll(Arrays.asList(captureShells()));
    }

    public Display getDisplay() {
        if (display == null) {
            displayOwner = Display.getCurrent() == null;
            display = Display.getDefault();
        }
        return display;
    }

    public Shell[] getNewShells() {
        final Collection<Shell> newShells = new LinkedList();
        final Shell[] shells = captureShells();
        for (final Shell shell : shells) {
            if (!capturedShells.contains(shell)) {
                newShells.add(shell);
            }
        }
        return newShells.toArray(new Shell[] {});
    }

    public Shell createShell() {
        return createShell(SWT.NONE);
    }

    public Shell createShell(final int style) {
        return new Shell(getDisplay(), style);
    }

    public void ensureDisplay() {
        getDisplay();
    }

    public void flushPendingEvents() {
        while (Display.getCurrent() != null && !Display.getCurrent().isDisposed()
                && Display.getCurrent().readAndDispatch()) {
        }
    }

    public void dispose() {
        flushPendingEvents();
        disposeNewShells();
        disposeDisplay();
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } finally {
                    dispose();
                }
            }
        };
    }

    private void disposeNewShells() {
        final Shell[] newShells = getNewShells();
        for (final Shell shell : newShells) {
            shell.dispose();
        }
    }

    private static Shell[] captureShells() {
        Shell[] result = new Shell[0];
        final Display currentDisplay = Display.getCurrent();
        if (currentDisplay != null) {
            result = currentDisplay.getShells();
        }
        return result;
    }

    private void disposeDisplay() {
        if (display != null && displayOwner) {
            if (display.isDisposed()) {
                display.dispose();
            }
            display = null;
        }
    }

}