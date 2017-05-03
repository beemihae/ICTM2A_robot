# ElevatorServer.py

from tcpcom import TCPServer
from gturtle import *
import random

def showStatus(msg):
    if not isDisposed():
        setStatusText(msg)       
    
def onStateChanged(state, msg):
    global eState
    if state == TCPServer.CONNECTED:
        showStatus("Connected to " + msg)
    if state == TCPServer.LISTENING:
        showStatus("Listening")
    if state == TCPServer.MESSAGE:
        if msg == "Go":
            eState = "UPWARD"

def onCloseClicked():
    server.terminate()
    dispose()

makeTurtle(closeClicked = onCloseClicked)
addStatusBar(30)
penUp()
hideTurtle()
back(200)
img0 = "sprites/elevator_0.png"
img1 = "sprites/elevator_1.png"
img2 = "sprites/elevator_2.png"
drawImage(img0)
port = 5000
server = TCPServer(port, stateChanged = onStateChanged)
eState = "STOPPED"
enableRepaint(False)
while not isDisposed():
    if eState == "UPWARD":
        clear()
        drawImage(img1)
        if getY() > 0:
            delay(random.randint(1000, 5000))
            eState = "UPWARD1"
        repaint()
        forward(4)
        delay(50)             
    if eState == "UPWARD1":
        clear()
        drawImage(img2)
        if getY() > 200:
            server.sendMessage("Go")
            eState = "DOWNWARD"
        repaint()
        forward(4)
        delay(50)             
    elif eState == "DOWNWARD":
        clear()
        drawImage(img0)
        repaint()
        back(20)             
        delay(50)             
        if getY() < -200:
            eState = "STOPPED"
    elif eState == "STOPPED":
        delay(1000)

