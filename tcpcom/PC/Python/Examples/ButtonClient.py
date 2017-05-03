# ButtonClient.py


from tcpcom import TCPClient
from easygui import msgbox, enterbox

def onStateChanged(state, msg):
    print "State: " + state + ". Message: " + msg

port = 5000 # IP port
host= enterbox("Button Server IP Address?", "Button Client", "", True)
if host != None:
    client = TCPClient(host, port, stateChanged = onStateChanged)
    rc = client.connect()
    if rc:
        msgbox("Button client running. OK to stop","Button Client")
        client.disconnect()