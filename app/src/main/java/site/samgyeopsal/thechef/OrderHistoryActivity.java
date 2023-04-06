package site.samgyeopsal.thechef;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.arjinmc.recyclerviewdecoration.RecyclerViewLinearItemDecoration;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import site.samgyeopsal.thechef.adapter.OrderHistoryAdapter;
import site.samgyeopsal.thechef.common.RetrofitManager;
import site.samgyeopsal.thechef.common.UserPreferenceManager;
import site.samgyeopsal.thechef.databinding.ActivityOrderHistoryBinding;
import site.samgyeopsal.thechef.model.OrderResponse;
import site.samgyeopsal.thechef.model.OrderUser;
import site.samgyeopsal.thechef.model.Store;
import site.samgyeopsal.thechef.retrofit.OrderService;
import timber.log.Timber;

/**
 * @author
 * 주문 내역 화면
 * <p>
 * 서버에서 데이터를 가져오는 함수를 제외한 나머지는 ReviewActivity 와 동일하다.
 */
public class OrderHistoryActivity extends BaseActivity {
    private final OrderHistoryAdapter adapter = new OrderHistoryAdapter();

    private ActivityOrderHistoryBinding binding;

    private OrderResponse orderResponse;
    private OrderUser orderUser;
    private final OrderService orderService = RetrofitManager.getInstance().orderService;
    private final ArrayList<OrderUser> users = new ArrayList<>();

    // 서버에서 주문내역을 가져올 때, 필요한 Parameter
    // TODO: FID 는 고정이 아닌 가변이 되도록 차후 수정이 필요함

    private UserPreferenceManager userPreferenceManager;



    private Call<List<OrderUser>> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        System.out.println("@@@@@@@@@@@@@@");

        userPreferenceManager = UserPreferenceManager.getInstance(this);
        Store store = userPreferenceManager.getUser().store;
        String sid = store.getSid();
        System.out.println("::::::SID:::::" + sid);


        //private final String fid = userPreferenceManager.getUser().store.sid;
        initUi();
    }


    private void initUi() {
        binding.homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderHistoryActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.titleContainer.setVisibility(View.GONE);
                binding.searchContainer.setVisibility(View.VISIBLE);
                binding.searchField.getEditText().requestFocus();

                showKeyboard(binding.searchField.getEditText());
            }
        });

        binding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();

                binding.searchField.getEditText().clearFocus(); // 포커스 제거 (사용자 편의성 향상)
                binding.searchField.getEditText().setText("");
                binding.searchContainer.setVisibility(View.GONE);
                binding.titleContainer.setVisibility(View.VISIBLE);
            }
        });

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

        binding.searchField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        adapter.setOnItemClickListener(this::showNotificationDialog);
        adapter.setOnItemLongClickListener(this::showRemoveDialog);
        binding.recyclerView.setAdapter(adapter);
        refresh();
    }

    private void setQuery(String query) {
        ArrayList<OrderUser> filteredReviews = new ArrayList<>();

        if (query.isEmpty()) {
            filteredReviews.addAll(users);
        } else {
            for (int i = 0; i < users.size(); i++) {
                OrderUser user = users.get(i);
                if (user.mName.contains(query)) {
                    filteredReviews.add(user);
                }
            }
        }

        adapter.submitList(filteredReviews);
    }

    private void refresh() {
        // 현재 새로고침 중이면 취소
        if (call != null) {
            call.cancel();
            call = null;
        }

        binding.swipeRefreshLayout.setRefreshing(true);
        Store store = userPreferenceManager.getUser().store;
        String sid = store.getSid();
        String sname = store.getStoreName();
        System.out.println("::::::SID:::::" + sid);
        System.out.println("::::::SNAME:::::" + sname);



        call = orderService.getQueueList(sid);
        call.enqueue(new Callback<List<OrderUser>>() {
            @Override
            public void onResponse(Call<List<OrderUser>> call, Response<List<OrderUser>> response) {
                OrderHistoryActivity.this.call = null;

                if (call.isCanceled()) return;
                if (response.isSuccessful()) {
                    users.clear();
                    users.addAll(response.body());
                    setQuery(binding.searchField.getEditText().getText().toString().trim());
                    binding.swipeRefreshLayout.setRefreshing(false);

                } else {
                    onFailure(call, new Exception("Failed to get reviews."));
                }
            }

            @Override
            public void onFailure(Call<List<OrderUser>> call, Throwable t) {
                OrderHistoryActivity.this.call = null;
                if (call.isCanceled()) return;

                Timber.d(t);

                users.clear();

                setQuery(binding.searchField.getEditText().getText().toString().trim());
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (binding.searchContainer.getVisibility() == View.VISIBLE) {
            binding.closeButton.performClick();
            return;
        }
        super.onBackPressed();
    }

    /**
     * 대기열 알림 다이얼로그를 출력한다.
     *
     * @param user 주문한 사용자 정보
     */
    private void showNotificationDialog(OrderUser user) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("대기열 알림")
                .setMessage("알림을 보내시겠습니까?")
                .setNegativeButton("아니오", null)
                .setPositiveButton("보냅니다", (dialog, which) -> {

                })
                .show();
    }

    /**
     * 대기열 삭제 다이얼로그를 출력
     * 삭제시 api/queue/userQrCode 를 호출
     *
     * @param user 주문한 사용자 정보
     */
    private void showRemoveDialog(OrderUser user) {
        userPreferenceManager = UserPreferenceManager.getInstance(this);
        new MaterialAlertDialogBuilder(this)
                .setTitle("대기열 삭제")
                .setMessage("고객이 도착하여 삭제하시겠습니까?")
                .setNegativeButton("아니오", null)
                .setPositiveButton("삭제합니다", (dialog, which) -> {
                    // TODO: QID 는 고정이 아닌 가변이 되도록 차후 수정이 필요함
                    String qid = user.qid;
                    System.out.println("ORDER: qid:::::::" + qid);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("qid", qid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestBody body = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            jsonObject.toString());

                    System.out.println("::::::::::::getQueue Body : " + body);

                    orderService.useQrCode(body).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                refresh();
                            } else {
                                onFailure(call, new Exception("Failed to insert queue."));
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Timber.d(t);

                            Toast.makeText(
                                    OrderHistoryActivity.this,
                                    "오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
                })
                .show();
    }
}