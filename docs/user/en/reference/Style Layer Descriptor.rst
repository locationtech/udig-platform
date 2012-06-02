


Style Layer Descriptor
~~~~~~~~~~~~~~~~~~~~~~

The **Style Layer Descriptor** file format is an XML document defined
by the Open Geospatial Consortium. Style Layer Descriptor documents
defines how Features are rendered on to the screen.

You can use your own Style Layer Descriptor documents in several ways:


+ Select the **XML** page of the `Style Editor dialog`_ and directly
  modify what is being used directly
+ Any sld file associated with a shapefile will be picked up and used
+ You can import and export SLD files from the `Style Editor dialog`_
+ you can drop an SLD file directly onto a Layer


`Style Editor dialog`_
> <a href="http: /www.opengeospatial.org/standards/sld">Styled Layer
Descriptor (OGC Specification)
> <a href="http: /www.opengeospatial.org/standards/symbol">Symbology
Encoding (OGC Specification)
> <a href="http:
/lyceum.massgis.state.ma.us/wiki/doku.php?id=wms:sld:home">SLD
Examples (from MassGIS)
> <a href="http:
/geoserver.org/display/GEOSDOC/SLD+Intro+Tutorial">SLD Intro Tutorial
(from GeoServer)
> <a href="http:
/geoserver.org/display/GEOSDOC/OGC+SLD+Explanations+and+Samples">SLD
Samples (from GeoServer)



Example
=======

You can use a function to choose a color, the following example can be
used with the countries.shp file.


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


.. _Style Editor dialog: Style Editor dialog.html


