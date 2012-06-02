package html;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import javax.swing.JFileChooser;

public class BulkConvert {

	public static void main(String args[]){
		if( args.length == 1 && "?".equals( args[0] )){
            System.out.println(" Usage: java html.BulkConvert [index.html] [rst directory]");
            System.out.println();
            System.out.println("Where:");
            System.out.println("  index.html Where you have unzipped the confluence html export");
            System.out.println("  rst directory location where you would like the html files saved");
            System.out.println();
            System.out.println("If not provided the appication will prompt you for the above information");
            
            System.exit(0);
        }
        File indexFile = args.length > 0 ? new File( args[0] ) : null;
        File rstDir = args.length > 1 ? new File( args[1] ) : null;

        if( indexFile != null && indexFile.isDirectory() ){
        	indexFile = new File( indexFile, "index.html");
        }

        if( indexFile == null || !indexFile.exists() ){
        	File cd = new File(".");
        	
            JFileChooser dialog = new JFileChooser( cd );
            dialog.setFileFilter( new javax.swing.filechooser.FileFilter() {
                public String getDescription() {
                    return "Html Files";
                }
                @Override
                public boolean accept(File f) {
                	return f.getName().endsWith(".html");
                }
            });
            dialog.setDialogTitle("Confluence wiki export");
            int open = dialog.showDialog( null, "Convert" );
            
            if( open == JFileChooser.CANCEL_OPTION ){
                System.out.println("Conversion canceled");
                System.exit(-1);
            }
            indexFile = dialog.getSelectedFile();
        }
        if ( indexFile == null || !indexFile.exists() || indexFile.isDirectory() ){
            System.out.println("File index.html to use for conversion not provided: '"+indexFile+"'");
            System.exit(-1);
        }
        File htmlDirectory = null;
		try {
			htmlDirectory = indexFile.getParentFile().getCanonicalFile();
		} catch (IOException eek) {
			System.out.println("Coudl not sort parent of "+indexFile+":"+eek );
			System.exit(-1);
		}

        if( rstDir == null ){
        	JFileChooser dialog = new JFileChooser( htmlDirectory.getParentFile() );
            dialog.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
            dialog.setDialogTitle("Target Directory for textile files");
            
            int open = dialog.showDialog( null, "Export" );
            
            if( open == JFileChooser.CANCEL_OPTION ){
                System.out.println("Canceled canceled");
                System.exit(-1);
            }
            rstDir = dialog.getSelectedFile();
        }
        if ( rstDir == null || !rstDir.isDirectory() || !rstDir.exists() ){
            System.out.println("Taget directory for textile files not provided: '"+rstDir+"'");
            System.exit(-1);
        }
        File[] htmlFileList = htmlDirectory.listFiles( new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".html");
			}
		});
        for( File htmlFile : htmlFileList ){
        	File dir = htmlFile.getParentFile();
        	
        	String htmlName = htmlFile.getName();
        	String rstName = htmlName.substring(0, htmlName.lastIndexOf('.'))+".rst";
        	
        	File rstFile = new File( dir, rstName );
        	System.out.println(htmlFile +" to "+ rstFile.getName() );
        	if( rstFile.exists() ){
        		boolean deleted = rstFile.delete();
        		if( !deleted ){
        			System.out.println( "\tCould not remove '"+rstFile+"' do you have it open in an editor?");
        			System.out.println( "\tSkipping ...");
        			continue;
        		}
        	}
        	String run[] = new String[]{"/usr/local/bin/html2rest", htmlFile.getName() };
        	
        	BufferedInputStream inputStream = null;
        	BufferedOutputStream outputStream = null;
        	try {
        		Process process = Runtime.getRuntime().exec( run, null, dir );
        		//inputStream = new BufferedInputStream(new FileInputStream(htmlFile));
        		//outputStream = new BufferedOutputStream( new FileOutputStream(rstFile));
        		
        		inputStream = new BufferedInputStream(process.getInputStream());
        		outputStream = new BufferedOutputStream(new FileOutputStream(rstFile));
        		
        		bufferedStreamsCopy( inputStream, outputStream );
        		
        		process.waitFor();
        		int exit = process.exitValue();
        		System.out.println("\tGenerated "+rstName+" with exist code "+exit );
			} catch (IOException e) {
        		System.out.println("\\tFailed on "+rstName+" with: "+e );
				e.printStackTrace();
				break;
			} catch (InterruptedException e) {
	       		System.out.println("\\tFailed on "+rstName+" with: "+e );
				break;
			}
        	finally {
				close( inputStream );
				close( outputStream );
			}
        	
        }
	}
	
	// From http://java.dzone.com/articles/file-copy-java-Ð-benchmark
	private static void bufferedStreamsCopy(InputStream fin, OutputStream fout) {
        try {
            
            int data;
            while ((data = fin.read()) != -1) {
                fout.write(data);
            }
            fout.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void close(Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
