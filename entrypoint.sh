#!/bin/bash
solr start && \
java -jar /uploadservlet/target/standalone.jar -httpPort=9999
#cp -prv /solr-data/* /var/solr/data/
#sleep 20 && \
#solr create_core -c frameshift -p 8983 && \
