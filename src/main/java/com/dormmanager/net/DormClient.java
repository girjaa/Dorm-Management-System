package com.dormmanager.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Client for communicating with the DormServer over a TCP Socket.
 * Fulfills Requirement 4 (Sockets between the client side and the server side).
 */
public class DormClient implements AutoCloseable {

    private final String host;
    private final int port;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public DormClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        this.socket = new Socket(host, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public String sendCommand(String command) throws IOException {
        if (socket == null || socket.isClosed()) {
            throw new IOException("Socket not connected");
        }
        out.println(command);
        return in.readLine();
    }

    public String ping() throws IOException {
        return sendCommand("PING");
    }

    public String submitComplaint(String roomId, String category, String priority, String description) throws IOException {
        String cmd = String.format("SUBMIT_COMPLAINT|%s|%s|%s|%s", roomId, category, priority, description);
        return sendCommand(cmd);
    }

    public int getComplaintCount() throws IOException {
        String resp = sendCommand("COUNT_COMPLAINTS");
        if (resp != null && resp.startsWith("COUNT:")) {
            return Integer.parseInt(resp.substring(6).trim());
        }
        return -1;
    }

    public String listRooms() throws IOException {
        return sendCommand("LIST_ROOMS");
    }

    @Override
    public void close() throws IOException {
        if (out != null) {
            try {
                out.println("QUIT");
            } catch (Exception ignored) {}
        }
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null && !socket.isClosed()) socket.close();
    }
}
