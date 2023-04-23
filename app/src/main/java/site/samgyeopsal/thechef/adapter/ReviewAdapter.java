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
import site.samgyeopsal.thechef.common.UserPreferenceManager;
import site.samgyeopsal.thechef.databinding.ItemReviewBinding;
import site.samgyeopsal.thechef.databinding.ItemReviewWithReplyBinding;
import site.samgyeopsal.thechef.model.Review;
import site.samgyeopsal.thechef.model.User;

/**
 * @filename ReviewAdapter
 * @author 최태승
 * @since 2023.03.22
 * 리뷰 어댑터이며 ListAdapter를 상속 받음
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.22	최태승        최초 생성
 * </pre>
 */
public class ReviewAdapter extends ListAdapter<Review, RecyclerView.ViewHolder> {


    private Consumer<Review>  onItemClickListener;

    private UserPreferenceManager userPreferenceManager;


    // 생성자에서 DiffUtil 사용을 위한 초기화
    public ReviewAdapter(UserPreferenceManager userPreferenceManager) {
        super(new DiffUtil.ItemCallback<Review>() {


            // 두 아이템이 같은지 확인
            @Override
            public boolean areItemsTheSame(@NonNull Review oldItem, @NonNull Review newItem) {
                return false;
            }

            // 두 아이템이 같은지 확인
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

        this.userPreferenceManager = userPreferenceManager;
    }

    // 클릭 리스너 설정
    public void setOnItemClickListener(Consumer<Review> onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    // 아이템 뷰 유형 결정
    @Override
    public int getItemViewType(int position){
        if (position == -1) return 0;
        if (getItemCount() <= position) return 0;

        if (TextUtils.isEmpty(getItem(position).reContent)) {
            return 0;
        } else {
            return 1;
        }
    }

    // 뷰홀더 생성
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

        // 일단 리뷰 뷰횰더일 경우
        if (holder instanceof ReviewItemViewHolder){
            ItemReviewBinding binding = ((ReviewItemViewHolder) holder).binding;

            // 아이템 클릭 시 이벤트 처리
            binding.getRoot().setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.accept(review);
                }
            });

            // 아이템 정보 설정
            binding.idTextView.setText(review.mEmail.split("@")[0]); // mEmail에서 @뒤로는 출력 안하기
            binding.ratingBar.setRating(review.rScore);

            // 프로필 이미지 설정
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

            // 답글이 있는 리뷰 뷰홀더의 경우
        } else if (holder instanceof ReviewWithReplyItemViewHolder) {
            ItemReviewWithReplyBinding binding = ((ReviewWithReplyItemViewHolder) holder).binding;

            // 아이템 클릭 시 이벤트 처리
            binding.clickContainer.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.accept(review);
                }
            });

            binding.idTextView.setText(review.mEmail.split("@")[0]);
            binding.ratingBar.setRating(review.rScore);

            // 프로필 이미지 설정
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

            if (TextUtils.isEmpty(userPreferenceManager.getUser().member.mProfile)) {
                binding.replyProfileImageView.setImageResource(R.drawable.img_profile_default);
            } else {
                Glide.with(binding.replyProfileImageView)
                        .load(userPreferenceManager.getUser().member.mProfile)
                        .placeholder(R.drawable.img_profile_default)
                        .into(binding.replyProfileImageView);
            }
        }

    }

    // 일반 리뷰 아이템 뷰홀더
    static class ReviewItemViewHolder extends RecyclerView.ViewHolder{
        public ItemReviewBinding binding;

        public ReviewItemViewHolder(@NonNull ItemReviewBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // 답글이 있는 리뷰 아이템 뷰홀더
    static class ReviewWithReplyItemViewHolder extends RecyclerView.ViewHolder{
        public ItemReviewWithReplyBinding binding;

        public ReviewWithReplyItemViewHolder(@NonNull ItemReviewWithReplyBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
