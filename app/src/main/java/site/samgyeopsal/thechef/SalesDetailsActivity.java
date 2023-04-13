package site.samgyeopsal.thechef;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.arjinmc.recyclerviewdecoration.RecyclerViewLinearItemDecoration;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import site.samgyeopsal.thechef.adapter.ProductAdapter;
import site.samgyeopsal.thechef.common.RetrofitManager;
import site.samgyeopsal.thechef.common.UserPreferenceManager;
import site.samgyeopsal.thechef.databinding.ActivitySalesDetailsBinding;
import site.samgyeopsal.thechef.model.OrderResponse;
import site.samgyeopsal.thechef.model.SalesItem;
import site.samgyeopsal.thechef.model.Product;
import site.samgyeopsal.thechef.retrofit.SalesService;
/**
 * @filename SalesDetailsActivity
 * @author 최태승
 * @since 2023.04.10
 * 판매 정보 상세 화면

 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.04.10	최태승        최초 생성
 * </pre>
 */
public class SalesDetailsActivity extends BaseActivity {

    private ActivitySalesDetailsBinding binding;
    private SalesItem item;
    private final SalesService service = RetrofitManager.getInstance().salesService;
    private UserPreferenceManager userPreferenceManager;


    // 액티비티 생성 시 호출되는 메서드
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Intent에서 판매 아이템 객체를 가져옴
        if (getIntent() != null) {
            item = getIntent().getParcelableExtra("item");
        }

        // 저장된 인스턴스 상태에서 판매 아이템 객체를 가져옴
        if (savedInstanceState != null) {
            item = savedInstanceState.getParcelable("item");
        }

        // 판매 아이템 객체가 없으면 액티비티 종료
        if (item == null) {
            finish();
            return;
        }

        // 레이아웃 바인딩 객체를 초기화하고, 뷰를 설정
        binding = ActivitySalesDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userPreferenceManager = UserPreferenceManager.getInstance(this);

        // 사용자 인터페이스 초기화
        initUi();

        // 판매 상세 정보를 가져오는 API 호출
        service.getSalesDetails(item.oId).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful()) {
                    bindData(response.body());
                } else {
                    onFailure(call, new Exception("Failed to get SalesDetails"));
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),
                        "정보를 가져오는데 실패하였습니다. 잠시 후 다시 시도해주세요.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 인스턴스 상태 저장 시 호출되는 메서드
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("item", item);
    }

    // 사용자 인터페이스를 초기화하는 메서드
    private void initUi() {
        binding.titleTextView.setText(userPreferenceManager.getUser().store.getStoreName());

        // 홈 버튼 클릭 리스너 설정
        binding.homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SalesDetailsActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // 뒤로 가기 버튼 클릭 리스너 설정
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /*
     * bindData : 데이터를 바인딩
     */
    private void bindData(OrderResponse response){
        binding.titleTextView.setText(response.fStoreName);
        binding.nameTextView.setText(response.fStoreName);

        // 주문 목록에 따라 summaryTextView의 텍스트 설정
        if (response.orders.size() == 1){
            binding.summaryTextView.setText(response.orders.get(0).fpTitle);
        } else {
            int count = response.orders.size() -1;
            binding.summaryTextView.setText(response.orders.get(0).fpTitle + "외 " + count + "개");
        }

        // 판매 날짜 표시
        binding.salesDateTextView.setText("주문일시 : " + DateUtils.formatDateTime(
                this,
                response.qrUsedDate,
                DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME)
        );
        binding.salesNumberTextView.setText(response.oId);


        // 리사이클러뷰에 아이템 간 구분선 추가
        RecyclerViewLinearItemDecoration decoration = new RecyclerViewLinearItemDecoration.Builder(this)
                .color(ContextCompat.getColor(this, R.color.black12))
                .thickness((int) (getResources().getDisplayMetrics().density * 1)) // 굵기
                .create();
        binding.recyclerView.addItemDecoration(decoration);

        // 제품 목록 어댑터를 생성하고 리사이클러뷰에 설정
        ProductAdapter adapter = new ProductAdapter();
        adapter.submitList(response.orders);
        binding.recyclerView.setAdapter(adapter);

        NumberFormat formatter = new DecimalFormat("#,###");

        // 가격, 할인금액, 총 금액
        binding.priceTextView.setText(formatter.format(response.oOriginPrice) + "원");
        binding.discountTextView.setText(formatter.format(response.oPrice - response.oOriginPrice) + "원");
        binding.totalPriceTextView.setText(formatter.format(response.oPrice) + "원");

        if (TextUtils.equals(response.pmCode, "1")) {
            binding.paymentMethodTextView.setText("가상계좌");
        } else {
            binding.paymentMethodTextView.setText("무통장입금");
        }

        binding.memoTextView.setText(response.oMemo);

    }
}