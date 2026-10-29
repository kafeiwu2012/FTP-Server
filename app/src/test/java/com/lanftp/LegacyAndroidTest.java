package com.lanftp;

import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import java.lang.reflect.Method;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.android.controller.ServiceController;
import org.robolectric.annotation.Config;

/** Exercise the APIs that previously prevented Android 5.x from opening or sharing. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {21, 22, 26})
public class LegacyAndroidTest {
  @Test
  @Config(sdk = 22)
  public void webAddressesIncludeGlobalAndPrivateIpv6WithoutUnusableScopes() throws Exception {
    for (String ip : new String[] {"192.168.1.2", "2001:db8::1234", "fd12:3456::1"})
      assertTrue(ip, FtpService.isWebAddress(java.net.InetAddress.getByName(ip)));
    for (String ip : new String[] {"127.0.0.1", "0.0.0.0", "::1", "::", "fe80::1", "ff02::1"})
      assertFalse(ip, FtpService.isWebAddress(java.net.InetAddress.getByName(ip)));
    assertEquals("http://[2001:db8::1234]:8080", FtpService.webAddress("2001:db8::1234", 8080));
    assertEquals("http://192.168.1.2:8080", FtpService.webAddress("192.168.1.2", 8080));
  }

  @Test
  public void activityOpensAndRequestsPersistableFolderAccess() {
    try (ActivityController<MainActivity> controller =
        Robolectric.buildActivity(MainActivity.class).setup()) {
      MainActivity activity = controller.get();
      activity.chooseFolder(Uri.parse("content://com.android.externalstorage.documents/tree/primary%3ATest"));
      Intent picker = shadowOf(activity).getNextStartedActivityForResult().intent;
      assertEquals(Intent.ACTION_OPEN_DOCUMENT_TREE, picker.getAction());
      assertTrue((picker.getFlags() & Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION) != 0);
      assertTrue((picker.getFlags() & Intent.FLAG_GRANT_WRITE_URI_PERMISSION) != 0);
      assertEquals(Build.VERSION.SDK_INT >= 26, picker.hasExtra(DocumentsContract.EXTRA_INITIAL_URI));
    }
  }

  @Test
  public void serviceCreatesUsableForegroundNotificationAndStops() throws Exception {
    ServiceController<FtpService> controller =
        Robolectric.buildService(FtpService.class).create();
    try {
      FtpService service = controller.get();
      Method method = FtpService.class.getDeclaredMethod("notification", String.class);
      method.setAccessible(true);
      Notification notification = (Notification) method.invoke(service, "正在启动…");
      assertNotNull(notification.contentIntent);
      assertEquals(1, notification.actions.length);
      assertEquals("停止共享", notification.actions[0].title.toString());
      assertNotNull(notification.actions[0].actionIntent);
      service.startForeground(1, notification);
      assertEquals(1, shadowOf(service).getLastForegroundNotificationId());
      assertEquals(FtpService.START_NOT_STICKY,
          service.onStartCommand(new Intent(service, FtpService.class).setAction("stop"), 0, 1));
      assertTrue(shadowOf(service).isStoppedBySelf());
    } finally {
      controller.destroy();
    }
  }
}
