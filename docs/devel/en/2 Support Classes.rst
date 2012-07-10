2 Support Classes
=================

There are a few classes to assist writing uDig tests.

In **net.refractions.udig.ui.tests**:

-  **UDIGTestUtil** - Provides methods for:

   -  Creating Features
   -  Creating GeoResources
   -  Creating Services
   -  Waiting for UI events to complete

In **net.refractions.udig.project.tests**:

-  **MapTests** - Provides methods for creating maps
-  **AbstractProjectTest** - Super class for test cases. Attempts to clean up after running by
   clearing the project registry, maps, and projects

In **net.refractions.udig.project.ui.tests**:

-  **AbstractProjectUITest** - Extends **AbstractProjectTest** and tries to close mapeditors in
   addition to what **AbstractProjectTest** does..

