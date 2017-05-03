# ElevatorClient.py

from tcpcom import TCPClient
from gturtle import *
import random

def showStatus(msg):
    if not isDisposed():
        setStatusText(msg)       
    
def onStateChanged(state, msg):
    global eState
    if state == TCPClient.CONNECTED:
        showStatus("Connected to " + msg)
    if state == TCPClient.DISCONNECTED:
        showStatus("Connection lost")
    if state == TCPClient.MESSAGE:
        if msg == "Go":
            eState = "UPWARD"

def onCloseClicked():
    client.disconnect()
    dispose()

host = inputString("Hostaddress?")
makeTurtle(closeClicked = onCloseClicked)
addStatusBar(30)
penUp()
hideTurtle()
back(200)
img0 = "sprites/elevator_0.png"
img1 = "sprites/elevator_3.png"
drawImage(img0)
port = 5000
client = TCPClient("localhost", port, stateChanged = onStateChanged)
rc = client.connect()
if rc:
    eState = "UPWARD"
    enableRepaint(False)
    while not isDisposed():
        if eState == "UPWARD":
            clear()
            drawImage(img1)
            repaint()
            forward(4)
            delay(50)             
            if getY() > 200:
                delay(random.randint(2000, 3000))
                client.sendMessage("Go")
                eState = "DOWNWARD"
        elif eState == "DOWNWARD":
            clear()
            drawImage(img0)
            repaint()
            back(20)             
            delay(50)             
            if getY() < -200:
                eState = "STOPPED"
        elif eState == "STOPPED":
            delay(100)
else:
    showStatus("Connection failed")    
