Java library for TinEye Services
================================

**TineyeServices** is a Java library for the MatchEngine, MobileEngine,
and MulticolorEngine APIs. MatchEngine, MobileEngine and MulticolorEngine
are general image matching engines that allow you to perform large
scale image comparisons for a variety of tasks.
See `<http://services.tineye.com/>`_ for more information.

Dependencies
============

The project was written to run with Java Standard Edition 6.
It depends on a number of external jars which are listed below::

    - log4j-1.2.16.jar: http://logging.apache.org/log4j/1.2/download.html
                        Provides logging for the library

    - commons-io.2.0.1.jar: http://commons.apache.org/io/download_io.cgi
                            Provides useful classes for converting
                            InputStream output to a String and for doing
                            file path manipulations.

    - httpclient-4.1.2.jar: http://hc.apache.org/downloads.cgi
      httpcore-4.1.2.jar:   (same URL)
      httpmime-4.1.2.jar:   (same URL)
                            These provide the classes need to handle the API HTTP requests.

    - commons-codec-1.5.jar:  http://commons.apache.org/codec/index.html
                              Provides the base64 class which is needed
                              for HTTP basic authentication.

    - json-lib-2.4-jdk15.jar: http://json-lib.sourceforge.net/index.html
                              Classes from here are used to handle the
                              parsing of API JSON responses.

    The JSON lib also requires the following jars:

    - ezmorph-1.0.6.jar:                http://ezmorph.sourceforge.net/
    - commons-beanutils-core-1.8.3.jar: http://commons.apache.org/beanutils/download_beanutils.cgi
    - commons-lang-2.6.jar:             http://commons.apache.org/lang/download_lang.cgi
    - commons-collections-3.2.1.jar:    http://commons.apache.org/collections/download_collections.cgi
    - commons-logging-1.1.1.jar:        http://commons.apache.org/logging/download_logging.cgi

Documentation
-------------

View `documentation <http://services.tineye.com/library/java/docs/>`_.

Support
-------

Please send comments, recommendations, or bugs reports to support@tineye.com.

