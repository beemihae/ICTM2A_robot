# TimeClient.py

from tcpcom import TCPClient

def onStateChanged(state, msg):
    print state, msg
    if state == TCPClient.MESSAGE:
        msgDlg("Server reports local date/time: " + msg)
    if state == TCPClient.CONNECTION_FAILED:
        msgDlg("Server " + host + " not available")

#host = inputString("Time Server IP Address?")
host = "localhost"
port = 5000
client = TCPClient(host, port, stateChanged = onStateChanged)
client.connect()


