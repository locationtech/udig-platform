package net.refractions.udig.ui.operations;

import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.core.runtime.IConfigurationElement;

public class OpFilterParser {

    FilterParser[] filterParsers;
    
    public OpFilterParser( FilterParser[] filterParser ) {
        super();
        
        assert filterParser != null;

        int i = 0;
        if( filterParser!=null )
            i=filterParser.length;
        this.filterParsers =new FilterParser[i];

        System.arraycopy(filterParser, 0, this.filterParsers, 0, i);

    }
    
    public OpFilter parseFilter( IConfigurationElement element ) {

        if( element==null ){
            UiPlugin.log("OpFilterParser: Parsing OpFilter, Configuration element is null so returning OpFilter null", null); //$NON-NLS-1$
            return OpFilter.TRUE;
        }

        String elementName = element.getName();
        if( elementName.equals("or")){ //$NON-NLS-1$
            return orFilter(element);
        }
        if( elementName.equals("not")){ //$NON-NLS-1$
            return notFilter(element);
        }
        if( elementName.equals("and")){ //$NON-NLS-1$
            return andFilter(element);
        }
        for( FilterParser parser : filterParsers ) {
            if( elementName.equals(parser.getElementName())){ 
                return parser.parse(element);
            }            
        }
        UiPlugin.log("OpFilterParser: Parsing OpFilter: no parser found for parsing "+element.getNamespaceIdentifier(), null); //$NON-NLS-1$

        return OpFilter.TRUE;
    }

    private OpFilter andFilter( IConfigurationElement element ) {
        And andFilter=new And();
        
        IConfigurationElement[] children = element.getChildren();
        if( children.length== 0){
            UiPlugin.log("OpFilterParser: Parsing OpFilter: No children of an AND OpFilter "+element.getNamespaceIdentifier(), null); //$NON-NLS-1$
            return OpFilter.TRUE;
        }
        for( IConfigurationElement element2 : children ) {
            andFilter.getFilters().add(parseFilter(element2));            
        }
        
        return andFilter;
    }

    private OpFilter notFilter( IConfigurationElement element ) {
        IConfigurationElement[] children = element.getChildren();
        if (!validateChildren(children) ){
            return OpFilter.TRUE;
        }
        return new Not(parseFilter(children[0]));
    }

    private OpFilter orFilter( IConfigurationElement element ) {
        Or orFilter=new Or();
        
        IConfigurationElement[] children = element.getChildren();
        if( children.length== 0){
            UiPlugin.log("OpFilterParser: Parsing OpFilter: No children of an OR OpFilter "+element.getNamespaceIdentifier(), null); //$NON-NLS-1$

            return OpFilter.TRUE;
        }
        for( IConfigurationElement element2 : children ) {
            orFilter.getFilters().add(parseFilter(element2));            
        }
        
        return orFilter;

    }
    
    private boolean  validateChildren( IConfigurationElement[] children ) {
        if( children.length<1){
            return false;
        }
        if( children.length > 1){
            UiPlugin.log("OpFilterParser: Error, more than one enablement element "+children[0].getDeclaringExtension().getExtensionPointUniqueIdentifier(), new Exception()); //$NON-NLS-1$
            return false;
        }
        return true;
    }
}
