ScriptExport (RabbitMQ)
=======================

This modification to the [Ephesoft][ephe] ScriptExport.java file will publish a message to the specified RabbitMQ queue upon export.

* Runs on Export workflow stage
* Uses parameters in the script to connect to RabbitMQ
* Uses rabbitmq-client and gson libraries


__Contributors__

* [kentaylor](taylor.kenneth@gmail.com)


Prerequisites
-------------
* [Ephesoft Enterprise Server][ephe]
* [RabbitMQ Server][rabbitmq]


Usage
-----
Install as below, run a batch job and watch the magic!


Installation
------------
1. Add the jar files for the following libraries to {Ephesoft Install Dir}\Application\WEB-INF\lib

* [Google Gson Java library][gson]
* [RabbitMQ Java Client][rabbitjava]

2. Stop and restart your Ephesoft server

3. Copy this version of ScriptExport.java to the appropriate Batch classes' scripts folder. E.g. {Ephesoft Install Dir}\SharedFolders\{Batch Class}\scripts\

4. Edit the ScriptExport.java file to change the settings for the RabbitMQ server


API Documentation
-----------------
TBD


About 
-----

TBD

[ephe]: http://www.ephesoft.com/
[gson]: http://code.google.com/p/google-gson/
[rabbitmq]: http://www.rabbitmq.com/
[rabbitjava]: http://www.rabbitmq.com/download.html 