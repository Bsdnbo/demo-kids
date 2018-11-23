package com.example.mychatrobot;

public class TalkBean {
	
	public String askcontent;
	public String answercontent;
	public boolean isask;
	public int imageId;
	

	public TalkBean(String askcontent, String answercontent, boolean isask,
			int imageId) {
		super();
		this.askcontent = askcontent;
		this.answercontent = answercontent;
		this.isask = isask;
		this.imageId = imageId;
	}
	

}
