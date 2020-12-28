package com.example.onelineedict.common;

import ohos.app.AbilityContext;
import ohos.data.DatabaseHelper;
import ohos.data.rdb.RdbOpenCallback;
import ohos.data.rdb.RdbStore;
import ohos.data.rdb.StoreConfig;
import ohos.data.resultset.ResultSet;
import ohos.global.resource.Resource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class AsyncSearchWord extends Thread {
    private String mWord;
    private RdbStore mRdbstore;
    private SearchWordCallback mCallback;

    public AsyncSearchWord(String word, RdbStore store, SearchWordCallback callback) {
        this.mWord = word;
        this.mRdbstore = store;
        this.mCallback = callback;
    }

    @Override
    public void run() {
        super.run();
        try {
            // 获取搜索结果（HTML形式）
            Document doc = Jsoup.connect("https://www.iciba.com/word?w=" + mWord).get();
            Elements ulElements = doc.getElementsByClass("Mean_part__1RA2V");
            // 将网络单词信息保存到本地的SQL语句
            String insertSQL = "insert into t_words(word, type, meanings) values(?,?,?);";
            List<WordData> wordDataList = new ArrayList<>();
            for (Element ulElement : ulElements) {
                // 获取单词的每一个词性和中文解释
                Elements liElements = ulElement.getElementsByTag("li");
                // 对每一个词性进行迭代
                for (Element liElement : liElements) {
                    WordData wordData = new WordData();
                    Elements iElements = liElement.getElementsByTag("i");
                    for (Element iElement : iElements) {
                        // 获取当前词性
                        wordData.type = iElement.text();
                        break;
                    }
                    // 获取中文解释
                    Elements divElements = liElement.getElementsByTag("div");
                    for (Element divElement : divElements) {
                        wordData.meanings = divElement.text();   // 提取词性对应的中文解释
                        break;
                    }
                    wordDataList.add(wordData);
                    mRdbstore.executeSql(
                            insertSQL, new String[]{mWord, wordData.type, wordData.meanings});
                }
                break;
            }
            if (mCallback != null) {
                mCallback.onResult(wordDataList);
            }
        } catch (Exception e) {
            // Noop
        }
    }
}

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

    // 异步搜索网络词典
    public void searchWebDict(String word, SearchWordCallback callback) {
        word = word.toLowerCase();
        // 异步搜索
        new AsyncSearchWord(word, mRdbStore, callback).start();
    }
}
