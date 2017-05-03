# DotClient.py

from gturtle import *
from tcpcom import TCPClient
import time

def onMouseHit(x, y):
    global isMouseEnabled
    if not isMouseEnabled:
        return
    if (x - xPrey) * (x - xPrey) + (y - yPrey) * (y - yPrey) < 400:
        reaction = time.time() - startTime
        setStatusText("My reaction time: " + str(round(reaction, 2)) + " s")
        clean()
        isMouseEnabled = False
        client.sendMessage("Client reaction time: " + str(round(reaction, 2)) + " s")
                
def onStateChanged(state, msg):
    global startTime, isMouseEnabled, xPrey, yPrey
    if state == TCPClient.CONNECTING:
        setStatusText("Trying to connect...")
    if state == TCPClient.CONNECTION_FAILED:
        setStatusText("Connection failed")
    if state == TCPClient.CONNECTED:
        setStatusText("Connection established")
    if state == TCPClient.MESSAGE:
        if "setPos" in msg:        
            exec(msg)
            xPrey = getX()
            yPrey = getY()
            drawImage("sprites/mouse.gif")
            startTime = time.time()
            isMouseEnabled = True

def onCloseClicked():
    client.disconnect()
    dispose()
    
makeTurtle(mouseHit = onMouseHit, closeClicked = onCloseClicked)
addStatusBar(30)
hideTurtle()
isMouseEnabled = False
setCustomCursor("sprites/cathead.png")
host = "localhost"
port = 5000
client = TCPClient(host, port, stateChanged = onStateChanged)
client.connect()



