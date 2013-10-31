/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
 package org.locationtech.udig.style.jgrass.core;


import java.nio.ByteBuffer;




/**
 * 
 */
public class ColorMapBuffer
{
  private int dataRow = 0;  

  private ByteBuffer rgbBuffer = null;

  /** Creates a new instance of ColorMapBuffer */
  public ColorMapBuffer()
  {
  }

  public void setRowOffset(int row)
  {
    dataRow = row;
  }

  public int getRowOffset()
  {
    return dataRow;
  }

  public void setRGBBuffer(ByteBuffer rgb)
  {
    rgbBuffer = rgb;
  }

  public ByteBuffer getRGBBuffer()
  {
    return rgbBuffer;
  }
}
