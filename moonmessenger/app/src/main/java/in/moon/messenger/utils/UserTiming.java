package in.moon.messenger.utils;

public class UserTiming {

    public boolean seen;
    public long timestamp;

    public UserTiming() {

    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public UserTiming(boolean seen, long timestamp) {
        this.seen = seen;
        this.timestamp = timestamp;
    }
}
