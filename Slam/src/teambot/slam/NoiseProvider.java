package teambot.slam;

import java.util.Random;

public class NoiseProvider {
	Random rand = new Random();
	float varianceX;
	float varianceY;
	float varianceAngle;
	public NoiseProvider(float varianceX,float varianceY, float varianceAngle){	
		this.varianceAngle = varianceAngle;
		this.varianceX = varianceX;
		this.varianceY = varianceY;
	}
	
	public float noiseX(){
		return (float) (rand.nextGaussian() * this.varianceX);
	}
	
	public float noiseY(){
		return (float) (rand.nextGaussian() * this.varianceY);
	}
	
	public float noiseAngle(){
		return (float) (rand.nextGaussian()*this.varianceAngle);
	}
	
	
	

}
