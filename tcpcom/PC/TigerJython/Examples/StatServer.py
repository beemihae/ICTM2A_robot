# StatServer.py

import random
import time
from entrydialog import *
from tcpcom import TCPServer

# number of throws per slice
sliceSize = 2000000

def showStatusDialog():
    global dlg, local, remote, locresult, remresult, totresult
    local = StringEntry("Local Status: ")
    local.setEditable(False)
    remote = StringEntry("Remote Status: ")
    remote.setEditable(False)
    pane1 = EntryPane(local, remote)
    locresult = StringEntry("Local Result: ")
    locresult.setEditable(False)
    remresult = StringEntry("Remote Result: ")
    remresult.setEditable(False)
    totresult = StringEntry("Total Result: ")
    totresult.setEditable(False)
    pane2 = EntryPane(locresult, remresult, totresult)
    dlg = EntryDialog(pane1, pane2)
    dlg.setTitle("Server Information")
    dlg.show()

def onStateChanged(state, msg):
    global remote_n, remote_k
    if state == TCPServer.LISTENING:
        local.setValue("Waiting")
        remote.setValue("Not connected")
    elif state == TCPServer.CONNECTED:
        remote.setValue("Connected. Working...")
    elif state == TCPServer.TERMINATED:
        local.setValue("Terminated");
        remote.setValue("Not connected.");
    elif state == TCPServer.MESSAGE:
        li = msg.split(";")
        n = int(li[0])
        k = int(li[1])
        pi = 4 * k / n
        info = "n: %d; k: %d; pi: %f" %(n, k, pi)
        remresult.setValue(info)
        remote_n = n
        remote_k = k
        showTotal()

def showTotal():
    n = remote_n + local_n
    k = remote_k + local_k
    pi = 4 * k / n
    info = "n: %d; k: %d; pi: %f" %(n, k, pi)
    totresult.setValue(info)

local_k = 0
local_n = 0
remote_k = 0
remote_n = 0
port = 5000
server = TCPServer(port, stateChanged = onStateChanged)             
showStatusDialog()
local.setValue("Waiting for connection...")
locresult.setValue("(n/a)")
remresult.setValue("(n/a)")
totresult.setValue("(n/a)")
while not server.isConnected() and not dlg.isDisposed():
    continue
if dlg.isDisposed():
    server.terminate()

local.setValue("Working...")
n = 0
k = 0
startTime = time.clock()
while not dlg.isDisposed() and server.isConnected():
    zx = random.random()
    zy = random.random()
    if zx * zx + zy * zy < 1:
        k += 1
    n += 1
    if n % sliceSize == 0:
        pi = 4 * k / n
        t = time.clock() - startTime
        info = "n: %d; k: %d; pi: %f; t: %3.1f" %(n, k, pi, t)
        locresult.setValue(info)
        local_n = n
        local_k = k
        showTotal()
server.terminate()
