package teambot.smartphone.helper;

import teambotData.FloatArrayData;

public class ValueEstimator {

	public static FloatArrayData estimate(FloatArrayData atTime1, FloatArrayData atTime2, FloatArrayData atTime3, long timeOfEstimate) {
		float timeDiff1 = atTime1.getTimestamp() - atTime2.getTimestamp();
		float timeDiff2 = atTime2.getTimestamp() - atTime3.getTimestamp();
		
		float valueDiff1;
		float valueDiff2;
		float change;
		float[] estimateValues = new float[atTime1.getData().length];
		
		for(int i = 0; i < atTime1.getData().length; i++) {
			valueDiff1 = atTime1.getData()[i] - atTime2.getData()[i];
			valueDiff2 = atTime2.getData()[i] - atTime3.getData()[i];	
			
			change = ( (valueDiff1 / timeDiff1) + (valueDiff2 / timeDiff2) ) * 0.5f;
			estimateValues[i] = atTime2.getData()[i] + change * (atTime1.getTimestamp() - timeOfEstimate);
		}
		
		return new FloatArrayData(timeOfEstimate, estimateValues); 
	}
}
