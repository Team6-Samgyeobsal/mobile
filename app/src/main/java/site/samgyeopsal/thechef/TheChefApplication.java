package site.samgyeopsal.thechef;

import android.app.Application;
import timber.log.Timber.DebugTree;
import timber.log.Timber;

public class TheChefApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();

        if (BuildConfig.DEBUG){
            Timber.plant(new DebugTree());
        }
    }
}

/*
해당 코드는 안드로이드 앱의 Application 클래스를 상속하여 TheChefApplication 클래스를 정의하고 있습니다.

onCreate() 메서드는 앱이 시작될 때 호출되는 메서드로, 해당 메서드에서 앱 초기화 작업을 수행합니다.

여기서는 먼저 super.onCreate() 메서드를 호출하여 부모 클래스의 onCreate() 메서드를 실행시키고, 자식 클래스에서 추가적인 초기화 작업을 수행하도록 합니다.

그 다음, BuildConfig.DEBUG 값을 검사하여 디버그 모드인 경우에만 Timber 라이브러리의 DebugTree 클래스를 사용하여 디버깅 정보를 출력할 수 있도록 합니다.

Timber는 안드로이드에서 로깅을 위해 많이 사용되는 라이브러리로, DebugTree는 디버그 모드에서 로그를 출력하는 역할을 합니다.

이 코드는 디버그 모드에서만 Timber를 사용하고 있으므로, 릴리스 모드에서는 로그가 출력되지 않습니다.

 */