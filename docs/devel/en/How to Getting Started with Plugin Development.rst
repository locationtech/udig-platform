How to Getting Started with Plugin Development
==============================================

* :doc:`Where to Start`

* :doc:`How to Make a View`


Where to Start
==============

    I am new in uDig framework and in GIS programming in particular.
     However, i want to make uDig work with a health database, in mysql.
     attached please find my plugin specification.

    basically i want to add a function which connect to a database,
     search an entry on the database, seach the same entry on shape file,
     then highlith the image on the map.

    This is in real life application. Please help me how to go about.

Sounds like fun!

Well we are happy to help with facilities over here, as in we can set up up with subversion space in
the community section. You should sign up to the developers email list and work your way through the
initial quickstart and plug-in tutorial.

Steps:

#. Sign up:
   :doc:`http://lists.refractions.net/mailman/listinfo/udig-devel`

#. Install: `1 SDK Quickstart <1%20SDK%20Quickstart.html>`_ for simple plugins (or
   `Home <http://udig.refractions.net/confluence//display/ADMIN/Home>`_ for core development)
#. Follow: `2 Plugin Tutorial <2%20Plugin%20Tutorial.html>`_

From there on out you can start to get our own uDig/Eclipse RCP docs and pick up some Eclipse books
(for help in making a user interface and so on), and I assume you are familiar with the MySQL/JDBC
part of things?

#. Read: `Home <Home.html>`_
#. Read: `5 Reading List <5%20Reading%20List.html>`_

One thing I should note, our docs are on as an needed basis, so as you have questions we will due
our best to make a page of docs to answer email, and help people later. So you you are already on
the right track, ask questions!

#. Email: `udig-devel@lists.refractions.net <mailto:udig-devel@lists.refractions.net>`_

How to Make a View
==================

    I have managed to develop a plug-in and is linking with a uDig very well. Is it possible to make
    a 'view' to apear like a form, with textboxes and buttons.

    I need to create form which will allow users to enter/select values. For example, i put a link
    on the view and when a user doubleclick it a form is poping up.

    currently my form opens outside eclipse!

Please follow any Eclipse book; or tutorial on the web for instructions on how to make a view.

Here is the one I used when learning:

* :doc:`Creating an Eclipse View`


In general we are using the Eclipse RCP platform; any instructions or tutorials you find on the
subject will serve you well when working on uDig. While we have taken some notes on tips and tricks
we have found; your best resource is often the Eclipse Help menu (because it is up to date and
matches the version of eclipse you are working with).
