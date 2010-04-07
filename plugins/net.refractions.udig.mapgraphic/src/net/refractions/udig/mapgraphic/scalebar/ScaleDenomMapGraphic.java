package net.refractions.udig.mapgraphic.scalebar;

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IStyleBlackboard;
import net.refractions.udig.ui.graphics.ViewportGraphics;

public class ScaleDenomMapGraphic implements MapGraphic {

    
    
    public ScaleDenomMapGraphic() {
        
    }
    
    public void draw( MapGraphicContext context ) {
        
        ScaleDenomMapGraphicBean configBean = getConfigBean(context.getLayer().getStyleBlackboard());
        
        //check for required bean parameters
        if (configBean.getFont() == null) throw new NullPointerException("ScaleDenomMapGraphicBean font must not be null"); //$NON-NLS-1$
        if (configBean.getTextColor() == null) throw new NullPointerException("ScaleDenomMapGraphicBean text color must not be null"); //$NON-NLS-1$
        if (configBean.getNumberFormat() == null) throw new NullPointerException("ScaleDenomMapGraphicBean number format must not be null"); //$NON-NLS-1$
        if (configBean.getWidth() <= 0) throw new IllegalArgumentException("ScaleDenomMapGraphicBean width must be a positive number"); //$NON-NLS-1$
        if (configBean.getHeight() <= 0) throw new IllegalArgumentException("ScaleDenomMapGraphicBean height must be a positive number"); //$NON-NLS-1$
        if (configBean.getLabel() == null) throw new NullPointerException("ScaleDenomMapGraphicBean label must not be null"); //$NON-NLS-1$
        
        ViewportGraphics g = context.getGraphics();
        IBlackboard mapblackboard = context.getMap().getBlackboard();
        
        double scaleDenom = context.getViewportModel().getScaleDenominator();
        
        Object value = mapblackboard.get("scale"); // scale may be set by printing engine        
        if( value != null && value instanceof Double ){
            scaleDenom = ((Double)value).doubleValue();
        }
        
        if (configBean.getBackgroundColor() != null) {
            g.setBackground(configBean.getBackgroundColor());
            g.clearRect(0, 0, configBean.getWidth(), configBean.getHeight());
        }
        g.setColor(configBean.getTextColor());
        g.setFont(configBean.getFont());
        String denomStr = configBean.getNumberFormat().format(scaleDenom);
        int horizAlignment;
        int anchorPointX;
        if (configBean.getHorizAlignment() == ScaleDenomMapGraphicBean.ALIGN_CENTER) {
            horizAlignment = ViewportGraphics.ALIGN_MIDDLE;
            anchorPointX = configBean.getWidth() / 2;
        } else if (configBean.getHorizAlignment() == ScaleDenomMapGraphicBean.ALIGN_RIGHT) {
            horizAlignment = ViewportGraphics.ALIGN_RIGHT;
            anchorPointX = configBean.getWidth();
        } else {
            horizAlignment = ViewportGraphics.ALIGN_LEFT;
            anchorPointX = 0;
        }
        g.drawString(configBean.getLabel()+"1:"+denomStr, anchorPointX, configBean.getHeight() / 2, horizAlignment, ViewportGraphics.ALIGN_MIDDLE); //$NON-NLS-1$
    }

    private ScaleDenomMapGraphicBean getConfigBean(IStyleBlackboard styleBlackboard) {
        ScaleDenomMapGraphicBean configBean = (ScaleDenomMapGraphicBean) styleBlackboard.get(ScaleDenomMapGraphicBean.KEY);
        
        //set defaults
        if (configBean == null) {
            configBean = new ScaleDenomMapGraphicBean();
            configBean.setFont(new Font("Arial",Font.PLAIN, 10)); //$NON-NLS-1$
            configBean.setWidth(150);
            configBean.setHeight(40);
            configBean.setTextColor(Color.BLACK);
            configBean.setBackgroundColor(Color.WHITE);
            configBean.setLabel(""); //$NON-NLS-1$
            configBean.setHorizAlignment(ScaleDenomMapGraphicBean.ALIGN_CENTER);
            
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(0);
            configBean.setNumberFormat(nf);
        }
        
        return configBean;
    }
    
}
