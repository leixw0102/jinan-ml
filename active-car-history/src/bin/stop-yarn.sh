#!/bin/sh

service_name=$1
script_path=$(cd "$(dirname "$0")"; pwd)
base=$(dirname ${script_path})
logFile=${base}/logs/${service_name}.log
applicationId=`cat ${logFile} | grep YarnClientImpl | grep -Po 'application_\d+_\d+'`
yarn application -kill ${applicationId}

