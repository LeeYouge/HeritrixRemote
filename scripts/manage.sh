#!/bin/bash
# ------------------------------------------------------------------------------
# Utility script for HeritrixRemote
# by Zsolt Juranyi
# http://juzraai.github.io/
# ------------------------------------------------------------------------------
# This script archives finished jobs and re-launches them. You can run this
# script e.g. weekly. Store command needs Heritrix to be placed on the same
# machine as the script, and jobs to be configured to use MirrorWriterProcessor.
# ------------------------------------------------------------------------------

# HeritrixRemote path, Heritrix location and authorization defined here:
source heritrixremote.sh

# Archive finished jobs:
$HERITRIXREMOTE store finished ./my-archive/

# Teardown and rebuild jobs so they will be ready for launch:
$HERITRIXREMOTE teardown finished
$HERITRIXREMOTE build unbuilt

# Launch jobs:
./launch.sh