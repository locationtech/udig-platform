.. _what_is_new_2_4:

What is new for uDig 2.4
========================

This is a release candidate, prior to the creation of a stable 2.4.x series. uDig 2.4 provides the same API as uDig 2.3 but runs on an updated runtime. Please consult :ref:`what_is_new_2_4_upgrade_notes` in case you are using SDK to build your own geospatial application with uDig.

.. contents:: :local:
   :depth: 1

Updated 3rd-party Dependencies
------------------------------

uDig has been updated to Eclipse Platform Release 2022-03 and Tycho 2.7.5 (`#686 <https://github.com/locationtech/udig-platform/pull/686>`_). Java 11 is required to build and run uDig from now on, Java 8 is not supported anymore.

.. _what_is_new_2_4_upgrade_notes:

Upgrade notes for SDK users
---------------------------

The uDig API is the same as in uDig 2.3, so applications and plug-ins based on the uDig SDK usually need changes to their build and runtime environment only:

* **Java 11** is required. Set ``Bundle-RequiredExecutionEnvironment: JavaSE-11`` in your bundles and the compiler compliance level of your IDE to 11.
* **Target platform** has to be based on Eclipse Release 2022-03, see the uDig target definition ``extras/org.locationtech.udig.target``.
* **Bundle dependencies** to uDig bundles with a lower bound of ``bundle-version="2.3.0"`` are still valid.
* **Java Advanced Imaging and ImageIO** are shipped as bundles with uDig, a Java installation with JAI and ImageIO installed into the JRE is not required anymore.
* **Internal Eclipse APIs** might have been changed or removed with Eclipse 2022-03. For example ``org.eclipse.ui.internal.dialogs.DialogUtil.removeAccel(String)`` does not exist anymore, use ``org.eclipse.jface.action.LegacyActionTools.removeMnemonics(String)`` instead.
* ``org.eclipse.core.runtime.Status`` created without message now uses the message of the given exception instead of an empty string.

If you build your application with Tycho, please consider the following changes when updating from Tycho 1.x to Tycho 2.7.5:

* Maven 3.6.3 or newer is required.
* Packaging type ``eclipse-update-site`` is not supported anymore, use ``eclipse-repository`` and rename ``site.xml`` to ``category.xml``.
* Goal ``source-feature`` of ``tycho-source-feature-plugin`` is replaced by goal ``feature-source`` of ``tycho-source-plugin``.
* Parameter ``resolver`` of ``target-platform-configuration`` has been removed.
