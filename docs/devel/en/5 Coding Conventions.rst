Coding Conventions
====================

As a strict policy we do our best to **use the tools** to capture our coding conventions; as such we
make use of FindBugz, Code Templates and Code Formatters for all the small stuff.

Converting a URL to a File
--------------------------

When you need to convert a java.net.URL object to a java.io.File object, you must take care that you
do not ignore the 'authority' property on the URL object. This contains information about the server
that the file is located on. On Windows systems, this includes the drive or network share, such as
"E:" or "fooServer".

The rule is simple: If URL.getAuthority isn't null or empty, then the File should be constructed
like so:

.. codeblock:: java

    URL url = ...
    File f = new File("//" + url.getAuthority() + url.getPath());


It is very important that you always check the authority when converting to a File!

There is a utility method in URLUtils that will do this for you:

.. codeblock:: java

    File f = URLUtils.urlToFile(url);

Use it!

Example Checklist for an Import Wizard
--------------------------------------

The preceding sections on `1 UDIG Guidelines <1%20UDIG%20Guidelines.html>`_,\ `2 Eclipse House
Rules <2%20Eclipse%20House%20Rules.html>`_ and `3 User Interface
Guidelines <3%20User%20Interface%20Guidelines.html>`_ may be a little bit abstract. Here is the
results of apply those guidelines into an actual Quality Assurance Checklist for the UDIGImportPage.

Check

Load Data

 

listed in the "Add Layer" and "Data Import" wizards

 

banner and a title

 

starts with a prompt (not an error message)

 

tab order as fields are filled in

 

test if data is loaded correctly

 

test that new content is selected (in layer or catalog view)

Check

Context and Error

 

starts with fields based on workspace context (ie the data we just loaded)

 

restart page and check that history is remembered

 

fill out the fields incorrectly and check reporting of error messages

Check

DnD

 

DnD with appropriate URL (check of canProcess method)

Check

Help and Internationalization

 

Check for presense of online reference page for this wizard

 

Press F1 and (or click on the ?) for context sensitive help

 

Restart and run tests with French

Implementation Tips

-  Workspace context is based on IDataWizard.getSelection().
-  History is maintained in Dialog settings and is remembered across runs (See `10 Adding History to
   Dialogs and Wizards <10%20Adding%20History%20to%20Dialogs%20and%20Wizards.html>`_)
-  Steal an existing `wizban <wizban.html>`_ image and modify
-  jdbc urls are not "valid" urls, see the `jdbc
   trail <http://java.sun.com/docs/books/tutorial/jdbc/basics/connecting.html>`_ and are not usual
   done using DND

Code Checks

-  Turn on all warnings as in environment setup
-  Classes have at minimum a javadoc comment
-  Strings should be externalized for internationalization

