# MathClient.py

from tcpcom import TCPClient
from entrydialog import *
import time

def onStateChanged(state, msg):
    if state == TCPClient.MESSAGE:
        print "Result:", msg
        
host = "localhost"
port = 5000
client = TCPClient(host, port, stateChanged = onStateChanged)
client.connect()
term = ""
while True:
    term = inputString("Enter your math function", False)
    if term == None:
        break
    client.sendMessage("y = math." + str(term))
client.disconnect()
