Draw2drenderer
##############

+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+
| uDig |
| :    |
| Draw |
| 2DRe |
| nder |
| er   |
| This |
| page |
| last |
| chan |
| ged  |
| on   |
| Jun  |
| 23,  |
| 2010 |
| by   |
| jgar |
| nett |
| .    |
| Ecli |
| pse  |
| RCP  |
| deve |
| lope |
| rs   |
| are  |
| used |
| to   |
| use  |
| the  |
| SWT  |
| Draw |
| 2D   |
| api  |
| for  |
| rend |
| erin |
| g;   |
| we   |
| woul |
| d    |
| like |
| to   |
| make |
| a    |
| new  |
| Rend |
| erer |
| that |
| enab |
| les  |
| this |
| appr |
| oach |
| .    |
|      |
| `Bac |
| kgro |
| und  |
| and  |
| Scop |
| e <# |
| Draw |
| 2DRe |
| nder |
| er-B |
| ackg |
| roun |
| dand |
| Scop |
| e>`_ |
| _    |
|      |
| -  ` |
| IRen |
| dere |
| r    |
|    t |
| o    |
|    I |
| Draw |
| 2DRe |
| nder |
| er < |
| #Dra |
| w2DR |
| ende |
| rer- |
| IRen |
| dere |
| rtoI |
| Draw |
| 2DRe |
| nder |
| er>` |
| __   |
| -  ` |
| Inte |
| ract |
| ion  |
|    w |
| ith  |
|    R |
| ende |
| rCon |
| text |
|  <#D |
| raw2 |
| DRen |
| dere |
| r-In |
| tera |
| ctio |
| nwit |
| hRen |
| derC |
| onte |
| xt>` |
| __   |
|      |
| `App |
| roac |
| h <# |
| Draw |
| 2DRe |
| nder |
| er-A |
| ppro |
| ach> |
| `__  |
| Back |
| grou |
| nd a |
| nd S |
| cope |
| ==== |
| ==== |
| ==== |
| ==== |
| ==== |
|      |
| IRen |
| dere |
| r to |
|  IDr |
| aw2D |
| Rend |
| erer |
| ---- |
| ---- |
| ---- |
| ---- |
| ---- |
| ---- |
| ---- |
|      |
| Curr |
| ent  |
| impl |
| emen |
| tors |
| of   |
| Rend |
| erIm |
| pl   |
| are  |
| requ |
| ired |
| to   |
| supp |
| ly   |
| the  |
| foll |
| owin |
| g    |
| two  |
| IRen |
| dere |
| r    |
| meth |
| ods: |
|      |
| .. c |
| ode: |
| : co |
| de-j |
| ava  |
|      |
|      |
| void |
|  ren |
| der( |
|  Gra |
| phic |
| s2D  |
| g, I |
| Prog |
| ress |
| Moni |
| tor  |
| moni |
| tor  |
| ) th |
| rows |
|  Ren |
| derE |
| xcep |
| tion |
| ;    |
|      |
| void |
|  ren |
| der( |
|  IPr |
| ogre |
| ssMo |
| nito |
| r mo |
| nito |
| r )  |
| thro |
| ws R |
| ende |
| rExc |
| epti |
| on;  |
|      |
| We   |
| woul |
| d    |
| like |
| impl |
| emen |
| tors |
| of   |
| Draw |
| 2DRe |
| nder |
| er   |
| to   |
| impl |
| emen |
| t    |
| the  |
| foll |
| owin |
| g    |
| IDra |
| w2DR |
| ende |
| rMet |
| hods |
| :    |
|      |
| .. c |
| ode: |
| : co |
| de-j |
| ava  |
|      |
|      |
| void |
|  ren |
| der( |
|  GC  |
| gc,  |
| IPro |
| gres |
| sMon |
| itor |
|  mon |
| itor |
|  ) t |
| hrow |
| s Re |
| nder |
| Exce |
| ptio |
| n;   |
|      |
| void |
|  ren |
| der( |
|  IPr |
| ogre |
| ssMo |
| nito |
| r mo |
| nito |
| r )  |
| thro |
| ws R |
| ende |
| rExc |
| epti |
| on;  |
|      |
| We   |
| need |
| to   |
| bala |
| nce  |
| the  |
| shor |
| t    |
| term |
| goal |
| is   |
| to   |
| get  |
| some |
| thin |
| g    |
| on   |
| the  |
| scre |
| en   |
| now, |
| with |
| the  |
| need |
| to   |
| reta |
| in   |
| the  |
| abil |
| ity  |
| to   |
| prin |
| t.   |
| For  |
| the  |
| mome |
| nt   |
| we   |
| will |
| make |
| IDra |
| w2DR |
| ende |
| rer  |
| exte |
| nd   |
| IRen |
| dere |
| r    |
| -    |
| and  |
| work |
| on   |
| maki |
| ng   |
| a    |
| nice |
| supe |
| r    |
| clas |
| s    |
| that |
| prov |
| ides |
| a    |
| sing |
| le   |
| impl |
| emen |
| tati |
| on   |
| of   |
| the  |
| rend |
| er(  |
| g,   |
| moni |
| tor  |
| )    |
| meth |
| od.  |
|      |
| Inte |
| ract |
| ion  |
| with |
|  Ren |
| derC |
| onte |
| xt   |
| ---- |
| ---- |
| ---- |
| ---- |
| ---- |
| ---- |
| ---- |
| --   |
|      |
| Curr |
| entl |
| y    |
| IRen |
| dere |
| r    |
| impl |
| emen |
| tati |
| ons  |
| make |
| use  |
| of   |
| the  |
| foll |
| owin |
| g    |
| in   |
| orde |
| r    |
| to   |
| obta |
| in   |
| an   |
| Grap |
| hics |
| 2D   |
| imag |
| e    |
| to   |
| draw |
| onto |
| :    |
|      |
| .. c |
| ode: |
| : co |
| de-j |
| ava  |
|      |
|      |
| publ |
| ic v |
| oid  |
| rend |
| er(I |
| Prog |
| ress |
| Moni |
| tor  |
| moni |
| tor) |
|      |
|      |
|      |
|      |
| thro |
| ws R |
| ende |
| rExc |
| epti |
| on { |
|      |
|      |
|      |
| Grap |
| hics |
| 2D g |
| raph |
| ics  |
| = nu |
| ll;  |
|      |
|      |
|      |
| try  |
| {    |
|      |
|      |
|      |
|      |
| grap |
| hics |
|  = g |
| etCo |
| ntex |
| t(). |
| getI |
| mage |
| ().c |
| reat |
| eGra |
| phic |
| s(); |
|      |
|      |
|      |
|      |
| rend |
| er(g |
| raph |
| ics, |
|  get |
| Rend |
| erBo |
| unds |
| (),  |
| moni |
| tor) |
| ;    |
|      |
|      |
|      |
| } fi |
| nall |
| y {  |
|      |
|      |
|      |
|      |
| if ( |
| grap |
| hics |
|  !=  |
| null |
| )    |
|      |
|      |
|      |
|      |
|      |
| grap |
| hics |
| .dis |
| pose |
| ();  |
|      |
|      |
|      |
| }    |
|      |
|      |
| }    |
|      |
| We   |
| need |
| to   |
| prov |
| ide  |
| simi |
| lar  |
| supp |
| ort  |
| in   |
| the  |
| Rend |
| erCo |
| ntex |
| t    |
| for  |
| the  |
| GC   |
| api  |
| -    |
| some |
| thin |
| g    |
| like |
| the  |
| foll |
| owin |
| g:   |
|      |
| .. c |
| ode: |
| : co |
| de-j |
| ava  |
|      |
|      |
| publ |
| ic v |
| oid  |
| rend |
| er(I |
| Prog |
| ress |
| Moni |
| tor  |
| moni |
| tor) |
|      |
|      |
|      |
|      |
| thro |
| ws R |
| ende |
| rExc |
| epti |
| on { |
|      |
|      |
|      |
| GC g |
| c =  |
| null |
| ;    |
|      |
|      |
|      |
| try  |
| {    |
|      |
|      |
|      |
|      |
| gc = |
|  get |
| Cont |
| ext( |
| ).ge |
| tIma |
| geDa |
| ta() |
| .cre |
| ateG |
| C(); |
|      |
|      |
|      |
|      |
| rend |
| er(g |
| c, g |
| etRe |
| nder |
| Boun |
| ds() |
| , mo |
| nito |
| r);  |
|      |
|      |
|      |
| } fi |
| nall |
| y {  |
|      |
|      |
|      |
|      |
| if ( |
| gc ! |
| = nu |
| ll)  |
|      |
|      |
|      |
|      |
|      |
| gc.d |
| ispo |
| se() |
| ;    |
|      |
|      |
|      |
| }    |
|      |
|      |
| }    |
|      |
| Appr |
| oach |
| ==== |
| ==== |
|      |
| We   |
| are  |
| goin |
| g    |
| to   |
| use  |
| a    |
| simi |
| lar  |
| appr |
| oach |
| to   |
| that |
| used |
| by   |
| the  |
| Rend |
| erEx |
| ecut |
| or;  |
| we   |
| are  |
| goin |
| g    |
| to   |
| crea |
| te   |
| a    |
| Buff |
| ered |
| Imag |
| e    |
| with |
| a    |
| know |
| n    |
| byte |
| orde |
| r;   |
| and  |
| cons |
| truc |
| t    |
| an   |
| SWT  |
| Imag |
| e    |
| arou |
| nd   |
| thes |
| e    |
| byte |
| s    |
| -    |
| pass |
| ing  |
| the  |
| Imag |
| e    |
| GC   |
| into |
| the  |
| abov |
| e    |
| rend |
| er   |
| meth |
| od.  |
|      |
| See  |
| the  |
| foll |
| owin |
| g    |
| meth |
| ods  |
| for  |
| deta |
| ils: |
|      |
| -  A |
| WTSW |
| TIma |
| geUt |
| ils. |
| crea |
| teDe |
| faul |
| tIma |
| ge(d |
| ispl |
| ay,  |
|    w |
| idth |
| ,    |
|    h |
| eigh |
| t);  |
| -  e |
| tc.. |
| .    |
      
+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+

+------------+----------------------------------------------------------+
| |image1|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+------------+----------------------------------------------------------+

.. |image0| image:: images/border/spacer.gif
.. |image1| image:: images/border/spacer.gif
