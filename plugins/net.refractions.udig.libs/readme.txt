uDIG Application
----------------
This java project is used to share common dependencies between the plug-ins
comprising the uDig Application.

The refresh.xml ant file is set up to download the required
jars into a lib folder if it has not done so already.

To force it to download the files you will need to manually
build the project. This will download the files from
http://lists.refractions.net/ and ibiblio.org as needed.

To customize the servers used by refresh.xml modify
copy.properties - this is very helpful if you are
building udig in other parts of the world.  You can also 
specify the location of your local maven repository and the
build target will you that as well as downloading files.
