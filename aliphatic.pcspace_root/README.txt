PCSPACE version -1.0 ALPHA PROTOTYPE 

(Note: soon to be implemented features are in parentheses)

OVERVIEW

This directory is a suggested directory structure for all data that
will can be used by PCSPACE.  

The basic user is concerned primarily with the "datasources"
subdirectory.  In this subdirectory you find a "DataSource", which
corresponds to the DataSource notion in PCSPACE.  A DataSource is a
directory which you may place your molecules in either SDF or SMI file
format, and also the corresponding descriptor (activity) files.
PCSPACE is monitoring this directory and will assemble them into one
large file with the name of the subdirectory appended by
".datasource".

DataSources themselves are never read into PCSPACE memory, but rather
provide a source of data to build sets of coordinate points, or
"Pointsets" which are plotted in a 3D scatter plot.  Each molecule
will be represented by one point in the Pointset.  An extensive user
interface is available to navigate, identify, and select points, and
thus molecules, of interest, and to export them to other informatic
tools.

The advanced user is concerned with the "descriptors" and (soon) the
"operators" subdirectories.  In the "descriptors" subdirectory,
you will find XML files with the file extension ".descriptor.xml".
This file instructs PCSPACE how to generate molecular descriptors
for any DataSources that you register with PCSPACE.  Essentially,
the descriptor XML file supplies information to run a program that
the advanced user provides, similar to a plug-in architecture.  
When PCSPACE sees any DataSources with molecules that do not have
descriptors calculated, it calculates the descriptors in the background
and deposits the file in the respective datasource directory as 
described above, which triggers PCSPACE to assemble the new datasource,
allowing the user to view an updated Pointset.

Operators work similarly to Descriptors, but rather operate on 
Pointsets instead of Molecules in DataSources.

It should be clear that this file-based approach is removes the 
tedious maintenance and administration of calculating and viewing 
very large numbers of molecules and descriptors.  Furthermore,
it may be used to basically monitor a file produced by other
informatic tools, but also used to apply proprietary descriptors
developed by the advanced user.  

This approach also allows outside agents to deposit files directly.
For example, a central server on site may push files of molecules to
the appropriate client, which will trigger their computer to calculate
descriptors that would be viewed each morning for visual patterns.  
The central server would also collect and archive completed DataSources 
and share them in a peer-to-peer fashion.

INSTALLATION 

Currently, copy this subdirectory to C:\pcspace_root.  There soon will
be a facility to specify this location within PCSPACE ASAP.
