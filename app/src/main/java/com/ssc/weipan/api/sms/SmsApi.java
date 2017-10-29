package com.ssc.weipan.api.sms;

import com.ssc.weipan.model.BaseModel;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by zhujj on 17-10-17.
 */
public class SmsApi {

    public static interface ISMS {

        @GET("/sms/get_sms_code")
        public void requereSMSCode(@Query("sms_code_type") String type, @Query("mobile") String mobile, Callback<BaseModel> cb);

        @GET("/sms/get_sms_code")
        public void requereSMSCode2(@Query("sms_code_type") String type, Callback<BaseModel> cb);

        @GET("/sms/verify_sms_code")
        public void verifySms(@Query("sms_code_type") String type, @Query("sms_code") String sms_code, Callback<BaseModel> cb);

    }
}
