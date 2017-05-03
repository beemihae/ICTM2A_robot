# EchoClient.py

from tcpcom import TCPClient
from entrydialog import *
import time

def onStateChanged(state, msg):
    print state, msg
    if state == TCPClient.MESSAGE:
        status.setValue("Reply: " + msg)
    if state == TCPClient.DISCONNECTED:
        status.setValue("Server died")
        
def showStatusDialog():
    global dlg, btn, status
    status = StringEntry("Status: ")
    status.setEditable(False)
    pane1 = EntryPane(status)
    btn = ButtonEntry("Finish")
    pane2 = EntryPane(btn)
    dlg = EntryDialog(pane1, pane2)
    dlg.setTitle("Client Information")
    dlg.show()
        
host = "localhost"
port = 5000
showStatusDialog()
client = TCPClient(host, port, stateChanged = onStateChanged)
status.setValue("Trying to connect to " + host + ":" + str(port) + "...")
time.sleep(2)
rc = client.connect()
if rc: 
    time.sleep(2)
    n = 0
    while not dlg.isDisposed():
        if client.isConnected():
            status.setValue("Sending: " + str(n))
            time.sleep(0.5)
            client.sendMessage(str(n), 5)  # block for max 5 s
            n += 1
        if btn.isTouched():
            dlg.dispose()
        time.sleep(0.5)    
    client.disconnect()
else:
    status.setValue("Connection failed.")
