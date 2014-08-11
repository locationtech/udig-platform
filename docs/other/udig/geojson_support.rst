Geojson Support
###############

+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+
| uDig :    |
| GeoJSON   |
| Support   |
| This page |
| last      |
| changed   |
| on Jun    |
| 23, 2010  |
| by        |
| jgarnett. |
| GeoJSON   |
| is a      |
| standard  |
| format    |
| for       |
| feature   |
| data that |
| was       |
| published |
| in the    |
| middle of |
| last      |
| year, and |
| the       |
| specifica |
| tion      |
| is        |
| available |
| at        |
| http://ww |
| w.geojson |
| .org.     |
| It        |
| supports  |
| all the   |
| common    |
| feature   |
| types,    |
| like      |
| Point,    |
| Line,     |
| Polygon   |
| and       |
| Multi-    |
| versions  |
| of all of |
| these. It |
| is based  |
| on the    |
| JSON      |
| standard, |
| which is  |
| a subset  |
| of the    |
| Javascrip |
| t         |
| language, |
| which     |
| makes it  |
| very      |
| popular   |
| with web  |
| applicati |
| ons.      |
|           |
|  This     |
| project   |
| suggestio |
| n         |
| is based  |
| on some   |
| prototype |
| work done |
| as part   |
| of the    |
| 'amanzi   |
| wireless  |
| explorer' |
| eclipse   |
| RCP       |
| applicati |
| on        |
| based on  |
| the uDIG  |
| SDK. We   |
| access a  |
| REST      |
| service   |
| on        |
| another   |
| applicati |
| on        |
| we wrote, |
| written   |
| in Ruby   |
| on Rails. |
| This REST |
| service   |
| publishes |
| a GeoJSON |
| dataset   |
| composed  |
| of Point  |
| features  |
| with a    |
| complex   |
| set of    |
| propertie |
| s         |
| specific  |
| to the    |
| mobile    |
| network   |
| modelling |
| domain    |
| the       |
| applicati |
| on        |
| was       |
| written   |
| for. For  |
| an        |
| example   |
| of the    |
| exact     |
| GeoJSON   |
| format we |
| use, see  |
| the wiki  |
| page at   |
| http://re |
| dmine.ama |
| nzi.org/w |
| iki/awe/N |
| etwork_Ge |
| oJSON.    |
| This data |
| is viewed |
| using a   |
| custom    |
| renderer  |
| that uses |
| the       |
| property  |
| data to   |
| render    |
| specific  |
| symbols.  |
|           |
| |image2|  |
|           |
| Future wo |
| rk        |
| ========= |
| ==        |
|           |
| |         |
| |  A lot  |
| of work   |
| needs to  |
| be done   |
| to        |
| generaliz |
| e         |
| this for  |
| use in    |
| uDIG.     |
|           |
| -  The    |
|    catalo |
| g         |
|    plugin |
|    needs  |
|    to     |
|    suppor |
| t         |
|    parsin |
| g         |
|    of all |
|    suppor |
| ted       |
|    featur |
| e         |
|    types. |
|    Curren |
| tly       |
|    it     |
|    suppor |
| ts        |
|    most   |
|    non-Mu |
| lti       |
|    types. |
|    Also   |
|    custom |
|    proper |
| ties      |
|    need   |
|    to be  |
|    interp |
| reted     |
|    in a   |
|    generi |
| c         |
|    way,   |
|    compat |
| ible      |
|    with   |
|    normal |
|    uDIG   |
|    table  |
|    and    |
|    info   |
|    tool.  |
| -  The    |
|    render |
|    need   |
|    to be  |
|    entire |
| ly        |
|    replac |
| e.        |
|    The    |
|    curren |
| t         |
|    custom |
|    render |
| er        |
|    is     |
|    comple |
| tely      |
|    specif |
| ic        |
|    to the |
|    partic |
| ular      |
|    data   |
|    used   |
|    in the |
|    protot |
| ype,      |
|    which  |
|    includ |
| es        |
|    only   |
|    Point  |
|    featur |
| es        |
|    with   |
|    very   |
|    specif |
| ic        |
|    custom |
|    proper |
| ties.     |
|    Either |
|    the    |
|    catalo |
| g         |
|    needs  |
|    to     |
|    transl |
| ate       |
|    the    |
|    data   |
|    stream |
|    into   |
|    someth |
| ing       |
|    unders |
| tood      |
|    by the |
|    normal |
|    uDIG   |
|    featur |
| e         |
|    render |
| er,       |
|    so it  |
|    can    |
|    apply  |
|    SLD's, |
|    or a   |
|    new    |
|    render |
| er        |
|    capabl |
| e         |
|    of     |
|    applyi |
| ng        |
|    SLD's  |
|    to     |
|    GeoJSO |
| N         |
|    should |
|    be     |
|    develo |
| ped.      |
| -  An     |
|    export |
|    capabi |
| lity      |
|    for    |
|    export |
| ing       |
|    other  |
|    layers |
|    to     |
|    GeoJSO |
| N         |
|    format |
|    is a   |
|    nice   |
|    comple |
| ment      |
|    to the |
|    load   |
|    and    |
|    render |
|    plugin |
| s.        |
|    Then   |
|    consis |
| tency     |
|    tests  |
|    can be |
|    done   |
|    on     |
|    export |
|    and    |
|    reload |
|    of     |
|    many   |
|    datase |
| ts        |
|    to     |
|    test   |
|    how    |
|    well   |
|    GeoJSO |
| N         |
|    suppor |
| ts        |
|    featur |
| e         |
|    data.  |
| -  Style  |
|    inform |
| ation     |
|    can be |
|    embedd |
| ed        |
|    into   |
|    the    |
|    proper |
| ties      |
|    tags   |
|    of the |
|    export |
| ed        |
|    and    |
|    re-imp |
| orted     |
|    GeoJSO |
| N         |
|    to     |
|    mainta |
| in        |
|    the    |
|    style. |
|           |
| Attachmen |
| ts:       |
| |image3|  |
| `GeoJSON\ |
| _Network. |
| png <down |
| load/atta |
| chments/8 |
| 389141/Ge |
| oJSON_Net |
| work.png> |
| `__       |
| (image/pn |
| g)        |
+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+

+------------+----------------------------------------------------------+
| |image5|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+------------+----------------------------------------------------------+

.. |image0| image:: /images/geojson_support/GeoJSON_Network.png
.. |image1| image:: images/icons/bullet_blue.gif
.. |image2| image:: /images/geojson_support/GeoJSON_Network.png
.. |image3| image:: images/icons/bullet_blue.gif
.. |image4| image:: images/border/spacer.gif
.. |image5| image:: images/border/spacer.gif
