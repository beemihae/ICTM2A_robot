# TrainServer.py

from tcpcom import TCPServer
from trainlib import *

def onStateChanged(state, msg):
    global isWaiting, start
    if state == TCPServer.LISTENING:
        ledLeft.setColor("red")
        gear.stop()
        isWaiting = True
    if state == TCPServer.CONNECTED:
        ledLeft.setColor("green")
    if state == TCPServer.MESSAGE:
        isWaiting = False

initTrain()
port = 5000
server = TCPServer(port, stateChanged = onStateChanged)
runTrain(server)                
server.terminate()        
