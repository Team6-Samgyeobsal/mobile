package site.samgyeopsal.thechef.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;

import site.samgyeopsal.thechef.R;
import site.samgyeopsal.thechef.databinding.ItemReviewBinding;
import site.samgyeopsal.thechef.databinding.ItemReviewWithReplyBinding;
import site.samgyeopsal.thechef.model.Review;

public class ReviewAdapter extends ListAdapter<Review, RecyclerView.ViewHolder> {


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
                        TextUtils.equals(oldItem.mProfile, newItem.mProfile) &&
                        TextUtils.equals(oldItem.rDate, newItem.rDate) &&
                        TextUtils.equals(oldItem.rContent, newItem.rContent) &&
                        TextUtils.equals(oldItem.reContent, newItem.reContent);
            }
        });
    }

    public void setOnItemClickListener(Consumer<Review> onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position){
        if (position == -1) return 0;

        if (TextUtils.isEmpty(getItem(position).reContent)) {
            return 0;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == 0) {
            ItemReviewBinding binding = ItemReviewBinding.inflate(inflater, parent, false);
            return new ReviewItemViewHolder(binding);
        } else {
            ItemReviewWithReplyBinding binding = ItemReviewWithReplyBinding.inflate(inflater, parent, false);
            return new ReviewWithReplyItemViewHolder(binding);
        }
    }

    /*
     * onBindViewHolder(ViewHo) : 각 아이템 뷰에 데이터를 바인딩하는 역할
     */

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Review review = getItem(position);

        if (holder instanceof ReviewItemViewHolder){
            ItemReviewBinding binding = ((ReviewItemViewHolder) holder).binding;

            binding.getRoot().setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.accept(review);
                }
            });

            binding.idTextView.setText(review.mEmail.split("@")[0]);
            binding.ratingBar.setRating(review.rScore);

            if (TextUtils.isEmpty(review.mProfile)){
                binding.profileImageView.setImageResource(R.drawable.img_profile_default);
            } else {
                Glide.with(binding.profileImageView)
                        .load(review.mProfile)
                        .placeholder(R.drawable.img_profile_default)
                        .into(binding.profileImageView);
            }

            binding.contentTextView.setText(review.rContent); // 리뷰 내용
            binding.dateTextView.setText(review.rDate); // 리뷰 날짜
    } else if (holder instanceof ReviewWithReplyItemViewHolder) {
            ItemReviewWithReplyBinding binding = ((ReviewWithReplyItemViewHolder) holder).binding;

            binding.clickContainer.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.accept(review);
                }
            });

            binding.idTextView.setText(review.mEmail.split("@")[0]);
            binding.ratingBar.setRating(review.rScore);

            if (TextUtils.isEmpty(review.mProfile)) {
                binding.profileImageView.setImageResource(R.drawable.img_profile_default);
            } else {
                Glide.with(binding.profileImageView)
                        .load(review.mProfile)
                        .placeholder(R.drawable.img_profile_default)
                        .into(binding.profileImageView);
            }
            binding.contentTextView.setText(review.rContent);
            binding.dateTextView.setText(review.rDate);
            binding.replyTextView.setText(review.reContent);
            binding.replyDateTextView.setText(
                    new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(new Date(review.reDate)));

            if (TextUtils.isEmpty(review.reProfile)) {
                binding.replyProfileImageView.setImageResource(R.drawable.img_profile_default);
            } else {
                Glide.with(binding.replyProfileImageView)
                        .load(review.reProfile)
                        .placeholder(R.drawable.img_profile_default)
                        .into(binding.replyProfileImageView);
            }
        }

    }

    static class ReviewItemViewHolder extends RecyclerView.ViewHolder{
        public ItemReviewBinding binding;

        public ReviewItemViewHolder(@NonNull ItemReviewBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    static class ReviewWithReplyItemViewHolder extends RecyclerView.ViewHolder{
        public ItemReviewWithReplyBinding binding;

        public ReviewWithReplyItemViewHolder(@NonNull ItemReviewWithReplyBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
