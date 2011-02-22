package net.refractions.udig.project.tests.ui;

import java.awt.Graphics2D;

import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.RenderException;

import org.eclipse.core.runtime.IProgressMonitor;

public class DummyRenderer extends RendererImpl {

    @Override
    public void render( Graphics2D destination, IProgressMonitor monitor ) throws RenderException {

    }

    @Override
    public void render( IProgressMonitor monitor ) throws RenderException {
    }

}
