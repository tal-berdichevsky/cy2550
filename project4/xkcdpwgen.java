import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

//to represent a xkcd password generator
public class xkcdpwgen {	 
	 String wordsDirectory = System.getProperty("user.dir") + "\\words.txt";
	 FileInputStream file = new FileInputStream(this.wordsDirectory);
     Scanner scanner;
     int numWords;
     int capsWordCount;
     int randNumCount;
     int randSymbolCount;

     //constructor
     xkcdpwgen(int numWords, int capsWordCount, int randNumCount, int randSymbolCount) throws FileNotFoundException {
    	 this.scanner = new Scanner(this.file);
		 this.numWords = numWords;
		 this.capsWordCount = capsWordCount;
		 this.randNumCount = randNumCount;
		 this.randSymbolCount = randSymbolCount;
	 }

     //handles the user command line and calls the necessary methods to generate a password or a help message
	 public static void main(String[] args) throws FileNotFoundException {
		 int newNumWords = 4;
		 int newCapsWordCount = 0;
		 int newRandNumCount = 0;
		 int newRandSymbolCount = 0;
		 xkcdpwgen gen = new xkcdpwgen(4, 0, 0, 0);
		 boolean generate = true;
		 for (int index = 0; index<args.length; index++) {
			 switch (args[index]) {
			   case "--help":
			   case "-h": System.out.println(gen.help());
				          generate = false;
				          break;
			   case "--words":
			   case "-w": newNumWords = gen.isInt(args[index+1]);
			   			  generate = true;
				          break;
			   case "--caps":
			   case "-c": newCapsWordCount = gen.isInt(args[index+1]);
			   			  generate = true;
				          break;
			   case "--numbers":
			   case "-n": newRandNumCount = gen.isInt(args[index+1]);
			   			  generate = true;
				          break;
			   case "--symbols":
			   case "-s": newRandSymbolCount = gen.isInt(args[index+1]);  
			   			  generate = true;
				          break;
			   default:   break;	
			 }
         }
		 if (generate) {
			 System.out.println(new xkcdpwgen(newNumWords, 
					 newCapsWordCount, newRandNumCount, newRandSymbolCount).getPW());
		 }
	  }
	 
	 //is the given String an int? if it is, returns the int. Otherwise, returns 0
	 public int isInt(String word) {
		 try {
			 int num = Integer.parseInt(word);
			 return num;
		 } catch (NumberFormatException exc) {return 0;}
	 }
	 
	 //chooses a random word from the list
	 public static String chooseWord() throws FileNotFoundException {
		 FileInputStream file = new FileInputStream(System.getProperty("user.dir") + "\\words.txt");
	     String chosenWord = null;
	     Random random = new Random();
	     int i = 0;
	     for(Scanner scanner = new Scanner(file); scanner.hasNext();) {
	        ++i;
	        String currentLine = scanner.nextLine();
	        if(random.nextInt(i) == 0)
	           chosenWord = currentLine;         
	     }
	     return chosenWord;      
	  }
	 
	 // returns a password following the given specifications
	 public String getPW() throws FileNotFoundException {
		 return String.join("", getPWHelper(this.numWords, this.RandomLocationInts(this.capsWordCount, false, false), 
				 this.RandomLocationInts(this.randNumCount, true, true), this.RandomLocationInts(this.randSymbolCount, true, true), 0));		 
	 }
	 
	 //returns a list of Integers representing random locations in the list
	 public List<Integer> RandomLocationInts(int length, boolean endInclusive, boolean repetitionsAllowed) {
		 int totalNum = this.numWords;
		 if (endInclusive) {totalNum += 1;}
		 List<Integer> list= new ArrayList<Integer>();
		 for (int count = 0; count < length; count++) {
			 int randomLocation = new Random().nextInt(totalNum);	
			 if (!repetitionsAllowed) {		
				 if (list.size() < totalNum) {     
		    	   while (list.contains(randomLocation)) {randomLocation = new Random().nextInt(totalNum);}	
				 }
			 }
			 list.add(randomLocation);
		 }
		 return list;
	 }
	 
	 //helper for the getPW method
     public List<String> getPWHelper(int numWordsLeft, List<Integer> capsWordSpots, List<Integer> randNumSpots, List<Integer> randSymbolSpots, int i) throws FileNotFoundException {
    	 if (numWordsLeft == 0) {
    		 return new ArrayList<String>();
    	 }else {
    		 String newString = this.wordAdjust(xkcdpwgen.chooseWord().toLowerCase(), capsWordSpots, randNumSpots, randSymbolSpots, i, (numWordsLeft == 1));
    		 List<String> list = this.getPWHelper(numWordsLeft - 1, capsWordSpots, randNumSpots, randSymbolSpots, i+1);
    		 list.add(0, newString);
    		 return list;
    	 }    	     	  
	 }
     
     //adjusts each chosen word based on the given specifications(capitalization, the addition of numbers/symbols)
     public String wordAdjust(String currentWord, List<Integer> capsWordSpots, List<Integer> randNumSpots, 
    		 List<Integer> randSymbolSpots, int i, boolean end) {
    	 String word = currentWord; 
    	 if (capsWordSpots.contains(i)) {
    		 word = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    	 }
    	 if (randNumSpots.contains(i)) {
    		 word = this.generateChars(Collections.frequency(randNumSpots, i), "0123456789") + word;
    	 }
    	 if (randSymbolSpots.contains(i)) {
    		 word = this.generateChars(Collections.frequency(randSymbolSpots, i), "~!@#$%^&*:;") + word;
    	 }
    	 if (end && randNumSpots.contains(i + 1)) {
    		 word += this.generateChars(Collections.frequency(randNumSpots, (i + 1)), "0123456789");
    	 }
    	 if (end && randSymbolSpots.contains(i + 1)) {
    		 word += this.generateChars(Collections.frequency(randSymbolSpots, (i + 1)), "!@#$%^&*:;");
    	 }
    	 return word;   	 
     }
     
     //generates random characters(symbols or numbers as given) to insert in a password
     public String generateChars(int numChars, String allChars) {
    	 String output = "";
    	 for (int count = 0; count < numChars; count++) {
    		 output += allChars.charAt(new Random().nextInt(10));
    	 }
    	 return output;
     }
     
     //generates the help message
     public String help() {
    	 return "usage: xkcdpwgen [-h] [-w WORDS] [-c CAPS] [-n NUMBERS] [-s SYMBOLS]\r\n"
    	 		+ "\r\n Generate a secure, memorable password using the XKCD method\r\n"
    	 		+ "\r\n optional arguments:\r\n"
    	 		+ "    -h, --help            show this help message and exit\r\n"
    	 		+ "    -w WORDS, --words WORDS\r\n"
    	 		+ "                          include WORDS words in the password (default=4)\r\n"
    	 		+ "    -c CAPS, --caps CAPS  capitalize the first letter of CAPS random words\r\n"
    	 		+ "                          (default=0)\r\n"
    	 		+ "    -n NUMBERS, --numbers NUMBERS\r\n"
    	 		+ "                          insert NUMBERS random numbers in the password\r\n"
    	 		+ "                          (default=0)\r\n"
    	 		+ "    -s SYMBOLS, --symbols SYMBOLS\r\n"
    	 		+ "                          insert SYMBOLS random symbols in the password\r\n"
    	 		+ "                          (default=0)";
     }
}
