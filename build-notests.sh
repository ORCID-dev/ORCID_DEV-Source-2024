#!/usr/bin/env bash
mvn clean install -f orcid-test/pom.xml -DskipTests && mvn clean install -DskipTests
