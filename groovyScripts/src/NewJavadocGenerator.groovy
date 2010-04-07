
/**
*  This class is a groovy script.  You can run it using the command groovy JavadocGen.groovy (plugin directory) (destination directory).
*  
*  This script searches for plugins (indicated by a .classpath file, dumb but it works) and creates a javadoc of all plugins that do not
*  have test or internal in the name.  Packages that contain internal or impl are not included in the javadoc.
*  
*  In addition adding a file called "nojavadoc" in the base plugin directory will also exclude the plugin from the javadocs.
* 
* 
* Future Work:  
*  Make javadoc linking work
*  Produce Andreas class diagrams for docs.
*  Process Manifest.MF in order to obtain the actual ID of the plugin.  Currently the root folder is used as the ID.  
*/
class NewJavadocGenerator {
    String root,docDest;
    
	public  NewJavadocGenerator( String root, String docDest ){
	    this.root=root;
	    this.docDest=docDest;
	}

	def i=0;
	/**
    * searches from the root until closure (cper) returns true.
    */
    def find( root, cper ){
	     i++
	     def dirs=[];
	     if ( i==100 ) {print ".";  i=0}
	    root.eachFile(){ file-> 
		   if( cper.call(file) )
		           return;
		   if( file.isDirectory() )
		       dirs.add(file)
	    }
	    
	    dirs.each(){ dir->
	        find(dir,cper);
	    }
	}
	 
	/**
	 * returns the name of the project of the file
	 */
	 def project( projects, file ){
	     if( file==null )
	         return null;
	     def parent=file.getParentFile()
	     if( projects.contains( parent ) )
	         return parent.getName();
	     
	     return project( projects, parent )
	 }
	 
	 /**
	 * Returns the package of the file as a string.
	 */
	 def asPackage(def classpaths, def file){
	  	   String path=file.getPath();
	  	   path=path[0..path.lastIndexOf(File.separator)]
	  	   for ( cp in classpaths ){
	  	       if( path.startsWith(cp) ) {
	  	         path=(path-cp).replace(File.separator,".")
	  	         if( path.length()==0 )
	  	             return null;
	  	         if( path.endsWith(".") )
 	                 path=path[0..path.length()-2]

 	        	 if( path.startsWith(".") )
 	        	 	path=path[1..path.length()-1]
	  	           return path
	  	       }
	  	   }
	  	   return null;
	 }
	 
	 /**
	 * Searches from the root for all classes and all projects.  Projects are identified by finding the .classpath file
	 */
	 def getData(){
	  	 def classes=[:]
  		 def classpath=[:]
	  	 def extensions=[:]
  		                
         def currentproject;
  		 def cper={file->
	      		    if( file.getName().contains(".test")||file.getName().equals(".svn") ||file.getName().equals("bin"))
	      		        return true;
	      		    if( file.isDirectory() && (file.getName().contains("internal") || file.getName().contains("impl") ))  
	      				return true
	      		    if( file.getName().equalsIgnoreCase(".classpath") ) {
	      		        currentproject=file.getParentFile();
	      		        classpath[currentproject]=file;
	      		        classes[currentproject]=[];
	      		        extensions[currentproject]=[];
	      		    }
	      	
	      		    if( file.getName().endsWith(".java") ){
	      		    	classes[currentproject].add(file); 
	      	 	    }
	      		    
	      		    if( file.getParentFile().getName().equals("doc") && file.getName().endsWith(".html") ){
	      		        extensions[currentproject].add(file);
	      		    }
	    	};
	 

		print "searching for configuration files....";
		find( new File(root ), cper )
		
	 	Data data=new Data();
	 	data.classes=classes;
	 	data.classpath=classpath;
	 	data.extensions=extensions;
	 	
	 	return data;
	}

	def removeUnwantedProjects(Data data){
  		def toRemove=[];
	    data.classes.keySet().each(){project->
	       def list=project.list();
	       list.each() { entry-> if (entry=="nojavadoc" ) toRemove.add(project); }
	    }
  		
	    toRemove.each(){ it ->
	       data.classes.remove(it);
	       data.classpath.remove(it);
	       data.extensions.remove(it);
	    }
	}
	
	def getSourcePaths(File classpathFile, Collection classes){
	    
	    println "parsing "+classpathFile.getParent();
	    
	    if( classes.isEmpty() )
	        return; 
	    
	    def srcpath="";
	    
	    classes.each{ file ->
	        srcpath+=file.getPath()+",";
	        
	    }
	    
		return srcpath[0..srcpath.length()-2];
	}
	
	def generateJavadocs(Data data){

	    def projects=[];
	    
	    data.classes.keySet().each(){ project ->
	    
	    	println "generating javadocs for ${project.getName()}"
	    
	        String srcpath=getSourcePaths(data.classpath[project], data.classes[project]);

	    	if( srcpath==null )
	    	    return;
	    	
	    	projects.add(project);
	    	
	        String projectName=project.getName().trim();
	        	        
	        AntBuilder javadoc=new AntBuilder();
	        
	   		javadoc.javadoc( access:"protected", 
	  		        author:"true", 
	  		        sourcefiles:srcpath, 
	  		        destdir:docDest+"/"+projectName,
	  		        source:"1.5",
	  		        doctitle:"Plugin: ${projectName}",
	  		        windowtitle:"uDig API- ${projectName}",
	  		        maxmemory:"512M"
	  		)
	    }
	    
	    /*
	    projects.each(){ project ->
	    
    		println "generating javadocs for ${project.getName()}"
    
	        String srcpath=getSourcePaths(data.classpath[project], data.classes[project]);
	
	        String projectName=project.getName().trim();
	        
	        AntBuilder javadoc=new AntBuilder();
	        
	   		javadoc.javadoc( access:"protected", 
	  		        author:"true", 
	  		        sourcefiles:srcpath, 
	  		        destdir:docDest+"/"+projectName,
	  		        source:"1.5",
	  		        doctitle:"Plugin: ${projectName}",
	  		        windowtitle:"uDig API- ${projectName}",
	  		        maxmemory:"512M"
	  		){
	   		    projects.each(){ p ->
	   		        javadoc.link( href:"file://${docDest}${p.getName()}", resolveLink:"true")
	   		    }
	   		}
	   		
	    }
	    */
	    return projects;
	}
	
	def copyExtensionDocs(Data data){
	    File docs=new File(docDest+'/extension-points');
	    if( !docs.exists() )
	        docs.mkdir();

	    File bookFile=new File(docs, "book.css");
	    bookFile.createNewFile();
	    bookFile.write(bookcss);
	    
	    File schemaFile=new File(docs, "schema.css");
	    schemaFile.createNewFile();
	    schemaFile.write(schemacss);
	    
	    def newFiles=[:];
	    
	    data.extensions.each(){ entry ->
	    	
			newFiles[entry.getKey()]=[];	    
	    	entry.getValue().each(){ ext ->
		    	File destFile=new File(docs, ext.getName());
		    	Writer writer=destFile.newWriter(false);
		    	
		    	println( "copying ${ext.getName()}")
				
		    	newFiles[entry.getKey()].add(destFile);
		    	ext.eachLine(){ line->

	    		def book=/<style>.+book.css/
	    		def schema=/<style>.+schema.css/
	    		
		    		if( line =~ book )
		    		    writer.write('<style>@import url(book.css);</style>' )
	    		    else if( line =~ schema )
		    		    writer.write('<style>@import url(schema.css);</style>' )
	    		    else
						writer.write(line);
		    		writer.write('\n');
		    	}
		    	
		    	writer.close();
	    	}
	    }
	    return newFiles;
	}

	def buildJavadocIndexPage(Collection projects){
	    File rootFile=new File(docDest+"/index.html");
	    def writer=rootFile.newPrintWriter();
	    def indexBuilder = new groovy.xml.MarkupBuilder(writer);
		
	    indexBuilder.html{
			head()
			body{
			    h1("uDig public API")
			    ul{
			        projects.each{ project ->
					indexBuilder.li{
					    a( href:"${project.getName().trim()}/index.html", project.getName().trim());
					}
			    }
			    }
			}
	    }
	    writer.close();
	}
	
	// extensionMap is <Project, List<extensionPointFile>>
	def buildExtensionPointIndexPage(def extensionMap){
	    File rootFile=new File(docDest+"/extension-points/index.html");
	    def writer=rootFile.newPrintWriter();
	    def indexBuilder = new groovy.xml.MarkupBuilder(writer);
		
	    indexBuilder.html{
			head()
			body{
			    h1("uDig Extension Point List")
			    ul{
			        extensionMap.each{ entry ->
					    if( !entry.getValue().isEmpty())
							indexBuilder.li(){
							    p("Extensions for Plugin ${entry.getKey().getName()}")
							    ul{
							        entry.getValue().each(){ extensionPoint ->
								        indexBuilder.li{
									    	a( href:"${extensionPoint.getName()}", extensionPoint.getName()[0..-5].replace('_','.'));
								        }
							        }
							    }
						}
				    }
			    }
			}
	    }
	    writer.close();
	}
	
	 def generate(){
	     Data data=getData()
	     
	     removeUnwantedProjects(data);
	     
	     // copies is <projects,list<extensionPointFiles>>
	     def copies=copyExtensionDocs(data);
	     
	     def projects=generateJavadocs(data);
	     
	     buildJavadocIndexPage(projects);
	     
	     buildExtensionPointIndexPage(copies);
	     
	 }
	  static void main(args) {
      
	      String root='.';
	      String dest='api-udig';
	      
	      if( args.size()>0 )
	          root=args[0];
	      if( args.size()>1 )
	          dest=args[1];
	      
	      NewJavadocGenerator generator=new NewJavadocGenerator(root, dest );
	      generator.generate();
	  }

	  String bookcss="""P.Code {
	  	display: block;
		text-align: left;
		text-indent: 0.00pt;
		margin-top: 0.000000pt;
		margin-bottom: 0.000000pt;
		margin-right: 0.000000pt;
		margin-left: 15pt;
		font-size: 10.000000pt;
		font-weight: normal;
		font-style: normal;
		color: #4444CC;
		text-decoration: none;
		vertical-align: baseline;
		text-transform: none;
		font-family: "Courier New", Courier, monospace;
	}
	H6.CaptionFigColumn {
		display: block;
		text-align: left;
		text-indent: 0.000000pt;
		margin-top: 3.000000pt;
		margin-bottom: 11.000000pt;
		margin-right: 0.000000pt;
		margin-left: 0.000000pt;
		font-size: 9.000000pt;
		font-weight: bold;
		font-style: Italic;
		color: #000000;
		text-decoration: none;
		vertical-align: baseline;
		text-transform: none;
	}
	P.Note {
		display: block;
		text-align: left;
		text-indent: 0pt;
		margin-top: 19.500000pt;
		margin-bottom: 19.500000pt;
		margin-right: 0.000000pt;
		margin-left: 30pt;
		font-size: 11.000000pt;
		font-weight: normal;
		font-style: Italic;
		color: #000000;
		text-decoration: none;
		vertical-align: baseline;
		text-transform: none;
	}
	EM.UILabel {
		font-weight: Bold;
		font-style: normal;
		text-decoration: none;
		vertical-align: baseline;
		text-transform: none;
	}
	EM.CodeName {
		font-weight: Bold;
		font-style: normal;
		text-decoration: none;
		vertical-align: baseline;
		text-transform: none;
		font-family: "Courier New", Courier, monospace;
	}

	body, html { border: 0px }


	/* following font face declarations need to be removed for DBCS */

	body, h1, h2, h3, h4, h5, h6, p, table, td, caption, th, ul, ol, dl, li, dd, dt {font-family: Arial, Helvetica, sans-serif; color: #000000}
	pre				{ font-family: "Courier New", Courier, monospace;}

	/* end font face declarations */

	/* following font size declarations should be OK for DBCS */
	body, h1, h2, h3, h4, h5, h6, p, table, td, caption, th, ul, ol, dl, li, dd, dt {font-size: 10pt; }
	pre				{ font-size: 10pt}

	/* end font size declarations */

	body	     { background: #FFFFFF; margin-bottom: 1em }
	h1           { font-size: 18pt; margin-top: 5; margin-bottom: 1 }	
	h2           { font-size: 14pt; margin-top: 25; margin-bottom: 3 }
	h3           { font-size: 11pt; margin-top: 20; margin-bottom: 3 }
	h4           { font-size: 10pt; margin-top: 20; margin-bottom: 3; font-style: italic }
	p            { margin-top: 10px; margin-bottom: 10px }
	pre          { margin-left: 6; font-size: 9pt; color: #4444CC } 
	a:link	     { color: #0000FF }
	a:hover	     { color: #000080 }
	a:visited    { text-decoration: underline }
	ul	     { margin-top: 10px; margin-bottom: 10px; }
	li	     { margin-top: 5px; margin-bottom: 5px; } 
	li p	     { margin-top: 5px; margin-bottom: 5px; }
	ol	     { margin-top: 10px; margin-bottom: 10px; }
	dl	     { margin-top: 10px; margin-bottom: 10px; }
	dt	     { margin-top: 5px; margin-bottom: 5px; font-weight: bold; }
	dd	     { margin-top: 5px; margin-bottom: 5px; }
	strong	     { font-weight: bold}
	em	     { font-style: italic}
	var	     { font-style: italic}
	div.revision { border-left-style: solid; border-left-width: thin; 
					   border-left-color: #7B68EE; padding-left:5 }
	th	     { font-weight: bold }

	a.command-link {
	}
	a.command-link img {
		border-width: 0px;
		border-style: none;
		vertical-align: middle;
	}"""
	  
	
	String schemacss="""
	    /*******************************************************************************
	     * Copyright (c) 2003 IBM Corporation and others.
	     * All rights reserved. This program and the accompanying materials 
	     * are made available under the terms of the Eclipse Public License v1.0
	     * which accompanies this distribution, and is available at
	     * http://www.eclipse.org/legal/epl-v10.html
	     * 
	     * Contributors:
	     *     IBM Corporation - initial API and implementation
	     *******************************************************************************/

	    H6.CaptionFigColumn#header {
	    	font-size:16px; 
	    	display:inline
	    }

	    P.Note#copyright {
	    	font-size: smaller; 
	    	font-style: normal;
	    	color: #336699; 
	    	display:inline;
	    	margin-top: 3.000000pt;
	    	margin-bottom: 11.000000pt;
	    	margin-right: 0.000000pt;
	    	margin-left: 0.000000pt;
	    }

	    P.Code#dtd {
	    	color: #800000; 
	    	margin-top: 0.000000pt;
	    	margin-bottom: 0.000000pt;
	    	margin-right: 0.000000pt;
	    	margin-left: 10.000000pt;
	    }

	    P.Code#dtdAttlist {
	    	color: #800000; 
	    	margin-top: 0.000000pt;
	    	margin-bottom: 0.000000pt;
	    	margin-right: 0.000000pt;
	    	margin-left: 20.000000pt;
	    }

	    P.Code#tag {
	    	color: #000080; 
	    	display:inline;
	    	margin-top: 0.000000pt;
	    	margin-bottom: 0.000000pt;
	    	margin-right: 0.000000pt;
	    	margin-left: 0.000000pt;
	    }

	    P.Code#cstring {
	    	color: #008000; 
	    	display:inline;
	    	margin-top: 0.000000pt;
	    	margin-bottom: 0.000000pt;
	    	margin-right: 0.000000pt;
	    	margin-left: 0.000000pt;	
	    }

	    .ConfigMarkup#elementDesc {
	    	color: black;
	    	margin-top: 0.000000pt;
	    	margin-bottom: 0.000000pt;
	    	margin-right: 0.000000pt;
	    	margin-left: 10.000000pt;
	    }

	    .ConfigMarkup#attlistDesc {
	    	color: black;
	    	margin-top: 0.000000pt;
	    	margin-bottom: 0.000000pt;
	    	margin-right: 0.000000pt;
	    	margin-left: 32.000000pt;
	    }"""

}


class Data{
    // <Projects, Collection<Classes>>
    def classes=[:];
    // <Projects, .classpath>
    def classpath=[:];
    // extension point html documentation
    def extensions=[];
}