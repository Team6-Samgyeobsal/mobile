package site.samgyeopsal.thechef;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.arjinmc.recyclerviewdecoration.RecyclerViewLinearItemDecoration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import site.samgyeopsal.thechef.BaseActivity;
import site.samgyeopsal.thechef.adapter.ReviewAdapter;
import site.samgyeopsal.thechef.common.RetrofitManager;
import site.samgyeopsal.thechef.databinding.ActivityReviewBinding;
import site.samgyeopsal.thechef.databinding.ItemReviewBinding;
import site.samgyeopsal.thechef.model.Review;
import site.samgyeopsal.thechef.retrofit.ReviewService;
import timber.log.Timber;

/**
 * @author 최태승
 * @filename ReviewActivity
 * @since 2023.03.22
 * 리뷰
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.22	최태승		최초 생성
 * </pre>
 */

public class ReviewActivity extends BaseActivity {
    private ReviewAdapter adapter = new ReviewAdapter();

    private ActivityReviewBinding binding;
    private final ReviewService reviewService = RetrofitManager.getInstance().reviewService;
    private ArrayList<Review> reviews = new ArrayList<>();
    private Call<List<Review>> call;

    /*
     * onCreate() : ActivityReviewBinding을 inflate
     *              initUi() 메서드 호출
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initUi();
    }

    /*
     * initUi() : UI 구성요소를 초기화하는 메서드.
     *            HomeButton, BackButton 클릭 이벤트 구현
     */

    private void initUi() {
        // 홈 버튼
        binding.homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReviewActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // 뒤로가기 버튼
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // 검색 버튼
        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.titleContainer.setVisibility(view.GONE);
                binding.searchContainer.setVisibility(view.VISIBLE);
                binding.searchField.getEditText().requestFocus();
                showKeyboard(binding.searchField.getEditText());
            }
        });

        // 닫기 버튼
        binding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                binding.searchField.getEditText().clearFocus(); // 포커스 제거 (사용자 편의성 향상)
                binding.searchField.getEditText().setText("");
                binding.searchContainer.setVisibility(view.GONE);
                binding.searchContainer.setVisibility(view.VISIBLE);

            }
        });

        /*
         * 검색 필드의 텍스트가 변경될 때마다 이를 감지하고, 변경된 텍스트를 처리
         */

        binding.searchField.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setQuery(binding.searchField.getEditText().getText().toString().trim());

            }
        });

        /*
         * 검색 입력창에 대한 에디터 액션 리스너 설정
         */
        binding.searchField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            /*
             * onEditorAction(이벤트가 발생한 TextView, 발생한 액션의 ID, 액션과 관련된 키 이벤트)
             */
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    v.clearFocus();
                    return true;
                }
                return false;
            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(this::refresh);

        RecyclerViewLinearItemDecoration decoration = new RecyclerViewLinearItemDecoration.Builder(this)
                .color(ContextCompat.getColor(this, R.color.black12))
                .thickness((int) (getResources().getDisplayMetrics().density * 8))
                .create();
        binding.recyclerView.addItemDecoration(decoration);

        binding.recyclerView.setAdapter(adapter);
        refresh();


    }

    /*
     * setQuery() : 검색어를 기반으로 필터링된 리뷰 목록을 어댑터에 출력
     */
    private void setQuery(String query) {
        ArrayList<Review> filteredReviews = new ArrayList<>();

        if (query.isEmpty()) {
            filteredReviews.addAll(reviews);
        } else {
            for (int i = 0; i < reviews.size(); i++) {
                Review review = reviews.get(i);
                if (review.content.contains(query)) {
                    filteredReviews.add(review);
                }
            }
        }

        adapter.submitList(filteredReviews);


    }

    private void refresh() {
        if (call != null) {
            call.cancel();
            call = null;
        }

        binding.swipeRefreshLayout.setRefreshing(true);

        call = reviewService.getReviews(1, 1, "funding");
        call.enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                ReviewActivity.this.call = null;

                if (call.isCanceled()) return;
                if (response.isSuccessful()) {
                    reviews.clear();
                    List<Review> responseBody = response.body();
                    if (responseBody != null) {
                        reviews.addAll(responseBody);
                    } else {
                        reviews.addAll(Collections.emptyList());
                    }

                    setQuery(binding.searchField.getEditText().getText().toString().trim());
                    binding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    onFailure(call, new Exception("Failed to get reviews."));
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                ReviewActivity.this.call = null;
                if (call.isCanceled()) return;

                Timber.d(t);

                reviews.clear();

                setQuery(binding.searchField.getEditText().getText().toString().trim());
                binding.swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    @Override
    public void onBackPressed(){
        if (binding.searchContainer.getVisibility() == View.VISIBLE){
            binding.closeButton.performClick(); return;
        } super.onBackPressed();
    }






}