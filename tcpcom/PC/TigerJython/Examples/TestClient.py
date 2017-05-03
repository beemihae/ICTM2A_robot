# TestServer.py

from tcpcom import TCPClient

def onStateChanged(state, msg):
    print state, "-", msg

host = "localhost"
port = 5000

client = TCPClient(host, port, stateChanged = onStateChanged, isVerbose = True)
success = client.connect()
if success:
    msgDlg("OK to terminate")
    client.sendMessage("Test Client says Hello To You!")
    client.disconnect()
else:
    msgDlg("OK to terminate")
