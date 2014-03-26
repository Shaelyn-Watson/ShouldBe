package app.there.shouldbe;

import twitter4j.Status;

public class Tweet {
   private String name;
   private String message;
   private String date;
   private long id;

   public Tweet(Status st) {
      id = st.getId();
      name = st.getUser().getScreenName();
      message = st.getText();
      date = st.getCreatedAt().toString().substring(0, 10);
   }
   public void setName(String _n){name = _n;}
   public void setMessage(String _m){message = _m;}
   public void setDate(String _d){date = _d;}

   public long getId(){return id;}
   public String getName(){return name;}
   public String getMessage(){return message;}
   public String getDate(){return date;}
   
   @Override
   public String toString() {
	   return id + " " + name + " " + message + " " + date;
   }
}