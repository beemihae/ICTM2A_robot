# MathServer-b.py

from tcpcom import TCPServer
import math

def onStateChanged(state, msg):
    if state == TCPServer.MESSAGE:
        try:
            exec(msg)
        except:
            server.sendMessage("illegal")
            return
        server.sendMessage(str(y))

port = 5000
server = TCPServer(port, stateChanged = onStateChanged)             
msgDlg("Math server ready for calculations. OK to stop")
server.terminate()
