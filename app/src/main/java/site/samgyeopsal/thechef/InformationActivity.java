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
import site.samgyeopsal.thechef.model.Store;
import site.samgyeopsal.thechef.retrofit.StoreService;
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
    private StoreService storeService;
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

        storeService = RetrofitManager.getInstance().storeService;
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

        Store store = userPreferenceManager.getUser().store;

        String sid = store.getSid();

        fetchFundingInformation(sid);
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

    private void fetchFundingInformation(String sid) {
        storeService.getStore(sid).enqueue(new Callback<Store>() {
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                if (response.isSuccessful()) {
                    Store store = response.body();

                    //region Header
                    // 배경 이미지
                    // binding.backgroundImageView.setImageBitmap();

                    // 프로필 이미지
                    // binding.profileImageView.setImageBitmap();

                    binding.totalEmailTextView.setText(String.valueOf(store.totalEmail));
                    binding.priceTextView.setText(String.valueOf(store.totalPrice));
                    binding.nameTextView.setText(store.storeName);
                    binding.categoryTextView.setText(store.ctName);
                    //endregion

                    //region Contents
                    binding.dateTextView.setText(DateUtils.formatDateTime(
                            InformationActivity.this,
                            store.fDate,
                            DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE)
                    );

                    binding.totalEmailTextView2.setText(String.valueOf(store.totalEmail));
                    binding.totalPriceTextView.setText(String.valueOf(store.totalPrice));
                    binding.addressTextView.setText(store.tName);
                    //endregion
                } else {
                    Timber.w(">>>>>>>>>>>> Failed to fetch funding data: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                Timber.w(t);
            }
        });
    }
}
