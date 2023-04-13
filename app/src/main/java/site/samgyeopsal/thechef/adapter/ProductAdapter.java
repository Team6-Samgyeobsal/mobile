package site.samgyeopsal.thechef.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import site.samgyeopsal.thechef.databinding.ItemProductBinding;
import site.samgyeopsal.thechef.model.Order;
/**
 * @filename ProductAdapter
 * @author 최태승
 * @since 2023.04.03
 * 주문 내역 화면 (OrderHistoryActivity)에서 사용되는 Adapter
 * 주문한 사용자를 출력하는 RecyclerView에서 사용됨
 *
 * <pre>
 * 수정일        	수정자       	수정내용
 * ----------  --------    ---------------------------
 * 2023.04.03   최태승        최초생성
 * 2023.04.11
 * </pre>
 */
public class ProductAdapter extends ListAdapter<Order, ProductAdapter.ProductItemViewHolder> {

    // 생성자에서 DiffUtil.ItemCallback<Order>를 사용하여 목록의 변경 사항을 계산
    public ProductAdapter() {
        super(new DiffUtil.ItemCallback<Order>() {
            // 항목의 고유성 확인
            @Override
            public boolean areItemsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
                return TextUtils.equals(oldItem.poId, newItem.poId);
            }
            // 항목의 내용이 같은지 확인
            @Override
            public boolean areContentsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
                return oldItem.amount == newItem.amount &&
                        TextUtils.equals(oldItem.poOption, newItem.poOption) &&
                        TextUtils.equals(oldItem.fpId, newItem.fpId) &&
                        TextUtils.equals(oldItem.fId, newItem.fId) &&
                        oldItem.fpPrice == newItem.fpPrice &&
                        oldItem.fpOriginPrice == newItem.fpOriginPrice &&
                        TextUtils.equals(oldItem.fpTitle, newItem.fpTitle) &&
                        TextUtils.equals(oldItem.fpContent, newItem.fpContent);
            }
        });
    }

    /*
     * onCreateViewHolder :  새로운 뷰 홀더 객체를 생성
     */
    @NonNull
    @Override
    public ProductItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemProductBinding binding = ItemProductBinding.inflate(inflater, parent, false);
        return new ProductItemViewHolder(binding);
    }

    /*
     * onBindViewHolder : 각 아이템 뷰에 데이터를 바인딩하는 역할
     */
    @Override
    public void onBindViewHolder(@NonNull ProductItemViewHolder holder, int position) {
        Order order = getItem(position);
        ItemProductBinding binding = holder.binding;
        NumberFormat formatter = new DecimalFormat("#,###");

        // 제품 이름과 수량을 텍스트 뷰에 설정
        binding.nameTextView.setText(order.fpTitle + " " + order.amount + "개");

        // 옵션 정보를 텍스트 뷰에 설정, 옵션이 없는 경우 텍스트 뷰를 숨김
        binding.optionsTextView.setText("• " + order.poOption);
        binding.optionsTextView.setVisibility(
                TextUtils.isEmpty(order.poOption) ? View.GONE : View.VISIBLE);

        // 총 가격을 텍스트 뷰에 설정
        binding.totalPriceTextView.setText(formatter.format(order.fpPrice * order.amount) + "원");   }


    /**
     * ProductItemViewHolder
     * RecyclerView의 각 아이템을 위한 뷰 홀더 정의
     */
    static class ProductItemViewHolder extends RecyclerView.ViewHolder {
        public ItemProductBinding binding;

        // 생성자에서 ItemProductBinding 객체를 초기화하고, 뷰 홀더의 루트 뷰를 전달
        public ProductItemViewHolder(@NonNull ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
