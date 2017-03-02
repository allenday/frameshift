FROM solr

MAINTAINER Allen Day "allenday@allenday.com"

USER root

ENV BUILD_PACKAGES=""
ENV IMAGE_PACKAGES="git maven telnet openjdk-8-jdk"

RUN apt-get -y update
RUN apt-get -y --no-install-recommends install $BUILD_PACKAGES $IMAGE_PACKAGES

ENV JETTY_HOME    =  /usr/local/jetty
ENV JETTY_BASE    =  /var/lib/jetty
ENV TMPDIR        =  /tmp/jetty

RUN mkdir -p $JETTY_BASE
RUN mkdir -p $TMPDIR

#jetty http
EXPOSE 80
#solr  http
EXPOSE 8983

WORKDIR /tmp
RUN wget http://central.maven.org/maven2/org/eclipse/jetty/jetty-distribution/9.4.2.v20170220/jetty-distribution-9.4.2.v20170220.tar.gz
RUN tar -xvzf jetty-distribution-9.4.2.v20170220.tar.gz
RUN mv /tmp/jetty-distribution-9.4.2.v20170220 /usr/local/jetty

## cleanup
#RUN apt-get -y remove --purge $BUILD_PACKAGES
#RUN apt-get -y remove --purge $(apt-mark showauto)
#RUN rm -rf /var/lib/apt/lists/*

WORKDIR /

RUN git clone https://github.com/allenday/image-similarity.git
RUN cd image-similarity && mvn install assembly:single

COPY uploadservlet /uploadservlet
#RUN cd /uploadservlet && mvn install assembly:single

USER solr
RUN echo 1
RUN solr start && sleep 5 && solr create_core -c frameshift -p 8983 && solr stop -p 8983
COPY managed-schema /opt/solr/server/solr/frameshift/conf/managed-schema

USER root
CMD solr start && java -jar $JETTY_HOME/start.jar
