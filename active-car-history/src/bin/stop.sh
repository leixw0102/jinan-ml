#!/bin/sh

script_path=$(cd "$(dirname "$0")"; pwd)
base=$(dirname ${script_path})
groupname=ml-history
$base/bin/daemon.sh stop "" ${groupname}