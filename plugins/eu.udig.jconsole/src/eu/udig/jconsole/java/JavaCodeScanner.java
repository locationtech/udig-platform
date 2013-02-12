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
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;

import eu.udig.jconsole.util.JavaColorProvider;
import eu.udig.jconsole.util.JavaWhitespaceDetector;
import eu.udig.jconsole.util.JavaWordDetector;
import eu.udig.jconsole.util.Keywords;

/**
 * A Java code scanner.
 */
public class JavaCodeScanner extends RuleBasedScanner {

    private static List<String> moduleFieldsNameList = new ArrayList<String>();
    private static List<String> moduleClassesNameList = new ArrayList<String>();

    /**
     * Creates a Java code scanner with the given color provider.
     *
     * @param provider the color provider
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public JavaCodeScanner( JavaColorProvider provider ) {

        // TODO review this if oms modules should be added
        //
        // HashMap<String, List<ModuleDescription>> modulesMap =
        // JConsolePlugin.getDefault().gatherModules();
        // Collection<List<ModuleDescription>> modulesDescriptions = modulesMap.values();
        // for( List<ModuleDescription> modulesDescriptionList : modulesDescriptions ) {
        // for( ModuleDescription moduleDescription : modulesDescriptionList ) {
        // List<FieldData> inputsList = moduleDescription.getInputsList();
        // for( FieldData inFieldData : inputsList ) {
        // moduleFieldsNameList.add(inFieldData.fieldName);
        // }
        // List<FieldData> outputsList = moduleDescription.getOutputsList();
        // for( FieldData outFieldData : outputsList ) {
        // moduleFieldsNameList.add(outFieldData.fieldName);
        // }
        // // String name = moduleDescription.getName();
        // String className = moduleDescription.getClassName();
        // moduleClassesNameList.add(className);
        // }
        // }

        IToken string = new Token(new TextAttribute(provider.getColor(JavaColorProvider.STRING)));
        IToken comment = new Token(new TextAttribute(provider.getColor(JavaColorProvider.SINGLE_LINE_COMMENT)));

        IToken keywordTok = new Token(new TextAttribute(provider.getColor(JavaColorProvider.KEYWORD), null, SWT.BOLD));
        IToken methodTok = new Token(new TextAttribute(provider.getColor(JavaColorProvider.METHOD), null, SWT.ITALIC));
        IToken typeTok = new Token(new TextAttribute(provider.getColor(JavaColorProvider.TYPE)));
        IToken constantsTok = new Token(new TextAttribute(provider.getColor(JavaColorProvider.CONSTANTS)));
        IToken geoscriptTok = new Token(new TextAttribute(provider.getColor(JavaColorProvider.GEOSCRIPT), null, SWT.BOLD));

        IToken other = new Token(new TextAttribute(provider.getColor(JavaColorProvider.DEFAULT)));
        IToken modulesFieldsTok = new Token(new TextAttribute(provider.getColor(JavaColorProvider.MODULES_FIELDS)));
        IToken omsComponentsTok = new Token(new TextAttribute(provider.getColor(JavaColorProvider.OMS_COMPONENTS)));
        IToken omsModulesTok = new Token(new TextAttribute(provider.getColor(JavaColorProvider.OMS_MODULES)));

        List rules = new ArrayList();

        // Add rule for single line comments.
        rules.add(new EndOfLineRule("//", comment)); //$NON-NLS-1$

        // Add rule for gstrings
        rules.add(new MultiLineRule("\"\"\"", "\"\"\"", string, '\\')); //$NON-NLS-2$ //$NON-NLS-1$
        // Add rule for strings
        rules.add(new SingleLineRule("\"", "\"", string, '\\')); //$NON-NLS-2$ //$NON-NLS-1$
        // and character constants.
        rules.add(new SingleLineRule("'", "'", string, '\\')); //$NON-NLS-2$ //$NON-NLS-1$
        //        rules.add(new SingleLineRule("'", "'", omsModulesTok, '\\')); //$NON-NLS-2$ //$NON-NLS-1$

        // Add generic whitespace rule.
        rules.add(new WhitespaceRule(new JavaWhitespaceDetector()));

        // Add word rule for keywords, types, and constants.
        WordRule wordRule = new WordRule(new JavaWordDetector(), other);

        List<String> keywords = Keywords.getValues(Keywords.KEYWORDS);
        for( int i = 0; i < keywords.size(); i++ )
            wordRule.addWord(keywords.get(i), keywordTok);

        List<String> types = Keywords.getValues(Keywords.TYPES);
        for( int i = 0; i < types.size(); i++ )
            wordRule.addWord(types.get(i), typeTok);

        List<String> constants = Keywords.getValues(Keywords.CONSTANTS);
        for( int i = 0; i < constants.size(); i++ )
            wordRule.addWord(constants.get(i), constantsTok);

        List<String> geoscript = Keywords.getValues(Keywords.GEOSCRIPT);
        for( int i = 0; i < geoscript.size(); i++ )
            wordRule.addWord(geoscript.get(i), geoscriptTok);

        List<String> jgtModules = Keywords.getValues(Keywords.JGTMODULES);
        for( int i = 0; i < jgtModules.size(); i++ )
            wordRule.addWord(jgtModules.get(i), geoscriptTok);

        List<String> method = Keywords.getValues(Keywords.METHODS);
        for( int i = 0; i < method.size(); i++ )
            wordRule.addWord(method.get(i), methodTok);

        List<String> jgt_method = Keywords.getValues(Keywords.JGTMETHODS);
        for( int i = 0; i < jgt_method.size(); i++ )
            wordRule.addWord(jgt_method.get(i), methodTok);

        List<String> oms = Keywords.getValues(Keywords.OMS);
        for( int i = 0; i < oms.size(); i++ ) {
            wordRule.addWord(oms.get(i), omsComponentsTok);
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
