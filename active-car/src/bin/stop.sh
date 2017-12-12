#!/bin/sh

script_path=$(cd "$(dirname "$0")"; pwd)
base=$(dirname ${script_path})
groupname=ml-active
$base/bin/daemon.sh stop "" ${groupname}
$base/bin/stop-yarn.sh ${groupname}
