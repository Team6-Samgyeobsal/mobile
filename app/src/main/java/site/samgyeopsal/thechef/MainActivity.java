package site.samgyeopsal.thechef;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.core.splashscreen.SplashScreen;
import site.samgyeopsal.thechef.common.UserPreferenceManager;
import site.samgyeopsal.thechef.databinding.ActivityMainBinding;
/**
 * @filename MainActivity
 * @author 최태승
 * @since 2023.03.18
 * 앱의 메인 화면을 나타냄
 * Activity가 생성될 때 필요한 초기화 작업을 수행
 * UI 요소에 이벤트 리스너를 등록하여 사용자와 상호작용
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.18	최태승		최초 생성
 * </pre>
 */
public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding; // MainActivity 레이아웃의 View 참조


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this); // 스플래시 화면 설치

        super.onCreate(savedInstanceState); // 상위 클래스의 onCreate() 메서드 호출


        if (UserPreferenceManager.getInstance(this).getUser() != null) {

            // 사용자가 로그인되어 있으면, HomeActivity로 이동. 애니메이션 효과를 제거하고 현재 Activity 종료
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

            finish();
            overridePendingTransition(0, 0);
            return;
        }

        // ActivityMainBinding 클래스를 사용하여 MainActivity 레이아웃을 화면에 표시
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // MainActivity의 UI 초기화
        initUi();
    }

    /*
     * initUi() : MainActivity의 UI를 초기화함
     * onCreate()에서 호출됨
     */
    private void initUi() {
        // '아이디로 로그인" 버튼에 클릭 리스너를 등록함
        binding.signInWithIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 시 SignInActivity 액티비티로 이동
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // 스택에 있는 액티비티를 그대로 사용하여 빠른 화면 전환이 가능
                startActivity(intent);
            }
        });
    }
}