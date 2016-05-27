package com.kvana.streamhub_android_sdk;

public enum LFSFlag {
	SPAM, OFFENSIVE, DISAGREE, OFF_TOPIC;
	public int value() {
		switch (this) {
		case SPAM:
			return 0;
		case OFFENSIVE:
			return 1;
		case DISAGREE:
			return 2;
		case OFF_TOPIC:
			return 3;
		default:
			return 4;
		}
	}

}
