package client;

import java.io.IOException;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
            new ChatWindow(socket);
        } catch (IOException e) {
            System.out.println(
                    Colored.RED + "[ERROR]" + Colored.RESET
                            + ": Não foi possível conectar-se ao servidor. Verifique se o servidor está online e tente novamente.");
        }
    }
}
