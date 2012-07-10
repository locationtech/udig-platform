How can I display my views by default
=====================================

**Q:** I would like a configuration option outside of changing code to display my panels by default
 **A:** You can either extend the current Map Perspective (that is an extension point) or create
your own perspective. An example of extending a perspective is in the
net.refractions.udig.feature.editor/plugin.xml file. (Hacking the Perspective class is wrong and the
example you see there is wrong too. I'm going to change it). If you define a new perspective and you
want it to be loaded by default you have to create a fragment for net.refractions.udig.ui and in the
fragment override the UDIGWorkbenchAdvisor class to return your perspective ID in the
getInitialWindowPerspectiveId method.
