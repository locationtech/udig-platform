/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.ui.palette;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represent palette information.
 * A colour palette has multiple colour schemes associated with it.
 * A colour palette is a representation of a type of colour combination
 * as named by ColorBrewer (qualitative, selective or diverging).
 * 
 * <p>
 * This has been inspired by the work of ColorBrewer, indeed this is used to capture some of the
 * information produced by that project.
 * </p>
 * @author ptozer
 * @deprecated use org.geotools.brewer.color.BrewerPalette
 */
public class ColourPalette {
	private HashMap<String,ColourScheme> allColourSchemes; //<name of colour scheme, ColourScheme object
	private String paletteType;
	
	/**
	 * 
	 */
	public ColourPalette( ) {
		super();
		allColourSchemes = new HashMap<String,ColourScheme>();
		paletteType = null;
	}
	
	public ColourPalette(String type ) {
		super();
		allColourSchemes = new HashMap<String,ColourScheme>();
		paletteType = type;
	}
	
	public void addColourScheme(String schemeName, ColourScheme scheme) {
		if( !(allColourSchemes.containsKey(schemeName)) ){
			allColourSchemes.put(schemeName, scheme);
		}
	}

	/**
	 * @return Returns the allColourSchemes.
	 */
	public Map<String,ColourScheme> getAllColourSchemes() {
		return Collections.unmodifiableMap((Map)allColourSchemes);
	}
	/**
	 * @param allColourSchemes The allColourSchemes to set.
	 */
	public void setAllColourSchemes(
			HashMap<String, ColourScheme> allColourSchemes) {
		this.allColourSchemes = allColourSchemes;
	}
	/**
	 * @return Returns the allSchemeNames.
	 */
	public List<String> getAllSchemeNames() {
        Set<String> set = allColourSchemes.keySet();
       // String [] array = new String[allColourSchemes.size()];
       // set.toArray(array);
        List<String> returnable = new ArrayList<String>();
        returnable.addAll(set);

		return returnable;
	}

	
	/**
	 * @return Returns the paletteType.
	 */
	public String getPaletteType() {
		return paletteType;
	}
	/**
	 * @param paletteType The paletteType to set.
	 */
	public void setPaletteType(String paletteType) {
		this.paletteType = paletteType;
	}
	
	public ColourScheme getColourSchemeByName(String name) {
		return allColourSchemes.get(name);
	}
	
}
