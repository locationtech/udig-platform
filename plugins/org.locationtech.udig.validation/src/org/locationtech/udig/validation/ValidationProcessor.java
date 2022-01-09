/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.validation;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.validation.FeatureValidation;
import org.geotools.validation.IntegrityValidation;
import org.geotools.validation.PlugIn;
import org.geotools.validation.Validation;
import org.geotools.validation.ValidationResults;
import org.geotools.validation.dto.ArgumentDTO;
import org.geotools.validation.dto.PlugInDTO;
import org.geotools.validation.dto.TestDTO;
import org.geotools.validation.dto.TestSuiteDTO;
import org.geotools.validation.xml.ValidationException;
import org.geotools.validation.xml.XMLReader;
import org.locationtech.udig.project.ILayer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;

/**
 * Subclass for the GeoTools ValidationProcessor, with added methods which allow for tree
 * navigation, etc. For the most part, this adds more baggage to the class so the Validation Dialog
 * can get information out of it.
 *
 * @author chorner
 * @since 1.0.1
 * @see org.geotools.validation.ValidationProcessor
 */
public class ValidationProcessor extends org.geotools.validation.ValidationProcessor {
    final Object ANYTYPENAME = new Object(); // copy of the ANYTYPENAME object

    private Map<String, PlugInDTO> pluginDTOs;

    private Map<String, TestSuiteDTO> testSuiteDTOs;

    private Map<String, ArgumentDTO> allArgs;

    /**
     * Constructor for the uDig ValidationProcessor subclass. The plugins parameter is required, but
     * the testSuites variable may be a null File object (a blank testSuite will be created).
     *
     * @param pluginsDir (directory containing pluginSchema XML files)
     * @param testSuites (testSuite file or a directory)
     * @throws Exception
     */
    public ValidationProcessor(File pluginsDir, File testSuiteFile) throws Exception {
        super();
        // load the pluginSchema files
        Map<String, PlugInDTO> pluginDTOs = XMLReader.loadPlugIns(pluginsDir);
        // load or create the testSuite
        Map<String, TestSuiteDTO> testSuiteDTOs;
        boolean fileExists = true;
        if (testSuiteFile == null)
            fileExists = false;
        else if (testSuiteFile.exists())
            fileExists = false;
        if (fileExists) {
            testSuiteDTOs = XMLReader.loadValidations(testSuiteFile, pluginDTOs);
        } else {
            // construct a map for DTO testSuites (empty)
            testSuiteDTOs = new HashMap<>();
            TestSuiteDTO testSuite1 = new TestSuiteDTO();
            testSuite1.setName("testSuite1"); //$NON-NLS-1$
            testSuite1.setDescription(""); //$NON-NLS-1$
            // create an empty map of tests (required!)
            Map emptyTestsMap = new HashMap();
            testSuite1.setTests(emptyTestsMap);
            // store it
            testSuiteDTOs.put("testSuite1", testSuite1); //$NON-NLS-1$
        }
        // save the plugins and testSuites for future use
        this.pluginDTOs = pluginDTOs;
        this.testSuiteDTOs = testSuiteDTOs;
        // load the ValidationProcessor
        load(pluginDTOs, testSuiteDTOs);
    }

    /**
     * Adds a testDTO validation to the testSuiteDTO, and calls addValidation from the superclass.
     *
     * @param validation FeatureValidation object
     * @param testSuiteDTOKey ID object (the key of the testSuiteDTO as referenced in testSuiteDTOs)
     * @see org.geotools.validation.ValidationProcessor.addValidation
     */
    public void addValidation(Validation validation, PlugInDTO plugin, Object testSuiteDTOKey) {
        // call the appropriate superclass method to add the validation to the feature/integrity
        // lookup
        if (validation instanceof FeatureValidation) {
            FeatureValidation FV = (FeatureValidation) validation;
            super.addValidation(FV);
        } else if (validation instanceof IntegrityValidation) {
            IntegrityValidation IV = (IntegrityValidation) validation;
            super.addValidation(IV);
        }
        // ensure that a plugin is present
        if (plugin == null) {
            String errorMsg = "Validation plugin '" + validation.getName() + "' not found"; //$NON-NLS-1$ //$NON-NLS-2$
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                    "Problem Occurred", errorMsg); //$NON-NLS-1$
            ValidationPlugin.log(errorMsg, new ValidationException());
            return;
        }
        // create a testDTO containing the new validation test and args
        TestDTO newTest = new TestDTO();
        // set the name, desc, plugin, and args
        newTest.setName(validation.getName());
        newTest.setDescription(validation.getDescription());
        newTest.setPlugIn(plugin);
        // create a copy of the map and args
        // note: this copies the args from the PlugInDTO
        // Map oldArgs = plugin.getArgs();
        Map<String, ArgumentDTO> oldArgs = allArgs;
        Map<String, ArgumentDTO> newArgs = new HashMap<>();
        for (Iterator i = oldArgs.keySet().iterator(); i.hasNext();) {
            ArgumentDTO oldArg = oldArgs.get(i.next());
            ArgumentDTO newArg = (ArgumentDTO) oldArg.clone();
            newArgs.put(newArg.getName(), newArg);
        }
        // store the new args
        newTest.setArgs(newArgs);
        // add the new test to the appropriate testSuite
        Map<String, TestDTO> tests = testSuiteDTOs.get(testSuiteDTOKey).getTests();
        tests.put(newTest.getName(), newTest);
        testSuiteDTOs.get(testSuiteDTOKey).setTests(tests);
    }

    public boolean renameValidation(String oldKey, String newKey, Object testSuiteDTOKey) {
        Map<String, TestDTO> tests = testSuiteDTOs.get(testSuiteDTOKey).getTests();
        if (oldKey.equals(newKey))
            return true; // no change
        if (tests.containsKey(newKey))
            return false; // duplicate key -- abort!
        TestDTO test = tests.remove(oldKey);
        test.setName(newKey);
        tests.put(newKey, test);
        return true;
    }

    /**
     * Removes a validation from its testSuiteDTO and from the FV/IV Lookups
     *
     * @param validation
     */
    public void removeValidation(TestDTO test) {
        String testName = test.getName();
        // PlugInDTO plugin = test.getPlugIn();
        // remove from FVLookup
        Map FVLookup = featureLookup;
        for (Iterator i = FVLookup.keySet().iterator(); i.hasNext();) {
            Object something;
            Object currentItem = i.next();
            something = FVLookup.get(currentItem);
            if (something != null) {
                ArrayList tests = (ArrayList) something;
                for (Object thisTest : tests) {
                    Validation thisValidation = (Validation) thisTest;
                    if (thisValidation.getName().equalsIgnoreCase(testName)) {
                        featureLookup.remove(currentItem);
                    }
                }
            }
        }
        // remove from IVLookup
        Map IVLookup = integrityLookup;
        for (Iterator i = IVLookup.keySet().iterator(); i.hasNext();) {
            Object something;
            Object currentItem = i.next();
            something = IVLookup.get(currentItem);
            if (something != null) {
                ArrayList tests = (ArrayList) something;
                for (Object thisTest : tests) {
                    Validation thisValidation = (Validation) thisTest;
                    if (thisValidation.getName().equalsIgnoreCase(testName)) {
                        integrityLookup.remove(currentItem);
                    }
                }
            }
        }
        // remove from all testSuites
        Map oldTests;
        Map newTests; // = new HashMap();
        // for each testSuite
        for (Iterator i = testSuiteDTOs.keySet().iterator(); i.hasNext();) {
            TestSuiteDTO testSuite = testSuiteDTOs.get(i.next());
            oldTests = testSuite.getTests();
            newTests = new HashMap(testSuite.getTests()); // create a new copy of the object, so we
                                                          // can modify it
            // for each test
            for (Iterator j = ((HashMap) oldTests).keySet().iterator(); j.hasNext();) {
                Object testKey = j.next();
                TestDTO currentTest = (TestDTO) oldTests.get(testKey);
                if (currentTest.equals(test)) {
                    newTests.remove(testKey); // this is the test we want to delete
                }
            }
            testSuite.setTests(newTests); // save the modified map of tests in the testSuite
        }
    }

    /**
     * Runs a single feature validation test
     *
     * @param testName
     * @param layers
     * @param results
     * @param monitor
     * @throws Exception
     */
    public void runFeatureTest(Object testName, ILayer[] layers, ValidationResults results,
            IProgressMonitor monitor) throws Exception {

        // get the validator from testKey
        FeatureValidation validator = null;
        // (navigate through the featureLookup until we find an instance of the test)
        for (Iterator i = featureLookup.keySet().iterator(); i.hasNext();) {
            ArrayList testList = (ArrayList) featureLookup.get(i.next());
            // iterate through each item in the list
            for (Object thisTest : testList) {
                Validation test = (Validation) thisTest;
                // this is the matching validation for the given test
                if (test.getName().equals(testName)) {
                    validator = (FeatureValidation) test;
                    break;
                }
            }
        }

        // run the test
        if (validator != null) // if we found the test
        {
            results.setValidation(validator);
            // get a list of typeRefs to figure out which layers to run the test on
            String[] typeRefs = validator.getTypeRefs();
            if (typeRefs == null) {
                // unfortunately, ALL typeRefs = null; we'll override that for now
                typeRefs = new String[1];
                typeRefs[0] = "*"; //$NON-NLS-1$
                // } else if (typeRefs.length == 0) {
                // return; //TODO: add messageBox "there are no typeRefs!"
            }
            Set<ILayer> relevantLayers = new HashSet<>();
            for (int i = 0; i < typeRefs.length; i++) {
                String typeRef = typeRefs[i];
                if (typeRef.equals("") || (typeRef.equals("*"))) { //$NON-NLS-1$//$NON-NLS-2$
                    // wildcard (I assume); add all layers to the relevantLayers and break
                    for (int j = 0; j < layers.length; j++) {
                        ILayer thisLayer = layers[j];
                        relevantLayers.add(thisLayer);
                    }
                    break;
                }
                // find the layer that matches the typeRef
                for (int j = 0; j < layers.length; j++) {
                    ILayer thisLayer = layers[j];
                    // make the typeRef
                    SimpleFeatureType schema = thisLayer.getSchema();
                    if (schema == null)
                        continue;

                    String dataStoreID = schema.getName().getNamespaceURI();
                    String thisTypeRef = dataStoreID + ":" + schema.getName().getLocalPart(); //$NON-NLS-1$
                    // if the typeRefs match, add the layer to our set
                    if (thisTypeRef.equals(typeRef)) {
                        relevantLayers.add(thisLayer);
                        break;
                    }
                }
            }

            // for each relevant layer
            for (Iterator k = relevantLayers.iterator(); k.hasNext();) {
                ILayer thisLayer = (ILayer) k.next();
                // get the SimpleFeatureType
                SimpleFeatureType type = thisLayer.getSchema();
                // create a FeatureReader (collection.reader)
                FeatureSource<SimpleFeatureType, SimpleFeature> source;
                source = thisLayer.getResource(FeatureSource.class, monitor);
                FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source
                        .getFeatures();
                // hmm... pretty pictures or efficiency?
                int count = collection.size();
                monitor.beginTask("", count); //$NON-NLS-1$
                FeatureIterator<SimpleFeature> reader = collection.features();
                // iterate through each feature and run the test on it
                int position = 0;
                while (reader.hasNext()) {
                    // check for the cancel button
                    if (monitor.isCanceled()) {
                        reader.close();
                        break;
                    }
                    // validate this feature
                    monitor.subTask(++position + "/" + count); //$NON-NLS-1$
                    SimpleFeature feature = reader.next();
                    try {
                        validator.validate(feature, type, results);
                    } catch (Throwable e) {
                        results.error(feature, e.getMessage());
                    }
                    monitor.worked(1);
                }
                reader.close();
            }
        }
    }

    /**
     * Runs a single integrity validation test
     *
     * @param testName
     * @param layers
     * @param results
     * @param monitor
     * @throws Exception
     */
    public void runIntegrityTest(Object testName, ILayer[] layers, ValidationResults results,
            IProgressMonitor monitor) throws Exception {

        // get the validator from testKey
        IntegrityValidation validator = null;
        // (navigate through the featureLookup until we find an instance of the test)
        for (Iterator i = integrityLookup.keySet().iterator(); i.hasNext();) {
            ArrayList testList = (ArrayList) integrityLookup.get(i.next());
            // iterate through each item in the list
            for (Object thisTest : testList) {
                Validation test = (Validation) thisTest;
                // this is the matching validation for the given test
                if (test.getName().equals(testName)) {
                    validator = (IntegrityValidation) test;
                    break;
                }
            }
        }
        if (validator != null) // if we found the test
        {
            results.setValidation(validator);
            ReferencedEnvelope envelope = layers[0].getMap().getViewportModel().getBounds();
            FeatureSource<SimpleFeatureType, SimpleFeature> source;
            String nameSpace;
            String typeName;
            Map<String, FeatureSource<SimpleFeatureType, SimpleFeature>> stores = new HashMap<>();
            for (int i = 0; i < layers.length; i++) {
                nameSpace = layers[i].getSchema().getName().getNamespaceURI();
                typeName = layers[i].getSchema().getName().getLocalPart();
                source = layers[i].getResource(FeatureSource.class, monitor);
                String typeRef = nameSpace.toString() + ":" + typeName; //$NON-NLS-1$
                stores.put(typeRef, source);
            }
            // run the test
            validator.validate(stores, envelope, results);
        }
    }

    /**
     * Runs all feature tests by iterating through the list of layers, and calling runFeatureTests()
     * on each layer.
     *
     * @see org.geotools.validation.ValidationProcessor runFeatureTests()
     *
     * @param layers
     * @param results
     * @param monitor
     * @throws Exception
     */
    public void runAllFeatureTests(ILayer[] layers, ValidationResults results,
            IProgressMonitor monitor) throws Exception {

        // for each layer
        for (int i = 0; i < layers.length; i++) {
            ILayer thisLayer = layers[i];

            // get the dataStoreID
            String dataStoreID = thisLayer.getSchema().getName().getNamespaceURI();

            // create a FeatureReader (collection.reader)
            SimpleFeatureSource source;
            source = thisLayer.getResource(SimpleFeatureSource.class, monitor);
            SimpleFeatureCollection collection = source.getFeatures();

            // run the tests on this layer
            runFeatureTests(dataStoreID, collection, results);
        }
    }

    /**
     * Runs all integrity tests (prepares and calls runIntegrityTests)
     *
     * @see org.geotools.validation.ValidationProcessor runIntegrityTests()
     *
     * @param layers
     * @param results
     * @param monitor
     * @throws Exception
     */
    public void runAllIntegrityTests(ILayer[] layers, ValidationResults results,
            IProgressMonitor monitor) throws Exception {

        // FIXME: take Map as input rather than layer(s)?
        ReferencedEnvelope envelope = layers[0].getMap().getViewportModel().getBounds();
        FeatureSource<SimpleFeatureType, SimpleFeature> source;
        String nameSpace;
        String typeName;
        Map<Name, FeatureSource<SimpleFeatureType, SimpleFeature>> stores = new HashMap<>();
        Set<Name> typeRefs = new HashSet<>();
        for (int i = 0; i < layers.length; i++) {
            Name name = layers[i].getSchema().getName();
            nameSpace = name.getNamespaceURI();
            typeName = name.getLocalPart();
            source = layers[i].getResource(FeatureSource.class, monitor);
            // map = dataStoreID:typeName
            String typeRef = nameSpace.toString() + ":" + typeName; //$NON-NLS-1$
            stores.put(name, source);
            typeRefs.add(name);
        }

        // run the tests
        runIntegrityTests(typeRefs, stores, envelope, results);
    }

    /**
     * Creates a new validation test of the correct type when passed the plugInDTO.
     *
     * @param INSTANCE
     * @return
     * @throws ValidationException
     * @throws ClassNotFoundException
     */
    public Validation createValidation(PlugInDTO dto)
            throws ValidationException, ClassNotFoundException {
        // create a PlugIn from a PlugInDTO
        Class plugInClass = null;
        plugInClass = Class.forName(dto.getClassName());
        PlugIn plugIn = new PlugIn(dto.getName(), plugInClass, dto.getDescription(), dto.getArgs());
        // copy the arguments over to the new object
        // NOTE: this may not be necessary, but we've cloned each argument and
        // hashmap here to ensure each new validation has its own unique argument
        // (and are not accidentally shared).
        Map<String, ArgumentDTO> oldArgs = dto.getArgs();
        Map<String, ArgumentDTO> newArgs = new HashMap<>();
        for (Iterator i = oldArgs.keySet().iterator(); i.hasNext();) {
            ArgumentDTO oldArg = oldArgs.get(i.next());
            ArgumentDTO newArg = (ArgumentDTO) oldArg.clone();
            newArgs.put(newArg.getName(), newArg);
        }
        // we have the default args, but we'll scan the complete list of args
        // and add any that are missing
        Map allArgs = plugIn.getPropertyMap();
        for (Iterator i = allArgs.keySet().iterator(); i.hasNext();) {
            Object thisArg = allArgs.get(i.next());

            if (thisArg instanceof PropertyDescriptor) {
                PropertyDescriptor thisElement = ((PropertyDescriptor) thisArg);
                String argName = thisElement.getName();
                Object argValue = thisElement.getValue(argName);
                // add keys to the map which do not contain "name", "description", or an existing
                // key
                if (!(newArgs.containsKey(argName)) && !(argName.equals("name")) //$NON-NLS-1$
                        && !(argName.equals("description"))) { //$NON-NLS-1$
                    ArgumentDTO newArg = new ArgumentDTO();
                    newArg.setName(argName);
                    newArg.setValue(argValue);
                    newArgs.put(newArg.getName(), newArg);
                }
            }
        }
        // store the complete list of Args for future use (will be overwritten by the latest test
        // creation)
        this.allArgs = newArgs;
        // create a new validation
        Validation validation = plugIn.createValidation(getUniqueName(getTests(), "Test"), //$NON-NLS-1$
                dto.getDescription(), newArgs);
        return validation;
    }

    /**
     * Regenerates the FV Lookup Map based on the contents of the testSuite
     *
     */
    public void updateFVLookup() {
        // get all validation tests from the featureLookup
        Set<Validation> validations = new HashSet<>();
        for (Iterator i = featureLookup.keySet().iterator(); i.hasNext();) {
            ArrayList list = (ArrayList) featureLookup.get(i.next());
            for (int j = 0; j < list.size(); j++) {
                Validation test = (Validation) list.get(j);
                validations.add(test);
            }
        }

        // clear the FV Lookup
        featureLookup.clear();

        // add each test to the Lookup (again)
        for (Iterator i = validations.iterator(); i.hasNext();) {
            Validation validation = (Validation) i.next();
            if (validation instanceof FeatureValidation) {
                // FeatureValidation FV = (FeatureValidation) validation;
                // addToFVLookup(FV); // <-- this is the proper way to do this

                // NOTE: code below is a verbatim copy of addToFVLookup(), which is private
                // TODO: change org.geotools.validation.ValidationProcessor.addToFVLookup() to
                // public or protected?
                String[] featureTypeList = validation.getTypeRefs();

                if (featureTypeList == Validation.ALL) // if null (ALL)
                {
                    ArrayList<Validation> tests = (ArrayList) featureLookup.get(ANYTYPENAME);

                    if (tests == null) { // if an ALL test doesn't exist yet
                        tests = new ArrayList<>(); // create it
                    }

                    tests.add(validation);
                    featureLookup.put(ANYTYPENAME, tests); // add the ALL test
                                                           // to it
                } else // a non ALL FeatureTypeInfo validation
                {
                    for (int j = 0; j < featureTypeList.length; j++) {
                        ArrayList<Validation> tests = (ArrayList) featureLookup
                                .get(featureTypeList[j]);
                        if (tests == null) { // if this FeatureTypeInfo doesn't have a validation
                                             // test yet
                            tests = new ArrayList(); // put it in the list
                        }
                        tests.add(validation);
                        featureLookup.put(featureTypeList[j], tests); // add a validation to it
                    }
                }
            }
        }
    }

    public void updateIVLookup() {
        // get all validation tests from the integrityLookup
        Set<Validation> validations = new HashSet<>();
        for (Iterator i = integrityLookup.keySet().iterator(); i.hasNext();) {
            ArrayList list = (ArrayList) integrityLookup.get(i.next());
            for (int j = 0; j < list.size(); j++) {
                Validation test = (Validation) list.get(j);
                validations.add(test);
            }
        }

        // clear the IV Lookup
        integrityLookup.clear();

        // add each test to the Lookup (again)
        for (Iterator i = validations.iterator(); i.hasNext();) {
            Validation validation = (Validation) i.next();
            if (validation instanceof IntegrityValidation) {
                // IntegrityValidation IV = (IntegrityValidation) validation;
                // addToIVLookup(IV); // <-- this is the proper way to do this

                // NOTE: code below is a verbatim copy of addToIVLookup(), which is private
                // TODO: change org.geotools.validation.ValidationProcessor.addToIVLookup() to
                // public or protected?
                String[] integrityTypeList = validation.getTypeRefs();

                if (integrityTypeList == Validation.ALL) // if null (ALL)
                {
                    ArrayList<Validation> tests = (ArrayList) integrityLookup.get(ANYTYPENAME);
                    if (tests == null) { // if an ALL test doesn't exist yet
                        tests = new ArrayList<>(); // create it
                    }
                    tests.add(validation);
                    integrityLookup.put(ANYTYPENAME, tests); // add the ALL test to it
                } else {
                    for (int j = 0; j < integrityTypeList.length; j++) {
                        ArrayList<Validation> tests = (ArrayList) integrityLookup
                                .get(integrityTypeList[j]);
                        if (tests == null) { // if this FeatureTypeInfo doesn't have a validation
                                             // test yet
                            tests = new ArrayList<>(); // put it in the list
                        }
                        tests.add(validation);
                        integrityLookup.put(integrityTypeList[j], tests); // add a validation to it
                    }
                }
            }
        }
    }

    /**
     * Returns a unique name for an automatically generated Test (Test1, Test2, etc), or where
     * labelPrefix is typically "Test"
     *
     * @param allItems
     * @param labelPrefix
     * @return
     */
    public String getUniqueName(Map allItems, String labelPrefix) {
        String currentName;
        for (int i = 1; i < 10000; i++) {
            // check to see if Test{i} is in use
            currentName = labelPrefix + i;
            if (!allItems.containsKey(currentName)) {
                return currentName;
            }
        }
        return null;
    }

    /**
     * Places a Map of tests one-by-one into a TestSuiteDTO. If an equal test already exists in the
     * testSuite, it is ignored.
     *
     * @param suite
     * @param tests
     * @param allDupes
     * @return
     */
    public TestSuiteDTO moveTests(TestSuiteDTO suite, Map<String, TestDTO> tests,
            boolean allowDupes) {
        Map<String, TestDTO> someTests = tests; // tests which are to be moved
        Map<String, TestDTO> allTests = suite.getTests(); // list of tests already in the suite
        // for each test
        for (Iterator i = someTests.keySet().iterator(); i.hasNext();) {
            // clone the test
            TestDTO currentTest = new TestDTO(someTests.get(i.next()));
            // if the current test does not exist, add it
            if (!(allTests.containsKey(currentTest.getName()))) {
                allTests.put(currentTest.getName(), currentTest);
                // if the current test does exist, rename if not identical (or allowDupes is true)
            } else if (allowDupes || !(allTests.get(currentTest.getName()).equals(currentTest))) {
                String newName = getUniqueName(tests, "Test"); //$NON-NLS-1$
                currentTest.setName(newName);
                allTests.put(currentTest.getName(), currentTest);
            }
        }
        suite.setTests(allTests);
        return suite;
    }

    /**
     * Returns a Set (HashSet) of plugins (validation tests) available.
     */
    public Set getPlugins() {
        HashMap hashMap = (HashMap) pluginDTOs;
        Set<PlugInDTO> plugins = new HashSet<>();
        for (Iterator i = hashMap.keySet().iterator(); i.hasNext();) {
            PlugInDTO plugin = (PlugInDTO) hashMap.get(i.next());
            plugins.add(plugin);
        }
        return plugins;
    }

    /**
     * Returns a complete list of available tests (all testSuites are merged)
     *
     * @return Map of tests
     */
    public Map getTests() {
        Map<String, TestDTO> testMap = new HashMap<>();
        // for each testSuite
        for (Iterator i = testSuiteDTOs.keySet().iterator(); i.hasNext();) {
            TestSuiteDTO testSuite = testSuiteDTOs.get(i.next());
            // for each test
            for (Iterator j = ((HashMap) testSuite.getTests()).keySet().iterator(); j.hasNext();) {
                TestDTO currentTest = (TestDTO) testSuite.getTests().get(j.next());
                testMap.put(currentTest.getName(), currentTest);
            }
        }
        return testMap;
    }

    /**
     * Returns an array of tests relevant to the plugin
     *
     * @param plugin
     * @return
     */
    public Object[] getTests(Object plugin) {
        Set<TestDTO> testSet = new HashSet<>();
        // for each testSuite
        for (Iterator i = testSuiteDTOs.keySet().iterator(); i.hasNext();) {
            TestSuiteDTO testSuite = testSuiteDTOs.get(i.next());
            // for each test
            for (Iterator j = ((HashMap) testSuite.getTests()).keySet().iterator(); j.hasNext();) {
                TestDTO currentTest = (TestDTO) testSuite.getTests().get(j.next());
                PlugInDTO thisPlugin = (PlugInDTO) plugin;
                // matching plugin?
                if (currentTest.getPlugIn().equals(thisPlugin)) {
                    testSet.add(currentTest);
                }
            }
        }
        return testSet.toArray();
    }

    /**
     * Determines if a given testSuite contains any tests or not.
     *
     * @param testSuiteKey
     * @return
     */
    public boolean testsExist(Object testSuiteKey) {
        TestSuiteDTO testSuite = testSuiteDTOs.get(testSuiteKey);
        return !testSuite.getTests().isEmpty();
    }

    public void setArg(TestDTO test, ArgumentDTO arg)
            throws ValidationException, IntrospectionException {
        Map FVLookup = featureLookup;
        Map<String, Object> args = new HashMap<>();
        args.put(arg.getName(), arg.getValue()); // value = argDTO or Object?
        // iterate through each item in the map (should contain a single ArrayList)
        for (Iterator i = FVLookup.keySet().iterator(); i.hasNext();) {
            ArrayList testList = (ArrayList) FVLookup.get(i.next());
            // iterate through each item in the list
            for (Object thisTest : testList) {
                Validation validation = (Validation) thisTest;
                // this is the matching validation for the given test
                if (validation.getName().equals(test.getName())) {
                    // create a property descriptor containing the argument name and plugin class
                    PropertyDescriptor property = new PropertyDescriptor(arg.getName(),
                            validation.getClass());
                    if (property == null) {
                        // error here
                        continue;
                    }
                    // store the value of the argument in the property descriptor
                    try {
                        property.getWriteMethod().invoke(validation,
                                new Object[] { arg.getValue() });
                    } catch (IllegalArgumentException e) {
                        String val = arg.getValue() == null ? arg.getValue().toString() : "null"; //$NON-NLS-1$
                        throw new ValidationException("test failed to configure " //$NON-NLS-1$
                                + validation.getClass().getSimpleName() + " " + arg.getName() + " " //$NON-NLS-1$ //$NON-NLS-2$
                                + val, e);
                    } catch (IllegalAccessException e) {
                        String val = arg.getValue() == null ? arg.getValue().toString() : "null"; //$NON-NLS-1$
                        throw new ValidationException("test failed to configure " //$NON-NLS-1$
                                + validation.getClass().getSimpleName() + " " + arg.getName() + " " //$NON-NLS-1$ //$NON-NLS-2$
                                + val, e);
                    } catch (InvocationTargetException e) {
                        String val = arg.getValue() == null ? arg.getValue().toString() : "null"; //$NON-NLS-1$
                        throw new ValidationException("test failed to configure " //$NON-NLS-1$
                                + validation.getClass().getSimpleName() + " " + arg.getName() + " " //$NON-NLS-1$ //$NON-NLS-2$
                                + val, e);
                    }
                }
            }
        }
    }

    public Map getPluginDTOs() {
        return pluginDTOs;
    }

    public void setPluginDTOs(Map<String, PlugInDTO> pluginDTOs) {
        this.pluginDTOs = pluginDTOs;
    }

    public Map<String, TestSuiteDTO> getTestSuiteDTOs() {
        return testSuiteDTOs;
    }

    public void setTestSuiteDTOs(Map<String, TestSuiteDTO> testSuiteDTOs) {
        this.testSuiteDTOs = testSuiteDTOs;
    }

}
