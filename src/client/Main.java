package client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nome para o chat: ");
        String userName = scanner.nextLine();

        try {
            Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
            Client client = new Client(socket, userName);
            client.receiveMessage();
            client.sendMessage();
        } catch (IOException e) {
            System.out.println(
                    Colored.RED + "[ERROR]" + Colored.RESET
                            + ": Não foi possível conectar-se ao servidor. Verifique se o servidor está online e tente novamente.");
        }
    }
}
