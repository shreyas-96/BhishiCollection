//package com.example.routewisecollection.utils;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//
//public class LoginManager {
//    private static final String PREF_NAME = "agent_session";
//    private static final String KEY_AGENT_ID = "agentId";
//    private static final String KEY_AGENT_NAME = "agentName";
//    private static final String KEY_AGENT_MOBILE = "agentMobile";
//    private static final String KEY_ROUTE = "route";
//
//    private final SharedPreferences prefs;
//
//    public LoginManager(Context context) {
//        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
//    }
//
//    public void saveLoginSession(String agentId, String agentName, String mobile, String route) {
//        prefs.edit()
//                .putString(KEY_AGENT_ID, agentId)
//                .putString(KEY_AGENT_NAME, agentName)
//                .putString(KEY_AGENT_MOBILE, mobile)
//                .putString(KEY_ROUTE, route)
//                .apply();
//    }
//
//    public boolean isLoggedIn() {
//        return prefs.contains(KEY_AGENT_ID);
//    }
//
//    public String getAgentId() { return prefs.getString(KEY_AGENT_ID, null); }
//    public String getAgentName() { return prefs.getString(KEY_AGENT_NAME, ""); }
//    public String getAgentMobile() { return prefs.getString(KEY_AGENT_MOBILE, ""); }
//    public String getRoute() { return prefs.getString(KEY_ROUTE, ""); }
//
//    public void logout() {
//        prefs.edit().clear().apply();
//    }
//}



package com.example.routewisecollection.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginManager {

    private static final String PREF_NAME = "RouteWiseLogin";
    private static final String KEY_AGENT_ID = "agent_id";
    private static final String KEY_AGENT_NAME = "agent_name";
    private static final String KEY_AGENT_MOBILE = "agent_mobile";
    private static final String KEY_ROUTE = "agent_route";
    private static final String KEY_LOGGED_IN = "is_logged_in";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public LoginManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void saveLoginSession(String agentId, String agentName, String mobile, String route) {
        editor.putString(KEY_AGENT_ID, agentId);
        editor.putString(KEY_AGENT_NAME, agentName);
        editor.putString(KEY_AGENT_MOBILE, mobile);
        editor.putString(KEY_ROUTE, route);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_LOGGED_IN, false);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public String getAgentId() {
        return preferences.getString(KEY_AGENT_ID, "");
    }

    public String getAgentName() {
        return preferences.getString(KEY_AGENT_NAME, "");
    }

    public String getAgentMobile() {
        return preferences.getString(KEY_AGENT_MOBILE, "");
    }

    public String getRoute() {
        return preferences.getString(KEY_ROUTE, "");
    }
}
