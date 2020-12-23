package com.example.onelineedict.common;

import ohos.app.AbilityContext;
import ohos.data.DatabaseHelper;
import ohos.data.rdb.RdbOpenCallback;
import ohos.data.rdb.RdbStore;
import ohos.data.rdb.StoreConfig;
import ohos.data.resultset.ResultSet;
import ohos.global.resource.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MyDict {
    private AbilityContext mContext;
    private File mDictPath;
    private File mDbPath;
    private static final RdbOpenCallback sRdbOpenCallback = new RdbOpenCallback() {
        @Override
        public void onCreate(RdbStore rdbStore) {

        }

        @Override
        public void onUpgrade(RdbStore rdbStore, int i, int i1) {

        }
    };
    private RdbStore mRdbStore;
    private StoreConfig mStoreConfig = StoreConfig.newDefaultConfig("dict.sqlite");

    public MyDict(AbilityContext mContext) {
        this.mContext = mContext;
        mDictPath = new File(mContext.getDataDir().toString() + "/MainAbility/databases/db");
        if (!mDictPath.exists()) {
            mDictPath.mkdirs();
        }
        mDbPath = new File(Paths.get(mDictPath.toString(), "dict.sqlite").toString());
    }

    private void extractDB() throws IOException {
        Resource resource = mContext.getResourceManager()
                .getRawFileEntry("resources/rawfile/dict.sqlite").openRawFile();
        if (mDbPath.exists()) {
            return;
        }

        FileOutputStream fos = new FileOutputStream(mDbPath);
        byte[] buffer = new byte[4096];
        int count;
        while ((count = resource.read(buffer)) >= 0) {
            fos.write(buffer, 0, count);
        }
        resource.close();
        fos.close();
    }

    public void init() throws IOException {
        extractDB();
        DatabaseHelper helper = new DatabaseHelper(mContext);
        mRdbStore = helper.getRdbStore(mStoreConfig, 1, sRdbOpenCallback, null);
    }

    public List<WordData> searchLocalData(String word) {
        word = word.toLowerCase();
        String[] args = new String[]{word};
        mRdbStore.isOpen();
        ResultSet resultSet = mRdbStore.querySql("select * from t_words where word=?", args);
        List<WordData> list = new ArrayList<>();
        while (resultSet.goToNextRow()) {
            WordData wordData = new WordData();
            wordData.type = resultSet.getString(2);
            wordData.meanings = resultSet.getString(3);
            list.add(wordData);
        }
        return list;
    }
}
