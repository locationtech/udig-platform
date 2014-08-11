Irc Meeting - 22 March 2007
###########################

+---------------------------+---------------------------+---------------------------+---------------------------+
| Community Plugins : IRC   |
| Meeting - 22 March 2007   |
| This page last changed on |
| Mar 08, 2010 by jgarnett. |
| | 1. RC10                 |
| |  2. SDK                 |
| |  3. Next week -1 hour   |
| |  4. Richard             |
| |  5. Console             |
| |  6. Nightly SDK         |
|                           |
| | (11:02) \*              |
| Jesse\_Eichar77 changes   |
| topic to '1) RC10 2) SDK  |
| 3) Next week -1 hour 4)   |
| Richard 5) Console'       |
| |  (11:02)                |
| <Jesse\_Eichar77> ok      |
| |  (11:02) <rgould> :O    |
| |  (11:02)                |
| <Jesse\_Eichar77> haha    |
| |  (11:02)                |
| <Jesse\_Eichar77> ok 1)   |
| |  (11:02) <moovida>      |
| |image31| what's up       |
| Richard?                  |
| |  (11:03) <rgould> i am  |
| apparently ( and I am     |
| still working |image32|   |
| |  (11:03)                |
| <Jesse\_Eichar77> Release |
| is looking good a couple  |
| of minor issues that I    |
| wanted to check out       |
| before making the         |
| release.                  |
| |  (11:03)                |
| <Jesse\_Eichar77> I think |
| they are addressed        |
| Geotiff and a problem     |
| with error reporting.     |
| |  (11:03) \* moovida     |
| just one more 6) sdk      |
| nightly builds            |
| |  (11:03)                |
| <Jesse\_Eichar77> so I    |
| have the next release     |
| ready for packaging.      |
| |  (11:03)                |
| <Jesse\_Eichar77> ok      |
| |  (11:04) \*             |
| Jesse\_Eichar77 changes   |
| topic to '1) RC10 2) SDK  |
| 3) Next week -1 hour 4)   |
| Richard 5) Console 6)     |
| nightly SDK'              |
| |  (11:04)                |
| <Jesse\_Eichar77> I'll    |
| put it out today for      |
| testing and start on the  |
| SDK                       |
| |  (11:05)                |
| <Jesse\_Eichar77> 2) SDK  |
| |  (11:05)                |
| <Jesse\_Eichar77> I'll    |
| start today               |
| |  (11:05) <moovida>      |
| |image33| great!          |
| |  (11:05)                |
| <Jesse\_Eichar77> 3) next |
| week -1 hour              |
| |  (11:05)                |
| <Jesse\_Eichar77>         |
| |image34|                 |
| |  (11:05)                |
| <Jesse\_Eichar77> -1 hour |
| sounds good to me.        |
| |  (11:05) <moovida> is   |
| that ok?                  |
| |  (11:05)                |
| <Jesse\_Eichar77> I'll    |
| update the calendar       |
| |  (11:05) <moovida>      |
| thanks                    |
| |  (11:07) <moovida>      |
| Richard, is four up to    |
| you?                      |
| |  (11:07) <rgould> guess |
| so                        |
| |  (11:07) <rgould> i     |
| have been hacking at udig |
| a bit lately              |
| |  (11:07) <rgould> quick |
| summary of some changes:  |
| |  (11:08) <rgould> added |
| a couple configuration    |
| items (extension points)  |
| for allowing custom       |
| applications to configure |
| aspects of the udig UI    |
| |  (11:08) <rgould>       |
| net.refractions.udig.ui.w |
| orkbenchConfigurations    |
| (configures the           |
| workbench)                |
| |  (11:08) <rgould>       |
| net.refractions.udig.ui.m |
| enuBuilders               |
| (sets up the menu and     |
| coolbar)                  |
| |  (11:08) <rgould> and   |
| net.refractions.udig.proj |
| ect.ui.toolManagers       |
| (specifies a custom       |
| implementation of         |
| toolmanager, so that you  |
| can control the udig      |
| toolbar)                  |
| |  (11:08) <rgould> these |
| are specified in          |
| plugin\_customization.ini |
| |  (11:09) <rgould> I     |
| also added a couple other |
| preferences that can be   |
| controlled from           |
| plugin\_customization.ini |
| |  (11:09) <rgould> such  |
| as display of the tips    |
| dialog                    |
| |  (11:09) <rgould> and   |
| whether maps should be    |
| re-opened on startup      |
| |  (11:09) <rgould> and i |
| fixed the open file       |
| dialog filter on linux    |
| |image35|                 |
| |  (11:10) <rgould>       |
| that's about it           |
| |  (11:10) <moovida> you  |
| mean the open file for    |
| project openings?         |
| |  (11:10) <rgould> right |
| now I am fixing a bug     |
| that has arisen with      |
| respect to editing while  |
| a the editmanager is      |
| locked to a specific      |
| layer                     |
| |  (11:10) <rgould> no,   |
| add layer -> files        |
| |  (11:10) <jgarnett>     |
| docs richard? Even quick  |
| code example ...          |
| |  (11:11) <jgarnett> or  |
| perhaps if that is too    |
| much a sample in svn      |
| examples                  |
| |  (11:11) <rgould> for   |
| what?                     |
| |  (11:12) <rgould> the   |
| new extensions?           |
| |  (11:12) <jgarnett> the |
| extension points above    |
| |  (11:12) <rgould> there |
| are some exmaples in the  |
| docs for the extension    |
| points                    |
| |  (11:12) <jgarnett>     |
| yeah!                     |
| |  (11:12) <rgould> i'll  |
| write more about it later |
| too                       |
| |  (11:12) <rgould> but i |
| need to get this project  |
| finishee                  |
| |  (11:12) <rgould>       |
| finished                  |
| |  (11:13)                |
| <Jesse\_Eichar77> wow     |
| |  (11:13)                |
| <Jesse\_Eichar77> good    |
| job richard. you follow   |
| that moovida?             |
| |  (11:13)                |
| <Jesse\_Eichar77>         |
| |image36|                 |
| |  (11:13) <moovida> yes, |
| I'm looking forward to    |
| try some |image37|        |
| |  (11:14) <moovida> that |
| will help creating better |
| toolbar configs, right?   |
| |  (11:14) <rgould> it's  |
| still not ideal. Some     |
| things feel kind of       |
| hacky, but it is a start  |
| |  (11:14) <rgould> yeah  |
| |  (11:14) <moovida> and  |
| menus and... and          |
| |image38|                 |
| |  (11:14) <rgould> (see  |
| my recent email to Tony   |
| Roth)                     |
| |  (11:14) <rgould>       |
| customizable toolbars     |
| could probably done in    |
| much a nicer manner       |
| |  (11:15) <moovida> that |
| is good, at the point the |
| console is ok, we have to |
| add tonns of stuff which  |
| will need some order      |
| |  (11:15) <rgould> cool  |
| - perhaps you can come up |
| with better ideas on how  |
| to do it                  |
| |  (11:16)                |
| <Jesse\_Eichar77> ok 5)   |
| Console:                  |
| |  (11:16)                |
| <Jesse\_Eichar77>         |
| moovida?                  |
| |  (11:16) <moovida> I'm  |
| here for your questions   |
| |image39|                 |
| |  (11:16) <moovida> or   |
| better where should I     |
| start?                    |
| |  (11:16) <moovida> did  |
| you have time to see my   |
| picture?                  |
| |  (11:17) <moovida> we   |
| decided to keep the       |
| console almost standalone |
| |  (11:17) <moovida> in   |
| udig we will add the      |
| access in 3 ways          |
| |  (11:18) <moovida>      |
| visual console, prompt    |
| console, single gui for   |
| every command             |
| |  (11:18) <moovida> the  |
| editors will support also |
| R syntax embedded in      |
| beanshell syntax          |
| |  (11:18)                |
| <Jesse\_Eichar77> could   |
| you give a brief          |
| description of each of    |
| those consoles?           |
| |  (11:19) <moovida>      |
| sure:                     |
| |  (11:19) <moovida> 1)   |
| visual console: a GEF     |
| based editor in which     |
| from the palette you can  |
| drag                      |
| |  (11:19) <moovida>      |
| models and datatypes      |
| |  (11:19)                |
| <Jesse\_Eichar77> oh      |
| right.                    |
| |  (11:19) <moovida> and  |
| link them together to     |
| read and write from every |
| type of data              |
| |  (11:19) <moovida> and  |
| process it                |
| |  (11:20) <moovida>      |
| there will be a play      |
| button that will launch   |
| the stuff                 |
| |  (11:20) <moovida> and  |
| you can save execution    |
| configs                   |
| |  (11:20) <moovida> 2)   |
| an editor window          |
| |  (11:21) <moovida> that |
| supports syntax highlight |
| for beanshell script,     |
| jgrass (and compliant)    |
| commmands                 |
| |  (11:21) <moovida> and  |
| R scripting               |
| |  (11:21) <moovida> same |
| here we have to push play |
| and the thing runs        |
| |  (11:22) <moovida> 3)   |
| the old nice standard way |
| |  (11:22) <moovida> from |
| a menu or toolbar you     |
| click on an icon, a gui   |
| appears                   |
| |  (11:22) <moovida> and  |
| you can choose input and  |
| output maps and values    |
| and then you push OK      |
| |  (11:22)                |
| <Jesse\_Eichar77> ok      |
| thanks                    |
| |  (11:22) <moovida> and  |
| it executes that thing    |
| |  (11:23) <moovida> yup  |
| |  (11:23) <moovida>      |
| questions?                |
| |  (11:23) <moovida>      |
| comments?                 |
| |  (11:24)                |
| <Jesse\_Eichar77> ok      |
| |  (11:24)                |
| <Jesse\_Eichar77> The     |
| visual context backs onto |
| a open MI model?          |
| |  (11:24) <moovida> all  |
| of them do so             |
| |  (11:24)                |
| <Jesse\_Eichar77> cool.   |
| |  (11:25) <moovida> they |
| all produce a string and  |
| a preprocessor does the   |
| needed creation of openmi |
| compliant stuff           |
| |  (11:25)                |
| <Jesse\_Eichar77> and the |
| open MI implementation is |
| open ended so we can do   |
| operations on features or |
| gridcoverages or ...      |
| |  (11:25)                |
| <Jesse\_Eichar77>         |
| correct?                  |
| |  (11:25) <moovida>      |
| absolutely |image40|      |
| |  (11:26)                |
| <Jesse\_Eichar77> cool    |
| |  (11:26)                |
| <Jesse\_Eichar77> why     |
| beanshell and not         |
| javascript?               |
| |  (11:26) <moovida> the  |
| lower part will base on   |
| JGrass API and geotools   |
| api                       |
| |  (11:26) <moovida>      |
| javascript? What do you   |
| mean?                     |
| |  (11:26)                |
| <Jesse\_Eichar77> the     |
| scripting language for    |
| the prompt console        |
| |  (11:26) <moovida> the  |
| one in jdk 1.6?           |
| |  (11:27) <moovida> it   |
| doesn't seem to be        |
| assured that it will stay |
| there                     |
| |  (11:27)                |
| <Jesse\_Eichar77> Is bean |
| shell part of the sdk?    |
| |  (11:28) <moovida> I    |
| thought it was, but       |
| things are not clear      |
| right now                 |
| |  (11:28) <moovida>      |
| beanshell is not a        |
| mandatory choice tho      |
| |  (11:28)                |
| <Jesse\_Eichar77> just    |
| curious about the choice  |
| that's all                |
| |  (11:28) <moovida> I    |
| want something open for   |
| other scripting languages |
| |  (11:28) <moovida> I    |
| would be glad to hear     |
| ideas                     |
| |  (11:29)                |
| <Jesse\_Eichar77> If java |
| script is available and   |
| easy to integrate I think |
| that would be useful      |
| simply because lots of    |
| people know... Especially |
| thanks to all the web 2.0 |
| hype.                     |
| |  (11:29) <moovida> I    |
| wanted to approach        |
| jhyton, since everyone    |
| seems to love it          |
| |  (11:29)                |
| <Jesse\_Eichar77>         |
| compared to beanshell.... |
| I only know 1 person who  |
| know it.                  |
| |  (11:29) <moovida>      |
| |image41|                 |
| |  (11:30) <moovida> the  |
| javascript bases on the   |
| web kavascript?           |
| |  (11:30) <moovida> kava |
| = java                    |
| |  (11:30)                |
| <Jesse\_Eichar77> the     |
| language is the same just |
| some of the libraries     |
| wouldn't apply to us      |
| |  (11:30) <moovida> I'm  |
| really confused about     |
| those                     |
| |  (11:30)                |
| <Jesse\_Eichar77> I know  |
| it can use java classes   |
| |  (11:31)                |
| <Jesse\_Eichar77> and     |
| java jars                 |
| |  (11:31)                |
| <Jesse\_Eichar77> so we   |
| could make the java       |
| objects                   |
| |  (11:31) <moovida> same |
| as beanshell, right... I  |
| will have a look at it,   |
| but I'm not sure if we    |
| mean the same             |
| |  (11:31) <moovida> do   |
| you have a link to what   |
| you mean?                 |
| |  (11:31)                |
| <Jesse\_Eichar77> Not off |
| the cuff                  |
| |  (11:31)                |
| <Jesse\_Eichar77> I'      |
| |  (11:32)                |
| <Jesse\_Eichar77> I'm not |
| that great at javascript. |
| |  (11:32)                |
| <Jesse\_Eichar77> but I   |
| do know it isn't          |
| restricted to the browser |
| |  (11:32)                |
| <Jesse\_Eichar77> its     |
| just a language like any  |
| other scripting language  |
| |  (11:32)                |
| <Jesse\_Eichar77> But     |
| lets compare the two and  |
| decide the best for the   |
| "main" scripting          |
| language.                 |
| |  (11:32)                |
| <Jesse\_Eichar77> If it   |
| is too hard to integrate  |
| then we may go to the     |
| beanshell route.          |
| |  (11:33)                |
| <Jesse\_Eichar77> but     |
| especially with java 6 it |
| integrates REALLY easily  |
| with java                 |
| |  (11:33) <moovida> yes, |
| it would be nice to hear  |
| someone that has          |
| experiences               |
| |  (11:33)                |
| <Jesse\_Eichar77> as for  |
| java 5 I don't know.      |
| |  (11:33)                |
| <Jesse\_Eichar77> ok lets |
| do some research.         |
| |  (11:33) <aaime> groovy |
| groovy                    |
| |  (11:33) \* aaime goes  |
| back in his dark conern   |
| |  (11:33)                |
| <Jesse\_Eichar77> I love  |
| groovy |image42|          |
| |  (11:33) <moovida>      |
| |image43| I don't know    |
| groovy                    |
| |  (11:33)                |
| <Jesse\_Eichar77> I'd     |
| vote for that |image44|   |
| but I think java script   |
| is better known |image45| |
| |  (11:34)                |
| <Jesse\_Eichar77> ok next |
| |  (11:34)                |
| <Jesse\_Eichar77> 6)      |
| nightly SDK               |
| |  (11:34) <moovida> one  |
| last                      |
| |  (11:34) <moovida>      |
| about console             |
| |  (11:34)                |
| <Jesse\_Eichar77> ok      |
| |  (11:34) <moovida> you  |
| are aware (I have to      |
| repeat it) that we will   |
| use the GRASS workspace   |
| as container for          |
| analyses?                 |
| |  (11:35) <moovida> is   |
| that ok for eveyone?      |
| |  (11:35) <moovida>      |
| speak now or ....         |
| |  (11:35) <moovida> or   |
| never |image46| Great!!   |
| |  (11:35)                |
| <Jesse\_Eichar77> wait!!! |
| |  (11:35)                |
| <Jesse\_Eichar77>         |
| |image47|                 |
| |  (11:36) <moovida>      |
| |image48| ok              |
| |  (11:36)                |
| <Jesse\_Eichar77> what do |
| you mean by that?         |
| |  (11:36)                |
| <Jesse\_Eichar77> all ins |
| and outs go into the      |
| grass workspace?          |
| |  (11:36) <moovida> yes, |
| somehow yes               |
| |  (11:37) <moovida> that |
| means that you will have  |
| to import some data to do |
| processing                |
| |  (11:37)                |
| <Jesse\_Eichar77> why is  |
| that a requirement?       |
| |  (11:38) <moovida>      |
| there are lots of         |
| reasons, which I then     |
| never remember when they  |
| ask me                    |
| |  (11:38) <moovida>      |
| consistency of projection |
| |  (11:38) <moovida>      |
| questions like            |
| |  (11:38) <moovida>      |
| where to write output     |
| maps?                     |
| |  (11:39)                |
| <Jesse\_Eichar77> It      |
| seems to me that there    |
| would be sources and      |
| sinks in the process      |
| chain                     |
| |  (11:39) <moovida> I    |
| can't afford to do lots   |
| of tranforming on maps,   |
| processing is already     |
| slow                      |
| |  (11:39) <moovida> what |
| do you mean by that?      |
| |  (11:40)                |
| <Jesse\_Eichar77> a       |
| source would be at the    |
| start of the chain (the   |
| "main inputs)             |
| |  (11:40)                |
| <Jesse\_Eichar77> Each    |
| operation would verify    |
| its pre-conditions (same  |
| projection)               |
| |  (11:40)                |
| <Jesse\_Eichar77> a sink  |
| is where the data goes at |
| the end.                  |
| |  (11:41)                |
| <Jesse\_Eichar77> That's  |
| the concept OSSIM uses    |
| |  (11:41)                |
| <Jesse\_Eichar77> a sink  |
| could be a screen even    |
| |  (11:41) <moovida>      |
| alright, but that would   |
| make almost impossible to |
| code a decoupled version  |
| of a console              |
| |  (11:41) <moovida> or   |
| better, a nightmare that  |
| gets worse when someone   |
| like me didn't do lot of  |
| work on vector formats    |
| |  (11:42)                |
| <Jesse\_Eichar77> tell    |
| you what. lets go for a   |
| middle ground.            |
| |  (11:42) <moovida> also |
| keeping the gRASS         |
| workspace makes us        |
| exploit everything from   |
| GRASS                     |
| |  (11:42) <moovida> and  |
| that is a lot             |
| |  (11:43) <moovida> yes  |
| Jesse?                    |
| |  (11:43) <moovida> what |
| is the middle ground?     |
| |  (11:43)                |
| <Jesse\_Eichar77> write   |
| it based on the grass     |
| workspace but everytime   |
| you have an source or     |
| sink try to capture that  |
| in an object. Or at least |
| consider migration paths. |
| |  (11:43) <jgarnett>     |
| moovida did my email      |
| about the difference      |
| between workspace and     |
| catalog make sense?       |
| |  (11:43)                |
| <Jesse\_Eichar77> I don't |
| want to make your job     |
| impossible but if we can  |
| leave the door open to    |
| future refactorings so    |
| that it can be done I     |
| think it would be REALLY  |
| beneficial.               |
| |  (11:43) <moovida>      |
| Jody, yes                 |
| |  (11:44) <jgarnett> I   |
| was thinking that a       |
| workspace was similar to  |
| a uDig Map                |
| |  (11:44) <moovida> yes, |
| but the point is that     |
| |  (11:44) <moovida> the  |
| catalog has too much but  |
| not everything            |
| |  (11:44) <jgarnett> The |
| comment a bout a          |
| "Scratch" workspace was   |
| similar to how uDig       |
| creates a new Map when    |
| needed .. you can create  |
| a new workspace when      |
| needed to make things     |
| easy for the user         |
| |  (11:44) <moovida> and  |
| the umap has not          |
| everything                |
| |  (11:45)                |
| <Jesse\_Eichar77> the     |
| umap is higher level not  |
| even part of the catalog  |
| |  (11:45) <jgarnett>     |
| moovida what does the     |
| catalog not have? (It is  |
| not supposed to have      |
| everything)               |
| |  (11:45) <moovida> we   |
| need to be able to work   |
| on maps even if they are  |
| not visible               |
| |  (11:45) <jgarnett> my  |
| guess is the part you are |
| missing is the "Extent"   |
| for which things in the   |
| workspace should be       |
| processed?                |
| |  (11:46) <moovida> what |
| do you mean ?             |
| |  (11:46) <jgarnett>     |
| Jesse\_Eichar77 agreed; I |
| view "workspace" as       |
| higher level not even     |
| part of the catalog as    |
| well                      |
| |  (11:46) <jgarnett> um  |
| can we have another name  |
| then workspace            |
| |  (11:46) <jgarnett>     |
| jworkspace perhaps        |
| |image49|                 |
| |  (11:46)                |
| <Jesse\_Eichar77>         |
| gworkspace                |
| |  (11:46) <jgarnett>     |
| good                      |
| |  (11:46) <moovida>      |
| alright                   |
| |  (11:47) <jgarnett>     |
| moovida my email question |
| - about what makes up a   |
| workspace - is what I am  |
| trying to answer.         |
| |  (11:47) <jgarnett> so  |
| far I am working on this  |
| assumption:               |
| |  (11:47) <jgarnett> -   |
| holds some resources for  |
| analysis                  |
| |  (11:47) <jgarnett> -   |
| has a common "extent" for |
| which those resources can |
| be processed              |
| |  (11:47) <moovida> yes  |
| |  (11:48) <moovida> now  |
| I see extent is what I    |
| call active region?       |
| |  (11:48) <jgarnett> -   |
| does not matter if the    |
| resources are visisble or |
| not (gworkspace is        |
| focused on use rather     |
| then display)             |
| |  (11:48) <jgarnett>     |
| right - active region     |
| |  (11:48) <moovida> yes  |
| |  (11:48) <moovida>      |
| perfect                   |
| |  (11:48) <jgarnett>     |
| that is about it for my   |
| idea of gworkspace        |
| |  (11:48) <jgarnett> am  |
| I missing anything?       |
| |  (11:48) <jgarnett>     |
| cool                      |
| |  (11:49) <moovida> no,  |
| I think that is all if    |
| projection is in extent   |
| |  (11:49) <jgarnett>     |
| since we are making this  |
| up - lets say yes!        |
| |  (11:49) <moovida> yes  |
| |  (11:49) <jgarnett> um  |
| active region is probably |
| a better name             |
| |  (11:49) <jgarnett> So  |
| you were asking about how |
| to make this "easy"       |
| |  (11:50) <jgarnett> if  |
| the user selects the      |
| "Active Region" tool and  |
| draws a box               |
| |  (11:50) <jgarnett> and |
| selects a layer in their  |
| map                       |
| |  (11:50) <jgarnett> and |
| says "analysis>munch      |
| |  (11:50) <jgarnett> you |
| can make a new gworkspace |
| populated with the data   |
| for that layer, and the   |
| active region defined by  |
| the tool - and start      |
| "munch"ing                |
| |  (11:51) <moovida> he   |
| he, but that is tooooo    |
| easy                      |
| |  (11:51) <moovida>      |
| |image50|                 |
| |  (11:51) <jgarnett>     |
| just the same way we make |
| a "new map" when the user |
| does not have one -> and  |
| the user does an "Add     |
| layer"                    |
| |  (11:51) <jgarnett> bah |
| - have fun!               |
| |  (11:51) <moovida> wait |
| Jody                      |
| |  (11:51) <jgarnett> I   |
| am here                   |
| |  (11:51) <moovida> in   |
| your example              |
| |  (11:52) <moovida> what |
| if you do this on a map   |
| that is not in the umap   |
| |  (11:52) <moovida> and  |
| trhat writes a raster     |
| |  (11:52) <moovida> I    |
| mean the munching         |
| |  (11:52) <moovida>      |
| writes a raster as output |
| |  (11:52) <moovida> a    |
| grass raster that needs a |
| grass structure to hold   |
| it                        |
| |  (11:52) <moovida> you  |
| can't force the user      |
| |  (11:53) <moovida> to   |
| choose evertime a folder  |
| |  (11:53) <moovida>      |
| inside a complicated      |
| structure of subfolders   |
| |  (11:53) <moovida> and  |
| let him decide if the     |
| projection is ok          |
| |  (11:53) <moovida> the  |
| user will die             |
| |  (11:53) <jgarnett> I   |
| understand                |
| |  (11:53) <jgarnett>     |
| much produces a result    |
| |  (11:53) <moovida> the  |
| user knows mapnames       |
| |  (11:53) <moovida> that |
| is it                     |
| |  (11:54) <jgarnett>     |
| what do you mean by a map |
| that is not in the umap   |
| |  (11:54) <jgarnett> (do |
| you mean not displayed    |
| right now?)               |
| |  (11:54) <jgarnett> you |
| can write the answer into |
| your gworkspace           |
| |  (11:54) <jgarnett> (it |
| does not need to be       |
| displayed)                |
| |  (11:54) <moovida> yes, |
| exactly, it lives         |
| somewhere                 |
| |  (11:54) <moovida> in   |
| gworkspace in my sight    |
| |  (11:54) <moovida>      |
| somewhere in the world in |
| your sight                |
| |  (11:54) <jgarnett> I   |
| was only talking about    |
| how to make acessing the  |
| gworkspace stuff easy for |
| people to get started.    |
| |  (11:55) <jgarnett>     |
| note the new entry in the |
| gworkspace should have an |
| entry in the catalog as   |
| well                      |
| |  (11:55) <jgarnett> it  |
| may be a temporary entry  |
| |  (11:55) <moovida> yes, |
| the creation of a         |
| gworkspace will be        |
| handled, sure             |
| |  (11:55) <jgarnett>     |
| (not sure if you plan on  |
| saving it or not?)        |
| Depends on the operation  |
| I guess .                 |
| |  (11:55) <moovida> but  |
| the user has to be aware  |
| of it                     |
| |  (11:55) <jgarnett> A   |
| view (the GWorkspace      |
| View) can pop up showing  |
| the newly created item in |
| the folder                |
| |  (11:55) <moovida> yes, |
| depends on operation      |
| |  (11:56) <jgarnett>     |
| Your folder idea; is that |
| a gworkspace?             |
| |  (11:56) <jgarnett> or  |
| does gworkspace have      |
| folders in it             |
| |  (11:56) <moovida>      |
| gworkspace has folders in |
| it                        |
| |  (11:56) <moovida> for  |
| rasters to read and write |
| |  (11:56) <moovida> for  |
| vectors                   |
| |  (11:56) <jgarnett>     |
| (note I recomend "tags"   |
| rather then folders ...   |
| so you can slice and dice |
| a bit more)               |
| |  (11:56) <moovida> the  |
| structure is know to the  |
| environment               |
| |  (11:57) <jgarnett>     |
| okay cool.                |
| |  (11:57)                |
| <Jesse\_Eichar77> So      |
| |  (11:57) <jgarnett> So  |
| your question was "which  |
| folder"                   |
| |  (11:57) <jgarnett> and |
| the udig answer is        |
| "sensible defaults"       |
| |  (11:57)                |
| <Jesse\_Eichar77> from    |
| what I understand the     |
| gworkspace is a kind of a |
| container for the         |
| operations to work in     |
| |  (11:57) <moovida> not  |
| really operations         |
| |  (11:57)                |
| <Jesse\_Eichar77> by      |
| default the operations    |
| will use the gworkspace   |
| container                 |
| |  (11:57) <jgarnett> so  |
| if their was not a        |
| gworkspace we made one    |
| ... can we do the same    |
| thing for a folder?       |
| |  (11:57) <moovida> I    |
| feel operations can be    |
| done on selected items    |
| and maps                  |
| |  (11:58) <moovida> here |
| we are talking about      |
| processing without having |
| data selected or whatever |
| |  (11:58) <moovida> (not |
| by default however)       |
| |  (11:58) <moovida> see  |
| it a bit like if it was   |
| geoserver and not udig    |
| |  (11:58) <moovida> you  |
| start something and it    |
| processes it              |
| |  (11:59)                |
| <Jesse\_Eichar77> but if  |
| it is a interface or      |
| abstract class then we    |
| can define other          |
| containers as well.       |
| |  (11:59)                |
| <Jesse\_Eichar77> no?     |
| |  (11:59) <moovida> yes, |
| we had that in JGrass     |
| |  (11:59) <moovida>      |
| reading and writing in    |
| different formats in      |
| abstract way              |
| |  (12:00) <moovida> so   |
| you are telling me to     |
| define an abstract        |
| gworkspace?               |
| |  (12:01) <moovida> that |
| then can be substituted?  |
| |  (12:01)                |
| <Jesse\_Eichar77> that's  |
| my idea                   |
| |  (12:01)                |
| <Jesse\_Eichar77> I think |
| it satisfies your and my  |
| requirements              |
| |  (12:01) <moovida> that |
| sounds good               |
| |  (12:01)                |
| <Jesse\_Eichar77> ok cool |
| |  (12:01) <moovida> but  |
| it will have to follow    |
| some pattern so that the  |
| objects visit it and      |
| together they do stuff    |
| |  (12:02)                |
| <Jesse\_Eichar77> Yes it  |
| will                      |
| |  (12:02) <moovida> only |
| the abstract workspace is |
| not enough I feel         |
| |  (12:03)                |
| <Jesse\_Eichar77> why     |
| not?                      |
| |  (12:03) <moovida> what |
| would the gwks define?    |
| |  (12:03) <moovida>      |
| paths for map types?      |
| |  (12:03) <moovida>      |
| projection definitions?   |
| |  (12:04) <moovida>      |
| bounds?                   |
| |  (12:04) <moovida>      |
| resolutions?              |
| |  (12:04)                |
| <Jesse\_Eichar77> what    |
| ever you need             |
| |  (12:04)                |
| <Jesse\_Eichar77> to be   |
| able to make the          |
| operations run            |
| |  (12:04) <jgarnett>     |
| Cool so the idea is you   |
| hack quickly and hard;    |
| and then we can come up   |
| with jesse common         |
| interface after ... if I  |
| am on the right track     |
| here?                     |
| |  (12:04)                |
| <Jesse\_Eichar77> Yes     |
| |  (12:04)                |
| <Jesse\_Eichar77> I want  |
| this topic to always be   |
| in consideration as you   |
| create the framework      |
| |  (12:05)                |
| <Jesse\_Eichar77> so you  |
| don't complete paint      |
| yourself into a corner    |
| and it is only useful for |
| jGrass                    |
| |  (12:05)                |
| <Jesse\_Eichar77> as      |
| great as that is I think  |
| it would be brilliant if  |
| we can have it more       |
| general                   |
| |  (12:05) <moovida>      |
| sure, that is also what I |
| do not want               |
| |  (12:05)                |
| <Jesse\_Eichar77>         |
| Consider this             |
| requirement:              |
| |  (12:05)                |
| <Jesse\_Eichar77> A user  |
| want to run some          |
| operations on Geotools    |
| gridcoverage              |
| |  (12:06)                |
| <Jesse\_Eichar77> but     |
| doesn't want JGrass       |
| (maybe the GPL            |
| requirements are too      |
| much)                     |
| |  (12:06)                |
| <Jesse\_Eichar77> we      |
| would want to be able to  |
| take the non-JGrass       |
| operations and the        |
| framework and still have  |
| it work without JGrass    |
| |  (12:06)                |
| <Jesse\_Eichar77> Maybe   |
| there won't be much       |
| functionality at first... |
| |  (12:06)                |
| <Jesse\_Eichar77> that's  |
| fine..                    |
| |  (12:06)                |
| <Jesse\_Eichar77> even if |
| there is only a framework |
| that is still fine        |
| |  (12:07)                |
| <Jesse\_Eichar77>         |
| operation will come if we |
| have the framework.       |
| |  (12:07) <moovida>      |
| alright, but the          |
| framework will be GPL     |
| |  (12:07)                |
| <Jesse\_Eichar77> oh?     |
| |  (12:07)                |
| <Jesse\_Eichar77> any     |
| reason?                   |
| |  (12:07) <moovida> the  |
| license is decided by the |
| Prof that puts the founds |
| |  (12:07) <moovida> he   |
| is GPL fan                |
| |  (12:08)                |
| <Jesse\_Eichar77> I see   |
| |  (12:08) <moovida>      |
| can't do anything to it   |
| |  (12:08)                |
| <Jesse\_Eichar77> that is |
| just an example.          |
| |  (12:08) <moovida> yes, |
| I got what you mean       |
| |  (12:08)                |
| <Jesse\_Eichar77> can it  |
| be the classpath GPL like |
| java?                     |
| |  (12:08) <moovida>      |
| what's that?              |
| |  (12:08)                |
| <Jesse\_Eichar77> Java is |
| being open sourced        |
| |  (12:09) <moovida> yes  |
| |  (12:09)                |
| <Jesse\_Eichar77> but in  |
| order to not require      |
| other libraries to be GPL |
| they use the GPL          |
| classpath license         |
| |  (12:09) <moovida> oh,  |
| I don't know what that    |
| means                     |
| |  (12:09)                |
| <Jesse\_Eichar77> so      |
| basically the code it     |
| self and code that        |
| directly stems from the   |
| code must be GPL          |
| |  (12:10)                |
| <Jesse\_Eichar77> but     |
| code that just links to   |
| it or extends it (with    |
| out any modification to   |
| the core code)            |
| |  (12:10)                |
| <Jesse\_Eichar77> can be  |
| any license               |
| |  (12:10) <moovida>      |
| hmmm, I will have to      |
| ask... honestly I thought |
| that definition is GPL :S |
| |  (12:10) <moovida>      |
| licensing is sooo bad     |
| |image51|                 |
| |  (12:11)                |
| <Jesse\_Eichar77> Not     |
| quite there are some      |
| forms of GPL that is very |
| viral and requires any    |
| other code that links to  |
| it to be GPL              |
| |  (12:11)                |
| <Jesse\_Eichar77> I hate  |
| the stuff... But that's   |
| the world we live in      |
| |image52|                 |
| |  (12:11)                |
| <Jesse\_Eichar77> ok lets |
| move on                   |
| |  (12:11)                |
| <Jesse\_Eichar77> 6)      |
| nighly sdk                |
| |  (12:11)                |
| <Jesse\_Eichar77> we need |
| to look into that again.  |
| |  (12:11) <moovida> So I |
| will draw the abstraction |
| and come back to the list |
| with it                   |
| |  (12:11) <moovida> so   |
| you can tell me if I'm    |
| going the right path      |
| |  (12:11)                |
| <Jesse\_Eichar77> Don't   |
| worry too much about the  |
| abstraction now           |
| |  (12:12) <moovida> ok   |
| 6(                        |
| |  (12:12)                |
| <Jesse\_Eichar77> do the  |
| work and as you go along  |
| define the abstraction    |
| |  (12:12)                |
| <Jesse\_Eichar77> then at |
| the end review it and     |
| clean it up               |
| |  (12:12)                |
| <Jesse\_Eichar77> I don't |
| want to slow you down too |
| much                      |
| |  (12:12) <moovida>      |
| alright                   |
| |  (12:12)                |
| <Jesse\_Eichar77> 6       |
| |  (12:12) <moovida> yes  |
| |  (12:12) <moovida> what |
| is the problem with       |
| nightly builds?           |
| |  (12:12)                |
| <Jesse\_Eichar77> I think |
| all we have to do to test |
| it is uncomment some      |
| lines in the ant classes  |
| and try it again.         |
| |  (12:12)                |
| <Jesse\_Eichar77> we have |
| no idea.                  |
| |  (12:13)                |
| <Jesse\_Eichar77> it      |
| worked when we were on    |
| 3.1                       |
| |  (12:13)                |
| <Jesse\_Eichar77> we      |
| migrated to 3.2.0 and it  |
| stopped.                  |
| |  (12:13)                |
| <Jesse\_Eichar77> we      |
| spent a few days to try   |
| to figure out the problem |
| |  (12:13)                |
| <Jesse\_Eichar77> and     |
| couldn't                  |
| |  (12:13)                |
| <Jesse\_Eichar77> It may  |
| have been a bug with      |
| eclipse so I'd like to    |
| try with 3.2.2            |
| |  (12:13)                |
| <Jesse\_Eichar77> The     |
| reason I think it might   |
| be a bug is because       |
| |  (12:14)                |
| <Jesse\_Eichar77> I used  |
| to not be able to export  |
| the application on a mac  |
| and have it run           |
| |  (12:14)                |
| <Jesse\_Eichar77> now     |
| that I upgraded to 3.2.2  |
| I can                     |
| |  (12:14)                |
| <Jesse\_Eichar77> the two |
| topics seem related so it |
| seems useful to try       |
| again.                    |
| |  (12:14)                |
| <Jesse\_Eichar77> that's  |
| all we have time to do    |
| right now.                |
| |  (12:14)                |
| <Jesse\_Eichar77>         |
| |image53|                 |
| |  (12:14) <moovida> I    |
| keep my fingers crossed   |
| |  (12:14)                |
| <Jesse\_Eichar77> you can |
| always manually create    |
| one.                      |
| |  (12:15) <jgarnett>     |
| note we needed 3.2.2 as   |
| the application crashed   |
| on vista                  |
| |  (12:15)                |
| <Jesse\_Eichar77> I       |
| export the sdk feature    |
| |  (12:15)                |
| <Jesse\_Eichar77> then    |
| unzip the exported        |
| archive over a normal     |
| udig install              |
| |  (12:15)                |
| <Jesse\_Eichar77> and     |
| that's it.                |
| |  (12:15) <moovida>      |
| seems easy... |image54|   |
| |  (12:15) <moovida>      |
| but... and but... and so  |
| on |image55|              |
| |  (12:16) <moovida> I    |
| will at some point do     |
| that                      |
| |  (12:16)                |
| <Jesse\_Eichar77>         |
| |image56|                 |
| |  (12:16) <moovida> now  |
| I have some problems with |
| keeping my stuff clean    |
| |  (12:16)                |
| <Jesse\_Eichar77> hit the |
| list when you have        |
| problems                  |
| |  (12:16)                |
| <Jesse\_Eichar77> I will  |
| try to get one out soon.  |
| |  (12:16)                |
| <Jesse\_Eichar77> for     |
| RC10                      |
| |  (12:16) <moovida> I'm  |
| not used toi work with    |
| millions of plugins in my |
| eclipse                   |
| |  (12:16)                |
| <Jesse\_Eichar77> Ah      |
| right.                    |
| |  (12:16) <moovida>      |
| alright                   |
| |  (12:16)                |
| <Jesse\_Eichar77> well    |
| its not too bad           |
| |  (12:17) <moovida> ok   |
| |  (12:17)                |
| <Jesse\_Eichar77> you     |
| just have to unzip the    |
| extras-1.1.zip jar over   |
| eclipse 3.2.2 and you     |
| good                      |
| |  (12:17) <moovida> I    |
| will try that approach on |
| the 3.2.2                 |
| |  (12:18) <moovida>      |
| maybe it's the good time  |
| |image57|                 |
| |  (12:18)                |
| <Jesse\_Eichar77>         |
| |image58|                 |
| |  (12:18)                |
| <Jesse\_Eichar77> OK I    |
| think we're done?         |
| |  (12:18)                |
| <Jesse\_Eichar77> only 20 |
| minutes over time         |
| |  (12:18) <moovida>      |
| alright, I'm gone if      |
| there is nothing else     |
| |  (12:18)                |
| <Jesse\_Eichar77> good    |
| |  (12:18) <moovida>      |
| |image59| perfect         |
| |  (12:18)                |
| <Jesse\_Eichar77> ttyl    |
| |  (12:19) <moovida> so   |
| thanks once again and     |
| cheers                    |
| |  (12:19)                |
| <Jesse\_Eichar77> bye for |
| now                       |
| |  (12:19) <moovida> I'm  |
| out to drink a beer on    |
| this IRC |image60|        |
| |  (12:19)                |
| <Jesse\_Eichar77> lol     |
| |  (12:20) <moovida> bye  |
| |image61|                 |
+---------------------------+---------------------------+---------------------------+---------------------------+

+-------------+----------------------------------------------------------+
| |image63|   | Document generated by Confluence on Aug 11, 2014 12:24   |
+-------------+----------------------------------------------------------+

.. |image0| image:: images/icons/emoticons/biggrin.gif
.. |image1| image:: images/icons/emoticons/wink.gif
.. |image2| image:: images/icons/emoticons/smile.gif
.. |image3| image:: images/icons/emoticons/smile.gif
.. |image4| image:: images/icons/emoticons/biggrin.gif
.. |image5| image:: images/icons/emoticons/wink.gif
.. |image6| image:: images/icons/emoticons/smile.gif
.. |image7| image:: images/icons/emoticons/smile.gif
.. |image8| image:: images/icons/emoticons/smile.gif
.. |image9| image:: images/icons/emoticons/smile.gif
.. |image10| image:: images/icons/emoticons/smile.gif
.. |image11| image:: images/icons/emoticons/smile.gif
.. |image12| image:: images/icons/emoticons/smile.gif
.. |image13| image:: images/icons/emoticons/wink.gif
.. |image14| image:: images/icons/emoticons/wink.gif
.. |image15| image:: images/icons/emoticons/smile.gif
.. |image16| image:: images/icons/emoticons/wink.gif
.. |image17| image:: images/icons/emoticons/biggrin.gif
.. |image18| image:: images/icons/emoticons/smile.gif
.. |image19| image:: images/icons/emoticons/smile.gif
.. |image20| image:: images/icons/emoticons/sad.gif
.. |image21| image:: images/icons/emoticons/sad.gif
.. |image22| image:: images/icons/emoticons/sad.gif
.. |image23| image:: images/icons/emoticons/smile.gif
.. |image24| image:: images/icons/emoticons/biggrin.gif
.. |image25| image:: images/icons/emoticons/smile.gif
.. |image26| image:: images/icons/emoticons/smile.gif
.. |image27| image:: images/icons/emoticons/smile.gif
.. |image28| image:: images/icons/emoticons/biggrin.gif
.. |image29| image:: images/icons/emoticons/smile.gif
.. |image30| image:: images/icons/emoticons/biggrin.gif
.. |image31| image:: images/icons/emoticons/biggrin.gif
.. |image32| image:: images/icons/emoticons/wink.gif
.. |image33| image:: images/icons/emoticons/smile.gif
.. |image34| image:: images/icons/emoticons/smile.gif
.. |image35| image:: images/icons/emoticons/biggrin.gif
.. |image36| image:: images/icons/emoticons/wink.gif
.. |image37| image:: images/icons/emoticons/smile.gif
.. |image38| image:: images/icons/emoticons/smile.gif
.. |image39| image:: images/icons/emoticons/smile.gif
.. |image40| image:: images/icons/emoticons/smile.gif
.. |image41| image:: images/icons/emoticons/smile.gif
.. |image42| image:: images/icons/emoticons/smile.gif
.. |image43| image:: images/icons/emoticons/smile.gif
.. |image44| image:: images/icons/emoticons/wink.gif
.. |image45| image:: images/icons/emoticons/wink.gif
.. |image46| image:: images/icons/emoticons/smile.gif
.. |image47| image:: images/icons/emoticons/wink.gif
.. |image48| image:: images/icons/emoticons/biggrin.gif
.. |image49| image:: images/icons/emoticons/smile.gif
.. |image50| image:: images/icons/emoticons/smile.gif
.. |image51| image:: images/icons/emoticons/sad.gif
.. |image52| image:: images/icons/emoticons/sad.gif
.. |image53| image:: images/icons/emoticons/sad.gif
.. |image54| image:: images/icons/emoticons/smile.gif
.. |image55| image:: images/icons/emoticons/biggrin.gif
.. |image56| image:: images/icons/emoticons/smile.gif
.. |image57| image:: images/icons/emoticons/smile.gif
.. |image58| image:: images/icons/emoticons/smile.gif
.. |image59| image:: images/icons/emoticons/biggrin.gif
.. |image60| image:: images/icons/emoticons/smile.gif
.. |image61| image:: images/icons/emoticons/biggrin.gif
.. |image62| image:: images/border/spacer.gif
.. |image63| image:: images/border/spacer.gif
