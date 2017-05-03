# StatClient.py

import random
import time
from entrydialog import *
from tcpcom import TCPClient

# number of throws per slice
sliceSize = 2000000

def showStatusDialog():
    global dlg, local, remote, locresult
    local = StringEntry("Local Status: ")
    local.setEditable(False)
    remote = StringEntry("Remote Status: ")
    remote.setEditable(False)
    pane1 = EntryPane(local, remote)
    locresult = StringEntry("Local Result: ")
    locresult.setEditable(False)
    pane2 = EntryPane(locresult)
    dlg = EntryDialog(pane1, pane2)
    dlg.setTitle("Client Information")
    dlg.show()

def onStateChanged(state, msg):
    if state == TCPClient.CONNECTION_FAILED:
        local.setValue("Connection failed")
    elif state == TCPClient.DISCONNECTED:
        local.setValue("Connection broken")
        remote.setValue("Disconnected")
    elif state == TCPClient.CONNECTED:
        local.setValue("Working...")
        remote.setValue("Connected. Working...")

#host = "192.168.0.102"
host = "localhost"
port = 5000
client = TCPClient(host, port, stateChanged = onStateChanged)             
showStatusDialog()
local.setValue("Trying to connect...")
remote.setValue("Disconnected")
locresult.setValue("(n/a)")
rc = client.connect()
if rc:
    k = 0
    n = 0
    startTime = time.clock()
    while not dlg.isDisposed() and client.isConnected():
        zx = random.random()
        zy = random.random()
        if zx * zx + zy * zy < 1:
            k += 1
        n += 1
        if n % sliceSize == 0:
            pi = 4 * k/n
            t = time.clock() - startTime
            info = "n: %d; k: %d; pi: %f. Time: %3.1f" %(n, k, pi, t)
            locresult.setValue(info)
            client.sendMessage(str(n) + ";" + str(k))
client.disconnect()