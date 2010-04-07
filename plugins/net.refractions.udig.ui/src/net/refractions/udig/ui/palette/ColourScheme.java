/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.ui.palette;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import net.refractions.udig.internal.ui.UiPlugin;

import org.geotools.brewer.color.BrewerPalette;

/**
 * <p>A colour scheme remaps the colours in a palette.</p>
 * 
 * @author ptozer
 * @author chorner
 */
public class ColourScheme {
    /** the number of items which use this scheme */
    private int itemCount = 0;
    /** the number of colours we grab from the palette */
    private int colourCount = 0;
    /** should the scheme automatically add/remove available colours? */
    private boolean canAutoSize = true;
    private HashMap<String,Integer> idMap; //object identifier, colour index
    private HashMap<Integer,Integer> colourMap; //colour index, new colour index 
    private BrewerPalette palette;
	
	public ColourScheme (BrewerPalette palette, int itemSize) {
        colourMap = new HashMap<Integer,Integer>();
        idMap = new HashMap<String,Integer>();
        setColourPalette(palette);
        setSizeScheme(itemSize);
	}

    public ColourScheme (BrewerPalette palette, int itemSize, int paletteSize) {
        colourMap = new HashMap<Integer,Integer>();
        idMap = new HashMap<String,Integer>();
        setColourPalette(palette);
        setSizePalette(paletteSize);
        setSizeScheme(itemSize);
    }
    
    public ColourScheme (BrewerPalette palette, HashMap<Integer,Integer> colourMap, HashMap<String,Integer> idMap, int itemSize, int paletteSize) {
        this.idMap = idMap;
        this.colourMap = colourMap;
        this.palette = palette;
        this.colourCount = paletteSize;
        this.itemCount = itemSize;
    }
    
    public static ColourScheme getDefault(final BrewerPalette palette) {
        return new ColourScheme(palette, 0);
    }
    
    public void setColourPalette(BrewerPalette palette) {
        this.palette = palette;
        //TODO: check number of colours in palette has not decreased 
    }
    
    public boolean getAutoSizing() {
        return canAutoSize;
    }
    
    /**
     * Sets the behaviour of the colour scheme as items are added. If true, the palette will morph
     * in size as items are added or removed. If false, the palette will remain static and scheme
     * colours will be repeated even if some are unused.
     * 
     * @param auto boolean
     */
    public void setAutoSizing(boolean auto) {
        canAutoSize = auto;
    }
    
    /**
     * Set the number of items this scheme is used by.  The size of the palette is automatically adjusted to fit. 
     *
     * @param numItems the number of items obtaining colours from this scheme
     */
    public void setSizeScheme(int numItems) {
        if (canAutoSize) { // we are allowed to adjust the number of colours from the palette this scheme uses
            setSizePalette(numItems); //setSizePalette is smart enough not to exceed the palette size
        }
        
        if (numItems > itemCount) { //items were added
            for (int i = itemCount; i < numItems; i++) {
                int newColourIndex = getNextColourIndex();
                colourMap.put(i, newColourIndex);
                itemCount++;
            }
        } else {
            //items were removed
            for (int i = numItems; i < itemCount; i++) {
                if (colourMap.containsKey(i))
                    colourMap.remove(i);
            }
            itemCount = numItems;
        }
    }

    /**
     * Sets the number of colours to use from the current palette. This method checks to ensure the
     * number of colours does not exceed the size of the palette.
     * 
     * @param numColours
     */
    public void setSizePalette(int numColours) {
        int minSize = palette.getMinColors();
        int maxSize = palette.getMaxColors();
        if (numColours > maxSize) {
            numColours = maxSize;
        }
        if (numColours < minSize) {
            numColours = minSize;
        }
        colourCount = numColours;
    }
    
    private int getLargestColourIndex(int numItems) {
        int largestIndex = -1;
        for (int i = 0; i < numItems; i++) {
            if (colourMap.containsKey(i)) {
                int thisIndex = colourMap.get(i);
                if (thisIndex > largestIndex)
                    largestIndex = i;
            }
        }
        return largestIndex;
    }
    
    public int getMinColours() {
        int minColours = palette.getMinColors();
        int colourWidth = getLargestColourIndex(itemCount) + 1;
        if (colourWidth > minColours) {
            return colourWidth;
        } else {
            return minColours;
        }
    }
    
    /**
     * Obtains a new colour index in the range specified, if unused. Colours are repeated if all are
     * in use.
     * 
     * @return colour index of the most appropriate next colour
     */
    private int getNextColourIndex() {
        // find an unused colour
        for (int i = 0; i < colourCount; i++) { 
            boolean hasColour = false;
            for (int j = 0; j < itemCount; j++) {
                if (colourMap.containsKey(j) && colourMap.get(j) == i) {
                    hasColour = true;
                    break;
                }
            }
            if (!hasColour) {
                return i;
            }
        }
        //we're out of colours, re-use one
        int[] instances = new int[colourCount];
        for (int i = 0; i < itemCount; i++) {
            if (colourMap.containsKey(i)) {
                instances[colourMap.get(i)]++;
            }
        }
        //find the first colourIndex which is used the least
        int leastInstances = -1;
        int index = 0;
        for (int i = 0; i < colourCount; i++) {
            if (instances[i] < leastInstances || leastInstances == -1) {
                leastInstances = instances[i];
                index = i;
            }
        }
        return index;
    }
    
    /**
     * Gets the number of colours currently available in the palette.  
     *
     * @return
     */
    public int getSizePalette() {
        return colourCount;
    }

    /**
     * Gets the number of classes utilizing this scheme.  
     *
     * @return
     */
    public int getSizeScheme() {
        return itemCount;
    }

    public boolean alignScheme(List<Color> colours) {
        int size = colours.size();
        if (itemCount < size) {
            setSizeScheme(size);
        }
        for (int i = 0; i < size; i++) {
            if (!getColour(i).equals(colours.get(i))) {
                boolean consistent = false;
                //find the first instance of this colour in the palette
                Color[] paletteColours = palette.getColors(colourCount);
                for (int j = 0; j < colourCount; j++) {
                    if (paletteColours[j].equals(colours.get(i))) {
                        consistent = true;
                        colourMap.remove(i);
                        colourMap.put(i, j);
                        break;
                    }
                }
                if (!consistent) { //utter failure
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Returns the next available colour. Good for comparing reality to what we think we have.
     * 
     * @param colours
     * @return
     */
    public Color getNextAvailableColour(List<Color> colours) {
        boolean[] inUse = new boolean[itemCount];
        for (int i = 0; i < itemCount; i++) {
            inUse[i] = false;
        }
        //for each colour in use
        for (Color colour : colours) {
            //check off all instances of it
            HashMap<Integer, Integer> clrMap = getColourMap();
            for (int index = 0; index < itemCount; index++) {
                int i;
                if (clrMap.containsKey(index)) {
                    i = clrMap.get(index);
                    Color aColour = palette.getColor(i, colourCount);
                    if (aColour.equals(colour)) {
                        inUse[index] = true;
                    }
                } else {
                    //index is not referenced
                }
            }
        }
        //find the first unused, yet mapped colour
        for (int i = 0; i < itemCount; i++) {
            if (!inUse[i]) {
                return getColour(i);
            }
        }
        if (palette.getMaxColors() == colourCount && colourCount <= itemCount) { //we're out of colours, so this logic won't work
            return getColour(itemCount);
        } else {
            setSizeScheme(itemCount+1);
            return getNextAvailableColour(colours); //recursion! run!
        }
    }
    
	public Color getColour(int index) {
        if (index >= itemCount) {
            setSizeScheme(index+1);
        }
        HashMap<Integer, Integer> clrMap = getColourMap();
        int i;
        if (clrMap.containsKey(index)) {
            i = clrMap.get(index);
        } else {
            UiPlugin.log("ColourScheme getColour("+index+") exceeded bounds", null); //$NON-NLS-1$ //$NON-NLS-2$
            i = 0; //return the first colour, instead of exploding
        }
        return palette.getColor(i, colourCount);
	}

	/**
	 * @return Returns all the available colours, without duplicates.
	 */
	public Color[] getAllColours() {
		return palette.getColors(colourCount);
	}
    
    public boolean equals(Object other) {
        if( !super.equals(other) )
            return false;
        if( !(other instanceof ColourScheme) )
            return false;
        
        ColourScheme schemeToCompare=(ColourScheme) other;
        if (schemeToCompare.getSizePalette() != colourCount)
            return false;
        if (schemeToCompare.getSizeScheme() != itemCount)
            return false;
        if (!schemeToCompare.getColourPalette().getName().equals(palette.getName())) //only compare name for the moment
            return false;
        for (int i = 0; i < itemCount; i++) {
            if (!schemeToCompare.getColourMap().get(i).equals(colourMap.get(i))) {
                return false;
            }
        }
        return true;
    }
    

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + colourCount;
        result = PRIME * result + itemCount;
        if( palette!=null && palette.getName()!=null )
        result = PRIME * result + palette.hashCode();
        for (int i = 0; i < itemCount; i++) {
            Integer integer = colourMap.get(i);
            result = PRIME * result + ((integer == null) ? 0 : integer.hashCode());
        }
        return result;
    }

    public BrewerPalette getColourPalette() {
        return palette;
    }

    public HashMap<Integer, Integer> getColourMap() {
        HashMap<Integer,Integer> colourMapping = new HashMap<Integer,Integer>();
        for (int i = 0; i < itemCount; i++) {
            if (colourMap.containsKey(i)) {
                colourMapping.put(i, colourMap.get(i));
            } else {
                if (i > colourCount) {
                    colourMapping.put(i,i%colourCount);
                } else {
                    colourMapping.put(i,i);
                }
            }
        }
        return colourMapping;
    }
    
    public HashMap<String, Integer> getIdMap() {
        return idMap;
    }
    
    public void setColourMap(HashMap<Integer, Integer> colourMap) {
        this.colourMap = colourMap;
        //TODO: synchronize size
    }
    
    public void swapColours(int firstIndex, int secondIndex) {
        if (firstIndex >= colourCount) {
            setSizeScheme(firstIndex+1);
        }
        if (secondIndex >= colourCount) {
            setSizeScheme(secondIndex+1);
        }
        int tempVal = colourMap.get(firstIndex);
        colourMap.put(firstIndex, colourMap.get(secondIndex));
        colourMap.put(secondIndex, tempVal);
    }
    
    public Color addItem() {
        int size = getSizeScheme();
        return getColour(size);
    }
    
    public Color addItem(String id) {
        int size = getSizeScheme();
        Color color = getColour(size);
        int index = indexOf(color);
        if (index > -1) {
            idMap.put(id, index);
        }
        return color;
    }
    
    /**
     * Add an item to the scheme, and modify the palette to contain this colour.
     *
     * @param color
     * @return
     */
    public void addItem(Color color) {
        //TODO
    }

    public int indexOf(String id) {
        if (idMap.containsKey(id)) {
            return idMap.get(id);
        } else {
            return -1;
        }
    }
    
    private int indexOf(Color color) {
        Iterator<Integer> it = colourMap.keySet().iterator();
        while (it.hasNext()) {
            Integer i = it.next();
            if (i < itemCount) { //don't modify the scheme!
                if (color.equals(getColour(i))) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public boolean removeItem(String id) {
        if (idMap.containsKey(id)) {
            int index = indexOf(id);
            idMap.remove(id);
            return removeItem(index);    
        }
        return false;
    }
    
    public boolean removeItem(String id, Color colour) {
        Set<Entry<String, Integer>> entries = idMap.entrySet();
        for (Entry<String, Integer> entry : entries) {
            if (id.equals(entry.getKey())) {
                if (colour.equals(getColour(entry.getValue()))) {
                    entries.remove(entry);
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean removeItem(int index) {
        if (index < 0) {
            return false;
        }
        if (idMap.containsValue(index)) {
            //optionally remove target
            Iterator<Entry<String, Integer>> it = idMap.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, Integer> entry = it.next();
                if (entry.getValue().equals(index)) {
                    idMap.remove(entry.getKey());
                }
            }
        }
        if (colourMap.containsKey(index)) {
            //remove the entry
            colourMap.remove(index);
            itemCount--;
        }
        
        return true;
    }
    
}
