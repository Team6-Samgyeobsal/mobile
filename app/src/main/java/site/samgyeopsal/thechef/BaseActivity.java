package site.samgyeopsal.thechef;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
/**
 * @filename BaseActivity
 * @author 최태승
 * @since 2023.03.18
 * 앱에서 모든 액티비티가 공통으로 사용하는 메서드를 정의
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.18	최태승		최초 생성
 * </pre>
 */
public class BaseActivity extends AppCompatActivity {

    /*
     * hideKeyboard() : 키보드를 숨김
     * InputMethodManager 객체를 사용하여 현재 포커스가 있는 View에서 키보드를 숨김
     */
    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) return;

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /*
     * showKeyboard() : View에 포커스를 준 뒤 키보드를 보여줌
     * InputMethodManager 객체를 사용하여 View에 포커스를 주고, 해당 View에서 키보드를 보여줌
     */
    protected void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }
}
