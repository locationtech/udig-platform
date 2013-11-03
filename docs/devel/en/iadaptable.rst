IAdaptable
~~~~~~~~~~

Learning how IAdaptable works is one of the big ideas you want to be comfortable with when starting
Eclipse RCP development.

Reference:

* `http://www.eclipsezone.com/articles/what-is-iadaptable/ <http://www.eclipsezone.com/articles/what-is-iadaptable/>`_
* `IAdaptable <http://help.eclipse.org/ganymede/topic/org.eclipse.platform.doc.isv/reference/api/org/eclipse/core/runtime/IAdaptable.html>`_
* `org.eclipse.core.runtime.adapters <http://help.eclipse.org/ganymede/topic/org.eclipse.platform.doc.isv/reference/extension-points/org_eclipse_core_runtime_adapters.html>`_

Example Use
^^^^^^^^^^^

You can use the **getAdapter** method anywhere you would normally do an instanceof check.

BEFORE:

::

    if( object instanceof IMap ){
       // work on a IMap
    }

AFTER:

::

    IMap map = (map)adaptable.getAdapter(IMap.class);
    if( map != null ){
        // work on the map
    }

Implementation
^^^^^^^^^^^^^^

At a minimum every implementation of IAdaptable should implement the following:

::

    class Foo implements IAdaptable {
      public String text;
      public Object getAdapter(Class adapter) {
         return Platform.getAdapterManager().getAdapter(this, adapter);
      }
    }

Alternative: If you want you could also extend the "PlatformObject" for the same effect:

::

    class Foo extends PlatformObject {
      public String text;
    }

Example of Direct Implementation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

You have a choice of two ways to make new "adapters" available:

-  Directly code in your class:

   ::

       class Foo implements IAdaptable {
         public String text;
         public Object getAdapter(Class adapter) {
            if( adapter.isAssignableFrom( String.class ) ){
               return text;
            }
            return Platform.getAdapterManager().getAdapter(this, adapter);
         }
       }

Example Using AdapterFactory
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

You can also implement an AdapterFactory to do the work:

::

    public class FooStringFactory implements IAdapterFactory {
      private static final Class[] TARGETS = {
        String.class,
      };
      public Class[] getAdapterList() {
        return TARGETS;
      }
      public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType.isAssignableFrom( String.class )) {
          return ((Foo)adaptableObject).text;
        }return null;    
      }
    }

And contribute it to the Platform.getAdapterManager using the following plugin.xml fragment:

::

    <extension point="org.eclipse.core.runtime.adapters">
      <factory class="FooStringFactory" adaptableType="Foo">
        <adapter type="java.lang.String"/>
       </factory>
    </extension>

Alternative: Registering AdapterFactory by Hand

::

    Platform.getAdapterManager().registerAdapters(new FooStringFactory(), Foo.class);

