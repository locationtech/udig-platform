Native Support Bundles For Java Advanced Imaging
################################################

uDig : native support bundles for java advanced imaging

This page last changed on Jan 15, 2012 by jgarnett.

Motivation
----------

Right now its quite tricky to setup the developer environment with JAI support in JRE. And it is
quite a lot to setup if testing and developing against different versions of the java runtime
environment. Therefor it would be nice to have this support out of the box. In addition it would be
the eclipse way how to handle libraries for native support of functionality - à la SWT.

Proposal
--------

Setup a few bundles, one with the JAI jars and different fragments with the native libraries, one
for each platform:

java advanced Imaging (JAI)
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  javax.media.jai
-  javax.media.jai.linux.gtk.x86 (contains \*.so files for 32bit linux)
-  javax.media.jai.linux.gtk.x86\_64 Fragment (contains \*.so files for 64bit linux)
-  javax.media.jai.win32.win32.x86 Fragment (contains \*.dll files for 32bit windows)

example manifest for JAI host bundle:

.. code:: code-java

    Manifest-Version: 1.0
    Bundle-ManifestVersion: 2
    Bundle-Name: Java Advanced Imaging API
    Bundle-SymbolicName: javax.media.jai
    Bundle-Version: 1.1.3
    Bundle-ClassPath: lib/jai_codec-1.1.3.jar,
     lib/jai_core-1.1.3.jar,
     lib/mlibwrapper_jai.jar
    Bundle-Vendor: Sun
    Export-Package: com.sun.media.jai.codec,
     com.sun.media.jai.codecimpl,
     com.sun.media.jai.codecimpl.fpx,
     com.sun.media.jai.codecimpl.util,
     com.sun.media.jai.iterator,
     com.sun.media.jai.mlib,
     com.sun.media.jai.opimage,
     com.sun.media.jai.remote,
     com.sun.media.jai.rmi,
     com.sun.media.jai.tilecodec,
     com.sun.media.jai.util,
     com.sun.media.jai.widget,
     com.sun.medialib.mlib,
     javax.media.jai,
     javax.media.jai.iterator,
     javax.media.jai.operator,
     javax.media.jai.registry,
     javax.media.jai.remote,
     javax.media.jai.tilecodec,
     javax.media.jai.util,
     javax.media.jai.widget

java advanced Imaging I/O (imageio)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  com.sun.media.imagio Plugin
-  com.sun.media.imagio.linux.gtk.x86 Fragment
-  com.sun.media.imagio.linux.gtk.x86\_64 Fragment
-  com.sun.media.imagio.win32.win32.x86 Fragment

.. code:: code-java

    Manifest-Version: 1.0
    Bundle-ManifestVersion: 2
    Bundle-Name: Java Advanced Imaging Image I/O
    Bundle-SymbolicName: com.sun.media.imageio;singleton:=true
    Bundle-Version: 1.1
    Bundle-Vendor: Sun Microsystems, Inc.
    Bundle-RequiredExecutionEnvironment: JavaSE-1.6
    Export-Package: com.sun.media.imageio.plugins.bmp,
     com.sun.media.imageio.plugins.jpeg2000,
     com.sun.media.imageio.plugins.pnm,
     com.sun.media.imageio.plugins.tiff,
     com.sun.media.imageio.stream,
     com.sun.media.imageioimpl.common,
     com.sun.media.imageioimpl.plugins.bmp,
     com.sun.media.imageioimpl.plugins.clib,
     com.sun.media.imageioimpl.plugins.gif,
     com.sun.media.imageioimpl.plugins.jpeg,
     com.sun.media.imageioimpl.plugins.jpeg2000,
     com.sun.media.imageioimpl.plugins.png,
     com.sun.media.imageioimpl.plugins.pnm,
     com.sun.media.imageioimpl.plugins.raw,
     com.sun.media.imageioimpl.plugins.tiff,
     com.sun.media.imageioimpl.plugins.wbmp,
     com.sun.media.imageioimpl.stream,
     com.sun.media.jai.imageioimpl,
     com.sun.media.jai.operator,
     jj2000.j2k,
     jj2000.j2k.codestream,
     jj2000.j2k.codestream.reader,
     jj2000.j2k.codestream.writer,
     jj2000.j2k.decoder,
     jj2000.j2k.entropy,
     jj2000.j2k.entropy.decoder,
     jj2000.j2k.entropy.encoder,
     jj2000.j2k.fileformat,
     jj2000.j2k.fileformat.reader,
     jj2000.j2k.fileformat.writer,
     jj2000.j2k.image,
     jj2000.j2k.image.forwcomptransf,
     jj2000.j2k.image.input,
     jj2000.j2k.image.invcomptransf,
     jj2000.j2k.io,
     jj2000.j2k.quantization,
     jj2000.j2k.quantization.dequantizer,
     jj2000.j2k.quantization.quantizer,
     jj2000.j2k.roi,
     jj2000.j2k.roi.encoder,
     jj2000.j2k.util,
     jj2000.j2k.wavelet,
     jj2000.j2k.wavelet.analysis,
     jj2000.j2k.wavelet.synthesis
    Bundle-ClassPath: .,
     lib/clibwrapper_jiio.jar,
     lib/jai_imageio.jar

excerpt manifest for the 32bit linux fragment:

.. code:: code-java

    Fragment-Host: javax.media.jai;bundle-version="1.1.3" 
    Eclipse-PlatformFilter: (& (osgi.ws=gtk) (osgi.os=linux) (osgi.arch=x86))

Notes:

-  Clarify the license issue, whether its possible to provide these bundles in the way mentioned
   above.
-  Check, whether to provide gdal in this way as well, so it would be possible do provide features
   that includes a gdal feature (coverages)
-  Check, whether the provided JRE is not required anymore - properly it would reduce the time to
   prepare releases and documentations
-  review net.refractions.udig.jai and net.refractions.udig.jai.macosx bundles (whether it is still
   required, or has never been used in the past)

   Status
   ------

Project Steering committee support:

-  Andrea Antonello: +1
-  Jesse Eichar:
-  Jody Garnett:
-  Mauricio Pazos:

Committer Support:

-  Frank Gasdorf: +1  

A vote of -1 requires an alternate suggestion; community members are invited to indicate
support/suggestions.

Documentation
-------------

Update Developer Documentation how to setup IDE.

-  `04 Java Runtime
   Environment <http://udig.refractions.net/confluence//display/ADMIN/04+Java+Runtime+Environment>`__
   We would need to provide instructions to use Oracle Java 6 or higher
-  `08 Libs Jars <http://udig.refractions.net/confluence//display/ADMIN/08+Libs+Jars>`__

   when merged the following steps are necessary to get running maven smooth:

   | goto libs/jai\_imageio
   |  run 'mvn clean install' (jai bundles and fragments will be installed into local maven
   repository)
   |  goto project root folder and
   |  run 'mvn clean install -Dall' as your already did before (don't forget to set MAVEN\_OPTS)
   |  Import into Eclipse workspace (File -> Import -> Existing Projects into Workspace -> Choose
   libs/jai\_imageio folder -> Done.

   Refresh and afterwards Project -> Clean

Tasks
=====

-  create bundles and native fragments and a feature as well
-  update developer guide
-  update documentation
-  IDE/Environment setup
-  how to create JRE for udig installers (product export)

A list of the tasks needed to accomplish this change; if you prefer you can use a single Jira issue
with subtasks. It is important to include any deadlines so the community knows when you are working
to a schedule.

 

no progress

|image0|

in progress

|image1|

blocked

|image2|

help needed

|image3|

done

Tasks:

#. |image4| Review from PSC member required
#. License check
#. initial bundle setup

   #. host bundle
   #. fragments with native libraries and specific platform filter

#. test interaction with standard JRE on different platforms
#. Updated user guide documentation ( concepts, reference and tasks )

+------------+----------------------------------------------------------+
| |image6|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+------------+----------------------------------------------------------+

.. |image0| image:: images/icons/emoticons/star_yellow.gif
.. |image1| image:: images/icons/emoticons/error.gif
.. |image2| image:: images/icons/emoticons/warning.gif
.. |image3| image:: images/icons/emoticons/check.gif
.. |image4| image:: images/icons/emoticons/warning.gif
.. |image5| image:: images/border/spacer.gif
.. |image6| image:: images/border/spacer.gif
