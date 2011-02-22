/**
*  This class is a groovy script.  You can run it using the command groovy JavadocGen.groovy.
*
*  This script searches for plugins (indicated by a .classpath file, dumb but it works) and creates a javadoc of all plugins that do not
*  have test in the name.  Packages that contain internal or impl are not included in the javadoc.
*
*  In addition adding a file called "nojavadoc" in the base plugin directory will also exclude the plugin from the javadocs.
*
*  The create javadoc overview page is organized by plugin.  So all packages in plugin catalog will be grouped together.
*
*  Note: it will blow-up if there is the same package in 2 different plugins.
*
* Future Work:
*  Provide warning if there is a duplicate package name
*  Create a list of extension points for each plugin in plugin package description or in overview.
*  Produce Andreas class diagrams for docs.
*  Process Manifest.MF in order to obtain the actual ID of the plugin.  Currently the root folder is used as the ID.
*
*/
public class JavadocGen{
    def i=0;

    def find( root, cper ){
	     i++
	     if ( i==100 ) {print ".";  i=0}
	    root.eachFile(){ file->
		   if( cper.call(file) )
		           return;
		   if( file.isDirectory() )
		       find( file, cper );
	    }
	}

	 def project( projects, file ){
	     if( file==null )
	         return null;
	     def parent=file.getParentFile()
	     if( projects.contains( parent ) )
	         return parent.getName();

	     return project( projects, parent )
	 }

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

	 def run(){
	     def classes=[]
  		 def classpath=[]
         def projects=[]
  		 def cper={file->
  		    if( file.getName().contains(".test")||file.getName().equals(".svn") )
  		        return true;
  		    if( file.isDirectory() && (file.getName().contains("internal") || file.getName().contains("impl") ))
  			return true
  		    if( file.getName().equalsIgnoreCase(".classpath") ) {
  		        classpath.add(file)
  		        projects.add(file.getParentFile());
  		    }

  		    if( file.getName().endsWith(".java") ){
  		    	classes.add(file)
  	 	    }
  		}


  		print "searching for configuration files...."
  		find( new File("." ), cper )

  		def toRemove=[];
	    projects.each(){project->
	       def list=project.list();
	       list.each() { entry-> if (entry=="nojavadoc" ) toRemove.add(project); }
	    }

	    projects.removeAll(toRemove);

  		println ""
  		def src=[]

  		def srcpath=""

  		classpath.each(){file->
  		  def cp=new XmlSlurper().parse(file);
  		  cp.classpathentry.each() { it ->
  		    if ( it['@kind']=="src" ) {
  		      src.add(file.getParent()+"/"+it['@path'] )
  		    }
  		  }
   		}

  		def groups=[:]
  		classes.each(){file->
  		  def filePackage=asPackage(src, file);
  		  if( filePackage!=null ){
	  		  assert filePackage instanceof String
  		      filePackage=filePackage.trim();
  		      def project=project(projects, file);
  		      if( project==null )
  		          return;
  	  		  assert project instanceof String
  		      if( groups[project]==null )
  		          groups[project]=[];
  		      if( !groups[project].contains(filePackage) )
	  		  	groups[project].add(filePackage)
	  		  srcpath+="${file.getPath()},"
  		  }
  		}

  		srcpath=srcpath[0..srcpath.length()-2]

  		def groupsString=""

  		def projectList=new ArrayList()
  		projectList.addAll(groups.keySet())
  		projectList.sort();


  		projectList.each(){ key->
  		    def value=groups[key]
	  		if( value.isEmpty() ) return;
  			def packages="";
  			value.sort();
  			value.each(){ tmp-> packages+="${tmp}:" }
  			packages=packages[0..packages.length()-2]
  		    groupsString+="Plugin_${key.trim()} ${packages},"
  		}

  		groupsString=groupsString[0..groupsString.length()-2]

  		AntBuilder javadoc=new AntBuilder();

  		javadoc.javadoc( access:"protected",
  		        author:"true",
  		        sourcefiles:srcpath,
  		        packagenames:"net.refractions.udig.*",
  		        destdir:"../api-udig",
  		        source:"1.5",
  		        doctitle:"uDig Public API",
  		        maxmemory:"512M",
  		        group:groupsString
  		)

	 }

    public static void main(String[] args){
		new JavadocGen().run();
    }
}
