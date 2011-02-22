/**
 *
 */
package net.refractions.udig.issues;

import static net.refractions.udig.issues.IssueConstants.EXTENSION_CLASS_ATTR;
import static net.refractions.udig.issues.IssueConstants.ISSUES_EXTENSION_ID;

import java.util.List;

import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.issues.internal.IssuesActivator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Jesse
 *
 */
public final class IssuesListUtil {

	private IssuesListUtil() {
	}

	/**
	 * Looks up the issue type from the extension point ID. If the current
	 * installation does not have the extension then null is returned. Look in
	 * the log for the reason.
	 *
	 * @param extensionPointID
	 * @return
	 * @throws CoreException
	 */
	public static IIssue createIssue(String extensionPointID) {
		List<IConfigurationElement> extensions = ExtensionPointList
				.getExtensionPointList(ISSUES_EXTENSION_ID);

		IConfigurationElement found = null;
		for (IConfigurationElement elem : extensions) {
			String namespace = elem.getNamespaceIdentifier();
			String name = elem.getAttribute("id"); //$NON-NLS-1$
			String id = namespace + "." + name; //$NON-NLS-1$
			if (extensionPointID.equals(id)) {
				found = elem;
				break;
			}
		}
		if (found == null) {
			IssuesActivator
					.log(
							"No matching issue extension was found: " + extensionPointID, null); //$NON-NLS-1$
			return null;
		}

		Object created;
		try {
			created = found.createExecutableExtension(EXTENSION_CLASS_ATTR);
			if (!(created instanceof IIssue)) {
				IssuesActivator
						.log(
								"Extension found did not create an issue object: " + extensionPointID, null); //$NON-NLS-1$
				return null;
			}

			return (IIssue) created;
		} catch (CoreException e) {
			IssuesActivator
					.log(
							"An exception was raised when trying to instantiate the extension for the issue: " + extensionPointID, e); //$NON-NLS-1$
			return null;
		}
	}

}
