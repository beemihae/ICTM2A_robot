# TimeServer.py

from easygui import msgbox
from tcpcom import TCPServer
import datetime

def onStateChanged(state, msg):
    print state, msg
    if state == TCPServer.CONNECTED:
        server.sendMessage(str(datetime.datetime.now()))

port = 5000
server = TCPServer(port, stateChanged = onStateChanged, isVerbose = True)
msgbox("Time server running. OK to stop","Time Server")
server.terminate()
