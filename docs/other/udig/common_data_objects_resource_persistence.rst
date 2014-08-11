Common Data Objects Resource Persistence
########################################

uDig : Common Data Objects resource persistence

This page last changed on Oct 29, 2011 by jgarnett.

Motivation
==========

The EMF resource persistence implementation in uDig 1.2.x have several shortcomings. The biggest
shortcoming is the use of absolute EMF resource URIs, which prevents users from moving a uDig
project from one folder to another. The other big shortcoming is the "proprietary" support for
concurrent editing of EMF objects. The massive effort required to maintain the EMF models in uDig
that this entails, argues that migration to a framework already maintained by others should be
chosen instead. There are several frameworks that address persistence and concurrent modifications.
`Teneo <http://wiki.eclipse.org/Teneo>`__ is one example on a database persistency solution for EMF
that is based on Hibernate or EclipseLink. `EMF
Transaction <http://www.eclipse.org/modeling/emf/?project=transaction#transaction>`__ is a another
example on a framework that allows concurrent modifications in a multi-threaded environment.

A contender which address both persistence and concurrency is `CDO <http://wiki.eclipse.org/CDO>`__.
CDO have been around for some time and the capabilities of CDO are impressive. It supports partial
loading and saving of EMF objects, distributed notifications (`see
visualization <http://www.youtube.com/watch?v=Cn5-Rg4nf0Y&feature=player_embedded>`__), paging,
standard (SQL) and user-defined query support, remote and embedded object repositories, cloning,
branching, temporality (rollback to reversion), merging, `concurrent
modifications <http://thegordian.blogspot.com/2011/07/concurrent-access-to-models.html>`__ and much
more. The documentation however, is not that good. The same goes for Teneo though. EMF Transaction
is really the only framework that have documentation that is good enough.

Inspiration
===========

The enhancement of the EMF persistence extension openes up to several ideas (from IRC chat between
Moovida and Kengu):

-  finally have a project that can be called as such. Really a workspace that may contain all the
   handled resources and can be handed over to any other uDig user. Just zip that thing and open it
   up with uDig on any other computer! CDO does this using a embedded store containing any number of
   repositories, each persisted to for example H2
-  it has been shown already in the past, but maybe we could bring that to the foregroud: the
   possibility to create persistence objects by simply extending the correct extension point. This
   could mean that we could give the result of a simulation a structure and save it the same way we
   do with maps. CDO supports both generated (static) and dynamic EMF, which allows any EMF object
   to be persisted to a repository in the CDO Store.
-  database integration. Applications extending uDig most of the time need a database support. We
   already have at least postgis and H2 remote and embedded database support for spatial data out of
   the box. Wouldn't it be nice to be able to use extention points to create objects that we can use
   in extending plugins? Oh yes, it would... Imagine you have some objects that you would want in
   the database, like records of measured data. Then imagine you just need to register the Namespace
   URI of the package which contains the objects, a resource name. And the framework does the rest.
   It would even handle indexing for you.
-  project and data versioning!!! Want an example? here it goes:
    1. User starts new project
    2. User shares the host and port number to project members
    3. User make change
    4. All users sees change in uDig (aka new map, layer, resource in catalog etc)
    NOT ENOUGH? Ok, let's go on:
    5. One user looses connection/goes offline
    6. Other users continue to change project
    7. Offline user make changes to local clone
    8. User goes online
    9. Remote and offline changes are merged into new
    Now, how does that sound? Like **social editing** of GIS projects? Zuckerman, we are coming for
   you |image0|

"Proof of concept"
==================

To get some idea of how much work is required to complete the migration to CDO, a local "proof of
concept" branch of uDig has been created. after some minor changes to the project model (old
GeoTools dependencies), the project and printing models was easily migrated to CDO using embedded H2
as local CDO store (standard CDO implementation). Project registry, project, map and layer resources
was successfully persisted to the H2 datastore. However, because of many modifications to generated
objects (non-standard lazy object creation and loading), maps did not render correctly.

**Discussion**

There is a lot of complexity built into the model (ProjectImpl and MapImpl are really scary), which
make them very difficult to understand and maintain. Inspection show three types of modification
done to generated EMF objects, which force the use off **@generated NOT**.

-  Lazy object creation (in getXXX() methods), example ProjectImpl.getLayerFactory()
-  Object locking and other synzhronization stuff
-  Methods for object creation and destruction

So, what can we do to reduce the complexity of these models to make them easier to understand and
maintain?

Lazy object creation

This can be moved into the factory methods instead, leaving the generated getXXX() methods
unchanged.

Object locking and other synchronization stuff

If we migrate the models to CDO, most of the synchronization stuff can be removed. CDO ensures that
each thread can access the model without the risk of race conditions resulting in memory
inconsistencies. The only remaining locking required is when a specific access order is required by
uDig processes, for example synchronization with the display thread.

Methods for object creation and destruction

These methods does not belong inside EMF objects. EMF objects are hard to understand just as they
are. Adding more methods to them than strictly necessary is contra-productive. A set of utility
classes should supply these methods instead.

**Concerns**

There are some concerns to be aware about.

The move to CDO will break all previous uDig projects

If the community wants uDig to still support standard EMF resource formats (XML,XMI,binary), the
move become much harder. It will require that model APIs are decoupled from generated
implementations. This is so because standard EMF and CDO EMF extends different base types
(InternalEObject and InternalCDOObject respectively). One nice solution is to create an
InternalUDIGObject which delegates to the actual uDig model implementation, determined during
project creation. This will involve one uDig model API model, and two uDig model implementations,
one using standard EMF (the model in use today), and one using CDO EMF. Clearly, the easiest and
best solution in terms of time and effort required, is to drop backwards compatibility and just add
a import method for old uDig projects.

uDig project resources will no longer be human readable

This depends on the CDO DB Store implementation. CDO currently support Derby, H2, HSQLDB, MySQL,
Objectivity and Hibernate. The first four DBs can all be embedded, so uDig projects can be persisted
locally without a DB being installed on the client machine. But the persistence (file) formats are
not human readable, making manual recovery much harder without a reader supporting the DB backend.
However, since CDO is versioned, most resource errors caused by users or programming bugs should be
recoverable (rollback at the persistence level).

The move to CDO is probably going to break some community plug-ins

Community plug-ins that depend on the non-standard EMF behavior of uDig models is going to break.
However, we will not know how big this problem is before the move is completed, since most of the
changes are internal (does not change public APIs).

uDig resource extension points

A common problem facing uDig developers is how to add their own resources to the uDig project model.
There is some support for this today, but it is really limited, and is does not "play that nice"
with existing resources. A better solution has to be found. Since CDO allows dynamic addition of new
resources, a quick fix is to add a API (extension point and interface) that allow plug-ins to add
custom resources to the project model.

**Conclusion**

The attempt to migrate to CDO shows that it is doable, but not without costs. Implementing the
changes described above, without breaking something is hard. The best discourse is probably to
rebuild the uDig project model from scratch, keeping the interfaces unchanged. As long as interfaces
does not change, client code depending on the model acting like standard EMF, should not be broken.
This allows us to redesign the inner parts of the model, implementing better synchronization
support, reducing the complexity and thus making it easier to maintain.

Proposal
========

We propose to make the following changes to a public uDig branch branched from the 1.2.2 master
branch.

#. **Migrate uDig models to CDO**
    This is the easy part, only requiring some minor API work to make uDig conform with the latest
   GeoTools APIs
#. **Catch up on technical debts in uDig models**
    Remove discrepad objects, attributes and operations from uDig models (there are quite a few of
   those). Look into how notifications are handled. For example, construction of layers raise
   notifications in the catalog which loads resources when they should not have been.
#. **Rewrite the internals of uDig Project model**
    This is the scary part, since much is still unknown about the dependency on non-standard EMF
   behaviors currently implemented.
#. **Add a (automatic) import/recovery method for old uDig projects**
    This is best done automatically, since current uDig models are really not moveable.
#. **Add project export/import methods**
    Since a uDig project is a catalog with files, moving it is best done as a zipped catalog.
    Methods for packing and unpacking uDig project catalogs should be added to make the procedure as
   easy as possible.
#. **Add API that allows uDig dependent plug-ins to easily add custom EMF resources to uDig
   projects**
    This involves both extension points and interface/class methods
#. **New documentation**
    Write documentation that make it easier to maintain, modify and re-generate uDig models

*In summary, the adoption of this proposal adds*:

-  Real project support, allowing users to move projects around
-  Better concurrency control, using a main-stream transaction framework, redusing the complexity of
   uDig model maintenance
-  Real-time and fault-tolerant sharing of uDig resources (aka social editing)

Much of this work is best done as code sprint, since the move probably involve some changes to uDig
plug-ins depending on model APIs behaving in non-standard EMF ways. The first real chance is the
`sprint after FOSS4G 2011 in Denver <http://wiki.osgeo.org/wiki/FOSS4G_2011_Code_Sprint#uDig>`__.

Documentation
=============

Documentation change to `Developers
Guide <http://udig.refractions.net/confluence//display/DEV/Home>`__ or `Project
Procedures <http://udig.refractions.net/confluence//display/ADMIN/Home>`__ (for an accepted change)
will come as the migrations moves along. The documentation is divided into the following sections

-  Introduction to uDig resources (model APIs)
-  How to modify uDig resource models (design patterns)
-  How to generate uDig resource models (step-by-step instructions)
-  How to use uDig resources in plug-ins (usage patterns)

Since EMF is by it self a BIG topic, task like EMF modeling and generation will not be covered by
the documentation in general, but relevant documentation will be linked when appropriate. As a rule,
the documentation should describe how to do specific task like modifying and generating the models,
and describe what is uDig specific with regard to resource persistence, concurrent modification,
versioning and so on.

Tasks
=====

A list of the tasks needed to accomplish this change;

 

no progress

|image1|

in progress

|image2|

blocked

|image3|

help needed

|image4|

done

Status

Task

Volunteer

|image5|

Get votes from PMC and Committers

Kenneth Gulbrandsøy

|image6|

Initial design for review and feedback (see proposal)

Kenneth Gulbrandsøy

|image7|

Create public branch 1.3.0 from uDig trunk

Kenneth Gulbrandsøy

|image8|

Initial implementation in preparation for code sprint

-  Migrate uDig models to CDO
-  Catch up on technical debts in uDig models

Kenneth Gulbrandsøy

 

Initial implementation of test cases for uDig models

 

 

Rewrite the internals of uDig Project model

 

 

Add a (automatic) import/recovery method for old uDig projects

 

 

Add project export/import methods

 

 

Add API that allows uDig dependent plug-ins to easily add custom EMF resources to uDig projects

 

 

Normal code review from PSC members (javadocs, findbugs, etc...)

 

 

Updated developers guide documentation

 

Schedule considerations:

-  Fall 2011
-  Jody: After August 18th
-  FOSS4G 2011 is the first week of September

Status
======

Project Steering committee support:

-  Andrea Antonello: +1
-  Jesse Eichar: +1
-  Jody Garnett: +1
-  Mauricio Pazos: +1

Committer Support:

-  Kenneth Gulbrandsoy +1

A vote of -1 requires an alternate suggestion; community members are invited to indicate
support/suggestions.

The RFC is accepted when it has over 3 votes; and no -1 votes.

+------------+----------------------------------------------------------+
| |image10|  | Document generated by Confluence on Aug 11, 2014 12:31   |
+------------+----------------------------------------------------------+

.. |image0| image:: images/icons/emoticons/smile.gif
.. |image1| image:: images/icons/emoticons/star_yellow.gif
.. |image2| image:: images/icons/emoticons/error.gif
.. |image3| image:: images/icons/emoticons/warning.gif
.. |image4| image:: images/icons/emoticons/check.gif
.. |image5| image:: images/icons/emoticons/check.gif
.. |image6| image:: images/icons/emoticons/check.gif
.. |image7| image:: images/icons/emoticons/star_yellow.gif
.. |image8| image:: images/icons/emoticons/star_yellow.gif
.. |image9| image:: images/border/spacer.gif
.. |image10| image:: images/border/spacer.gif
