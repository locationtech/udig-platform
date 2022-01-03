.. _what_is_new_2_3:

What is new for uDig 2.3
========================

This is a release candidate, prior to the creation of a stable 2.3.x series. Please consult upgrade guide in case you are using SDK to build your own geospatial application with uDig.

.. contents:: :local:
   :depth: 1

Improvements and Fixes
----------------------
* `#398 <https://github.com/locationtech/udig-platform/issues/398>`_ : valid state for layer in Layers View as checked if visible
* `#415 <https://github.com/locationtech/udig-platform/issues/415>`_ : fixed NullPointerExecption if tool category extension has name attribute not set
* `#420 <https://github.com/locationtech/udig-platform/issues/420>`_ : allows to hide composite renderer jobs shown in Progress View, see :ref:`pref_project_HIDE_RENDER_JOB`
* `#421 <https://github.com/locationtech/udig-platform/issues/421>`_ : Option to disable CRS Chooser in Map Editor, see :ref:`pref_project_DISABLE_CRS_SELECTION`
* `#432 <https://github.com/locationtech/udig-platform/issues/432>`_ : allows translate Progress View label and nl-specific icon
* `#439 <https://github.com/locationtech/udig-platform/issues/439>`_ : uses Mockito as Test-Mockup framework rather than EasyMock
* `#443 <https://github.com/locationtech/udig-platform/issues/443>`_ : moved jfreechart libraries into separate bundle, to allow to choose other/newer version for sdk-users
* `#444 <https://github.com/locationtech/udig-platform/issues/444>`_ : ActiveMapTracker implemented as IStartup
* `#431 <https://github.com/locationtech/udig-platform/issues/431>`_ : Fixed links from Welcome-Screen to internal documentation
* `#433 <https://github.com/locationtech/udig-platform/issues/433>`_ : removed deprecated classes org.locationtech.udig.project.ui.PlatformGIS.java (use ApplicationGIS instead)
* `#451 <https://github.com/locationtech/udig-platform/issues/451>`_ : migrated from Travis build to GitHub workflow to get feedback for any change
* `#435 <https://github.com/locationtech/udig-platform/issues/435>`_ : fixed print issue on Windows OS
* `#640 <https://github.com/locationtech/udig-platform/issues/640>`_ : allows to enable advanced projection support and continuous map wrapping with preference `org.locationtech.udig.project/ADVANCED_PROJECTION_SUPPORT=true`, see :ref:`Advanced projection support <project_preferences-advanced-projection-support>`
* fixed Exceptions
  (
  `#415 <https://github.com/locationtech/udig-platform/issues/415>`_,
  `#403 <https://github.com/locationtech/udig-platform/issues/403>`_,
  `#406 <https://github.com/locationtech/udig-platform/issues/406>`_,
  `#424 <https://github.com/locationtech/udig-platform/issues/424>`_,
  `#393 <https://github.com/locationtech/udig-platform/issues/393>`_,
  `#430 <https://github.com/locationtech/udig-platform/issues/430>`_
  )
* clean-up dependency management (
  `#401 <https://github.com/locationtech/udig-platform/issues/401>`_,
  `#411 <https://github.com/locationtech/udig-platform/issues/411>`_,
  `#417 <https://github.com/locationtech/udig-platform/issues/417>`_,
  `#440 <https://github.com/locationtech/udig-platform/issues/440>`_,
  `#428 <https://github.com/locationtech/udig-platform/issues/428>`_
  )
* Removal of deprecated classes (
  `#649 org.locationtech.udig.project.command.BasicCommandFactory.java`_ : Please us the class instead org.locationtech.udig.project.command.factory.BasicCommandFactory.java
  )
