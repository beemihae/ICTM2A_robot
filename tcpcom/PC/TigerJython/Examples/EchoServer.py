# EchoServer.py

from tcpcom import TCPServer

def onStateChanged(state, msg):
    print state, msg
    if state == TCPServer.MESSAGE:
        server.sendMessage(msg)

port = 5000
server = TCPServer(port, stateChanged = onStateChanged)             
msgDlg("Echo Server running. OK to stop")
server.terminate()
