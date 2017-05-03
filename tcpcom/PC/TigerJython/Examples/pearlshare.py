# pearlshare.py

from gamegrid import *

activeRow = -1
nbPearl = 18
nbRemoved = 0
isMyMove = False
isOver = False     

def initGame():
    global btn
    nbRows = 4
    nb = 6
    for k in range(nbRows):
        for i in range(nb):
             pearl = Actor("sprites/token_1.png")
             addActor(pearl, Location(i + 1, k + 1))
        nb -=1    
    btn = Actor("sprites/btn_ok.gif", 2)
    addActor(btn, Location(6, 5))

def handleMouseRelease(e):
    btn.show(0)
    refresh()

def handleMousePress(e, node):
    global isMyMove, nbPearl, isOver, activeRow, nbRemoved
    if not isMyMove or isOver:
        return
    btnLoc = Location(6,5)    
    loc = toLocationInGrid(e.getX(), e.getY())
    if loc == btnLoc:
      btn.show(1)
      refresh()
      if nbRemoved == 0: # ok btn pressed
        setStatusText("You have to remove at least 1 pearl!")
      else:
        node.sendMessage("ok")          
        setStatusText("Wait!")
        nbRemoved = 0
        activeRow = -1
        isMyMove = False          
        
    else:
        x = loc.x
        y = loc.y  
        if activeRow != -1 and activeRow != y:
            setStatusText("You must remove pearls from the same row.")
        else:
            actor = getOneActorAt(loc)
            if actor != None:      
                actor.removeSelf()
                refresh()        
                node.sendMessage(str(x) + str(y))
                activeRow = y 
                nbPearl -= 1
                nbRemoved += 1
                if nbPearl == 0:    
                    isOver = True
                    setStatusText("End of game. You lost!")

def handleMessage(msg):                                                                
    global nbPearl, isMyMove, isOver
    if msg == "ok":
        isMyMove = True
        setStatusText("Remove any number of pearls from same row and click OK!")
    else:
        x = int(msg[0])
        y = int(msg[1])
        loc = Location(x, y)            
        getOneActorAt(loc).removeSelf()
        refresh()            
        nbPearl -= 1
        if nbPearl == 0:
            isOver = True
            setStatusText("End of Game. You won.")    
                                    