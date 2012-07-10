03 GeoSelectionService
======================

GeoSelectionService.
====================

What is this?
-------------

``GeoSelectionService`` is an UDIG object service for geographical information selection management
and interoperability of various application parts through unified processing unit.

In Eclipse RCP applications the standard workbench selection service is available for providers and
listeners to bind each other through well defined API hiding implementation specifics. The workbench
selection service is general and especially used in structured viewers to organize user selections
and listeners responsible for the selection processing. It is not fully appropriate for management
of selection of distinct geographical information on the map since does not bring the functionality
of multiple selection providers (various modal tools being able to make selections with different
semantics) and persistent storage has been made across switching of maps.

``GeoSelectionService`` follows the paradigm of standard workbench selection service and brings
mentioned functionality for the persistent management of multiple selections of geographical
information/objects on the maps in different contexts.

The architecture
----------------

The ``GeoSelectionService`` API is contained in package
``net.refractions.udig.project.geoselection``. Like ``ISelection`` interface works for workbench
selection service keeping selected objects, ``IGeoSelection`` gives a value for
``GeoSelectionService``. The one advantage of the ``IGeoSelection`` is that it is ``IAdaptable`` and
implementations can be really specific hiding all details behind ``IAdaptable`` interface. Listeners
donâ€™t really need to know the real implementation of ``IGeoSelection`` in most cases while working
with ``IAdaptable.getAdapter(Class)`` method to extract selected objects of the type they are
interested in.

``IGeoSelection`` is still under design.

``GeoSelectionService`` is a just singleton point of access like Display in SWT world. It contains
multiple selection managers implementing ``IGeoSelectionManager`` interface. The UDIG platform
contains the main selection manager:
 ``PlatformGeoSelectionManager implements IGeoSelectionManager``

It is available from ``GeoSelectionService.getDefault().getPlatformSelectionManager().``

The functionality of ``PlatformGeoSelectionManager`` is based of the currently active ``MapEditor``
with an active ``IMap`` object. ``IGeoSelections`` can be made as by modal tools like standard
selection tools or programmatically from any piece of code.

The paradigm of ``GeoSelectionService`` brings the next items:
 Multiple ``IGeoSelectionManagers`` can be registered with ``GeoSelectionService`` to serve specific
needs of the developers. The default ``IGeoSelectionManager`` is an implementation
``PlatformGeoSelectionManager`` available from
``GeoSelectionService.getDefault().getPlatformSelectionManager()``.
 ``IGeoSelectionManager`` supports multiple ``IGeoSelections``: each of them in the specified
context. The context defines the domain of substitution policy for the ``IGeoSelection``. The
substitution policy determines the scope where only one ``IGeoSelection`` can be made and be active
and persistent. The ``IGeoSelection`` being made in a context â€œrewritesâ€� the previous
``IGeoSelection`` in the same .
 ``IGeoSelectionManager`` supports notification of all registered listeners when any new
``IGeoSelection`` is being made with a ``GeoSelectionChangedEvent``.
 ``IGeoSelectionManager`` supports providing the iteration capability through all currently made and
persisted ``IGeoSelections`` in their unique contexts.

Use cases
---------

The modal selection tool creates a Filter-based selection of multiple features: ``IGeoSelection``
implementation will be adaptable to ``Filter, ILayer`` where the selection is made and the object is
passed to the ``PlatformGeoSelectionManager``. All listeners will get an event with old selection
object, new selection object and other necessary information. Feature editor can listen
PlatformGeoSelectionManager to respond on the event: to create an UI for the editing of attributes
of multiple features described by Filter.

When the ``FidFilter`` is used for one feature the ``IGeoSelection`` can adapt to an instance of
``Feature`` and a corresponding editor can create an UI for editing of Feature attributes.

The context menu being opened on the ``MapEditor`` surface can ask the
``PlatformGeoSelectionManager`` and iterate through all currently available ``IGeoSelections`` to
create necessary items in the menu. For this purpose the new small framework is desired to configure
context menu items based on ``IGeoSelections`` in ``GeoSelectionService`` through RCP extension
point mechanism in future.
