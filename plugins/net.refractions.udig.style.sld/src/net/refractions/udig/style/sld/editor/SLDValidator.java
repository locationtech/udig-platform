/* Copyright (c) 2001, 2003 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license.
 */
package net.refractions.udig.style.sld.editor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.style.sld.internal.Messages;

//import org.apache.xerces.parsers.SAXParser;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SLDValidator borrowed from geoserver org.vfny.geoserver.util
 * <p>
 *
 * </p>
 * @author chorner
 * @since 1.1.0
 */
public class SLDValidator {
    public SLDValidator() {
    }

    public static String getErrorMessage(InputStream xml, List<SAXParseException> errors) {
        return getErrorMessage(new InputStreamReader(xml), errors);
    }

    /**
     * returns a better formatted error message - suitable for framing. There's
     * a more complex version in StylesEditorAction. This will kick out a VERY
     * LARGE errorMessage.
     *
     * @param xml
     * @param errors
     *
     * @return DOCUMENT ME!
     */
    public static String getErrorMessage(Reader xml, List<SAXParseException> errors) {
        BufferedReader reader = null;
        StringBuffer result = new StringBuffer();
        result.append(Messages.StyleEditor_xml_validator_common); 

        try {
            reader = new BufferedReader(xml);

            String line = reader.readLine();
            int linenumber = 1;
            int exceptionNum = 0;

            //check for lineNumber -1 errors  --> invalid XML
            if (errors.size() > 0) {
                SAXParseException sax = (SAXParseException) errors.get(0);

                if (sax.getLineNumber() < 0) {
                    result.append("   INVALID XML: " //$NON-NLS-1$
                        + sax.getLocalizedMessage() + "\n"); //$NON-NLS-1$
                    result.append(" \n"); //$NON-NLS-1$
                    exceptionNum = 1; // skip ahead (you only ever get one error in this case)
                }
            }

            while (line != null) {
                line = line.replace('\n', ' ');
                line = line.replace('\r', ' ');

                String header = linenumber + ": "; //$NON-NLS-1$
                result.append(header + line + "\n"); // record the current line //$NON-NLS-1$

                boolean keep_going = true;

                while (keep_going) {
                    if ((exceptionNum < errors.size())) {
                        SAXParseException sax = (SAXParseException) errors.get(exceptionNum);

                        if (sax.getLineNumber() <= linenumber) {
                            String head = "---------------------".substring(0, //$NON-NLS-1$
                                    header.length() - 1);
                            String body = "--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"; //$NON-NLS-1$

                            int colNum = sax.getColumnNumber(); //protect against col 0 problems

                            if (colNum < 1) {
                                colNum = 1;
                            }

                            if (colNum > body.length()) {
                                body = body + body + body + body + body + body; // make it longer (not usually required, but might be for SLD_BODY=... which is all one line)

                                if (colNum > body.length()) {
                                    colNum = body.length();
                                }
                            }

                            result.append(head + body.substring(0, colNum - 1)
                                + "^\n"); //$NON-NLS-1$
                            result.append("       (line " + sax.getLineNumber() //$NON-NLS-1$
                                + ", column " + sax.getColumnNumber() + ")"  //$NON-NLS-1$//$NON-NLS-2$
                                + sax.getLocalizedMessage() + "\n"); //$NON-NLS-1$
                            exceptionNum++;
                        } else {
                            keep_going = false; //report later (sax.getLineNumber() > linenumber)
                        }
                    } else {
                        keep_going = false; // no more errors to report
                    }
                }

                line = reader.readLine(); //will be null at eof
                linenumber++;
            }

            for (int t = exceptionNum; t < errors.size(); t++) {
                SAXParseException sax = (SAXParseException) errors.get(t);
                result.append("       (line " + sax.getLineNumber() //$NON-NLS-1$
                    + ", column " + sax.getColumnNumber() + ")" //$NON-NLS-1$ //$NON-NLS-2$
                    + sax.getLocalizedMessage() + "\n"); //$NON-NLS-1$
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result.toString();
    }

    public List<SAXParseException> validateSLD(InputStream xml, String SchemaUrl) {
        return validateSLD(new InputSource(xml), SchemaUrl);
    }

    /**
     * validate a .sld against the schema
     *
     * @param xml input stream representing the .sld file
     * @param SchemaUrl location of the schemas. Normally use
     *        ".../schemas/sld/StyleLayerDescriptor.xsd"
     *
     * @return list of SAXExceptions (0 if the file's okay)
     */
    public List<SAXParseException> validateSLD(InputSource xml, String SchemaUrl) {
        SAXParser parser = new SAXParser();

        try {
//     1. tell the parser to validate the XML document vs the schema
//     2. does not validate the schema (the GML schema is *not* valid.  This is
//        			an OGC blunder)
//     3. tells the validator that the tags without a namespace are actually
//        			SLD tags.
//     4. tells the validator to 'override' the SLD schema that a user may
//        			include with the one inside geoserver.

            parser.setFeature("http://xml.org/sax/features/validation", true); //$NON-NLS-1$
            parser.setFeature("http://apache.org/xml/features/validation/schema", //$NON-NLS-1$
                true);
            parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", //$NON-NLS-1$
                false);
            parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", //$NON-NLS-1$
                SchemaUrl);
            parser.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation", //$NON-NLS-1$
                "http://www.opengis.net/sld " + SchemaUrl); //$NON-NLS-1$

            //parser.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation","http://www.opengis.net/ows "+SchemaUrl);
            Validator handler = new Validator();
            parser.setErrorHandler(handler);
            parser.parse(xml);

            return handler.errors;
        } catch (java.io.IOException ioe) {
            ArrayList<SAXParseException> al = new ArrayList<SAXParseException>();
            al.add(new SAXParseException(ioe.getLocalizedMessage(), null));

            return al;
        } catch (SAXException e) {
            ArrayList<SAXParseException> al = new ArrayList<SAXParseException>();
            al.add(new SAXParseException(e.getLocalizedMessage(), null));

            return al;
        }
    }

    // errors in the document will be put in "errors".
    // if errors.size() ==0  then there were no errors.
    private class Validator extends DefaultHandler {
        public ArrayList<SAXParseException> errors = new ArrayList<SAXParseException>();

        @Override
        public void error(SAXParseException exception)
            throws SAXException {
            errors.add(exception);
        }

        @Override
        public void fatalError(SAXParseException exception)
            throws SAXException {
            errors.add(exception);
        }

        @Override
        public void warning(SAXParseException exception)
            throws SAXException {
            //do nothing
        }
    }
}
