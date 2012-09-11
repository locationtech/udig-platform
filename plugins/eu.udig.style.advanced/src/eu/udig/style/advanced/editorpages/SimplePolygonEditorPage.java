/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.style.advanced.editorpages;

import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.style.internal.StyleLayer;
import net.refractions.udig.style.sld.SLDContent;
import net.refractions.udig.style.sld.editor.StyleEditorDialog;
import net.refractions.udig.style.sld.editor.StyleEditorPage;
import net.refractions.udig.ui.graphics.SLDs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.geotools.data.FeatureSource;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.visitor.DuplicatingStyleVisitor;

import eu.udig.style.advanced.internal.Messages;
import eu.udig.style.advanced.polygons.PolygonPropertiesEditor;
import eu.udig.style.advanced.utils.Utilities;

/**
 * Style editor for simple polygons.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class SimplePolygonEditorPage extends StyleEditorPage {
    public static final String ID = "eu.udig.style.advanced.editorpages.SimplePolygonEditorPage"; //$NON-NLS-1$
    
    private Style style = null;
    private PolygonPropertiesEditor propertiesEditor;
    private StackLayout stackLayout;
    private Label noFeatureLabel;
    private Composite mainComposite;

    private Style oldStyleCopy;

    public SimplePolygonEditorPage() {
        super();
        setSize(new Point(740, 500));
    }

    @Override
    public void createPageContent( Composite parent ) {

        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        stackLayout = new StackLayout();
        mainComposite.setLayout(stackLayout);

        noFeatureLabel = new Label(mainComposite, SWT.NONE);
        noFeatureLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        noFeatureLabel.setText(Messages.SimplePolygonEditorPage_0);

        StyleLayer layer = getSelectedLayer();
        IGeoResource resource = layer.getGeoResource();
        if (resource.canResolve(FeatureSource.class)) {
            StyleBlackboard styleBlackboard = layer.getStyleBlackboard();
            style = (Style) styleBlackboard.get(SLDContent.ID);
            if (style == null) {
                style = Utilities.createDefaultPolygonStyle();
            }
            
            DuplicatingStyleVisitor dsv = new DuplicatingStyleVisitor();
            dsv.visit(style);
            oldStyleCopy = (Style) dsv.getCopy();

            if (isPolygonStyle(style)) {
                propertiesEditor = new PolygonPropertiesEditor(layer);
                propertiesEditor.open(mainComposite, style);
                stackLayout.topControl = propertiesEditor.getControl();
            } else {
                stackLayout.topControl = noFeatureLabel;
            }
        } else {
            stackLayout.topControl = noFeatureLabel;
        }

    }

    private boolean isPolygonStyle( Style style ) {
        Symbolizer[] symbolizers = SLDs.symbolizers(style);
        for( Symbolizer symbolizer : symbolizers ) {
            if (symbolizer instanceof PolygonSymbolizer) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public void gotFocus() {
        refresh();
    }

    @Override
    public boolean performCancel() {
        return true;
    }

    @Override
    public void styleChanged( Object source ) {

    }

    public boolean okToLeave() {
        return true;
    }

    public boolean performApply() {
        applyStyle();

        return true;
    }

    private void applyStyle() {
       StyleLayer layer = getSelectedLayer();
        
        Style newStyle = propertiesEditor.getStyle();
        List<FeatureTypeStyle> featureTypeStyles = newStyle.featureTypeStyles();
        int ftsNum = featureTypeStyles.size();
        if (ftsNum < 1) {
            MessageDialog.openWarning(getShell(), Messages.SimplePolygonEditorPage_1, Messages.SimplePolygonEditorPage_2);
            style = oldStyleCopy;
            setStyle(oldStyleCopy);
            layer.revertAll();
            layer.apply();
            
            StyleEditorDialog dialog = (StyleEditorDialog) getContainer();
            dialog.getCurrentPage().refresh();
            return;
        }
        
        newStyle.setName(layer.getName());

        setStyle(newStyle);

        StyleBlackboard styleBlackboard = layer.getStyleBlackboard();
        styleBlackboard.put(SLDContent.ID, newStyle);
    }

    public boolean performOk() {
        applyStyle();
        propertiesEditor.close();
        return true;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public void refresh() {
        Layer layer = getSelectedLayer();
        IGeoResource resource = layer.getGeoResource();
        if (!resource.canResolve(FeatureSource.class)) {
            return;
        }

        StyleBlackboard styleBlackboard = layer.getStyleBlackboard();
        style = (Style) styleBlackboard.get(SLDContent.ID);
        if (style == null) {
            style = Utilities.createDefaultPolygonStyle();
        }
        DuplicatingStyleVisitor dsv = new DuplicatingStyleVisitor();
        dsv.visit(style);
        oldStyleCopy = (Style) dsv.getCopy();

        if (!isPolygonStyle(style)) {
            stackLayout.topControl = noFeatureLabel;
        } else {
            stackLayout.topControl = propertiesEditor.getControl();
            propertiesEditor.updateStyle(style);
        }
        mainComposite.layout();
        
    }

}
