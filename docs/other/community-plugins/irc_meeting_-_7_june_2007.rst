Irc Meeting - 7 June 2007
#########################

+---------------------------+---------------------------+---------------------------+---------------------------+
| Community Plugins : IRC   |
| Meeting - 7 June 2007     |
| This page last changed on |
| Mar 08, 2010 by jgarnett. |
| | 1) charting             |
| |  2) GPS                 |
| |  3) raster legend       |
| |  4) SoC on trunk        |
| |  5) style on trunk      |
| |  6) color ramp          |
| |  7) udig wps wizard     |
|                           |
| | (10:03) <jgarnet1> 1)   |
| charting                  |
| |  (10:03) <moovida>      |
| Alright, we talked in the |
| list                      |
| |  (10:03) <moovida> but  |
| I would like to know what |
| people think              |
| |  (10:03) <jgarnet1> So  |
| it sounds like we need    |
| **anything** ? A          |
| community module can do   |
| what ever it wants ... I  |
| would actually like to    |
| see a community module    |
| try out BIRT and then     |
| report back with          |
| success/failure ?         |
| |  (10:04) <moovida> that |
| is ok for me, I browsed   |
| through the demos and are |
| impressed                 |
| |  (10:04) <moovida>      |
| heavy start as usual, but |
| then...                   |
| |  (10:04) <moovida> and  |
| doing simple charts       |
| should be not that hard   |
| |  (10:05) <moovida> I    |
| have a doubt about        |
| perfomance for scientific |
| charts                    |
| |  (10:05) <jgarnet1> I   |
| have done an extensive    |
| review of BIRT (for a     |
| customer) and am content  |
| that the technology       |
| works, and that we could  |
| intergrate maps into it   |
| ... but right now they    |
| have only had data        |
| providers based on        |
| javax.sql.DataSource      |
| |  (10:05) <acuster>      |
| sounds great              |
| |  (10:05) <moovida> with |
| lots of values and        |
| timeseries                |
| |  (10:05) <jgarnet1> I   |
| want to look at the       |
| widgets ...               |
| |  (10:05) <jgarnet1> the |
| BIRT project did carve    |
| out a sub project just    |
| for the widgets ... so    |
| perhaps we could get just |
| the charts.               |
| |  (10:06) <moovida> as a |
| start I would say yes     |
| |  (10:06) <moovida> I    |
| will soon need it for a   |
| project so I will be able |
| to report back            |
| |  (10:07) <moovida> will |
| the needed plugins then   |
| end in the sdk?           |
| |  (10:07) <jgarnet1>     |
| http://www.eclipse.org/ne |
| bula/                     |
| |  (10:08) <jgarnet1>     |
| (darn no chart)           |
| |  (10:08) <chorner>      |
| hmm...                    |
| |  (10:08) <acuster>      |
| moovida, why not make a   |
| separte plugin just for   |
| the charting stuff?       |
| |  (10:08) <moovida> what |
| do you mean? bundling the |
| stuff into a plugin?      |
| |  (10:08) <acuster> so   |
| it wouldn't necessarily   |
| be in uDig-core           |
| |  (10:09) <acuster> but  |
| no in uDig-grass either   |
| |  (10:09) <acuster> but  |
| not                       |
| |  (10:09) <acuster> so   |
| any projects could use it |
| |  (10:09) <acuster>      |
| plugin is the wrong word  |
| |  (10:09) <acuster> the  |
| rcp unit that can be      |
| added into the app as a   |
| separte entity            |
| |  (10:11) <chorner> i    |
| think it only makes it    |
| into the sdk if it's core |
| |  (10:11) \* moovida is  |
| sorry but doesn't         |
| understand yet            |
| |  (10:11) <chorner> for  |
| the moment, you'd have to |
| drag the dependencies in  |
| on your own               |
| |  (10:11) <chorner> but  |
| they should have a handy  |
| update site               |
| |  (10:11) <jgarnet1> We  |
| could package up a an     |
| optional "charting        |
| feature" based on a       |
| community module you put  |
| together moovida.         |
| |  (10:12) <acuster>      |
| uDig-grass can plug into  |
| uDig-core, right?         |
| |  (10:12) <jgarnet1> the |
| feature would list your   |
| plugin; and maybe some    |
| BIRT plugins?             |
| |  (10:12) <acuster> so   |
| uDig-chart could be the   |
| same                      |
| |  (10:12) <jgarnet1> and |
| we could include it in    |
| the SDK                   |
| |  (10:12) <acuster> and  |
| uDig-grass depends on     |
| uDig-core plus uDig-grass |
| |  (10:12) <chorner> that |
| all sounds correct        |
| |  (10:12) <acuster> or,  |
| if it's small, it could   |
| be in uDig-core directly  |
| |  (10:13) <moovida>      |
| sounds good to me         |
| |  (10:13) <jgarnet1>     |
| (the docs from here look  |
| very good -               |
| http://www.eclipse.org/tp |
| tp/platform/documents/des |
| ign/ChartingService/conce |
| pts/ctpchrtov.html        |
| )                         |
| |  (10:14) <jgarnet1>     |
| okay so is that a         |
| direction then? Moovida   |
| you can start the         |
| plugin/experiment ... and |
| we can communicate on the |
| udig-devel list?          |
| |  (10:14) <moovida> I    |
| will do some tests and    |
| then we can talk again    |
| |  (10:14) <moovida>      |
| exactly                   |
| |  (10:14) <jgarnet1>     |
| sweet                     |
| |  (10:14) <jgarnet1> 2)  |
| GPS                       |
| |  (10:14) <moovida> ok   |
| |  (10:14) <jgarnet1>     |
| rgould you had some       |
| exposure to this via a    |
| SoC project last year did |
| you not?                  |
| |  (10:14) <chorner> that |
| was me                    |
| |  (10:15) <moovida> I    |
| need a GPS plugin that    |
| takes position and        |
| highlights a layer as I   |
| wander through the fields |
| with a table pc           |
| |  (10:15) <moovida> not  |
| just from file            |
| |  (10:15) <moovida>      |
| which I think was the     |
| thing last year           |
| |  (10:15) <moovida>      |
| right?                    |
| |  (10:15) <chorner>      |
| correct                   |
| |  (10:15) <moovida>      |
| alright                   |
| |  (10:15) <moovida> NMEA |
| is not a problem          |
| |  (10:15) <moovida> My   |
| problem is the platform   |
| dependency                |
| |  (10:16) <moovida> I    |
| have some code i used on  |
| an Axim                   |
| |  (10:16) <moovida> so   |
| it would be ok for        |
| windows                   |
| |  (10:16) <moovida> but  |
| the communication api is  |
| not the same for linux    |
| |  (10:16) <moovida>      |
| there is a port tho       |
| |  (10:16) <moovida> but  |
| I don't know for Mac      |
| |  (10:16) <chorner> you  |
| can make a release of     |
| your plugin for each OS   |
| it is supported on        |
| |  (10:16) <moovida> The  |
| other way would be (first |
| was via bluetooth)        |
| |  (10:17) <moovida> have |
| a daemon running and      |
| access it via TCP         |
| |  (10:17) <chorner>      |
| (perhaps they would be    |
| seperate plugins)         |
| |  (10:17) <moovida>      |
| hmmm... I have one big    |
| problem |image9|          |
| |  (10:18) <moovida> I    |
| develop on linux...       |
| |  (10:18) <moovida>      |
| developing out of one's   |
| environment is always     |
| pain                      |
| |  (10:18) <moovida> so I |
| was searching for a       |
| portable way              |
| |  (10:18) <moovida>      |
| anyone experiences to     |
| share?                    |
| |  (10:19) <chorner> i    |
| don't think we've tackled |
| this before               |
| |  (10:19) <jgarnet1> heh |
| I am a java monkey        |
| |  (10:19) <jgarnet1> and |
| do not own a GPS myself.  |
| |  (10:20) <moovida>      |
| |image10|                 |
| |  (10:20) <chorner> yes  |
| – requiring hardware      |
| complicates things        |
| |  (10:20) <moovida> yes, |
| this is tricky            |
| |  (10:20) <chorner> i    |
| suppose you code blind    |
| and solicit testers       |
| |  (10:21) \* jgarnet1    |
| has quit IRC (Read error: |
| 104 (Connection reset by  |
| peer))                    |
| |  (10:21) \* jgarnett    |
| has joined #udig          |
| |  (10:21) \* jgarnett    |
| has left #udig            |
| |  (10:21) <moovida>      |
| |image11| yeah, I assume  |
| that is what will happen  |
| |  (10:21) \* jgarnett    |
| has joined #udig          |
| |  (10:22) <jgarnett>     |
| back ... bad program was  |
| using 100% cpu            |
| |  (10:22) <chorner>      |
| there are others on the   |
| list interested in GPS... |
| should try provoking them |
| |  (10:22) <chorner>      |
| anything else on this     |
| issue?                    |
| |  (10:22) <moovida> no,  |
| ok for me                 |
| |  (10:22) <chorner> 3)   |
| raster legend             |
| |  (10:22) <moovida>      |
| alright. A summary        |
| |  (10:23) <moovida> we   |
| now support GRASS rasters |
| and workspaces            |
| |  (10:23) <moovida>      |
| color ramps are supported |
| |  (10:23) <moovida> and  |
| categories, which means   |
| that you can label values |
| with strings              |
| |  (10:23) <moovida> so   |
| the legend has names      |
| instead of values         |
| |  (10:23) <moovida>      |
| value ranges in fact      |
| |  (10:23) <moovida> this |
| gives me now a problem    |
| |  (10:24) <moovida> The  |
| legend graphic            |
| |  (10:24) <moovida> It   |
| should be represented     |
| properly                  |
| |  (10:24) <moovida> in   |
| the layer view as well as |
| in the legend graphic box |
| |  (10:25) <moovida>      |
| where can I pass those    |
| graphics?                 |
| |  (10:25) <moovida> I've |
| been looking into the     |
| legend plugin...          |
| |  (10:25) <moovida> but  |
| first:                    |
| |  (10:25) <rgould> the   |
| Legend map graphic only   |
| looks for SLD objects, I  |
| believe                   |
| |  (10:25) <moovida> the  |
| layer view should have a  |
| raster legend like        |
| arcview?                  |
| |  (10:26) <moovida> yes  |
| Richard, that I think is  |
| the problem               |
| |  (10:26) <moovida> and  |
| I do not have SLD objects |
| to give                   |
| |  (10:26) <chorner> i    |
| think you may be breaking |
| our assumptions |image12| |
| |  (10:26) <moovida>      |
| |image13|                 |
| |  (10:26) <moovida> does |
| that mean I better start  |
| to be desperate?          |
| |  (10:27) <moovida> I    |
| really need help on that. |
| |  (10:27) <chorner> it   |
| means we may need to make |
| some changes to udig to   |
| allow more stylie things  |
| besides SLD               |
| |  (10:27) <jgarnett> um  |
| moovida it means you      |
| should start a new view?  |
| |  (10:27) <jgarnett> one |
| that is a legend.         |
| |  (10:28) <jgarnett>     |
| have you tried the legend |
| map graphics?             |
| |  (10:28) <jgarnett> it  |
| does color ramps          |
| |  (10:28) <moovida> i    |
| hoped that would not be   |
| the case...               |
| |  (10:28) <moovida> I    |
| don't feel so fast in     |
| core things yet |image14| |
| |  (10:28) <jgarnett>     |
| thinking                  |
| |  (10:28) <moovida> I    |
| hoped to get a graphics   |
| environment               |
| |  (10:28) <jgarnett> I   |
| appologize for the layers |
| view not being what       |
| everyone always wants -   |
| everyone wants it to be a |
| Legend for the map        |
| |image15|                 |
| |  (10:29) <moovida> and  |
| that there I could draw   |
| |  (10:29) <jgarnett> at  |
| the time we needed it to  |
| actually control the      |
| layers                    |
| |  (10:29) <moovida> no   |
| Jody, that is not a       |
| problem                   |
| |  (10:29) <chorner>      |
| jgarnett: no, we should   |
| make the layers view more |
| extensible                |
| |  (10:29) <moovida> in   |
| my opinion the layer view |
| could simply have the     |
| raster icon               |
| |  (10:29) <jgarnett> I   |
| would rather leave the    |
| layers view alone; and    |
| copy it to work on a      |
| Legend view               |
| |  (10:29) <moovida> but  |
| the same problem is on    |
| the true legend           |
| |  (10:29) <rgould> could |
| add Label/Icon Content    |
| Provider pattern to the   |
| layer. Then the           |
| LegendGraphic/whatever    |
| could just use those to   |
| grab the style info       |
| |  (10:30) <jgarnett> oh  |
| okay ... sorry I must be  |
| getting confused.         |
| |  (10:30) <jgarnett>     |
| moovida can you try       |
| explaining agan please    |
| |  (10:30) <moovida>      |
| alright                   |
| |  (10:30) <moovida> to   |
| start two old jgrass      |
| screenshots with the two  |
| cases I have              |
| |  (10:30) <moovida>      |
| color ramp with values:   |
| http://www.hydrologis.com |
| /screenshots/79_spearfish |
| range.png                 |
| |  (10:31) <moovida> map  |
| with categories:          |
| http://www.hydrologis.com |
| /screenshots/80_spearfish |
| cats.png                  |
| |  (10:31) <moovida>      |
| please have a look at     |
| that                      |
| |  (10:32) <moovida>      |
| first let's talk about    |
| the legend                |
| |  (10:32) <moovida> i.e. |
| the box in the map window |
| |  (10:32) \*             |
| bastianschaeffer has quit |
| IRC ("CGI:IRC (Session    |
| timeout)")                |
| |  (10:32) <moovida> i    |
| will have to add there    |
| something similar you can |
| see in the screenshots,   |
| right?                    |
| |  (10:32) <moovida> but  |
| how?                      |
| |  (10:32) <chorner> ah:  |
| a continuous ramp versus  |
| explicit values           |
| |  (10:33) <moovida> yep  |
| |  (10:33) <jgarnett> I   |
| can see them now          |
| |  (10:33) <moovida> I    |
| create the graphics with  |
| the values...             |
| |  (10:33) <chorner>      |
| you'll need to make your  |
| own map graphic legend,   |
| for rasters               |
| |  (10:33) <moovida> and  |
| then? How can I get that  |
| on the legend box?        |
| |  (10:33) <chorner>      |
| perhaps?                  |
| |  (10:34) <jgarnett>     |
| thinking                  |
| |  (10:34) <rgould>       |
| either make your own      |
| MapGraphic, or            |
| extend/alter the          |
| LegendGraphic             |
| |  (10:34) <jgarnett>     |
| there is one method that  |
| takes an Style Blackboard |
| |  (10:34) <chorner> or   |
| the current legend        |
| graphic could be tweaked  |
| |  (10:34) <jgarnett> and |
| makes a glyph (ie icon)   |
| out of it                 |
| |  (10:34) <jgarnett>     |
| right now it only takes   |
| the SLD off the black     |
| board and starts trying   |
| to guess what looks good  |
| |  (10:34) <jgarnett> let |
| me find the code and      |
| point you in the right    |
| direction                 |
| |  (10:34) <jgarnett> I   |
| will send email           |
| |  (10:35) <moovida>      |
| about adding an own       |
| graphics for rasters,     |
| |  (10:35) <chorner> it   |
| would be nice if we could |
| combine the raster        |
| symbolization for both    |
| grass and sld             |
| |  (10:35) <moovida> I    |
| guess people are used to  |
| have all together         |
| |  (10:35) <chorner>      |
| since they should only    |
| vary a little bit (input  |
| format)                   |
| |  (10:35) \*             |
| bastianschaeffer has      |
| joined #udig              |
| |  (10:36) <moovida>      |
| Cory: what do you mean?   |
| press GRASS into sld?     |
| |  (10:36) <moovida> or   |
| just the appereance       |
| |  (10:36) <moovida> ?    |
| |  (10:36) <chorner> we   |
| haven't solved the color  |
| ramp legend problem for   |
| sld                       |
| |  (10:36) <chorner> this |
| is why we don't have an   |
| easy solution right now   |
| |  (10:37) <moovida> I    |
| see...                    |
| |  (10:37) <moovida>      |
| since the legend should   |
| first have the feature    |
| legends                   |
| |  (10:37) <moovida> and  |
| then the rasters          |
| |  (10:38) <chorner> i    |
| don't mean convert grass  |
| into sld                  |
| |  (10:38) <moovida>      |
| isn't there a way to      |
| supply the graphics and   |
| let other formats draw on |
| it?                       |
| |  (10:38) \* moovida     |
| knows it is dirty, but    |
| just to give an idea      |
| |  (10:38) <jgarnett>     |
| LayerGeneratedFlphyDecora |
| tor.generateDefaultIcon(  |
| layer )                   |
| |  (10:39) <jgarnett>     |
| this supplies the         |
| graphic; some of the      |
| decorators will add       |
| status information over   |
| top                       |
| |  (10:39) <moovida>      |
| Jody: in which plugin?    |
| |  (10:39) <chorner>      |
| hmm... maybe we should    |
| think about this more     |
| |  (10:39) <jgarnett>     |
| net.refractions.udig.proj |
| ect.ui                    |
| |  (10:39) <jgarnett> it  |
| **only** makes a default  |
| icon                      |
| |  (10:39) <chorner>      |
| maybe the grass format    |
| could be read into the    |
| SLD colorramp **object**  |
| |  (10:40) <moovida> for  |
| the layer view or for the |
| legend?                   |
| |  (10:40) <moovida>      |
| Cory: could be an idea... |
| but I know 0 about sld    |
| |image16|                 |
| |  (10:40) <jgarnett>     |
| thinking they are both    |
| supposed to use the same  |
| code                      |
| |  (10:40) <jgarnett> um  |
| you can **set** the icon  |
| on a layer                |
| |  (10:40) <jgarnett> and |
| it will be used for both  |
| the legend and the layer  |
| view                      |
| |  (10:41) <jgarnett>     |
| icon is part of our data  |
| model.                    |
| |  (10:41) <moovida>      |
| Jody: that would solve    |
| the situation, right?     |
| |  (10:41) <chorner> i    |
| think we should move      |
| along – still have 4      |
| agenda items              |
| |  (10:41) <jgarnett> 4)  |
| SoC on trunk              |
| |  (10:42) <jgarnett> um  |
| that is about it ...      |
| |  (10:42) <jgarnett> it  |
| would be nice if we could |
| make an SDK from trunk    |
| (so students can get      |
| going faster)             |
| |  (10:42) <jgarnett> but |
| I would also like there   |
| feedback as they follow   |
| the instructions on       |
| building udig ...         |
| |  (10:43) <jgarnett> to  |
| be clear one of the SoC   |
| students is working on    |
| caching (and will need    |
| GeoTools trunk) ... the   |
| 1.1 branch is still using |
| GeoTools 2.2              |
| |  (10:43) <jgarnett> The |
| other one is working on   |
| GeoRSS ... needing the    |
| GML parser available on   |
| geotools trunk            |
| |  (10:43) <chorner>      |
| (udig trunk has jumped to |
| GeoTools 2.4)             |
| |  (10:43) <moovida>      |
| yes?!?!? really?!?!       |
| |  (10:44) <jgarnett> has |
| been there for a while.   |
| GeoTools 2.4 is supposed  |
| to be released real soon  |
| now (depending on         |
| GeoServer so your milage  |
| may differ - they keep    |
| fixing bugs)              |
| |  (10:44) <chorner> the  |
| rest of you are using     |
| udig 1.1.x for plugin     |
| development, correct?     |
| |  (10:44) <jgarnett>     |
| There is one known        |
| problem with udig trunk   |
| ... we need to fix the    |
| Style editor.             |
| |  (10:45) <moovida>      |
| ahhh, not the 1.1.x,      |
| sorry                     |
| |  (10:45) <jgarnett>     |
| (well I am not .. my tile |
| service reader only works |
| on trunk)                 |
| |  (10:45) <chorner> i    |
| will fix the style editor |
| when i have some free     |
| time                      |
| |  (10:45) <chorner>      |
| hopefully in a couple     |
| weekends                  |
| |  (10:47) <chorner> ok – |
| sounds good?              |
| |  (10:47) <chorner> i    |
| will do a quick perusal   |
| of trunk and make sure    |
| the latest commits from   |
| 1.1.x have been ported    |
| forward                   |
| |  (10:49) <chorner> next |
| |  (10:49) <chorner> 6)   |
| color ramp                |
| |  (10:49) <moovida>      |
| already discussed I guess |
| |  (10:50) <moovida> it   |
| is there and waits for    |
| legend                    |
| |  (10:50) <moovida>      |
| |image17|                 |
| |  (10:50) <chorner> this |
| is acuster                |
| |  (10:50) <chorner> ?    |
| |  (10:51) <moovida>      |
| fainted beaten by         |
| sickness?                 |
| |  (10:51) <acuster>      |
| yeah,move on              |
| |  (10:52) <acuster> it   |
| was not a topic           |
| |  (10:52) <chorner>      |
| ok... i think we're in    |
| agreement that the color  |
| ramp needs love           |
| |  (10:52) <chorner> 7)   |
| udig wps wizard           |
| |  (10:52) <jgarnett>     |
| (sent email on that       |
| topic)                    |
| |  (10:52) <chorner>      |
| bastian, theodor?         |
| |  (10:52) <Theodor> yes  |
| |  (10:52) <Theodor> we   |
| want to migrate our       |
| changes to udig           |
| |  (10:52) <jgarnett> So  |
| as far as I know this is  |
| a community plugin - that |
| would like a code review  |
| and being folded into the |
| uDig application?         |
| |  (10:52) <jgarnett>     |
| sweet.                    |
| |  (10:53) \* moovida is  |
| happy                     |
| |  (10:53)                |
| <bastianschaeffer> ko     |
| |  (10:53)                |
| <bastianschaeffer> ok     |
| |  (10:53) <jgarnett> So  |
| the usual stuff ... the   |
| (c) gets changed at the   |
| top of the file ... we do |
| a code review ... we ask  |
| for some user             |
| documentation on it etc.  |
| |  (10:53) <Theodor> yes, |
| but the most important    |
| is, that the changes we   |
| applied to the udig       |
| wizard page classes goes  |
| into udig                 |
| |  (10:53) <jgarnett> all |
| of that sounds cool?      |
| |  (10:54) <jgarnett> We  |
| are running out of time   |
| for this meeting          |
| |  (10:54) <chorner> 1.   |
| code review 2. docs 3.    |
| add to 1.1.x 4. port to   |
| trunk                     |
| |  (10:54)                |
| <bastianschaeffer> but we |
| had to make some changes  |
| to                        |
| net.refractions.udig.cata |
| log                       |
| |  (10:54) <jgarnett> can |
| we ...                    |
| |  (10:54) <rgould> we    |
| require copyright         |
| changing before           |
| integrating into udig?    |
| |  (10:54) <moovida> what |
| is the usual stuff?       |
| |  (10:54) <jgarnett> Can |
| we get the docs, code and |
| people into the same      |
| place ... and then call a |
| Breakout IRC to do the    |
| review                    |
| |  (10:55) <jgarnett> the |
| usual stuff is as listed  |
| by chorner, 1. code       |
| review (including         |
| headers, javadocs) 2.     |
| docs (user guide, update  |
| developer tutorials if    |
| needed) 3. actually add   |
| the code to udig (1.1.x   |
| branch and trunk)         |
| |  (10:56) <jgarnett>     |
| bastianshaeffer - it      |
| could be that you are     |
| ready? Or do you need a   |
| bit of time to prepair    |
| documentation?            |
| |  (10:56) <moovida> that |
| can all be done on        |
| several ways... deeper    |
| guidelines?               |
| |  (10:56) <rgould> How   |
| big are the changes to    |
| udig.catalog?             |
| |  (10:56)                |
| <bastianschaeffer> well,  |
| we need to add some java  |
| docs...                   |
| |  (10:56) <jgarnett> the |
| programmers guide has the |
| guidelines                |
| |  (10:57)                |
| <bastianschaeffer> the    |
| changes are minimal, 2 or |
| 3 method had to be        |
| overwritten from          |
| superclasses              |
| |  (10:57) <jgarnett>     |
| mostly I care about       |
| keeping it easy -         |
| "sensible defaults" and   |
| so on.                    |
| |  (10:57) <jgarnett>     |
| sounds good.              |
| |  (10:57) <chorner> what |
| license is the code       |
| under? LGPL or GPL?       |
| |  (10:57) <Theodor> GPL  |
| |  (10:57) <chorner>      |
| could we convince you to  |
| go LGPL?                  |
| |  (10:57) <chorner> GPL  |
| will reduce its mileage   |
| |  (10:57) <Theodor> it   |
| is part of the 52north    |
| incubator                 |
| |  (10:58) <chorner> (it  |
| may not get included in   |
| usual udig release)       |
| |  (10:58) <jgarnett>     |
| good point chorner; but   |
| let me be a bit more      |
| clear                     |
| |  (10:58) <Theodor> it   |
| is only about the changes |
| in the udig.catalog       |
| stuff... not the wps      |
| client plug-in            |
| |  (10:58) <rgould> for   |
| the changes to            |
| udig.catalog, you could   |
| just submit patches and   |
| one of us can review and  |
| apply them                |
| |  (10:59) <jgarnett> For |
| the udig application (to  |
| show users what we have   |
| going on) we can include  |
| LGPL and GPL plugins etc  |
| ... even stupid oracle    |
| jars                      |
| |  (10:59) <Theodor> ok,  |
| how do we send these      |
| patches?                  |
| |  (10:59) <chorner> yes: |
| i am talking strictly     |
| about the wps plugin re:  |
| license                   |
| |  (10:59) <jgarnett> but |
| for the core SDK we want  |
| to keep it LGPL (or we    |
| scare people away)        |
| |  (10:59) <jgarnett>     |
| would GPL+classpath       |
| exception work for you    |
| BTW?                      |
| |  (10:59) <rgould>       |
| either create a JIRA task |
| and attach them there, or |
| send them to udig-devel   |
| |  (10:59) <Theodor> yes, |
| for the wps client plug   |
| in we will stick to GPL.  |
| |  (11:00) <jgarnett>     |
| Theodor after we have     |
| done a code review and    |
| are "happy" we do welcome |
| you to join the udig team |
| and hack away on the      |
| core.                     |
| |  (11:00) <Theodor>      |
| Jesse created a JIRA      |
| issue for that already    |
| |  (11:00) <chorner>      |
| ok... i think we'll       |
| either have a seperate    |
| download with all the GPL |
| plugins added, or send    |
| people off to an update   |
| site after uDig installed |
| to get the GPL plugins    |
| |  (11:00) <rgould>       |
| Theodor: which one? (have |
| the link ready?)          |
| |  (11:01) <jgarnett>     |
| guys I am going to have   |
| to call time on this      |
| meeting :-D               |
| |  (11:01) <Theodor> for  |
| the wizard problem (the   |
| udig.catalog patch)       |
| |  (11:01) <jgarnett> (be |
| quick ...) the chat will  |
| still be open ... but I   |
| gotta go back to work     |
| |  (11:02) <Theodor> Ok,  |
| we will attach the code   |
| to the JIRA I guess...    |
| |  (11:02) <rgould>       |
| anyone volunteer to       |
| review the patches to     |
| udig.catalog?             |
| |  (11:02) <rgould> (if   |
| not I will try to)        |
| |  (11:03) <jgarnett> I   |
| will                      |
| |  (11:03) <Theodor> ok,  |
| great thanks. we will     |
| mail you within the next  |
| days                      |
| |  (11:03) <jgarnett>     |
| Sweet                     |
| |  (11:04) <jgarnett>     |
| thanks everyone for a     |
| productive / busy /       |
| active meeting            |
| |  (11:04) <jgarnett> (um |
| since I was kicked out    |
| someone else will need to |
| post the logs)            |
| |  (11:04) <moovida>      |
| thanks, been a pleasure   |
| |  (11:04) <chorner> ok   |
+---------------------------+---------------------------+---------------------------+---------------------------+

+-------------+----------------------------------------------------------+
| |image19|   | Document generated by Confluence on Aug 11, 2014 12:24   |
+-------------+----------------------------------------------------------+

.. |image0| image:: images/icons/emoticons/smile.gif
.. |image1| image:: images/icons/emoticons/smile.gif
.. |image2| image:: images/icons/emoticons/smile.gif
.. |image3| image:: images/icons/emoticons/tongue.gif
.. |image4| image:: images/icons/emoticons/smile.gif
.. |image5| image:: images/icons/emoticons/smile.gif
.. |image6| image:: images/icons/emoticons/sad.gif
.. |image7| image:: images/icons/emoticons/sad.gif
.. |image8| image:: images/icons/emoticons/smile.gif
.. |image9| image:: images/icons/emoticons/smile.gif
.. |image10| image:: images/icons/emoticons/smile.gif
.. |image11| image:: images/icons/emoticons/smile.gif
.. |image12| image:: images/icons/emoticons/tongue.gif
.. |image13| image:: images/icons/emoticons/smile.gif
.. |image14| image:: images/icons/emoticons/smile.gif
.. |image15| image:: images/icons/emoticons/sad.gif
.. |image16| image:: images/icons/emoticons/sad.gif
.. |image17| image:: images/icons/emoticons/smile.gif
.. |image18| image:: images/border/spacer.gif
.. |image19| image:: images/border/spacer.gif
