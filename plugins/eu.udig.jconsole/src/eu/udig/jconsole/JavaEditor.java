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
package eu.udig.jconsole;

import java.io.File;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.internal.ui.UDigByteAndLocalTransfer;
import net.refractions.udig.project.internal.impl.LayerImpl;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.dnd.IDragAndDropService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import eu.udig.omsbox.core.JConsoleOutputConsole;

/**
 * Java specific text editor.
 */
public class JavaEditor extends TextEditor {
    public static final String ID = "eu.udig.jconsole.editor"; //$NON-NLS-1$

    /** The outline page */
    private JavaContentOutlinePage fOutlinePage;
    /** The projection support */
    private ProjectionSupport fProjectionSupport;
    private JConsoleOutputConsole outputConsole;

    private TextTransfer textTransfer;
    private FileTransfer fileTransfer;
    private URLTransfer urlTransfer;
    private UDigByteAndLocalTransfer udigTransfer;

    /**
     * Default constructor.
     */
    public JavaEditor() {
        super();
        setDocumentProvider(new JavaEditorDocumentProvider());
        outputConsole = new JConsoleOutputConsole(null);
    }

    private Process process;

    private String filePath;

    public Process getProcess() {
        return process;
    }
    public void setProcess( Process process ) {
        this.process = process;
    }

    public JConsoleOutputConsole getOutputConsole() {
        return outputConsole;
    }

    /** The <code>JavaEditor</code> implementation of this
     * <code>AbstractTextEditor</code> method extend the
     * actions to add those specific to the receiver
     */
    protected void createActions() {
        super.createActions();
    }

    /** The <code>JavaEditor</code> implementation of this
     * <code>AbstractTextEditor</code> method performs any extra
     * disposal actions required by the java editor.
     */
    public void dispose() {
        ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new IConsole[]{outputConsole});
        if (fOutlinePage != null)
            fOutlinePage.setInput(null);

        super.dispose();
    }

    /** The <code>JavaEditor</code> implementation of this
     * <code>AbstractTextEditor</code> method performs any extra
     * revert behavior required by the java editor.
     */
    public void doRevertToSaved() {
        super.doRevertToSaved();
        if (fOutlinePage != null)
            fOutlinePage.update();
    }

    /** The <code>JavaEditor</code> implementation of this
     * <code>AbstractTextEditor</code> method performs any extra
     * save behavior required by the java editor.
     *
     * @param monitor the progress monitor
     */
    public void doSave( IProgressMonitor monitor ) {
        super.doSave(monitor);
        if (fOutlinePage != null)
            fOutlinePage.update();
    }

    /** The <code>JavaEditor</code> implementation of this
     * <code>AbstractTextEditor</code> method performs any extra
     * save as behavior required by the java editor.
     */
    public void doSaveAs() {
        super.doSaveAs();
        if (fOutlinePage != null)
            fOutlinePage.update();
        // File lastOpenFolder = JConsolePlugin.getDefault().getLastOpenFolder();
        // FileDialog fileDialog = new FileDialog(this.getSite().getShell(), SWT.SAVE);
        // fileDialog.setFilterExtensions(new String[]{"*.jgrass"});
        // fileDialog.setFilterPath(lastOpenFolder.getAbsolutePath());
        // String path = fileDialog.open();
        // if (path == null || path.length() < 1) {
        // return;
        // }
        // File f = new File(path);
        // if (!f.getParentFile().exists()) {
        // return;
        // }

    }

    /** The <code>JavaEditor</code> implementation of this
     * <code>AbstractTextEditor</code> method performs sets the
     * input of the outline page after AbstractTextEditor has set input.
     *
     * @param input the editor input
     * @throws CoreException in case the input can not be set
     */
    public void doSetInput( IEditorInput input ) throws CoreException {
        super.doSetInput(input);
        if (input instanceof JavaFileEditorInput) {
            JavaFileEditorInput javaFile = (JavaFileEditorInput) input;
            filePath = javaFile.getAbsolutePath();
        }

        if (fOutlinePage != null)
            fOutlinePage.setInput(input);
    }

    /*
     * @see org.eclipse.ui.texteditor.ExtendedTextEditor#editorContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
     */
    protected void editorContextMenuAboutToShow( IMenuManager menu ) {
        super.editorContextMenuAboutToShow(menu);
        addAction(menu, "ContentAssistProposal"); //$NON-NLS-1$
        addAction(menu, "ContentAssistTip"); //$NON-NLS-1$
        //        addAction(menu, "DefineFoldingRegion"); //$NON-NLS-1$
    }

    /** The <code>JavaEditor</code> implementation of this
     * <code>AbstractTextEditor</code> method performs gets
     * the java content outline page if request is for a an
     * outline page.
     *
     * @param required the required type
     * @return an adapter for the required type or <code>null</code>
     */
    public Object getAdapter( Class required ) {
        if (IContentOutlinePage.class.equals(required)) {
            if (fOutlinePage == null) {
                fOutlinePage = new JavaContentOutlinePage(getDocumentProvider(), this);
                if (getEditorInput() != null)
                    fOutlinePage.setInput(getEditorInput());
            }
            return fOutlinePage;
        }

        if (fProjectionSupport != null) {
            Object adapter = fProjectionSupport.getAdapter(getSourceViewer(), required);
            if (adapter != null)
                return adapter;
        }

        return super.getAdapter(required);
    }

    /* (non-Javadoc)
     * Method declared on AbstractTextEditor
     */
    protected void initializeEditor() {
        super.initializeEditor();
        setSourceViewerConfiguration(new JavaSourceViewerConfiguration());
    }

    /*
     * @see org.eclipse.ui.texteditor.ExtendedTextEditor#createSourceViewer(org.eclipse.swt.widgets.Composite, org.eclipse.jface.text.source.IVerticalRuler, int)
     */
    protected ISourceViewer createSourceViewer( Composite parent, IVerticalRuler ruler, int styles ) {

        fAnnotationAccess = createAnnotationAccess();
        fOverviewRuler = createOverviewRuler(getSharedColors());

        ISourceViewer viewer = new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);
        // ensure decoration support has been created and configured.
        getSourceViewerDecorationSupport(viewer);

        return viewer;
    }

    /*
     * @see org.eclipse.ui.texteditor.ExtendedTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( Composite parent ) {
        super.createPartControl(parent);
        ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
        fProjectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
        fProjectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.error"); //$NON-NLS-1$
        fProjectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.warning"); //$NON-NLS-1$
        fProjectionSupport.install();
        viewer.doOperation(ProjectionViewer.TOGGLE);

        // MultiPassContentFormatter formatter=
        // new MultiPassContentFormatter(
        // getConfiguredDocumentPartitioning(viewer),
        // IDocument.DEFAULT_CONTENT_TYPE);
        //
        // formatter.setMasterStrategy(
        // new JavaFormattingStrategy());
        // formatter.setSlaveStrategy(
        // new CommentFormattingStrategy(...),
        // IJavaPartitions.JAVA_DOC);
        //

        final IDragAndDropService dndService = (IDragAndDropService) this.getSite().getService(IDragAndDropService.class);
        StyledText st = viewer.getTextWidget();
        textTransfer = TextTransfer.getInstance();
        fileTransfer = FileTransfer.getInstance();
        urlTransfer = URLTransfer.getInstance();
        udigTransfer = UDigByteAndLocalTransfer.getInstance();
        Transfer[] types = new Transfer[]{fileTransfer, textTransfer, urlTransfer, udigTransfer};

        dndService.addMergedDropTarget(st, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT, //
                types, dropTargetListener);
    }

    private DropTargetListener dropTargetListener = new DropTargetAdapter(){
        public void drop( DropTargetEvent event ) {
            if (textTransfer.isSupportedType(event.currentDataType)) {
                String text = (String) event.data;
                System.out.println(text);
            }
            if (fileTransfer.isSupportedType(event.currentDataType)) {
                String[] files = (String[]) event.data;
                if (files.length > 0) {
                    File file = new File(files[0]);
                    if (file.exists()) {
                        pasteDropContent(file);
                        JConsolePlugin.getDefault().setLastOpenFolder(file.getParentFile().getAbsolutePath());
                    }
                }
            }
            if (urlTransfer.isSupportedType(event.currentDataType)) {
                Object data2 = event.data;
                System.out.println(data2);
            }
            if (udigTransfer.isSupportedType(event.currentDataType)) {
                try {
                    Object data = event.data;
                    if (data instanceof TreeSelection) {
                        TreeSelection selection = (TreeSelection) data;
                        Object firstElement = selection.getFirstElement();

                        IGeoResource geoResource = null;
                        if (firstElement instanceof LayerImpl) {
                            LayerImpl layer = (LayerImpl) firstElement;
                            geoResource = layer.getGeoResource();

                        }
                        if (firstElement instanceof IService) {
                            IService service = (IService) firstElement;
                            List< ? extends IGeoResource> resources = service.resources(new NullProgressMonitor());
                            if (resources.size() > 0) {
                                geoResource = resources.get(0);
                            }
                        }
                        if (geoResource != null) {
                            ID id = geoResource.getID();
                            if (id != null)
                                if (id.isFile()) {
                                    File file = id.toFile();
                                    if (file.exists()) {
                                        pasteDropContent(file);
                                        JConsolePlugin.getDefault().setLastOpenFolder(file.getParentFile().getAbsolutePath());
                                    }
                                } else if (id.toString().contains("#") && id.toString().startsWith("file")) {
                                    // try to get the file
                                    String string = id.toString().replaceAll("#", "");
                                    URL url = new URL(string);
                                    File file = new File(url.toURI());
                                    if (file.exists()) {
                                        pasteDropContent(file);
                                        JConsolePlugin.getDefault().setLastOpenFolder(file.getParentFile().getAbsolutePath());
                                    }
                                } else {
                                    System.out.println("Not a file: " + id.toString());
                                }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
    };

    private void pasteDropContent( File file ) {
        String path = file.getAbsolutePath();
        try {
            IDocumentProvider dp = getDocumentProvider();
            IDocument doc = dp.getDocument(getEditorInput());
            // int numberOfLines = doc.getNumberOfLines() - 1;
            // int offset = doc.getLineOffset(numberOfLines);

            ISelectionProvider selectionProvider = getSelectionProvider();
            ISelection selection = selectionProvider.getSelection();
            if (selection instanceof ITextSelection) {
                ITextSelection textSelection = (ITextSelection) selection;
                int offset = textSelection.getOffset();
                doc.replace(offset, 0, path);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /*
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#adjustHighlightRange(int, int)
     */
    protected void adjustHighlightRange( int offset, int length ) {
        ISourceViewer viewer = getSourceViewer();
        if (viewer instanceof ITextViewerExtension5) {
            ITextViewerExtension5 extension = (ITextViewerExtension5) viewer;
            extension.exposeModelRange(new Region(offset, length));
        }
    }
}
