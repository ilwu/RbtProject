package test;

import java.util.ArrayList;
import java.util.List;

public class TestBean {

	public TestBean() {
		// TODO Auto-generated constructor stub
	}

	public String publicCC = "cc";
	private String privateAA;
	public List publicList = new ArrayList();
	public Object[] publicObjAry;
	/**
	 * @return the privateAA
	 */
	public String getPrivateAA() {
		return this.privateAA;
	}
	/**
	 * @param privateAA the privateAA to set
	 */
	public void setPrivateAA(String privateAA) {
		this.privateAA = privateAA;
	}
	/**
	 * @return the publicObjAry
	 */
	public Object[] getPublicObjAry() {
		return this.publicObjAry;
	}
	/**
	 * @param publicObjAry the publicObjAry to set
	 */
	public void setPublicObjAry(Object[] publicObjAry) {
		this.publicObjAry = publicObjAry;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


}
