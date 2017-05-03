# PearlClient.py

from gamegrid import *
from tcpcom import TCPClient
import pearlshare
    
def onMousePressed(e):
    pearlshare.handleMousePress(e, client)

def onMouseReleased(e):
    pearlshare.handleMouseRelease(e)
                        
def onNotifyExit():
    client.disconnect()
    dispose()
   
def onStateChanged(state, msg):
    if state == TCPClient.CONNECTED:
        setStatusText("Connection established. Remove any number of pearls from same row and click OK!")
        pearlshare.isMyMove = True
    elif state == TCPClient.CONNECTION_FAILED:
        setStatusText("Connection failed")
    elif state == TCPClient.SERVER_OCCUPIED:
        setStatusText("Server occupied")
    elif state == TCPClient.DISCONNECTED:
        setStatusText("Connection lost")
        pearlshare.isMyMove = False
    elif state == TCPClient.MESSAGE:                       
        pearlshare.handleMessage(msg)
 
makeGameGrid(8, 6, 70, False, mousePressed = onMousePressed, mouseReleased = onMouseReleased, 
                              notifyExit = onNotifyExit)
addStatusBar(30)
pearlshare.initGame()
show()
host = "localhost"
port = 5000
client = TCPClient(host, port, stateChanged = onStateChanged)
client.connect()
