package ch.maybites.quescript.messages;

public class CMsgFadedOut implements CMsgInterface{
	String ramp;
	
	public CMsgFadedOut(String _ramp){
		ramp = _ramp;
	}

	public boolean isTrigger(String _name) {
		return false;
	}

	public boolean isFade(String _name) {
		return false;
	}

	public boolean isFadedOut(String _name) {
		return (ramp.equals(_name)?true:false);
	}

	public boolean isAnim(String _name) {
		return false;
	}

	public boolean isStop() {
		return false;
	}

	public boolean isFade() {
		return false;
	}
}
