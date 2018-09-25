package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class EvilHangmanGame implements IEvilHangmanGame {
	private int wordLength;
	private TreeSet<String> remainingWords;
	private TreeSet<String> previouslyGuessedLetters; // this is going to contain the guesses
	private Pattern pattern;
	private int numGuesses;
	private int numInstances;
	
	public EvilHangmanGame(){
		this.remainingWords = new TreeSet<String>();
		this.previouslyGuessedLetters = new TreeSet<String>();
	}
	
	@Override
	public void startGame(File dictionary, int wordLength) {
		this.wordLength=wordLength;

		Scanner scanner = null;
		try {
			scanner = new Scanner(dictionary);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
// Import all words into a TreeSet of words		
		while(scanner.hasNext()) {
			String temp = scanner.next();
			boolean addToDict = true;
			
			if(temp.length() != wordLength) { // Change this to be the size of word input as param 
				addToDict = false;
			}
			for(int i=0; i<temp.length(); i++) {
				if(!Character.isLetter(temp.charAt(i))) {
					addToDict = false;
				}
			}
			
			if(addToDict) {
				this.remainingWords.add(temp.toLowerCase());
			}
						
		}
						
		this.pattern = new Pattern(wordLength);
	

	}

	@Override
	public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
		// Reset numInstances
		
		this.numInstances = 0;
		//if guess in already guessed, throw GuessAlreadyMadeException. Catch in method that calls this
		
		if(this.letterPreviouslyGuessed(Character.toString(guess))) {
			throw new IEvilHangmanGame.GuessAlreadyMadeException();
		}
		
		this.previouslyGuessedLetters.add(Character.toString(guess));
		
		TreeMap<String, TreeSet<String>> wordFamilies = this.getWordFamilies(this.remainingWords, guess);
		ArrayList<String> tempWinningKeys = this.getWinningWordFamilyKeys(wordFamilies);
				
		if(tempWinningKeys.size()>1) {
			String winner = this.breakWordFamilyTie(guess,wordFamilies,tempWinningKeys);
			this.remainingWords = new TreeSet<String>(wordFamilies.get(winner));
			this.updatePatternAndDecr(winner);
			
		}
		else {
			this.remainingWords = new TreeSet<String>(wordFamilies.get(tempWinningKeys.get(0)));
			this.updatePatternAndDecr(tempWinningKeys.get(0));
		}
						
		return this.remainingWords;
	}
	
	// HELPER FUNCTIONS
	private void updatePatternAndDecr(String pat) {
		boolean charAdded = false;
		
		for(int i =0; i<pat.length(); i++) {
			if(pat.charAt(i) != '-') {
				charAdded = true;
				this.pattern.editIndexofPattern(i, pat.charAt(i));
				this.numInstances++;
			}
		}
		
		if(!charAdded) {
			this.numGuesses--;
		}
		
	}
	
	private String breakWordFamilyTie(char guess, TreeMap<String, TreeSet<String>> wordFamilies, ArrayList<String> tiedWordFamilyKeys) {
		ArrayList<String> tempWinningKeys = new ArrayList<String>();
		//Rules 1 & 2
		for(String wordFamilyKey:tiedWordFamilyKeys) {
			if(tempWinningKeys.size()==0) {
				tempWinningKeys.add(wordFamilyKey);
			}
			else if(this.getCountOfGuessChars(wordFamilyKey)<this.getCountOfGuessChars(tempWinningKeys.get(0))){
				tempWinningKeys = new ArrayList<String>();
				tempWinningKeys.add(wordFamilyKey);
			}
			else if(this.getCountOfGuessChars(wordFamilyKey)==this.getCountOfGuessChars(tempWinningKeys.get(0))) {
				tempWinningKeys.add(wordFamilyKey);
			}
		}
				
		if(tempWinningKeys.size()==1) {
			return tempWinningKeys.get(0);
		}
		else {
			// Implement Rules
			return this.getRightmostWinner(tempWinningKeys);
		}

	}
	
	private String getRightmostWinner(ArrayList<String> tempWinningKeys) {
		TreeSet<String> testSet = new TreeSet<String>();
		
		for(String str:tempWinningKeys) {
			testSet.add(str);
		}
		
		return testSet.pollFirst();
		
	}
	
	private Integer getCountOfGuessChars(String wordFamilyKey) {
		int tempCounter = 0;
		
		for(int i = 0; i<wordFamilyKey.length(); i++) {	
			if(wordFamilyKey.charAt(i)!='-') {
				tempCounter += 1;
			}
		}
		
		return tempCounter;
	}
	
	private ArrayList<String> getWinningWordFamilyKeys(TreeMap<String, TreeSet<String>> wordFamilies){
		ArrayList<String> tempWinningKeys = new ArrayList<String>();
		for(String key:wordFamilies.keySet()) {
			if(tempWinningKeys.size() == 0) {
				tempWinningKeys.add(key);
			}
			else if(wordFamilies.get(key).size()> wordFamilies.get(tempWinningKeys.get(0)).size()) {
				tempWinningKeys = new ArrayList<String>();
				tempWinningKeys.add(key);
			}
			else if(wordFamilies.get(key).size() == wordFamilies.get(tempWinningKeys.get(0)).size()) {
				tempWinningKeys.add(key);
			}
		}
		
		return tempWinningKeys;
	}
	
	private TreeMap<String, TreeSet<String>> getWordFamilies(TreeSet<String> oldSet, char guess){
		
		TreeMap<String, TreeSet<String>> wordFamilyCollection = new TreeMap<String, TreeSet<String>>();
	
		for(String word:oldSet) {
			Pattern wordFamilyIndices = this.getWordFamilyIndices(word, guess);
			String patternStr = wordFamilyIndices.toString();
			
			if(wordFamilyCollection.size() == 0) {
				TreeSet<String> ts = new TreeSet<String>();
				ts.add(word);
				wordFamilyCollection.put(patternStr, ts);
			}
			else {
				if(wordFamilyCollection.containsKey(patternStr)) {
					wordFamilyCollection.get(patternStr).add(word);
				}
				else{
					TreeSet<String> ts = new TreeSet<String>();
					ts.add(word);
					wordFamilyCollection.put(patternStr, ts);
				}
			}
		}
			
		return wordFamilyCollection;
	}
	
	private Pattern getWordFamilyIndices(String word, char guess){
		Pattern pattern = new Pattern(word.length());
		
		for(int i=0; i<word.length();i++) {
			if(word.charAt(i) == guess) {
				pattern.editIndexofPattern(i, guess);
			}
		}
		
		return pattern;
	}
	
	// GETTERS & SETTERS
	public void setGuessesCount(int guesses) {
		this.numGuesses=guesses;
	}
	
	public void decrNumGuessesLeft() {
		this.numGuesses -= 1;
	}
	
	public int getNumGuessesLeft() {
		return this.numGuesses;
	}
	
	public boolean letterPreviouslyGuessed(String guess){
				
		return this.previouslyGuessedLetters.contains(guess);
	}
	
	public void addLetterToGuessed(String guess) {
		this.previouslyGuessedLetters.add(guess);
	}
	
	public Set<String> getPreviouslyGuessedLetters(){
		return this.previouslyGuessedLetters;
	}
	
	public String getPattern() {
		return this.pattern.toString();
	}
	
	public TreeSet<String> getDictionary() {
		
		return this.remainingWords;
	}
	
	public boolean gameWon(){
		boolean gameIsDone = true;
		
		for(int i=0; i<this.pattern.toString().length(); i++) {
			if(this.pattern.toString().charAt(i)=='-') {
				gameIsDone = false;
			}
		}
		
		return gameIsDone;
	}
	
	public int getNumInstances() {
		return this.numInstances;
	}
}
