package com.akumina.android.auth.akuminalib.msal;

import java.io.File;

public class AuthFile {

    private Integer fileId;

    private File file;

    public  AuthFile(Integer fileId) {
        this.fileId = fileId;
    }

    public  AuthFile (File file) {
        this.file = file;
    }

    public Integer getFileId() {
        return fileId;
    }

    public File getFile() {
        return file;
    }

    public boolean isFileBased() {
        return  file!=null;
    }
}
