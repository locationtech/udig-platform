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
package eu.hydrologis.jgrass.jconsole;


import eu.hydrologis.jgrass.jconsole.java.JavaAutoIndentStrategy;
import eu.hydrologis.jgrass.jconsole.java.JavaCompletionProcessor;
import eu.hydrologis.jgrass.jconsole.java.JavaDoubleClickSelector;
import eu.hydrologis.jgrass.jconsole.javadoc.JavaDocCompletionProcessor;
import eu.hydrologis.jgrass.jconsole.util.JavaColorProvider;

import org.eclipse.swt.graphics.RGB;

import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;


/**
 * Example configuration for an <code>SourceViewer</code> which shows Java code.
 */
public class JavaSourceViewerConfiguration extends SourceViewerConfiguration {


		/**
		 * Single token scanner.
		 */
		static class SingleTokenScanner extends BufferedRuleBasedScanner {
			public SingleTokenScanner(TextAttribute attribute) {
				setDefaultReturnToken(new Token(attribute));
			}
		}


	/**
	 * Default constructor.
	 */
	public JavaSourceViewerConfiguration() {
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new JavaAnnotationHover();
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		IAutoEditStrategy strategy= (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType) ? new JavaAutoIndentStrategy() : new DefaultIndentLineAutoEditStrategy());
		return new IAutoEditStrategy[] { strategy };
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredDocumentPartitioning(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		return JConsolePlugin.JAVA_PARTITIONING;
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, JavaPartitionScanner.JAVA_DOC, JavaPartitionScanner.JAVA_MULTILINE_COMMENT };
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

		ContentAssistant assistant= new ContentAssistant();
		assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		assistant.setContentAssistProcessor(new JavaCompletionProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setContentAssistProcessor(new JavaDocCompletionProcessor(), JavaPartitionScanner.JAVA_DOC);

		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(500);
		assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
		assistant.setContextInformationPopupBackground(JConsolePlugin.getDefault().getJavaColorProvider().getColor(new RGB(150, 150, 0)));

		return assistant;
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		return new JavaDoubleClickSelector();
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
		return new String[] { "\t", "    " }; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

		JavaColorProvider provider= JConsolePlugin.getDefault().getJavaColorProvider();
		PresentationReconciler reconciler= new PresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr= new DefaultDamagerRepairer(JConsolePlugin.getDefault().getJavaCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr= new DefaultDamagerRepairer(JConsolePlugin.getDefault().getJavaDocScanner());
		reconciler.setDamager(dr, JavaPartitionScanner.JAVA_DOC);
		reconciler.setRepairer(dr, JavaPartitionScanner.JAVA_DOC);

		dr= new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(provider.getColor(JavaColorProvider.MULTI_LINE_COMMENT))));
		reconciler.setDamager(dr, JavaPartitionScanner.JAVA_MULTILINE_COMMENT);
		reconciler.setRepairer(dr, JavaPartitionScanner.JAVA_MULTILINE_COMMENT);

		return reconciler;
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public int getTabWidth(ISourceViewer sourceViewer) {
		return 4;
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		return new JavaTextHover();
	}
}
