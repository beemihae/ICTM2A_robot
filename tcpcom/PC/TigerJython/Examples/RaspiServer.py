# RaspiServer.py

from tcpcom import TCPServer
import time
import RPi.GPIO as GPIO

def onStateChanged(state, msg):
    print "State:", state, "Msg:", msg

P_BUTTON1 = 16 # Switch pin number
dt = 1  # 1 s period
port = 5000 # IP port

GPIO.setmode(GPIO.BOARD)
GPIO.setwarnings(False)
GPIO.setup(P_BUTTON1, GPIO.IN, GPIO.PUD_UP)
server = TCPServer(port, stateChanged = onStateChanged)
n = 0
while True:
    if server.isConnected():
        rc = GPIO.input(P_BUTTON1)
        if rc == 0:
            server.sendMessage("pressed")
            n += 1
            if n == 3:
                break
        else:
            server.sendMessage("released")
            n = 0
        time.sleep(dt)
server.terminate()
print "Server terminated"

