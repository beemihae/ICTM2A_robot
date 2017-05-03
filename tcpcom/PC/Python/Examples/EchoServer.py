# EchoServer.py

from easygui import msgbox
from tcpcom import TCPServer

def onStateChanged(state, msg):
    if state == TCPServer.MESSAGE:
        print "Received msg:", msg, " - echoing it..."
        server.sendMessage(msg)

port = 5000
server = TCPServer(port, stateChanged = onStateChanged)
msgbox("Echo server running. OK to stop","Echo Server")
server.terminate()
