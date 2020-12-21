package com.example.onelineedict.common;

import ohos.app.AbilityContext;
import ohos.global.resource.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyDict {
    private AbilityContext mContext;
    private File mDictPath;
    private File mDbPath;

    public MyDict(AbilityContext mContext) {
        this.mContext = mContext;
        mDictPath = new File(mContext.getDatabaseDir().toString()
                + "/MainAbillty/databases/db");
        if (!mDictPath.exists()) {
            mDictPath.mkdirs();
        }
        mDbPath = new File(mDictPath.getPath() + "dict.sqlite");
    }

    private void extractDB() throws IOException {
        Resource resource = mContext.getResourceManager()
                .getRawFileEntry("resources/rawfile/dict.sqlite").openRawFile();
        if (mDbPath.exists()) {
            return;
        }

        FileOutputStream fos = new FileOutputStream(mDbPath);
        byte buffer[] = new byte[4096];
        int count;
        while ((count = resource.read(buffer)) >= 0) {
            fos.write(buffer, 0, count);
        }
        resource.close();
        fos.close();
    }

    public void init() throws IOException {
        extractDB();
    }
}
