This project is used to create a SDK of udig for distribution.
 There are a couple of tricks to making it work for you. Feel free to
 add your own tips.

First you need to change the base variable in the build.properties to the
location of your eclipse installation.  For example
base="/opt/eclipse"
base="C:\Program Files/eclipse"

This product should be changed to
product=/features/net.refractions.udig_sdk-feature/udig-sdk.product

Then set the location where the SDK will be built.  This is the buildRoot
variable
buildRoot=C:\udig-SDK
buildRoot=~/udig-SDK

Next you need to change the config variable to represent your environment.
config=linux,gtk,x86
config=win32,win32,x86

You also need to install the udig extras pack to get the org.eclipse.rcp.nls1
feature and it's plugins.  The extra zip can be found at
http://udig.refractions.net/files/downloads/extras/

This got me to the point where I was unable to find the
org.eclipse.help.nl1_3.2.0.v200606220026 plugin.  However there is a newer version of
this plugin that I think is stomping on the old version and messing things up.
If you know how to fix this please share.

A different SDK procedure can be found here.
 http://udig.refractions.net/confluence/display/ADMIN/SDK+Export+Procedure
