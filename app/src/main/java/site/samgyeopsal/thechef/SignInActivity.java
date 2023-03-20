package site.samgyeopsal.thechef;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import site.samgyeopsal.thechef.common.RetrofitManager;
import site.samgyeopsal.thechef.common.UserPreferenceManager;
import site.samgyeopsal.thechef.databinding.ActivitySignInBinding;
import site.samgyeopsal.thechef.model.User;
import site.samgyeopsal.thechef.retrofit.AuthService;
import timber.log.Timber;
/**
 * @filename SignInActivity
 * @author 최태승
 * @since 2023.03.20
 * 로그인
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.20	최태승		최초 생성
 * </pre>
 */
public class SignInActivity extends BaseActivity {

    private ActivitySignInBinding binding;

    private UserPreferenceManager userPreferenceManager;
    private AuthService authService;



    /*
     * onCreate() : ActivitySignInBinding을 인플레이트하여 SignInActivity의 뷰를 설정하고,
     *              UserPreferenceManager와 AuthService 인스턴스를 초기화함.
     *              initUi() 메서드를 호출하여 UI를 초기화함.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userPreferenceManager = UserPreferenceManager.getInstance(this);
        authService = RetrofitManager.getInstance().authService;

        initUi();
    }

    /*
     * initUi() : toolbar의 NavigationOnClickListener를 등록함.
     *            idTextField와 passwordTextField의 텍스트를 모니터링하는 TextWatcher를 등록,
     *            텍스트가 변경될 때마다 signInButton의 활성화 여부를 설정
     */
    private void initUi() {

        // 뒤로가기 버튼 클릭시 뒤로감
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Text 내용을 변경할 때 발생하는 이벤트 감지
        for (TextInputLayout field : Arrays.asList(binding.idTextField, binding.passwordTextField)) {
            field.getEditText().addTextChangedListener(new TextWatcher() {

                /*
                 * beforeTextChanged() : EditText에 입력하기 전에 호출되는 메서드
                 *                       문자열의 변경 전 상태, 변경 전 문자열의 시작 위치와 길이를 나타냄
                 */
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                /*
                 * onTextChanged() : 사용자가 입력하는 동안 EditText의 상태를 업데이트
                 */

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                /*
                 * afterTextChanged() : TextInputLayout의 EditText 내용을 가져와서,
                 *                      'id', 'password' 변수에 저장.
                 */

                @Override
                public void afterTextChanged(Editable s) {
                    String id = binding.idTextField.getEditText().getText().toString().trim();
                    String password = binding.passwordTextField.getEditText().getText().toString().trim();

                    // id, password가 입력되었을 때만 로그인 버튼이 활성화 되도록 설정
                    binding.signInButton.setEnabled(!id.isEmpty() && !password.isEmpty());
                }
            });
        }

        // passwordTextField에서 키보드의 'DONE' 버튼을 누르면 signIn() 메서드를 호출
        binding.passwordTextField.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                signIn();
                return true;
            }

            return false;
        });

        // 클릭 시 signIn() 메서드 호출
        binding.signInButton.setOnClickListener(v -> {
            signIn();
        });
    }

    /*
     * onBackPressed() : 사용자가 뒤로 가기 버튼을 눌렀을 때 호출됨.
     *                   현재 화면을 종료하고 이전 화면으로 돌아감.
     */

    @Override
    public void onBackPressed() {
        // 로딩중일때는 뒤로가기가 안 됨
        if (binding.progressView.getVisibility() == View.VISIBLE) return;

        super.onBackPressed();
    }

    private void signIn() {
        // 로딩중일땐 로그인 요청 중단
        if (binding.progressView.getVisibility() == View.VISIBLE) return;

        hideKeyboard(); // 키보드 숨김

        binding.progressView.setVisibility(View.VISIBLE); // 로딩창

        // id, password 가져오기
        String id = binding.idTextField.getEditText().getText().toString().trim();
        String password = binding.passwordTextField.getEditText().getText().toString().trim();

        // 로그인 요청에 사용할 json 객체 생성
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", id);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // json 객체를 RequestBody로 변환
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString());

        // 로그인 요청 보내기
        authService.signIn(body).enqueue(new Callback<User>() {

            // 로그인 요청이 성공한 경우 호출되는 콜백 메서드
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // 응답이 성공적으로 오면 로그인 정보를 저장
                if (response.isSuccessful()) {
                    userPreferenceManager.setUser(response.body());

                    // 홈 화면으로 이동
                    Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                    startActivity(intent);

                    finishAffinity(); // 이전에 열려있던 액티비티 모두 종료

                } else {
                    onFailure(call, new Exception("로그인 API 호출 실패"));
                }
            }
            /*
             * onFailure() : 로그인 요청이 실패했을 때 호출되는 콜백 메서드
             */

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Timber.d(t);

                binding.progressView.setVisibility(View.GONE); // 로딩창 숨기기

                Toast.makeText(
                        getApplicationContext(),
                        "아이디, 비밀번호를 다시 확인해 주세요.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}