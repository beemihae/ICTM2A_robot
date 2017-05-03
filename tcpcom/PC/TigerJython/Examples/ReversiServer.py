# ReversiServer.py

from gamegrid import *
from tcpcom import TCPServer
import reversilib
      
def onMousePressed(e):
    global isMyMove, location
    if not isMyMove or isOver:
          return
    location = toLocationInGrid(e.getX(), e.getY())
    if getOneActorAt(location) == None and reversilib.hasNeighbours(location):
        stone = Actor("sprites/token.png", 2)
        addActor(stone, location)
        stone.show(1)
        reversilib.checkStones(1, location)
        refresh()
        server.sendMessage("" + str(location.x) + str(location.y)) # send location
        isMyMove = False
        setStatusText("Wait!")
        if len(getOccupiedLocations()) == 64:    
           reversilib.endOfGame()
           server.sendMessage("end")

def onNotifyExit():
    server.terminate()
    dispose()
    
def onStateChanged(state, msg):
    global isMyMove,imegeID, location
    if state == TCPServer.PORT_IN_USE:
        setStatusText("TCP port occupied. Restart IDE.")
    if state == TCPServer.LISTENING:
        setStatusText("Waiting for a partner to play")        
    if state == TCPServer.CONNECTED:
        setStatusText("Client connected. Wait for partner's move!")
    elif state == TCPServer.MESSAGE: 
        if msg == "end":
            reversilib.endOfGame()
        else:
            x = int(msg[0])
            y = int(msg[1])
            location = Location(x, y)            
            stone = Actor("sprites/token.png", 2)
            addActor(stone, location)          
            stone.show(0)
            reversilib.checkStones(0, location)
            refresh()        
            isMyMove = True
            setStatusText("Make your move!")
 
isMyMove = False   
makeGameGrid(8, 8, 60, Color.gray, False, mousePressed = onMousePressed, notifyExit = onNotifyExit)
addStatusBar(30)
reversilib.initGame()
show()
port = 5000
server = TCPServer(port, stateChanged = onStateChanged)
isOver = False




