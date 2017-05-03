# TrainClient.py

from raspibrick import *
from tcpcom import TCPClient
import time

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

robot = Robot()
display = Display()
ledLeft = Led(LED_LEFT)
ledLeft.setColor("red")
gear = Gear()
gear.setSpeed(25)
irLeft = InfraredSensor(IR_LINE_LEFT)
irRight = InfraredSensor(IR_LINE_RIGHT)
irCenter = InfraredSensor(IR_CENTER)
r = 0.1
host = "192.168.0.4"
port = 5000
client = TCPClient(host, port, stateChanged = onStateChanged)
rc = client.connect()
isWaiting = False
display.showText("try")
if rc:
    display.showText("run")
    start = time.time()
    while not isEscapeHit():
        if isWaiting:
            continue
        vL = irLeft.getValue()
        vR = irRight.getValue()
        vC = irCenter.getValue()
        if vL == 1 and vR == 0:
            gear.forward()
        elif vL == 0 and vR == 0:   
            gear.leftArc(r)
        elif vL == 1 and vR == 1:
            gear.rightArc(r)
        if vC == 1 and time.time() - start > 5:
            client.sendMessage("go")
            display.showText("stop")
            gear.stop()
            isWaiting = True
    client.disconnect()                
robot.exit()
