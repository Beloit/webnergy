NOTE: This software is still in a very beta version. If you are not a developer you should rather check the compiled Windows release on: http://www.webofthings.com/energievisible/

# Howto #

## Run the software ##
Note that you first need to install the RXTX libraries for your computer. This
enables the communication with the Zigbee dongle.

Compile (using maven or whatever tool you'd like)
or download the latest archive and run:

java -Djava.library.path=. -jar
WebPlogg-VERSION-SNAPSHOT-jar-with-dependencies.jar


Once started, the software will poll all the discovered Ploggs every 10
seconds using the dongle on /dev/ttyUSB0 (default on Linux). You can
change this in the config.properties file in the Jar.

## Test the API ##

Go to:

http://localhost:9998/rest/ver/1/smartmeters/

Note 1: you can replace localhost by your IP
Note 2: you NEED the trailing slash at the end of the URL

From there your can access all the functionality by browsing the Web UI,
following links, and using forms.

## Develop using the RESTful API ##

For integrating data with your application you can request alternative
representations by using the Accept header.

E.g. for JSON Accept: application/json
for XML: Accept: application/xml

To test this you can use cURL (command line tool), the “Poster” firefox
plugin or any programming language that understands HTTP (i.e. ANY
prog/script language!)

This would be what you need to get the json:
curl -X GET -H Accept:application/json
http://IP:9998/rest/ver/1/smartmeters/0021ED0000044515

You can also turn on/off devices this way:

curl -X PUT -H Content-type:text/plain -d “off”
http://IP:9998/rest/ver/1/smartmeters/0021ED0000044515/status/

Note that to change the status of a device you do a PUT, this is the
REST good practice but we also support POST with
application/x-www-form-urlencoded
(which is what the plain HTML Web form uses in the Web UI)

All right, hope that helps, comments are VERY welcomed :-)
Webnergy is part of the Webofthings.com Energy Visible project:
http://www.webofthings.com/energievisible/