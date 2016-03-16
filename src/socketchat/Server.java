package socketchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class Server {

	private static final int PORT = 8181;
	private static HashSet<String> names = new HashSet<String>();
	private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

	public static void main(String[] args) throws Exception {
		System.out.println("The chat server is running.");

		try (ServerSocket listener = new ServerSocket(PORT);) {
			while (true) {
				new Handler(listener.accept()).start();
			}
		}
	}

	private static class Handler extends Thread {
		private String name;
		private Socket sock;
		private BufferedReader in;
		private PrintWriter out;

		public Handler(Socket socket) {
			this.sock = socket;
		}

		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(
						sock.getInputStream()));
				out = new PrintWriter(sock.getOutputStream(), true);

				while (true) {
					out.println("Mr. Server: accept your name");
					name = in.readLine();
					if (name == null) {
						return;
					}
					synchronized (names) {
						if (!names.contains(name)) {
							names.add(name);
							break;
						} else
							out.println("Mr. Server: this name allready exists");
					}
				}

				out.println("Mr. Server: name accepted!\nyou're welcome!\n---------");
				writers.add(out);

				while (true) {
					String input = in.readLine();
					if (input == null){						
						for (PrintWriter writer : writers)
							writer.println(name + " exited chat");
						return;
					}

					for (PrintWriter writer : writers)
						writer.println(name + ": " + input);

				}
			} catch (IOException e) {
				System.out.println(e);
			} finally {
				if (name != null)
					names.remove(name);

				if (out != null)
					writers.remove(out);

				try {
					sock.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	
}
