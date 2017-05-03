# TestServer.py

from tcpcom import TCPServer
from easygui import msgbox  # Standard Python with easygui

def onStateChanged(state, msg):
    print state, "-", msg

port = 5000
server = TCPServer(port, stateChanged = onStateChanged, isVerbose = True)
msgbox("Server running. OK to stop", "Test Server")  # Standard Python
server.terminate()
