package utils;

import com.mrsmyx.network.CCNetwork;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by cj on 1/16/16.
 */
public class Network {
    public static InetAddress getWLANipAddress(String protocolVersion) throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
            if (netint.isUp() && !netint.isLoopback() && !netint.isVirtual()) {
                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    if (protocolVersion.equals("IPv4")) {
                        if (inetAddress instanceof Inet4Address) {
                            return inetAddress;
                        }
                    } else {
                        if (inetAddress instanceof Inet6Address) {
                            return inetAddress;
                        }
                    }
                }
            }
        }
        return null;
    }

    public interface NetworkListener {
        void OnNetworkFound(InetAddress byName);
        void OnNetworkFail(String response);
    }

    public static void printReachableHosts(InetAddress inetAddress, Network.NetworkListener networkListener) throws SocketException {
        String ipAddress = inetAddress.toString();
        ipAddress = ipAddress.substring(1, ipAddress.lastIndexOf('.')) + ".";
        int found = 0;
        for (int i = 0; i < 256; i++) {
            String otherAddress = ipAddress + String.valueOf(i);
            try {
                if (InetAddress.getByName(otherAddress.toString()).isReachable(50)) {
                    System.out.println(otherAddress);
                    try {
                        if (exists("http://" + InetAddress.getByName(otherAddress.toString()).getHostName() + ":6333/ccapi/getfirmwareinfo")) {
                            networkListener.OnNetworkFound(InetAddress.getByName(otherAddress.toString()));
                            found++;
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(found == 0) networkListener.OnNetworkFail("Could not locate your PS3. Please make sure you have CCAPI version 2.70+");
    }

    public static boolean exists(String URLName) throws IOException {
        CCNetwork ccNetwork = new CCNetwork();
        List<String> list = ccNetwork.getListRequest(URLName);
        if(list.size() > 0){
            String s = list.get(2);
            int x = Integer.parseInt(s,16);
            if(x >= 270) return true;
            else return false;
        }
        return false;
    }
}
