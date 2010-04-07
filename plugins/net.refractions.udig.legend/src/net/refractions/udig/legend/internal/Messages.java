package net.refractions.udig.legend.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.legend.internal.messages"; //$NON-NLS-1$

	public static String LegendGraphicStyleConfigurator_background_colour;

	public static String LegendGraphicStyleConfigurator_font_colour;

	public static String LegendGraphicStyleConfigurator_horizontal_margin;

	public static String LegendGraphicStyleConfigurator_horizontal_spacing;

	public static String LegendGraphicStyleConfigurator_indent_size;

	public static String LegendGraphicStyleConfigurator_vertical_margin;

	public static String LegendGraphicStyleConfigurator_vertical_spacing;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
