package com.ssc.weipan.account;


import com.ssc.weipan.base.BaseApp;
import com.ssc.weipan.base.PreferencesUtil;


public class AccountHelper {

    public final static String localAnonymouseUserId = "local_anonymouse_user";
    private static final String Key_Current_User = "Current_User";
    private static final String Key_Last_User = "Last_User";
    private static String sCurrentUser;
    
    
    private static final String DB_Version = "_v14";

    private static String currentUserId = null;
    private static String currentUserDb = null;

    private static PersistentCookieStore sP;
    public static synchronized  PersistentCookieStore getCookieStore() {
        if (sP == null) {
            sP = new PersistentCookieStore(BaseApp.getApp());

        }
        return sP;
    }

    public static void setCurrUser(String name) {
        PreferencesUtil.putString(BaseApp.getApp(), Key_Current_User, name);
    }
    
    public static void initAccount() {
        sCurrentUser = PreferencesUtil.getString(BaseApp.getApp(), Key_Current_User, null);
        currentUserDb = sCurrentUser + DB_Version;
    }

    public static void setLastAccountId(String accountId) {
        PreferencesUtil.putString(BaseApp.getApp(), Key_Last_User, accountId);
    }

    public static String getLastAccountId() {
        return PreferencesUtil.getString(BaseApp.getApp(), Key_Last_User, null);
    }

//    public static void initAccountDebug() {
//        sCurrentUser = localAnonymouseUserId;
//        currentUserDb = "default_user_db" + DB_Version;
//
//        Realm realm = Realm.getInstance(BaseApp.getApp(), currUserDb());
//        realm.executeTransaction(LogTransaction.newInstantance(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                User user = User.getOrCreateUser(realm, AccountHelper.currentUser());
//
//                Random random = new Random();
//                user.setTotalGeAnsCount(random.nextInt(200));
//                user.setValidateTuancesCount(random.nextInt(200));
//                user.setFinishedCount(random.nextInt(200));
//            }
//        }));
//        realm.close();
//    }
     
    public static String currentUser() {
        return sCurrentUser;
    }
    
    public static String currUserDb() {
        return currentUserDb;
    }
    
//    public static String getHostUrl() {
//        return URI.create(ServerAPI.HOST + AccountAPI.LoginPATH).toString();
//    }
//
//    public static String getSessionCookie() {
//        StringBuilder sb = new StringBuilder();
//        List<HttpCookie> cookie = getCookieStore().get(URI.create(ServerAPI.HOST + AccountAPI.LoginPATH));
//        for (int i = 0; i < cookie.size(); i++) {
//            sb.append(cookie.get(i).toString());
//            if (i != cookie.size() -1)
//                sb.append("; ");
//        }
//        return sb.toString();
//    }
//
//    public static List<HttpCookie> getSessionCookieV2() {
//        return getCookieStore().get(URI.create(ServerAPI.HOST + AccountAPI.LoginPATH));
//    }
//
//    public static Object tokenExist() {
//        List<HttpCookie> cookie = getCookieStore().get(URI.create(ServerAPI.HOST + AccountAPI.LoginPATH));
//        HttpCookie token = null;
//        for (int i = 0; i < cookie.size(); i++) {
//            if (cookie.get(i).getName().equalsIgnoreCase("shxAuthToken")) {
//                token = cookie.get(i);
//                break;
//            }
//        }
//
//        return token;
//    }

    public static void logout() {
//        Runnable quit = new Runnable() {
//
//            @Override
//            public void run() {
//                getCookieStore().removeAll();
//                getCookieStore().getCookies();
//                setCurrUser(null);
//                initAccount();
//                Utils.finishAllActivity(BaseApp.getApp(), SplashActivity.class);
//            }
//        };
//
//        Action.logout(quit);
    }
}
