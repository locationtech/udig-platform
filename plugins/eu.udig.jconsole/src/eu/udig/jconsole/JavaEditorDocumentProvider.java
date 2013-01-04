/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.jconsole;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.jface.operation.IRunnableContext;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;

public class JavaEditorDocumentProvider
    extends AbstractDocumentProvider {

	private JavaDocumentSetupParticipant m_documentSetup;

	public JavaEditorDocumentProvider() {

		m_documentSetup = new JavaDocumentSetupParticipant();
	}

	/*
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#createDocument(java.lang.Object)
	 */
    protected IDocument createDocument( Object element )
	    throws CoreException {

		if( element instanceof IEditorInput ) {
			IDocument document = new Document();
			if( setDocumentContent( document, ( IEditorInput )element ) ) {
				setupDocument( document );
			}
			return document;
		}

		return null;
	}

	private boolean setDocumentContent( IDocument document, IEditorInput input )
	    throws CoreException {

		// XXX handle encoding
		Reader reader;
		try {
			if( input instanceof IPathEditorInput )
				reader = new FileReader( ( ( IPathEditorInput )input )
				        .getPath().toFile() );
			else
				reader = new FileReader( input.getName() );
		}
		catch( FileNotFoundException e ) {
			// return empty document and save later
			return true;
		}

		try {
			setDocumentContent( document, reader );
			return true;
		}
		catch( IOException e ) {
			throw new CoreException(
			        new Status(
			                IStatus.ERROR,
			                "org.eclipse.ui.examples.rcp.texteditor", IStatus.OK, "ERROR reading file", e ) ); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void setDocumentContent( IDocument document, Reader reader )
	    throws IOException {

		Reader in = new BufferedReader( reader );
		try {

			StringBuffer buffer = new StringBuffer( 512 );
			char[] readBuffer = new char[ 512 ];
			int n = in.read( readBuffer );
			while( n > 0 ) {
				buffer.append( readBuffer, 0, n );
				n = in.read( readBuffer );
			}

			document.set( buffer.toString() );

		}
		finally {
			in.close();
		}
	}

	/**
	 * Set up the document - default implementation does nothing.
	 * 
	 * @param document
	 *        the new document
	 */
	protected void setupDocument( IDocument document ) {

		m_documentSetup.setup( document );
	}

	/*
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#createAnnotationModel(java.lang.Object)
	 */
	@SuppressWarnings("unused")
    protected IAnnotationModel createAnnotationModel( Object element )
	    throws CoreException {

		return null;
	}

	/*
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#doSaveDocument(org.eclipse.core.runtime.IProgressMonitor,
	 *      java.lang.Object, org.eclipse.jface.text.IDocument, boolean)
	 */
    protected void doSaveDocument( IProgressMonitor monitor, Object element,
	    IDocument document, boolean overwrite )
	    throws CoreException {

		if( element instanceof IPathEditorInput ) {
			IPathEditorInput pei = ( IPathEditorInput )element;
			IPath path = pei.getPath();
			File file = path.toFile();

			try {
				file.createNewFile();

				if( file.exists() ) {
					if( file.canWrite() ) {
						Writer writer = new FileWriter( file );
						writeDocumentContent( document, writer, monitor );
					}
					else {
						// XXX prompt to SaveAs
						throw new CoreException(
						        new Status(
						                IStatus.ERROR,
						                "org.eclipse.ui.examples.rcp.texteditor", IStatus.OK, "file is read-only", null ) ); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				else {
					throw new CoreException(
					        new Status(
					                IStatus.ERROR,
					                "org.eclipse.ui.examples.rcp.texteditor", IStatus.OK, "ERROR creating file", null ) ); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			catch( IOException e ) {
				throw new CoreException(
				        new Status(
				                IStatus.ERROR,
				                "org.eclipse.ui.examples.rcp.texteditor", IStatus.OK, "ERROR when saving file", e ) ); //$NON-NLS-1$ //$NON-NLS-2$
			}

		}
	}

	private void writeDocumentContent( IDocument document, Writer writer,
	    IProgressMonitor monitor )
	    throws IOException {

		Writer out = new BufferedWriter( writer );
		try {
			out.write( document.get() );
		}
		finally {
			out.close();
		}
	}

	/*
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#getOperationRunner(org.eclipse.core.runtime.IProgressMonitor)
	 */
    protected IRunnableContext getOperationRunner( IProgressMonitor monitor ) {

		return null;
	}

	/*
	 * @see org.eclipse.ui.texteditor.IDocumentProviderExtension#isModifiable(java.lang.Object)
	 */	
    public boolean isModifiable( Object element ) {

		if( element instanceof IPathEditorInput ) {
			IPathEditorInput pei = ( IPathEditorInput )element;
			File file = pei.getPath().toFile();
			return file.canWrite() || !file.exists(); // Allow to edit new
			// files
		}
		return false;
	}

	/*
	 * @see org.eclipse.ui.texteditor.IDocumentProviderExtension#isReadOnly(java.lang.Object)
	 */
    public boolean isReadOnly( Object element ) {

		return !isModifiable( element );
	}

	/*
	 * @see org.eclipse.ui.texteditor.IDocumentProviderExtension#isStateValidated(java.lang.Object)
	 */
    public boolean isStateValidated( Object element ) {

		return true;
	}
}
