Irc Meeting - 21 June 2007
##########################

+---------------------------+---------------------------+---------------------------+---------------------------+
| Community Plugins : IRC   |
| Meeting - 21 June 2007    |
| This page last changed on |
| Mar 08, 2010 by jgarnett. |
| | 1) JGrass on trunk      |
| |  2) docs!               |
| |  3) breakout hack       |
| session                   |
| |  4) native code example |
|                           |
| | (10:01) <chorner> 1)    |
| JGrass on trunk           |
| |  (10:01) <jgarnett> oh  |
| can we do one more...     |
| |  (10:01) <moovida> ok,  |
| did you see my mails on   |
| the list?                 |
| |  (10:02) <chorner> yes  |
| |  (10:02) <jgarnett> I   |
| did; it made a large      |
| amount of sense ...       |
| |  (10:02) <moovida> is   |
| this a migration issue    |
| yet?                      |
| |  (10:02) <moovida> or   |
| am I doing something      |
| wrong?                    |
| |  (10:02) <jgarnett>     |
| (and I would like to      |
| udate my silly picture to |
| reflect that plan - if we |
| all think it is a good    |
| idea?)                    |
| |  (10:03) <moovida> yes, |
| right                     |
| |  (10:03) <moovida> do   |
| we think it's a good      |
| idea?                     |
| |  (10:03) <moovida>      |
| |image19|                 |
| |  (10:03) <chorner>      |
| pages of interest for     |
| those on trunk:           |
| |  (10:03) <chorner>      |
| http://docs.codehaus.org/ |
| display/GEOTOOLS/Upgrade+ |
| to+2.4                    |
| |  (10:03) <chorner>      |
| http://udig.refractions.n |
| et/confluence/display/UDI |
| G/GeoTools+2.4+for+uDig+D |
| evelopers                 |
| |  (10:04) <chorner>      |
| c'mon confluence... load  |
| that page – you can do it |
| |  (10:04) <moovida>      |
| |image20|                 |
| |  (10:04) <moovida> very |
| nice page (the second is  |
| loading)                  |
| |  (10:05) <jgarnett>     |
| yeah people are reading   |
| them                      |
| |  (10:05) <moovida> I    |
| believe it |image21| it   |
| the bible at the moment,  |
| I guess                   |
| |  (10:06) <moovida>      |
| but... are the issues I   |
| have also in there (in    |
| the page that doesn't     |
| load)                     |
| |  (10:06) <moovida> ?    |
| |  (10:07) <chorner>      |
| website seems to be down  |
| |image22|                 |
| |  (10:08) <chorner> some |
| are not there             |
| |  (10:08) <chorner> we   |
| will investigate them     |
| tonight in our hack       |
| session                   |
| |  (10:08) <moovida>      |
| alright                   |
| |  (10:09) \* moovida     |
| wondering what the hack   |
| session is                |
| |  (10:09) <chorner>      |
| unpaid hacking on udig    |
| after work                |
| |  (10:09) <moovida>      |
| |image23| ahhh, the dream |
| of every developer... I   |
| like those                |
| |  (10:10) <moovida>      |
| alright, back to the      |
| problems                  |
| |  (10:10) <chorner> your |
| upgrade to trunk might be |
| a little bumpy            |
| |  (10:10) <moovida> why  |
| that                      |
| |  (10:10) <chorner> i    |
| believe everything should |
| compile though            |
| |  (10:10) <moovida> I    |
| miss the FactoryFinder    |
| |  (10:11) <jgarnett>     |
| which one?                |
| |  (10:11) <chorner> not  |
| many people are           |
| developing against it,    |
| since we've been focusing |
| on cleaning up 1.1.x      |
| |  (10:11) <moovida>      |
| Severity and Description  |
| Path Resource Location    |
| Creation Time Id          |
| |  (10:11) <moovida> The  |
| import                    |
| org.geotools.referencing. |
| FactoryFinder             |
| cannot be resolved        |
| net.refractions.udig.proj |
| ect/src/net/refractions/u |
| dig/project/internal/impl |
| AbstractContextImpl.java  |
| line 37 1182403518933     |
| 13546                     |
| |  (10:11) <moovida>      |
| |  (10:11) <jgarnett>     |
| part of the problem is we |
| "skipped" a release (ie   |
| 2.3) so many things that  |
| we should of had lots of  |
| warning (ie deprecations) |
| to change                 |
| |  (10:12) <jgarnett> are |
| hitting us harder (since  |
| they are now gone, along  |
| with the exact advice on  |
| how to switch in the form |
| of the deprecated         |
| message)                  |
| |  (10:12) <moovida> I    |
| have really few problems  |
| that require adapting     |
| |  (10:12) <jgarnett>     |
| FactoryFinder ...         |
| thinking.                 |
| |  (10:12) <chorner>      |
| FactoryFinder should be   |
| covered by that geotools  |
| upgrade page              |
| |  (10:12) <jgarnett>     |
| ReferencingFactoryFinder  |
| ?                         |
| |  (10:12) <moovida> I    |
| just have those three     |
| errors in the udig code   |
| that I can't solve        |
| |  (10:12) <moovida>      |
| org.geotools.referencing. |
| FactoryFinder             |
| |  (10:12) <jgarnett> I   |
| have not put              |
| ReferencingFactoryFinder  |
| on their yet              |
| |  (10:13) <jgarnett>     |
| (but if people are using  |
| it I can ...)             |
| |  (10:13) <moovida> hold |
| on a second, I think I    |
| miss something            |
| |  (10:14) <moovida> I    |
| checked the code out and  |
| did whatever i usually do |
| to set up the source      |
| environment in eclipse    |
| |  (10:14) <moovida>      |
| those few errors should   |
| appear to everyone,       |
| right?                    |
| |  (10:14) <moovida> and  |
| udig should not be able   |
| to start from source      |
| |  (10:14) <moovida> is   |
| that true or am I going   |
| slowly mad?               |
| |  (10:15) <moovida>      |
| (just to be sure we are   |
| talking about the same    |
| thing)                    |
| |  (10:15) <jgarnett>     |
| thinking                  |
| |  (10:15) <jgarnett> I   |
| wonder if we need to      |
| deploy geotools? chorner? |
| |  (10:16) <chorner>      |
| richard did it yesterday  |
| |  (10:16) <jgarnett> did |
| he deploy trunk?          |
| |  (10:16) <moovida>      |
| nothing changed from      |
| yesterday night           |
| |  (10:16) <moovida> had  |
| the same problems this    |
| morning                   |
| |  (10:16) <jgarnett> let |
| me back up ... moovida    |
| are you having this       |
| problem on trunk? or on   |
| 1.1.x ?                   |
| |  (10:16) <jgarnett>     |
| (problem = missing        |
| factory finder)           |
| |  (10:17) <moovida> also |
| this urlToFile is not     |
| there                     |
| |  (10:17) <moovida> I'm  |
| talking about trunk       |
| |  (10:17) <moovida>      |
| 1.1.x is ok for me        |
| |  (10:17) <chorner> i    |
| see                       |
| gt2-main-2.4-SNAPSHOT.jar |
| was updated last night at |
| 10 PM                     |
| |  (10:17) <chorner> so   |
| richard did deploy        |
| |  (10:18) <chorner> to   |
| get the new jars, you'll  |
| have to clean the udig    |
| libs plugin               |
| |  (10:18) <moovida>      |
| urlToFile breaks in the   |
| geotools, where it can't  |
| find it in the            |
| DataUtilities             |
| |  (10:18) <chorner>      |
| |image24| udig website is |
| back                      |
| |  (10:18) <moovida> I    |
| did it at least 6 times   |
| |  (10:19) <chorner>      |
| ok... something may have  |
| broken                    |
| |  (10:19) <chorner> it   |
| might be best to wait for |
| us to have a look         |
| tonight, and in the       |
| meantime use 1.1.x        |
| |  (10:19) <rgould> i     |
| deployed 2.2.x and 2.4.x  |
| yesterday                 |
| |  (10:19) <moovida> I do |
| clean projects            |
| |  (10:19) <moovida>      |
| refresh libs              |
| |  (10:19) <moovida> and  |
| it downloads the gt2      |
| |  (10:20) <moovida>      |
| after refresh and update  |
| classpath                 |
| |  (10:20) <jgarnett> Ah  |
| so you did deploy 2.4.x   |
| yesterday; I think udig   |
| was still using the       |
| FactoryFinder as moovida  |
| described - we need to    |
| change it to              |
| ReferencingFactoryFinder  |
| (I was not going to       |
| deploy geotools until I   |
| had uDig sorted out)      |
| |  (10:20) <moovida> the  |
| problem is still there    |
| |  (10:20) <jgarnett> So  |
| you are not insane        |
| moovida - just faster     |
| than me.                  |
| |  (10:20) <moovida>      |
| thanks jgarnett           |
| |  (10:21) <moovida> but  |
| what about the urlToFile? |
| |  (10:21) <jgarnett>     |
| Back to richard on that   |
| |  (10:21) <moovida> that |
| is missing in the         |
| geotools part, so the     |
| libs seem to be out of    |
| sync                      |
| |  (10:21) <rgould> aha,  |
| so apparently udig was    |
| not ready for new         |
| geotools jars |image25|   |
| |  (10:22) <rgould> i     |
| will set up a trunk       |
| workspace quickly and     |
| look at the problem       |
| |  (10:22) <moovida> that |
| would be great, great     |
| rgould                    |
| |  (10:22) <rgould>       |
| shouldn't take long       |
| |  (10:22) <rgould> i'm   |
| just breaking everything  |
| with these fixes          |
| |image26|                 |
| |  (10:23) <moovida> what |
| do you mean?              |
| |  (10:23) <rgould> i     |
| broke geotools too        |
| |  (10:23) <moovida>      |
| |image27|                 |
| |  (10:24) <jgarnett> go  |
| richard!                  |
| |  (10:24) <jgarnett> (I  |
| won't say where ...)      |
| |  (10:24) <rgould> haha  |
| |  (10:24) <jgarnett>     |
| okay so we are stuck on   |
| some details here         |
| |  (10:24) <jgarnett>     |
| jgrass on trunk is a good |
| idea                      |
| |  (10:24) <moovida> glad |
| to hear that              |
| |  (10:25) <jgarnett> I   |
| want the style editor     |
| fixed on trunk (that is   |
| what I was waiting for    |
| before making the         |
| recomendation - and it is |
| the subject of tonights   |
| hacking session).         |
| |  (10:25) <jgarnett> We  |
| may ask you guys for a    |
| code review tomorrow      |
| morning? If that is okay  |
| ...                       |
| |  (10:25) <moovida>      |
| whatever that means, yes, |
| sure |image28|            |
| |  (10:26) <jgarnett> it  |
| means update trunk, and   |
| laugh at us on email if   |
| we forgot to do our       |
| javadocs. |image29|       |
| |  (10:26) <jgarnett>     |
| Running the style editor  |
| would also be nice.       |
| |  (10:27) <moovida> yes, |
| absolutely                |
| |  (10:27) <jgarnett> I   |
| think if we can get this; |
| then JGrass is good to go |
| for trunk. We may have a  |
| few more bugs (uDig is    |
| almost two years behind   |
| geotools trunk right      |
| now), but we also have    |
| much nicer performance.   |
| |  (10:27) <jgarnett> 2)  |
| docs!                     |
| |  (10:27) <jgarnett>     |
| This is just quick ..     |
| |  (10:27) <jgarnett>     |
| acuster has been beating  |
| me up (in a cheerful      |
| manner) over the state of |
| documentation ...         |
| |  (10:28) <jgarnett>     |
| http://docs.codehaus.org/ |
| display/GEOTDOC/02+API    |
| |  (10:28) <jgarnett> I   |
| am making a little bit of |
| progress ... you can      |
| click on the arrows under |
| the picture and go back   |
| and forth between the     |
| different geotools        |
| "layers"                  |
| |  (10:28) <chorner> yay  |
| |  (10:28) <jgarnett>     |
| including the "JTS and    |
| GeoAPI" layer             |
| |  (10:28) <chorner>      |
| hehehe... "you are here"  |
| |  (10:28) <jgarnett>     |
| http://docs.codehaus.org/ |
| display/GEOTDOC/01+GeoAPI |
| +and+JTS                  |
| |  (10:29) <moovida> this |
| is very nice |image30|    |
| |  (10:29) <jgarnett>     |
| Well I don't know how     |
| else to explain it        |
| |image31|                 |
| |  (10:29) <jgarnett>     |
| Well pass thanks on to    |
| acuser as well - he has   |
| been very helpful.        |
| |  (10:29) <moovida>      |
| thanks Adrian |image32|   |
| |  (10:32) <moovida>      |
| anything you wanted to    |
| say about that jgarnett?  |
| |  (10:33) <moovida>      |
| anyway also the migration |
| doc is great, thanks      |
| |  (10:33) <jgarnett>     |
| thanks for the reminder,  |
| I will make a note on the |
| "proposal" page to update |
| the migration doc.        |
| |  (10:34) <jgarnett> um  |
| this may or may not be    |
| old new?                  |
| |  (10:34) <jgarnett>     |
| GeoTools has switched to  |
| a formal process for      |
| chaning the api of        |
| anything                  |
| |  (10:34) <jgarnett> so  |
| a proposal page is        |
| created                   |
| |  (10:34) <jgarnett>     |
| that also has the BEFORE  |
| / AFTER section           |
| |  (10:34) <jgarnett>     |
| Once the proposal page is |
| accepted it is moved      |
| under the release being   |
| hacked on                 |
| |  (10:34) <jgarnett>     |
| http://docs.codehaus.org/ |
| display/GEOTOOLS/2.4.x    |
| |  (10:35) <jgarnett> You |
| can see a bunch of pages  |
| there (some of which were |
| proposals)                |
| |  (10:35) <jgarnett>     |
| example:                  |
| http://docs.codehaus.org/ |
| display/GEOTOOLS/DataSour |
| ce+Hint+for+EPSG          |
| |  (10:35) <jgarnett> Has |
| a good BEFORE / AFTER     |
| section for the API       |
| changes                   |
| |  (10:35) <jgarnett> (so |
| in the future - since     |
| geotools is now more      |
| organized) the migration  |
| instructions will be      |
| easier to write.          |
| |  (10:36) <jgarnett> end |
| rant ...                  |
| |  (10:36) <jgarnett> 3)  |
| breakout hack session     |
| |  (10:36) <jgarnett> we  |
| kind of already covered   |
| this                      |
| |  (10:36) <jgarnett>     |
| Some of us are hacking    |
| tonight, since we miss    |
| working on uDig, and      |
| there is so much cool     |
| stuff to get done.        |
| |  (10:36) <jgarnett> If  |
| we can ask for a code     |
| review (or even email     |
| abuse) after it woudl be  |
| great.                    |
| |  (10:36) \* moovida     |
| would like to add one     |
| after this (native code   |
| example)                  |
| |  (10:37) <jgarnett>     |
| that would be interesting |
| |  (10:37) <moovida> yes, |
| but you will hack on      |
| what?                     |
| |  (10:37) <jgarnett>     |
| chorner anything to add?  |
| Basically we are trying   |
| to follow the migration   |
| guide to update the       |
| StyleEditor tonight.      |
| |  (10:37) <moovida> truk |
| or 1.1.x?                 |
| |  (10:37) <jgarnett>     |
| trunk                     |
| |  (10:37) <moovida>      |
| great then                |
| |  (10:38) <chorner>      |
| that's it                 |
| |  (10:38) <moovida>      |
| alright, very short about |
| native code if you mind   |
| |  (10:38) \* aaim1 has   |
| joined #udig              |
| |  (10:38) <aaim1> Hi     |
| udiggers                  |
| |  (10:38) <moovida> I    |
| hoped acuster to be       |
| here...                   |
| |  (10:38) <moovida> hi   |
| aaim1                     |
| |  (10:39) \* aaim1 is    |
| now known as aaime        |
| |  (10:39) <chorner> 4)   |
| native code example       |
| |  (10:39) <moovida> I    |
| made the example we       |
| talked in the last irc    |
| |  (10:39) <moovida> a    |
| plugin that takes a       |
| feature from a layer      |
| |  (10:39) <moovida>      |
| extracts its coordinates  |
| |  (10:39) <moovida> and  |
| prints them in C and      |
| fortran |image33|         |
| |  (10:39) <moovida>      |
| fortran?!?!?!             |
| |  (10:40) <moovida> so a |
| really awful example of   |
| JNI and C2fortran         |
| |  (10:40) <moovida> it   |
| work well, but:           |
| |  (10:41) <moovida> 1) I |
| made makefiles and        |
| everything for linux,     |
| since I'm probably not    |
| able to do it for windows |
| |  (10:41) <jgarnett> wow |
| that is fun!              |
| |  (10:41) <moovida> 2) I |
| did it with swig and am   |
| not able to understand    |
| how to pass               |
| multidimensional          |
| arrays...                 |
| |  (10:41) <moovida> any  |
| swig genious here?        |
| |  (10:41) <moovida>      |
| |image34|                 |
| |  (10:42) <aaime>        |
| moovida, I suggest you    |
| ask on the swig ml, or on |
| the gdal one (they use    |
| swig quite a bit)         |
| |  (10:42) <moovida> I    |
| guess you are absolutely  |
| right,                    |
| |  (10:43) <moovida> but  |
| I'm afraid of talking to  |
| people that handle such   |
| incredible things         |
| |image35|                 |
| |  (10:43) <moovida> so I |
| hoped in someone "nearer" |
| |  (10:43) <aaime> Ah,    |
| just be polite and        |
| thankful and do not fear  |
| anything                  |
| |  (10:43) <jgarnett> um  |
| try #osgeo channel        |
| |  (10:43) <aaime> Oh,    |
| and say you read the      |
| manual, and did not       |
| understood it |image36|   |
| |  (10:43) <jgarnett>     |
| they are spatial people,  |
| and friendly, and some of |
| the gdal people hang out  |
| there                     |
| |  (10:43) <aaime> (so    |
| they cannot tell you      |
| RTFM)                     |
| |  (10:43) <jgarnett>     |
| aaime++                   |
| |  (10:44) <moovida> what |
| is RTFM?                  |
| |  (10:44) <jgarnett>     |
| better do a web search on |
| that one.                 |
| |  (10:44) <aaime> Read   |
| The Fucked Manual         |
| |  (10:44) <moovida>      |
| |image37|                 |
| |  (10:44) <moovida>      |
| great!                    |
| |  (10:44) <moovida> i    |
| read it and that part     |
| says:                     |
| |  (10:44) <moovida> you  |
| better take you r C book  |
| out of the shelf again    |
| |  (10:45) <moovida> if   |
| you want to pass          |
| multi-arrays              |
| |  (10:45) <moovida> as   |
| pointers                  |
| |  (10:45) <aaime> Oh, a  |
| suggestion, read the      |
| manual of the lastest     |
| version, not the stable   |
| one                       |
| |  (10:45) <aaime> it has |
| lots more info            |
| |  (10:45) <moovida> ok,  |
| thanks, I'll check that   |
| out                       |
| |  (10:47) <moovida>      |
| alright, anyone anything  |
| else?                     |
| |  (10:47) <jgarnett>     |
| thanks for the meeting!   |
+---------------------------+---------------------------+---------------------------+---------------------------+

+-------------+----------------------------------------------------------+
| |image39|   | Document generated by Confluence on Aug 11, 2014 12:24   |
+-------------+----------------------------------------------------------+

.. |image0| image:: images/icons/emoticons/smile.gif
.. |image1| image:: images/icons/emoticons/smile.gif
.. |image2| image:: images/icons/emoticons/smile.gif
.. |image3| image:: images/icons/emoticons/sad.gif
.. |image4| image:: images/icons/emoticons/smile.gif
.. |image5| image:: images/icons/emoticons/smile.gif
.. |image6| image:: images/icons/emoticons/smile.gif
.. |image7| image:: images/icons/emoticons/smile.gif
.. |image8| image:: images/icons/emoticons/smile.gif
.. |image9| image:: images/icons/emoticons/smile.gif
.. |image10| image:: images/icons/emoticons/smile.gif
.. |image11| image:: images/icons/emoticons/smile.gif
.. |image12| image:: images/icons/emoticons/sad.gif
.. |image13| image:: images/icons/emoticons/smile.gif
.. |image14| image:: images/icons/emoticons/smile.gif
.. |image15| image:: images/icons/emoticons/biggrin.gif
.. |image16| image:: images/icons/emoticons/smile.gif
.. |image17| image:: images/icons/emoticons/smile.gif
.. |image18| image:: images/icons/emoticons/smile.gif
.. |image19| image:: images/icons/emoticons/smile.gif
.. |image20| image:: images/icons/emoticons/smile.gif
.. |image21| image:: images/icons/emoticons/smile.gif
.. |image22| image:: images/icons/emoticons/sad.gif
.. |image23| image:: images/icons/emoticons/smile.gif
.. |image24| image:: images/icons/emoticons/smile.gif
.. |image25| image:: images/icons/emoticons/smile.gif
.. |image26| image:: images/icons/emoticons/smile.gif
.. |image27| image:: images/icons/emoticons/smile.gif
.. |image28| image:: images/icons/emoticons/smile.gif
.. |image29| image:: images/icons/emoticons/smile.gif
.. |image30| image:: images/icons/emoticons/smile.gif
.. |image31| image:: images/icons/emoticons/sad.gif
.. |image32| image:: images/icons/emoticons/smile.gif
.. |image33| image:: images/icons/emoticons/smile.gif
.. |image34| image:: images/icons/emoticons/biggrin.gif
.. |image35| image:: images/icons/emoticons/smile.gif
.. |image36| image:: images/icons/emoticons/smile.gif
.. |image37| image:: images/icons/emoticons/smile.gif
.. |image38| image:: images/border/spacer.gif
.. |image39| image:: images/border/spacer.gif
