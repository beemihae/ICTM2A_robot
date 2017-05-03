# TCPGearC.py

from tcpcom import TCPClient
from trainlib import *

def onStateChanged(state, msg):
    global isWaiting, start
    if state == TCPClient.CONNECTED:
        ledLeft.setColor("green")
    if state == TCPClient.DISCONNECTED:
        ledLeft.setColor("red")
        gear.stop()
    if state == TCPClient.MESSAGE:
        isWaiting = False
        start = time.time()
        display.showText("run")

host = "192.168.0.4"
port = 5000
client = TCPClient(host, port, stateChanged = onStateChanged)
rc = client.connect()
    initTrain()
    runTrain(client)
    client.disconnect()                
