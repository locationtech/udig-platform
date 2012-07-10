04 Using UDIGApplication
========================

The base class UDIGApplication serves three purposes:

-  It is a show case of what the SDK can do; usable by end-users as a functional GIS Application
-  It is an example for other RCP developers to copy (either cut and paste or subclass)
-  It provides static final helper methods that can be used by other RCP developers

This class is to be used as part of a product definition:

-  UDIG 1.0: IPlatformRunnable interface provided by RCP
-  UDIG 1.1: IApplication interface provided by Equinox

Links to Related Content:

-  For the latest instructions please see the `1 Getting Started <1%20Getting%20Started.html>`_
   tutorial

* :doc:`Login Example`

* :doc:`Using uDig 1.0 Application`


As a Showcase
=============

As a showcase the UDIGApplication needs to provide a fine balance between enough functionality to be
useful; and not so much that the result is intimidating.

The actual uDig application does not do much:

-  Kicks the net.refractions.udig.libs "Activator" into loading the EPSG database
-  Does a few sanity checks about the execution environment; displaying warning dialogs if
   everything is not right
-  Starts up a workbench using the UDIGWorkbenchAdvisor

As a Base
=========

As base class UDIGApplication must ensure that every contribution shows up in a logical part of the
user interface. This is especially important with respect to **menubars** and **toolbars**; we must
include examples of each kind of contribution.

Check for JAI
-------------

This has moved to an **org.eclipse.ui.startup** for the net.refractions.udig.ui. It will display a
dialog if JAI is not found.

Login
-----

There is no method for this right now; do you want to add one? Override the following method in your
subclass:

::

    public static boolean checkLogin(){
            return true;
        }

Init
----

The init method as provided will kick the net.refractions.udig.libs Activator class into loading the
EPSG database. It actually does a quick sanity check first (to see if loading will take a long
time).

::

    /**
         * We have a couple things that need to happen
         * before the workbench is opened. The org.eclipse.ui.startup
         * extension point is willing to run stuff for us *after*
         * the workbench is opened - but that is not so useful
         * when we need to configure the EPSG database for libs
         * and load up the local catalog.
         * <p>
         * Long term we will want to create a startup list
         * (much like we have shutdown hooks).
         */
        protected boolean init() {
            ProgressMonitorDialog progress = new ProgressMonitorDialog( Display.getCurrent().getActiveShell());
            final Bundle bundle = Platform.getBundle(Activator.ID);
            
            // We should kick the libs plugin to load the EPSG database now
            if( ThreadedH2EpsgFactory.isUnpacked()){
                // if there is not going to be a long delay
                // don't annoy users with a dialog
                Activator.initializeReferencingModule( null );            
            }
            else {
                // We are going to take a couple of minutes to set this up
                // so we better set up a progress dialog thing
                //
                try {
                    progress.run(false,false, new IRunnableWithProgress(){            
                        public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                                InterruptedException {
                            Activator.initializeReferencingModule( monitor);
                        }
                    });
                } catch (InvocationTargetException e) {
                    Platform.getLog(bundle).log(
                            new Status(IStatus.ERROR, Activator.ID, e.getCause().getLocalizedMessage(), e
                                    .getCause()));
                    return false;
                } catch (InterruptedException e) {
                    Platform.getLog(bundle).log(
                            new Status(IStatus.ERROR, Activator.ID, e.getCause().getLocalizedMessage(), e
                                    .getCause()));
                    return false;
                }
            }
            // We should kick the CatalogPlugin to load now...
            return true;
        }

As a Utility Class
==================

Utility methods exist to perform checks commonly needed at startup.

Java Advanced Imaging
---------------------

The **checkForJAI** method will return false if JAI is not installed into the current JRE:

::

    boolean optional = UDIGApplication.checkForJAI();

Your application may need JAI or may not; it is required for raster operations - if your application
is limited to vector work you can get by without this functionality.

GDI
---

The **checkForGDI** method will return false if GDI+ is required (ie on WIN\_32 platform) and not
available:

::

    boolean required = UDIGApplication.checkForGDI();

We required GDI+ for SWT matrix calculations; instructions on how to obtain GDI+ for older versions
are provided as part of the running udig instructions.
