package com.example.myapplication;

public class KEY {
    private String googleAPIKey = "YOUR GOOGLE MAP API KEY";
    private String jusoStreetNameApiKey = "YOUR STREET API KEY";
    private String jusoLocationApiKey = "YOUR LOCATION API KEY";
    private String MYNodeServer = "YOUR SERVER URL";
    public String getGoogleApiKey() {
        return googleAPIKey;
    }
    public String getJusoStreetNameApiKey() {
        return jusoStreetNameApiKey;
    }
    public String getJusoLocationApiKey(){
        return jusoLocationApiKey;
    }
    public String getMYNodeServer(String opt){

        if(opt.equals("lat")) {
            return MYNodeServer + "/lat?lat=";
        }else if(opt.equals("lng")){
            return MYNodeServer + "/lng?lng=";
        }else if(opt.equals("loc")) {
            return MYNodeServer + "/loc?";
        }else {
            return MYNodeServer;
        }
    }

}
