package net.refractions.udig.tutorials.examples.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.tutorials.examples.internal.messages"; //$NON-NLS-1$
	public static String InternationalizedDialog_Prompt;
	public static String InternationalizedDialog_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
