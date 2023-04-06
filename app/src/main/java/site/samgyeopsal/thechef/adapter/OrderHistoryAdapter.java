package site.samgyeopsal.thechef.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.function.Consumer;

import site.samgyeopsal.thechef.databinding.ItemOrderHistoryBinding;
import site.samgyeopsal.thechef.model.OrderUser;

/**
 * @filename OrderHistoryAdapter
 * @author 최태승
 * @since 2023.04.03
 * 주문 내역 화면 (OrderHistoryActivity)에서 사용되는 Adapter
 * 주문한 사용자를 출력하는 RecyclerView에서 사용됨
 *
 * <pre>
 * 수정일        	수정자       	수정내용
 * ----------  --------    ---------------------------
 * 2023.04.03   최태승        최초생성
 * </pre>
 */
public class OrderHistoryAdapter extends ListAdapter<OrderUser, OrderHistoryAdapter.QueueUserItemViewHolder> {

    private Consumer<OrderUser> onItemClickListener;
    private Consumer<OrderUser> onItemLongClickListener;

    public OrderHistoryAdapter() {
        super(new DiffUtil.ItemCallback<OrderUser>() {
            @Override
            public boolean areItemsTheSame(@NonNull OrderUser oldItem, @NonNull OrderUser newItem) {
                return false;
            }

            @Override
            public boolean areContentsTheSame(@NonNull OrderUser oldItem, @NonNull OrderUser newItem) {
                return false;
            }
        });
    }

    public void setOnItemClickListener(Consumer<OrderUser> onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(Consumer<OrderUser> onItemLongClickListener){
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    @Override
    public QueueUserItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemOrderHistoryBinding binding = ItemOrderHistoryBinding.inflate(inflater, parent, false);
        return new QueueUserItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull QueueUserItemViewHolder holder, int position) {
        OrderUser user = getItem(position);
        System.out.println(">>>>>>>>>>>>>>>>>" + getItem(position).toString());
        ItemOrderHistoryBinding binding = holder.binding;

        binding.getRoot().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(onItemClickListener != null){
                    onItemClickListener.accept(user);
                }
            }
        });

        binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemLongClickListener != null){
                    onItemLongClickListener.accept(user);
                }
                return false;
            }
        });

        binding.contentTextView.setText(user.mName); // 사용자 이름
        binding.contentTextView.setText(user.qid);
        System.out.println("::::::USER QID : " + user.qid);
        System.out.println(":::::USER NAME : " + user.mName);
        Calendar now = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(user.qDate);

        if (now.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                now.get(Calendar.MONTH) == date.get(Calendar.MONTH) &&
                now.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)){
            // 주문 날짜가 오늘인 경우 시간만 출력
            binding.dateTextView.setText(
                    new SimpleDateFormat("hh:mma", Locale.KOREA).format(date.getTime()));}
        else {
            // 주문 날짜가 오늘이 아닌 경우 날짜만 출력
            binding.dateTextView.setText(
                    new SimpleDateFormat("MM/dd", Locale.KOREA).format(date.getTime()));
        }
    }



    static class QueueUserItemViewHolder extends RecyclerView.ViewHolder{
        public ItemOrderHistoryBinding binding;

        public QueueUserItemViewHolder(@NonNull ItemOrderHistoryBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
