package site.samgyeopsal.thechef;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import site.samgyeopsal.thechef.common.RetrofitManager;
import site.samgyeopsal.thechef.common.UserPreferenceManager;
import site.samgyeopsal.thechef.databinding.ActivityInformationBinding;
import site.samgyeopsal.thechef.model.Funding;
import site.samgyeopsal.thechef.retrofit.FundingService;
import timber.log.Timber;

/**
 * @author 최태승
 * @filename InformationActivity
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
    private FundingService fundingService;
    private UserPreferenceManager userPreferenceManager;


    /*
     * onCreate() : ActivityInformationBinding을 inflate
     *              UserPreferenceManager 초기화
     *              initUi() 메서드 호출
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        WindowInsetsControllerCompat windowInsetsControllerCompat =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsControllerCompat != null) {
            windowInsetsControllerCompat.setAppearanceLightNavigationBars(true); // 네비게이션 바를 밝게
        }

        binding = ActivityInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fundingService = RetrofitManager.getInstance().fundingService;
        userPreferenceManager = UserPreferenceManager.getInstance(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.contentContainer, new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat windowInsets) {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()); // 시스템 바 차지하는 공간 개선

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.backButton.getLayoutParams();
                params.topMargin = insets.top;
                binding.backButton.setLayoutParams(params);

                v.setPadding(
                        v.getPaddingLeft(),
                        v.getPaddingTop(),
                        v.getPaddingRight(),
                        insets.bottom
                );

                return WindowInsetsCompat.CONSUMED; // 다른 뷰들이 이벤트를 처리할 수 없도록
            }
        });

        initUi();
        fetchFundingInformation(10);
    }

    /*
     * initUi() : UI 구성요소를 초기화하는 메서드.
     *            HomeButton, BackButton 클릭 이벤트 구현
     */
    private void initUi() {
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                onBackPressed();
            }
        });

        binding.signOutButton.setOnClickListener(v -> {
            userPreferenceManager.setUser(null);

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            finishAffinity();
        });
    }

    private void fetchFundingInformation(long id) {
        fundingService.getFunding(id).enqueue(new Callback<Funding>() {
            @Override
            public void onResponse(Call<Funding> call, Response<Funding> response) {
                if (response.isSuccessful()) {
                    Funding funding = response.body();

                    //region Header
                    // 배경 이미지
                    // binding.backgroundImageView.setImageBitmap();

                    // 프로필 이미지
                    // binding.profileImageView.setImageBitmap();

                    binding.totalEmailTextView.setText(String.valueOf(funding.totalEmail));
                    binding.priceTextView.setText(String.valueOf(funding.totalPrice));
                    binding.nameTextView.setText(funding.storeName);
                    binding.categoryTextView.setText(funding.ctName);
                    //endregion

                    //region Contents
                    binding.dateTextView.setText(DateUtils.formatDateTime(
                            InformationActivity.this,
                            funding.date,
                            DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE)
                    );

                    binding.totalEmailTextView2.setText(String.valueOf(funding.totalEmail));
                    binding.totalPriceTextView.setText(String.valueOf(funding.totalPrice));
                    binding.addressTextView.setText(funding.tName);
                    //endregion
                } else {
                    Timber.w("Failed to fetch funding data: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Funding> call, Throwable t) {
                Timber.w(t);
            }
        });
    }
}
