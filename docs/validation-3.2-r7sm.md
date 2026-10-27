# 3.2：OPPO R7sm / Android 5.1.1 兼容验证

目标设备由用户确认为 OPPO R7sm、Android 5.1.1（API 22）。最低支持版本为 Android 5.0（API 21），targetSdk 保持 35。

## 兼容修改

- Android 8.0 以下使用 `startService`，服务仍立即进入前台；通知频道和双参数通知 Builder 仅在 API 26+ 使用。
- 通知管理使用旧系统支持的字符串服务名；停止通知使用兼容重载；不可变 PendingIntent 标志仅在 API 23+ 添加。
- Android 5.x 使用深色状态栏，避免浅色背景配白色图标；目录选择器仅在 API 26+ 传入初始目录 URI。
- 启用标准 Java 库 desugaring，覆盖 Base64、集合排序、比较器、String.join 等旧系统缺失的 API。已检查应用 DEX 中 Basic 认证解码引用重写为 `j$/util/Base64`。
- API 26 以下修改文件时间戳返回明确失败，不触发缺失 API；不绕过 SAF 授权边界。
- 为旧系统添加 ColorOS 后台运行提示；不新增全盘存储权限。

## 本次执行结果

| 检查 | 结果 |
| --- | --- |
| Debug / 未签名 Release / AndroidTest APK 构建 | 通过 |
| Robolectric 界面启动、持久目录授权请求、服务创建、前台通知与停止 | API 21、22、26，共 6 项，通过；非实体设备测试 |
| 生产 FTP 引擎回归 | 12 / 12 通过 |
| 生产网页引擎、跨协议读写和权限回归 | 10 / 10 通过 |
| Chromium 实际操作 | desktop、tablet 均通过 |
| Lint | 0 错误、1 警告：Android 5.x 忽略 usesCleartextTraffic 属性；旧系统本身允许明文 HTTP，属于预期 |
| APK 元数据 | versionCode 7、versionName 3.2、minSdk 21、targetSdk 35 |
| Debug APK 签名 | apksigner 验证通过，包含 v1 与 v2；Android 5.1.1 使用 v1 |
| OPPO 真机 / ColorOS 后台行为 / 真实 SAF 提供器 | 未运行，需设备验收 |

复现：

```sh
./scripts/cloud-build.sh :app:assembleDebug :app:assembleRelease :app:assembleDebugAndroidTest :app:testDebugUnitTest :app:lintDebug
python3 tests/test_ftp.py
python3 tests/test_web.py
python3 tests/run_browser_smoke.py
```

云环境执行 Python 测试前使用 `source /workspace/tools/activate.sh` 激活 JDK 与 Playwright 路径。

## 安装与真机验收

交付的是调试签名测试 APK，不使用或包含历史发布私钥。它不能保证覆盖历史发布版；签名不同时需先备份应用设置，再卸载旧应用后安装，或使用原发布密钥签署 Release 构建。卸载后需重新选择共享目录和配置权限。历史 3.1 APK 不支持 Android 5.1.1。

在 R7sm 上确认：安装打开、系统目录授权、开启账号读写共享、另一台设备网页登录与 FTP 登录、中文文件上传下载、覆盖重命名删除、通知停止、锁屏后传输。ColorOS 后台限制由系统管理，需要用户允许后台运行并锁定最近任务。若系统文档选择器被禁用，应恢复系统组件。

浏览器测试使用 Chromium，不代表 R7sm 内置旧版浏览器或 Safari 已通过。仓库 downloads 目录提供调试签名测试包及 SHA-256 校验；这不是使用历史发布密钥签署的正式升级包。
