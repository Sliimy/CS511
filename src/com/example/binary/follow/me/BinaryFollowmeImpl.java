package com.example.binary.follow.me;

import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.omg.PortableInterceptor.LOCATION_FORWARD;

public class BinaryFollowmeImpl implements DeviceListener{

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
	public synchronized void bindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		   System.out.println("bind presence sensor "+ presenceSensor.getSerialNumber());
		   presenceSensor.addListener(this);
	}

	/** Unbind Method for presenceSensors dependency */
	public synchronized void unbindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		   System.out.println("Unbind presence sensor "+ presenceSensor.getSerialNumber());
		   presenceSensor.removeListener(this);
	}

	/** Component Lifecycle Method */
	public void stop() {
		   System.out.println("Component is stopping...");
		   for(PresenceSensor sensor :presenceSensors){
			   sensor.removeListener(this);
		   }
	}

	/** Component Lifecycle Method */
	public void start() {
		   System.out.println("Component is starting...");
	}

	public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue,Object newValue) {
		//we assume that we listen only to presence sensor
		assert device instanceof PresenceSensor : "Device must be a presence sensor only";
		PresenceSensor changingSensor = (PresenceSensor) device;
		if (propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {
			String detectorLocation =(String) changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
			System.out.println("The device with the serial number : "+changingSensor.getSerialNumber()+" has changed");
			System.out.println("This sensor is in the room:"+detectorLocation);
			List<BinaryLight> binaryLightsLocation = getBinaryLightFromlocation(detectorLocation);
			for (BinaryLight binaryLight : binaryLightsLocation) {
				if(changingSensor.getSensedPresence()) {
					binaryLight.setPowerStatus(true);
				}else {
					binaryLight.setPowerStatus(false);
				}
			}
		}
	}
	/**
	 * The name of the LOCATION property
	 */
	public static final String LOCATION_PROPERTY_NAME = "Location";
	 
	/**
	 * The name of the location for unknown value
	 */
	public static final String LOCATION_UNKNOWN = "unknown";

	@Override
	public void deviceAdded(GenericDevice arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deviceEvent(GenericDevice arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void devicePropertyAdded(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deviceRemoved(GenericDevice arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private synchronized List<BinaryLight> getBinaryLightFromlocation(String location){
		List<BinaryLight> binaryLightsLocation = new ArrayList<BinaryLight>();
		for (BinaryLight binaryLight : binaryLights) {
			if (binaryLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				binaryLightsLocation.add(binaryLight);
			}
		}
		return binaryLightsLocation;
	}
}
