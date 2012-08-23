/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 *      http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to licence under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.tools.feature.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Quantity;
import javax.measure.unit.BaseUnit;
import javax.measure.unit.Unit;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.osgi.framework.Bundle;

import eu.udig.tools.Activator;
import eu.udig.tools.internal.i18n.Messages;


/**
 * Class responsible of managing the most common used units.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * 
 */
public final class UnitList {

	
	private static UnitList THIS = null;
	/**
	 * Commonly used units of length measure
	 */
	private static Map<Unit<?>, String>	LENGTH_UNITS= new java.util.HashMap<Unit<?>, String> ();

	private static final String[][]		DEFAULT_LENGTH_UNITS	= { 
	        { "km", Messages.GeoToolsUtils_unitName_kilometers }, //$NON-NLS-1$ 
			{ "pixel", Messages.GeoToolsUtils_unitName_pixels }, //$NON-NLS-1$ 
			{ "ft", Messages.GeoToolsUtils_unitName_feet }, //$NON-NLS-1$ 
			{ "yd", Messages.GeoToolsUtils_unitName_yards }, //$NON-NLS-1$
			{ "in", Messages.GeoToolsUtils_unitName_inches }, //$NON-NLS-1$ 
			{ "cm", Messages.GeoToolsUtils_unitName_centimeters }, //$NON-NLS-1$ 
			{ "m", Messages.GeoToolsUtils_unitName_meters }, //$NON-NLS-1$ 
			{ "\u00B0", Messages.GeoToolsUtils_unitName_degrees } };//$NON-NLS-1$ 

	public static final Unit<?>			PIXEL_UNITS			= Unit.valueOf("pixel");//$NON-NLS-1$

	protected static final Logger		LOGGER				= Logger.getLogger(UnitList.class.getName());


	
	private UnitList(){
		// singleton 
	}
	
	public static synchronized UnitList getInstance() {

		if(THIS != null ){
			return THIS;
		} else {
			THIS = new UnitList();
			popultate();
		}
		return THIS;
	}
	
	private static void popultate()  {
		BufferedReader reader = null;
		try {

			for (int i = 0; i < DEFAULT_LENGTH_UNITS.length; i++) {
				Unit<?> unit = Unit.valueOf(DEFAULT_LENGTH_UNITS[i][0]);
				String unitName = DEFAULT_LENGTH_UNITS[i][1];
				LENGTH_UNITS.put(unit, unitName);
			}
			File crsFile = getCRSfile();
			String line = ""; //$NON-NLS-1$
			String totalReadedCrs = ""; //$NON-NLS-1$
			List<String> crsList = new LinkedList<String>();

			reader = new BufferedReader(new FileReader(
					crsFile.getAbsolutePath()));

			// the file contains only one line.
			while ((line = reader.readLine()) != null
					&& !line.trim().equals("")) { //$NON-NLS-1$

				// get the total crsReaded
				totalReadedCrs = line.substring(0, line.indexOf("|")); //$NON-NLS-1$
				line = line.substring(line.indexOf("|") + 1); //$NON-NLS-1$ 
				// fill the CRS list
				while (line.contains("|")) { //$NON-NLS-1$
					crsList.add(line.substring(0, line.indexOf("|"))); //$NON-NLS-1$
					line = line.substring(line.indexOf("|") + 1); //$NON-NLS-1$ 
				}
				// add the last part of the line
				crsList.add(line);
			}
			if (crsList.size() == 0) {
				fillcommonLengthUnits(crsFile);
			} else {
				// check if the total of read CRSs are the same.
				int totalCount = getTotalCrsCount();
				if (totalCount != Integer.parseInt(totalReadedCrs)) {// different
					// load all and create the .properties file again.
					fillcommonLengthUnits(crsFile);
				} else {
					loadFromProperties(crsList);
				}
			}
			LENGTH_UNITS = Collections
					.unmodifiableMap(LENGTH_UNITS);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING,  e.getMessage(), e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Returns a set of the most commonly used units of measure for measuring
	 * lengths at a GIS application scale
	 * 
	 * @return a set of the most common units to use in operations like buffer, Parallels, etc.
	 * @throws Exception 
	 */
	public synchronized Set<Unit<?>> getCommonLengthUnits() throws Exception {

		if (LENGTH_UNITS != null) {
			return LENGTH_UNITS.keySet();
		} else {
			return Collections.emptySet();
		}
	}

	private static void loadFromProperties(List<String> crsList) throws Exception {

		for (String crsUnit : crsList) {

			Unit<?> unit = new BaseUnit<Quantity>(crsUnit);

			if (!LENGTH_UNITS.containsKey(unit)) {
				LENGTH_UNITS.put(unit, unit.toString());
			}
		}
	}

	private static int getTotalCrsCount() {

		int crsCount = 0;
		for (Object object : ReferencingFactoryFinder.getCRSAuthorityFactories(null)) {
			CRSAuthorityFactory factory = (CRSAuthorityFactory) object;
			try {
				Set<String> codes = factory.getAuthorityCodes(CoordinateReferenceSystem.class);
				crsCount = crsCount + codes.size();
			} catch (Exception e) {
				LOGGER.warning("exception" + e.getMessage()); //$NON-NLS-1$
			}
		}
		return crsCount;
	}

	private static File getCRSfile() throws IOException {

		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);

		URL internal = bundle.getEntry("crs.properties"); //$NON-NLS-1$
		URL fileUrl = FileLocator.toFileURL(internal);

		String externalForm = fileUrl.toExternalForm();
		String path = externalForm.replaceFirst("file:", ""); //$NON-NLS-1$//$NON-NLS-2$

		return new File(path);
	}

	private  static void fillcommonLengthUnits(File crsFile) throws IOException {

		int crsCount = 0;
		StringBuilder crsSequence = new StringBuilder(); 

		for (Object object : ReferencingFactoryFinder.getCRSAuthorityFactories(null)) {
			CRSAuthorityFactory factory = (CRSAuthorityFactory) object;
			try {
				CoordinateReferenceSystem crs;
				Set<String> codes = factory.getAuthorityCodes(CoordinateReferenceSystem.class);
				for (String code : codes) {

					crsCount++;
					try {
						crs = factory.createCoordinateReferenceSystem(code);
						Unit<?> unit = GeoToolsUtils.getDefaultCRSUnit(crs);
						if (!LENGTH_UNITS.containsKey(unit)) {
							LENGTH_UNITS.put(unit, unit.toString());

							Unit<?> baseUnit = unit.getStandardUnit();

							UnitConverter multiply = unit.toStandardUnit();
							crsSequence.append("|" + unit.toString()); //$NON-NLS-1$
						}
					} catch (Exception e) {
						LOGGER.warning("exception" + e.getMessage()); //$NON-NLS-1$
					}
				}
			} catch (Exception e) {
				LOGGER.warning("exception" + e.getMessage()); //$NON-NLS-1$
			}
		}
		String finalcrsSequence = crsCount + crsSequence.toString();
		BufferedWriter writer = new BufferedWriter(new FileWriter(crsFile.getAbsolutePath()));
		writer.write(finalcrsSequence);
		writer.close();
	}

	/**
	 * Returns the localized unit name
	 * 
	 * @param unit
	 *            the unit
	 * @return the localized
	 */
	public synchronized String getUnitName(final Unit<?> unit) {

		assert unit != null;

		String unitName = LENGTH_UNITS.get(unit);

		assert unitName != null : "unit name cannot be null"; //$NON-NLS-1$

		return unitName;
	}

}
