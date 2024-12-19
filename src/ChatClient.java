import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JTextArea chatArea;
    private JTextField messageField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClient::new);
    }

    public ChatClient() {
        JFrame frame = new JFrame("Чат клиент");
        chatArea = new JTextArea(20, 50);
        chatArea.setEditable(false);
        messageField = new JTextField(50);

        JButton sendButton = new JButton("Отправить");
        sendButton.addActionListener(e -> sendMessage());

        messageField.addActionListener(e -> sendMessage());

        JPanel panel = new JPanel();
        panel.add(new JScrollPane(chatArea));
        panel.add(messageField);
        panel.add(sendButton);

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(new IncomingMessagesHandler()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            out.println(message);
            messageField.setText("");
        }
    }

    private class IncomingMessagesHandler implements Runnable {
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
