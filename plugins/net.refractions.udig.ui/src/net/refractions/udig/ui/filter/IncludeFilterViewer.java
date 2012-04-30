package net.refractions.udig.ui.filter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.filter.Filter;

/**
 * FilterViewer with a simple radio button enabling the user to choose between Filter.INCLUDE and
 * Filter.Exclude
 * 
 * @author Scott
 * @since 1.3.0
 */
public class IncludeFilterViewer extends IFilterViewer {
    /**
     * Factory used for the general purpose IncludeFilterViewer.
     * 
     * @author Jody Garnett
     * @since 1.3.2
     */
    public static class Factory extends FilterViewerFactory {
        /**
         * Only {@link FilterViewerFactory#APPROPRIATE} for INCLUDE and EXCLUDE.
         */
        @Override
        public int appropriate(FilterInput input, Filter filter) {
            // we are general purpose and will ignore schema
            if (filter == Filter.EXCLUDE || filter == Filter.INCLUDE) {
                return APPROPRIATE;
            }
            return INCOMPLETE; // only able to display INCLUDE and EXCLDUE
        }
        @Override
        public IFilterViewer createViewer(Composite parent, int style) {
            return new IncludeFilterViewer(parent, style);
        }
    }

    protected Composite control;

    protected Button enableButton;

    protected Button disableButton;

    public IncludeFilterViewer(Composite parent) {
        this(parent, SWT.SINGLE);
    }

    /**
     * Creates an simple IncludeFilterViewer using the provided style.
     * 
     * @param parent
     * @param none
     */
    public IncludeFilterViewer(Composite parent, int style) {
        control = new Composite(parent, style);
        
        enableButton = new Button(control, SWT.RADIO);
        enableButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                internalUpdate(Filter.INCLUDE);
                feedback();
            }
        });

        enableButton.setBounds(20, 31, 90, 16);
        enableButton.setText("Enable");

        disableButton = new Button(control, SWT.RADIO);
        disableButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                internalUpdate( Filter.EXCLUDE);
                feedback();
            }
        });
        disableButton.setBounds(20, 53, 90, 16);
        disableButton.setText("Disable");
    }

    /**
     * This is the widget used to display the Expression; its parent has been provided in the
     * ExpressionViewer's constructor; but you may need direct access to it in order to set layout
     * data etc.
     * 
     * @return
     */
    public Control getControl() {
        return control;
    }

    /**
     * Used to check for any validation messages (such as required field etc...)
     * 
     * @return Validation message
     */
    public String getValidationMessage() {
        if (enableButton.getSelection() && disableButton.getSelection()) {
            return "Unable to represent " + ECQL.toCQL(filter);
        } else if (!enableButton.getSelection() && !disableButton.getSelection()) {
            return "Unable to represent " + ECQL.toCQL(filter);
        }
        return null; // all good then
    }
    @Override
    public void setFilter(Filter filter) {
        this.filter = filter;
        refresh();
    }
    @Override
    public void refresh() {
        if (filter == Filter.INCLUDE) {
            enableButton.setSelection(true);
            disableButton.setSelection(false);
        } else if (filter == Filter.EXCLUDE) {
            enableButton.setSelection(false);
            disableButton.setSelection(true);
        } else {
            // we have a warning as the filter does not match our abilities
            enableButton.setSelection(false);
            disableButton.setSelection(false);
            
            feedbackReplace( filter );
        }
    }

}