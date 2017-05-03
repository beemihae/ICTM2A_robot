# TestServer.py

from tcpcom import TCPServer

def onStateChanged(state, msg):
    print state, "-", msg

port = 5000
server = TCPServer(port, stateChanged = onStateChanged, isVerbose = True)
msgDlg("OK to terminate") 
server.terminate()
