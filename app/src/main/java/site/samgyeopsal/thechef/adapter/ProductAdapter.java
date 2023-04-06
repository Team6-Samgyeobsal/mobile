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

public class ProductAdapter extends ListAdapter<Order, ProductAdapter.ProductItemViewHolder> {

    public ProductAdapter() {
        super(new DiffUtil.ItemCallback<Order>() {
            @Override
            public boolean areItemsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
                return TextUtils.equals(oldItem.poId, newItem.poId);
            }

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

    @NonNull
    @Override
    public ProductItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemProductBinding binding = ItemProductBinding.inflate(inflater, parent, false);
        return new ProductItemViewHolder(binding);
    }

    /*
     * onBindViewHolder(ViewHo) : 각 아이템 뷰에 데이터를 바인딩하는 역할
     */
    @Override
    public void onBindViewHolder(@NonNull ProductItemViewHolder holder, int position) {
        Order order = getItem(position);
        ItemProductBinding binding = holder.binding;
        NumberFormat formatter = new DecimalFormat("#,###");

        binding.nameTextView.setText(order.fpTitle + " " + order.amount + "개");

        binding.optionsTextView.setText(order.poOption);
        binding.optionsTextView.setVisibility(
                binding.optionsTextView.getText().toString().trim().isEmpty() ? View.GONE : View.VISIBLE);

        binding.totalPriceTextView.setText(formatter.format(order.fpPrice * order.amount) + "원");   }


    static class ProductItemViewHolder extends RecyclerView.ViewHolder {
        public ItemProductBinding binding;

        public ProductItemViewHolder(@NonNull ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
