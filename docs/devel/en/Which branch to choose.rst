Which branch to choose
======================

The answer to this question comes down to what your focus is going to be when working with the uDig
project.

uDig 1.1.x branch
=================

The udig 1.1.x branch is stable and not subject to change; indeed there will be no further changes
on this branch, and very little by way of bug fixes.

-  Use this branch if you just want to focus on your own project and are happy with the level of
   functionality provided by the sample uDig 1.1. sample application.

The uDig 1.1.x branch makes use of the GeoTools 2.2.x library. GeoTools 2.2.x is only being kept
alive for the uDig project. While we can make new point releases for GeoTools 2.2.x (to fix bugs in
GeoTools) please be advised that is not an appropriate platform for any new development - quite
simply the community has moved on.

We hope to make a 1.1.x releases as needed. Please understand there is no funding for this work -
the only motivation is to recognize the translation and bug fixes donated by the community, and
support uDig based projects.

GeoTools 2.3.x and GeoTools 2.4.x are not a suitable target platform for the uDig project; while
additional work has been done on new Datastores (for use with GeoServer) there is a range of known
problems with these releases; and we are making the effort to fix them for the GeoTools 2.5.x
release.

uDig 1.2.x branch
=================

The uDig 1.2.x branch is not stable and is subject to a lot of change. Some of these changes are bug
fixes and improvements to the uDig framework the vast majority of effort has gone into updating to
the use GeoTools 2.5.x and fixing several problem that prevented us using GeoTools 2.3.x and
GeoTools 2.4.x.

-  Use this branch if you are wanting to work with the new improved GeoTools raster implementation
-  Use this branch if you are hooking up your own DataStore implementation

Updating to GeoTools 2.5.x
--------------------------

The following work is needed in order to update to GeoTools 2.5.x.

Increase in scope for GeoTools 2.3-2.5:

-  new Feature model based on GeoAPI and informed by ISO Feature Model and GML 3 requirements
-  new Filter model based on GeoAPI and Filter 1.1 specification, provides the ability to work with
   Filters and Expression based on more than just Feature content and makes good on the promiss of
   using Expressions as a "untyped" middle ground between a range of object models.

Known problems and bug fixes needed for GeoTools 2.3-2.5:

-  Shapefile DataStore was known to deadlock when reading and writing on two different threads
-  The aggregate functions used by uDig used to operate by "magically knowning" their parent
   FeatureCollection; the functions have been recast to explicitly work on a FeatureCollection - we
   need to test out and fix this new definition and update the uDig Style Editor Theme page to
   operate based on these new ideas.
-  The list of supported DataStores for uDig 1.1.x was limited to those FeatureStores that handled
   transaction; and events correctly. To successfully support more DataStores additional QA will be
   needed for each DataStore added to the project. A quick test is open up two maps and edit the
   same content; noticing each time the screen does not refresh or the results look wrong.
-  The WFSDataStore is stuck at version 1.0 of the specification; and we have reports of problems
   parsing TransactionResponse.

For more information please review:

* :doc:`Technical Debt`

* :doc:`2.5.x`


.. figure:: images/icons/emoticons/information.gif
   :align: center
   :alt: 

In the past I have managed to give offence by implying that GeoTools did not keep enough QA controls
in places this work was done; the truth is that keeping the library usable for a desktop application
is the responsibility of the uDig project. We took a break and focused on our developer community
for a couple of years - now is the time to pay the price.

Please be kind when reporting bug fixes and remember that the GeoTools community is not responsible
for meeting our requirements; we are.
