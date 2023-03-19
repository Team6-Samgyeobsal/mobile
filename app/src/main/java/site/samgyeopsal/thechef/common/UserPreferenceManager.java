package site.samgyeopsal.thechef.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import site.samgyeopsal.thechef.model.User;

/**
 * @filename UserPreferenceManager
 * @author 최태승
 * @since 2023.03.18
 * 사용자 데이터를 저장하고 불러옴
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.18	최태승		최초 생성
 * </pre>
 */
public class UserPreferenceManager {

    private static UserPreferenceManager instance;


    /*
     * Singleton pattern을 사용해 하나의 메서
     */
    public static UserPreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserPreferenceManager(context);
        }

        return instance;
    }


    /*
     * SharedPreferences : 안드로이드 앱에서 데이터를 저장하고 가져올 때 사용되는 인터페이스.
     * 키-값 형태로 데이터를 저장
     * 앱에서 SharedPreferences를 사용하면 앱이 시작할 때마다 데이터를 불러오거나 데이터를 저장하는 복잡한 코드를 작성하지 않아도 됨
     * 앱의 설정, 로그인 정보, 사용자 환경설정 등고 같은 간단한 데이터를 저장하는 데 적합
     *
     * 앱에서 SharedPreferences를 사용하려면 먼저 Context 객체를 얻어와야 함. Context 객체를 사용하여 SharedPreferences 객체를 만들고
     * 해당 SharedPreferences 객체를 사용하여 데이터를 저장하고 불러올 수 있음
     * -> 이 객체는 앱의 데이터를 저장할 수 있는 파일을 만들고, 그 파일에 데이터를 저장
     */
    private final SharedPreferences prefs;


    private UserPreferenceManager(Context context) {
        this.prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    /*
     * getUser : SharedPreferences에서 "profile" key를 사용하여 저장된 JSON 형식의 사용자 데이터를 가져와
     *          User 클래스로 변환함. 사용자 데이터가 존재하지 않을 경우 null 반환
     */
    public User getUser() {
        String json = prefs.getString("profile", null);
        if (json == null) return null;

        return new Gson().fromJson(json, User.class);
    }

    /*
     * setUser : SharedPreferences에서 "profile" 키를 사용하여 사용자 데이터를 저장
     * 사용자 데이터가 null이면 "profile" 키를 제거
     * null이 아니면 Gson 라이브러리를 사용하여 User 객체를 json 문자열로 변환하여 SharedPreference에 저장
     */

    public void setUser(User user) {
        if (user == null) {
            prefs.edit().remove("profile").apply();
        } else {
            prefs.edit().putString("profile", new Gson().toJson(user)).apply();
        }
    }
}
