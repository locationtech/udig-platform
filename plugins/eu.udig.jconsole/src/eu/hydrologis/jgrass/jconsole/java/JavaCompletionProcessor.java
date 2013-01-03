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
package eu.hydrologis.jgrass.jconsole.java;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.jgrasstools.gears.libs.modules.ClassField;

import eu.hydrologis.jgrass.jconsole.jgrasstools.JGrassTools;

/**
 * Example Java completion processor.
 */
public class JavaCompletionProcessor implements IContentAssistProcessor {

    /**
     * Simple content assist tip closer. The tip is valid in a range
     * of 5 characters around its popup location.
     */
    protected static class Validator implements IContextInformationValidator, IContextInformationPresenter {

        protected int fInstallOffset;

        /*
         * @see IContextInformationValidator#isContextInformationValid(int)
         */
        public boolean isContextInformationValid( int offset ) {
            return Math.abs(fInstallOffset - offset) < 5;
        }

        /*
         * @see IContextInformationValidator#install(IContextInformation, ITextViewer, int)
         */
        public void install( IContextInformation info, ITextViewer viewer, int offset ) {
            fInstallOffset = offset;
        }

        /*
         * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#updatePresentation(int, TextPresentation)
         */
        public boolean updatePresentation( int documentPosition, TextPresentation presentation ) {
            return false;
        }
    }

    protected static String[] fgProposals = null;

    protected IContextInformationValidator fValidator = new Validator();

    public JavaCompletionProcessor() {

        super();

        if (fgProposals == null) {
            fgProposals = JGrassTools.getInstance().getAllFields();
        }
    }

    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public ICompletionProposal[] computeCompletionProposals( ITextViewer viewer, int documentOffset ) {
        // get the word the user is currently writing
        String guessedModelWord = null;
        String guessedFieldWord = null;
        String readWord = null;
        try {
            String text = viewer.getDocument().get(0, documentOffset);
            String[] textSplit = text.split("\\s+|'"); //$NON-NLS-1$
            readWord = textSplit[textSplit.length - 1];

            String[] split = readWord.split("\\.");
            guessedModelWord = split[0];
            if (split.length != 1) {
                guessedFieldWord = split[1];
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        List<ICompletionProposal> props = new ArrayList<ICompletionProposal>();
        LinkedHashMap<String, List<ClassField>> moduleName2Fields = JGrassTools.getInstance().getModuleName2Fields();
        Set<Entry<String, List<ClassField>>> entrySet = moduleName2Fields.entrySet();
        /*
         * if the module name is named the same as the
         * class, supply only its fields. 
         */

        for( Entry<String, List<ClassField>> module : entrySet ) {
            String moduleName = module.getKey();
            if (guessedModelWord.toLowerCase().matches(moduleName.toLowerCase() + "[0-9]*")) {
                List<ClassField> fieldsList = module.getValue();

                for( ClassField field : fieldsList ) {
                    String fieldName = field.fieldName;
                    if (guessedFieldWord != null && fieldName.startsWith(guessedFieldWord)) {
                        /*
                        * module. situation, fieldname == null
                        */
                        props.add(new CompletionProposal(fieldName, documentOffset - guessedFieldWord.length(), guessedFieldWord
                                .length(), fieldName.length()));
                    } else if (guessedFieldWord == null) {
                        props.add(new CompletionProposal(fieldName, documentOffset - 0, 0, fieldName.length()));
                    }

                }
                return (ICompletionProposal[]) props.toArray(new ICompletionProposal[props.size()]);
            }
        }

        for( int i = 0; i < fgProposals.length; i++ ) {
            // pass only those words that start with the letters the user is writing
            if (guessedFieldWord != null && fgProposals[i].startsWith(guessedFieldWord)) {
                // if (guessedFieldWord == null || fgProposals[i].startsWith(guessedFieldWord)
                // || guessedModelWord.endsWith(".")){
                props.add(new CompletionProposal(fgProposals[i], documentOffset - guessedFieldWord.length(), guessedFieldWord
                        .length(), fgProposals[i].length()));
                // props.add(new CompletionProposal(m_proposals[i], documentOffset -
                // myWord.length(),
                // myWord.length(), m_proposals[i].length()));
            } else if (readWord.endsWith(".")) {
                props.add(new CompletionProposal(fgProposals[i], documentOffset, 0, fgProposals[i].length()));
            }
        }
        return (ICompletionProposal[]) props.toArray(new ICompletionProposal[props.size()]);
    }
    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public IContextInformation[] computeContextInformation( ITextViewer viewer, int documentOffset ) {
        IContextInformation[] result = new IContextInformation[5];
        for( int i = 0; i < result.length; i++ )
            result[i] = new ContextInformation(
                    MessageFormat
                            .format(
                                    JavaEditorMessages.getString("CompletionProcessor.ContextInfo.display.pattern"), new Object[]{new Integer(i), new Integer(documentOffset)}), //$NON-NLS-1$
                    MessageFormat
                            .format(
                                    JavaEditorMessages.getString("CompletionProcessor.ContextInfo.value.pattern"), new Object[]{new Integer(i), new Integer(documentOffset - 5), new Integer(documentOffset + 5)})); //$NON-NLS-1$
        return result;
    }

    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public char[] getCompletionProposalAutoActivationCharacters() {
        return new char[]{'.', '('};
    }

    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public char[] getContextInformationAutoActivationCharacters() {
        return new char[]{'#'};
    }

    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public IContextInformationValidator getContextInformationValidator() {
        return fValidator;
    }

    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public String getErrorMessage() {
        return null;
    }
}
