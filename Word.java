public class Word {
	String content;
	int timesInTweets;

	public Word(String content) {
		this.content = content;
		this.timesInTweets = 1;
	}

	public String getContent() {
		return this.content;
	}

	public int getTimesInTweets() {
		return this.timesInTweets;
	}

	public void incrementTimesInTweets() {
		this.timesInTweets += 1;
	}

	public String toString() {
		return ("The word " + "\"" + this.content + "\"" + " is used " + this.timesInTweets + " times.");
	}
}
