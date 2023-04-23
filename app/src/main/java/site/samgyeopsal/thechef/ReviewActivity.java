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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.arjinmc.recyclerviewdecoration.RecyclerViewLinearItemDecoration;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import site.samgyeopsal.thechef.adapter.ReviewAdapter;
import site.samgyeopsal.thechef.common.RetrofitManager;
import site.samgyeopsal.thechef.common.UserPreferenceManager;
import site.samgyeopsal.thechef.databinding.ActivityReviewBinding;
import site.samgyeopsal.thechef.databinding.DialogReviewReplyBinding;
import site.samgyeopsal.thechef.model.Review;
import site.samgyeopsal.thechef.model.ReviewResponse;
import site.samgyeopsal.thechef.model.Store;
import site.samgyeopsal.thechef.retrofit.ReviewService;
import site.samgyeopsal.thechef.retrofit.StoreService;
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
    private ReviewAdapter adapter;

    private ActivityReviewBinding binding;
    private final StoreService storeService = RetrofitManager.getInstance().storeService;
    private final ReviewService reviewService = RetrofitManager.getInstance().reviewService;
    private ArrayList<Review> reviews = new ArrayList<>();
    private Call<ReviewResponse> call;
    public UserPreferenceManager userPreferenceManager;
    private String replyProfileUrl = null;




    /*
     * onCreate() : ActivityReviewBinding을 inflate
     *              initUi() 메서드 호출
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Review onCreate ");

        binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userPreferenceManager = UserPreferenceManager.getInstance(this);
        adapter =new ReviewAdapter(userPreferenceManager);



        initUi();

    }

    /*
     * initUi() : UI 구성요소를 초기화하는 메서드.
     *            HomeButton, BackButton 클릭 이벤트 구현
     */

    private void initUi() {
        binding.titleTextView.setText(userPreferenceManager.getUser().store.getStoreName());
        // 홈 버튼
        System.out.println(":::::::initUI");
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
                binding.titleContainer.setVisibility(view.VISIBLE);

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

        // 선 굵기
        RecyclerViewLinearItemDecoration decoration = new RecyclerViewLinearItemDecoration.Builder(this)
                .color(ContextCompat.getColor(this, R.color.black12))
                .thickness((int) (getResources().getDisplayMetrics().density * 1))
                .create();
        binding.recyclerView.addItemDecoration(decoration);
        adapter.setOnItemClickListener(this::writeReply);
        binding.recyclerView.setAdapter(adapter);

        storeService.getStore(userPreferenceManager.getUser().store.sid).enqueue(new Callback<Store>() {
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                if (response.isSuccessful()){
                    replyProfileUrl = response.body().sThumbUrl;
                } refresh();
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                refresh();
            }
        });
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
                if (review.rContent.contains(query)) {
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


        Store s = userPreferenceManager.getUser().store;
        System.out.println("Store = "+s);
        String sid = s.getSid();


        // fid 받아오는법 알아보기
        call = reviewService.getReviews(sid, 1, "STORE");
        System.out.println("::::::::call :::::::: " + call);
        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                ReviewActivity.this.call = null;

                if (call.isCanceled()) return;
                if (response.isSuccessful()) {
                    reviews.clear();
                    ReviewResponse responseBody = response.body();
                    if (responseBody != null) {
                        reviews.addAll(responseBody.reviews);
                    } else {
                        reviews.addAll(Collections.emptyList());
                    }

                    for (Review review : reviews){
                        review.reProfile = replyProfileUrl;
                    }

                    setQuery(binding.searchField.getEditText().getText().toString().trim());
                    binding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    onFailure(call, new Exception("Failed to get reviews."));
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
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


    public void writeReply(Review review){
        DialogReviewReplyBinding binding = DialogReviewReplyBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(binding.getRoot())
                .create();

        // 내용, 날짜 바인딩
        binding.contentTextView.setText(review.rContent);
        binding.dateTextView.setText(review.rDate);

        if (review.reContent != null && !review.reContent.isEmpty()){
            binding.replyField.getEditText().append(review.reContent);
            binding.positiveButton.setText("수정");
            binding.negativeButton.setText("삭제");
            binding.negativeButton.setOnClickListener(v -> {
                writeReply(review,"");
                dialog.dismiss();
            });
        } else {
            binding.negativeButton.setOnClickListener(v -> {
                dialog.dismiss();
            });
        }

        binding.positiveButton.setOnClickListener(v -> {
            writeReply(review, binding.replyField.getEditText().getText().toString().trim());
            dialog.dismiss();
            });

        dialog.show();

    }

    private void writeReply(Review review, String message){

        // 로그인 요청에 사용할 JSon 객체 생성
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("recontent", message);
            jsonObject.put("fid", userPreferenceManager.getUser().store.getSid());
            jsonObject.put("memail", review.mEmail);
            jsonObject.put("rtype", "STORE");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // json 객체를 ResponseBody로 반환
        RequestBody body = RequestBody.create(

                MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString()
        );

        reviewService.replyToReview(userPreferenceManager.getUser().store.getSid(), body).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    if(TextUtils.isEmpty(review.reContent)){
                        if (!message.isEmpty()){
                            Toast.makeText(
                                    getApplicationContext(),
                                    "답글이 등록되었습니다.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    } else {
                        if (message.isEmpty()){
                            Toast.makeText(
                                    getApplicationContext(),
                                    "답글이 삭제되었습니다.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "답글이 수정되었습니다.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                    refresh();
                } else {
                    Timber.d(response.message());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) { Timber.d(t);}

        });

        System.out.println("::::::body::::::" + body);
        refresh();
    }
}