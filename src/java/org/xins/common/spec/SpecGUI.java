/*
 * $Id: SpecGUI.java,v 1.8 2007/09/18 11:20:47 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;
import org.xins.common.xml.Viewer;

/**
 * Graphical user interface that allows to browse the specification of an API
 * and execute the functions of this API.
 *
 * @version $Revision: 1.8 $ $Date: 2007/09/18 11:20:47 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 2.1
 */
public class SpecGUI {

   /**
    * Perl 5 pattern compiler.
    */
   private static final Perl5Compiler PATTERN_COMPILER = new Perl5Compiler();

   private JFrame specFrame;

   private JPanel specPanel;

   private JMenuBar specMenuBar;

   private JLabel jlAPIName;

   private JTextField jtfEnvironment;

   private JTextField jtfQuery;

   private Viewer xmlViewer;

   private APISpec specs;

   private Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();

   /**
    * Constructs a new <code>ConsoleGUI</code>.
    */
   public SpecGUI() {
      JFrame mainFrame = new JFrame();
      initUI(mainFrame);
      initData();
      mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      mainFrame.setVisible(true);
   }

   /**
    * Constructs a new <code>SpecGUI</code>.
    *
    * @param mainFrame
    *    the main frame or <code>null</code> if no frame is available.
    */
   public SpecGUI(JFrame mainFrame) {
      initUI(mainFrame);
      initData();
   }

   /**
    * Creates the user interface.
    * This method also creates the actions available in the menu.
    *
    * @param mainFrame
    *    the main frame or <code>null</code> if no frame is available.
    */
   protected void initUI(JFrame mainFrame) {
      specPanel = new JPanel();
      xmlViewer = new Viewer();
      xmlViewer.setPreferredSize(new Dimension(500, 400));
      specPanel.setLayout(new BorderLayout(5,5));

      JPanel queryPanel = createQueryPanel();
      specPanel.add(queryPanel, BorderLayout.NORTH);
      JScrollPane scrollXMLViewer = new JScrollPane(xmlViewer);
      scrollXMLViewer.setBorder(new EmptyBorder(5,5,5,5));
      specPanel.add(scrollXMLViewer, BorderLayout.CENTER);

      // Add the actions
      createMenuBar();

      if (mainFrame != null) {
         mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         URL iconLocation = SpecGUI.class.getResource("/org/xins/common/servlet/container/xins.gif");
         if (iconLocation != null) {
            mainFrame.setIconImage(new ImageIcon(iconLocation).getImage());
         }
         mainFrame.setTitle("XINS Specification Viewer");
         mainFrame.setJMenuBar(getMenuBar());
         mainFrame.getContentPane().add(getMainPanel());
         mainFrame.pack();

         // Center the JFrame
         Dimension appDim = mainFrame.getSize();
         mainFrame.setLocation((screenDim.width - appDim.width) / 2,(screenDim.height - appDim.height) / 2);
         specFrame = mainFrame;
      }
   }

   protected void initData() {
      try {
         ElementParser parser = new ElementParser();
         Element webapp = parser.parse(getClass().getResourceAsStream("/WEB-INF/web.xml"));
         String apiClassName = null;
         Iterator itParams = webapp.getUniqueChildElement("servlet").getChildElements("init-param").iterator();
         while (itParams.hasNext()) {
            Element initParam = (Element) itParams.next();
            String paramName = initParam.getUniqueChildElement("param-name").getText();
            if (paramName.equals("org.xins.api.class")) {
               apiClassName = initParam.getUniqueChildElement("param-value").getText();
            }
         }
         Class apiClass = Class.forName(apiClassName);
         specs = new APISpec(apiClass, getClass().getResource("/WEB-INF/specs/").toString());
         jlAPIName.setToolTipText(specs.getDescription());

      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   protected JPanel createQueryPanel() {
      //JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      JPanel queryPanel = new JPanel(new BorderLayout());
      jtfEnvironment = new JTextField("http://localhost:8080/?_convention=_xins-std");
      jtfQuery = new JTextField();
      jtfQuery.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            query(jtfQuery.getText());
         }
      });
      JPanel queryLabelPanel = new JPanel();
      queryLabelPanel.setLayout(new GridLayout(2,1,5,5));

      jlAPIName = new JLabel("API") {
         public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Color background = getBackground();

            Paint oldPaint = g2.getPaint();
            GradientPaint gradient = new GradientPaint(0.0f, 0.0f, background.brighter(),
                  specPanel.getWidth() + 0.1f, getHeight() + 0.1f, background.darker());
            g2.setPaint(gradient);
            g2.fill(new Rectangle(specPanel.getWidth(), getHeight()));
            g2.setPaint(oldPaint);
            super.paint(g);
         }
      };
      jlAPIName.setOpaque(false);
      jlAPIName.setFont(jlAPIName.getFont().deriveFont(20.0f));
      queryPanel.add(jlAPIName, BorderLayout.NORTH);

      queryLabelPanel.add(new JLabel("Environment: "));
      queryLabelPanel.add(new JLabel("Query: "));
      queryLabelPanel.setBorder(new EmptyBorder(5,5,0,0));
      JPanel queryTFPanel = new JPanel();
      queryTFPanel.setLayout(new GridLayout(2,1,5,5));
      queryTFPanel.add(jtfEnvironment);
      queryTFPanel.add(jtfQuery);
      queryTFPanel.setBorder(new EmptyBorder(5,0,0,5));
      queryPanel.add(queryLabelPanel, BorderLayout.WEST);
      queryPanel.add(queryTFPanel, BorderLayout.CENTER);
      return queryPanel;
   }

   protected void createMenuBar() {
      JMenu fileMenu = new JMenu("File");
      fileMenu.setMnemonic('f');
      Action clearAction = new AbstractAction("Clear") {
         public void actionPerformed(ActionEvent ae) {
            xmlViewer.setText("");
         }
      };
      fileMenu.add(clearAction);
      fileMenu.add(new TestPatternAction());
      fileMenu.addSeparator();
      Action exitAction = new AbstractAction("Exit") {
         public void actionPerformed(ActionEvent ae) {
            System.exit(0);
         }
      };
      exitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
      fileMenu.add(exitAction);

      JMenu metaFunctionsMenu = new JMenu("Meta functions");
      metaFunctionsMenu.setMnemonic('m');
      metaFunctionsMenu.add(new QueryFunction("_GetVersion"));
      metaFunctionsMenu.add(new QueryFunction("_GetSettings"));
      metaFunctionsMenu.add(new QueryFunction("_GetStatistics"));
      metaFunctionsMenu.add(new QueryFunction("_NoOp"));
      metaFunctionsMenu.add(new QueryFunction("_ReloadProperties"));
      metaFunctionsMenu.add(new QueryFunction("_CheckLinks"));
      metaFunctionsMenu.add(new QueryFunction("_ResetStatistics"));
      metaFunctionsMenu.add(new QueryFunction("_WSDL"));
      metaFunctionsMenu.add(new QueryFunction("_SMD"));
      metaFunctionsMenu.add(new QueryFunction("_GetFunctionList"));
      metaFunctionsMenu.add(new QueryFunction("_DisableAPI"));
      metaFunctionsMenu.add(new QueryFunction("_EnableAPI"));
      JMenu specMenu = new JMenu("Specifications");
      specMenu.setMnemonic('s');

      JMenu testFormMenu = new JMenu("Test Form");
      testFormMenu.setMnemonic('t');
      try {
         Element api = new ElementParser().parse(getClass().getResourceAsStream("/WEB-INF/specs/api.xml"));
         String apiName = api.getAttribute("name");
         jlAPIName.setText(apiName + " API");
         jtfEnvironment.setText("http://localhost:8080/" + apiName + "/?_convention=_xins-std");
         Iterator itFunctions = api.getChildElements("function").iterator();
         while (itFunctions.hasNext()) {
            Element nextFunction = (Element) itFunctions.next();
            String functionName = nextFunction.getAttribute("name");
            specMenu.add(new ViewSpecAction(functionName + ".fnc"));
            testFormMenu.add(new TestFormAction(functionName));
         }
         List types = api.getChildElements("type");
         if (types.size() > 0) {
            specMenu.addSeparator();
         }
         Iterator itTypes = types.iterator();
         while (itTypes.hasNext()) {
            Element nextType = (Element) itTypes.next();
            String typeName = nextType.getAttribute("name");
            if (typeName.indexOf('/') != -1) {
               typeName = typeName.substring(typeName.indexOf('/') + 1);
            }
            specMenu.add(new ViewSpecAction(typeName + ".typ"));
         }
         List resultCodes = api.getChildElements("resultcode");
         if (resultCodes.size() > 0) {
            specMenu.addSeparator();
         }
         Iterator itResultCodes = resultCodes.iterator();
         while (itResultCodes.hasNext()) {
            Element nextResultCode = (Element) itResultCodes.next();
            String rcdName = nextResultCode.getAttribute("name");
            if (rcdName.indexOf('/') != -1) {
               rcdName = rcdName.substring(rcdName.indexOf('/') + 1);
            }
            specMenu.add(new ViewSpecAction(rcdName + ".rcd"));
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }

      JMenu helpMenu = new JMenu("Help");
      helpMenu.setMnemonic('h');
      Action aboutAction = new AbstractAction("About") {
         public void actionPerformed(ActionEvent ae) {
            Object[] aboutMessage = { "XINS", "http://www.xins.org/" };

            JOptionPane optionPane = new JOptionPane();
            optionPane.setMessage(aboutMessage);
            optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
            JDialog dialog = optionPane.createDialog(null, "About");
            dialog.setVisible(true);
         }
      };
      helpMenu.add(aboutAction);
      specMenuBar = new JMenuBar();
      specMenuBar.add(fileMenu);
      specMenuBar.add(metaFunctionsMenu);
      specMenuBar.add(specMenu);
      specMenuBar.add(testFormMenu);
      specMenuBar.add(helpMenu);
   }

   public JPanel getMainPanel() {
      return specPanel;
   }

   public JMenuBar getMenuBar() {
      return specMenuBar;
   }

   private void query(String url) {
      try {
         URL urlQuery = new URL(url);
         xmlViewer.setIndentation(!url.endsWith("_function=_WSDL"));
         xmlViewer.parse(urlQuery.openStream());
         /*BufferedReader in = new BufferedReader(new InputStreamReader(urlQuery.openStream()));

         String inputLine;

         while ((inputLine = in.readLine()) != null) {
            xmlViewer.getDocument().insertString(xmlViewer.getDocument().getLength(), inputLine + "\n", null);
         }
         in.close();*/
      } catch (IOException ioe) {
         ioe.printStackTrace();
      }
   }

   private void showDialog(JPanel panel, String title) {
      JDialog testFormDialog = new JDialog(specFrame, title, false);
      testFormDialog.getContentPane().add(panel);
      testFormDialog.pack();
      Dimension appDim = testFormDialog.getSize();
      testFormDialog.setLocation((screenDim.width - appDim.width) / 2,(screenDim.height - appDim.height) / 2);
      testFormDialog.setVisible(true);
   }

   class QueryMetaFunction extends AbstractAction {

      QueryMetaFunction(String metaFunction) {
         super(metaFunction);
      }

      public void actionPerformed(ActionEvent ae) {
         String query = jtfEnvironment.getText() + "&_function=_" + getValue(Action.NAME);
         jtfQuery.setText(query);
         query(query);
      }
   }

   class QueryFunction extends AbstractAction {

      private TestFormPanel testForm;

      QueryFunction(String functionName) {
         super(functionName);
      }

      public void actionPerformed(ActionEvent ae) {
         String query = jtfEnvironment.getText() + "&_function=" + getValue(Action.NAME);
         if (testForm != null) {
            query += testForm.getParameters();
         }
         jtfQuery.setText(query);
         query(query);
      }

      void setTestForm(TestFormPanel testForm) {
         this.testForm = testForm;
      }
   }

   class ViewSpecAction extends AbstractAction {

      ViewSpecAction(String specFile) {
         super(specFile);
      }

      public void actionPerformed(ActionEvent ae) {
         try {
            xmlViewer.setIndentation(false);
            xmlViewer.parse(getClass().getResourceAsStream("/WEB-INF/specs/" + getValue(Action.NAME)));
         } catch (IOException ioe) {
            ioe.printStackTrace();
         }
      }
   }

   class TestFormAction extends AbstractAction {

      TestFormAction(String functionName) {
         super(functionName);
      }

      public void actionPerformed(ActionEvent ae) {
         String functionName = (String) getValue(Action.NAME);
         QueryFunction queryAction = new QueryFunction(functionName);
         TestFormPanel testFormPanel = new TestFormPanel(specs, functionName, queryAction);
         queryAction.setTestForm(testFormPanel);
         showDialog(testFormPanel, specs.getName() +" API");
      }
   }

   class TestPatternAction extends AbstractAction {

      private Color tfBackground;
      private Color tfInvalidColor;
      private Color tfValidColor;
      private Perl5Matcher patternMatcher = new Perl5Matcher();

      TestPatternAction() {
         super("Test regular expression");
         tfBackground = UIManager.getColor("TextField.background");
         tfInvalidColor = new Color(
               Math.min(tfBackground.getRed() + 30, 255),
               Math.max(tfBackground.getGreen() - 15, 0),
               Math.max(tfBackground.getBlue() - 20, 0));
         tfValidColor = new Color(
               Math.max(tfBackground.getRed() -15, 0),
               Math.min(tfBackground.getGreen() + 30, 255),
               Math.max(tfBackground.getBlue() - 20, 0));
      }

      public void actionPerformed(ActionEvent ae) {
         Object[] components = new Object[4];
         components[0] = "Pattern:";
         final JTextField jtfPattern = new JTextField();
         jtfPattern.setBackground(tfValidColor);
         components[1] = jtfPattern;
         components[2] = "Text to test:";
         final JTextField jtfTextToTest = new JTextField();
         jtfTextToTest.setBackground(tfInvalidColor);
         components[3] = jtfTextToTest;

         jtfPattern.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent de) {
               try {
                  String patternText = de.getDocument().getText(0, de.getDocument().getLength());
                  try {
                     Pattern pattern = PATTERN_COMPILER.compile(patternText);
                     jtfPattern.setBackground(tfValidColor);
                     if (patternMatcher.matches(jtfTextToTest.getText(), pattern)) {
                        jtfTextToTest.setBackground(tfValidColor);
                     } else {
                        jtfTextToTest.setBackground(tfInvalidColor);
                     }
                  } catch (MalformedPatternException mpex) {
                     jtfPattern.setBackground(tfInvalidColor);
                  }
               } catch (BadLocationException ble) {
                  ble.printStackTrace();
               } catch (Exception ex) {
                  ex.printStackTrace();
               }
            }
            public void insertUpdate(DocumentEvent de) {
               changedUpdate(de);
            }
            public void removeUpdate(DocumentEvent de) {
               changedUpdate(de);
            }
         });
         jtfTextToTest.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent de) {
               try {
                  String patternText = jtfPattern.getText();
                  Pattern pattern = PATTERN_COMPILER.compile(patternText);
                  if (patternMatcher.matches(jtfTextToTest.getText(), pattern)) {
                     jtfTextToTest.setBackground(tfValidColor);
                  } else {
                     jtfTextToTest.setBackground(tfInvalidColor);
                  }
               } catch (MalformedPatternException mpex) {
               }
            }
            public void insertUpdate(DocumentEvent de) {
               changedUpdate(de);
            }
            public void removeUpdate(DocumentEvent de) {
               changedUpdate(de);
            }
         });

         JOptionPane.showOptionDialog(specFrame, components, "Test regular expression",
               JOptionPane.CLOSED_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

      }
   }
}
