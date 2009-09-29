/*
 * $Id: ConsoleGUI.java,v 1.9 2007/09/18 08:45:08 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URL;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 * Graphical user interface for the Servlet container.
 * This class may move to another package.
 *
 * @version $Revision: 1.9 $ $Date: 2007/09/18 08:45:08 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 2.1
 */
public class ConsoleGUI {

   private JPanel consolePanel;

   private JTextPane console;

   private JMenuBar consoleMenuBar;

   private Style[] logStyles;

   private int logLevel = 0;

   private Pattern patternFilter = null;

   private String logFilter = null;

   /**
    * Constructs a new <code>ConsoleGUI</code>.
    *
    * @param mainFrame
    *    the main frame or <code>null</code> if no frame is available.
    *
    * @param cmdArgs
    *    the command line arguments, cannot be <code>null</code>.
    */
   public ConsoleGUI(JFrame mainFrame, CommandLineArguments cmdArgs) {
      initUI(mainFrame, cmdArgs);
      initData();
   }

   /**
    * Creates the user interface.
    * This method also creates the actions available in the menu.
    *
    * @param mainFrame
    *    the main frame or <code>null</code> if no frame is available.
    *
    * @param cmdArgs
    *    the command line arguments, cannot be <code>null</code>.
    */
   protected void initUI(final JFrame mainFrame, final CommandLineArguments cmdArgs) {
      consolePanel = new JPanel();
      console = new JTextPane();
      console.setPreferredSize(new Dimension(700, 400));
      consolePanel.setLayout(new BorderLayout(5,5));
      consolePanel.add(new JScrollPane(console), BorderLayout.CENTER);

      consoleMenuBar = new JMenuBar();

      // Add the actions
      JMenu consoleMenu = new JMenu("Console");
      consoleMenu.setMnemonic('c');
      Action showSpec = new AbstractAction("Specifications") {
         public void actionPerformed(ActionEvent ae) {
            try {
               ClassLoader loader = ServletClassLoader.getServletClassLoader(cmdArgs.getWarFile(), cmdArgs.getLoaderMode());

               loader.loadClass("org.xins.common.spec.SpecGUI").newInstance();
            } catch (Exception ex) {
               ex.printStackTrace();
            }
         }
      };
      showSpec.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
      consoleMenu.add(showSpec);
      Action clearAction = new AbstractAction("Clear") {
         public void actionPerformed(ActionEvent ae) {
            console.setText("");
         }
      };
      consoleMenu.add(clearAction);
      consoleMenu.addSeparator();
      Action exitAction = new AbstractAction("Exit") {
         public void actionPerformed(ActionEvent ae) {
            System.exit(0);
         }
      };
      exitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
      consoleMenu.add(exitAction);

      JMenu logLevelMenu = new JMenu("Log level");
      logLevelMenu.setMnemonic('l');
      JCheckBoxMenuItem debugMenu = new JCheckBoxMenuItem(new ChangeLogLevel(0, "Debug"));
      debugMenu.setSelected(true);
      JCheckBoxMenuItem infoMenu = new JCheckBoxMenuItem(new ChangeLogLevel(1, "Info"));
      JCheckBoxMenuItem noticeMenu = new JCheckBoxMenuItem(new ChangeLogLevel(2, "Notice"));
      JCheckBoxMenuItem warningMenu = new JCheckBoxMenuItem(new ChangeLogLevel(3, "Warning"));
      JCheckBoxMenuItem errorMenu = new JCheckBoxMenuItem(new ChangeLogLevel(4, "Error"));
      JCheckBoxMenuItem fatalMenu = new JCheckBoxMenuItem(new ChangeLogLevel(5, "Fatal"));
      ButtonGroup logLevelGroup = new ButtonGroup();
      logLevelGroup.add(debugMenu);
      logLevelGroup.add(infoMenu);
      logLevelGroup.add(noticeMenu);
      logLevelGroup.add(warningMenu);
      logLevelGroup.add(errorMenu);
      logLevelGroup.add(fatalMenu);
      logLevelMenu.add(debugMenu);
      logLevelMenu.add(infoMenu);
      logLevelMenu.add(noticeMenu);
      logLevelMenu.add(warningMenu);
      logLevelMenu.add(errorMenu);
      logLevelMenu.add(fatalMenu);
      Action regexpFilterAction = new AbstractAction("Filter") {
         public void actionPerformed(ActionEvent ae) {
            String pattern = (String) JOptionPane.showInputDialog(mainFrame,
                  "Please regular expression to match", "Log Filter",
                  JOptionPane.QUESTION_MESSAGE, null, null, logFilter);
            if ("".equals(pattern)) {
               logFilter = null;
               patternFilter = null;
            } else if (pattern != null) {
               logFilter = pattern;
               patternFilter = Pattern.compile(logFilter);
            }
         }
      };
      logLevelMenu.add(regexpFilterAction);

      JMenu helpMenu = new JMenu("Help");
      helpMenu.setMnemonic('h');
      String javaVersion = System.getProperty("java.version");
      if (javaVersion.startsWith("1.6") || javaVersion.startsWith("1.7")) {
         helpMenu.add(new BrowseAction("XINS Web site", "http://www.xins.org/"));
         helpMenu.add(new BrowseAction("User Guide", "http://www.xins.org/docs/"));
         helpMenu.addSeparator();
      }
      Action aboutAction = new AbstractAction("About") {
         public void actionPerformed(ActionEvent ae) {
            Object[] aboutMessage = { "XINS", "http://www.xins.org/" };

            JOptionPane optionPane = new JOptionPane();
            optionPane.setMessage(aboutMessage);
            optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
            JDialog dialog = optionPane.createDialog(mainFrame, "About");
            dialog.setVisible(true);
         }
      };
      helpMenu.add(aboutAction);
      consoleMenuBar.add(consoleMenu);
      consoleMenuBar.add(logLevelMenu);
      consoleMenuBar.add(helpMenu);

      if (mainFrame != null) {
         mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         URL iconLocation = ConsoleGUI.class.getResource("/org/xins/common/servlet/container/xins.gif");
         if (iconLocation != null) {
            mainFrame.setIconImage(new ImageIcon(iconLocation).getImage());
         }
         String title = "XINS Servlet Console " + cmdArgs.getWarFile().getName();
         if (cmdArgs.getPort() != HTTPServletStarter.DEFAULT_PORT_NUMBER) {
            title += " [port:" + cmdArgs.getPort() + "]";
         }
         mainFrame.setTitle(title);
         mainFrame.setJMenuBar(getMenuBar());
         mainFrame.getContentPane().add(getMainPanel());
         mainFrame.pack();

         // Center the JFrame
         Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
         Dimension appDim = mainFrame.getSize();
         mainFrame.setLocation((screenDim.width - appDim.width) / 2,(screenDim.height - appDim.height) / 2);
      }
   }

   protected void initData() {

      // Initialize the styles
      Style debug = console.addStyle("Debug", null);
      StyleConstants.setForeground(debug, Color.DARK_GRAY);
      Style info = console.addStyle("Info", null);
      StyleConstants.setForeground(info, Color.BLACK);
      Style notice = console.addStyle("Notice", null);
      StyleConstants.setForeground(notice, Color.BLUE.darker());
      Style warning = console.addStyle("Warning", null);
      StyleConstants.setForeground(warning, Color.ORANGE.darker());
      Style error = console.addStyle("Error", null);
      StyleConstants.setForeground(error, Color.RED.darker());
      Style fatal = console.addStyle("Fatal", null);
      StyleConstants.setForeground(fatal, Color.RED);
      StyleConstants.setBackground(fatal, Color.LIGHT_GRAY);
      logStyles = new Style[] {debug, info, notice, warning, error, fatal};

      try {
         // Set up System.out
         PipedInputStream piOut = new PipedInputStream();
         PipedOutputStream poOut = new PipedOutputStream(piOut);
         System.setOut(new PrintStream(poOut, true));

         // Set up System.err
         /*PipedInputStream piErr = new PipedInputStream();
         PipedOutputStream poErr = new PipedOutputStream(piErr);
         System.setErr(new PrintStream(poErr, true));*/
         // Create reader threads
         new ReaderThread(piOut).start();
         //new ReaderThread(piErr).start();
      } catch (IOException ioe) {
      }
   }

   public JPanel getMainPanel() {
      return consolePanel;
   }

   public JMenuBar getMenuBar() {
      return consoleMenuBar;
   }

   protected int getLogLevel(String text) {
      String textToSearch = text;
      if (text.length() > 50) {
         textToSearch = text.substring(0, 50);
      }
      if (textToSearch.indexOf("DEBUG") != -1) {
         return 0;
      } else if (textToSearch.indexOf("INFO") != -1) {
         return 1;
      } else if (textToSearch.indexOf("NOTICE") != -1) {
         return 2;
      } else if (textToSearch.indexOf("WARN") != -1) {
         return 3;
      } else if (textToSearch.indexOf("ERROR") != -1) {
         return 4;
      } else if (textToSearch.indexOf("FATAL") != -1) {
         return 5;
      } else {
         return -1;
      }
   }

   class ReaderThread extends Thread {
      BufferedReader br;

      ReaderThread(PipedInputStream pi) {
         br = new BufferedReader(new InputStreamReader(pi));
      }

      public void run() {
         while (true) {
            try {
               final String text = br.readLine();
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     try {
                        int consoleLength = console.getDocument().getLength();
                        int messageLogLevel = getLogLevel(text);
                        if (messageLogLevel < logLevel && messageLogLevel != -1) {
                           return;
                        }
                        if (logFilter != null) {
                           boolean match = patternFilter.matcher(text).find();
                           if (!match) {
                              return;
                           }
                        }
                        if (messageLogLevel == -1) {
                           console.getDocument().insertString(consoleLength, text + "\n", null);
                        } else {
                           Style style = logStyles[messageLogLevel];
                           console.getDocument().insertString(consoleLength, text + "\n", style);
                        }

                        // Make sure the last line is always visible
                        consoleLength = console.getDocument().getLength();
                        console.setCaretPosition(consoleLength);

                        // Keep the text area down to a certain character size
                        int idealSize = 100000;
                        int maxExcess = 50000;
                        int excess = consoleLength - idealSize;
                        if (excess >= maxExcess) {
                           console.getDocument().remove(0, excess);
                        }
                     } catch (BadLocationException e) {
                     }
                  }
               });
            } catch (IOException e) {
               // XXX a Write end dead is throw everytime (I don't know why)
               try {
                  sleep(500);
               } catch (InterruptedException ie) {
               }
            }
         }
      }
   }

   class ChangeLogLevel extends AbstractAction {

      private int _newLogLevel;

      ChangeLogLevel(int newLogLevel, String level) {
         super(level);
         _newLogLevel = newLogLevel;
      }
      public void actionPerformed(ActionEvent ae) {
         logLevel = _newLogLevel;
      }
   }

   class BrowseAction extends AbstractAction {

      private String _url;

      BrowseAction(String title, String url) {
         super(title);
         _url = url;
      }

      public void actionPerformed(ActionEvent ae) {
         try {
            Class destkopClass = Class.forName("java.awt.Desktop");
            Object     desktop = destkopClass.getMethod("getDesktop").invoke(null);

            destkopClass.getMethod("browse", URI.class).invoke(desktop, new URI(_url));
         } catch (Throwable ex) {
            // Ignore
         }
      }
   }
}
