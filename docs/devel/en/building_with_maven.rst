Building with Maven
===================

uDig is built with `Maven <https://maven.apache.org/>`_ and `Eclipse Tycho <https://www.eclipse.org/tycho/>`_.
The same build is used by the GitHub workflow for every pull request, so a successful local build is a good
indicator that your change will pass the continuous integration as well.

.. contents:: :local:
   :depth: 1

Requirements
------------

* JDK 11
* Maven 3.6.3 or newer
* Optional, to build the documentation: Python and Sphinx as described in :doc:`docguide/sphinxEnvironment`
* Optional, to run the tests on Linux without a desktop: ``xvfb-run`` (package ``xvfb``)

The build checks the versions of Java and Maven and stops early if one of them is too old.

Overview
--------

The build runs in two steps:

#. **pom-libs.xml** downloads 3rd-party libraries such as GeoTools from Maven repositories. These libraries
   are not available as OSGi bundles, so they are copied into the ``lib`` and ``lib-src`` folders of wrapper
   bundles like ``plugins/org.locationtech.udig.libs``.
#. **pom.xml** builds the uDig bundles, features, product and SDK with Tycho. The bundles of the Eclipse
   platform are resolved from the target platform defined in ``extras/org.locationtech.udig.target``.

Step 1 is required once after checkout and again whenever a ``pom-libs.xml`` changes, e.g. after an update of
GeoTools. The downloaded libraries are ignored by git.

Step 1: Download 3rd-party libraries
------------------------------------

Run in the root folder of the repository::

    mvn clean install -f pom-libs.xml

Step 2: Build uDig
------------------

Build the uDig product::

    mvn clean install -Pproduct

Profiles enable additional parts of the build and can be combined, for example ``-Pproduct,sdk,test``:

.. list-table::
   :widths: 15 85
   :header-rows: 1

   * - Profile
     - Description
   * - ``product``
     - uDig application for Linux, Windows and macOS. The archives are created in
       ``features/org.locationtech.udig-product/target/products/``.
   * - ``sdk``
     - SDK bundles, tutorials and features. The SDK archive ``udig-<version>-sdk.zip`` is created in
       ``features/org.locationtech.udig_sdk-feature/target/``.
   * - ``test``
     - Test bundles, see `Running tests`_.
   * - ``site``
     - p2 update site in ``features/org.locationtech.udig-site/target/repository/``.
   * - ``unstable``
     - Features that are not yet part of the product.
   * - ``docs``
     - Documentation, see `Building the documentation`_.
   * - ``coverage``
     - Code coverage with JaCoCo while running the tests.

Setting the property ``-Dall`` enables the profiles ``sdk``, ``site`` and ``unstable``; ``-Dcoverage``
enables the profile ``coverage``.

Useful Maven options:

* ``-DskipTests`` skips the tests of the test bundles if the profile ``test`` is enabled.
* ``-T4`` builds independent modules in parallel with 4 threads.
* ``--fail-at-end`` continues with independent modules if a module fails and reports all failures at the end.
* ``-o`` builds offline, once all dependencies have been downloaded.

Running tests
-------------

The tests are part of the test bundles which are enabled by the profile ``test``. Many tests require a
display. The GitHub workflow builds everything first and runs the tests afterwards::

    mvn clean install -Pproduct,sdk,test -DskipTests
    mvn integration-test -Ptest --fail-at-end

On Linux without a desktop, e.g. on a server or in a container, run the tests with a virtual display::

    xvfb-run -a mvn integration-test -Ptest --fail-at-end

To run the tests of a single test bundle after a successful ``install`` of the whole project, select the
module with ``-pl``::

    mvn integration-test -Ptest -pl plugins/org.locationtech.udig.core.tests

By default the stack traces in the test reports are trimmed to the frames of the test class. Add
``-DtrimStackTrace=false`` to see the complete stack trace, e.g. if an exception is thrown deep inside uDig.

.. note::
   ``mvn integration-test`` temporarily replaces the version in the ``about.mappings`` files of some bundles.
   The original content is restored in the ``verify`` phase only, so revert these files before committing,
   or run ``mvn install`` instead.

Building the documentation
--------------------------

The documentation is built with Sphinx, see :doc:`docguide/sphinxEnvironment` for the required versions.
It is built in addition to uDig with the profile ``docs``::

    mvn clean install -Pproduct,sdk,docs

To build the documentation only, run the documentation module without Tycho. This is how the GitHub workflow
builds it::

    mvn clean install -f docs/pom.xml -Dtycho.mode=maven

The result is created in ``docs/target``:

* ``user/en/html`` -- User Guide
* ``devel/en/html`` -- Developer Guide
* ``data/en/html`` -- Data Guide
* ``udig-eclipse-help.zip`` -- User Guide as Eclipse help

If the documentation is built with the profile ``docs`` from the root folder, the bundle
``plugins/org.locationtech.udig.help`` unpacks the Eclipse help into its folder ``EN`` and updates its
``toc.xml``. Commit ``toc.xml`` if it changes, e.g. after adding a new page to the User Guide.

While working on a single guide it is faster to build only this guide with one of the profiles ``user``,
``devel`` or ``data``. Use the goal ``compile``, because the ``package`` phase requires the Eclipse help of the
User Guide::

    mvn clean compile -f docs/pom.xml -Dtycho.mode=maven -Pdevel

.. note::
   Sphinx is skipped if ``sphinx-build`` is not available in ``PATH``. Always use ``clean`` to make sure the
   documentation is built from scratch and not taken from a previous build.
