# ChatClient.py

from tcpcom import TCPClient
from entrydialog import *

def showChatDialog():
    global dlg, se1, se2, se3, btn 
    se1 = StringEntry("Text to send: ")
    se2 = StringEntry("Text received: ")
    se2.setEditable(False)
    se3 = StringEntry("Status: ")
    se3.setEditable(False)
    pane1 = EntryPane(se1, se2, se3)
    btn = ButtonEntry("Send")
    pane2 = EntryPane(btn)
    dlg = EntryDialog(pane1, pane2)
    dlg.setTitle("Clients's Chat Dialog")
    dlg.show()
        
def onStateChanged(state, msg):
    if state == TCPClient.CONNECTING:
       se3.setValue("Trying to connect to " + host)
    elif state == TCPClient.CONNECTION_FAILED:
        se3.setValue("Connection to " + host + " failed")
    elif state == TCPClient.CONNECTED:
        se3.setValue("Connected. Enter your message!")
    elif state == TCPClient.DISCONNECTED:
        se3.setValue("Server disconnected")
    elif state == TCPClient.MESSAGE:
        se2.setValue(msg)

port = 5000
host = inputString("Enter Server IP Address", False)
if host != None:
    client = TCPClient(host, port, stateChanged = onStateChanged)             
    showChatDialog()
    isConnected = client.connect(5)
    if isConnected:
        while not dlg.isDisposed():
            if btn.isTouched():
                client.sendMessage(se1.getValue())
                se1.setValue("")
client.disconnect()                
