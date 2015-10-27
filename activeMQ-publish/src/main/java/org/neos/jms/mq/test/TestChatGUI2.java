package org.neos.jms.mq.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.jms.JMSException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.neos.jms.mq.ChatConexion2;

public class TestChatGUI2 {
	String _block = "";
    String      appName     = "CHAT JMS - ";
    TestChatGUI2     mainGUI;
    JFrame      newFrame    = new JFrame(appName);
    JButton     sendMessage;
    JTextField  messageBox;
    JTextArea   chatBox;
    JTextField  usernameChooser;
    JFrame      preFrame;
    ChatConexion2 chat;
    String inMessage;
    String  username;
    JButton block    = new JButton("Block..."); //nueva linea
    JButton unBlock    = new JButton("UnBlock..."); //nueva linea
    JButton clear    = new JButton("Clear.."); //nueva linea
    JMenuBar  menu     = new JMenuBar(); //nueva linea

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager
                            .getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TestChatGUI2 mainGUI = new TestChatGUI2();
                
                mainGUI.preDisplay();
            }
        });
    }

    public void preDisplay() {
        newFrame.setVisible(false);
        preFrame = new JFrame(appName);
        usernameChooser = new JTextField(15);
        JLabel chooseUsernameLabel = new JLabel("Pick a username:");
        JButton enterServer = new JButton("Enter Chat Server");
        enterServer.addActionListener(new enterServerButtonListener());
        JPanel prePanel = new JPanel(new GridBagLayout());

        GridBagConstraints preRight = new GridBagConstraints();
        preRight.insets = new Insets(0, 0, 0, 10);
        preRight.anchor = GridBagConstraints.EAST;
        GridBagConstraints preLeft = new GridBagConstraints();
        preLeft.anchor = GridBagConstraints.WEST;
        preLeft.insets = new Insets(0, 10, 0, 10);
       
        preRight.fill = GridBagConstraints.HORIZONTAL;
        preRight.gridwidth = GridBagConstraints.REMAINDER;

        prePanel.add(chooseUsernameLabel, preLeft);
        prePanel.add(usernameChooser, preRight);
        preFrame.add(BorderLayout.CENTER, prePanel);
        preFrame.add(BorderLayout.SOUTH, enterServer);
        preFrame.setSize(300, 300);
        preFrame.setLocationRelativeTo(null);
        preFrame.setVisible(true);

    }

    public void display() throws IOException {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
      

        JPanel southPanel = new JPanel();
        southPanel.setBackground(Color.BLUE);
        southPanel.setLayout(new GridBagLayout());

        messageBox = new JTextField(30);
        messageBox.requestFocusInWindow();

        sendMessage = new JButton("Send Message");
        sendMessage.addActionListener(new sendMessageButtonListener());

        chatBox = new JTextArea();
        chatBox.setEditable(false);
        chatBox.setFont(new Font("Serif", Font.PLAIN, 15));
        chatBox.setLineWrap(true);

        mainPanel.add(new JScrollPane(chatBox), BorderLayout.CENTER);
        
        /***************NUEVAS LINEAS **************************/
        menu.add(block,BorderLayout.BEFORE_FIRST_LINE);
        menu.add(unBlock,BorderLayout.BEFORE_FIRST_LINE);
        menu.add(clear,BorderLayout.BEFORE_FIRST_LINE);
        mainPanel.add(menu, BorderLayout.NORTH);
        
        // clear text area
        clear.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent ev) {
        	   System.out.println("EN EL BOTON LIMPIAR....");
        	   chatBox.setText("Cleared all messages.....\n");
               messageBox.setText("");
           }
        });
        
     // replace subscriber according to blocked user
        block.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent ev) {
              String user = (String) JOptionPane.showInputDialog(newFrame,
                  "Block user:", "Select user name",
                  JOptionPane.QUESTION_MESSAGE, null,
                  null, _block);
              if (user == null ||  _block.equals(user)) return;
              _block = user;
              try {
                 chat.subscriber.close(); //nueva linea
                 String sel = "sender <> '" + _block + "'";
                 chat.subscriber = chat.getPubSession().createSubscriber(chat.chatTopic, sel, false);
                 chatBox.append("***blocking*** " + _block +"\n");
                // setMessageListener();
                 //incoming.append("blocking " + _block);
              } catch (JMSException ex) { ex.printStackTrace(); }
           }
        });
        
     // replace subscriber according to blocked user
        unBlock.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent ev) {
              String user = (String) JOptionPane.showInputDialog(newFrame,
                  "UnBlock user:", "Select user name",
                  JOptionPane.QUESTION_MESSAGE, null,
                  null, _block);
              if ( _block.equals(user)){
              _block = user;
              try {
                 chat.subscriber.close(); //nueva linea
                 String sel = "sender <> '" + _block + "'";
                 chat.subscriber = chat.getPubSession().createSubscriber(chat.chatTopic, sel, true);
                 chatBox.append("*** UnBlocking*** " + _block +"\n");
                // setMessageListener();
                 //incoming.append("blocking " + _block);
              } catch (JMSException ex) { ex.printStackTrace(); }
           }
           }
        });
        /*******************************************************/
        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.LINE_START;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.weightx = 512.0D;
        left.weighty = 1.0D;

        GridBagConstraints right = new GridBagConstraints();
        right.insets = new Insets(0, 10, 0, 0);
        right.anchor = GridBagConstraints.LINE_END;
        right.fill = GridBagConstraints.NONE;
        right.weightx = 1.0D;
        right.weighty = 1.0D;

        southPanel.add(messageBox, left);
        southPanel.add(sendMessage, right);

        mainPanel.add(BorderLayout.SOUTH, southPanel);
        newFrame.add(mainPanel);
     
       // newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            	
            	int reply = JOptionPane.showConfirmDialog(newFrame, "Are you sure to close this chat?", "Really Closing?", JOptionPane.YES_NO_OPTION);
            	System.out.println(reply);
            	if (reply == JOptionPane.YES_OPTION)
                {
            		try {
						chat.writeMessage(" lef the session..",username);
						chat.close();
						System.out.println("SE HA CERRADO CORREctamente la SESSION....");
					} catch (JMSException e) {
						e.printStackTrace();
					}
                System.exit(0);
                }
            }
        });
        newFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        newFrame.setSize(470, 300);
        newFrame.setLocationRelativeTo(null); //pone al centro la ventana
        newFrame.setVisible(true);
        
        
       
        
        
    }

    class sendMessageButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (messageBox.getText().length() < 1) {
                // do nothing
            } else if (messageBox.getText().equals(".clear")) {
                chatBox.setText("Cleared all messages\n");
                messageBox.setText("");
            } else {
            	try {
					chat.writeMessage(messageBox.getText(),username);
				} catch (JMSException e) {
					e.printStackTrace();
				}
                chatBox.append("<" + username + ">:  " + messageBox.getText()+ "\n");
                messageBox.setText("");
            }
            messageBox.requestFocusInWindow();
        }
    }

    //

	class enterServerButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			username = usernameChooser.getText();
			appName = appName+ username;
			newFrame.setTitle(appName);
			try {
				chat = new ChatConexion2("TopicCF", "topic1", username);
				chat.writeMessage("has joined the session..",username);
				
				TimerTask task = new TimerTask() {
					  @Override
					  public void run() {
						  inMessage = chat.getMessageText();
							if (inMessage != null) {
								if (!inMessage.equals("")) {
									chatBox.append(inMessage + "\n");
									messageBox.setText("");
									chat.setMessageText("");
								}
							}
					  }
					};
					Timer timer = new Timer();
					timer.schedule(task, 0l, 1000l); 

			} catch (Exception e) {

				e.printStackTrace();
			}
			if (username.length() < 1) {
				System.out.println("No!");
			} else {
				preFrame.setVisible(false);
				try {
					display();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

    }
}