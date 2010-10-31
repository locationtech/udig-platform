package eu.udig.style.advanced.points;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.refractions.udig.style.sld.SLD;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.geotools.styling.Font;
import org.geotools.styling.TextSymbolizer;

import com.sun.xml.internal.ws.util.UtilException;

import eu.udig.style.advanced.StylePlugin;
import eu.udig.style.advanced.common.FiltersComposite;
import eu.udig.style.advanced.common.IStyleChangesListener;
import eu.udig.style.advanced.common.styleattributeclasses.PointSymbolizerWrapper;
import eu.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import eu.udig.style.advanced.common.styleattributeclasses.SymbolizerWrapper;
import eu.udig.style.advanced.common.styleattributeclasses.TextSymbolizerWrapper;
import eu.udig.style.advanced.points.widgets.PointBoderParametersComposite;
import eu.udig.style.advanced.points.widgets.PointFillParametersComposite;
import eu.udig.style.advanced.points.widgets.PointGeneralParametersComposite;
import eu.udig.style.advanced.points.widgets.PointLabelsParametersComposite;
import eu.udig.style.advanced.utils.Utilities;
import static eu.udig.style.advanced.utils.Utilities.*;

@SuppressWarnings("nls")
public class PointPropertiesComposite extends SelectionAdapter implements ModifyListener, IStyleChangesListener {

    private static final String[] POINT_STYLE_TYPES = {"Simple Style", "Graphics Based Style"};

    private static final String[] WK_MARK_NAMES = wkMarkNames;
    private RuleWrapper ruleWrapper;

    private Composite simplePointComposite = null;
    private Composite graphicsPointComposite = null;
    private PointPropertiesEditor pointPropertiesEditor;
    private Composite mainComposite;
    private StackLayout mainStackLayout;

    private Combo wknMarksCombo;

    private String[] numericAttributesArrays;
    private String[] allAttributesArrays;
    private Text graphicsPathText;

    private Composite parentComposite;

    private Combo styleTypecombo;

    private final Composite parent;

    private PointGeneralParametersComposite generalParametersCompositeSIMPLE;

    private PointGeneralParametersComposite generalParametersCompositeGRAPHICS;

    private PointBoderParametersComposite borderParametersComposite;

    private PointFillParametersComposite fillParametersComposite;

    private PointLabelsParametersComposite labelsParametersComposite;
    
    private FiltersComposite filtersComposite;

    public PointPropertiesComposite( final PointPropertiesEditor pointPropertiesEditor, Composite parent ) {
        this.pointPropertiesEditor = pointPropertiesEditor;
        this.parent = parent;
    }

    public void setRule( RuleWrapper ruleWrapper ) {
        this.ruleWrapper = ruleWrapper;

        System.out.println("setting rule: " + ruleWrapper.getName());

        if (mainComposite == null) {
            init();
            if (simplePointComposite == null) {
                createSimpleComposite();
            }
            if (graphicsPointComposite == null) {
                createGraphicsComposite();
            }
        } else {
            update();
        }
        setRightPanel();
    }

    private void update() {
        SymbolizerWrapper geometrySymbolizersWrapper = ruleWrapper.getGeometrySymbolizersWrapper();
        PointSymbolizerWrapper pointSymbolizerWrapper = geometrySymbolizersWrapper.adapt(PointSymbolizerWrapper.class);
        
        filtersComposite.update(ruleWrapper);
        
        if (!pointSymbolizerWrapper.hasExternalGraphic()) {
            generalParametersCompositeSIMPLE.update(ruleWrapper);
            borderParametersComposite.update(ruleWrapper);
            fillParametersComposite.update(ruleWrapper);
            labelsParametersComposite.update(ruleWrapper);

            // mark
            String markName = pointSymbolizerWrapper.getMarkName();
            if (markName == null) {
                markName = WK_MARK_NAMES[0];
            }
            for( int i = 0; i < WK_MARK_NAMES.length; i++ ) {
                if (markName.equalsIgnoreCase(WK_MARK_NAMES[i])) {
                    wknMarksCombo.removeSelectionListener(this);
                    wknMarksCombo.select(i);
                    wknMarksCombo.addSelectionListener(this);
                    break;
                }
            }
        } else {
            generalParametersCompositeGRAPHICS.update(ruleWrapper);

            // external graphics path
            graphicsPathText.removeModifyListener(this);
            try {
                graphicsPathText.setText(pointSymbolizerWrapper.getExternalGraphicPath());
            } catch (MalformedURLException e) {
                graphicsPathText.setText("");
            }
            graphicsPathText.addModifyListener(this);
        }

        pointPropertiesEditor.refreshTreeViewer(ruleWrapper);
        pointPropertiesEditor.refreshPreviewCanvasOnStyle();
    }

    private void init() {
        // System.out.println("open: " + rule.getName());
        final PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);

        List<String> numericAttributeNames = pointPropertiesEditor.getNumericAttributeNames();
        numericAttributesArrays = (String[]) numericAttributeNames.toArray(new String[numericAttributeNames.size()]);
        List<String> allAttributeNames = pointPropertiesEditor.getAllAttributeNames();
        allAttributesArrays = (String[]) allAttributeNames.toArray(new String[allAttributeNames.size()]);
        // geometryPropertyName = pointPropertiesEditor.getGeometryPropertyName().getLocalPart();

        parentComposite = new Composite(parent, SWT.NONE);
        parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        parentComposite.setLayout(new GridLayout(1, false));

        styleTypecombo = new Combo(parentComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        styleTypecombo.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
        styleTypecombo.setItems(POINT_STYLE_TYPES);
        styleTypecombo.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                int selectionIndex = styleTypecombo.getSelectionIndex();
                if (selectionIndex == 0) {
                    int index = wknMarksCombo.getSelectionIndex();

                    String markName = wknMarksCombo.getItem(index);
                    pointSymbolizerWrapper.setMarkName(markName);

                    mainStackLayout.topControl = simplePointComposite;

                    generalParametersCompositeSIMPLE.update(ruleWrapper);

                    pointPropertiesEditor.refreshTreeViewer(ruleWrapper);
                    pointPropertiesEditor.refreshPreviewCanvasOnStyle();
                } else {
                    try {
                        URL iconUrl = Platform.getBundle(StylePlugin.PLUGIN_ID).getResource("icons/delete.png");
                        String iconPath = "";
                        try {
                            iconPath = FileLocator.toFileURL(iconUrl).getPath();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        pointSymbolizerWrapper.setExternalGraphicPath(iconPath);
                        graphicsPathText.setText(iconPath);
                    } catch (MalformedURLException e1) {
                        // can't happen
                    }
                    mainStackLayout.topControl = graphicsPointComposite;

                    generalParametersCompositeGRAPHICS.update(ruleWrapper);

                    pointPropertiesEditor.refreshTreeViewer(ruleWrapper);
                    pointPropertiesEditor.refreshPreviewCanvasOnStyle();
                }
                mainComposite.layout();
            }
        });

        mainComposite = new Composite(parentComposite, SWT.NONE);
        mainStackLayout = new StackLayout();
        mainComposite.setLayout(mainStackLayout);
        GridData mainCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        mainComposite.setLayoutData(mainCompositeGD);
        // mainStackLayout.topControl = l;
    }

    private void setRightPanel() {
        final PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);
        boolean hasExt = pointSymbolizerWrapper.hasExternalGraphic();
        if (!hasExt) {
            styleTypecombo.select(0);
            mainStackLayout.topControl = simplePointComposite;
        } else {
            styleTypecombo.select(1);
            mainStackLayout.topControl = graphicsPointComposite;
        }
        mainComposite.layout();
    }

    public Composite getComposite() {
        return parentComposite;
    }

    private void createSimpleComposite() {
        final PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);

        simplePointComposite = new Composite(mainComposite, SWT.None);
        simplePointComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        simplePointComposite.setLayout(new GridLayout(1, false));

        // rule name
        Composite nameComposite = new Composite(simplePointComposite, SWT.NONE);
        nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        nameComposite.setLayout(new GridLayout(2, true));

        // well known marks
        wknMarksCombo = new Combo(simplePointComposite, SWT.DROP_DOWN);
        GridData wknMarksComboGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        wknMarksCombo.setLayoutData(wknMarksComboGD);
        wknMarksCombo.setItems(WK_MARK_NAMES);
        String markName = pointSymbolizerWrapper.getMarkName();
        if (markName == null) {
            // use a default
            markName = WK_MARK_NAMES[0];
        }
        for( int i = 0; i < WK_MARK_NAMES.length; i++ ) {
            if (markName.equalsIgnoreCase(WK_MARK_NAMES[i])) {
                wknMarksCombo.select(i);
                break;
            }
        }
        wknMarksCombo.addSelectionListener(this);

        // use an expandbar for the properties
        Group propertiesGroup = new Group(simplePointComposite, SWT.SHADOW_ETCHED_IN);
        propertiesGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        propertiesGroup.setLayout(new GridLayout(1, false));
        propertiesGroup.setText("Style Properties");

        TabFolder tabFolder = new TabFolder(propertiesGroup, SWT.BORDER);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        generalParametersCompositeSIMPLE = new PointGeneralParametersComposite(tabFolder, numericAttributesArrays);
        generalParametersCompositeSIMPLE.init(ruleWrapper);
        generalParametersCompositeSIMPLE.addListener(this);
        Composite generalParametersInternalComposite = generalParametersCompositeSIMPLE.getComposite();

        TabItem tabItem1 = new TabItem(tabFolder, SWT.NULL);
        tabItem1.setText("General");
        tabItem1.setControl(generalParametersInternalComposite);

        // BORDER GROUP
        borderParametersComposite = new PointBoderParametersComposite(tabFolder, numericAttributesArrays);
        borderParametersComposite.init(ruleWrapper);
        borderParametersComposite.addListener(this);
        Composite borderParametersInternalComposite = borderParametersComposite.getComposite();

        TabItem tabItem2 = new TabItem(tabFolder, SWT.NULL);
        tabItem2.setText("Border  ");
        tabItem2.setControl(borderParametersInternalComposite);

        // Fill GROUP
        fillParametersComposite = new PointFillParametersComposite(tabFolder, numericAttributesArrays);
        fillParametersComposite.init(ruleWrapper);
        fillParametersComposite.addListener(this);
        Composite fillParametersInternalComposite = fillParametersComposite.getComposite();

        TabItem tabItem3 = new TabItem(tabFolder, SWT.NULL);
        tabItem3.setText("Fill   ");
        tabItem3.setControl(fillParametersInternalComposite);

        // Label GROUP
        labelsParametersComposite = new PointLabelsParametersComposite(tabFolder, numericAttributesArrays, allAttributesArrays);
        labelsParametersComposite.init(ruleWrapper);
        labelsParametersComposite.addListener(this);
        Composite labelParametersInternalComposite = labelsParametersComposite.getComposite();

        TabItem tabItem4 = new TabItem(tabFolder, SWT.NULL);
        tabItem4.setText("Labels  ");
        tabItem4.setControl(labelParametersInternalComposite);
        
        // Filter GROUP
        filtersComposite = new FiltersComposite(tabFolder);
        filtersComposite.init(ruleWrapper);
        filtersComposite.addListener(this);
        Composite filtersInternalComposite = filtersComposite.getComposite();

        TabItem tabItem5 = new TabItem(tabFolder, SWT.NULL);
        tabItem5.setText("Filter  ");
        tabItem5.setControl(filtersInternalComposite);


    }

    private void createGraphicsComposite() {
        final PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);

        graphicsPointComposite = new Composite(mainComposite, SWT.None);
        graphicsPointComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        graphicsPointComposite.setLayout(new GridLayout(1, false));

        // rule name
        Composite nameComposite = new Composite(graphicsPointComposite, SWT.NONE);
        nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        nameComposite.setLayout(new GridLayout(2, true));

        // external graphics path
        Group pathGroup = new Group(graphicsPointComposite, SWT.NONE);
        pathGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        pathGroup.setLayout(new GridLayout(2, false));
        pathGroup.setText("Graphics path");
        graphicsPathText = new Text(pathGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        graphicsPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        try {
            graphicsPathText.setText(pointSymbolizerWrapper.getExternalGraphicPath());
        } catch (MalformedURLException e1) {
            graphicsPathText.setText("");
        }
        graphicsPathText.addModifyListener(this);
        Button pathButton = new Button(pathGroup, SWT.PUSH);
        pathButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        pathButton.setText("...");
        pathButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                FileDialog fileDialog = new FileDialog(graphicsPathText.getShell(), SWT.OPEN);
                String path = fileDialog.open();
                if (path == null || path.length() < 1) {
                    graphicsPathText.setText("");
                } else {
                    graphicsPathText.setText(path);
                }
            }
        });

        // rule name, size, rotation, offset, zoomlevels group
        Group genericsGroup = new Group(graphicsPointComposite, SWT.NONE);
        genericsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        genericsGroup.setLayout(new GridLayout(1, true));
        genericsGroup.setText("General properties");

        generalParametersCompositeGRAPHICS = new PointGeneralParametersComposite(genericsGroup, numericAttributesArrays);
        generalParametersCompositeGRAPHICS.init(ruleWrapper);
        generalParametersCompositeGRAPHICS.addListener(this);
    }

    @Override
    public void widgetSelected( SelectionEvent e ) {
        Object source = e.getSource();
        System.out.println(ruleWrapper.getName());

        PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);

        if (source.equals(wknMarksCombo)) {
            int selectionIndex = wknMarksCombo.getSelectionIndex();
            String item = wknMarksCombo.getItem(selectionIndex);
            pointSymbolizerWrapper.setMarkName(item);
            pointPropertiesEditor.refreshTreeViewer(ruleWrapper);
            pointPropertiesEditor.refreshPreviewCanvasOnStyle();
            return;
        }

    }

    public void modifyText( ModifyEvent e ) {
        Object source = e.getSource();
        if (source.equals(graphicsPathText)) {
            try {
                setNewGraphicPath();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
                return;
            }
        }
        pointPropertiesEditor.refreshTreeViewer(ruleWrapper);
        pointPropertiesEditor.refreshPreviewCanvasOnStyle();
    }

    private void setNewGraphicPath() throws MalformedURLException {
        String path = graphicsPathText.getText();
        PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);
        pointSymbolizerWrapper.setExternalGraphicPath(path);
    }

    public void onStyleChanged( Object source, String[] values, boolean fromField, STYLEEVENTTYPE styleEventType ) {
        String value = values[0];

        PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);
        TextSymbolizerWrapper textSymbolizerWrapper = ruleWrapper.getTextSymbolizersWrapper();

        switch( styleEventType ) {
        // GENERAL PARAMETERS
        case NAME:
            ruleWrapper.setName(value);
            break;
        case SIZE:
            pointSymbolizerWrapper.setSize(value, fromField);
            break;
        case ROTATION:
            pointSymbolizerWrapper.setRotation(value, fromField);
            break;
        case OFFSET:
            pointSymbolizerWrapper.setOffset(value);
            break;
        case MAXSCALE:
            ruleWrapper.setMaxScale(value);
            break;
        case MINSCALE:
            ruleWrapper.setMinScale(value);
            break;
        // BORDER PARAMETERS
        case BORDERENABLE: {
            boolean enabled = Boolean.parseBoolean(value);
            pointSymbolizerWrapper.setHasStroke(enabled);
            break;
        }
        case BORDERWIDTH: {
            pointSymbolizerWrapper.setStrokeWidth(value, fromField);
            break;
        }
        case BORDERCOLOR: {
            pointSymbolizerWrapper.setStrokeColor(value);
            break;
        }
        case BORDEROPACITY: {
            pointSymbolizerWrapper.setStrokeOpacity(value, fromField);
            break;
        }
            // FILL PARAMETERS
        case FILLENABLE: {
            boolean enabled = Boolean.parseBoolean(value);
            pointSymbolizerWrapper.setHasFill(enabled);
            break;
        }
        case FILLCOLOR: {
            pointSymbolizerWrapper.setFillColor(value);
            break;
        }
        case FILLOPACITY: {
            pointSymbolizerWrapper.setFillOpacity(value, fromField);
            break;
        }
            // LABEL PARAMETERS
        case LABELENABLE: {
            boolean doEnable = Boolean.parseBoolean(value);
            if (doEnable) {
                if (textSymbolizerWrapper == null) {
                    TextSymbolizer textSymbolizer = Utilities.createDefaultTextSymbolizer(SLD.POINT);
                    ruleWrapper.addSymbolizer(textSymbolizer, TextSymbolizerWrapper.class);
                    labelsParametersComposite.update(ruleWrapper);
                }
            } else {
                ruleWrapper.removeTextSymbolizersWrapper();
            }
            break;
        }
        case LABEL: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setLabelName(value, fromField);
            break;
        }
        case LABELFONT: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            String name = values[0];
            int style = Integer.parseInt(values[1]);
            int height = Integer.parseInt(values[2]);
            Font font = sb.createFont(name, style == SWT.ITALIC, style == SWT.BOLD, height);

            textSymbolizerWrapper.setFont(font);
            break;
        }
        case LABELCOLOR: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setColor(value);
            break;
        }
        case LABELHALOCOLOR: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setHaloColor(value);
            break;
        }
        case LABELHALORADIUS: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setHaloRadius(value);
            break;
        }
        case LABELANCHOR: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setAnchorX(values[0]);
            textSymbolizerWrapper.setAnchorY(values[1]);
            break;
        }
        case LABELDISPLACEMENT: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setDisplacement(value);
            break;
        }
        case LABELROTATION: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setRotation(value, fromField);
            break;
        }
        case LABELMAXDISPLACEMENT_VO: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setMaxDisplacementVO(value);
            break;
        }
        case LABELAUTOWRAP_VO: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setAutoWrapVO(value);
            break;
        }
        case LABELSPACEAROUND_VO: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setSpaceAroundVO(value);
            break;
        }
        case FILTER: {
            if (value.length() > 0) {
                try {
                    ruleWrapper.setFilter(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
        }
        default:
            break;
        }

        pointPropertiesEditor.refreshTreeViewer(ruleWrapper);
        pointPropertiesEditor.refreshPreviewCanvasOnStyle();

    }

}
