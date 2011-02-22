package net.refractions.udig.printing.ui;

/**
 * The TemplateFactory is used by the printing system to instantiate
 * instances of Templates so that they can be used for printing.
 *
 * @author rgould
 */
public interface TemplateFactory {

	/**
	 * Instantiates a new instance of a Template.
	 */
	public Template createTemplate();

	/**
	 * The name of the templates that this factory produces.
	 * This must be human-readable. For example, a list of all the
	 * names of the Templates available may be presented to a user.
	 */
	public String getName();
}
