package site.samgyeopsal.thechef;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.arjinmc.recyclerviewdecoration.RecyclerViewLinearItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import site.samgyeopsal.thechef.adapter.SalesHistoryItemAdapter;
import site.samgyeopsal.thechef.common.RetrofitManager;
import site.samgyeopsal.thechef.common.UserPreferenceManager;
import site.samgyeopsal.thechef.databinding.ActivitySalesHistoryBinding;
import site.samgyeopsal.thechef.model.Option;
import site.samgyeopsal.thechef.model.Product;
import site.samgyeopsal.thechef.model.SalesItem;
import site.samgyeopsal.thechef.model.Store;
import site.samgyeopsal.thechef.retrofit.SalesService;
import timber.log.Timber;

/**
 * @filename SalesHistoryActivity
 * @author 최태승
 * @since 2023.04.10
 * 판매 내역 화면
 *
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.04.10	최태승        최초 생성
 * </pre>
 */
public class SalesHistoryActivity extends BaseActivity {

    private ActivitySalesHistoryBinding binding;
    private final SalesService salesService = RetrofitManager.getInstance().salesService;
    private UserPreferenceManager userPreferenceManager;


    private final SalesHistoryItemAdapter adapter = new SalesHistoryItemAdapter();
    private final ArrayList<SalesItem> items = new ArrayList<>();

    private Call<List<SalesItem>> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySalesHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userPreferenceManager = UserPreferenceManager.getInstance(this);

        initUi();
    }

    private void initUi() {
        binding.titleTextView.setText(userPreferenceManager.getUser().store.getStoreName());
        binding.homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SalesHistoryActivity.this, HomeActivity.class);
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
                binding.titleContainer.setVisibility(view.GONE);
                binding.searchContainer.setVisibility(view.VISIBLE);
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
                binding.searchContainer.setVisibility(view.GONE);
                binding.titleContainer.setVisibility(view.VISIBLE);
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

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Timber.d("onItemSelected");

                setOrder();
                setQuery(binding.searchField.getEditText().getText().toString().trim());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(this::refresh);

        RecyclerViewLinearItemDecoration decoration = new RecyclerViewLinearItemDecoration.Builder(this)
                .color(ContextCompat.getColor(this, R.color.black12))
                .thickness((int) (getResources().getDisplayMetrics().density * 2))
                .create();
        binding.recyclerView.addItemDecoration(decoration);
        adapter.setOnItemClickListener((item) -> {
            Intent intent = new Intent(this, SalesDetailsActivity.class);
            intent.putExtra("item", item);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        binding.recyclerView.setAdapter(adapter);
        refresh();
    }

    /*
     * setQuery() : 검색어를 기반으로 필터링된 판매내역을 어댑터에 출력
     */
    private void setQuery(String query) {
        ArrayList<SalesItem> filteredItems = new ArrayList<>();

        if (query.isEmpty()) {
            filteredItems.addAll(items);
        } else {
            for (int i = 0; i < items.size(); i++) {
                SalesItem salesItem = items.get(i);
               if (salesItem.mName.contains(query)){
                   filteredItems.add(salesItem); break;
               }
            }
        }

        adapter.submitList(filteredItems);
    }

    private void setOrder() {
        int position = binding.spinner.getSelectedItemPosition();

        // 판매시간순
        if (position == 0) {
            items.sort(Comparator.comparingLong(o -> o.qDate));
            Collections.reverse(items);
        } else if (position == 1) {
            //TODO : 상품명, 판매금액으로 변경해야함

            items.sort(Comparator.comparing(o -> o.getList().get(0).getFpTitle()));

        } else {
            items.sort(Comparator.comparingLong(o -> o.oPrice));
            Collections.reverse(items);
        }
    }

    private void refresh() {
        if(call != null){
            call.cancel();
            call = null;
        }

        binding.swipeRefreshLayout.setRefreshing(true);
        Store store = userPreferenceManager.getUser().store;
        String sid = store.getSid();

        call = salesService.getSalesList(sid);
        Timber.d("sId: " + sid);

        call.enqueue(new Callback<List<SalesItem>>() {
            @Override
            public void onResponse(Call<List<SalesItem>> call, Response<List<SalesItem>> response) {
                SalesHistoryActivity.this.call = null;

                if (call.isCanceled()) return;
                if (response.isSuccessful()) {
                    items.clear();
                    items.addAll(response.body());

                    setOrder();
                    setQuery(binding.searchField.getEditText().getText().toString().trim());
                    binding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    onFailure(call, new Exception("Failed to get sales history"));
                }

            }

            @Override
            public void onFailure(Call<List<SalesItem>> call, Throwable t) {
                SalesHistoryActivity.this.call = null;
                if (call.isCanceled()) return;

                Timber.d(t);
                items.clear();


                setOrder();
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
}