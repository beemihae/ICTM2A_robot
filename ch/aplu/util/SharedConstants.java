// ShareConstants.java

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

/* History:
V1.36 - Sep 2004:
  - First official release
V1.37 - Nov 2004:
  - Add focus listener in GPanel and request focus
    when using APPLETFRAME to show the GPanel window
    in front of browser window
V1.38 - Dez 2004:
  - Add Console.setTitle()
V1.39 - Jan 2005:
  - Add Console.printf() in V5 library, add init() in setTitle()
V1.40 - March 2005:
  - No changes (modifications in ch.aplu.turtle)
V1.41 - May 2005:
  - Autowrapping in Console
V1.42 - Dec 2005:
  - class LptPort, ExitListener in Console
V1.43 - Feb 2006:
  - No changes (modifications in ch.aplu.turtle)
V1.44 - March 2007:
  - Remove deprecated methods from GPanel
  - Added methods in GPanel: windowSize(int width, int height)
                             windowCenter()
                             several constructors to set the size of the client area
  - new class Fullscreen in order to create a GPanel on full screen
  - Minor changes in GWindow in order to support full screen windows
  - Minor updates in Javadoc documentation
  - Classes added in package
     - MessagePane
     - QuitPane
     - Cleanable (interface)
  - class Console: Call Swing methods from Event Dispatch Thread only
 V1.45 - Aug 2007:
   - Repaired printf() in ch.aplu.util.Console (for J2SE V5up)
   - Added class Monitor
   - Modified Console use wait/notify to reduce CPU time (from 100% to 2%)
 V1.46 - Aug 2007:
   - Added classes SoundPlayer, SoundPlayerExt and interface SoundPlayerListener
   - GPanel.color() now returns the previous color
 V1.47 - Sept 2007:
   - Correction of transformation methods toWindow, toUser, when GPanel is zoomed
 V1.48 - Sept 2007:
   - Remove bug with getKeyWait()
   - Add storeGraphics(), recallGraphics(), clearStore();
 V1.49 - Oct 2007:
   - Added: interface SoundConverter for easy integration of a sound converter
 V1.50 - Oct 2007:
   - Modified implementation of MessagePane, added MessagePane.title()
 V1.51- Nov 2007:
    - No changes (modifications in ch.aplu.turtle)
 V1.52 - Nov 2007:
    - Added bean support for GPanel in order use it in a Gui builder
 V1.53 - Nov 2007:
    - No changes (modifications in ch.aplu.turtle)
 V1.54 - Nov 2007:
    - Added method JRunner, GPane creates GPanel 100x100
    - class Monitor modified: notifyAll() instead of notify()
 V1.55 - Dec 2007:
    - Added more methods to JRunner
    - Added property enableFocus to GPane, default: setFocusable(false)
 V1.56 - March 2008:
    - ModelessOptionDialog: changed icon resource to url, added: showTitle()
 V1.57 - March 2008:
    - JRunner.run() allows invoking void method, JRunner doc revisted
 V1.58 - March 2008:
    - No changes (modifications in ch.aplu.turtle)
 V1.59 - Oct 2008:
    - Changed code in MessageDialog in order to avoid depreciation message
    - Added Thread.currentThread().sleep(1) in QuitPane.quit()
    - Added Gpanel.getPixelColor(x, y) to return color of pixel at (x, y)
 V2.00 - Nov 2008:
    - Fixed bug in callback notification of AlarmTimer
    - J2SE V1.4 no longer supported
 V2.01 - Jan 2009:
    - GPanel no longer changes the current look-and-feel
 V2.02 - Feb 2009:
    - QuitPane, Console, GPanel, MessageDialog, MessagePane, ModelessOptionPane
      constructors and InputDialog methods run in EDT now
 V2.03 - Feb 2009:
    - Added ModelessOptionPane.dispose()
 V2.04 - Feb 2009:
    - Added ModelessOptionPane.setVisible(), ModelessOptionPane.getDialog()
    - Added QuitPane.setVisible(), QuitPane.getDialog()
    - Added MessageDialog.getDialog()
    - Added MessagePane.setVisible(),  MessagePane.getDialog()
 V2.05 - Feb 2009:
    - Fixed bug in QuitPane: listener cleanable did not work any more
    - Added QuitPane.halt() to block the current thread until Quit or Close is hit.
    - Fixed bug in BaseAlarmTimer: callback timeElapsed() was not triggered
      at elapsed time after calling resume()
 V2.06 - March 2009:
    - Added default constructor to QuitPane and QuitPane.addQuitNotifier()
 V2.07 - March 2009:
    - Added GPanel.delay()
 V2.08 - April 2009:
    - Added GPanel.addMouseMotionListener, GPanel.addMouseWheelListener
 V2.09 - June 2009:
    - Added GPane.delay()
*/

package ch.aplu.util;

interface SharedConstants
{
  int DEBUG_LEVEL_OFF = 0;
  int DEBUG_LEVEL_LOW = 1;
  int DEBUG_LEVEL_MEDIUM = 2;
  int DEBUG_LEVEL_HIGH = 3;

  int DEBUG = DEBUG_LEVEL_OFF;

  String ABOUT =
    "2003-2009 Aegidius Pluess\n" +
    "OpenSource Free Software\n" +
    "http://www.aplu.ch\n" +
    "All rights reserved";
  String VERSION = "2.09 - June 2009";
}
