package site.samgyeopsal.thechef;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.arjinmc.recyclerviewdecoration.RecyclerViewLinearItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import site.samgyeopsal.thechef.adapter.ProductAdapter;
import site.samgyeopsal.thechef.common.RetrofitManager;
import site.samgyeopsal.thechef.common.UserPreferenceManager;
import site.samgyeopsal.thechef.databinding.ActivityTakeOrderBinding;
import site.samgyeopsal.thechef.model.OrderResponse;
import site.samgyeopsal.thechef.model.OrderUser;
import site.samgyeopsal.thechef.retrofit.OrderService;
import timber.log.Timber;
/**
 * @filename TakeOrderActivity
 * @author 최태승
 * @since 2023.03.27
 * 주문 받기 화면
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.27	최태승        최초 생성
 * 2023.04.03   최태승        대기열 추가
 * </pre>
 */

public class TakeOrderActivity extends BaseActivity {

    private ActivityTakeOrderBinding binding;
    private OrderResponse orderResponse;
    private final OrderService orderService = RetrofitManager.getInstance().orderService;
    private UserPreferenceManager userPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 현재 액티비티에 연결된 intent 가져오기 (주문 정보를 받아옴)
        if (getIntent()!= null){
            orderResponse = getIntent().getParcelableExtra("order");
        }

        // 액티비티가 재생성될 때, 저장된 인스턴스 상태에서 주문 정보를 가져옴
        if (savedInstanceState != null){
            orderResponse = savedInstanceState.getParcelable("order");
        }

        // 주문 정보가 없으면 액티비티를 종료함
        if(orderResponse == null) {
            finish();
            return;
        }

        // 뷰 바인딩 객체를 초기화하고 화면에 표시
        binding = ActivityTakeOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userPreferenceManager = UserPreferenceManager.getInstance(this);

        initUi();
    }

    /*
     * onSaveInstanceState : 상태 저장 (주문 정보 저장)
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState){
        outState.putParcelable("order", orderResponse);
        super.onSaveInstanceState(outState);
    }

    /*
     * initUi : UI를 초기화
     */
    private void initUi() {
        binding.titleTextView.setText(userPreferenceManager.getUser().store.getStoreName());
        // 홈 버튼을 클릭시 홈 액티비티로 이동
        binding.homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakeOrderActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 주문 받기 클릭시, 서버의 api/queue/insertQueue 호출
        binding.orderTakeButton.setOnClickListener(v -> insertQueue());


        // View binding
        binding.nameTextView.setText(orderResponse.fStoreName);
        binding.userNameTextView.setText(orderResponse.mName);

        // 주문 아이템 개수에 따른 summaryTextView 표시
        if (orderResponse.orders.size() == 1) {
            binding.summaryTextView.setText(orderResponse.orders.get(0).fpTitle);
        } else {
            int count = orderResponse.orders.size() - 1;
            binding.summaryTextView.setText(orderResponse.orders.get(0).fpTitle + " 외 " + count + "개");
        }

        // 리사이클러뷰 항목 구분선 설정
        RecyclerViewLinearItemDecoration decoration = new RecyclerViewLinearItemDecoration.Builder(this)
                .color(ContextCompat.getColor(this, R.color.black12))
                .thickness((int) (getResources().getDisplayMetrics().density * 2))
                .create();
        binding.recyclerView.addItemDecoration(decoration);

        // 제품 어댑터 생성 및 리사이클러뷰에 설정
        ProductAdapter adapter = new ProductAdapter();
        adapter.submitList(orderResponse.orders);
        binding.recyclerView.setAdapter(adapter);

        // 가격 형식을 변환하여 텍스트 뷰에 표시
        NumberFormat formatter = new DecimalFormat("#,###");
        binding.priceTextView.setText(formatter.format(orderResponse.oOriginPrice) + "원");
        binding.memoTextView.setText(orderResponse.oMemo);
    }

    /*
     * insertQueue : 대기열에 주문 추가
     */
    private void insertQueue() {

        String qid = orderResponse.qid;
        System.out.println(":::::qid(insertQueue) : " + qid);

        // JSON 객체를 생성하고 qid를 추가
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("qid", qid);
        } catch (JSONException e){
            e.printStackTrace();
        }

        // JSON 객체를 바디로 변황
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString()
        );
        System.out.println("::::::body(insertQueue) : " + body);

        // 서버에 대기열 추가 요청
        orderService.insertQueue(body).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    // 대기열 목록 요청
                    orderService.getQueueList(userPreferenceManager.getUser().store.sid).enqueue(new Callback<List<OrderUser>>() {
                        @Override
                        public void onResponse(Call<List<OrderUser>> call, Response<List<OrderUser>> response) {
                            if (response.isSuccessful()){
                                // 대기열 크기 계산
                                int size = response.body().size();
                                JSONObject jsonObject = new JSONObject();
                                try{
                                    jsonObject.put("oid", orderResponse.oId);
                                    jsonObject.put("msg", "대기열 :" + size + "\\n"
                                    + "예상시간은 " + (size * 5) + "분 입니다.");
                                    // jsonObject.put("msg", "메세지 체크");
                                } catch (JSONException e){
                                    e.printStackTrace();
                                }

                                // JSON 객체를 바디로 변환
                                RequestBody body = RequestBody.create(
                                        MediaType.parse("application/json; charset=utf-8"),
                                        jsonObject.toString()
                                );

                                System.out.println("::::::::getQueueBody : " + jsonObject);

                                // 알림 전송 요청
                                orderService.sendNotification(body).enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        Timber.d("onResponse");
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Timber.e(t);
                                    }
                                });

                                // 주문 받기 완료 토스트 메시지 표시
                                Toast.makeText(
                                        getApplicationContext(),
                                        "주문 받기 완료",
                                        Toast.LENGTH_SHORT
                                ).show();;
                                finish();
                            } else {
                                Timber.d("Failed to get queue list.");
                                Timber.d(response.message());

                                Toast.makeText(
                                        getApplicationContext(),
                                        "주문 받기 실패",
                                        Toast.LENGTH_SHORT
                                ).show();
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<OrderUser>> call, Throwable t) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "주문 받기 완료",
                                    Toast.LENGTH_SHORT
                            ).show();
                            Timber.e(t);
                            finish();
                        }
                    });
                }else {
                    onFailure(call, new Exception(">>>>>>>> Failed to insert queue. (TakeOrderActivity) "));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Timber.d(t);
                Toast.makeText(
                        TakeOrderActivity.this,
                        "오류가 발생했습니다. 잠시 후 시도해주세요.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}