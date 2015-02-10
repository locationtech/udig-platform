Net.refractions.udig.render.features
####################################

+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+
| uDig :      |
| net.refract |
| ions.udig.r |
| ender.featu |
| res         |
| This page   |
| last        |
| changed on  |
| Jul 14,     |
| 2012 by     |
| jgarnett.   |
| The raster  |
| pipeline    |
| takes a     |
| feature     |
| Collection  |
| and renders |
| them to a   |
| Graphics2D  |
| object.     |
|             |
| Functional  |
| Requirement |
| s           |
| ''''''''''' |
| ''''''''''' |
| '           |
|             |
| -  Renders  |
|    geometri |
| es          |
|    from a   |
|    feature  |
|    store    |
| -  Maintain |
| s           |
|    a caches |
|    to       |
|    improve  |
|    renderin |
| g           |
|    performa |
| nce         |
| -  Transfor |
| m           |
|    between  |
|    the      |
|    followin |
| g           |
|    coordina |
| te          |
|    systems  |
|    when     |
|    needed   |
|             |
|    -  Layer |
| CS          |
|             |
|       -  Th |
| e           |
|          Co |
| ordinate    |
|          Sy |
| stem        |
|          th |
| at          |
|          th |
| e           |
|          fe |
| atures      |
|          ar |
| e           |
|          in |
|          wh |
| en          |
|          th |
| ey          |
|          re |
| ach         |
|          th |
| e           |
|          fe |
| ature       |
|          re |
| nderer      |
|             |
|    -  mapCS |
|             |
|       -  Th |
| e           |
|          Co |
| ordinate    |
|          Sy |
| stem        |
|          th |
| at          |
|          th |
| e           |
|          us |
| er          |
|          wi |
| ll          |
|          se |
| e           |
|          on |
|          th |
| e           |
|          sc |
| reen        |
|             |
|    -  textC |
| S           |
|             |
|       -  Th |
| e           |
|          Ja |
| va2D        |
|          co |
| ordinate    |
|          sy |
| stem.       |
|             |
|    -  Devic |
| eCS         |
|             |
|       -  Th |
| e           |
|          de |
| vice        |
|          co |
| ordinate    |
|          sy |
| stem.       |
|          Ea |
| ch          |
|          "u |
| nit"        |
|          is |
|          a  |
|          pi |
| xel         |
|          of |
|          de |
| vice-depend |
| ent         |
|          si |
| ze.         |
|             |
| -  Styles   |
|    features |
|    with SLD |
|    styling  |
| -  Renders  |
|    non-sele |
| cted        |
|    features |
|    onto on  |
|    Graphics |
| 2D          |
| -  Selected |
|    Features |
| :           |
|             |
|    -  Rende |
| rs          |
|       selec |
| ted         |
|       featu |
| res         |
|       to a  |
|       Graph |
| ics2D       |
|       (not  |
|       the   |
|       same  |
|       Graph |
| ics2D       |
|       as    |
|       selec |
| ted         |
|       featu |
| res)        |
|    -  Style |
| s           |
|       selec |
| ted         |
|       Featu |
| res         |
|       based |
|       on    |
|       SLD   |
|             |
| Non-functio |
| nal Require |
| ments:      |
| ''''''''''' |
| ''''''''''' |
| ''''''      |
|             |
| -  Render   |
|    features |
|    set in   |
|    3-5      |
|    seconds  |
| -  Must be  |
|    able to  |
|    render   |
|    customiz |
| ed          |
|    styles.  |
| -  Multiple |
|    renderin |
| g           |
|    options: |
|             |
|    -  Rende |
| r           |
|       all   |
|       featu |
| res         |
|       assoc |
| iated       |
|       with  |
|       a     |
|       query |
|    -  rende |
| r           |
|       a     |
|       selec |
| tion        |
|    -  Rende |
| ring        |
|       statu |
| s           |
|       can   |
|       be    |
|       on/of |
| f           |
|       Featu |
| re          |
|       Rende |
| rer         |
|       Pipel |
| ine         |
|             |
| Design Note |
| s:          |
| ''''''''''' |
| ''          |
|             |
| -  Inputs:  |
|             |
|    -  Featu |
| reStore     |
|    -  Query |
|    -  Selec |
| tion        |
|       Filte |
| rs          |
|    -  Style |
|    -  view  |
|       area  |
|             |
| -  Outputs: |
|             |
|    -  Image |
| Buffer      |
|       with  |
|       rende |
| red         |
|       featu |
| res         |
|    -  Image |
| Buffer      |
|       with  |
|       selec |
| ted         |
|       featu |
| res         |
|             |
| -  Listens  |
|    to       |
|    `net.ref |
| ractions.ud |
| ig.project. |
| context <ne |
| t.refractio |
| ns.udig.pro |
| ject.contex |
| t.html>`__  |
|    for Bbox |
|    and SRS  |
|    events   |
| -  Listens  |
|    to       |
|    DataStor |
| e           |
|    for      |
|    feature  |
|    change   |
|    events   |
| -  Listens  |
|    to       |
|    `net.ref |
| ractions.ud |
| ig.project. |
| selection < |
| net.refract |
| ions.udig.p |
| roject.sele |
| ction.html> |
| `__         |
|    for      |
|    selectio |
| n           |
|    events   |
             
+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+

+------------+----------------------------------------------------------+
| |image1|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+------------+----------------------------------------------------------+

.. |image0| image:: images/border/spacer.gif
.. |image1| image:: images/border/spacer.gif
