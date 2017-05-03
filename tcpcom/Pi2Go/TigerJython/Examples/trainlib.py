# trainlib.py

from raspibrick import *

def initTrain():
    global irLeft, irRight, irCenter, gear
    robot = Robot()
    ledLeft = Led(LED_LEFT)
    ledLeft.setColor("red")
    gear = Gear()
    gear.setSpeed(25)
    irLeft = InfraredSensor(IR_LINE_LEFT)
    irRight = InfraredSensor(IR_LINE_RIGHT)
    irCenter = InfraredSensor(IR_CENTER)

def runTrain(node): 
    global isWaiting       
    isWaiting = True
    r = 0.1
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
            node.sendMessage("go")
            gear.stop()
            isWaiting = True
    robot.exit()
                