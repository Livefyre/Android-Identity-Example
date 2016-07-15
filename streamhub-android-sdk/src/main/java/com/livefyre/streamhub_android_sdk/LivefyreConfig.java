package com.livefyre.streamhub_android_sdk;

public class LivefyreConfig {
	// comments
	public static String scheme = "http";
	public static String environment = "livefyre.com";
	public static String bootstrapDomain = "bootstrap";
	public static String quillDomain = "quill";
	public static String adminDomain = "admin";
	public static String identityDomain = "identity";
	public static String streamDomain = "stream1";
	public static String origin = "http://livefyre-cdn-dev.s3.amazonaws.com";
	public static String referer = "http://livefyre-cdn-dev.s3.amazonaws.com/demos/lfep2-comments.html";
//	public static String networkId ="labs.fyre.co";
	private static String networkID =null;

	public static void setLivefyreNetworkID(String networkID){
		LivefyreConfig.networkID=networkID;
	}
	public static String getConfiguredNetworkID(){
		if(networkID==null){
			throw new AssertionError("You should set Livefyre Network key");
		}
		return networkID;
	}
}
