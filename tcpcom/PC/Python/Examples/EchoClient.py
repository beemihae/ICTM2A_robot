# EchoClient.py

from easygui import msgbox, enterbox
from tcpcom import TCPClient
import time

def onStateChanged(state, msg):
    if state == TCPClient.MESSAGE:
        print msg

title = "Echo Client"
port = 5000

host= enterbox("Echo Server IP Address?", title, "localhost", True)
if host != None:
    client = TCPClient(host, port, stateChanged = onStateChanged)
    client.connect()
    for i in range(100):
        client.sendMessage(str(i))
        time.sleep(0.1)
    client.disconnect()

