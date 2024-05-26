package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {
    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        int port = 1337;

        try {
            if (args.length > 0)
                port = Integer.parseInt(args[0]);

            if (port < 1024 || port > 65535)
                throw new IllegalArgumentException("Porta deve ser um int entre 1024 e 65535.");

            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println(Colored.GREEN + "[RUNNING]" + Colored.RESET + ": Server iniciado na porta: " + port);

                // Loop para aceitar conexões
                while (!serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();
                    System.out.println(Colored.BLUE + "[LOG]" + Colored.RESET + ": Novo cliente conectado: "
                            + socket.getRemoteSocketAddress().toString());
                    ClientHandler client = new ClientHandler(socket, clientHandlers);

                    Thread thread = new Thread(client);
                    thread.start();
                }
            } catch (IOException e) {
                System.out.println(Colored.RED + "[ERROR]" + Colored.RESET
                        + ": Erro ao iniciar o servidor ou aceitar conexões: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            System.out.println(Colored.RED + "[ERROR]" + Colored.RESET
                    + ": Argumento de porta inválido. Deve ser um número inteiro.");
        } catch (IllegalArgumentException e) {
            System.out.println(Colored.RED + "[ERROR]" + Colored.RESET + ": " + e.getMessage());
        } catch (Exception e) {
            System.out.println(Colored.RED + "[ERROR]" + Colored.RESET + ": Erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
