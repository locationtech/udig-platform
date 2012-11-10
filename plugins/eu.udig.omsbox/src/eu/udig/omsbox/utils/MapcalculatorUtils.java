/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.omsbox.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import eu.udig.omsbox.OmsBoxPlugin;

/**
 * Utilities for mapcalculators.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public class MapcalculatorUtils {
    public static final String GENERAL = "general";
    public static final String CONTROL_FLOW = "control flow";
    public static final String LOGICAL = "logical";
    public static final String ARITHMETIC = "arithmetic";
    public static final String NUMERIC = "numeric";
    public static final String STATISTICAL = "statistical";
    public static final String PROCESSING = "processing area";

    private static Color darkGreenColor = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
    private static Color darkBlueColor = Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE);
    private static Color darkCyanColor = Display.getDefault().getSystemColor(SWT.COLOR_DARK_CYAN);
    private static Color redColor = Display.getDefault().getSystemColor(SWT.COLOR_RED);
    private static Color darkRedColor = Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);

    private static List<String> reservedWords = Arrays
            .asList("boolean", "break", "breakif", "con", "double", "else", "false", "float", "foreach", "if", "images", "in",
                    "init", "int", "null", "options", "read", "true", "until", "while", "write");
    private static List<String> operationsWords = Arrays.asList("sqrt", "isnan", "isinf", "isnull", "null", "radToDeg",
            "degToRad", "max", "min", "mean", "median", "mode", "range", "sdev", "sum", "variance", "height", "width", "xmin",
            "ymin", "xmax", "ymax", "xres", "yres");

    public static enum Constructs {

        // CONTROL_FLOW
        IMG("img", "images {\n    example = read;\n    result = write;\n} \n", "Initial image definition block", CONTROL_FLOW), //
        IF("if", "if (?) {\n    result = 1;\n} else {\n    result = 0;\n} \n", "If-else block", CONTROL_FLOW), //
        CON("con", "con(?, ?, ?) \n", "Conditional block", CONTROL_FLOW), //
        FOREACH("for",
                "foreach (dy in -1:1) {\n    foreach (dx in -1:1) {\n        n += srcimage[dx, dy] > someValue;\n    }\n} \n",
                "Foreach loop example which iterates through pixels in a 3x3 neighbourhood", CONTROL_FLOW), //
        WHILE("while", "while ( ? ) {\n\n} \n",
                "A conditional loop which executes the target statement or block while its conditional expression is non-zero",
                CONTROL_FLOW), //
        // GENERAL
        SEMICOLON(";", ";", "The semicolon, every statement", GENERAL), //
        COMMENT("/* */", "/*\n* comment\n*/", "Block comment", GENERAL), //
        COMMENTLINE("// ", "// comment", "Line comment", GENERAL), //
        ARRAY("[a,b] ", "array = [1, 2, 3];", "Example declaration of an array", GENERAL), //
        // LOGICAL
        LOGICAL1("AND", "&&", "logical AND", LOGICAL), //
        LOGICAL2("OR", "||", "logical OR", LOGICAL), //
        LOGICAL3("==", "==", "equality test", LOGICAL), //
        LOGICAL4("!=", "!=", "inequality test", LOGICAL), //
        LOGICAL5("> ", "> ", "greater than", LOGICAL), //
        LOGICAL6(">=", ">=", "greater than or equal to", LOGICAL), //
        LOGICAL7("<=", "<=", "less than", LOGICAL), //
        LOGICAL8("< ", "< ", "less than or equal to", LOGICAL), //
        // ARITHMETIC
        ARITHM1("^", "^", "Raise to power", ARITHMETIC), //
        ARITHM2("*", "*", "Multiply", ARITHMETIC), //
        ARITHM3("/", "/", "Divide", ARITHMETIC), //
        ARITHM4("%", "%", "Modulo (remainder)", ARITHMETIC), //
        ARITHM5("+", "+", "Add", ARITHMETIC), //
        ARITHM6("-", "-", "Subtract", ARITHMETIC), //
        ARITHM7("=", "=", "Assignment", ARITHMETIC), //
        // NUMERIC
        SQRT("sqrt", "sqrt(?)", "Square root", NUMERIC), //
        ISNULL("null?", "isnull(?)", "Is null test on value x", NUMERIC), //
        ISINF("inf?", "isinf( ? )", "Is infinite test on value x", NUMERIC), //
        ISNAN("nan?", "isnan( ? )", "Is not a number test on value x", NUMERIC), //
        RADTODEG("r2d", "radToDeg( ? )", "Radians to degrees", NUMERIC), //
        DEG2RAD("r2d", "degToRad( ? )", "Degrees to radians", NUMERIC), //
        // STATISTICAL
        STATS1("max", "max(?, ?)", "Maximum", STATISTICAL), //
        STATS3("mean", "mean(?)", "Mean", STATISTICAL), //
        STATS4("min", "min(?, ?)", "Minimum", STATISTICAL), //
        STATS6("med", "median(?)", "Median of an array of values", STATISTICAL), //
        STATS7("mode", "mode(?)", "Mode of an array of values", STATISTICAL), //
        STATS8("range", "range(?)", "Range of an array of values", STATISTICAL), //
        STATS9("sdev", "sdev(?)", "Standard deviation of an array of values", STATISTICAL), //
        STATS10("sum", "sum(?)", "Sum of an array of values", STATISTICAL), //
        STATS11("var", "variance(?)", "Variance of an array of values", STATISTICAL), //
        // PROCESSING
        PROCESSINGSAREA1("h", "height()", "Height of the processing area (world units)", PROCESSING), //
        PROCESSINGSAREA2("w", "width()", "Width of the processing area (world units)", PROCESSING), //
        PROCESSINGSAREA3("xmin", "xmin()", "Minimum X ordinate of the processing area (world units)", PROCESSING), //
        PROCESSINGSAREA4("ymin", "ymin()", "Minimum Y ordinate of the processing area (world units)", PROCESSING), //
        PROCESSINGSAREA5("xmax", "xmax()", "Maximum X ordinate of the processing area (world units)", PROCESSING), //
        PROCESSINGSAREA6("ymax", "ymax()", "Maximum Y ordinate of the processing area (world units)", PROCESSING), //
        PROCESSINGSAREA7("x", "x()", "X ordinate of the current processing position (world units)", PROCESSING), //
        PROCESSINGSAREA8("y", "y()", "Y ordinate of the current processing position (world units)", PROCESSING), //
        PROCESSINGSAREA9("xres", "xres()", "Pixel width (world units)", PROCESSING), //
        PROCESSINGSAREA10("yres", "yres()", "Pixel height (world units)", PROCESSING), //
        ;

        String name;
        String toolTip;
        String construct;
        String category;
        private Constructs( String name, String construct, String toolTip, String category ) {
            this.name = name;
            this.toolTip = toolTip;
            this.construct = construct;
            this.category = category;
        }
    }

    public static void createMapcalcConstructsButtons( Composite buttonsComposite, final StyledText text ) {

        final CTabFolder tabFolder = new CTabFolder(buttonsComposite, SWT.TOP);
        tabFolder.setBorderVisible(true);
        tabFolder.setMaximized(true);
        GridData tabGD = new GridData(SWT.CENTER, SWT.FILL, false, true);
        tabFolder.setLayoutData(tabGD);

        HashMap<String, CTabItem> tabsMap = new HashMap<String, CTabItem>();
        Constructs[] values = Constructs.values();
        for( final Constructs construct : values ) {
            String category = construct.category;
            CTabItem tabItem = tabsMap.get(category);
            if (tabItem == null) {
                Composite composite = new Composite(tabFolder, SWT.NONE);
                GridLayout compositeLayout = new GridLayout(2, true);
                composite.setLayout(compositeLayout);
                GridData compositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
                composite.setLayoutData(compositeGD);
                tabItem = new CTabItem(tabFolder, SWT.NONE);
                tabItem.setText(category);
                tabItem.setControl(composite);
                tabsMap.put(category, tabItem);
            }
            Control control = tabItem.getControl();
            Button constructButton = new Button((Composite) control, SWT.PUSH);
            constructButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            constructButton.setText(construct.name);
            constructButton.setToolTipText(construct.toolTip);
            constructButton.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected( SelectionEvent e ) {
                    insertTextAtCaretPosition(text, construct.construct);
                }
            });
        }
        tabFolder.setSelection(0);
        tabFolder.setSingle(true);
        tabFolder.pack();
    }

    private static void insertTextAtCaretPosition( StyledText text, String string ) {
        int caretPosition = text.getCaretOffset();

        String textStr = text.getText();
        String sub1 = textStr.substring(0, caretPosition);
        String sub2 = textStr.substring(caretPosition);

        StringBuilder sb = new StringBuilder();
        sb.append(sub1);
        sb.append(string);
        sb.append(sub2);

        text.setText(sb.toString());
    }

    public static StyledText createMapcalcPanel( Composite parent, int rows ) {
        Composite mapcalcComposite = new Composite(parent, SWT.NONE);
        GridData compositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        compositeGD.verticalSpan = rows;
        compositeGD.widthHint = 100;
        mapcalcComposite.setLayoutData(compositeGD);
        mapcalcComposite.setLayout(new GridLayout(2, false));

        final StyledText text = new StyledText(mapcalcComposite, SWT.MULTI | SWT.WRAP | SWT.LEAD | SWT.BORDER | SWT.V_SCROLL);
        GridData textGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        textGD.verticalSpan = rows;
        textGD.heightHint = 100;
        text.setLayoutData(textGD);
        text.addModifyListener(new ModifyListener(){
            public void modifyText( ModifyEvent e ) {
                MapcalculatorUtils.checkStyle(text);
            }
        });

        Composite buttonsComposite = new Composite(mapcalcComposite, SWT.NONE);
        buttonsComposite.setLayoutData(new GridData(SWT.END, SWT.FILL, false, true));
        GridLayout buttonsCompositeLayout = new GridLayout(1, true);
        buttonsCompositeLayout.marginWidth = 0;
        buttonsComposite.setLayout(buttonsCompositeLayout);

        String[] mapcalcHistoryItems = new String[0];
        String mapcalcHistory = OmsBoxPlugin.getDefault().getMapcalcHistory();
        if (mapcalcHistory != null && mapcalcHistory.length() > 0) {
            mapcalcHistoryItems = mapcalcHistory.split(OmsBoxConstants.MAPCALCHISTORY_SEPARATOR);
        }

        HashMap<String, String> items2ValuesMap = new HashMap<String, String>();
        for( String item : mapcalcHistoryItems ) {
            String descr = item.replaceAll("\n", " ");
            items2ValuesMap.put(descr, item);
        }
        mapcalcHistoryItems = items2ValuesMap.keySet().toArray(new String[0]);

        final Combo historyCombo = new Combo(mapcalcComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData historyComboGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        historyComboGD.horizontalSpan = 2;
        historyCombo.setLayoutData(historyComboGD);
        historyCombo.setItems(mapcalcHistoryItems);
        historyCombo.setData(items2ValuesMap);
        historyCombo.select(0);
        historyCombo.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                int selectionIndex = historyCombo.getSelectionIndex();
                String item = historyCombo.getItem(selectionIndex);
                @SuppressWarnings("unchecked")
                HashMap<String, String> descriptionsMap = (HashMap<String, String>) historyCombo.getData();
                String value = descriptionsMap.get(item);
                text.setText(value);
            }
        });

        createMapcalcConstructsButtons(buttonsComposite, text);
        MapcalculatorUtils.checkStyle(text);
        return text;
    }

    public static void saveMapcalcHistory( String function ) {
        String mapcalcHistory = OmsBoxPlugin.getDefault().getMapcalcHistory();
        if (mapcalcHistory == null) {
            mapcalcHistory = "";
        }
        String[] historySplit = mapcalcHistory.split(OmsBoxConstants.MAPCALCHISTORY_SEPARATOR);
        List<String> historyList = new ArrayList<String>();
        for( String tmp : historySplit ) {
            tmp = tmp.trim();
            if (tmp.length() > 0) {
                if (!historyList.contains(tmp))
                    historyList.add(tmp);
            }
        }
        if (!historyList.contains(function))
            historyList.add(0, function);

        while( historyList.size() > 15 ) {
            historyList.remove(15);
        }

        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < historyList.size(); i++ ) {
            if (i == 0) {
                sb.append(historyList.get(i).trim());
            } else {
                sb.append(OmsBoxConstants.MAPCALCHISTORY_SEPARATOR).append(historyList.get(i).trim());
            }
        }
        OmsBoxPlugin.getDefault().setMapcalcHistory(sb.toString());
    }

    public static void checkStyle( StyledText functionAreaText ) {
        String text = functionAreaText.getText() + " ";

        // color reserved words
        for( int i = 0; i < reservedWords.size(); i++ ) {
            String reservedWord = reservedWords.get(i);

            int index = 0;
            while( (index = text.indexOf(reservedWord, index)) != -1 ) {
                StyleRange styleRange = new StyleRange();
                styleRange.start = index;
                int length = reservedWord.length();
                styleRange.length = length;
                styleRange.foreground = darkRedColor;
                styleRange.fontStyle = SWT.BOLD | SWT.ITALIC;
                functionAreaText.setStyleRange(styleRange);
                index = index + length;
            }
        }

        // color important words
        for( int i = 0; i < operationsWords.size(); i++ ) {
            String opWord = operationsWords.get(i);

            int index = 0;
            while( (index = text.indexOf(opWord, index)) != -1 ) {
                StyleRange styleRange = new StyleRange();
                styleRange.start = index;
                int length = opWord.length();
                styleRange.length = length;
                styleRange.foreground = darkCyanColor;
                styleRange.fontStyle = SWT.BOLD | SWT.ITALIC;
                functionAreaText.setStyleRange(styleRange);
                index = index + length;
            }
        }

        // brackets
        String[] textSplit = text.split("\\(|\\)|\\{|\\}"); //$NON-NLS-1$
        if (textSplit.length > 1) {
            List<Integer> bracketPositions = new ArrayList<Integer>();
            int position = 0;
            for( int i = 0; i < textSplit.length - 1; i++ ) {
                position = position + textSplit[i].length() + 1;
                bracketPositions.add(position - 1);
            }

            for( Integer pos : bracketPositions ) {
                StyleRange styleRange = new StyleRange();
                styleRange.start = pos;
                styleRange.length = 1;
                styleRange.foreground = darkBlueColor;
                styleRange.fontStyle = SWT.BOLD;
                functionAreaText.setStyleRange(styleRange);
            }
        }

        // ;
        textSplit = text.split(";"); //$NON-NLS-1$
        if (textSplit.length > 1) {

            List<Integer> bracketPositions = new ArrayList<Integer>();
            int position = 0;
            for( int i = 0; i < textSplit.length - 1; i++ ) {
                position = position + textSplit[i].length() + 1;
                bracketPositions.add(position - 1);
            }

            for( Integer pos : bracketPositions ) {
                StyleRange styleRange = new StyleRange();
                styleRange.start = pos;
                styleRange.length = 1;
                styleRange.foreground = darkGreenColor;
                styleRange.fontStyle = SWT.BOLD;
                functionAreaText.setStyleRange(styleRange);
            }
        }

        textSplit = text.split("\\?"); //$NON-NLS-1$
        if (textSplit.length > 1) {
            List<Integer> bracketPositions = new ArrayList<Integer>();
            int position = 0;
            for( int i = 0; i < textSplit.length - 1; i++ ) {
                position = position + textSplit[i].length() + 1;
                bracketPositions.add(position - 1);
            }

            for( Integer pos : bracketPositions ) {
                StyleRange styleRange = new StyleRange();
                styleRange.start = pos;
                styleRange.length = 1;
                styleRange.foreground = redColor;
                styleRange.fontStyle = SWT.BOLD;
                functionAreaText.setStyleRange(styleRange);
            }
        }

    }

}
