# Tic-Tac-Toe Client

from gturtle import *
from tcpcom import TCPClient

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
    setStatusText("Wait for the partner's move")
    setFillColor("blue")
    fill(x, y)
    client.sendMessage("fill(" + str(x) + "," + str(y) + ")")
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
    global pattern
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
    elif "OOO" in pattern:
        setStatusText("BLUE won")
    elif not "-" in pattern:
        setStatusText("Board full")

def onCloseClicked():
    if client.isConnecting():
        setStatusText("Connecting...please wait!")
        return
    client.disconnect()
    dispose()
    
def onStateChanged(state, msg):
    global isMyTurn
    if state == TCPClient.MESSAGE:
        setFillColor("red")
        exec(msg)
        setStatusText("Make a move!")
        showGameState()
        isMyTurn = True

host = inputString("Enter Server IP Address", False)
if host != None:
    makeTurtle(mouseHit = onMouseHit, closeClicked = onCloseClicked)
    addStatusBar(30)
    hideTurtle()
    clear("gray")
    drawGrid()
    port = 5000
    client = TCPClient(host, port, stateChanged = onStateChanged)
    setStatusText("Client connecting...")
    isOver = False
    if client.connect(5):
        setStatusText("Make a move!")
        isMyTurn = True
    else:
        setStatusText("Server game room closed")
