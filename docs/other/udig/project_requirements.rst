Project Requirements
####################

+--------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| uDig :       |
| Project      |
| Requirements |
| This page    |
| last changed |
| on Feb 03,   |
| 2005 by      |
| aalam.       |
| These        |
| requirements |
| are from the |
| project      |
| proposal.    |
|              |
| Functional R |
| equirements  |
| ^^^^^^^^^^^^ |
| ^^^^^^^^^^^  |
|              |
| -  **WFS     |
|    client    |
|    read/writ |
| e            |
|    support** |
| ,            |
|    to allow  |
|    direct    |
|    editing   |
|    of data   |
|    exposed   |
|    via       |
|    transacti |
| on           |
|    web       |
|    feature   |
|    servers   |
| -  **WMS     |
|    support** |
| ,            |
|    to allow  |
|    viewing   |
|    of        |
|    backgroun |
| d            |
|    data      |
|    published |
|    via WMS   |
| -  **Styled  |
|    Layer     |
|    Descripto |
| r            |
|    (SLD)     |
|    support** |
| ,            |
|    to allow  |
|    the       |
|    client-di |
| rected       |
|    dynamic   |
|    re-stylin |
| g            |
|    of WMS    |
|    layers    |
| -  **Web     |
|    Registry  |
|    Server    |
|    support** |
| ,            |
|    for quick |
|    location  |
|    of        |
|    available |
|    CGDI      |
|    layers    |
| -  **Printin |
| g            |
|    support** |
| ,            |
|    to allow  |
|    users to  |
|    create    |
|    standard  |
|    and large |
|    format    |
|    cartograp |
| hy           |
|    from      |
|    their     |
|    desktops  |
|    using     |
|    CGDI data |
|    sources   |
| -  **Standar |
| d            |
|    GID file  |
|    format    |
|    support** |
| ,            |
|    to allow  |
|    users to  |
|    directly  |
|    open,     |
|    overlay,  |
|    and edit  |
|    local     |
|    Shape and |
|    GeoTiff   |
|    files     |
|    with CGDI |
|    online    |
|    data.     |
| -  **Coordin |
| ate          |
|    porjectio |
| n            |
|    support** |
| ,            |
|    to        |
|    transpare |
| ntly         |
|    intergrat |
| e            |
|    remote    |
|    layers in |
|    the cline |
|    tapplicat |
| ion          |
|    where     |
|    necessary |
| .            |
| -  **Databas |
| e            |
|    access    |
|    support** |
| ,            |
|    to allow  |
|    users to  |
|    directly  |
|    open,     |
|    overlay   |
|    and edit  |
|    data      |
|    stored in |
|    PostGIS,  |
|    OracleSpa |
| tial,        |
|    ArcSDE,   |
|    and       |
|    MySQL.    |
| -  **Cross-p |
| latform      |
|    support** |
| ,            |
|    using     |
|    Java as   |
|    an        |
|    implement |
| ation        |
|    language, |
|    and       |
|    providing |
|    one-click |
|    setup     |
|    files for |
|    Windows,  |
|    OS/X, and |
|    Linux.    |
| -  **Multi-l |
| ingual       |
|    design**, |
|    allowing  |
|    easy      |
|    internati |
| onalization  |
|    of the    |
|    interface |
| ,            |
|    with      |
|    French    |
|    and       |
|    English   |
|    translati |
| ons          |
|    of the    |
|    interface |
|    completed |
|    initially |
| .            |
| -  **Customi |
| zability     |
|    and       |
|    modularit |
| y**,         |
|    to allow  |
|    third     |
|    party     |
|    developer |
| s            |
|    to add    |
|    new       |
|    capabilit |
| ies,         |
|    or strip  |
|    out       |
|    existing  |
|    capabilit |
| ies          |
|    as        |
|    necessary |
|    when      |
|    intergrat |
| ing          |
|    the       |
|    applicati |
| on           |
|    with      |
|    existing  |
|    enterpris |
| e            |
|    infrastru |
| ctures.      |
|              |
| Non Function |
| al Requireme |
| nts          |
| ^^^^^^^^^^^^ |
| ^^^^^^^^^^^^ |
| ^^^          |
|              |
| -  **Well-Ro |
| unded        |
|    Framework |
| **,          |
|    built on  |
|    standard  |
|    and       |
|    best-of-b |
| reed         |
|    libraries |
|    to offer  |
|    a         |
|    sustainab |
| le,          |
|    competiti |
| ve           |
|    advantage |
|    to uDig   |
|    developer |
| s.           |
|              |
|    -  **Plug |
| -in          |
|       Deploy |
| ment         |
|       Model* |
| *,           |
|       with   |
|       versio |
| ning         |
|       and    |
|       plug-i |
| n            |
|       manage |
| ment         |
|       to     |
|       ease   |
|       the    |
|       cost   |
|       of     |
|       deploy |
| ment,        |
|       upgrad |
| ing          |
|       and    |
|       instal |
| lation.      |
|    -  **Inte |
| gration/Exte |
| nsion**,     |
|       mainta |
| in           |
|       common |
|       appear |
| ance,        |
|       workfl |
| ow,          |
|       framew |
| ork          |
|       and    |
|       persis |
| tence        |
|       mechan |
| isms         |
|       betwee |
| n            |
|       built- |
| in           |
|       editin |
| g            |
|       and    |
|       third- |
| party        |
|       module |
| s.           |
|    -  **Logs |
| **,          |
|       make   |
|       use of |
|       loggin |
| g            |
|       standa |
| rds          |
|       and    |
|       librar |
| ies.         |
|              |
| -  **Open    |
|    Developme |
| nt           |
|    Process** |
| ,            |
|    capture   |
|    developer |
|    interest  |
|    and third |
|    party     |
|    contribut |
| ions.        |
| -  Marketing |
| -  **Release |
|    Managemen |
| t**,         |
|    stable    |
|    and       |
|    developme |
| nt           |
|    releases. |
| -  **Product |
|    Developme |
| nt           |
|    and       |
|    Branding* |
| *,           |
|    continued |
|    use of    |
|    JUMP      |
|    branding. |
| -  Licensing |
|    Model and |
|    Business  |
|    Model     |
|              |
|    -  **Appl |
| ication      |
|       Licens |
| e            |
|       Model* |
| *,           |
|       open-s |
| ource        |
|       licens |
| e            |
|       to     |
|       allow  |
|       distri |
| bution       |
|       and    |
|       extens |
| ion          |
|       withou |
| t            |
|       incurr |
| ing          |
|       multip |
| le           |
|       licens |
| ing          |
|       fees,  |
|       commer |
| cial         |
|       suppor |
| t            |
|       allows |
|       for a  |
|       busine |
| ss           |
|       model. |
|    -  **Exte |
| nsion        |
|       Licens |
| e            |
|       Model* |
| *,           |
|       open-s |
| ource        |
|       Framew |
| ork          |
|       API    |
|       allows |
|       GPL or |
|       Commer |
| cial         |
|       extens |
| ion.         |
|              |
| -  **Usabili |
| ty**,        |
|    use       |
|    industry  |
|    standard  |
|    user-inte |
| rface        |
|    construct |
| s            |
|    and       |
|    terminolo |
| gy           |
|    to reduce |
|    training  |
|    time.     |
|              |
|    -  **Conf |
| iguration    |
|       and    |
|       Prefer |
| ences**,     |
|       make   |
|       use of |
|       sensib |
| le           |
|       defaul |
| ts,          |
|       use    |
|       contex |
| t            |
|       where  |
|       possib |
| le.          |
|    -  **Inst |
| allation**,  |
|       allow  |
|       instal |
| lation       |
|       with   |
|       sensib |
| le           |
|       defaul |
| ts           |
|       and    |
|       little |
|       user   |
|       input. |
|    -  **Prof |
| essional     |
|       Appear |
| ance**,      |
|       integr |
| ate          |
|       with   |
|       existi |
| ng           |
|       instal |
| lation       |
|       base.  |
|    -  **Quic |
| k            |
|       Respon |
| se**,        |
|       provid |
| e            |
|       immedi |
| ate          |
|       feedba |
| ck.          |
|              |
| -  Performan |
| ce           |
|              |
|    -  **Data |
|       Access |
|       Perfor |
| mance**,     |
|       ESRI   |
|       Shapef |
| ile          |
|       access |
|       is a   |
|       signif |
| icant        |
|       measur |
| e            |
|       of     |
|       applic |
| ation        |
|       perfor |
| mance        |
|       and    |
|       must   |
|       be     |
|       more   |
|       then   |
|       compet |
| itive.       |
|    -  **Oper |
| ative        |
|       Perfor |
| mance**,     |
|       applic |
| ation        |
|       must   |
|       be     |
|       suffic |
| iently       |
|       respon |
| sive         |
|       so     |
|       that   |
|       an     |
|       operat |
| or           |
|       can    |
|       mainta |
| in           |
|       concen |
| tration.     |
|              |
| -  **Securit |
| y**,         |
|    considere |
| d            |
|    where     |
|    applicabl |
| e:           |
|    database  |
|    passwords |
|    will not  |
|    be stored |
|    with      |
|    project   |
|    file; the |
|    OWS       |
|    infrastru |
| cture        |
|    lacks a   |
|    strong    |
|    security  |
|    model.    |
|              |
| +----------- |
| ------------ |
| ------------ |
| ------------ |
| ------------ |
| ---------+   |
| | |image3|   |
|              |
|              |
|              |
|              |
|          |   |
| | **Next**   |
|              |
|              |
|              |
|              |
|          |   |
| | Cannot res |
| olve externa |
| l resource i |
| nto attachme |
| nt. Source L |
| icense   |   |
| +----------- |
| ------------ |
| ------------ |
| ------------ |
| ------------ |
| ---------+   |
              
+--------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+

+------------+----------------------------------------------------------+
| |image5|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+------------+----------------------------------------------------------+

.. |image0| image:: images/icons/emoticons/information.gif
.. |image1| image:: images/icons/emoticons/information.gif
.. |image2| image:: images/icons/emoticons/information.gif
.. |image3| image:: images/icons/emoticons/information.gif
.. |image4| image:: images/border/spacer.gif
.. |image5| image:: images/border/spacer.gif
