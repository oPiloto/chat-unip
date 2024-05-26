package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class ClientHandler implements Runnable {
    public ClientHandler(Socket socket, ArrayList<ClientHandler> clientHandlers) {
        try {
            this.socket = socket;
            this.chatColor = getRandomColor();
            this.clientHandlers = clientHandlers;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.userName = bufferedReader.readLine();

            synchronized (clientHandlers) {
                clientHandlers.add(this);
            }

            broadcastMessage(Colored.RESET + "[SERVER]: " + chatColor + userName + Colored.RESET + " entrou.");
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    private String getRandomColor() {
        String[] colors = {
                Colored.RED, Colored.YELLOW, Colored.BLUE,
                Colored.PURPLE, Colored.CYAN
        };
        Random random = new Random();
        return colors[random.nextInt(colors.length)];
    }

    public void broadcastMessage(String message) {
        synchronized (clientHandlers) {
            for (ClientHandler client : clientHandlers) {
                try {
                    if (!client.userName.equals(userName)) {
                        client.bufferedWriter.write(message);
                        client.bufferedWriter.newLine();
                        client.bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    closeAll(client.socket, client.bufferedReader, client.bufferedWriter);
                }
            }
        }
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClient();
        try {
            if (bufferedReader != null)
                bufferedReader.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClient() {
        broadcastMessage(Colored.RESET + "[SERVER]: " + chatColor + userName + Colored.RESET + " saiu.");
        synchronized (clientHandlers) {
            clientHandlers.remove(this);
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                String message = bufferedReader.readLine();
                if (message != null) {
                    broadcastMessage(chatColor + userName + Colored.RESET + ": " + message);
                } else {
                    closeAll(socket, bufferedReader, bufferedWriter);
                    break;
                }
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    // Members
    private ArrayList<ClientHandler> clientHandlers;
    private String chatColor;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;
}