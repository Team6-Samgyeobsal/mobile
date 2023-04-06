package site.samgyeopsal.thechef.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;

import site.samgyeopsal.thechef.databinding.ItemReviewBinding;
import site.samgyeopsal.thechef.model.Review;

public class ReviewAdapter extends ListAdapter<Review, ReviewAdapter.ReviewItemViewHolder> {


    private Consumer<Review>  onItemClickListener;

    public ReviewAdapter() {
        super(new DiffUtil.ItemCallback<Review>() {
            @Override
            public boolean areItemsTheSame(@NonNull Review oldItem, @NonNull Review newItem) {
                return false;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Review oldItem, @NonNull Review newItem) {
                return oldItem.rScore == newItem.rScore &&
                        TextUtils.equals(oldItem.rType, newItem.rType) &&
                        TextUtils.equals(oldItem.rImageUrl, newItem.rImageUrl) &&
                        TextUtils.equals(oldItem.mEmail, newItem.mEmail) &&
                        TextUtils.equals(oldItem.rDate, newItem.rDate) &&
                        TextUtils.equals(oldItem.rContent, newItem.rContent) &&
                        TextUtils.equals(oldItem.reContent, newItem.reContent);
            }
        });
    }

    public void setOnItemClickListener(Consumer<Review> onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ReviewItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemReviewBinding binding = ItemReviewBinding.inflate(inflater, parent, false);
        return new ReviewItemViewHolder(binding);
    }

    /*
     * onBindViewHolder(ViewHo) : 각 아이템 뷰에 데이터를 바인딩하는 역할
     */

    @Override
    public void onBindViewHolder(@NonNull ReviewItemViewHolder holder, int position) {
        Review review = getItem(position);
        ItemReviewBinding binding = holder.binding;

        binding.getRoot().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(onItemClickListener!= null){
                    onItemClickListener.accept(review);
                }
            }
        });

        binding.contentTextView.setText(review.rContent); // 리뷰 내용
        binding.dateTextView.setText(review.rDate); // 리뷰 날짜

        if (review.reContent == null) {
            binding.replyContainer.setVisibility(View.GONE);
        } else {
            binding.replyContainer.setVisibility(View.VISIBLE);
            binding.replyTextView.setText(review.reContent);
            binding.replyDateTextView.setText(
                    new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(new Date(review.reDate))
            );
        }
    }


    static class ReviewItemViewHolder extends RecyclerView.ViewHolder{
        public ItemReviewBinding binding;

        public ReviewItemViewHolder(@NonNull ItemReviewBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
