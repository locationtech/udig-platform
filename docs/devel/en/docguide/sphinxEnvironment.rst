Sphinx Environment
==================

This page describes how to prepare your enviroment to allow to build uDig documentation.

Sphinx Requirements
-------------------

The following list represents the Versions we use and test with to build uDig Documentation.
There might exists newer versions out there you can test with. We recommend to use versions as follows:

* `Python 3.14 <https://www.python.org/>`_ (Sphinx 9.2.0 requires at least Python 3.13)
* `Sphinx 9.2.0 <https://www.sphinx-doc.org/en/master/usage/installation.html>`_

Install Sphinx with the same version as used by the GitHub workflow::

    pip install "sphinx==9.2.0"

.. note::
   The Sphinx build is skipped if ``sphinx-build`` is not available in ``PATH``. Make sure to build
   the documentation with ``mvn clean install`` to avoid packaging outdated documentation.

Getting Started Guides
----------------------

* `How to install Python <https://www.python.org/about/gettingstarted/>`_
* `How to setup and use Package Installer for Python (pip) <https://pip.pypa.io/en/stable/installation/>`_