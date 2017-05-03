# PearlServer.py

from gamegrid import *
from tcpcom import TCPServer
import pearlshare
                                                                          
def onMousePressed(e):
    pearlshare.handleMousePress(e, server)

def onMouseReleased(e):
    pearlshare.handleMouseRelease(e)

def onNotifyExit():
    server.terminate()
    dispose()
    
def onStateChanged(state, msg):
    global gameStarted, partnerLost
    if state == TCPServer.PORT_IN_USE:
        setStatusText("TCP port occupied. Restart IDE.")
    elif state == TCPServer.LISTENING:
        if gameStarted:
            setStatusText("Connection lost. Restart server.")
            pearlshare.isMyMove = False
            server.terminate()
        else:
            setStatusText("Waiting for a partner to play")
    elif state == TCPServer.CONNECTED:
        if gameStarted:
            return
        setStatusText("Client connected. Wait for partner's move!")
        gameStarted = True
    elif state == TCPServer.MESSAGE: 
        pearlshare.handleMessage(msg)    
 
makeGameGrid(8, 6, 70, False, mousePressed = onMousePressed, mouseReleased = onMouseReleased, 
                              notifyExit = onNotifyExit)
addStatusBar(30)
pearlshare.initGame()
show()
gameStarted = False
port = 5000
server = TCPServer(port, stateChanged = onStateChanged)
server.terminate()