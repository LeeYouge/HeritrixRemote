HeritrixRemote
==============

###Version: 1.0

=

##1. Introduction - *What's this?*

*HeritrixRemote* is a command line application written in Java, which can help you control your running [Heritrix](https://webarchive.jira.com/wiki/display/Heritrix/Heritrix) crawler, especially when you've got a lot of crawling jobs to manage.


##2. Features - *What can it do?*

(soon)


##3. Requirements - *What do you need?*

* installed **JDK 1.6** or newer - *to run this stuff*
* installed **cURL** - *need to communicate with Heritrix*
* running **Heritrix 3.x** - *you will control this*
* and maybe some shell scripting skills - *in case bundled .sh files are not enough*


##3. User Guide - How to use?

(soon)


##4. Additional information - *developer notes*

This application is based on [Heritrix 3.x API Guide](https://webarchive.jira.com/wiki/display/Heritrix/Heritrix+3.x+API+Guide).

I tested it with *Heritrix 3.1.1*.

I developed this program according to our needs at my workplace (especially the "store" command), but I tried to make it more universal.


##5. Features in the future - *ideas, plans*

It may be great if *HeritrixRemote* could have the option to create a job without a CXML template and help configuring the job in that case. Command line arguments should be accepted for job configuration, such as "within-domain", "mirror-writer" and "contact-url <URL>".

A "describe job" command can also be useful, it should print detailed information about a job: directory, seed URLs, configuration, size and of course job state and start date.