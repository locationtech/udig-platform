package net.refractions.udig.printing.ui.internal;

import net.refractions.udig.printing.ui.Template;
import net.refractions.udig.printing.ui.TemplateFactory;

public class BasicTemplateFactory implements TemplateFactory {

	public Template createTemplate() {
		return new BasicTemplate();
	}

	public String getName() {
		return createTemplate().getName();
	}
}
