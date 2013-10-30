
/**
 * 
 */
package net.refractions.udig.project.ui;

import java.util.List;

import org.eclipse.jface.wizard.IWizard;

/**
 * @author jones
 *
 */
public interface IDataDropWizard extends IWizard {
	public void setIDs(List<String> dataIDs);
}
