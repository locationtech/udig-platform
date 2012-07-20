Style
=====

.. _GeoTools User Guide: http://docs.geotools.org/latest/userguide/

uDig can provide maps symbolized by implementing styles. A style describes how to paint points,
lines, polygons and raster data sets by providing a set of painting rules.

Related Reference:

* `GeoTools User Guide: Renderer <http://docs.geotools.org/latest/userguide/library/render/index.html>`_
* `Styled Layer Descriptor Reference Document <http://www.opengis.org/docs/02-070.pdf>`_

uDig Style Framework
--------------------

The style api has three major extension points (defined by the **net.refractions.udig.project**
plug-in):

-  **StyleBlackboard**: a persisted blackboard of all the user supplied rendering settings for a
   layer
-  **StyleContent**: this is used to hold any object you wish on the style blackboard. Methods exist
   to load / save your object, and create a default object.

   -  For the **org.geotools.styling.Style** object (representing an SLD file) the value is managed
      by the class **SLDContent**.

-  **StyleConfigurator**: This the the user interface responsible for editing a style on the style
   blackboard.

The uDig application will use the entire style blackboard, as well as all known Renderers when
choosing how to draw what is on the screen. RenderMetricsFactory and RenderingMetrics are both used
to inspect the style blackboard. The renderer that is able to understand the most styles on the
blackboard is chosen to draw what is on the screen.

StyleContent, Objects and the StyleBlackboard
---------------------------------------------

Here is how the StyleBlackboard is used for collaboration:

-  StyleContent describes how Objects are stored on the StyleBlackboard, it is also able to create a
   default style for a given GeoResource.
-  Objects are stored on the blackboard by an ID.
-  Renderers can look up a Style by ID and make use of it when drawing.
-  StyleConfigurators allow the user interface to modify the objects on the style blackboard.

Since different renders can use completely different style objects, the coupling between the render
and the style needs to kept low. In order to achieve this, we decided to create the notion of a
style blackboard which is associated with a layer. This way the render and configurator can
collaborate without talking to each other.

The Renderer and the StyleConfigurator look for particular style objects on the blackboard in order
to do their part. The StyleContent is responsible for creating, and persiting these style object.

Each Layer has a StyleBlackboard. StyleConfigurators should not write to this blackboard directly.
Each style configurator is supplied with a copy of the actual layer blackboard. Each time a style
object is changed, it must be replaced onto the blackboard for persistence to occure.

StyleContent
------------

The style content extension-point is used to teach the system about a new kind of style information
you would like to store on the blackboard.

The StyleContent interface is responsible for:

-  The ID used to hold the value on the blackboard
-  Persistence
-  Creation of a good default value based on a GeoResource

By having an extension-point here we are able to store objects on the style blackboard that can not
normally be serialized; or choose the format in which the object is serialized.

For an example of the production of a StyleContent please review the
net.refractions.udig.tutorials.style.color tutorial.

StyleConfigurator
-----------------

The StyleConfigurator is a user interface; that should store no state of its own (since it could be
close at any time). All state should be stored in the style objects on the style blackboard. When a
user interface widget changes state, the style object should be written to immediately to reflect
the change.

When the StyleConfigurator becomes active, the user interface widgets should be initialized from the
values of style objects on the blackboard. This should be performed every time refresh() is called.

Whenever style objects are read from the blackboard, the example below is pretending to talk to a
"point.style" key that is used to store a Point object.

.. code-block:: java

    void apply() {
          StyleBlackboard styleBlackboard = getStyleBlackboard();
          Point point = styleBlackboard.lookup("point.style");      
          if (style == null) {
              style = new Point();    
              styleBlackboard.put("point.style", point);
          }
          
          point.setX(...) //set to some value from ui
          point.setY(...) //set to some value from ui
      }  
      void init() {
          StyleBlackboard styleBlackboard = getStyleBlackboard();
          Point point= styleBlackboard.lookup("point.style");
          if (point!= null) {
              //set some ui widget to value of style.getX();
              //set some ui widget to value of style.getY();
          }
      }

Built-in Styles
---------------

The uDig application provides several built-in StyleContent implementations. These are described in
this section; but in a few cases you will need to go off and read formal documentation provided by a
standards body.

ProjectBlackboardConstants.LAYER\_DATA\_QUERY
---------------------------------------------

This is an entry on the style blackboard that can be used to hold an OGC Filter or WFS Query. This
is used to turn your layer into a simple "view" of the complete content provided by the GeoResource.

To quickly create a filter you can use the CQL utility class:

.. code-block:: java

    Filter filter = CQL.toFilter("attName >= 5");

    layer.getStyleBlackboard().put(ProjectBlackboardConstants.LAYER__DATA_QUERY, filter);

Or a FilterFactory:

.. code-block:: java

    FilterFactory ff = CommonFactoryFinder.getFilterFactory( null );
    Filter filter = ff.propertyLessThan( ff.property( "AGE"), ff.literal( 12 ) );

    layer.getStyleBlackboard().put(ProjectBlackboardConstants.LAYER__DATA_QUERY, filter);

References:

* `GeoTools User Guide`_
* `http://www.opengeospatial.org/standards/filter <http://www.opengeospatial.org/standards/filter>`_

SLDContent
----------

The SLDContent entry is used to store a "Style Layer Descriptor" document on the style blackboard.
The OGC Style Layer Descriptor specification defines a style that can be used to portray Features
and GridCoverages.

The **net.refractions.udig.style.sld** plug-in captures everything we know about these SLD files:

-  org.geotools.styling.Style - is the data structure we use to represent an SLD file
-  SLDContent - is the class used to hold this data structure on the style blackboard
-  StyleEditor - this is a special dialog that can be used to edit an SLD file
-  StyleEditorPage - this is a page in the style dialog; you can define additional pages

The class **SLDContent** is used to store an **org.geotools.styling.Style** object (representing a
SLD file). If you would like to interact with this object you can request it from the style
blackboard using the key SLDContent.ID.

.. code-block:: java

    Style sld = (Style) = layer.getStyleBlackboard().get(SLDContent.ID);

References:

* `GeoTools User Guide`_
* `http://www.opengeospatial.org/standards/sld <http://www.opengeospatial.org/standards/sld>`_

Default Style
-------------

SLDContent will ask your GeoResource for a default style; you have several ways of supplying a
default:

-  For a Shapefile you can include an \*.sld file; ShpGeoResourceImpl already knows how to check for
   this file and will provide it to SLDContent – using resolve( Style, null )
-  If you are making your own GeoResource you can make sure canResolve( Style.class) returns true
-  If you are adding a default style to an existing GeoResource (like ArcSDE) you will need to use
   an IResolveAdapaterFactory to teach the existing class about your default style

To ask SLDContent for a default style yourself:

.. code-block:: java

    SLDContent sldContent = new SLDContent(SLDContent.ID);
    Style sld = (Style) SLDContent.createDefaultStyle( resource, Color.BLACK, new NullProgressMonitor() );

The **CreateLayerCommand** normally takes care of this step for you when you are creating

GeoTools Style Class
--------------------

The GeoTools Style object represents the contents of an SLD file. You can create a Style using a
StyleBuilder

.. code-block:: java

    StyleFactory styleFactory = StyleFactory.createStyleFactory();
    Style style = styleFactory.getDefaultStyle();

    FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
    fts.setFeatureTypeName("feature-type-1");
    style.addFeatureTypeStyle(fts);


Overview of Style classes mentioned above:

-  FeatureTypeStyle: A FeatureTypeStyle declares a part of a style that is specifically geared
   toward a FeatureType. Features will be rendered according a FeatureTypeStyle only if their
   FeatureType name matches what is recorded in the FeatureTypeStyle or a descendant.

   -  Rule: A Rule contains filters that will decide whether features will be displayed or not,
      specifically:

      -  a minimum and maximum map scale, if set and the current scale is outside the specified
         range, the rule won't apply and thus its symbolizers won't be used;
      -  a Filter that is applied to the features, only the features matching the filter will be
         painted according to the Rule symbolizers;
         as an alternative, the rule can have an "else filter". This special kind of filter catches
         all of the features that still haven't been symbolized by previous rules with a regular
         filter.

         -  SymbolizersL A Symbolizer describes how to represent a feature on the screen based on
            the feature contents (geometry and attributes). Each rule can have one or more
            Symbolizer attached to it.

            -  Text Symbolizer
            -  Line Symbolizer
            -  Polygon Symbolizer
            -  Point Symbolizer
            -  Raster Symbolizer

SLDs utility class
------------------

Since a Style is composed of a complex set of objects, a StyleBuilder object is provided for you to
conveniently build simple styles without the need to build all of the style elements by hand. For
example, you can create a PolygonSymbolizer and then create a Style out of it with a single method
call: the builder will generate a default FeatureTypeStyle and the Rule for you.

Using SLDs utility class to query an SLD:

.. code-block:: java

    Style sld = (Style) = layer.getStyleBlackboard().get(SLDContent.ID);
    FeatureTypeStyle style = SLDs.getFeatureTypeStyle( sld );
    double minScale = SLDs.minScale( style );

