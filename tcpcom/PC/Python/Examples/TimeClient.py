# TimeClient.py

from easygui import msgbox, enterbox
from tcpcom import TCPClient

def onStateChanged(state, msg):
    print state, msg
    if state == TCPClient.MESSAGE:
        client.disconnect()
        msgbox("Server reports local date/time: " + msg, title)
    elif state == TCPClient.CONNECTION_FAILED:
        msgbox("Server " + host + " not available", title)

title = "Time Client"
port = 5000

host= enterbox("Time Server IP Address?", title, "localhost", True)
if host != None:
    client = TCPClient(host, port, stateChanged = onStateChanged, isVerbose = True)
    client.connect()

