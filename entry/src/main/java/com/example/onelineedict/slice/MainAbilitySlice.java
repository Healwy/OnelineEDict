package com.example.onelineedict.slice;

import com.example.onelineedict.ResourceTable;
import com.example.onelineedict.common.MyDict;
import com.example.onelineedict.common.WordData;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.agp.components.TextField;

import java.io.IOException;
import java.util.List;

public class MainAbilitySlice extends AbilitySlice {
    private MyDict mMyDict;
    private TextField mTextFieldWord;
    private Text mTextResult;
    private Image mImageSearch;
    private Image mImageLogo;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        mMyDict = new MyDict(this);
        try {
            mMyDict.init();
        } catch (IOException e) {
            terminateAbility();
        }
        mTextFieldWord = (TextField) findComponentById(ResourceTable.Id_text_field_word);
        mTextResult = (Text) findComponentById(ResourceTable.Id_text_search_result);
        mImageSearch = (Image) findComponentById(ResourceTable.Id_image_search);
        if (mImageSearch != null) {
            mImageSearch.setClickedListener(component -> {
                mImageLogo.setVisibility(Component.INVISIBLE);
                mTextResult.setVisibility(Component.VISIBLE);
                List<WordData> searchList = mMyDict.searchLocalData(mTextFieldWord.getText());
                if (searchList != null && searchList.size() > 0) {
                    mTextResult.setText("");
                    for (WordData wordData : searchList) {
                        mTextResult.append(wordData.type + " : " + wordData.meanings + "\r\n");
                    }
                } else {
                    mTextResult.setText("本地词库未搜索到，正在进行网络搜索！！！！");
                }
            });
        }
        mImageLogo = (Image) findComponentById(ResourceTable.Id_image_logo);
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
