# DotServer.py

from gturtle import *
from tcpcom import TCPServer
import random
import time

def onMouseHit(x, y):
    global isMouseEnabled, serverReady
    if not isMouseEnabled:
        return
    if (x - xPrey) * (x - xPrey) + (y - yPrey) * (y - yPrey) < 400:
        reaction = time.time() - startTime
        setStatusText("My reaction time: " + str(round(reaction, 2)) + " s")
        clean()
        isMouseEnabled = False
        serverReady = True
        
def onStateChanged(state, msg):
    global serverReady, clientReady
    if state == TCPServer.LISTENING:
        setStatusText("Waiting for game partner...")
    if state == TCPServer.CONNECTED:
        setStatusText("Partner entered my game room")
        serverReady = True
        clientReady = True
    if state == TCPServer.MESSAGE:
        setStatusText(msg)
        clientReady = True
 
def onCloseClicked():
    global isGameServerRunning
    isGameServerRunning = False
    
readyForNextTurn = False
isMouseEnabled = False
makeTurtle(mouseHit = onMouseHit, closeClicked = onCloseClicked)
addStatusBar(30)
hideTurtle()
setCustomCursor("sprites/cathead.png")
port = 5000
server = TCPServer(port, stateChanged = onStateChanged)

# Game Supervisor
isGameServerRunning = True
serverReady = False
clientReady = False
while isGameServerRunning:
    if serverReady and clientReady:
        time.sleep(4)
        serverReady = False
        clientReady = False
        xPrey = random.randint(-300, 300)
        yPrey = random.randint(-300, 300)
        server.sendMessage("setPos(" + str(xPrey) + ", " + str(yPrey) + ")")
        setPos(xPrey, yPrey)
        drawImage("sprites/mouse.gif")
        startTime = time.time()
        isMouseEnabled = True
server.terminate()
dispose()