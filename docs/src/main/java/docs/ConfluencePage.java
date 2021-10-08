/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package docs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ConfluencePage {
    private String title = new String();

    private String id = new String();

    private String status = new String();

    private String bodyText = new String();

    private String version = new String();

    private String parent = new String();

    private boolean historicalVersions = false;

    private boolean originalVersion = false;

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public boolean isOriginalVersion() {
        return originalVersion;
    }

    public void setOriginalVersion(boolean originalVersion) {
        this.originalVersion = originalVersion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ConfluencePage() {

    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setHistoricalVersions(boolean hasHistoricalVersions) {
        this.historicalVersions = hasHistoricalVersions;
    }

    public boolean isCurrent() {
        return (status.equalsIgnoreCase("current") //$NON-NLS-1$
                && ((this.version.equalsIgnoreCase("1") && !this.historicalVersions //$NON-NLS-1$
                        && !this.originalVersion) || (this.historicalVersions)));
    }

    public void writeTextile(File location) {
        String header = "h1. " + this.title + "\n\n"; //$NON-NLS-1$ //$NON-NLS-2$

        try {
            String targetName = this.title.replaceAll("\\?", "") + ".textile"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            File targetFile = new File(location, targetName);

            BufferedWriter f = new BufferedWriter(new FileWriter(targetFile));
            f.write(header + this.bodyText);
            f.close();
        } catch (IOException e) {
            System.out.println("Unable to write out '" + this.title + "': " + e); //$NON-NLS-1$ //$NON-NLS-2$
            e.printStackTrace();
        }

    }

    public void fixTextile() {

        this.replaceNewLine("h[1-9]\\..*\\n\\S", "\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
        this.replaceNewLine("\\S\\nh[1-9]\\..*", "\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
        this.fixHtmlLinks();

    }

    private void replaceNewLine(String searchExp, String replaceExp) {
        Pattern pattern;
        Matcher matcher;

        try {
            pattern = Pattern.compile(searchExp);
            matcher = pattern.matcher(this.bodyText);

            StringBuffer replacedText = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(replacedText,
                        matcher.group().replaceAll("\\n", replaceExp)); //$NON-NLS-1$
            }
            matcher.appendTail(replacedText);
            this.bodyText = replacedText.toString();
        } catch (PatternSyntaxException e) {
            System.out.println(e);
        }

    }

    private void fixHtmlLinks() {
        Pattern pattern;
        Matcher matcher;

        try {
            pattern = Pattern.compile("\\[.*\\|http://.*\\]"); //$NON-NLS-1$
            matcher = pattern.matcher(this.bodyText);

            StringBuffer replacedText = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(replacedText, matcher.group().replaceAll("\\[", "\"") //$NON-NLS-1$ //$NON-NLS-2$
                        .replaceAll("\\]", " ").replaceAll("\\|", "\":")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            }
            matcher.appendTail(replacedText);
            this.bodyText = replacedText.toString();
        } catch (PatternSyntaxException e) {
            System.out.println(e);
        }

    }

}
