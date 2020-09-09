/*
 * Copyright 2019-2020 Bart Verhoeven
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

import org.junit.jupiter.api.Test;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test that covers {@code BingProxy}-related functionality.
 */
class BingProxyServletTest {

  private static final String TEST_URL = "http://bing.com/";
  private static final String TEST_KEY = "bingMapsKey";

  @Test
  void proxy() throws Exception {
    // Init proxy servlet.
    BingProxyServlet servlet = spy(BingProxyServlet.class);
    initParams(servlet, TEST_URL, TEST_KEY);
    initReply(servlet, "some dummy response");

    // Mock request and response objects.
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    CapturingOutputStream outputStream = new CapturingOutputStream();
    when(response.getOutputStream()).thenReturn(outputStream);

    // Perform the actual request.
    servlet.doGet(request, response);

    // Verify response.
    String actual = new String(outputStream.getValue(), StandardCharsets.UTF_8);
    assertThat(actual, is("some dummy response"));
  }

  @Test
  void defaultParameters() throws ServletException {
    BingProxyServlet servlet = new BingProxyServlet();
    initParams(servlet, TEST_URL, TEST_KEY);

    HttpServletRequest request = mock(HttpServletRequest.class);
    String actual = servlet.createRequestUrl(request).toString();

    String expected =
      new StringBuilder(TEST_URL)
        .append("Aerial")
        .append("?key=").append(TEST_KEY)
        .append("&include=ImageryProviders")
        .append("&culture=en-US")
        .append("&output=json")
        .toString();

    assertThat(actual, is(expected));
  }

  @Test
  void customCultureParameter() throws ServletException {
    BingProxyServlet servlet = new BingProxyServlet();
    initParams(servlet, TEST_URL, TEST_KEY);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("culture")).thenReturn("nl-BE");

    String actual = servlet.createRequestUrl(request).toString();
    assertThat(actual.contains("culture=nl-BE"), is(true));
  }

  @Test
  void customMapTypeParameter() throws ServletException {
    BingProxyServlet servlet = new BingProxyServlet();
    initParams(servlet, TEST_URL, TEST_KEY);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("mapType")).thenReturn("Road");

    String actual = servlet.createRequestUrl(request).toString();
    assertThat(actual.startsWith(TEST_URL + "Road"), is(true));
  }

  @Test
  void customOutputParameter() throws ServletException {
    BingProxyServlet servlet = new BingProxyServlet();
    initParams(servlet, TEST_URL, TEST_KEY);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("output")).thenReturn("xml");

    String actual = servlet.createRequestUrl(request).toString();
    assertThat(actual.contains("output=xml"), is(true));
  }

  @Test
  void requiredInitParameterUrlMissing() throws ServletException {
    BingProxyServlet servlet = new BingProxyServlet();

    assertThrows(ServletException.class,
      () -> initParams(servlet, null, TEST_KEY));
  }

  @Test
  void requiredInitParameterKeyMissing() throws ServletException {
    BingProxyServlet servlet = new BingProxyServlet();

    assertThrows(ServletException.class,
      () -> initParams(servlet, TEST_URL, null));
  }

  /**
   * Initializes the servlet with the provided parameter values.
   *
   * @param servlet the servlet to initialize
   * @param url the value for the param {@code url}
   * @param key the value for the param {@code key}
   * @throws ServletException the servlet could not be initialized
   */
  private static void initParams(Servlet servlet, String url, String key)
  throws ServletException {

    ServletConfig servletConfig = mock(ServletConfig.class);
    when(servletConfig.getInitParameter("url")).thenReturn(url);
    when(servletConfig.getInitParameter("key")).thenReturn(key);

    servlet.init(servletConfig);
  }

  /**
   * Initializes the servlet with a default response for all requests.
   *
   * @param servlet a servlet mock
   * @param response the response
   * @throws IOException never happens
   */
  private static void initReply(BingProxyServlet servlet, String response)
  throws IOException {

    byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

    URLConnection connection = mock(URLConnection.class);
    InputStream inputStream = new ByteArrayInputStream(bytes);

    when(connection.getInputStream()).thenReturn(inputStream);
    doReturn(connection).when(servlet).openConnection(any());
  }
}
