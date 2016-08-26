Utility Classes
~~~~~~~~~~~~~~~

There are a host of utility classes provided to make your life as an RCP developer easier.

org.locationtech.udig.ui
^^^^^^^^^^^^^^^^^^^^^^^^

The **org.locationtech.udig.ui** plugin contains all manner of code and utilities that help with user
interface development. You will find some of our basic drag and drop and operation support code
here.

PlatformGIS
'''''''''''

The **PlatformGIS** class contains helper code that is useful when scheduling work from a user
interface. The trick here is you want to make sure you do no work in a Display thread (no talking to
disk, or external services for example). Every time you do it looks like your application has
crashed; it won't respond to the user and they think it is broken.

Out of the box Eclipse RCP provides some facilities:

-  Display.getCurrent(); will get the Display for the current thread - but **only if** an event
   thread
-  Display.getDefault(); a good default Display (usually the one associated with the application)

Once you have a Display you can call:

-  Display.asyncExec( Runnable ); will schedule this Runnable to run on the event thread
-  Display.syncExec( Runnable ); will block your current thread until this has been scheduled and
   run

The tricks involved in making this code work and work well go something like this:

::

    Display display = Display.getCurrent();
    if( display==null ){
       display = Display.getDefault();
    }
    display.asyncExec( new Runnable(){
        public void run(){
           ... do some work that needs to talk to a user interface widget ...
        }
    });

But getting this perfect every time is boring (and figuring out how to make asyncExec work at all is
a challenge); please use ...

Running something in the Display Thread
'''''''''''''''''''''''''''''''''''''''

To run something in a display thread (it will check if the current thread is a display thread) try
the following:

::

    Runnable enableButton = new Runnable(){
        public void run(){
           button.setEnable( true );
        }
    }
    PlatformGIS.asyncInDisplay( enableButton, true); // use the thread now if possible

To schedule the runnable to go on the display thread later:

::

    Runnable enableButton = new Runnable(){
        public void run(){
           button.setEnable( true );
        }
    }
    PlatformGIS.asyncInDisplay( enableButton, false );

Waiting for something from the Display Thread
'''''''''''''''''''''''''''''''''''''''''''''

To ask for a value from a display widget you need to **wait** for the work in the Display thread to
complete.

::

    final String textPointer[] = new String[1]; // pointer to output
    Runnable queryText =
    PlatformGIS.syncInDisplay( new Runnable(){
        public void run(){
           textPointer[0] = inputField.getText(); // this is the users input
        }
    });
    String inputText = textPointer[0];

The above code example uses a final array to "hold" the value between the runnable and your code;
you could also do this using a Class field.

Accessing the Boundary Service
''''''''''''''''''''''''''''''

The Boundary Service provides access to the boundary that the user has selected via the Boundary
View. This boundary is then be used to restrict functionality such as zoom to extent and catalog
search results.

::

    IBoundaryService service = PlatformGIS.getBoundaryService();

This service then provides methods to return the extent or an actual geometry of the current
boundary.

::

    ReferencedEnvelope extent = service.getExtent();

or

::

    Geometry boundingGeom = service.getGeometry();

org.locationtech.udig.core
^^^^^^^^^^^^^^^^^^^^^^^^^^

This plug-in contains adapters and wrapper between Eclipse RCP concepts (like IAdaptable) and open
source concepts like Features; Filters and so on.

CorePlugin.RELAXED\_HANDLER
'''''''''''''''''''''''''''

The CorePlugin class itself provides a RELAXED\_HANDLER implementation of UrlHandler; you can use
this class to construct "invalid" URLs. Normally java only lets you create a URL for content that it
knows how to connect to; you can teach Java new formats by providing a URLHandler that knows how to
connect and parse the content.

Here is how to create a invalid URL:

::

    return new URL(null, the_spec, CorePlugin.RELAXED_HANDLER);

We ended up using URL as a strict "key" or "identifier" to look up content in our catalog; and we
quickly ran into services for which no normal URL would suffice. While java has the concept of a
"JDBC URL" it has not integrated this concept as a URL.

Pair
''''

The Pair class is used to return two results out of a method; It is basically a pointer or a type
safe replacement for Object2.

::

    Pair<String,Integer> mapGrid = new Pair<String,Integer>( "A", 1 );
    System.out.println("You sunk my battleship: "+ mapGrid );

Option
''''''

An alternative to returning **null**.

The Option class is used to return a single result; and communicate if the result is available. It
is a type safe replacement for returning **null**.

::

    Option<String> value = someFunction();
    if( value instanceof Option.Some ){
      // we actually got an answer
      String text = ((Some<String>)value).value();
    }

This replaces code such as the following:

::

    String value = someFunction();
    if( value != null ){
      // we actually got an answer
    }

org.locationtech.udig.libs
^^^^^^^^^^^^^^^^^^^^^^^^^^

This plug-in rounds up all the open source libraries we use.
