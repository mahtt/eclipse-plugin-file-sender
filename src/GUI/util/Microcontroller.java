package GUI.util;

import java.io.Serializable;

/**
 * Representaion of microcontroller profile
 */
public class Microcontroller implements Serializable {
	/**
	 * uniquely identifies class during deserialization process
	 */
	private static final long serialVersionUID = -1808295147480842755L;
	/**
	 * name associated with the microcontroller
	 */
	private String name;

	/**
	 * ip address associated with the microcontroller
	 */
	private String ip;

	/**
	 * port number associated with the microcontroller
	 */
	private String port;

	/**
	 * Constructor
	 * 
	 * @param name name associated with the microcontroller
	 * @param ip   ip address associated with the microcontroller
	 * @param port number associated with the microcontroller
	 */
	public Microcontroller(String name, String ip, String port) {

		this.name = name;
		this.ip = ip;
		this.port = port;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public String toString() {
		return name + "(" + ip + ":" + port + ")";
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

}
