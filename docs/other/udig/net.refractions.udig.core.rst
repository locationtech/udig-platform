Net.refractions.udig.core
#########################

+----------+----------+----------+----------+----------+----------+----------+----------+----------+----------+----------+----------+
| uDig :   |
| net.refr |
| actions. |
| udig.cor |
| e        |
| This     |
| page     |
| last     |
| changed  |
| on Jul   |
| 14, 2012 |
| by       |
| jgarnett |
| .        |
| Defines  |
| the      |
| requirem |
| ents     |
| needed   |
| for      |
| plug-in  |
| develope |
| rs       |
| or       |
| scriptin |
| g        |
| services |
| .        |
|          |
| Function |
| al Requi |
| rements  |
| ~~~~~~~~ |
| ~~~~~~~~ |
| ~~~~~~~  |
|          |
| -  acces |
| s        |
|    to    |
|    data  |
|    (in   |
|    the   |
|    catal |
| og       |
|    sense |
| ,        |
|    list  |
|    of    |
|    defin |
| ed       |
|    datas |
| tore/ser |
| vers)    |
| -  acces |
| s        |
|    to    |
|    viewp |
| ort      |
|    (bbox |
| ,        |
|    crsm  |
|    trans |
| action   |
|    *-Wha |
| t        |
|    is    |
|    this? |
| *,       |
|    selec |
| tion,    |
|    map   |
|    layer |
| s)       |
| -  acces |
| s        |
|    to    |
|    issue |
| s        |
|    list  |
|    (is   |
|    this  |
|    per   |
|    viewp |
| ort?     |
|    *-We  |
|    shoul |
| d        |
|    make  |
|    a     |
|    defin |
| ition    |
|    for   |
|    view  |
|    port* |
| )        |
| -  abili |
| ty       |
|    to    |
|    defin |
| e        |
|    user  |
|    inter |
| face     |
|    (defi |
| ne       |
|    view, |
|    add   |
|    menu  |
|    items |
| /toolbar |
| s,       |
|    key   |
|    short |
| -cuts)   |
| -  abili |
| ty       |
|    to    |
|    cance |
| l        |
|    a     |
|    runni |
| ng       |
|    opera |
| tion     |
|    (fram |
| ework    |
|    shoul |
| d        |
|    provi |
| de       |
|    threa |
| ds       |
|    by    |
|    defau |
| lt)      |
| -  gener |
| ate      |
|    progr |
| ess      |
|    event |
| s        |
| -  versi |
| oned     |
|    (inst |
| allation |
|    metad |
| ata)     |
|    *-Ela |
| borate!* |
| -  Acces |
| s        |
|    to    |
|    tempo |
| rary     |
|    data  |
|    dump  |
|    (loca |
| l        |
|    files |
| ystem    |
|    or    |
|    slave |
|    datab |
| ase)     |
|    for   |
|    opera |
| tion     |
|    resul |
| ts       |
|    (acts |
|    as a  |
|    stagi |
| ng       |
|    area  |
|    from  |
|    which |
|    user  |
|    can   |
|    right |
|    click |
|    and   |
|    expor |
| t        |
|    to    |
|    real  |
|    datab |
| ase      |
|    table |
|    - or  |
|    sync  |
|    with  |
|    real  |
|    datab |
| ase      |
|    table |
| )        |
| -  shoul |
| d        |
|    be    |
|    Trans |
| action   |
|    Aware |
|    (poss |
| ibly     |
|    hooke |
| d        |
|    into  |
|    threa |
| ding?)   |
|    *-Pro |
| vide     |
|    link  |
|    to    |
|    trans |
| action*  |
|          |
| Non Func |
| tional R |
| equireme |
| nts      |
| ~~~~~~~~ |
| ~~~~~~~~ |
| ~~~~~~~~ |
| ~~~      |
|          |
| -  stron |
| g        |
|    ui    |
|    guide |
| lines    |
|          |
| Design N |
| otes:    |
| ~~~~~~~~ |
| ~~~~~    |
|          |
| -  this  |
|    plug- |
| in       |
|    is    |
|    vastl |
| y        |
|    limit |
| ed       |
|    in    |
|    scope |
|    relat |
| ive      |
|    to    |
|    JUMP  |
|    (focu |
| s        |
|    on    |
|    hacki |
| ng       |
|    data  |
|    under |
|    user  |
|    contr |
| ol)      |
| -  black |
|    board |
|    is    |
|    recom |
| ended    |
|    by    |
|    JUMP  |
|    for   |
|    inter |
|    plug- |
| in       |
|    commu |
| nication |
| ,        |
|    simil |
| ar       |
|    to    |
|    servl |
| et       |
|    conte |
| xt       |
| -  Appar |
| ently    |
|    a     |
|    popul |
| ar       |
|    reque |
| st       |
|    of    |
|    JUMP  |
|    is    |
|    the   |
|    wish  |
|    for a |
|    visit |
| or       |
|    that  |
|    can   |
|    be    |
|    scrub |
| bed      |
|    over  |
|    one   |
|    datas |
| et       |
|    to    |
|    produ |
| ce       |
|    a     |
|    secon |
| d.       |
|    Right |
|    now   |
|    jump  |
|    uses  |
|    itera |
| tor      |
|    patte |
| rn       |
| -  plug- |
| in       |
|    can   |
|    just  |
|    regis |
| ter      |
|    for   |
|    the   |
|    same  |
|    notif |
| ications |
|    as    |
|    the   |
|    main  |
|    frame |
| work     |
|          |
| Thanks   |
| to       |
| Martin   |
| for      |
| passing  |
| on JUMP  |
| experien |
| ce.      |
|          |
| Wild     |
| Ideas:   |
|          |
| -  provi |
| de       |
|    enoug |
| h        |
|    glue  |
|    to    |
|    hook  |
|    plug- |
| in       |
|    up    |
|    with  |
|    Sourc |
| e/Destin |
| ation    |
|    Featu |
| reStores |
|    (Fram |
| ework    |
|    or    |
|    Super |
| class?)  |
| -  hook  |
|    threa |
| ded      |
|    by    |
|    defau |
| lt       |
|    /     |
|    desti |
| nation   |
|    Featu |
| reStores |
|    /     |
|    progr |
| ess      |
|    and   |
|    Trans |
| action   |
|    aware |
|    toget |
| her      |
|    so    |
|    that  |
|          |
|    "canc |
| el"      |
|    or    |
|    trans |
| action   |
|    rollb |
| ack      |
|    stops |
|    every |
| thing    |
|    and   |
|    clean |
| s        |
|    up    |
|    the   |
|    mess  |
|    with  |
|    out   |
|    expli |
| ct       |
|    plug- |
| in       |
|    devel |
| oper     |
|    pain  |
|    *-I   |
|    can't |
|    make  |
|    sense |
|    of    |
|    this* |
          
+----------+----------+----------+----------+----------+----------+----------+----------+----------+----------+----------+----------+

+------------+----------------------------------------------------------+
| |image1|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+------------+----------------------------------------------------------+

.. |image0| image:: images/border/spacer.gif
.. |image1| image:: images/border/spacer.gif
