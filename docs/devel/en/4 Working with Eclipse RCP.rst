4 Working with Eclipse RCP
==========================

* :doc:`00 Working with Plugins`


   * :doc:`1 Creating a Plugin`


      * :doc:`Common Plugin Files`

      * :doc:`PlugIn Structure`


   * :doc:`2 Plugin Setup`


      * :doc:`Plugin Dependency`

      * :doc:`Plugin Internationalization`


   -  `3 Plugin Activator <3%20Plugin%20Activator.html>`_ — responsible for managing the life cycle
      of your plugin
   -  `4 Plugin Internationalization with
      ResourceBundles <4%20Plugin%20Internationalization%20with%20ResourceBundles.html>`_
   -  `5 Image Cache and Images <5%20Image%20Cache%20and%20Images.html>`_ — management of
      :doc:`Icons`

   * :doc:`6 Plugin Help`

   * :doc:`7 Plugin Classloader Use`

   -  `8 Adding Debug Tracing Support to Your
      Plugin <8%20Adding%20Debug%20Tracing%20Support%20to%20Your%20Plugin.html>`_

* :doc:`01 Working With Features`

* :doc:`02 Making a Branding Plugin`

* :doc:`03 Making a Product and Executable`

* :doc:`04 Using UDIGApplication`


   * :doc:`Login Example`

   * :doc:`Using uDig 1.0 Application`


-  `06 Traditional Menus using Actions and
   ActionSets <06%20Traditional%20Menus%20using%20Actions%20and%20ActionSets.html>`_

   * :doc:`Adding a Menu to uDig 1.0`


-  `07 New Menus based on Commands, Handlers and Key
   Bindings <07%20New%20Menus%20based%20on%20Commands,%20Handlers%20and%20Key%20Bindings.html>`_
-  `09 uDig menus using Operations and
   Tools <09%20uDig%20menus%20using%20Operations%20and%20Tools.html>`_
-  `10 Adding History to Dialogs and
   Wizards <10%20Adding%20History%20to%20Dialogs%20and%20Wizards.html>`_
* :doc:`11 Working with SWT and JFace`


   * :doc:`1 Actions`

   * :doc:`2 Wizards`

   * :doc:`3 Components and Layouts`

   * :doc:`Adding Control Decorations`


* :doc:`12 Working with Extension Points`


   * :doc:`0 What is an Extension Point`

   -  `1 Creating and Using Extension
      Points <1%20Creating%20and%20Using%20Extension%20Points.html>`_
   * :doc:`2 uDig extension points list`

   * :doc:`3 Example of creating a view`

   -  `4 Example of extending an existing
      perspective <4%20Example%20of%20extending%20an%20existing%20perspective.html>`_
   -  `5 Example of creating a new
      perspective <5%20Example%20of%20creating%20a%20new%20perspective.html>`_
   * :doc:`Creating Singleton extensions`


* :doc:`13 Testing`


   * :doc:`1 Writing JUnit Test Plugins`

   * :doc:`2 Support Classes`


* :doc:`14 Bundle a JAR up as a Plugin`

* :doc:`15 How to turn stuff off`

* :doc:`How do I turn off menus`

* :doc:`Using the UDIGWorkbenchAdvisor`

* :doc:`Using UDIGMenuBuilder`

* :doc:`Using WorkbenchConfigurations`

* :doc:`Working with Cheat Sheets`


Tips, tricks and utility classes for working with the Eclipse RCP platform.

Please understand that fashions change - and the Eclipse RCP API has evolved over time. As such some
of these instructions will be dated; please make use of the Eclipse Help menu for accurate
documentation that reflects the version of Eclipse you are running with today!

As an example Eclipse 3.4 introduced a new "org.eclipse.ui.menu" extension point; use of this
extension should gradually replace the use of Actions and ActionSets.
