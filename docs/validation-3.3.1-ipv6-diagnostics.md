# 3.3.1：手机端 IPv6 诊断

用户反馈 3.3 在作为服务器的手机上访问自身 IPv6 地址失败。当前尚未取得该手机的浏览器错误或诊断输出，不能认定问题已修复。

## 已确认的证据

在 Android 5.1.1 / API 22 AOSP 软件模拟器，以普通应用 UID 运行 3.3 的生产 WebServer，实际 TCP 请求结果：

- IPv4 回环 `127.0.0.1`：HTTP 200。
- IPv6 回环 `::1`：HTTP 200。
- 模拟器自身的非回环 IPv6 地址：HTTP 200。

这证实原有引擎可在该 Android 运行时处理 IPv6，但不能代表 OPPO / ColorOS 真机、手机浏览器或用户网络已验证。

3.3.1 诊断版在同一 Android 5.1.1 模拟器中再次通过上述三条真实 HTTP 路径，自检测报告均为 HTTP 200，Instrumentation 返回 Activity.RESULT_OK。Debug / 未签名 Release / AndroidTest 构建、7 项 JVM Android 回归以及 Lint 均通过；Lint 保留 1 项旧系统忽略 usesCleartextTraffic 的预期警告。

## 诊断版的变化

3.3.1 启动共享后，在后台检测 IPv4 回环、IPv6 回环以及当前枚举到的手机自身 IPv6 地址；“连接诊断”显示实际绑定地址及每个检测的 HTTP 状态或连接异常。检测不发送账号密码，不读取共享文件；未开启匿名访问时 HTTP 401 也表示监听已成功到达。网页请求计数包含这些本机检测请求。网络变化后请停止再启动共享，重新采集检测结果。

根据结果定位下一步：

| 结果 | 含义 |
| --- | --- |
| IPv4 可访问、IPv6 回环连接失败 | 优先排查 IPv6 监听或系统 IPv6 能力 |
| IPv6 回环可访问、本机 IPv6 地址连接失败 | 优先排查该地址是否属于当前接口、路由或系统网络限制 |
| 本机 IPv6 返回 HTTP 400 | 请求已到达应用，继续排查请求头与浏览器行为 |
| 本机 IPv6 返回 HTTP 200 / 401，但浏览器失败 | 检查浏览器提示、实际 URL、代理 / VPN 等访问路径 |

绑定地址是监听信息，不能单凭 `::` 或 `0.0.0.0` 判断双栈可达性，应以实测 HTTP 结果为准。

## 自动化

`python3 tests/test_web.py` 现含 13 项，新增本机检测的匿名 HTTP 200、需登录 HTTP 401，以及强制 IPv4-only JVM 下的 IPv6 失败分类。现有 IPv6 上传下载、权限与跨站检查保留。

Android 实机运行时检查通过现有测试驱动的独立模式运行，不涉及 SAF 文件授权或真实文件读写：

```sh
./scripts/cloud-build.sh :app:assembleDebug :app:assembleDebugAndroidTest
adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk
adb -s emulator-5554 install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb -s emulator-5554 shell am instrument -w -r -e mode ipv6 com.lanftp.test/com.lanftp.SmokeInstrumentation
```

必须检查返回的 `result` 中 HTTP 测试与诊断均成功，并且 `INSTRUMENTATION_CODE: -1`（Activity.RESULT_OK）；仅凭 adb 命令退出码不足以判定通过。测试驱动只允许可丢弃的 AOSP 模拟器。
