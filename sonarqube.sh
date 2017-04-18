#!/usr/bin/env bash

if [ "$CIRCLE_BRANCH" == "master" ]; then
	#Do a full SonarQube run
	./sonar-scanner-3.0.1.733/bin/sonar-scanner -Dsonar.login=${SONAR_KEY}
elif [ ! -z $CI_PULL_REQUEST ]; then
	#Do a PR preview SonarCube run
	export PR_NUMBER=${CI_PULL_REQUEST##*/}
	./sonar-scanner-3.0.1.733/bin/sonar-scanner -Dsonar.analysis.mode=preview \
		                                         -Dsonar.github.pullRequest=$PR_NUMBER \
	                                            -Dsonar.github.repository=CMSgov/qpp-conversion-tool \
	                                            -Dsonar.github.oauth=${SONAR_PR_KEY} \
	                                            -Dsonar.login=${SONAR_KEY}
fi
