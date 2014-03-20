iNotes-exporter-web
===================

This project exports a Lotus Domino iNotes 8.5 webmail as an archive which can be imported by Outlook (Macintosh and Windows versions).  
This is the web front-end of [iNotes-exporter](https://github.com/javabean/iNotes-exporter).

Requirements
------------
* Lotus iNotes (the Lotus Notes webmail; tested with version 8.5.3)
* Java 6
* Tomcat 6
* Maven 3 for compiling

Configuration
-------------
Compile [iNotes-exporter](https://github.com/javabean/iNotes-exporter) and place it in `src/main/webapp/WEB-INF/` (sorry, it is not available in any Maven repository!)  
Set your webmail URL in `src/main/webapp/WEB-INF/web.xml`

Compiling
---------
(after configuration!)

	mvn -Dmaven.test.skip clean package
