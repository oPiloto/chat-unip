package client;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ChatWindow extends Window {
    private JTextPane textPane;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName = "";
    private JTextField southPanelTextField;
    private JButton northPanelClearChatButton;

    public ChatWindow(int x, int y, Socket socket) {
        super("Chat", x, y, 400, 400, WindowConstants.EXIT_ON_CLOSE);

        this.setLayout(new BorderLayout(2, 2));
        this.setSize(400, 400);
        this.setResizable(false);

        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public ChatWindow(Socket socket) {
        this(-1, -1, socket);
    }

    @Override
    protected void setupComponents() {
        JPanel southPanel = new JPanel();
        JButton southPanelButton = new JButton("Enviar");
        this.southPanelTextField = new JTextField(27);
        southPanelTextField.addActionListener(this::onSendMessage);
        southPanelButton.addActionListener(this::onSendMessage);
        southPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
        southPanel.add(this.southPanelTextField);
        southPanel.add(southPanelButton);
        this.add(southPanel, BorderLayout.SOUTH);

        this.textPane = new JTextPane();
        this.textPane.setEnabled(false);
        this.textPane.setText("Insira o seu nome do campo de texto abaixo e clique em enviar!");
        JScrollPane scrollPane = new JScrollPane(textPane);
        this.add(scrollPane, BorderLayout.CENTER);

        JPanel northPanel = new JPanel();
        this.northPanelClearChatButton = new JButton("Limpar");
        this.northPanelClearChatButton.setEnabled(false);
        this.northPanelClearChatButton.addActionListener(this::clearChat);
        northPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
        northPanel.add(northPanelClearChatButton);
        this.add(northPanel, BorderLayout.NORTH);
    }

    private void onSendMessage(ActionEvent e) {
        this.sendMessage(this.southPanelTextField.getText());
        this.southPanelTextField.setText("");
    }

    private void clearChat(ActionEvent e) {
        this.textPane.setText("");
    }

    public void sendMessage(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            if (userName.isEmpty()) {
                this.userName = message;
                this.clearChat(null);
                this.receiveMessage();
                this.northPanelClearChatButton.setEnabled(true);
            } else {
                this.textPane.setText(this.textPane.getText() + "\n" + "Você: " + message);
            }
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void receiveMessage() {
        new Thread(() -> {
            String message;

            while (socket.isConnected()) {
                try {
                    message = bufferedReader.readLine();
                    if (message != null) {
                        System.out.println(message);
                        this.textPane.setText(this.textPane.getText() + "\n" + message);

                    } else {
                        System.out.println(Colored.RED + "[ERROR]" + Colored.RESET + ": Server ficou offline.");
                        System.out.println("Fechando sockets...");
                        closeAll(socket, bufferedReader, bufferedWriter);
                        break;
                    }
                } catch (IOException e) {
                    closeAll(socket, bufferedReader, bufferedWriter);
                    break;
                }
            }
        }).start();
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null)
                bufferedReader.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
            if (socket != null)
                socket.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
