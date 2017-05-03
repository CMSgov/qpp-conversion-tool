#!/bin/bash
BM_OUTPUT=target/benchmark_output
CSV=${BM_OUTPUT}.cvs
TXT=${BM_OUTPUT}.txt
IS_A_NUMBER='^[0-9]*([.][0-9]+)?$'

THROUGHPUT=2      # operations per second
AVERAGE_TIME=.5   # seconds per operation

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

PROJECT_VERSION=$(echo '${project.version}' | mvn help:evaluate | grep -v '^[[]')
mvn dependency:get -Dartifact=gov.cms.qpp.conversion:java-conversion-tool:${PROJECT_VERSION} -o
if [[ ! "$?" = "0" ]]; then
  (cd ../java-conversion-tool && mvn clean install -Dmaven.test.skip=true)
fi

mvn clean package
java -jar target/benchmarks.jar -rff $CSV -o $TXT -bm thrpt -bm avgt

OLD_IFS=$IFS
IFS=,
[ ! -f $CSV ] && { echo "$CSV file not found"; exit 1; }
sed 's/\"//g' $CSV > "${CSV}.copy"
mv "${CSV}.copy" $CSV

# "gov.cms.qpp.conversion.ConverterBenchmark.benchmarkMain","thrpt",1,15,15.425165,2.926078,"ops/s"
# "gov.cms.qpp.conversion.ConverterBenchmark.benchmarkMain","avgt",1,15,0.066603,0.014395,"s/op"

while read benchmark mode threads samples score scoreErr unit
do
  if [[ "$mode" = "thrpt" && $(echo "$score < $THROUGHPUT" | bc -l) > 0 ]] ; then
    >&2 echo "Failed throughput benchmark. $score < $THROUGHPUT"
    exit 2
  fi

  if [[ "$mode" = "avgt" && $(echo "$score > $AVERAGE_TIME" | bc -l) > 0 ]] ; then
    >&2 echo "Failed average time benchmark. $score > $AVERAGE_TIME"
    exit 3
  fi
done < $CSV
IFS=$OLD_IFS

