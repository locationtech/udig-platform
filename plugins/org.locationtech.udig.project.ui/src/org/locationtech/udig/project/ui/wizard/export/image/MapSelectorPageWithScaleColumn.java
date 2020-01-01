/**
 * 
 */
package org.locationtech.udig.project.ui.wizard.export.image;

import java.util.Collection;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.AdapterFactoryLabelProviderDecorator;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectExplorer;
import org.locationtech.udig.project.ui.wizard.export.MapSelectorPage;

/**
 * Adds a column where the user can set the scale at which to export the map
 * 
 * @author Jesse
 */
public class MapSelectorPageWithScaleColumn extends MapSelectorPage {
    
    private static final String SCALE = "EXPORT_SCALE"; //$NON-NLS-1$

    public MapSelectorPageWithScaleColumn( String page, String title, ImageDescriptor banner ) {
        super( page, title, banner );
        setDescription(Messages.MapSelectorPageWithScaleColumn_Description );
    }
    @Override
    public void createControl( Composite parent ) {
        super.createControl(parent);
        Composite comp = (Composite) getControl();
        Button scale = new Button(comp, SWT.PUSH);
        scale.setText(Messages.MapSelectorPageWithScaleColumn_scale);
        GridData gridData = new GridData();
        gridData.verticalAlignment = SWT.BOTTOM;
        gridData.horizontalAlignment = SWT.FILL;
        scale.setLayoutData(gridData);
        
        scale.addListener(SWT.Selection, new Listener(){
            public void handleEvent( Event event ) {
                Collection<IMap> maps = getMaps();
                for( IMap map : maps ) {
                    map.getBlackboard().put(SCALE, null);
                }
                viewer.update(maps.toArray(), new String[]{SCALE});                
            }            
        });
    }
    
    @Override
    protected void createColumns( Table table, TableLayout tableLayout ) {
        super.createColumns(table, tableLayout);
        table.setHeaderVisible(true);
        createScaleColumn(table, tableLayout);
    }

    private void createScaleColumn( Table table, TableLayout tableLayout ) {
        TableColumn column = new TableColumn(table, SWT.DEFAULT);
        column.setText(Messages.MapSelectorPageWithScaleColumn_scaleColumnText);
        column.setResizable(true);
        column.setAlignment(SWT.LEFT);
        tableLayout.addColumnData(new ColumnWeightData(1, true));
    }

    @Override
    protected void configureEditors( final TableViewer viewer2 ) {
        final String message = Messages.MapSelectorPageWithScaleColumn_invalidScaleError;

        TextCellEditor textCellEditor = new TextCellEditor(viewer2.getTable());
        ICellEditorValidator validator = new ICellEditorValidator(){

            public String isValid( Object value ) {
                setErrorMessage(null);

            try{
                int parseInt = Integer.parseInt((String) value);
                if( parseInt<-1 ){
                    return message;
                }
            }catch(Exception e){
                return message;
            }
                return null;
            }
            
        };
        textCellEditor.setValidator(validator );
        viewer2.setCellEditors(new CellEditor[] { null, textCellEditor });
        final String scaleColumn = "scaleColumn"; //$NON-NLS-1$
        
        viewer2.setCellModifier(new ICellModifier() {

            public boolean canModify(Object element, String property) {
                return property.equals(scaleColumn);
            }

            public Object getValue(Object element, String property) {
                int scale2 = getScaleDenom((IMap) element);
                return Integer.toString(scale2);
            }

            public void modify(Object element, String property, Object value) {
                TableItem item = (TableItem) element;
                IMap map = (IMap) item.getData();
                try{
                    int parseInt = Integer.parseInt((String) value);
                    if( parseInt<-1 ){
                        setErrorMessage(message);
                    }
                    map.getBlackboard().putInteger(SCALE, parseInt);
                    viewer2.update(map, new String[]{SCALE});                
                    setErrorMessage(null);
                }catch (NumberFormatException e) {
                    setErrorMessage(message);
                    return ;
                }
            }
            
        });
        
        viewer2.setColumnProperties(new String[] {"1",scaleColumn}); //$NON-NLS-1$
        ColumnViewerEditorActivationStrategy editorActivationStrategy = new ColumnViewerEditorActivationStrategy(viewer2);
        TableViewerEditor.create(viewer2, editorActivationStrategy, ColumnViewerEditor.TABBING_VERTICAL);
    }
    
    @Override
    protected IBaseLabelProvider createLabelProvider(StructuredViewer viewer) {
        return new TableLabelProvider(new AdapterFactoryLabelProviderDecorator(ProjectExplorer.getProjectExplorer()
                .getAdapterFactory(), viewer));
    }
    
    public static int getScaleDenom( IMap map ) {
        Object scaleObject = map.getBlackboard().get(SCALE);
        int scale;
        if( scaleObject == null ){
            scale = (int) map.getViewportModel().getScaleDenominator();
        } else {
            scale = (Integer) scaleObject;
        }
        return scale;
    }
    
    
    private static class TableLabelProvider extends LabelProvider implements ITableLabelProvider{

        private final AdapterFactoryLabelProviderDecorator wrapped;
        
        
        public TableLabelProvider( AdapterFactoryLabelProviderDecorator wrapped ) {
            this.wrapped = wrapped;
        }

        public Image getColumnImage( Object element, int columnIndex ) {
            if( columnIndex==0 ){
                return wrapped.getImage(element);
            }
            return null;
        }

        public String getColumnText( Object element, int columnIndex ) {
            if( columnIndex==0 ){
                return wrapped.getText(element);
            }
            IMap map = (IMap) element;
            int scale = getScaleDenom(map);
            if( scale==-1 ){
                return Messages.MapSelectorPageWithScaleColumn_defaultScale;
            } 
            return "1:"+scale; //$NON-NLS-1$
        }
        
        @Override
        public boolean isLabelProperty( Object element, String property ) {
            return property.equals(SCALE);
        }

        @Override
        public void dispose() {
            wrapped.dispose();
        }
        
    }
}
