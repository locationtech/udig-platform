Jface Field Decorations
#######################

To add a decorator to a label (the 'x' and '!' that appear with warning and labels) you can use the
ControlDecoration as shown here:

::

    final Image fieldDecorationWarningImage;
    fieldDecorationWarningImage = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_WARNING).getImage();       
    warningDecorator = new ControlDecoration(label, SWT.LEFT | SWT.CENTER);
    warningDecorator.setMarginWidth(4);
    warningDecorator.setImage(fieldDecorationWarningImage);
    warningDecorator.setDescriptionText("Warning!");

To show the label decoration:

::

     warningDecorator.show();

To hide the label decoration

::

     warningDecorator.hide();

