[![GitHub version](https://img.shields.io/github/tag/Nepherte/bing-proxy.svg?label=latest)](https://github.com/Nepherte/bing-proxy/releases/latest)
[![Build Status](https://img.shields.io/travis/com/Nepherte/bing-proxy/develop.svg)](https://travis-ci.com/Nepherte/bing-proxy)

**bing-proxy** is a Java proxy servlet for the retrieval of Bing™ Maps imagery 
metadata. The returned imagery metadata includes the urls and dimensions for
imagery tiles, ranges of zoom levels, and imagery vintage information.

The latest version is SNAPSHOT:

- [bing-proxy-SNAPSHOT.jar](https://ivy.nepherte.be/be.nepherte/bing-proxy/SNAPSHOT/bing-proxy-SNAPSHOT.jar)
- [bing-proxy-SNAPSHOT.war](https://ivy.nepherte.be/be.nepherte/bing-proxy/SNAPSHOT/bing-proxy-SNAPSHOT.war)
- [bing-proxy-SNAPSHOT-javadoc.jar](https://ivy.nepherte.be/be.nepherte/bing-proxy/SNAPSHOT/bing-proxy-SNAPSHOT-javadoc.jar)
- [bing-proxy-SNAPSHOT-sources.jar](https://ivy.nepherte.be/be.nepherte/bing-proxy/SNAPSHOT/bing-proxy-SNAPSHOT-sources.jar)


Installation
------------
Follow these steps for a stand-alone installation of the proxy servlet:

1. [Download](https://ivy.nepherte.be/be.nepherte/bing-proxy/SNAPSHOT/) the latest release of bing-proxy
2. Deploy the war on your application server
3. Insert your bing maps key in the [web.xml](#configuration)

#### Prerequisites
The proxy servlet relies on an application server that is compliant with the
servlet 3.0 specification. An example server that requires little to no 
configuration is [Apache Tomcat](https://tomcat.apache.org/). The supported 
versions are 7.0.x and higher.

The proxy servlet makes use of the Bing™ Maps API. The usage of this API 
requires a key. If you do not have one, read 
[this page](https://msdn.microsoft.com/en-us/library/ff428642.aspx) for more 
information. Basic keys are available at no charge that provide a limited number
of transactions per year.


Configuration
-------------
bing-proxy adheres to the servlet 3.0 specification and uses the `@WebServlet`
annotation to facilitate deployment. The servlet is registered under the name
`bing-proxy` and mapped onto the url `/bing`.

#### Init parameters
The proxy servlet requires the presence of two init parameters (`url` and `key`)
in your web deployment descriptor (`web.xml`). Failing to do so will result in a
`ServletException`. Consider the following minimal configuration:

```xml
<servlet>
  <servlet-name>bing-proxy</servlet-name>
  <servlet-class>be.nepherte.bingmaps.BingProxyServlet</servlet-class>

  <init-param>
    <param-name>url</param-name>
    <param-value>http://dev.virtualearth.net/REST/v1/Imagery/Metadata</param-value>
    <description>the bingmaps metadata url</description>
  </init-param>

  <init-param>
    <param-name>key</param-name>
    <param-value>YOUR_KEY_HERE</param-value>
    <description>the bingmaps api key</description>
  </init-param>
</servlet>
````

#### Servlet mapping  
To map the proxy servlet onto a different url, add a `<servlet-mapping>` to your
web deployment descriptor (`web.xml`):

```xml
<servlet-mapping>
  <servlet-name>bing-proxy</servlet-name>
  <url-pattern>/bing</url-pattern>
</servlet-mapping>
```


Usage
-----
The proxy servlet supports HTTP GET requests. It accepts the optional parameters
`mapType`, `culture` and `output`. The default request looks like 
`http://example.com/bing&mapType=Aerial&culture=en-US&output=json`.

#### Map type
The `mapType` parameter specifies the type of imagery for which you are 
requesting metadata. For an exhaustive list of supported values, see [Supported map types](
https://docs.microsoft.com/en-us/bingmaps/rest-services/imagery/get-imagery-metadata).
In the absence of this parameter, the servlet defaults to `Aerial`.

#### Culture
The `culture` parameter specifies the language and/or region to use. For an 
exhaustive list of supported values, see [Supported culture codes](
https://docs.microsoft.com/en-us/bingmaps/rest-services/common-parameters-and-types/supported-culture-codes).
In the absence of this parameter, the servlet defaults to `en-US`.

#### Output
The `output` parameters specifies the format of the server's response to 
incoming requests. Allowed values are `xml` and `json`. In the absence of this 
parameter. the servlet defaults to `json`.