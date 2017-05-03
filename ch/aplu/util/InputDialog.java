// InputDialog.java

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

import javax.swing.*;
import java.io.*;
import java.awt.*;

/**
 * Modal input dialog.<br><br>
 * If the user enters a value with wrong type, the dialog will reappear.<br><br>
 * All methods run in the Event Dispatch Thread (EDT).
 */
public class InputDialog
{
  String _title;
  String _prompt;
  String _str;

  /**
   * Construct dialog with default title and prompt.
   */
  public InputDialog()
  {
    this("Input Dialog", "Enter a value please:");
  }

  /**
   * Construct dialog with given title and prompt.
   */
  public InputDialog(String title, String prompt)
  {
    _title = title;
    _prompt = prompt;
  }

  /**
   * Show the dialog to get a string value.
   * @return null if user pressed the Cancel button.
   * @see #readString
   */
  public String getString()
  {
    if (EventQueue.isDispatchThread())
      _str = askString();
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            _str = askString();
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
    return _str;
  }

  private String askString()
  {
    String valueStr;
    boolean ok = false;

    do
    {
      valueStr = JOptionPane.showInputDialog(null, _prompt, _title,
                                             JOptionPane.QUESTION_MESSAGE);
      if (valueStr == null)
        return null;
    }
    while (valueStr.trim().length() == 0);
    return valueStr;
  }

  /**
   * Show the dialog to get a string value.
   * @see #getString
   * @return null if user pressed Cancel button.
   */
  public String readString()
  {
    return getString();
  }

  /**
   * Show the dialog to get an integer value.
   * @return null if user pressed Cancel button.
   */
  public Integer getInt()
  {
    Integer value = null;
    String valueStr;
    boolean ok = false;

    while (!ok)
    {
      valueStr = getString();
      if (valueStr == null)
        return null;
      try
      {
        value = new Integer(valueStr);
        ok = true;
      }
      catch (NumberFormatException e) {}
    }
    return value;
  }

  /**
   * Show the dialog to read an integer value.
   * Redraw the dialog with empty field if Cancel is pressed.
   */
  public int readInt()
  {
    Integer value = null;
    do
    {
      value = getInt();
    }
    while (value == null);
    return value.intValue();
  }

  /**
   * Show the dialog to get a double value.
   * @return null if user pressed Cancel button.
   */
  public Double getDouble()
  {
    Double value = null;
    String valueStr;
    boolean ok = false;

    while (!ok)
    {
      valueStr = getString();
      if (valueStr == null)
        return null;
      try
      {
        value = new Double(valueStr);
        ok = true;
      }
      catch (NumberFormatException e) {}
    }
    return value;
  }

  /**
   * Show the dialog to read a double value.
   * Redraw the dialog with empty field if Cancel is pressed.
   */
  public double readDouble()
  {
    Double value = null;
    do
    {
      value = getDouble();
    }
    while (value == null);
    return value.doubleValue();
  }

  /**
   * Show the dialog to get a long value.
   * @return null if user pressed Cancel button.
   */
  public Long getLong()
  {
    Long value = null;
    String valueStr;
    boolean ok = false;

    while (!ok)
    {
      valueStr = getString();
      if (valueStr == null)
        return null;
      try
      {
        value = new Long(valueStr);
        ok = true;
      }
      catch (NumberFormatException e) {}
    }
    return value;
  }

  /**
   * Show the dialog to read a long value.
   * Redraw the dialog with empty field if Cancel is pressed.
   */
  public long readLong()
  {
    Long value = null;
    do
    {
      value = getLong();
    }
    while (value == null);
    return value.longValue();
  }


  /**
   * Show the dialog to get a boolean.
   * @return null if user pressed Cancel button.
   */
  public Boolean getBoolean()
  {
    final String[] NOYES =
    {
      "no", "yes"
    };

    String rc = (String)JOptionPane.showInputDialog(null, _prompt, _title,
    JOptionPane.QUESTION_MESSAGE, null, NOYES, NOYES[0]);

    if (rc != null)
      return (rc.equals("no") ? Boolean.FALSE : Boolean.TRUE);
    else
      return null;
  }

  /**
   * Show the dialog to read a boolean.
   * Redraw the dialog with empty field if Cancel is pressed.
   */
  public boolean readBoolean()
  {
    Boolean value = null;
    do
    {
      value = getBoolean();
    }
    while (value == null);
    return value.booleanValue();
  }
}

