Drag and Drop
~~~~~~~~~~~~~

There are two broad categories of drag and drop in udig.

#. Drag and drop between components with in udig itself
#. Drag and drop between udig and the outside elements (Internet Explorer for example).

There are a set of extension points that allows a programmer to configure the behaviour of drag and
drop for Drag and Drop category 1 which is also used, but to a lesser degree for category 2 drag and
drop events.

Drag and Drop within uDig
^^^^^^^^^^^^^^^^^^^^^^^^^

There are two drag and drop extension points that control the behaviour of drag and drop within
udig. The most important of the two is the
`net.refractions.udig.ui.dropAction <http://svn.geotools.org/udig/trunk/plugins/net.refractions.udig.ui/schema/dropAction.exsd>`_
extention point. This extension point defines a destination object (the object that objects can be
dropped on), an action to take when objects are dropped on the object and the objects that can be
dropped on the object for that action.

The second extension point is the
`net.refractions.udig.ui.dropTransfers <http://svn.geotools.org/udig/trunk/plugins/net.refractions.udig.ui/schema/dropTransfer.exsd>`_
extension point. This extension point advertises
`Transfer <http://help.eclipse.org/help31/topic/org.eclipse.platform.doc.isv/reference/api/org/eclipse/swt/dnd/Transfer.html>`_
objects that are available for use. The Transfers defined by the normal udig platform are:

* UDigByteAndLocalTransfer - Transfers objects within udig. This transfer is not useful outside of udig.
* `TextTransfer <http://help.eclipse.org/help31/topic/org.eclipse.platform.doc.isv/reference/api/org/eclipse/swt/dnd/TextTransfer.html>`_ - Transfers text. (Part of eclipse DND)
* `FileTransfer <http://help.eclipse.org/help31/topic/org.eclipse.platform.doc.isv/reference/api/org/eclipse/swt/dnd/FileTransfer.html>`_ - Transfers files. (Part of eclipse DND)
* `RTFTransfer <http://help.eclipse.org/help31/topic/org.eclipse.platform.doc.isv/reference/api/org/eclipse/swt/dnd/RTFTransfer.html>`_ - Transfers rich text formatted text. (Part of eclipse DND)
* `HTMLTransfer <http://help.eclipse.org/help31/topic/org.eclipse.platform.doc.isv/reference/api/org/eclipse/swt/dnd/HTMLTransfer.html>`_
  \- Transfers HTML formatted text. (Part of eclipse DND)
* FeatureTextTransfer - Converts features to and from GML2 text. (uDig 1.1 only)
* GeometryTextTransfer - Converts geometries from and to text, either GML or vivid solution well
  known text format (uDig 1.1 only)

Drag and Drop to and from uDig
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

To drag things into uDig a dropAction extension must be defined that expects data to be in the form
provided by one of the standard transfer types.
`net.refractions.udig.project.ui/project.xml <http://svn.geotools.org/udig/trunk/plugins/net.refractions.udig.project.ui/plugin.xml>`_
has contains examples of both. Drop actions that accept String objects will activate if a url or
some text are dropped from outside of udig.

Only uDig 1.1 and later support dragging from udig to other applications. When dragging and dropping
between uDig and the outside world, derivatives of the eclipse DND transfers must be used. In uDig
1.1 and later the XXXTextTransfers can be used to transform objects with in udig to the outside
world. They do this by advertising themselves as text transfers to the outside world.

Example
~~~~~~~

Example of a drag and drop action that takes a url to an SLD document and styles a layer when the
url is dropped on a layer.

plugin.xml Extension Definition

.. code-block:: xml

    <action class="net.refractions.udig.project.ui.internal.actions.SLDDropAction" name="SLD Drag and Drop">
            <destination class="net.refractions.udig.project.ILayer"/>
            <acceptedType class="java.io.File"/>
            <acceptedType class="java.net.URL"/>
            <acceptedType class="java.lang.String"/>
         </action>

DropAction Code

.. code-block:: java

    import java.io.File;
    import java.io.IOException;
    import java.net.MalformedURLException;
    import java.net.URL;

    import net.refractions.udig.project.internal.Layer;
    import net.refractions.udig.project.ui.internal.Policy;
    import net.refractions.udig.project.ui.internal.ProjectUIPlugin;
    import net.refractions.udig.style.sld.SLDContent;
    import net.refractions.udig.ui.IDropAction;

    import org.eclipse.core.runtime.IProgressMonitor;
    import org.geotools.styling.Style;

    public class SLDDropAction extends IDropAction {

        /** the sld url * */
        URL url;

        @Override
        public boolean accept( Object source, Object destination ) {
            // make sure we can turn the object into an sld
            try {
                if (source instanceof URL) {
                    url = (URL) source;
                } else if (source instanceof File) {
                    url = ((File) source).toURL();
                } else if (source instanceof String) {
                    try {
                        url = new URL((String) source);
                    } catch (MalformedURLException e) {
                        // try attaching a file protocol
                        url = new URL("file:///" + (String) source); //$NON-NLS-1$
                    }

                }
            } catch (MalformedURLException e) {
                String msg = Policy.bind("SLDDropAction.badSldUrl"); //$NON-NLS-1$
                ProjectUIPlugin.log(msg, e);
            }

            return url != null;
        }

        @Override
        public void perform( Object source, Object destination, IProgressMonitor monitor ) {

            // grab the actual target
            Object target = getDropHandler().getTarget();
            if (target != null && target instanceof Layer) {
                Layer layer = (Layer) target;
                // parse the sld object

                try {
                    Style style = SLDContent.parse(url);
                    if (style != null) {
                        SLDContent.apply(layer, style, monitor);
                    }
                    layer.refresh(null);

                } catch (IOException e) {
                    String msg = Policy.bind("SLDDropAction.sldParseError"); //$NON-NLS-1$
                    ProjectUIPlugin.log(msg, e);
                }
            }

        }

    }

