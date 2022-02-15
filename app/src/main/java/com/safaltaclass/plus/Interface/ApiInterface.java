package com.safaltaclass.plus.Interface;

import com.safaltaclass.plus.model.AboutUsResponse;
import com.safaltaclass.plus.model.AppVersionResponse;
import com.safaltaclass.plus.model.BatchList;
import com.safaltaclass.plus.model.CalendarResponse;
import com.safaltaclass.plus.model.CentreResponse;
import com.safaltaclass.plus.model.CourseResponseBody;
import com.safaltaclass.plus.model.DynamicVideoData;
import com.safaltaclass.plus.model.EnquiryRequest;
import com.safaltaclass.plus.model.EnquiryResponse;
import com.safaltaclass.plus.model.ForgetRequestBody;
import com.safaltaclass.plus.model.ForgetResponsebody;
import com.safaltaclass.plus.model.LoginRequestBody;
import com.safaltaclass.plus.model.LoginResponseBody;
import com.safaltaclass.plus.model.PosterImageResponse;
import com.safaltaclass.plus.model.RegisterRequestBody;
import com.safaltaclass.plus.model.RegisterResponseBody;
import com.safaltaclass.plus.model.SearchRequestBody;
import com.safaltaclass.plus.model.SubCourseRequestBody;
import com.safaltaclass.plus.model.TopicRequestBody;
import com.safaltaclass.plus.model.TopicResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiInterface {

    @POST("app-start/")
    Call<AppVersionResponse> getVersion();

    @POST("login/check")
    Call<LoginResponseBody> getLogin(@Body LoginRequestBody loginResponseBody);

    @POST("courses/list")
    Call<CourseResponseBody> getCourse();

    @POST("courses/list")
    Call<CourseResponseBody> getSubCourse(@Body SubCourseRequestBody subCourseRequestBody);

    @POST("courses/content")
    Call<TopicResponseBody> getData(@Body TopicRequestBody topicRequestBody);

    @POST("")
    Call<DynamicVideoData> getVideo(@Url String url);

    @POST("public/batches")
    Call<BatchList> getBatches();

    @POST("public/about-us")
    Call<AboutUsResponse> getAboutUs();

    @POST("public/centre")
    Call<CentreResponse> getCentres();

    @POST("public/dashboard-poster")
    Call<PosterImageResponse> getPoster();

    @POST("public/calendar")
    Call<CalendarResponse> getCalendar();

    @POST("public/enquiry")
    Call<EnquiryResponse> getEnquiry(@Body EnquiryRequest enquiryRequest);

    @POST("courses/content")
    Call<TopicResponseBody> getData(@Body SearchRequestBody searchRequestBody);

    @POST("public/password")
    Call<ForgetResponsebody> getForget(@Body ForgetRequestBody forgetRequestBody);

    @POST("public/register-student")
    Call<RegisterResponseBody> getRegister(@Body RegisterRequestBody registerRequestBody);
}
