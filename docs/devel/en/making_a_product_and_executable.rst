Making a Product and Executable
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

A Product for your Application
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

A product is a main entry point into your uDig (or Eclipse) based application. It is used within the
Eclipse SDK to package your application for release, and to launch it from within Eclipse. The file
is XML-based, but Eclipse provides a very nice GUI editor.

uDig's application lives in the net.refractions.udig plug-in:

`http://svn.geotools.org/udig/trunk/plugins/net.refractions.udig/udig.product <http://svn.geotools.org/udig/trunk/plugins/net.refractions.udig/udig.product>`_

Creating the .product File
--------------------------

To create a product for your application, you will need to create a product file and place it in
your branding plug-in.

In your Eclipse SDK:

#. Select **File**->\ **New**->\ **Other...**
#. In the **Plug-in Development** folder, select **Product Configuration** and press **Next**
#. Select your **branding plug-in** for the parent folder. (eg:
   "**net.refractions.udig.community.jody.visual**\ ")
#. Under **File name**, enter a file name (eg: "**visual.product"**)
#. If you are basing your application off of uDig, under **Use an existing product**, select
   **net.refractions.udig.product** (Recommended)
#. Press **Finish**

This screenshot shows a newly created visual.product file based on udig.product:
 `|image0| <http://udig.refractions.net/confluence//download/attachments/9358/newProduct.jpg>`_

Configuring your Product
^^^^^^^^^^^^^^^^^^^^^^^^

**Product Name**

Note that **product name** is set to "%product.name". You should either create a plugin.properties
file and provide a value, or change it to an appropriate product name (eg: "Visual Application").
For an example properties file, see
`http://svn.geotools.org/udig/trunk/plugins/net.refractions.udig/plugin.properties <http://svn.geotools.org/udig/trunk/plugins/net.refractions.udig/plugin.properties>`_

**Feature-Based Product**

You can base your product on plug-ins or features. Features are generally preferable as they enable
the use of an update-site. This is what uDig uses. If you change it to features, you will have to
add the two features uDig depends on, on the **Configuration** page. These two features are
**net.refractions.udig** and **org.eclipse.rcp**. You should also add in your own feature for
plug-ins you want to include (ex: **net.refractions.udig.community.jody.visual\_feature**)

The following screenshot shows the **Configuration** page of a product based on features:

`|image1| <http://udig.refractions.net/confluence//download/attachments/9358/featureBasedProduct.jpg>`_

**Branding**

The **Launcher** and **Branding** pages can be used to configure the branding of your application.
This includes items such as the launching executable's name and icon, and the splash screen that is
shown during start-up.

You probably also want to give your product a **launcher** name. On the **Launcher** page, enter a
value under Launcher name. (eg: "visual")

Using the Product to Run your Application
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

A product file can be used to run your application within Eclipse. On the **Overview** page, under
**Testing**, there are a couple buttons. **Synchronize** creates (or updates) the Eclipse run-time
configuration (What you see under Run->Run History), and **Launch the Product** or **Launch the
Product in Debug mode**.

To use the product file to run your application:

#. Press **Synchronize** (this isn't always necessary, but can help fix problems sometimes)
#. Press on of the **Launch** buttons.

Using the Product to build a Release
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The product file can be used to build an archive containing an executable release of your
application. On the **Overview** page, click **Eclipse product export wizard**. The wizard will
appear and you will be prompted to enter configuration details, such as root directory and
destination location.

Change **Root directory** to something suitable for your application (eg: "visual"). Select
**Archive file** and point it to a location where you want the zip file saved (eg: c:\\visual.zip).
Press Finish and then wait for it to build.

The following screenshot shows the Eclipse export wizard:
 `|image2| <http://udig.refractions.net/confluence//download/attachments/9358/export.jpg>`_

Note that if you wish to export for multiple platforms, you will need to install the Eclispe RCP
Delta Pack.

.. |image0| image:: download/thumbnails/9358/newProduct.jpg
.. |image1| image:: download/thumbnails/9358/featureBasedProduct.jpg
.. |image2| image:: download/thumbnails/9358/export.jpg
