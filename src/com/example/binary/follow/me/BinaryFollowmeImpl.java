package com.example.binary.follow.me;

import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import java.util.Map;

public class BinaryFollowmeImpl {

	/** Field for presenceSensors dependency */
	private PresenceSensor[] presenceSensors;
	/** Field for binaryLights dependency */
	private BinaryLight[] binaryLights;

	/** Bind Method for binaryLights dependency */
	public void bindBinaryLight(BinaryLight binaryLight, Map properties) {
		  System.out.println("bind binary light " + binaryLight.getSerialNumber());
	}

	/** Unbind Method for binaryLights dependency */
	public void unbindBinaryLight(BinaryLight binaryLight, Map properties) {
		  System.out.println("unbind binary light " + binaryLight.getSerialNumber());
	}

	/** Bind Method for presenceSensors dependency */
	public void bindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		   System.out.println("bind presence sensor "+ presenceSensor.getSerialNumber());
	}

	/** Unbind Method for presenceSensors dependency */
	public void unbindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		   System.out.println("Unbind presence sensor "+ presenceSensor.getSerialNumber());
	}

	/** Component Lifecycle Method */
	public void stop() {
		   System.out.println("Component is stopping...");
	}

	/** Component Lifecycle Method */
	public void start() {
		   System.out.println("Component is starting...");
	}

}
