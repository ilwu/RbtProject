package com.rbt.util.google.map.bean;

/**
 * @author Allen
 *
 */
public class ViewPort {

	public ViewPort() {
	}

	private Location northeast;
	private Location southwest;

	// ==================================================================
	// gatter & setter
	// ==================================================================
	public Location getSouthwest() {
		return this.southwest;
	}

	public void setSouthwest(Location southwest) {
		this.southwest = southwest;
	}

	public Location getNortheast() {
		return this.northeast;
	}

	public void setNortheast(Location northeast) {
		this.northeast = northeast;
	}
}
