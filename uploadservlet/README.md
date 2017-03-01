Example of @MultipartConfig on Jetty
====================================

This project uses maven.
This checkout is a valid ${jetty.base} directory too.

To build:

```
mvn clean package
mvn assembly:single
mvn jetty:run -Djetty.http.port=9999 -Dorg.eclipse.jetty.server.Request.maxFormContentSize=5000000
```

This should have created a webapps/root.war file for you

To run in jetty distribution:

```
java -jar /path/to/jetty-distribution-9.3.6.v20151106/start.jar
```

To test:

```
mkdir /tmp/upload
curl -i -X POST -H "Content-Type: multipart/form-data" -F "file_id=some.mp4" -F "time_offset=42.5" -F "file=@some.jpg" http://localhost:9999/upload
```

Example results:

```
curl -i -X POST -H "Content-Type: multipart/form-data" -F "param1=bla" -F "file=@big.tar.gz" http://localhost:8080/upload
HTTP/1.1 100 Continue
HTTP/1.1 200 OK
Content-Type: text/plain;charset=iso-8859-1
Content-Length: 65
Server: Jetty(9.3.6.v20151106)

Got part: ...
Got part: ...
```

```
ls -la /tmp/upload/
```
