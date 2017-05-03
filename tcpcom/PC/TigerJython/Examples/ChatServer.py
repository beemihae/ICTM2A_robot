# ChatServer.py

from tcpcom import TCPServer
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
    dlg.setTitle("Server's Chat Dialog")
    dlg.show()
        
def onStateChanged(state, msg):
    if state == TCPServer.PORT_IN_USE:
       se3.setValue("Port " + str(port) + " in use")
    elif state == TCPServer.LISTENING:
       se3.setValue("Waiting for a client on port " + str(port))
       se1.setValue("")
       se2.setValue("")
    elif state == TCPServer.CONNECTED:
        se3.setValue("Client connected. Enter your message!")
    elif state == TCPServer.MESSAGE:
        se2.setValue(msg)

port = 5000
server = TCPServer(port, stateChanged = onStateChanged)             
showChatDialog()
while not dlg.isDisposed():
    if btn.isTouched():
        server.sendMessage(se1.getValue())
        se1.setValue("")
server.terminate()
