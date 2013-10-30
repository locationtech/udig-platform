/* Image Georeferencing
 * 
 * Axios Engineering 
 *      http://www.axios.es 
 *
 * (C) 2011, Axios Engineering S.L. (Axios)
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package eu.udig.image.georeferencing.ui;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.udig.image.georeferencing.internal.process.MarkModel;
import eu.udig.image.georeferencing.internal.process.MarkModelFactory;

/**
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.3
 * 
 */
final class MarkStorage {

	private Properties	propertiesMarks	= new Properties();
	private int			index;
	private int			highestIndex	= -1;
	private String		loadedCrsName	= "";				//$NON-NLS-1$

	public boolean canLoadMarks(final String path, final CoordinateReferenceSystem crs) throws IOException {

		Reader reader = null;
		try {

			reader = new FileReader(path);
			this.propertiesMarks.load(reader);

			loadedCrsName = this.propertiesMarks.getProperty("0"); //$NON-NLS-1$
			String actualCrs = crs.getName().getCode();
			if (loadedCrsName.equals(actualCrs)) {
				return true;
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return false;
	}

	public String getLoadedCrsName() {

		assert !"".equals(loadedCrsName); //$NON-NLS-1$

		return loadedCrsName;
	}

	public Map<String, MarkModel> loadMarks(final String path) throws IOException {

		Reader reader = null;
		try {

			Map<Integer, MarkModel> unsortedMarkMap = new HashMap<Integer, MarkModel>();

			reader = new FileReader(path);
			this.propertiesMarks.load(reader);

			Set<Entry<Object, Object>> entrySet = this.propertiesMarks.entrySet();
			Iterator<Entry<Object, Object>> iter = entrySet.iterator();
			while (iter.hasNext()) {

				Entry<Object, Object> entry = iter.next();
				String key = (String) entry.getKey();
				if (key.equals("0")) { //$NON-NLS-1$
					continue; // This is the key used to store the CRS. Don't
								// load like a mark.
				}

				String markValues = (String) entry.getValue();
				// load the marks
				MarkModel mark = parseMarkData(markValues);
				unsortedMarkMap.put(index, mark);
			}

			return sortMap(unsortedMarkMap);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

	}

	private Map<String, MarkModel> sortMap(Map<Integer, MarkModel> unsortedMarkMap) {

		Map<String, MarkModel> sortedMap = new LinkedHashMap<String, MarkModel>();
		for (int i = 1; i <= highestIndex; i++) {

			MarkModel mark = unsortedMarkMap.get(i);
			String key = mark.getID();
			sortedMap.put(key, mark);
		}

		return sortedMap;
	}

	private MarkModel parseMarkData(String markValues) {

		MarkModel newMark = null;

		StringTokenizer tokenizer = new StringTokenizer(markValues, ";"); //$NON-NLS-1$
		String id = tokenizer.nextToken();

		// we don't know how many values have been set to the model, so try to
		// get all of the values catching the exception nextToken throws
		// when there aren't any more tokens
		try {
			Integer xImage = Integer.parseInt(tokenizer.nextToken());
			Integer yImage = Integer.parseInt(tokenizer.nextToken());
			Double xCoord = Double.parseDouble(tokenizer.nextToken());
			Double yCoord = Double.parseDouble(tokenizer.nextToken());
			newMark = MarkModelFactory.getInstance().create(id, xImage, yImage, xCoord, yCoord);

			this.index = Integer.parseInt(tokenizer.nextToken());
		} catch (NoSuchElementException e) {
			// do nothing
		}
		// keep the highest index.
		if (this.index > this.highestIndex) {
			this.highestIndex = this.index;
		}

		return newMark;

	}

	/**
	 * Saves the mark model list in file
	 * 
	 * @param marks	 
	 * @param crs	the map CRS
	 * @param fileName	
	 * @throws IOException
	 */
	public void saveMarks(final Map<String, MarkModel> marks, final CoordinateReferenceSystem crs, final String fileName)
		throws IOException {

		Writer out = null;
		try {

			// save the CRS, special case, save it under key 0
			this.propertiesMarks.setProperty("0", crs.getName().getCode()); //$NON-NLS-1$

			Set<Entry<String, MarkModel>> entrySet = marks.entrySet();
			Iterator<Entry<String, MarkModel>> iter = entrySet.iterator();
			int order = 1;
			while (iter.hasNext()) {
				Entry<String, MarkModel> entry = iter.next();

				String key = entry.getKey();
				MarkModel mark = entry.getValue();

				this.propertiesMarks.setProperty(key, mark.toString() + ";" + order); //$NON-NLS-1$
				order++;
			}
			out = new FileWriter(fileName);

			this.propertiesMarks.store(out, "georeferencing marks"); //$NON-NLS-1$
		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

}
