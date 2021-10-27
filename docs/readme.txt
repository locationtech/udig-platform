To build the docs for the first time....

.. this is covered in the developers guide setup instructions in greater details.

Windows Sphinx Install
^^^^^^^^^^^^^^^^^^^^^^

Install Python:

1. Python version 3.9 has been verified to work: https://www.python.org/downloads/release/python-3100/
2. You will need to add it to your path.::
     
     set 'PYTHON=C:\Python-3.9.0\'
     set 'PATH=%PATH%;%PYTHON%'

3. To install sphinx easily its recommend to install pip first

   download https://bootstrap.pypa.io/get-pip.py

   run 'python get-pip.py'

5. Install Sphinx
   
        pip  sphinx==4.2.0

   Optionally you could install a specific version of sphinx (although we try and use the latest)::

        easy_install sphinx==4.2.0

Mac Sphinx Install
^^^^^^^^^^^^^^^^^^

You can use the distribution manager of your choice (example bru, macports, etc...). The following example
is for macports.

1. On OSX Use macports to install Python 3.9::
     
     sudo port install python39
     sudo port install python_select
     sudo python_select python39
     
2. You can use macports to install Python Image Library::
     
     sudo port install py27-pil
     
3. You can now use python easy_install to install sphinx::
     
     sudo easy_install sphinx
   
   Optionally you could ask for a specific version (we try and use the latest)::
     
      sudo easy_install sphinx==4.2.0
 
4. To build the PDF targets you will also need rst2pdf.::
     
     sudo easy_install rst2pdf

5. If you uses easy_install to grab the python image library it easy to get compile errors.
      

Linux Sphinx Install
^^^^^^^^^^^^^^^^^^^^

Use apt-get and easy install.

1. Python is usually available by default, if not::
     
      apt-get install Python
  
   You may need to use sudo (if for example you are on unbuntu)
      
2. Use easy_install to graph sphinx (using sudo if required)::
     
     easy_install sphinx
  
  Optionally you can install a specifc version::
  
     easy_install sphinx==4.2.0
 
