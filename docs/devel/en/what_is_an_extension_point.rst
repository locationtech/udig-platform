What is an Extension Point
~~~~~~~~~~~~~~~~~~~~~~~~~~

An extension point is a formal declaration in a plugin.xml file where customization is allowed.
Customization takes the place of other plug-ins (inclusive) providing a little chunk of XML
describing the specific customization that is requested.

In practice, most extension points allow the XML to indicate a **class** of some sort (where all the
magic occurs). If we were just doing straight-up object oriented programming, we would handle this
kind of thing by allowing others to provide a **strategy object** that conformed to a specific
interface.

It works the same way with most extension points wherein, when they allow a class contribution, they
indicate what interface is required.

However, it is important to remember this is not straight-up object oriented programming, instead,
that chunk of XML (the extension) also needs to correspond to a respective interface. In XML-speak,
this is what is known as an XML Schema.

Here is a link to the extension points used by uDig right now: `uDig extension
points <uDig%20extension%20points%20list.html>`_. In addition to these extension points, you can use
any of the extension points defined by the Eclipse RCP application (allowing you to do everything
from additional views... to online help).

--------------

When the class/interface is extended to create a plug-in, the plugin.xml file must be updated with
the appropriate information.

For example:

::

    <?xml version="1.0" encoding="UTF-8"?>
    <?eclipse version="3.0"?>
    <plugin
        id="com.yourname.helloworld"
        name="HelloWorld Plugin"
        version="1.0.0"
        provider="EXAMPLE"
        class="com.yourname.helloworld.HelloWorldPlugin">

       <runtime>
        <library name="helloworld.jar">
         <export name="*"/>
        </library>
       </runtime>

       <requires>
        <import plugin="org.eclipse.ui"/>
        <import plugin="org.eclipse.core.runtime"/>
        <import plugin="org.eclipse.core.runtime.compatibility"/>
       </requires>

       <extension
             point="org.eclipse.ui.views">
          <category
                name="Hello Category"
                id="com.yourname.helloworld">
          </category>
          <view
                name="Hello View"
                icon="icons/sample.gif"
                category="com.yourname.helloworld"
                class="com.yourname.helloworld.HelloWorldView"
                id="com.yourname.helloworld.HelloWorldView">
          </view>
       </extension>

    </plugin>

Notice the <extension> tag. The extension point for this example hello world plug-in is the
org.eclipse.ui.views class.

+-------------------------------------------------------+
| The above example code can be found in the section:   |
|  "Plug it in: Hello World meets the Workbench"        |
+-------------------------------------------------------+

+----------------------------------------------+
| of the "Platform Plug-in Developer Guide":   |
|  org.eclipse.platform.doc.isv\_3.0.1.pdf     |
+----------------------------------------------+

+-------------------------------------------------------------------------------------------------------+
| Located on Eclipse's documentation page:                                                              |
|  `http://www.eclipse.org/documentation/main.html <http://www.eclipse.org/documentation/main.html>`_   |
+-------------------------------------------------------------------------------------------------------+

The Plugin.xml file contains the XML code that allows the Eclipse run-time engine to execute new
plug-ins. By specifying an "Extension Point" in the XML file, Eclipse can run the class, and because
the class implements an already existing interface, Eclipse knows what functions it must run.
