package com.galaxy.voicealarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditMemo extends AppCompatActivity {

    Memo memo;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memo);
        memo = getIntent().getExtras().getParcelable("Memo");
        editText = (EditText)findViewById(R.id.edit);
        if(null != memo.getContent()){
            editText.setText(memo.getContent());
        }
    }

    public void OnSaveMemo(View v){
        String content = editText.getText().toString();
        if(content.length() == 0) {
            Toast.makeText(EditMemo.this, "메모를 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(-1 == memo.getID()){
            memo.setContent(content);
            DBHelper.getInstance().insertMemoinDB(memo);
            VoiceAlarmApplication.settingMemoList();
            finish();
        }else{
            memo.setContent(content);
            DBHelper.getInstance().updateMemoinDB(memo);
            VoiceAlarmApplication.settingMemoList();
            finish();
        }
    }
}
