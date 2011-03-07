package eu.udig.style.advanced.points.widgets;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.geotools.renderer.style.FontCache;

import eu.udig.style.advanced.common.ParameterComposite;
import eu.udig.style.advanced.common.IStyleChangesListener.STYLEEVENTTYPE;
import eu.udig.style.advanced.common.styleattributeclasses.PointSymbolizerWrapper;
import eu.udig.style.advanced.common.styleattributeclasses.RuleWrapper;

/**
 * A composite that holds widgets for font and character selection.
 * 
 * @author pjessup
 */
public class PointCharacterChooserComposite extends ParameterComposite implements SelectionListener {
    private static final String TTF_PREFIX = "ttf://"; //$NON-NLS-1$
    private static final String HEX_PREFIX = "#0x"; //$NON-NLS-1$
    private static final String LABEL_PREFIX = "Character Code: 0x"; //$NON-NLS-1$
    private static final int FIRST_CHAR = 0x21;
    private static final int CHARACTERS = 0x10000;
    private static final int PLUS_SIGN = 0x2b;
    private static final int COLUMNS = 8;
    private static final int FONT_SIZE = 10;

    private Combo fontCombo;
    private Table table;
    private Label characterLabel;
    private TableCursor tableCursor;
    private String fontName;
    private String characterCode;
    private String[] characterString;

    private final Composite parent;

    private Composite mainComposite;

    public PointCharacterChooserComposite( Composite parent ) {
        this.parent = parent;
    }

    public Composite getComposite() {
        return mainComposite;
    }

    /**
     * Initialize the composite with pre-existing values.
     * 
     * @param ruleWrapper the {@link RuleWrapper}.
     */
    public void init( RuleWrapper ruleWrapper ) {
        PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper()
                .adapt(PointSymbolizerWrapper.class);

        mainComposite = new Composite(parent, SWT.RESIZE);
        mainComposite.setLayout(new GridLayout(2, false));
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label fontLabel = new Label(mainComposite, SWT.NONE);
        fontLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
        fontLabel.setText("Font:"); //$NON-NLS-1$
        fontCombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        fontCombo.setItems(getScalableFonts());
        fontCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        String characterPath = pointSymbolizerWrapper.getMarkName();
        if (characterPath != null && characterPath.matches("ttf://.+#.+")) { //$NON-NLS-1$
            String[] fontElements = characterPath.substring(6).split("#"); //$NON-NLS-1$
            int index = fontCombo.indexOf(fontElements[0]);
            if (index != -1) {
                fontCombo.select(index);
                characterCode = fontElements[1].substring(2);
            } else {
                fontCombo.select(0);
                characterCode = Integer.toHexString(PLUS_SIGN);
            }
        } else {
            fontCombo.select(0);
            characterCode = Integer.toHexString(PLUS_SIGN);
        }

        fontCombo.addSelectionListener(this);
        fontName = fontCombo.getItem(fontCombo.getSelectionIndex());

        table = new Table(mainComposite, SWT.BORDER | SWT.V_SCROLL | SWT.SIMPLE);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.horizontalSpan = 2;
        gridData.heightHint = 300;
        table.setLayoutData(gridData);
        table.setLinesVisible(true);
        table.setRedraw(false);
        Font font = new Font(Display.getCurrent(), fontName, FONT_SIZE, SWT.NORMAL);
        table.setFont(font);

        for( int i = 0; i < COLUMNS; i++ ) {
            new TableColumn(table, SWT.NONE);
        }

        java.awt.Font awtFont = (new java.awt.Font(fontName, java.awt.Font.PLAIN, FONT_SIZE));
        TableItem tableItem = new TableItem(table, SWT.NONE);
        initializeCharacterStringArray();
        for( int ch = FIRST_CHAR; ch < CHARACTERS; ch++ ) {
            int col = ch % COLUMNS;
            if (col == 0) {
                tableItem = new TableItem(table, SWT.NONE);
            }
            if (ch > 0xFF && !awtFont.canDisplay(ch)) {
                tableItem.setText(col, ""); //$NON-NLS-1$                
            } else {
                tableItem.setText(col, characterString[ch]);
            }
        }

        table.getColumn(0).pack();
        int width = table.getColumn(0).getWidth();
        for( int i = 1; i < COLUMNS; i++ ) {
            table.getColumn(i).setWidth(width);
        }
        
        // Set redraw back to true so that the table
        // will paint appropriately
        table.setRedraw(true);

        tableCursor = new TableCursor(table, SWT.NONE);
        tableCursor.setBackground(Display.getCurrent()
                .getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

        int index = Integer.parseInt(characterCode, 16);
        int row = index / COLUMNS - 4;
        int col = index % COLUMNS;
        tableCursor.setSelection(row, col);
        tableCursor.setFont(font);
        tableCursor.addSelectionListener(this);

        characterLabel = new Label(mainComposite, SWT.NONE);
        characterLabel.setText(LABEL_PREFIX + characterCode + "      "); //$NON-NLS-1$
    }

    /**
     * Get mark path in the <code>ttf://fontName#code</code> format
     */
    public String getCharacterPath() {
        if (fontName == null || characterCode == null) {
            return null;
        }
        return TTF_PREFIX + fontName + HEX_PREFIX + characterCode;
    }

    private String[] getScalableFonts() {
        FontData[] fontData = Display.getCurrent().getFontList(null, true);
        Set<String> fontSet = new HashSet<String>();
        for( FontData fd : fontData ) {
            fontSet.add(fd.getName());
        }
        Set<String> fontCache = new TreeSet<String>((new FontCache()).getAvailableFonts());
        fontCache.retainAll(fontSet);
        return (String[]) fontCache.toArray(new String[0]);
    }

    public void widgetSelected( SelectionEvent e ) {
        Object source = e.getSource();
        if (source.equals(fontCombo)) {
            fontName = fontCombo.getItem(fontCombo.getSelectionIndex());
            update();
            notifyListeners(getCharacterPath(), false, STYLEEVENTTYPE.MARKNAME);
        } else if (source.equals(tableCursor)) {
            TableItem[] tableItem = new TableItem[]{tableCursor.getRow()};
            table.setSelection(tableItem);
            int index = (table.getSelectionIndex() + 4) * COLUMNS + tableCursor.getColumn();
            table.deselect(table.getSelectionIndex());
            if (index >= FIRST_CHAR) {
                characterCode = Integer.toHexString(index);
                characterLabel.setText(LABEL_PREFIX + characterCode);
            }
            notifyListeners(getCharacterPath(), false, STYLEEVENTTYPE.MARKNAME);
        }
    }

    public void widgetDefaultSelected( SelectionEvent e ) {
    }

    /*
     * Update font and redraw table
     */
    private void update() {
        table.setRedraw(false);
        Font font = new Font(Display.getCurrent(), fontName, FONT_SIZE, SWT.NORMAL);
        table.setFont(font);
        tableCursor.removeSelectionListener(this);
        tableCursor.setFont(font);
        tableCursor.addSelectionListener(this);
        java.awt.Font awtFont = (new java.awt.Font(fontName, java.awt.Font.PLAIN, FONT_SIZE));
        TableItem[] tableItem = table.getItems();
        int row = 0;
        for( int ch = FIRST_CHAR; ch < CHARACTERS; ch++ ) {
            int col = ch % COLUMNS;
            if (col == 0) {
                row++;
            }
            if (ch > 0xFF && !awtFont.canDisplay(ch)) {
                tableItem[row].setText(col, ""); //$NON-NLS-1$
            } else {
                tableItem[row].setText(col, characterString[ch]);
            }
        }
        table.setRedraw(true);
    }

    private void initializeCharacterStringArray() {
        characterString = new String[CHARACTERS];
        for( int i = 0; i < CHARACTERS; i++ ) {
            characterString[i] = Character.toString((char) i);
        }
    }
}