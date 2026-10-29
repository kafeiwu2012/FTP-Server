# 3.3：HTTP IPv6

HTTP 服务继续使用双栈环境中的通配监听，允许 IPv4 和 IPv6 TCP 连接。原有 Host 检查仅允许 IPv4；本次增加严格校验的方括号 IPv6 字面地址，端口范围为 1–65535（也可省略）。不解析客户端 Host 的 DNS，不允许任意域名、歧义地址或带接口 zone 的地址。

主界面分别显示第一个可用的 IPv4 与 IPv6 网页地址；复制菜单提供各接口的全部可用地址。IPv6 全局地址和 ULA 私有地址使用 `http://[地址]:端口` 形式；过滤回环、未指定、组播和链路本地 IPv6 地址。FTP 菜单保留 IPv4 地址。

版本：versionCode 8 / versionName 3.3；继续支持 Android 5.0+，保留 OPPO R7sm / Android 5.1.1 兼容分支。

## 验证

- `python3 tests/test_web.py`：12 项通过。其中一个测试实际连接 `::1`，验证未登录 / 错误密码拒绝、网页、中文目录、二进制上传、IPv6 下载、IPv4 交叉下载、Range、同源删除与跨站修改拒绝；另一个覆盖合法 IPv6 / IPv4 Host 与畸形 Host 拒绝。
- `python3 tests/test_ftp.py`：12 项通过。
- `python3 tests/run_browser_smoke.py` 和 `BROWSER_HOST=::1 python3 tests/run_browser_smoke.py`：IPv4 / IPv6 各自的 desktop 与 tablet 全流程通过，包括登录、目录、上传、覆盖、逐字节下载校验和删除。
- `:app:testDebugUnitTest`：7 项通过，包含 API 22 地址筛选和 IPv6 URL 方括号格式检查，以及 API 21 / 22 / 26 原有界面与通知回归。
- Debug、未签名 Release、AndroidTest APK 构建通过；Lint 无错误，保留旧版系统忽略 usesCleartextTraffic 属性的预期警告。

## 范围

本次在云环境中通过真实 IPv6 回环 TCP 与 Chromium 验证，不代表手机网络的 IPv6 路由、防火墙、运营商接入或 OPPO 真机已验证。访问双方必须有 IPv6 连通性；本功能不配置路由器、端口映射或公网访问策略。仍保留账号、匿名和 SAF 文件授权权限检查，并拒绝不同 Origin 的写操作。IPv6 HTTP 与 IPv4 HTTP 一样不加密。

`fe80::` 链路本地地址依赖访问端接口标识，浏览器支持不一致，本次不作为网页访问地址提供。手机如果没有全局 / ULA IPv6 地址，仍使用 IPv4；地址变化后应重新复制当前显示的地址。
