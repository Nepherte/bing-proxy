/*
 * Copyright 2019 Bart Verhoeven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nepherte.bingmaps;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * <p>A proxy servlet to allow the retrieval of BingMaps imagery metadata.
 *
 * <p><strong>Required Init Params</strong>
 * <table summary="Required Init Params">
 *   <tr>
 *     <td style="width: 175px">url</td>
 *     <td style="width: 400px">the BingMaps base url</td>
 *   </tr>
 *   <tr>
 *     <td style="width: 175px">key</td>
 *     <td style="width: 400px">the BingMaps account key</td>
 *   </tr>
 * </table>
 *
 * <p><strong>Optional Request Params</strong>
 * <table summary="Optional Request Params">
 *  <tr>
 *    <td style="width: 175px">culture</td>
 *    <td style="width: 400px">the locale to use (default: en-US)</td>
 *  </tr>
 *  <tr>
 *    <td style="width: 175px">mapType</td>
 *    <td style="width: 400px">the type of imagery (default: Aerial)</td>
 *  </tr>
 *  <tr>
 *    <td style="width: 175px">output</td>
 *    <td style="width: 400px">the response output format (default: json)</td>
 *  </tr>
 * </table>
 */
@WebServlet(name = "bing-proxy", urlPatterns = "/bing")
public class BingProxyServlet extends HttpServlet {

  private static final long serialVersionUID = -7361305529355558891L;

  private static final String INIT_PARAM_URL = "url";
  private static final String INIT_PARAM_KEY = "key";

  private static final String PARAM_CULTURE = "culture";
  private static final String PARAM_MAP_TYPE = "mapType";
  private static final String PARAM_OUTPUT = "output";

  private static final String DEFAULT_CULTURE_VALUE = "en-US";
  private static final String DEFAULT_MAP_TYPE_VALUE = "Aerial";
  private static final String DEFAULT_OUTPUT_VALUE = "json";

  private static final int BUFFER_SIZE = 8192;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    requireInitParameter(INIT_PARAM_URL);
    requireInitParameter(INIT_PARAM_KEY);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
  throws ServletException, IOException {

    URL requestUrl = createRequestUrl(request);
    URLConnection connection = openConnection(requestUrl);
    response.setContentType(connection.getContentType());

    try (InputStream inputStream = connection.getInputStream();
         OutputStream outputStream = response.getOutputStream()) {

      copy(inputStream, outputStream);
    }
  }

  /**
   * Creates the url that requests metadata from BingMaps.
   *
   * @param request the http request
   * @return the url the forward the request to
   * @throws ServletException a malformed url
   */
  protected URL createRequestUrl(ServletRequest request)
  throws ServletException {

    String url = getInitParameter(INIT_PARAM_URL);
    String key = getInitParameter(INIT_PARAM_KEY);

    String mapType = getParam(request, PARAM_MAP_TYPE, DEFAULT_MAP_TYPE_VALUE);
    String culture = getParam(request, PARAM_CULTURE, DEFAULT_CULTURE_VALUE);
    String output = getParam(request, PARAM_OUTPUT, DEFAULT_OUTPUT_VALUE);

    //noinspection ControlFlowStatementWithoutBraces
    if (!url.endsWith("/")) url += "/";

    // Construct the base imagery metadata url.
    StringBuilder query = new StringBuilder().append(url).append(mapType);

    // Append the required and optional request params.
    query.append("?key=").append(key);
    query.append("&include=ImageryProviders");
    query.append("&culture=").append(culture);
    query.append("&output=").append(output);

    try {
      return new URL(query.toString());
    }
    catch (MalformedURLException exception) {
      throw new ServletException(exception);
    }
  }

  /**
   * Opens up a connection to a url. For stubbing purposes only.
   *
   * @param requestUrl the url to which to open up a connection
   * @return a connection made to the provided url
   * @throws IOException no connection could be opened
   */
  URLConnection openConnection(URL requestUrl) throws IOException {
    return requestUrl.openConnection();
  }

  /**
   * Requires an init parameter to be present.
   *
   * @param param the parameter that needs to be present
   * @throws ServletException if the parameter is not present
   */
  private void requireInitParameter(String param) throws ServletException {
    String initParam = getInitParameter(param);

    if (initParam == null) {
      throw new ServletException(
        "Required init parameter [" + param + "] missing"
      );
    }
  }

  /**
   * Returns the value of a request parameter, or a default value.
   *
   * @param request the http request
   * @param param the parameter name
   * @param defaultValue the default value
   * @return the value of the request parameter, or {@code defaultValue}
   */
  private static String getParam(ServletRequest request,
                                 String param, String defaultValue) {

    String value = request.getParameter(param);
    return value == null ? defaultValue : value;
  }

  /**
   * Copies all bytes from the input stream to the output stream.
   *
   * @param in the input stream to read from
   * @param out the output stream to write to
   * @throws IOException if an I/O error occurs
   */
  private static void copy(InputStream in, OutputStream out) throws IOException{
    byte[] buf = new byte[BUFFER_SIZE];

    while (true) {
      int r = in.read(buf);

      if (r == -1) {
        break;
      }

      out.write(buf, 0, r);
    }
  }
}
