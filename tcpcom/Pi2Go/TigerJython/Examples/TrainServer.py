# TrainServer.py

from raspibrick import *
from tcpcom import TCPServer

def onStateChanged(state, msg):
    global isWaiting, start
    if state == TCPServer.LISTENING:
        ledLeft.setColor("red")
        display.showText("stop")
        gear.stop()
        isWaiting = True
    if state == TCPServer.CONNECTED:
        ledLeft.setColor("green")
    if state == TCPServer.MESSAGE:
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
port = 5000
server = TCPServer(port, stateChanged = onStateChanged)
start = time.time()
isWaiting = True
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
        server.sendMessage("go")
        display.showText("stop")
        gear.stop()
        isWaiting = True
                
server.terminate()        
robot.exit()
