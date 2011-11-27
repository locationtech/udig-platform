package net.refractions.udig.tutorials.template;

import net.refractions.udig.printing.ui.Template;
import net.refractions.udig.printing.ui.TemplateFactory;

public class ImageTemplateFactory implements TemplateFactory {

	public Template createTemplate() {
		return new ImageTemplate();
	}

	public String getName() {
		return "Image template"; // should be internationalized
	}

}
