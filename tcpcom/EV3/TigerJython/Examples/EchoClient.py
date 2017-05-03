# EchoClient.py

from ev3robot import *
import time

def onStateChanged(state, msg):
    if state == TCPClient.MESSAGE:
        robot.drawString(msg, 0, 4)
    elif state == TCPClient.CONNECTED:
        robot.drawString("Connected", 0, 2)
    elif state == TCPClient.DISCONNECTED:
        robot.drawString("Disconnected", 0, 2)
 
robot = LegoRobot()
robot.drawString("Echo Client", 0, 1)
host = "192.168.0.17"
port = 5000
client = TCPClient(host, port, stateChanged = onStateChanged)
if client.connect():
    robot.drawString("Connected", 0, 2)
else:
    robot.drawString("Connection failed", 0, 2)
n = 0
while not robot.isEscapeHit():
    client.sendMessage(str(n))
    n += 1
    time.sleep(0.1)
client.disconnect()     
robot.exit()