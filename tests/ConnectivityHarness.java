package com.lanftp;

import java.nio.file.Paths;
import java.util.Arrays;

/** Exercise the production phone self-check with a real listener and without Android stubs. */
public final class ConnectivityHarness {
  public static void main(String[] args) throws Exception {
    boolean anonymous = Boolean.parseBoolean(args[1]);
    try (WebServer web = new WebServer(new LocalFileSystem(Paths.get(args[0])),
        "tester", "test-password", 0, new AccessPolicy(false, anonymous, false))) {
      web.start();
      web.checkLocalConnectivity(Arrays.asList("127.0.0.1", "::1"));
      System.out.println(web.diagnostics());
    }
  }
}
