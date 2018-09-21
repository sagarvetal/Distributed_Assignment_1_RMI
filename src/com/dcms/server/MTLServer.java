package com.dcms.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.dcms.service.ActivityLoggerService;
import com.dcms.util.ActionConstants;
import com.dcms.util.FieldConstants;
import com.dcms.util.FileConstants;
import com.dcms.util.LocationConstants;
import com.dcms.util.MessageTypeConstants;
import com.dcms.util.PortConstants;
import com.dcms.util.UdpServerMessages;

/**
 * MTL Server Class
 * Create server instance for MTL location
 */

public class MTLServer {
   
    public static void main(String[] args) throws Exception {
        final CenterServerImpl mtlServer = new CenterServerImpl(LocationConstants.MONTREAL);
        final Registry registry = LocateRegistry.createRegistry(PortConstants.MTL_REGISTRY_PORT);
        registry.bind(LocationConstants.MONTREAL, mtlServer);
        System.out.println("#================= Montreal Server is started =================#");
        
        final ActivityLoggerService activityLogger = new ActivityLoggerService(FileConstants.SERVER_LOG_FILE_PATH + LocationConstants.MONTREAL + "/" + FileConstants.ACTIVITY_LOG);
        /*Start UDP server for Montreal*/
        new Thread(() -> {
        	startUdpServer(activityLogger, mtlServer);
        }).start();
    }
 
    private static void startUdpServer(final ActivityLoggerService activityLogger, final CenterServerImpl server) {
        DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(PortConstants.getUdpPort(server.getServerName()));
			activityLogger.log(MessageTypeConstants.INFO, String.format(UdpServerMessages.UDP_SERVER_STARTED, server.getServerName()));
			while (true) {
				try {
					final byte[] data = new byte[1000];
					final DatagramPacket packet = new DatagramPacket(data, data.length);
					socket.receive(packet);
					
					new Thread(() -> {
						processRequest(activityLogger, server, packet);
		        	}).start();
				} catch (IOException e) {
					activityLogger.log(MessageTypeConstants.ERROR, e.getMessage());
				}
			}
		} catch (SocketException e1) {
			activityLogger.log(MessageTypeConstants.ERROR, e1.getMessage());
		} finally {
            if (socket != null) {
            	socket.close();
            }
        }
    }
    
    private static void processRequest(final ActivityLoggerService activityLogger, final CenterServerImpl server, final DatagramPacket packet) {
    	byte[] response;
        DatagramSocket socket = null;
        final String request = new String(packet.getData()).trim();
        final String[] packetData = request.split(FieldConstants.FIELD_SEPARATOR_ARROW.trim());
        final String sourceServer = packetData[0].trim();
        final String action = packetData[1].trim();
        try {
            socket = new DatagramSocket();
            activityLogger.log(MessageTypeConstants.INFO, String.format(UdpServerMessages.UDP_REQUEST_RECEIVED, action, sourceServer));
            
            if(ActionConstants.GET_COUNT.equalsIgnoreCase(action)) {
            	response = server.getRecordCount().toString().getBytes();
            	socket.send(new DatagramPacket(response, response.length, packet.getAddress(), packet.getPort()));
            }
            
            activityLogger.log(MessageTypeConstants.INFO, String.format(UdpServerMessages.UDP_RESPONSE_SENT, action, sourceServer));
        } catch (IOException e) {
        	activityLogger.log(MessageTypeConstants.ERROR, e.getMessage());
        } finally {
            if (socket != null) {
            	socket.close();
            }
        }
    }
    
}