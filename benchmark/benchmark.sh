#!/bin/bash

# the benchmark output file names
BM_OUTPUT=target/benchmark_output
CSV=${BM_OUTPUT}.cvs
TXT=${BM_OUTPUT}.txt

# number regex for parameter parsing
IS_A_NUMBER='^[0-9]*([.][0-9]+)?$'

# default benchmark expectations
THROUGHPUT=2      # operations per second
AVERAGE_TIME=.5   # seconds per operation

# parse arguments
while getopts ":a:t:" opt; do
  case $opt in
    a)
      if ! [[ $OPTARG =~ $IS_A_NUMBER ]] ; then
         echo "error: Not a number" >&2
         exit 1
      fi
      AVERAGE_TIME=$OPTARG
      ;;
    t)
      if ! [[ $OPTARG =~ $IS_A_NUMBER ]] ; then
         echo "error: Not a number" >&2
         exit 1
      fi
      THROUGHPUT=$OPTARG
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument." >&2
      exit 1
      ;;
  esac
done

cd $(dirname $0)

# build and run the benchmark project
mvn package
java -jar target/benchmarks.jar -rff $CSV -o $TXT -bm thrpt -bm avgt 2>conversion-error.log

# make sure we have a results benchmark output file
IFS=,
[ ! -f $CSV ] && { echo "$CSV file not found"; exit 1; }

# remove double quotes from csv entries
sed 's/\"//g' $CSV > "${CSV}.copy"
mv "${CSV}.copy" $CSV

# sample raw csv output (note the existing double quotes)
# "gov.cms.qpp.conversion.ConverterBenchmark.benchmarkMain","thrpt",1,15,15.425165,2.926078,"ops/s"
# "gov.cms.qpp.conversion.ConverterBenchmark.benchmarkMain","avgt",1,15,0.066603,0.014395,"s/op"

# not sure how this is reading the csv file but it is and the strings
# after the 'read' are the first line column names
while read benchmark mode threads samples score scoreErr unit
do
  # if the throughput is greater then the expected then log as such
  if [[ "$mode" = "thrpt" && $(echo "$score < $THROUGHPUT" | bc -l) > 0 ]] ; then
    >&2 echo "Failed throughput benchmark. $score < $THROUGHPUT"
    exit 2
  fi

  # if the average time is greater then the expected then log as such
  if [[ "$mode" = "avgt" && $(echo "$score > $AVERAGE_TIME" | bc -l) > 0 ]] ; then
    >&2 echo "Failed average time benchmark. $score > $AVERAGE_TIME"
    exit 3
  fi
done < $CSV
