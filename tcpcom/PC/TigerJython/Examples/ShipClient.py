# ShipClient.py
# 1 dim, Turtle Graphics

from gturtle import *
import random
from tcpcom import TCPClient

def initGame():    
    for x in range(-250, 250, 50):   
        setPos(x, 0)
        setFillColor("gray")
        startPath()
        repeat 4:
            forward(50)
            right(90)
        fillPath()    

def createShips():
    setFillColor("green")
    li = random.sample(range(1, 10), 4) # 4 unique random numbers 1..10
    for i in li:       
        fill(-275 + i * 50, 25)    

def onMouseHit(x, y):
    global isMyTurn 
    setPos(x, y) 
    if getPixelColorStr() == "white" or isOver or not isMyTurn:
        return     
    client.sendMessage("setPos(" + str(x) + "," + str(y) + ")")
    isMyTurn = False  

def onCloseClicked():
    client.disconnect()
    dispose()
    
def onStateChanged(state, msg):
    global isMyTurn, myHits, partnerHits    
    if state == TCPClient.DISCONNECTED:
        setStatusText("Partner disappeared")
        initGame()
    elif state == TCPClient.MESSAGE:
        if msg == "hit":
            myHits += 1
            setStatusText("Hit! Partner's remaining fleet size " + str(4 - myHits))
            if myHits == 4:
                setStatusText("Game over, You won!")
                isOver = True            
        elif msg == "miss":
            setStatusText("Miss! Partner's remaining fleet size " + str(4 - myHits))
        else:        
            exec(msg)
            if getPixelColorStr() != "gray":
                client.sendMessage("hit")  
                setFillColor("gray")
                fill()
                partnerHits += 1                      
                if partnerHits == 4:
                    setStatusText("Game over, Play partner won")
                    isOver = True 
                    return                            
            else:
                client.sendMessage("miss")
            setStatusText("Make your move")          
            isMyTurn = True

makeTurtle(mouseHit = onMouseHit, closeClicked = onCloseClicked)
makeTurtle()
addStatusBar(30)
hideTurtle()
initGame()
host = "localhost"
port = 5000
client = TCPClient(host, port, stateChanged = onStateChanged)
setStatusText("Client connecting...")
isOver = False
myHits = 0
partnerHits = 0
if client.connect():
    setStatusText("Connected. Make your first move!")
    createShips()
    isMyTurn = True
else:
    setStatusText("Server game room closed")

