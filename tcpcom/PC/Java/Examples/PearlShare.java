// PearlShare.java

import ch.aplu.jgamegrid.*;
import ch.aplu.tcpcom.*;

public class PearlShare
{
  static Actor btn;
  static int activeRow = -1;
  static int nbPearl = 18;
  static int nbRemoved = 0;
  static boolean isMyMove = false;
  static boolean isOver = false;

  static void initGame(GameGrid gg)
  {
    int nbRows = 4;
    int nb = 6;
    for (int k = 0; k < nbRows; k++)
    {
      for (int i = 0; i < nb; i++)
      {
        Actor pearl = new Actor("sprites/token_1.png");
        gg.addActor(pearl, new Location(1 + i, 1 + k));
      }
      nb -= 1;
    }
    btn = new Actor("sprites/btn_ok.gif", 2);
    gg.addActor(btn, new Location(6, 5));
  }

  static void handleMousePress(GameGrid gg, Object node, GGMouse mouse)
  {
    if (!isMyMove || isOver)
      return;
    Location btnLoc = new Location(6, 5);
    Location loc = gg.toLocationInGrid(mouse.getX(), mouse.getY());
    if (loc.equals(btnLoc))
    {
      btn.show(1);
      gg.refresh();
      if (nbRemoved == 0) // ok btn pressed
        gg.setStatusText("You have to remove at least 1 pearl!");
      else
      {
        sendMessage(node, "ok");
        gg.setStatusText("Wait!");
        nbRemoved = 0;
        activeRow = -1;
        isMyMove = false;
      }
    }
    else
    {
      int x = loc.x;
      int y = loc.y;
      if (activeRow != -1 && activeRow != y)
        gg.setStatusText("You must remove pearls from the same row.");
      else
      {
        Actor actor = gg.getOneActorAt(loc);
        if (actor != null)
        {
          actor.removeSelf();
          gg.refresh();
          sendMessage(node, "" + x + y);
          activeRow = y;
          nbPearl -= 1;
          nbRemoved += 1;
          if (nbPearl == 0)
          {
            isOver = true;
            gg.setStatusText("End of game. You lost!");
          }
        }
      }
    }
  }

  static void handleMouseRelease(GameGrid gg, Object node, GGMouse mouse)
  {
    btn.show(0);
    gg.refresh();
  }

  static void sendMessage(Object node, String msg)
  {
    if (node instanceof TCPServer)
      ((TCPServer)node).sendMessage(msg);
    else if (node instanceof TCPClient)
      ((TCPClient)node).sendMessage(msg);
  }

  static void handleMessage(GameGrid gg, String msg)
  {
    if (msg.equals("ok"))
    {
      isMyMove = true;
      gg.setStatusText("Remove any number of pearls from same row and click OK!");
    }
    else
    {
      int x = msg.charAt(0) - 48;
      int y = msg.charAt(1) - 48;
      Location loc = new Location(x, y);
      gg.getOneActorAt(loc).removeSelf();
      gg.refresh();
      nbPearl -= 1;
      if (nbPearl == 0)
      {
        isOver = true;
        gg.setStatusText("End of Game. You won.");
      }
    }
  }
}
