Branding Imagery
================

Now that your branding plug-in is created we need to provide a little bit of content before we get down to coding and configuration. An important aspect of defining a new product is the all important visual identity.

Fiddling around with drawing programs is a bit beyond the scope of this workbook but we can quickly cover what is needed.

* splash.bmp
  |20000007000033AE000021145DBD48E0_svm|
  The splash screen must be in bmp format, it is actually read by a C++ program responsible for launching your application. The recommended size for this image is 500x330 pixels.


* about.gif
  |2000000700000B100000108A9DCD022D_svm|
  The about image is limited to the gif format, the maximum limit is 500x330, for images smaller than 250x330 you have the option of including your own text and html links in the about dialog.


* Window images displayed by the operating system.
  |2000000700000D3C00000D3C160AD700_svm|
  |200000070000069E0000069EA8EDE6C0_svm|
  |20000007000004F7000004F7E3C2B0BA_svm|
  |200000070000035000000350E0540BF5_svm|
  |20000007000001C3000001C3DDC23CD8_svm|
  gif files sized 128x128,64x64,48x48,32x32,16x16


* Assorted icons


+---------------+-------------------------------------------------+--+
| icons/elcl16  | Used on a view tool bar actions.                |  |
| icons/dlcl16  | (16x16 left and top clear)                      |  |
|               |                                                 |  |
+---------------+-------------------------------------------------+--+
| icons/etool16 | Used in application tool bar and menu bar       |  |
| icons/dtool16 | (16x16 left and top clear)                      |  |
|               |                                                 |  |
+---------------+-------------------------------------------------+--+
| icons/obj16   | Used in trees to represent objects or ideas     |  |
|               | (16x16 centered, bottom clear)                  |  |
|               |                                                 |  |
+---------------+-------------------------------------------------+--+
| icons/ovr16   | Used to decorate a obj16 icon to indicate state |  |
|               | (7x8 one pixel white outline)                   |  |
|               |                                                 |  |
+---------------+-------------------------------------------------+--+
| icons/wizban  | Banner used in wizard dialog windows            |  |
|               | (55x45 bottom left on a blue gradient)          |  |
|               |                                                 |  |
+---------------+-------------------------------------------------+--+

Enough of that - lets download the files we need and keep going. If you have any questions please consult the
:

* Download the following file:


* Select
  File > Import
  to open up the Import wizard


* Choose
  General > Archive File
  and press
  Next
  |100000000000021F000001C065E7981C_png|


* Fill in the following details on the
  Archive file

  page:
  From archive file:
  rcp_branding.zip
  Into folder:
  net.refractions.udig.tutorials.customapp
  |100000000000021F000002267C4B4FA7_png|


* Press
  Finish
  , two folders will be added to your project. There is an “nl” folder with language specific branding; and an icons folder.


.. |2000000700000D3C00000D3C160AD700_svm| image:: images/2000000700000D3C00000D3C160AD700.svm
    :width: 3.388cm
    :height: 3.388cm


.. |20000007000004F7000004F7E3C2B0BA_svm| image:: images/20000007000004F7000004F7E3C2B0BA.svm
    :width: 1.272cm
    :height: 1.272cm


.. |100000000000021F000001C065E7981C_png| image:: images/100000000000021F000001C065E7981C.png
    :width: 8.62cm
    :height: 7.11cm


.. |2000000700000B100000108A9DCD022D_svm| image:: images/2000000700000B100000108A9DCD022D.svm
    :width: 1.981cm
    :height: 2.96cm


.. |20000007000033AE000021145DBD48E0_svm| image:: images/20000007000033AE000021145DBD48E0.svm
    :width: 9.26cm
    :height: 5.93cm


.. |100000000000021F000002267C4B4FA7_png| image:: images/100000000000021F000002267C4B4FA7.png
    :width: 8.62cm
    :height: 8.729cm


.. |20000007000001C3000001C3DDC23CD8_svm| image:: images/20000007000001C3000001C3DDC23CD8.svm
    :width: 0.452cm
    :height: 0.452cm


.. |200000070000069E0000069EA8EDE6C0_svm| image:: images/200000070000069E0000069EA8EDE6C0.svm
    :width: 1.693cm
    :height: 1.693cm


.. |200000070000035000000350E0540BF5_svm| image:: images/200000070000035000000350E0540BF5.svm
    :width: 0.848cm
    :height: 0.848cm

