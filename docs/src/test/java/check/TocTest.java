/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package check;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Check that each rst file is mentinoed in the toc.xml file.
 *
 * @author Jody Garnett
 * @since 1.3.2
 */
public class TocTest {

    @Test
    public void pageCheck() throws Exception {
        File docs = new File("."); //$NON-NLS-1$
        File toc = new File(new File(docs, "user"), "toc.xml"); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("toc.xml found", toc.exists()); //$NON-NLS-1$

        String line;
        Matcher matcher;

        // Test with http://www.regexplanet.com/advanced/java/index.html
        // .*href="EN/(.+?).html".*
        Pattern hrefPattern = Pattern.compile(".*href=\"(.+?).html\".*"); //$NON-NLS-1$
        line = "        <topic label=\"uDig Overview\" href=\"EN/uDig Overview.html\">"; //$NON-NLS-1$
        matcher = hrefPattern.matcher(line);
        assertTrue("hrefPattern match", matcher.matches()); //$NON-NLS-1$
        assertEquals("hrefPattern Page extract", "EN/uDig Overview", matcher.group(1)); //$NON-NLS-1$ //$NON-NLS-2$

        // topic="EN/(.+?).html"
        Pattern topicPattern = Pattern.compile(".*topic=\"(.+?).html\".*"); //$NON-NLS-1$
        line = "<toc label=\"Users Guide\" topic=\"EN/index.html\">"; //$NON-NLS-1$
        matcher = topicPattern.matcher(line);
        assertTrue("topicPattern match", matcher.matches()); //$NON-NLS-1$
        assertEquals("topicPattern Page extract", "EN/index", matcher.group(1)); //$NON-NLS-1$ //$NON-NLS-2$

        line = "<toc label=\"Users Guide\" topic=\"EN/concept/index.html\">"; //$NON-NLS-1$
        matcher = topicPattern.matcher(line);
        assertTrue("topicPattern match", matcher.matches()); //$NON-NLS-1$
        assertEquals("topicPattern Page extract", "EN/concept/index", matcher.group(1)); //$NON-NLS-1$ //$NON-NLS-2$

        // scan contents of toc.xml file
        Map<String, String> pages = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(toc));
        try {
            int number = 0;
            while ((line = reader.readLine()) != null) {
                number++;

                matcher = hrefPattern.matcher(line);
                if (matcher.matches()) {
                    String page = matcher.group(1);
                    if (pages.containsKey(page)) {
                        conflict(number, page, line, pages.get(page));
                    } else {
                        pages.put(page, line);
                    }
                }
                matcher = topicPattern.matcher(line);
                if (matcher.matches()) {
                    String page = matcher.group(1);
                    if (pages.containsKey(page)) {
                        conflict(number, page, line, pages.get(page));
                    } else {
                        pages.put(page, line);
                    }
                }
            }
            // scan rst files!
            File en = new File(new File(docs, "user"), "en"); //$NON-NLS-1$ //$NON-NLS-2$
            boolean check = checkDirectory("EN", en, pages); //$NON-NLS-1$

            assertTrue(
                    "Some RST pages not mentioned in toc.xml. Please review standard output for details", //$NON-NLS-1$
                    check);
        } finally {
            if (reader != null)
                reader.close();
        }
        if (!pages.isEmpty()) {
            String failMessage = "toc.xml refers to " + pages.size() + " missing pages"; //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println(failMessage);
            for (String missing : pages.values()) {
                System.out.println(missing);
            }
            fail(failMessage);
        }

    }

    private boolean checkDirectory(String path, File directory, Map<String, String> pages) {
        boolean check = true;
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                boolean checked = checkDirectory(path + "/" + file.getName(), file, pages); //$NON-NLS-1$
                if (checked == false) {
                    check = false;
                }
            }
            if (file.getName().endsWith(".rst")) { //$NON-NLS-1$
                String name = file.getName();
                name = name.substring(0, name.length() - 4);

                String key = path + "/" + name; //$NON-NLS-1$
                if (pages.containsKey(key)) {
                    pages.remove(key); // checked!
                } else {
                    System.out.println("toc.xml does not reference '" + key + ".html'"); //$NON-NLS-1$ //$NON-NLS-2$

                    System.out.print("        <topic label=\""); //$NON-NLS-1$
                    System.out.print(name);
                    System.out.print("\" href=\""); //$NON-NLS-1$
                    System.out.print(path);
                    System.out.print("/"); //$NON-NLS-1$
                    System.out.print(name);
                    System.out.println(".html\">"); //$NON-NLS-1$
                    System.out.println("                </topic>"); //$NON-NLS-1$

                    check = false;
                }
            }
        }
        return check;
    }

    private void conflict(int number, String page, String reference, String origional) {
        System.out.print("Line "); //$NON-NLS-1$
        System.out.print(number);
        System.out.print("  duplicate page reference for "); //$NON-NLS-1$
        System.out.print(page);
        System.out.println(".rst"); //$NON-NLS-1$
        System.out.println("origional:" + origional); //$NON-NLS-1$
        System.out.println("reference:" + reference); //$NON-NLS-1$
    }

}
