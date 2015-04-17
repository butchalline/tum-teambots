#How we control our dynamixel AX-12 motors in endless mode via Arduino.

# Motor Control #

Code to


# Pseudo code #

```
int Y: A/D-Wandler-Output
float W = Wunschwinkelgeschwindigkeit (positiv wenn direction true)
float t = aktuelle Zeit 
boolean direction = true, wenn Spannung zunehmend

constructor: transformer(slope,tInit):
	this.tInit = tInit;
	this.slope = slope;
	maxBit = 1023
	this.state = enum('linear','highP','middleP','lowP');
end

//##########################################################
float transform(t,Y,W,direction)
	dt = t - this.t;
	this.t = t;
	
	if(Y < maxBit && Y > 0)
		pose = Y*slope;
		state = linear;

	else if(Y == maxBit)
		if( state != highP)
		state = highP
			if(direction) 
				pose = 323/180*pi
			else
				pose = 341,5/180 *pi
			end				
		end
		pose = pose + dt * W
	else if( Y > 0.4*maxBit && Y < 0.6*maxBit && state != linear)
		if(state != middleP) 
			state = middleP;
			if(direction)
				pose = 341.5/180*pi;
			else
				pose = 350/180*pi;
			end
		end
		pose = pose + W*dt
	else if ( Y == 0)
		if(state != lowP) 
			state = lowP;
			if(direction)
				pose = 350/180*pi;
			else
				pose = 0;
			end
			pose = pose + W * dt
		end
	else
		print(ERROR, cant identify poseState)
	end
```