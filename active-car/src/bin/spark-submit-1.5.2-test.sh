#!/bin/sh
# loading dependency jar in lib directory
base_dir=$(dirname $0)/..
main_class=$1
service_name=$2
executeJar=$3
args=$4

java_options=$5
jars=$6
master=yarn
mode=cluster
driverMem=4g
executorCore=2
executorMem=2g
cmd="spark-submit\
 --master ${master}\
 --deploy-mode ${mode}\
 --class ${main_class}\
 --num-executors 3\
 --driver-memory ${driverMem}\
 --executor-cores ${executorCore}\
 --executor-memory ${executorMem}\
 --driver-java-options \"-XX:PermSize=128M -XX:MaxPermSize=512M\"\
 --jars ${jars}\
 --files ${java_options}\
 ${executeJar}\
 ${args}"
$base_dir/bin/daemon.sh start "$cmd" "$service_name"