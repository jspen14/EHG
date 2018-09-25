package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

import hangman.IEvilHangmanGame.GuessAlreadyMadeException;

public class Main {
	
	
	public static void main(String[] args) throws IOException {
				
		if(args.length == 3) {
			if(Character.isDigit(args[1].charAt(0)) && Character.isDigit(args[2].charAt(0))) {
				String dictionary = args[0];
				int wordLength = Integer.parseInt(args[1]); // >= 2
				int numGuesses = Integer.parseInt(args[2]); // >= 1
				
				
				if(wordLength>=2 && numGuesses>=1) {
					EvilHangmanGame backend = new EvilHangmanGame(wordLength, numGuesses);
					File dictFile = new File(dictionary);
					
					backend.startGame(dictFile, wordLength);
					
					if(backend.getDictionary().size()==0) {
						System.out.println("There are no words to guess.");
					}
					else {
						gameInteraction(backend);
					}
				}
				else {
					System.out.println("wordLength must be ≥ 2 and guesses must be ≥ 1");
				}
			}
			else {
				System.out.println("wordLength must be ≥ 2 and guesses must be ≥ 1");
			}
		}
		else {
			System.out.println("Must enter in filepath, wordLength, and numGuesses");
		}
	}
	
	private static void gameInteraction(EvilHangmanGame backend)throws IOException{
		Scanner scanner = new Scanner(System.in);
		String guessString = "";
		char guessChar = 0;
		boolean badInput = true;
		
		while(backend.getNumGuessesLeft()>0 && !backend.gameWon()) {
			badInput=true;
			
			while(badInput){
				printConsole(backend.getNumGuessesLeft(), backend.getPreviouslyGuessedLetters(), backend.getPattern());
				System.out.print("Enter guess: ");
				guessString = scanner.nextLine();
				
				if(guessString.length()!=1){
					System.out.println("Invalid Input. Enter a SINGLE alphabetic character.");
				}
				else {
					guessChar = guessString.charAt(0);
					
					if(!Character.isLetter(guessChar)){
				 		System.out.println("Invalid input."); // this needs to be tested multiple times
					}
					else{
						badInput = false;
						guessString=guessString.toLowerCase();
						guessChar = guessString.charAt(0);
						
						try {
							backend.makeGuess(guessChar);
							int numInstances = backend.getNumInstances();
							
							if(numInstances == 0) {
								System.out.println("Sorry, there are no " + guessChar + "'s");
							}
							else if(numInstances == 1) {
								System.out.println("Yes, there is " + numInstances + " " + guessChar);
							}
							else {
								System.out.println("Yes, there are " + numInstances + " " + guessChar + "'s");
							}
						} catch (GuessAlreadyMadeException e) {
							System.out.println("You already used that letter");
						}
					} 
				}
			}	
		}
		
		if(backend.gameWon()) {
			System.out.println("You win!");
		}
		else {
			System.out.println("You lose!");
			System.out.println("The word was: " + backend.getDictionary().pollFirst());
		}
	}

	private static void printConsole(int numGuesses, Set<String> usedLetters, String word){
		
		System.out.print("\n");
		System.out.println("You have " + numGuesses + " guesses left");
		
		System.out.print("Used letters: ");
		for (String letter:usedLetters) {
			System.out.print(letter + " ");
		}
		System.out.print("\n");
		
		System.out.println("Word: " + word);
	}
	
}
