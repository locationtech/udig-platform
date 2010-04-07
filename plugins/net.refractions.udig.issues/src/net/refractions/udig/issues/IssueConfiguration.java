/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.issues;

import static net.refractions.udig.issues.internal.PreferenceConstants.KEY_VIEW_CONTENT_PROVIDER;
import static net.refractions.udig.issues.internal.PreferenceConstants.KEY_VIEW_EXPANSION_PROVIDER;
import static net.refractions.udig.issues.internal.PreferenceConstants.KEY_VIEW_LABEL_PROVIDER;
import static net.refractions.udig.issues.internal.PreferenceConstants.KEY_VIEW_SORTER;
import net.refractions.udig.issues.internal.IssuesActivator;
import net.refractions.udig.issues.internal.view.IssueExpansionProvider;
import net.refractions.udig.issues.internal.view.IssuesContentProvider;
import net.refractions.udig.issues.internal.view.IssuesLabelProvider;
import net.refractions.udig.issues.internal.view.IssuesSorter;
import net.refractions.udig.issues.internal.view.IssuesView;
import net.refractions.udig.issues.internal.view.IssuesViewRefresher;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;



/**
 * This class allows many of the behaviours of the issues to be configured by another plugin.  Such as how 
 * the issues are sorted in the Issues View.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class IssueConfiguration {

    private static IssueConfiguration instance=new IssueConfiguration();
    private static IPreferenceStore preferences=IssuesActivator.getDefault().getPreferenceStore();
    
    /**
     * Returns configurator instance.
     *
     * @return
     */
    public static IssueConfiguration get(){
        return instance;
    }
    
    /**
     * Sets the sorting strategy to be used by the issues view.
     *
     * @param sorter the new strategy to use.
     */
    public void setIssuesViewSorter( IIssuesViewSorter sorter ){
        String extensionID = sorter.getExtensionID();
        if( extensionID==null )            
            preferences.setValue(KEY_VIEW_SORTER, ""); //$NON-NLS-1$
        else
            preferences.setValue(KEY_VIEW_SORTER, extensionID);
        
        try{
            IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(IssueConstants.VIEW_ID);
            if( view!=null ){
                ((IssuesView) view).setSorter(sorter);
            }
        }catch (Exception e) {
            // fall through.
        }
    }
    
    /**
     * Resets the sorting strategy to be the default.  It sorts on the column that is clicked on (header is clicked on).
     */
    public void setDefaultViewSorter( ){
        setIssuesViewSorter(new IssuesSorter());
    }
    
    /**
     * Sets the Content providers used to organize the structure of the issues view.  
     *
     * @param provider new provider
     */
    public void setContentProvider( IIssuesContentProvider provider){
        String extensionID = provider.getExtensionID();
        if( extensionID==null )            
            preferences.setValue(KEY_VIEW_CONTENT_PROVIDER, ""); //$NON-NLS-1$
        else
            preferences.setValue(KEY_VIEW_CONTENT_PROVIDER, extensionID);
        
        try{
            IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(IssueConstants.VIEW_ID);
            if( view!=null ){
                ((IssuesView) view).setContentProvider(provider);
            }
        }catch (Exception e) {
            // fall through.
        }
    }
    
    /**
     * Resets the content provider.  The default will structure issues in groups or in a flat list.
     */
    public void setDefaultContentProvider(){
        setContentProvider(new IssuesContentProvider());
    }
    
    /**
     * Sets the expansion provider used by issues view.
     *
     * @param provider the new provider to use.
     */
    public void setExpansionProvider( IIssuesExpansionProvider provider ){
        String extensionID = provider.getExtensionID();
        if( extensionID==null )            
            preferences.setValue(KEY_VIEW_EXPANSION_PROVIDER, ""); //$NON-NLS-1$
        else
            preferences.setValue(KEY_VIEW_EXPANSION_PROVIDER, extensionID);
        
        try{
            IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(IssueConstants.VIEW_ID);
            if( view!=null ){
                ((IssuesView) view).setExpansionProvider(provider);
            }
        }catch (Exception e) {
            // fall through.
        }
    }

    /**
     * Resets the expansion provider.  The default expands all elements.
     */
    public void setDefaultExpansionProvider( ){
        setExpansionProvider(new IssueExpansionProvider());
    } 
    
    /**
     * Sets the expansion provider used by issues view.
     *
     * @param provider the new provider to use.
     */
    public void setLabelProvider( IIssuesLabelProvider provider ){
        String extensionID = provider.getExtensionID();
        if( extensionID==null )            
            preferences.setValue(KEY_VIEW_LABEL_PROVIDER, ""); //$NON-NLS-1$
        else
            preferences.setValue(KEY_VIEW_LABEL_PROVIDER, extensionID);
        
        try{
            IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(IssueConstants.VIEW_ID);
            if( view!=null ){
                ((IssuesView) view).setLabelProvider(provider);
            }
        }catch (Exception e) {
            // fall through.
        }
    }

    /**
     * Resets the expansion provider.  The default expands all elements.
     */
    public void setDefaultLabelProvider( ){
        setLabelProvider(new IssuesLabelProvider());
    }
    
    public IRefreshControl createViewRefeshControl(){
        return new IssuesViewRefresher();
    }
}
