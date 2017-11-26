import java.util.ArrayList;

public class Functions {

	static int dictCall = 0;
    
    // checks dictionary API
    public static boolean isAWord(String s) {
    	dictCall++;
    	return true;
    }
    
    public static int randomWithRange(int min, int max)
    {
       int range = (max - min) + 1;     
       return (int)(Math.random() * range) + min;
    }
    
    public static int choose(int charsLen, int wordLen) {
    	if (wordLen == 1)
    		return 1;
    	int product = charsLen;
    	int multiplier = charsLen-1;
    	for (int i = 0; i < wordLen-1; i++) {
    		product *= multiplier;
    		multiplier -= 1;
    		System.out.println("hi "+product);
    	}
    	System.out.println("product for wordLen "+wordLen+" is "+product);
    	return product;
    }
    
    public static ArrayList simulatedAnnealingGetWords(char[] chars) {
    	int randomInt = 0;
    	int length = 2;
    	char[] temp = chars;
    	int count = 0;
    	int randomizations = 5;
    	ArrayList<String> words = new ArrayList();
    	// case for length = 1:
    	for (char c : chars) {
    		if (isAWord(c+""))
    			words.add(c+"");
    	}
    	// all other lengths
    	for (int i = length; i <= chars.length; i++) {
    		System.out.println("i = "+i);
    		String word = "";
    		int choice = choose(chars.length, i);
    		System.out.println("choice = "+choice);
    		for (int k = 0; k < choice*randomizations; k++) {
    			System.out.println("k = "+k);
    			word = "";
    			temp = chars.clone();
    			for (int j = 0; j < i; j++) {
        			randomInt = randomWithRange(0, chars.length-1);
        			while (temp[randomInt] == '?')
        				randomInt = randomWithRange(0, chars.length-1);
        			word += temp[randomInt];
        			temp[randomInt] = '?';
        		}	
        		if (!words.contains(word)) {
        			if (isAWord(word))
        				words.add(word);
        			else System.out.println("REJECT BECAUSE NOT VALID WORD IN DICTIONARY");
        		}
        		else System.out.println("REJECT BECAUSE DUPLICATE");
        		count++;
        		System.out.println("ran this amount of times: "+count);
        		System.out.println(word);
    		}
    	}
    	return words;
    }
	public static void main(String[] args) {
		String input = "abcdefg";
		char[] chars = input.toCharArray();
//		ArrayList<String> anagrams = getAnagrams(chars, input);
		ArrayList<String> anagrams = simulatedAnnealingGetWords(chars);
		System.out.println(anagrams.size());
		System.out.println(anagrams.toString());
    	System.out.println("dictionaryCalls: "+ dictCall);
//        int len = chars.length;
//        for (int i = len; i > 0; i--) {
//        	iterate(chars, i, new char[i], 0);
//        }
	}
}