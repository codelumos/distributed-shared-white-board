package org.unimelb.whiteboard.server.CentralServer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.unimelb.whiteboard.client.StateCode.StateCode;
import org.unimelb.whiteboard.server.Room.Room;
import org.unimelb.whiteboard.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Map;

/**
 * Thread per request structure.
 */
public class RequestHandler extends Thread {
    private final Socket clientSocket;
    Server controller;

    public RequestHandler(Socket clientSocket, Server controller) {
        this.clientSocket = clientSocket;
        this.controller = controller;
    }

    public void run() {
        try {
            DataInputStream reader = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream writer = new DataOutputStream(clientSocket.getOutputStream());
            JSONObject reqJSON = JSON.parseObject(reader.readUTF());
            int command = Integer.parseInt(reqJSON.get("command").toString());
            JSONObject resJSON = new JSONObject();
            // Execute command
            String password = "";
            int roomId = -1;
            String userId = "";

            switch (command) {
                case StateCode.ADD_ROOM:
                    // get info from request
                    String roomName = reqJSON.getString("roomName");
                    password = reqJSON.getString("password");
                    String hostName = reqJSON.getString("hostName");
                    String ipAddress = reqJSON.getString("hostIp");
                    int port = reqJSON.getInteger("hostPort");
                    // return roomId
                    roomId = controller.getRoomManager().addRoom(ipAddress, port, hostName, roomName, password);
                    controller.printOnBoth(
                            hostName + " create a room! Current room num: " + controller.getRoomManager().getRoomNum());
                    controller.printOnBoth("- Host: " + ipAddress + ": " + port);
                    resJSON.put("state", StateCode.SUCCESS);
                    resJSON.put("roomId", roomId);
                    break;
                case StateCode.REMOVE_ROOM:
                    roomId = Integer.parseInt(reqJSON.get("roomId").toString());
                    int state = controller.getRoomManager().removeRoom(roomId);
                    resJSON.put("state", String.valueOf(state));
                    controller.printOnBoth("Room: " + roomId + " is removed.");
                    break;
                case StateCode.GET_ROOM_LIST:
                    Map<Integer, String> roomList = controller.getRoomManager().getRoomList();
                    resJSON.put("state", StateCode.SUCCESS);
                    resJSON.put("roomList", roomList);
                    System.out.println("A client request for roomlist info.");
                    break;
                case StateCode.GET_ROOM_INFO:
                    roomId = reqJSON.getInteger("roomId");
                    password = reqJSON.getString("password");
                    if (controller.getRoomManager().checkRoomPassword(roomId, password)) {
                        System.out.println("Password Correct!");
                        Room room = controller.getRoomManager().getRoomInfo(roomId);
                        resJSON.put("hostId", room.getHostName());
                        resJSON.put("state", StateCode.SUCCESS);
                        resJSON.put("ip", room.getIpAddress());
                        resJSON.put("port", room.getPort());
                        controller.printOnBoth("A client asks room " + roomId + " with correct password.");
                    } else {
                        resJSON.put("state", StateCode.FAIL);
                        controller.printOnBoth("A client asks room " + roomId + " with worng password.");
                    }
                    break;
                case StateCode.ADD_USER:
                    userId = reqJSON.get("userId").toString();
                    if (controller.getUserlist().containsKey(userId)) {
                        controller.printOnBoth("A user try to join but " + userId + " exist!");
                        resJSON.put("state", StateCode.FAIL);
                    } else {
                        controller.getUserlist().put(userId, userId);
                        controller.printOnBoth(
                                "User-" + userId + " join. " + "Current user number: " + controller.getUserlist().size());
                        resJSON.put("state", StateCode.SUCCESS);
                    }
                    break;
                case StateCode.REMOVE_USER:
                    userId = reqJSON.get("userId").toString();
                    if (controller.getUserlist().containsKey(userId)) {
                        controller.getUserlist().remove(userId);
                        controller.printOnBoth(
                                "Delete " + userId + ". " + "Current user number: " + controller.getUserlist().size());
                        controller.getRoomManager().removeRoom(userId);
                        controller.printOnBoth("Delete " + userId + "'s room. " + "Current user number: "
                                + controller.getRoomManager().getRoomNum());
                        resJSON.put("state", StateCode.SUCCESS);
                    } else {
                        controller.printOnBoth(userId + " not exist! Can't be delete.");
                        resJSON.put("state", StateCode.FAIL);
                    }
                    break;
                default:
                    System.out.print("Err: Unknown Command: " + command);
                    break;
            }
            // Send back to client
            writer.writeUTF(resJSON.toJSONString());
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
