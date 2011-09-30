uDIG Application
----------------
This java project is used to share common dependencies 
between the plug-ins comprising the uDig Application.

The refresh.xml ANT file is set up to download the required
jars into a lib folder if it has not done so already.

To force it to download the files you will need to manually
build the project. This will download the required files
as needed.

To customize the servers used by refresh.xml modify
pom.xml file - this is very helpful if you are building 
uDig in other parts of the world.  You can also specify 
the location of your local maven repository and the build 
target will you that as well as downloading files.

========================================

There is now also an experimental MAVEN 3 script to do
the same thing. If you use a Nexus, this option is
generally a great deal faster.

To execute maven libs fetch script, you will need maven 3 installed
and on your PATH. Once you have that:

To install dependencies:
  >> mvn install -f mvn-udig-deps.xml
  
To clean lib directory:
  >> mvn clean -f mvn-udig-deps.xml
