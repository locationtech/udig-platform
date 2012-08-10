Using Udigmenubuilder
#####################

This is the first attempt at the "use the UDIGWorkBenchAdvisor but let me change some stuff story".

The use of this class is triggered by providing the **net.refractions.udig.ui.menuBuilders**
extention:

::

    <extension
              point="net.refractions.udig.ui.menuBuilders">
           <menuBuilder
                 class="net.refractions.udig.ui.UDIGMenuBuilder"
                 id="net.refractions.udig.ui.menuBuilder">
           </menuBuilder>
        </extension>

There are a couple of reasons why you may wish to use this class.

Override UDIGWorkBenchAdvisor
=============================

By implementing this extension point you can pretty mych override the default UDIGWorkBenchAdvisor.

Why would you do this rather than make up your own WorkBenchAdvisor implementation? Well for uDig
1.0 the default UDIGApplication performed some nice JAI and GDI checks; now that these have been
made available as utility methods there is little reason for this class.

Mix in UDIG contributions to an existing RCP Application
========================================================

If you have an existing RCP application you can use the UDIGMenuBuilder to "mix in" the expected
UDIG Menus (such as Navigation and Layer).

I have not figured out the best place to call UDIGMenuBuilder from an RCP application:

-  extention org.eclipse.ui.startup: happens too late in the startup process, probably as a result
   of everything moving to OSGi
-  extention net.refractions.udig.ui.workbenchConfigurations: happens too early in the start up
   process (before org.eclipse.ui.menus has been processed) so positioning menus relative to
   expected ones does not work
-  Only working advise is to call it from your ``ActionBarAdvisor`` implementation as used for your
   application. This does not accomplish the goal of making uDig plugins "drop in" to an existing
   RCP application
-  Your suggestions are welcome on this one

As we move towards the use of the **org.eclipse.ui.menu** extention point this use case for
UDIGMenuBuilder will also vanish.
