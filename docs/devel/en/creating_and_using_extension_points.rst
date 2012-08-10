Creating and Using Extension Points
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

`Eclipse notes on
plugins <http://www.eclipse.org/articles/Article-Plug-in-architecture/plugin_architecture.html>`_
has a great deal of useful information on plugins including; how to create and use plugin
extensions.

Creating Extension Points
^^^^^^^^^^^^^^^^^^^^^^^^^

In the Plug-in Manifest Editor's Extension Point tab, new extension points can be declared. In
addition, the plugin.xml file can be edited by hand to declare new extension points.

**Plug-in Manifest Editor**

-  Open Plug-in Manifest Editor (double click on plugin.xml)
-  Click Extension Point tab
-  Click Add button
-  Enter ID - this is appended to your plugin id to form a fully qualified identification for the
   extension point
-  Enter Name (human reading purposes)
-  Click Finish

The extension-point schema defines how to parameterize extensions.

**Extension-point Schema**

The extension-point schema defines how to parameterize extensions. An extension-point schema can be
edited by hand or using Eclipse's extension-point schema editor.

See `Eclipse help
guide <http://help.eclipse.org/help21/index.jsp?topic=/org.eclipse.pde.doc.user/guide/pde_schema_editor.htm>`_
for detailed instructions on editing extension-point schemas.

To create a extension-point schema that requires a extension to implement an interface:

-  Create an extension-point as described above.
-  If extension-point schema editor does not open. Open it. (double click on the new .exsd file)
-  Click new Element button
-  In the properties view click on the Name property and enter a name for the element. It should be
   logical. Parser is a good example.
-  Ensure Parser is selected in editor and click the New Attribute button
-  In properties view change Name to class.
-  In properties view change Kind to java
-  In properties view change Based On to "name of Interface". Remember to include package name.
-  (Not required) In properties view change Name to id.
-  (Not required) Create a Name attribute
-  Select extension in Editor
-  Right click on Sequence under **Element Grammar** Heading
-  Select New->Reference->"Name of Created Element"

Done.

Finding Extensions
^^^^^^^^^^^^^^^^^^

The following classes demonstrates how to find registered extensions:

::

    import org.eclipse.core.runtime.IConfigurationElement;
    import org.eclipse.core.runtime.IExtension;

    public interface IProcessMember {
        public Object process(IExtension extension, 
          IConfigurationElement member);
    }

This class is a visitor.

::

    import org.eclipse.core.runtime.IConfigurationElement;
    import org.eclipse.core.runtime.IExtension;
    import org.eclipse.core.runtime.IExtensionPoint;
    import org.eclipse.core.runtime.IExtensionRegistry;
    import org.eclipse.core.runtime.Platform;

    public class ProcessExtensions {
     public static void process(String xpid, IProcessMember processor) {
            IExtensionRegistry registry = Platform.getExtensionRegistry();
            IExtensionPoint extensionPoint =
                registry.getExtensionPoint(xpid);
            IExtension[] extensions = extensionPoint.getExtensions();
            // For each extension ...
         for (int i = 0; i < extensions.length; i++) {           
                IExtension extension = extensions[i];
             IConfigurationElement[] elements = 
                  extension.getConfigurationElements();
                // For each member of the extension ...
             for (int j = 0; j < elements.length; j++) {
                    IConfigurationElement element = elements[j];
                 processor.process(extension, element);               
                }
            }
        }
    }

This class is used to process extensions as identified by an id. An id and a visitor are passed to
the process method. The visitor knows the class that uses the extension.
