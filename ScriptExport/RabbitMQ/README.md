ScriptExport (RabbitMQ)
=======================

This modification to the [Ephesoft][ephe] ScriptExport.java file will publish a message to the specified [RabbitMQ][rabbitmq] queue upon export.

* Runs on Export workflow stage
* Uses parameters in the script to connect to [RabbitMQ][rabbitmq]
* Uses rabbitmq-client and gson libraries


**Contributors**

* [Ken Taylor](taylor.kenneth@gmail.com)


Prerequisites
-------------
* [Ephesoft Enterprise Server][ephe]
* [RabbitMQ Server][rabbitmq]


Usage
-----
Install as below, run a batch job and watch the magic! Seriously though...you will need to create a consumer for the message you are publishing. You can view the queue via the RabbitMQ Admin UI.

The contents of the message sent to the queue is a json payload of the following format:

    {
	    "DocumentIdentifier": "BC1",
	    "DateReceived": "2012-12-05T12:00:00"
    }


Installation
------------
1. Add the jar files for the following libraries to `(Ephesoft_Install_Dir)\Application\WEB-INF\lib`
	* [Google Gson Java library][gson]
	* [RabbitMQ Java Client][rabbitjava]
2. Stop and restart your Ephesoft server
3. Copy this version of ScriptExport.java to the appropriate Batch classes' scripts folder. E.g. `{Ephesoft_Install_Dir}\SharedFolders\{Batch_Class}\scripts\`
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