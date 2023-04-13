package site.samgyeopsal.thechef.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Comparator;
import java.util.function.Consumer;

import site.samgyeopsal.thechef.databinding.ItemSalesHistoryBinding;
import site.samgyeopsal.thechef.model.SalesItem;
/**
 * @filename SalesHistoryItemAdapter
 * @author 최태승
 * @since 2023.04.10
 * 판매 내역의 아이템 어댑터
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.04.10	최태승        최초 생성
 * </pre>
 */
public class SalesHistoryItemAdapter extends ListAdapter<SalesItem, SalesHistoryItemAdapter.SalesItemViewHolder> {

    private Consumer<SalesItem> onItemClickListener;
    private SalesItem salesItem;

    // 생성자에서 DiffUtil 사용을 위한 초기화
    public SalesHistoryItemAdapter() {
        super(new DiffUtil.ItemCallback<SalesItem>() {

            // 두 아이템이 같은지 확인하는 메서드
            @Override
            public boolean areItemsTheSame(@NonNull SalesItem oldItem, @NonNull SalesItem newItem) {
                return TextUtils.equals(oldItem.oId, newItem.oId);
            }

            // 두 컨텐츠가 같은지 확인하는 메서드
            @Override
            public boolean areContentsTheSame(@NonNull SalesItem oldItem, @NonNull SalesItem newItem) {
                return true;
            }
        });
    }

    // 클릭 리스너 설정 메서드
    public void setOnItemClickListener(Consumer<SalesItem> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    // 뷰 홀더를 생성하는 메서드
    @NonNull
    @Override
    public SalesItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemSalesHistoryBinding binding = ItemSalesHistoryBinding.inflate(inflater, parent, false);
        return new SalesItemViewHolder(binding);
    }

    /*
     * onBindViewHolder(ViewHo) : 각 아이템 뷰에 데이터를 바인딩하는 역할
     */

    @Override
    public void onBindViewHolder(@NonNull SalesItemViewHolder holder, int position) {
        SalesItem salesItem = getItem(position);
        ItemSalesHistoryBinding binding = holder.binding;

        // 아이템 클릭 시 이벤트 처리
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.accept(salesItem);
                }
            }
        });

        // 아이템 내용 설정
        binding.contentTextView.setText(salesItem.getList().get(0).getFpTitle());
        if(salesItem.getList().size() == 1){
            binding.contentTextView.setText(salesItem.getList().get(0).getFpTitle());
        } else {
            int count = salesItem.getList().size() -1;
            binding.contentTextView.setText(salesItem.getList().get(0).getFpTitle() + " 외 " + count + "개");
        }

        // 아이템 날짜 설정
        Context context = binding.getRoot().getContext();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(salesItem.qDate);

        Calendar now = Calendar.getInstance();

        // 오늘 날짜와 비교하여 날짜/시간 표시
        if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {
            binding.dateTextView.setText(DateUtils.formatDateTime(
                    context,
                    salesItem.qDate,
                    DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_ABBREV_TIME
            ));
        } else {
            binding.dateTextView.setText(DateUtils.formatDateTime(
                    context,
                    salesItem.qDate,
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL
            ));
        }
    }


    static class SalesItemViewHolder extends RecyclerView.ViewHolder {
        public ItemSalesHistoryBinding binding;

        public SalesItemViewHolder(@NonNull ItemSalesHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
