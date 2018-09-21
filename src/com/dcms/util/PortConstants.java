package com.dcms.util;

public class PortConstants {
   
   public static final int MTL_REGISTRY_PORT = 1234;
   public static final int LVL_REGISTRY_PORT = 1235;
   public static final int DDO_REGISTRY_PORT = 1236;
   
   public static final int MTL_UDP_PORT = 9090;
   public static final int LVL_UDP_PORT = 9091;
   public static final int DDO_UDP_PORT = 9092;
   
   public static int getUdpPort(final String serverLocation) {
	   if(LocationConstants.MONTREAL.equalsIgnoreCase(serverLocation)) {
		   return MTL_UDP_PORT;
	   } else if(LocationConstants.LAVAL.equalsIgnoreCase(serverLocation)) {
		   return LVL_UDP_PORT;
	   } else if(LocationConstants.DOLLARD.equalsIgnoreCase(serverLocation)) {
		   return DDO_UDP_PORT;
	   } 
	   return 0;
   }
   
}