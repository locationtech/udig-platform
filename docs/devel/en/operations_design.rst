Operations Design
#################

Summary
=======

The **org.locationtech.udig.ui.operation** extension point allows an operation to be defined for one
or more objects. When an object or set of objects are selected anywhere in uDig operations are
enabled or disabled depending on the configuration specified in the operation extension definition.
Enabled operations can then be ran from the Operations menu item or (usually) from the context menu.

Extension Definition Information
================================

**Example Extension Definition**

::

    <operation
        class="org.locationtech.udig.tool.edit.SplitLineOp"
        enablesFor="+"
        id="org.locationtech.udig.tool.edit.operation1"
        menuPath="edit/add.ext"
        name="%operation.splitLine.name"
        targetClass="org.locationtech.udig.tools.edit.support.Point">
        <enablement>
            <and>
            <property name="CurrentShape.ShapeType" target="LINE"/>
                <property name="OpenMap" target="java.lang.Object"/>
            </and>
        </enablement>
    </operation>

There are multiple ways that the objects that the operation applies to can be defined. To start with
the object class type can be used to restrict the potential operations. In the example above the
line: *targetClass="org.locationtech.udig.tools.edit.support.Point"* indicates that the operation can
only operate on a Point object.

A second method for specifying operation enablement is the enablesFor attribute. Legal values are
,?,\*, n or n (where n is a number). The attribute specifies how many objects there may be in the
current selection. In the example above there must be 1 or more selected points.

The third option for specifying the enablement is the enablement child element. The enablement
element (also used by the tool framework) can be used to define the enablement with very fine
precision. The enablement element uses another extension point,
**org.locationtech.udig.ui.objectProperty**, to determine if an operation can be enabled for an
object. In the example above both properties: *CurrentShape.ShapeType==LINE* and *OpenMap* must be
true. The exact semantics of an property is determined by the extension.

Since ObjectProperty is an extension it is possible to nearly an method of determining whether an
operation is enabled.

Implementation Information
==========================

The IOp interface must be implemented by all operations. All operations that are executed are ran in
a background thread so implementors do not have to be concerned with blocking the UI thread.
However, since operations are not ran in the UI thread the UI cannot be updated without calling
display.asyncExec() or display.syncExec(). The current display is passed in as a parameter.
