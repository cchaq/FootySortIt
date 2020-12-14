package org.mobile.android.footysortit.main;


import java.util.ArrayList;

public class PlayerResponseList {
    private static ArrayList<String> positiveResponse = new ArrayList<String>();
    private static ArrayList<String> negativeResponse = new ArrayList<String>();
    private static ArrayList<String> neutralResponse = new ArrayList<String>();

    public static ArrayList<String> returnPositiveResponses(){
        positiveResponse.add("yes");
        return positiveResponse;

    }
    public static ArrayList<String> returnNegativeResponse(){
        negativeResponse.add("no");
        return negativeResponse;
    }
    public static ArrayList<String> returnNeutralResponse(){
        neutralResponse.add("maybe");
        return neutralResponse;
    }
}
