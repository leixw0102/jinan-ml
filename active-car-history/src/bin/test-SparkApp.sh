#!/bin/sh
# loading dependency jar in lib directory

base_dir=$(dirname $0)/..
main_class=com.ehl.ml.History
service_name=ml-history
args="-isYarn true -isEs true -yarnFile active.properties"
executeJar=$base_dir/lib/active-car-history-1.0-SNAPSHOT.jar

for file in $base_dir/lib/*.jar;
do
    if [ "$file" != "$base_dir/lib/active-car-history-1.0-SNAPSHOT.jar" ]; then
      JARS=$JARS,$file
    fi
done
  JARS=${JARS:1}

#if [ -z "$BASE_OPTS" ]; then
#  BASE_OPTS="-Dservice-conf=$base_dir/conf/active.properties"
#fi

java_options="$base_dir/conf/active.properties"

$base_dir/bin/spark-submit-1.5.2-test.sh  "$main_class" "$service_name" "$executeJar" "${args}" "${java_options}" "${JARS}"