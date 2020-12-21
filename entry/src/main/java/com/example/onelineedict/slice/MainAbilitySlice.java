package com.example.onelineedict.slice;

import com.example.onelineedict.ResourceTable;
import com.example.onelineedict.common.MyDict;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;

import java.io.IOException;

public class MainAbilitySlice extends AbilitySlice {
    private MyDict mMyDict;

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
