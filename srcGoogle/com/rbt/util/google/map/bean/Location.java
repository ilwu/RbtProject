package com.rbt.util.google.map.bean;

/**
 * @author Allen
 *
 */
public class Location {

	public Location() {
	}

	double lat;
	double lng;

	// ==================================================================
	// gatter & setter
	// ==================================================================
	public double getLat() {
		return this.lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return this.lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
}
