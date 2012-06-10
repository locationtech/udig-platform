Validation
----------

Validation allows you to check your data source and helps to identify features which do not conform
to the test(s) you specify.

-  Select a layer from the Layers View and go to **Operations > Validation...**
-  Select a test (***Is Valid Geometry** for example*) from the list and click **New** to create the
   new test.
-  Customize your test and click **Run**

Each feature in the current layer will be validated to ensure it passes each test. Features which
fail a test will be displayed in the Issues List, which appears if at least one feature fails a
validation.

Available tests:

-  **Is Valid Geometry**: the feature conforms to its own geometry specification
-  **NameExists**
-  **Line Not Self Intersection**: LineStrings do not intersect self
-  **Line Not Self Overlapping**: LineStrings do not overlap self
-  **Line Without Dangles**
-  **UniqueFID**: FID is unique

