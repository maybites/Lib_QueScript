package ch.maybites.quescript;

public interface QueMessage {

	/**
	 * For Max MXJ External use only
	 * @return
	 */
	public boolean hasAtoms();

	/**
	 * For Max MXJ External use only
	 * @return
	 */
	public Object[] getAtoms();
	
	/**
	 * Start iteration through message tokens
	 */
	public void iterate();
	
	/**
	 * returns true if there is another token
	 * @return
	 */
	public boolean hasNext();
	
	/**
	 * tells if next token is a string
	 * @return
	 */
	public boolean isNextString();

	/**
	 * tells is next token is a float
	 * @return
	 */
	public boolean isNextFloat();
	
	/**
	 * returns next token as a string
	 * @return
	 */
	public String nextString();

	/**
	 * returns next token as a float
	 * @return
	 */
	public float nextFloat();
	
}
