package com.example.mychatrobot;

import java.util.ArrayList;

public class JsonBean {
	public ArrayList<WS> ws;
	
	class WS{
		public ArrayList<CW> cw;
	}
	
	class CW{
		public String w;
	}
}
