Issues Framework Design
#######################

Issues Framework
================

The issues framework consists of a view that displays all the issues/tasks that has been identified
by uDig and its plugins that need to be resolved in some manner. It is a generic framework that
allows uDig extenders to define custom issues, methods for resolving issues and even the storage
mechanism for the issues.

Basic Design
============

The basic framework consists of a IIssuesManager that provides broad control over a single contained
IIssuesList. An IIssuesList is basically an eventified List<IIssue> that has a couple additional
methods. For example a method for obtaining all issues of the provided GroupID. IIssuesLists raise
events when IIssues are added/removed/saved. A sub-interface of IIssuesList is the IRemoveIssuesList
it has the additional methods save(IIssue) and refresh().

An IIssue is a generic issue that represents an issue or task. It provides accessor methods that
allow information about the issue to be obtained as well as a fix method so the issue can be
*fixed*. IIssues are defined by extension points and what the fix method does is dependent on the
implementation. AbstractIssue reduces the overhead of implementing new IIssues implementations.
AbstractFixableIssue and FixableIssue also help in quickly and easily developing new IIssue
implementations.

Existing implementations
========================

IIssues implementations:
------------------------

-  FeatureIssue - An issue that indicates that a feature needs to be inspected. Description should
   provide more details as to what the issue is with the feature. The fix method opens the map and
   zooms to the issue with the problem. The FeatureEditor is also opened with the feature selected.
-  FixableIssue - An issue that uses the org.locationtech.udig.issues.issueFixer extension point to
   determine how to fix the issue. An accompanying IFixer

IIssuesList implementations:
----------------------------

-  IssuesList - An in-memory implementation of the IIssuesList interface.
-  PostGISIssuesList - An IRemoteIssuesList that stores its issues as features in a PostGIS
   database. The PostGISIssuesList is implemented using the StrategizedIssuesList and the
   AbstractDatastoreStrategy. It is the reference implementation using those support classes and can
   be used to determine how to develop a custom IRemoteIssuesList. The table that the issues list
   uses is quite flexible but it must at minimum have:

-  a Polygon or MultiPolygon or Geometry geometry column
-  8 text columns
-  at least 2 of the text columns should have an unlimited length
    Ideally the table should have the following columns (case is unimportant):

.. list-table::
   :widths: 30 20 50
   :header-rows: 1

   * - Column
     - Type
     - Comments
   * - ID
     - text
     - should be able to contain at least 20 characters
   * - resolution
     - text
     - can be limited to 8 characters
   * - priority
     - text
     - can be limited to 8 characters
   * - description
     - text
     - should be able to contain at least 100 characters
   * - groupID
     - text
     - should be able to contain at least 20 characters
   * - memento
     - text
     - should be unlimited or very large
   * - viewmemento
     - text
     - should be unlimited or very large
   * - extensionid
     - text
     - should be able to contain at least 20 characters
   * - bounds
     - polygon
     - 

.. note::
   The PostgisDatastoreStrategy is a strategy for the StrategizedIssuesList so more implementations 
   will be forthcoming in the future. If you desire an implementation that backs onto another datastore 
   look at the classes: 
   * org.locationtech.udig.issues.StrategizedIssuesList
   * org.locationtech.udig.issues.internal.datastore.PostgisDatastoreStrategy
   * org.locationtech.udig.issues.internal.datastore.AbstractDatastoreStrategy


Extension points
================

For developers who want to create custom issues or issue lists the following extension points will
be of interest to you:

.. list-table::
   :widths: 40 60
   :header-rows: 1

   * - Extension point ID
     - Description
   * - org.locationtech.udig.issues.issue
     - Allows new issue types to be declared. The getExtensionID should return the id of your extension
   * - org.locationtech.udig.issues.issuesList
     - Declares a new issues list implementation
   * - org.locationtech.udig.issues.issueFixer
     - Declares a fixer for subclasses of AbstractFixableIssue

Support Classes
===============

StrategizedIssuesList
---------------------

The StrategizedIssuesList is an implementation of the IRemoteIssuesList interface that delegates the
reading and writing of features to an IListStrategy object. The StrategizedIssuesList handles all of
the "tricky" logic for caching of features organizing them into groups, etc... The IListStrategy is
a very simple interface designed to reduce the overhead of implementing IRemoteIssuesLists.

AbstractDatastoreStrategy
-------------------------

The AbstractDatastoreStrategy is an implementation of the IListStrategy interface converts features
from a FeatureStore (the instance is determined by the implementation of the abstract
getFeatureStore() method) into issues for use by the StrategizedIssuesList

IssueFixer
----------

IssuesFixer is a framework described below that is used by the FixableIssue class to allow the
workflow or method of fixing an issue to be declared as extension of the
org.locationtech.issues.issueFixer extension point. See the next section for more details.

IssueFixer
==========

extension attributes
--------------------

-  **id**: extension identifier
-  **class**: the fixer class which implements org.locationtech.udig.core.IFixer and provides the
   mechanism for resolving issues
-  **targetClass**: the IIssue class this fixer expects to see (a subclass of AbstractFixableIssue).
   If you have a specific IIssue implementation implementation, chances are your issue fixer will be
   specific to that implementation.
-  **requiredKey**: (multiple instances) requires that the saveMemento contain this attribute for
   fixer to be a potential candidate.

.. note::
   The use of targetClass and requiredKey is recommended but not required; using them reduces the
   number of fixer classes which need to be instantiated (in order for the IFixer.canFix method to be
   called) each time an issue is "fixed", resulting in lower overhead.

Methods
-------

The first parameter taken by each method (generic object) is expected to be an AbstractFixableIssue
and should be cast as such.

**canFix(Object, IMemento)**: by looking at an AbstractFixableIssue (IIssue) and its IMemento, this
method determines if the issue is suitable for (can be fixed by) the fixer.

**fix(Object, IMemento)**: starts the issue resolution process. This method is responsible for
calling the complete method in some way, directly or indirectly by initializing other classes which
may do so either automatically or through some sort of workflow.

**complete(Object)**: called by fix method, or one of the classes it sets up to guide the user
through the issue resolution process.

AbstractFixableIssue IMemento composition
-----------------------------------------

The memento of an IIssue (not to be confused with its viewMemento – which is entirely a UI
persistence construct), contains the following *tiered* elements of interest unique to an
AbstractFixableIssue:

-  **saveMemento**: persists the issue

   -  other elements unique to this issue class
   -  **fixerMemento**: persists data relevant only to the fixer (this may include the state of a
      partially resolved issue)

FixableIssue vs AbstractFixableIssue
------------------------------------

FixableIssue is a simple implementation of AbstractFixableIssue which serves as a good example of
how to override the init and save methods. It may be overridden, and subclasses should take care to
override the getExtensionID method and return their own defining extension's ID.

