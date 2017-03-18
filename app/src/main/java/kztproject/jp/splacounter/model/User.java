package kztproject.jp.splacounter.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by k-seito on 2017/03/18.
 */

public class User {

    @SerializedName("id")
    public int id;

    @SerializedName("full_name")
    public String fullName;

}
