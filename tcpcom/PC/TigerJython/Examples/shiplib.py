# shiplib.py

from gamegrid import *

isOver = False
isMyMove = False
dropLoc = None
myHits = 0
partnerHits = 0
nbShips = 2

class Ship(Actor):
     def __init__(self):
         Actor.__init__(self, "sprites/boat.gif")

def handleMousePress(node, loc):
    global isMyMove, dropLoc
    dropLoc = loc
    if not isMyMove or isOver:
          return
    node.sendMessage("" + str(dropLoc.x) + str(dropLoc.y)) # send location
    setStatusText("Bomb fired. Wait for result...")
    isMyMove = False

def handleMessage(node, state, msg):
    global isMyMove, myHits, partnerHits, first, isOver
    if msg == "hit":
        myHits += 1
        setStatusText("Hit! Partner's fleet size " + str(nbShips - myHits) 
            + ". Wait for partner's move!")
        addActor(Actor("sprites/checkgreen.gif"), dropLoc)
        if myHits == nbShips:
            setStatusText("Game over, You won!")
            isOver = True        
    elif msg == "miss":
        setStatusText("Miss! Partner's fleet size " + str(nbShips - myHits) 
            + ". Wait for partner's move!")
        addActor(Actor("sprites/checkred.gif"), dropLoc)
    else:
        x = int(msg[0])
        y = int(msg[1])
        loc = Location(x, y)            
        bomb = Actor("sprites/explosion.gif")
        addActor(bomb, loc)
        delay(2000)
        bomb.removeSelf()
        refresh()
        actor = getOneActorAt(loc, Ship)
        if actor != None:
            actor.removeSelf()
            refresh()
            node.sendMessage("hit")
            partnerHits += 1             
            if partnerHits == nbShips:
                setStatusText("Game over! Partner won")
                isOver = True 
                return  
        else:
            node.sendMessage("miss")     
        isMyMove = True
        setStatusText("You fire!")
 