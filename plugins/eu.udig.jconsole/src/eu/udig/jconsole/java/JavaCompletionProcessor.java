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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import eu.udig.jconsole.util.Keywords;

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

    protected static String[] singleWordsProposals = null;
    protected static String[] methodWordsProposals = null;

    protected IContextInformationValidator fValidator = new Validator();

    public JavaCompletionProcessor() {

        super();

        if (singleWordsProposals == null) {
            List<String> all = new ArrayList<String>();
            all.addAll(Keywords.getValues(Keywords.GEOSCRIPT));
            all.addAll(Keywords.getValues(Keywords.KEYWORDS));
            all.addAll(Keywords.getValues(Keywords.TYPES));
            all.addAll(Keywords.getValues(Keywords.CONSTANTS));
            all.addAll(Keywords.getValues(Keywords.JGTMODULES));

            singleWordsProposals = all.toArray(new String[0]);
        }
        if (methodWordsProposals == null) {
            List<String> all = new ArrayList<String>();
            List<String> methods = Keywords.getValues(Keywords.METHODS);
            List<String> jgtmethods = Keywords.getValues(Keywords.JGTMETHODS);
            all.addAll(methods);
            all.addAll(jgtmethods);
            methodWordsProposals = all.toArray(new String[0]);
        }
    }

    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public ICompletionProposal[] computeCompletionProposals( ITextViewer viewer, int documentOffset ) {
        // get the word the user is currently writing
        String guessedModelWord = null;
        String guessedMethodWord = null;
        String readWord = null;
        try {
            String text = viewer.getDocument().get(0, documentOffset);
            String[] textSplit = text.split("\\s+"); //$NON-NLS-1$
            readWord = textSplit[textSplit.length - 1];

            String[] split = readWord.split("\\.");
            if (split.length > 0) {
                guessedModelWord = split[0];
                if (split.length != 1) {
                    guessedMethodWord = split[1];
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        List<ICompletionProposal> props = new ArrayList<ICompletionProposal>();

        if (guessedMethodWord != null) {
            List<ICompletionProposal> tmp = new ArrayList<ICompletionProposal>();
            for( int i = 0; i < methodWordsProposals.length; i++ ) {
                String wordProposal = methodWordsProposals[i];
                if (wordProposal.toLowerCase().startsWith(guessedMethodWord.toLowerCase())) {
                    // propose first only those words that start with the letters the user is
                    // writing
                    String replacementString = wordProposal;
                    int replacementOffset = documentOffset - guessedMethodWord.length();
                    int replacementLength = guessedMethodWord.length();
                    int cursorPosition = wordProposal.length();
                    CompletionProposal completionProposal = new CompletionProposal(replacementString, replacementOffset,
                            replacementLength, cursorPosition);
                    props.add(completionProposal);
                } else if (wordProposal.toLowerCase().contains(guessedMethodWord.toLowerCase())) {
                    // propose then those words that contain the letters the user is writing
                    String replacementString = wordProposal;
                    int replacementOffset = documentOffset - guessedMethodWord.length();
                    int replacementLength = guessedMethodWord.length();
                    int cursorPosition = wordProposal.length();
                    CompletionProposal completionProposal = new CompletionProposal(replacementString, replacementOffset,
                            replacementLength, cursorPosition);
                    tmp.add(completionProposal);
                }
            }
            // add second choice
            props.addAll(tmp);
        } else {
            if (readWord.endsWith(".")) {
                // after the dot, propose methods
                for( int i = 0; i < methodWordsProposals.length; i++ ) {
                    String replacementString = methodWordsProposals[i];
                    int replacementOffset = documentOffset;
                    int replacementLength = 0;
                    int cursorPosition = methodWordsProposals[i].length();
                    CompletionProposal completionProposal = new CompletionProposal(replacementString, replacementOffset,
                            replacementLength, cursorPosition);
                    props.add(completionProposal);
                }
            } else {
                List<ICompletionProposal> tmp = new ArrayList<ICompletionProposal>();
                // propose classes and other words
                for( int i = 0; i < singleWordsProposals.length; i++ ) {
                    String wordProposal = singleWordsProposals[i];
                    if (guessedModelWord != null && wordProposal.toLowerCase().startsWith(guessedModelWord.toLowerCase())) {
                        String replacementString = wordProposal;
                        int replacementOffset = documentOffset - guessedModelWord.length();
                        int replacementLength = guessedModelWord.length();
                        int cursorPosition = wordProposal.length();
                        CompletionProposal completionProposal = new CompletionProposal(replacementString, replacementOffset,
                                replacementLength, cursorPosition);
                        props.add(completionProposal);
                    } else if (wordProposal.toLowerCase().contains(guessedModelWord.toLowerCase())) {
                        // propose then those words that contain the letters the user is writing
                        String replacementString = wordProposal;
                        int replacementOffset = documentOffset - guessedModelWord.length();
                        int replacementLength = guessedModelWord.length();
                        int cursorPosition = wordProposal.length();
                        CompletionProposal completionProposal = new CompletionProposal(replacementString, replacementOffset,
                                replacementLength, cursorPosition);
                        tmp.add(completionProposal);
                    }
                }
                props.addAll(tmp);
            }
        }

        return (ICompletionProposal[]) props.toArray(new ICompletionProposal[props.size()]);
    }

    public IContextInformation[] computeContextInformation( ITextViewer viewer, int documentOffset ) {
        // CONTEXT INFO DISABLED FOR NOW
        IContextInformation[] result = new IContextInformation[0];
        // for( int i = 0; i < result.length; i++ ) {
        // result[i] = new ContextInformation(
        // MessageFormat
        //                            .format(JavaEditorMessages.getString("CompletionProcessor.ContextInfo.display.pattern"), new Object[]{new Integer(i), new Integer(documentOffset)}), //$NON-NLS-1$
        // MessageFormat.format(
        //                            JavaEditorMessages.getString("CompletionProcessor.ContextInfo.value.pattern"), new Object[]{new Integer(i), new Integer(documentOffset - 5), new Integer(documentOffset + 5)})); //$NON-NLS-1$
        // }
        return result;
    }

    public char[] getCompletionProposalAutoActivationCharacters() {
        return new char[]{'.', '('};
    }

    public char[] getContextInformationAutoActivationCharacters() {
        // CONTEXT INFO DISABLED FOR NOW
        return new char[]{'#'};
    }

    public IContextInformationValidator getContextInformationValidator() {
        return fValidator;
    }

    public String getErrorMessage() {
        return null;
    }
}
