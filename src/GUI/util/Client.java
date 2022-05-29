package GUI.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.HashMap;

import GUI.UI.Gui;

/**
 * The client part of the client-server architecture
 *
 */
public class Client {

	private Gui gui;
	private McProfileStorage mcs;

	/**
	 * Constructor
	 */
	public Client() {
		gui = new Gui();
		mcs = new McProfileStorage();
	}

	/**
	 * 
	 * @param request
	 */
	public void sendRequest(int request) {
		// check if storage is set up correctly
		HashMap<String, Microcontroller> profiles = mcs.getMc();
		if (!profiles.isEmpty()) {
			if (mcs.isProfileSelected()) {
				Microcontroller m = mcs.getMc().get(mcs.getSelectedMcProfile());
				if (!mcs.isFilepathSet()) {
					gui.displayMessage("Please specify a file and try again");
					gui.launch();
				} else {
					int sendStatus = this.send(m.getIp(), m.getPort(), request);
					if (sendStatus == 1) {
						// sending was successful
					} else {
						String errorMessage;
						switch (sendStatus) {
						case GlobalsDefs.CONNECTION_EXCEPTION_OCCURED:
							errorMessage = "Connection refused. \n Make sure, the provided ip and port numbers are correct and the server is running.";
							break;
						case GlobalsDefs.IO_EXCEPTION_OCCURED:
							errorMessage = "An IOException occurred.";
							break;
						default:
							errorMessage = "An error occurred.";
						}
						gui.displayMessage(errorMessage);
					}
				}
			} else {
				gui.displayMessage(GlobalsDefs.NO_PROFILE_ADDED_OR_SELECTED);
				gui.launch();
			}
		} else {
			gui.displayMessage((GlobalsDefs.NO_PROFILE_ADDED_OR_SELECTED));
			gui.launch();
		}

	}

	/**
	 * Creates the command-header, which encodes the request of the client
	 * 
	 * @param cmd request encoded as integer
	 * @return the created command header
	 */
	private String addCmdToHeader(int cmd) {
		return "<cmd>" + cmd + "<\\cmd>";
	}

	/**
	 * sends a file to the server
	 * 
	 * @param os       the output stream
	 * @param filepath path of file which should be send
	 */
	private void sendFile(OutputStream os, String filepath) {
		try {
			FileInputStream fs = new FileInputStream(filepath);
			BufferedInputStream bis = new BufferedInputStream(fs);
			byte[] bytes = new byte[8192];
			int count;
			while ((count = bis.read(bytes)) > 0) {
				os.write(bytes, 0, count);
			}
			bis.close();
		} catch (ConnectException ce) {
			gui.displayMessage("A ConnectException occured");
		} catch (IOException e) {
			gui.displayMessage("An IOException occurred.");
		}

	}

	/**
	 * interprets the server response and displays a message according to it
	 * 
	 * @param server_response the response of the server encoded as an integer
	 */
	private void interpret_server_response(int server_response) {
		String msg = "";
		switch (server_response) {
		case GlobalsDefs.SERVER_RESPONDS_OK: // if client request was granted, no message needed.
			break;

		// interpreter states
		case GlobalsDefs.I_READY:
			msg = "Interpreter state is 'READY'";
			break;
		case GlobalsDefs.I_WAITING:
			msg = "Interpreter state is 'WAITING'";
			break;
		case GlobalsDefs.I_RUNNING:
			msg = "Interpreter state is 'RUNNING'";
			break;
		case GlobalsDefs.I_INTERRUPTED:
			msg = "Interpreter state is 'INTERRUPTED'";
			break;
		case GlobalsDefs.I_EXECUTION_FINISHED:
			msg = "Interpreter state is 'EXECUTION FINISHED'";
			break;
		case GlobalsDefs.I_ERROR:
			msg = "Interpreter state is 'ERROR'";
			break;
			
		// mikrocontroller responds error code
		case GlobalsDefs.REQUEST_DENIED:
			msg = "The interpreter is not in the right state to fullfill the request.";
			break;
		case GlobalsDefs.FILE_DEPLOYMENT_NOT_POSSIBLE:
			msg = "Error: File deployment not possible. Check interpreter state.";
			break;
		case GlobalsDefs.RUN_NOT_POSSIBLE:
			msg = "Error: Running interpreter not possible.";
			break;
		case GlobalsDefs.NO_COMMAND_CODE_WAS_SEND:
			msg = "Error: No command code representing the request was send";
			break;
		case GlobalsDefs.NO_COMMAND_HEADER_WAS_SEND:
			msg = "Error: No command header was send.";
			break;
		case GlobalsDefs.COMMAND_CODE_COULD_NOT_BE_INTERPRETED:
			msg = "Error: Command code representing the request could not be interpreted";
			break;
		default:
			msg = "An error occured.";
		}
		if (!msg.isEmpty()) {
			gui.displayMessage(msg);
		}
	}

	/**
	 * sends a request to the server
	 * 
	 * @param ip   ip of the server
	 * @param port port of the server
	 * @return 1 -> file was successful send -1 -> connection refused -2 ->
	 *         IOException occurred
	 */
	private int send(String ip, String port, int client_request) {
		int res = 1;
		try {
			Socket client_socket = new Socket(ip, Integer.parseInt(port));

			DataInputStream input = new DataInputStream(client_socket.getInputStream());
			OutputStream os = client_socket.getOutputStream();

			String header = "";
			int server_response = GlobalsDefs.UNSPECIFIED_ERROR;
			boolean deploy_file = (client_request == GlobalsDefs.DEPLOY_FILE)
					|| (client_request == GlobalsDefs.DEPLOY_AND_RUN);
			if (deploy_file) {
				header = addCmdToHeader(client_request);
				os.write(header.getBytes(), 0, header.length());

				server_response = input.readInt();
				if (server_response == GlobalsDefs.SERVER_RESPONDS_OK) {
					sendFile(os, mcs.getFilePath());
				} else {
					gui.displayMessage("An error occurred reading the saved parameters."); // serialization file is null
				}
			} else {
				header = addCmdToHeader(client_request);
				os.write(header.getBytes(), 0, header.length());
				server_response = input.readInt();
			}
			client_socket.close();
			interpret_server_response(server_response);
		} catch (ConnectException ce) {
			res = GlobalsDefs.CONNECTION_EXCEPTION_OCCURED;
		} catch (IOException e) {
			res = GlobalsDefs.IO_EXCEPTION_OCCURED;
		}
		return res;
	}
}
