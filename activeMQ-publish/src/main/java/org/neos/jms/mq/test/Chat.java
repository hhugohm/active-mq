package org.neos.jms.mq.test;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
                                                                           
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
                                                                           
import javax.naming.InitialContext;
                                                                           
import javax.jms.Topic;
import javax.jms.Session;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.TopicSession;
import javax.jms.JMSException;
import javax.jms.TopicPublisher;
import javax.jms.MessageListener;
import javax.jms.TopicSubscriber;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
                                                                           
/**
   This class ilustrates a very simple chat application. Users will
   connect to "topic0" and exchange messages. Please note that this
   program does not perform any error checking.
 */
                                                                           
public class Chat
{
    String _block = "";
    TopicSubscriber subscriber;
                                                                           
    JFrame    frame    = new JFrame("Chat");
    JButton   send     = new JButton("Send");
    JMenuBar  menu     = new JMenuBar();
    JMenuItem block    = new JMenuItem("Block...");
    JMenuItem clear    = new JMenuItem("Clear");
    JTextArea incoming = new JTextArea();
    JTextArea outgoing = new JTextArea();
                                                                           
    public static void main(String[] args) throws Exception
    {
       @SuppressWarnings("unused")
	Chat chat = new Chat();
    }
                                                                           
    public Chat() throws Exception
    {
       // create publisher and subscriber
       InitialContext ctx = new InitialContext();
       TopicConnectionFactory factory = (TopicConnectionFactory)
           ctx.lookup("queueConnectionFactory");
       final Topic topic = (Topic) ctx.lookup("topic1");
       TopicConnection conn = factory.createTopicConnection();
       final TopicSession session = conn.createTopicSession(false,
           Session.AUTO_ACKNOWLEDGE);
       final TopicPublisher publisher = session.createPublisher(topic); 
       subscriber = session.createSubscriber(topic);
                                                                          
       // create the gui
       incoming.setEditable(false);
                                                                          
       // add menu items
       menu.add(block);
       menu.add(clear);
                                                                          
       // replace subscriber according to blocked user
       block.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ev) {
             String user = (String) JOptionPane.showInputDialog(frame,
                 "Block user:", "Select user name",
                 JOptionPane.QUESTION_MESSAGE, null,
                 null, _block);
             if (user == null ||  _block.equals(user)) return;
             _block = user;
             try {
                subscriber.close();
                String sel = "sender <> '" + _block + "'";
                subscriber = session.createSubscriber(topic, sel, false);//selector
                setMessageListener();
                incoming.append("blocking " + _block);
             } catch (JMSException ex) { ex.printStackTrace(); }
          }
       });
                                                                          
       // clear text area
       clear.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ev) {
             incoming.setText("");
          }
       });
                                                                          
       setMessageListener();
                                                                          
       // now start the connection
       conn.start();
                                                                          
       // send message when user clicks the button
       send.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
             String msg = outgoing.getText();
             if (msg.length() > 0) {
                try {
                   String sender = System.getProperty("user.name");
                   TextMessage m = session.createTextMessage(msg);
                   m.setStringProperty("sender", sender);
                   publisher.publish(m);
                   outgoing.setText("");
                } catch (JMSException ex) { ex.printStackTrace(); }
             }
          }
       });
                                                                          
       // setup rest of gui
       JPanel panel = new JPanel(new BorderLayout());
       panel.add(outgoing);
       panel.add(send, BorderLayout.EAST);
       JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
           incoming, panel);
       split.setOneTouchExpandable(true);
       split.setDividerLocation(205);
                                                                          
       frame.getContentPane().setLayout(new BorderLayout());
       frame.getContentPane().add(split);
       frame.getContentPane().add(menu, BorderLayout.NORTH);
       frame.setSize(new Dimension(400,300));
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setVisible(true);
    }
                                                                           
    public void setMessageListener() throws JMSException
    {
       // add asynchronous message listener
       subscriber.setMessageListener(new MessageListener() {
          public void onMessage(Message m) {
             try {
                TextMessage msg = (TextMessage) m;
                String sender = msg.getStringProperty("sender");
                incoming.append(sender + "> " + msg.getText() + "\n");
             } catch (Exception ex) { ex.printStackTrace(); }
          }
       });
    }
}