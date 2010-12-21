/*
##############################################
# 
#   This script pushes all the properties 
#   files back in the right place in the udig 
#   file structure.
#
#   This can be used after a translation, to 
#   get the translated files back into uDig.
#
##############################################
*/


def base = new File("../../plugins/");
def destination = new File("./properties");

if(!base.exists() || !destination.exists()){
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
destination.eachFileRecurse() { translatedFile ->
    def path = translatedFile.name;
    def newName = path.replaceFirst("", "");
    newName = newName.replaceAll("/", "__");
    files << newName;
}

// copy them over to the properties folder
files.each{ file ->
    def newName = file.replaceAll("__", "/");
    def src = new File(destination, file);

    def destFile = new File(base, newName);
    if(!destFile.exists()){
        println """
            The original translation file doesn't seem to exist, 
            something might be going wrong. Stopping!

            File trying to copy: ${destFile}
        """
        System.exit(1);
    }
    //destFile.delete();
    def dest = destFile.absolutePath;

    new AntBuilder().copy ( file : "${src}" , tofile : "${dest}" )
}



