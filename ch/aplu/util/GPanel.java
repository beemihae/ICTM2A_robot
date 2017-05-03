// GPanel.java

/*
  This software is part of the JEX (Java Exemplarisch) Utility Library.
  It is Open Source Free Software, so you may
    - run the code for any purpose
    - study how the code works and adapt it to your needs
    - integrate all or parts of the code in your own programs
    - redistribute copies of the code
    - improve the code and release your improvements to the public
  However the use of the code is entirely your responsibility.
 */

package ch.aplu.util;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.print.*;
import javax.swing.*;
import java.net.*;

/**
 * Simple graphics window of default size 501x501 pixels (client drawing area)
 * using a coordinate system with x-axis from left to right, y-axis from bottom to top
 * (called window coordinates, default range 0..1, 0..1).
 * The drawing methods perform all drawings in an offscreen buffer
 * and automatically repaint it on the screen, so the graphics is shown step-by-step.
 * <br><br>
 *
 * A current graph position is used, that remembers the end position of the last
 * drawing process and where new drawings start.<br><br>
 *
 * By default the window may be resized by mouse dragging as long as resizable(false) isn't called.
 * Resizing transforms the current graphics content through an affine transformation from
 * the offscreen buffer. In this process, some graphics resolution is lost.<br><br>
 *
 * If pixel accuracy is needed, set the window coordinates to 500, 500.<br><br>
 *
 * Clicking the title bar's close button will terminate the application by calling
 * <code>System.exit(0)</code> unless an ExitListener is registered.<br><br>
 *
 * More than one GPanel may be instantiated. They are automatically positioned two-by-two.<br><br>
 *
 * All constructors and Swing methods are invoked by the Event Dispatch Thread (EDT).
 * Therefore GPanel is no longer derived from JPanel, but contains a JPanel now (has-a relation).
 * If you need some special methods from the internal JPanel, call getPane().<br><br>
 *
 * Do not mix with Graphics or Graphics2D methods. Use GWindow instead.
 * @see ch.aplu.util.GPane
 * @see ch.aplu.util.GWindow
 */

public class GPanel
{
  private GPane _gPane;
  private Size _wSize = new Size(501, 501);
  // will produce usable coordinates 0..500 (0 and 500 inclusive = 501 pixels)
  private static int _previousWidth;  // Width of previous window (for multiple GPanels)
  private static int _previousUlx;  // ulx of previous window (for multiple GPanels)
  private static int _previousUly;  // uly of previous window (for multiple GPanels)
  private String _title = "GPanel";
  private boolean _isFullscreen = false;
  private int _panelMode = STANDARD;

  /**
   * Mode for a standard GPanel.
   */
  static public int STANDARD = 0;

  /**
   * Mode for a GPanel embedded in a top-level window (no longer supported).
   * Use a GPane instance instead.
   */
  static public int EMBEDDED = 1;

  /**
   * Mode for a GPanel used as standalone window in a applet.
   */
  static public int APPLETFRAME = 2;

  /**
   * Mode for a GPanel with no title bar.
   */
  static public int NOTITLEBAR = 3;


  /**
   * Instance of class Fullscreen.
   */
  public static Fullscreen FULLSCREEN = new Fullscreen();


// -------------- Start of ctors -------------------------

  /**
   * Construct a GPanel for special purposes.<br><br>
   * Use parameter GPanel.APPLETFRAME for an Applet in its own frame.<br><br>
   * Use parameter GPanel.NOTITLEBAR for GPanel which has no title bar. Does not show
   * until visible(true) is called.<br><br>
   *
   * To embed a GPanel in a top-level window (JFrame, JDialog, Browswer window, etc.)
   * use class GPane instead.<br><br>
   */
  public GPanel(final int mode)
  {
    if (mode == EMBEDDED)
    {
      String msg = "Use a GPane instance for an embedded GPanel";
      String title = "Error when constructing GPanel(mode)";
      JOptionPane.showMessageDialog(null, msg);
      System.exit(1);
    }

    _panelMode = mode;
    if (EventQueue.isDispatchThread())
      init(_title, null, 0, 1, 0, 1, false);
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            init(_title, null, 0, 1, 0, 1, false);
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  }


  /**
   * Same as GPanel() with given mode and window size (client area).
   * Default window size is 501/501 (user coordinates 0..500/0..500.<br>
   * If size is an instance of Fullscreen, the size is set to the current screen width/height.
   */
  public GPanel(final int mode, final Size size)
  {
    if (mode == EMBEDDED)
    {
      String msg = "Use a GPane instance for an embedded GPanel";
      String title = "Error when constructing GPanel(mode, size)";
      JOptionPane.showMessageDialog(null, msg);
      System.exit(1);
    }

    _panelMode = mode;
    if (EventQueue.isDispatchThread())
      createGPanel(mode, size);
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            createGPanel(mode, size);
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  }

  private void createGPanel(int mode, Size size)
  {
     _wSize = size;
     init(_title, null, 0, 1, 0, 1, true);
   }

  /**
   * Construct a GPanel and show the window.
   * Window coordinates are 0, 1, 0, 1.
   * (0, 0) is at lower left corner which is the current graph cursor position.
   */
  public GPanel()
  {
    this(true);
  }

  /**
   * Same as GPanel() with given visibility.
   */
  public GPanel(final boolean visible)
  {
    if (EventQueue.isDispatchThread())
      init(_title, null, 0, 1, 0, 1, visible);
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            init(_title, null, 0, 1, 0, 1, visible);
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  }

  /**
   * Same as GPanel() with given window size (client area).
   * User coordinates (pixels) in range 0..width-1/0..height-1<br>
   * Default window size is 501/501 (user coordinates 0..500/0..500.<br>
   * Use <code>GPanel(new Size(width, height))</code> to set size to width/height.<br>
   * If size is an instance of Fullscreen, the size is set to the current screen width/height
   * and the window is positioned at upper left corner.
   */
  public GPanel(final Size size)
  {
    if (EventQueue.isDispatchThread())
      createGPanel(size);
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            createGPanel(size);
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  }

  private void createGPanel(Size size)
  {
    _wSize = size;
    init(_title, null, 0, 1, 0, 1, true);
  }

  /**
   * Same as GPanel() but show given menuBar.
   */
  public GPanel(JMenuBar menuBar)
  {
    this(menuBar, true);
  }

  /**
   * Same as GPanel() with given menuBar and visibility.
   */
  public GPanel(final JMenuBar menuBar, final boolean visible)
  {
    if (EventQueue.isDispatchThread())
      init(_title, menuBar, 0, 1, 0, 1, visible);
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            init(_title, menuBar, 0, 1, 0, 1, visible);
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  }

  /**
   * Same as GPanel() with given title.
   */
  public GPanel(String title)
  {
    this(title, true);
  }

  /**
   * Same as GPanel() with given title and visibility.
   */
  public GPanel(final String title, final boolean visible)
  {
    if (EventQueue.isDispatchThread())
      init(title, null, 0, 1, 0, 1, visible);
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            init(title, null, 0, 1, 0, 1, visible);
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  }

  /**
   * Same as GPanel() with given title and menuBar.
   */
  public GPanel(String title, JMenuBar menuBar)
  {
    this(title, menuBar, true);
  }

  /**
   * Same as GPanel() with given title, menuBar and visibility.
   */
  public GPanel(final String title, final JMenuBar menuBar, final boolean visible)
  {
    if (EventQueue.isDispatchThread())
      init(title, menuBar, 0, 1, 0, 1, visible);
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            init(title, menuBar, 0, 1, 0, 1, visible);
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  }

  /**
   * Construct a GPanel with given window coordinates.
   * (0, 0) is lower left
   * If you need to draw as accurate as possible on pixels
   * use GPanel(0, 500, 0, 500).
   * Set the current graph position to (0, 0).
   */
  public GPanel(double xmin, double xmax, double ymin, double ymax)
  {
    this(xmin, xmax, ymin, ymax, true);
  }

  /**
   * Same as GPanel() with given window coordinates and visibility.
   */
  public GPanel(final double xmin, final double xmax, final double ymin,
                final double ymax, final boolean visible)
  {
    if (EventQueue.isDispatchThread())
      init(_title, null, xmin, xmax, ymin, ymax, visible);
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            init(_title, null, xmin, xmax, ymin, ymax, visible);
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  }

  /**
   * Same as GPanel(xmin, xmax, ymin, ymax) with given menuBar.
   */
  public GPanel(JMenuBar menuBar, double xmin, double xmax, double ymin,
                double ymax)
  {
    this(menuBar, xmin, xmax, ymin, ymax, true);
  }

  /**
   * Same as GPanel() with given menuBar, window coordinates and visibility.
   */
  public GPanel(final JMenuBar menuBar, final double xmin, final double xmax,
                final double ymin, final double ymax, final boolean visible)
  {
    if (EventQueue.isDispatchThread())
      init(_title, menuBar, xmin, xmax, ymin, ymax, visible);
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
           init(_title, menuBar, xmin, xmax, ymin, ymax, visible);
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  }

  /**
   * Same as GPanel(xmin, xmax, ymin, ymax) with given title.
   */
  public GPanel(String title, double xmin, double xmax, double ymin,
                double ymax)
  {
    this(title, xmin, xmax, ymin, ymax, true);
  }

  /**
   * Same as GPanel() with given title, window coordinates and visibility.
   */
  public GPanel(final String title, final double xmin, final double xmax,
                final double ymin, final double ymax, final boolean visible)
  {
    if (EventQueue.isDispatchThread())
      init(title, null, xmin, xmax, ymin, ymax, visible);
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            init(title, null, xmin, xmax, ymin, ymax, visible);
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  }

  /**
   * Same as GPanel(xmin, xmax, ymin, ymax) with given title and menuBar.
   */
  public GPanel(String title, JMenuBar menuBar, double xmin, double xmax,
                double ymin, double ymax)
  {
    this(title, menuBar, xmin, xmax, ymin, ymax, true);
  }

  /**
   * Same as GPanel() with given title, menuBar, window coordinates and visibility.
   */
  public GPanel(final String title, final JMenuBar menuBar, final double xmin,
                final double xmax, final double ymin, final double ymax,
                final boolean visible)
  {
    if (EventQueue.isDispatchThread())
      init(title, menuBar, xmin, xmax, ymin, ymax, visible);
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            init(title, menuBar, xmin, xmax, ymin, ymax, visible);
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  }

  /**
   * Same as GPanel() with given window size (client area),
   * title, menuBar, window coordinates and visibility.<br>
   * User coordinates (pixels) in range 0..width-1/0..height-1<br>
   * Default window size is 501/501.<br>
   * Set menuBar = null, if no menu is used.<br>
   * If size is an instance of Fullscreen, the size is set to the current screen width/height.
   */
  public GPanel(final Size size, final String title, final JMenuBar menuBar,
                final double xmin, final double xmax, final double ymin,
                final double ymax, final boolean visible)
  {
    if (EventQueue.isDispatchThread())
      createGPanel(size, title, menuBar, xmin, xmax, ymin, ymax, visible);
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            createGPanel(size, title, menuBar, xmin, xmax, ymin, ymax, visible);
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  }

  private void createGPanel(Size size, String title, JMenuBar menuBar,
                       double xmin, double xmax, double ymin,
                       double ymax, boolean visible)
  {
    _wSize = size;
    init(title, menuBar, xmin, xmax, ymin, ymax, visible);
  }

// -------------- End of ctors -------------------------

  private void init(String title, JMenuBar menuBar, double xmin, double xmax,
                    double ymin, double ymax, boolean visible)
  {
    Size size;
    if (_wSize instanceof Fullscreen)
      size = FULLSCREEN;
    else
      // must increase 2 pixels because 0,0 is on components border
      size = new Size(_wSize.getWidth() + 2, _wSize.getHeight() + 2);

    _gPane = new GPane(_panelMode, size, title, menuBar,
                       xmin, xmax, ymin, ymax, visible);
  }

  /**
   * Register an ExitListener to get a notification when the close button is clicked.
   * After registering the automatic termination is disabled.
   * To terminate the application, System.exit() can be called.
   */
  public void addExitListener(ExitListener exitListener)
  {
    _gPane.getWindow().addExitListener(exitListener);
  }

  /**
   * Set window coordinates.
   * left_x, right_x, bottom_y, top_y
   */
  public void window(double xmin, double xmax, double ymin, double ymax)
  {
    _gPane.window(xmin, xmax, ymin, ymax);
  }


  /**
   * Select if GPanel's window is visible or not.
   * (You may draw into an invisible GPanel and show it
   * afterwards.)
   */
  public void visible(boolean isVisible)
  {
    _gPane.visible(isVisible);
  }

  /**
   * Return a reference to the GWindow used by the GPanel.
   * Can be used to access the surrounding JFrame
   * because GWindow is derived from JFrame.
   */
  public GWindow getWindow()
  {
    return _gPane.getWindow();
  }


  /**
   * Test if GPanel is fully initialized.
   * Only used for GPanel.EMBEDDED.<br>
   * (If GPanel is embedded in an other component, any graphics
   * drawing operation must be postponed until the parent component is shown
   * because the GPanel's size is only known at this time.)<br>
   * Because yield() is called, it can be used in a narrow loop.
   *
   */
  public boolean isReady()
  {
    return _gPane.isReady();
  }

  /**
   * Print the graphics context to an attached printer with
   * the given magnification scale factor.
   * scale = 1 will print on standard A4 format paper.
   *
   * The given gp must implement the GPrintable interface,
   * e.g. the single method void draw(), where all the
   * drawing into the GPanel must occur.
   *
   * A standard printer dialog is shown before printing is
   * started.<br>
   *
   * Return false, if printing is canceled in this dialog,
   * return true, when print data is sent to the printer spooler.
   *
   *(On some platforms the Java Virtual Machine crashes when
   * changing the printer in this dialog. Use the default printer)
   * <br>
   * Example:<br>
   * <code><br>
import ch.aplu.util.*;<br>
<br>
public class PrintEx extends GPanel implements GPrintable<br>
{<br>
&nbsp;&nbsp;public PrintEx()<br>
&nbsp;&nbsp;{<br>
&nbsp;&nbsp;&nbsp;&nbsp;draw();       // Draw on screen<br>
&nbsp;&nbsp;&nbsp;&nbsp;print(this);  // Draw on printer<br>
&nbsp;&nbsp;}<br>
<br>
&nbsp;&nbsp;public void draw()<br>
&nbsp;&nbsp;{<br>
&nbsp;&nbsp;&nbsp;&nbsp;move(0, 0);<br>
&nbsp;&nbsp;&nbsp;&nbsp;draw(1, 0);<br>
&nbsp;&nbsp;&nbsp;&nbsp;draw(1, 1);<br>
&nbsp;&nbsp;&nbsp;&nbsp;draw(0, 1);<br>
&nbsp;&nbsp;&nbsp;&nbsp;draw(0, 0);<br>
<br>
&nbsp;&nbsp;&nbsp;&nbsp;line(0, 1, 1, 0);<br>
&nbsp;&nbsp;&nbsp;&nbsp;line(0, 0, 1, 1);<br>
&nbsp;&nbsp;}<br>
<br>
&nbsp;&nbsp;public static void main(String[] args)<br>
&nbsp;&nbsp;{<br>
&nbsp;&nbsp;&nbsp;&nbsp;new PrintEx();<br>
&nbsp;&nbsp;}<br>
}<br>
*/

  public boolean print(GPrintable gp, double scale)
  {
    return _gPane.print(gp, scale);
  }

  /**
   * Same as print(GPrintable gp, double scale)
   * with scale = 1.
   */
  public boolean print(GPrintable gp)
  {
    return _gPane.print(gp);
  }

  /**
   * Print the current screen view to an attached printer
   * with the given magnification scale factor.
   * A standard printer dialog is shown before printing is
   * started.<br>
   * Return false, if printing is canceled in this dialog,
   * return true, when print data is sent to the printer spooler.
   *
   */
  public boolean printScreen(double scale)
  {
    return _gPane.printScreen(scale);
  }

  /**
   * Same printScreen(double scale) with scale = 1.
   */
  public boolean printScreen()
  {
    return _gPane.printScreen();
  }


  /**
   * Set the background color. All drawing are erased.
   * Return the previous color.
   */
  public Color bgColor(Color color)
  {
    return _gPane.bgColor(color);
  }

  /**
   *  Return the current background color.
   */
  public Color getBgColor()
  {
    return _gPane.getBgColor();
  }

  /**
   * Set the title in the window's title bar.
   */
  public void title(String title)
  {
    _gPane.title(title);
  }

  /**
   * Return user coordinate x of given window coordinate x.
   */
  public int toUserX(double windowX)
  {
    return _gPane.toUserX(windowX);
  }

  /**
   * Return user coordinate y of given window coordinate y.
   */
  public int toUserY(double windowY)
  {
    return _gPane.toUserY(windowY);
  }

  /**
   * Return reference to point in user coordinate of given point in window coordinate.
   */
  public Point toUser(Point2D.Double windowPt)
  {
    return _gPane.toUser(windowPt);
  }

  /**
   * Return reference to point in user coordinate of given window coordinates.
   */
  public Point toUser(double windowX, double windowY)
  {
    return _gPane.toUser(windowX, windowY);
  }

  /**
   * Return user coordinates increment x of given window coordinates increment x.
   * Increment is always positive.
   */
  public int toUserWidth(double windowWidth)
  {
    return _gPane.toUserWidth(windowWidth);
  }

  /**
   * Return user coordinates increment y of given window coordinates increment y.
   * Increment is always positive.
   */
  public int toUserHeight(double windowHeight)
  {
    return _gPane.toUserHeight(windowHeight);
  }

  /**
   * Return window coordinate x of given user coordinate x.
   */
  public double toWindowX(int userX)
  {
    return _gPane.toWindowX(userX);
  }

  /**
   * Return window coordinate y of given user coordinate y.
   */
  public double toWindowY(int userY)
  {
    return _gPane.toWindowY(userY);
  }

  /**
   * Return reference to point in window coordinates of given point in user coordinates.
   */
  public Point2D.Double toWindow(Point userPt)
  {
    return _gPane.toWindow(userPt);
  }

  /**
   * Return reference to point in window coordinates of given user coordinates.
   */
  public Point2D.Double toWindow(int userX, int userY)
  {
    return _gPane.toWindow(userX, userY);
  }

  /**
   * Return window coordinates increment x of given user coordinates increment x.
   * Increment is always positive.
   */
  public double toWindowWidth(int userWidth)
  {
    return _gPane.toWindowWidth(userWidth);
  }

  /**
   * Return window coordinates increment y of given user coordinates increment y.
   * Increment is always positive.
   */
  public double toWindowHeight(int userHeight)
  {
    return _gPane.toWindowHeight(userHeight);
  }

  /**
   * Set the current line width in pixels.
   */
  public void lineWidth(int width)
  {
    _gPane.lineWidth(width);
  }

  /**
   * Set the given new color and return the previous color
   */
  public Color color(Color color)
  {
    return _gPane.color(color);
  }

  /**
   * Draws a line with given window coordinates
   * and set the graph position to the endpoint.
   */
  public void line(double x1, double y1, double x2, double y2)
  {
    _gPane.line(x1, y1, x2, y2);
  }

  /**
   * Draws a line with given points (in window coordinates)
   * and set the graph position to the endpoint.
   */
  public void line(Point2D.Double pt1, Point2D.Double pt2)
  {
    _gPane.line(pt1, pt2);
  }

  /**
   * Draw a line from current graph position to given window coordinates and
   * set the graph position to the endpoint.
   */
  public void draw(double x, double y)
  {
    _gPane.draw(x, y);
  }

  /**
   * Draw a line from current graph position to given point (in window coordinates)
   * and set the graph position to the endpoint.
   */
  public void draw(Point2D.Double pt)
  {
    _gPane.draw(pt);
  }

  /**
   * Set the current graph position to given window coordinates
   * (without drawing anything).
   */
  public void move(double x, double y)
  {
    _gPane.move(x, y);
  }

  /**
   * Same as move(double x, double y).
   * ( Override java.component.move(int x, int y) )
   */
  public void move(int x, int y)
  {
    _gPane.move(x, y);
  }

  /**
   * Same as move(double x, double y).
   */
  public void pos(double x, double y)
  {
    _gPane.pos(x, y);
  }

  /**
   * Set the current graph position to given point (in window coordinates)
   * (without drawing anything).
   */
  public void move(Point2D.Double pt)
  {
    _gPane.move(pt);
  }

  /**
   * Same as move(Point2D.Double pt).
   */
  public void pos(Point2D.Double pt)
  {
    _gPane.pos(pt);
  }

  /**
   * Return window coordinate x of current graph position.
   */
  public double getPosX()
  {
    return _gPane.getPosX();
  }

  /**
   * Return window coordinate y of current graph position.
   */
  public double getPosY()
  {
    return _gPane.getPosY();
  }

  /**
   * Return reference to point of current graph position (in window coordinates).
   */
  public Point2D.Double getPos()
  {
    return _gPane.getPos();
  }

  /**
   * Clear the graphics window (fully paint with background color)
   * (and the offscreen buffer used by the window).
   * Set the current graph position to (0, 0).<br>
   * If enableRepaint(false) only clear the offscreen buffer.
   */
  public void clear()
  {
    _gPane.clear();
  }

  /**
   * Same as clear() but let the current graph position unchanged.
   */
  public void erase()
  {
    _gPane.erase();
  }

  /**
   * Draw a circle with center at the current graph position
   * and given radius in horizontal window coordinates.
   * The graph position is unchanged.
   */
  public void circle(double radius)
  {
    _gPane.circle(radius);
  }

  /**
   * Draw a filled circle with center at the current graph position
   * and given radius in window coordinates using the current color.
   * The graph position is unchanged.
   */
  public void fillCircle(double radius)
  {
    _gPane.fillCircle(radius);
  }

  /**
   * Draw a rectangle with center at the current graph position
   * and given width and height in window coordinates.
   * The graph position is unchanged.
   */
  public void rectangle(double width, double height)
  {
    _gPane.rectangle(width, height);
  }

  /**
   * Draw a rectangle with given opposite corners
   * in window coordinates.
   * The graph position is unchanged.
   */
  public void rectangle(double x1, double y1, double x2, double y2)
  {
    _gPane.rectangle(x1, y1, x2, y2);
  }

  /**
   * Draw a rectangle with given opposite corner points
   * in window coordinates.
   * The graph position is unchanged.
   */
  public void rectangle(Point2D.Double pt1, Point2D.Double pt2)
  {
    _gPane.rectangle(pt1, pt2);
  }

  /**
   * Draw a filled rectangle with center at the current graph position
   * and given width and height in window coordinates using the current color.
   * The graph position is unchanged.
   */
  public void fillRectangle(double width, double height)
  {
    _gPane.fillRectangle(width, height);
  }

  /**
   * Draw a filled rectangle with given opposite corners
   * in window coordinates using the current color.
   * The graph position is unchanged.
   */
  public void fillRectangle(double x1, double y1, double x2, double y2)
  {
    _gPane.fillRectangle(x1, y1, x2, y2);
  }

  /**
   * Draw a filled rectangle with given opposite corner points
   * in window coordinates using the current color.
   * The graph position is unchanged.
   */
  public void fillRectangle(Point2D.Double pt1, Point2D.Double pt2)
  {
    _gPane.fillRectangle(pt1, pt2);
  }

  /**
   * Draw an arc with center at the current graph position
   * and given radius in window coordinates.
   * Start angle and extend angle in degrees (zero to east, positive counterclockwise).
   * The graph position is unchanged.
   */
  public void arc(double radius, double startAngle, double extendAngle)
  {
    _gPane.arc(radius, startAngle, extendAngle);
  }

  /**
   * Draw a filled arc with center at the current graph position
   * and given radius in window coordinates.
   * Start angle and extend angle in degrees (zero to east, positive counterclockwise).
   * The graph position is unchanged.
   */
  public void fillArc(double radius, int startAngle, int extendAngle)
  {
    _gPane.fillArc(radius, startAngle, extendAngle);
  }

  /**
   * Draw a polygon with given corner coordinates in window coordinates.
   * (Both arrays must be of equal size.)
   * The graph position is unchanged.
   */
  public void polygon(double[] x, double[] y)
  {
    _gPane.polygon(x, y);
  }

  /**
   * Draw a polygon with given corner points in window coordinates.
   * The graph position is unchanged.
   */
  public void polygon(Point2D.Double[] corner)
  {
    _gPane.polygon(corner);
  }

  /**
   * Draw a filled polygon with given corner coordinates in window coordinates
   * using the current color.
   * (Both arrays must be of equal size.)
   * The graph position is unchanged.
   */
  public void fillPolygon(double[] x, double[] y)
  {
    _gPane.fillPolygon(x, y);
  }

  /**
   * Draw a filled polygon with given corner points in window coordinates
   * using the current color.
   * The graph position is unchanged.
   */
  public void fillPolygon(Point2D.Double[] corner)
  {
    _gPane.fillPolygon(corner);
  }

  /**
   * Draw a triangle with given corner coordinates in window coordinates.
   * The graph position is unchanged.
   */
  public void triangle(double x1, double y1, double x2, double y2, double x3,
                       double y3)
  {
    _gPane.triangle(x1, y1, x2, y2, x3, y3);
  }

  /**
   * Draw a triangle with given corners in window coordinates.
   * The graph position is unchanged.
   */
  public void triangle(Point2D.Double pt1, Point2D.Double pt2, Point2D.Double pt3)
  {
    _gPane.triangle(pt1, pt2, pt3);
  }

  /**
   * Draw a filled triangle with given corner coordinates in window coordinates
   * using the current color.
   * The graph position is unchanged.
   */
  public void fillTriangle(double x1, double y1, double x2, double y2,
                           double x3, double y3)
  {
    _gPane.fillTriangle(x1, y1, x2, y2, x3, y3);
  }

  /**
   * Draw a filled triangle with given corners in window coordinates
   * using the current color.
   * The graph position is unchanged.
   */
  public void fillTriangle(Point2D.Double pt1, Point2D.Double pt2, Point2D.Double pt3)
  {
    _gPane.fillTriangle(pt1, pt2, pt3);
  }

  /**
   * Draw a figure defined by the given GeneralPath (using window coordinates).
   * The graph position is unchanged.
   */
  public void generalPath(GeneralPath gp)
  {
    _gPane.generalPath(gp);
  }

  /**
   * Fill a figure defined by the given GeneralPath (using window coordinates)
   * using the current color.
   * The graph position is unchanged.
   */
  public void fillGeneralPath(GeneralPath gp)
  {
    _gPane.fillGeneralPath(gp);
  }

  /**
   * Draw a string at the current graph position.
   * The graph position is unchanged.
   */
  public void text(String str)
  {
    _gPane.text(str);
  }

  /**
   * Draw a char at the current graph position.
   * The graph position is unchanged.
   */
  public void text(char c)
  {
    _gPane.text(c);
  }

  /**
   * Draw a string at the given position.
   * The graph position is unchanged.
   */
  public void text(double x, double y, String str)
  {
    _gPane.text(x, y, str);
  }

  /**
   * Draw a char at the given position.
   * The graph position is unchanged.
   */
  public void text(double x, double y, char c)
  {
    _gPane.text(x, y, c);
  }

  /**
   * Select the given font for all following text operations.
   */
  public void font(Font font)
  {
    _gPane.font(font);
  }

  /**
   * Draw a single point at the given window coordinates.
   * The graph position is set to the given point.
   */
  public void point(double x, double y)
  {
    _gPane.point(x, y);
  }

  /**
   * Draw a single point at the given pt (in window coordinates).
   * The graph position is set to the given point.
   */
  public void point(Point2D.Double pt)
  {
    _gPane.point(pt);
  }

  /**
   * Show the GIF image from given file path at given window coordinates
   * (specifies lowerleft corner of image).
   * Return true if successful.
   * If enableRepaint(false), the image is drawn in offscreen buffer only.
   */
  public boolean image(String imagePath, double x, double y)
  {
    return _gPane.image(imagePath, x, y);
  }

  /**
   * Show the GIF image from given file path at given point(in window coordinates)
   * (specifies lowerleft corner of image).
   * Return true if successful.
   * If enableRepaint(false), the image is drawn in offscreen buffer only.
   */
  public boolean image(String imagePath, Point2D.Double pt)
  {
    return _gPane.image(imagePath, pt);
  }

  /**
   * Show the GIF image from URL at given window coordinates
   * (specifies lowerleft corner of image).
   * Return true if successful after the image is completely transfered.
   * If enableRepaint(false), the image is drawn in offscreen buffer only.
   */
  public boolean image(URL imageUrl, double x, double y)
  {
    return _gPane.image(imageUrl, x, y);
  }

  /**
   * Show the GIF image from given URL at given point(in window coordinates)
   * (specifies lowerleft corner of image).
   * Return true if successful after the image is completely transfered.
   * If enableRepaint(false), the image is drawn in offscreen buffer only.
   */
  public boolean image(URL imageUrl, Point2D.Double pt)
  {
    return _gPane.image(imageUrl, pt);
  }

  /**
   * Return the width (horizontal size) of the GIF image from the given path (in window coordinates).
   * Return 0, if GIF image is invalid.
   */
  public double imageWidth(String imagePath)
  {
    return _gPane.imageWidth(imagePath);
  }

  /**
   * Return the width (horizontal size) of the GIF image from the given URL (in window coordinates).
   * Return 0, if GIF image is invalid.
   */
  public double imageWidth(URL imageUrl)
  {
    return _gPane.imageWidth(imageUrl);
  }

  /**
   * Return the width (horizontal size) of the last loaded GIF image (in window coordinates).
   * Return 0, if GIF image is invalid.
   */
  public double imageWidth()
  {
    return _gPane.imageWidth();
  }

  /**
   * Return the height (vertical size) of the GIF image from the given path (in window coordinates).
   * Return 0, if GIF image is invalid.
   */
  public double imageHeight(String imagePath)
  {
    return _gPane.imageHeight(imagePath);
  }

  /**
   * Return the height (vertical size) of the GIF image from the given URL (in window coordinates).
   * Return 0, if GIF image is invalid.
   */
  public double imageHeight(URL imageUrl)
  {
    return _gPane.imageHeight(imageUrl);
  }

  /**
   * Return the height (vertical size) of the last loaded GIF image (in window coordinates).
   * Return 0, if GIF image is invalid.
   */
  public double imageHeight()
  {
    return _gPane.imageHeight();
  }

  /**
   * Apply the given AffineTransform to offscreen buffer.
   */
  public void applyTransform(AffineTransform at)
  {
    _gPane.applyTransform(at);
  }

  /**
   * Enable or disable the automatic repaint in graphics methods.
   * Return the current state of repainting.<br>
   * When automatic repaint is disabled, the method repaint() must be called to show
   * the image on the screen.<br>
   * Useful to avoid flickering while animating an image. If repainting is disabled
   * clear() will only erase the offscreen buffer and not the screen. repaint() should
   * be called after drawing the new situation in order to copy the offscreen buffer to the screen.<br>
   * While printing, repainting is disabled.
   */
  public boolean enableRepaint(boolean doRepaint)
  {
    return _gPane.enableRepaint(doRepaint);
  }

  /**
   * Set the paint mode of the graphics context to overwrite
   * with current color.
   */
  public void setPaintMode()
  {
    _gPane.setPaintMode();
  }

  /**
   * Sets the paint mode to alternate between the current color and the given color.
   * This specifies that logical pixel operations are performed in the XOR mode,
   * which alternates pixels between the current color and a specified XOR color.
   * When drawing operations are performed, pixels which are the current color
   * are changed to the specified color, and vice versa.
   * Pixels that are of colors other than those two colors are changed in an
   * unpredictable but reversible manner; if the same figure is drawn twice,
   * then all pixels are restored to their original values.
   */
  public void setXORMode(Color c)
  {
    _gPane.setXORMode(c);
  }

  /**
   * Set the window position (upper left corner in device coordinates).
   */
  public void windowPosition(final int ulx, final int uly)
  {
    if (EventQueue.isDispatchThread())
    {
      _gPane.getWindow().setWinPosition(ulx, uly);
    }

    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            _gPane.getWindow().setWinPosition(ulx, uly);
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  }

  /**
   * Set the window position to the center of the screen.
   */
  public void windowCenter()
  {
    if (EventQueue.isDispatchThread())
    {
      _gPane.getWindow().setWinCenter();
    }

    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            _gPane.getWindow().setWinCenter();
          }
        });
      }
      catch (Exception ex)
      {}
    }
  }

  /**
   * Set the size of the entire window (including title bar and borders)
   * in device coordinates.<br>
   * The window coordinates are unchanged.<br>
   * Due to rescaling some graphics resolution is lost. To avoid this, use size
   * parameters when instantiating the GPanel.
   */
  public void windowSize(int width, int height)
  {
    _gPane.windowSize(width, height);
  }

  /**
   * Enable/disable resizing of the window by mouse dragging.
   */
  public void resizable(final boolean b)
  {
    if (EventQueue.isDispatchThread())
    {
      _gPane.getWindow().setResizable(b);
    }

    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            _gPane.getWindow().setResizable(b);
          }
        });
      }
      catch (Exception ex)
      {}
    }
  }

  /**
   * Return the Graphics2D context of the offscreen buffer
   */
  public Graphics2D getOffG2D()
  {
    return _gPane.getOffG2D();
  }

  /**
   * Release all used system resources (offscreen buffer, graphics context)
   * and unblock getKeyWait() and getKeyCodeWait().
   * After calling, don't use the GPanel instance anymore.
   */
  public void dispose()
  {
    _gPane.dispose();
  }

  /**
   * Disable/enable the title bar's closing button.<br>
   * (Even a registered ExitListener will not be called.)
   */
  public void disableClose(final boolean b)
  {
    if (EventQueue.isDispatchThread())
    {
      _gPane.getWindow().disableClose(b);
    }

    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            _gPane.getWindow().disableClose(b);
          }
        });
      }
      catch (Exception ex)
      {}
    }
  }

  /**
   * Create a store buffer and copy current graphics to it.
   * @see #recallGraphics
   * @see #clearStore
   */
  public void storeGraphics()
  {
    _gPane.storeGraphics();
  }

  /**
   * Copy graphics from store buffer to offscreen buffer
   * and render it on the screen window (if enableRepaint(true)).
   * @see #storeGraphics
   * @see #clearStore
   */
  public void recallGraphics()
  {
    _gPane.recallGraphics();
  }

  /**
   * Clear store buffer by uniformly painting it with with given color.
   * @see #storeGraphics
   * @see #recallGraphics
   */
  public void clearStore(Color color)
  {
    _gPane.clearStore(color);
  }


  /**
   * Return version information
   */
  public String getVersion()
  {
    return SharedConstants.VERSION;
  }

  /**
   * Return copywrite information
   */
  public String getAbout()
  {
    return SharedConstants.ABOUT;
  }

  /**
   * Return the color of the pixel at given window coordinates.
   */
  public Color getPixelColor(double x, double y)
  {
    return _gPane.getPixelColor(x, y);
  }

  /**
   * Return the color of the pixel at given point (in window coordinates).
   */
  public Color getPixelColor(Point2D.Double pt)
  {
    return _gPane.getPixelColor(pt);
  }

  /**
   * Set the focusable state to the specified value.
   */
  public void setFocusable(boolean focusable)
  {
    _gPane.setFocusable(focusable);
  }

  /**
   * Invoke the internal JPanels repaint().
   */
  public void repaint()
  {
    _gPane.repaint();
  }

  /**
   * Invoke the internal JPanels addMouseListener().
   */
  public void addMouseListener(MouseListener listener)
  {
    _gPane.addMouseListener(listener);
  }

  /**
   * Invoke the internal JPanels addMouseMotionListener().
   */
  public void addMouseMotionListener(MouseMotionListener listener)
  {
    _gPane.addMouseMotionListener(listener);
  }

  /**
   * Invoke the internal JPanels addMouseWheelListener().
   */
  public void addMouseWheelListener(MouseWheelListener listener)
  {
    _gPane.addMouseWheelListener(listener);
  }

    /**
   * Return true, if a key was typed
   * since the last call to getChar() or getCharWait().
   * The one-character buffer is not changed.
   * Put the current thread to sleep for 1 ms, to improve
   * response time when used in a loop.
   */
  public boolean kbhit()
  {
    return _gPane.kbhit();
  }

  /**
   * Return the unicode character associated with last key typed.
   * The one-character buffer is cleared.
   * Return KeyEvent.CHAR_UNDEFINED if the buffer is empty.
   */
  public char getKey()
  {
    return _gPane.getKey();
  }

  /**
   * Return the keycode associated with last key pressed.
   * The one-character buffer is cleared.
   * Return KeyEvent.CHAR_UNDEFINED if the buffer is empty.
   */
  public int getKeyCode()
  {
    return _gPane.getKeyCode();
  }

  /**
   * Wait until a key is typed and
   * return the unicode character associated with it.
   * Unblocked by calling dispose().
   */
  public char getKeyWait()
  {
    return _gPane.getKeyWait();
  }

  /**
   * Wait until a key is typed and
   * return the keycode associated with last key pressed.
   * Unblocked by calling dispose().
   */
  public int getKeyCodeWait()
  {
    return _gPane.getKeyCodeWait();
  }

  /**
   *  Return the modifiers associated with last key pressed.
   */
   public int getModifiers()
  {
    return _gPane.getModifiers();
  }

  /**
   *  Return the modifiers text description associated with last key pressed.
   */
  public String getModifiersText()
  {
    return _gPane.getModifiersText();
  }

  /**
   * Append the specified component to the container.
   */
  public void add(Component comp)
  {
    _gPane.add(comp);
  }

  /**
   * Validate the container.
   */
  public void validate()
  {
    _gPane.validate();
  }

  /**
   * Return the GPane container that holds the graphics.
   */
  public GPane getPane()
  {
    return _gPane;
  }

  /**
   * Delay execution for the given amount of time ( in ms ).
   */
  public static void delay(int time)
  {
    try
    {
      Thread.currentThread().sleep(time);
    }
    catch (Exception e) {}
  }

}
