package com.myhealth.healthmanagermain.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public class H2ConfigurationHelper {

  public H2ConfigurationHelper() {
  }

  public static Object createServer() throws SQLException {
    return createServer("9092");
  }

  public static Object createServer(String port) throws SQLException {
    try {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      Class<?> serverClass = Class.forName("org.h2.tools.Server", true, loader);
      Method createServer = serverClass.getMethod("createTcpServer", String[].class);
      return createServer.invoke((Object) null,
          new String[]{"-tcp", "-tcpAllowOthers", "-tcpPort", port});
    } catch (LinkageError | ClassNotFoundException var4) {
      throw new RuntimeException("Failed to load and initialize org.h2.tools.Server", var4);
    } catch (NoSuchMethodException | SecurityException var5) {
      throw new RuntimeException("Failed to get method org.h2.tools.Server.createTcpServer()",
          var5);
    } catch (IllegalArgumentException | IllegalAccessException var6) {
      throw new RuntimeException("Failed to invoke org.h2.tools.Server.createTcpServer()", var6);
    } catch (InvocationTargetException var7) {
      Throwable t = var7.getTargetException();
      if (t instanceof SQLException) {
        throw (SQLException) t;
      } else {
        throw new RuntimeException("Unchecked exception in org.h2.tools.Server.createTcpServer()",
            t);
      }
    }
  }

  public static void initH2Console() {
    initH2Console("src/main/resources");
  }

  static void initH2Console(String propertiesLocation) {
    try {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      Class<?> serverClass = Class.forName("org.h2.tools.Server", true, loader);
      Method createWebServer = serverClass.getMethod("createWebServer", String[].class);
      Method start = serverClass.getMethod("start");
      Object server = createWebServer.invoke((Object) null,
          new String[]{"-properties", propertiesLocation});
      start.invoke(server);
    } catch (Exception var6) {
      throw new RuntimeException("Failed to start h2 webserver console", var6);
    }
  }

  public static void initH2Console(ServletContext servletContext) {
    try {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      Class<?> servletClass = Class.forName("org.h2.server.web.WebServlet", true, loader);
      Servlet servlet = (Servlet) servletClass.getDeclaredConstructor().newInstance();
      ServletRegistration.Dynamic h2ConsoleServlet = servletContext.addServlet("H2Console",
          servlet);
      h2ConsoleServlet.addMapping(new String[]{"/h2-console/*"});
      h2ConsoleServlet.setInitParameter("-properties", "src/main/resources/");
      h2ConsoleServlet.setLoadOnStartup(1);
    } catch (LinkageError | NoSuchMethodException | InvocationTargetException |
             ClassNotFoundException var5) {
      throw new RuntimeException("Failed to load and initialize org.h2.server.web.WebServlet",
          var5);
    } catch (InstantiationException | IllegalAccessException var6) {
      throw new RuntimeException("Failed to instantiate org.h2.server.web.WebServlet", var6);
    }
  }
}
