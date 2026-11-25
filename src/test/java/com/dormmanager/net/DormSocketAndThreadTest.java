package com.dormmanager.net;

import com.dormmanager.service.ComplaintService;
import com.dormmanager.service.FileStorageService;
import com.dormmanager.service.RoomService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class DormSocketAndThreadTest {

    @TempDir
    Path tempDir;

    private DormServer server;
    private RoomService roomService;
    private ComplaintService complaintService;
    private int port;

    @BeforeEach
    void setUp() throws IOException {
        FileStorageService storage = new FileStorageService(tempDir.toString());
        roomService = new RoomService(storage);
        complaintService = new ComplaintService(storage);

        roomService.addRoom(101, 1, "North-Wing", 2);
        roomService.addRoom(102, 1, "North-Wing", 2);

        port = 18888;
        server = new DormServer(port, complaintService, roomService, 5);
        server.start();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void testSingleClientSocketCommunication() throws IOException {
        try (DormClient client = new DormClient("localhost", port)) {
            client.connect();

            // Test Ping
            String pong = client.ping();
            assertEquals("PONG", pong);

            // Test listing rooms
            String rooms = client.listRooms();
            assertTrue(rooms.contains("North-Wing-1-101"));

            // Test submit anonymous complaint over socket
            String response = client.submitComplaint(
                    "North-Wing-1-101",
                    "PLUMBING",
                    "HIGH",
                    "Bathroom faucet continuously leaking cold water"
            );
            assertTrue(response.startsWith("OK|"));

            // Verify count
            assertEquals(1, client.getComplaintCount());
            assertEquals(1, complaintService.getAllComplaints().size());
        }
    }

    @Test
    void testConcurrentClientsServedByThreads() throws Exception {
        int clientCount = 4;
        ExecutorService executor = Executors.newFixedThreadPool(clientCount);
        CountDownLatch latch = new CountDownLatch(clientCount);
        List<Future<Boolean>> results = new ArrayList<>();

        for (int i = 0; i < clientCount; i++) {
            final int id = i;
            results.add(executor.submit(() -> {
                try (DormClient client = new DormClient("localhost", port)) {
                    client.connect();
                    latch.countDown();
                    latch.await(5, TimeUnit.SECONDS);

                    String res = client.submitComplaint(
                            "North-Wing-1-101",
                            "ELECTRICAL",
                            "NORMAL",
                            "Concurrent client issue report number " + id
                    );
                    return res != null && res.startsWith("OK|");
                }
            }));
        }

        for (Future<Boolean> fut : results) {
            assertTrue(fut.get(10, TimeUnit.SECONDS));
        }

        executor.shutdown();
        assertEquals(clientCount, complaintService.getAllComplaints().size());
    }
}
