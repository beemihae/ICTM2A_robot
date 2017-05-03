# ReversiClient.py

from gamegrid import *
from tcpcom import TCPClient
import reversilib                                                                            
      
def onMousePressed(e):
    global isMyMove, location
    if not isMyMove or isOver:
        return
    location = toLocationInGrid(e.getX(), e.getY())
    if getOneActorAt(location) == None and reversilib.hasNeighbours(location):
        stone = Actor("sprites/token.png", 2)
        addActor(stone, location)
        stone.show(0)
        reversilib.checkStones(0, location)
        refresh()
        client.sendMessage(str(location.x) + str(location.y)) # send location
        isMyMove = False
        setStatusText("Wait!")
        if len(getOccupiedLocations()) == 64:    
           reversilib.endOfGame()
           client.sendMessage("end")

def onNotifyExit():
    client.disconnect()
    dispose()
   
def onStateChanged(state, msg):
    global isMyMove, imegeID, location
    if state == TCPClient.CONNECTED:
        setStatusText("Connection established. You play!")
        isMyMove = True
    elif state == TCPClient.CONNECTION_FAILED:
        setStatusText("Connection failed")
    elif state == TCPClient.DISCONNECTED:
        setStatusText("Server died")
        isMyMove = False
    elif state == TCPClient.MESSAGE:                       
        if msg == "end":
           reversilib.endOfGame()
        else:
            x = int(msg[0])
            y = int(msg[1])
            location = Location(x, y)           
            stone = Actor("sprites/token.png", 2)
            addActor(stone, location)         
            stone.show(1)
            reversilib.checkStones(1, location)
            refresh()
            isMyMove = True
            setStatusText("Make your move!")
 
isMyMove = False  
makeGameGrid(8, 8, 60, Color.gray, False, mousePressed = onMousePressed, notifyExit = onNotifyExit)
addStatusBar(30)
reversilib.initGame()
show()
host = "localhost"
port = 5000
client = TCPClient(host, port, stateChanged = onStateChanged)
client.connect()
isOver = False
