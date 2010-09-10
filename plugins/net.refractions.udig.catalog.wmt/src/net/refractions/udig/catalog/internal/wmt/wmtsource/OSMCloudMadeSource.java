package net.refractions.udig.catalog.internal.wmt.wmtsource;

import net.refractions.udig.catalog.internal.wmt.WMTPlugin;

public class OSMCloudMadeSource extends OSMSource{
    public static final String NAME = "CloudMade"; //$NON-NLS-1$
    private static final int DEFAULT_STYLE = 1;
    private int style;
            
    @Override
    protected void init(String resourceId) throws Exception {
        int style;
        
        try{
            style = Integer.parseInt(resourceId);
        } catch(Exception exc) {
            WMTPlugin.log("[OSMCloudMadeSource.init] Couldn't get the style-id, taking the default-id:", exc); //$NON-NLS-1$
            
            style = DEFAULT_STYLE; // set default style
        }
        
        this.style = style;
        
        setName(NAME + style);
    }

    @Override
    public String getBaseUrl() {
        return "http://tile.cloudmade.com/c8d1aeca771d57d6a0584fea7ce386f4/" + style + "/256/";  //$NON-NLS-1$//$NON-NLS-2$
    }

}
