<?xml version='1.0' encoding='ISO-8859-1' ?>
<!DOCTYPE helpset
  PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN"
         "../dtd/helpset_2_0.dtd">

<?Tool1 this is data for my favorite application ?>

<helpset version="1.0">

  <!-- title -->
  <title>PCSpace Help</title>

    <!-- maps -->
  <maps>
     <homeID>mapkey.top</homeID>
     <mapref location="Map.jhm"/>
  </maps>

  <!-- views -->
  <view>
    <name>TOC</name>
    <label>Table of Contents</label>
    <type>javax.help.TOCView</type>
    <data>app_toc.xml</data>
  </view>

  <view>
    <name>Index</name>
    <label>Index</label>
    <type>javax.help.IndexView</type>
    <data>app_index.xml</data>
  </view>

  <view>
    <name>Search</name>
    <label>Search</label>
    <type>javax.help.SearchView</type>
    <data engine="com.sun.java.help.search.DefaultSearchEngine">
      JavaHelpSearch
    </data>
  </view>

<!-- presentation -->
  <presentation default="true" displayviews="true" displayviewimages="true">
    <name>PCSpaceMainWindow</name>
    default="true"
    <size width="600" height="500" />
    <location x="200" y="200" />
    <title>PCSpace Help</title>
    <image>mapkey.frame_icon</image>
  </presentation>
  
</helpset>
