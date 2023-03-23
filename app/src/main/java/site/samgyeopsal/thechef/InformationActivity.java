package site.samgyeopsal.thechef;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import site.samgyeopsal.thechef.common.UserPreferenceManager;
import site.samgyeopsal.thechef.databinding.ActivityInformationBinding;
import site.samgyeopsal.thechef.model.User;

/**
 * @filename InformationActivity
 * @author 최태승
 * @since 2023.03.21
 * 사용자 정보, 로그아웃
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.21	최태승		최초 생성
 * </pre>
 */

public class InformationActivity extends AppCompatActivity {

    private ActivityInformationBinding binding;
    private UserPreferenceManager userPreferenceManager;


    /*
     * onCreate() : ActivityInformationBinding을 inflate
     *              UserPreferenceManager 초기화
     *              initUi() 메서드 호출
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        binding = ActivityInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userPreferenceManager = UserPreferenceManager.getInstance(this);

        initUi();
    }


    /*
     * initUi() : UI 구성요소를 초기화하는 메서드.
     *            HomeButton, BackButton 클릭 이벤트 구현
     */
    private void initUi(){
        binding.homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             Intent intent = new Intent(InformationActivity.this,HomeActivity.class);
             intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
             startActivity(intent);
            }
        });

        binding.signOutButton.setOnClickListener(v -> {
            userPreferenceManager.setUser(null);

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            finishAffinity();
        });

        User user = userPreferenceManager.getUser();
        if (user != null){
            binding.nameTextView.setText(user.member.name);
        }

        binding.backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                onBackPressed();
            }
        });
    }

}
