package ak.miniproject3;

public class Event {
    public String id;
    public String email;
    public String imageURL;
    public String eventName;
    public String eventDescription;
    public int numberInterested;
    public int day;
    public int month;
    public int year;
    public long time;
    public String date;
    public Event() {
        //do nothing
    }


    public Event(String id, String email, String imageURL, String eventName, int numberInterested, String eventDescription, String date, long time) {
        this.id = id;
        this.email = email;
        this.imageURL = imageURL;
        this.eventName = eventName;
        this.numberInterested = numberInterested;
        this.time = time;
        this.eventDescription = eventDescription;
        this.date = date;

    }

    public String getDate() {
        return date;
    }
    public String getId() {
        return id;
    }
    public String getEmail() {
        return email;

    }

    public String getImageURL() {
        return imageURL;
    }

    public String getEventName() {
        return eventName;
    }

    public int getNumberInterested() {
        return numberInterested;
    }

    public void updateInterested() {
        numberInterested += 1;
    }
    public String getEventDescription() {
        return eventDescription;
    }


}
