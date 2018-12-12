public class Tweet {
	private String text;
	private String date;
	private String time;
	private int retweets;
	private int favorites;
	public Tweet(String text, String date, String time, int retweets, int favorites) {
		this.text = text;
		this.date = date;
		this.time = time;
		this.retweets = favorites;
		this.favorites = retweets;
	}
	public String getText() {
		return this.text;
	}
	public String getDate() {
		return this.date;
	}
	public String getTime() {
		return this.time;
	}
	public int getRetweets() {
		return this.retweets;
	}
	public int getFavorites() {
		return this.favorites;
	}
	public String toString() {
		return ("Tweet: " + this.text + " Date: " + this.date + " Time: " + this.time + " Retweets: " + this.retweets + " Favorites: " + this.favorites);
	}
}
