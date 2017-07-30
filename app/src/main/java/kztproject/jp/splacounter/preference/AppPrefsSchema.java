package kztproject.jp.splacounter.preference;

import com.rejasupotaro.android.kvs.annotations.Key;
import com.rejasupotaro.android.kvs.annotations.Table;

@Table(name = "AppPrefsSchema")
public class AppPrefsSchema {

    @Key(name = "user_id") int userId;

    @Key(name = "user_name") String userName;
}
