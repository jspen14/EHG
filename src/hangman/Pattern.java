package hangman;

public class Pattern{
	String pattern;
	
	Pattern(int size){
		StringBuilder patternBuilder = new StringBuilder("");
		
		for(int i=0;i<size;i++) {
			patternBuilder.append("-");
		}
		
		this.pattern = patternBuilder.toString();
		
	}
	
	public String toString() {
		
		return pattern.toString();		
	}
	
	public void editIndexofPattern(int index, char input) {
		StringBuilder patternBuilder = new StringBuilder(this.pattern);
		
		patternBuilder.setCharAt(index, input);
		
		this.pattern = patternBuilder.toString();
		
	}
	
	public String getPattern() {
		return this.pattern;
	}
	
}
