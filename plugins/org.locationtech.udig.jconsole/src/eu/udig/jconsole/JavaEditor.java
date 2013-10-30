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
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.internal.ui.UDigByteAndLocalTransfer;
import net.refractions.udig.project.internal.impl.LayerImpl;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.dnd.IDragAndDropService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.joda.time.DateTime;

import eu.udig.jconsole.util.ImageCache;
import eu.udig.jconsole.util.Keywords;
import eu.udig.omsbox.OmsBoxPlugin;
import eu.udig.omsbox.core.IProcessListener;
import eu.udig.omsbox.core.JConsoleOutputConsole;
import eu.udig.omsbox.core.OmsScriptExecutor;
import eu.udig.omsbox.ui.RunningProcessListDialog;
import eu.udig.omsbox.utils.OmsBoxConstants;

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

    private static final String CONTENTASSIST_PROPOSAL_ID = "eu.udig.jconsole.java.JavaCompletionProcessor";

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

        ResourceBundle bundle = JavaEditorMessages.getResourceBundle();
        // This action will fire a CONTENTASSIST_PROPOSALS operation
        // when executed
        IAction action = new TextOperationAction(//
                bundle, "ContentAssistProposal", this, SourceViewer.CONTENTASSIST_PROPOSALS);
        action.setActionDefinitionId(CONTENTASSIST_PROPOSAL_ID);
        // Tell the editor about this new action
        setAction(CONTENTASSIST_PROPOSAL_ID, action);
        // Tell the editor to execute this action
        // when Ctrl+Spacebar is pressed
        setActionActivationCode(CONTENTASSIST_PROPOSAL_ID, ' ', -1, SWT.CTRL);
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
        Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout mainLayout = new GridLayout(1, false);
        mainLayout.marginHeight = 0;
        mainLayout.marginWidth = 0;
        mainComposite.setLayout(mainLayout);

        addEditorActions(mainComposite);

        Composite editorComposite = new Composite(mainComposite, SWT.BORDER);
        editorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        FillLayout editorLayout = new FillLayout();
        editorLayout.marginHeight = 0;
        editorLayout.marginWidth = 0;
        editorComposite.setLayout(editorLayout);

        super.createPartControl(editorComposite);

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

    private void addEditorActions( Composite mainComposite ) {
        Composite buttonsComposite = new Composite(mainComposite, SWT.NONE);
        buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        GridLayout buttonsLayout = new GridLayout(6, false);
        buttonsLayout.marginTop = 1;
        buttonsLayout.marginBottom = 0;
        buttonsLayout.marginWidth = 1;
        buttonsComposite.setLayout(buttonsLayout);

        Button startButton = new Button(buttonsComposite, SWT.PUSH);
        startButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        startButton.setToolTipText("Start the current script");
        startButton.setImage(ImageCache.getInstance().getImage(ImageCache.START));
        startButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                startScript();
            }
        });
        Button stopButton = new Button(buttonsComposite, SWT.PUSH);
        stopButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        stopButton.setToolTipText("Stop a running script");
        stopButton.setImage(ImageCache.getInstance().getImage(ImageCache.STOP));
        stopButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                stopScript();
            }
        });
        Button templateButton = new Button(buttonsComposite, SWT.PUSH);
        templateButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        templateButton.setToolTipText("Insert commonly used imports");
        templateButton.setImage(ImageCache.getInstance().getImage(ImageCache.TEMPLATE));
        templateButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                insertTemplates();
            }
        });

        Label spacer = new Label(buttonsComposite, SWT.NONE);
        spacer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Combo heapCombo = new Combo(buttonsComposite, SWT.DROP_DOWN);
        heapCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        heapCombo.setItems(OmsBoxConstants.HEAPLEVELS);
        heapCombo.setToolTipText("Memory [MB]");
        int savedHeapLevel = OmsBoxPlugin.getDefault().retrieveSavedHeap();
        for( int i = 0; i < OmsBoxConstants.HEAPLEVELS.length; i++ ) {
            if (OmsBoxConstants.HEAPLEVELS[i].equals(String.valueOf(savedHeapLevel))) {
                heapCombo.select(i);
                break;
            }
        }
        heapCombo.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                String item = heapCombo.getText();
                OmsBoxPlugin.getDefault().saveHeap(Integer.parseInt(item));
            }
        });
        heapCombo.addModifyListener(new ModifyListener(){
            public void modifyText( ModifyEvent e ) {
                String item = heapCombo.getText();
                try {
                    Integer.parseInt(item);
                } catch (Exception ex) {
                    return;
                }
                if (item.length() > 0) {
                    OmsBoxPlugin.getDefault().saveHeap(Integer.parseInt(item));
                }
            }
        });

        final Combo logCombo = new Combo(buttonsComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        logCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        logCombo.setItems(OmsBoxConstants.LOGLEVELS_GUI);
        logCombo.setToolTipText("Enable/disable logging");
        String savedLogLevel = OmsBoxPlugin.getDefault().retrieveSavedLogLevel();
        for( int i = 0; i < OmsBoxConstants.LOGLEVELS_GUI.length; i++ ) {
            if (OmsBoxConstants.LOGLEVELS_GUI[i].equals(savedLogLevel)) {
                logCombo.select(i);
                break;
            }
        }
        logCombo.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                String item = logCombo.getText();
                OmsBoxPlugin.getDefault().saveLogLevel(item);
            }
        });

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

    private void insertTemplates() {
        IDocument doc = getDocumentProvider().getDocument(getEditorInput());
        String text = doc.get();
        StringBuilder sb = new StringBuilder();
        List<String> values = Keywords.getValues(Keywords.IMPORTS);
        for( String value : values ) {
            sb.append(value).append("\n");
        }

        StringBuilder finalSb = new StringBuilder();
        finalSb.append(sb.toString());
        finalSb.append("\n");
        finalSb.append(text);

        doc.set(finalSb.toString());

    }

    private void stopScript() {
        HashMap<String, Process> runningProcessesMap = OmsBoxPlugin.getDefault().getRunningProcessesMap();

        Shell shell = getEditorSite().getShell();
        if (runningProcessesMap.size() == 0) {
            MessageDialog.openInformation(shell, "Process List", "No running processes available at the current time");
        } else {
            RunningProcessListDialog dialog = new RunningProcessListDialog();
            dialog.open(shell, SWT.MULTI);
        }
    }

    private void startScript() {
        IDocument doc = getDocumentProvider().getDocument(getEditorInput());
        // JConsoleOutputConsole outputConsole = getOutputConsole();
        // outputConsole.clearConsole();

        String text = null;
        ISelection selection = getSelectionProvider().getSelection();
        if (selection instanceof ITextSelection) {
            ITextSelection textSelection = (ITextSelection) selection;
            if (!textSelection.isEmpty()) {
                text = textSelection.getText();

                if (text.trim().length() > 0 && !text.trim().startsWith("import")) {
                    // something in the middle was selected, we need to add the imports
                    StringBuilder sb = new StringBuilder();
                    List<String> defaultImports = Keywords.getValues(Keywords.IMPORTS);
                    for( String defaultImport : defaultImports ) {
                        sb.append(defaultImport).append("\n");
                    }
                    sb.append(text);
                    text = sb.toString();
                }
            }
        }
        if (text == null || 0 >= text.length()) {
            text = doc.get();
        }

        String dateTimeString = new DateTime().toString(OmsBoxConstants.dateTimeFormatterYYYYMMDDHHMMSS);

        String title = getTitle();
        JConsoleOutputConsole outputConsole = new JConsoleOutputConsole("Script: " + title + " (" + dateTimeString + " )");
        outputConsole.clearConsole();

        PrintStream internalStream = outputConsole.internal;
        // PrintStream outputStream = outputConsole.out;
        PrintStream errorStream = outputConsole.err;
        // open console
        IConsoleManager manager = org.eclipse.ui.console.ConsolePlugin.getDefault().getConsoleManager();
        manager.addConsoles(new IConsole[]{outputConsole});
        manager.showConsoleView(outputConsole);

        try {
            final String scriptID = "geoscript_" + dateTimeString;
            OmsScriptExecutor executor = new OmsScriptExecutor();
            String loggerLevelGui = OmsBoxPlugin.getDefault().retrieveSavedLogLevel();
            String ramLevel = String.valueOf(OmsBoxPlugin.getDefault().retrieveSavedHeap());
            executor.addProcessListener(new IProcessListener(){
                public void onProcessStopped() {
                    OmsBoxPlugin.getDefault().cleanProcess(scriptID);
                }
            });
            Process process = executor.exec(text, internalStream, errorStream, loggerLevelGui, ramLevel);
            OmsBoxPlugin.getDefault().addProcess(process, scriptID);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
