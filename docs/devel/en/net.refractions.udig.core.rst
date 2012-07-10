net.refractions.udig.core
=========================

Defines the requirements needed for plug-in developers or scripting services.

Functional Requirements
~~~~~~~~~~~~~~~~~~~~~~~

-  access to data (in the catalog sense, list of defined datastore/servers)
-  access to viewport (bbox, crsm transaction *-What is this?*, selection, map layers)
-  access to issues list (is this per viewport? *-We should make a definition for view port*)
-  ability to define user interface (define view, add menu items/toolbars, key short-cuts)
-  ability to cancel a running operation (framework should provide threads by default)
-  generate progress events
-  versioned (installation metadata) *-Elaborate!*
-  Access to temporary data dump (local filesystem or slave database) for operation results (acts as
   a staging area from which user can right click and export to real database table - or sync with
   real database table)
-  should be Transaction Aware (possibly hooked into threading?) *-Provide link to transaction*

Non Functional Requirements
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  strong ui guidelines

Design Notes:
~~~~~~~~~~~~~

-  this plug-in is vastly limited in scope relative to JUMP (focus on hacking data under user
   control)
-  black board is recomended by JUMP for inter plug-in communication, similar to servlet context
-  Apparently a popular request of JUMP is the wish for a visitor that can be scrubbed over one
   dataset to produce a second. Right now jump uses iterator pattern
-  plug-in can just register for the same notifications as the main framework

Thanks to Martin for passing on JUMP experience.

Wild Ideas:

-  provide enough glue to hook plug-in up with Source/Destination FeatureStores (Framework or
   Superclass?)
-  hook threaded by default / destination FeatureStores / progress and Transaction aware together so
   that
    "cancel" or transaction rollback stops everything and cleans up the mess with out explict
   plug-in developer pain *-I can't make sense of this*

