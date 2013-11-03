/*
##############################################
#   This script pulls all the properties 
#   files out of the udig plugins and
#   puts them into the folder:  properties
#
#   The properties files are all put in the same folder
#   and will have the form:
#   pluginname---packagename.message_xx.properties for messages and
#   pluginname---plugin_xx.properties for plugin files
#
##############################################
*/


def base = new File("../../plugins/");
def destination = new File("./properties");

println """
	Copy from base folder: ${base.absolutePath}
	to folder: ${destination.absolutePath}
"""

if(!base.exists()){
    println """
        Some of the folders of the original file structure are missing. 
        This script should be run only inside the folder:
                "udig-platform/extras/plugin-translations"
        of the uDig folder structure.
    """
    System.exit(1);
}

// get files matching translation files
def files = [];
base.eachFileRecurse() {
    if (it.name.matches(".*(messages|messages[\\D]{3}|plugin|plugin[\\D]{3}).properties")) {
        if(!it.absolutePath.matches(".*[\\\\/]bin[\\\\/].*")){
            files << it;
        }
    }
}

// copy them over to the properties folder
files.each{ file ->
    def src = file.absolutePath
    def newName = src.replaceFirst(".*[\\\\/]..[\\\\/]..[\\\\/]plugins[\\\\/]", "");
    newName = newName.replaceAll("[\\\\/]", "__");
	
    def destFile = new File(destination, newName);
    if(destFile.exists()){
        destFile.delete();
    }
    def dest = destFile.absolutePath;

    new AntBuilder().copy ( file : "${src}" , tofile : "${dest}" )
}





/*
File[] filesList = base.listFiles(new FileFilter(){
    public boolean accept( File pathname ) {
        String folder = pathname.getParent();
        String name = pathname.getName();
        println name
        if (name.matches(".*(messages|messages[\\D]{3}|plugin|plugin[\\D]{3}).properties")) {
            return true;
        }
        return false;
    }
});
println "Found ${filesList.length} files."
for( File file : filesList ) {
    System.out.println(file.getAbsolutePath());
}
*/


//list=`find ../../plugins/ -iname *.properties | grep -v "/bin/" | egrep '(messages.*|plugin.*).properties$'`


