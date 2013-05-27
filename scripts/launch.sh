#!/bin/bash
# ------------------------------------------------------------------------------
# Utility script for HeritrixRemote
# by Zsolt Juranyi
# http://juzraai.github.io/
# ------------------------------------------------------------------------------
# This script launches and unpauses all jobs which are ready.
# ------------------------------------------------------------------------------

# HeritrixRemote path, Heritrix location and authorization defined here:
source heritrixremote.sh

# Launch jobs:
$HERITRIXREMOTE launch ready

# Sleep while Heritrix prepares them:
sleep 30

# Unpause jobs so they start crawling:
$HERITRIXREMOTE unpause paused