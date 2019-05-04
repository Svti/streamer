package com.streamer.worker.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class WebUtils {

	public static InetAddress bind() throws IOException {
		InetAddress local = null;
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			Enumeration<InetAddress> addresss = interfaces.nextElement().getInetAddresses();
			while (addresss.hasMoreElements()) {
				InetAddress address = addresss.nextElement();
				if (address.isSiteLocalAddress()) {
					return address;
				} else {
					continue;
				}
			}
		}
		return local;
	}
}
