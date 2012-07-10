API rules of engagement
=======================

We follow the same API rules of engagement as the Eclipse project; a summary of which follows.

What is API:

API

permission

details

package

 

Any package that does not have *internal*, *examples*, or *tests* in the name

class
 or interface

public

Any *public* class or interface in an API pacakge.

method
 or constructor

public
 protected

Of course this is limited to API class or interface

field

public
 protected

Of course this is limited to API class or interface

Everything else is considered an internal implementation detail and should not be used by client
code.

If you really need something please ask on the email list, chances are we would be happy to make it
available,
 and are just waiting for someone to ask. One of the ways we have kept the udig application small is
by not
 inventing (or advertising) API until a developers asks.

The eclipse rules go into more details on subclassing and overriding. Please assume we are following
eclipse
 conventions - and feel free to report a bug when we dont. |image0|

For more information:

-  `Eclipse platform API rules of
   engagement <http://help.eclipse.org/galileo/topic/org.eclipse.platform.doc.isv/reference/misc/api-usage-rules.html>`_

Q: net.refractions.udig.xyz is optional?

Yes - if you don't have any plublic API (because you are a pure extention) don't declare any public
api

Q: Implementation is not required?

Correct - if you are simply republishing some jars so that other may depend on them. The
net.refractions.udig.libs plug-in is an example of this.

Q: Can I have subpackages?

Sort of - just not public ones. Please limit your subpackages to your internals (where others cannot
depend on them).

Eclipse does keep everything in different class loaders - so one can have two plug-ins with the same
Class and different implementations. While this is possible it is not sane.

The RCP forces us to make sharing explicit - hense the reason for plug-ins like
net.refractions.udig.libs.

.. |image0| image:: images/icons/emoticons/smile.gif
