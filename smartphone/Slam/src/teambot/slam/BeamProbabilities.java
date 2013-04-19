package teambot.slam;

public class BeamProbabilities {

	float _l0;
	float _lOccupation;
	float _lFree;
	
	public BeamProbabilities(float p0, float pOccupation, float pFree){
		this._l0 = (float) Math.log(p0/(1- p0));
		this._lOccupation = (float) Math.log(pOccupation/(1- pOccupation));
		this._lFree = (float) Math.log(pFree/(1- pFree));		
	}
	
	public BeamProbabilities(BeamProbabilities probabilities){
		this._l0 = probabilities._l0;
		this._lOccupation = probabilities._lOccupation;
		this._lFree = probabilities._lFree;
	}
	
	public float getLogOddStart()
	{
		return _l0;
	}
	
	public float getLogOddOccupation()
	{
		return _lOccupation;
	}
	
	public float getLogOddFree()
	{
		return _lFree;
	}
}
