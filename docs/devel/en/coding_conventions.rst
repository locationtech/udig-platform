Coding Conventions
==================

As a strict policy we do our best to **use the tools** to capture our coding conventions; as such we
make use of FindBugz, Code Templates and Code Formatters for all the small stuff.

This page covers what is left.

Converting a URL to a File
--------------------------

When you need to convert a java.net.URL object to a java.io.File object, you must take care that you
do not ignore the 'authority' property on the URL object. This contains information about the server
that the file is located on. On Windows systems, this includes the drive or network share, such as
"E:" or "fooServer".

The rule is simple: If URL.getAuthority isn't null or empty, then the File should be constructed
like so:

.. code-block:: java

    URL url = ...
    File f = new File("//" + url.getAuthority() + url.getPath());

It is very important that you always check the authority when converting to a File!

There is a utility method in URLUtils that will do this for you:

.. code-block:: java

    File f = URLUtils.urlToFile(url);

Use it!

Logging in Bundles
------------------
To write logs that appear in the Error Log View with different states, it is recommended to use LoggingSupport-class of core bundle.
A common scenario is logging in case of an exception shown as follows:

.. code-block:: java

    try {
        // do something here that might cause an error
        pseudoFunction();
    } catch (Exception e) {
        LoggingSupport.log(PluginImplementation.getDefault(), "error executing pseudo function", e);
    }

The first parameter is the Plugin of the bundle to create log-entries for. A message without a Throwable just creates an **INFO** entry
in Error Log View. Throwable is a **WARNING** whereas any other Exception is reported as an **ERROR**.

Example Checklist for an Import Wizard
--------------------------------------

The preceding sections on 

* :doc:`uDig Guidelines <udig_guidelines>`, 
* :doc:`Eclipse House Rules <eclipse_house_rules>` and 
* :doc:`User Interface Guidelines <user_interface_guidelines>` 

may be a little bit abstract. Here is the results of apply those guidelines into an 
actual Quality Assurance Checklist for the UDIGImportPage.

Load Data

-  listed in the "Add Layer" and "Data Import" wizards
-  banner and a title
-  starts with a prompt (not an error message)
-  tab order as fields are filled in
-  test if data is loaded correctly
-  test that new content is selected (in layer or catalog view)

Context and Error

-  starts with fields based on workspace context (ie the data we just loaded)
-  restart page and check that history is remembered
-  fill out the fields incorrectly and check reporting of error messages

Drag and Drop (Dnd):

-  DnD with appropriate URL (check of canProcess method)

Help and Internationalization

-  Check for presense of online reference page for this wizard
-  Press F1 and (or click on the ?) for context sensitive help
-  Restart and run tests with Italian

Wizard Implementation Tips
--------------------------

-  Workspace context is based on IDataWizard.getSelection().
-  History is maintained in Dialog settings and is remembered across runs 
   (See :doc:`adding_history_to_dialogs_and_wizards`)
-  Steal an existing wizban image and modify
-  jdbc urls are not "valid" urls, see the `jdbc
   trail <http://java.sun.com/docs/books/tutorial/jdbc/basics/connecting.html>`_ and are not usual
   done using DND

Code Checks
-----------

-  Turn on all warnings as in environment setup
-  Classes have at minimum a javadoc comment
-  Strings are externalized for internationalization

