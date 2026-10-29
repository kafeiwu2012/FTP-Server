package com.lanftp;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Real Android TCP/HTTP check, independent of the SAF picker integration scenario. */
final class Ipv6Smoke {
  static String run() throws Exception {
    FtpServer.Vfs fs = new FtpServer.Vfs() {
      public FtpServer.Entry stat(String path) { return new FtpServer.Entry("", true, 0, 0); }
      public List<FtpServer.Entry> list(String path) { return Collections.emptyList(); }
      public InputStream open(String path) throws IOException { throw new FileNotFoundException(); }
    };
    StringBuilder report = new StringBuilder("API=").append(android.os.Build.VERSION.SDK_INT);
    try (WebServer server = new WebServer(fs, "tester", "test-password", 0,
        new AccessPolicy(false, true, false))) {
      server.start();
      List<String> hosts = new ArrayList<>(Arrays.asList("127.0.0.1", "::1"));
      for (String ip : FtpService.lanWebIps()) if (ip.indexOf(':') >= 0) hosts.add(ip);
      for (String host : hosts) {
        try (Socket socket = new Socket()) {
          socket.connect(new InetSocketAddress(InetAddress.getByName(host), server.port()), 3000);
          socket.setSoTimeout(3000);
          String authority = (host.indexOf(':') >= 0 ? "[" + host + "]" : host) + ":" + server.port();
          socket.getOutputStream().write(("GET /api/list?path=/ HTTP/1.1\r\nHost: " + authority
              + "\r\nConnection: close\r\n\r\n").getBytes(StandardCharsets.US_ASCII));
          ByteArrayOutputStream bytes = new ByteArrayOutputStream();
          byte[] buffer = new byte[4096];
          for (int n; (n = socket.getInputStream().read(buffer)) >= 0;) bytes.write(buffer, 0, n);
          String response = new String(bytes.toByteArray(), StandardCharsets.UTF_8);
          if (!response.startsWith("HTTP/1.1 200 ") || !response.contains("\"entries\":[]"))
            throw new IOException(host + ": " + response);
          report.append("\n").append(host).append(" HTTP 200");
        }
      }
      server.checkLocalConnectivity(hosts);
      String diagnostics = server.diagnostics();
      if (!diagnostics.contains("IPv6 回环：可访问")) throw new IOException(diagnostics);
      return report.append("\n").append(diagnostics).toString();
    }
  }
}
