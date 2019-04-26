package com.example.kishanthprab.placehook.Remote;

import android.util.Log;

import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;

public class UberClient {

    private static final String TAG = "UberClient";
    public static SessionConfiguration config = null;

    public static void initiateUberClient() {
        try {
            config = new SessionConfiguration.Builder()
                    // mandatory
                    .setClientId("C2wPLy3iSu048oKLYLAXfyvZMkTaPJoU")
                    // required for enhanced button features
                    .setServerToken("2RmBPHEJEB0fkMMh9o0-8W86icgdaPM_Zz9gT55M")
                    // required for implicit grant authentication
                    .setRedirectUri("https://developer.uber.com/docs/riders/guides/sandbox")
                    // optional: set sandbox as operating environment
                    .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                    .build();

            UberSdk.initialize(config);
            Log.d(TAG, "getUberClient: connection established succesfully");
        } catch (Exception e) {
            Log.d(TAG, "getUberClient: error - " + e.getMessage());
        }

    }

    public static ServerTokenSession getNewUberSession() {
        return new ServerTokenSession(config);
    }

}
