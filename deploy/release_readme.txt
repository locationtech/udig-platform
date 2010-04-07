uDig 0.1.0
User-Friendly Desktop Internet GIS
Refractions Research
http://www.refractions.net

1. Introduction
2. License
3. Project Requirements
4. Project Team
5. Retrieval
6. Installation
7. Running uDig
8. Current State



1. Introduction
-------------------------------------------------------------------------------

The User-friendly Desktop Internet GIS for OpenGIS Spatial Data Infrastructures
project (uDig) will create an open-source desktop GIS application, to make
viewing, editing, and printing data from CGDI and local data sources simple for
ordinary computer users.

Open-source components are a critical part of the CGDI vision, because they
allow organizations to deploy infrastructure widely, in a distributed fashion,
without incurring multiple licensing fees. Open-source components are also the
most tractable for fast support of new OpenGIS interoperability standards.
There are already many different pieces of open-source software that implement
OpenGIS server standards: Mapserver implements WMS, GeoServer implements
WMS and WFS-T, PostGIS implements SFSQL, DeeGree implements WMS and
WFS, and so on. However, there is not a single piece of desktop software capable
of binding information from all these servers together into a unified desktop 
view. uDig is the open-source application which will bring CGDI data sources to
the desktop, and integrate them with local data sources for standard business
processes - data viewing, data editing, and data printing.

2. License
-------------------------------------------------------------------------------

This application is free software; you can redistribute it and/or modify it 
under the terms of the GNU Lesser General Public License>> as published by 
the Free Software Foundation; either version 2.1 of the License, or (at your 
option) any later version.

This application is distributed in the hope that it will be useful, but WITHOUT
FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
details.

3. Project Requirements
-------------------------------------------------------------------------------
 Functional Requirements

    * WFS client read/write support, to allow direct editing of data exposed 
      via transaction web feature servers
    * WMS support, to allow viewing of background data published via WMS
    * Styled Layer Descriptor (SLD) support, to allow the client-directed 
      dynamic re-styling of WMS layers
    * Web Registry Server support, for quick location of available CGDI layers
    * Printing support, to allow users to create standard and large format 
      cartography from their desktops using CGDI data sources
    * Standard GID file format support, to allow users to directly open, 
      overlay, and edit local Shape and GeoTiff files with CGDI online data.
    * Coordinate porjection support, to transparently intergrate remote layers
      in the cline tapplication where necessary.
    * Database access support, to allow users to directly open, overlay and 
      edit data stored in PostGIS, OracleSpatial, ArcSDE, and MySQL.
    * Cross-platform support, using Java as an implementation language, and 
      providing one-click setup files for Windows, OS/X, and Linux.
    * Multi-lingual design, allowing easy internationalization of the 
      interface, with French and English translations of the interface 
      completed initially.
    * Customizability and modularity, to allow third party developers to add 
      new capabilities, or strip out existing capabilities as necessary when 
      intergrating the application with existing enterprise infrastructures.

Non Functional Requirements

    * Well-Rounded Framework, built on standard and best-of-breed libraries to 
      offer a sustainable, competitive advantage to uDig developers.
          o Plug-in Deployment Model, with versioning and plug-in management to
            ease the cost of deployment, upgrading and installation.
          o Integration/Extension, maintain common appearance, workflow, 
            framework and persistence mechanisms between built-in editing and 
            third-party modules.
          o Logs, make use of logging standards and libraries.
    * Open Development Process, capture developer interest and third party 
      contributions.
    * Marketing
    * Release Management, stable and development releases.
    * Product Development and Branding, continued use of JUMP branding.
    * Licensing Model and Business Model
          o Application License Model, open-source license to allow distribution
            and extension without incurring multiple licensing fees, commercial 
            support allows for a business model.
          o Extension License Model, open-source Framework API allows GPL or 
            Commercial extension.
    * Usability, use industry standard user-interface constructs and 
      terminology to reduce training time.
          o Configuration and Preferences, make use of sensible defaults, use
            context where possible.
          o Installation, allow installation with sensible defaults and little 
            user input.
          o Professional Appearance, integrate with existing installation base.
          o Quick Response, provide immediate feedback.
    * Performance
          o Data Access Performance, ESRI Shapefile access is a significant 
            measure of application performance and must be more then competitive.
          o Operative Performance, application must be sufficiently responsive
            so that an operator can maintain concentration.
    * Security, considered where applicable: database passwords will not be 
      stored with project file; the OWS infrastructure lacks a strong security model.

4. Project Team
-------------------------------------------------------------------------------

Paul Ramsey - pramsey@refractions.net 
 * OpenGIS based workflow
 
Jody Garnett - jgarnett@refractions.net
 * Developement Framework and User experience
 
David Zwiers - dzwiers@refractions.net 
 * WFS/GML and Opperations API
 
Jesse Eichar - jeichar@refractions.net
 * Rendering and Grid Coverage Exchange
 
Richard Gould - rgould@refractions.net
 * WMS/GCE/Printing/Context
 
5. Retrieval
-------------------------------------------------------------------------------

You can download the current version of uDig from the website at
http://udig.refractions.net.

6. Installation
-------------------------------------------------------------------------------

Unzip the installation zip file to the desired directory. It will create a uDig
directory that will contain the uDig application.

7. Running uDig
-------------------------------------------------------------------------------

Win32:

In the uDig directory is a uDig.bat file. Double click on this file to
start uDig.

Linux:

In the uDig directory, on the command line type ./uDig and it should start.

8. Current State
-------------------------------------------------------------------------------
uDig 0.1.0

Note that uDig currently doesn't actually do much. 

If you go to Window->Show View->Other, you can select a View representing 
different aspects of uDig. The most interesting one is the Local Registry.
It will read all of the shape files in your home directory (~ in Linux,
C:\Documents and Settings\<username> in Windows 2000 or XP) and display 
a list of them.

The ViewForTesting is a test of the renderer, and the TestTreeView is a test of
SWT features. UDigView and WMSView are placeholders.

