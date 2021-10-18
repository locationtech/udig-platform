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

import java.io.File;

import javax.swing.JFileChooser;

public class ConvertConfluence {

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1 && "?".equals(args[0])) { //$NON-NLS-1$
            System.out.println(
                    " Usage: java docs.ConvertConfluence [entities.xml] [target directory]"); //$NON-NLS-1$
            System.out.println();
            System.out.println("Where:"); //$NON-NLS-1$
            System.out
                    .println("  entities.xml Is produced when backing up a confluence wiki as xml"); //$NON-NLS-1$
            System.out.println(
                    "  target directory is the location to export the textile documents to"); //$NON-NLS-1$
            System.out.println();
            System.out.println(
                    "If not provided the appication will prompt you for the above information"); //$NON-NLS-1$

            System.exit(0);
        }
        File entitiesFile = args.length > 0 ? new File(args[0]) : null;
        File targetDirectory = args.length > 1 ? new File(args[1]) : null;

        if (entitiesFile != null && entitiesFile.isDirectory()) {
            entitiesFile = new File(entitiesFile, "entities.xml"); //$NON-NLS-1$
        }
        if (entitiesFile == null) {

            JFileChooser dialog = new JFileChooser(entitiesFile);
            dialog.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public String getDescription() {
                    return "Confluence entities.xml export"; //$NON-NLS-1$
                }

                @Override
                public boolean accept(File f) {
                    return "entities.xml".equals(f.getName()); //$NON-NLS-1$
                }
            });
            dialog.setDialogTitle("Convert entities.xml to textile"); //$NON-NLS-1$
            int open = dialog.showDialog(null, "Convert"); //$NON-NLS-1$

            if (open == JFileChooser.CANCEL_OPTION) {
                System.out.println("Conversion canceled"); //$NON-NLS-1$
                System.exit(-1);
            }
            entitiesFile = dialog.getSelectedFile();
        }
        if (entitiesFile == null || !entitiesFile.exists() || entitiesFile.isDirectory()) {
            System.out.println(
                    "File entities.xml to use for conversion not provided: '" + entitiesFile + "'"); //$NON-NLS-1$ //$NON-NLS-2$
            System.exit(-1);
        }

        if (targetDirectory == null) {
            JFileChooser dialog = new JFileChooser(targetDirectory);
            dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            dialog.setDialogTitle("Target Directory for textile files"); //$NON-NLS-1$

            int open = dialog.showDialog(null, "Export"); //$NON-NLS-1$

            if (open == JFileChooser.CANCEL_OPTION) {
                System.out.println("Canceled canceled"); //$NON-NLS-1$
                System.exit(-1);
            }
            targetDirectory = dialog.getSelectedFile();
        }
        if (targetDirectory == null || !targetDirectory.isDirectory()
                || !targetDirectory.exists()) {
            System.out.println(
                    "Taget directory for textile files not provided: '" + targetDirectory + "'"); //$NON-NLS-1$ //$NON-NLS-2$
            System.exit(-1);
        }
        ConfluenceDom myConfluenceDom = new ConfluenceDom(entitiesFile);

        myConfluenceDom.writeCurrentPagesTextile(targetDirectory);
    }

}
