# EchoServer.py

from ev3robot import *

def onStateChanged(state, msg):
    if state == TCPServer.LISTENING:
        robot.drawString("Listening", 0, 2)
    elif state == TCPServer.CONNECTED:
        robot.drawString("Connected", 0, 2)
    elif state == TCPServer.MESSAGE:
        robot.drawString(msg, 0, 4)
        server.sendMessage(msg)
 
robot = LegoRobot()
robot.drawString("Echo Server", 0, 1)
port = 5000
server = TCPServer(port, stateChanged = onStateChanged)
while not robot.isEscapeHit():
    continue
server.terminate()     
robot.exit()