package net.refractions.udig.tutorials.featureeditor;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.project.command.CompositeCommand;
import net.refractions.udig.project.command.factory.EditCommandFactory;
import net.refractions.udig.project.ui.IFeaturePanel;
import net.refractions.udig.project.ui.IFeatureSite;
import net.refractions.udig.project.ui.tool.IToolContext;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PartInitException;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;

public class CountryFeaturePanel extends IFeaturePanel {
	/** Attribute name for attribute GMI_CNTRY */
	public final static String GMI_CNTRY = "GMI_CNTRY";

	/** Attribute name for attribute REGION */
	public final static String COLOR_MAP = "COLOR_MAP";

	/** Attribute name for attribute NAME */
	public final static String NAME = "CNTRY_NAME";

	public final static Object[] COLOR_MAP_OPTS = new Object[] { 1, 2, 3, 4, 5,
			6, 7, 8 };

	Text gmiCntry;
	Text name;
	ComboViewer colorMap;
	private Button apply;
	private Button reset;

	private SimpleFeature oldFeature;

	/**
	 * Listen to the selection change.
	 * <p>
	 * It is poliet to keep your listeners internal and not pollute your class
	 * definition with extra interfaces that have nothing to do with your
	 * interaction with the outside world (and are basically an internal
	 * detail).
	 */
	private ISelectionChangedListener selectionListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection selection = (IStructuredSelection) event
					.getSelection();

			Integer value = (Integer) selection.getFirstElement();
			setEnabled(true);
		}
	};
	/**
	 * List to the fields change as keys are pressed.
	 */
	private KeyListener keyListener = new KeyListener() {
		public void keyPressed(KeyEvent e) {
			// do nothing
		}

		public void keyReleased(KeyEvent e) {
			setEnabled(true);
		}
	};

	/**
	 * Enable the apply/reset buttons...
	 * 
	 * @param enabled
	 */
	private void setEnabled(boolean enabled) {
		if (oldFeature == null && enabled) {
			return;
		}
		apply.setEnabled(enabled);
		reset.setEnabled(enabled);
	}

	private void applyChanges() {
		SimpleFeature editedFeature = getSite().getEditManager()
				.getEditFeature();

		try {
			editedFeature.setAttribute(NAME, name.getText());
			editedFeature.setAttribute(GMI_CNTRY, gmiCntry.getText());

			IStructuredSelection selection = (IStructuredSelection) colorMap
					.getSelection();
			Integer color = (Integer) selection.getFirstElement();
			editedFeature.setAttribute(COLOR_MAP, color.toString());

		} catch (IllegalAttributeException e1) {
			// shouldn't happen.
		}

		CompositeCommand compComm = new CompositeCommand();
		EditCommandFactory editFactory = getSite().getEditFactory();
		compComm.getCommands().add(
				editFactory.createSetEditFeatureCommand(editedFeature));
		compComm.getCommands().add(editFactory.createWriteEditFeatureCommand());
		getSite().sendASyncCommand(compComm);
		setEnabled(false);
	}

	/**
	 * Step 0 - Default constructor.
	 */
	public CountryFeaturePanel() {
	}

	/**
	 * Step 1 - init using the editor site and memento holding any information
	 * from last time
	 */
	@Override
	public void init(IFeatureSite site, IMemento memento)
			throws PartInitException {
		super.init(site, memento);
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new MigLayout("", "[right]10[left, grow][min!][min!]",
				"30"));
		// SWT Widgets
		Label label = new Label(parent, SWT.SHADOW_IN);
		label.setText("Country:");

		name = new Text(parent, SWT.SHADOW_IN | SWT.BORDER);
		name.setLayoutData("span 3, growx, wrap");
		name.addKeyListener(keyListener);

		label = new Label(parent, SWT.SHADOW_IN);
		label.setText("Code:");

		gmiCntry = new Text(parent, SWT.SHADOW_IN | SWT.BORDER);
		gmiCntry.setLayoutData("span 3, growx, wrap");
		gmiCntry.addKeyListener(keyListener);

		// JFace Viewer
		label = new Label(parent, SWT.SHADOW_IN);
		label.setText("Color Map:");

		colorMap = new ComboViewer(parent, SWT.SHADOW_IN);
		colorMap.getControl().setLayoutData("wrap");
		colorMap.addSelectionChangedListener(selectionListener);

		// hook up to data
		colorMap.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof Object[]) {
					return (Object[]) inputElement;
				}
				return null;
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				// for dynamic content we would register listeners here
			}

			public void dispose() {
			}
		});
		colorMap.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return " " + element + " color";
			}
		});
		colorMap.setInput(COLOR_MAP_OPTS);

		// Buttons
		apply = new Button(parent, SWT.PUSH);
		apply.setLayoutData("skip2");
		apply.setText("Apply");
		apply.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// applyChanges();
			}
		});
		apply.setEnabled(false);

		reset = new Button(parent, SWT.PUSH);
		reset.setText("Reset");
		reset.setEnabled(false);
		reset.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// resetChanges();
			}
		});
	}

	@Override
	public String getDescription() {
		return "Details on the selected country.";
	}

	@Override
	public String getName() {
		return "Country";
	}

	@Override
	public String getTitle() {
		return "Country Details";
	}

}
