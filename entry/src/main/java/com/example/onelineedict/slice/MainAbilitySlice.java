package com.example.onelineedict.slice;

import com.example.onelineedict.ResourceTable;
import com.example.onelineedict.common.MyDict;
import com.example.onelineedict.common.SearchWordCallback;
import com.example.onelineedict.common.WordData;
import com.example.onelineedict.utils.DeviceUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.agp.components.TextField;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import org.jsoup.internal.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainAbilitySlice extends AbilitySlice {
    public static final int SEARCH_RESULT = 100;
    private MyDict mMyDict;
    private TextField mTextFieldWord;
    private Text mTextResult;
    private Image mImageSearch;
    private Image mImageLogo;
    EventRunner runner = EventRunner.getMainEventRunner();
    MyEventHandler mHandler = new MyEventHandler(runner);

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        if (DeviceUtils.isTv()) {
            super.setUIContent(ResourceTable.Layout_ability_main);
        } else {
            super.setUIContent(ResourceTable.Layout_ability_main_wearable);
        }
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
                if (DeviceUtils.isTv()) {
                    mImageLogo.setVisibility(Component.INVISIBLE);
                    mTextResult.setVisibility(Component.VISIBLE);
                }

                List<WordData> searchList = mMyDict.searchLocalData(mTextFieldWord.getText());
                if (searchList != null && searchList.size() > 0) {
                    if (DeviceUtils.isTv()) {
                        mTextResult.setText("");
                        for (WordData wordData : searchList) {
                            mTextResult.append(wordData.type + " : " + wordData.meanings + "\r\n");
                        }
                    } else if (DeviceUtils.isWearable()) {
                        showSearchResultForWearableDevice(searchList);
                    }
                } else {
                    if (DeviceUtils.isTv()) {
                        mTextResult.setText("本地词库未搜索到，正在进行网络搜索！！！！");
                    }
                    mMyDict.searchWebDict(mTextFieldWord.getText(), new SearchWordCallbackImpl());
                }
            });
        }
        mImageLogo = (Image) findComponentById(ResourceTable.Id_image_logo);
        mTextFieldWord.addTextObserver(new Text.TextObserver() {
            @Override
            public void onTextUpdated(String s, int i, int i1, int i2) {

                if (StringUtil.isBlank(s)) {
                    mImageSearch.setEnabled(false);
                } else {
                    mImageSearch.setEnabled(true);
                }
            }
        });
    }

    private void showSearchResultForWearableDevice(List<WordData> dataList) {
        Intent intent = new Intent();
        ArrayList<String> typeList = new ArrayList<>();
        ArrayList<String> meaningList = new ArrayList<>();
        for (WordData wordData : dataList) {
            typeList.add(wordData.type);
            meaningList.add(wordData.meanings);
        }
        intent.setStringArrayListParam(WearableSearchResultAbilitySlice.MEANING_LIST, meaningList);
        intent.setStringArrayListParam(WearableSearchResultAbilitySlice.TYPE_LIST, typeList);
        present(new WearableSearchResultAbilitySlice(), intent);
    }


    private class SearchWordCallbackImpl implements SearchWordCallback {
        @Override
        public void onResult(List<WordData> result) {
            InnerEvent event = InnerEvent.get();
            event.eventId = SEARCH_RESULT;
            event.object = result;
            mHandler.sendEvent(event);
        }
    }

    public class MyEventHandler extends EventHandler {

        public MyEventHandler(EventRunner runner) {
            super(runner);
        }

        @Override
        protected void processEvent(InnerEvent event) {
            super.processEvent(event);
            if (event == null) {
                return;
            }
            int eventId = event.eventId;
            switch (eventId) {
                case SEARCH_RESULT: {
                    List<WordData> result = (List<WordData>) event.object;
                    if (DeviceUtils.isTv()) {
                        if (result.size() == 0) {
                            mTextResult.setText("单词没有查到，请确认单词是否输入错误！");
                        } else {
                            mTextResult.setText("");
                            for (WordData wordData : result) {
                                mTextResult.append(wordData.type + " " + wordData.meanings + "\r\n");
                            }
                        }
                    } else if (DeviceUtils.isWearable()) {
                        showSearchResultForWearableDevice(result);
                    }
                    break;
                }
            }
        }
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
