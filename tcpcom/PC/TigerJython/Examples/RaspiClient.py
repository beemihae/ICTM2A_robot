# RaspiClient.py

from tcpcom import TCPClient
import time

def onStateChanged(state, msg):
    print "State: " + state + ". Message: " + msg

host = "192.168.0.5"
#host = inputString("Host Address?")           
port = 5000 # IP port
client = TCPClient(host, port, stateChanged = onStateChanged)
rc = client.connect()
if rc:
    msgDlg("Connected. OK to terminate")
    client.disconnect()
