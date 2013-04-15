package teambot.slam;

public class BeamProbabilities {

	float _cellSize_mm;
	float _l0,_lOccupation,_lFree;
	float _maxDist;
	
	public BeamProbabilities(float cellSize_mm, float maxRange ,float p0, float pOccupation, float pFree){
		this._cellSize_mm = cellSize_mm;
		this._lOccupation = (float) Math.log(pOccupation/(1- pOccupation));
		this._lFree = (float) Math.log(pFree/(1- pFree));
		this._l0 = (float) Math.log(p0/(1- p0));
		this._maxDist = maxRange; 
	}
	
	public float getLogStartProbability()
	{
		return _l0;
	}
	
	public float getLogOccupationProbability()
	{
		return _lOccupation;
	}
	
	public float getLogFreeProbability()
	{
		return _lOccupation;
	}
}
