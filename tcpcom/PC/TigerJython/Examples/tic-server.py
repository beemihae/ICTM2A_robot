# Tic-Tac-Toe Server

from gturtle import *
from tcpcom import TCPServer

def square():
    setFillColor("white") 
    startPath()
    repeat 4:
        forward(50)
        right(90)
    fillPath()

def drawGrid():
    for x in range(0, 101, 50):
        for y in range(0, 101, 50):
            setPos(x, y)
            square()

def onMouseHit(x, y):
    global isMyTurn
    if isOver or not isMyTurn:
        return
    setPos(x, y)
    if getPixelColorStr() != "white":
        return
    isMyTurn = False
    setStatusText("Wait for your partner's move")
    setFillColor("red")
    fill(x, y)
    server.sendMessage("fill(" + str(x) + "," + str(y) + ")")
    showGameState()

def setPattern(x, y):
    global pattern
    setPos(x, y)
    if getPixelColorStr() == "red":
        pattern += 'X'
    elif getPixelColorStr() == "blue":
        pattern += 'O'
    else:
        pattern += '-'

def showGameState():
    # Convert board state into string pattern
    global pattern, isOver
    pattern = ""
    # Horizontal
    for y in range(25, 126, 50):
        for x in range(25, 126, 50):
            setPattern(x, y)
        pattern += ','  # Separator
    # Vertical
    for x in range(25, 126, 50):
        for y in range(25, 126, 50):
            setPattern(x, y)
        pattern += ','
    # Diagonal
    for x in range(25, 126, 50):
      setPattern(x, x);
    pattern += ','
    for x in range(25, 126, 50):
      setPattern(x, 150 - x);

    if "XXX" in pattern:
        setStatusText("RED won")
        isOver = True
    elif "OOO" in pattern:
        setStatusText("BLUE won")
        isOver = True
    elif not "-" in pattern:
        setStatusText("Board full")
        isOver = True

def onCloseClicked():
    server.terminate()
    dispose()
    
def onStateChanged(state, msg):
    global isMyTurn, isOver
    if state == TCPServer.CONNECTED:
        setStatusText("Client entered in my game room")
        clear("gray")
        drawGrid()
        isOver = False
    elif state == TCPServer.LISTENING:
        setStatusText("Waiting for a partner...")
    elif state == TCPServer.MESSAGE:
        setFillColor("blue")
        exec(msg)
        setStatusText("Make your move!")
        showGameState()
        isMyTurn = True
    
makeTurtle(mouseHit = onMouseHit, closeClicked = onCloseClicked)
addStatusBar(30)
clear("gray")
drawGrid()
port = 5000
server = TCPServer(port, stateChanged = onStateChanged)
isOver = False
isMyTurn = False


