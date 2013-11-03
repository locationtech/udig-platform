MapInterceptor and LayerInterceptor
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

A Map interceptor is declared using **org.locationtech.udig.project.mapInterceptor** and can be used
to configure a Map or Layer prior to use.

Map Interceptor
^^^^^^^^^^^^^^^

You can register a map interceptor:

::

    public interface MapInterceptor {
        public void run(Map map);
    }

Layer Interceptor
^^^^^^^^^^^^^^^^^

::

    public interface LayerInterceptor {
        public void run(Layer layer);
    }

