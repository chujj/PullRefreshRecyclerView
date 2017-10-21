package com.ssc.weipan.api.login;

import com.ssc.weipan.model.BaseModel;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by zhujj on 17-10-18.
 */
public class LoginApi {


    public final static String LOGIN_PATH = "/customer/login";

    // https://www.showdoc.cc/1676150?page_id=15434277
    public static interface ILogin {

        @FormUrlEncoded
        @POST(LOGIN_PATH)
        public void login(@Field("mobile") String mobile,
                          @Field("smsCode") String smsCode,
//                          @Field("password") String password,
                          Callback<LoginResp> cb);
    }


    public static class LoginResp extends BaseModel {
        public LoginData data;
    }

    public static class LoginData extends BaseModel {
        public int id; // ": "1",
        public String nickname; // ": "昵称",
        public String mobile; // ": "13889898989",
        public String headPortrait; // ": "头像地址",
        public int brokerId;
//        brokerId	long	推荐人ID


        public boolean needShowInputBroker () {
            return brokerId == 0;
        }
    }
}
