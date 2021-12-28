Getting Started Eclipse IDE
===========================

Download Eclipse IDE
--------------------
To allow to develop with uDig and contribute to the project is recommend to use Eclipse IDE.

Go to https://www.eclipse.org/downloads/packages/ in your prefered browser application and download latest release
for Eclipse Modeling Tools. Install it on your machine.

Download Java Development Tools (JDT)
-------------------------------------

Download a JDK which is compatible with Eclipse Modeling Tools and install it on the machine as well. It helps if the
bin folder of this installation is configured in PATH environment variable.

Preferences
-----------

We have a few global settings to configure before we can proceed. Open Preferences Dialog
(:menuselection:`Windows --> Preferences`) and configure as follows.

.. note::
   Depending from downloaded Eclipse Release the options that are available in Dialogs might be different than
   shown in screeshots.

Java Installed JRE and Compiler
```````````````````````````````
The JRE/JDK which is used to start Eclipse IDE is configured as default Installed JRE. To allow to build and run
uDig with another JRE/JDK than pre-configured we need to setup one. The following is kind of an example since the
supported version might change over time.

In Preferences Dialog choose :menuselection:`Java --> Installed JREs` and hit :guilabel:`Add` in the right panel.
Navigate on file-system and choose installation folder of JRE/JDK to use

 .. image:: images/ide_jre_selection.png
    :width: 60%

In addition to the JRE its recommend to configure a compile-level for the projects of the Eclipse Workspace. In
:menuselection:`Java --> Compiler` check and set at least :guilabel:`Compiler compliance level` to `1.8`.

 .. image:: images/ide_compiler_compliance.png
    :width: 60%

API Baselines
`````````````
Navigate to :menuselection:`Plug-in Development --> API Baseline` and change the Missing API baseline to ‘Warning’.

 .. image:: images/ide_api_baseline.png
    :width: 60%

Compiler Settings
`````````````````
In :menuselection:`Java --> Compiler --> Errors/Warnings` open the Code Style category and change Non-externalized
strings to **Warning**.

 .. image:: images/ide_compiler_nls_warning.png
    :width: 60%

Code Formatter
``````````````
Configures the **code formatter** to decide where the spaces go and where to set brackes & Co. On the lefthanded
side of the Preference dialog choose :menuselection:`Java --> Code Style --> Coding Template`. Import configuration
from repository checkout location `extras/org.locationtech.udig.dev/codeformatter.xml`.

 .. image:: images/ide_code_formatter.png
    :width: 60%

.. _Autosave Actions:

Code Templates
``````````````
Select :menuselection:`Java --> Code Style --> Coding Template`. Use the Import button and select the
file `extras/org.locationtech.udig.dev/codetemplates.xml`. Confirm the uDig header is available for **New**
Java Files as shown

 .. image:: images/ide_code_template.png
    :width: 60%

Autosave Actions
`````````````````
To keep code organized and clean **Auto Save Actions** helps here. Choose :menuselection:`Java --> Edtor --> Save Actions`
to configure as follows:

 .. image:: images/ide_java_autosave_actions.png
    :width: 60%

Code Style Clean Up
```````````````````
This configuration allows you to clean up existing code and should have the same settings as configured in `Autosave Actions`_.
Go to :menuselection:`Java --> Code Style --> Clean Up` and import configuration from `extras/org.locationtech.udig.dev/codecleanup.xml`.

 .. image:: images/ide_code_cleanup.png
    :width: 60%

How to use it? Just navigate to a project in the Eclipse Workspace (once you have imported uDig modules). Choose with
right mouse on a source folder, any java source package, or any java file with Context menu
:menuselection:`Source --> Clean Up...` and run the task.

Configure Target-Platform
-------------------------

To allow to build and run uDig from with Eclipse IDE its necessary to setup a target platform. The configuration
which is used for both, Eclipse and Maven builds it defined in `extras\org.locationtech.udig.target`. It resolves
dependencies from Eclipse Update Sites such as:

 * Eclipse Rich Client Platform (Indigo or Luna)
 * Eclipse Orbit (open source components that have been checked by the Eclipse legal team)
 * Refractions JAI Bundles

To import it into Eclipse workspace just use :menuselection:`File --> Import...` and select
:menuselection:`General --> Existing Projects into Workspace`. Choose :guilabel:`Next` and navigate to
`extras\org.locationtech.udig.target` folder of repository checkout directory and select it.

 .. image:: images/ide_target_import_project.png
    :width: 50%

 .. note::
    The next step to resolve/download dependencies might take a while depending on the network bandwidth.


Once the project is imported, select the file `org.locationtech.udig.target.target` and open it with Target Platform
Editor. Eclipse resolves configured locations and if everything works out correctly click on :guilabel:`Set as Target Platform`
top right within the editor.


 .. image:: images/ide_target_platform_resolve.png
    :width: 60%

**Optionally** its possible to persist resolved target platform locally to avoid requesting external servers on every startup of
Eclipse. Since the target definition uses online resources it�s a good idea to export it for offline development purposes.
To do so, click on the export action top right in the target platform editor.

 .. image:: images/ide_target_export.png

Choose a destination folder to store features and bundles as a local P2 repository resolved via the target defintion file.

 .. image:: images/ide_target_export_destination.png


Download 3rd-party dependencies
-------------------------------

As a preperation-step its necessary load addtional geospatial libraries. These are not OSGi ready and need to be bundled
for uDig. The easiest way to do so is with Maven itself in console. Open a console which has Maven and Java configured in
**PATH** and execute the following command:

  :command:`mvn clean package -f pom-libs.xml`

Import Projects into Workspace
-------------------------------

Again use :menuselection:`File --> Import...` to import remaining plugins and features into workspace. Select
:menuselection:`General --> Existing Projects into Workspace`. Choose **Next** and navigate to root-checkout folder of
uDig repositiory. Enable option :guilabel:`Search for nested projects`.

 .. image:: images/ide_import_projects.png
    :width: 50%

Deselect in the list the following projects for now:

 * deploy
 * docs
 * installer
 * udigDeploy

Click on :guilabel:`Finish`.

Launch uDig
-----------

Within :guilabel:`Project Explorer` go to project **org.locationtech.udig-product** and double-click on file
`org.locationtech.udig-product.product`. Click :guilabel:`Launch the product`.
