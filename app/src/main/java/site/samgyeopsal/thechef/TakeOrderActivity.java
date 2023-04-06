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
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import site.samgyeopsal.thechef.adapter.ProductAdapter;
import site.samgyeopsal.thechef.common.RetrofitManager;
import site.samgyeopsal.thechef.databinding.ActivityTakeOrderBinding;
import site.samgyeopsal.thechef.model.Option;
import site.samgyeopsal.thechef.model.OrderResponse;
import site.samgyeopsal.thechef.model.Product;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 현재 액티비티에 연결된 intent 가져오기
        if (getIntent()!= null){
            orderResponse = getIntent().getParcelableExtra("order");
        }

        if (savedInstanceState != null){
            orderResponse = savedInstanceState.getParcelable("order");
        }

        if(orderResponse == null) {
            finish();
            return;
        }

        binding = ActivityTakeOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initUi();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState){
        outState.putParcelable("order", orderResponse);
        super.onSaveInstanceState(outState);
    }

    private void initUi() {
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
        binding.orderTakeButton.setOnClickListener(v -> {
            // TODO : qid 가변값으로 추후 반드시 수정
            String qid = orderResponse.qid;
            System.out.println("::::::::::qid : " + qid);

            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("qid", qid);
            } catch (JSONException e){
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    jsonObject.toString());
            System.out.println("::::::::::body::: : " + body);

            orderService.insertQueue(body).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()){
                        // 성공시 Activity 종료
                        finish();
                    } else {
                        onFailure(call, new Exception(">>>>> Failed to insert queue. (TakeOrderActivity) "));
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
        });



        // View binding
        binding.nameTextView.setText(orderResponse.fStoreName);
        binding.userNameTextView.setText(orderResponse.mName);

        if (orderResponse.orders.size() == 1) {
            binding.summaryTextView.setText(orderResponse.orders.get(0).fpTitle);
        } else {
            int count = orderResponse.orders.size() - 1;
            binding.summaryTextView.setText(orderResponse.orders.get(0).fpTitle + " 외 " + count + "개");
        }


        RecyclerViewLinearItemDecoration decoration = new RecyclerViewLinearItemDecoration.Builder(this)
                .color(ContextCompat.getColor(this, R.color.black12))
                .thickness((int) (getResources().getDisplayMetrics().density * 2))
                .create();
        binding.recyclerView.addItemDecoration(decoration);

        ProductAdapter adapter = new ProductAdapter();
        adapter.submitList(orderResponse.orders);
        binding.recyclerView.setAdapter(adapter);

        NumberFormat formatter = new DecimalFormat("#,###");
        binding.priceTextView.setText(formatter.format(orderResponse.oOriginPrice) + "원");
        binding.memoTextView.setText(orderResponse.oMemo);
    }
}