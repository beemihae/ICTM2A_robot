# ShipServer2.py
# 2 dim, GameGrid

from gamegrid import *
from tcpcom import TCPServer
import shiplib

def onMousePressed(e):
     loc = toLocationInGrid(e.getX(), e.getY())
     shiplib.handleMousePress(server, loc)                                                                                                                                     

def onStateChanged(state, msg):
    global first        
    if state == TCPServer.PORT_IN_USE:
        setStatusText("TCP port occupied. Restart IDE.")
    elif state == TCPServer.LISTENING:
        setStatusText("Waiting for a partner to play")
        if first:
            first = False
        else:
            removeAllActors()
            for i in range(shiplib.nbShips):
                addActor(shiplib.Ship(), getRandomEmptyLocation())
    elif state == TCPServer.CONNECTED:
        setStatusText("Client connected. Wait for partner's move!")
    elif state == TCPServer.MESSAGE:
        shiplib.handleMessage(server, state, msg)

def onNotifyExit():
    server.terminate()
    dispose()

makeGameGrid(6, 6, 50, Color.red, False, mousePressed = onMousePressed, notifyExit = onNotifyExit)
addStatusBar(30)
for i in range(shiplib.nbShips):
    addActor(shiplib.Ship(), getRandomEmptyLocation())
show()
port = 5000
first = True
server = TCPServer(port, stateChanged = onStateChanged)
shiplib.node = server



