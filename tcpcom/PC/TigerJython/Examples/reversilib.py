# reversilib.py

from gamegrid import *

def initGame():
    white = Actor("sprites/token.png", 2)
    white2 = Actor("sprites/token.png", 2)
    black = Actor("sprites/token.png", 2)
    black2 = Actor("sprites/token.png", 2)
    addActor(white, Location(3, 3))
    addActor(white2, Location(4, 4))
    addActor(black, Location(4, 3))
    addActor(black2, Location(3, 4))
    white.show(0)
    white2.show(0)
    black.show(1)
    black2.show(1)

#Checks if cell has a neighbour in the north, east, south or west
def hasNeighbours(loc):
    locs = toList(loc.getNeighbourLocations(0.5))
    for i in range(4):
        if getOneActorAt(locs[i]) != None:
            return True
    return False

# Check for stones in all 8 directions and if they can be turned add list
def checkStones(imageID, location):
    for c in range (0, 360, 45):
        actors = []
        loc = location.getNeighbourLocation(c)
        a = getOneActorAt(loc)
        hasSameImageID = False
        while a != None and not hasSameImageID:
            if a.getIdVisible() != imageID:
                actors.append(a)
                loc = loc.getNeighbourLocation(c)
                a = getOneActorAt(loc)          
            else:    
                if a.getIdVisible() == imageID:
                    hasSameImageID = True
        # Turn stones 
        if hasSameImageID:
            for actor in actors:
                actor.show(imageID)  
                
# endOfGame
def endOfGame():
    countYellow = 0
    countRed = 0
    all = getOccupiedLocations()
    for lc in all:
        if getOneActorAt(lc).getIdVisible() == 0:
           countYellow += 1
        else:
           countRed += 1
    if len(all) == 64:
        if countRed > countYellow:
            setStatusText("Game over. Red wins - " + str(countRed) + ":" + str(countYellow))
        elif countRed < countYellow:
            setStatusText("Game over. Yellow wins - " + str(countYellow) + ":" + str(countRed))
        else:
            setStatusText("The game ended in a tie")  