package com.dormmanager.net;

import com.dormmanager.model.Complaint;
import com.dormmanager.model.ComplaintCategory;
import com.dormmanager.model.Priority;
import com.dormmanager.model.Room;
import com.dormmanager.service.ComplaintService;
import com.dormmanager.service.RoomService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Multi-threaded TCP Socket Server for the Dormitory Management System.
 * Fulfills Requirement 3 (Threads to serve several clients) and Requirement 4 (Sockets between client and server).
 */
public class DormServer implements Runnable {

    private final int port;
    private final ComplaintService complaintService;
    private final RoomService roomService;
    private final ExecutorService clientThreadPool;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private ServerSocket serverSocket;

    public DormServer(int port, ComplaintService complaintService, RoomService roomService, int poolSize) {
        this.port = port;
        this.complaintService = complaintService;
        this.roomService = roomService;
        this.clientThreadPool = Executors.newFixedThreadPool(poolSize);
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running.set(true);
        Thread serverThread = new Thread(this, "DormServer-Listener");
        serverThread.setDaemon(true);
        serverThread.start();
        System.out.println("✅ Multi-threaded Dorm Server started on port " + port);
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                Socket clientSocket = serverSocket.accept();
                // Requirement 3: dispatch each incoming client socket to a thread from the thread pool
                clientThreadPool.submit(new ClientHandler(clientSocket, complaintService, roomService));
            } catch (IOException e) {
                if (!running.get()) {
                    break; // expected on stop
                }
                System.err.println("Server accept error: " + e.getMessage());
            }
        }
    }

    public void stop() {
        running.set(false);
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ignored) {}

        clientThreadPool.shutdown();
        try {
            if (!clientThreadPool.awaitTermination(2, TimeUnit.SECONDS)) {
                clientThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            clientThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("🛑 Dorm Server stopped.");
    }

    public boolean isRunning() {
        return running.get();
    }

    public int getPort() {
        return port;
    }

    /**
     * Handles an individual client connection on its own thread.
     */
    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private final ComplaintService complaintService;
        private final RoomService roomService;

        public ClientHandler(Socket socket, ComplaintService complaintService, RoomService roomService) {
            this.socket = socket;
            this.complaintService = complaintService;
            this.roomService = roomService;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String requestLine;
                while ((requestLine = in.readLine()) != null) {
                    if ("QUIT".equalsIgnoreCase(requestLine.trim())) {
                        out.println("BYE");
                        break;
                    }
                    String response = processCommand(requestLine);
                    out.println(response);
                }
            } catch (IOException e) {
                // Client disconnected
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }

        /**
         * Protocol commands:
         * PING -> PONG
         * LIST_ROOMS -> room1;room2;...
         * SUBMIT_COMPLAINT|roomId|category|priority|description -> OK|complaintId
         * COUNT_COMPLAINTS -> <number>
         */
        private String processCommand(String command) {
            if (command == null || command.isBlank()) {
                return "ERROR: Empty command";
            }

            String[] parts = command.split("\\|", -1);
            String action = parts[0].trim().toUpperCase();

            switch (action) {
                case "PING":
                    return "PONG";

                case "LIST_ROOMS":
                    StringBuilder sb = new StringBuilder();
                    for (Room r : roomService.getAllRooms()) {
                        if (!sb.isEmpty()) sb.append(";");
                        sb.append(r.getId());
                    }
                    return "ROOMS:" + sb;

                case "COUNT_COMPLAINTS":
                    return "COUNT:" + complaintService.getAllComplaints().size();

                case "SUBMIT_COMPLAINT":
                    if (parts.length < 5) {
                        return "ERROR: Expected SUBMIT_COMPLAINT|roomId|category|priority|description";
                    }
                    String roomId = parts[1].trim();
                    String categoryStr = parts[2].trim();
                    String priorityStr = parts[3].trim();
                    String description = parts[4].trim();

                    Room room = roomService.findRoomById(roomId);
                    if (room == null) {
                        return "ERROR: Room not found: " + roomId;
                    }
                    try {
                        ComplaintCategory category = ComplaintCategory.fromString(categoryStr);
                        Priority priority = Priority.valueOf(priorityStr.toUpperCase());
                        Complaint complaint = complaintService.submitAnonymousComplaint(description, category, priority, room);
                        return "OK|" + complaint.getComplaintId();
                    } catch (Exception e) {
                        return "ERROR: " + e.getMessage();
                    }

                default:
                    return "ERROR: Unknown command: " + action;
            }
        }
    }
}
