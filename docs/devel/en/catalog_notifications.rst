Catalog Notifications
=====================

What if you want to keep track of all of the changes to the catalog that happen while your plug-in
is running? You can register an IResolveChangeListener with the catalog plugin. Your listener will
be notified of the changes via an IResolveChangeEvent object, which describes the changes.

If you are an proficient eclipse developer you can consider IResolve as similar to the IResource api
provided by eclipse. There is a difference in blocking behavior, as resolve explicitly represents a
handle to a remote resource or service.

Have a look at the article below; you may find it better written than what we have here (both
systems use the same design).

Registering a listener
----------------------

First, you must register a resource change listener with the catalog plugin.

.. code-block:: java

    IResolveChangeListener listener = new MyResolveChangeReporter();
       CataqlogPlugin.addResourceChangeListener( listener );


Your listener will be notified after modifications to the resources have been made. Catalog API
methods that modify resources trigger these events as part of their documented behavior. The method
comment for a Catalog API method explicitly states whether or not it triggers a resource change
event.

For example, the following is included in the IService.getInfo() comment:

When new information is available changes will be reported in a change event, including an
indication that this service's contents have been changed.

Most catalog resources trigger these events the first time the handle is used. Methods that create,
delete, or change a resource typically trigger these events on every use.

Resolve change events
---------------------

The resolve change event describes the specifics of the change that have occurred in the catalog.
The event contains a "delta" that describes the net effect of the changes.

The resolve delta is structured as a tree rooted at the catalog. The resolve delta tree describes
these types of changes:

*  Resources that have been created, deleted, or changed. If you have deleted (or added) a Web Map
   Server, the resource delta will include the Web Map Server and all Layers provided by that
   service.

*  Resources that have been replaced replaced.

To traverse a resolve delta tree, you may implement the IResolveDeltaVisitor interface or traverse
the tree explicitly using IResource.getChildren. Resolve delta visitors implement a visit method
that is called by the resource delta as it enumerates each change in the tree.

The types of resource change events and when they are reported.

*  PRE\_CLOSE

   Notifies listeners that resolve is about to be closed. This event can be used to save necessary
   information from the in-memory representation (e.g., session properties) before it is closed.

*  PRE\_DELETE

   Notifies listeners that a project is about to be deleted. This event can be used to perform
   clean-up operations, such as removing any saved state that is related to the resolve.

*  POST\_CHANGE

   Describes a set of changes that have occured to the workspace since the last POST\_CHANGE event
   was reported. Triggered after the catalog API is used. The event contains a resource delta
   describing the net changes since the last POST\_CHANGE event.

Implementing a resolve change listener
--------------------------------------

The following example implements a console-based resolve change listener. A resolve change listener
is registered for events and information about these events is printed to the console:

.. code-block:: java

    IResolveChangeListener listener = new MyResolveChangeReporter();
       CatalogPlugin.addListener(listener);

The listener checks for each event type and reports information about the resolve that was changed
and the kinds of changes that occurred.

.. code-block:: java

    import org.eclipse.resources.*;
    import org.eclipse.runtime.*;

    public class MyResolveChangeReporter implements IResolveChangeListener {
       public void changed(IResolveChangeEvent event) {
          IResolve res = event.getResolve();
          switch (event.getType()) {
             case IResolveChangeEvent.PRE_CLOSE:
                System.out.print(res.getIdentifier());
                System.out.println(" is about to close.");
                break;
             case IResolveChangeEvent.PRE_DELETE:
                System.out.print(res.getFullPath());
                System.out.println(" is about to be deleted.");
                break;
             case IResolveChangeEvent.POST_CHANGE:
                System.out.println("Resources have changed.");
                event.getDelta().accept(new DeltaPrinter());
                break;
          }
       }
    }

The DeltaPrinter class implements the IResolveDeltaVisitor interface to interrogate the resolve
delta. The visit() method is called for each resolve change in the resolve delta. The visitor uses a
return value to indicate whether deltas for children should be visited.

.. code-block:: java

    class DeltaPrinter implements IResolveDeltaVisitor {
          public boolean visit(IResourceDelta delta) {
             IResource res = delta.getResource();
             IResource old = delta.getOldResource();
             switch (delta.getKind()) {
                case IResolveDelta.Kind.ADDED:
                   System.out.print("Resource ");
                   System.out.print(res.getIdentifier());
                   System.out.println(" was added.");
                   break;
                case IResolveDelta.Kind.REMOVED"
                   System.out.print("Resource ");
                   System.out.print(res.getIdentifier());
                   System.out.println(" was removed.");
                   break;
                case IResolveDelta.Kind.CHANGED:
                   System.out.print("Resource ");
                   System.out.print(res.getIdentifier());
                   System.out.println(" has changed.");
                   break;
                case IResolveDelta.Kind.REPLACED:
                   System.out.print("Resource ");
                   System.out.print(res.getIdentifier());
                   System.out.print(" is replaced by ");
                   System.out.print(res.getNewIdentifier().getIdentifier());
                   break;
             }
             return true; // visit the children
          }
      }

For a complete description of resolve deltas, visitors, consult the API specification for
IResolveDelta and IResolveDeltaVisitor.

Note: Resolve change listeners are useful for tracking changes that occur to resources while your
plug-in is activated. If your plug-in registers a listener during its startup code, it's possible
that many resolve change events have been triggered before the activation of your plug-in.

.. note::

   Note most change events are triggered during processing that occurs in a background 
   thread. Resolve change listeners should be thread-safe.

Related reference
-----------------

* `How You've Changed! <http://www.eclipse.org/articles/Article-Resource-deltas/resource-deltas.html>`_

