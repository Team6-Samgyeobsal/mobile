package site.samgyeopsal.thechef;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.Lifecycle;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.ScanMode;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.zxing.BarcodeFormat;
import com.squareup.seismic.ShakeDetector;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import site.samgyeopsal.thechef.app.GlideApp;
import site.samgyeopsal.thechef.common.RetrofitManager;
import site.samgyeopsal.thechef.common.UserPreferenceManager;
import site.samgyeopsal.thechef.databinding.ActivityHomeBinding;
import site.samgyeopsal.thechef.model.OrderResponse;
import site.samgyeopsal.thechef.model.OrderUser;
import site.samgyeopsal.thechef.model.Store;
import site.samgyeopsal.thechef.model.User;
import site.samgyeopsal.thechef.retrofit.OrderService;
import site.samgyeopsal.thechef.retrofit.StoreService;
import timber.log.Timber;

/**
 * @filename HomeActivity
 * @author 최태승
 * @since 2023.03.18
 * 앱의 홈 화면을 나타냄
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.18	최태승		최초 생성
 * </pre>
 */
public class HomeActivity extends AppCompatActivity implements ShakeDetector.Listener  {

    private ActivityHomeBinding binding;
    private StoreService storeService;
    private OrderService orderService;
    private CodeScanner codeScanner;
    private ShakeDetector shakeDetector;
    private UserPreferenceManager userPreferenceManager;

    private ImageView imageView;
    private Thread thread;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    /*
     * ActivityResultLauncher : 안드로이드에서 액티비티를 실행하고 그 결과를 처리하는 인터페이스.
     * 카메라 권한 요청 : codeScanner
     * 권한 요청을 처리하기 위해 ActivityResultContracts.RequestPermission() 메서드를 사용하여 권한 요청 결과를 처리.
     * registerForActivityResult() 메서드를 사용하여 ActivityResultLauncher를 들록하고 람다식으로 처리할 결과를 지정.
     * result가 true이면 codeScanner.startPreview() 메서드를 호출하여 카메라 미리보기를 시작함
     */
    private final ActivityResultLauncher<String> requestPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result-> {
                if (result) codeScanner.startPreview();
            });


    /*
     * Activity의 레이아웃을 ActivityHomeBinding으로부터 inflate
     * View를 설정하고, 새로운 WindowInsetsControllerCompat 객체를 생성
     * getWindow().getDecorView()를 사용하여 현재 Activity의 DecorView를 가져와 ViewCompat.getWindowInsetsController()를 호출함
     * WindowInsectsControllerCompat Class : 시스템 상태 표시줄과 네비게이션 바의 색상 변경
     * ActivityHomeBinding Class : 앱의 홈 레이아웃을 화면에 표시
     * ViewCompat Class : 앱의 바텀 시트에 Padding(뷰와 내용 사이의 여백) 추가
     * initCodeScanner(), initShakeDetector() 메서드를 호출하여, 바코드 스캐너와 흔들림 감지기를 초기화
     */

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // 레이아웃의 fitsSystemWindows 특성을 제거하여 시스템 바와 상호 작용하는 뷰의 크기를 조정
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        WindowInsetsControllerCompat windowInsetsControllerCompat =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsControllerCompat!= null){
            windowInsetsControllerCompat.setAppearanceLightNavigationBars(true); // 네비게이션 바를 밝게
        }

        /*
         * ActivityHomeBinding 클래스를 사용하여 레이아웃 파일에 있는 뷰들을 바인딩
         * inflate 메서드를 호출하여 인플레이션(레이아웃 파일을 메모리에 로드)을 수행
         * getLayoutInflater() 메서드를 사용해 LayoutInflater 객체를 생성
         */
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imageView = binding.imageView;

        storeService = RetrofitManager.getInstance().storeService;
        orderService = RetrofitManager.getInstance().orderService;
        userPreferenceManager = UserPreferenceManager.getInstance(this);

        System.out.println("userpreference :::::: " + userPreferenceManager);



        // padding bottom에 해당하는 부분 조절, WindowInsets의 변경 사항 감지 후 처리하는 리스너 등록
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomSheet, new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat windowInsets) {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()); // 시스템 바 차지하는 공간 개선

                v.setPadding(
                        v.getPaddingLeft(),
                        v.getPaddingTop(),
                        v.getPaddingRight(),
                        insets.bottom
                );

                return WindowInsetsCompat.CONSUMED; // 다른 뷰들이 이벤트를 처리할 수 없도록
            }
        });

        initCodeScanner();
        initShakeDetector();
        initUi();

        Store store = userPreferenceManager.getUser().store;

        String sid = store.getSid();
        System.out.println("sid ::::::::" + sid);

        fetchFundingInformation(sid);
    }

    /*
     * initCodeScanner() : QR 코드 스캐너를 초기화하고 설정하는 메서드
     *
     */
    private void initCodeScanner(){
        codeScanner = new CodeScanner(this, binding.scannerView); // 현재 액티비티(this)와 Scanner View 전달
        codeScanner.setCamera(CodeScanner.CAMERA_BACK); // 카메라 지정
        codeScanner.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE)); // 바코드 형식설정 (only QRCode)
        codeScanner.setAutoFocusMode(AutoFocusMode.SAFE); // 카메라 오토 포커스 지정
        codeScanner.setScanMode(ScanMode.SINGLE); // 한 번의 스캔 후 자동으로 스캔이 멈춤
        codeScanner.setAutoFocusEnabled(true); // 자동 포커스 기능 ON
        codeScanner.setFlashEnabled(false); // 플래쉬 기능 OFF

        codeScanner.setDecodeCallback(result -> { // 오류 시 콜백 (로그 출력)
            Timber.d("스캔 결과: " + result.getText());

            uiHandler.post(() -> { // UI 스레드에서 실행되도록 요청
                ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, ToneGenerator.MAX_VOLUME);
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP); // 스캔시 Beep음 출력

                // Intent를 이용하여 해당 URL을 브라우저로 열어줌
                if (URLUtil.isNetworkUrl(result.getText())){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getText()));
                    startActivity(intent);
                } else {
                    getOrder(result.getText());
                }

                uiHandler.postDelayed(()-> { // 2초 후에 다시 스캔
                    if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED){
                        codeScanner.startPreview();
                    }
                }, 2000);
            });
        });

        codeScanner.setErrorCallback(Timber::d);
    }

    private void getOrder(String oid){
        if (binding.progressView.getVisibility() == View.VISIBLE) return;

        binding.progressView.setVisibility(View.VISIBLE);
        System.out.println("주문번호(OID) ::::::: " + oid);
        orderService.getOrder(oid).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                binding.progressView.setVisibility(View.GONE);

                if (response.isSuccessful()){
                    OrderResponse orderResponse = response.body();

                    Intent intent = new Intent(HomeActivity.this, TakeOrderActivity.class);
                    intent.putExtra("order", orderResponse);
                    startActivity(intent);

                } else {
                    Toast.makeText(
                            HomeActivity.this,
                            "유효하지 않은 QR 코드입니다. 주문 정보를 다시 확인해주세요.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Timber.d(t);
                binding.progressView.setVisibility(View.GONE);

            }
        });
    }

    /*
     * initShakeDetector() : ShakeDetector 클래스에서 사용되는 Shake 이벤트를 감지하기 위해 SensorManager를 인스턴스화하고,
     *                       ShakeDetector의 start 메서드를 호출하여 Shake 이벤트 감지를 시작함
     */
    private void initShakeDetector() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        shakeDetector = new ShakeDetector(this);
        shakeDetector.start(sensorManager);
    }

    /*
     * onResume() : Activity가 사용자 화면에서 보일 때마다 실행
     * 카메가 권한 요청
     */

    private void initUi() {
        // 주문 내역 버튼 클릭시, 주문 내역 화면 시작
        binding.orderHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, OrderHistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        binding.salesHistoryButton.setOnClickListener(v->{
            Intent intent = new Intent(HomeActivity.this, SalesHistoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        binding.myInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, InformationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        binding.reviewButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(HomeActivity.this, ReviewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED) {
            requestPermission.launch(Manifest.permission.CAMERA);
        } else {
            codeScanner.startPreview(); // 카메라 미리보기
        }

        thread = new Thread(){
            @Override
            public void run() {
                while (true) {
                    uiHandler.post(() -> updateWaitingNumber());
                    try{
                        Thread.sleep(1000);
                    } catch (InterruptedException e){
                        break;
                    }
                }
            }
        } ;
        thread.start();
    }

    @Override
    protected void onPause(){
        codeScanner.releaseResources();

        if (thread != null){
            thread.interrupt();
            thread = null;
        }

        super.onPause();
    }

    @Override
    protected void onDestroy(){
        shakeDetector.stop();
        super.onDestroy();
    }


    /*
     * hearShake() : ShakeDetector.ShakeListener 인터페이스를 구현한 메서드
     * 기기를 흔들 때 실행되며, 로그 메세지 출력
     *
     */
    @Override
    public void hearShake() {
        Timber.d("기기 흔들림 감지");

        // resume 상태면 메서드 종료
        if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) return;

        // resume 상태가 아닐 경우 Intent
        Intent intent = new Intent(this, HomeActivity.class);
        // 이미 HomeActivity가 실행 중이라면 그 액티비티를 최상위로 올리고 기존의 액티비티 사용
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



    private void fetchFundingInformation(String sid) {
        System.out.println("String sid :::: >>>>> " + sid);
        Context context= imageView.getContext();
        storeService.getStore(sid).enqueue(new Callback<Store>() {
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions = requestOptions.transforms(new CircleCrop());

                    Store store = response.body();
                    System.out.println(":::::: store : " + store);

                    if (store.sThumbUrl != null){
                        GlideApp.with(context)
                                .load(userPreferenceManager.getUser().member.mProfile)
                                .apply(requestOptions)
                                .into(imageView);
                        Timber.d("fThumbUrl : %s", store.sThumbUrl);
                    } else {
                        Timber.w("fThumbUrl is null");
                    }

                    /*GlideApp.with(context)
                            .load(response.body().fThumbUrl)
                            //.override(5,5)
                            .into(imageView);
                    System.out.println(">>> fThumbUrl :  "  + response.body().fThumbUrl);

                    Timber.d("fThumbUrl: %s", response.body().fThumbUrl);*/





                    // 프로필 이미지
                    // binding.profileImageView.setImageBitmap();


                    binding.nameTextView.setText(store.storeName);

                    System.out.println(">>> storename : " + store.storeName);



                    //endregion
                } else {
                    Timber.w(">>>>>>>>>>>>>>>>>>>Failed to fetch funding data: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                Timber.w(t);
            }
        });
    }

    private void updateWaitingNumber() {
        String sid = userPreferenceManager.getUser().store.getSid();

        orderService.getQueueList(sid).enqueue(new Callback<List<OrderUser>>() {
            @Override
            public void onResponse(Call<List<OrderUser>> call, Response<List<OrderUser>> response) {
                int number = 0;

                if (response.isSuccessful()) {

                    number = response.body().size();
                }
                binding.waitingNumberTextView.setText(String.valueOf(number));
            }

            @Override
            public void onFailure(Call<List<OrderUser>> call, Throwable t) {
                binding.waitingNumberTextView.setText(String.valueOf(0));
            }
        });
    }


}
