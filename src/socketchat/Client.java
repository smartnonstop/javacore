package socketchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	
	private static final String SERVER_ADDRESS = "127.0.0.1";
	private static final int PORT = 8181;

	private BufferedReader in;
	private PrintWriter out;
	private Scanner sc;

	private void run() throws IOException {

		Socket socket = new Socket(SERVER_ADDRESS, PORT);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		sc = new Scanner(System.in);

		Thread chatReader = new ChatReader(in);
		chatReader.start();

		while (true) {
			String input = sc.nextLine();
			if (null != input) {
				if (input.trim().contains(":q!")) {
					chatReader.interrupt();
					socket.close();
					System.exit(0);
				}
				out.println(input);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Client client = new Client();
		client.run();
	}

	private static class ChatReader extends Thread {

		private BufferedReader in;

		public ChatReader(BufferedReader in) {
			this.in = in;
		}

		public void run() {
			while (true) {
				try {
					String input = in.readLine();
					if (null != input)
						System.out.println(input);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	

}
