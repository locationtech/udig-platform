package net.refractions.udig.project.geoselection;

public class GeoSelectionEntry implements IGeoSelectionEntry {
    
    private String context;
    
    private IGeoSelection selection;
    
    
    public GeoSelectionEntry(String context){
        this.context = context;
    }
    
    public GeoSelectionEntry(String context, IGeoSelection selection){
        this.context = context;
        this.selection = selection;
    }
    

    public void setSelection( IGeoSelection selection ) {
        this.selection = selection;
    }

    public String getContext() {
        return context;
    }

    public IGeoSelection getSelection() {
        return selection;
    }

}
