package eu.udig.style.jgrass.legend;
/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
 //    /* line color alpha blending value */
//    private float alpha = .5f;
//
//    /* X position of legend */
//    private float xpos = 0f;
//
//    /* Y position of legend */
//    private float ypos = 0f;
//
//    /* Width of the legend */
//    private float width = 0f;
//
//    /* Width and height of the legend colorbox */
//    private float boxwidth = 0f;
//    private float boxheightcorrection = 0f;
//
//    /* Height of the legend */
//    private float height = 0f;
//
//    private int fontsize = 12;
//
//    /* Background color */
//    private float[] bgcolor = null;
//
//    /* Stroke color */
//    private float[] strokecolor = null;
//
//    /* text color */
//    private float[] textcolor = null;
//
//    /* Texture font object */
//    private GLTextureFont texFont = null;
//
//    private String mapname = null;
//
//    private String legendString = null;
//
//    /** Creates a new instance of NorthArrowDisplayObject */
//    public MapLegendDisplayObject( Tag prefs ) {
//        super(prefs.getAttribute("label").getValue());
//
//        alpha = java.lang.Float.parseFloat(prefs.getAttribute("alpha").getValue());
//        xpos = java.lang.Float.parseFloat(prefs.getAttribute("xpos").getValue());
//        ypos = java.lang.Float.parseFloat(prefs.getAttribute("ypos").getValue());
//        width = java.lang.Float.parseFloat(prefs.getAttribute("width").getValue());
//        boxwidth = java.lang.Float.parseFloat(prefs.getAttribute("boxwidth").getValue());
//        height = java.lang.Float.parseFloat(prefs.getAttribute("height").getValue());
//        fontsize = Integer.parseInt(prefs.getAttribute("fontsize").getValue());
//        mapname = prefs.getAttribute("mapname").getValue();
//
//        strokecolor = new float[3];
//        JGrassUtilities.getColor(prefs.getAttribute("strokecolor").getValue(), Color.black)
//                .getRGBColorComponents(strokecolor);
//        bgcolor = new float[3];
//        JGrassUtilities.getColor(prefs.getAttribute("bgcolor").getValue(), Color.black)
//                .getRGBColorComponents(bgcolor);
//        textcolor = new float[3];
//        JGrassUtilities.getColor(prefs.getAttribute("textcolor").getValue(), Color.black)
//                .getRGBColorComponents(textcolor);
//
//        setVisible(prefs.getAttribute("visible").getValue().equals("true") ? true : false);
//        configTag = prefs;
//    }
//
//    /**
//     *
//     */
//    public void renderObject( GLAutoDrawable drawable, Camera c ) {
//        BroadcastListener graphicschannels = GrassEnvironmentManager.getInstance()
//                .getBroadcastChannelListeners(GrassEnvironmentManager.GRAPHICS_BROADCAST_CHANNEL);
//        Vector v = new Vector();
//        graphicschannels.broadcast(new BroadcastMessage.GetDisplayObjectsMessage(null, v));
//
//        GDisplayObject dobj = null;
//        if (v.size() > 0) {
//            LinkedList displayObjects = (LinkedList) v.elementAt(0);
//            Iterator it = displayObjects.iterator();
//
//            while( it.hasNext() ) {
//                dobj = (GDisplayObject) it.next();
//                if (dobj.getName().equals(mapname)) {
//                    break;
//                }
//                dobj = null;
//            }
//        }
//
//        if (dobj != null) {
//            legendString = dobj.getLegendString();
//
//            /* Draw the legend. */
//            if (legendString != null && !legendString.equals("")) {
//                JlsTokenizer tk = new JlsTokenizer(legendString, LEGEND_SEPERATOR);
//                /* Discard first token i.e raster, vector, etc */
//                tk.nextToken();
//
//                int leftentries = tk.countTokens() / 2;
//                String[][] legend = new String[leftentries][2];
//
//                boolean discrete = false;
//                int numberOfLegendEntries = 0;
//                for( int i = 0; i < legend.length; i++ ) {
//                    if (tk.hasMoreTokens()) {
//                        String legendText = tk.nextToken().replace('_', ' ');
//
//                        // check if the legend is discrete or range
//                        // discrete = the first value has an attribute
//                        // range = the first value has empty attribute (there is one attr. every 7
//                        // colors)
//                        if (!legendText.equals("") && i == 0) {
//                            discrete = true;
//                        }
//
//                        if (discrete) {
//                            // draw discrete legend
//                            legend[i][0] = legendText;
//
//                        } else {
//                            // draw a legend with range blending
//                            if (!legendText.equals("")) {
//                                String[] intervalMaxMin = legendText.split(" to ");
//                                float intervalmin = Float.parseFloat(intervalMaxMin[0]);
//                                float intervalmax = Float.parseFloat(intervalMaxMin[1]);
//
//                                float delta = (intervalmax - intervalmin) / 6f;
//
//                                legend[i - 3][0] = String.valueOf(intervalmin);
//                                legend[i - 2][0] = String.valueOf(intervalmin + delta);
//                                legend[i - 1][0] = String.valueOf(intervalmin + 2f * delta);
//                                legend[i][0] = String.valueOf(intervalmin + 3f * delta);
//                                legend[i + 1][0] = String.valueOf(intervalmin + 4f * delta);
//                                legend[i + 2][0] = String.valueOf(intervalmin + 5f * delta);
//                                legend[i + 3][0] = String.valueOf(intervalmax);
//
//                            }
//
//                        }
//                        String legendColor = tk.nextToken();
//                        legend[i][1] = legendColor;
//                    }
//                }
//
//                // remove double entries
//                LinkedHashMap leg = new LinkedHashMap(6);
//                for( int j = 0; j < legend.length; j++ ) {
//                    leg.put(legend[j][0], legend[j][1]);
//                }
//
//                legend = new String[leg.size()][2];
//                numberOfLegendEntries = legend.length;
//                // Iterate over the keys in the map
//                Iterator it = leg.keySet().iterator();
//                for( int i = 0; i < legend.length; i++ ) {
//                    // Get key
//                    legend[i][0] = (String) it.next();
//                    legend[i][1] = (String) leg.get(legend[i][0]);
//                }
//
//                /*
//                 * finally start to draw
//                 */
//                GL gl = drawable.getGL();
//                GLU glu = new GLU();
//                Dimension sz = new Dimension(drawable.getWidth(), drawable.getHeight());
//
//                gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);
//
//                gl.glEnable(GL.GL_ALPHA_TEST);
//                gl.glAlphaFunc(GL.GL_GREATER, 0.0f);
//
//                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
//                gl.glEnable(GL.GL_BLEND);
//
//                /* Initialise font texture the first time. */
//                if (texFont == null) {
//                    texFont = new GLTextureFont(drawable);
//                    texFont.CreateFont(new Font("Sans Serif", Font.PLAIN, fontsize));
//                    if (!texFont.ready()) {
//                        if (logger.isDebugEnabled())
//                            logger.debug("cannot load texture font.");
//                        return;
//                    }
//                    texFont.initFontTexture(gl);
//                }
//
//                /* Change projection to X, Y */
//                gl.glMatrixMode(GL.GL_PROJECTION);
//                // gl.glPushMatrix();
//                gl.glLoadIdentity();
//                glu.gluOrtho2D(0, sz.width, 0, sz.height);
//
//                gl.glMatrixMode(GL.GL_MODELVIEW);
//                // gl.glPushMatrix();
//                gl.glLoadIdentity();
//
//                // int[] vp = new int[4];
//                // gl.glGetIntegerv(GL.GL_VIEWPORT, vp);
//                gl.glViewport(0, 0, sz.width, sz.height);
//
//                float x1 = xpos;// * sz.width;
//                float y1 = ypos;// * sz.height;
//                float w = width;// * sz.width;
//                float h = height;// * sz.height;
//
//                JoglUtilities.drawFilledBox(gl, x1, y1, w, h, bgcolor, 1f, alpha);
//                JoglUtilities.drawEmptyBox(gl, x1, y1, w, h, strokecolor, 2f, alpha + alpha * 0.2f);
//
//                /*
//                 * discrete or not dependent parts
//                 */
//                float strokewidth = 1f;
//                // the legend doesn't fill as required, therefore correct
//                // float correctiondiscrete = 0.75f;
//                // upper and lower inset
//                float yinset = boxwidth / 2f;
//                if (discrete) {
//                    // the size of the box
//                    float ybox = (h - 2f * strokewidth * numberOfLegendEntries - 2f * yinset)
//                            / (numberOfLegendEntries * 6f / 5f - 1f / 5f - 0.75f - boxheightcorrection);
//                    // space between a box and the other
//                    float yglue = ybox / 5f;
//
//                    float currentX = xpos + yinset;
//                    float currentY = ypos - yinset;
//
//                    for( int i = 0; i < legend.length; i++ ) {
//                        // draw the colorbox
//                        JoglUtilities.drawEmptyBox(gl, currentX, currentY, boxwidth, ybox,
//                                textcolor, strokewidth, 1);
//                        String attribute = legend[i][0];
//                        float[] boxcolortris = new float[3];
//                        JGrassUtilities.getColor(legend[i][1], Color.black).getRGBColorComponents(
//                                boxcolortris);
//                        JoglUtilities.drawFilledBox(gl, currentX + 2f, currentY - 2f,
//                                boxwidth - 4f, ybox - 4f, boxcolortris, strokewidth, 1);
//
//                        // write the text
//                        texFont.setForegroundColor(textcolor);
//                        texFont.setHorizontalAlignment(GLTextureFont.LEFT_ALIGNED);
//                        texFont.setVerticalAlignment(GLTextureFont.CENTER_ALIGNED);
//                        texFont.Begin(gl, glu);
//                        // int charwidth = texFont.getCharWidth('x');
//                        float tx = currentX + 1.5f * boxwidth;
//                        float ty = currentY - ybox * 3f / 4f;
//                        texFont.DrawString(attribute, tx, ty, false, true);
//                        texFont.End();
//
//                        currentY = currentY - ybox - yglue;
//                    }
//
//                } else {
//                    // GRADIENT LEGEND
//                    // the size of the box
//                    float ybox = (h - 2f * yinset) / (numberOfLegendEntries + boxheightcorrection);
//
//                    float currentX = xpos + yinset;
//                    float currentY = ypos - yinset;
//                    float startX = xpos + yinset;
//                    float startY = ypos - yinset;
//
//                    for( int i = 0; i < legend.length; i++ ) {
//                        String attribute = legend[i][0];
//                        float[] actualboxcolortris = new float[3];
//                        JGrassUtilities.getColor(legend[i][1], Color.black).getRGBColorComponents(
//                                actualboxcolortris);
//                        float[] nextboxcolortris = new float[3];
//
//                        if (i != legend.length - 1) {
//                            JGrassUtilities.getColor(legend[i + 1][1], Color.black)
//                                    .getRGBColorComponents(nextboxcolortris);
//                            JoglUtilities.drawFilledGradientBox(gl, currentX, currentY, boxwidth,
//                                    ybox, actualboxcolortris, nextboxcolortris, strokewidth, 1);
//                        } else {
//                            // in the last just make it without gradient
//                            nextboxcolortris = actualboxcolortris;
//                            JoglUtilities
//                                    .drawFilledGradientBox(gl, currentX, currentY, boxwidth,
//                                            ybox / 2f, actualboxcolortris, nextboxcolortris,
//                                            strokewidth, 1);
//                        }
//
//                        if (i % 3 == 0) {
//                            // write the text
//                            texFont.setForegroundColor(textcolor);
//                            texFont.setHorizontalAlignment(GLTextureFont.LEFT_ALIGNED);
//                            texFont.setVerticalAlignment(GLTextureFont.CENTER_ALIGNED);
//                            texFont.Begin(gl, glu);
//                            // int charwidth = texFont.getCharWidth('x');
//                            float tx = currentX + 1.5f * boxwidth;
//                            float ty = currentY - ybox * 3f / 4f;
//                            // texFont.DrawString(attribute, tx, ty, false, true);
//                            texFont.DrawString(Format
//                                    .sprintf("%-8.2f", Float.parseFloat(attribute)), tx, ty, false,
//                                    true);
//
//                            texFont.End();
//                        }
//
//                        currentY = currentY - ybox;
//                    }
//                    // draw the colorbox
//                    JoglUtilities.drawEmptyBox(gl, startX, startY, boxwidth,
//                            (numberOfLegendEntries - 0.5f) * ybox, textcolor, strokewidth, 1);
//
//                }
//
//                gl.glDisable(GL.GL_ALPHA_TEST);
//                gl.glDisable(GL.GL_BLEND);
//
//                gl.glPopAttrib();
//            }
//        }
//    }
//
//    /**
//     *
//     */
//    public void editProperties() {
//        /* Clear the temporary protery values store */
//        clearTempPropertiesStore();
//
//        WizardDialog dialog = new WizardDialog((JFrame) GrassEnvironmentManager.getInstance()
//                .getGuiParentFrame(), "Map Legend Display Object Properties", true);
//
//        try {
//            /* Parse xml gui file and build panels */
//            dialog.initContentPanel(XMLGUIBuilder.buildTabbedPane(new InputStreamReader(
//                    ResourceLoader
//                            .LoadEtcResourceAsStream("xml/maplegenddisplayobjectproperties.xml")),
//                    JTabbedPane.BOTTOM, this));
//        } catch (Exception ex) {
//            GrassEnvironmentManager.getInstance().getGuiParentFrame().showErrorDialog(ex, "Error");
//            return;
//        }
//
//        /* Layout the panels and center dialog. */
//        dialog.showWizard();
//
//        if (dialog.okPressed()) {
//            /*
//             * Iterate through all variables stored in the temporary store. Only those variables
//             * that have had their values adjusted will be in the temporary store.
//             */
//            Iterator it = this.getTempPropertyStoreIterator();
//            while( it.hasNext() ) {
//                String s = (String) it.next();
//                if (s.equals("alpha")) {
//                    float f = Float.parseFloat(getFromTempPropertyStore(s));
//                    if (f < 0f)
//                        alpha = 0f;
//                    else if (f > 1f)
//                        alpha = 1f;
//                    else
//                        alpha = f;
//                    configTag.getAttribute("alpha").setValue(String.valueOf(alpha));
//                } else if (s.equals("mapname")) {
//                    mapname = getFromTempPropertyStore(s);
//                } else if (s.equals("xpos")) {
//                    xpos = Float.parseFloat(getFromTempPropertyStore(s));
//                } else if (s.equals("ypos")) {
//                    ypos = Float.parseFloat(getFromTempPropertyStore(s));
//                } else if (s.equals("width")) {
//                    width = Float.parseFloat(getFromTempPropertyStore(s));
//                } else if (s.equals("height")) {
//                    height = Float.parseFloat(getFromTempPropertyStore(s));
//                } else if (s.equals("boxwidth")) {
//                    boxwidth = Float.parseFloat(getFromTempPropertyStore(s));
//                } else if (s.equals("boxheightcorrection")) {
//                    boxheightcorrection = Float.parseFloat(getFromTempPropertyStore(s));
//                }
//            }
//        }
//        /* Clear the temporary protery values store */
//        clearTempPropertiesStore();
//    }
//
//    /**
//     *
//     */
//    public String getStringProperty( String prop ) {
//        if (prop.equalsIgnoreCase("alpha"))
//            return String.valueOf(alpha);
//        else if (prop.equalsIgnoreCase("mapname"))
//            return mapname;
//        else if (prop.equalsIgnoreCase("xpos"))
//            return String.valueOf(xpos);
//        else if (prop.equalsIgnoreCase("ypos"))
//            return String.valueOf(ypos);
//        else if (prop.equalsIgnoreCase("width"))
//            return String.valueOf(width);
//        else if (prop.equalsIgnoreCase("height"))
//            return String.valueOf(height);
//        else if (prop.equalsIgnoreCase("boxwidth"))
//            return String.valueOf(boxwidth);
//        else if (prop.equalsIgnoreCase("boxheightcorrection"))
//            return String.valueOf(boxheightcorrection);
//
//        return "";
//    }
//
//    /**
//     * from the XML side, the first is the name of the component
//     */
//    public void setStringProperty( String prop, String value ) {
//        putInTempPropertyStore(prop, value);
//    }
//}
