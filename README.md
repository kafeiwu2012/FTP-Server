# 局域网文件共享 3.3.1（手机 IPv6 诊断版）

Android 5.0 及以上的本地文件服务器：共享手机已授权的文件夹，让同一局域网内的 Windows、iPad 等设备浏览、下载，并按权限上传、覆盖、删除。支持账号和匿名访问，无需 Root，不上传云端。

**[历史稳定版 APK 3.1（要求 Android 8.0+，不适用于 R7sm Android 5.1.1）](https://github.com/kafeiwu2012/FTP/raw/refs/heads/main/downloads/LanFTP-3.1.apk)** · [源码压缩包](downloads/LanFTP-3.1-source.zip) · [SHA-256 校验](downloads/SHA256SUMS)

## HTTP IPv6

[下载 3.3.1 IPv6 诊断测试 APK](downloads/LanFTP-3.3.1-IPv6-diagnostics-debug.apk) · [SHA-256 校验](downloads/SHA256SUMS)

3.3.1 在“连接诊断”中增加手机自身的 IPv4 / IPv6 HTTP 连通性检测，用于排查手机访问自身 IPv6 失败。尚不能据此宣称 OPPO 真机问题已修复；使用方法和证据见 [诊断版说明](docs/validation-3.3.1-ipv6-diagnostics.md)。

[下载 3.3 IPv6 调试测试 APK](downloads/LanFTP-3.3-IPv6-debug.apk) · [SHA-256 校验](downloads/SHA256SUMS)

3.3 的网页服务支持 IPv4 和 IPv6。启动共享后，主界面显示可用的 IPv4 / IPv6 网页地址；“复制连接地址”可选择各个网络接口的地址。IPv6 地址必须带方括号，例如 `http://[2001:db8::1234]:8080`（示例地址，实际请复制手机显示的地址）。账号、匿名开关、文件授权与写入权限在两种地址下相同。

展示的 IPv6 包括全局地址与 ULA 私有地址。`fe80::` 链路本地地址需要客户端接口标识，浏览器支持不一致，因此不作为可复制的网页地址，也不接受带 zone 的 Host。手机与访问设备需要具备相应 IPv6 连通性；本功能不会替路由器或运营商配置 IPv6、开放防火墙。FTP 地址菜单仍使用 IPv4。

IPv6 仍使用明文 HTTP，仅向可信网络共享。自动化验证包含真实 `::1` IPv6 TCP 连接和 IPv6 Chromium 文件操作；手机网络上的实际可达性需在设备上确认。详见 [3.3 IPv6 验证记录](docs/validation-3.3-ipv6.md)。

## OPPO R7sm / Android 5.1.1

[下载 3.2 R7sm 调试测试 APK](downloads/LanFTP-3.2-R7sm-debug.apk) · [SHA-256 校验](downloads/SHA256SUMS)

3.2 将最低系统版本降至 Android 5.0（API 21），针对 R7sm 的 Android 5.1.1（API 22）增加旧版服务启动、前台通知、目录选择器与 Java API 兼容处理。继续使用 SAF 系统文件夹授权，不申请全盘存储权限或 Root。应用无原生库，不依赖手机的 CPU 指令集。

构建后的 `app/build/outputs/apk/debug/app-debug.apk` 是可安装的调试测试包，包含 Android 5.x 所需的 v1 签名。调试签名与历史发布签名不同，不能承诺覆盖安装历史发布版；不要用旧的 3.1 下载链接测试 R7sm。正式升级包需持有原发布密钥后签名。

在手机中允许安装该 APK，选择实际要共享的本地文件夹并授权，再启动共享。ColorOS 中允许应用后台运行，并在最近任务中锁定应用；如锁屏后断开，检查系统省电管理。设置项名称随 ColorOS 版本可能不同。若提示缺少系统文件选择器，需恢复 / 启用系统文档应用；不会退回无授权的全盘访问。

Android 5.x 不支持当前 SAF 适配器的修改时间戳操作，会返回明确失败；浏览、下载、上传、覆盖、重命名、删除仍走相同权限检查。代码与自动化检查通过不等于 OPPO 真机已验收，详见 [3.2 兼容验证](docs/validation-3.2-r7sm.md)。

## 安装与使用

1. 覆盖安装 APK，与 1.x / 2.x 保持同一应用 ID 和发布签名。
2. 选择手机上的共享文件夹，在系统选择器中允许访问。旧版本若只有读取授权，开启写入前需要重新选择并授权。
3. 设置账号密码，按需打开权限开关，点击“启动文件共享”。
4. 手机与另一台设备连接同一 Wi-Fi，或让另一台设备连接手机热点。在 **iPad Safari 或 Windows 浏览器输入手机显示的 `http://` 网页地址**，例如 `http://192.168.1.20:8080`。
5. 未开启匿名访问时，浏览器要求输入手机界面的账号密码。匿名只读时，可点击网页“账号登录”使用账号的写入权限。
6. 浏览文件夹，点击文件下载；写入权限开启后，网页显示上传、新建文件夹、删除按钮。覆盖前必须确认。iPad 可从 Safari 下载列表将文件保存到“文件”App。
7. 用完点击“停止共享”，或从前台通知中停止。权限修改需要停止后重新启动。

手机主界面的“在本机预览共享文件”可先确认目录内容；“复制连接地址”提供全部网络接口的网页和 FTP 地址。优先显示 Wi-Fi／热点地址；若该地址不可达，可从复制菜单选择同一局域网的另一个地址。地址随当前网络变化，以手机实际显示为准。

## 权限

网页、FTP 使用同一份授权目录和权限，不会因为更换协议而扩大写入权限。

| 开关 | 效果 |
| --- | --- |
| 允许账号用户上传、覆盖、删除 | 账号用户可写；关闭时账号只读 |
| 允许匿名 / 来宾访问 | 无需密码即可读取；默认关闭 |
| 同时允许匿名用户上传、删除 | 前两个开关均开启时才可开启；匿名用户可覆盖、删除 |

写入直接修改手机文件，删除不进入回收站；仅允许删除空目录。取消或中断上传可能留下不完整文件，覆盖中断可能影响原文件。只向可信局域网共享，并对重要文件保留备份。

密码不写入偏好设置；服务退出后重新打开界面会生成新密码。浏览器使用标准 HTTP Basic 身份验证，不在页面脚本中保存密码；浏览器可能缓存登录凭据，可关闭对应隐私浏览窗口结束缓存会话。错误账号不会自动降级为匿名。

## FTP

FTP 地址为 `ftp://手机IP:2121`，可自定义端口。使用 FileZilla 或支持 FTP 的文件管理器，支持主动／被动模式、断点下载、上传、追加、重命名、空目录操作。

3.1 基于 3.0 移除了 SMB 服务、认证与 RPC 实现、端口设置、地址、界面提示和相关测试依赖。仅启动网页和 FTP 两种服务；iPad 使用 Safari 网页入口。

## 文件与系统边界

- 使用 Android SAF 系统文件夹授权，所有协议限制在该目录内；不申请“所有文件访问权限”或 Root。受 Android 限制，不能访问其他应用私有数据；部分系统禁止选择存储根目录、Download 根目录或 Android/data，可选择具体子文件夹。
- 下载按流读取，网页上传按流写入，不将整个大文件加载到内存或上传到外部服务。网页支持 HTTP Range 断点下载和 Content-Length / chunked 上传。
- 网页不依赖 CDN、外部脚本或云端。文件名按文本显示；修改请求检查来源和自定义请求头，不开放跨域访问。
- HTTP 和 FTP 不提供文件内容加密，只用于可信局域网，不映射公网端口。
- 不支持完整 Windows ACL、锁、远程命令执行、跨目录重命名、符号链接共享或任意文档提供器能力。时间戳、剩余空间及写入能力依赖 Android 提供器，失败时返回错误。
- 网页最多 12 个并发连接，FTP 最多 8 个连接，闲置连接约 120 秒断开。网页默认 8080，端口不可用时尝试到 8090，最终端口显示在主界面。
- 前台通知和 CPU / Wi-Fi 锁帮助保持传输，厂商省电策略仍可能干预。Android 15 的累计后台 dataSync 时限约 6 小时，达到后停止，需手动重启。

“连接诊断”显示启动目录项数、网页请求状态、目录读取结果。不记录文件名、密码或账号。网页请求状态可帮助排查网络、认证和文件读取故障。

## 构建

JDK 17 / 21、Android SDK Platform 35 / Build Tools 35.0.0，Gradle Wrapper 8.9、AGP 8.7.3。Java 标准库兼容由 `desugar_jdk_libs` 提供，支持旧版 Android；无在线服务依赖。

```sh
export JAVA_HOME=/path/to/jdk
export ANDROID_HOME=/path/to/android-sdk
./gradlew --no-daemon --max-workers=2 :app:assembleDebug :app:assembleRelease :app:lintDebug
```

云环境使用 `./scripts/cloud-build.sh` 激活已有工具及受信代理 CA。调试包：`app/build/outputs/apk/debug/app-debug.apk`。Release 构建生成未签名包；可使用自己保管的签名密钥签署，云环境使用 `./scripts/sign-local.sh 3.3.1`。签名私钥位于仓库外，不包含在源码、APK 或下载目录中。后续更新需使用同一密钥。下载及 TLS 校验保持开启。

## 验证

[3.1 验证记录](docs/validation-3.1.md)。

```sh
python3 tests/test_ftp.py
python3 tests/test_web.py
./scripts/cloud-build.sh :app:testDebugUnitTest
./gradlew --no-daemon --max-workers=2 :app:lintDebug
```

12 项 FTP、10 项网页与权限测试，使用生产 Java 引擎与有边界检查的临时本地目录，覆盖中文读写、上传覆盖、分块上传、Range 下载、重命名删除、匿名关闭／只读／可写、错误密码不降级、越界与符号链接拦截、跨站修改拦截、重复请求头与歧义传输长度拒绝。

实际浏览器操作测试需要 Node、Playwright 与 Chromium：

```sh
python3 tests/run_browser_smoke.py
BROWSER_HOST=::1 python3 tests/run_browser_smoke.py
```

在桌面和平板尺寸下实际操作匿名只读、账号登录、新建目录、中文上传、确认覆盖、下载逐字节校验、删除；同时检查页面没有 JavaScript 错误。使用 Chromium，不代表 Safari 引擎或 iPad 真机已通过。

### Android 11 AOSP 模拟器集成验证

测试 APK 使用普通应用 UID，打开真实系统目录选择器并授予 SAF 读写权限；不伪造授权，不调用 Root。仅限可丢弃的 AOSP 模拟器。

```sh
./scripts/cloud-build.sh :app:assembleDebug :app:assembleDebugAndroidTest
adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk
adb -s emulator-5554 install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb -s emulator-5554 shell mkdir -p /sdcard/Documents/LanFTPTest
adb -s emulator-5554 push tests/fixtures/android-sample.txt /sdcard/Documents/LanFTPTest/sample.txt
adb -s emulator-5554 shell run-as com.lanftp rm -f files/smoke-ready files/smoke-stop
adb -s emulator-5554 shell am instrument -w -r com.lanftp.test/com.lanftp.SmokeInstrumentation
```

最后一条命令运行期间，在另一个终端执行：

```sh
BROWSER_SMOKE=1 ADB=/path/to/adb ANDROID_TEST_SERIAL=emulator-5554 python tests/android_host_smoke.py
```

主机通过 adb TCP 转发访问实际 Android 前台服务；文件操作仍由普通应用 UID 和真实 SAF 提供器执行。检查网页／FTP 的中文读写、跨协议逐字节校验、目录操作、匿名写入拒绝、网页 Range 下载、实际浏览器全流程和停止服务。结束后关闭测试共享、移除自己创建的转发。无 KVM 时，模拟器冷启动与系统选择器操作较慢。
