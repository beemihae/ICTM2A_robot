# TestServer.py

from tcpcom import TCPClient
from easygui import msgbox  # Standard Python with easygui

def onStateChanged(state, msg):
    print state, "-", msg

host = "localhost"
port = 5000

client = TCPClient(host, port, stateChanged = onStateChanged, isVerbose = True)
success = client.connect()
if success:
    msgbox("Client connected. OK to disconnect","Test Client")  # Standard Python
    client.sendMessage("Test Client says Hello To You!")
    client.disconnect()
else:
    msgbox("Client connection failed. OK to quit","Test Client")   # Standard Python
