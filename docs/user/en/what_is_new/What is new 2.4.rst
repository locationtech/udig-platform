.. _what_is_new_2_4:

What is new for uDig 2.4
========================

This is a release candidate, prior to the creation of a stable 2.4.x series. uDig 2.4 provides the same API as uDig 2.3 but runs on an updated runtime. Please consult upgrade guide in case you are using SDK to build your own geospatial application with uDig.

.. contents:: :local:
   :depth: 1

Updated 3rd-party Dependencies
------------------------------

uDig has been updated to Eclipse Platform Release 2022-03 and Tycho 2.7.5 (`#686 <https://github.com/locationtech/udig-platform/pull/686>`_). Java 11 is required to build and run uDig from now on, Java 8 is not supported anymore.

Applications based on the uDig SDK have to use Java 11 and a target platform based on Eclipse Release 2022-03.
