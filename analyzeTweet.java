import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class analyzeTweet {

	public static String removePunctuation(String word) {
		int length = word.length();
		int count = 0;
		while (count < length) {
			if (word.charAt(count) == ';' || word.charAt(count) == '}' || word.charAt(count) == '@'
					|| word.charAt(count) == '{' || word.charAt(count) == '.' || word.charAt(count) == '!'
					|| word.charAt(count) == '?' || word.charAt(count) == '\"' || word.charAt(count) == '\''
					|| word.charAt(count) == ':') {
				word = word.substring(0, count) + word.substring(count + 1, length);
				length = word.length();
				count = 0;
			} else {
				count += 1;
			}
		}
		return word;
	}

	public static ArrayList<Tweet> loadTweets() {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		Scanner infile = null;
		int count = 0;
		int retweets = 0;
		int favorites = 0;
		try {
			infile = new Scanner(new FileReader("Trump.txt"));
			while (infile.hasNextLine()) {
				String sentence = infile.nextLine();
				if (count > 0) {
					if (sentence.equals("\"\"")) {
						continue;
					} else {
						sentence = sentence.replaceAll("amp;", "and");
						sentence = sentence.replaceAll("&", "");
						String[] parts = sentence.split(",");
						if (parts.length == 4) {
							retweets = Integer.parseInt(parts[2]);
							favorites = Integer.parseInt(parts[3]);
							String[] timeAndDateParts = parts[1].split(" ");
							tweets.add(
									new Tweet(parts[0], timeAndDateParts[0], timeAndDateParts[1], retweets, favorites));
						}
					}
				}
				count++;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			infile.close();
		}
		return tweets;
	}

	public static int[] mostCommonWord(ArrayList<Word> words, int n) {
		int maxVal = 0;
		int timesInTweets = 0;
		int length = words.size();
		int[] occurrences = new int[n];
		for (int k = 0; k < n; k++) {
			occurrences[k] = 0;
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < length; j++) {
				if (!words.get(j).getContent().equals("")) {
					timesInTweets = words.get(j).getTimesInTweets();
					if (timesInTweets > maxVal) {
						maxVal = timesInTweets;
					}
				}
			}
			occurrences[i] = maxVal;
			for (int k = 0; k < length; k++) {
				if (words.get(k).getTimesInTweets() == maxVal) {
					Word temp = new Word("");
					words.set(k, temp);
				}
			}
			maxVal = 0;
		}
		return occurrences;
	}

	public static ArrayList<Word> loadWords(ArrayList<Tweet> tweets) {
		ArrayList<Word> words = new ArrayList<Word>();
		int lengthTweets = tweets.size();
		for (int i = 0; i < lengthTweets; i++) {
			String sentence = tweets.get(i).getText();
			sentence = sentence.replaceAll("/", " ");
			sentence = sentence.replaceAll("-", " ");
			sentence = sentence.replaceAll("#", " ");
			sentence = sentence.replaceAll("\\(", " ");
			sentence = sentence.replaceAll("\\)", " ");
			sentence = removePunctuation(sentence);
			sentence = sentence.toLowerCase();
			String[] parts = sentence.split(" ");
			int length = parts.length;
			for (int j = 0; j < length; j++) {
				if (parts[j].contains("https")) {
					for (int k = j; k < length; k++) {
						parts[k] = "";
					}
				}
			}
			for (int j = 0; j < parts.length; j++) {
				if (doesWordExist(words, parts[j]) == 0) {
					words.add(new Word(parts[j]));
				} else {
					words.get(doesWordExist(words, parts[j])).incrementTimesInTweets();
				}
			}
		}
		return words;
	}

	public static int doesWordExist(ArrayList<Word> words, String content) {
		int length = words.size();
		for (int i = 0; i < length; i++) {
			if (words.get(i).getContent().equals(content)) {
				return i;
			}
		}
		return 0;
	}

	public static ArrayList<Tweet> searchTweets(ArrayList<Tweet> tweets, String[] arrSearchWords) {
		ArrayList<Tweet> searchedTweets = new ArrayList<Tweet>();
		int i = 0;
		int j = 0;
		int count = 0;
		while (i < tweets.size()) {
			while (j < arrSearchWords.length) {
				if (!tweets.get(i).getText().toLowerCase().contains(arrSearchWords[j])) {
					i += 1;
					j = 0;
					count = 0;
					break;
				} else {
					j += 1;
					count += 1;
					if (count == arrSearchWords.length) {
						searchedTweets.add(tweets.get(i));
						count = 0;
						i += 1;
						j = 0;
					}
				}
			}
		}
		searchedTweets = sortTweets(searchedTweets);
		return searchedTweets;
	}

	public static ArrayList<Tweet> sortTweets(ArrayList<Tweet> searchedTweets) {
		int length = searchedTweets.size();
		for (int i = 0; i < length - 1; i++) {
			int indexOfMinVal = i;
			for (int j = i + 1; j < length; j++) {
				if (searchedTweets.get(j).getFavorites() < searchedTweets.get(indexOfMinVal).getFavorites()) {
					indexOfMinVal = j;
				}
			}
			Tweet temp = searchedTweets.get(i);
			searchedTweets.set(i, searchedTweets.get(indexOfMinVal));
			searchedTweets.set(indexOfMinVal, temp);
		}
		return searchedTweets;
	}

	public static ArrayList<Tweet> tweetsWithinHours(ArrayList<Tweet> tweets, int startingHour, int endingHour) {
		ArrayList<Tweet> validTweets = new ArrayList<Tweet>();
		for (int i = 0; i < tweets.size(); i++) {
			String[] parts = tweets.get(i).getTime().split(":");
			int hour = Integer.parseInt(parts[0]);
			if (startingHour < endingHour) {
				if (hour >= startingHour & hour < endingHour) {
					validTweets.add(tweets.get(i));
				}
			} else {
				if ((hour >= startingHour & hour <= 23) | (hour >= 0 & hour < endingHour)) {
					validTweets.add(tweets.get(i));
				}
			}

		}
		validTweets = sortTweets(validTweets);
		return validTweets;
	}

	public static int[] tweetDistribution(ArrayList<Tweet> tweets) {
		int[] months = new int[108];
		for (int i = 0; i < 108; i++) {
			months[i] = 0;
		}
		for (int i = 0; i < tweets.size(); i++) {
			String[] parts = tweets.get(i).getDate().split("-");
			int month = Integer.parseInt(parts[0]);
			int year = Integer.parseInt(parts[2]);
			int num = ((year - 2009) * 12 + month) - 1;
			months[num] = months[num] + 1;
		}
		return months;
	}

	public static void main(String[] args) {
		ArrayList<Tweet> tweets = loadTweets();
		ArrayList<Word> words = loadWords(tweets);
		Scanner scn = new Scanner(System.in);
		System.out.println("You are accessing a dataset of " + tweets.size() + " tweets.");
		System.out.println("Remember that more tweets have been made since I downloaded this archive");
		System.out.println("To see the \"n\" most common words, enter a number \"n\".");
		int n = scn.nextInt();
		int[] occurrences = new int[n];
		occurrences = (mostCommonWord(words, n));
		words = loadWords(tweets);
		int i = 0;
		while (i < n) {
			for (int j = 0; j < words.size(); j++) {
				if (words.get(j).getTimesInTweets() == occurrences[i]) {
					System.out.println(words.get(j));
					i += 1;
					break;
				}
			}
		}
		scn.nextLine();
		String verification = "";
		Boolean wantMore = true;
		while (wantMore) {
			System.out.println("Enter a word to see how many times Trump has used it.");
			String word = scn.nextLine();
			System.out.println("Now, to see the \"n\" most popular tweets using this word, enter a number \"n\".");
			int num = scn.nextInt();
			if (doesWordExist(words, word) == 0) {
				System.out.println(
						"Trump has never used this word. Would you like to try again? Enter \"yes\" or \"no\".");
				while (true) {
					verification = scn.nextLine();
					if (verification.equals("yes")) {
						break;
					} else if (verification.equals("no")) {
						wantMore = false;
						break;
					} else {
						System.out.println("Your entry was not a \"yes\" or \"no\". Try again");
					}
				}
			} else {
				System.out.println(words.get(doesWordExist(words, word)));
				String[] singleWordArr = new String[1];
				singleWordArr[0] = word;
				ArrayList<Tweet> tweetsWithWord = searchTweets(tweets, singleWordArr);
				for (int j = tweetsWithWord.size() - num; j < tweetsWithWord.size(); j++) {
					System.out.println(tweetsWithWord.get(j));
				}
				System.out.println("Would you like to enter more words? Enter \"yes\" or \"no\".");
				while (true) {
					verification = scn.nextLine();
					if (verification.equals("yes")) {
						break;
					} else if (verification.equals("no")) {
						wantMore = false;
						break;
					} else {
						System.out.println("Your entry was not a \"yes\" or \"no\". Try again");
					}
				}
			}
		}
		wantMore = true;
		while (wantMore) {
			System.out.println(
					"To see all of the tweets where Trump uses a set of words, first enter the number of words you will be entering.");
			n = scn.nextInt();
			scn.nextLine();
			String[] arrSearchWords = new String[n];
			System.out.println("Now enter each word one by one, pressing enter after each word.");
			for (int j = 0; j < n; j++) {
				arrSearchWords[j] = scn.nextLine();
			}
			ArrayList<Tweet> searchedTweets = searchTweets(tweets, arrSearchWords);
			for (int j = 0; j < searchedTweets.size(); j++) {
				System.out.println(searchedTweets.get(j));
			}
			System.out.println("There were " + searchedTweets.size() + " of these tweets.");
			System.out.println("NOTE: These tweets were sorted by favorites.");
			System.out.println("Would you like to enter more words? Enter \"yes\" or \"no\".");
			while (true) {
				verification = scn.nextLine();
				if (verification.equals("yes")) {
					break;
				} else if (verification.equals("no")) {
					wantMore = false;
					break;
				} else {
					System.out.println("Your entry was not a \"yes\" or \"no\". Try again");
				}
			}
		}
		wantMore = true;
		while (wantMore) {
			System.out.println(
					"To see all of Trump's tweets between a range of hours, first enter the beginning of the range and then enter the end of the range.");
			System.out.println(
					"Enter the hours as whole numbers between 0 and 23 (0 signifies 12:00 AM). For example, to see Trump's tweets between 10 PM and 3 AM, you would enter \"22\" and \"3\".");
			int startingHour = scn.nextInt();
			int endingHour = scn.nextInt();
			ArrayList<Tweet> validTweets = tweetsWithinHours(tweets, startingHour, endingHour);
			for (int j = 0; j < validTweets.size(); j++) {
				System.out.println(validTweets.get(j));
			}
			float percentTweets = (((float) validTweets.size()) / tweets.size()) * 100;
			System.out.println("These tweets comprise " + percentTweets
					+ "% of Trump's total tweets. NOTE: These tweets were sorted by favorites.");
			System.out.println("Would you like to see more time ranges? Enter \"yes\" or \"no\".");
			while (true) {
				scn.nextLine();
				verification = scn.nextLine();
				if (verification.equals("yes")) {
					break;
				} else if (verification.equals("no")) {
					wantMore = false;
					break;
				} else {
					System.out.println("Your entry was not a \"yes\" or \"no\". Try again");
				}
			}
		}
		int[] tweetsDistributed = new int[108];
		tweetsDistributed = tweetDistribution(tweets);
		for (int j = 0; j < 108; j++) {
			int year = (j / 12) + 2009;
			int monthNum = (j - (j / 12) * 12) + 1;
			String month = "";
			switch (monthNum) {
			case 1:
				month = "January";
				break;
			case 2:
				month = "February";
				break;
			case 3:
				month = "March";
				break;
			case 4:
				month = "April";
				break;
			case 5:
				month = "May";
				break;
			case 6:
				month = "June";
				break;
			case 7:
				month = "July";
				break;
			case 8:
				month = "August";
				break;
			case 9:
				month = "September";
				break;
			case 10:
				month = "October";
				break;
			case 11:
				month = "November";
				break;
			case 12:
				month = "December";
				break;
			}
			System.out.println(month + " " + year + ": " + tweetsDistributed[j]);
		}
		System.out.println("Here is the distribution of tweets since Trump created his account in 2009.");
		scn.close();
	}
}