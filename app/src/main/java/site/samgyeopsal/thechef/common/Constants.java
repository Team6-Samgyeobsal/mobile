package site.samgyeopsal.thechef.common;

import okhttp3.CookieJar;

/**
 * @filename Constants
 * @author 최태승
 * @since 2023.03.20
 * 상수(Constant)를 정의하기 위한 클래스
 * 네트워크 요청 URL 등을 상수로 정의
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.20
 * </pre>
 */
public class Constants {

    // Retrofit base Url 선언
    public static final String baseUrl = "http://192.168.137.108:80";

    // CookieJar 전역 변수 생성 및 초기화
    public static CookieJar cookieJar = null;
}

