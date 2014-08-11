Wms Tile Caching
################

+-----------------------------------+-----------------------------------+-----------------------------------+
| uDig : WMS Tile Caching           |
| This page last changed on Sep 21, |
| 2012 by jgarnett.                 |
| +---------+---------+---------+-- |
| -------+---------+---------+----- |
| ----+---------+---------+-------- |
| -+---------+---------+---------+- |
| --------+                         |
| | Motivat |                       |
| | ion     |                       |
| | ======= |                       |
| | ===     |                       |
| |         |                       |
| | The     |                       |
| | WMS\_C  |                       |
| | idea is |                       |
| | actuall |                       |
| | y       |                       |
| | taking  |                       |
| | shape;  |                       |
| | and     |                       |
| | would   |                       |
| | allow   |                       |
| | us to   |                       |
| | benefit |                       |
| | from    |                       |
| | project |                       |
| | s       |                       |
| | such as |                       |
| | GeoWebC |                       |
| | ache    |                       |
| | (formal |                       |
| | ly      |                       |
| | JTileCa |                       |
| | che)    |                       |
| | and     |                       |
| | receive |                       |
| | a       |                       |
| | signifi |                       |
| | cant    |                       |
| | speed   |                       |
| | boost   |                       |
| | over    |                       |
| | our     |                       |
| | usual   |                       |
| | WMS     |                       |
| | experie |                       |
| | nce.    |                       |
| |         |                       |
| | Inspira |                       |
| | tion    |                       |
| | ======= |                       |
| | ====    |                       |
| |         |                       |
| | The     |                       |
| | page on |                       |
| | this    |                       |
| | stuff   |                       |
| | is      |                       |
| | here:   |                       |
| |         |                       |
| | -  http |                       |
| | ://wiki |                       |
| | .osgeo. |                       |
| | org/wik |                       |
| | i/WMS_T |                       |
| | ile_Cac |                       |
| | hing    |                       |
| | -  http |                       |
| | ://wiki |                       |
| | .osgeo. |                       |
| | org/wik |                       |
| | i/WMS_T |                       |
| | iling_C |                       |
| | lient_R |                       |
| | ecommen |                       |
| | dation  |                       |
| |         |                       |
| | We can  |                       |
| | make    |                       |
| | use of  |                       |
| | some    |                       |
| | previou |                       |
| | s       |                       |
| | work    |                       |
| | done in |                       |
| | a       |                       |
| | communi |                       |
| | ty      |                       |
| | modules |                       |
| | ;       |                       |
| | and on  |                       |
| | the     |                       |
| | GeoWebC |                       |
| | ache    |                       |
| | code    |                       |
| | itself  |                       |
| | (due to |                       |
| | its use |                       |
| | of an   |                       |
| | LGPL    |                       |
| | license |                       |
| | ).      |                       |
| | I would |                       |
| | like to |                       |
| | make    |                       |
| | contact |                       |
| | with    |                       |
| | the     |                       |
| | develop |                       |
| | ers     |                       |
| | there   |                       |
| | and see |                       |
| | if      |                       |
| | there   |                       |
| | is room |                       |
| | to      |                       |
| | shake   |                       |
| | out a   |                       |
| | common  |                       |
| | "tile   |                       |
| | queue"  |                       |
| | library |                       |
| | which   |                       |
| | both    |                       |
| | project |                       |
| | s       |                       |
| | could   |                       |
| | use.    |                       |
| |         |                       |
| | Some    |                       |
| | impleme |                       |
| | ntation |                       |
| | s       |                       |
| | to      |                       |
| | conside |                       |
| | r:      |                       |
| |         |                       |
| | -  `geo |                       |
| | webcach |                       |
| | e <http |                       |
| | ://geow |                       |
| | ebcache |                       |
| | .org/>` |                       |
| | __      |                       |
| |    (liv |                       |
| | e       |                       |
| |    proj |                       |
| | ect,    |                       |
| |    cons |                       |
| | idered  |                       |
| |    stab |                       |
| | le      |                       |
| |    and  |                       |
| |    bein |                       |
| | g       |                       |
| |    ship |                       |
| | ped     |                       |
| |    with |                       |
| |    GeoS |                       |
| | erver   |                       |
| |    1.7. |                       |
| | 0)      |                       |
| | -  `jti |                       |
| | lecache |                       |
| |  <http: |                       |
| | //code. |                       |
| | google. |                       |
| | com/p/j |                       |
| | tilecac |                       |
| | he/>`__ |                       |
| |    (Ini |                       |
| | tial    |                       |
| |    proo |                       |
| | f       |                       |
| |    of   |                       |
| |    conc |                       |
| | ept     |                       |
| |    done |                       |
| |    as a |                       |
| |    Goog |                       |
| | le      |                       |
| |    SoC  |                       |
| |    proj |                       |
| | ect)    |                       |
| | -  `net |                       |
| | .refrac |                       |
| | tions.u |                       |
| | dig.com |                       |
| | munity. |                       |
| | jody.ti |                       |
| | le <htt |                       |
| | p://svn |                       |
| | .refrac |                       |
| | tions.n |                       |
| | et/udig |                       |
| | /udig/c |                       |
| | ommunit |                       |
| | y/jody/ |                       |
| | trunk/p |                       |
| | lugins/ |                       |
| | net.ref |                       |
| | raction |                       |
| | s.udig. |                       |
| | communi |                       |
| | ty.jody |                       |
| | .tile/> |                       |
| | `__     |                       |
| |         |                       |
| | Proposa |                       |
| | l       |                       |
| | ======= |                       |
| | =       |                       |
| |         |                       |
| | #. To   |                       |
| |    star |                       |
| | t       |                       |
| |    out  |                       |
| |    with |                       |
| |    we   |                       |
| |    can  |                       |
| |    get  |                       |
| |    a    |                       |
| |    quic |                       |
| | k       |                       |
| |    beni |                       |
| | fit     |                       |
| |    by   |                       |
| |    usin |                       |
| | g       |                       |
| |    the  |                       |
| |    exis |                       |
| | ting    |                       |
| |    geot |                       |
| | ools    |                       |
| |    WMS  |                       |
| |    clie |                       |
| | nt      |                       |
| |    code |                       |
| | :       |                       |
| | #. -    |                       |
| |    Crea |                       |
| | te      |                       |
| |    a    |                       |
| |    Rend |                       |
| | erer    |                       |
| |    that |                       |
| |    requ |                       |
| | ests    |                       |
| |    WMS  |                       |
| |    data |                       |
| |    as   |                       |
| |    seri |                       |
| | es      |                       |
| |    of   |                       |
| |    Tile |                       |
| | s;      |                       |
| |    comp |                       |
| | osing   |                       |
| |    them |                       |
| |    into |                       |
| |    a    |                       |
| |    fina |                       |
| | l       |                       |
| |    imag |                       |
| | e;      |                       |
| |    and  |                       |
| |    not  |                       |
| |    thro |                       |
| | wing    |                       |
| |    out  |                       |
| |    tile |                       |
| | s       |                       |
| |    unti |                       |
| | l       |                       |
| |    they |                       |
| |    are  |                       |
| |    off  |                       |
| |    scre |                       |
| | en.     |                       |
| |    This |                       |
| |    can  |                       |
| |    be   |                       |
| |    used |                       |
| |    to   |                       |
| |    prac |                       |
| | tice    |                       |
| |    comp |                       |
| | osition |                       |
| |    and  |                       |
| |    even |                       |
| | t       |                       |
| |    hand |                       |
| | ling.   |                       |
| | #. We   |                       |
| |    can  |                       |
| |    cont |                       |
| | inue    |                       |
| |    this |                       |
| |    by   |                       |
| |    intr |                       |
| | oducing |                       |
| |    a    |                       |
| |    data |                       |
| |    acce |                       |
| | ss      |                       |
| |    api: |                       |
| |         |                       |
| |    -  C |                       |
| | reate   |                       |
| |       a |                       |
| |       W |                       |
| | MSC     |                       |
| |       d |                       |
| | ata     |                       |
| |       a |                       |
| | ccess   |                       |
| |       a |                       |
| | pi      |                       |
| |       t |                       |
| | hat     |                       |
| |       b |                       |
| | reaks   |                       |
| |       t |                       |
| | hings   |                       |
| |       d |                       |
| | own     |                       |
| |       i |                       |
| | nto     |                       |
| |       a |                       |
| |       m |                       |
| | odel    |                       |
| |       o |                       |
| | f       |                       |
| |       T |                       |
| | ileSets |                       |
| |       / |                       |
| |       Z |                       |
| | oomLeve |                       |
| | ls      |                       |
| |       e |                       |
| | tc      |                       |
| |       a |                       |
| | nd      |                       |
| |       c |                       |
| | an      |                       |
| |       s |                       |
| | afely   |                       |
| |       p |                       |
| | roduce  |                       |
| |       t |                       |
| | iles    |                       |
| |       o |                       |
| | n       |                       |
| |       r |                       |
| | equest. |                       |
| |    -  P |                       |
| | utting  |                       |
| |       t |                       |
| | hese    |                       |
| |       t |                       |
| | wo      |                       |
| |       t |                       |
| | ogether |                       |
| |       w |                       |
| | ould    |                       |
| |       a |                       |
| | llow    |                       |
| |       t |                       |
| | he      |                       |
| |       r |                       |
| | enderer |                       |
| |       t |                       |
| | o       |                       |
| |       r |                       |
| | equest  |                       |
| |       t |                       |
| | iles    |                       |
| |       i |                       |
| | n       |                       |
| |       e |                       |
| | xactly  |                       |
| |       t |                       |
| | he      |                       |
| |       m |                       |
| | anner   |                       |
| |       G |                       |
| | eoWebCa |                       |
| | che     |                       |
| |       e |                       |
| | xpects; |                       |
| |       t |                       |
| | hus     |                       |
| |       a |                       |
| | llowing |                       |
| |       u |                       |
| | s       |                       |
| |       t |                       |
| | o       |                       |
| |       b |                       |
| | enifit  |                       |
| |       f |                       |
| | rom     |                       |
| |       t |                       |
| | he      |                       |
| |       c |                       |
| | ache    |                       |
| |         |                       |
| | #. Fina |                       |
| | lly     |                       |
| |    we   |                       |
| |    can  |                       |
| |    star |                       |
| | t       |                       |
| |    to   |                       |
| |    cach |                       |
| | e       |                       |
| |    our  |                       |
| |    tile |                       |
| | s:      |                       |
| |         |                       |
| |    -  C |                       |
| | reate   |                       |
| |       a |                       |
| |       " |                       |
| | TileCac |                       |
| | he"     |                       |
| |       d |                       |
| | ata     |                       |
| |       s |                       |
| | tructur |                       |
| | e       |                       |
| |       f |                       |
| | or      |                       |
| |       t |                       |
| | he      |                       |
| |       W |                       |
| | MSC     |                       |
| |       t |                       |
| | o       |                       |
| |       u |                       |
| | se      |                       |
| |       b |                       |
| | ehind   |                       |
| |       t |                       |
| | he      |                       |
| |       s |                       |
| | cenes.  |                       |
| |    -  I |                       |
| | t       |                       |
| |       w |                       |
| | ould    |                       |
| |       m |                       |
| | ake     |                       |
| |       s |                       |
| | ense    |                       |
| |       t |                       |
| | o       |                       |
| |       u |                       |
| | se      |                       |
| |       a |                       |
| |       J |                       |
| | TS      |                       |
| |       s |                       |
| | patial  |                       |
| |       i |                       |
| | ndex    |                       |
| |       f |                       |
| | or      |                       |
| |       e |                       |
| | ach     |                       |
| |       z |                       |
| | oom     |                       |
| |       l |                       |
| | evel    |                       |
| |       a |                       |
| | nd      |                       |
| |       c |                       |
| | ombine  |                       |
| |       i |                       |
| | mages   |                       |
| |       i |                       |
| | n       |                       |
| |       m |                       |
| | uch     |                       |
| |       t |                       |
| | he      |                       |
| |       s |                       |
| | ame     |                       |
| |       w |                       |
| | ay      |                       |
| |       a |                       |
| | s       |                       |
| |       t |                       |
| | he      |                       |
| |       g |                       |
| | eotools |                       |
| |       i |                       |
| | mage    |                       |
| |       m |                       |
| | oasic   |                       |
| |       c |                       |
| | ode.    |                       |
| |         |                       |
| | For     |                       |
| | extra   |                       |
| | points  |                       |
| | it      |                       |
| | would   |                       |
| | be good |                       |
| | to make |                       |
| | use of  |                       |
| | the     |                       |
| | HTTP    |                       |
| | header  |                       |
| | informa |                       |
| | tion    |                       |
| | (to     |                       |
| | determi |                       |
| | ne      |                       |
| | how     |                       |
| | long we |                       |
| | should  |                       |
| | hold    |                       |
| | onto    |                       |
| | our     |                       |
| | cached  |                       |
| | tile    |                       |
| | for     |                       |
| | etc).   |                       |
| | Some of |                       |
| | this    |                       |
| | depends |                       |
| | on how  |                       |
| | smart   |                       |
| | the     |                       |
| | GeoWebC |                       |
| | ache    |                       |
| | project |                       |
| | is      |                       |
| | about   |                       |
| | that    |                       |
| | stuff?  |                       |
| |         |                       |
| | Update: |                       |
| |         |                       |
| | A       |                       |
| | summer  |                       |
| | of code |                       |
| | project |                       |
| | has     |                       |
| | been    |                       |
| | accepte |                       |
| | d       |                       |
| | to      |                       |
| | extend  |                       |
| | this    |                       |
| | work to |                       |
| | support |                       |
| | additio |                       |
| | nal     |                       |
| | tile    |                       |
| | servers |                       |
| | .       |                       |
| |         |                       |
| | | Updat |                       |
| | e:      |                       |
| | |       |                       |
| | WMS-C   |                       |
| | support |                       |
| | is now  |                       |
| | availab |                       |
| | le      |                       |
| | on      |                       |
| | trunk.  |                       |
| +---------+---------+---------+-- |
| -------+---------+---------+----- |
| ----+---------+---------+-------- |
| -+---------+---------+---------+- |
| --------+                         |
                                   
+-----------------------------------+-----------------------------------+-----------------------------------+

+------------+----------------------------------------------------------+
| |image1|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+------------+----------------------------------------------------------+

.. |image0| image:: images/border/spacer.gif
.. |image1| image:: images/border/spacer.gif
