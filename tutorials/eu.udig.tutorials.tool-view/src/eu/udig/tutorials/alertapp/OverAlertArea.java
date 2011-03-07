/**
 * 
 */
package eu.udig.tutorials.alertapp;

import java.util.List;

import net.refractions.udig.ui.operations.AbstractPropertyValue;

/**
 * Tests whether or not an Alert Area has been selected.  This is 
 * a property used in the definition of the SelectAffectedFeatures extension definition
 * to determine whether or not the operation can be executed.  
 */
public class OverAlertArea extends AbstractPropertyValue<AlertAppContext> {

	@Override
	public boolean isTrue(AlertAppContext context, String value) {
		@SuppressWarnings("rawtypes")
		List list = (List) context.getShowAlertsLayer().getBlackboard().get(ShowAlertsMapGraphic.ALERTS_KEY);
		return list != null && !list.isEmpty();
	}

	@Override
	public boolean canCacheResult() {
		return false;
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

}
