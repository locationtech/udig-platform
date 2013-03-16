Style Layer Descriptor
######################

The **Style Layer Descriptor** file format is an XML document defined by the Open Geospatial
Consortium. Style Layer Descriptor documents defines how Features are rendered on to the screen.

You can use your own Style Layer Descriptor documents in several ways:

-  Select the **XML** page of the :doc:`/reference/Style Editor dialog` and directly
   modify what is being used directly
-  Any sld file associated with a shapefile will be picked up and used
-  You can import and export SLD files from the :doc:`/reference/Style Editor dialog`
-  you can drop an SLD file directly onto a Layer

**Related reference**

:doc:`/reference/Style Editor dialog`

`Styled Layer Descriptor <http://www.opengeospatial.org/standards/sld>`_ (OGC Specification)

`Symbology Encoding <http://www.opengeospatial.org/standards/symbol>`_ (OGC Specification)

`SLD Intro Tutorial (from GeoServer) <http://docs.geoserver.org/stable/en/user/styling/sld-introduction.html>`_

`SLD Cookbook with Samples (from GeoServer) <http://docs.geoserver.org/stable/en/user/styling/sld-cookbook/index.html#sld-cookbook>`_


Example
=======

You can use a function to choose a color, the following example can be used with the countries.shp
file.

::

    <?xml version="1.0" encoding="UTF-8"?>
    <sld:StyledLayerDescriptor
       xmlns="http://www.opengis.net/sld"
       xmlns:sld="http://www.opengis.net/sld"
       xmlns:ogc="http://www.opengis.net/ogc"
       xmlns:gml="http://www.opengis.net/gml"
       version="1.0.0">
        <sld:UserLayer>
            <sld:LayerFeatureConstraints>
                <sld:FeatureTypeConstraint/>
            </sld:LayerFeatureConstraints>
            <sld:UserStyle>
                <sld:Name>Default Styler</sld:Name>
                <sld:Title/>
                <sld:IsDefault>1</sld:IsDefault>
                <sld:FeatureTypeStyle>
                    <sld:Name>simple</sld:Name>
                    <sld:FeatureTypeName>Feature</sld:FeatureTypeName>
                    <sld:SemanticTypeIdentifier>generic:geometry</sld:SemanticTypeIdentifier>
                    <sld:SemanticTypeIdentifier>simple</sld:SemanticTypeIdentifier>
                    <sld:Rule>
                        <sld:PolygonSymbolizer>
                            <sld:Fill>
                                <sld:CssParameter name="fill">
                                    <ogc:Function name="if_then_else">
                                        <ogc:Function name="equalTo">
                                            <ogc:PropertyName>LANDLOCKED</ogc:PropertyName>
                                            <ogc:Literal>Y</ogc:Literal>
                                        </ogc:Function>
                                        <ogc:Literal>#FFFF00</ogc:Literal>
                                        <ogc:Literal>#0000FF</ogc:Literal>
                                    </ogc:Function>
                                </sld:CssParameter>
                                <sld:CssParameter name="fill-opacity">0.5</sld:CssParameter>
                            </sld:Fill>
                        </sld:PolygonSymbolizer>
                    </sld:Rule>
                </sld:FeatureTypeStyle>
            </sld:UserStyle>
        </sld:UserLayer>
    </sld:StyledLayerDescriptor>
