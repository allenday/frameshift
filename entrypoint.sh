#!/bin/bash
solr start && java -jar /uploadservlet/target/standalone.jar -httpPort=9999
