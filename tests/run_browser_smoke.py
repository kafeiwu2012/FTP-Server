#!/usr/bin/env python3
import os,pathlib,subprocess
from test_web import WebTests,server
WebTests.setUpClass()
try:
 with server(WebTests.build,write=True,anonymous=True) as (root,ftp,port):
  (root/'<img onerror=alert(1)>.txt').write_text('filename must be text')
  subprocess.run(['node',str(pathlib.Path(__file__).with_name('browser_smoke.js')),str(port),os.environ.get('BROWSER_HOST','127.0.0.1')],check=True)
finally:WebTests.tearDownClass()
