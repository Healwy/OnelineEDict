package com.example.onelineedict.slice;

import com.example.onelineedict.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Text;

import java.util.List;

public class WearableSearchResultAbilitySlice extends AbilitySlice {
    private Text mResultText;
    public static final String TYPE_LIST = "type_list";
    public static final String MEANING_LIST = "meaning_list";

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        setUIContent(ResourceTable.Layout_wearable_search_result);
        mResultText = (Text) findComponentById(ResourceTable.Id_text_search_result);
        List<String> typeList = intent.getStringArrayListParam(TYPE_LIST);
        List<String> meaningList = intent.getStringArrayListParam(MEANING_LIST);
        if (typeList.size() > 0) {
            for (int i = 0; i < meaningList.size(); i++) {
                String type = typeList.get(i);
                String meaning = meaningList.get(i);
                mResultText.append(type + " : " + meaning + "\r\n");
            }
        } else {
            mResultText.setText("没有搜索到词义，请检查是否输入正确单词");
        }

    }
}
