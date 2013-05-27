#!/bin/bash
# ------------------------------------------------------------------------------
# Utility script for HeritrixRemote
# by Zsolt Juranyi
# http://juzraai.github.io/
# ------------------------------------------------------------------------------
# This script creates jobs for URLs and builds them. my-crawler-beans.cxml file
# will be used as configuration for all jobs, HeritrixRemote will insert the
# seed URLs for each job separately.
#
# You must specify a contact URL in the CXML file before using it!
# ------------------------------------------------------------------------------

# HeritrixRemote path, Heritrix location and authorization defined here:
source heritrixremote.sh

# URLs defined here:
source urls.sh

# Go thru URLs:
for (( i=0; i<${#urls[*]}; i++ )) 
do
	# Create job:
	$HERITRIXREMOTE create ${urls[$i]} use my-crawler-beans.cxml
done

# Build jobs:
$HERITRIXREMOTE build unbuilt