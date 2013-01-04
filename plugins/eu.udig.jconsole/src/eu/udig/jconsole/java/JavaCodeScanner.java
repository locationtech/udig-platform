/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package eu.udig.jconsole.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;

import eu.udig.jconsole.JConsolePlugin;
import eu.udig.jconsole.util.JavaColorProvider;
import eu.udig.jconsole.util.JavaWhitespaceDetector;
import eu.udig.jconsole.util.JavaWordDetector;
import eu.udig.omsbox.core.FieldData;
import eu.udig.omsbox.core.ModuleDescription;

/**
 * A Java code scanner.
 */
public class JavaCodeScanner extends RuleBasedScanner {

    private static String[] fgKeywords = {"println", "abstract", "break", "case", "catch", "class", "continue", "default", "do",
            "else", "extends", "final", "finally", "for", "if", "implements", "import", "instanceof", "interface", "native",
            "new", "package", "private", "protected", "public", "return", "static", "super", "switch", "synchronized", "this",
            "throw", "throws", "transient", "try", "volatile", "while"};

    private static String[] geoscriptKeywords = {//
    /*    */"Point", "LineString", "Polygon", //
            "buffer", "intersects", "intersect", "union", "centroid", "area", "length" //
    };

    private static String[] fgTypes = {"void", "boolean", "char", "byte", "short", "int", "long", "float", "double", "def", "NaN"};

    private static String[] fgConstants = {"false", "null", "true"}; //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$

    private static String[] omsComponents = {"components", "parameter", "connect"}; //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$

    private static String[] omsSim = {"sim"}; //$NON-NLS-1$

    private static String[] omsModel = {"model"}; //$NON-NLS-1$

    private static List<String> moduleFieldsNameList = new ArrayList<String>();
    private static List<String> moduleClassesNameList = new ArrayList<String>();

    /**
     * Creates a Java code scanner with the given color provider.
     *
     * @param provider the color provider
     */
    public JavaCodeScanner( JavaColorProvider provider ) {

        HashMap<String, List<ModuleDescription>> modulesMap = JConsolePlugin.getDefault().gatherModules();
        Collection<List<ModuleDescription>> modulesDescriptions = modulesMap.values();
        for( List<ModuleDescription> modulesDescriptionList : modulesDescriptions ) {
            for( ModuleDescription moduleDescription : modulesDescriptionList ) {
                List<FieldData> inputsList = moduleDescription.getInputsList();
                for( FieldData inFieldData : inputsList ) {
                    moduleFieldsNameList.add(inFieldData.fieldName);
                }
                List<FieldData> outputsList = moduleDescription.getOutputsList();
                for( FieldData outFieldData : outputsList ) {
                    moduleFieldsNameList.add(outFieldData.fieldName);
                }
                // String name = moduleDescription.getName();
                String className = moduleDescription.getClassName();
                moduleClassesNameList.add(className);
            }
        }

        IToken omsSimTok = new Token(new TextAttribute(provider.getColor(JavaColorProvider.OMS_SIM)));
        IToken omsModelTok = new Token(new TextAttribute(provider.getColor(JavaColorProvider.OMS_SIM)));
        IToken omsComponentsTok = new Token(new TextAttribute(provider.getColor(JavaColorProvider.OMS_COMPONENTS)));
        IToken omsModulesTok = new Token(new TextAttribute(provider.getColor(JavaColorProvider.OMS_MODULES)));
        IToken keyword = new Token(new TextAttribute(provider.getColor(JavaColorProvider.KEYWORD), null, SWT.BOLD));
        IToken type = new Token(new TextAttribute(provider.getColor(JavaColorProvider.TYPE)));
        IToken string = new Token(new TextAttribute(provider.getColor(JavaColorProvider.STRING)));
        IToken comment = new Token(new TextAttribute(provider.getColor(JavaColorProvider.SINGLE_LINE_COMMENT)));
        IToken other = new Token(new TextAttribute(provider.getColor(JavaColorProvider.DEFAULT)));
        IToken geoscriptTok = new Token(new TextAttribute(provider.getColor(JavaColorProvider.DEFAULT), null, SWT.BOLD));
        IToken modulesFieldsTok = new Token(new TextAttribute(provider.getColor(JavaColorProvider.MODULES_FIELDS)));

        List rules = new ArrayList();

        // Add rule for single line comments.
        rules.add(new EndOfLineRule("//", comment)); //$NON-NLS-1$

        // Add rule for strings
        rules.add(new SingleLineRule("\"", "\"", string, '\\')); //$NON-NLS-2$ //$NON-NLS-1$
        // and character constants.
        //        rules.add(new SingleLineRule("'", "'", omsModulesTok, '\\')); //$NON-NLS-2$ //$NON-NLS-1$

        // Add generic whitespace rule.
        rules.add(new WhitespaceRule(new JavaWhitespaceDetector()));

        // Add word rule for keywords, types, and constants.
        WordRule wordRule = new WordRule(new JavaWordDetector(), other);
        for( int i = 0; i < fgKeywords.length; i++ )
            wordRule.addWord(fgKeywords[i], keyword);
        for( int i = 0; i < fgTypes.length; i++ )
            wordRule.addWord(fgTypes[i], type);
        for( int i = 0; i < fgConstants.length; i++ )
            wordRule.addWord(fgConstants[i], type);
        for( int i = 0; i < geoscriptKeywords.length; i++ )
            wordRule.addWord(geoscriptKeywords[i], geoscriptTok);
        for( int i = 0; i < omsComponents.length; i++ ) {
            wordRule.addWord(omsComponents[i], omsComponentsTok);
        }
        for( int i = 0; i < omsSim.length; i++ ) {
            wordRule.addWord(omsSim[i], omsSimTok);
        }
        for( int i = 0; i < omsModel.length; i++ ) {
            wordRule.addWord(omsModel[i], omsModelTok);
        }
        for( String moduleFieldsName : moduleFieldsNameList ) {
            wordRule.addWord(moduleFieldsName, modulesFieldsTok);
        }
        for( String moduleClassesName : moduleClassesNameList ) {
            wordRule.addWord(moduleClassesName, omsModulesTok);
        }

        rules.add(wordRule);

        IRule[] result = new IRule[rules.size()];
        rules.toArray(result);
        setRules(result);
    }
}
