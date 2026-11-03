Sphinx Environment
==================

This page describes how to prepare your enviroment to allow to build uDig documentation.

Sphinx Requirements
-------------------

The following list represents the Versions we use and test with to build uDig Documentation.
There might exists newer versions out there you can test with. We recommend to use versions as follows:

* `Python 3.12 <https://www.python.org/>`_
* `Sphinx 4.2.0 <https://www.sphinx-doc.org/en/master/usage/installation.html>`_

.. note::
   Sphinx 4.2.0 does not run with Python 3.13 or newer. In addition it does not pin its dependencies,
   current versions of these packages require a newer Sphinx. Install Sphinx with the same package
   versions as used by the GitHub workflow:

   ::

       pip install "sphinx==4.2.0" "setuptools<81" "sphinxcontrib-applehelp<1.0.5" \
           "sphinxcontrib-devhelp<1.0.3" "sphinxcontrib-htmlhelp<2.0.2" \
           "sphinxcontrib-serializinghtml<1.1.6" "sphinxcontrib-qthelp<1.0.4" \
           "alabaster<0.7.14" "jinja2<3.1"

Getting Started Guides
----------------------

* `How to install Python <https://www.python.org/about/gettingstarted/>`_
* `How to setup and use Package Installer for Python (pip) <https://pip.pypa.io/en/stable/installation/>`_