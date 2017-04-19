#!/usr/bin/env bash

if [[ "$CIRCLE_BRANCH" == "master" || ( ! -z $SONAR_OTHER_BRANCH && "$CIRCLE_BRANCH" == "$SONAR_OTHER_BRANCH" ) ]]; then
	#Do a full SonarQube run
	echo "Doing full SonarQube run"
	./sonar-scanner-3.0.1.733/bin/sonar-scanner -Dsonar.login=${SONAR_KEY}
elif [[ ! -z $CI_PULL_REQUEST ]]; then
	#Do a PR preview SonarCube run
	export PR_NUMBER=${CI_PULL_REQUEST##*/}
	echo "Doing preview SonarQube run on PR $PR_NUMBER"
	./sonar-scanner-3.0.1.733/bin/sonar-scanner -Dsonar.analysis.mode=preview \
	                                            -Dsonar.github.pullRequest=${PR_NUMBER} \
	                                            -Dsonar.github.repository=CMSgov/qpp-conversion-tool \
	                                            -Dsonar.github.oauth=${SONAR_PR_KEY} \
	                                            -Dsonar.login=${SONAR_KEY}
else
	echo "Not on master nor in a pull request."
fi
