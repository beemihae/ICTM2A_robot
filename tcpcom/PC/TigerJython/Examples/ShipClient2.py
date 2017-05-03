# ShipClient2.py
# 2 dim, GameGrid

from gamegrid import *
from tcpcom import TCPClient
import shiplib

def onMousePressed(e):
     loc = toLocationInGrid(e.getX(), e.getY())
     shiplib.handleMousePress(client, loc)
     
def onStateChanged(state, msg):    
    if state == TCPClient.CONNECTED:
        setStatusText("Connection established. You fire!")
        shiplib.isMyMove = True
    elif state == TCPClient.CONNECTION_FAILED:
        setStatusText("Connection failed")
    elif state == TCPClient.DISCONNECTED:
        setStatusText("Server died")
        shiplib.isMyMove = False
    elif state == TCPClient.MESSAGE:
        shiplib.handleMessage(client, state, msg)

def onNotifyExit():
    client.disconnect()
    dispose()

makeGameGrid(6, 6, 50, Color.red, False, 
    mousePressed = onMousePressed, notifyExit = onNotifyExit)
addStatusBar(30)
for i in range(shiplib.nbShips):
    addActor(shiplib.Ship(), getRandomEmptyLocation())
show()
host = "localhost"
port = 5000
client = TCPClient(host, port, stateChanged = onStateChanged)
client.connect()
