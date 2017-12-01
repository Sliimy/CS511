package com.example.binary.follow.me;

import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.omg.PortableInterceptor.LOCATION_FORWARD;
import fr.liglab.adele.icasa.device.light.DimmerLight;

public class BinaryFollowmeImpl implements DeviceListener {

	/** Field for presenceSensors dependency */
	private PresenceSensor[] presenceSensors;
	/** Field for binaryLights dependency */
	private BinaryLight[] binaryLights;
	/** Field for dimmerLights dependency */
	private DimmerLight[] dimmerLights;
	
	/**
	 * The name of the LOCATION property
	 */
	public static final String LOCATION_PROPERTY_NAME = "Location";

	/**
	 * The name of the location for unknown value
	 */
	public static final String LOCATION_UNKNOWN = "unknown";

	private int maxLightsToTurnOnPerRoom = 2;

	/** Bind Method for binaryLights dependency */
	public synchronized void bindBinaryLight(BinaryLight binaryLight, Map properties) {
		System.out.println("bind binary light " + binaryLight.getSerialNumber());
		binaryLight.addListener(this);
	}

	/** Unbind Method for binaryLights dependency */
	public synchronized void unbindBinaryLight(BinaryLight binaryLight, Map properties) {
		System.out.println("unbind binary light " + binaryLight.getSerialNumber());
		binaryLight.removeListener(this);
	}

	/** Bind Method for presenceSensors dependency */
	public synchronized void bindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		System.out.println("bind presence sensor " + presenceSensor.getSerialNumber());
		presenceSensor.addListener(this);
	}

	/** Unbind Method for presenceSensors dependency */
	public synchronized void unbindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		System.out.println("Unbind presence sensor " + presenceSensor.getSerialNumber());
		presenceSensor.removeListener(this);
	}
	
	/** Bind Method for dimmerLifhts dependency */
	public synchronized void bindDimmerLight(DimmerLight dimmerLight, Map properties) {
		System.out.println("bind dimmer light " + dimmerLight.getSerialNumber());
		dimmerLight.addListener(this);
		
	}

	/** Unbind Method for dimmerLifhts dependency */
	public synchronized void unbindDimmerLight(DimmerLight dimmerLight, Map properties) {
		System.out.println("unbind dimmer light " + dimmerLight.getSerialNumber());
		dimmerLight.removeListener(this);
	}

	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("Component is stopping...");
		for (PresenceSensor sensor : presenceSensors) {
			sensor.removeListener(this);
		}
		for (BinaryLight binaryLight : binaryLights) {
			binaryLight.removeListener(this);
		}
		for (PresenceSensor presenceSensor : presenceSensors) {
			presenceSensor.removeListener(this);
		}
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("Component is starting...");
	}

	public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {
		//we assume that we listen only to presence sensor
		assert device instanceof PresenceSensor : "Device must be a presence sensor only";
		if (device instanceof PresenceSensor) {
			PresenceSensor changingSensor = (PresenceSensor) device;
			if (propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {
				String detectorLocation = (String) changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
				System.out.println(
						"The device with the serial number : " + changingSensor.getSerialNumber() + " has changed");
				System.out.println("This sensor is in the room:" + detectorLocation);
				setStatusLightsFromPresenceSensor(changingSensor);
			}
		} else if (device instanceof BinaryLight) {
			BinaryLight changingLight = (BinaryLight) device;
			for (PresenceSensor presenceSensor : presenceSensors) {
				setStatusLightsFromPresenceSensor(presenceSensor);
//				La méthode commentait ne prend en compte que la lumiere changée le problème était que si dans l'ancienne piexe une lumière autre que celle-ci était éteint car
//				le nombre maximal de lumiere était allumé et que celle-ci était allumé, alors aucune lumière ne s'allumait
//				if (presenceSensor.getPropertyValue(LOCATION_PROPERTY_NAME).equals(changingLight.getPropertyValue(LOCATION_PROPERTY_NAME))) {
//					if (presenceSensor.getSensedPresence()) {
//						//check number of light ON in the room
//						if (getNumberBinaryLightONFromLocation((String) changingLight
//								.getPropertyValue(LOCATION_PROPERTY_NAME)) < maxLightsToTurnOnPerRoom) {
//							changingLight.setPowerStatus(true);
//						}
//					} else {
//						changingLight.setPowerStatus(false);
//					}
//				}
			}
		} else if (device instanceof DimmerLight) {
			DimmerLight changingDL=(DimmerLight) device;
			for (PresenceSensor presenceSensor : presenceSensors) {
				if (presenceSensor.getPropertyValue(LOCATION_PROPERTY_NAME).equals(changingDL.getPropertyValue(LOCATION_PROPERTY_NAME))) {
					if (presenceSensor.getSensedPresence()) {
						setStatusLightsFromPresenceSensor(presenceSensor);
						// Comenté pour la même raison qu'au dessus 
						//check number of light ON in the room
//						if (getNumberLightONFromLocation((String) changingDL.getPropertyValue(LOCATION_PROPERTY_NAME)) < maxLightsToTurnOnPerRoom) {
//							changingDL.setPowerLevel(changingDL.getMaxPowerLevel());
//						}
//					} else {
//						changingDL.setPowerLevel(0);
//					}
				}
			}
			
		}
		//System.out.println("<<<<<<<<<<< TEST 2 >>>>>>>>>");;
		}
	}

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
	
	/**
	 * 	 * Récupère l'ensemble des Binary lights d'une pièce
	 * @param location
	 * @return
	 */
	private synchronized List<BinaryLight> getBinaryLightFromlocation(String location) {
		List<BinaryLight> binaryLightsLocation = new ArrayList<BinaryLight>();
		for (BinaryLight binaryLight : binaryLights) {
			if (binaryLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				binaryLightsLocation.add(binaryLight);
			}
		}
		return binaryLightsLocation;
	}
	
	/**
	 * Récupère l'ensemble des dimmer lights d'une pièce
	 * @param location
	 * @return
	 */
	private synchronized List<DimmerLight> getDimmerLightFromlocation(String location) {
		List<DimmerLight> dimmerLightsFromLocation = new ArrayList<DimmerLight>();
		for (DimmerLight dimmerLight : dimmerLights) {
			if (dimmerLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				dimmerLightsFromLocation.add(dimmerLight);
			}
		}
		return dimmerLightsFromLocation;
	}
	
	/**
	 *Permet d'avoir le nombre de Binary Light allumé dans une pièce 
	 * @param location
	 * @return
	 */
	public int getNumberBinaryLightONFromLocation(String location) {
		int res = 0;
		for (BinaryLight binaryLight : binaryLights) {
			if (binaryLight.getPowerStatus() && binaryLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location))
				res++;
		}
		return res;
	}
	
	/**
	 * Permet d'avoir le nombre de lumière allumé dans une pièce
	 * @param location
	 * @return
	 */
	public int getNumberLightONFromLocation(String location) {
		int res = 0;
		for (BinaryLight binaryLight : binaryLights) {
			if (binaryLight.getPowerStatus() && binaryLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location))
				res++;
		}
		for (DimmerLight dimmerLight : dimmerLights) {
			if (dimmerLight.getPowerLevel()!=0 && dimmerLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location))
				res++;
		}
		return res;
	}
	/**
	 * Allume les lumières d'une pices en respectant la condition de nombre de lumière maximum allumé dans cette pièce
	 * @param presenceSensor
	 */
	public void setStatusLightsFromPresenceSensor(PresenceSensor presenceSensor) {
		String detectorLocation=(String) presenceSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
		List<BinaryLight> binaryLightsLocation = getBinaryLightFromlocation(detectorLocation);
		for (BinaryLight binaryLight : binaryLightsLocation) {
			if (presenceSensor.getSensedPresence()) {
				if (getNumberLightONFromLocation((String) binaryLight.getPropertyValue(LOCATION_PROPERTY_NAME)) < maxLightsToTurnOnPerRoom) {
					binaryLight.setPowerStatus(true);
				}
			} else {
				binaryLight.setPowerStatus(false);
			}
		}
		List<DimmerLight> dimmerLightsLocation = getDimmerLightFromlocation(detectorLocation);
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< test1235 DL");
		for (DimmerLight dimmerLight : dimmerLightsLocation) {
			System.out.println("dimmer light  "+dimmerLight.toString());
			if (presenceSensor.getSensedPresence()) {
				System.out.println("presence sensor true");
				System.out.println("location dimmer light : "+(String) dimmerLight.getPropertyValue(LOCATION_PROPERTY_NAME));
				System.out.println("Number light on  : "+getNumberLightONFromLocation((String) dimmerLight.getPropertyValue(LOCATION_PROPERTY_NAME).toString()));
				System.out.println("test end");
				if (getNumberLightONFromLocation((String) dimmerLight.getPropertyValue(LOCATION_PROPERTY_NAME)) < maxLightsToTurnOnPerRoom) {
					dimmerLight.setPowerLevel((double)1);
					System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< test DL");
				}
			} else {
				dimmerLight.setPowerLevel(0);
			}
		}
	}



}
