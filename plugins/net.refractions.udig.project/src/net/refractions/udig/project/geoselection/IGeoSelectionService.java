package net.refractions.udig.project.geoselection;

public interface IGeoSelectionService {

    public void registerSelectionManager(String id,  IGeoSelectionManager selectionManager );
    
    public void unregisterSelectionManager(String id);
    
    public IGeoSelectionManager getSelectionManager(String id);
    
}
