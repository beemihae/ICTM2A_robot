# TimeServer.py

from tcpcom import TCPServer
import datetime
        
def onStateChanged(state, msg):
    print state, msg
    if state == TCPServer.CONNECTED:
        server.sendMessage(str(datetime.datetime.now()))
        server.disconnect()

port = 5000
server = TCPServer(port, stateChanged = onStateChanged)             
msgDlg("Time Server running. OK to stop")
server.terminate()
